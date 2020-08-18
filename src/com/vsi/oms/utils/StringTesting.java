package com.vsi.oms.utils;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class is mainly used to support String testing. Input can be either order header key
 * or order no, document type, enterprise code.
 * Input:
 * 
 * 
 * @author IBM
 *
 */
public class StringTesting  implements VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(StringTesting.class);
	public String strOrderEnteredBy = "";
	
	private static YIFApi api;


	
	public Document stringTesting (YFSEnvironment yfsEnv, Document docOrder) throws
	Exception {
		log.endTimer("VSIProcessShipConfirmFromWMS.Stringtesting : START");
		
		Element eleOrder = docOrder.getDocumentElement();
		String strTakeTill = eleOrder.getAttribute("TakeTill");
		String strOrderChannel = eleOrder.getAttribute("OrderChannel");
		Document docTemplate = (Document)yfsEnv.getApiTemplate("VSIStringTesting");
		
		YFSEnvironment env = createEnvironment("admin",
				"admin");
		Document docOrderOutput = null;
		if(YFCCommon.isVoid(strOrderChannel)){
		docOrderOutput = VSIUtils.invokeAPI(env, "createOrder", docOrder);
		}else{
			docOrderOutput = docOrder;
		}
		
		
		
		//Document docOrderOutput = VSIUtils.invokeAPI(env, "createOrder", docOrder);
		Element eleOrderOutput = docOrderOutput.getDocumentElement();
		
		if(strTakeTill.equalsIgnoreCase("ResolveHolds")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
		}
		if(strTakeTill.equalsIgnoreCase("Schedule")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
		}
		if(strTakeTill.equalsIgnoreCase("Release")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
		}
		if(strTakeTill.equalsIgnoreCase("SentToWH")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
		}
		if(strTakeTill.equalsIgnoreCase("ReleaseAck")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
			acknowledgeRelease(env1, eleOrderOutput,"Ack");
		}
		if(strTakeTill.equalsIgnoreCase("ReleaseReject")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
			acknowledgeRelease(env1, eleOrderOutput,"Reject");
		}
		if(strTakeTill.equalsIgnoreCase("BackOrderFromNode")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
			Document docOrderReleaseList = acknowledgeRelease(env1, eleOrderOutput,"Ack");
			api.releaseEnvironment(env);
			YFSEnvironment env2 = createEnvironment("admin","admin");
			backOrderFromNode(env2,docOrderReleaseList);
		}
		if(strTakeTill.equalsIgnoreCase("Ship")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
			Document docOrderReleaseList = acknowledgeRelease(env1, eleOrderOutput,"Ack");
			String shipOptions = eleOrder.getAttribute("ShippingOptions");
			shipOrder(env1,docOrderReleaseList,shipOptions,false,docOrderOutput);
		}

		if(strTakeTill.equalsIgnoreCase("Invoice")){
			resolveHolds(env,docOrderOutput);
			VSIUtils.invokeAPI(env, "requestCollection", docOrderOutput);
			api.releaseEnvironment(env);
			YFSEnvironment env1 = createEnvironment("admin","admin");
			VSIUtils.invokeAPI(env1, "executeCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "requestCollection", docOrderOutput);
			VSIUtils.invokeAPI(env1, "scheduleOrder", docOrderOutput);
			VSIUtils.invokeAPI(env1, "releaseOrder", docOrderOutput);
			VSIUtils.invokeService(env1, "VSISendRelease", docOrderOutput);
			Document docOrderReleaseList = acknowledgeRelease(env1, eleOrderOutput,"Ack");
			String shipOptions = eleOrder.getAttribute("ShippingOptions");
			shipOrder(env1,docOrderReleaseList,shipOptions,true,docOrderOutput);
			VSIUtils.invokeAPI(env1, "processOrderPayments", docOrderOutput);
		}
		if(!YFCCommon.isVoid(docTemplate)){
			docOrderOutput = VSIUtils.invokeAPI(env, docTemplate, "getOrderDetails", docOrderOutput);
		}
		
		return docOrderOutput;
	}
	
	private void backOrderFromNode(YFSEnvironment env1, Document docReleaseList) throws Exception {
		Element eleOrderReleaseList = docReleaseList.getDocumentElement();
		ArrayList<Element> arrOrderRelease = SCXmlUtil.getChildren(eleOrderReleaseList, "OrderRelease");
		for(Element eleOrderRelease : arrOrderRelease){
			eleOrderRelease.setAttribute("Action", "CANCEL_BACKORDER");
			eleOrderRelease.setAttribute(ATTR_ORDER_NO, eleOrderRelease.getAttribute("SalesOrderNo"));
			eleOrderRelease.removeAttribute("Status");
			VSIUtils.invokeService(env1, "VSIProcessCanceledDuringWavingMessage", SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrderRelease)));
		}		
	}

	private void shipOrder(YFSEnvironment env, Document docOrderReleaseList, String shipOptions,boolean bInvoice,Document docOrderOutput) throws Exception {
		
		Element eleOrderReleaseList = docOrderReleaseList.getDocumentElement();
		Element eleOrderRelease1 = SCXmlUtil.getChildElement(eleOrderReleaseList, ELE_ORDER_RELEASE);
		ArrayList<Element> eleOrderReleases = SCXmlUtil.getChildren(eleOrderReleaseList, ELE_ORDER_RELEASE);
		Document docShipmentList = SCXmlUtil.createDocument("ShipmentList");
		Element eleShipmentList = docShipmentList.getDocumentElement();
		Element eleOrder = SCXmlUtil.createChild(eleShipmentList, ELE_ORDER);
		eleOrder.setAttribute(ATTR_DOCUMENT_TYPE, eleOrderRelease1.getAttribute(ATTR_DOCUMENT_TYPE));
		eleOrder.setAttribute(ATTR_ORDER_NO, eleOrderRelease1.getAttribute(ATTR_SALES_ORDER_NO));
		eleOrder.setAttribute("DistributionOrderId", eleOrderRelease1.getAttribute("SalesOrderNo").concat("*").concat(eleOrderRelease1.getAttribute("ReleaseNo")));
		
		for(Element eleOrderRelease : eleOrderReleases){
			Element eleShipmentInput = SCXmlUtil.createChild(eleShipmentList, ELE_SHIPMENT);
			//Set Shipment Level Attributes
			//eleShipmentInput.setAttribute("EnterpriseCode", "VSI.com");
			eleShipmentInput.setAttribute("ShipNode", eleOrderRelease.getAttribute("ShipNode"));
			eleShipmentInput.setAttribute("OrderNo", eleOrderRelease.getAttribute("SalesOrderNo"));
			eleShipmentInput.setAttribute("SCAC", "UPSN");
			eleShipmentInput.setAttribute("CarrierServiceCode", "STANDARD");
			eleShipmentInput.setAttribute("ActualShipDate",getCurrentDate() );
			eleShipmentInput.setAttribute("DocumentType", eleOrderRelease.getAttribute("DocumentType"));
			eleShipmentInput.setAttribute("DistributionOrderId", eleOrderRelease.getAttribute("SalesOrderNo").concat("*").concat(eleOrderRelease.getAttribute("ReleaseNo")));
			
			//Form Container Element
			Element eleContainers = SCXmlUtil.createChild(eleShipmentInput, "Containers");
			Element eleContainer = SCXmlUtil.createChild(eleContainers, "Container");
			//set Container attributes
			eleContainer.setAttribute("ContainerNo", Integer.toString(new Random().nextInt()));
			eleContainer.setAttribute("TrackingNo", "1Z0882331351212914");
			eleContainer.setAttribute("ContainerGrossWeight", "10");
			eleContainer.setAttribute("ContainerGrossWeightUOM", "LBS");
			eleContainer.setAttribute("EnterpriseCode", "VSI.com");
			
			//Form Container Details Element
			
			Element eleContainerDetails = SCXmlUtil.createChild(eleContainer, "ContainerDetails");
			Element eleShipmentShipmentLines = SCXmlUtil.createChild(eleShipmentInput, "ShipmentLines");
			
			Element eleOrderReleaseLines = SCXmlUtil.getChildElement(eleOrderRelease, "OrderLines");
			ArrayList<Element> eleOrderLines = SCXmlUtil.getChildren(eleOrderReleaseLines, "OrderLine");
			
			for(Element eleOrderLine : eleOrderLines){
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
				Element eleContainerDetail = SCXmlUtil.createChild(eleContainerDetails, "ContainerDetail");
				eleContainerDetail.setAttribute("Quantity", eleOrderLine.getAttribute("OrderedQty"));
				Element eleContainerShipmentLine = SCXmlUtil.createChild(eleContainerDetail, "ShipmentLine");
				eleContainerShipmentLine.setAttribute("DocumentType", eleOrderRelease.getAttribute("DocumentType"));
				eleContainerShipmentLine.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
				eleContainerShipmentLine.setAttribute("PrimeLineNo", eleOrderLine.getAttribute("PrimeLineNo"));
				eleContainerShipmentLine.setAttribute("SubLineNo", "1");
				eleContainerShipmentLine.setAttribute("Quantity", eleOrderLine.getAttribute("OrderedQty"));
				eleContainerShipmentLine.setAttribute("UnitOfMeasure", eleItem.getAttribute("UnitOfMeasure"));
				eleContainerShipmentLine.setAttribute("ProductClass", eleItem.getAttribute("ProductClass"));
				eleContainerShipmentLine.setAttribute("ReleaseNo", eleOrderRelease.getAttribute("ReleaseNo"));
				eleContainerShipmentLine.setAttribute("OrderNo", eleOrderRelease.getAttribute("SalesOrderNo"));
			
				//Set Attributes at Shipment/ShipmentLine level
				
				Element eleShipmentShipmentLine = SCXmlUtil.createChild(eleShipmentShipmentLines, "ShipmentLine");
				eleShipmentShipmentLine.setAttribute("DocumentType", eleOrderRelease.getAttribute("DocumentType"));
				eleShipmentShipmentLine.setAttribute("ItemID", eleItem.getAttribute("ItemID"));
				eleShipmentShipmentLine.setAttribute("PrimeLineNo", eleOrderLine.getAttribute("PrimeLineNo"));
				eleShipmentShipmentLine.setAttribute("SubLineNo", "1");
				eleShipmentShipmentLine.setAttribute("Quantity", eleOrderLine.getAttribute("OrderedQty"));
				eleShipmentShipmentLine.setAttribute("UnitOfMeasure", eleItem.getAttribute("UnitOfMeasure"));
				eleShipmentShipmentLine.setAttribute("ProductClass", eleItem.getAttribute("ProductClass"));
				eleShipmentShipmentLine.setAttribute("ReleaseNo", eleOrderRelease.getAttribute("ReleaseNo"));
				eleShipmentShipmentLine.setAttribute("OrderNo", eleOrderRelease.getAttribute("SalesOrderNo"));
			}
			VSIUtils.invokeService(env, "VSIProcessShipConfirmMessage", docShipmentList);
			
		}
		if(bInvoice){
			Document docShipmentListTemplate = SCXmlUtil.createDocument("ShipmentList");
			Element eleShipmentListTemplate = docShipmentListTemplate.getDocumentElement();
			Element eleShipmentTemplate = SCXmlUtil.createChild(eleShipmentListTemplate, ELE_SHIPMENT);
			eleShipmentTemplate.setAttribute("ShipmentKey",ATTR_EMPTY);
			
			Document docShipmentListForOrder = VSIUtils.invokeAPI(env, docShipmentListTemplate, "getShipmentListForOrder", docOrderOutput);
			Element eleShipmentListForOrder = docShipmentListForOrder.getDocumentElement();
			ArrayList<Element> arrShipments = SCXmlUtil.getChildren(eleShipmentListForOrder, ELE_SHIPMENT);
			for(Element eleShipmentOut:arrShipments){
				eleShipmentOut.setAttribute("TransactionId","CREATE_SHMNT_INVOICE.0001");
				VSIUtils.invokeAPI(env, "createShipmentInvoice", SCXmlUtil.createFromString(SCXmlUtil.getString(eleShipmentOut)));
			}
		}
	}
	
	private String getCurrentDate()
	{
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
		Date date = new Date();
		String strCurrentDate = sdf.format(date);
		return strCurrentDate;
	}

	public static YFSEnvironment createEnvironment(String userID, String progID)
			throws Exception {
		
		api = YIFClientFactory.getInstance().getApi();
		Document doc = SCXmlUtil.createDocument("YFSEnvironment");
		Element elem = doc.getDocumentElement();
		elem.setAttribute("userId", userID);
		elem.setAttribute("progId", progID);

		return api.createEnvironment(doc);
	}
	
	private Document formGetOrderReleaseListTemplate(){
		Document docGetOrderReleaseListTemplate = SCXmlUtil.createDocument("OrderReleaseList");
		Element eleGetOrderReleaseList = docGetOrderReleaseListTemplate.getDocumentElement();
		Element eleGetOrderRelease = SCXmlUtil.createChild(eleGetOrderReleaseList, "OrderRelease");
		Element eleOrderLines = SCXmlUtil.createChild(eleGetOrderRelease, "OrderLines");
		Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, "OrderLine");
		
		//Set Release Level Attributes
		eleGetOrderRelease.setAttribute("SalesOrderNo", "");
		eleGetOrderRelease.setAttribute("DocumentType", "");
		eleGetOrderRelease.setAttribute("ReleaseNo", "");
		eleGetOrderRelease.setAttribute("ShipNode", "");
		eleGetOrderRelease.setAttribute("Status", "");
		
		//Set Order Line Level Attributes
		eleOrderLine.setAttribute("PrimeLineNo", "");
		eleOrderLine.setAttribute("SubLineNo", "");
		eleOrderLine.setAttribute("OrderedQty", "");
		Element eleItem = SCXmlUtil.createChild(eleOrderLine, "Item");
		eleItem.setAttribute("ItemID", "");
		eleItem.setAttribute("ProductClass", "");
		eleItem.setAttribute("UnitOfMeasure", "");
		
		return docGetOrderReleaseListTemplate;
	}
	
	private Document acknowledgeRelease(YFSEnvironment env, Element eleOrder,String action)
			throws YIFClientCreationException, RemoteException, Exception {
		Document docOrderReleaseInput = SCXmlUtil.createDocument("OrderRelease");
		Element eleOrderReleaseInput = docOrderReleaseInput.getDocumentElement();
		eleOrderReleaseInput.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrder.getAttribute(ATTR_ORDER_HEADER_KEY));
		eleOrderReleaseInput.setAttribute(ATTR_STATUS, "3200.600");
		
		Document docOrderReleaseListTemplate = formGetOrderReleaseListTemplate();
		Document docOrderReleaseList = VSIUtils.invokeAPI(env, docOrderReleaseListTemplate, "getOrderReleaseList", docOrderReleaseInput);
		
		Element eleOrderReleaseList = docOrderReleaseList.getDocumentElement();
		ArrayList<Element> eleOrderReleases = SCXmlUtil.getChildren(eleOrderReleaseList, ELE_ORDER_RELEASE);
		for(Element eleOrderRelease : eleOrderReleases){
			Document docChangeOrderStatus = SCXmlUtil.createDocument("OrderStatusChange");
			Element eleChangeOrderStatus = docChangeOrderStatus.getDocumentElement();
			if(action.equalsIgnoreCase("Ack")){
				eleChangeOrderStatus.setAttribute("BaseDropStatus", "3200.1000");
			}else{
				eleChangeOrderStatus.setAttribute("BaseDropStatus", "3200.800");
			}
			
			eleChangeOrderStatus.setAttribute("EnterpriseCode", "VSI.com");
			eleChangeOrderStatus.setAttribute("OrderNo",eleOrder.getAttribute(ATTR_ORDER_NO));
			eleChangeOrderStatus.setAttribute("TransactionId", "RELEASE_ACKNOWLEDGED.0001.ex");
			eleChangeOrderStatus.setAttribute("DocumentType", "0001");
			eleChangeOrderStatus.setAttribute("ModificationReasonCode", "ReleaseAckFromWMS");
			eleChangeOrderStatus.setAttribute("ModificationReasonText", "ReleaseAckFromWMS");
			eleChangeOrderStatus.setAttribute("OrderNoType", "O");
			Element eleOrderLines = SCXmlUtil.createChild(eleChangeOrderStatus, "OrderLines");
			Element eleOrderLine = SCXmlUtil.createChild(eleOrderLines, "OrderLines");
			eleOrderLine.setAttribute("ReleasNo", eleOrderRelease.getAttribute("ReleaseNo"));
			
			VSIUtils.invokeService(env, "VSIProcessReleaseAcknowledgement", docChangeOrderStatus);
			
		}
		return docOrderReleaseList;
	}
	private void resolveHolds(YFSEnvironment env,Document docOrderOutput) throws YFSException, RemoteException, YIFClientCreationException {
		Document docChangeOrder = SCXmlUtil.createFromString(SCXmlUtil.getString(docOrderOutput));
		Element eleChangeOrder = docChangeOrder.getDocumentElement();
		Element eleOrderHoldTypes = SCXmlUtil.createChild(eleChangeOrder, "OrderHoldTypes");
		Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, "OrderHoldType");
		eleOrderHoldType.setAttribute("HoldType", "VSI_FRAUD_HOLD");
		eleOrderHoldType.setAttribute("Status", "1300");
		Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, "OrderHoldType");
		eleOrderHoldType1.setAttribute("HoldType", "VSI_REMORSE_HOLD");
		eleOrderHoldType1.setAttribute("Status", "1300");
		VSIUtils.invokeAPI(env, "changeOrder", docChangeOrder);
	}
	
	/*private void adjustInventoryAsPerScheduleOptions(YFSEnvironment env, Document docOrder) throws YFSException, RemoteException, YIFClientCreationException{
		//Extract Item Ids
		Element eleOrder = docOrder.getDocumentElement();
		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, "OrderLines");
		ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(eleOrderLines, "OrderLine");
		for(Element eleOrderLine: arrOrderLines){
			
			Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
			String strItemID = eleItem.getAttribute("ItemId");
			
			//getDemandSummary
			Document docDemandSummary = SCXmlUtil.createDocument("DemandSummary");
			Element eleDemandSummary = docDemandSummary.getDocumentElement();
			eleDemandSummary.setAttribute("ItemID", strItemID);
			eleDemandSummary.setAttribute("ProductClass", "GOOD");
			eleDemandSummary.setAttribute("UnitOfMeasure", "EACH");
			eleDemandSummary.setAttribute("OrganizationCode", "VSI-Cat");
			
			Document docDemandSummaryOpenOrder = VSIUtils.invokeAPI(env, "getDemandSummary",docDemandSummary);
			
			Element eleShipNodeList = SCXmlUtil.createChild(eleDemandSummary, "ShipNodes");
			Element eleShipNode1 = SCXmlUtil.createChild(eleShipNodeList, "ShipNode");
			Element eleShipNode2 = SCXmlUtil.createChild(eleShipNodeList, "ShipNode");
			eleShipNode1.setAttribute("ShipNode", "9004");
			eleShipNode1.setAttribute("ShipNode", "9005");
			
			Document docDemandSummaryNonOpenOrder = VSIUtils.invokeAPI(env, "getDemandSummary",docDemandSummary);
			
			
			
			Document docGetInventorySupply = SCXmlUtil.createDocument("InventorySupply");
			Element eleGetInventorySupply = docGetInventorySupply.getDocumentElement();
			eleGetInventorySupply.setAttribute("ItemID", strItemID);
			eleGetInventorySupply.setAttribute("ProductClass", "GOOD");
			eleGetInventorySupply.setAttribute("UnitOfMeasure", "EACH");
			eleGetInventorySupply.setAttribute("OrganizationCode", "VSI-Cat");
			eleGetInventorySupply.setAttribute("DistributionRuleId", "VSI_DC");
			
			//get current supply and make it zero
			Document docGetInventorySupplyOutput = VSIUtils.invokeAPI(env, "getInventorySupply", docGetInventorySupply);
			Element eleGetInventorySupplyOutput = docGetInventorySupplyOutput.getDocumentElement();
			Element eleSupplies = SCXmlUtil.getChildElement(eleGetInventorySupplyOutput, "Supplies");
			ArrayList<Element> arrInventorySupply =  SCXmlUtil.getChildren(eleSupplies, "InventorySupply");
			for(Element eleInventorySupply :arrInventorySupply ){
				adjustInventoryForSupplies(strItemID, eleInventorySupply);
			}
			
			//get Current demand 
			
			
			
			//Make supply equal to demand.
		}
		
		
		 
			//If demand ship date is current or onhand, add onhand supply else add future supply same as demand ship date
		//Take 1/2 quantity of orderedqty and add as onhand and add remaining as future. Do this for both nodes.
	}
*/
	private void adjustInventoryForSupplies(String strItemID, Element eleInventorySupply) {
		String strQuantity = eleInventorySupply.getAttribute("Quantity");
		Document docAdjustInventory = SCXmlUtil.createDocument("Items");
		Element eleAdjustInventoryItems = docAdjustInventory.getDocumentElement();
		Element eleAdjustInventoryItem = SCXmlUtil.createChild(eleAdjustInventoryItems, "Item");
		eleAdjustInventoryItem.setAttribute("ItemID", strItemID);
		eleAdjustInventoryItem.setAttribute("UnitOfMeasure", "EACH");
		eleAdjustInventoryItem.setAttribute("OrganizationCode", "VSI-Cat");
		eleAdjustInventoryItem.setAttribute("ShipNode", eleInventorySupply.getAttribute("ShipNode"));
		eleAdjustInventoryItem.setAttribute("Quantity", "-".concat(strQuantity));
		eleAdjustInventoryItem.setAttribute("SupplyType", eleInventorySupply.getAttribute("SupplyType"));
		
	}
}
