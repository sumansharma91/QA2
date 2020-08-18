package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.condition.VSIConditionToCheckShipToHomeOrderStatus;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIRestrictedItemValidation;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDoubleUtils;
import com.yantra.yfs.core.YFSObject;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 * Before Change order UE.
 *
 */
public class VSIBeforeChangeOrder implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIBeforeChangeOrder.class);
	YIFApi api;
	HashMap<String, Element> storeDetails = new HashMap<String, Element>();
	
	public Document vsiBeforeChangeOrder(YFSEnvironment env, Document inXML)
	throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIBeforeChangeOrder================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		HashMap mapLineQty = new HashMap();
		try{
			//System.out.println("inXML"+ XmlUtils.getString(inXML));

			//OMS-1333 : Start
			if(log.isDebugEnabled()){
				log.debug("Start of country validation");
			}
			Element eleOrderFromIp=inXML.getDocumentElement();
			Element elePersonInfoBillToIP=SCXmlUtil.getChildElement(eleOrderFromIp, VSIConstants.ELE_PERSON_INFO_BILL_TO);
			if(!YFCObject.isVoid(XmlUtils.getString(elePersonInfoBillToIP))){
				String strCountryIP=elePersonInfoBillToIP.getAttribute(VSIConstants.ATTR_COUNTRY);
				String sState1 = SCXmlUtil.getAttribute(elePersonInfoBillToIP, VSIConstants.ATTR_STATE);
				//OMS-1093 start
				if(!strCountryIP.equals("US")&&YFCObject.isVoid(sState1)){
					elePersonInfoBillToIP.setAttribute(VSIConstants.ATTR_STATE, strCountryIP);
					if(log.isDebugEnabled()){
						log.verbose("Printing elePersonInfoBillToIP :" + SCXmlUtil.getString(elePersonInfoBillToIP));
					}
	    		}
				//OMS-1093 END
				if(!YFCCommon.isStringVoid(strCountryIP) && strCountryIP.length()==3){
					if(log.isDebugEnabled()){
						log.debug("Country code lenght is 3 , coverting to 2 characters");
					}
					String strCountryFromCC=getCountryCode(env,strCountryIP);
					if(!YFCCommon.isStringVoid(strCountryFromCC)){
						if(log.isDebugEnabled()){
							log.debug("stamping country as: "+strCountryFromCC);
						}
						elePersonInfoBillToIP.setAttribute(VSIConstants.ATTR_COUNTRY, strCountryFromCC);
					}
				}
			}
			Element elePersonInfoShipToIP=SCXmlUtil.getChildElement(eleOrderFromIp, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			if(!YFCObject.isVoid(XmlUtils.getString(elePersonInfoShipToIP))){
				String strCountryIP=elePersonInfoShipToIP.getAttribute(VSIConstants.ATTR_COUNTRY);
            	String sState1 = SCXmlUtil.getAttribute(elePersonInfoShipToIP, VSIConstants.ATTR_STATE);
				//OMS-1093 start
				if(!strCountryIP.equals("US")&&YFCObject.isVoid(sState1)){
					elePersonInfoShipToIP.setAttribute(VSIConstants.ATTR_STATE, strCountryIP);
					if(log.isDebugEnabled()){
						log.verbose("Printing elePersonInfoShipToIP :" + SCXmlUtil.getString(elePersonInfoShipToIP));
					}
	    		}
				//OMS-1093 END
				if(!YFCCommon.isStringVoid(strCountryIP) && strCountryIP.length()==3){
					if(log.isDebugEnabled()){
						log.debug("Country code lenght is 3 , coverting to 2 characters");
					}
					String strCountryFromCC=getCountryCode(env,strCountryIP);
					if(!YFCCommon.isStringVoid(strCountryFromCC)){
						if(log.isDebugEnabled()){
							log.debug("stamping country as: "+strCountryFromCC);
						}
						elePersonInfoShipToIP.setAttribute(VSIConstants.ATTR_COUNTRY, strCountryFromCC);
					}
				}
			}
			if(log.isDebugEnabled()){
				log.debug("End of country validation");
			}
			//OMS-1333 : End
				//Start - task ARS-276
				Element eleIntHold= SCXmlUtil.getXpathElement(inXML.getDocumentElement(), "OrderHoldTypes/OrderHoldType[@HoldType='VSI_INTERNAT_HOLD']");
				if(!YFCObject.isVoid(XmlUtils.getString(eleIntHold))){
				double intIntHoldStatus = Integer.valueOf(eleIntHold.getAttribute(VSIConstants.ATTR_STATUS));
					if(intIntHoldStatus==1300){
						boolean callChangeOrder = false;
						Document docCancelOrderLineIntHold = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						//change order document creation
						Element eleRootChangeOrder = docCancelOrderLineIntHold.getDocumentElement();
						Element eleOrderLinesChangeOrder =SCXmlUtil.createChild(eleRootChangeOrder, VSIConstants.ELE_ORDER_LINES);
						//call get orderlist using UE input
						Document getOrderListOuput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_INTERNATIONAL_HOLD, VSIConstants.API_GET_ORDER_LIST, inXML);
						Element eleOrderList = getOrderListOuput.getDocumentElement();
						eleRootChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER).getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
						SimpleDateFormat dateformat = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
						NodeList nlOrderLineList = eleOrderList
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						//loop through lines and check for future inventory
						for (int i = 0; i < nlOrderLineList.getLength(); i++) {
							Element eleOrderLine = (Element) nlOrderLineList.item(i);
							String strScheduleOrderLineKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							NodeList nlScheduleList = eleOrderLine
									.getElementsByTagName(VSIConstants.ELE_SCHEDULE);
							double dblQuantity = 0; 
							boolean addLineToChangeOrder = false;
							
							for (int j = 0; j < nlScheduleList.getLength(); j++) {
								Element eleSchedule = (Element) nlScheduleList.item(j);
								String strAvailableDate  = eleSchedule.getAttribute(VSIConstants.ATTR_PRODUCT_AVAILABILITY_DATE);
								Date dateFormattedAvilDate = VSIGeneralUtils.convertDate(strAvailableDate);
								String datefinalformat = VSIGeneralUtils.formatDate(dateFormattedAvilDate, VSIConstants.YYYY_MM_DD);
								Date finalAvailDate = dateformat.parse(datefinalformat);
								Date currentDate = new Date();
								Date finalcurrentDate= dateformat.parse(dateformat.format(currentDate));

								String strQty = eleSchedule.getAttribute(VSIConstants.ATTR_QUANTITY);
								if(finalAvailDate.after(finalcurrentDate)){
									dblQuantity = dblQuantity + Double.parseDouble(strQty);
									addLineToChangeOrder = true;
									callChangeOrder = true;
								}//end of if date check
							}//end of Schedule List loop
							//Set Cancel attributes if future inventory only
							//System.out.println("strQuantity::"+dblQuantity);
							if(addLineToChangeOrder){
								Element eleOrderLineNoAvail = SCXmlUtil.createChild(eleOrderLinesChangeOrder, VSIConstants.ELE_ORDER_LINE);
								eleOrderLineNoAvail.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
								eleOrderLineNoAvail.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,strScheduleOrderLineKey);
								eleOrderLineNoAvail.setAttribute(VSIConstants.ATTR_ORD_QTY,String.valueOf(dblQuantity));
							}
						}//end of OrderLine loop
						//System.out.println("Input to changeOrder for International Holds: "+SCXmlUtil.getString(docCancelOrderLineIntHold));
						if(callChangeOrder){
							Element eleInputOrderLines = (Element) inXML.getDocumentElement().getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
							SCXmlUtil.removeNode(eleInputOrderLines);
							Element ele = (Element) inXML.importNode(eleOrderLinesChangeOrder, true);
							inXML.getDocumentElement().appendChild(ele);
						}//end of changeOrder
					}
			}
			//END- task ARS-276
				String attrDraftOrderFlag = "";
				boolean bCheckShipToHomeStatus = false;
				boolean bPromotionsReset = false;
				String progId = env.getProgId(); 
				String orderType = null; 
				Document docOrderDetails = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_DETAILS_BEFORE_CHANGE_ORDER, API_GET_ORDER_DETAILS, inXML);
				Element eleOrderDetails = docOrderDetails.getDocumentElement();
				if(!YFCCommon.isVoid(eleOrderDetails)){
					if(!YFCCommon.isVoid(eleOrderDetails)){
						attrDraftOrderFlag = eleOrderDetails.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
						orderType = eleOrderDetails.getAttribute("OrderType");
						VSIConditionToCheckShipToHomeOrderStatus VSIConditionToCheckShipToHomeOrderStatus=new VSIConditionToCheckShipToHomeOrderStatus();
						bCheckShipToHomeStatus = VSIConditionToCheckShipToHomeOrderStatus.evaluateCondition(env,"VSIConditionToCheckShipToHomeOrderStatus",null, docOrderDetails);
					}
				}
				if(!YFCObject.isVoid(attrDraftOrderFlag) && attrDraftOrderFlag.equalsIgnoreCase(VSIConstants.FLAG_Y) ||
						((attrDraftOrderFlag.equalsIgnoreCase(VSIConstants.FLAG_N) && (VSIConstants.PROG_ID_CALL_CENTER.equalsIgnoreCase(progId)
								|| VSIConstants.PROG_ID_STORE.equalsIgnoreCase(progId))))&&bCheckShipToHomeStatus){
					bPromotionsReset = true;
					
				}
				//OMS-1635 : Start
				Element eleOrder2=inXML.getDocumentElement();
				if(!YFCObject.isVoid(attrDraftOrderFlag) && attrDraftOrderFlag.equalsIgnoreCase(VSIConstants.FLAG_Y))
				{
					Element eleRootExtn=null;
					eleRootExtn=SCXmlUtil.getChildElement(eleOrder2, "Extn");
					if(null!=eleRootExtn)
					{
						eleRootExtn.setAttribute("ExtnNewOrder", "Y");
					}
					else
					{
						eleRootExtn=inXML.createElement("Extn");
						eleOrder2.appendChild(eleRootExtn);
						eleRootExtn.setAttribute("ExtnNewOrder", "Y");
						
					}
				}
			//OMS-1635 :End	
			//OMS-1951: Start
				Document docGetOrderList = null;
				Document docGetOrderListInput = null;
				Element eleOrd=null;
				docGetOrderListInput=SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleOrderElement = docGetOrderListInput.getDocumentElement();
				eleOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrderFromIp.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				eleOrderElement.setAttribute(VSIConstants.ATTR_MAX_RECORDS, "1");
				
				docGetOrderList = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_BEFOREORDERCHANGE_GETORDERLIST, VSIConstants.API_GET_ORDER_LIST, docGetOrderListInput);
				eleOrd = SCXmlUtil.getChildElement(docGetOrderList.getDocumentElement(), VSIConstants.ELE_ORDER);
				Element elePaymentMethods = SCXmlUtil.getChildElement(eleOrd, VSIConstants.ELE_PAYMENT_METHODS);
				if(!YFCObject.isNull(elePaymentMethods)){
						Element paymentMethodsElement = SCXmlUtil.getChildElement(eleOrd, VSIConstants.ELE_PAYMENT_METHODS);
						if(!YFCObject.isNull(paymentMethodsElement)){
							ArrayList<Element> alPaymentMethodsElement = SCXmlUtil.getChildren(paymentMethodsElement, VSIConstants.ELE_PAYMENT_METHOD);
							//Looping through Payment methods of output of getOrderList
							for(Element PaymentMethodElement:alPaymentMethodsElement){
								String strPaymentType = PaymentMethodElement.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
								String strSuspendAnyMoreCharges = PaymentMethodElement.getAttribute(VSIConstants.ATTR_SUSPEND_ANYMORE_CHARGES);
								if(!YFCObject.isNull(strPaymentType) && VSIConstants.STR_CREDIT_CARD.equals(strPaymentType) && !VSIConstants.FLAG_Y.equals(strSuspendAnyMoreCharges)){
									{
										Element eleExtn = SCXmlUtil.getChildElement(eleOrderFromIp, VSIConstants.ELE_EXTN);
										if (eleExtn == null) {
											eleExtn = SCXmlUtil.createChild(eleOrderFromIp, VSIConstants.ELE_EXTN);

										}
										eleExtn.setAttribute(VSIConstants.ATTR_EXTN_TNS_AUTHORIZED, VSIConstants.FLAG_Y);
										break;
									}
								}
							}	
						}
				}	
			//OMS-1951: End	
			//OMS- 1137 : Stamp Reservation : Start
			stampOrderLineReservation(env,inXML,docOrderDetails);
			//OMS-117 : Stamp Reservation : End	
			//START - Fix for SU-26
			updateShipNodePersonInfo(env, inXML);
			//System.out.println("UPDATED_inXML: "+SCXmlUtil.getString(inXML));
			//END - Fix for SU-26

			 NodeList nlOrderExtn =  inXML.getDocumentElement().getElementsByTagName(VSIConstants.ELE_EXTN);
			 if(!YFCObject.isVoid(nlOrderExtn)){
				 Element eleOrderExtn= (Element) nlOrderExtn.item(0);
				 if(!YFCCommon.isVoid(eleOrderExtn)){
					 String strExtnOriginalTrackingNo = eleOrderExtn.getAttribute("ExtnOriginalTrackingNo");
					 if(!YFCObject.isVoid(strExtnOriginalTrackingNo)){
						 if(log.isDebugEnabled()){
							 log.debug("ORIG_TRACKING_NO: "+strExtnOriginalTrackingNo);
						 }
						 env.setTxnObject("ORIG_TRACKING_NO", strExtnOriginalTrackingNo);
					 }
				 }
			 }
			Map<String, String> orderLinekeyMap = new HashMap<String, String>();
			//For uncommit coupon. Setting a  uncommit flag for IIB to determine.
			HashMap<String, String> vcOrderLineStatus = new HashMap<String, String>();
			String strUnCommitFlag="Y";
            
			Element rootElement = inXML.getDocumentElement();
			String sOrderType = rootElement.getAttribute(ATTR_ORDER_TYPE);
			String sEntryType = rootElement.getAttribute(ATTR_ENTRY_TYPE);
			NodeList orderLineInfoinXML = rootElement.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int orderLineLen1= orderLineInfoinXML.getLength();
			for(int k=0; k < orderLineLen1; k++){
				
				Element orderLineElement = (Element)orderLineInfoinXML.item(k);
				String OrderLineKey=orderLineElement.getAttribute("OrderLineKey");
				double dblQuantity = SCXmlUtil.getDoubleAttribute(orderLineElement, ATTR_ORD_QTY);
				mapLineQty.put(OrderLineKey, dblQuantity);
				
				Element eleOrderLinePromotions = SCXmlUtil.getChildElement(orderLineElement, ELE_PROMOTION);
				if(YFCCommon.isVoid(eleOrderLinePromotions)){
					eleOrderLinePromotions = SCXmlUtil.createChild(orderLineElement, ELE_PROMOTIONS);
				}
				if(!YFCCommon.isVoid(eleOrderLinePromotions) && bPromotionsReset){
					eleOrderLinePromotions.setAttribute(ATTR_RESET, FLAG_Y);
				}
				
				Element eleOrderLineAwards = SCXmlUtil.getChildElement(orderLineElement, ELE_AWARDS);
				if(YFCCommon.isVoid(eleOrderLineAwards)){
					eleOrderLineAwards = SCXmlUtil.createChild(orderLineElement, ELE_AWARDS);
				}
				if(!YFCCommon.isVoid(eleOrderLineAwards) && bPromotionsReset){
					eleOrderLineAwards.setAttribute(ATTR_RESET, FLAG_Y);
				}

				String strAction=orderLineElement.getAttribute("Action");
				if(strAction.equalsIgnoreCase("CANCEL")){
				//String orderlinestatus="Cancelled";	
				//orderLineElement.setAttribute("Status","Cancelled");
				//String Status=orderLineElement.getAttribute("Status");
				vcOrderLineStatus.put(OrderLineKey, "Cancelled");
				}
				
				//copying PersonInfoShipTo Element from Order level to all Order Lines for orders 
				String sLineType = orderLineElement.getAttribute("LineType");
				String sDeliveryMethod = orderLineElement.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
				if(sOrderType.equalsIgnoreCase("WEB") && sLineType.equalsIgnoreCase("SHIP_TO_HOME")){

					Element elePersonInfoShipTo  = SCXmlUtil.getChildElement(rootElement, "PersonInfoShipTo");
 					SCXmlUtil.importElement(orderLineElement, elePersonInfoShipTo);
                     
				}//end of if
				
			}
			
		     // validations for checking weather the item is restricted or not from the UI is done in js.
			//Validations for Restricted shipping address
			//Created the class VSIRestrictedItemValidation to check wether the item is restricted or not
			
			VSIRestrictedItemValidation restrictedItem = new VSIRestrictedItemValidation();//creating object for class
			Document getOrderListOutput = null;
			Map<String, String> mapOrderLines = null;
			Element orderElement = inXML.getDocumentElement();
			
			Element eleOrderLines1 = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines1, VSIConstants.ELE_ORDER_LINE);
			if(alOrderLines.size() > 0){
			Element eleItem = SCXmlUtil.getChildElement(alOrderLines.get(0), ELE_ITEM);
			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(alOrderLines.get(0), ATTR_PERSON_INFO_SHIP_TO);
			
			
			if(YFCObject.isVoid(eleItem) && !YFCObject.isVoid(elePersonInfoShipTo) ){
				
				//getting the item id from getOrderList
				getOrderListOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST, VSIConstants.API_GET_ORDER_LIST, inXML);
				Element eleOrderLineList = getOrderListOutput.getDocumentElement();
			    Element eleOrder = SCXmlUtil.getChildElement(eleOrderLineList, VSIConstants.ELE_ORDER);
			    Element eleOrderLines2 = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
			    ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(eleOrderLines2, VSIConstants.ELE_ORDER_LINE);
				
				mapOrderLines = new HashMap <String, String>();
				// checking for OrderLines from output of getOrderList.
				
				for(Element eleOrderLine1 : alOrderLineList) {
					Element itemElement = SCXmlUtil.getChildElement(eleOrderLine1, VSIConstants.ELE_ITEM);
					
					mapOrderLines.put(eleOrderLine1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY), itemElement.getAttribute(VSIConstants.ATTR_ITEM_ID));
					
					
					
				} // end for loop
				
			    // checking for orderlines from inXML
				for(Element eleOrderLine:alOrderLines){
					//System.out.println("eleOrderLine = " + SCXmlUtil.getString(eleOrderLine));
									// String strItemId=eleOrderLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
									// Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
					//eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item", true);
				    elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, ATTR_PERSON_INFO_SHIP_TO, true);
				    if(!YFCObject.isNull(elePersonInfoShipTo)){
									 String strCountry =SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY);
									 String strState =SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_STATE);
									 String strZipCode =SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);
									 String itemid = mapOrderLines.get(eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
									 restrictedItem.vsiRestrictedItemValidation(env, itemid, strCountry, strState, strZipCode);
				    		}//end of if
						}//end of for
			    
			
				
			}
		}
					
			
			//End Of:For uncommit coupon. Setting a  uncommit flag for IIB to determine.
			
			/*
			//For Jira 590 uncommit coupon. Setting a  uncommit flag for IIB to determine.
			String orderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
			String strrOrderNo = rootElement.getAttribute("OrderNo");
			String strEnterpriseCode = rootElement.getAttribute("EnterpriseCode");
			 int noOfStatus=0;
			Document getOrderLineStatusListInput = XMLUtil.createDocument("OrderLineStatus");
			Element statusLineEle = getOrderLineStatusListInput.getDocumentElement();
			 if(orderHeaderKey ==null || "".equalsIgnoreCase(orderHeaderKey)) {
				 statusLineEle.setAttribute("OrderNo", strrOrderNo); 
				 statusLineEle.setAttribute("EnterpriseCode", strEnterpriseCode);
			 } else{
				 statusLineEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			 }
			 Document getOrderLineStatusListOutput = VSIUtils.invokeAPI(env, "getOrderLineStatusList", getOrderLineStatusListInput);
			 if(getOrderLineStatusListOutput != null){
			 NodeList statusNL = getOrderLineStatusListOutput.getElementsByTagName("OrderStatus");
			  noOfStatus = statusNL.getLength();
			  
			  
			 
//			 for(int k=0; k < noOfStatus; k++){
//				 Element statusEle = (Element) statusNL.item(k);
//				 String strStatus = statusEle.getAttribute("Status");
//				 if(!(strStatus.equalsIgnoreCase("9000"))&&!(strStatus.equalsIgnoreCase("9000.200"))
//						 &&!(strStatus.equalsIgnoreCase("9000.100"))){
//					 strUnCommitFlag="N";
//				 }
//			 }
			 }
			 
			 //end for jira 589
			
			//end jira 590 */

			NodeList orderLineListinXML = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			Element orderLineEleCheck = (Element) orderLineListinXML.item(0);
			int orderLineLen= orderLineListinXML.getLength();
			String emailType = null;
			String messageType = null;
			String modReasonCode = rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE);
			String modReasonText = rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT);
            if((YFCObject.isVoid(modReasonText))){
            	rootElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, modReasonCode);
            }
           if (modReasonText == null){
        	   rootElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, modReasonCode);
           }
           if(log.isDebugEnabled()){
        	   log.info("modReasonCode: "+modReasonCode);
           }
			if(AUTOCANCEL.equalsIgnoreCase(modReasonCode)) {
				//env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSICancelEmailTemplate.xml");//Template TBD
				//api = YIFClientFactory.getInstance().getApi();
				Document outDoc = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORD_LIST_VSI_CNCL_EMAIL, API_GET_ORDER_LIST,inXML);
				//env.clearApiTemplates();	

				if(!YFCObject.isVoid(outDoc)){
					outDoc.getDocumentElement().setAttribute(ATTR_EMAIL_TYPE, NO_INV_CANCEL);
					outDoc.getDocumentElement().setAttribute(ATTR_MESSAGE_TYPE, AUTOCANCEL);
					//api = YIFClientFactory.getInstance().getApi();
					//api.executeFlow(env, "VSICancelPublishToJDA", outDoc);
					// ARE-432 : Moving publishing JDA cancel message to ORDER_CHANGE.ON_CANCEL event : BEGIN
					//VSIUtils.invokeService(env, VSI_CNCL_PUBLISH_TO_JDA, inXML);
					env.setTxnObject(JDA_CNCL_MSG, inXML);
					// ARE-432 : Moving publishing JDA cancel message to ORDER_CHANGE.ON_CANCEL event : END
				}
			} else {
				String modeReasontext = "";
				if(rootElement.hasAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT))
					{
					modeReasontext = rootElement.getAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT);
					}
				else{
					rootElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_TEXT, modReasonCode);
				}
				String strActionOrder = 	rootElement.getAttribute(VSIConstants.ATTR_ACTION);
				String strActionOrderElement = null;
				if(orderLineEleCheck != null){
					strActionOrderElement = 	orderLineEleCheck.getAttribute(VSIConstants.ATTR_ACTION);
				}

				if(null!=modReasonCode && !"".equals(modReasonCode)){

					emailType = VSIUtils.getCommonCodeLongDescriptionByCodeValue(env, VSIConstants.ATTR_DEFAULT, modReasonCode);
					////System.out.println("Common Code Out:"+emailType);
				}
				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(ES) && modeReasontext != null && modeReasontext.equalsIgnoreCase("No Longer In Stock")){
					emailType = "CCM";
				}
				/*OMS-1730: Start*/
			if(modReasonCode !=null && ("VSI_CANCEL".equalsIgnoreCase(modReasonCode)||"VSI_CANCEL_NO_INV".equalsIgnoreCase(modReasonCode)) && !"Marketplace".equalsIgnoreCase(orderType)){
					emailType = "FTCBD";
				}
				if(modReasonCode !=null && ("VSI_CANCEL".equalsIgnoreCase(modReasonCode)||"VSI_CANCEL_NO_INV".equalsIgnoreCase(modReasonCode)) && "Marketplace".equalsIgnoreCase(orderType)){
					emailType = "FTC_CANCEL";
					messageType = "FTC_CANCEL";
				}
				/*OMS-1730: End*/	
				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(COULD_NOT_SHIP)){
					//OMS -957:Start 
					//updating the emailtype and messagetype from CouldNotShip to COULD_NOT_SHIP
					emailType = COULD_NOT_SHIP_EMAIL;
					messageType = COULD_NOT_SHIP_EMAIL;
					//OMS -957:End
				}
				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(SHIP_RESTRICTED)){
					emailType = "LITM";
				}
				if(modReasonCode !=null && (modReasonCode.equalsIgnoreCase(DISCONTINUED_ITEM) || modReasonCode.equalsIgnoreCase("InvalidSku"))){
					emailType = DISCONTINUED_ITEM;
					messageType = DISCONTINUED_ITEM;
				}
				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(ES) && modeReasontext !=null && modeReasontext.equalsIgnoreCase("Customer Changed Mind")){
					emailType = "CCM";
				}else if( modReasonCode.contains(ES) || modeReasontext.equalsIgnoreCase("ES-Customer Changed Mind") ){
					emailType = "CCM";
				}

				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(POS_ENTRY_TYPE) && modeReasontext !=null && modeReasontext.equalsIgnoreCase("Restock")){
					emailType = "LITM";
				}
				if(modReasonCode !=null && modReasonCode.equalsIgnoreCase(STS_BO_CANCEL)){
					emailType = "LITM";
				}
				if(modReasonCode !=null && (modReasonCode.equalsIgnoreCase(AUTOCANCEL) || modReasonCode.equalsIgnoreCase("JDA") || modReasonCode.equalsIgnoreCase("AutoCancel") ) ){
					emailType = "LITM";
				}
				if((modReasonCode !=null && modReasonCode.equalsIgnoreCase(AUTH_CANCEL_ORDER)) || (modeReasontext !=null && modeReasontext.equalsIgnoreCase(AUTH_CANCEL_ORDER))){
					emailType = "FAFSI";
				}
				if(((modReasonCode !=null && modeReasontext.equalsIgnoreCase("Fraud Check Declined"))|| (modeReasontext !=null && modeReasontext.equalsIgnoreCase("Fraud Check Declined")))){
					emailType = "FAFSI";
				}
				//OMS-2290 START
				if((modReasonCode !=null && modReasonCode.equalsIgnoreCase("DeclinePayment BillToAddress Mismatch")) || (modeReasontext !=null && modeReasontext.equalsIgnoreCase("DeclinePayment BillToAddress Mismatch"))){
					emailType = "FAFSI";
				}
				//OMS-2290 END
				if( modReasonCode.equalsIgnoreCase("Customer Changed Mind") || modReasonCode.equalsIgnoreCase("Cancel and Substitute") || modReasonCode.equalsIgnoreCase("Duplicate Item/Order") || modReasonCode.equalsIgnoreCase("Wrong Item") || modReasonCode.equalsIgnoreCase("Wrong Store")  ){
					emailType = "CCM";
				}
				if( modReasonCode.equalsIgnoreCase(MIS_SHIP)){
					emailType = "LITM";
				}
				if((modReasonCode !=null && modReasonCode.equalsIgnoreCase(NO_STORE_PICK))){
					emailType = "LITM";
					//emailType = NO_STORE_PICK;
				}
				if((modReasonCode !=null && modReasonCode.equalsIgnoreCase(NO_CUSTOMER_PICK))){
					emailType = "NCP";
				}
				// Fix for OMS-737
				if(modReasonCode.equalsIgnoreCase("Missed Delivery Date")){
					emailType = "LITM";
				}
				
				if(modReasonCode.equalsIgnoreCase(LOST_IN_TRANSIT)){
					emailType = "LITM";
				}
			
				if((null!=strActionOrder && strActionOrder.equalsIgnoreCase(ACTION_CAPS_CANCEL))|| (null!=strActionOrderElement && strActionOrderElement.equalsIgnoreCase(ACTION_CAPS_CANCEL))){

					String sOHK = 	rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
//					Element eleOrderLineInput = (Element)rootElement.getElementsByTagName("OrderLine").item(0);
//					String strCustPONo = "";
//					if(!YFCObject.isVoid(eleOrderLineInput)){
//					eleOrderLineInput.getAttribute("");
//					}
					String strOrderNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);


					Document getOrderListInput = XMLUtil.createDocument(ELE_ORDER);
					Element eleOrder = getOrderListInput.getDocumentElement();

					if(!YFCObject.isVoid(sOHK)){
						eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, sOHK);

					}
					else if(!YFCObject.isVoid(strOrderNo)){

						Element eleOrderLine = getOrderListInput.createElement(ELE_ORDER_LINE);
						eleOrderLine.setAttribute(ATTR_CUSTOMER_PO_NO, strOrderNo);
						//getOrderListInput.appendChild(eleOrderLine);
						eleOrder.appendChild(eleOrderLine);
					}

					Boolean lineCancelFlag = false;
					Boolean publishFlag = false;

					for(int i =0;i<orderLineLen;i++){

						Element orderLineElement = (Element)orderLineListinXML.item(i);


						String strActionOrderLine = 	orderLineElement.getAttribute(VSIConstants.ATTR_ACTION);

						if(strActionOrderLine.equalsIgnoreCase(ACTION_CAPS_CANCEL)){

							lineCancelFlag = true;
							String OLKinXML = 	orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
							orderLinekeyMap.put(OLKinXML, FLAG_Y);


						}
					}// end for i
					
					String strActionFromInputDoc = rootElement.getAttribute(VSIConstants.ATTR_ACTION);
					
					 // "CANCEL".equalsIgnoreCase(rootElement.getAttribute(VSIConstants.ATTR_ACTION)) condition has been added to publishing  auth cancel emails.
					//For Jira 569
					if(lineCancelFlag || ACTION_CAPS_CANCEL.equalsIgnoreCase(strActionFromInputDoc)){

						//env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, TEMPLATE_GET_ORD_LIST_VSI_CNCL_EMAIL);//Template TBD
						//api = YIFClientFactory.getInstance().getApi();
						Document outDoc = VSIUtils.invokeAPI(env, TEMPLATE_GET_ORD_LIST_VSI_CNCL_EMAIL, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
						//env.clearApiTemplates();	
						
						Element eleOrderOut = (Element)outDoc.getElementsByTagName(ELE_ORDER).item(0);
						//For Jira 589
						//eleOrderOut.setAttribute("Uncommit", strUnCommitFlag);
						//For Jira 589
						String strOrderHeaderKey = eleOrderOut.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
						if(YFCObject.isVoid(sOHK)){
							rootElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
						}
						Element eleOrderLines = (Element)outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINES).item(0);
						NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						int linelength= orderLineList.getLength();
						//For uncommit coupon. Setting a  uncommit flag for IIB to determine.
						if (null != orderLineList){
							for(int i=0;i<linelength;i++){
						Element orderLineElement = (Element)orderLineList.item(i);
						String orderLineKey=orderLineElement.getAttribute("OrderLineKey");
						String status=orderLineElement.getAttribute("Status");
						if(!vcOrderLineStatus.containsKey(orderLineKey)){
							vcOrderLineStatus.put(orderLineKey, status);
						}
						}
						}
						
						if(!ACTION_CAPS_CANCEL.equalsIgnoreCase(strActionFromInputDoc))
						{
							for(String Key : vcOrderLineStatus.keySet()){
								String status=vcOrderLineStatus.get(Key);
									 if (!status.equalsIgnoreCase("Cancelled") && !status.equalsIgnoreCase("Restock") && !status.equalsIgnoreCase("Restock In Transit")){
										 strUnCommitFlag="N";
										 break;
									 }
									 
								}
						}
						eleOrderOut.setAttribute("Uncommit", strUnCommitFlag);

						//End of:For uncommit coupon. Setting a  uncommit flag for IIB to determine.
						
						// Condition has been added to publishing  auth cancel emails.
						//For Jira 569
					if (0>= orderLineLen   && "CANCEL".equalsIgnoreCase(strActionFromInputDoc)) {
							
							publishFlag=true;
						} else if (null != orderLineList){
							for(int i=0;i<linelength;i++){

								Element orderLineElement = (Element)orderLineList.item(i);

								String strOrderLinekey = orderLineElement.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
								
								if(!orderLinekeyMap.containsKey(strOrderLinekey)){
									eleOrderLines.removeChild(orderLineElement);
									linelength--;
									i--;
								}else{
									publishFlag=true;
									orderLineElement.setAttribute("Publish", "Y"); 
									double dblOrderedQty = SCXmlUtil.getDoubleAttribute(orderLineElement, ATTR_ORD_QTY);
									double dblInpOrderedQty = 0;
									if(!YFCCommon.isVoid(mapLineQty.get(orderLineElement.getAttribute(ATTR_ORDER_LINE_KEY))))
									{
										dblInpOrderedQty = (double)mapLineQty.get(orderLineElement.getAttribute(ATTR_ORDER_LINE_KEY));
										if(YFCDoubleUtils.lessThan(dblInpOrderedQty,dblOrderedQty))
										{
											orderLineElement.setAttribute(ATTR_ORD_QTY, String.valueOf(Double.valueOf((dblOrderedQty-dblInpOrderedQty)).intValue()));
										}
									}
									
									
								}
							}
						}
						
						Element rootOutDoc= outDoc.getDocumentElement();
						Element eleRootOrder = SCXmlUtil.getChildElement(rootOutDoc, ELE_ORDER);
						rootOutDoc.setAttribute("Action", "CANCEL");
						if(null!=emailType) 
							rootOutDoc.setAttribute("EmailType", emailType);
						if(null!=messageType) {
							rootOutDoc.setAttribute("MessageType", messageType);
							eleRootOrder.setAttribute("MessageType", messageType);
						}

						XMLUtil.copyElement(outDoc, rootElement, rootOutDoc);
						NodeList orderLineListFinal = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
						int orderLineLength = orderLineListFinal.getLength();
						
					 for(int j=0;j< orderLineLength;j++){
							Element orderLineEle = (Element) orderLineListFinal.item(j);
							if(!YFCObject.isVoid(orderLineEle))
								publishFlag = true;
							orderLineEle.setAttribute("Publish", "Y");
						}
                   

						/*String minLineStatus = 	orderLineEle.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
					Double dMinLine = Double.parseDouble(minLineStatus);

					if(dMinLine>=1500){

						String OLKOutDoc = 	orderLineEle.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);



						if(orderLinekeyMap.containsValue(OLKOutDoc)){

							publishFlag=true;
							orderLineEle.setAttribute("IsCancel", "Y");

						}


					}


					}*/
					 sOrderType = eleRootOrder.getAttribute(ATTR_ORDER_TYPE);
					 sEntryType = eleRootOrder.getAttribute(ATTR_ENTRY_TYPE);
						if(publishFlag
								//Added condition to suppress msg for marketplace customer cancellations
								){
							
							/**START OMS-792**/
							if (!YFCObject.isVoid(env.getTxnObject("EnvCancelQty"))) {
								outDoc = setEmailQty(env, outDoc);
								//System.out.println("OMS-792 - Set EmailQty for Cancel Email");		
							}
							/**END OMS-792**/							
							
							//api = YIFClientFactory.getInstance().getApi();
							// TODO: suppress this message for marketplace customer cancellations
							// ARE-432 : Moving publishing JDA cancel message to ORDER_CHANGE.ON_CANCEL event : BEGIN
							//VSIUtils.invokeService(env, VSI_CNCL_PUBLISH_TO_JDA, outDoc);
							env.setTxnObject(JDA_CNCL_MSG, outDoc);
							// ARE-432 : Moving publishing JDA cancel message to ORDER_CHANGE.ON_CANCEL event : END
						}
					}else{
						Element ordExtnEle = (Element) rootElement.getElementsByTagName("Extn").item(0);
						if(ordExtnEle != null){
							NodeList orderToBNL = ordExtnEle.getElementsByTagName("VSIOrderTOB");
							if(orderToBNL.getLength() > 0){
								Element orderToBEle = (Element) orderToBNL.item(0);
								if(orderToBEle != null){
									String extnToBOrdKey = orderToBEle.getAttribute("ExtnVSIOrderTobKey");
									if(extnToBOrdKey != null  && !extnToBOrdKey.equalsIgnoreCase("")){
										orderToBEle.setAttribute("IsTobDirty", "Y");
									}
								}
							}
						}		
					}
//					}
				}
			}

		}catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
			"Order No. Not found.");
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
			"Exception");

		}
		//System.out.println("OUTPUT_FROM_CLASS: "+SCXmlUtil.getString(inXML));
		return inXML;

		

	}

	/**OMS-792**/
	public Document setEmailQty(YFSEnvironment env, Document outDoc) {
			
		Map <String, String> object = (Map<String, String>) env.getTxnObject("EnvCancelQty");
		//System.out.println(XMLUtil.getXMLString(outDoc));
		
		if (!YFCObject.isVoid(object)) {
			
			NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			for (int i = 0; i < orderLineList.getLength(); i++) 
			{
				Element orderLineEle = (Element) orderLineList.item(i);
				String sOrderedQty = orderLineEle.getAttribute("OrderedQty");
				Element itemEle = (Element) orderLineEle.getElementsByTagName("Item").item(0);
				if (null != itemEle) {
					String sItem = itemEle.getAttribute(VSIConstants.ATTR_ITEM_ID);
					String sQty = "";
					if (!YFCObject.isVoid(object.get(sItem))) {
						sQty = object.get(sItem).toString();
						
						if (Integer.parseInt(sOrderedQty) < Integer.parseInt(sQty))	{
							orderLineEle.setAttribute("EmailQty", sOrderedQty);
							object.put(sItem, Integer.toString(Integer.parseInt(sQty) - Integer.parseInt(sOrderedQty)));
						}
						else {
							if (!"0".equals(sQty)) {
								orderLineEle.setAttribute("EmailQty", sQty);
								object.put(sItem, "0");
							}
						}
					}
				}
	
			}
			env.setTxnObject("EnvCancelQty","");
		}
		
		return outDoc;
	}
	
	/**
	 * This method is used to update the PersonInfoShipTo for the line if DeliveryMethod = 'PICK'
	 * 
	 * Fixes the issue reported in SU-26
	 * 
	 * @param env
	 * @param nlOrderLine
	 * @return
	 */
	public Document updateShipNodePersonInfo(YFSEnvironment env,
			Document inXML) {

		//System.out.println("INSIDE_updateShipNodePersonInfo: "+SCXmlUtil.getString(inXML));
		NodeList nlOrderLine = inXML.getElementsByTagName("OrderLine");
		Element elShipNodePersoninfo = null;
		Document getShipNodeListOutput = null;
		Element elGetShipNodeList = null;

		for (int i = 0; i < nlOrderLine.getLength(); i++) {
			Element elOrderLine = (Element) nlOrderLine.item(i);
			Element elOLPersonInfoShipTo = (Element) elOrderLine
					.getElementsByTagName("PersonInfoShipTo").item(0);
			String strShipNode = elOrderLine.getAttribute("ShipNode");
			String strDeliveryMethod = elOrderLine
					.getAttribute("DeliveryMethod");
			if ("PICK".equals(strDeliveryMethod)
					&& !YFCObject.isVoid(strShipNode)
					&& YFCObject.isVoid(elOLPersonInfoShipTo)) {
				//System.out.println("PICK_IN_STORE_LINE");
				if(!storeDetails.containsKey(strShipNode)){
					//System.out.println("FETCHING_SHIPNODE_DETAILS");
					Document getShipNodeListInput = SCXmlUtil
							.createFromString("<ShipNode ShipnodeKey='"
									+ strShipNode + "'/>");
					try {
						getShipNodeListOutput = VSIUtils
								.invokeAPI(
										env,
										VSIConstants.TEMPLATE_GET_SHIP_NODE_LIST_BEFORECHANGEORDERUE,
										"getShipNodeList", getShipNodeListInput);
						elGetShipNodeList = getShipNodeListOutput
								.getDocumentElement();
						elShipNodePersoninfo = (Element) elGetShipNodeList
								.getElementsByTagName("ShipNodePersonInfo").item(0);
					} catch (YFSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (YIFClientCreationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					elShipNodePersoninfo = storeDetails.get(strShipNode);
				}
				
				NamedNodeMap personInfoElemAttrs = elShipNodePersoninfo
						.getAttributes();
				elOLPersonInfoShipTo = SCXmlUtil.createChild(elOrderLine, "PersonInfoShipTo");
				for (int h = 0; h < personInfoElemAttrs.getLength(); h++) {
					Attr a1 = (Attr) personInfoElemAttrs.item(h);
					elOLPersonInfoShipTo.setAttribute(a1.getName(),
							a1.getValue());
				}
				storeDetails.put(strShipNode, elShipNodePersoninfo);
			}
			//System.out.println("OUTPUT_RETURNED: "+SCXmlUtil.getString(inXML));
		}
		return inXML;
	}	
	//OMS-1333: Start
	/**
	 * This method converts the country code 3 characters to 2 characters.
	 * 
	 * @param env
	 * @param strCountry
	 * @return
	 */
	
	private String getCountryCode(YFSEnvironment env,String strCountry){
		
		Element eleCommonCodeOutput=null;
		try {
			if(!YFCCommon.isStringVoid(strCountry)){
				Document docInputgetCommonCodeList=XMLUtil.createDocument(ELE_COMMON_CODE);
				Element eleCommonCode = docInputgetCommonCodeList.getDocumentElement();
				eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_COUNTRYCODE_MAP");
				eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION, strCountry);

				if(log.isDebugEnabled()){
					log.debug("Input for getCommonCodeList :VSI_COUNTRYCODE_MAP:" +XMLUtil.getXMLString(docInputgetCommonCodeList));
				}
				Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInputgetCommonCodeList);
				NodeList nlCommonCode = docCommonCodeListOP
						.getElementsByTagName(ELE_COMMON_CODE);

				if(nlCommonCode!=null && nlCommonCode.getLength()>0){
					eleCommonCodeOutput=((Element) nlCommonCode.item(0));
					if(log.isDebugEnabled()){
						log.debug("output is  : "+XMLUtil.getElementString(eleCommonCodeOutput));
					}
					strCountry=eleCommonCodeOutput.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				}
			}

		} catch (Exception e) {
			// TODO: handle exception
			log.error(e);
		}
		
		return strCountry;
	}
	//OMS-1333: End
	
	//OMS- 1137 : Stamp Reservation : Start
	private void stampOrderLineReservation(YFSEnvironment env,Document inXML,Document docOrderDetails){
		if(log.isDebugEnabled()){
			log.debug("Input for stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
		}
		try {
			Element eleOrder=docOrderDetails.getDocumentElement();
			String strDraftOrderFlag=eleOrder.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
			if(log.isDebugEnabled()){
				log.debug("DraftOrderFlag : "+strDraftOrderFlag);
			}
			//if order is not a draft order then add line level reservation and add inventory activity
			if(!YFCCommon.isStringVoid(strDraftOrderFlag) && "N".equals(strDraftOrderFlag) ){
				if(log.isDebugEnabled()){
					log.debug("Order is not a draft order");
				}
				String strOrganizationCode=eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
				String strAllocationRuleID=eleOrder.getAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID);
				
				Element elePersonInfoShipTo=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
				
				//create input for createInventoryActivityList
				Document docInventoryActivityListIP=SCXmlUtil.createDocument("InventoryActivityList");
				Element eleInventoryActivityList=docInventoryActivityListIP.getDocumentElement();
				
				NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int iLineCount=nlOrderLine.getLength();
				for (int i = 0; i < iLineCount; i++) {
					Element eleOrderLine=(Element)nlOrderLine.item(i);
					String strTransactionalLineId=eleOrderLine.getAttribute("TransactionalLineId");
					if(!YFCCommon.isStringVoid(strTransactionalLineId)){
						if(log.isDebugEnabled()){
							log.debug("Line is newly created, reserving the inventory");
						}
						Element eleOrderLineReservations=SCXmlUtil.createChild(eleOrderLine, "OrderLineReservations");
						String strDeliveryMethod=eleOrderLine.getAttribute("DeliveryMethod");
						if(!YFCCommon.isStringVoid(strDeliveryMethod)&& "SHP".equals(strDeliveryMethod)){
							Document docReserveInvOP=reserveAvailableInvetory(env,eleOrderLine,elePersonInfoShipTo,strOrganizationCode,
									strAllocationRuleID);
							NodeList nlReservation=docReserveInvOP.getElementsByTagName("Reservation");
							int iReservationCount=nlReservation.getLength();
							for (int j = 0; j < iReservationCount; j++) {
								Element eleOrderLineReservation=SCXmlUtil.createChild(eleOrderLineReservations, "OrderLineReservation");
								Element eleReservation=(Element)nlReservation.item(j);
								eleOrderLineReservation.setAttribute("ReservationID", eleReservation.getAttribute("ReservationID"));
								eleOrderLineReservation.setAttribute("ItemID", eleReservation.getAttribute("ItemID"));
								eleOrderLineReservation.setAttribute("ProductClass", eleReservation.getAttribute("ProductClass"));
								eleOrderLineReservation.setAttribute("UnitOfMeasure", eleReservation.getAttribute("UnitOfMeasure"));
								eleOrderLineReservation.setAttribute("Quantity", eleReservation.getAttribute("ReservedQty"));
								eleOrderLineReservation.setAttribute("Node", eleReservation.getAttribute("ShipNode"));
								eleOrderLineReservation.setAttribute("RequestedReservationDate", eleReservation.getAttribute("ReservationNodeShipDate"));
							}
							//creating inventory activity entry for newly created item
							Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
							String strItemID=eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
							String strProductClass=eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
							String strUnitOfMeasure=eleItem.getAttribute(VSIConstants.ATTR_UOM);
							Element eleInventoryActivity=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
							eleInventoryActivity.setAttribute("CreateForInvItemsAtNode", "N");
							eleInventoryActivity.setAttribute("ItemID", strItemID);
							eleInventoryActivity.setAttribute("OrganizationCode", "ADP");
							eleInventoryActivity.setAttribute("ProductClass", strProductClass);
							eleInventoryActivity.setAttribute("UnitOfMeasure", strUnitOfMeasure);
							
							Element eleInventoryActivity1=SCXmlUtil.createChild(eleInventoryActivityList, "InventoryActivity");
							eleInventoryActivity1.setAttribute("CreateForInvItemsAtNode", "N");
							eleInventoryActivity1.setAttribute("ItemID", strItemID);
							eleInventoryActivity1.setAttribute("OrganizationCode", "MCL");
							eleInventoryActivity1.setAttribute("ProductClass", strProductClass);
							eleInventoryActivity1.setAttribute("UnitOfMeasure", strUnitOfMeasure);
						}
					}
					
				}
				
				if(log.isDebugEnabled()){
					log.debug("Input for createInventoryActivityList:  "+SCXmlUtil.getString(docInventoryActivityListIP));
				}
				if(eleInventoryActivityList.hasChildNodes()) {
					VSIUtils.invokeAPI(env, "createInventoryActivityList", docInventoryActivityListIP);
				}
			}
			
			if(log.isDebugEnabled()){
				log.debug("output from  stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private Document reserveAvailableInvetory(YFSEnvironment env,Element eleOrderLine,
			Element elePersonInfoShipTo,String strOrganizationCode, String strAllocationRuleID){
		Document docReserveInvIP=null;
		Document docReserveInvOP=null;
		try {
			docReserveInvIP=SCXmlUtil.createDocument("Promise");
			Element elePromise=docReserveInvIP.getDocumentElement();
			elePromise.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strOrganizationCode);
			if(!YFCCommon.isStringVoid(strAllocationRuleID)){
				elePromise.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, strAllocationRuleID);
			}
			Element eleReservationParameters=SCXmlUtil.createChild(elePromise, "ReservationParameters");
			
			String strSeqName = "VSI_SEQ_RESERVATION_ID";
	    	String strSeqNumber = VSIDBUtil.getNextSequence(env, strSeqName);
	    	eleReservationParameters.setAttribute("ReservationID", strSeqNumber);
	    	
	    	Element elePromiseLines=SCXmlUtil.createChild(elePromise, "PromiseLines");
	    	Element elePromiseLine=SCXmlUtil.createChild(elePromiseLines, "PromiseLine");
	    	
	    	Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
	    	elePromiseLine.setAttribute("LineId", "1");
	    	elePromiseLine.setAttribute("ItemID", eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID));
	    	Element eleOrderLineTranQuantity=SCXmlUtil.getChildElement(eleOrderLine, "OrderLineTranQuantity");
	    	String strOrderedQty="";
	    	if(!YFCCommon.isVoid(eleOrderLineTranQuantity)){
	    		strOrderedQty=eleOrderLineTranQuantity.getAttribute("OrderedQty");
	    	}
	    	if(!YFCCommon.isStringVoid(strOrderedQty))
	    		elePromiseLine.setAttribute("RequiredQty", strOrderedQty);
	    	else
	    		elePromiseLine.setAttribute("RequiredQty", eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY));
	    	
	    	elePromiseLine.setAttribute("UnitOfMeasure", eleItem.getAttribute(VSIConstants.ATTR_UOM));
	    	elePromiseLine.setAttribute("ProductClass", eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS));
	    	//elePromiseLine.setAttribute("DistributionRuleId", "VSI_DC");
	    	elePromiseLine.setAttribute("FulfillmentType", "SHIP_TO_HOME");
	    	//elePromiseLine.setAttribute("FulfillmentType", eleOrderLine.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE));
	    	elePromiseLine.setAttribute("DeliveryMethod", eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD));
	    	String strShipNode=eleOrderLine.getAttribute(VSIConstants.ATTR_SHIP_NODE);
	    	if(!YFCCommon.isStringVoid(strShipNode))
	    		elePromiseLine.setAttribute(VSIConstants.ATTR_SHIP_NODE, strShipNode);
	    	
	    	Element eleShipToAddress=SCXmlUtil.createChild(elePromise, "ShipToAddress");
	    	eleShipToAddress.setAttribute("AddressLine1",  elePersonInfoShipTo.getAttribute("AddressLine1"));
	    	eleShipToAddress.setAttribute("City",  elePersonInfoShipTo.getAttribute("City"));
	    	eleShipToAddress.setAttribute("Country",  elePersonInfoShipTo.getAttribute("Country"));
	    	eleShipToAddress.setAttribute("DayPhone",  elePersonInfoShipTo.getAttribute("DayPhone"));
	    	eleShipToAddress.setAttribute("FirstName",  elePersonInfoShipTo.getAttribute("FirstName"));
	    	eleShipToAddress.setAttribute("LastName",  elePersonInfoShipTo.getAttribute("LastName"));
	    	eleShipToAddress.setAttribute("State",  elePersonInfoShipTo.getAttribute("State"));
	    	eleShipToAddress.setAttribute("ZipCode", elePersonInfoShipTo.getAttribute("ZipCode"));
	    	
	    	if(log.isDebugEnabled()){
	    		log.debug("input for reserveAvailableInventory API : "+SCXmlUtil.getString(docReserveInvIP));
	    	}
	    	
	    	//invoking reserveAvailableInventory 
	    	docReserveInvOP=VSIUtils.invokeAPI(env, "reserveAvailableInventory", docReserveInvIP);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(log.isDebugEnabled()){
			log.debug("reserveAvailableInvetory output : "+SCXmlUtil.getString(docReserveInvOP));
		}
		return docReserveInvOP;
	}
	
	//OMS-1137 : Stamp Reservation : End
}