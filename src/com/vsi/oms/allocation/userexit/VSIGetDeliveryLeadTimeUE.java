package com.vsi.oms.allocation.userexit;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;

import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import com.yantra.yfs.japi.YFSException;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetDeliveryLeadTimeUE;
import com.yantra.yfs.core.YFSSystem;


public class VSIGetDeliveryLeadTimeUE implements YFSGetDeliveryLeadTimeUE,VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetDeliveryLeadTimeUE.class);
	private static final String TAG = VSIGetDeliveryLeadTimeUE.class.getSimpleName();
	YIFApi api;

	@Override
	public Document getDeliveryLeadTime(YFSEnvironment env, Document docInput) throws YFSUserExitException {
		
		log.beginTimer("VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime : START");
		log.info("VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime : START");
		
		Element eleInput = docInput.getDocumentElement();
		Document docDelLeadTime = SCXmlUtil.createDocument(VSIConstants.ATTR_DEL_LEAD_TIME);
		Element eleDelLeadTime = docDelLeadTime.getDocumentElement();
		Element eleGetDeliveryleadTime = (Element)docInput.getElementsByTagName(VSIConstants.ELE_GET_DELIVERY_LEAD_TIME).item(0);
		String strShipFrom= eleGetDeliveryleadTime.getAttribute(VSIConstants.ATTR_SHIP_NODE);
		String strCarrierServiceCode= eleGetDeliveryleadTime.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE);

		try {
			log.info("Input to VSIGetDeliveryLeadTimeUE API: "+SCXmlUtil.getString(docInput));
			
			// If transaction id matches schedule.0001 or release.0001, and env.getTxnObject(DeliveryLeadTime) is empty
			// perform below logic. Else exit. This UE should not be called for create order, scheduleOrderLines etc.
			Element elePersonInfoShipFrom = SCXmlUtil.getChildElement(eleInput, VSIConstants.ELE_PERSON_INFO_SHIP_FROM);
			Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleInput, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			
			
			if((VSIGeneralUtils.identifyCallingProgram(API_SCHEDULE_ORDER)||VSIGeneralUtils.identifyCallingProgram(TXN_SCHEDULE)||
					VSIGeneralUtils.identifyCallingProgram(API_RELEASE_ORDER)||VSIGeneralUtils.identifyCallingProgram(TXN_RELEASE)
					||VSIGeneralUtils.identifyCallingProgram(TXN_BACKORDER) ||VSIGeneralUtils.identifyCallingProgram(API_GETCARRIERSERVICEOPTIONS_FORORDERING)) 
					&& !YFCCommon.isVoid(elePersonInfoShipFrom)){
					//Below If clause is for Delivery Lead Time UE for new SFS flow
				
				if ((!strCarrierServiceCode.isEmpty()) && (strShipFrom.equalsIgnoreCase("9004") || strShipFrom.equalsIgnoreCase("9005") || strShipFrom.isEmpty()))
				{
			
					// For Parcel items with Ground/Standard Carrier Service, invoke getTimeInTransit API to fetch corresponding time in transit data.
					if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.STANDARD)
						|| eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.GROUND)) {
						// Create the input doc for getExtnTimeInTransit
					
						
						
						Document docGetTimeInTransit = SCXmlUtil.createDocument(VSIConstants.ELE_EXTN_TIME_IN_TRANSIT);
						Element eleGetTimeInTransit = docGetTimeInTransit.getDocumentElement();
						eleGetTimeInTransit.setAttribute(VSIConstants.ATTR_EXTN_CARRIER, eleInput.getAttribute(VSIConstants.ATTR_SCAC));
						eleGetTimeInTransit.setAttribute(VSIConstants.ATTR_EXTN_CARRIER_SERVICE_CODE,VSIConstants.GROUND);
						elePersonInfoShipFrom = SCXmlUtil.getChildElement(eleInput, VSIConstants.ELE_PERSON_INFO_SHIP_FROM);
						eleGetTimeInTransit.setAttribute(VSIConstants.ATTR_EXTN_SHIP_TO_ZIP_CODE,
								elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_SHORT_ZIP_CODE));
						eleGetTimeInTransit.setAttribute(VSIConstants.ATTR_EXTN_SHIP_FROM_ZIP_CODE,
								elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_SHORT_ZIP_CODE));
						
						// Call service to get the time in transit
						Document docTimeInTransitList = VSIUtils.invokeService(env, VSIConstants.SERVICE_GET_TIME_IN_TRANSIT, docGetTimeInTransit);
						Element eleTimeInTransitList = docTimeInTransitList.getDocumentElement();
						Element eleTimeInTranist = SCXmlUtil.getFirstChildElement(eleTimeInTransitList);
						if (!YFCObject.isVoid(eleTimeInTranist)) {
							eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME,
									eleTimeInTranist.getAttribute(VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT));
						} else {
							// If a zip code pair is not found in custom table, transit time is defaulted to 5 days(configurable) for lines with STANDARD service
							try {
								ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT,
										eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE), eleInput.getAttribute(VSIConstants.ATTR_SELLING_ORG_CODE));
								Element eleCommonCode = alCommonCodeList.get(0);
								eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION));
							} catch (Exception e) {
								log.error("Exception in VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime() while getting time in transit data from EXTN_TIME_IN_TRANSIT"
										+ "\nSetting time in transit to 5 days", e);
								eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.FIVE);
							}
						}
					}
					// If carrier service is 2 DAY or NEXTDAY, set transit time to 2 and 1 respectively
					else if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.SCAC_2DAY)) {
						eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.TWO);
					} else if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.SCAC_NEXTDAY)) {
						eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.ONE);
				} 
			}
				else if ((!strCarrierServiceCode.isEmpty()) && (!strShipFrom.equalsIgnoreCase("9004")) && (!strShipFrom.equalsIgnoreCase("9005")) && (!strShipFrom.isEmpty()))
				{
					
					if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.STANDARD)
						|| eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.GROUND))
					{
				
						String strShipFromAddressLine1=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_ADDRESS1);
						String strShipFromAddressLine2=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_ADDRESS2);
						String strShipFromCity=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_CITY);
						String strShipFromState=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_STATE);
						String strShipFromZip=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_ZIPCODE);
						String strShipFromPhone=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_DAY_PHONE);
						String strShipFromCountryCode=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_COUNTRY);
						String strShipFromFirstName=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_FIRST_NAME);
						String strShipFromLastName=elePersonInfoShipFrom.getAttribute(VSIConstants.ATTR_LAST_NAME);				 
						String strShipFromPerson=strShipFromFirstName+" "+strShipFromLastName;
					
						
						String strShipToAddressLine1=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ADDRESS1);
						String strShipToAddressLine2=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ADDRESS2);
						String strShipToCity=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_CITY);
						String strShipToState=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_STATE);
						String strShipToZip=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_ZIPCODE);
						String strShipToCountryCode=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_COUNTRY);
						String strShipToPhone=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_DAY_PHONE);
						String strShipToFirstName=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_FIRST_NAME);
						String strShipToLastName=elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_LAST_NAME);				 
						String strShipToPerson=strShipToFirstName+" "+strShipToLastName;
						
						
						
						
						Document docGetDelLeadTimeWebserviceInXML=XMLUtil.createDocument("CartonData");
						Element eleGetDelLeadTimeWebserviceInXML=docGetDelLeadTimeWebserviceInXML.getDocumentElement();
						
						//Adding Elements and Attributes to call Web service
						
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"DocumentType", "RateShop");
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ContainerType", "VS");
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"Service", "STANDARD");
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"OrderType", "STH");
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromFacilityID", strShipFrom);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromCompany", "Vitamin Shoppe");
						
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromAddressLine1", strShipFromAddressLine1);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromAddressLine2", strShipFromAddressLine2);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromCity", strShipFromCity);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromState", strShipFromState);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromZip", strShipFromZip);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromCountryCode", strShipFromCountryCode);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromPhone", strShipFromPhone);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipFromPerson", strShipFromPerson);
										
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToPerson", strShipToPerson);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToAddressLine1", strShipToAddressLine1);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToAddressLine2", strShipToAddressLine2);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToCity", strShipToCity);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToState", strShipToState);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToZip", strShipToZip);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToCountryCode", strShipToCountryCode);
						putElementValue(eleGetDelLeadTimeWebserviceInXML,"ShipToPhone", strShipToPhone);

						Document docGetDelLeadTimeWebserviceOut=invokeGetDelLeadTimeWebservice(docGetDelLeadTimeWebserviceInXML);				
						Element eleGetDelLeadTimeWebserviceOut = docGetDelLeadTimeWebserviceOut.getDocumentElement();
						
						log.info("Output from  invokeGetDelLeadTimeWebservice : "+SCXmlUtil.getString(eleGetDelLeadTimeWebserviceOut));
						
						Element eleEstimatedDeliveryDate=SCXmlUtil.getChildElement(eleGetDelLeadTimeWebserviceOut, "EstimatedDaysToDeliver");
						
						String EstimatedDeliveryDate=eleEstimatedDeliveryDate.getTextContent();	
						
						if (EstimatedDeliveryDate.equalsIgnoreCase("ERR")) {
							eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, "4");
						}
						else{
							eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, EstimatedDeliveryDate);	
						}
					}
					// If carrier service is 2 DAY or NEXTDAY, set transit time to 2 and 1 respectively
					else if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.SCAC_2DAY)) 
					{
							eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.TWO);
					} 
					else if (eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE).equals(VSIConstants.SCAC_NEXTDAY))
					{
							eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.ONE);
					} 
				}
				
			//	else if (strCarrierServiceCode.isEmpty()){
			//		eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.TWO);
			//		eleDelLeadTime.setAttribute(VSIConstants.ATTR_TRANSIT_UNIT_OF_MEASURE, VSIConstants.HR);
			//	}
	
				}
		} 
		catch (Exception e) {
			log.error("Exception in VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime() while getting time in transit data from EXTN_TIME_IN_TRANSIT"
					+ "\nSetting time in transit to configured number of days for STANDARD service", e);
			// TODO: Are we configuring this in common codes?
			try {
				if (strShipFrom.equalsIgnoreCase("9004") || strShipFrom.equalsIgnoreCase("9005")) {
				ArrayList<Element> alCommonCodeList = VSIUtils.getCommonCodeList(env, VSIConstants.ATTR_EXTN_TIME_IN_TRANSIT,
						eleInput.getAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE), eleInput.getAttribute(VSIConstants.ATTR_SELLING_ORG_CODE));
				Element eleCommonCode = alCommonCodeList.get(0);
				eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION));
					}
				else {
					eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, "4");
					}
				} catch (Exception e1) {
				log.error("Exception in VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime() while getting time in transit data from EXTN_TIME_IN_TRANSIT"
						+ "\nSetting time in transit to 5 days", e1);
				eleDelLeadTime.setAttribute(VSIConstants.ATTR_DEL_LEAD_TIME, VSIConstants.FIVE);
					}
				}
		
		log.endTimer("VSIGetDeliveryLeadTimeUE.getDeliveryLeadTime : END");
		
		log.info(" Final output from VSIGetDeliveryLeadTimeUE END-- returned object: "+ XMLUtil.getXMLString(docDelLeadTime));	
		
		return docDelLeadTime;
	}
	
private Document invokeGetDelLeadTimeWebservice(Document docGetDelLeadTimeWebserviceInXML) throws IOException, ParserConfigurationException, SAXException {
	
		log.info("Printing input to Get Del Lead Time Webservice XML :"+SCXmlUtil.getString(docGetDelLeadTimeWebserviceInXML));
		
		String endPointURLInital=YFSSystem.getProperty("MINISOFT_SFS_WEBSERVICE_URL");		
		String inDocString=XMLUtil.getXMLString(docGetDelLeadTimeWebserviceInXML);
		Document responseDoc=null;
		DataOutputStream wr=null;
		String responseStr="";
		try {

			URL url = new URL (endPointURLInital);
			HttpURLConnection connection =  (HttpURLConnection) url.openConnection();			
			 connection.setRequestMethod("POST");
			 byte[] bContent = inDocString.getBytes();	
			 connection.setRequestProperty("Content-Type", "application/xml;charset=utf-8");
			 connection.setRequestProperty("Content-Type", String.valueOf(bContent.length));
			 connection.setDefaultUseCaches(false);
			 connection.setDoOutput(true);

			wr = new DataOutputStream (connection.getOutputStream());
			wr.write(bContent);
			wr.flush();
	        wr.close();
			log.info("Response Code from Webservice  = "+connection.getResponseCode());
			String responseStatus = connection.getResponseMessage();
			log.info("Response Status from Webservice  = "+responseStatus);
			
			if(connection.getResponseCode() == 200)
			 {
			      BufferedReader in = new BufferedReader(new InputStreamReader(
			        		connection.getInputStream()));
			        String inputLine;
			        StringBuffer response = new StringBuffer();
			        while ((inputLine = in.readLine()) != null) {
			            response.append(inputLine);
			        }
			        in.close();			      
			        responseStr= response.toString();			          
			 }
			 responseDoc=SCXmlUtil.createFromString(responseStr); 
		}
		catch (YFSException e) {
			log.info("Inside YFSException");
			e.printStackTrace();
			throw new YFSException();
		} catch (Exception e){
			log.info("Inside Exception");
			e.printStackTrace();
			throw new YFSException();
		}
		finally {
			 if(wr!=null){
				 wr.close();
			 }
		 }
		return responseDoc;
	}
	
	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}
	
	
	private void putElementValue(Element childEle, String key, Object value) {
		Element ele = SCXmlUtil.createChild(childEle, key);
		if(value instanceof String ) {
			ele.setTextContent((String)value);
		}else if(value instanceof Element ) {
			ele.appendChild((Element)value);
		}
	}
	
	private String getElementValue(Element element, String tagName) {
		String value="";
		if(element!=null) {
			Element ele = (Element) element.getElementsByTagName(tagName).item(0);
			if(ele!=null) {
				value=ele.getTextContent();
			}
		}
		return value;
	}
}
