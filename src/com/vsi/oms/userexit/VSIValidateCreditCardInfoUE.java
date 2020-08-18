package com.vsi.oms.userexit;



import java.rmi.RemoteException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.pca.ycd.japi.ue.YCDValidateCreditCardInfoUE;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * @author Perficient Inc.
 * 
 * Validate Credit Card Info UE
 *
 */
public class VSIValidateCreditCardInfoUE  implements YCDValidateCreditCardInfoUE {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIValidateCreditCardInfoUE.class);
	YIFApi api;
	
	
	public Document validateCreditCardInfo(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		// TODO Auto-generated method stub
		Document outdoc = null;
		Element eleInput = inXML.getDocumentElement();
		YFCDocument inDoc = null;
		Element elePaymentMethod = (Element)eleInput.getElementsByTagName("PaymentMethod").item(0);
		if(!YFCObject.isVoid(elePaymentMethod) ){
			{
				
				
					
						  inDoc = YFCDocument.createDocument("PaymentMethods");
						 YFCElement eleRootElement = inDoc.getDocumentElement();
						 YFCElement eleInPaymentMethod = eleRootElement.createChild("PaymentMethod");
						 //inDoc =  XMLUtil.createDocument("PaymentMethods");
						 
						// Element eleInPaymentMethod = inDoc.creat("PaymentMethod");
						 String strCCNo = elePaymentMethod.getAttribute("CreditCardNo");
						 String strExpiryMonth = elePaymentMethod.getAttribute("CreditCardExpMonth");
						 String strexpiryYear = elePaymentMethod.getAttribute("CreditCardExpYear");
						 String strOrganizationCode = "DEFAULT";
						 eleInPaymentMethod.setAttribute("Token",strCCNo);
						 eleInPaymentMethod.setAttribute("expiryMonth",strExpiryMonth);
						 eleInPaymentMethod.setAttribute("expiryYear",strexpiryYear.substring(2, 4));
						 eleInPaymentMethod.setAttribute("OrganizationCode",strOrganizationCode);
						 eleInPaymentMethod.setAttribute("OrderNo",strCCNo);						 				 
						
						
					
					try {
						api = YIFClientFactory.getInstance().getApi();
					} catch (YIFClientCreationException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				
				try {
					if(!YFCObject.isVoid(inDoc)){
						////System.out.println(XMLUtil.getXMLString(inDoc.getDocument()));
						outdoc = api.executeFlow(env, "VSIVerifyCreditCard",inDoc.getDocument());
						Element elePaymentMethodOut = (Element)outdoc.getElementsByTagName("PaymentMethod").item(0);
						if(!YFCObject.isVoid(elePaymentMethodOut)){
							String strIsValid = elePaymentMethodOut.getAttribute("IsValid");
							Element eleValidation = XMLUtil.createElement(inXML,"Validation","");
							eleValidation.setAttribute("IsValid", strIsValid);
							if("Y".equalsIgnoreCase(strIsValid))
								eleValidation.setAttribute("Message", "Credit Card is Valid");
							else	
								eleValidation.setAttribute("Message", "Credit Card is Invalid");
							elePaymentMethod.appendChild(eleValidation);
						}
						return inXML;
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					throw new YFSException(
							"EXTN_ERROR",
							"EXTN_ERROR",
					"Customer Not found");
				}
			}
		}
	
	return inXML;	 

	}
}
