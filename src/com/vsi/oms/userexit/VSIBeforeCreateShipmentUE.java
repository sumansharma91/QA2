package com.vsi.oms.userexit;

import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

/**
 * @author IBM
 * 
 * 
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ydm.japi.ue.YDMBeforeCreateShipment;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class VSIBeforeCreateShipmentUE implements YDMBeforeCreateShipment{
	
	private YFCLogCategory log = YFCLogCategory.instance(YDMBeforeCreateShipment.class);

	@Override
	public Document beforeCreateShipment(YFSEnvironment env, Document inDoc) throws YFSUserExitException {

		String strExtnTransactionNo = "";
		String strShipNodePadded= "";
		
		
		try {
			if(log.isDebugEnabled()){
				log.debug("VSIBeforeCreateShipmentUE : input : " + XMLUtil.getXMLString(inDoc));
			}
			
			Element eleShipment=inDoc.getDocumentElement();
			String strDocumentType=eleShipment.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			//OMS-1049 : Start
			eleShipment.setAttribute(VSIConstants.ATTR_ACTUAL_SHIP_DATE, eleShipment.getAttribute("ActualShipDate"));
			//OMS-1049 : End
			if(!YFCCommon.isStringVoid(strDocumentType) && "0001".equals(strDocumentType)){
				if(log.isDebugEnabled()){
					log.debug("Shipment is a sales order shipment ,stamping ExtnTransactionNo");
				}
				//Invoke getOrderList and fetch the order details
				Element eleOrder=fetchOrderDetails(env,eleShipment);
				if(log.isDebugEnabled()){
					log.debug("fetchOrderDetails output"+XMLUtil.getElementXMLString(eleOrder));
				}
				Element eleOrderExtn=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_EXTN);
				String strExtnDTCOrder = eleOrderExtn.getAttribute("ExtnDTCOrder");
				String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				String strShipNode=eleShipment.getAttribute(VSIConstants.ATTR_SHIP_NODE);
				String strExtnSubscriptionOrder = eleOrderExtn.getAttribute("ExtnSubscriptionOrder");
				String strExtnOriginalADPOrder = eleOrderExtn.getAttribute("ExtnOriginalADPOrder");
				String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				String strEnteredBy=eleOrder.getAttribute(VSIConstants.ATTR_ENTERED_BY);
				/* OMS-1220 changes : start*/
				String strExtnReOrder = eleOrderExtn.getAttribute("ExtnReOrder");
				/*OMS-1220 changes : end */
				NodeList nlOrderLine=eleOrder.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				String strDeliveryMethod=null;
				if(nlOrderLine!=null){
					Element eleOrderLine=(Element)nlOrderLine.item(0);
					strDeliveryMethod=eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
					
				}
				if(YFCCommon.isVoid(strDeliveryMethod) && "Y".equals(strExtnDTCOrder))
				{
					strDeliveryMethod = VSIConstants.ATTR_DEL_METHOD_SHP;
				}
				
				
				//logic to set the ExtnTransactionNo
				String seqNum =VSIConstants.SEQ_VSI_SEQ_TRANID;
				if(log.isDebugEnabled()){
					log.debug("seqNum"+seqNum);
				}
				/*OMS-1220 changes :start*/
				if(!YFCCommon.isVoid(strExtnReOrder) && VSIConstants.FLAG_Y.equals(strExtnReOrder) && VSIConstants.ATTR_DEL_METHOD_SHP.equalsIgnoreCase(strDeliveryMethod))
				{
					seqNum = seqNum +"92_";
					if(log.isDebugEnabled()){
						log.debug("ReOrders, seqNum"+seqNum);
					}
				} 
				/*OMS-1220 changes :end*/
				else if(!YFCCommon.isVoid(strExtnSubscriptionOrder) && "Y".equals(strExtnSubscriptionOrder))
				{
					seqNum = seqNum +"91_";
					if(log.isDebugEnabled()){
						log.debug("AutoDeliveryOrders, seqNum"+seqNum);
					}
				} // Auto delivery orders get 91
				else if(!YFCCommon.isVoid(strExtnOriginalADPOrder) && "Y".equals(strExtnOriginalADPOrder))
				{
					seqNum = seqNum +"9_";
					if(log.isDebugEnabled()){
						log.debug("ADP Orders seqNum"+seqNum);
					}
				}
				else if(!YFCCommon.isVoid(strOrderType) && strOrderType.equalsIgnoreCase(VSIConstants.ATTR_ORDER_TYPE_POS)){
					seqNum = seqNum+VSIConstants.ATTR_TRAN_SEQ_POS_PICK;
					if(log.isDebugEnabled()){
						log.debug("POS Orders seqNum"+seqNum);
					}
				}// All POS order get 88_
				else if(!YFCCommon.isVoid(strDeliveryMethod) && strDeliveryMethod.equalsIgnoreCase(VSIConstants.ATTR_DEL_METHOD_SHP)){
					seqNum = seqNum +VSIConstants.ATTR_TRAN_SEQ_SHP;
					if(log.isDebugEnabled()){
						log.debug("WEB Orders seqNum"+seqNum);
					}
				}//All WEB Ship orders 5_
				else 
				{
					seqNum = seqNum+VSIConstants.ATTR_TRAN_SEQ_WEB_PICK;
					if(log.isDebugEnabled()){
						log.debug("WEB PICK and STS Orders seqNum"+seqNum);
					}
				}// WEB PICK and STS gets 89_
				
				
				if("SHP".equalsIgnoreCase(strDeliveryMethod)){
					strShipNode=strEnteredBy;
				}
				if(strShipNode != null)
					
					strShipNodePadded = ("00000" + strShipNode).substring(strShipNode.trim().length());
				
				seqNum = seqNum+strShipNodePadded;
				
				if(log.isDebugEnabled()){
					log.debug("SeqNo after ShipNode padding:"+seqNum);
				}
				strExtnTransactionNo=VSIDBUtil.getNextSequence(env, seqNum);
				if(log.isDebugEnabled()){
					log.debug("TransactionNo generated for the order :  "+strExtnTransactionNo);
				}
				
				Element eleExtn=SCXmlUtil.getChildElement(eleShipment, VSIConstants.ELE_EXTN);
				if(eleExtn==null){
					eleExtn=SCXmlUtil.createChild(eleShipment, VSIConstants.ELE_EXTN);
					
				}
				eleExtn.setAttribute("ExtnTransactionNo", strExtnTransactionNo);
				
				//logic to stamp ShipmentSeqNo
				String strShipmentSeqNo=findShipmentSeqNo(env,strOrderHeaderKey);
				eleExtn.setAttribute("ShipmentSeqNo", strShipmentSeqNo);
				
				
			}
			
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			log.error("Error in VSIBeforeCreateShipmentUE  "+e.getMessage());
			throw new YFSException();
			
		}
		if(log.isDebugEnabled()){
		log.debug("VSIBeforeCreateShipmentUE : output : " + XMLUtil.getXMLString(inDoc));
		}
		return inDoc;
	}
	
	
	
	/**
	 * 
	 * 
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws ParserConfigurationException 
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 */
	
	
	private String findShipmentSeqNo(YFSEnvironment env, String strOrderHeaderKey) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		String strShipmentSeqNo="";
		int iShipmentSeqNo=1;
		Document docInput=XMLUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleShipmentIP=docInput.getDocumentElement();
		eleShipmentIP.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		
		if(log.isDebugEnabled()){
			log.debug("findShipmentSeqNo:Input for getShipmentList "+ XMLUtil.getXMLString(docInput));
		}
		Document docOutput=VSIUtils.invokeAPI(env, VSIConstants.API_GET_SHIPMENT_LIST, docInput);
		
		NodeList nlShipment =docOutput.getElementsByTagName(VSIConstants.ELE_SHIPMENT);
		int iShipmentCount=nlShipment.getLength();
		iShipmentSeqNo=iShipmentCount+1;
		if(log.isDebugEnabled()){
			log.debug("Shipment count :" +iShipmentCount+" ShipmentSeqNo: "+iShipmentSeqNo);
		}
		strShipmentSeqNo=String.valueOf(iShipmentSeqNo);
		if(log.isDebugEnabled()){
			log.debug("findShipmentSeqNo :Output is : "+strShipmentSeqNo);
		}
		return strShipmentSeqNo;
	}




	/**
	 * 
	 * @param env
	 * @param eleShipment
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 */

	private Element fetchOrderDetails(YFSEnvironment env, Element eleShipment) throws ParserConfigurationException, YFSException, RemoteException, YIFClientCreationException {
		// TODO Auto-generated method stub
		if(log.isDebugEnabled()){
			log.debug("Inside fetchOrderDetails :Input  :"+XMLUtil.getElementXMLString(eleShipment));
		}
		String strOrderNo=eleShipment.getAttribute(VSIConstants.ATTR_ORDER_NO);
		String strEnterpriseCode=eleShipment.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		
		Document docInput=SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
		Element eleOrderIP=docInput.getDocumentElement();
		eleOrderIP.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
		eleOrderIP.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEnterpriseCode);
		eleOrderIP.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, "0001");
		
		if(log.isDebugEnabled()){
			log.debug("fetchOrderDetails:input for getOrderList "+ XMLUtil.getXMLString(docInput));
		}
		
		Document docOutput=VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_CREATE_SHIPMENT, VSIConstants.API_GET_ORDER_LIST, docInput);
		if(log.isDebugEnabled()){
			log.debug("getOrderList output"+XMLUtil.getXMLString(docOutput));
		}
		NodeList nlOrder=docOutput.getElementsByTagName(VSIConstants.ELE_ORDER);
		Element eleOrderOuput=null;
		if(nlOrder.getLength()>0)
			eleOrderOuput=(Element)nlOrder.item(0);
		
		if(log.isDebugEnabled()){
			log.debug("Output : fetchOrderDetails"+XMLUtil.getElementXMLString(eleOrderOuput));
		}
		return eleOrderOuput;
	}
	
}