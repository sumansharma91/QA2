package com.vsi.oms.api.order;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIDBUtil;


import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class VSIConfirmShipmentFromJDABR2 {
	private YFCLogCategory log = YFCLogCategory.instance(VSIConfirmShipmentFromJDABR2.class);
	YIFApi api;
	public void vsiConfirmShipmentFromJDABR2(YFSEnvironment env, Document inXML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSIConfirmShipmentFromJDA================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(inXML));
		}
		
		try{
		Element rootElement = inXML.getDocumentElement();
		String statusAttribute = rootElement.getAttribute(VSIConstants.ATTR_STATUS);
		if(statusAttribute.equals("SHIPPED")){
			String containerNumber = rootElement.getAttribute(VSIConstants.ATTR_CONTAINER_NO);
			String transferOrderNo = rootElement.getAttribute(VSIConstants.ATTR_TRANSFER_NO);
			
			Element shipmentLineElement = (Element) inXML.getElementsByTagName(VSIConstants.ELE_SHIPMENT_LINE).item(0);
			
			String strCustPoNo = shipmentLineElement.getAttribute(VSIConstants.ATTR_ORDER_NO);
				Document getShipmentListInput = XMLUtil.createDocument("Shipment");
				Element shipmentElement = getShipmentListInput.getDocumentElement();
			
				shipmentElement.setAttribute(VSIConstants.ATTR_ORDER_NO, transferOrderNo);
    			Element containersElement = XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			XMLUtil.appendChild(getShipmentListInput, shipmentElement, VSIConstants.ELE_CONTAINERS, "");
    			Element containerElement = XMLUtil.appendChild(getShipmentListInput, containersElement, VSIConstants.ELE_CONTAINER, "");
    			containerElement.setAttribute(VSIConstants.ATTR_CONTAINER_NO, containerNumber);
			
    		api = YIFClientFactory.getInstance().getApi();
			Document outDoc = api.invoke(env, VSIConstants.API_GET_SHIPMENT_LIST,getShipmentListInput);
			Element shipmentListRoot = outDoc.getDocumentElement();
			String shipmentKey = shipmentListRoot.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY);
			
			Document confirmShipmentInput = XMLUtil.createDocument("Shipment");
			Element confirmShipmentElement = confirmShipmentInput.getDocumentElement();
			confirmShipmentElement.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, shipmentKey);
			
			
			api = YIFClientFactory.getInstance().getApi();
			api.invoke(env, VSIConstants.API_CONFIRM_SHIPMENT,confirmShipmentInput);
			
			callGetOrderLineList(env,strCustPoNo);
			
			
			
			
		}else{
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Input Status error");
		}
		
		
		}catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (YIFClientCreationException e) {
			e.printStackTrace();
		} catch (YFSException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	
			
		
	}	// end method
	
	private void callGetOrderLineList(YFSEnvironment env, String strCustPoNo) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		
		Double maxReqAmt = 0.0;
		int validLines = 0;
		Document getOrderLineListInput = XMLUtil.createDocument("OrderLine");
		Element eleOrderLine = getOrderLineListInput.getDocumentElement();
		eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPoNo);
		//eleOrderLine.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, "VSI.com");
		eleOrderLine.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		
		api = YIFClientFactory.getInstance().getApi();
		Document outDoc = api.invoke(env, VSIConstants.API_GET_ORDER_LINE_LIST,getOrderLineListInput);
		
		
		
		NodeList orderLineList = outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
		
		if(null != orderLineList){
		Element orderElement = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
		String orderHeaderKey=orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
		
		
		int linelength= orderLineList.getLength();
		
		for(int i=0;i<linelength;i++){
			
			Element orderLineEle1 = (Element) outDoc.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(i);
			//String strOrderLineKey = orderLineEle1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
		
			String minLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MIN_LINE_STATUS);
			String maxLineStatus = orderLineEle1.getAttribute(VSIConstants.ATTR_MAX_LINE_STATUS);
			Double dMinLine = Double.parseDouble(minLineStatus);
			Double dMaxLine = Double.parseDouble(maxLineStatus);

			if(dMinLine == 2160.200 && dMaxLine == 2160.200){
			
			String lineTotal = orderLineEle1.getAttribute(VSIConstants.ATTR_LINE_TOTAL);
			Double dLineTotal = Double.parseDouble(lineTotal);
			
			maxReqAmt+=dLineTotal;
			validLines++;
			////System.out.println("Sum of Lines"+maxReqAmt);
			
			}
			else{
				
				break;
				
			}
			if(validLines>0)
					createCallCTR(env,orderHeaderKey,strCustPoNo,maxReqAmt);
			
			
		}//end for
		
		}else{
			
			throw new YFSException();
		}
		
	
		
	}

	private void createCallCTR(YFSEnvironment env, String oHK, String custPoNo, Double maxReqAmt) throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {

		try{
		String maxRequestAmount = String.valueOf(maxReqAmt);
		String ctrSeqId = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_CC_CTR);
		String ChargeTransactionRequestId = ctrSeqId;
		Document chargeTransactionRequestInput = XMLUtil.createDocument("ChargeTransactionRequestList");
		Element ctrListEle = chargeTransactionRequestInput.getDocumentElement();
		ctrListEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, oHK);
		Element ctrEle =	chargeTransactionRequestInput.createElement("ChargeTransactionRequest");
		ctrListEle.appendChild(ctrEle);
		ctrEle.setAttribute("MaxRequestAmount", maxRequestAmount);
		ctrEle.setAttribute("ChargeTransactionRequestKey", custPoNo);
		ctrEle.setAttribute("ChargeTransactionRequestId", ChargeTransactionRequestId);
	
		////System.out.println("chargeTransactionRequestInput:"+XMLUtil.getXMLString(chargeTransactionRequestInput));
		
		api = YIFClientFactory.getInstance().getApi();
		api.invoke(env, VSIConstants.API_MANAGE_CHARGE_TRAN_REQ,chargeTransactionRequestInput);
		}
		catch (ParserConfigurationException e) {
                     e.printStackTrace();
              } catch (YIFClientCreationException e) {
                     e.printStackTrace();
              } catch (YFSException e) {
                     e.printStackTrace();
              } catch (RemoteException e) {
                     e.printStackTrace();
              } catch (Exception e) {
                     e.printStackTrace();
              }

		
	}	

}//end class
