package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
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
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
//OMS-2510: Start
public class VSISendInvoice implements VSIConstants{
//OMS-2510: End
	private YFCLogCategory log = YFCLogCategory.instance(VSISendInvoice.class);
	YIFApi api;
	public Document vsiSendInvoice(YFSEnvironment env, Document inXML)
	throws Exception {
		
		String strShipNode="";
		String strCustomerOrderNo=null;
		String sDateInvoiced=null;
		String strLineType = null;
		Element invoiceHeaderele = (Element) inXML.getElementsByTagName("InvoiceHeader").item(0);
		Element eleLineDetails=(Element)invoiceHeaderele.getElementsByTagName("LineDetails").item(0);
		String sInvoiceType=invoiceHeaderele.getAttribute("InvoiceType");
		Element orderLineElement = (Element) inXML.getElementsByTagName("OrderLine").item(0);
		//OMS-2510: Start
		String strShipNodeKey=null;
		boolean isBossShipment=true;
		Element eleInvHeaderShipment = (Element) inXML.getElementsByTagName(ELE_SHIPMENT).item(0);
		Element eleShipNode = (Element) inXML.getElementsByTagName(ELE_SHIP_NODE).item(0);
		strShipNodeKey=eleShipNode.getAttribute(ATTR_SHIPNODE_KEY);
		if (VSI_VADC.equals(strShipNodeKey) || VSI_AZDC.equals(strShipNodeKey) )
		{
			isBossShipment=false;
		}
		//OMS-2510: End
		if (sInvoiceType.equalsIgnoreCase("CREDIT_MEMO") ||sInvoiceType.equalsIgnoreCase("INFO") || sInvoiceType.equalsIgnoreCase("DEBIT_MEMO")){
			if(!YFCObject.isVoid(orderLineElement)){
				strLineType = orderLineElement.getAttribute("LineType");
				strShipNode=orderLineElement.getAttribute("ShipNode");
				strCustomerOrderNo=orderLineElement.getAttribute("CustomerPONo");
			}

				sDateInvoiced=invoiceHeaderele.getAttribute("DateInvoiced");
				Element createShipmentEle = XMLUtil.appendChild(inXML, invoiceHeaderele, "Shipment", "");
				
				if(!YFCCommon.isVoid(strShipNode))
				{
					createShipmentEle.setAttribute("ShipNode", strShipNode);
				}
				createShipmentEle.setAttribute("ShipDate", sDateInvoiced);
				Element elePersonInfoBillTo = (Element) inXML.getElementsByTagName("PersonInfoBillTo").item(0);
				Element eleToAddress = XMLUtil.appendChild(inXML, createShipmentEle, "ToAddress", "");
				
				String reference1 = invoiceHeaderele.getAttribute("Reference1");
				if(!YFCCommon.isVoid(reference1) && reference1.contains("_"))
				{
					String[] reference1Array = reference1.split("_");
					invoiceHeaderele.setAttribute("Reference1",reference1Array[0]);
				}

				NamedNodeMap personInfoElemAttrs = elePersonInfoBillTo.getAttributes();
				for(int h = 0 ; h<personInfoElemAttrs.getLength();h++) 
				{
					Attr a1 = (Attr)personInfoElemAttrs.item(h);
					eleToAddress.setAttribute(a1.getName(), a1.getValue());
				}
				Element extn = (Element) invoiceHeaderele.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
				double TotalRefundedAmount= 0.0;
				double totalRefundamt = 0.0;
				if (sInvoiceType.equalsIgnoreCase("INFO") ){
					Element eleCollectionDetails = (Element)inXML.getElementsByTagName("CollectionDetails").item(0);
					if(YFCObject.isVoid(eleCollectionDetails))
						eleCollectionDetails = XMLUtil.appendChild(inXML, invoiceHeaderele, "CollectionDetails", "");
					Element eleCollectionDetail = XMLUtil.appendChild(inXML, eleCollectionDetails, "CollectionDetail", "");
					eleCollectionDetail.setAttribute("AmountCollected",invoiceHeaderele.getAttribute("AmountCollected"));
					// sending only GC # to SA. 
					String appeaseAmount = extn.getAttribute("ExtnAppeaseAmount");
					if(!YFCCommon.isVoid(appeaseAmount))
					{
						double appeaseDoubleAmount = 0.00;
						appeaseDoubleAmount = -Double.parseDouble(appeaseAmount);
						eleCollectionDetail.setAttribute("AmountCollected",String.valueOf(appeaseDoubleAmount));
						invoiceHeaderele.setAttribute("AmountCollected",String.valueOf(appeaseDoubleAmount));
					}
					
					Element elePaymentMethod = XMLUtil.appendChild(inXML, eleCollectionDetail, "PaymentMethod", "");
					elePaymentMethod.setAttribute("PaymentType","ONLINE_GIFT_CARD");
					elePaymentMethod.setAttribute("SvcNo",invoiceHeaderele.getAttribute("Reference1"));
					
					TotalRefundedAmount = Double.parseDouble(invoiceHeaderele.getAttribute("AmountCollected")); 
					 totalRefundamt=-TotalRefundedAmount;

					elePaymentMethod.setAttribute("TotalRefundedAmount",Double.toString(totalRefundamt));
					elePaymentMethod.setAttribute("TotalCharged",Double.toString(TotalRefundedAmount));



				}else if (sInvoiceType.equalsIgnoreCase("CREDIT_MEMO") ){
					NodeList ndlCollectionDetail = inXML.getElementsByTagName("CollectionDetail");
					for(int iCDetail = 0; iCDetail < ndlCollectionDetail.getLength(); iCDetail++){
						Element eleCollectionDetail = (Element)ndlCollectionDetail.item(iCDetail);
						if(!YFCObject.isVoid(eleCollectionDetail)){
							Element elePaymentMethod = (Element)eleCollectionDetail.getElementsByTagName("PaymentMethod").item(0);
							if(!YFCObject.isVoid(elePaymentMethod)){
								String strTotalRefundedAmount = elePaymentMethod.getAttribute("TotalRefundedAmount");
								if(!YFCObject.isVoid(strTotalRefundedAmount)){
									TotalRefundedAmount = Double.parseDouble(strTotalRefundedAmount); 
									 totalRefundamt = -TotalRefundedAmount;	
								}

								elePaymentMethod.setAttribute("TotalCharged",Double.toString(totalRefundamt));
							}
						}
					}




				}
			
		}

		
		
		//Element shipmentElem = (Element) inXML.getElementsByTagName("Shipment").item(0);
		//Commemted for  for Omni 2.0 jira 769
		/**
		String shipDate=shipmentElem.getAttribute("ShipDate");
		String trimedShipDate=shipDate.substring(0,10);
		String trimedtime=shipDate.substring(shipDate.length() - 6);
		String newShipDate=trimedShipDate+"T00:01:00" +trimedtime;
	
		shipmentElem.setAttribute("ShipDate", newShipDate);**/

		String tranNumber = "";
		Element invoiceHeader = (Element) inXML.getElementsByTagName("InvoiceHeader").item(0);
		
		Element orderEle = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		Element orderExtnEle = (Element) orderEle.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
		Element extn = (Element) invoiceHeader.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
		/*NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		int len = orderLineList.getLength();
		 */
		
		String isSubscriptionOrder = orderExtnEle.getAttribute("ExtnSubscriptionOrder");
		String isOrigADPOrder = orderExtnEle.getAttribute("ExtnOriginalADPOrder");
		/* OMS-1220 changes : start*/
		String strExtnReOrder = orderExtnEle.getAttribute("ExtnReOrder");
		/*OMS-1220 changes : end */
		/*OMS-1351/1352 changes -start */
		String isDTCOrder = null;
		isDTCOrder=orderExtnEle.getAttribute("ExtnDTCOrder");
		/*OMS-1351/1352 changes -end */
		String invoiceKey = invoiceHeader.getAttribute("OrderInvoiceKey");
		String OHK = orderEle.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		String returnSeqId = invoiceHeader.getAttribute("ReturnSeqIdentifier");
		String extnStoreNo = orderExtnEle.getAttribute("ExtnStoreNo");
		
		//Start: Added during Payment Changes added  04-27-2017 
		// Setting shipnode for SHP virtual store orders
		String enteredBy=null;
		Element orderElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		enteredBy=orderElement.getAttribute(VSIConstants.ATTR_ENTERED_BY);
		//String attrOrderType = inXML.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		String attrDelMeth = null;
		if(!YFCCommon.isVoid(orderLineElement))
		{
			attrDelMeth = orderLineElement.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
		}
		
		if(YFCCommon.isVoid(attrDelMeth) && "Y".equals(isDTCOrder))
		{
			attrDelMeth = VSIConstants.ATTR_DEL_METHOD_SHP;
		}
		
		if(!YFCObject.isVoid(attrDelMeth)){
			if(attrDelMeth.equalsIgnoreCase(VSIConstants.ATTR_DEL_METHOD_SHP)){
			NodeList shipmenteleList = inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
				for (int i = 0; i < shipmenteleList.getLength(); i++) 
				{	
					Element shipmentEle = (Element) shipmenteleList.item(i);
						shipmentEle.setAttribute(VSIConstants.ATTR_SHIP_NODE, enteredBy);
				}//End of loop setting Shipnode=EnteredBy
			}//end of SHP check
		}//end of nullcheck for Del Meth
			//END: Added during Payment Changes added  04-27-2017 
	
		

		//Modifications done for OMS -413 on 20-11-2014
		String ordType = orderEle.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		String ordNo = orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
		//String entryType = orderEle.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
		
		if(!YFCCommon.isVoid(isSubscriptionOrder) && "Y".equals(isSubscriptionOrder) && 
				orderExtnEle.hasAttribute("ExtnStoreNo") && "6101".equals(extnStoreNo))
		{
			orderExtnEle.removeAttribute("ExtnStoreNo");
		}
		//Start: Modified during Payment Changes added  04-27-2017 
		//SHP Sequence Payment Changes added  04-27-2017
		String seqNum =VSIConstants.SEQ_VSI_SEQ_TRANID;
		String shipNodePadded= "";
		
		
		if(!YFCCommon.isVoid(returnSeqId))
		{
			seqNum = seqNum +"5_";
		}
		else
		{
			//OMS-2510 Start
			if(isBossShipment && !YFCCommon.isVoid(strExtnReOrder) && FLAG_Y.equals(strExtnReOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(attrDelMeth)|| !YFCCommon.isVoid(attrDelMeth)))
				//OMS-2510 End
			{
					seqNum = seqNum +"98_";
					if(log.isDebugEnabled()){
						log.debug("BOSS ReOrders, seqNum"+seqNum);
					}
			} 
			else if(isBossShipment && !YFCCommon.isVoid(isSubscriptionOrder) && FLAG_Y.equals(isSubscriptionOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(attrDelMeth)&& !YFCCommon.isVoid(attrDelMeth)))
			{
				seqNum = seqNum +"97_";
				if(log.isDebugEnabled()){
					log.debug("BOSS Subscription Order, seqNum"+seqNum);
				}
				
			}
			else if(isBossShipment && !YFCCommon.isVoid(isOrigADPOrder) && FLAG_Y.equals(isOrigADPOrder) && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(attrDelMeth)&& !YFCCommon.isVoid(attrDelMeth)))
			{
				seqNum = seqNum +"96_";
				if(log.isDebugEnabled()){
					log.debug("BOSS Template Order, seqNum"+seqNum);
				}
				
			}
			else if(isBossShipment && (ATTR_DEL_METHOD_SHP.equalsIgnoreCase(attrDelMeth)&& !YFCCommon.isVoid(attrDelMeth)))
			{
				seqNum = seqNum +"95_";
				if(log.isDebugEnabled()){
					log.debug("BOSS Order, seqNum"+seqNum);
			}
			}
			/*OMS-1220 changes :start*/
			else if(!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder) && VSIConstants.ATTR_DEL_METHOD_SHP.equalsIgnoreCase(attrDelMeth))
			//OMS-2510 End
			{
				seqNum = seqNum +"92_";
				if(log.isDebugEnabled()){
					log.debug("ReOrders, seqNum"+seqNum);
				}
			} 
			/*OMS-1220 changes :end*/
			/*OMS-1352 changes start*/
			else if(!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder) && (VSIConstants.FLAG_N.equalsIgnoreCase(isDTCOrder)|| YFCCommon.isVoid(isDTCOrder)))
			{
				seqNum = seqNum +"94_";
				if(log.isDebugEnabled()){
					log.debug("strExtnReOrder:Y and isDTCOrder: N or null, seqNum"+seqNum);
				}
				
			}
			/*OMS-1352 changes end*/
			/*OMS-1351 changes start*/
			else if(!YFCCommon.isVoid(isSubscriptionOrder) && "Y".equals(isSubscriptionOrder) && (VSIConstants.FLAG_N.equalsIgnoreCase(isDTCOrder)|| YFCCommon.isVoid(isDTCOrder)))
			{
				seqNum = seqNum +"93_";
				if(log.isDebugEnabled()){
					log.debug("isSubscriptionOrder:Y and isDTCOrder: N or null, seqNum"+seqNum);
				}
				
			}
			/*OMS-1351 changes end*/
			else if(!YFCCommon.isVoid(isSubscriptionOrder) && "Y".equals(isSubscriptionOrder))
			{
				seqNum = seqNum +"91_";
			} // Auto delivery orders get 91_
			//original ADP order changes done for OMS-857
			else if(!YFCCommon.isVoid(isOrigADPOrder) && "Y".equals(isOrigADPOrder))
			{
				seqNum = seqNum +"9_";
			}
			else if(ordType != null && ordType.equalsIgnoreCase(VSIConstants.ATTR_ORDER_TYPE_POS)){
				seqNum = seqNum+VSIConstants.ATTR_TRAN_SEQ_POS_PICK;
			}// All POS order get 88_
			//OMS-2010 start
			else if (!YFCCommon.isStringVoid(ordType)&&VSIConstants.WHOLESALE.equalsIgnoreCase(ordType)) {
				seqNum = seqNum + VSIConstants.ATTR_TRAN_SEQ_WHOLESALE;
			}
			//OMS-2010 End
			else if(!YFCObject.isVoid(attrDelMeth) && attrDelMeth.equalsIgnoreCase(VSIConstants.ATTR_DEL_METHOD_SHP)){
				seqNum = seqNum +VSIConstants.ATTR_TRAN_SEQ_SHP;
			}//All WEB Ship orders 5_
			else 
			{
				seqNum = seqNum+VSIConstants.ATTR_TRAN_SEQ_WEB_PICK;
			}// WEB PICK and STS gets 89_
			//End: Modified during Payment Changes added  04-27-2017 
		}
		Element shipmentEle = (Element) inXML.getElementsByTagName("Shipment").item(0);



		try {
			//OMS-1353 : Start
			//Commenting the changeShipment call for ShipmentSeqNo
			
			/*if(!YFCCommon.isVoid(orderLineElement))
			{
				String lineType = orderLineElement.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				//Changes done for OMS-909
				
				if(lineType.equalsIgnoreCase("SHIP_TO_HOME") && !sInvoiceType.equalsIgnoreCase("RETURN")){
					api = YIFClientFactory.getInstance().getApi();

					String sHK= shipmentEle.getAttribute("ShipmentKey");

					
					*Added this condition as ShipmentKey would be missing for Invoice Type as "Credit Memo" and the below code was throwing an error.
					*Due to this Transaction # was not getting generated.
					
					if (null != sHK && "" != sHK) {

						Element extnShipmentEle = (Element) shipmentEle.getElementsByTagName("Extn").item(0);
						String shipmentSeqNo= extnShipmentEle.getAttribute("ShipmentSeqNo");
						int iRSeq =0;

						if(null==shipmentSeqNo || ""==shipmentSeqNo){

							Document getShipmentListInput = XMLUtil.createDocument("Shipment");
							Element shipmentElement = getShipmentListInput.getDocumentElement();
							shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, OHK);
							shipmentElement.setAttribute(VSIConstants.ATTR_STATUS, "1600.002");


							env.setApiTemplate(VSIConstants.API_GET_SHIPMENT_LIST, "global/template/api/getShipmentForInvoice.xml");// to be set if reqd
							YIFApi callApi = YIFClientFactory.getInstance().getLocalApi();
							Document outputDoc = callApi.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);	
							env.clearApiTemplates();

							NodeList shipmentList = outputDoc.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
							int shipmentLength = shipmentList.getLength();
							if(shipmentLength == 1){

								shipmentSeqNo="1";
								extnShipmentEle.setAttribute("ShipmentSeqNo",shipmentSeqNo);

								Document changeShipmentDoc = XMLUtil.createDocument("Shipment");
								Element changeShipmentEle = changeShipmentDoc.getDocumentElement();
								//shipmentEle.setAttribute("ShipmentKey", sHK);
								changeShipmentEle.setAttribute("ShipmentKey", sHK);
								Element extnElement = changeShipmentDoc.createElement(VSIConstants.ELE_EXTN);
								extnElement.setAttribute("ShipmentSeqNo", shipmentSeqNo);
								changeShipmentEle.appendChild(extnElement);

								api.invoke(env, "changeShipment", changeShipmentDoc);

							}else{

								//navigate through shipmentlist to get highest seqnum
								String sHighestNum = "0";
								for(int j=0;j<shipmentLength;j++){
									//Fixed for STH - Extn Transaction No not stamped on Invoice

									Element shipElement = (Element)shipmentList.item(j);
									if(!YFCObject.isNull(shipElement)){
										Element ExtnEle = (Element) shipElement.getElementsByTagName("Extn").item(0);
										if(!YFCObject.isNull(ExtnEle)){
											if (!YFCObject.isNull(ExtnEle.getAttribute("ShipmentSeqNo"))) {
												String sProcessNum = ExtnEle.getAttribute("ShipmentSeqNo");
												if(!YFCObject.isNull(sProcessNum)){
													if(Integer.parseInt(sProcessNum) > Integer.parseInt(sHighestNum)){
														sHighestNum = sProcessNum;
													}
												}
											}
										}
									}
								}

								int iShipSeq = Integer.valueOf(sHighestNum);
								String sSeqToSet = String.valueOf(iShipSeq + 1);
								extnShipmentEle.setAttribute("ShipmentSeqNo",sSeqToSet);

								Document changeShipmentDoc = XMLUtil.createDocument("Shipment");
								Element changeShipmentEle = changeShipmentDoc.getDocumentElement();
								//shipmentEle.setAttribute("ShipmentKey", sHK);
								changeShipmentEle.setAttribute("ShipmentKey", sHK);
								Element extnElement = changeShipmentDoc.createElement(VSIConstants.ELE_EXTN);
								extnElement.setAttribute("ShipmentSeqNo", sSeqToSet);
								changeShipmentEle.appendChild(extnElement);

								api.invoke(env, "changeShipment", changeShipmentDoc);

							}

						}
					}//Change Ended

				}
			}*/
	
	//OMS-1353 : End
			api = YIFClientFactory.getInstance().getApi();
			String sShipNode =  null;
			//api.executeFlow(env, "VSISendInvoice", inXML);
			if(!YFCCommon.isVoid(returnSeqId))
			{
				sShipNode = returnSeqId;
			}
			else
			{
				sShipNode =  shipmentEle.getAttribute("ShipNode");
				// All ship to home gets entered by as ship node
				//String sDelMethod =  shipmentEle.getAttribute("DeliveryMethod");
				if("SHP".equalsIgnoreCase(attrDelMeth)){
					sShipNode=enteredBy;
				}
			}
			
			if(sShipNode != null)
				shipNodePadded = ("00000" + sShipNode).substring(sShipNode.trim()
						.length());
			seqNum = seqNum+shipNodePadded;
			//OMS-1353 : Start
			boolean isChangeShipmentReqForTran=false;
			boolean isChangeShipmentReqForSeq=false;
			String strExtnSeqNo=null;
			if(log.isDebugEnabled()){
				log.debug("InvoiceType"+sInvoiceType);
			}
			if(sInvoiceType.equalsIgnoreCase("SHIPMENT") ){
				Element eleShipmentExtn=SCXmlUtil.getChildElement(shipmentEle, VSIConstants.ELE_EXTN);
				tranNumber=eleShipmentExtn.getAttribute("ExtnTransactionNo");
				strExtnSeqNo=eleShipmentExtn.getAttribute("ShipmentSeqNo");
				if(YFCCommon.isStringVoid(tranNumber))
					isChangeShipmentReqForTran=true;
				if(YFCCommon.isStringVoid(strExtnSeqNo))
					isChangeShipmentReqForSeq=true;
				if(log.isDebugEnabled()){
					log.debug("InvoiceType is Shipment, fetching ExtnTransactionNo from event xml");
				}
			}
			if(YFCCommon.isStringVoid(tranNumber)){
				tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
				if(log.isDebugEnabled()){
					log.debug("generating the next ExtnTransactionNo : "+tranNumber);
				}
			}
			/*else{
				tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
				log.debug("InvoiceType is not Shipment, generating the next ExtnTransactionNo");
			}*/
			//OMS-1353 :End
			if(log.isDebugEnabled()){
				log.debug("Transaction Number:"+ tranNumber);
			}
			extn.setAttribute("ExtnTransactionNo", tranNumber);
			Document changeOrderInvoiceDocument = XMLUtil.createDocument("OrderInvoice");
			Element orderInvoice = changeOrderInvoiceDocument.getDocumentElement();
			orderInvoice.setAttribute("OrderInvoiceKey", invoiceKey);
			Element extnElement = changeOrderInvoiceDocument.createElement(VSIConstants.ELE_EXTN);
			extnElement.setAttribute("ExtnTransactionNo", tranNumber);
			orderInvoice.appendChild(extnElement);

			api.invoke(env, "changeOrderInvoice", changeOrderInvoiceDocument);
			//OMS-1353 : Start
			if (isChangeShipmentReqForSeq ||isChangeShipmentReqForTran)
			{
				Document changeShipmentDoc = XMLUtil.createDocument("Shipment");
				Element changeShipmentEle = changeShipmentDoc.getDocumentElement();
				String sHK= shipmentEle.getAttribute("ShipmentKey");
				changeShipmentEle.setAttribute("ShipmentKey", sHK);
				Element eleShipmentExtn = changeShipmentDoc.createElement(VSIConstants.ELE_EXTN);
				if(isChangeShipmentReqForSeq) {
					strExtnSeqNo=findShipmentSeqNo(env,OHK);
					eleShipmentExtn.setAttribute("ShipmentSeqNo", strExtnSeqNo);
				}
				if(isChangeShipmentReqForTran)
					eleShipmentExtn.setAttribute("ExtnTransactionNo", tranNumber);
				changeShipmentEle.appendChild(eleShipmentExtn);
				api.invoke(env, "changeShipment", changeShipmentDoc);
			}
			//OMS-1353 : End
		}catch (Exception e) {
			e.printStackTrace();
		}
		if (sInvoiceType.equalsIgnoreCase("INFO") ){
			XMLUtil.removeChild(invoiceHeaderele, eleLineDetails);
			String invoiceCreationReason = invoiceHeaderele.getAttribute("InvoiceCreationReason");
			if(!invoiceCreationReason.contains("Refund Due to"))
			{
				invoiceHeaderele.setAttribute("InvoiceCreationReason", "Customer Appeasement");
			}
			Element elLineDetails = XMLUtil.appendChild(inXML, invoiceHeaderele, "LineDetails", "");
			Element elLineDetail = XMLUtil.appendChild(inXML, elLineDetails, "LineDetail", "");
			Element eleOrderLine = XMLUtil.appendChild(inXML, elLineDetail, "OrderLine", "");
			//eleOrderLine.setAttribute("CustomerPONo", strCustomerOrderNo);
			if("POS".equals(ordType))
				eleOrderLine.setAttribute("CustomerPONo", ordNo);	
			if(!YFCObject.isVoid(strCustomerOrderNo))
				eleOrderLine.setAttribute("CustomerPONo", strCustomerOrderNo);
			if(!YFCObject.isVoid(strLineType))
			{
				eleOrderLine.setAttribute("LineType", strLineType);
			}
			else
			{
				if("SHP".equalsIgnoreCase(attrDelMeth))
				{
					eleOrderLine.setAttribute("LineType", "SHIP_TO_HOME");
				}
			}
			if (!YFCObject.isVoid(attrDelMeth)) {
				eleOrderLine.setAttribute(VSIConstants.ATTR_DELIVERY_METHOD, attrDelMeth);
			}
			Element eleOrderElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			if(!YFCObject.isVoid(eleOrderElement)){
				eleOrderElement.setAttribute("DocumentType", "0003");
			}
			

		}
		
		if(sInvoiceType.equalsIgnoreCase("CREDIT_MEMO"))
		{
			Element eleOrderElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			if(!YFCObject.isVoid(eleOrderElement)){
				eleOrderElement.setAttribute("DocumentType", "0003");
			}
		}
       //OMS-1343 start
		Element orderele = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String strDocumentType=orderele.getAttribute("DocumentType");
		if(strDocumentType.equalsIgnoreCase("0003"))
		{
			Element invoiceHeaderelement = (Element) inXML.getElementsByTagName("InvoiceHeader").item(0);
			String derivedFromOrderHeaderKey=invoiceHeaderelement.getAttribute("DerivedFromOrderHeaderKey");
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, derivedFromOrderHeaderKey);
			
			api = YIFClientFactory.getInstance().getApi();
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderNo.xml");
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);	
			env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
			
			if(log.isDebugEnabled()){
				log.debug("getting sales order no : \n "+XMLUtil.getXMLString(outDoc));
			}
			Element salesorderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			if(null != salesorderEle){
			String orderNo=salesorderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);

			invoiceHeaderele.setAttribute("DerivedFromOrderNo", orderNo);
		}
		}
		//OMS-1343 end
		//OMS-1654 start
		//OMS-2150 Start
		
		if("0001".equalsIgnoreCase(strDocumentType)){
		Element eleLineDetail=null;
		Document getItemListInput=null;
		Document docGetItemListTemp=null;
		Element eleItem=null;
		String strItemId=null;
		Element eleItemInput=null;
		Element eleGetItemList=null;
		Element eleItemTempl=null;
		Element eleExtnTempl=null;
		String strExtnDept=null;
		String strExtnClass=null;
		String strExtnColor=null;
		String strExtnStyle=null;
		String strExtnItemSize=null;
		String strExtnBrandTitle=null;
		
		NodeList nlLineDetail = inXML.getElementsByTagName(VSIConstants.ELE_LINE_DETAIL);
		if(!YFCObject.isVoid(nlLineDetail)){
		for (int j = 0; j < nlLineDetail.getLength(); j++) {
			eleLineDetail = (Element) nlLineDetail.item(j);
			eleItem = (Element) eleLineDetail.getElementsByTagName(VSIConstants.ELE_ITEM).item(0);
			if(!YFCObject.isVoid(eleItem)){
			strItemId = eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
			
			// creating getItemList input
			getItemListInput = XMLUtil.createDocument(VSIConstants.ELE_ITEM);
			eleItemInput = getItemListInput.getDocumentElement();
			eleItemInput.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
			eleItemInput.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, "VSI-Cat");
			
			//creating template
			 docGetItemListTemp = XMLUtil.createDocument(VSIConstants.ELE_ITEM_LIST);
			 eleGetItemList = docGetItemListTemp.getDocumentElement();
			 eleItemTempl = SCXmlUtil.createChild(eleGetItemList,VSIConstants.ELE_ITEM);
			 eleItemTempl.setAttribute(VSIConstants.ATTR_ITEM_ID, "");
			 eleExtnTempl = SCXmlUtil.createChild(eleItemTempl, VSIConstants.ELE_EXTN);
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_CLASS, "");
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_DEPT, "");
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_COLOR, "");
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_STYLE, "");
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE, "");
			 eleExtnTempl.setAttribute(VSIConstants.ATTR_EXTN_ITEM_SIZE, "");
			 
			 
			
			Document docOutput = VSIUtils.invokeAPI(env,docGetItemListTemp, VSIConstants.API_GET_ITEM_LIST,getItemListInput);
			Element eleitemEle = SCXmlUtil.createChild(eleItem, "Extn");
			strExtnDept = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_DEPT);
			
			if(!YFCObject.isVoid(strExtnDept)){
				
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_DEPT, strExtnDept);
			}
			strExtnClass = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_CLASS);
			if(!YFCObject.isVoid(strExtnClass)){
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_CLASS, strExtnClass);
			}
			//OMS-1801 START
			strExtnColor = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_COLOR);
			
			if(!YFCObject.isVoid(strExtnColor)){
				
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_COLOR, strExtnColor);
			}
			strExtnStyle = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_STYLE);
			
			if(!YFCObject.isVoid(strExtnStyle)){
				
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_STYLE, strExtnStyle);
			}
			//OMS-1801 END
			//OMS-2150 START
			strExtnBrandTitle = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE);
			
			if(!YFCObject.isVoid(strExtnBrandTitle)){
				
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE, strExtnBrandTitle);
			}
			
			strExtnItemSize = YFCDocument.getDocumentFor(docOutput).getDocumentElement().getChildElement(VSIConstants.ELE_ITEM).getChildElement(VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_ITEM_SIZE);
			
			if(!YFCObject.isVoid(strExtnItemSize)){
				
				eleitemEle.setAttribute(VSIConstants.ATTR_EXTN_ITEM_SIZE, strExtnItemSize);
			}
			//OMS-2150 END 
			

		}
		}
		}
		}
		//oms-1654 end
		return inXML;
	}
	//OMS-1353 : Start 
		private String findShipmentSeqNo(YFSEnvironment env, String strOrderHeaderKey) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
			String strShipmentSeqNo="";
			int iShipmentSeqNo=0;
			int iShipmentCount=0;
			Document docInput=XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
			Element eleShipmentIP=docInput.getDocumentElement();
			eleShipmentIP.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			
			if(log.isDebugEnabled()){
				log.debug("findShipmentSeqNo:Input for getShipmentList "+ XMLUtil.getXMLString(docInput));
			}
			Document docOutput=VSIUtils.invokeAPI(env, VSIConstants.API_GET_SHIPMENT_LIST, docInput);
			
			NodeList nlShipment =docOutput.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
			
			int iShipmentLength=nlShipment.getLength();
			if(iShipmentLength==1)
				iShipmentSeqNo=1;
			else
			{
				String sHighestNum = "0";
				for(int j=0;j<iShipmentLength;j++){
					//Fixed for STH - Extn Transaction No not stamped on Invoice

					Element shipElement = (Element)nlShipment.item(j);
					if(!YFCObject.isNull(shipElement)){
						Element ExtnEle = (Element) shipElement.getElementsByTagName("Extn").item(0);
						if(!YFCObject.isNull(ExtnEle)){
							if (!YFCObject.isNull(ExtnEle.getAttribute("ShipmentSeqNo"))) {
								String sProcessNum = ExtnEle.getAttribute("ShipmentSeqNo");
								if(!YFCObject.isNull(sProcessNum)){
									if(Integer.parseInt(sProcessNum) > Integer.parseInt(sHighestNum)){
										sHighestNum = sProcessNum;
									}
								}
							}
						}
					}
				}
				iShipmentCount = Integer.valueOf(sHighestNum);
				iShipmentSeqNo=iShipmentCount+1;
			}
			if(log.isDebugEnabled()){
				log.debug(" ShipmentSeqNo: "+iShipmentSeqNo);
			}
			strShipmentSeqNo=String.valueOf(iShipmentSeqNo);
			if(log.isDebugEnabled()){
				log.debug("findShipmentSeqNo :Output is : "+strShipmentSeqNo);
			}
			return strShipmentSeqNo;
		}
		//OMS-1353 : End
}
