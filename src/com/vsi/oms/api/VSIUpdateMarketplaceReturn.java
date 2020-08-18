package com.vsi.oms.api;

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

public class VSIUpdateMarketplaceReturn extends VSIBaseCustomAPI implements VSIConstants {
	
	YIFApi api;
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIUpdateMarketplaceReturn.class);

	public Document updateMarketplaceReturn(YFSEnvironment env, Document inXML) throws Exception{
		
		Element eleInputOrder = inXML.getDocumentElement();
		Element eleInputOrderLines = SCXmlUtil.getChildElement(eleInputOrder, ELE_ORDER_LINES);
		Element eleInputOrderLine = SCXmlUtil.getChildElement(eleInputOrderLines, ELE_ORDER_LINE);
		Document salesOrderListXML = null;
		
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
				Document changeOrderXML = SCXmlUtil.createDocument(ELE_ORDER);
				Element eleChangeOrderRoot = changeOrderXML.getDocumentElement();
				eleChangeOrderRoot.setAttribute(ATTR_ORDER_HEADER_KEY, eleInputOrder.getAttribute(ATTR_ORDER_HEADER_KEY));
				eleChangeOrderRoot.setAttribute(ATTR_CUSTOMER_PO_NO, eleSalesOrder.getAttribute(ATTR_CUSTOMER_PO_NO));
				eleChangeOrderRoot.setAttribute(ATTR_OVERRIDE, FLAG_Y);
				eleChangeOrderRoot.setAttribute("BypassPricing", "Y");
				Element eleChangeOrderOrderLines = SCXmlUtil.createChild(eleChangeOrderRoot, ELE_ORDER_LINES);

				NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				for(int i = 0; i < nlOrderLine.getLength(); i++){
					
					Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderOrderLines, ELE_ORDER_LINE);
					Element eleReturnOrderLine = (Element) nlOrderLine.item(i);
					Element eleSalesOrderLine = SCXmlUtil.getXpathElement(eleSalesOrder, 
							"//OrderLine[@OrderLineKey='" + SCXmlUtil.getAttribute(
									eleReturnOrderLine,ATTR_DERIVED_FROM_ORDER_LINE_KEY) + "']");				
					
					SCXmlUtil.setAttribute(eleChangeOrderLine, ATTR_ORDER_LINE_KEY, 
							SCXmlUtil.getAttribute(eleReturnOrderLine,ATTR_ORDER_LINE_KEY));
					Element eleSalesOrderLineExtn = SCXmlUtil.getChildElement(eleSalesOrderLine, ELE_EXTN);
					String strExtnChannelItemID = "";
					if(!YFCObject.isVoid(eleSalesOrderLineExtn)){
						
						strExtnChannelItemID = SCXmlUtil.getAttribute(eleSalesOrderLineExtn, ATTR_EXTN_CHANNEL_ITEM_ID);
						if(!YFCObject.isVoid(strExtnChannelItemID)){

							SCXmlUtil.setAttribute(SCXmlUtil.createChild(
									eleChangeOrderLine, ELE_EXTN), ATTR_EXTN_CHANNEL_ITEM_ID, strExtnChannelItemID);
						}
					}
					
					Element eleInputLineCharges = SCXmlUtil.getChildElement(eleReturnOrderLine, ELE_LINE_CHARGES);
					if(!YFCObject.isVoid(eleInputLineCharges) && eleInputLineCharges.hasChildNodes()){
						
						Element eleChangeOrderLineCharges = SCXmlUtil.createChild(eleChangeOrderLine, ELE_LINE_CHARGES);
						eleChangeOrderLineCharges.setAttribute(ATTR_RESET, FLAG_Y);
						NodeList nlInputLineCharge = eleInputLineCharges.getElementsByTagName(ELE_LINE_CHARGE);
						for(int j = 0; j < nlInputLineCharge.getLength(); j++){
							
							Element eleChangeOrderLineCharge = SCXmlUtil.createChild(eleChangeOrderLineCharges, ELE_LINE_CHARGE);
							Element eleInputLineCharge = (Element) nlInputLineCharge.item(j);
							String strInputChargeName = SCXmlUtil.getAttribute(eleInputLineCharge, ATTR_CHARGE_NAME);
							String strInputChargeCategory = SCXmlUtil.getAttribute(eleInputLineCharge, ATTR_CHARGE_CATEGORY);
							SCXmlUtil.setAttribute(eleChangeOrderLineCharge, 
									ATTR_CHARGE_CATEGORY, strInputChargeCategory);
							SCXmlUtil.setAttribute(eleChangeOrderLineCharge, 
									ATTR_CHARGE_NAME, strInputChargeName);
							SCXmlUtil.setAttribute(eleChangeOrderLineCharge, ATTR_CHARGE_PER_UNIT, 
									SCXmlUtil.getAttribute(eleInputLineCharge, ATTR_CHARGE_PER_UNIT));
							SCXmlUtil.setAttribute(eleChangeOrderLineCharge, ATTR_CHARGE_PER_LINE, 
									SCXmlUtil.getAttribute(eleInputLineCharge, ATTR_CHARGE_PER_LINE));
							Element eleSalesOrderLineChargeExtn = SCXmlUtil.getXpathElement(eleSalesOrderLine, 
									"//LineCharge[@ChargeName='" + strInputChargeName 
									+ "' and @ChargeCategory='" + strInputChargeCategory + "']/Extn");
							if(!YFCObject.isVoid(eleSalesOrderLineChargeExtn)){
								
								Element eleChangeOrderLineChargeExtn = SCXmlUtil.createChild(
										eleChangeOrderLineCharge, ELE_EXTN);
								SCXmlUtil.setAttribute(eleChangeOrderLineChargeExtn, ATTR_PROMO_NAME, 
										eleSalesOrderLineChargeExtn.getAttribute(ATTR_PROMO_NAME));
								SCXmlUtil.setAttribute(eleChangeOrderLineChargeExtn, ATTR_PROMO_DESC, 
										eleSalesOrderLineChargeExtn.getAttribute(ATTR_PROMO_DESC));
							}
						}
					}
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
			}
		}
		
		return inXML;
	}
	
	/*private static Document loadXMLFrom(String path) throws Exception{
		
		File fXmlFile = new File(path);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		
		return doc;
	}*/
}
