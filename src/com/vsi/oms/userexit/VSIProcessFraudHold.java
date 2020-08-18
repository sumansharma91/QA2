package com.vsi.oms.userexit;

import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.rmi.RemoteException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIKountRIS;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSProcessOrderHoldTypeUE;

public class VSIProcessFraudHold implements YFSProcessOrderHoldTypeUE,
		VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIProcessFraudHold.class);

	public Document processOrderHoldType(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		Document processedHoldDoc = null;
		if(log.isDebugEnabled()){
			log.info(" Inside class VSIResolveHold "
					+ XMLUtil.getXMLString(inXML));
		}
		String ohk = null;
		String orderNo = null;
		String enterpriseCode = "";
		if (inXML != null) {
			ohk = inXML.getDocumentElement().getAttribute(
					VSIConstants.ATTR_ORDER_HEADER_KEY);
			orderNo = inXML.getDocumentElement().getAttribute(
					VSIConstants.ATTR_ORDER_NO);
			enterpriseCode = inXML.getDocumentElement().getAttribute(
					VSIConstants.ATTR_ENTERPRISE_CODE);
		}
		if(log.isDebugEnabled()){
			log.info(" ohk " + ohk);
			log.info(" orderNo " + orderNo);
		}
		if (ohk != null && !ohk.equals("") && orderNo != null && !orderNo.equals("") ) {
			try {
				Document getOrderListIp = XMLUtil
						.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(
						VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				Document getOrderListOp = VSIUtils
						.invokeAPI(
								env,
								"global/template/api/VSIGetOrderListSOForFraudHold.xml",
								VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				
				if (getOrderListOp != null) {
					Element ipOrderEle = (Element) getOrderListOp
							.getDocumentElement().getElementsByTagName("Order")
							.item(0);
					NodeList orderHoldTypeNL = ipOrderEle
							.getElementsByTagName("OrderHoldType");
					
					if (orderHoldTypeNL.getLength() > 0) {

						processedHoldDoc = XMLUtil
								.createDocument(VSIConstants.ELE_ORDER);
						Element orderEle = processedHoldDoc
								.getDocumentElement();

						String strXPATHOrderHoldType = VSIGeneralUtils
								.formXPATHWithOneCondition(
										"/OrderList/Order/OrderHoldTypes/OrderHoldType",
										"HoldType", "VSI_FRAUD_HOLD");
						NodeList nlOrderHoldTypeList = XMLUtil
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
									if (hldStatus != null
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
										// 1) Form thr NVP request String
										String nvpRequest = constructKountRequest(getOrderListOp);

										// 2) Invoke the Kount method to make a
										// call to Kount system
										if(log.isDebugEnabled()){
								    		log.debug("Printing nvpRequest: "
														+ nvpRequest);
										}
										HashMap nvpResponse = callKountRIS(env,nvpRequest);
										if(log.isDebugEnabled()){
								    		log.debug("Printing nvpResponse: "
														+ nvpResponse);
										}
										// 3) Process the response and act
										// accordingly within OMS
										processedHoldDoc = processKountResponse(
												env, nvpResponse,
												processedHoldDoc, ohk, orderNo, enterpriseCode);

									}

								}

							}

							// log.info(" *** Printing Order XML : \n"+XMLUtil.getElementXMLString(orderEle));
						}

						
						int prossdOLCount = orderEle.getOwnerDocument()
								.getElementsByTagName(ELE_ORDER_LINE)
								.getLength();
						if (prossdOLCount > 0) {
							processedHoldDoc = orderEle.getOwnerDocument();

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
			}
		}
		// System.out.println("Printing processedHoldDoc "+XMLUtil.getXMLString(processedHoldDoc));
		if(log.isDebugEnabled()){
			log.info("Printing processedHoldDoc "
					+ XMLUtil.getXMLString(processedHoldDoc));
		}
		// TODO Auto-generated method stub
		return processedHoldDoc;
	}

	private Document processKountResponse(YFSEnvironment env,
			HashMap nvpResponse, Document processedHoldDoc, String ohk, String orderNo, String enterpriseCode)
			throws ParserConfigurationException, YFSException, RemoteException,
			YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.info("Printing nvpResponse: " + nvpResponse);
		}
		
		String strAuto = "";
		Element orderEle = processedHoldDoc.getDocumentElement();
		Element processedHTEle = processedHoldDoc
				.createElement("ProcessedHoldTypes");
		orderEle.appendChild(processedHTEle);
		Element procsdOrderHTEle = processedHoldDoc
				.createElement("OrderHoldType");
		procsdOrderHTEle.setAttribute(ATTR_HOLD_TYPE, "VSI_FRAUD_HOLD");
		processedHTEle.appendChild(procsdOrderHTEle);
		if (nvpResponse != null) {
			if (nvpResponse.containsKey("AUTO")) {
				strAuto = (String) nvpResponse.get("AUTO");
			}

		}
		if(log.isDebugEnabled()){
			log.info("Printing AUTO: " + strAuto);
		}
		if (strAuto != null && strAuto.trim().equalsIgnoreCase("A")) {
			// RelaeseHold

			procsdOrderHTEle.setAttribute(ATTR_STATUS, "1300");
			procsdOrderHTEle.setAttribute(ATTR_REASON_TEXT,
					"Fraud Check Approved");

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("D")) {
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
			raiseAlert(env, orderNo,"Fraud Check Declined");

		} else if (strAuto != null && strAuto.trim().equalsIgnoreCase("R")) {

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
			raiseAlert(env, orderNo,"Fraud Check Under Review");
		}
		if(log.isDebugEnabled()){
			log.info("Printing processedHoldDoc: "
					+ XMLUtil.getXMLString(processedHoldDoc));
		}
		return processedHoldDoc;

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
			String rvwdStatus = inXML.getDocumentElement().getAttribute("NewValue");
			String reasonCode = inXML.getDocumentElement().getAttribute("ReasonCode");
			String enterpriseCode = inXML.getDocumentElement().getAttribute(ATTR_ENTERPRISE_CODE);
			if(rvwdStatus != null && !rvwdStatus.trim().equalsIgnoreCase("")){
				Document changeOrderInput = XMLUtil.createDocument("Order");
				Element orderElement = changeOrderInput.getDocumentElement();
				orderElement.setAttribute("Override", "Y");
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
				orderElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
				orderElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, enterpriseCode);
				if(rvwdStatus.equalsIgnoreCase("Decline")){
					raiseAlert(env, orderNo,"Fraud Check Declined");
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
				resolveAlert(env,orderNo,"Fraud Check Under Review");
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

	private void raiseAlert(YFSEnvironment env, String orderNo, String reason)
			throws YFSException, RemoteException, YIFClientCreationException,
			ParserConfigurationException {
		Document createExInput = XMLUtil.createDocument("Inbox");
		Element InboxElement = createExInput.getDocumentElement();

		InboxElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);

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
				"OrderNo");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE,
				"Reprocess");
		InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, orderNo);

		InboxReferencesListElement.appendChild(InboxReferencesElement);

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

	private String constructKountRequest(Document getOrderListOp) {

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
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
String penc = "";
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Element orderEle = (Element) getOrderListOp.getElementsByTagName(
				VSIConstants.ELE_ORDER).item(0);

		String orderNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strEmailID = orderEle.getAttribute("CustomerEMailID");
		String strSCAC = orderEle.getAttribute("SCAC");
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
		nvpStr += "&EMAL=" + strEmailID;
		 //nvpStr += "&EMAL=" + "JOHNDOEAPPROVE@ACME.COM";
		//nvpStr += "&EMAL=" + "JOHNDOEDECLINE@ACME.COM";
		//nvpStr += "&EMAL=" + "JOHNDOEREVIEW@ACME.COM";
		nvpStr += "&IPAD=" + "204.156.7.6";
		nvpStr += "&MACK=" + "Y";
		String sLineType = null;
		String sShipNode = null;
		for (int i = 0; i < noOfLines; i++) {

			extnTaxprodCode = "";
			Element orderLineEle = (Element) orderLinesNL.item(i);
			
			sLineType = orderLineEle.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			sShipNode = orderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
			if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
				
				iShip++;
				
			}else if ((sLineType.equalsIgnoreCase("PICK_IN_STORE"))){
				
				iPick++;
			
			}
			
			Element itemEle = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_ITEM).item(0);
			Element itemExtnEle = (Element) itemEle.getElementsByTagName(
					VSIConstants.ELE_EXTN).item(0);
			Element linePriceEle = (Element) orderLineEle.getElementsByTagName(
					VSIConstants.ELE_LINE_PRICE).item(0);

			nvpStr += "&PROD_DESC[" + i + "]="
					+ itemEle.getAttribute("ItemDesc");
			nvpStr += "&PROD_ITEM[" + i + "]="
					+ itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
			nvpStr += "&PROD_PRICE[" + i + "]="
					+ linePriceEle.getAttribute(VSIConstants.ATTR_LINE_TOTAL);
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
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//Authorization interface changes
String AttrKountToken = paymtMthdEle.getAttribute("PaymentReference6");
//if(AttrKountToken!="" || AttrKountToken!=null){
	//paymentType = "CARD";
    //penc ="KHASH";
    //paymentKey=AttrKountToken;
	
//}
//else if (AttrKountToken=="" || AttrKountToken==null  ){
	//paymentType = "CARD";
	  //paymentKey=AttrKountToken;
	  //paymentKey = paymtMthdEle.getAttribute("PaymentReference1");
//}
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
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
		nvpStr += "&SESS=" + orderNo;
		nvpStr += "&SITE=" + "6101";
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//nvpStr += "&PENC=" + penc;
//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
		Element ordTotalEle = (Element) orderEle.getElementsByTagName(
				"OverallTotals").item(0);
		if (ordTotalEle != null) {
			orderTotal = ordTotalEle.getAttribute("GrandTotal");
		}

		nvpStr += "&TOTL=" + orderTotal;

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

		Element shipToAddEle = (Element) orderEle.getElementsByTagName(
				"PersonInfoShipTo").item(0);
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
		nvpStr += "&S2A1=" + address1;
		nvpStr += "&S2CI=" + city;
		nvpStr += "&S2NM=" + custName;
		nvpStr += "&S2ST=" + state;
		nvpStr += "&S2CC=" + country;
		nvpStr += "&S2PC=" + postalCode;

		nvpStr += "&SHTP=" + strSCAC;
		nvpStr += "&ORDR=" + orderNo;
		nvpStr += "&AVST=" + "X";
		nvpStr += "&AVSZ=" + "X";
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
		nvpStr += "&URL=" + YFSSystem.getProperty("KOUNT_URL");
		//nvpStr += "&URL=" + "https://risk.test.kount.net";

		return nvpStr;

	}

}
