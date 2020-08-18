package com.vsi.oms.api;

import java.util.HashMap;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIUpdateWholesaleReturn extends VSIBaseCustomAPI implements VSIConstants {
	
	YIFApi api;
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIUpdateMarketplaceReturn.class);

	public Document updateWholesaleReturn(YFSEnvironment env, Document inXML) throws Exception{
		
		Element eleInputOrder = inXML.getDocumentElement();	
		Element eleInputOrderLines = SCXmlUtil.getChildElement(eleInputOrder, ELE_ORDER_LINES);
		Element eleInputOrderLine = SCXmlUtil.getChildElement(eleInputOrderLines, ELE_ORDER_LINE);
		Document salesOrderListXML = null;
		Boolean bSendReturnCreateEmail = false;
		
		if(!YFCObject.isVoid(eleInputOrderLine.getAttribute(ATTR_DERIVED_FROM_ORDER_HEADER_KEY))){
			
			String derivedFromOHK = eleInputOrderLine.getAttribute(ATTR_DERIVED_FROM_ORDER_HEADER_KEY);
			if(!YFCObject.isVoid(derivedFromOHK)){
				
				Document getOrderListInXML = SCXmlUtil.createDocument(ELE_ORDER);
				getOrderListInXML.getDocumentElement().setAttribute(ATTR_ORDER_HEADER_KEY, derivedFromOHK);
				salesOrderListXML = VSIUtils.invokeService(env, 
						"VSIGetSalesOrderDetailsForMarketplaceReturn", getOrderListInXML);
				//salesOrderListXML = loadXMLFrom("C:\\salesorderlist.xml");
			}
		}
		
		if(!YFCObject.isVoid(salesOrderListXML)){

			Element eleOrderList = salesOrderListXML.getDocumentElement();
			if(eleOrderList.hasChildNodes()){

				Element eleSalesOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
				String strSalesOrderType = SCXmlUtil.getAttribute(eleSalesOrder, ATTR_ORDER_TYPE);
				if(strSalesOrderType.equalsIgnoreCase(WHOLESALE)){
					
					String strEnterpriseCode = SCXmlUtil.getAttribute(eleInputOrder, ATTR_ENTERPRISE_CODE);
					HashMap<String, String> hm_ReasonCode_ReasonDesc = new HashMap<String, String>();
					HashMap<String, String> hm_SalesOrderOrderLineKey_ShipNode = new HashMap<String, String>();
					
					NodeList nlSalesOrderLine = eleSalesOrder.getElementsByTagName(ELE_ORDER_LINE);
					for(int j = 0; j < nlSalesOrderLine.getLength(); j++){
						
						Element eleSalesOrderLine = (Element) nlSalesOrderLine.item(j);
						NodeList nlOrderStatus = eleSalesOrderLine.getElementsByTagName(ELE_ORDER_STATUS);
						for(int a = 0; a < nlOrderStatus.getLength(); a++){
							
							Element eleOrderStatus = (Element) nlOrderStatus.item(a);
							Element eleShipNode = SCXmlUtil.getChildElement(eleOrderStatus, ELE_SHIP_NODE);
							if(!YFCObject.isVoid(eleShipNode)){
																
								hm_SalesOrderOrderLineKey_ShipNode.put(
										SCXmlUtil.getAttribute(eleOrderStatus, ATTR_ORDER_LINE_KEY), SCXmlUtil.getAttribute(eleShipNode, ATTR_DESCRIPTION));
							}
						}
					}
					
					try{

						Document getReturnReasonList = getReturnReasonsforOrg(env, strEnterpriseCode);
						if(getReturnReasonList.getDocumentElement().hasChildNodes()){

							NodeList nlCommonCode = getReturnReasonList.getElementsByTagName(ELE_COMMON_CODE);
							for(int i = 0; i < nlCommonCode.getLength(); i++){

								Element eleCommonCode = (Element) nlCommonCode.item(i);
								hm_ReasonCode_ReasonDesc.put(
										SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_VALUE), SCXmlUtil.getAttribute(eleCommonCode, ATTR_CODE_SHORT_DESCRIPTION));
							}
						}

						NodeList nlOrderLine = eleInputOrderLines.getElementsByTagName(ELE_ORDER_LINE);
						for(int j = 0; j < nlOrderLine.getLength(); j++){

							Element eleOrderLine = (Element) nlOrderLine.item(j);
							String strLineType = SCXmlUtil.getAttribute(eleOrderLine, ATTR_LINE_TYPE);

							if(log.isDebugEnabled()){
								log.debug("LineType: " + strLineType);
							}

							if(!strLineType.equalsIgnoreCase(LINE_TYPE_CREDIT)){
								bSendReturnCreateEmail = true;
							}

							String strReturnReason = SCXmlUtil.getAttribute(eleOrderLine, ATTR_RETURN_REASON);
							if(!YFCObject.isVoid(strReturnReason)){

								if(hm_ReasonCode_ReasonDesc.containsKey(strReturnReason)){
									String strReturnReasonDesc = hm_ReasonCode_ReasonDesc.get(strReturnReason);
									SCXmlUtil.setAttribute(eleOrderLine, ATTR_RETURN_REASON.concat(ATTR_DESC), strReturnReasonDesc);
								}
							}
							
							String derivedFromOLK = eleOrderLine.getAttribute(ATTR_DERIVED_FROM_ORDER_LINE_KEY);
							if(!YFCObject.isVoid(derivedFromOLK)){
								
								if(hm_SalesOrderOrderLineKey_ShipNode.containsKey(derivedFromOLK)){
									
									String strSalesOrderShipNode = hm_SalesOrderOrderLineKey_ShipNode.get(derivedFromOLK);
									if(!YFCObject.isVoid(strSalesOrderShipNode)){
										
										SCXmlUtil.setAttribute(eleOrderLine, ELE_ORDER + ATTR_SHIP_NODE, strSalesOrderShipNode);
									}
								}
							}
						}
					}catch(Exception e){
						e.printStackTrace();
					}finally{
						
						if(!YFCObject.isVoid(hm_ReasonCode_ReasonDesc)){
							hm_ReasonCode_ReasonDesc.clear();
							hm_ReasonCode_ReasonDesc = null;
						}
						
						if(!YFCObject.isVoid(hm_SalesOrderOrderLineKey_ShipNode)){
							hm_SalesOrderOrderLineKey_ShipNode.clear();
							hm_SalesOrderOrderLineKey_ShipNode = null;
						}
					}
					
					Element eleExtn = SCXmlUtil.getChildElement(eleSalesOrder, ELE_EXTN);
					Document changeOrderXML = SCXmlUtil.createDocument(ELE_ORDER);
					Element eleChangeOrderRoot = changeOrderXML.getDocumentElement();
					eleChangeOrderRoot.setAttribute(ATTR_ORDER_HEADER_KEY, eleInputOrder.getAttribute(ATTR_ORDER_HEADER_KEY));
					eleChangeOrderRoot.setAttribute(ATTR_CUSTOMER_PO_NO, eleSalesOrder.getAttribute(ATTR_CUSTOMER_PO_NO));
					eleChangeOrderRoot.setAttribute(ATTR_ORDER_TYPE, eleSalesOrder.getAttribute(ATTR_ORDER_TYPE));
					eleChangeOrderRoot.setAttribute(ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);
					eleChangeOrderRoot.setAttribute(ATTR_OVERRIDE, FLAG_Y);
					//eleChangeOrderRoot.setAttribute(ATTR_BYPASS_PRICING, "Y");
					if(!YFCObject.isVoid(eleExtn)){

						Element eleChangeOrderExtn = SCXmlUtil.createChild(eleChangeOrderRoot, ELE_EXTN);
						SCXmlUtil.setAttribute(eleChangeOrderExtn, ATTR_EXTN_VENDOR_ID, SCXmlUtil.getAttribute(eleExtn, ATTR_EXTN_VENDOR_ID));
						SCXmlUtil.setAttribute(eleChangeOrderExtn, ATTR_EXTN_COST_CENTER, SCXmlUtil.getAttribute(eleExtn, ATTR_EXTN_COST_CENTER));
					}
					
					if(log.isDebugEnabled()){
						log.debug("Input to changeOrder: " + SCXmlUtil.getString(changeOrderXML));
					}
					//System.out.println(SCXmlUtil.getString(changeOrderXML));
					try{
						api = YIFClientFactory.getInstance().getApi();
						api.invoke(env, API_CHANGE_ORDER , changeOrderXML);
					}catch(Exception e){
						//throw new YFSException(e.getMessage());
						e.printStackTrace();
					}
					
					if(bSendReturnCreateEmail){

						Boolean emailSent = false;
						for(int i = 0; i < 3; i++){

							try{
								VSIUtils.invokeService(env, "VSIWholesaleReturnCreateEmail", inXML);
								emailSent = true;
							}catch(Exception e){
								log.error("Exception in VSIWholesaleReturnCreateEmail: " + e.getMessage());
								e.printStackTrace();
							}
							if(emailSent){
								break;
							}
						}
					}
				}
			}
		}
		
		if(log.isDebugEnabled()){
			log.debug("Output of VSIUpdateWholesaleReturn.updateWholesaleReturn() " + SCXmlUtil.getString(inXML));
		}
		return inXML;
	}

	private Document getReturnReasonsforOrg(YFSEnvironment env, String strEnterpriseCode) throws Exception{
		
		Document getCommonCodeListInXML = SCXmlUtil.createDocument(ELE_COMMON_CODE);
		Element eleCommonCode = getCommonCodeListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(eleCommonCode, ATTR_ORGANIZATION_CODE, strEnterpriseCode);
		SCXmlUtil.setAttribute(eleCommonCode, ATTR_CODE_TYPE, CODE_TYPE_RETURN_REASON);
		Document getCommonCodeListOutXML = VSIUtils.invokeAPI(env, API_COMMON_CODE_LIST, getCommonCodeListInXML);
		
		return getCommonCodeListOutXML;
	}
	
	/*private static Document loadXMLFrom(String path) throws Exception{
		
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		return doc;
	}*/
}
