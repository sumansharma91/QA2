package com.vsi.oms.shipment.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIStampTranNo implements VSIConstants{
	private YFCLogCategory log = YFCLogCategory.instance(VSIStampTranNo.class);

	public Document processConfirmShipment(YFSEnvironment env, Document inDoc) {

		String strExtnTransactionNo = "";
		String strShipNodePadded = "";

		try {
			if(log.isDebugEnabled()){
				log.debug("VSIStampTranNo : input : " + XMLUtil.getXMLString(inDoc));
			}
			
			Element eleShipment = inDoc.getDocumentElement();
			String strDocumentType = eleShipment.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			String strShipmentKey = eleShipment.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			if (!YFCCommon.isStringVoid(strDocumentType) && "0001".equals(strDocumentType)) {
				if(log.isDebugEnabled()){
					log.debug("Shipment is a sales order shipment ,stamping ExtnTransactionNo");
				}
				
				// Invoke getOrderList and fetch the order details
				Element eleOrder = fetchOrderDetails(env, eleShipment);
				if(log.isDebugEnabled()){
					log.debug("fetchOrderDetails output" + XMLUtil.getElementXMLString(eleOrder));
				}
				if (null != eleOrder) {
					Element eleOrderExtn = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_EXTN);
					String strExtnDTCOrder = eleOrderExtn.getAttribute("ExtnDTCOrder");
					String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					String strShipNode = eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE);
					String strExtnSubscriptionOrder = eleOrderExtn.getAttribute("ExtnSubscriptionOrder");
					String strExtnOriginalADPOrder = eleOrderExtn.getAttribute("ExtnOriginalADPOrder");
					String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
					String strEnteredBy = eleOrder.getAttribute(VSIConstants.ATTR_ENTERED_BY);
					String strExtnReOrder = eleOrderExtn.getAttribute("ExtnReOrder");
					NodeList nlOrderLine = eleOrder.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
					String strDeliveryMethod = null;
					if (nlOrderLine != null) {
						Element eleOrderLine = (Element) nlOrderLine.item(0);
						strDeliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);

					}
					if (YFCCommon.isVoid(strDeliveryMethod) && "Y".equals(strExtnDTCOrder)) {
						strDeliveryMethod = VSIConstants.ATTR_DEL_METHOD_SHP;
					}

					// logic to set the ExtnTransactionNo
					String seqNum = VSIConstants.SEQ_VSI_SEQ_TRANID;
					if(log.isDebugEnabled()){
						log.debug("seqNum" + seqNum);
					}
					//OMS-2510 Start
					String strShipNodeKey=null;
					boolean isBossShipment=true;
					strShipNodeKey=eleShipment.getAttribute(ATTR_SHIP_NODE);
					if (!YFCCommon.isVoid(strShipNodeKey) && (VSI_VADC.equals(strShipNodeKey) || VSI_AZDC.equals(strShipNodeKey)))
					{
						isBossShipment=false;
					}
					if(isBossShipment && !YFCCommon.isVoid(strExtnReOrder) && FLAG_Y.equals(strExtnReOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)&& !YFCCommon.isVoid(strDeliveryMethod)))
					{
							seqNum = seqNum +"98_";
							if(log.isDebugEnabled()){
								log.debug("BOSS ReOrders, seqNum"+seqNum);
							}
					} 
					else if(isBossShipment && !YFCCommon.isVoid(strExtnSubscriptionOrder) && FLAG_Y.equals(strExtnSubscriptionOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)&& !YFCCommon.isVoid(strDeliveryMethod)))
					{
						seqNum = seqNum +"97_";
						if(log.isDebugEnabled()){
							log.debug("BOSS Subscription Order, seqNum"+seqNum);
						}
						
					}
					else if(isBossShipment && !YFCCommon.isVoid(strExtnOriginalADPOrder) && FLAG_Y.equals(strExtnOriginalADPOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)&& !YFCCommon.isVoid(strDeliveryMethod)))
					{
						seqNum = seqNum +"96_";
						if(log.isDebugEnabled()){
							log.debug("BOSS Template Order, seqNum"+seqNum);
						}
						
					}
					else if(isBossShipment && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)|| !YFCCommon.isVoid(strDeliveryMethod)))
					{
						seqNum = seqNum +"95_";
						if(log.isDebugEnabled()){
							log.debug("BOSS Order, seqNum"+seqNum);
					}
					// Added new logic-start
					}	
					//OMS-2510 END	
					else if (!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder)
							&& VSIConstants.ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)) {
						seqNum = seqNum + "92_";
						if(log.isDebugEnabled()){
							log.debug("ReOrders, seqNum" + seqNum);
						}
					}
					/* OMS-1220 changes :end */
					/* OMS-1352 changes start */
					else if (!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder)
							&& (VSIConstants.FLAG_N.equalsIgnoreCase(strExtnDTCOrder)
									|| YFCCommon.isVoid(strExtnDTCOrder))) {
						seqNum = seqNum + "94_";
						if(log.isDebugEnabled()){
							log.debug("strExtnReOrder:Y and isDTCOrder: N or null, seqNum" + seqNum);
						}

					}
					/* OMS-1352 changes end */
					/* OMS-1351 changes start */
					else if (!YFCCommon.isVoid(strExtnSubscriptionOrder) && "Y".equals(strExtnSubscriptionOrder)
							&& (VSIConstants.FLAG_N.equalsIgnoreCase(strExtnDTCOrder)
									|| YFCCommon.isVoid(strExtnDTCOrder))) {
						seqNum = seqNum + "93_";
						if(log.isDebugEnabled()){
							log.debug("isSubscriptionOrder:Y and isDTCOrder: N or null, seqNum" + seqNum);
						}

					}
					/* OMS-1351 changes end */
					else if (!YFCCommon.isVoid(strExtnSubscriptionOrder) && "Y".equals(strExtnSubscriptionOrder)) {
						seqNum = seqNum + "91_";
					} // Auto delivery orders get 91_
						// original ADP order changes done for OMS-857
					else if (!YFCCommon.isVoid(strExtnOriginalADPOrder) && "Y".equals(strExtnOriginalADPOrder)) {
						seqNum = seqNum + "9_";
					} else if (strOrderType != null
							&& strOrderType.equalsIgnoreCase(VSIConstants.ATTR_ORDER_TYPE_POS)) {
						seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_POS_PICK;
					} // All POS order get 88_
					//OMS-2010 start
					else if (!YFCCommon.isStringVoid(strOrderType)&&VSIConstants.WHOLESALE.equalsIgnoreCase(strOrderType)) {
						seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_WHOLESALE;
					}
					//OMS-2010 End
					else if (!YFCObject.isVoid(strDeliveryMethod)
							&& strDeliveryMethod.equalsIgnoreCase(VSIConstants.ATTR_DEL_METHOD_SHP)) {
						seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_SHP;
					} // All WEB Ship orders 5_
					else {
						seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_WEB_PICK;
					} // WEB PICK and STS gets 89_
						// End: Modified during Payment Changes added 04-27-2017
					if ("SHP".equalsIgnoreCase(strDeliveryMethod)) {
						strShipNode = strEnteredBy;
					}
					if (strShipNode != null)

						strShipNodePadded = ("00000" + strShipNode).substring(strShipNode.trim().length());

					seqNum = seqNum + strShipNodePadded;
					// Added new logic-end
					if(log.isDebugEnabled()){
						log.debug("SeqNo after ShipNode padding:" + seqNum);
					}
					//OMS-1892 START
					/*strExtnTransactionNo = VSIDBUtil.getNextSequence(env, seqNum);
					if(log.isDebugEnabled()){
						log.debug("TransactionNo generated for the order :  " + strExtnTransactionNo);
					}*/

					Element eleExtn = SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_EXTN);
					/*if (eleExtn == null) {
						eleExtn = SCXmlUtil.createChild(eleShipment, VSIConstants.ELE_EXTN);

					}
					eleExtn.setAttribute("ExtnTransactionNo", strExtnTransactionNo);*/
					// logic to stamp ShipmentSeqNo
					String strShipmentSeqNo = findShipmentSeqNo(env, strOrderHeaderKey);
					eleExtn.setAttribute("ShipmentSeqNo", strShipmentSeqNo);
					// calling changeShipment to update the details:
					Document docChangeShipment = XMLUtil.createDocument("Shipment");
					Element changeShipmentEle = docChangeShipment.getDocumentElement();
					changeShipmentEle.setAttribute("ShipmentKey", strShipmentKey);
					Element extnElement = docChangeShipment.createElement(VSIConstants.ELE_EXTN);
					extnElement.setAttribute("ShipmentSeqNo", strShipmentSeqNo);
					
					//check if ExtnTransactionNo already exist
					strExtnTransactionNo=eleExtn.getAttribute(VSIConstants.ATTR_EXTN_TRANS_NO);
					if(YFCCommon.isVoid(strExtnTransactionNo)){
					strExtnTransactionNo = VSIDBUtil.getNextSequence(env, seqNum);
					if(log.isDebugEnabled()){
						log.debug("TransactionNo generated for the order :  " + strExtnTransactionNo);
					}
					extnElement.setAttribute("ExtnTransactionNo", strExtnTransactionNo);
					}
					//OMS-1892 END
					changeShipmentEle.appendChild(extnElement);
					Document docOutChangeShipment = VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_SHIPMENT,
							docChangeShipment);
					if(log.isDebugEnabled()){
						log.debug("Output : fetchOrderDetails" + XMLUtil.getXMLString(docOutChangeShipment));
					}
					return docOutChangeShipment;
				}

			}
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Error in VSIStampTranNo  " + e.getMessage());
			throw new YFSException();

		}
		if(log.isDebugEnabled()){
			log.debug("VSIStampTranNo : output : " + XMLUtil.getXMLString(inDoc));
		}
		return inDoc;
	}

	/**This method will call getShipmentList and find the shipment sequence no.
	 * 
	 * 
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 * @throws YFSException
	 */

	private String findShipmentSeqNo(YFSEnvironment env, String strOrderHeaderKey)
			throws ParserConfigurationException, RemoteException, YIFClientCreationException {
		String strShipmentSeqNo = "";
		int iShipmentSeqNo = 0;
		Document docInput = XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleShipmentIP = docInput.getDocumentElement();
		eleShipmentIP.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);

		if(log.isDebugEnabled()){
			log.debug("findShipmentSeqNo:Input for getShipmentList " + XMLUtil.getXMLString(docInput));
		}
		Document docOutput = VSIUtils.invokeAPI(env, VSIConstants.API_GET_SHIPMENT_LIST, docInput);

		NodeList nlShipment = docOutput.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
		int iShipmentCount = nlShipment.getLength();
		iShipmentSeqNo = iShipmentCount;
		if(log.isDebugEnabled()){
			log.debug("Shipment count :" + iShipmentCount + " ShipmentSeqNo: " + iShipmentSeqNo);
		}
		strShipmentSeqNo = String.valueOf(iShipmentSeqNo);
		if(log.isDebugEnabled()){
			log.debug("findShipmentSeqNo :Output is : " + strShipmentSeqNo);
		}
		return strShipmentSeqNo;
	}

	/** This method will fetch the order details by calling getOrderList.
	 * 
	 * @param env
	 * @param eleShipment
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 */
	private Element fetchOrderDetails(YFSEnvironment env, Element eleShipment)
			throws RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("Inside fetchOrderDetails :Input  :" + XMLUtil.getElementXMLString(eleShipment));
		}
		String strOrderHeaderKey = eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrderIP = docInput.getDocumentElement();
		eleOrderIP.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		if(log.isDebugEnabled()){
			log.debug("fetchOrderDetails:input for getOrderList " + XMLUtil.getXMLString(docInput));
		}
		Document docOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_CREATE_SHIPMENT,
				VSIConstants.API_GET_ORDER_LIST, docInput);
		if(log.isDebugEnabled()){
			log.debug("getOrderList output" + XMLUtil.getXMLString(docOutput));
		}
		NodeList nlOrder = docOutput.getElementsByTagName(VSIConstants.ELE_ORDER);
		Element eleOrderOuput = null;
		if (nlOrder.getLength() > 0)
			eleOrderOuput = (Element) nlOrder.item(0);
		if(log.isDebugEnabled()){
			log.debug("Output : fetchOrderDetails" + XMLUtil.getElementXMLString(eleOrderOuput));
		}
		return eleOrderOuput;
	}
}
