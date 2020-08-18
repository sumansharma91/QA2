package com.vsi.oms.userexit;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.OMPConfirmRefundDistributionUE;

/**
 * @author Perficient
 * @version 1.0
 * 
 *          This UE is implemented to issue Virtual GC to Customers when refund
 *          is created for Online Gift Card or Vouchers on an Sales Order.
 */

public class VSIRefundDistribution implements OMPConfirmRefundDistributionUE,VSIConstants {

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIRefundDistribution.class.getName());

	@Override
	public Document confirmRefundDistribution(YFSEnvironment env, Document inXML)
			throws YFSUserExitException {
		if(log.isDebugEnabled()){
    		log.debug("Inside VSIRefundDistribution: confirmRefundDistribution: inXML: \n"
				+ XMLUtil.getXMLString(inXML));
		}
		if (inXML != null) {
			Element rootEle = inXML.getDocumentElement();
			if (rootEle != null) {
				String orderNo = rootEle
						.getAttribute(VSIConstants.ATTR_ORDER_NO);
				String orderHK = rootEle
						.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				String strDocumentType = rootEle.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
				if (orderNo != null && orderHK != null) {
					
					if(strDocumentType.equalsIgnoreCase("0001")){
					Element eleRefundPaymentMethods = SCXmlUtil.getChildElement(rootEle, "RefundPaymentMethods");
					ArrayList<Element> arrRefundPaymentMethods = SCXmlUtil.getChildren(eleRefundPaymentMethods, "RefundPaymentMethod");
					String payType = null;
					
					for(Element refPayMthdEle:arrRefundPaymentMethods ){
						if(!YFCCommon.isVoid(refPayMthdEle)){
							//payType = refPayMthdEle
									//.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
							Element refundForPaymentMethods = SCXmlUtil.getChildElement(refPayMthdEle, "RefundForPaymentMethods");
							
							if(!YFCCommon.isVoid(refundForPaymentMethods))
							{
								Element refundForPaymentMethod = SCXmlUtil.getChildElement(refundForPaymentMethods, "RefundForPaymentMethod");
								String payKey = refundForPaymentMethod
										.getAttribute(VSIConstants.ATTR_PAYMENT_KEY);
								
								try
								{									
									Document getPaymentList = XMLUtil.createDocument("Payment");
									Element eleOrder = getPaymentList.getDocumentElement();
									eleOrder.setAttribute(VSIConstants.ATTR_PAYMENT_KEY, payKey);
									 Document docTemplateForGetPaymentList = this
								              .createTemplateForGetPaymentList();
									Document outDoc = VSIUtils.invokeAPI(env, docTemplateForGetPaymentList, "getPaymentList", getPaymentList);
									Element paymentEle = SCXmlUtil.getChildElement(outDoc.getDocumentElement(), "Payment");
									payType = paymentEle.getAttribute("PaymentType");
									
									if (payType.equalsIgnoreCase("CASH") || payType.equalsIgnoreCase("CHECK")
											) {
										
										//eleRefundPaymentMethods.removeChild(refPayMthdEle);
										refPayMthdEle.setAttribute("PaymentType", payType);
										refPayMthdEle.setAttribute("PaymentKey", payKey);
										refPayMthdEle.setAttribute("PaymentReference2", "");
										refPayMthdEle.removeChild(refundForPaymentMethods);
									}
								}
								catch (Exception e)
								{
									throw new YFSUserExitException(e.getMessage());
								}
							}
							
						}	
					}
					
				}
				}
			}

		}
		if(log.isDebugEnabled()){
    		log.debug("VSIRefundDistribution: confirmRefundDistribution: outXML: \n"
				+ XMLUtil.getXMLString(inXML));
		}
		return inXML;
	}
	
 private Document createTemplateForGetPaymentList() throws TransformerException {

	    // Create template Document for getOrderDetails .
	    Document docTemplateForGetPaymentList = SCXmlUtil
	        .createDocument("PaymentList");
	    Element paymentEle = SCXmlUtil.createChild(docTemplateForGetPaymentList.getDocumentElement(), "Payment");
	    paymentEle.setAttribute("PaymentType", "");
	    return docTemplateForGetPaymentList;

	  }


	
}// End of VSIRefundDistribution

