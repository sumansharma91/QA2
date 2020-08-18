package com.vsi.som.shipment;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSISOMProcessShortage {
	
	private static YFCLogCategory log = YFCLogCategory.instance(VSISOMProcessShortage.class);
	private static final String TAG = VSISOMProcessShortage.class.getSimpleName();
	YIFApi api;
	
	public Document processShortage(YFSEnvironment env, Document inXML){
		
		printLogs("================Inside VSISOMProcessShortage Class and processShortage Method================");
		printLogs("Printing Input XML :"+SCXmlUtil.getString(inXML));
		
		try{
			
			Element eleInXML=inXML.getDocumentElement();
			String strDeliveryMethod=eleInXML.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
			String strStatus=eleInXML.getAttribute(VSIConstants.ATTR_STATUS);
			String strShipNode=eleInXML.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			
			String strOrderHeaderKey=null;
			
			if("1100.70.06.20".equals(strStatus) || "1100.70.06.70".equals(strStatus)){
				
				if("1100.70.06.20".equals(strStatus)){
					printLogs("Shortage Recorded during Backroom Pick Process");
				}else if("1100.70.06.70".equals(strStatus)){
					printLogs("Shortage Recorded during Pack Shipment Process");
				}
				
				Document docAdjInvIn=XMLUtil.createDocument(VSIConstants.ELE_ITEMS);
				Element eleAdjInvIn=docAdjInvIn.getDocumentElement();
				
				Document docChngOrdIn=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleChngOrdIn=docChngOrdIn.getDocumentElement();
				Element eleOrderLines=SCXmlUtil.createChild(eleChngOrdIn, VSIConstants.ELE_ORDER_LINES);
				
				Element eleShipmentLines=SCXmlUtil.getChildElement(eleInXML, VSIConstants.ELE_SHIPMENT_LINES);
				NodeList nlShipmentLine=eleShipmentLines.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE);
				for(int i=0; i<nlShipmentLine.getLength(); i++){
					Element eleShipmentLine=(Element) nlShipmentLine.item(i);
					String strOHK=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					if(YFCCommon.isVoid(strOrderHeaderKey)){
						strOrderHeaderKey=strOHK;
					}
					String strQtyReduced=eleShipmentLine.getAttribute("QuantityReduced");
					double dQtyReduced=Double.parseDouble(strQtyReduced);					
					if(dQtyReduced>0){				
						String strAdjType=null;
						String strQtyUpdt=null;
						String strItemID=eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
						String strQuantity=eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY);
						int iQty=Integer.parseInt(strQuantity);
						if(iQty==0){
							strAdjType="ABSOLUTE";
							strQtyUpdt=strQuantity;
						}else if(iQty>0){
							strAdjType="ADJUSTMENT";
							double dQtyUpdt=dQtyReduced * (-1);
							strQtyUpdt=Double.toString(dQtyUpdt);
						}						
						String strProductClass=eleShipmentLine.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
						String strUOM=eleShipmentLine.getAttribute(VSIConstants.ATTR_UOM);						
						
						Element eleItem=SCXmlUtil.createChild(eleAdjInvIn, VSIConstants.ELE_ITEM);
						eleItem.setAttribute("AdjustmentType", strAdjType);
						eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemID);
						eleItem.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSIINV");
						eleItem.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, strProductClass);
						eleItem.setAttribute(VSIConstants.ATTR_QUANTITY, strQtyUpdt);
						eleItem.setAttribute(VSIConstants.ATTR_SHIP_NODE, strShipNode);
						eleItem.setAttribute("SupplyType", "ONHAND");
						eleItem.setAttribute(VSIConstants.ATTR_UOM, strUOM);
						
						Element eleOrderLine=SCXmlUtil.createChild(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
						String strOrdLnKey=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
						eleOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
						eleOrderLine.setAttribute(VSIConstants.ATTR_FROM_STATUS, VSIConstants.STATUS_BACKORDERED);
						eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrdLnKey);
						eleOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, strQuantity);
					}
				}
				
				NodeList nlItems=eleAdjInvIn.getElementsByTagName(VSIConstants.ELE_ITEM);
				if(nlItems.getLength()>0){
					printLogs("Input to adjustInventory API for Backroom Pick scenario: "+SCXmlUtil.getString(docAdjInvIn));
					api = YIFClientFactory.getInstance().getLocalApi();
					api.invoke(env, "adjustInventory", docAdjInvIn);
					printLogs("adjustInventory API was invoked successfully for Backroom Pick scenario");
				}
				
				if(VSIConstants.ATTR_DEL_METHOD_PICK.equalsIgnoreCase(strDeliveryMethod)){
					printLogs("Status is "+strStatus+" and Delivery Method is PICK");
					NodeList nlOrderLine=eleOrderLines.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
					if(nlOrderLine.getLength()>0){
						printLogs("changeOrder API will be invoked");
						eleChngOrdIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
						eleChngOrdIn.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "VSI_CANCEL_NO_INV");
						eleChngOrdIn.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, "VSI_CANCEL_NO_INV");
						printLogs("Input to changeOrder API for Backroom Pick scenario: "+SCXmlUtil.getString(docChngOrdIn));
						api = YIFClientFactory.getInstance().getLocalApi();
						api.invoke(env, VSIConstants.API_CHANGE_ORDER, docChngOrdIn);
						printLogs("changeOrder API was invoked successfully for Backroom Pick scenario");
					}
				}else if(VSIConstants.ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)){
					printLogs("Status is "+strStatus+" and Delivery Method is SHP");
					printLogs("JDA Reverse Allocation Service will be invoked");
					printLogs("Input to VSISOMJDAReverseAllocation_Sync Service: "+SCXmlUtil.getString(inXML));
					VSIUtils.invokeService(env, "VSISOMJDAReverseAllocation_Sync", inXML);
					printLogs("JDA Reverse Allocation Service was invoked successfully");
				}
			}else if("1100.70.06.30".equals(strStatus)){
				printLogs("Shortage Recorded during Customer Pick Process");
				
				Document docAdjInvIn=XMLUtil.createDocument(VSIConstants.ELE_ITEMS);
				Element eleAdjInvIn=docAdjInvIn.getDocumentElement();
				
				Document docChngOrdIn=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleChngOrdIn=docChngOrdIn.getDocumentElement();
				Element eleOrderLines=SCXmlUtil.createChild(eleChngOrdIn, VSIConstants.ELE_ORDER_LINES);
				
				boolean bIsCancellation=false;
								
				Element eleShipmentLines=SCXmlUtil.getChildElement(eleInXML, VSIConstants.ELE_SHIPMENT_LINES);
				NodeList nlShipmentLine=eleShipmentLines.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE);
				for(int i=0; i<nlShipmentLine.getLength(); i++){
					Element eleShipmentLine=(Element) nlShipmentLine.item(i);
					String strOHK=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					if(YFCCommon.isVoid(strOrderHeaderKey)){
						strOrderHeaderKey=strOHK;
					}
					String strQtyReduced=eleShipmentLine.getAttribute("QuantityReduced");
					double dQtyReduced=Double.parseDouble(strQtyReduced);
					if(dQtyReduced>0){
						String strOrdLnKey=eleShipmentLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
						
						Document docGetOrdLnLstIn=XMLUtil.createDocument(VSIConstants.ELE_ORDER_LINE);
						Element eleGetOrdLnLstIn=docGetOrdLnLstIn.getDocumentElement();
						eleGetOrdLnLstIn.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrdLnKey);
						
						printLogs("Input to getOrderLineList API: "+SCXmlUtil.getString(docGetOrdLnLstIn));
						api = YIFClientFactory.getInstance().getLocalApi();
						Document docGetOrdLnLstOut = api.invoke(env, "getOrderLineList", docGetOrdLnLstIn);
						printLogs("Output from getOrderLineList API: "+SCXmlUtil.getString(docGetOrdLnLstOut));
						
						Element eleGetOrdLnLstOut=docGetOrdLnLstOut.getDocumentElement();
						Element eleOrdLn=SCXmlUtil.getChildElement(eleGetOrdLnLstOut, VSIConstants.ELE_ORDER_LINE);
						String strMinLineSts=eleOrdLn.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
						String strCustPONo=eleOrdLn.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
						
						String strItemID=eleShipmentLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
						
						if(VSIConstants.STATUS_BACKORDERED.equals(strMinLineSts) || VSIConstants.STATUS_BACKORDERED_FROM_NODE.equals(strMinLineSts)){
							
							printLogs("BackOrder Scenario");
							String strAdjType=null;
							String strQtyUpdt=null;
							
							String strQuantity=eleShipmentLine.getAttribute(VSIConstants.ATTR_QUANTITY);
							int iQty=Integer.parseInt(strQuantity);
							if(iQty==0){
								strAdjType="ABSOLUTE";
								strQtyUpdt=strQuantity;
							}else if(iQty>0){
								strAdjType="ADJUSTMENT";
								double dQtyUpdt=dQtyReduced * (-1);
								strQtyUpdt=Double.toString(dQtyUpdt);
							}							
							String strProductClass=eleShipmentLine.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
							String strUOM=eleShipmentLine.getAttribute(VSIConstants.ATTR_UOM);
							
							Element eleItem=SCXmlUtil.createChild(eleAdjInvIn, VSIConstants.ELE_ITEM);
							eleItem.setAttribute("AdjustmentType", strAdjType);
							eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemID);
							eleItem.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSIINV");
							eleItem.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, strProductClass);
							eleItem.setAttribute(VSIConstants.ATTR_QUANTITY, strQtyUpdt);
							eleItem.setAttribute(VSIConstants.ATTR_SHIP_NODE, strShipNode);
							eleItem.setAttribute("SupplyType", "ONHAND");
							eleItem.setAttribute(VSIConstants.ATTR_UOM, strUOM);
							
							Element eleOrderLine=SCXmlUtil.createChild(eleOrderLines, VSIConstants.ELE_ORDER_LINE);							
							eleOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
							eleOrderLine.setAttribute(VSIConstants.ATTR_FROM_STATUS, VSIConstants.STATUS_BACKORDERED);
							eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrdLnKey);
							eleOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, strQuantity);
							
						}else{
							
							printLogs("Non-BackOrder Scenario");
							String strOrderedQty=eleOrdLn.getAttribute(VSIConstants.ATTR_ORD_QTY);
							String strOrigOrdQty=eleOrdLn.getAttribute(VSIConstants.ATTR_ORIGINAL_ORDERED_QTY);
							if(!strOrderedQty.equals(strOrigOrdQty)){
								bIsCancellation=true;
								
								//SOM Restock changes -- Start
								String strQueueId="VSI_RESTOCK_"+strShipNode;
								Document docInboxIn=XMLUtil.createDocument(VSIConstants.ELE_INBOX);
								Element eleInboxIn=docInboxIn.getDocumentElement();
								eleInboxIn.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, VSIConstants.FLAG_Y);
								eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
								eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrdLnKey);
								eleInboxIn.setAttribute(VSIConstants.ATTR_ORDER_NO, strCustPONo);
								eleInboxIn.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Restock");
								eleInboxIn.setAttribute(VSIConstants.ATTR_QUEUE_ID, strQueueId);
								eleInboxIn.setAttribute(VSIConstants.ATTR_SHIPNODE_KEY, strShipNode);
								Element eleInbxRfrncsLst=SCXmlUtil.createChild(eleInboxIn, VSIConstants.ELE_INBOX_REFERANCES_LIST);
								Element eleItemReference=SCXmlUtil.createChild(eleInbxRfrncsLst, VSIConstants.ELE_INBOX_REFERANCES);
								eleItemReference.setAttribute(VSIConstants.ATTR_NAME, VSIConstants.ATTR_ITEM_ID);
								eleItemReference.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "TEXT");
								eleItemReference.setAttribute(VSIConstants.ATTR_VALUE, strItemID);
								Element eleQtyReference=SCXmlUtil.createChild(eleInbxRfrncsLst, VSIConstants.ELE_INBOX_REFERANCES);
								eleQtyReference.setAttribute(VSIConstants.ATTR_NAME, "Qty");
								eleQtyReference.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "TEXT");
								eleQtyReference.setAttribute(VSIConstants.ATTR_VALUE, strQtyReduced);
								
								printLogs("createException API Input: "+SCXmlUtil.getString(docInboxIn));								
								
								VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION, docInboxIn);
								
								printLogs("createException API was invoked successfully");								
								//SOM Restock changes -- End
							}
						}
					}
				}
				NodeList nlItems=eleAdjInvIn.getElementsByTagName(VSIConstants.ELE_ITEM);
				if(nlItems.getLength()>0){
					printLogs("Input XML to adjustInventory API for Customer Pick scenario: "+SCXmlUtil.getString(docAdjInvIn));
					api = YIFClientFactory.getInstance().getLocalApi();
					api.invoke(env, "adjustInventory", docAdjInvIn);
					printLogs("adjustInventory API was invoked successfully for Customer Pick scenario");
				}
				NodeList nlOrderLine=eleOrderLines.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				if(nlOrderLine.getLength()>0){
					eleChngOrdIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
					eleChngOrdIn.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "VSI_CANCEL_NO_INV");
					eleChngOrdIn.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, "VSI_CANCEL_NO_INV");
					printLogs("Input to changeOrder API for Customer Pick scenario: "+SCXmlUtil.getString(docChngOrdIn));
					api = YIFClientFactory.getInstance().getLocalApi();
					api.invoke(env, VSIConstants.API_CHANGE_ORDER, docChngOrdIn);
					printLogs("changeOrder API was invoked successfully for Customer Pick scenario");
				}
				if(bIsCancellation){
					processShortageCancellation(env, inXML, strOrderHeaderKey);
				}
			}			
		}catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException();
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException();
		}		
		
		printLogs("================Exiting VSISOMProcessShortage Class and processShortage Method================");
		return inXML;
		
	}

	private void processShortageCancellation(YFSEnvironment env,
			Document inXML, String strOHK) throws ParserConfigurationException,
			YIFClientCreationException, RemoteException, TransformerException,
			Exception {
		
		printLogs("================Inside processShortageCancellation Method================");
		
		Document docGetOrdLstIn=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleGetOrdLstIn=docGetOrdLstIn.getDocumentElement();
		eleGetOrdLstIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOHK);
		
		printLogs("Input to getOrderList API: "+SCXmlUtil.getString(docGetOrdLstIn));
		Document docGetOrdLstOut = VSIUtils.invokeAPI(env,"global/template/api/getOrderList_SOMProcessShortage.xml","getOrderList", docGetOrdLstIn);
		printLogs("Output from getOrderList API: "+SCXmlUtil.getString(docGetOrdLstOut));
		
		Element eleGetOrdLstOut=docGetOrdLstOut.getDocumentElement();
		eleGetOrdLstOut.setAttribute(VSIConstants.ATTR_EMAIL_TYPE, "CCM");
		eleGetOrdLstOut.setAttribute(VSIConstants.MESSAGE_TYPE, VSIConstants.ACTION_CAPS_CANCEL);
		Element eleOrder=(Element)eleGetOrdLstOut.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		eleOrder.setAttribute(VSIConstants.MESSAGE_TYPE, VSIConstants.ACTION_CAPS_CANCEL);
		String strMinOrdSts=eleOrder.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
		String strMaxOrdSts=eleOrder.getAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS);
		if(VSIConstants.STATUS_CANCEL.equals(strMinOrdSts) && VSIConstants.STATUS_CANCEL.equals(strMaxOrdSts)){
			printLogs("Order is fully Cancelled");
			eleOrder.setAttribute("Uncommit", VSIConstants.FLAG_Y);
		}
		
		Element eleOrderLines=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
		NodeList nlOrderLine=eleOrderLines.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);								
		for(int j=0; j<nlOrderLine.getLength(); j++){
			boolean bLnToBeRemoved=true;
			Element eleOrderLine=(Element) nlOrderLine.item(j);
			String strOrdLneKey=eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			Element eleShpmntLnFrmIn=XMLUtil.getElementByXPath(inXML, "/Shipment/ShipmentLines/ShipmentLine[@OrderLineKey='"+strOrdLneKey+"']");
			if(!YFCCommon.isVoid(eleShpmntLnFrmIn)){
				String strQtyRducd=eleShpmntLnFrmIn.getAttribute("QuantityReduced");
				double dQtyRducd=Double.parseDouble(strQtyRducd);				
				if(dQtyRducd>0){
					bLnToBeRemoved=false;
					eleOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, strQtyRducd);
				}
			}
			if(bLnToBeRemoved){
				printLogs("OrderLine with OrderLineKey as "+strOrdLneKey+" will be removed from getOrderList Output");
				eleOrderLine.getParentNode().removeChild(eleOrderLine);
				j--;
			}
		}
		printLogs("Input to VSISOMCancelPublishToJDA service: "+SCXmlUtil.getString(docGetOrdLstOut));
		VSIUtils.invokeService(env, "VSISOMCancelPublishToJDA", docGetOrdLstOut);
		printLogs("Cancellation XML was sent to service VSISOMCancelPublishToJDA service successfully");
		
		printLogs("================Exiting processShortageCancellation Method================");
	}
	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}

}
