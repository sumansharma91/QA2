package com.vsi.oms.api;


import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.api.VSIKountRequestForSTH;
import com.google.gson.Gson;
import com.riskified.Environment;
import com.riskified.RiskifiedClient;
import com.riskified.models.Order;
import com.riskified.models.Response;
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

public class VSIKountRequestForBOPUS {
	private YFCLogCategory log = YFCLogCategory.instance(VSIKountRequestForBOPUS.class);
	YIFApi api;
	public void BOPUSkountRequest(YFSEnvironment env, Document inXML){
		String strOrderHeaderKey = null;
		String strOrderNo = null;
		if(log.isDebugEnabled()){
			log.debug("BOPUSkountRequest input is : "+XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			 strOrderHeaderKey = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		}
		String strRiskifiedFlag=null;
		if(strOrderHeaderKey != null && !strOrderHeaderKey.equals("")){
			try {
				Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
				Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrderListSOForHold.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				
				if(log.isDebugEnabled()){
					log.debug("*** NOW PRINTING getOrderListOp "+XMLUtil.getXMLString(getOrderListOp));
				}
				if(getOrderListOp != null){
					Element eleOrderOutput = (Element) getOrderListOp.getDocumentElement().getElementsByTagName("Order").item(0);
					 strOrderNo=eleOrderOutput.getAttribute(VSIConstants.ATTR_ORDER_NO);
					 ArrayList<Element> listRiskifiedFlag;
						listRiskifiedFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_RISKIFIED, VSIConstants.ATTR_DEFAULT);
						if(!listRiskifiedFlag.isEmpty()){
						Element eleCommonCode=listRiskifiedFlag.get(0);
							 strRiskifiedFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
							if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_N.equalsIgnoreCase(strRiskifiedFlag)){
											// 1) Form the NVP request String
											String nvpRequest = constructKountRequest(getOrderListOp,env);
											if(log.isDebugEnabled()){
												log.debug("Printing nvpRequest: "
														+ nvpRequest);
											}
											Document fraudRequest = XMLUtil.createDocument("FraudRequest");
											fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
											String chargeType="Fraud Request";
											storeFraudRequestandResponse(env,strOrderHeaderKey,nvpRequest,chargeType);
											
											api = YIFClientFactory.getInstance().getApi();
											Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
											Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
											String responseStr=kountResponse.getTextContent();
											if(log.isDebugEnabled()){
												log.debug("Fraud Response: "+ responseStr);
											}
											chargeType="Fraud Response";
											storeFraudRequestandResponse(env,strOrderHeaderKey,responseStr,chargeType);
											
											// 3) Process the response and act
											// accordingly within OMS
											processKountResponse(
													env, responseStr,
													 strOrderHeaderKey, strOrderNo);

										}
							else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.ATTR_BOTH.equalsIgnoreCase(strRiskifiedFlag)){
								// 1) Form the NVP request String
								String nvpRequest = constructKountRequest(getOrderListOp,env);
								if(log.isDebugEnabled()){
									log.debug("Printing nvpRequest: "
											+ nvpRequest);
								}
								Document fraudRequest = XMLUtil.createDocument("FraudRequest");
								fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
								String chargeType="Fraud Request";
								storeFraudRequestandResponse(env,strOrderHeaderKey,nvpRequest,chargeType);
								
								api = YIFClientFactory.getInstance().getApi();
								Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
								Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
								String responseStr=kountResponse.getTextContent();
								if(log.isDebugEnabled()){
									log.debug("Fraud Response: "+ responseStr);
								}
								chargeType="Fraud Response";
								storeFraudRequestandResponse(env,strOrderHeaderKey,responseStr,chargeType);
								
								// 3) Process the response and act
								// accordingly within OMS
								processKountResponse(
										env, responseStr,
										 strOrderHeaderKey, strOrderNo);
								
								//construct Riskified request
								VSIKountRequestForSTH KountCall= new VSIKountRequestForSTH();
								
								Order order=KountCall.constructRiskifiedRequest(getOrderListOp, env);
								
					            								
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
								String strStatus=res.getOrder().getStatus();
								String chargeType2="Fraud Response";
								if(!YFCObject.isVoid(strResponse)){
									// 3) Process the response and act
									// accordingly within OMS
								storeFraudRequestandResponse(env,strOrderHeaderKey,strResponse,chargeType2);
								}
								
								
								

								
							}
							else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
								
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
									
									//OMS-2046 START
									//construct Riskified request
									VSIKountRequestForSTH KountCall= new VSIKountRequestForSTH();
									
									Order order=KountCall.constructRiskifiedRequest(getOrderListOp, env);
									
						            								
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
										applyFrauldHold(env,strOrderHeaderKey);
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
		
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YFSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YIFClientCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				
						if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
							// TODO Auto-generated catch block
							try {
								applyFrauldHold(env,strOrderHeaderKey);
							} catch (YFSException | RemoteException
									| ParserConfigurationException
									| YIFClientCreationException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						} 

				
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		if(log.isDebugEnabled()){
			log.info("BOPUSkountRequest End ");
		}

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
			String strResponse, String ohk, String orderNo)
			throws YFSException, RemoteException, ParserConfigurationException,
			YIFClientCreationException {
		// TODO Auto-generated method stub
		log.info("Printing RiskifiedRespnse: " + strResponse);

		if (strResponse != null) {

			if (strResponse.contains(VSIConstants.ATTR_STATUS_APPROVE)) {
				Document orderInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element sOrderElement = orderInput.getDocumentElement();
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
						ohk);
                api = YIFClientFactory.getInstance().getApi(); 
				api.executeFlow(env, "VSIScheduleBOPUS_Q", orderInput);
				//OMS-2088
				sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_NO,orderNo);
				sOrderElement.setAttribute(VSIConstants.FRAUD_RESULT,"Approved");
				try {
					VSIUtils.invokeService(env, "VSISendAcceptOrDeclinedToATG", orderInput);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (strResponse.contains(VSIConstants.ATTR_STATUS_DECLINED)) {
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

			} else {
				applyFrauldHold(env,ohk);
				

			}

		}
	}


		//OMS-2046 END



	private void applyFrauldHold(YFSEnvironment env, String ohk) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		// TODO Auto-generated method stub
		Document processedHoldDoc = null;
		Element eleOrder = null;
		Element eleOrderHoldTypes = null;
		Element eleOrderHoldType = null;

		processedHoldDoc = XMLUtil
				.createDocument(VSIConstants.ELE_ORDER);
		eleOrder = processedHoldDoc.getDocumentElement();
		eleOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
		eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		eleOrderHoldTypes = SCXmlUtil.createChild(eleOrder,
				VSIConstants.ELE_ORDER_HOLD_TYPES);
		eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes,
				VSIConstants.ELE_ORDER_HOLD_TYPE);
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
				VSIConstants.HOLD_FRAUD_HOLD);
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_CREATE);
		eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
				"Fraud Verification Hold");

		log.info("Printing processedHoldDoc: "
				+ XMLUtil.getXMLString(processedHoldDoc));
		VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER,
				processedHoldDoc);
		
	}


	private void processKountResponse(YFSEnvironment env,
			String fraudResponse, String ohk, String orderNo)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("Printing fraudRespnse: " + fraudResponse);
		}

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

		
			if(log.isDebugEnabled()){
				log.info("Printing AUTO: " + strAuto);
			}
		if (strAuto != null && strAuto.trim().equalsIgnoreCase("A")) {

			Document orderInput = XMLUtil.createDocument("Order");
			Element sOrderElement = orderInput.getDocumentElement();
			sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
            api = YIFClientFactory.getInstance().getApi();
		    api.executeFlow(env, "VSIScheduleBOPUS_Q", orderInput);
			
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
			if(log.isDebugEnabled()){
				log.debug("changeOrderInput: "
						+ XMLUtil.getXMLString(changeOrderInput));
			}

			VSIUtils.invokeAPI(env, "changeOrder", changeOrderInput);

		}
			
			else if (strAuto != null && strAuto.trim().equalsIgnoreCase("ERR")) {
			
				Document processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				Element orderEle =processedHoldDoc.getDocumentElement();
				orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
				Element eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
				Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
				eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
						"VSI_FRAUD_HOLD");
				eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
				eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
						"Fraud Verification Hold");
				if(log.isDebugEnabled()){
					log.info("Printing processedHoldDoc: "
							+ XMLUtil.getXMLString(processedHoldDoc));
				}
		VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);	
		
				int errLength=fraudResponse.length();
				if(errLength > 100){
					fraudResponse=fraudResponse.substring(0, 99);
				}
				raiseAlert(env,orderNo,ohk,fraudResponse);
				

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("R")) {

			Document processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element orderEle =processedHoldDoc.getDocumentElement();
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_FRD_REVIEW_HOLD");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Fraud Check Under Review");
			if(log.isDebugEnabled()){
				log.info("Printing processedHoldDoc: "
						+ XMLUtil.getXMLString(processedHoldDoc));
			}
	VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);
			raiseAlert(env, orderNo,ohk,"Fraud Check Under Review");
		}
		
		else 
		{
			Document processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element orderEle =processedHoldDoc.getDocumentElement();
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_FRAUD_HOLD");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Fraud Verification Hold");
			if(log.isDebugEnabled()){
				log.info("Printing processedHoldDoc: "
						+ XMLUtil.getXMLString(processedHoldDoc));
			}
	       VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);	
	
		
		}
		}
		else{
			Document processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
			Element orderEle =processedHoldDoc.getDocumentElement();
			orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
			Element eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
			Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
					"VSI_FRAUD_HOLD");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, "1100");
			eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,
					"Fraud Verification Hold");
			if(log.isDebugEnabled()){
				log.info("Printing processedHoldDoc: "
						+ XMLUtil.getXMLString(processedHoldDoc));
			}
	       VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, processedHoldDoc);	
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
			double dLineTotal=Double.parseDouble(strLineTotal);
			double dUnitPrice=dLineTotal/dQty;
			String strUnitPrice=df.format(dUnitPrice);
			double formattedUnitPrice=Double.parseDouble(strUnitPrice);
	        double dKountLineTot=formattedUnitPrice*100;
	        String strKountLinetotal=Double.toString(dKountLineTot);
			
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
		if(log.isDebugEnabled()){
			log.debug("AvsResp : "+ strAvsResp);
		}
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
		
		if(log.isDebugEnabled()){
			log.debug("EntryType : "+strEntryType+ " IP: "+strIP);
		}
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

				if(log.isDebugEnabled()){
					log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInputgetCommonCodeList));
				}
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
						.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);

				if(nlCommonCode!=null && nlCommonCode.getLength()>0){
					eleCommonCodeOutput=((Element) nlCommonCode.item(0));
					if(log.isDebugEnabled()){
						log.debug("output is  : "+XMLUtil.getElementString(eleCommonCodeOutput));
					}
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

