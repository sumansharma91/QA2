package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIDBUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 * This code is called on success of ReleaseOrder. 
 * For BOPUS & Ship to home
 * 1. create Charge Transaction request
 * 
 * For BOSTS
 * 1. Check if hold status is AUTH_PENDING, if no
 * 	a. Call changeorder status to change to "NoAction"
 * 	b. Send an email to the customer
 */
public class VSIAwaitPickupSTS {

	private YFCLogCategory log = YFCLogCategory.instance(VSIAwaitPickupSTS.class);
	YIFApi api;
	public void vsiAwaitPickupSTS(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
					
		
		Element rootElement = inXML.getDocumentElement();
		String OHK = rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		NodeList holdList = inXML.getElementsByTagName("OrderHoldType");
String attPaymentRuleID = rootElement.getAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID);
		NodeList hdrChrsNL = rootElement.getElementsByTagName("HeaderCharge");
		NodeList hdrTaxesNL = rootElement.getElementsByTagName("HeaderTax");

		

		Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
		Element changeOrderStatusElement = changeOrderStatusInput.getDocumentElement();
		changeOrderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
		//changeOrderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.500");
		Element OrderLinesElement =	changeOrderStatusInput.createElement(VSIConstants.ELE_ORDER_LINES);
		changeOrderStatusElement.appendChild(OrderLinesElement);
		
		// for PICK_IN_STORE (post 1B)
		Document changeOrderStatusInput1 = XMLUtil.createDocument("OrderStatusChange");
		Element changeOrderStatusElement1 = changeOrderStatusInput1.getDocumentElement();
		changeOrderStatusElement1.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
		//changeOrderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.500");
		Element OrderLinesElement1 =	changeOrderStatusInput1.createElement(VSIConstants.ELE_ORDER_LINES);
		changeOrderStatusElement1.appendChild(OrderLinesElement1);
		
		String custPoNo="";
			
		NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int linelength= orderLineList.getLength();
		int validShipLines= 0;
		int validPickLines = 0;
		int validHomeLines = 0;
		Double maxReqAmt=0.0;
		Boolean processSTSFlag=true;
		for(int i=0;i<linelength;i++){
			
			Element orderLineEle1 = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			
			//String strOrderLineKey = orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			
			String minLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
			String maxLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
			String lineType= orderLineEle1.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			if(minLineStatus.equals("3200") && maxLineStatus.equals("3200") && lineType.equalsIgnoreCase("SHIP_TO_STORE") ){
				
				String sQty = orderLineEle1.getAttribute("OrderedQty");
				Element eleOrderLine = changeOrderStatusInput.createElement(VSIConstants.ELE_ORDER_LINE);
						String OLK =orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
						eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, OLK);
						eleOrderLine.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.401");
						eleOrderLine.setAttribute(VSIConstants.ATTR_QUANTITY, sQty);
						eleOrderLine.setAttribute("ChangeForAllAvailableQty", "Y");
						OrderLinesElement.appendChild(eleOrderLine);
						
				if(null!=holdList){
						int holdLength= holdList.getLength();
						
						for(int j=0;j<holdLength;j++){
							
						
						Element orderHoldEle = (Element) inXML.getElementsByTagName("OrderHoldType").item(j);
						String holdType = orderHoldEle.getAttribute(VSIConstants.ATTR_HOLD_TYPE);
						String holdStatus = orderHoldEle.getAttribute(VSIConstants.ATTR_STATUS);
						if(holdType.equalsIgnoreCase("AUTH_PENDING") && holdStatus.equalsIgnoreCase("1100")){
							processSTSFlag=false;
							
						 }//end holdType check
						}
					}//end null check
				
				
						validShipLines++;
	
			}
			else if (minLineStatus.equals("3200") && maxLineStatus.equals("3200") && lineType.equalsIgnoreCase("PICK_IN_STORE")){
				
				Element linePriceInfoEle = (Element) orderLineEle1.getElementsByTagName("LinePriceInfo").item(0);
				String strlineTotal = linePriceInfoEle.getAttribute("LineTotal");
				Double dLineTotal = Double.parseDouble(strlineTotal);
				maxReqAmt=maxReqAmt+dLineTotal;
				
				custPoNo = orderLineEle1.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
				String sQty = orderLineEle1.getAttribute(VSIConstants.ATTR_QUANTITY);
				Element eleOrderLine = changeOrderStatusInput1.createElement(VSIConstants.ELE_ORDER_LINE);
				String OLK =orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
				eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, OLK);
				eleOrderLine.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.500");
				eleOrderLine.setAttribute(VSIConstants.ATTR_QUANTITY, sQty);
				eleOrderLine.setAttribute("ChangeForAllAvailableQty", "Y");
				OrderLinesElement1.appendChild(eleOrderLine);
						
				validPickLines++;
				
				
			}
			
			else if (minLineStatus.equals("3200") && maxLineStatus.equals("3200") && lineType.equalsIgnoreCase("SHIP_TO_HOME")){
				
				Element linePriceInfoEle = (Element) orderLineEle1.getElementsByTagName("LinePriceInfo").item(0);
				String strlineTotal = linePriceInfoEle.getAttribute("LineTotal");
				Double dLineTotal = Double.parseDouble(strlineTotal);
				maxReqAmt=maxReqAmt+dLineTotal;
				
				custPoNo = orderLineEle1.getAttribute(VSIConstants.ATTR_CUST_PO_NO);		
				validHomeLines++;
				
				
			}
			
			
			}

		////System.out.println("ChangeOrderStatusInput:"+XMLUtil.getXMLString(changeOrderStatusInput));
		
		if(validShipLines>0 && processSTSFlag==true){
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "STSNoAction.0001.ex");
			changeOrderStatusElement.setAttribute(VSIConstants.ATTR_MODIFICATION_CODE, "STS No Action");
			
			
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
		//api.executeFlow(env, VSIConstants.SERVICE_AWAIT_CUST_PICKUP, inXML);//Sending this to the service for email to customer
	
		}
		
	
		else if(validPickLines>0 && "DEFAULT".equalsIgnoreCase(attPaymentRuleID)){
			
			
			createCallCTR(env,OHK,custPoNo,maxReqAmt);
			//api = YIFClientFactory.getInstance().getApi();
			//api.executeFlow(env, VSIConstants.SERVICE_AWAIT_CUST_PICKUP, inXML);//Sending this to the service for email to customer
			
		}
		else if (validPickLines>0 && !YFCObject.isVoid(attPaymentRuleID) && !"DEFAULT".equalsIgnoreCase(attPaymentRuleID))
		{
			changeOrderStatusElement1.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");		
			changeOrderStatusElement1.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "AwaitingCustomerPickup.0001.ex");
			changeOrderStatusElement1.setAttribute(VSIConstants.ATTR_MODIFICATION_CODE, "Awaiting Customer Pickup");
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput1);
		}
		
else if(validHomeLines>0 && !YFCObject.isVoid(attPaymentRuleID) && "DEFAULT".equalsIgnoreCase(attPaymentRuleID)){

			
			Double dshippingChrg = 0.0;
			Double dheaderTaxes = 0.0;
			for(int m=0; m < hdrChrsNL.getLength(); m++ ){
				Element hdrChrEle = (Element) hdrChrsNL.item(m);
				if(!YFCObject.isVoid(hdrChrEle)){
					String strCharge = hdrChrEle.getAttribute("ChargeAmount");
					if(!YFCObject.isVoid(strCharge))
						dshippingChrg = dshippingChrg + Double.parseDouble(strCharge);
				}
			}
			
			for(int n = 0; n < hdrTaxesNL.getLength(); n++ ){
				Element hdrTaxEle = (Element) hdrTaxesNL.item(n);
				if(!YFCObject.isVoid(hdrTaxEle)){
					String strTax = hdrTaxEle.getAttribute("Tax");
					if(!YFCObject.isVoid(strTax))
						dheaderTaxes = dheaderTaxes + Double.parseDouble(strTax);
				}
			}
			
			
			maxReqAmt = maxReqAmt + dshippingChrg  + dheaderTaxes;
			createCallCTR(env,OHK,custPoNo,maxReqAmt);	
			
			//api = YIFClientFactory.getInstance().getApi();
			//api.executeFlow(env, VSIConstants.SERVICE_AWAIT_CUST_PICKUP, inXML);//Sending this to the service for email to customer
			
			}
		
		
		
		
	}
	private void createCallCTR(YFSEnvironment env, String oHK, String custPoNo, Double maxReqAmt) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {

		try{
		String maxRequestAmount = String.valueOf(maxReqAmt);
	    String ctrSeqId = VSIDBUtil.getNextSequence(env,VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
		Document chargeTransactionRequestInput = XMLUtil.createDocument("ChargeTransactionRequestList");
		Element ctrListEle = chargeTransactionRequestInput.getDocumentElement();
		ctrListEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		Element ctrEle =	chargeTransactionRequestInput.createElement("ChargeTransactionRequest");
		ctrListEle.appendChild(ctrEle);
		ctrEle.setAttribute("MaxRequestAmount", maxRequestAmount);
		ctrEle.setAttribute("ChargeTransactionRequestKey", custPoNo);
		ctrEle.setAttribute("ChargeTransactionRequestId", ChargeTransactionRequestId);
	
		////System.out.println("chargeTransactionRequestInput is ****:"+XMLUtil.getXMLString(chargeTransactionRequestInput));
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,chargeTransactionRequestInput);
		}
		catch (ParserConfigurationException e) {
                     e.printStackTrace();
              } catch (YIFClientCreationException e) {
                     e.printStackTrace();
              } catch (YFSException e) {
                     e.printStackTrace();
              } catch (RemoteException e) {
                     e.printStackTrace();
              } catch (Exception e) {
                     e.printStackTrace();
              }

		
		
	}
}//end class
