package com.vsi.oms.api;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * VSIQASWebservice class will search, refine and get the valid address for the customer.
 * 
 * @author IBM
 *
 */
public class VSIQASWebservice {

	private YFCLogCategory log = YFCLogCategory.instance(VSIQASWebservice.class);
	String strSoapAction = "http://www.qas.com/OnDemand-2011-03/";
	String strAction = "";
	private boolean isLine2Refine = false;
	private boolean isCanBlankRefine = false;
	private String country = "";
	private String monickFromCANSearch = "";
	/**
	 * triggerQAS() method will trigger the QAS web service to search, refine
	 * or get address for the customer.
	 * 
	 * @param env
	 * @param inXML	 * 
	 * @return Document 
	 * @throws ParserConfigurationException 
	 * @throws Exception
	 */
	public Document triggerQAS(YFSEnvironment env, Document inXML) throws ParserConfigurationException {

		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside doSearchQAS================================");
		}
		
		Document responseDoc = null;

		//call the method to prepare the input for QAS Web Service based on Action
		Document requestDoc = preparedInputBasedOnAction(inXML);
		String strInputXml = SCXmlUtil.getString(requestDoc);

		//Craete Output document
		Document outXML = SCXmlUtil.createDocument("GetAddress");
		Element eleOutput = outXML.getDocumentElement();
		try {
			responseDoc = InvokeWebservice.invokeQASWebService(strInputXml, strSoapAction);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "QAS is down.Address cannot be verified at the moment");
			return outXML;
		} 
		
		//Invoke QAS Web Service
		
		//System.out.println("triggerQAS Response>>>>>> " + SCXmlUtil.getString(responseDoc));


		
		// call the method to set the output data
		setDataBasedOnAction(responseDoc, eleOutput);
		
		
		// If Canada do one more refinement with empty element.
		if("CAN".equals(country) && 
				"Search".equals(strAction) && !eleOutput.hasAttribute("ErrorMessage") )
		{
			//Document outXML1 = SCXmlUtil.createDocument("GetAddress");
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
			Document outXML1 = db.newDocument();
			
			Element newDoc = (Element)outXML1.importNode(eleOutput, true);
			outXML1.appendChild(newDoc);
			//System.out.println("NEW DOCUMENT IS"+SCXmlUtil.getString(outXML1));
			//Element eleOut1 = SCXmlUtil.importElement(outXML1.getDocumentElement(), eleOutput);
			Element eleOut1 = outXML1.getDocumentElement();
			eleOut1.setAttribute("Action", "Refine");
			isCanBlankRefine = true;
			
			requestDoc = preparedInputBasedOnAction(outXML1);
			strInputXml = SCXmlUtil.getString(requestDoc);
			//System.out.println("input going in for second time"+strInputXml);
			//System.out.println("new output"+SCXmlUtil.getString(eleOut1));
			//System.out.println("original"+SCXmlUtil.getString(eleOutput));
			//Invoke QAS Web Service
			strSoapAction = "http://www.qas.com/OnDemand-2011-03/";
			strSoapAction = strSoapAction.concat("DoRefine");

			try {
				responseDoc = InvokeWebservice.invokeQASWebService(strInputXml, strSoapAction);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				//throw new YFSException("EXTN_ERROR","EXTN_ERROR", "QAS is down.Address cannot be verified at the moment");
				eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "QAS is down.Address cannot be verified at the moment");
				return outXML;
			} 
			
			//System.out.println("triggerQAS Response to get final address after Address Line 2 refine>>>>>> " + SCXmlUtil.getString(responseDoc));
			setDataBasedOnAction(responseDoc,eleOut1);
			//SCXmlUtil.getChildElement(responseDoc, childName)
			eleOutput.setAttribute("Action", "Search");
			Element addressList = SCXmlUtil.getChildElement(eleOutput, "AddressList");
			Element addressEle = SCXmlUtil.getChildElement(addressList, "Address");
			//System.out.println("moniker we got is"+monickFromCANSearch);
			addressEle.setAttribute("Moniker", monickFromCANSearch);
			
		}
		
		if(isLine2Refine && !eleOutput.hasAttribute("ErrorMessage"))
		{
			isLine2Refine = false;
			eleOutput.setAttribute("Action", "GetAddress");
			requestDoc = preparedInputBasedOnAction(outXML);
			strInputXml = SCXmlUtil.getString(requestDoc);

			//Invoke QAS Web Service
			strSoapAction = "http://www.qas.com/OnDemand-2011-03/";
			strSoapAction = strSoapAction.concat("DoGetAddress");
			//Create Output document
			outXML = SCXmlUtil.createDocument("GetAddress");
			eleOutput = outXML.getDocumentElement();
			try {
				responseDoc = InvokeWebservice.invokeQASWebService(strInputXml, strSoapAction);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "QAS is down.Address cannot be verified at the moment");
				return outXML;
			} 
			
			//System.out.println("triggerQAS Response to get final address after Address Line 2 refine>>>>>> " + SCXmlUtil.getString(responseDoc));

			
			 setDataBasedOnAction(responseDoc,eleOutput);
			 
		}
		

		return outXML;
	}

	/**
	 * preparedInputBasedOnAction() Method will prepare QAS web service input
	 * based on the Action. Below is the sample Request XML.
	 * 
	 *******************
	 *  <GetAddress Action="Search/Refine/GetAddress"/>
	 *  	<AddressList>
	 *  		<Address Moniker="" AddressLine1="" ZipCode="" Country=""/>
	 *  	</AddressList>
	 *  </GetAddress>
	 *******************
	 *   
	 * @param inXML
	 * @return
	 */
	public Document preparedInputBasedOnAction(Document inXML) {

		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside preparedInputBasedOnAction================================");
		}

		//Iterate the Input xml and based on Action prepare Input
		Element eleInput = inXML.getDocumentElement();
		strAction = eleInput.getAttribute(VSIConstants.ATTR_ACTION);
		Element eleAddressList = SCXmlUtil.getChildElement(eleInput, "AddressList");
		Element eleAddress = SCXmlUtil.getChildElement(eleAddressList, "Address");

		//Prepare Input for QAS web service
		Document actionBasedQASInDoc = SCXmlUtil.createDocument("soapenv:Envelope");
		Element eleActionBasedQASInput = actionBasedQASInDoc.getDocumentElement();
		eleActionBasedQASInput.setAttribute("xmlns:soapenv", "http://schemas.xmlsoap.org/soap/envelope/");
		eleActionBasedQASInput.setAttribute("xmlns:ond", "http://www.qas.com/OnDemand-2011-03");
		Element eleBody = SCXmlUtil.createChild(eleActionBasedQASInput, "soapenv:Body");

		// If SOAP Action = doSearch 
		if("Search".equals(strAction)){
			strSoapAction = strSoapAction.concat("DoSearch");
			prepareDoSearchInput(eleBody,eleAddress);
		}else if("Refine".equals(strAction)){
			strSoapAction= strSoapAction.concat("DoRefine");
			prepareDoRefineInput(eleBody,eleAddress);
		}else if("GetAddress".equals(strAction)){
			strSoapAction = strSoapAction.concat("DoGetAddress");
			prepareDoGetAddressInput(eleBody,eleAddress);
		}
		//System.out.println("Action ::"+strAction+"\n"+"QAS Input>>>>"+ SCXmlUtil.getString(actionBasedQASInDoc));

		if(log.isDebugEnabled()){
			log.verbose("Printing QAS Web Service Request:" + SCXmlUtil.getString(actionBasedQASInDoc));
		}
		return actionBasedQASInDoc;
	}


	/**
	 * prepareDoSearchInput() method will prepare QASearch request
	 * document for QAS DoSearch action.
	 * 
	 * @param eleRequest
	 * @param eleAddress
	 */
	private void prepareDoSearchInput(Element eleRequest,Element eleAddress){
		Element eleQASearch = SCXmlUtil.createChild(eleRequest, "ond:QASearch");
		Element eleCountry = SCXmlUtil.createChild(eleQASearch, "ond:Country");
		country = eleAddress.getAttribute(VSIConstants.ATTR_COUNTRY);
		eleCountry.setTextContent(country);
		Element eleEngine = SCXmlUtil.createChild(eleQASearch, "ond:Engine");
		eleEngine.setAttribute("Flatten", "true");
		eleEngine.setAttribute("Intensity", "Close");
		eleEngine.setAttribute("PromptSet", "Default");
		eleEngine.setAttribute("Threshold", "100");
		eleEngine.setAttribute("Timeout", "10000");
		eleEngine.setTextContent("Typedown");
		Element eleSearch = SCXmlUtil.createChild(eleQASearch, "ond:Search");
		eleSearch.setTextContent(eleAddress.getAttribute(VSIConstants.ATTR_ZIPCODE));
		Element eleLayout = SCXmlUtil.createChild(eleQASearch, "ond:Layout");
		eleLayout.setTextContent("VSI");
		Element eleFormattedAddressInPicklist = SCXmlUtil.createChild(eleQASearch, "ond:FormattedAddressInPicklist");
		eleFormattedAddressInPicklist.setTextContent("true");

	}

	/**
	 * prepareDoRefineInput() method will prepare QARefine request
	 * document for QAS DoRefine action.
	 * 
	 * @param eleRequest
	 * @param eleAddress
	 */
	private void prepareDoRefineInput(Element eleRequest,Element eleAddress){
		Element eleQARefine = SCXmlUtil.createChild(eleRequest, "ond:QARefine");
		Element eleMoniker = SCXmlUtil.createChild(eleQARefine,"ond:Moniker");
		
		eleMoniker.setTextContent(eleAddress.getAttribute("Moniker"));
		Element eleRefinement = SCXmlUtil.createChild(eleQARefine,"ond:Refinement");
		eleRefinement.setAttribute("Threshold", "5");
		if(!YFCCommon.isVoid(eleAddress.getAttribute(VSIConstants.ATTR_ADDRESS1)))
		{
			eleRefinement.setTextContent(eleAddress.getAttribute(VSIConstants.ATTR_ADDRESS1));
			if(isCanBlankRefine)
			{
				eleRefinement.setTextContent("");
			}
		}
		if(!YFCCommon.isVoid(eleAddress.getAttribute(VSIConstants.ATTR_ADDRESS2)))
		{
			eleRefinement.setTextContent(eleAddress.getAttribute(VSIConstants.ATTR_ADDRESS2));
			isLine2Refine = true;
		}
		Element eleLayout = SCXmlUtil.createChild(eleQARefine, "ond:Layout");
		eleLayout.setTextContent("VSI");
		Element eleFormattedAddressInPicklist = SCXmlUtil.createChild(eleQARefine, "ond:FormattedAddressInPicklist");
		eleFormattedAddressInPicklist.setTextContent("false");
		if(log.isDebugEnabled()){
    		log.debug("refine request input is"+SCXmlUtil.getString(eleRequest));
		}
	}

	/**
	 * prepareDoGetAddressInput() method will prepare QAGetAddress request
	 * document for QAS DoGetAddress action.
	 * 
	 * @param eleRequest
	 * @param eleAddress
	 */
	private void prepareDoGetAddressInput(Element eleRequest,Element eleAddress){
		Element eleQAGetAddress = SCXmlUtil.createChild(eleRequest, "ond:QAGetAddress");
		Element eleLayout = SCXmlUtil.createChild(eleQAGetAddress, "ond:Layout");
		eleLayout.setTextContent("VSI");
		Element eleMoniker = SCXmlUtil.createChild(eleQAGetAddress,"ond:Moniker");
		eleMoniker.setTextContent(eleAddress.getAttribute("Moniker"));	
	}

	//Set the QAS WebService Output Data based on Action 
	/**
	 * setDataBasedOnAction() method will process web service response document
	 * to prepare final output for the consumer in below format.
	 * 
	 ********************
	 * <GetAddress ErrorMessage="" Action="Search/Refine/GetAddress">
	 * 	<AddressList>
	 * 		<Address PartialAddress="" Moniker="" AddressLine1="" AddressLine2=""
	 * 			  City="" State="" Country="" ZipCode=""/>
	 * 	</AddressList>
	 * </GetAddress>
	 ********************
	 *
	 * @param responseXML
	 * @param eleOutput
	 */
	public void setDataBasedOnAction(Document responseXML, Element eleOutput){
		if(log.isDebugEnabled()){
			log.verbose("Printing Output XML :" + SCXmlUtil.getString(responseXML));
			log.info("================Inside setDataBasedOnAction================================");
		}
		
		//Iterate the Output xml and set Data based on Action 
		Element eleResponse = responseXML.getDocumentElement();
		Element eleBody = SCXmlUtil.getChildElement(eleResponse, "soap:Body");

		eleOutput.setAttribute(VSIConstants.ATTR_ACTION, strAction);
		if("Search".equals(strAction)){
			setDoSearchOutput(eleBody,eleOutput);
		}else if("Refine".equals(strAction)){
			setDoRefineOutput(eleBody, eleOutput);
		}else if("GetAddress".equals(strAction)){
			setDoGetAddressOutput(eleBody,eleOutput);
		}
	}

	/**
	 * setDoSearchOutput() method will prepare response document for QAS DoSearch action.
	 * 
	 * @param eleResponseBody
	 * @param eleOutput
	 */
	private void setDoSearchOutput(Element eleResponseBody, Element eleOutput){
		Element eleAddressList = SCXmlUtil.createChild(eleOutput, "AddressList");
		Element eleAddress =  SCXmlUtil.createChild(eleAddressList, "Address");

		Element eleQASearchResult = SCXmlUtil.getChildElement(eleResponseBody, "QASearchResult");
		Element eleQAPicklist = SCXmlUtil.getChildElement(eleQASearchResult, "QAPicklist");
		Element elePicklistEntry = SCXmlUtil.getChildElement(eleQAPicklist, "PicklistEntry");
		Element eleMoniker = SCXmlUtil.getChildElement(elePicklistEntry, "Moniker");
		if(null != eleMoniker && null != eleMoniker.getTextContent() && eleMoniker.getTextContent().trim().length() > 0){
			eleAddress.setAttribute("Moniker",eleMoniker.getTextContent());
			Element elePicklist = SCXmlUtil.getChildElement(elePicklistEntry, "Picklist");
			String strPickList = elePicklist.getTextContent();
			String [] strArrPickList = strPickList.split(",");
			if (strArrPickList.length == 4)
			{
				eleAddress.setAttribute(VSIConstants.ATTR_ZIPCODE, strArrPickList[0].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_CITY, strArrPickList[2].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_STATE, strArrPickList[3].trim());
			}
			else if(strArrPickList.length == 3)
			{
				eleAddress.setAttribute(VSIConstants.ATTR_ZIPCODE, strArrPickList[0].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_CITY, strArrPickList[1].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_STATE, strArrPickList[2].trim());
			}
			else if(strArrPickList.length == 2)
			{
				eleAddress.setAttribute(VSIConstants.ATTR_ZIPCODE, strArrPickList[0].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_CITY, "");
				eleAddress.setAttribute(VSIConstants.ATTR_STATE, strArrPickList[1].trim());
				eleOutput.setAttribute("InfoMessage", "Multiple city matches found. Start typing address");
			}
			else if(strArrPickList.length == 1)
			{
				eleAddress.setAttribute(VSIConstants.ATTR_ZIPCODE, strArrPickList[0].trim());
				eleAddress.setAttribute(VSIConstants.ATTR_CITY, "");
				eleAddress.setAttribute(VSIConstants.ATTR_STATE, "");
				eleOutput.setAttribute("InfoMessage", "Multiple city / state matches found. Start typing address");
			}
			
		}else{
			Element elePicklist = SCXmlUtil.getChildElement(elePicklistEntry, "Picklist");
			eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, elePicklist.getTextContent());
		}
	}

	/**
	 * setDoRefineOutput() method will prepare response document for QAS DoRefine action.
	 * 
	 * @param eleResponseBody
	 * @param eleOutput
	 */
	private void setDoRefineOutput(Element eleResponseBody, Element eleOutput){
		Element eleAddressList = SCXmlUtil.createChild(eleOutput, "AddressList");
		
		Element eleResPicklist = SCXmlUtil.getChildElement(eleResponseBody, "Picklist");
		Element eleQAPicklist = SCXmlUtil.getChildElement(eleResPicklist, "QAPicklist");
		ArrayList<Element> arrListPicklistEntry = SCXmlUtil.getChildren(eleQAPicklist, "PicklistEntry");
		//Iterate over QAPicklist -> PicklistEntry
		for(Element elePicklistEntry : arrListPicklistEntry){
			String strUnresolvableRange = elePicklistEntry.getAttribute("UnresolvableRange");
			String strFullAddress = elePicklistEntry.getAttribute("FullAddress");
			String strCanStep = elePicklistEntry.getAttribute("CanStep");
			String information = elePicklistEntry.getAttribute("Information");
			if("CAN".equals(country) && "true".equals(strUnresolvableRange) && isCanBlankRefine)
			{
				continue;
			}
			if("CAN".equals(country) && !"true".equals(strUnresolvableRange) && isCanBlankRefine)
			{
				Element eleMoniker1 = SCXmlUtil.getChildElement(elePicklistEntry, "Moniker");
				
				if(null != eleMoniker1 && null != eleMoniker1.getTextContent() && eleMoniker1.getTextContent().trim().length() > 0){
					monickFromCANSearch = eleMoniker1.getTextContent();
					//System.out.println("monicker from partial address"+monickFromCANSearch);
				}
				if("true".equals(strFullAddress))
				{
					return;
				}
			}
			if(!isCanBlankRefine && !"true".equals(strUnresolvableRange)){
				Element eleMoniker = SCXmlUtil.getChildElement(elePicklistEntry, "Moniker");

				if(null != eleMoniker && null != eleMoniker.getTextContent() && eleMoniker.getTextContent().trim().length() > 0){
					Element eleAddress =  SCXmlUtil.createChild(eleAddressList, "Address");
					eleAddress.setAttribute("Moniker",eleMoniker.getTextContent());
					Element elePartialAddress = SCXmlUtil.getChildElement(elePicklistEntry, "PartialAddress");
					if(isLine2Refine)
					{
						eleAddress.setAttribute(VSIConstants.ATTR_ADDRESS2,elePartialAddress.getTextContent());
						Element elePicklist = SCXmlUtil.getChildElement(elePicklistEntry, "Picklist");
						//eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, elePicklist.getTextContent());
						//System.out.println("information value is"+information);
						if(!YFCCommon.isVoid(elePicklist.getTextContent())  && "true".equals(information))
						{
							eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "Address not recognized");
							return;
						}
					}
					else
					{
						eleAddress.setAttribute(VSIConstants.ATTR_ADDRESS1,elePartialAddress.getTextContent());
						if("true".equals(strCanStep))
						{
							eleAddress.setAttribute("InfoMessage", "Possible Apt/Suite Info found.");
						}
						Element elePicklist = SCXmlUtil.getChildElement(elePicklistEntry, "Picklist");
						if(!YFCCommon.isVoid(elePicklist.getTextContent())  && "true".equals(information))
						{
							eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "Address not recognized");
							return;
						}
					}
					
				}else{
					Element elePicklist = SCXmlUtil.getChildElement(elePicklistEntry, "Picklist");
					eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, elePicklist.getTextContent());
					return;
				}
			}
			
		}
		if(!eleAddressList.hasChildNodes())
		{
			eleOutput.setAttribute(VSIConstants.ATTR_ERROR_MESSAGE, "No matches found");
		}
	}

	/**
	 * setDoGetAddressOutput() method will prepare response document for QAS DoGetAddress action.
	 * 
	 * @param eleResponseBody
	 * @param eleInput
	 */
	private void setDoGetAddressOutput(Element eleBody, Element eleOutput){
		Element eleAddressList = SCXmlUtil.createChild(eleOutput, "AddressList");
		Element eleAddress =  SCXmlUtil.createChild(eleAddressList, "Address");
		//Iterate the Body of QAS Response
		Element eleResponseAddress = SCXmlUtil.getChildElement(eleBody, "Address");
		Element eleQAAddress = SCXmlUtil.getChildElement(eleResponseAddress, "QAAddress");
		ArrayList<Element> arrListAddressLine = SCXmlUtil.getChildren(eleQAAddress, "AddressLine");
		for(Element eleAddressLine : arrListAddressLine){
			Element eleLabel = SCXmlUtil.getChildElement(eleAddressLine, "Label");
			if(!YFCObject.isVoid(eleLabel))
			{
				Element eleLine = SCXmlUtil.getChildElement(eleAddressLine,"Line");
				String strLabel = eleLabel.getTextContent();
				if(VSIConstants.ATTR_ADDRESS1.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_ADDRESS1, eleLine.getTextContent().trim());
				}
				else if(VSIConstants.ATTR_ADDRESS2.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_ADDRESS2, eleLine.getTextContent().trim());
				}else if(VSIConstants.ATTR_CITY.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_CITY, eleLine.getTextContent().trim());
				}else if(VSIConstants.ATTR_STATE.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_STATE, eleLine.getTextContent().trim());
				}else if(VSIConstants.ATTR_ZIPCODE.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_ZIPCODE, eleLine.getTextContent().trim());
				}else if(VSIConstants.ATTR_COUNTRY.equals(strLabel)){
					eleAddress.setAttribute(VSIConstants.ATTR_COUNTRY, eleLine.getTextContent().trim());
				}
			}
		}
	}
}
