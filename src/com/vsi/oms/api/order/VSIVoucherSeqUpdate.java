package com.vsi.oms.api.order;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
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
public class VSIVoucherSeqUpdate {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIVoucherSeqUpdate.class);
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
	public Document seqNumberUpdate(YFSEnvironment env, Document inXML)
			throws Exception {
		
		
		Element rootElement = inXML.getDocumentElement();
		
		Element voucherEle = (Element) rootElement.getElementsByTagName("Voucher").item(0);
		String orderNo=	voucherEle.getAttribute("RedeemOrderNo");
		String store=	voucherEle.getAttribute("RedeemStore");

		Document getOrderListInput = XMLUtil.createDocument("Order");
		Element orderEle = getOrderListInput.getDocumentElement();
		orderEle.setAttribute("OrderNo", orderNo);
		orderEle.setAttribute("DocumentType", "0001");
		//orderEle.setAttribute("EnterpriseCode","VSI.com");
		api = YIFClientFactory.getInstance().getApi();
		Document outdocOrderList = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
        Element orderList = outdocOrderList.getDocumentElement();
		
		Element orderElement = (Element) orderList.getElementsByTagName("Order").item(0); 
		String orderType=orderElement.getAttribute("OrderType");
		
		Element orderLinesEle = SCXmlUtil.getChildElement(orderElement, "OrderLines");
		Element orderLineEle = SCXmlUtil.getChildElement(orderLinesEle, "OrderLine");
		String deliveryMethod = orderLineEle.getAttribute("DeliveryMethod");

		 String seqNum ="VSI_SEQ_V_TRANID_";
		 String shipNodePadded= "";
		 if(orderType != null && orderType.equalsIgnoreCase("POS")){
			 seqNum = seqNum+"88_";
		 }
		 else if("SHP".equals(deliveryMethod))
		 {
			 seqNum = seqNum+"5_";
		 }else if ("PICK".equals(deliveryMethod)){
			 seqNum = seqNum+"89_";
		 }
		 store="8002";
	
		 shipNodePadded = ("00000" + store).substring(store.trim()
					 .length());
		 seqNum = seqNum+shipNodePadded;

		String tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
			
		voucherEle.setAttribute("Comments",tranNumber);
		return inXML;
}
}