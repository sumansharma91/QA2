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
public class VSIRewardCalculate {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRewardCalculate.class);
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
	public Document rewardCalculate(YFSEnvironment env, Document inXML)
			throws Exception {
		
		Element rootElement = inXML.getDocumentElement();
		//Element orderEle=(Element)rootElement.getElementsByTagName("Order").item(0);
		String orderHdrKey=rootElement.getAttribute("OrderHeaderKey");
		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element orderElement = getOrderListInput.getDocumentElement();
		orderElement.setAttribute("OrderHeaderKey",orderHdrKey);
		env.setApiTemplate("getOrderList", "global/template/api/VSIRewardGetOrderList.xml");
		api = YIFClientFactory.getInstance().getApi();
		Document outOrderListDoc = api.invoke(env,"getOrderList", getOrderListInput);
		env.clearApiTemplates();
		Element overallTotal=(Element)outOrderListDoc.getElementsByTagName("OverallTotals").item(0);
		double grandTotal=Double.parseDouble(overallTotal.getAttribute("GrandTotal"));

		DecimalFormat df = new DecimalFormat("#.##");
		NodeList orderLineList = rootElement.getElementsByTagName("OrderLine");
		int lineLength=orderLineList.getLength();
		NodeList paymentList = outOrderListDoc.getElementsByTagName("PaymentMethod");
		double voucherAmt=0.0;
		double rewardLinetotal=0.0;
		double lineVoucherAmt=0.0;
		 for (int h = 0; h < paymentList.getLength(); h++) {
				Element paymentEle = (Element) paymentList.item(h);
				
				if("VOUCHERS".equalsIgnoreCase(paymentEle.getAttribute("PaymentType"))){
					double voucherLineAmt=Double.parseDouble(paymentEle.getAttribute("MaxChargeLimit"));
					voucherAmt=voucherLineAmt+voucherAmt;
				}
			 
		 }
		 if(voucherAmt>=grandTotal){
				 for (int i = 0; i < orderLineList.getLength(); i++) {
					Element orderLineEle = (Element) orderLineList.item(i);
					Element linePriceInfo=(Element)orderLineEle.getElementsByTagName("LinePriceInfo").item(0);
					linePriceInfo.setAttribute("RewardTotal","0.00");
			       }
		       }
		 else{
			
		    lineVoucherAmt=voucherAmt/lineLength;
		 		 
		    for (int i = 0; i < orderLineList.getLength(); i++) {
			Element orderLineEle = (Element) orderLineList.item(i);
			Element linePriceInfo=(Element)orderLineEle.getElementsByTagName("LinePriceInfo").item(0);
			double lineTotal=Double.parseDouble(linePriceInfo.getAttribute("LineTotal"));
			NodeList lineTaxList = orderLineEle.getElementsByTagName("LineTax");
			double taxLineToal=0.0;
			
			for (int k = 0; k < lineTaxList.getLength(); k++) {
				Element taxEle = (Element) lineTaxList.item(k);
				double taxTotal=Double.parseDouble(taxEle.getAttribute("Tax"));
				taxLineToal=taxLineToal+taxTotal;
											
			}
			rewardLinetotal=lineTotal-(taxLineToal+lineVoucherAmt);
			 linePriceInfo.setAttribute("RewardTotal", df.format(rewardLinetotal));

						
		 }
		 }
		return inXML;
}
}