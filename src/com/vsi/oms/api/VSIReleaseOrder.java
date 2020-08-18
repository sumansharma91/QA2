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

public class VSIReleaseOrder {

	private YFCLogCategory log = YFCLogCategory.instance(VSIReleaseOrder.class);
	YIFApi api;
	public void vsiReleaseOrder(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside vsiReleaseOrder================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{

	
			//Element rootElement = inXML.getDocumentElement();
			Element itemElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_ITEM).item(0);
			String reference1=itemElement.getAttribute(VSIConstants.ATTR_REFERENCE_1);
			if(reference1 != null){
			Document getShipmentListInput = XMLUtil.createDocument("Shipment");
			Element shipmentElement = getShipmentListInput.getDocumentElement();
			shipmentElement.setAttribute(VSIConstants.ATTR_PICK_TICKET_NO, reference1);//transferNo sent from JDA is stored in PickticketNo 
			env.setApiTemplate(VSIConstants.API_GET_SHIPMENT_LIST, "global/template/api/getShipmentListForRelease.xml");
			api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);
			
			Element rootElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_SHIPMENT).item(0);
			String customerPoNo = rootElement.getAttribute(VSIConstants.ATTR_SHIP_CUST_PO_NO);
			
			String eCode=VSIConstants.VSICOM_ENTERPRISE_CODE;
			String dType=VSIConstants.DOCUMENT_TYPE;
			if(log.isDebugEnabled()){
				log.debug("CustomerPONo: "+ customerPoNo);
			}
					////System.out.println("CustomerPONo: "+ customerPoNo);
			
					Document getOrderListInput = XMLUtil.createDocument("Order");
					Element eleOrder = getOrderListInput.getDocumentElement();
					eleOrder.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
					eleOrder.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, eCode);
					eleOrder.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, dType);
			//Calling the getOrderList API with CustomerPONo,EnterpriseCode and DocumentType		
						api = YIFClientFactory.getInstance().getApi();
						Document orderOutDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
			
						Element orderElement = (Element) orderOutDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
						String orderHeaderKey=orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
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
						"Reference_1 data error");
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
	
