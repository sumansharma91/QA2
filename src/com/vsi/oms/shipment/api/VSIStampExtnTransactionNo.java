package com.vsi.oms.shipment.api;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

//this class is used for stamping the transactionNo and current date in the shipment xml

public class VSIStampExtnTransactionNo {
	private YFCLogCategory log = YFCLogCategory.instance(VSIStampExtnTransactionNo.class);
	YIFApi api;
	public void stampExtnTransNo(YFSEnvironment env, Document inDoc) {

		String strExtnTransactionNo = "";
		String strShipNodePadded = "";

		try {
			if(log.isDebugEnabled()){
				log.debug("VSIStampExtnTransactionNo : input : " + XMLUtil.getXMLString(inDoc));
			}
			Element eleShipmentList = inDoc.getDocumentElement();

			ArrayList<Element> arrShipments = SCXmlUtil.getChildren(eleShipmentList, VSIConstants.ELE_SHIPMENT);
			for (Element eleShipment : arrShipments) {
				String strDocumentType = eleShipment.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
				if (!YFCCommon.isStringVoid(strDocumentType) && VSIConstants.DOCUMENT_TYPE.equals(strDocumentType)) {
					if(log.isDebugEnabled()){
						log.debug("Shipment is a sales order shipment ,stamping ExtnTransactionNo");
					}

					// Invoke getOrderList and fetch the order details
					Element eleOrder = fetchOrderDetails(env, eleShipmentList);
					if(log.isDebugEnabled()){
						log.debug("fetchOrderDetails output" + XMLUtil.getElementXMLString(eleOrder));
					}
					if (null != eleOrder) {
						Element eleOrderExtn = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_EXTN);
						String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
						if(!VSIConstants.WHOLESALE.equalsIgnoreCase(strOrderType)){
							String strExtnDTCOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_DTC_ORDER);
							String strShipNode = eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE);
							String strExtnSubscriptionOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_SUBSCRIPTION_ORDER);
							String strExtnOriginalADPOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_ORIGINAL_ADP_ORDER);
							String strEnteredBy = eleOrder.getAttribute(VSIConstants.ATTR_ENTERED_BY);
							String strExtnReOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_RE_ORDER);
							NodeList nlOrderLine = eleOrder.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
							String strDeliveryMethod = null;
							if (nlOrderLine != null) {
								Element eleOrderLine = (Element) nlOrderLine.item(0);
								strDeliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);

							}
							if (YFCCommon.isVoid(strDeliveryMethod) && VSIConstants.FLAG_Y.equals(strExtnDTCOrder)) {
								strDeliveryMethod = VSIConstants.ATTR_DEL_METHOD_SHP;
							}

							// logic to set the ExtnTransactionNo
							String seqNum = VSIConstants.SEQ_VSI_SEQ_TRANID;
							log.debug("seqNum" + seqNum);

							// Added new logic-start
							if (!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder)
									&& VSIConstants.ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod)) {
								seqNum = seqNum + "92_";
								log.debug("ReOrders, seqNum" + seqNum);
							}
							/* OMS-1220 changes :end */
							/* OMS-1352 changes start */
							else if (!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder)
									&& (VSIConstants.FLAG_N.equalsIgnoreCase(strExtnDTCOrder)
											|| YFCCommon.isVoid(strExtnDTCOrder))) {
								seqNum = seqNum + "94_";
								log.debug("strExtnReOrder:Y and isDTCOrder: N or null, seqNum" + seqNum);

							}
							/* OMS-1352 changes end */
							/* OMS-1351 changes start */
							else if (!YFCCommon.isVoid(strExtnSubscriptionOrder) && VSIConstants.FLAG_Y.equals(strExtnSubscriptionOrder)
									&& (VSIConstants.FLAG_N.equalsIgnoreCase(strExtnDTCOrder)
											|| YFCCommon.isVoid(strExtnDTCOrder))) {
								seqNum = seqNum + "93_";
								log.debug("isSubscriptionOrder:Y and isDTCOrder: N or null, seqNum" + seqNum);

							}
							/* OMS-1351 changes end */
							else if (!YFCCommon.isVoid(strExtnSubscriptionOrder) && VSIConstants.FLAG_Y.equals(strExtnSubscriptionOrder)) {
								seqNum = seqNum + "91_";
							} // Auto delivery orders get 91_
							// original ADP order changes done for OMS-857
							else if (!YFCCommon.isVoid(strExtnOriginalADPOrder) && VSIConstants.FLAG_Y.equals(strExtnOriginalADPOrder)) {
								seqNum = seqNum + "9_";
							} else if (strOrderType != null
									&& strOrderType.equalsIgnoreCase(VSIConstants.ATTR_ORDER_TYPE_POS)) {
								seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_POS_PICK;
							} // All POS order get 88_
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
							log.debug("SeqNo after ShipNode padding:" + seqNum);
							strExtnTransactionNo = VSIDBUtil.getNextSequence(env, seqNum);
							if(log.isDebugEnabled()){
								log.debug("TransactionNo generated for the order :  " + strExtnTransactionNo);
							}
							Element eleExtn = SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_EXTN);
							if (eleExtn == null) {
								eleExtn = SCXmlUtil.createChild(eleShipment, VSIConstants.ELE_EXTN);

							}
							eleExtn.setAttribute(VSIConstants.ATTR_EXTN_TRANS_NO, strExtnTransactionNo);

							//stamp current date
							DateFormat dateFormat = new SimpleDateFormat(VSIConstants.YYYY_MM_DD_T_HH_MM_SS);
							Date date = new Date();
							String strCurrentdate = dateFormat.format(date);
							eleShipment.setAttribute(VSIConstants.ATTR_CURRENT_SHIPDATE, strCurrentdate);
                            //OMS-2007 START
							//send message to VSIProcessShipConfirmMessage service
							//api = YIFClientFactory.getInstance().getApi();
							//api.executeFlow(env, "VSIProcessShipConfirm_Q", inDoc);
							//OMS-2007 END
						}

					}
				}
			}
			 //OMS-2007 START
			//send message to VSIProcessShipConfirmMessage service
			api = YIFClientFactory.getInstance().getApi();
			api.executeFlow(env, "VSIProcessShipConfirm_Q", inDoc);
			//OMS-2007 END
						
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Error in VSIStampTranNo  " + e.getMessage());
			throw new YFSException();

		}
		if(log.isDebugEnabled()){
			log.debug("VSIStampExtnTransactionNo : output : " + XMLUtil.getXMLString(inDoc));
		}

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
	private Element fetchOrderDetails(YFSEnvironment env, Element eleShipmentList)
			throws RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("Inside fetchOrderDetails :Input  :" + XMLUtil.getElementXMLString(eleShipmentList));
		}
		Element eleOrder = SCXmlUtil.getChildElement(eleShipmentList, VSIConstants.ELE_ORDER);
		String strOrderNo = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO);
		Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrderIP = docInput.getDocumentElement();
		eleOrderIP.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
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


