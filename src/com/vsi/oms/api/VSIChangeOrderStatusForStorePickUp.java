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

public class VSIChangeOrderStatusForStorePickUp {

	private YFCLogCategory log = YFCLogCategory.instance(VSIChangeOrderStatusForStorePickUp.class);
	YIFApi api;
	public void vsiChangeOrderStatus(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIChangeOrderStatusForStorePickUp================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

	
			
			Element orderElem = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			String orderType=orderElem.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			
			if(orderType.equals("STORE")){
				
				Document changeOrderStatusInput = XMLUtil.createDocument("OrderStatusChange");
				Element changeOrderStatusElement = changeOrderStatusInput.getDocumentElement();
				changeOrderStatusElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
				changeOrderStatusElement.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "1100.500");
				changeOrderStatusElement.setAttribute(VSIConstants.ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, "Y");
				changeOrderStatusElement.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "AwaitingCustPickup.0001.ex");
				changeOrderStatusElement.setAttribute(VSIConstants.ATTR_SELECT_METHOD, "WAIT");
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,changeOrderStatusInput);
				
			}
			
			
	
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
	/*Created By Saraswathy on 21st Aug to check build */
	public void testBuild(){
		log.info("===============****Added To Test Build********================================");
		
	}
}
	
