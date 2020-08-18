package com.vsi.oms.api.order;

/**
 * This class is used to stamp the ExtnTotalPointsEarned & ExtnPointsEarned at Order/Extn
 * This is invoked in VSIOrderVerify service just before the changeOrder API call
 * 
 */
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIStampExtnAttributes implements VSIConstants{
	
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIStampExtnAttributes.class);
	
	public Document vsiStampExtnAttribs(YFSEnvironment env, Document changeOrderIpDoc) {
		if(log.isDebugEnabled()){
			log.info("*** Inside VSIStampExtnAttributes ***");
			log.debug("*** Printing Input XML to VSIStampExtnAttributes:\n "+XMLUtil.getXMLString(changeOrderIpDoc));
		}
		if(changeOrderIpDoc != null){
			String strShipNode = "";
			String strEntryType = "";
			String strOrderType = "";
			//changeOrderIpDoc = stampReqShipDate(env,changeOrderIpDoc);
			Element changeOrdEle = changeOrderIpDoc.getDocumentElement();
			
			//BOP400 fix
			strEntryType = changeOrdEle.getAttribute("EntryType");
			strOrderType = changeOrdEle.getAttribute("OrderType");
			
			
			NodeList orderLinesNL = changeOrdEle.getElementsByTagName("OrderLine");
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			Calendar cal = Calendar.getInstance();
			//String strReqShipDate = dateFormat.format(cal.getTime());
			for(int k=0; k < orderLinesNL.getLength() ; k ++ ){
				Element orderLineEle = (Element) orderLinesNL.item(k);
				if(!YFCObject.isVoid(orderLineEle)){
					strShipNode = orderLineEle.getAttribute("ShipNode");
					String strLineType = orderLineEle.getAttribute("LineType");
					String reqDelDate = orderLineEle.getAttribute("ReqDeliveryDate");
					if(!YFCObject.isVoid(reqDelDate) && "SHIP_TO_STORE".equalsIgnoreCase(strLineType)){
						orderLineEle.setAttribute("ReqShipDate",reqDelDate);
					}
				}
			}
			Element eleShipNode = (Element)changeOrdEle.getElementsByTagName("Shipnode").item(0);
			
			if(!YFCObject.isVoid(eleShipNode)){				
				
				changeOrdEle.appendChild(eleShipNode);
				
			}
			
			//stamping calendar details
			if(!YFCObject.isVoid(strShipNode)){
				Document getCalInDoc;
				try {
					getCalInDoc = XMLUtil.createDocument("Calendar");
				
				Element rootEle = getCalInDoc.getDocumentElement();

				rootEle.setAttribute("CalendarId", strShipNode);
				rootEle.setAttribute("OrganizationCode", "VSI");

				Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarList", getCalInDoc);
				if(getCalOutDoc != null){
					NodeList calListNL = getCalOutDoc.getElementsByTagName("Calendar");
					int calCnt = calListNL.getLength();
					if(calCnt > 0){
						Element calEle = (Element) calListNL.item(0);
						Node copyCalNode = changeOrderIpDoc.importNode(calEle, true);
						changeOrdEle.appendChild(copyCalNode);
					}

				}
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YFSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (YIFClientCreationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			}
			
			//BOP400 fix
			if(!"WEB".equalsIgnoreCase(strEntryType)){
			changeOrderIpDoc = stampExtnPoints(env,changeOrderIpDoc);
			}
			
			
			
		}
		if(log.isDebugEnabled()){
			log.info("*** Exiting class VSIStampExtnAttributes:\n "+XMLUtil.getXMLString(changeOrderIpDoc));
		}
		return changeOrderIpDoc;
	}
	
	public Document stampExtnPoints(YFSEnvironment env, Document inXML){
		String custID = null;
		String extnTotPtsErnd = "";
		Element extnOrdEle = null;
		Element rootEle = inXML.getDocumentElement();
		NodeList extnEleNL = rootEle.getElementsByTagName("Extn");
		int eleCount = extnEleNL.getLength();
		if(eleCount > 0){
			extnOrdEle = (Element) rootEle.getElementsByTagName("Extn").item(0);
		}else{
			extnOrdEle = inXML.createElement("Extn");
			rootEle.appendChild(extnOrdEle);
		}
		
		if(rootEle.hasAttribute("BillToID")){
			custID = rootEle.getAttribute("BillToID");
			//changeOrdEle.removeAttribute("BillToID");
		}
		
		if(custID != null && !custID.trim().equalsIgnoreCase("")){
			try {
				  Document healthyRewardsIPXML = XMLUtil.createDocument("Customer");
				  healthyRewardsIPXML.getDocumentElement().setAttribute("HealthyAwardsNo", custID);
				  
				  if(log.isDebugEnabled()){
					  log.debug(" healthyRewardsIPXML : "+XMLUtil.getXMLString(healthyRewardsIPXML));
				  }
				  
					Document healthyRewardsOPXML = VSIUtils.invokeService(env, "VSIGetCustRewardPoints", healthyRewardsIPXML);
					if(healthyRewardsOPXML != null ){
						extnTotPtsErnd = YFCDocument.getDocumentFor(healthyRewardsOPXML).getDocumentElement()
				 				.getChildElement("soap:Body")
				 				.getChildElement("GetCustomerRewardAvailablePointsResponse")
				 				.getChildElement("GetCustomerRewardAvailablePointsResult")
				 				.getNodeValue();
						
						
						
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		extnOrdEle.setAttribute("ExtnTotalPointsEarned", extnTotPtsErnd);
		
		//TBD ----   plugin logic to get the ExtnPointsEarned -- Neha service
		try {
			if(log.isDebugEnabled()){
				log.info(XMLUtil.getXMLString(inXML));
			}
			Document getCustomerRewardPointsDoc = VSIUtils.invokeService(env, "VSICustomerRewardPoint", inXML);
			if(log.isDebugEnabled()){
				log.info(XMLUtil.getXMLString(getCustomerRewardPointsDoc));
			}
			String extnPointsEarned = "";
			if(getCustomerRewardPointsDoc != null){
				rootEle = getCustomerRewardPointsDoc.getDocumentElement();
				if(rootEle.hasAttribute("TotalRewardsPoint")){
					extnPointsEarned = rootEle.getAttribute("TotalRewardsPoint");
				}
			}
			extnOrdEle.setAttribute("ExtnPointsEarned", extnPointsEarned);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return inXML;
		
	}
	
	public Document stampReqShipDate(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, ParserConfigurationException, YIFClientCreationException{
		Element rootEle = inXML.getDocumentElement();
		NodeList orderLinesNL = rootEle.getElementsByTagName("OrderLine");
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
		Calendar cal = Calendar.getInstance();
		String strReqShipDate = dateFormat.format(cal.getTime());
		String strShipNode = "";
		for(int k=0; k < orderLinesNL.getLength() ; k ++ ){
			Element orderLineEle = (Element) orderLinesNL.item(k);
			String strLineType = orderLineEle.getAttribute("LineType");
			String reqDelDate = orderLineEle.getAttribute("ReqDeliveryDate");
			if(!YFCObject.isVoid(reqDelDate) && "SHIP_TO_STORE".equalsIgnoreCase(strLineType)){
				orderLineEle.setAttribute("ReqShipDate",reqDelDate);
			}
			strShipNode = orderLineEle.getAttribute("ShipNode");
			if(k == 0){
				NodeList shipNodeNL = orderLineEle.getElementsByTagName("Shipnode");
				if(shipNodeNL.getLength() > 0){
					Element shipNodeEle = (Element) shipNodeNL.item(0);
					Node copyShipNde = inXML.importNode(shipNodeEle, true);
					rootEle.appendChild(copyShipNde);
					
				}
			}
			
		}
		
		//stamping calendar details
		if(!YFCObject.isVoid(strShipNode)){
			Document getCalInDoc = XMLUtil.createDocument("Calendar");
						rootEle.setAttribute("CalendarId", strShipNode);
			rootEle.setAttribute("OrganizationCode", "VSI");

			Document getCalOutDoc = VSIUtils.invokeAPI(env, "global/template/api/VSIGetCalendarDetails.xml", "getCalendarList", getCalInDoc);
			if(getCalOutDoc != null){
				NodeList calListNL = getCalOutDoc.getElementsByTagName("Calendar");
				int calCnt = calListNL.getLength();
				if(calCnt > 0){
					Element calEle = (Element) calListNL.item(0);
					Node copyCalNode = inXML.importNode(calEle, true);
					rootEle.appendChild(copyCalNode);
				}

			}

		}
		return inXML;
		
	}
	

}
