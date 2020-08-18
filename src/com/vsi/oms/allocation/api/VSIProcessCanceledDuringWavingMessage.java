package com.vsi.oms.allocation.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This component is responsible for receiving waved message from WMS via middle ware
 * 
 * Input:
 * 	<OrderRelease OrderNo="" DocumentType="" Action="" EnterpriseCode="" ReleaseNo=""/>
 * 
 * @author IBM
 *
 */
public class VSIProcessCanceledDuringWavingMessage extends VSIBaseCustomAPI implements VSIConstants{

	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessCanceledDuringWavingMessage.class);

	public Document processCanceledDuringWavingMessage(YFSEnvironment env, Document docInput) {
		if(log.isDebugEnabled()){
			log.beginTimer("VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage : START");
		}
		Element eleInput = docInput.getDocumentElement();
		if(eleInput.getAttribute(ATTR_DOCUMENT_TYPE).equalsIgnoreCase(DOC_TYPE_TO)){
			eleInput.setAttribute("CustomerPoNo", eleInput.getAttribute(ATTR_ORDER_NO));
			eleInput.removeAttribute(ATTR_ORDER_NO);
		}else{
			eleInput.setAttribute("SalesOrderNo", eleInput.getAttribute(ATTR_ORDER_NO));
		}
		eleInput.removeAttribute(ATTR_ENTERPRISE_CODE);
		try {
			if (VSIConstants.ACTION_CANCEL_BACKORDER.equals(eleInput.getAttribute(VSIConstants.ATTR_ACTION))) {
				
				//Get the Release Details
				Document docOrderReleaseList = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORD_REL_LIST_SEND_RELEASE, API_GET_ORDER_RELEASE_LIST, docInput);
				
				//Cancel/Backorder the release
				//Update quantities for JDA ForceAllocationService
				//SuppressInventory
				//Invoke JDA Service
				changeRelease(env,docOrderReleaseList);
			
			} 
		} catch (RemoteException re) {
			log.error("RemoteException in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred",
					"Exception in VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage() : ");
		}
		if(log.isDebugEnabled()){
			log.endTimer("VSIProcessCanceledDuringWavingMessage.processCanceledDuringWavingMessage : END");
		}
		return docInput;
	}

	/*This function is used to backorder or cancel 
	 * the release. Also it invokes manageInventoryNodeControl
	 * to suppress the inventory.
	 */
	private void changeRelease(YFSEnvironment env,Document docOrderReleaseList) throws Exception {
			Document docChangeRelease = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER_RELEASE);
			Element eleOrderRelease = SCXmlUtil.getChildElement(docOrderReleaseList.getDocumentElement(), ELE_ORDER_RELEASE);
			Element eleOrder = SCXmlUtil.getChildElement(eleOrderRelease, ELE_ORDER);
			NodeList nlOrderLine = XMLUtil.getNodeListByXpath(docOrderReleaseList, "//OrderReleaseList/OrderRelease/OrderLines/OrderLine");
			
			String strReleaseStatus = eleOrderRelease.getAttribute(ATTR_STATUS);
			String strFromStatus = STATUS_CODE_RELEASE_ACKNOWLEDGED;
			if(strReleaseStatus.equalsIgnoreCase("Sent To Warehouse")){
				strFromStatus = STATUS_CODE_SENT_TO_NODE;
			}
			String strDocumentType = eleOrderRelease.getAttribute(ATTR_DOCUMENT_TYPE);
			String strOrderSourcingClassification = eleOrder.getAttribute(ATTR_SOURCING_CLASSIFICATION);
			String strEnterpriseCode = eleOrder.getAttribute(ATTR_ENTERPRISE_CODE);
			String strNode = eleOrderRelease.getAttribute(ATTR_SHIP_NODE);
			//OMS-1612 : Start
			/*Boolean bInternationalOrder =false;
			
			//Check for international order
			if (!YFCCommon.isVoid(strOrderSourcingClassification)){
				if(strOrderSourcingClassification.equalsIgnoreCase(INTERNATIONAL_ORDER)) {
					bInternationalOrder = true;
				}
			}*/
			//OMS-1612 : End
			//Check for STS orders
			Boolean bIsSTSOrder = false;
			if (!YFCCommon.isVoid(strDocumentType)){
				if(strDocumentType.equalsIgnoreCase(DOC_TYPE_TO)) {
					bIsSTSOrder = true;
					String strReceivingNode = eleOrderRelease.getAttribute(ATTR_RECEIVING_NODE);
					String strShipNode = eleOrderRelease.getAttribute(ATTR_SHIP_NODE);
					eleOrderRelease.setAttribute(ATTR_SHIP_NODE, strReceivingNode);
					eleOrderRelease.setAttribute(ATTR_STS_SHIP_NODE, strShipNode);
				}
			}
			
			// Check or MCL order
			Boolean bIsMCLOrder = false;
			if(!YFCCommon.isVoid(strEnterpriseCode) && YFCCommon.equals(strEnterpriseCode, ENT_MCL)){
				bIsMCLOrder = true;
			}
			
			//Wholesale Change: Start
			boolean blRejectFlag = false;
			if (eleOrder.getAttribute(ATTR_ORDER_TYPE).equalsIgnoreCase(WHOLESALE))
			{
				ArrayList<Element> eleGetCommonCodeRejectFlagList = VSIUtils.getCommonCodeList(env, VSI_WH_CNCL_ON_REJ, strEnterpriseCode, "");
				if(!eleGetCommonCodeRejectFlagList.isEmpty() && (FLAG_Y.equalsIgnoreCase(eleGetCommonCodeRejectFlagList.get(0).getAttribute(ATTR_CODE_SHORT_DESCRIPTION))))
					blRejectFlag = true;
			}
			//Wholesale Change: End
			
			//Start forming input for change release
			Element eleChangeRelease = docChangeRelease.getDocumentElement();
			eleChangeRelease.setAttribute(ATTR_ORDER_RELEASE_KEY, eleOrderRelease.getAttribute(ATTR_ORDER_RELEASE_KEY));
			eleChangeRelease.setAttribute(ATTR_OVERRIDE, FLAG_Y);
			eleChangeRelease.setAttribute(ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);
			Element eleChangeReleaseOrderLines = SCXmlUtil.createChild(eleChangeRelease, ELE_ORDER_LINES);
			
			String strChainedFromOhk = null;	
			String strOrderLineKey=null;
			List<String> lsSTSOrderLineKey = new ArrayList();	
			List<String> lsOrderLineKey = new ArrayList();	
			
			// Append OrderLines to changeRelease and 
			//loop through the order lines to decide whether to CANCEL or BACKORDER
			
			for(int i=0;i<nlOrderLine.getLength();i++){
				Element eleOrderLine = (Element)nlOrderLine.item(i);
				Element eleItemDetails = SCXmlUtil.getChildElement(eleOrderLine, ELE_ITEM_DETAILS);
				Element eleItemExtn = SCXmlUtil.getChildElement(eleItemDetails, ELE_EXTN);
				strChainedFromOhk = eleOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_HEADER_KEY);
				
				
				
				// ARE-431 : Modified to use the status quantity instead of the order quantity to backorder/cancel from released : BEGIN
				//String strSignedOrderedQty = "-"+eleOrderLine.getAttribute(ATTR_ORD_QTY);
				String strSignedOrderedQty = INT_ZER0_NUM;
				String strStatusQty = INT_ZER0_NUM;
				Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, ELE_ORDER_STATUSES);
				ArrayList <Element> alOrderStatusList = SCXmlUtil.getChildren(eleOrderStatuses, ELE_ORDER_STATUS);
				for (Element eleOrderStatus : alOrderStatusList) {
				//OMS-1074 : Added status check for Sent To Warehouse : Start
					if ((STATUS_CODE_RELEASE_ACKNOWLEDGED.equals(eleOrderStatus.getAttribute(ATTR_STATUS)) || STATUS_CODE_SENT_TO_NODE.equals(eleOrderStatus.getAttribute(ATTR_STATUS)))
							&& !INT_ZER0_NUM.equals(eleOrderStatus.getAttribute(ATTR_STATUS_QUANTITY)))
				//OMS-1074 : Added status check for Sent To Warehouse : End		
					{
						strStatusQty = eleOrderStatus.getAttribute(ATTR_STATUS_QUANTITY);
						break;
					}
				}
				strSignedOrderedQty = "-" + strStatusQty;
				// ARE-431 : Modified to use the status quantity instead of the order quantity to backorder/cancel from released : END
				
				String strItem = eleItemDetails.getAttribute(ATTR_ITEM_ID);
				if(!YFCCommon.isVoid(eleChangeReleaseOrderLines)){
					Element eleChangeReleaseOrderLine = SCXmlUtil.createChild(eleChangeReleaseOrderLines, ELE_ORDER_LINE);
					if(!YFCCommon.isVoid(eleChangeReleaseOrderLine)){
						eleChangeReleaseOrderLine.setAttribute(ATTR_CHANGE_IN_QTY, strSignedOrderedQty);
						eleChangeReleaseOrderLine.setAttribute(ATTR_PRIME_LINE_NO, eleOrderLine.getAttribute(ATTR_PRIME_LINE_NO));
						eleChangeReleaseOrderLine.setAttribute(ATTR_SUB_LINE_NO, eleOrderLine.getAttribute(ATTR_SUB_LINE_NO));
						eleChangeReleaseOrderLine.setAttribute(ATTR_FROM_STATUS, strFromStatus);

						
						//Set attributes on the Original release document for
						//sending it to JDA to reverse the force allocation
						eleOrderLine.setAttribute(ATTR_ORG_ALLOCATED_QTY, eleOrderLine.getAttribute(ATTR_ORD_QTY));
						eleOrderLine.setAttribute(ATTR_ORD_QTY, INT_ZER0_NUM);
						
						/* OMS-1316 Start
						Boolean bIsDiscontinuedItem = false;
						//Check for Discontinued Items
						if (VSIConstants.FLAG_Y.equals(eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_IS_DISCNTND_ITEM))) {
							bIsDiscontinuedItem = true;
							eleChangeRelease.setAttribute(ATTR_MODIFICATION_REASON_CODE, DISCONTINUED_ITEM);
						}
						OMS-1316 End */
						//Cancel if discontinued/STS/International else Back order
						//ARS-268
						//OMS-1316 removing bIsDiscontinuedItem from if condition
						////OMS-1612 : Removing bInternationalOrder flag check from the below condition
						if(bIsSTSOrder || bIsMCLOrder || blRejectFlag){
							eleChangeReleaseOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_CANCEL);
							eleChangeReleaseOrderLine.setAttribute(ATTR_TO_STATUS, STATUS_CANCEL);
							if(bIsSTSOrder)
							{
								String strChainedFromOlk = eleOrderLine.getAttribute(ATTR_CHAINED_FROM_ORDER_LINE_KEY);
								lsSTSOrderLineKey.add(strChainedFromOlk);
							}
							env.setTxnObject("FromWMSCancel", "Y");

						}else{
							eleChangeReleaseOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_BO);
							eleOrderLine.setAttribute(ATTR_TO_STATUS, STATUS_BACKORDERED_FROM_NODE);
							strOrderLineKey = eleOrderLine.getAttribute(ATTR_ORDER_LINE_KEY);
							lsOrderLineKey.add(strOrderLineKey);
						}
						
						//Suppress Inventory for items at Node
						invokeManageInvNodeControl(env,strItem,strNode);
						
					}
					
					
					//OMS-1122: Start
					//Cancel the line if it is ADP box line
					Element eleOrderLineExtn=SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);
					if(!YFCCommon.isVoid(eleOrderLineExtn)){
						String strExtnSDKLine=eleOrderLineExtn.getAttribute("ExtnSDKLine");
						if(!YFCCommon.isStringVoid(strExtnSDKLine) && "Y".equals(strExtnSDKLine)){
							if(log.isDebugEnabled()){
								log.debug("Line is an ADP box line, cancelling the order line");
							}
							eleChangeReleaseOrderLine.setAttribute(ATTR_ACTION, ACTION_RELEASE_CANCEL);
						}
					}
					//OMS-1122: End
				}
			}
			
			//Invoke changeRelease to cancel or backorder release.
			VSIUtils.invokeAPI(env, API_CHANGE_RELEASE, docChangeRelease);
			
			
			eleOrderRelease.setAttribute("ReverseAllocation", FLAG_Y);
			//Return document to be used by JDA Service
			Document docOrderRelease = SCXmlUtil.createFromString(SCXmlUtil.getString(eleOrderRelease));
			
			//Invoke JDA Service to reverse the force allocation.
			VSIUtils.invokeService(env, VSIConstants.SERVICE_PROCESS_RELEASE, docOrderRelease);
			
			
				
						
			
			//ARS-268
			if(!YFCObject.isVoid(lsSTSOrderLineKey) && lsSTSOrderLineKey.size() > 0){
				cancelSalesOrder(env, strChainedFromOhk, lsSTSOrderLineKey);
			}
			//OMS-1881 start
			String strNewChangeFlag=null;
			try {

				ArrayList<Element> listWavingFlag;
				listWavingFlag = VSIUtils.getCommonCodeList(env, "VSI_WAVING_FLAG", "NEWCHANGE", "DEFAULT");
				if(!listWavingFlag.isEmpty()){
					Element eleCommonCode=listWavingFlag.get(0);
					strNewChangeFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strNewChangeFlag)&&FLAG_Y.equalsIgnoreCase(strNewChangeFlag)){
						String strOrderheaderKey=eleOrderRelease.getAttribute(ATTR_ORDER_HEADER_KEY);
						NodeList nlOrderHoldTypes = SCXmlUtil.getXpathNodes(eleOrder, "//Order/OrderHoldTypes/OrderHoldType");
						for(int i1=0;i1<nlOrderHoldTypes.getLength();i1++){
							Element eleOrderHoldType = (Element)nlOrderHoldTypes.item(i1);	
							String strStatus = eleOrderHoldType.getAttribute(VSIConstants.ATTR_STATUS);
							String strHoldType = eleOrderHoldType.getAttribute(VSIConstants.ATTR_HOLD_TYPE);

							if(STATUS_CREATE.equals(strStatus) && CSR_CANCELLED_HOLD.equals(strHoldType)&&!lsOrderLineKey.isEmpty() && ATTR_DOCUMENT_TYPE_SALES.equalsIgnoreCase(strDocumentType) &&!WHOLESALE.equalsIgnoreCase(eleOrder.getAttribute(ATTR_ORDER_TYPE))){
								cancelCompleteOrder(env, strOrderheaderKey,lsOrderLineKey);
								resolveHold(env, strOrderheaderKey);
								break;
							}

						}
					}
				}

			}catch (Exception e) {
				e.printStackTrace();
				throw new YFSException();
			}
	}
	// this method is used for cancelling the order which has CSR_CANCELLED_HOLD hold on it
	private void cancelCompleteOrder(YFSEnvironment env,
			String strOrderheaderKey, List<String> lsOrderLineKey)throws Exception{ 
	
		Document inputChangeOrder = SCXmlUtil.createDocument();
		Element eleOrder = inputChangeOrder.createElement(ELE_ORDER);
		eleOrder.setAttribute(ATTR_OVERRIDE, FLAG_Y);
		inputChangeOrder.appendChild(eleOrder);

		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderheaderKey);
		eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, "Customer Care Cancel - No Email");

		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderheaderKey);
		Element eleOrderLines = inputChangeOrder.createElement(ELE_ORDER_LINES);
		eleOrder.appendChild(eleOrderLines);
		Iterator itr = lsOrderLineKey.iterator();
		while(itr.hasNext()){
			String strOrderLineKey = (String) itr.next();
			Element eleOrderLine = inputChangeOrder.createElement(ELE_ORDER_LINE);
			eleOrderLines.appendChild(eleOrderLine);
			eleOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleOrderLine.setAttribute(ATTR_ACTION, ACTION_CAPS_CANCEL);
		}
		if(log.isDebugEnabled()){
			log.debug("VSIProcessCanceledDuringWavingMessage_cancelSTHSalesOrder"+SCXmlUtil.getString(inputChangeOrder));
		}
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, inputChangeOrder);


	}
// this method is used for resolving CSR_CANCELLED_HOLD hold
	private void resolveHold(YFSEnvironment env, String strOrderheaderKey) throws YFSException, RemoteException, YIFClientCreationException {

		try {
			Document docProcessedHold =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element eleOrderHold =docProcessedHold.getDocumentElement();
			eleOrderHold.setAttribute(ATTR_OVERRIDE, FLAG_Y);
			eleOrderHold.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderheaderKey);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(eleOrderHold, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					CSR_CANCELLED_HOLD);
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, STATUS_RESOLVED);
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"CSR Cancelled Request Hold");
			log.info("Printing processedHoldDoc: "
					+ XMLUtil.getXMLString(docProcessedHold));
			VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docProcessedHold);	

		} catch (ParserConfigurationException | YFSException | RemoteException | YIFClientCreationException e) {

			e.printStackTrace();
		}

	}
	//OMS-1881 end

	/*This function forms input for manageInventoryNodeControl
	 * to suppress the inventory when release is canceled from 
	 * warehouse.
	 */
	private void invokeManageInvNodeControl(YFSEnvironment env, String strItemID,String strNode) throws YFSException, RemoteException, YIFClientCreationException{
		
		//Form input for manageInventoryNodeControl
		Document docManageInvNodeControl = SCXmlUtil.createDocument(VSIConstants.ELE_INVENTORY_NODE_CONTROL);
		Element eleManageInvNodeControl = docManageInvNodeControl.getDocumentElement();
		eleManageInvNodeControl.setAttribute(ATTR_ITEM_ID, strItemID);
		//OMS-898 : Start
		//eleManageInvNodeControl.setAttribute(ATTR_ORG_CODE, ENT_VSI_CAT);
		//OMS-898 :end
		eleManageInvNodeControl.setAttribute(ATTR_PRODUCT_CLASS, "GOOD");
		eleManageInvNodeControl.setAttribute(ATTR_UOM, UOM_EACH);
		eleManageInvNodeControl.setAttribute(ATTR_NODE, strNode);
		eleManageInvNodeControl.setAttribute(ATTR_NODE_CONTROL_TYPE, "ON_HOLD");
		eleManageInvNodeControl.setAttribute(ATTR_INVENTORY_PICTURE_CORRECT, FLAG_N);
		eleManageInvNodeControl.setAttribute(ATTR_INVENTORY_PICTURE_INCORRECT_DATE, "2500-12-31");
		
		//OMS-898 : Start
		//invoke getCommonCodeList and get the list of enterprise which needs to put on inventory hold
		try {
			Document docGetCommonCodeListINC = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCodeINC = docGetCommonCodeListINC.getDocumentElement();
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_ORG_CODE, ATTR_DEFAULT);
			eleCommonCodeINC.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_INV_NODE_CNTRL");
			Document docCommonCodeListINCOutput=VSIUtils.getCommonCodeList(env, docGetCommonCodeListINC);
			if(docCommonCodeListINCOutput!=null){
				NodeList nlCommonCode = docCommonCodeListINCOutput.getElementsByTagName(ELEMENT_COMMON_CODE);
				int iCount=nlCommonCode.getLength();
				for (int i = 0; i < iCount; i++) {
					Element eleCommonCode=(Element)nlCommonCode.item(i);
					String strOrgCode=eleCommonCode.getAttribute(ATTR_CODE_VALUE);
					eleManageInvNodeControl.setAttribute(ATTR_ORG_CODE, strOrgCode);
					Document invNodeCntrlList = VSIUtils.invokeAPI(env, "getInventoryNodeControlList", docManageInvNodeControl);
					if(!invNodeCntrlList.getDocumentElement().hasChildNodes())
					{
//						VSIUtils.invokeAPI(env, API_MANAGE_INVENTORY_NODE_CONTROL, docManageInvNodeControl);
					}
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error("RemoteException in VSIProcessCanceledDuringWavingMessage.invokeManageInvNodeControl() : " , e);
		}
		
		//Invoke manageInventoryNodeControl
		//VSIUtils.invokeAPI(env, API_MANAGE_INVENTORY_NODE_CONTROL, docManageInvNodeControl);
		
		//OMS-898 : End
		
	}
	
	private void cancelSalesOrder(YFSEnvironment env,String strOrderHeaderKey,List<String> lsSTSOrderLineKey) throws Exception{
		
		String strCancelReason= null;
		ArrayList<Element> arrFTCRulesForCancelWindow = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, "CancelReason", ATTR_DEFAULT);
		if(arrFTCRulesForCancelWindow.size()>0){
			Element eleFTCRule = arrFTCRulesForCancelWindow.get(0);
			strCancelReason = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
		}
		
		Document inputChangeOrder = SCXmlUtil.createDocument();
		Element eleOrder = inputChangeOrder.createElement(ELE_ORDER);
		eleOrder.setAttribute("Override", "Y");
		inputChangeOrder.appendChild(eleOrder);
		
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		if(!YFCCommon.isVoid(strCancelReason)){
			eleOrder.setAttribute(ATTR_MODIFICATION_REASON_CODE, strCancelReason);
		}
		
		eleOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		Element eleOrderLines = inputChangeOrder.createElement(ELE_ORDER_LINES);
		eleOrder.appendChild(eleOrderLines);
		Iterator itr = lsSTSOrderLineKey.iterator();
		while(itr.hasNext()){
			String strOrderLineKey = (String) itr.next();
			Element eleOrderLine = inputChangeOrder.createElement(ELE_ORDER_LINE);
			eleOrderLines.appendChild(eleOrderLine);
			eleOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleOrderLine.setAttribute(ATTR_ACTION, "CANCEL");
		}
		if(log.isDebugEnabled()){
			log.debug("VSIProcessCanceledDuringWavingMessage_cancelSTSSalesOrder"+SCXmlUtil.getString(inputChangeOrder));
		}
		VSIUtils.invokeAPI(env, API_CHANGE_ORDER, inputChangeOrder);
	}
}
