package com.vsi.oms.api;


import java.io.IOException;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.json.JSONException;
import org.apache.commons.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.riskified.Environment;
import com.riskified.RiskifiedClient;
import com.riskified.RiskifiedError;
import com.riskified.models.Address;
import com.riskified.models.ChargeFreePaymentDetails;
import com.riskified.models.ClientDetails;
import com.riskified.models.CreditCardPaymentDetails;
import com.riskified.models.Customer;
import com.riskified.models.DiscountCode;
import com.riskified.models.LineItem;
import com.riskified.models.Order;
import com.riskified.models.PaypalPaymentDetails;
import com.riskified.models.Response;
import com.riskified.models.ShippingLine;
import com.riskified.validations.FieldBadFormatException;
import com.riskified.validations.Validation;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
// This class is used for sending fraud request to Kount 
public class VSIKountRequestForSTH {
	private YFCLogCategory log = YFCLogCategory.instance(VSIKountRequestForSTH.class);
	YIFApi api;
	public void STHkountRequest(YFSEnvironment env, Document inXML){
		String strOrderHeaderKey = null;
		String strOrderNo = null;
		String strRiskifiedFlag=null;
		log.debug("STHkountRequest input is : "+XMLUtil.getXMLString(inXML));
		if(inXML != null){
			 strOrderHeaderKey = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		}
		if(strOrderHeaderKey != null && !strOrderHeaderKey.equals("")){
			try {
				Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
				Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrderListSOForHold.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				
				log.debug("*** NOW PRINTING getOrderListOp "+XMLUtil.getXMLString(getOrderListOp));
				if(getOrderListOp != null){
					Element eleOrderOutput = (Element) getOrderListOp.getDocumentElement().getElementsByTagName("Order").item(0);
					if(!YFCObject.isVoid(eleOrderOutput)){
					 strOrderNo=eleOrderOutput.getAttribute(VSIConstants.ATTR_ORDER_NO);
					 //OMS-2046 START
					 ArrayList<Element> listRiskifiedFlag;
						listRiskifiedFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_RISKIFIED, VSIConstants.ATTR_DEFAULT);
						if(!listRiskifiedFlag.isEmpty()){
						Element eleCommonCode=listRiskifiedFlag.get(0);
							 strRiskifiedFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_N.equalsIgnoreCase(strRiskifiedFlag)){
											// 1) Form the NVP request String
											String nvpRequest = constructKountRequest(getOrderListOp,env);
											
											log.debug("Printing nvpRequest: "
													+ nvpRequest);
											Document fraudRequest = XMLUtil.createDocument("FraudRequest");
											fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
											String chargeType="Fraud Request";
											storeFraudRequestandResponse(env,strOrderHeaderKey,nvpRequest,chargeType);
											
											api = YIFClientFactory.getInstance().getApi();
											Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
											Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
											if(!YFCObject.isVoid(kountResponse)){
											String responseStr=kountResponse.getTextContent();
											log.debug("Fraud Response: "+ responseStr);
											chargeType="Fraud Response";
											storeFraudRequestandResponse(env,strOrderHeaderKey,responseStr,chargeType);
											
											// 3) Process the response and act
											// accordingly within OMS
											processKountResponse(
													env, responseStr,
													 strOrderHeaderKey, strOrderNo);

										}
											else{
																																
												resolveAndApplyHold(env,strOrderHeaderKey);
											}
							}
							else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.ATTR_BOTH.equalsIgnoreCase(strRiskifiedFlag)){
								// 1) Form the NVP request String
								String nvpRequest = constructKountRequest(getOrderListOp,env);

								log.debug("Printing nvpRequest: "
										+ nvpRequest);
								Document fraudRequest = XMLUtil.createDocument("FraudRequest");
								fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
								String chargeType="Fraud Request";
								storeFraudRequestandResponse(env,strOrderHeaderKey,nvpRequest,chargeType);

								api = YIFClientFactory.getInstance().getApi();
								Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
								Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
								if(!YFCObject.isVoid(kountResponse)){
									String responseStr=kountResponse.getTextContent();
									log.debug("Fraud Response: "+ responseStr);
									chargeType="Fraud Response";
									storeFraudRequestandResponse(env,strOrderHeaderKey,responseStr,chargeType);

									// 3) Process the response and act
									// accordingly within OMS
									processKountResponse(
											env, responseStr,
											strOrderHeaderKey, strOrderNo);

								}
								else{

									resolveAndApplyHold(env,strOrderHeaderKey);
								}
								Order order=constructRiskifiedRequest(getOrderListOp,env);
								
								String chargeType1="Fraud Request";
								Gson gson = new Gson();
								String strOrder = com.riskified.JSONFormater.toJson(order);

								log.debug("Riskified Request: "
										+ strOrder);
								storeFraudRequestandResponse(env,strOrderHeaderKey,strOrder,chargeType1);
								
								//riskified response
								Validation c1=Validation.IGNORE_MISSING;
								Environment e1=Environment.SANDBOX;
								RiskifiedClient client = new RiskifiedClient();

								Response res = client.analyzeOrder(order);
								String strResponse = com.riskified.JSONFormater.toJson(res);
								log.debug("Riskified Response: "+ strResponse);
								//String strStatus=res.getOrder().getStatus();
								String chargeType2="Fraud Response";
								if(!YFCObject.isVoid(strResponse)){
									// 3) Process the response and act
									// accordingly within OMS
								storeFraudRequestandResponse(env,strOrderHeaderKey,strResponse,chargeType2);
								}

							}
							else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
											//OMS-2046 START
								//construct Riskified request
								log.debug("*** RISKIFIED FLAG IS TRUE");
								
								//OMS-2419 -- Start
								int iRiskifiedCount = 0;
								int iRiskifiedCountCC = 0;
								boolean isRiskifiedCallReqd = false;
								Element eleOrderExtn = SCXmlUtil.getChildElement(eleOrderOutput, VSIConstants.ELE_EXTN);
								String strRiskifiedCount = eleOrderExtn.getAttribute("ExtnRiskifiedCount");
								if(YFCCommon.isVoid(strRiskifiedCount)){
									isRiskifiedCallReqd=true;									
								}
								else{									
									iRiskifiedCount=Integer.parseInt(strRiskifiedCount);								
									ArrayList<Element> listRiskifiedCount;
									listRiskifiedCount = VSIUtils.getCommonCodeListWithCodeType(env, "RISKIFIED_CALL_COUNT", null, VSIConstants.ATTR_DEFAULT);
									if(!listRiskifiedCount.isEmpty()){
										Element eleRiskifiedCount=listRiskifiedCount.get(0);
										String strRiskifiedCountCC=eleRiskifiedCount.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
										if(!YFCCommon.isVoid(strRiskifiedCountCC)){
											iRiskifiedCountCC = Integer.parseInt(strRiskifiedCountCC);
										}
									}
								
									if(iRiskifiedCount<iRiskifiedCountCC){
										isRiskifiedCallReqd=true;
									}
								}
								if(isRiskifiedCallReqd){
									log.debug("Number of Riskified Requests is within the allowed limit, Riskified Call will be triggerred");
									//OMS-2419 -- End
								            Order order=constructRiskifiedRequest(getOrderListOp,env);
											
											String chargeType="Fraud Request";
											Gson gson = new Gson();
											String strOrder = com.riskified.JSONFormater.toJson(order);

											log.debug("Riskified Request: "
													+ strOrder);
											storeFraudRequestandResponse(env,strOrderHeaderKey,strOrder,chargeType);
											
											//riskified response
											Validation c1=Validation.IGNORE_MISSING;
											Environment e1=Environment.SANDBOX;
											RiskifiedClient client = new RiskifiedClient();

											Response res = client.analyzeOrder(order);
											String strResponse = com.riskified.JSONFormater.toJson(res);
											log.debug("Riskified Response: "+ strResponse);
											String strStatus=res.getOrder().getStatus();
											chargeType="Fraud Response";
											if(!YFCObject.isVoid(strResponse)){
												
												//OMS-2419 -- Start
												updateRiskifiedCount(env,strOrderHeaderKey,iRiskifiedCount);
												//OMS-2419 -- End
												
												// 3) Process the response and act
												// accordingly within OMS
												
												storeFraudRequestandResponse(env,strOrderHeaderKey,strResponse,chargeType);
											}
											
											if(!YFCObject.isVoid(strStatus)){
											processRiskifiedResponse(
													env, strStatus,
													 strOrderHeaderKey, strOrderNo);
											}
											else
											{
												resolveAndApplyHold(env,strOrderHeaderKey);
											}
											//OMS-2046 END
								//OMS-2419 -- Start
								}
								else{
									log.debug("Number of Riskified Requests has exceeded the allowed limit, hence will not be triggerred");
								}
								//OMS-2419 -- End
							}
						}
					}
				}
		
			} catch (YFSException | RemoteException
					| YIFClientCreationException |ParserConfigurationException e) {
				
										resolveAndApplyHold(env,strOrderHeaderKey);
				

			} catch (ParseException e) {
				if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
					resolveAndApplyHold(env,strOrderHeaderKey);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (RiskifiedError e) {
				if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
					resolveAndApplyHold(env,strOrderHeaderKey);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (IOException e) {
				if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
					resolveAndApplyHold(env,strOrderHeaderKey);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (FieldBadFormatException e) {
				if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
					resolveAndApplyHold(env,strOrderHeaderKey);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch (Exception e) {
				if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
					resolveAndApplyHold(env,strOrderHeaderKey);
				}
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
		}

	log.info("VSIKountRequestForSTH End ");

	}


	//OMS-2419 -- Start
	/**
	 * @param env
	 * @param strOrderHeaderKey
	 * @param iRiskifiedCount
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 */
	private void updateRiskifiedCount(YFSEnvironment env,
			String strOrderHeaderKey, int iRiskifiedCount)
			throws ParserConfigurationException, YIFClientCreationException,
			RemoteException {
		
		log.info("Inside updateRiskifiedCount method");
		log.info("Current value of RiskifiedCount: "+Integer.toString(iRiskifiedCount));
		int iUpdtRiskifiedCount = iRiskifiedCount+1;
		String strUpdtRiskifiedCount = Integer.toString(iUpdtRiskifiedCount);
		log.info("Updated value of RiskifiedCount: "+strUpdtRiskifiedCount);
		Document updateRiskifiedCountDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleRiskifiedCount =updateRiskifiedCountDoc.getDocumentElement();
		eleRiskifiedCount.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
		eleRiskifiedCount.setAttribute("Override", "Y");												
		Element eleRiskifiedExtn = SCXmlUtil.createChild(eleRiskifiedCount, VSIConstants.ELE_EXTN);												
		eleRiskifiedExtn.setAttribute("ExtnRiskifiedCount",strUpdtRiskifiedCount);
		
		log.info("Change Order Input to update ExtnRiskifiedCount: "
				+ XMLUtil.getXMLString(updateRiskifiedCountDoc));
      
		VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, updateRiskifiedCountDoc);
		
		log.info("ExtnRiskifiedCount updated successfully");
		
		log.info("Exiting updateRiskifiedCount method");
	}
	
	//OMS-2419 -- End

//OMS-2046 START

	private void processRiskifiedResponse(YFSEnvironment env,
			String strResponse, String ohk, String orderNo) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException {
		// TODO Auto-generated method stub
		log.info("Printing RiskifiedRespnse: " + strResponse);
		

		if (strResponse != null ) {
			if (strResponse.contains(VSIConstants.ATTR_STATUS_APPROVE)) {
				Document orderInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element sOrderElement = orderInput.getDocumentElement();
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				resolveHold(env,ohk);				
				api = YIFClientFactory.getInstance().getApi();
				api.executeFlow(env, "VSIScheduleSTH_Q", orderInput);
				//OMS-2088
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_NO,orderNo);
				sOrderElement.setAttribute(VSIConstants.FRAUD_RESULT,"Approved");
				try {
					VSIUtils.invokeService(env, "VSISendAcceptOrDeclinedToATG", orderInput);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else if(strResponse.contains(VSIConstants.ATTR_STATUS_DECLINED)) {
				// Cancel Order
				Document changeOrderInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element orderElement = changeOrderInput.getDocumentElement();
				orderElement.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
				orderElement.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT,
						"Fraud Check Declined");
				orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE,
						"Fraud Check Declined");
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				log.debug("changeOrderInput: "
						+ XMLUtil.getXMLString(changeOrderInput));

				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, changeOrderInput);
				//OMS-2088
				Document orderInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element sOrderElement = orderInput.getDocumentElement();
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_NO,orderNo);
				sOrderElement.setAttribute(VSIConstants.FRAUD_RESULT,"Declined");
				try {
					VSIUtils.invokeService(env, "VSISendAcceptOrDeclinedToATG", orderInput);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			else{
			resolveAndApplyHold(env,ohk);
			}
		}

	}	//OMS-2046 END


	private void processKountResponse(YFSEnvironment env,
			String fraudResponse, String ohk, String orderNo)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		log.info("Printing fraudRespnse: " + fraudResponse);

		String strAuto = "";

		if (fraudResponse != null ) {
		
		
			if (fraudResponse.contains("AUTO=A")) {
				strAuto = "A";
			}
			else if(fraudResponse.contains("AUTO=D")) {
				strAuto = "D";
			}
			else if (fraudResponse.contains("AUTO=R")) {
				strAuto = "R";
			}
			else if(fraudResponse.contains("ERR")) {
				strAuto = "ERR";
			}

		
		log.info("Printing AUTO: " + strAuto);
		if (strAuto != null && strAuto.trim().equalsIgnoreCase("A")) {

			Document orderInput = XMLUtil.createDocument("Order");
			Element sOrderElement = orderInput.getDocumentElement();
			sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
			
			resolveHold(env,ohk);
			api = YIFClientFactory.getInstance().getApi();
		    api.executeFlow(env, "VSIScheduleSTH_Q", orderInput);
			
		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("D")) {

			// Cancel Order
			
			Document changeOrderInput = XMLUtil.createDocument("Order");
			Element orderElement = changeOrderInput.getDocumentElement();
			orderElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			orderElement.setAttribute("Override", "Y");
			orderElement.setAttribute("ModificationReasonText",
					"Fraud Check Declined");
			orderElement.setAttribute("ModificationReasonCode",
			"Fraud Check Declined");
			orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
			log.debug("changeOrderInput: "
					+ XMLUtil.getXMLString(changeOrderInput));

			VSIUtils.invokeAPI(env, "changeOrder", changeOrderInput);

		}
			
			else if (strAuto != null && strAuto.trim().equalsIgnoreCase("ERR")) {
				
				resolveAndApplyHold(env,ohk);
				
				int errLength=fraudResponse.length();
				if(errLength > 100){
					fraudResponse=fraudResponse.substring(0, 99);
				}
				raiseAlert(env,orderNo,ohk,fraudResponse);
				

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("R")) {
			

			Document processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element orderEle =processedHoldDoc.getDocumentElement();
			orderEle.setAttribute("Override", "Y");
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_FRD_REVIEW_HOLD");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Fraud Check Under Review");
			Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_KOUNT_STH_HOLD");
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, "1300");
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Kount Call STH Hold");
	log.info("Printing processedHoldDoc: "
			+ XMLUtil.getXMLString(processedHoldDoc));
	VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);
			raiseAlert(env, orderNo,ohk,"Fraud Check Under Review");
		}
		
		else 
		{
			resolveAndApplyHold(env,ohk);
					
		}
		}

		}

	private void resolveAndApplyHold(YFSEnvironment env, String ohk) {
		Document processedHoldDoc=null;
		Element eleOrder=null;
		Element eleOrderHoldTypes=null;
		Element eleOrderHoldType=null;
		Element eleOrderHoldType1=null;
		try {
		processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		eleOrder =processedHoldDoc.getDocumentElement();
		eleOrder.setAttribute("Override", "Y");
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
		eleOrderHoldTypes = SCXmlUtil.createChild(eleOrder, VSIConstants.ELE_ORDER_HOLD_TYPES);
		eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
				"VSI_FRAUD_HOLD");
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
				"Fraud Verification Hold");
		eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
		eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
				"VSI_KOUNT_STH_HOLD");
		eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, "1300");
		eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
				"Kount Call STH Hold");
       log.info("Printing processedHoldDoc: "
		+ XMLUtil.getXMLString(processedHoldDoc));
       VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);	
		} catch (ParserConfigurationException | YFSException | RemoteException | YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}


	private void resolveHold(YFSEnvironment env, String ohk) {
		Document docProcessedHold=null;
		Element orderEle=null;
		Element eleOrderHoldTypes=null;
		Element eleOrderHoldType=null;
		try {
		    docProcessedHold =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			orderEle =docProcessedHold.getDocumentElement();
			orderEle.setAttribute("Override", "Y");
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
			eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_KOUNT_STH_HOLD");
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, "1300");
			eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Kount Call STH Hold");
	log.info("Printing processedHoldDoc: "
			+ XMLUtil.getXMLString(docProcessedHold));
	VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docProcessedHold);	

		} catch (ParserConfigurationException | YFSException | RemoteException | YIFClientCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}


	public void storeFraudRequestandResponse(YFSEnvironment env,String ohk,String request, String chargeType) 
	throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException{
		Document doc = XMLUtil.createDocument("PaymentRecords");
  		Element elePayRecrds = doc.getDocumentElement();
  		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
  				ohk);
  		elePayRecrds.setAttribute("Record", request);
  		elePayRecrds.setAttribute("ChargeType", chargeType);
  		YIFApi api;
  		api = YIFClientFactory.getInstance().getApi();
  		api.executeFlow(env,"VSIPaymentRecords", doc);
		
		
	}



	private void raiseAlert(YFSEnvironment env, String orderNo, String ohk,String reason)
			throws YFSException, RemoteException, YIFClientCreationException,
			ParserConfigurationException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();

		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);

		InboxElement.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION,
				reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_FRAUD_ALERT");

		Element InboxReferencesListElement = createExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = createExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderHeaderKey");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, ohk);

		InboxReferencesListElement.appendChild(InboxReferencesElement);
		InboxElement.setAttribute("Consolidate",
		"Y");
		
		Element consolidationTemplate = createExInput
		.createElement("ConsolidationTemplate");
		InboxElement.appendChild(consolidationTemplate);
		Element InboxCpyEle = createExInput.createElement("Inbox");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		InboxCpyEle.setAttribute("Consolidate", "Y");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
		InboxCpyEle.setAttribute("DetailDescription",
				reason);
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				reason);
		InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Fraud Check");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"Fraud Check");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
		InboxCpyEle.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_FRAUD_ALERT");
		
		consolidationTemplate.appendChild(InboxCpyEle);

		/*System.out.println("Printing createExInput: "
				+ XMLUtil.getXMLString(createExInput));*/
		VSIUtils.invokeAPI(env, VSIConstants.API_CREATE_EXCEPTION,
				createExInput);

	}
	public Order constructRiskifiedRequest(Document getOrderListOp,
			YFSEnvironment env) throws ParseException, RiskifiedError, IOException, FieldBadFormatException, TransformerException {
		log.debug("*** NOW PRINTING constructRiskifiedRequest ");
		Element eleOrderList=getOrderListOp.getDocumentElement();
		Element eleOrder=SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
		String strEntryType=eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
		Element eleOrderExtn=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_EXTN);
		String strExtnSubscriptionOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_SUBSCRIPTION_ORDER);
		String strExtnOriginalADPOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_ORIGINAL_ADP_ORDER);
		String strExtnBrowserIP = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_BROWSER_IP);
		String strExtnCustomerCreatets = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_CUSTOMER_CREATETS);
		String strExtnAcceptUser = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_ACCEPT_USER);
		String strExtnUserAgent = eleOrderExtn.getAttribute(VSIConstants.ATTR_USER_AGENT);
		String strExtnCheckoutId = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_CHECKOUTID);
		String strExtnAurusToken = eleOrderExtn.getAttribute(VSIConstants.EXTN_AURUS_TOKEN);
		
		String strCustomerFirstName=eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_FIRST_NAME);
		String strCustomerLastName=eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_LAST_NAME);
		String strBillToID=eleOrder.getAttribute(VSIConstants.ATTR_BILL_TO_ID);
		String createts=eleOrder.getAttribute(VSIConstants.ATTR_CREATETS);
		//OMS-2405 -- Start
		String modifyts=eleOrder.getAttribute(VSIConstants.ATTR_MODIFYTS);
		//OMS-2405 -- End		
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-hh",Locale.US); 

		//order
		Order order = new Order();
		List<LineItem> lineItems = new ArrayList<LineItem>();
		List<DiscountCode> Discount = new ArrayList<DiscountCode>();
		order.setId(eleOrder.getAttribute(VSIConstants.ATTR_ORDER_NO));
		order.setEmail(eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID));
		
		//OMS-2405 -- Start
		if(!YFCCommon.isVoid(strEntryType) && VSIConstants.ENTRYTYPE_CC.equals(strEntryType)){
			order.setCreatedAt(dt.parse(modifyts));
			order.setUpdatedAt(dt.parse(modifyts));
		}else{
			order.setCreatedAt(dt.parse(createts));
			order.setUpdatedAt(dt.parse(createts));
		}
		//OMS-2405 -- End		
		
		order.setCurrency(VSIConstants.ATTR_CURRENCY);		
		if(!YFCObject.isVoid(strEntryType)&&!YFCObject.isVoid(strExtnCheckoutId)&&!((VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType))){
			order.setCheckoutId(strExtnCheckoutId);
		}

		//Changes for OMS-2177 -- Start
		String strExtnMobileOrder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_MOBILE_ORDER);
		//Changes for OMS-2177 -- End
		
		//Changes for OMS-2192 -- Start
		String strExtnReorder = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_RE_ORDER);		

		if(!YFCCommon.isVoid(strExtnReorder) && VSIConstants.FLAG_Y.equals(strExtnReorder)){
			order.setSource(VSIConstants.ATTR_REORDER);
		}
		//Changes for OMS-2192 -- End
		else if(!YFCObject.isVoid(strEntryType)&&(VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType)){
			order.setSource(VSIConstants.ATTR_PHONE);
		}
		else if(!YFCObject.isVoid(strExtnSubscriptionOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnSubscriptionOrder)){
			order.setSource(VSIConstants.ATTR_SUBSCRIPTION);
		}
		/*else if(!YFCObject.isVoid(strExtnOriginalADPOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnOriginalADPOrder)){
			order.setSource(VSIConstants.ATTR_INITIAL_SUBSCRIPTION);
		}*/
		//Changes for OMS-2177 -- Start
		else if(!YFCCommon.isVoid(strExtnMobileOrder) && VSIConstants.FLAG_Y.equals(strExtnMobileOrder)){
			String strExtnMobileType = eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_MOBILE_TYPE);
			order.setSource(strExtnMobileType);			
		}
		//Changes for OMS-2177 -- End
		else{
			order.setSource(VSIConstants.ATTR_WEB);
		}
		String strAcceptUser=null;
		String strUserAgent=null;
		String strCustomerCreatets=null;
		if(!YFCObject.isVoid(strExtnAcceptUser)){
			 strAcceptUser=strExtnAcceptUser;
		}
		else{
			 strAcceptUser="en-CA";
		}
		
		if(!YFCObject.isVoid(strExtnUserAgent)){
			 strUserAgent=strExtnUserAgent;
		}
		else{
			strUserAgent="Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)";
		}
		if(!YFCObject.isVoid(strExtnCustomerCreatets)){
			strCustomerCreatets=strExtnCustomerCreatets;
		}
		else{
			strCustomerCreatets=createts;
		}
			
		Element eleOverallTotals=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_OVERALL_TOTALS);
		String strGrandTotal = eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_TOTAL);
		String strGrandDiscount = eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_DISCOUNT);
		String strGrandShippingCharges=eleOverallTotals.getAttribute(VSIConstants.ATTR_GRAND_SHIPPING_CHARGE);
		double dGrandTotal=0.00;
		double dGrandDiscount=0.00;
		double dGrandShippingCharges=0.00;
		if(!YFCObject.isVoid(strGrandTotal)){
		 dGrandTotal = Double.parseDouble(strGrandTotal);
		}
		if(!YFCObject.isVoid(strGrandDiscount)){
		 dGrandDiscount = Double.parseDouble(strGrandDiscount);
		}
		if(!YFCObject.isVoid(strGrandShippingCharges)){
		 dGrandShippingCharges = Double.parseDouble(strGrandShippingCharges);
		}

		order.setTotalPrice(dGrandTotal);
		order.setTotalDiscounts(dGrandDiscount);
		
		if(eleOrderExtn!=null &&!VSIConstants.ENTRYTYPE_CC.equalsIgnoreCase(strEntryType)){
			String extnSessionId=eleOrderExtn.getAttribute(VSIConstants.ATTR_EXTN_RISKIFIED_SESSION_ID);
			if(!YFCCommon.isVoid(strExtnBrowserIP)&&!VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnSubscriptionOrder)){
			order.setBrowserIp(strExtnBrowserIP);
			}
			else if(!VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnSubscriptionOrder)){
			order.setBrowserIp("50.49.141.248");
			}
			if(!YFCCommon.isVoid(extnSessionId)){
			order.setCartToken(extnSessionId);
			}
		}
		
		order.setReferringSite("null");

		// LineItems
		NodeList nlOrderLines = eleOrder
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		Element orderLineElement = (Element) getOrderListOp.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		int noOfLines = nlOrderLines.getLength();
		String strSCAC=null;
		String strLineType=null;
		int k=1;
		for (int i = 0; i < noOfLines; i++) {
			Element orderLineEle = (Element) nlOrderLines.item(i);
			strLineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);			
			strSCAC = orderLineEle.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE);

			Element eleItem = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_ITEM).item(0);

			Element eleItemDetails = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_ITEM_DETAILS).item(0);
			Element eleItemExtn = (Element) eleItemDetails.getElementsByTagName(
					VSIConstants.ELE_EXTN).item(0);
			//OMS-968 : End
			Element elelinePrice = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_LINE_PRICE).item(0);


			String strItemDesc= eleItem.getAttribute(VSIConstants.ATTR_ITEM_DESC);
			String strSkuId= eleItemExtn.getAttribute(VSIConstants.EXTN_ACT_SKU_ID);
			String strItemId= eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
			String strBrandTitle= eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE);
			String strLineTotal=elelinePrice.getAttribute(VSIConstants.ATTR_UNIT_PRICE);
			String strQty=orderLineEle.getAttribute(VSIConstants.ATTR_ORD_QTY);
			String strOrderLineKey = orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			Element eleFirstDelayDate = XMLUtil.getElementByXPath(getOrderListOp,
					"OrderList/Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_FIRST_PROMISE_DATE']");
			


			int dQty=Integer.parseInt(strQty);

			if(dQty!=0.00){

				double dUnitPrice=Double.parseDouble(strLineTotal);


				LineItem item = new LineItem(dUnitPrice, dQty, strItemDesc, strSkuId);
				item.setBrand(strBrandTitle);
				item.setSku(strItemId);
				item.setProductType("physical");
				item.setProperties(null);
				item.setTaxLines(null);
				String isGiftCardItem = eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_ITEM_TYPE);

				if (VSIConstants.GIFT_CARD.equals(isGiftCardItem)|| VSIConstants.GIFT_CARD_VAR.equals(isGiftCardItem)) 
				{
					String strsku="giftcard"+k;
					item.setSku(strsku);
					item.setRequiresShipping(true);
					k = k+1;
				}
				boolean bbackorder=false;
				if(!YFCCommon.isVoid(eleFirstDelayDate)){
					String strActualDate=eleFirstDelayDate.getAttribute(VSIConstants.ATTR_ACTUAL_DATE);
					String strExpectedDate=eleFirstDelayDate.getAttribute(VSIConstants.ATTR_EXPECTED_DATE);
					String strShipmentDateFormat=null;
					if(!YFCCommon.isVoid(strActualDate)||!YFCCommon.isVoid(strExpectedDate)){
					if(!YFCCommon.isVoid(strActualDate)){
						 strShipmentDateFormat=strActualDate.substring(0, strActualDate.indexOf("T")).replaceAll("-", "");
					}
					else if(!YFCCommon.isVoid(strExpectedDate)){
						 strShipmentDateFormat=strExpectedDate.substring(0, strExpectedDate.indexOf("T")).replaceAll("-", "");
					}

						DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
						Date date = new Date();	
						String currentdate = dateFormat.format(date);
						Date dcurrentdate = dateFormat.parse(currentdate);
						 Calendar c = Calendar.getInstance();
						 c.setTime(dcurrentdate);
						 c.add(Calendar.DAY_OF_MONTH, 1);
						 Date currentDatePlusTwo = c.getTime();
						Date dstrShipmentDateFormat = dateFormat.parse(strShipmentDateFormat);
						if(dstrShipmentDateFormat.after(currentDatePlusTwo)){
							bbackorder=true;
							//item.setSubCategory("backorder");
						}
					}
				}
				if(bbackorder&&!YFCObject.isVoid(strExtnOriginalADPOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnOriginalADPOrder)){
					item.setSubCategory(VSIConstants.ATTR_INITIAL_SUBSCRIPTION +" and backorder");
				}
				else if(bbackorder){
					item.setSubCategory("backorder");
				}
				else if(!YFCObject.isVoid(strExtnOriginalADPOrder)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnOriginalADPOrder)){
					item.setSubCategory(VSIConstants.ATTR_INITIAL_SUBSCRIPTION);
				}
				lineItems.add(item);

				//discount

				NodeList ndlLineCharges  = orderLineEle.getElementsByTagName(VSIConstants.ELE_LINE_CHARGE);

				for(int j= 0; j < ndlLineCharges.getLength(); j++){
					Element eleLineCharge = (Element)ndlLineCharges.item(j);

					if(!YFCObject.isVoid(eleLineCharge)){
						String strChargeCategory = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
						if(VSIConstants.DISCOUNT.equalsIgnoreCase(strChargeCategory) || VSIConstants.DISCOUNT_CATEGORY_ADJ.equalsIgnoreCase(strChargeCategory)){
							String strChargeAmount = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
							String strChargeName = eleLineCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
							double dstrChargeAmount=Double.parseDouble(strChargeAmount);
							Discount.add(new DiscountCode(dstrChargeAmount,strChargeName));
						}
					}
				}


			}
		}
		
		//Changes for OMS-2193 -- Start
		Element eleHeaderCharges = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_HEADER_CHARGES);
		NodeList ndlHeaderCharge = eleHeaderCharges.getElementsByTagName(VSIConstants.ELE_HEADER_CHARGE);
		for(int m= 0; m < ndlHeaderCharge.getLength(); m++){
			
			Element eleHeaderCharge = (Element)ndlHeaderCharge.item(m);
			if(!YFCCommon.isVoid(eleHeaderCharge)){
				String strHdrChrgCategory = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
				if(VSIConstants.DISCOUNT_CATEGORY_ADJ.equals(strHdrChrgCategory)){
					String strHdrChrgName = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
					String strHdrChrgAmnt = eleHeaderCharge.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
					double dHdrChrgAmnt = Double.parseDouble(strHdrChrgAmnt);
					Discount.add(new DiscountCode(dHdrChrgAmnt,strHdrChrgName));
				}
			}
			
		}
		//Changes for OMS-2193 -- End
		order.setLineItems(lineItems);
		order.setDiscountCodes(Discount);


		// ShippingLines
		if(VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType) || VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)){
			//order.setSource(VSIConstants.ATTR_WEB);
			order.setShippingLines(Arrays.asList(new ShippingLine(dGrandShippingCharges, VSIConstants.ATTR_STORE_PICK_UP)));

		}
		else{
			if(YFCCommon.isVoid(strSCAC)){
				strSCAC="STANDARD";
			}
			order.setShippingLines(Arrays.asList(new ShippingLine(dGrandShippingCharges, strSCAC)));
		}

		//PaymentDetails
		boolean bvoucher=false;
		boolean bcreditCard=false;
		 boolean bpaypal=false;
		 boolean bgcCard=false;
		NodeList nlPaymtMthd = eleOrder
				.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
		int pymthdCount = nlPaymtMthd.getLength();
		String strPaymentTechCode=null;
		List<ChargeFreePaymentDetails> chargefreePaymentDetails= new ArrayList<ChargeFreePaymentDetails>();
		for (int n = 0; n < pymthdCount; n++) {
			Element paymtMthdEle = (Element) nlPaymtMthd.item(n);
			String strAvsResp=SCXmlUtil.getXpathAttribute(eleOrder, "Extn/TNSAuthRecordsList/TNSAuthRecords/@AvsGatewayCode");
			String strCVVResp=SCXmlUtil.getXpathAttribute(eleOrder, "Extn/TNSAuthRecordsList/TNSAuthRecords/@CVVResp");
			if(YFCCommon.isStringVoid(strCVVResp)){
				strCVVResp="null";
			}
			//log.debug("AvsResp : "+ strAvsResp);
			/*if(!YFCCommon.isStringVoid(strAvsResp)){
				//Sample value for strAvsResp AvsResp-I8, fetch the value after -
				String[] arrAvsResp= strAvsResp.split("-");
				if(arrAvsResp.length==2){
					 strPaymentTechCode=arrAvsResp[1];
					
				}
			}*/
			if (paymtMthdEle != null) {

				String strPaymentType = paymtMthdEle
						.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
				String strCreditCardNumber=null;
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_CC)) {
					String strCreditCarType = paymtMthdEle
							.getAttribute(VSIConstants.ATTR_CREDIT_CARD_TYPE);
					Element eleExtnPayment = SCXmlUtil.getChildElement(paymtMthdEle, VSIConstants.ELE_EXTN);
					if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)&&!YFCCommon.isVoid(eleExtnPayment)&&!YFCCommon.isStringVoid(eleExtnPayment.getAttribute("ExtnAurusReferralNUM"))){
						 strCreditCardNumber=eleExtnPayment.getAttribute("ExtnAurusReferralNUM");
							
						}
						else{
						 strCreditCardNumber = paymtMthdEle.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_2);
						}
	                    String creditCardBin=null;
	                    String strCardNo=null;
	                    if(!YFCCommon.isVoid(strCreditCardNumber) ){
						 creditCardBin=strCreditCardNumber.substring(0, 6);
						 strCardNo=strCreditCardNumber.substring(strCreditCardNumber.length()-4);
	                    }
					String strTotalAuthorized = paymtMthdEle
							.getAttribute(VSIConstants.ATTR_TOTAL_AUTHORIZED);
					if(!YFCCommon.isVoid(strTotalAuthorized)&& Double.parseDouble(strTotalAuthorized)!=0.0){
						 bcreditCard=true;
						 order.setPaymentDetails(Arrays.asList(new CreditCardPaymentDetails(creditCardBin, strAvsResp, strCVVResp, strCardNo, strCreditCarType)));

						}
					if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)){
					order.setGateway("Aurus");
					}
					else{
						order.setGateway("TNS");
					}
						

									}
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_PP)) {
					order.setGateway("Paypal");
					 bpaypal=true;
					String strPayerEmail = paymtMthdEle.getAttribute(VSIConstants.ATTR_PAYMENT_REFERENCE_3);
					order.setPaymentDetails(Arrays.asList(new PaypalPaymentDetails(strPayerEmail, "verified", "unconfirmed", "Eligible")));
				}
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_GC)) {

					String strTotalAuthorized = paymtMthdEle
							.getAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT);
					order.setGateway("giftcard");
				
					double dTotalAuthorized=Double.parseDouble(strTotalAuthorized);
					if(!YFCCommon.isVoid(dTotalAuthorized)&& dTotalAuthorized!=0.0){
						bgcCard=true;
					if(order.getChargeFreePaymentDetails() != null && VSIConstants.PAYMENT_MODE_GC == order.getChargeFreePaymentDetails().getGateway()){
						order.getChargeFreePaymentDetails().setAmount(order.getChargeFreePaymentDetails().getAmount()+ dTotalAuthorized);
					}else{
						ChargeFreePaymentDetails chargeFreePaymentDetails=new ChargeFreePaymentDetails(VSIConstants.PAYMENT_MODE_GC, dTotalAuthorized);
						chargefreePaymentDetails.add(chargeFreePaymentDetails);
						//order.setChargeFreePaymentDetails(chargeFreePaymentDetails);
					}

				}
				}
				if (strPaymentType != null
						&& strPaymentType.equalsIgnoreCase(VSIConstants.PAYMENT_MODE_VOUCHERS)) {

					String strTotalAuthorized = paymtMthdEle
							.getAttribute(VSIConstants.ATTR_TOTAL_CHARGED);
					order.setGateway("healthyawards");
					
					double dTotalAuthorized=Double.parseDouble(strTotalAuthorized);
					if(!YFCCommon.isVoid(dTotalAuthorized)&& dTotalAuthorized!=0.0){
						bvoucher=true;
					if(order.getChargeFreePaymentDetails() != null && VSIConstants.PAYMENT_MODE_VOUCHERS == order.getChargeFreePaymentDetails().getGateway()){
						order.getChargeFreePaymentDetails().setAmount(order.getChargeFreePaymentDetails().getAmount()+ dTotalAuthorized);
					}else{
						ChargeFreePaymentDetails chargeFreePaymentDetails=new ChargeFreePaymentDetails(VSIConstants.PAYMENT_MODE_VOUCHERS, dTotalAuthorized);
						chargefreePaymentDetails.add(chargeFreePaymentDetails);
						//order.setChargeFreePaymentDetails(chargeFreePaymentDetails);
					}
				}
				}
			}
		}
		/*Map<String, Object> m= new HashMap(); 
		m.put("charge_free_payment_details", chargefreePaymentDetails);
		
		order.setAdditionalData(m);*/
		if(chargefreePaymentDetails.size()>0) {
			ChargeFreePaymentDetails cfg=createChargeFreePaymentDetailsObj(chargefreePaymentDetails);
			order.setChargeFreePaymentDetails(cfg);
		}
		if(bcreditCard&&(bvoucher||bgcCard)){
			if(!YFCCommon.isVoid(strExtnAurusToken) && VSIConstants.FLAG_Y.equals(strExtnAurusToken)){
				order.setGateway("Aurus");
			}
			else{
			order.setGateway("TNS");
			}
		}
		else if(bpaypal&&(bvoucher||bgcCard))
		{
			order.setGateway("paypal");
		}
		else if(bvoucher&&bgcCard){
			order.setGateway("giftcard");
		}
		else if(bvoucher){
			order.setGateway("healthyawards");
		}
		else if(bvoucher||bgcCard){
			order.setGateway("giftcard");
		}
		//BillingAddress
		Element eleBillToAdd = (Element) eleOrder.getElementsByTagName(
				VSIConstants.ELE_PERSON_INFO_BILL_TO).item(0);
		if (eleBillToAdd != null) {
			String strFirstName = eleBillToAdd.getAttribute(VSIConstants.ATTR_FIRST_NAME);
			String strLastname= eleBillToAdd.getAttribute(VSIConstants.ATTR_LAST_NAME);
			String address1 = eleBillToAdd.getAttribute(VSIConstants.ATTR_ADDRESS1);
			String address2 = eleBillToAdd.getAttribute(VSIConstants.ATTR_ADDRESS2);
			String city = eleBillToAdd.getAttribute(VSIConstants.ATTR_CITY);
			String state = eleBillToAdd.getAttribute(VSIConstants.ATTR_STATE);
			String country = eleBillToAdd.getAttribute(VSIConstants.ATTR_COUNTRY);
			String postalCode = eleBillToAdd.getAttribute(VSIConstants.ATTR_ZIPCODE);
			String phone = eleBillToAdd.getAttribute(VSIConstants.ATTR_DAY_PHONE);
			if(YFCCommon.isVoid(strFirstName)){
				strFirstName="Dummy";
			}
			if(YFCCommon.isVoid(strLastname)){
				strLastname="Dummy";
			}
			if(YFCCommon.isVoid(address1)){
				address1="Dummy";
			}
			if(YFCCommon.isVoid(address2)){
				address2="null";
			}
			if(YFCCommon.isVoid(phone)){
				phone="1234567891";
			}

			Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
			address.setAddress2(address2);

			address.setCountryCode(country);

			//address.setProvince(state);
			address.setProvinceCode(state);
			address.setZip(postalCode);
			order.setBillingAddress(address);
		}

		//ShippingAddress
		Element eleShipToAdd1 = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_BILL_TO);
		String strFirstName=null;
		String strLastname=null;
		Element eleShipToAdd = (Element) orderLineElement.getElementsByTagName(VSIConstants.ELE_PERSON_INFO_SHIP_TO).item(0);
		if (eleShipToAdd != null) {
			if(VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType)||VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)){
				 strFirstName = eleShipToAdd1.getAttribute(VSIConstants.ATTR_FIRST_NAME);
				 strLastname= eleShipToAdd1.getAttribute(VSIConstants.ATTR_LAST_NAME);
				}
				else{
					 strFirstName = eleShipToAdd.getAttribute(VSIConstants.ATTR_FIRST_NAME);
					 strLastname= eleShipToAdd.getAttribute(VSIConstants.ATTR_LAST_NAME);
				}
			String address1 = eleShipToAdd.getAttribute(VSIConstants.ATTR_ADDRESS1);
			String address2 = eleShipToAdd.getAttribute(VSIConstants.ATTR_ADDRESS2);
			String city = eleShipToAdd.getAttribute(VSIConstants.ATTR_CITY);
			String state = eleShipToAdd.getAttribute(VSIConstants.ATTR_STATE);
			String country = eleShipToAdd.getAttribute(VSIConstants.ATTR_COUNTRY);
			String postalCode = eleShipToAdd.getAttribute(VSIConstants.ATTR_ZIPCODE);
			String phone = eleShipToAdd.getAttribute(VSIConstants.ATTR_DAY_PHONE);
			if(YFCCommon.isVoid(strFirstName)){
				strFirstName="Dummy";
			}
			if(YFCCommon.isVoid(strLastname)){
				strLastname="Dummy";
			}
			if(YFCCommon.isVoid(address1)){
				address1="Dummy";
			}
			if(YFCCommon.isVoid(address2)){
				address2="null";
			}
			if(YFCCommon.isVoid(phone)){
				phone="1234567891";
			}
			
			Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
			address.setAddress2(address2);

			address.setCountryCode(country);

		//	address.setProvince(state);
			address.setProvinceCode(state);
			address.setZip(postalCode);
			order.setShippingAddress(address);
		}
		else{

			Element eleShipToAddress = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			if (eleShipToAddress != null&&VSIConstants.LINETYPE_STH.equalsIgnoreCase(strLineType)) {
			
						 strFirstName = eleShipToAddress.getAttribute(VSIConstants.ATTR_FIRST_NAME);
						 strLastname= eleShipToAddress.getAttribute(VSIConstants.ATTR_LAST_NAME);
				
				String address1 = eleShipToAddress.getAttribute(VSIConstants.ATTR_ADDRESS1);
				String address2 = eleShipToAddress.getAttribute(VSIConstants.ATTR_ADDRESS2);
				String city = eleShipToAddress.getAttribute(VSIConstants.ATTR_CITY);
				String state = eleShipToAddress.getAttribute(VSIConstants.ATTR_STATE);
				String country = eleShipToAddress.getAttribute(VSIConstants.ATTR_COUNTRY);
				String postalCode = eleShipToAddress.getAttribute(VSIConstants.ATTR_ZIPCODE);
				String phone = eleShipToAddress.getAttribute(VSIConstants.ATTR_DAY_PHONE);
				if(YFCCommon.isVoid(strFirstName)){
					strFirstName="Dummy";
				}
				if(YFCCommon.isVoid(strLastname)){
					strLastname="Dummy";
				}
				if(YFCCommon.isVoid(address1)){
					address1="Dummy";
				}
				if(YFCCommon.isVoid(address2)){
					address2="null";
				}
				if(YFCCommon.isVoid(phone)){
					phone="1234567891";
				}
				
				Address address = new Address(strFirstName, strLastname, address1, city, phone, null);
				address.setAddress2(address2);

				address.setCountryCode(country);

			//	address.setProvince(state);
				address.setProvinceCode(state);
				address.setZip(postalCode);
				order.setShippingAddress(address);
			}
			
		
			
		}
		
		boolean btrue=false;
		// Customer
		if(YFCCommon.isVoid(strCustomerFirstName)){
			strCustomerFirstName="Dummy";
		}
		if(YFCCommon.isVoid(strCustomerLastName)){
			strCustomerLastName="Dummy";
		}
		Customer customer = new Customer(
				eleOrder.getAttribute(VSIConstants.ATTR_CUSTOMER_EMAIL_ID),
				strCustomerFirstName,
				strCustomerLastName,
				strBillToID,
				dt.parse(strCustomerCreatets),
				btrue,
				null);
		customer.setSocial(null);
		
		//Changes for OMS-2213 -- Start
		/*String strGuestCheckOut = eleOrderExtn.getAttribute(VSIConstants.ATTR_GUEST_CHECKOUT);
		if(!YFCCommon.isVoid(strGuestCheckOut) && (VSIConstants.FLAG_Y.equals(strGuestCheckOut) || (VSIConstants.TRUE.equals(strGuestCheckOut))) && VSIConstants.ATTR_ORDER_TYPE_VALUE.equals(strEntryType)){
			customer.setAccountType(VSIConstants.ATTR_CHECKOUT_TYPE_GUEST);
		} else{
			customer.setAccountType(VSIConstants.ATTR_CHECKOUT_TYPE_REGISTERED);
		}
		//Changes for OMS-2213 -- End */
		
		//OMS-2375
		String strGuestCheckOut = eleOrderExtn.getAttribute(VSIConstants.ATTR_CUSTOMER_TIER);
		if(!YFCCommon.isVoid(strGuestCheckOut) && VSIConstants.ATTR_ORDER_TYPE_VALUE.equals(strEntryType)){
			customer.setAccountType(strGuestCheckOut);
		} else{
			customer.setAccountType(VSIConstants.ATTR_CHECKOUT_TYPE_REGISTERED);
		}
			//OMS-2375 END
		
		order.setCustomer(customer);
		order.setName(strCustomerFirstName);
		//client details
		if((!YFCObject.isVoid(strEntryType)&&!(VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType))&&(!VSIConstants.FLAG_Y.equalsIgnoreCase(strExtnSubscriptionOrder))){
		ClientDetails clientDetails = new ClientDetails();
		clientDetails.setAcceptLanguage(strAcceptUser);
		clientDetails.setUserAgent(strUserAgent);
		order.setClientDetails(clientDetails);
		}
		
		return order;

		
		//response
		
			}
		

	private static ChargeFreePaymentDetails createChargeFreePaymentDetailsObj (List<ChargeFreePaymentDetails> cfp){
		ChargeFreePaymentDetails cfpd = null;
		List<String> tempList = new ArrayList<String>();
		Double amount =0.0 ;
		for(int i=0 ; i<cfp.size() ;i++) {
			if(!tempList.contains(cfp.get(i).getGateway())) {
				tempList.add(cfp.get(i).getGateway());
			}
			amount=amount+cfp.get(i).getAmount();
		}
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<tempList.size();i++) {
			sb.append(tempList.get(i));
			if(i<tempList.size()-1)
				sb.append(" and ");
		}

		cfpd= new ChargeFreePaymentDetails(sb.toString(), amount);
		return cfpd;
	}



	private String constructKountRequest(Document getOrderListOp,YFSEnvironment env) {

		String extnTaxprodCode;
		String paymentKey = "";
		String paymentType = "";
		String orderTotal = "";
		String custName = "";
		String address1 = "";
		String city = "";
		String state = "";
		String country = "";
		String postalCode = "";
		String phone = "";
		String extnSessionId="";
		String strIP="";

		Element orderEle = (Element) getOrderListOp.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);
		Element orderLineElement = (Element) getOrderListOp.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);

		String orderNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strEmailID = orderEle.getAttribute("CustomerEMailID");
					//changes for OMS-1059, instead of taking from header level, we are taking from carrier service code of a line

		//String strSCAC = orderEle.getAttribute("SCAC");
		String strSCAC=null;
		String entryType = orderEle.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
		NodeList orderLinesNL = orderEle
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int noOfLines = orderLinesNL.getLength();
		int iShip = 0;
    	int iPick = 0;
		String nvpStr = "MERC=" + YFSSystem.getProperty("KOUNT_MERCHANT_ID");
    	//String nvpStr = "MERC=" + "115800";
		nvpStr += "&MODE=" + "Q";
		nvpStr += "&VERS=" + "0630";
		nvpStr += "&AUTH=" + "A";
		nvpStr += "&CURR=" + "USD";
		//for jira OMS-634
		if(!YFCObject.isVoid(strEmailID)){
		nvpStr += "&EMAL=" + strEmailID;
		}
		else{
		nvpStr += "&EMAL=" + "noemail@kount.com";
		}
			//for jira OMS-634
		 //nvpStr += "&EMAL=" + "JOHNDOEAPPROVE@ACME.COM";
		//nvpStr += "&EMAL=" + "JOHNDOEDECLINE@ACME.COM";
		//nvpStr += "&EMAL=" + "JOHNDOEREVIEW@ACME.COM";
		//OMS-978:Start
		//nvpStr += "&IPAD=" + "204.156.7.6";
		strIP=getIPAddressForEntryType(env,entryType);
		nvpStr += "&IPAD=" + strIP;		
		//OMS-978:End
		nvpStr += "&MACK=" + "Y";
		String sLineType = null;
		String sShipNode = null;
		for (int i = 0; i < noOfLines; i++) {

			extnTaxprodCode = "";
			Element orderLineEle = (Element) orderLinesNL.item(i);
			
			sLineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			//changes for OMS-1059
			strSCAC = orderLineEle.getAttribute("CarrierServiceCode");
			sShipNode = orderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			
			if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
				
				iShip++;
				
			}else if ((sLineType.equalsIgnoreCase("PICK_IN_STORE"))){
				
				iPick++;
			
			}
			
			Element itemEle = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_ITEM).item(0);
			//OMS-968 : Start
			//Element itemExtnEle = (Element) itemEle.getElementsByTagName(
			//		VSIConstants.ELE_EXTN).item(0);
			Element ItemDetailsEle = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_ITEM_DETAILS).item(0);
			Element itemExtnEle = (Element) ItemDetailsEle.getElementsByTagName(
					VSIConstants.ELE_EXTN).item(0);
			//OMS-968 : End
			Element linePriceEle = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_LINE_PRICE).item(0);
			

			nvpStr += "&PROD_DESC[" + i + "]="
					+ itemEle.getAttribute(VSIConstants.ATTR_ITEM_DESC);
			nvpStr += "&PROD_ITEM[" + i + "]="
					+ itemExtnEle.getAttribute(VSIConstants.EXTN_ACT_SKU_ID);
			
			String strLineTotal=linePriceEle.getAttribute(VSIConstants.ATTR_LINE_TOTAL);
			String strQty=orderLineEle.getAttribute(VSIConstants.ATTR_ORD_QTY);
			DecimalFormat df = new DecimalFormat("#.##");

			double dQty=Double.parseDouble(strQty);
			String strKountLinetotal=null;
			if(dQty==0.00){
				strKountLinetotal="0.00";
			}
			else{
			double dLineTotal=Double.parseDouble(strLineTotal);
			double dUnitPrice=dLineTotal/dQty;
			String strUnitPrice=df.format(dUnitPrice);
			double formattedUnitPrice=Double.parseDouble(strUnitPrice);
	        double dKountLineTot=formattedUnitPrice*100;
	        strKountLinetotal=Double.toString(dKountLineTot);
		}
			nvpStr += "&PROD_PRICE[" + i + "]="
					+ strKountLinetotal;
			nvpStr += "&PROD_QUANT[" + i + "]="
					+ orderLineEle.getAttribute(VSIConstants.ATTR_ORD_QTY);

			if (itemExtnEle != null) {
				extnTaxprodCode = itemExtnEle
						.getAttribute("ExtnTaxProductCode");
				if(extnTaxprodCode == null || extnTaxprodCode.trim().equalsIgnoreCase("")){
					extnTaxprodCode = "1";
				}
			}

			nvpStr += "&PROD_TYPE[" + i + "]=" + extnTaxprodCode;
		}

		NodeList paymtMthdNL = orderEle
				.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
		int pymthdCount = paymtMthdNL.getLength();
		for (int n = 0; n < pymthdCount; n++) {
			Element paymtMthdEle = (Element) paymtMthdNL.item(n);
			if (paymtMthdEle != null) {

				paymentType = paymtMthdEle
						.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
				if (paymentType != null
						&& paymentType.equalsIgnoreCase("CREDIT_CARD")) {
					paymentType = "CARD";
					//paymentType = "PYPL";
					paymentKey = paymtMthdEle.getAttribute("PaymentReference2");
					break;
				} else if (paymentType != null
						&& paymentType.equalsIgnoreCase("PAYPAL")) {
					paymentType = "PYPL";
					paymentKey = paymtMthdEle.getAttribute("PaymentReference1");
					break;
				} else if (paymentType != null
						&& paymentType.equalsIgnoreCase("GIFT_CARD")) {
					paymentType = "GIFT";
					paymentKey = paymtMthdEle.getAttribute("SvcNo");
					if (n++ == pymthdCount) {
						break;
					}
				} else {
					paymentType = "NONE";
					paymentKey = "";
				}
			}
		}

		nvpStr += "&PTOK=" + paymentKey;
		nvpStr += "&PTYP=" + paymentType;
		//OMS-978:Start
		Element eleOrderExtn=SCXmlUtil.getChildElement(orderEle, VSIConstants.ELE_EXTN);
		if(eleOrderExtn!=null){
			extnSessionId=eleOrderExtn.getAttribute("ExtnSessionID");
		}
		if(!YFCCommon.isStringVoid(extnSessionId)){
			nvpStr += "&SESS=" + extnSessionId;
		}
		else{
			nvpStr += "&SESS=" + orderNo;
		}
			
		
		//OMS-978 : End
		// Updated for JIRA BOP-644
		String strExtnMobileOrder = "";
		if(entryType.equalsIgnoreCase("WEB")){
			if(sLineType.equalsIgnoreCase("PICK_IN_STORE") || sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
				nvpStr += "&SITE=" + "WBOPUS";

			} else{
				/*String strContinuityOrder=orderNo.substring(0,4);
				if(strContinuityOrder.equalsIgnoreCase("WO45") ||strContinuityOrder.equalsIgnoreCase("WO40")){
					nvpStr += "&SITE=" + "WCONT";
				}*/
				String strExtnSubscriptionOrder = eleOrderExtn.getAttribute("ExtnSubscriptionOrder");
				String strExtnOriginalADPOrder = eleOrderExtn.getAttribute("ExtnOriginalADPOrder");
				strExtnMobileOrder = eleOrderExtn.getAttribute("ExtnMobileOrder");
				if(VSIConstants.FLAG_Y.equals(strExtnSubscriptionOrder)){
					nvpStr += "&SITE=" + "ADPR";
				} else if(VSIConstants.FLAG_Y.equals(strExtnOriginalADPOrder)){
					nvpStr += "&SITE=" + "ADPI";
				} else if(VSIConstants.FLAG_Y.equals(strExtnMobileOrder)){
					nvpStr += "&SITE=" + "MOBAPP";
				} else{
					nvpStr += "&SITE=" + "WDTC";
				}
			}
		}
		else if(entryType.equalsIgnoreCase("Call Center")){
			if(sLineType.equalsIgnoreCase("PICK_IN_STORE") || sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
				nvpStr += "&SITE=" + "CBOPUS";

			}
			else{
				nvpStr += "&SITE=" + "CDTC";
		}
		}

		Element ordTotalEle = (Element) orderEle.getElementsByTagName(
				"OverallTotals").item(0);
		if (ordTotalEle != null) {
			orderTotal = ordTotalEle.getAttribute("GrandTotal");
		}
        double dOrderTotal=Double.parseDouble(orderTotal);
        double dKountOrderTot=dOrderTotal*100;
        String strKounttotal=Double.toString(dKountOrderTot);
		nvpStr += "&TOTL=" + strKounttotal;

		Element billToAddEle = (Element) orderEle.getElementsByTagName(
				"PersonInfoBillTo").item(0);
		if (billToAddEle != null) {
			custName = billToAddEle.getAttribute("FirstName") + " "
					+ billToAddEle.getAttribute("LastName");
			address1 = billToAddEle.getAttribute("AddressLine1");
			city = billToAddEle.getAttribute("City");
			state = billToAddEle.getAttribute("State");
			country = billToAddEle.getAttribute("Country");
			postalCode = billToAddEle.getAttribute("ZipCode");
			phone = billToAddEle.getAttribute("EveningPhone");
		}

		nvpStr += "&NAME=" + custName;

		nvpStr += "&B2A1=" + address1;

		nvpStr += "&B2CI=" + city;

		nvpStr += "&B2ST=" + state;

		nvpStr += "&B2CC=" + country;

		nvpStr += "&B2PC=" + postalCode;

		nvpStr += "&B2PN=" + phone;
//Commentng below for jir  oms -711
		//Element shipToAddEle = (Element) orderEle.getElementsByTagName("PersonInfoShipTo").item(0);
		Element shipToAddEle = (Element) orderLineElement.getElementsByTagName("PersonInfoShipTo").item(0);
		if (shipToAddEle != null) {
			custName = shipToAddEle.getAttribute("FirstName") + " "
					+ shipToAddEle.getAttribute("LastName");
			address1 = shipToAddEle.getAttribute("AddressLine1");
			city = shipToAddEle.getAttribute("City");
			state = shipToAddEle.getAttribute("State");
			country = shipToAddEle.getAttribute("Country");
			postalCode = shipToAddEle.getAttribute("ZipCode");
			phone = shipToAddEle.getAttribute("EveningPhone");
		}
		
		if(!YFCCommon.isStringVoid(strSCAC) && strSCAC.equalsIgnoreCase("STANDARD")){
			strSCAC="ST";
		}
		else if(!YFCCommon.isStringVoid(strSCAC) && strSCAC.equalsIgnoreCase("NEXTDAY")){
			strSCAC="ND";
		}
		else if(!YFCCommon.isStringVoid(strSCAC) && strSCAC.equalsIgnoreCase("TWODAY")){
			strSCAC="2D";
		}
		
		
		
		nvpStr += "&S2A1=" + address1;
		nvpStr += "&S2CI=" + city;
		nvpStr += "&S2NM=" + custName;
		nvpStr += "&S2ST=" + state;
		nvpStr += "&S2CC=" + country;
		nvpStr += "&S2PC=" + postalCode;

		nvpStr += "&SHTP=" + strSCAC;
		nvpStr += "&ORDR=" + orderNo;
		//OMS-1212 :Start
		boolean bUseDefaultValues=true;
		String strAvsResp=SCXmlUtil.getXpathAttribute(orderEle, "Extn/TNSAuthRecordsList/TNSAuthRecords/@AvsResp");
		log.debug("AvsResp : "+ strAvsResp);
		if(!YFCCommon.isStringVoid(strAvsResp)){
			//Sample value for strAvsResp AvsResp-I8, fetch the value after -
			String[] arrAvsResp= strAvsResp.split("-");
			if(arrAvsResp.length==2){
				String strPaymentTechCode=arrAvsResp[1];
				Element eleCommonCodeOutput=getAVSCodes(env,strPaymentTechCode);
				if(eleCommonCodeOutput!=null){
					String strAVST=eleCommonCodeOutput.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					String strAVSZ=eleCommonCodeOutput.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
					nvpStr += "&AVST=" + strAVST;
					nvpStr += "&AVSZ=" + strAVSZ;
					bUseDefaultValues=false;		
				}
			}
		}
		if(bUseDefaultValues){
			nvpStr += "&AVST=" + "X";
			nvpStr += "&AVSZ=" + "X";
		}
		//OMS-1212: End
		// nvpStr += "&SEC=" + "V!t@m!n$";
		// nvpStr += "&LOC=" + "/Sterling/kount/kount.p12";
		nvpStr += "&UDF[" + entryType+"]";
		 if(iPick > 0 && iShip > 0){
			 sLineType = "BOPUSC";
		 }
		nvpStr += "&UDF[" + sLineType+"]";
		if(sShipNode.length() < 4){
			while(sShipNode.length() < 4){
				sShipNode = "0"+sShipNode;
			}
			
			
		}else if(sShipNode.length() > 4){
			sShipNode = sShipNode.substring(sShipNode.length() - 4, sShipNode.length());
			
			
		} 
		nvpStr += "&UDF[" + sShipNode+"]";
		nvpStr += "&UDF[" + orderNo+"]";
		String strExtnMobileType = eleOrderExtn.getAttribute("ExtnMobileType");
		if(VSIConstants.FLAG_Y.equals(strExtnMobileOrder) && !YFCObject.isVoid(strExtnMobileType)){
			nvpStr += "&UDF[" + strExtnMobileType+"]";
		}
		nvpStr += "&URL=" + YFSSystem.getProperty("KOUNT_URL");
		//nvpStr += "&URL=" + "https://risk.test.kount.net";

		return nvpStr;

	}
	
	


	
	//OMS-978: Start
	
	/*
	 * To fetch the IP address from the CommonCode
	 * 
	 */
	private String getIPAddressForEntryType(YFSEnvironment env,String strEntryType){
		String strIP=VSIConstants.VAL_DEFAULT_IPAD;
		try {
			if(!YFCCommon.isStringVoid(strEntryType)){
				Document docInputgetCommonCodeList=XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_IPAD_IP");
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEntryType);
				
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
				        .getElementsByTagName(VSIConstants.ELE_COMMON_CODE);

				if(nlCommonCode!=null && nlCommonCode.getLength()>0){
					strIP=((Element) nlCommonCode.item(0))
					          .getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e);
		}
		
		log.debug("EntryType : "+strEntryType+ " IP: "+strIP);
		return strIP;
	}
	
	//OMS-978 :End
	
	
	
	//OMS-1212: Start

	/*
	 * To fetch the AVS code from the CommonCode
	 * 
	 */
	private Element getAVSCodes(YFSEnvironment env,String strPaymentTechCode){
		Element eleCommonCodeOutput=null;
		try {
			if(!YFCCommon.isStringVoid(strPaymentTechCode)){
				Document docInputgetCommonCodeList=XMLUtil.createDocument(VSIConstants.ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_AVS_CODE");
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, strPaymentTechCode);

				log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInputgetCommonCodeList));
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
						.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);

				if(nlCommonCode!=null && nlCommonCode.getLength()>0){
					eleCommonCodeOutput=((Element) nlCommonCode.item(0));
					log.debug("output is  : "+XMLUtil.getElementString(eleCommonCodeOutput));
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error(e);
		}

		return eleCommonCodeOutput;
	}

	//OMS-1212 :End


		
	}

	

