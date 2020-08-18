package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSITransferOrderFromJDA implements YIFCustomApi {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSITransferOrderFromJDA.class);
	YIFApi api;
		@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

		public void vsiTransferOrderCreate(YFSEnvironment env, Document inXML)
				throws Exception {
			if(log.isDebugEnabled()){
				log.info("================Inside vsiTransferOrderCreate================================");
				log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
			}
			
			try{
			Element rootElement = inXML.getDocumentElement();

			//get OrderNo from Input XML
			String orderNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String transferNo = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);
			
			//env.setTxnObject(orderNo, transferNo); // Setting the obj
			
			
			/*String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
			String dType=VSIConstants.DOCUMENT_TYPE;
			String selectMethod=VSIConstants.SELECT_METHOD_WAIT;*/
			
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element orderElement = getOrderListInput.getDocumentElement();
			orderElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
			
			api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			Element orderElementFromList = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			
			String chainedOrderKey = orderElementFromList.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
			Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
			Element orderLineElement = getOrderLineListInput.getDocumentElement();
			orderLineElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, chainedOrderKey);
			api = YIFClientFactory.getInstance().getApi();
			Document lineListOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
			Element orderLineListElement = (Element) lineListOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey = orderLineListElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);

			Document changeOrderInput = XMLUtil.createDocument("Order");
			Element changeOrderElement = changeOrderInput.getDocumentElement();
			changeOrderElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, transferNo);
			changeOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		  	
			api = YIFClientFactory.getInstance().getApi();
			 api.invoke(env, VSIConstants.API_CHANGE_ORDER,changeOrderInput);
			
			
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
			
		
		}//end vsiTransferOrderCreate
}
