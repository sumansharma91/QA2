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

public class VSIStoreReceived {

	private YFCLogCategory log = YFCLogCategory.instance(VSIStoreReceived.class);
	YIFApi api;
	public void vsiStoreReceived(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIStoreReceived================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

	
			Element rootElement = inXML.getDocumentElement();
			Element orderElem = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String orderHeaderKey=orderElem.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element OrderElement = getOrderListInput.getDocumentElement();
			OrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
			env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/getOrderListStoreReceived.xml");
			api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			
			Element orderEle = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
			String minOrderStatus = orderEle.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
			if(minOrderStatus.equals("2160.400")){
				
				Document releaseOrderInput = XMLUtil.createDocument("ReleaseOrder");
				Element releaseOrderElement = releaseOrderInput.getDocumentElement();
				releaseOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,orderHeaderKey);
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_RELEASE_ORDER,releaseOrderInput);
				
			}
			
			
			else{
				throw new YFSException(
						"EXTN_ERROR",
						"EXTN_ERROR",
						"Input minOrderStatus not Matched");
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
}
	
