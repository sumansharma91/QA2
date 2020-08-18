package com.vsi.oms.api;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;




import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIAwaitingPaymentStatus {

	private YFCLogCategory log = YFCLogCategory.instance(VSIAwaitingPaymentStatus.class);
	YIFApi api;
	public void vsiAwaitingPaymentStatus(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIAwaitingPaymentStatus================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

			Element rootElement = inXML.getDocumentElement();
			String orderHeaderKey = rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
			Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
			Element orderStatusElement = changeOrderStatusInput.getDocumentElement();
			orderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "3200.400");
			orderStatusElement.setAttribute(VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
			orderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			orderStatusElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
			orderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "AwaitingPayment.0001.ex");
			
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
			
			
			
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
		}
	
	}
}
