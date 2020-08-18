package com.vsi.oms.api.order;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
public class VSIBR1OrderCreate {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIBR1OrderCreate.class);
	YIFApi api;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors	
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public Document updateOrder(YFSEnvironment env, Document inXML)
			throws Exception {
		NodeList paymentMethod = inXML.getElementsByTagName("PaymentMethod");
		int paymentLength = paymentMethod.getLength();
		 for(int i=0;i< paymentLength;i++){
			 Element paymentMethodEle = (Element) inXML.getElementsByTagName("PaymentMethod").item(i);
			 String amount=paymentMethodEle.getAttribute("PaymentReference9");
			 Element paymentDetailEle = XMLUtil.appendChild(inXML,paymentMethodEle,"PaymentDetails", "");
			 paymentDetailEle.setAttribute("ChargeType", "CHARGE");
			 paymentDetailEle.setAttribute("RequestAmount", amount);
			 paymentDetailEle.setAttribute("ProcessedAmount",amount);
			 paymentDetailEle.setAttribute("RequestProcessed", "Y");
			 paymentDetailEle.setAttribute("TranType", "CHARGE");
			 
		 }
		 
		return inXML;

	}
		
}
