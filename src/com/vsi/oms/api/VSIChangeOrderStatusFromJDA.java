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

public class VSIChangeOrderStatusFromJDA {

	private YFCLogCategory log = YFCLogCategory.instance(VSIChangeOrderStatusFromJDA.class);
	YIFApi api;
	public Document vsiChangeOrderStatusFromJDA(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIChangeOrderStatusFromJDA================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

			Element rootElement = inXML.getDocumentElement();
			String orderNo = rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
			
			String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
			String dType=VSIConstants.DOCUMENT_TYPE;
			
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
			eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
			eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
			
			api = YIFClientFactory.getInstance().getApi();
			Document outDoc  = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			Element eleOrderLine = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
			String lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
			
			if(lineType.equals("SHIP_TO_STORE")){
				
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,inXML);
				
				
			}
			
			else if(lineType.equals("SHIP_TO_HOME")){
		
				Element rootEle = inXML.getDocumentElement();
				rootEle.setAttribute(VSIConstants.ATTR_BASE_DROP_STATUS, "1100.100");
				rootEle.setAttribute(VSIConstants.ATTR_TRANSACTION_ID, "JDAAck.0001.ex");
				api = YIFClientFactory.getInstance().getApi();
				api.invoke(env, VSIConstants.API_CHANGE_ORDER_STATUS,inXML);
			
			
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
		return inXML;
	
	}
}
