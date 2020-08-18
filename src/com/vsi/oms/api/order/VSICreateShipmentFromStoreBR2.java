package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import org.w3c.dom.*;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

/**
 * When Sterling OMS receives create Shipment from ES, the input message will only contain the CustomerPONo or orderNo will be Sterling's CustomerPoNo(i.e. ES Order number).
 * This class will call getOrderList using the CustomerPoNo and get the original orderNo finally calling createShipment with confirmShip="Y"
 * @author Perficient, Inc.
 * 
 *
 */
public class VSICreateShipmentFromStoreBR2 implements YIFCustomApi {
	@SuppressWarnings("unused")
	private Properties properties;
	private static YFCLogCategory logger;
	YIFApi api;
	
	static {
		logger = YFCLogCategory
				.instance(com.vsi.oms.api.order.VSICreateShipmentFromStoreBR2.class);
	}

	/**
	 * This method sets the properties passed from the service
	 * 
	 * @param apiArguments
	 * @throws Exception
	 */
	public void setProperties(Properties apiArguments) throws Exception {
		this.properties = apiArguments;
	}
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public void vsiGetOrderNumberForShipment (YFSEnvironment env, Document inXML)
			throws Exception {
		if(logger.isDebugEnabled()){
			logger.info("================Inside vsiGetOrderNumberForShipment================================");
			logger.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		//YFCDocument inputDoc = YFCDocument.getDocumentFor(inXML);
		
		try{
		Element rootElement = inXML.getDocumentElement();

		//get CustomerPONo from Input XML
		String customerPoNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
		String dType=VSIConstants.DOCUMENT_TYPE;
		if(logger.isDebugEnabled()){
			logger.debug("CustomerPONo: "+ customerPoNo);
		}
		
		//OMS-1304 : Start
		Map<String,String> hmLineTypeMap=new HashMap<String,String>();
		//OMS-1304 : End
		
		//OMS-808 Changes
		env.setTxnObject("customerPoNo", customerPoNo);
		ArrayList<String> ItemDetailForShip = new ArrayList<String>();

				////System.out.println("CustomerPONo: "+ customerPoNo);
		
				Document getOrderListInput = XMLUtil.createDocument("Order");
				Element eleOrder = getOrderListInput.getDocumentElement();
				
				Element eleOrderLine = getOrderListInput.createElement(VSIConstants.ELE_ORDER_LINE);
				eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
				
				//eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
				eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
				eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
				eleOrder.appendChild(eleOrderLine);
		//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
					api = YIFClientFactory.getInstance().getApi();
					env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderListTemplateForShipment.xml");
					Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
					env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
					Element orderElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
					Element orderLineEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
					if(null != orderElement){
					String orderNo=orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
					
					
		//Setting rootElement OrderNo of inXML			
		rootElement.setAttribute("OrderNo", orderNo);
		
	
		
		NodeList shipmentLineList = inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE);
		int test=shipmentLineList.getLength();
		Map<Integer, String> orderLinekeyMap = new HashMap<Integer, String>();
		String orderLineKeyTemp = null;
		////System.out.println("Length ShipmentLine:"+test);
		for (int i = 0; i < shipmentLineList.getLength(); i++) {
			Element shipmentLineElement = (Element) shipmentLineList.item(i);
			String item = shipmentLineElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
			String Quantity = shipmentLineElement.getAttribute(VSIConstants.ATTR_QUANTITY);
			Double dQty = Double.parseDouble(Quantity);
			NodeList orderLineList	= outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			
			ItemDetailForShip.add(item.concat(",").concat(Integer.toString(Double.valueOf(Quantity).intValue())));

				for (int j=0; j< orderLineList.getLength(); j++){
			
					Element orderLineElement = (Element) orderLineList.item(j);
					
					String custPoNo = orderLineElement.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
					
					if(customerPoNo.equalsIgnoreCase(custPoNo)){
						
					String minLineStatus = orderLineElement.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
					String intQuantity = orderLineElement.getAttribute("OrderedQty");
					Double dIntQty = Double.parseDouble(intQuantity);
					//OMS-1304 using the correct order line element
					String lineType=orderLineElement.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				    ////System.out.println("LineTYPE::::"+lineType);
					String status = getStatus(lineType);
					Element itemElement = (Element) orderLineElement.getElementsByTagName(VSIConstants.ELE_ITEM).item(0);
						String itemID = itemElement.getAttribute(VSIConstants.ATTR_ITEM_ID);
						
						int dval = Double.compare(dQty,dIntQty);
						
						////System.out.println("minLine"+minLineStatus);
						
						if(itemID.equals(item) && minLineStatus.equals(status) && dval==0){
							
							//OMS-1304 : Start
							String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							if(logger.isDebugEnabled()){
								logger.debug("OrderLineKey : "+orderLineKey +"LineType :"+lineType);
							}
							hmLineTypeMap.put(orderLineKey, lineType);
							//OMS-1304 : End

							if (status.equals("1100.500") || status.equals("1100.100")){
								
								////OMS-1304 moving the line outside if 
								//String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
								////System.out.println("condition"+orderLinekeyMap.containsValue(orderLineKey));
								if (!orderLinekeyMap.containsValue(orderLineKey)){
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
									orderLineKeyTemp=orderLineKey;
									
									break;
								}
								//Setting the attributes in the inXML
								
							}else{
								
								
								/*NodeList orderStatusList = orderLineElement.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
								//OMS-1791 :Start
								String releaseKey=null;
								for (int k=0; k< orderStatusList.getLength(); k++){
									Element orderStatusElement = (Element) orderStatusList.item(k);
									
									if(status.equals(orderStatusElement.getAttribute("Status")))
									{	
									  releaseKey = orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
									}
								}
								//OMS-1791 :End
								////System.out.println("ReleaseKEY"+releaseKey);
								////OMS-1304 moving the line outside if 
								//String orderLineKey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
								////System.out.println("condition"+orderLinekeyMap.containsValue(orderLineKey));
								if (!orderLinekeyMap.containsValue(orderLineKey)){
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, releaseKey);
									orderLineKeyTemp=orderLineKey;
									////System.out.println("orderLineKey is "+orderLineKey);
									
									break;
									

									
								}*/
								//Setting the attributes in the inXML

								
								
								//NodeList orderStatusList = orderLineElement.getElementsByTagName(VSIConstants.ELE_ORDER_STATUS);
								String Xpath="/OrderLine/OrderStatuses/OrderStatus[@Status='"+status+"']";
								
								logger.debug(Xpath);
								NodeList orderStatusList = XMLUtil.getNodeListByXpath(XMLUtil.getDocumentForElement(orderLineElement), Xpath);
								int size=orderStatusList.getLength();
								logger.debug("Size"+orderStatusList.getLength());
								String releaseKey=null;
								if(size==1)
								{
									//OMS-1907 : Start
									if (!orderLinekeyMap.containsValue(orderLineKey)){
									Element orderStatusElement = (Element) orderStatusList.item(0);
									releaseKey = orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
									shipmentLineElement.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, releaseKey);
									orderLineKeyTemp=orderLineKey;
									////System.out.println("orderLineKey is "+orderLineKey);								
									break;	
									}
									//OMS-1907 : End				
								}
								else
								{
									
									for (int k=0; k< orderStatusList.getLength(); k++){
										Element orderStatusElement = (Element) orderStatusList.item(k);
										
										if(status.equals(orderStatusElement.getAttribute("Status")))
										{	
										  releaseKey = orderStatusElement.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
										}
									if (!orderLinekeyMap.containsValue(orderLineKey)){
										Node copyNode = inXML.importNode(shipmentLineElement, true);
										shipmentLineElement.getParentNode().appendChild(copyNode);
										Element eleShipNew = (Element) copyNode;
										//Node eleShipNew = inXML.importNode(shipmentLineElement, true);
										//shipmentLineElement.getParentNode().appendChild(eleShipNew);
										eleShipNew.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
										eleShipNew.setAttribute(VSIConstants.ATTR_QUANTITY, orderStatusElement.getAttribute(VSIConstants.ATTR_STATUS_QUANTITY) );
										eleShipNew.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, orderLineKey);
										eleShipNew.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, releaseKey);
										if(k==(size-1))
										{	
										shipmentLineElement.getParentNode().removeChild(shipmentLineElement);	
										orderLineKeyTemp=orderLineKey;
										////System.out.println("orderLineKey is "+orderLineKey);								
										break;		
										}
									}
									}
								}
								
								//Setting the attributes in the inXML
								
							
								
							}
							
							
							
						}
						
						
					}
				
				}// end if cust PO Check
				orderLinekeyMap.put(i, orderLineKeyTemp);
				////System.out.println("map" +orderLinekeyMap);		
		}//end for j loop
		
		//OMS-808
		env.setTxnObject("EnvItemsShipped", ItemDetailForShip);
				
		////System.out.println(" Printing Output XML"+XmlUtils.getString(inXML));
		//Invoking CreateShipmentAPI
		//OMS-2225:Inventory Issue in OMS related to Inventory movement : Start
		 ArrayList<Element> listShipmentFlag=VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_SHIPMENT_CODE_TYPE, VSIConstants.ATTR_STS_SHIPMENT_FLOW, VSIConstants.ATTR_DEFAULT);
			if(!listShipmentFlag.isEmpty()){
			Element eleCommonCode=listShipmentFlag.get(0);
				String strShipmentFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				if(!YFCCommon.isStringVoid(strShipmentFlag) && VSIConstants.FLAG_N.equalsIgnoreCase(strShipmentFlag)){
					//OMS-1304 : Start
					transferInventoryOwnership(env,inXML,hmLineTypeMap);
					//OMS-1304 : End
				}
				else
				{
					transferInventoryOwnershipNewLogic(env,inXML,hmLineTypeMap);
				}
			}
		//OMS-2225: High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC  : End
		Document createShipmentDoc = api.invoke(env, VSIConstants.API_CREATE_SHIPMENT,inXML);
		////System.out.println("createShipment Output"+ createShipmentDoc);
		

					}else{
						throw new YFSException(
								"EXTN_ERROR",
								"EXTN_ERROR",
								"Order No. not found");
						}
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Parse Error");
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Client Creation Error");
		} catch (YFSException e) {
			e.printStackTrace();
			//OMS-1894: Start
			throw new YFSException(e.getMessage());
			/*throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Order No. Not found");*/
			//OMS-1894: End
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e){
			e.printStackTrace();
			//OMS-1894: Start
			throw new YFSException(e.getMessage());
			/*throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");*/
			//OMS-1894: End

		}

		
	}//End vsiGetOrderNumberForShipment

	
	public String getStatus(String lineType){
		
		if(lineType.equals("SHIP_TO_STORE"))
		{
			String status="3200.500";
			return status;
			
		}else if(lineType.equals("PICK_IN_STORE")) {
			
			String status="3200.500";
			return status;
			
		}else{
			
			String status="1100.100";
			return status;
		}
		
		
	}
	
	//OMS-1304 : Start
	private void transferInventoryOwnership(YFSEnvironment env, Document docShipment, Map<String,String> hmLineTypeMap) throws YFSException, RemoteException, YIFClientCreationException {
		
		if(logger.isDebugEnabled()){
			logger.debug("Inside transferInventoryOwnership :input "+ XMLUtil.getXMLString(docShipment));
			logger.debug("LineKey LineType Map "+ hmLineTypeMap.toString());
		}
		boolean isOwnershipChangeReq=false;
		Element eleShipment = docShipment.getDocumentElement();
		Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, "ShipmentLines");
		ArrayList<Element> arrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines, "ShipmentLine");
		String strEnterprise = eleShipment.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		
		
		//Form input for getInventorySupply
		Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "GOOD");
		eleGetInventorySupply.setAttribute(VSIConstants.ATTR_UOM, "EACH");
		eleGetInventorySupply.setAttribute("SupplyType", "ONHAND");
		eleGetInventorySupply.setAttribute("ShipNode", eleShipment.getAttribute("ShipNode"));
		
		//Form input for transferInventoryOwnership
		Document docTransferInventory = SCXmlUtil.createDocument("Items");
		Element eleTransferInventory = docTransferInventory.getDocumentElement();
		
		
		for(Element eleShipmentLine:arrShipmentLine) {
			
			String strOrderLineKey=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			String strLineType=hmLineTypeMap.get(strOrderLineKey);
			if(logger.isDebugEnabled()){
				logger.debug("LineKey :LineType  "+ strOrderLineKey +" : " +strLineType);
			}
			if(!YFCCommon.isStringVoid(strLineType) && strLineType.equals("SHIP_TO_STORE"))
					continue;
			else if(!YFCCommon.isStringVoid(strLineType) && strLineType.equals("PICK_IN_STORE")){
				
				isOwnershipChangeReq=true;
				//form input for getInventorySupply
				String strItemId = eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
				Double dblRequiredQty = SCXmlUtil.getDoubleAttribute(eleShipmentLine, "Quantity");
				String strFromOrganizationCode = "VSIINV";
				
				
				eleGetInventorySupply.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
				
				dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
				
				if(dblRequiredQty>0) {
					
					if (strEnterprise.equalsIgnoreCase("ADP")) {
						strFromOrganizationCode="ADPINV";
						
					}
					else if (strEnterprise.equalsIgnoreCase("MCL") )
					{
						strFromOrganizationCode="MCLINV";
						
					}
					else {
						strFromOrganizationCode="DTCINV";
					}
					dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
				}
				
				if(dblRequiredQty>0) {
					if(strEnterprise.equalsIgnoreCase("ADP")) {
						strFromOrganizationCode="DTCINV";
						dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
					}
					else if (!strEnterprise.equalsIgnoreCase("MCL"))
					{					
						strFromOrganizationCode="MCLINV";
						dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
					}
				}
				
				if(dblRequiredQty>0) 
				{
					if(strEnterprise.equalsIgnoreCase("ADP"))
					{
						strFromOrganizationCode="MCLINV";
						dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
					}
					else 
					{
						//add VSIINV to transferInventoryOwnership.
						strFromOrganizationCode = "VSIINV";
						Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, "Item");
						eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "GOOD");
						eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, "EACH");
						eleTransferItems.setAttribute("SupplyType", "ONHAND");
						eleTransferItems.setAttribute("ShipNode", eleShipment.getAttribute("ShipNode"));
						eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
						eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
						eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
						eleTransferItems.setAttribute("Quantity", Double.toString(dblRequiredQty));
						dblRequiredQty =0.0;
					}
				}
				
				if(dblRequiredQty>0) 
				{
						//add VSIINV to transferInventoryOwnership.
						strFromOrganizationCode = "VSIINV";
						Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, "Item");
						eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "GOOD");
						eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, "EACH");
						eleTransferItems.setAttribute("SupplyType", "ONHAND");
						eleTransferItems.setAttribute("ShipNode", eleShipment.getAttribute("ShipNode"));
						eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
						eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
						eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
						eleTransferItems.setAttribute("Quantity", Double.toString(dblRequiredQty));
						dblRequiredQty =0.0;
				}
				
			}
		}
		//invoke transferInventoryOwnership API
		if(isOwnershipChangeReq){
			VSIUtils.invokeAPI(env, "transferInventoryOwnership", docTransferInventory);
		}
	}
	
	
	private Double invokeGetInventorySupply(YFSEnvironment env, Document docGetInventorySupply, Double dblRequiredQty,Element eleTransferInventory,String strFromOrganizationCode,String strToEnterprise)
			throws YIFClientCreationException, RemoteException {
		Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
		eleGetInventorySupply.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strFromOrganizationCode);
		eleGetInventorySupply.setAttribute("InventoryItemKey", "");
		Document docInventorySupplyOutput = null;
		try {
			docInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
		}catch(Exception e){
			String strMessage = e.getMessage();
			if(strMessage.contains("INV80_042")) {
				//this issue is because of a product bug. Should be resolved in future with ARH-172.
				//Ignore the exception.
			}else {
				throw e;
			}
		}
		if(!YFCCommon.isVoid(docInventorySupplyOutput) ){
			Element eleInventorySupplyOutput = docInventorySupplyOutput.getDocumentElement();
			Element eleInventoryItemSupplies = SCXmlUtil.getChildElement(eleInventorySupplyOutput, "Supplies");
			ArrayList<Element> arrInventorySupply = SCXmlUtil.getChildren(eleInventoryItemSupplies, "InventorySupply");
			
			
			for(Element eleInventoryItemSupply:arrInventorySupply) {
				Double dblAvailableQuantity = SCXmlUtil.getDoubleAttribute(eleInventoryItemSupply, "Quantity");
				if(dblAvailableQuantity != 0.0)
				{
					//add items to transferInventoryOwnership
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, "Item");
					eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "GOOD");
					eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, "EACH");
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute("ShipNode", eleInventoryItemSupply.getAttribute("ShipNode"));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strToEnterprise);
					eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, eleInventorySupplyOutput.getAttribute("ItemID"));
					
					if(dblAvailableQuantity>dblRequiredQty) {
						eleTransferItems.setAttribute("Quantity", Double.toString(dblRequiredQty));
					}else {
						eleTransferItems.setAttribute("Quantity", Double.toString(dblAvailableQuantity));
					}
					dblRequiredQty = dblRequiredQty-dblAvailableQuantity;
					break;
				}
				
			}
		}
		return dblRequiredQty;
	}
	
	//OMS-1304 : End
	//OMS-2225: High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC:Start
		private void transferInventoryOwnershipNewLogic(YFSEnvironment env, Document docShipment, Map<String,String> hmLineTypeMap)
				throws YFSException, RemoteException, YIFClientCreationException {
			if(logger.isDebugEnabled()){
				logger.debug("Inside transferInventoryOwnership :input "+ XMLUtil.getXMLString(docShipment));
				logger.debug("LineKey LineType Map "+ hmLineTypeMap.toString());
			}
			boolean isOwnershipChangeReq=false;
			Element eleShipment = docShipment.getDocumentElement();
			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment, "ShipmentLines");
			ArrayList<Element> arrShipmentLine = SCXmlUtil.getChildren(eleShipmentLines, "ShipmentLine");
			String strEnterprise = eleShipment.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			
			
			//Form input for getInventorySupply
			Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
			Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
			eleGetInventorySupply.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, "GOOD");
			eleGetInventorySupply.setAttribute(VSIConstants.ATTR_UOM, "EACH");
			eleGetInventorySupply.setAttribute("SupplyType", "ONHAND");
			eleGetInventorySupply.setAttribute("ShipNode", eleShipment.getAttribute("ShipNode"));
			
			//Form input for transferInventoryOwnership
			Document docTransferInventory = SCXmlUtil.createDocument("Items");
			Element eleTransferInventory = docTransferInventory.getDocumentElement();
			
			
			for(Element eleShipmentLine:arrShipmentLine) {
				
				String strOrderLineKey=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				String strLineType=hmLineTypeMap.get(strOrderLineKey);
				if(logger.isDebugEnabled()){
					logger.debug("LineKey :LineType  "+ strOrderLineKey +" : " +strLineType);
				}
				if(!YFCCommon.isStringVoid(strLineType) && strLineType.equals("SHIP_TO_STORE"))
						continue;
				else if(!YFCCommon.isStringVoid(strLineType) && strLineType.equals("PICK_IN_STORE")){
					
			isOwnershipChangeReq=true;
			String strItemId = eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
			Double dblRequiredQty = SCXmlUtil.getDoubleAttribute(eleShipmentLine, "Quantity");
			String strFromOrganizationCode = "VSIINV";
			eleGetInventorySupply.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
			dblRequiredQty = invokeGetInventorySupply(env, docGetInventorySupply, dblRequiredQty,eleTransferInventory,strFromOrganizationCode,strEnterprise);
			if (dblRequiredQty > 0) {

					if (VSIConstants.ENT_ADP.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = "ADPINV";

					} else if ("MCL".equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = "MCLINV";

					} else {
						strFromOrganizationCode = "DTCINV";
					}
					dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
							eleTransferInventory, strFromOrganizationCode, strEnterprise);
				}

				if (dblRequiredQty > 0) {
					if (VSIConstants.ENT_ADP.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = "DTCINV";
						dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					} else if (VSIConstants.ENT_NAVY_EXCHANGE.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = "MCLINV";
						dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					}
				}

				if (dblRequiredQty > 0) {
					if (VSIConstants.ENT_ADP.equalsIgnoreCase(strEnterprise)) {
						strFromOrganizationCode = "MCLINV";
						dblRequiredQty = invokeGetInventorySupplyNewLogic(env, docGetInventorySupply, dblRequiredQty,
								eleTransferInventory, strFromOrganizationCode, strEnterprise);
					} else {
						// add VSIINV to transferInventoryOwnership.
						strFromOrganizationCode = "VSIINV";
						Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, VSIConstants.ELE_ITEM);
						eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, VSIConstants.GOOD);
						eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
						eleTransferItems.setAttribute("SupplyType", "ONHAND");
						eleTransferItems.setAttribute(VSIConstants.ATTR_SHIP_NODE, eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE));
						eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
						eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
						eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
						eleTransferItems.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(dblRequiredQty));
						dblRequiredQty = 0.0;
					}
				}

				if (dblRequiredQty > 0) {
					// add VSIINV to transferInventoryOwnership.
					strFromOrganizationCode = "VSIINV";
					Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, VSIConstants.ELE_ITEM);
					eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, VSIConstants.GOOD);
					eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
					eleTransferItems.setAttribute("SupplyType", "ONHAND");
					eleTransferItems.setAttribute(VSIConstants.ATTR_SHIP_NODE, eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE));
					eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
					eleTransferItems.setAttribute("ToOrganizationCode", strEnterprise);
					eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
					eleTransferItems.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(dblRequiredQty));
					dblRequiredQty = 0.0;
				}
			}
			}
			// invoke transferInventoryOwnership API
			VSIUtils.invokeAPI(env, "transferInventoryOwnership", docTransferInventory);
		}
		private Double invokeGetInventorySupplyNewLogic(YFSEnvironment env, Document docGetInventorySupply, Double dblRequiredQty,
				Element eleTransferInventory, String strFromOrganizationCode, String strToEnterprise)
				throws YIFClientCreationException, RemoteException {
			Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
			eleGetInventorySupply.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strFromOrganizationCode);
			eleGetInventorySupply.setAttribute("InventoryItemKey", "");
			Document docInventorySupplyOutput = null;
			try {
				docInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
			} catch (Exception e) {
				String strMessage = e.getMessage();
				if (strMessage.contains("INV80_042")) {
					// this issue is because of a product bug. Should be resolved in
					// future with ARH-172.
					// Ignore the exception.
				} else {
					throw e;
				}
			}
			if (!YFCCommon.isVoid(docInventorySupplyOutput)) {
				Element eleInventorySupplyOutput = docInventorySupplyOutput.getDocumentElement();
				Element eleInventoryItemSupplies = SCXmlUtil.getChildElement(eleInventorySupplyOutput, VSIConstants.ELE_SUPPLIES);
				ArrayList<Element> arrInventorySupply = SCXmlUtil.getChildren(eleInventoryItemSupplies, "InventorySupply");

				for (Element eleInventoryItemSupply : arrInventorySupply) {
					Double dblAvailableQuantity = SCXmlUtil.getDoubleAttribute(eleInventoryItemSupply, VSIConstants.ATTR_QUANTITY);
					// Sayeesh - changes made here
					if (dblAvailableQuantity>0.0) {
						// add items to transferInventoryOwnership
						Element eleTransferItems = SCXmlUtil.createChild(eleTransferInventory, VSIConstants.ELE_ITEM);
						eleTransferItems.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, VSIConstants.GOOD);
						eleTransferItems.setAttribute(VSIConstants.ATTR_UOM, VSIConstants.UOM_EACH);
						eleTransferItems.setAttribute("SupplyType", "ONHAND");
						eleTransferItems.setAttribute(VSIConstants.ATTR_SHIP_NODE, eleInventoryItemSupply.getAttribute(VSIConstants.ATTR_SHIP_NODE));
						eleTransferItems.setAttribute("FromOrganizationCode", strFromOrganizationCode);
						eleTransferItems.setAttribute("ToOrganizationCode", strToEnterprise);
						eleTransferItems.setAttribute(VSIConstants.ATTR_ITEM_ID, eleInventorySupplyOutput.getAttribute(VSIConstants.ATTR_ITEM_ID));

						if (dblAvailableQuantity > dblRequiredQty) {
							eleTransferItems.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(dblRequiredQty));
							dblRequiredQty = 0.0;
						} else {
							eleTransferItems.setAttribute(VSIConstants.ATTR_QUANTITY, Double.toString(dblAvailableQuantity));
							dblRequiredQty = dblRequiredQty - dblAvailableQuantity;
						}
						break;
					}

				}
			}
			return dblRequiredQty;
		}

	//OMS-2225:High Priority: Inventory Issue in OMS causing 0 inventory STH orders dropped to DC: End	
	
}// End Class
