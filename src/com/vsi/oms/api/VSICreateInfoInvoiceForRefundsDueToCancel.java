package com.vsi.oms.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * 
 * @author Srikanth
 * 
 *         Custom API to create an Info invoice when a  line prepaid using voucher gets
 *         cancelled and an e gift card is generated for refund. An info invoice is created to 
 *         book the e gift card in sales audit
 *         
 *         This custom java file gets invoked on_liability_transfer of a payment collection.
 */

public class VSICreateInfoInvoiceForRefundsDueToCancel implements YIFCustomApi {

	private Properties props;

	private YFCLogCategory log = YFCLogCategory
			.instance(VSICreateInfoInvoiceForRefundsDueToCancel.class);

	

	String strOrdHeaderKey = "";

	
	YIFApi api;

	boolean isCancelled = false;
	
	public Document createInfoInvForRefunds(YFSEnvironment env, Document inDoc)
			throws Exception {
		api = YIFClientFactory.getInstance().getApi();


		if(log.isDebugEnabled()){
			log.verbose("createInfoInvForRefunds : createInfoInvForDeposits : START");
		}

		Element eleInput = inDoc.getDocumentElement();

		if(log.isDebugEnabled()){
			log.verbose("Input to VSICreateInfoInvoiceForAppeasements: "
					+ XMLUtil.getElementXMLString(eleInput));
		}

		strOrdHeaderKey = eleInput.getAttribute("OrderHeaderKey");

		if(!YFCObject.isVoid(strOrdHeaderKey)){
			 Element lineDetailsEle = XMLUtil.appendChild(inDoc, eleInput, "LineDetails", "");

			Element eleGetOrderDetails = callGetOrderList(env,strOrdHeaderKey);
			if(!YFCObject.isVoid(eleGetOrderDetails)){
				NodeList ndlOrderLines = eleGetOrderDetails.getElementsByTagName("OrderLine");
				for(int i = 0; i < ndlOrderLines.getLength(); i++){
					Element eleOrderLine = (Element)ndlOrderLines.item(i);
					if(!YFCObject.isVoid(eleOrderLine)){
						NodeList ndlOrderStatus = eleOrderLine.getElementsByTagName("OrderStatus");
						Element linePriceInfo = (Element)eleOrderLine.getElementsByTagName("LinePriceInfo").item(0);
				        //System.out.println("line price info input " +XMLUtil.getElementXMLString(linePriceInfo) );

					String strUnitPrice=linePriceInfo.getAttribute("UnitPrice");
                     String strTaxableFlag=linePriceInfo.getAttribute("TaxableFlag");
                     if(log.isDebugEnabled()){
                    	 log.debug("UnitPrice " + strUnitPrice);
                    	 log.debug("strTaxableFlag " + strTaxableFlag);
                     }
                     
						for(int iOrderStatus = 0; iOrderStatus < ndlOrderStatus.getLength();iOrderStatus++){
							Element eleOrderStatus = (Element)ndlOrderStatus.item(iOrderStatus);
							if(!YFCObject.isVoid(eleOrderStatus) ){
								String strStatus = eleOrderStatus.getAttribute("Status");
								String statusQty = eleOrderStatus.getAttribute("StatusQty");

								if(!YFCObject.isVoid(strStatus) ){
									strStatus = strStatus.substring(0, 4);
									Integer istrStatus = Integer.parseInt(strStatus);
									
									if(istrStatus == Integer.parseInt("9000")){
										isCancelled = true;
				 Element lineDetailEle = XMLUtil.appendChild(inDoc, lineDetailsEle, "LineDetail", "");
				 Element OrderLineEle = XMLUtil.appendChild(inDoc, lineDetailEle, "OrderLine", "");

				 lineDetailEle.setAttribute("ShippedQty", statusQty);
				 lineDetailEle.setAttribute("OrderLineKey", eleOrderLine.getAttribute("OrderLineKey"));
				 lineDetailEle.setAttribute("Quantity", statusQty);
				 OrderLineEle.setAttribute("OrderLineKey",  eleOrderLine.getAttribute("OrderLineKey"));
				 Element eleLinePriceInfo = XMLUtil.appendChild(inDoc, OrderLineEle, "LinePriceInfo", "");
				 eleLinePriceInfo.setAttribute("UnitPrice", strUnitPrice);
				 eleLinePriceInfo.setAttribute("TaxableFlag", strTaxableFlag);
										break;
										
									}
								}
							}


						}
					}
				}


				if(isCancelled){
					//System.out.println("Inside Invoce");
					createInfoInvoice(env,eleInput);
				}

				try {

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(log.isDebugEnabled()){	
				log.verbose("VSICreateInfoInvoiceForDeposits : createInfoInvForDeposits : END");
			}
			}
		return inDoc;

	}

	private Element callGetOrderList(YFSEnvironment env, String sOrderHeaderKey) throws Exception {
        Document outDoc = null;
        Document docInputForGetOrderList = XMLUtil.createDocument("Order");
        Element eleOrderList = docInputForGetOrderList.getDocumentElement();
        eleOrderList.setAttribute("OrderHeaderKey",sOrderHeaderKey);
        //System.out.println("OrderHeaderKey " + sOrderHeaderKey);
		env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/getInvoiceOrderList.xml");
        outDoc= api.invoke(env, "getOrderList",docInputForGetOrderList);
        Element eleOutput = outDoc.getDocumentElement();
        Element eleOrder = (Element)eleOutput.getElementsByTagName("Order").item(0);
        if(log.isDebugEnabled()){
    		log.debug("order list input " +XMLUtil.getElementXMLString(eleOrder) );
        }
        env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
        
        if(YFCObject.isVoid(eleOrder)){
              eleOrder = eleOrderList;
        }
        return eleOrder;
  }

	private void createInfoInvoice(YFSEnvironment env,  Element eleInput)
			throws Exception {

		if(log.isDebugEnabled()){
			log.verbose("VSICreateInfoInvoiceForDeposits : createInfoInvoice : START");
		}
		double dblChargeAmount = 0.0;
		NodeList ndlChargeTransactions = eleInput.getElementsByTagName("DistributedToChargeTransaction");
		String SvcNo=null;
		String PIN = null;
		for(int i = 0 ; i < ndlChargeTransactions.getLength(); i++){
			Element eleChargeTransaction = (Element)ndlChargeTransactions.item(i);
			if(!YFCObject.isVoid(eleChargeTransaction)){
				Element elePaymentMethod = (Element)eleChargeTransaction.getElementsByTagName("PaymentMethod").item(0);
				if(!YFCObject.isVoid(elePaymentMethod)){
					String strRefundedAmount = elePaymentMethod.getAttribute("TotalRefundedAmount");
					dblChargeAmount += Double.parseDouble(strRefundedAmount);
					 SvcNo=elePaymentMethod.getAttribute("SvcNo");
					 PIN=elePaymentMethod.getAttribute("PaymentReference2");
				}
				
			}
			
		}
		Document inRecordInvoiceCreationDoc = XMLUtil
				.createDocument("OrderInvoice");
		Element eleLineDetails=(Element)eleInput.getElementsByTagName("LineDetails").item(0);
		if(log.isDebugEnabled()){
    		log.debug("original line details " +XMLUtil.getElementXMLString(eleLineDetails) );
		}

		Element eleOrderInvoice = inRecordInvoiceCreationDoc
				.getDocumentElement();
		eleOrderInvoice.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				strOrdHeaderKey);
		eleOrderInvoice.setAttribute("InvoiceType",
				"INFO");
		eleOrderInvoice.setAttribute("InvoiceCreationReason",
				"Refund Due to Cancellation");
		if(!YFCCommon.isVoid(PIN))
		{
			eleOrderInvoice.setAttribute("Reference1", SvcNo+"_"+PIN);
		}
		else
		{
			eleOrderInvoice.setAttribute("Reference1", SvcNo);
		}
		
		 Element OrderLineEle = XMLUtil.appendChild(inRecordInvoiceCreationDoc, eleOrderInvoice, "LineDetails", "");
			XMLUtil.copyElement(inRecordInvoiceCreationDoc,eleLineDetails, OrderLineEle);

			if(log.isDebugEnabled()){
	    		log.debug("invoice line details " +XMLUtil.getElementXMLString(OrderLineEle) );
			}

		//eleOrderInvoice.appendChild(eleLineDetails);

		Element eleHheaderChargeList = inRecordInvoiceCreationDoc
				.createElement("HeaderChargeList");
		Element eleHeaderCharge = inRecordInvoiceCreationDoc
				.createElement("HeaderCharge");
		eleHheaderChargeList.appendChild(eleHeaderCharge);
		eleOrderInvoice.appendChild(eleHheaderChargeList);

		eleHeaderCharge.setAttribute("ChargeName",
				"CUSTOMER_APPEASEMENT");

		eleHeaderCharge.setAttribute("ChargeCategory",
				"Discount");		
			double amountCollected=-dblChargeAmount;
		eleHeaderCharge.setAttribute("ChargeAmount",Double.toString(dblChargeAmount));
		eleOrderInvoice.setAttribute("AmountCollected", Double.toString(amountCollected));

		Element eleInvoice = inRecordInvoiceCreationDoc.getDocumentElement();
		if(log.isDebugEnabled()){
    		log.debug("record invoice " +XMLUtil.getElementXMLString(eleInvoice) );
		}

		VSIUtils.invokeAPI(env, "recordInvoiceCreation", inRecordInvoiceCreationDoc) ;

	

	}

	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

	

	
}
