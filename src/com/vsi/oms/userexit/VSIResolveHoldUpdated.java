package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;


import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;
import com.riskified.Environment;
import com.riskified.RiskifiedClient;
import com.riskified.models.Order;
import com.riskified.models.Response;
import com.riskified.validations.Validation;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.api.VSIKountRequestForSTH;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIKountRIS;
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
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSProcessOrderHoldTypeUE;

public class VSIResolveHoldUpdated implements YFSProcessOrderHoldTypeUE,VSIConstants{

	private YFCLogCategory log = YFCLogCategory.instance(VSIResolveHoldUpdated.class);
	YIFApi api;
	public Document processOrderHoldType(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		
		Document processedHoldDoc = null;
		////System.out.println(" Inside class VSIResolveHold " + XMLUtil.getXMLString(inXML));
		String ohk = null;
		String orderNo = null;
		String txnID = null;
		String OrderDate = null;
		if(inXML != null){
			ohk = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			orderNo = inXML.getDocumentElement().getAttribute(
					VSIConstants.ATTR_ORDER_NO);
			txnID = inXML.getDocumentElement().getAttribute(
					"TransactionId");
		}
		if(ohk != null && !ohk.equals("")){
			try {
				Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/VSIGetOrderListSOForHold.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				
				if(log.isDebugEnabled()){
		    		log.debug("*** NOW PRINTING getOrderListOp "+XMLUtil.getXMLString(getOrderListOp));
				}
				if(getOrderListOp != null){
					////System.out.println("Printing getOrderListOp is not null");
					Element ipOrderEle = (Element) getOrderListOp.getDocumentElement().getElementsByTagName("Order").item(0);
					String paymentStatus = ipOrderEle.getAttribute(VSIConstants.ATTR_PAYMENT_STATUS);
					OrderDate = ipOrderEle.getAttribute(VSIConstants.ATTR_ORDER_DATE);
					NodeList orderHoldTypeNL = ipOrderEle.getElementsByTagName("OrderHoldType");
					String holdType = null;
					String status = null;
					String holdCreatedTS = null;
					String olk = null;
					
					////System.out.println("Printing orderHoldTypeNL.getLength() : "+orderHoldTypeNL.getLength());
					if(orderHoldTypeNL.getLength() > 0){
						
						Integer remorsPrd=0; 
						Integer iCCOrdHldPrd=0; 
						Integer iOrderDate=0;
						String entCode = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
						String entryType = ipOrderEle.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
						
						processedHoldDoc =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						Element orderEle =processedHoldDoc.getDocumentElement();
						
						
						//if(entryType != null && entryType.equalsIgnoreCase("Call Center")){
							
							String strXPATHOrderHoldType = VSIGeneralUtils
									.formXPATHWithOneCondition(
											"/OrderList/Order/OrderHoldTypes/OrderHoldType",
											"HoldType", "VSI_ORD_VERIFY_HOLD");
							NodeList nlOrderHoldTypeList = XMLUtil.getNodeListByXpath(
									getOrderListOp, strXPATHOrderHoldType);
							if(nlOrderHoldTypeList.getLength() > 0){
								////System.out.println("Printing nlOrderHoldTypeList.getLength() : "+nlOrderHoldTypeList.getLength());
								for(int k=0; k < nlOrderHoldTypeList.getLength(); k++){
									Element ordHldTyp = (Element) nlOrderHoldTypeList.item(k);
									if(ordHldTyp != null){
										String hldStatus = ordHldTyp.getAttribute(VSIConstants.ATTR_STATUS);
										String hldTyp = ordHldTyp.getAttribute(ATTR_HOLD_TYPE);
										if(txnID != null && txnID.equalsIgnoreCase("VSIRslvTOBHld.0001.ex") && hldStatus != null && hldStatus.equalsIgnoreCase("1100") && hldTyp != null && hldTyp.equalsIgnoreCase("VSI_ORD_VERIFY_HOLD")){
											
											String strCCOrdHldPrd = getRemorsePeriod(env, entCode, VSIConstants.VSI_CC_HLD_PRD);
											////System.out.println("strCCOrdHldPrd = " +strCCOrdHldPrd);
											if(strCCOrdHldPrd != null && !strCCOrdHldPrd.equals("")){
												iCCOrdHldPrd=Integer.parseInt(strCCOrdHldPrd);
											}
											holdCreatedTS = ordHldTyp.getAttribute("LastHoldTypeDate");
											boolean isRemorseExprd = checkRemorseExpry(holdCreatedTS,iCCOrdHldPrd);
											////System.out.println("isRemorseExprd = " +isRemorseExprd);
											if(isRemorseExprd){
												Element processedHTEle = processedHoldDoc.createElement("ProcessedHoldTypes");
												orderEle.appendChild(processedHTEle);
												Element procsdOrderHTEle = processedHoldDoc.createElement("OrderHoldType");
												procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_ORD_VERIFY_HOLD");
												procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
												procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT, "Hold Duration Expired");
												processedHTEle.appendChild(procsdOrderHTEle);
											}
											
										}
										
										
										
									}
									
								}
								
								
								
								//log.info(" *** Printing Order XML : \n"+XMLUtil.getElementXMLString(orderEle));	
							}
							
							strXPATHOrderHoldType = VSIGeneralUtils
									.formXPATHWithOneCondition(
											"/OrderList/Order/OrderHoldTypes/OrderHoldType",
											"HoldType", "VSI_FRAUD_HOLD");
							nlOrderHoldTypeList = XMLUtil
									.getNodeListByXpath(getOrderListOp,
											strXPATHOrderHoldType);
							if (nlOrderHoldTypeList.getLength() > 0) {
								// //System.out.println("Printing nlOrderHoldTypeList.getLength() : "+nlOrderHoldTypeList.getLength());
								for (int k = 0; k < nlOrderHoldTypeList.getLength(); k++) {
									Element ordHldTyp = (Element) nlOrderHoldTypeList
											.item(k);
									if (ordHldTyp != null) {
										String hldStatus = ordHldTyp
												.getAttribute(VSIConstants.ATTR_STATUS);
										String hldTyp = ordHldTyp
												.getAttribute(ATTR_HOLD_TYPE);
										if (txnID != null && txnID.equalsIgnoreCase("VSIRslvFrdHld.0001.ex") && hldStatus != null
												&& hldStatus
														.equalsIgnoreCase("1100")
												&& hldTyp != null
												&& hldTyp
														.equalsIgnoreCase("VSI_FRAUD_HOLD")) {

											if(log.isDebugEnabled()){
									    		log.debug("Printing getOrderListOp: "
															+ XMLUtil
																	.getXMLString(getOrderListOp));
											}
											//OMS-2046 START
											ArrayList<Element> listRiskifiedFlag;
											listRiskifiedFlag = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_RISKIFIED, VSIConstants.ATTR_DEFAULT);
											if(!listRiskifiedFlag.isEmpty()){
												Element eleCommonCode=listRiskifiedFlag.get(0);
												String strRiskifiedFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
												if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_N.equalsIgnoreCase(strRiskifiedFlag)){
											// 1) Form thr NVP request String
											String nvpRequest = constructKountRequest(getOrderListOp,env);
											if(log.isDebugEnabled()){
									    		log.debug("Printing nvpRequest: "
													+ nvpRequest);
											}
											Document fraudRequest = XMLUtil.createDocument("FraudRequest");
											fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
											String chargeType="Fraud Request";
											storeFraudRequestandResponse(env,ohk,nvpRequest,chargeType);
											
											api = YIFClientFactory.getInstance().getApi();
											Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
											Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
											String responseStr=kountResponse.getTextContent();
											if(log.isDebugEnabled()){
									    		log.debug("Fraud Response: "+ responseStr);
											}
											chargeType="Fraud Response";
											storeFraudRequestandResponse(env,ohk,responseStr,chargeType);

											
									

											//HashMap nvpResponse=responseStr;
											// 2) Invoke the Kount method to make a
											// call to Kount system
									
											//commented for Jira OMS-651. Removing the call to be made from OMs to Kount.
											//HashMap nvpResponse = callKountRIS(env,nvpRequest);
											//System.out.println("Printing nvpResponse: "+ nvpResponse);
															
											// 3) Process the response and act
											// accordingly within OMS
											processedHoldDoc = processKountResponse(
													env, responseStr,
													processedHoldDoc, ohk, orderNo);

										}
												else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.ATTR_BOTH.equalsIgnoreCase(strRiskifiedFlag)){
													// 1) Form thr NVP request String
													String nvpRequest = constructKountRequest(getOrderListOp,env);
													if(log.isDebugEnabled()){
											    		log.debug("Printing nvpRequest: "
															+ nvpRequest);
													}
													Document fraudRequest = XMLUtil.createDocument("FraudRequest");
													fraudRequest.getDocumentElement().setAttribute("NVPRequest", nvpRequest);
													String chargeType="Fraud Request";
													storeFraudRequestandResponse(env,ohk,nvpRequest,chargeType);
													
													api = YIFClientFactory.getInstance().getApi();
													Document outDoc = api.executeFlow(env, "VSIKountRequest", fraudRequest);
													Element kountResponse = (Element) outDoc.getElementsByTagName("kountResponse").item(0);
													String responseStr=kountResponse.getTextContent();
													if(log.isDebugEnabled()){
											    		log.debug("Fraud Response: "+ responseStr);
													}
													chargeType="Fraud Response";
													storeFraudRequestandResponse(env,ohk,responseStr,chargeType);

													
											

													//HashMap nvpResponse=responseStr;
													// 2) Invoke the Kount method to make a
													// call to Kount system
											
													//commented for Jira OMS-651. Removing the call to be made from OMs to Kount.
													//HashMap nvpResponse = callKountRIS(env,nvpRequest);
													//System.out.println("Printing nvpResponse: "+ nvpResponse);
																	
													// 3) Process the response and act
													// accordingly within OMS
													processedHoldDoc = processKountResponse(
															env, responseStr,
															processedHoldDoc, ohk, orderNo);
                                                VSIKountRequestForSTH KountCall= new VSIKountRequestForSTH();
													
													Order order=KountCall.constructRiskifiedRequest(getOrderListOp, env);
													
										            								
													String chargeType1="Fraud Request";
													Gson gson = new Gson();
													String strOrder = com.riskified.JSONFormater.toJson(order);

													log.debug("Riskified Request: "
															+ strOrder);
													storeFraudRequestandResponse(env,ohk,strOrder,chargeType1);
													
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
													storeFraudRequestandResponse(env,ohk,strResponse,chargeType2);
													}
													
												}
												
												else if(!YFCCommon.isStringVoid(strRiskifiedFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strRiskifiedFlag)){
													
													//OMS-2419 -- Start
													int iRiskifiedCount = 0;
													int iRiskifiedCountCC = 0;
													boolean isRiskifiedCallReqd = false;
													Element eleOrderExtn = SCXmlUtil.getChildElement(ipOrderEle, VSIConstants.ELE_EXTN);
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
														storeFraudRequestandResponse(env,ohk,strOrder,chargeType);
														
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
															updateRiskifiedCount(env,ohk,iRiskifiedCount);
															//OMS-2419 -- End
															
															// 3) Process the response and act
															// accordingly within OMS
															
															storeFraudRequestandResponse(env,ohk,strResponse,chargeType);
														}
														
														if(!YFCObject.isVoid(strStatus)){
															processRiskifiedResponse(
																	env, strStatus,
																	ohk, strOrder);
														}
														//OMS-2046 END
													//OMS-2419 -- Start
													}
													else{
														log.debug("Number of Riskified Requests has exceeded the allowed limit, hence will not be triggerred");
														
														//OMS-2440 -- Start
														Document doccreateExceptionInput = SCXmlUtil.createDocument(VSIConstants.ELE_INBOX);
														Element eleInbox = doccreateExceptionInput.getDocumentElement();
														eleInbox.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
														eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
														eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_RISKIFY_RECURRING_ORDERS");
														eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "Riskify Recurring Order");
														eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Riskify Recurring Order");
														eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE,VSIConstants.FLAG_Y);
														eleInbox.setAttribute(VSIConstants.ATTR_CONS_WINDOW,VSIConstants.VAL_FOREVER);														
														
														Element eleConsolidationTemplate = doccreateExceptionInput.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
														eleInbox.appendChild(eleConsolidationTemplate);
														Element eleInboxCpy = (Element) eleInbox.cloneNode(true);
														eleConsolidationTemplate.appendChild(eleInboxCpy);
														
														log.debug("Create Exception API Input prepared is: "+doccreateExceptionInput.toString());
														
														VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, doccreateExceptionInput);
														
														log.debug("VSI_RISKIFY_RECURRING_ORDERS Alert was successfully raised on the order");
														//OMS-2440 -- End
													}
													//OMS-2419 -- End
												}
											}
										}

									}

								}

								// log.info(" *** Printing Order XML : \n"+XMLUtil.getElementXMLString(orderEle));
							}
							
							
							//Start - Resolve Order Invoice Hold
							
							//If All lines are in status >= 3700 
							String sMinOrderStatus = ipOrderEle.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
							
							Element eleOrderLines = SCXmlUtil.getChildElement(ipOrderEle, VSIConstants.ELE_ORDER_LINES);
							List<Element> eleOrderLineList = XMLUtil.getElementsByTagName(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
							
							if(Integer.parseInt(sMinOrderStatus) >= 3700)
							{
							
							strXPATHOrderHoldType = VSIGeneralUtils
									.formXPATHWithOneCondition(
											"/OrderList/Order/OrderHoldTypes/OrderHoldType",
											"HoldType", "VSI_WH_INVCREATE_HLD");
							nlOrderHoldTypeList = XMLUtil
									.getNodeListByXpath(getOrderListOp,
											strXPATHOrderHoldType);
							if (nlOrderHoldTypeList.getLength() > 0) {
								// //System.out.println("Printing nlOrderHoldTypeList.getLength() : "+nlOrderHoldTypeList.getLength());
								for (int k = 0; k < nlOrderHoldTypeList.getLength(); k++) {
									Element ordHldTyp = (Element) nlOrderHoldTypeList
											.item(k);
									if (ordHldTyp != null) {
										String hldStatus = ordHldTyp
												.getAttribute(VSIConstants.ATTR_STATUS);
										String hldTyp = ordHldTyp
												.getAttribute(ATTR_HOLD_TYPE);
										if (txnID != null && txnID.equalsIgnoreCase("VSIRslvInvHold.0001.ex") && hldStatus != null
												&& hldStatus
														.equalsIgnoreCase("1100")
												&& hldTyp != null
												&& hldTyp
														.equalsIgnoreCase("VSI_WH_INVCREATE_HLD")) {

											if(log.isDebugEnabled()){
									    		log.debug("Printing getOrderListOp: "
															+ XMLUtil
																	.getXMLString(getOrderListOp));
											}
											
											// Create input for CreateOrderInvoice
											
											
											
											Document docCreateOrderInvoice = createOrderInvoiceIn(ohk,eleOrderLineList);
											
											api = YIFClientFactory.getInstance().getApi();
											Document createOrderInvoiceOutDoc = api.invoke(env, "createOrderInvoice",
													docCreateOrderInvoice);
											
											if (log.isVerboseEnabled()) {
												log.verbose("createOrderInvoice output : \n"
														+ XMLUtil.getXMLString(createOrderInvoiceOutDoc)); 
										
											}
											
											//Process the Hold
											
											Element processedHTEle = processedHoldDoc.createElement("ProcessedHoldTypes");
											orderEle.appendChild(processedHTEle);
											Element procsdOrderHTEle = processedHoldDoc.createElement("OrderHoldType");
											procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_WH_INVCREATE_HLD");
											procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
											procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT, "Order Invoice Hold Resolved");
											processedHTEle.appendChild(procsdOrderHTEle);

										}

									}

								}

								// log.info(" *** Printing Order XML : \n"+XMLUtil.getElementXMLString(orderEle));
							}
							}
							
							//End - Resolve Order Invoice Hold
							
							
							
							
							//OMS-1082: START	
						if(txnID != null && txnID.equalsIgnoreCase("VSIResolveReshipHold.0001.ex"))
							{	
							String strSTSRemorsPrd = null;
							String strSTHRemorsPrd = null;
							String strAgentModTime =null;
							int intSTSremorsPrd = 0;
							int intSTHremorsPrd = 0;
							Integer agentModHoldRemorse=0;
							
							Element processedHTEle = null;
							Element orderLinesEle = null;
							for(int i=0;i<orderHoldTypeNL.getLength();i++){
								Element processedHTOLEle = null;
								Element orderHTEle = (Element) orderHoldTypeNL.item(i);
								holdType = orderHTEle.getAttribute(ATTR_HOLD_TYPE);
								status = orderHTEle.getAttribute(ATTR_STATUS);
								olk = orderHTEle.getAttribute(ATTR_ORDER_LINE_KEY);
								holdCreatedTS = orderHTEle.getAttribute("LastHoldTypeDate");
								if(status.equalsIgnoreCase("1100") )
								{
									
									if(holdType.equalsIgnoreCase("RESHIP_LINE_HOLD"))
									{
										/*if(YFCCommon.isVoid(strSTSRemorsPrd))
										{
											strSTSRemorsPrd = getRemorsePeriod(env, entCode, VSIConstants.VSI_STS_REMORSE_PERD);
											intSTSremorsPrd=Integer.parseInt(strSTSRemorsPrd);
										}
										*/
										boolean isReShipExprd = checkRemorseExpry(holdCreatedTS,30);
										if(YFCCommon.isVoid(orderLinesEle))
										{
										 orderLinesEle = processedHoldDoc.createElement(ELE_ORDER_LINES);
										 orderEle.appendChild(orderLinesEle);
										}
										
										if(isReShipExprd)
										{
											Element orderLineEle = processedHoldDoc.createElement(ELE_ORDER_LINE);
											orderLineEle.setAttribute(ATTR_ORDER_LINE_KEY, olk);
											orderLinesEle.appendChild(orderLineEle);
											if(YFCCommon.isVoid(processedHTOLEle))
											{
												processedHTOLEle = processedHoldDoc.createElement("ProcessedHoldTypes");
												orderLineEle.appendChild(processedHTOLEle);
											}
											
											Element procsdOrderHTEle = processedHoldDoc.createElement("OrderHoldType");
											procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "RESHIP_LINE_HOLD");
											procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
											procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT, "ReShip Hold Expired");
											processedHTOLEle.appendChild(procsdOrderHTEle);
										}	
									}
								}
									
								}
							
							}
						//OMS-1082: END
						if(txnID != null && txnID.equalsIgnoreCase("VSIResolveRHold.0001.ex"))
						{
							
							String strSTSRemorsPrd = null;
							String strSTHRemorsPrd = null;
							String strAgentModTime =null;
							int intSTSremorsPrd = 0;
							int intSTHremorsPrd = 0;
							Integer agentModHoldRemorse=0;
							
							Element processedHTEle = null;
							Element orderLinesEle = null;
							for(int i=0;i<orderHoldTypeNL.getLength();i++){
								Element processedHTOLEle = null;
								Element orderHTEle = (Element) orderHoldTypeNL.item(i);
								holdType = orderHTEle.getAttribute(ATTR_HOLD_TYPE);
								status = orderHTEle.getAttribute(ATTR_STATUS);
								olk = orderHTEle.getAttribute(ATTR_ORDER_LINE_KEY);
								holdCreatedTS = orderHTEle.getAttribute("LastHoldTypeDate");
								if(status.equalsIgnoreCase("1100") )
								{
									if(holdType.equalsIgnoreCase("REMORSE_HOLD"))
									{
										if(YFCCommon.isVoid(strSTSRemorsPrd))
										{
											strSTSRemorsPrd = getRemorsePeriod(env, entCode, VSIConstants.VSI_STS_REMORSE_PERD);
											intSTSremorsPrd=Integer.parseInt(strSTSRemorsPrd);
										}
										
										boolean isRemorseExprd = checkRemorseExpry(holdCreatedTS,intSTSremorsPrd);
										if(YFCCommon.isVoid(orderLinesEle))
										{
										 orderLinesEle = processedHoldDoc.createElement(ELE_ORDER_LINES);
										 orderEle.appendChild(orderLinesEle);
										}
										
										if(isRemorseExprd)
										{
											Element orderLineEle = processedHoldDoc.createElement(ELE_ORDER_LINE);
											orderLineEle.setAttribute(ATTR_ORDER_LINE_KEY, olk);
											orderLinesEle.appendChild(orderLineEle);
											if(YFCCommon.isVoid(processedHTOLEle))
											{
												processedHTOLEle = processedHoldDoc.createElement("ProcessedHoldTypes");
												orderLineEle.appendChild(processedHTOLEle);
											}
											
											Element procsdOrderHTEle = processedHoldDoc.createElement("OrderHoldType");
											procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "REMORSE_HOLD");
											procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
											procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT, "Remorse Expired");
											processedHTOLEle.appendChild(procsdOrderHTEle);
										}	
									}
									
									if(holdType.equalsIgnoreCase("VSI_REMORSE_HOLD"))
									{
										if(YFCCommon.isVoid(strSTHRemorsPrd))
										{
											strSTHRemorsPrd = getRemorsePeriod(env, entCode, VSIConstants.VSI_REMORSE_DURATION);
											intSTHremorsPrd=Integer.parseInt(strSTHRemorsPrd);
										}
										boolean isRemorseExp = checkRemorseExpry(holdCreatedTS,intSTHremorsPrd);
										if(isRemorseExp){
											if(YFCCommon.isVoid(processedHTEle))
											{
												processedHTEle = processedHoldDoc.createElement("ProcessedHoldTypes");
												orderEle.appendChild(processedHTEle);
											}
											orderEle.appendChild(processedHTEle);
											Element procsdOrderHTEle = processedHoldDoc.createElement("OrderHoldType");
											procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_REMORSE_HOLD");
											procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
											processedHTEle.appendChild(procsdOrderHTEle);
										} 
										
									}
									if(holdType.equals(VSIConstants.AGENT_MOD_HOLD))
									{
										if(YFCCommon.isVoid(strAgentModTime))
										{
											
											strAgentModTime = getRemorsePeriod(env, entCode, VSIConstants.AGENT_MOD_HOLD);
											agentModHoldRemorse=Integer.parseInt(strAgentModTime);
										}
										
										boolean isRemorseExprd = checkRemorseExpry(holdCreatedTS,agentModHoldRemorse);
										if(isRemorseExprd){
											if(YFCCommon.isVoid(processedHTEle))
											{
												processedHTEle = processedHoldDoc.createElement(VSIConstants.ATTR_PROCESSED_HOLD_TYPES);
												orderEle.appendChild(processedHTEle);
											}
											
											Element procsdOrderHTEle = processedHoldDoc.createElement(VSIConstants.ELE_ORDER_HOLD_TYPE);
											procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, VSIConstants.AGENT_MOD_HOLD);
											procsdOrderHTEle.setAttribute(ATTR_STATUS, VSIConstants.STATUS_BACKORDERED);
											processedHTEle.appendChild(procsdOrderHTEle);
										} 

									}
									
								}
								
								int prossdOLCount = orderEle.getOwnerDocument().getElementsByTagName(ELE_ORDER_LINE).getLength();
								if( prossdOLCount > 0){
									processedHoldDoc = orderEle.getOwnerDocument();
									
								}
							}
							
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
			} catch (TransformerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//System.out.println("Printing processedHoldDoc "+XMLUtil.getXMLString(processedHoldDoc));
		if(log.isDebugEnabled()){
			log.info("Printing processedHoldDoc "+XMLUtil.getXMLString(processedHoldDoc));
		}
		// TODO Auto-generated method stub
		return processedHoldDoc;
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

	private void processRiskifiedResponse(YFSEnvironment env,
			String strResponse, String ohk, String orderNo) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException {
		// TODO Auto-generated method stub
		log.info("Printing RiskifiedRespnse: " + strResponse);
		

		if (strResponse != null ) {

			if (strResponse.contains(VSIConstants.ATTR_STATUS_APPROVE)) {
				Document docProcessedHold=null;
				Element orderEle=null;
				Element eleOrderHoldTypes=null;
			
				    docProcessedHold =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
					orderEle =docProcessedHold.getDocumentElement();
					orderEle.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
					orderEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,ohk);
					eleOrderHoldTypes = SCXmlUtil.createChild(orderEle, VSIConstants.ELE_ORDER_HOLD_TYPES);
					Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
					eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
							"VSI_FRAUD_HOLD");
					eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_RESOLVED);
					eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
							"Fraud Check Approved");
			log.info("Printing docProcessedHold: "
					+ XMLUtil.getXMLString(docProcessedHold));
			VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docProcessedHold);	
				
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

			}
			
		}

	}

	//OMS-2046 END





	private Document createOrderInvoiceIn(String ohk, List<Element> eleOrderLineList) throws ParserConfigurationException ,YFSException, RemoteException  {
		
		String strOrderLineKey = null;
		String strQty = null;
		
		Document createInvoiceDoc = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleCreateInvoiceDoc = createInvoiceDoc.getDocumentElement();

		eleCreateInvoiceDoc.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		eleCreateInvoiceDoc.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, VSIConstants.ELE_VSI_CREATE_ORDER_INVOICE);
		eleCreateInvoiceDoc.setAttribute(VSIConstants.ELE_IGNORE_STATUS_CHECK, VSIConstants.FLAG_Y);
		eleCreateInvoiceDoc.setAttribute(VSIConstants.ELE_IGNORE_TRANSACTION_DEPENDENCIES, VSIConstants.FLAG_Y);
		
		Element eleOrderLinesOut = createInvoiceDoc
				.createElement(VSIConstants.ELE_ORDER_LINES);
				
		eleCreateInvoiceDoc.appendChild(eleOrderLinesOut);
		
		for (int i = 0; i < eleOrderLineList.size(); i++) {
			Element eleOL = eleOrderLineList.get(i);
			strOrderLineKey = eleOL.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			strQty= eleOL.getAttribute(VSIConstants.ATTR_ORD_QTY);
			Element eleOrderLineOut = createInvoiceDoc
					.createElement(VSIConstants.ELE_ORDER_LINE);
			eleOrderLineOut.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,strOrderLineKey);
			eleOrderLineOut.setAttribute(VSIConstants.ATTR_QUANTITY,strQty);
			
			eleOrderLinesOut.appendChild(eleOrderLineOut);
			
			if(log.isDebugEnabled()){
				log.debug("strOrderLineKey"+strOrderLineKey);
			}
		}
		
		if (log.isVerboseEnabled()) {
			log.verbose("createOrderInvoice input : \n"
					+ XMLUtil.getXMLString(createInvoiceDoc));
		}
		return createInvoiceDoc;

	}


	private Document processKountResponse(YFSEnvironment env,
			String fraudResponse, Document processedHoldDoc, String ohk, String orderNo)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("Printing fraudRespnse: " + fraudResponse);
		}
		
		String strAuto = "";
		Element orderEle = processedHoldDoc.getDocumentElement();
		//System.out.println(" *****  nvpResponse.size() = "+nvpResponse.size());
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
			Element processedHTEle = processedHoldDoc
			.createElement("ProcessedHoldTypes");
	orderEle.appendChild(processedHTEle);
	Element procsdOrderHTEle = processedHoldDoc
			.createElement("OrderHoldType");
	procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_FRAUD_HOLD");
	processedHTEle.appendChild(procsdOrderHTEle);
			// RelaeseHold

			procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
			procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT,
					"Fraud Check Approved");

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("D")) {
			Element processedHTEle = processedHoldDoc
			.createElement("ProcessedHoldTypes");
	orderEle.appendChild(processedHTEle);
	Element procsdOrderHTEle = processedHoldDoc
			.createElement("OrderHoldType");
	procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_FRAUD_HOLD");
	processedHTEle.appendChild(procsdOrderHTEle);
			// Cancel Order
			// Code to be added to cancel the order
			Document changeOrderInput = XMLUtil.createDocument("Order");
			Element orderElement = changeOrderInput.getDocumentElement();
			orderElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
			orderElement.setAttribute("Override", "Y");
			orderElement.setAttribute("ModificationReasonText",
					"Fraud Check Declined");
			orderElement.setAttribute("ModificationReasonCode",
			"Fraud Check Declined");
			orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
			/*System.out.println("changeOrderInput: "
					+ XMLUtil.getXMLString(changeOrderInput));*/
			VSIUtils.invokeAPI(env, "changeOrder", changeOrderInput);
			//raiseAlert(env, orderNo,ohk,"Fraud Check Declined");
		}
			
			else if (strAuto != null && strAuto.trim().equalsIgnoreCase("ERR")) {
				// Cancel Order
				// Code to be added to cancel the order
				int errLength=fraudResponse.length();
				if(errLength > 100){
					fraudResponse=fraudResponse.substring(0, 99);
				}
				raiseAlert(env,orderNo,ohk,fraudResponse);
				

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("R")) {
			Element processedHTEle = processedHoldDoc
			.createElement("ProcessedHoldTypes");
	orderEle.appendChild(processedHTEle);
	Element procsdOrderHTEle = processedHoldDoc
			.createElement("OrderHoldType");
	procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_FRAUD_HOLD");
	processedHTEle.appendChild(procsdOrderHTEle);

			// create FRR hold and raise exception & CSR alert
			// code to be added to create FRR hold and raising exception & CSR
			// alert
			procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
			procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT,
					"Fraud Check Under Review");
			Element eleHoldTypesToAdd= processedHoldDoc
					.createElement("HoldTypesToAdd");
			orderEle.appendChild(eleHoldTypesToAdd);
			Element procsdOrderHTEle2 = processedHoldDoc
					.createElement("OrderHoldType");
			procsdOrderHTEle2.setAttribute(ATTR_HOLD_TYPE,
					"VSI_FRD_REVIEW_HOLD");
			procsdOrderHTEle2.setAttribute(ATTR_STATUS, "1100");
			procsdOrderHTEle2.setAttribute(ATTR_REASON_TEXT,
					"Fraud Check Under Review");
			eleHoldTypesToAdd.appendChild(procsdOrderHTEle2);
			raiseAlert(env, orderNo,ohk,"Fraud Check Under Review");
		}
		}
		if(log.isDebugEnabled()){
			log.info("Printing processedHoldDoc: "
					+ XMLUtil.getXMLString(processedHoldDoc));
		}
		return processedHoldDoc;

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
	public void processFraudReviewUpdate(YFSEnvironment env,
			Document inXML)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("Printing inXML: " + XMLUtil.getXMLString(inXML));
			//log.debug("Printing inXML: " + XMLUtil.getXMLString(inXML));
		}
		if(inXML != null){
			String orderNo = inXML.getDocumentElement().getAttribute("OrderNo");
			Document orderListinp = XMLUtil.createDocument("Order");
			Element orderElementlist = orderListinp.getDocumentElement();
			orderElementlist.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
			orderElementlist.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
			//orderElementlist.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, "VSI.com");
			Document outdoc=VSIUtils.invokeAPI(env, VSIConstants.API_GET_ORDER_LIST, orderListinp);
			Element ipOrderEle = (Element) outdoc.getDocumentElement().getElementsByTagName("Order").item(0);
			String ohk=ipOrderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
			String rvwdStatus = inXML.getDocumentElement().getAttribute("NewValue");
			String reasonCode = inXML.getDocumentElement().getAttribute("ReasonCode");
			if(rvwdStatus != null && !rvwdStatus.trim().equalsIgnoreCase("")){
				Document changeOrderInput = XMLUtil.createDocument("Order");
				Element orderElement = changeOrderInput.getDocumentElement();
				orderElement.setAttribute("Override", "Y");
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
				orderElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
				//orderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, "VSI.com");
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				if(rvwdStatus.equalsIgnoreCase("Decline")){
					//raiseAlert(env, orderNo,ohk,"Fraud Check Declined");
					orderElement.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
					orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, reasonCode);
					orderElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "FRAUD");
				}else if (rvwdStatus.equalsIgnoreCase("Approve")){
					orderElement.setAttribute(VSIConstants.ATTR_ACTION, "MODIFY");
					Element orderHldTypesEle = changeOrderInput.createElement("OrderHoldTypes");
					orderElement.appendChild(orderHldTypesEle);
					Element orderHldTypeEle = changeOrderInput.createElement("OrderHoldType");
					orderHldTypesEle.appendChild(orderHldTypeEle);
					orderHldTypeEle.setAttribute(ATTR_HOLD_TYPE,
							"VSI_FRD_REVIEW_HOLD");
					orderHldTypeEle.setAttribute(ATTR_STATUS, "1300");
					orderHldTypeEle.setAttribute(ATTR_REASON_TEXT,
							reasonCode);
				}
				if(log.isDebugEnabled()){
					log.info("Printing changeOrderInput: " + changeOrderInput);
					//log.debug("Printing changeOrderInput:  " + XMLUtil.getXMLString(changeOrderInput));
				}
				VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, changeOrderInput);
				//OMS-1098, commenting below line
				//resolveAlert(env,orderNo,"Fraud Check Under Review");
			}
			
			
		}
		

	}
	
	private void resolveAlert(YFSEnvironment env, String orderNo, String reason) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		Document resolveExInput = XMLUtil.createDocument("ResolutionDetails");
		Element rootElement = resolveExInput.getDocumentElement();
		Element InboxElement = resolveExInput.createElement("Inbox");
		rootElement.appendChild(InboxElement);
		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);

		InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION,
				reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON,
				reason);
		InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
				"Fraud Check");
		InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID,
				"VSI_FRAUD_ALERT");

		Element InboxReferencesListElement = resolveExInput
				.createElement("InboxReferencesList");

		InboxElement.appendChild(InboxReferencesListElement);
		Element InboxReferencesElement = resolveExInput
				.createElement("InboxReferences");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME,
				"OrderNo");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, orderNo);

		InboxReferencesListElement.appendChild(InboxReferencesElement);
		VSIUtils.invokeAPI(env, "resolveException",
				resolveExInput);
		
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

	private HashMap callKountRIS(YFSEnvironment env, String nvpRequest) {
		HashMap kountRespMap = new HashMap();
		kountRespMap = VSIKountRIS.callKountRequest(env,nvpRequest);
		return kountRespMap;
	}

	private String constructKountRequest(Document getOrderListOp,YFSEnvironment env) {

		String extnTaxprodCode;
		String paymentKey = "";
		String paymentType = "";
		String orderTotal = "";
		String custName = "";
		String address1 = "";
		//OMS-1191: Send AddressLine 2 in Kount Req:Start
		String strAddress2 ="";
		//OMS-1191: Send AddressLine 2 in Kount Req:End
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
					+ itemEle.getAttribute(ATTR_ITEM_DESC);
			nvpStr += "&PROD_ITEM[" + i + "]="
					+ itemExtnEle.getAttribute(EXTN_ACT_SKU_ID);
			
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
		Element eleOrderExtn=SCXmlUtil.getChildElement(orderEle, ELE_EXTN);
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
					//OMS-1414 :Start
				if(FLAG_Y.equals(strExtnSubscriptionOrder) && FLAG_Y.equals(strExtnMobileOrder)){
					nvpStr += "&SITE=" + "MOBADPR";
				} else if(FLAG_Y.equals(strExtnOriginalADPOrder)&& FLAG_Y.equals(strExtnMobileOrder) ){
					nvpStr += "&SITE=" + "MOBADPI";
				}//OMS-1414 :End
				
				else
				if(FLAG_Y.equals(strExtnSubscriptionOrder)){
					nvpStr += "&SITE=" + "ADPR";
				} else if(FLAG_Y.equals(strExtnOriginalADPOrder)){
					nvpStr += "&SITE=" + "ADPI";
				} else if(FLAG_Y.equals(strExtnMobileOrder)){
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
			//OMS-1191:Send AddressLine 2 in Kount Req:Start
			strAddress2 = billToAddEle.getAttribute("AddressLine2");
			//OMS-1191:Send AddressLine 2 in Kount Req:End
			city = billToAddEle.getAttribute("City");
			state = billToAddEle.getAttribute("State");
			country = billToAddEle.getAttribute("Country");
			postalCode = billToAddEle.getAttribute("ZipCode");
			phone = billToAddEle.getAttribute("EveningPhone");
		}

		nvpStr += "&NAME=" + custName;

		nvpStr += "&B2A1=" + address1;
		//OMS-1191:Send AddressLine 2 in Kount Req:Start
		nvpStr += "&B2A2" + strAddress2;
		//OMS-1191:Send AddressLine 2 in Kount Req:End
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
			//OMS-1191:Send AddressLine 2 in Kount Req:Start
			strAddress2 = shipToAddEle.getAttribute("AddressLine2");
			//OMS-1191:Send AddressLine 2 in Kount Req:End
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
		//OMS-1191:Send AddressLine 2 in Kount Req:Start
		nvpStr += "&S2A2=" + strAddress2;
		//OMS-1191:Send AddressLine 2 in Kount Req:End
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
		if(FLAG_Y.equals(strExtnMobileOrder) && !YFCObject.isVoid(strExtnMobileType)){
			nvpStr += "&UDF[" + strExtnMobileType+"]";
		}
		nvpStr += "&URL=" + YFSSystem.getProperty("KOUNT_URL");
		//nvpStr += "&URL=" + "https://risk.test.kount.net";

		return nvpStr;

	}
	
	private boolean checkRemorseExpry(String holdCreatedTS, Integer remorsPrd) {
		
		boolean isRemorseExprd = false;
		
		try {
			////System.out.println("");
			////System.out.println(" Entered method - checkRemorseExpry printing args - holdCreatedTS "+holdCreatedTS+" remorsPrd " +remorsPrd);
			Date orderHTCreatedDte = VSIGeneralUtils.convertDate(holdCreatedTS);
			////System.out.println("*** Printing orderHTCreatedDte "+orderHTCreatedDte.toString());
			Calendar cal = Calendar.getInstance();
			cal.setTime(orderHTCreatedDte);
			cal.add(Calendar.MINUTE,remorsPrd);
			orderHTCreatedDte = cal.getTime();
			long actDtlng=orderHTCreatedDte.getTime();
			////System.out.println("*** Printing orderHTCreatedDte after adding remorse Period " +orderHTCreatedDte.toString());
			////System.out.println("*** Printing actDtlng "+actDtlng);
			Calendar localCal=Calendar.getInstance();
			long curtimelong=localCal.getTimeInMillis();
			////System.out.println("*** Printing curtimelong "+curtimelong);
			if(curtimelong >= actDtlng){
				////System.out.println("*** Remorse Expired ***");
				isRemorseExprd = true;
			}
			
			
			
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		////System.out.println(" Exit Method checkRemorseExpry printing isRemorseExprd "+isRemorseExprd);
		return isRemorseExprd;
	}

	private String getRemorsePeriod(YFSEnvironment env, String strEntCode, String stsRemorsePrd) {
		
		String remorsePrd = null;
		try{
		Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

		Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE,strEntCode);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,stsRemorsePrd);

		Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);
		
		
		if (docAfterGetCommonCodeList != null) {
			Element eleOutCommonCode=(Element) docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
			if(eleOutCommonCode != null){
				remorsePrd = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
			}
		
			
		}}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		////System.out.println("***remorsePrd " +remorsePrd);
		
		// TODO Auto-generated method stub
		return remorsePrd;
	}
	
	
	//OMS-978: Start
	
	/*
	 * To fetch the IP address from the CommonCode
	 * 
	 */
	private String getIPAddressForEntryType(YFSEnvironment env,String strEntryType){
		String strIP=VAL_DEFAULT_IPAD;
		try {
			if(!YFCCommon.isStringVoid(strEntryType)){
				Document docInputgetCommonCodeList=XMLUtil.createDocument(ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_IPAD_IP");
				eleCommonCode.setAttribute(ATTR_CODE_VALUE, strEntryType);
				
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
				        .getElementsByTagName(ELE_COMMON_CODE);

				if(nlCommonCode!=null && nlCommonCode.getLength()>0){
					strIP=((Element) nlCommonCode.item(0))
					          .getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
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
				Document docInputgetCommonCodeList=XMLUtil.createDocument(ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_AVS_CODE");
				eleCommonCode.setAttribute(ATTR_CODE_VALUE, strPaymentTechCode);

				if(log.isDebugEnabled()){
					log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInputgetCommonCodeList));
				}
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
						.getElementsByTagName(ELE_COMMON_CODE);

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
