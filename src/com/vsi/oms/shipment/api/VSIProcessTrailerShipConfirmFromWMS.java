package com.vsi.oms.shipment.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessTrailerShipConfirmFromWMS extends VSIBaseCustomAPI implements VSIConstants {

	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessTrailerShipConfirmFromWMS.class);
	String enterpriseCode = "";
	String orderType = "";
	Document docOutputShipment = null;
	String uniqueTrailerID = "";

	public Document processTrailerShipConfirmFromWMS(YFSEnvironment env, Document inXML) throws Exception{

		setUniqueTrailerID(env, inXML);
		NodeList nlShipmentList = inXML.getElementsByTagName(ELE_SHIPMENT_LIST);
		VSIProcessShipConfirmFromWMS existingImpl = new VSIProcessShipConfirmFromWMS();

		for(int i = 0; i < nlShipmentList.getLength(); i++){

			Element eleShipmentList = (Element) nlShipmentList.item(i);
			existingImpl.processShipConfirmFromWMS(env, XMLUtil.createDocumentFromElement(eleShipmentList));
		}

		getOrderAttributes(env, inXML);

		if (orderType.equalsIgnoreCase(WHOLESALE)){
			
			if (!YFCCommon.isVoid(enterpriseCode)){

				Element eleCommonCode = (Element) (VSIUtils
						.getCommonCodeList(env, ATTR_VSI_WH_ASN_TYPE, enterpriseCode, ATTR_DEFAULT).get(0));
				if(!YFCObject.isVoid(eleCommonCode)){

					String strASNMode = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCObject.isVoid(strASNMode)){

						if(strASNMode.equalsIgnoreCase(STR_TRAILER)){

							sendTrailerLevelASNforWholeSale(env, inXML);
						}
					}
				}

				eleCommonCode = (Element) (VSIUtils
						.getCommonCodeList(env, VSI_WH_SEND_INV_MODE, enterpriseCode, ATTR_DEFAULT).get(0));
				if(!YFCObject.isVoid(eleCommonCode)){

					String strSendInvoiceMode = eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCObject.isVoid(strSendInvoiceMode)){
						if(strSendInvoiceMode.equalsIgnoreCase(STR_TRAILER)){ //|| strSendInvoiceMode.equalsIgnoreCase(ATTR_WH_INVOICE_LEVEL_ORDER)){

							createShipmentInvoiceForTrailer(env, inXML);
						}
					}
				}
			}
		}
		
		return inXML;
	}

	private void setUniqueTrailerID(YFSEnvironment env, Document inXML) {
		
		uniqueTrailerID = VSIUtils.getUniqueID();
		NodeList nlShipment = inXML.getElementsByTagName(ELE_SHIPMENT);
		for(int i = 0; i < nlShipment.getLength(); i++){
			
			Element eleShipment = (Element) nlShipment.item(i);
			String strTrailerNo = SCXmlUtil.getAttribute(eleShipment, ATTR_TRAILER_NO);
			// If WMS Trailer No is not present, set internal trailer ID
			if(YFCObject.isVoid(strTrailerNo)){
				SCXmlUtil.setAttribute(eleShipment, ATTR_TRAILER_NO, uniqueTrailerID);
			}
			Element eleExtn = SCXmlUtil.getChildElement(eleShipment, ELE_EXTN);
			if(YFCObject.isVoid(eleExtn)){
				
				eleExtn = SCXmlUtil.createChild(eleShipment, ELE_EXTN);
			}
			
			SCXmlUtil.setAttribute(eleExtn, ATTR_EXTN_UNIQUE_TRAILER_ID, uniqueTrailerID);
		}
	}

	private void sendTrailerLevelASNforWholeSale(YFSEnvironment env, Document inXML)
			throws Exception {

		log.beginTimer("VSIProcessTrailerShipConfirmFromWMS.TrailerLevelASNforWholeSale");

		Element eleShipment = (Element) inXML.getElementsByTagName(ELE_SHIPMENT).item(0);
		//String strTrailer = eleShipment.getAttribute(ATTR_TRAILER_NO);

		Document docShipment = SCXmlUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleShip = docShipment.getDocumentElement();
		Element eleExtn = SCXmlUtil.createChild(eleShip, ELE_EXTN);
		SCXmlUtil.setAttribute(eleExtn, ATTR_EXTN_UNIQUE_TRAILER_ID, uniqueTrailerID);
		//eleShip.setAttribute(ATTR_TRAILER_NO, strTrailer);
		docOutputShipment = VSIUtils.invokeService(env,
				SERVICE_GET_SHIPMENT_LIST_FOR_ASN, docShipment);
		
		NodeList nlShipment = docOutputShipment.getElementsByTagName(ELE_SHIPMENT);
		for(int i = 0; i < nlShipment.getLength(); i++){
			
			eleShipment = (Element) nlShipment.item(i);
	        String strSCAC= eleShipment.getAttribute(ATTR_SCAC);
	        if(!YFCObject.isVoid(strSCAC)){

	        	Document docGetScacList = SCXmlUtil.createDocument("Scac");
	        	Element eleGetScacList= docGetScacList.getDocumentElement();
	        	eleGetScacList.setAttribute("Scac", strSCAC);
	        	Document outDoc = VSIUtils.invokeAPI(env, VSIConstants.API_GET_SCAC_LIST, docGetScacList);
	        	if(!YFCObject.isNull(outDoc) && outDoc.getDocumentElement().hasChildNodes()){
	        		
	        		Element eleScacList= outDoc.getDocumentElement();
	        		Element eleSCAC = SCXmlUtil.getChildElement(eleScacList, "Scac");
	        		eleShipment.setAttribute("CarrierName",eleSCAC.getAttribute("ScacDesc")); //setting CarrierName at Shipment level
	        	}
	        }
		}
		
		if (!YFCCommon.isVoid(docOutputShipment)) {

			// send ASN msg for Trailer
			VSIUtils.invokeService(env, SERVICE_POST_ASN_MSG_TO_IIB, docOutputShipment);
			NodeList nlShipments = docOutputShipment.getDocumentElement()
					.getElementsByTagName(ELE_SHIPMENT);

			int totalShipmentinTrailer = nlShipments.getLength();


			// Set Extn Last Trailer Ship Flag for last
			// shipment in the trailer
			eleShipment = (Element) nlShipments.item(totalShipmentinTrailer - 1);
			String shipmentKey = eleShipment.getAttribute(ATTR_SHIPMENT_KEY);
			if (!YFCCommon.isVoid(shipmentKey)) {

				Document docInputChangeShipment = SCXmlUtil
						.createDocument(VSIConstants.ELE_SHIPMENT);
				Element eleChangeShipment = docInputChangeShipment.getDocumentElement();
				eleChangeShipment.setAttribute(ATTR_SHIPMENT_KEY,
						eleShipment.getAttribute(ATTR_SHIPMENT_KEY));
				eleExtn = SCXmlUtil.createChild(eleChangeShipment, ELE_EXTN);
				eleExtn.setAttribute(ATTR_EXTN_LAST_TRAILER_SHIP, FLAG_Y);
				VSIUtils.invokeService(env, SERVICE_CHANGE_SHIPMENT_FOR_ASN,
						docInputChangeShipment);
			}

		}


		log.endTimer("VSIProcessTrailerShipConfirmFromWMS.TrailerLevelASNforWholeSale");
	}
	
	private void createShipmentInvoiceForTrailer(YFSEnvironment env, Document inXML) throws Exception{

		Element eleShipment = null;
		
		if(YFCObject.isVoid(docOutputShipment)){

			eleShipment = (Element) inXML.getElementsByTagName(ELE_SHIPMENT).item(0);
			//String strTrailer = eleShipment.getAttribute(ATTR_TRAILER_NO);

			Document docShipment = SCXmlUtil.createDocument(VSIConstants.ELE_SHIPMENT);
			Element eleShip = docShipment.getDocumentElement();
			Element eleExtn = SCXmlUtil.createChild(eleShip, VSIConstants.ELE_EXTN);
			SCXmlUtil.setAttribute(eleExtn, VSIConstants.ATTR_EXTN_UNIQUE_TRAILER_ID, uniqueTrailerID);
			docOutputShipment = VSIUtils.invokeService(env,
					SERVICE_WHOLESALE_GET_SHIPMENT_LIST, docShipment);
		}
		
		NodeList nlShipment = docOutputShipment.getDocumentElement()
				.getElementsByTagName(ELE_SHIPMENT);
		for(int i = 0; i < nlShipment.getLength(); i++){
			
			eleShipment = (Element) nlShipment.item(i);
			String shipmentKey = SCXmlUtil.getAttribute(eleShipment, ATTR_SHIPMENT_KEY);
			if(!YFCObject.isVoid(shipmentKey)){
				
				// <Shipment ShipmentKey="" TransactionId="CREATE_SHMNT_INVOICE.0001"/>
				try{
					
					Document createShipmentInvoiceInXML = SCXmlUtil.createDocument(ELE_SHIPMENT);
					Element eleRoot = createShipmentInvoiceInXML.getDocumentElement();
					SCXmlUtil.setAttribute(eleRoot, ATTR_SHIPMENT_KEY, shipmentKey);
					SCXmlUtil.setAttribute(eleRoot, ATTR_TRANSACTION_ID, TRAN_ID_CREATE_SHMNT_INVOICE_0001);
					
					VSIUtils.invokeAPI(env, API_CREATE_SHIPMENT_INVOICE, createShipmentInvoiceInXML);	
				}catch(Exception e){
					
					throw new YFSException(e.getMessage());
				}
			}
		}
	}

	private void getOrderAttributes(YFSEnvironment env, Document inXML) throws Exception{

		Element eleTrailer = inXML.getDocumentElement();
		Element eleShipmentList = SCXmlUtil.getChildElement(eleTrailer, ELE_SHIPMENT_LIST);
		if(!YFCObject.isVoid(eleShipmentList)){

			Element eleOrder = SCXmlUtil.getChildElement(eleShipmentList, ELE_ORDER);
			if(!YFCObject.isVoid(eleOrder)){

				Document docGetOrderList = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
				Element eleGetOrderList = docGetOrderList.getDocumentElement();
				eleGetOrderList.setAttribute(ATTR_DOCUMENT_TYPE, eleOrder.getAttribute(ATTR_DOCUMENT_TYPE));
				eleGetOrderList.setAttribute(ATTR_ORDER_NO, eleOrder.getAttribute(ATTR_ORDER_NO));
				eleGetOrderList.setAttribute(ATTR_MAX_RECORDS, ONE);

				Document docOrderList = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST, docGetOrderList);
				Element eleOrderList = docOrderList.getDocumentElement();
				Element eleOrderOut = SCXmlUtil.getChildElement(eleOrderList, VSIConstants.ELE_ORDER);
				if (!YFCCommon.isVoid(eleOrderOut)) {
					enterpriseCode = eleOrderOut.getAttribute(ATTR_ENTERPRISE_CODE);
					orderType = eleOrderOut.getAttribute(ATTR_ORDER_TYPE);
				}
			}
		}
	}
}
