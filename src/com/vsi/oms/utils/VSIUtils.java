package com.vsi.oms.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.xpath.CachedXPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * 
 * @author Perficient
 * 
 */

public class VSIUtils {
	public static String getDocumentXMLString(Document document) {
		return SCXmlUtil.getString(document);
	}

	public static String getElementXMLString(Element element) {
		return SCXmlUtil.getString(element);
	}

	public static Element getRootElement(Document document) {
		return document.getDocumentElement();
	}

	public static Document createEmptyDocument()
			throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance()
				.newDocumentBuilder().newDocument();
		return doc;
	}

	public static Document createDocument(String strRootElement)
			throws ParserConfigurationException {
		Document doc = SCXmlUtil.createDocument(strRootElement);
		return doc;
	}

	public static Document getDocFromElement(Element inputElement, boolean deep)
			throws ParserConfigurationException, Exception {
		Document outputDocument = createEmptyDocument();
		outputDocument.appendChild(outputDocument
				.importNode(inputElement, deep));
		return outputDocument;
	}

	public static double getDoubleFromString(Element element,
			String attributeName) {
		return SCXmlUtil.getDoubleAttribute(element, attributeName);
	}

	public static int getIntFromString(Element element, String attributeName) {
		return SCXmlUtil.getIntAttribute(element, attributeName);
	}

	public static List<Element> getElementListByXpath(Document inXML,
			String XPath) throws ParserConfigurationException,
			TransformerException {
		NodeList nodeList = null;
		List<Element> elementList = new ArrayList<Element>();
		CachedXPathAPI cachedXPathAPI = new CachedXPathAPI();
		nodeList = cachedXPathAPI.selectNodeList(inXML, XPath);
		int iNodeLength = nodeList.getLength();
		for (int iCount = 0; iCount < iNodeLength; iCount++) {
			Element node = (Element) nodeList.item(iCount);
			elementList.add(node);
		}
		return elementList;
	}

	/**
	 * Invokes a Sterling API - without output template
	 */
	public static Document invokeAPI(YFSEnvironment env, String apiName,
			Document inXMLDoc) throws YIFClientCreationException, YFSException,
			RemoteException {
		YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
		Document outXML = yifApi.invoke(env, apiName, inXMLDoc);
		return outXML;
	}

	/**
	 * Invokes a Sterling API - with output template
	 */
	public static Document invokeAPI(YFSEnvironment env, String templateName,
			String apiName, Document inDoc) throws YIFClientCreationException,
			YFSException, RemoteException {
		env.setApiTemplate(apiName, templateName);
		YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
		Document outXML = yifApi.invoke(env, apiName, inDoc);
		env.clearApiTemplate(apiName);
		return outXML;
	}
	
	
	/**
   * Invokes a Sterling Commerce API.
   * 
   * @param env
   *          Sterling Commerce Environment Context.
   * @param template
   *          Output template document for the API
   * @param apiName
   *          Name of API to invoke.
   * @param inDoc
   *          Input Document to be passed to the API.
   * @throws java.lang.Exception
   *           Exception thrown by the API.
   * @return Output of the API.
   */
  public static Document invokeAPI(YFSEnvironment env, Document template, String apiName,
      Document inDoc) throws YIFClientCreationException, YFSException, RemoteException {

    env.setApiTemplate(apiName, template);
    YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
    Document returnDoc = yifApi.invoke(env, apiName, inDoc);
    env.clearApiTemplate(apiName);
    return returnDoc;
  }
	
	
		/**
	 * Retrieves the property stored in the environment under a certain key.
	 * 
	 * @param env
	 *            Yantra Environment Context.
	 * @param key
	 *            Key to identify object in environment.
	 * @return Property retrieved from the environment under the given key.
	 */
	public static String getContextProperty(YFSEnvironment env, String key) {
		String value = null;
		Object obj = env.getTxnObject(key);
		if (obj != null)
			value = obj.toString();
		return value;
	}

	/**
	 * Invokes a Sterling Service.
	 * 
	 * @param env
	 *            Sterling Environment Context.
	 * @param serviceName
	 *            Name of Service to invoke.
	 * @param inDoc
	 *            Input Document to be passed to the Service.
	 * @throws java.lang.Exception
	 *             Exception thrown by the Service.
	 * @return Output of the Service.
	 */
	public static Document invokeService(YFSEnvironment env,
			String serviceName, Document inDoc) throws Exception {
		YIFApi yifApi = YIFClientFactory.getInstance().getLocalApi();
		return yifApi.executeFlow(env, serviceName, inDoc);
	}

	/**
	 * Returns the clone of an XML Document.
	 */
	public static Document cloneDocument(Document doc) throws Exception {
		return YFCDocument.parse(SCXmlUtil.getString(doc)).getDocument();
	}

	/**
	 * csc stands for Convert Special Character. Change &, <, ", ' into XML
	 * acceptable. Because it could be used frequently, it is short-named to
	 * 'csc'. Usually when a string is used for XML values, the string should be
	 * parsed first.
	 * 
	 * @param str
	 *            the String to convert.
	 * @return converted String with & to &amp;amp;, < to &amp;lt;, " to
	 *         &amp;quot;, ' to &amp;apos;
	 */
	public static String csc(String str) {
		if (str == null || str.length() == 0)
			return str;

		StringBuffer buf = new StringBuffer(str);
		int i = 0;
		char c;

		while (i < buf.length()) {
			c = buf.charAt(i);
			if (c == '&') {
				buf.replace(i, i + 1, "&amp;");
				i += 5;
			} else if (c == '<') {
				buf.replace(i, i + 1, "&lt;");
				i += 4;
			} else if (c == '"') {
				buf.replace(i, i + 1, "&quot;");
				i += 6;
			} else if (c == '\'') {
				buf.replace(i, i + 1, "&apos;");
				i += 6;
			} else if (c == '>') {
				buf.replace(i, i + 1, "&gt;");
				i += 4;
			} else
				i++;
		}
		return buf.toString();
	}
	
	/**
     * Checks if input value is null or empty string
     * @param inputValue Input Value
     * @return true - if null or empty string; false - otherwise
     */
    public static boolean isNullOrEmpty(String inputValue)
    {
        if (inputValue == null) return true;
        if (inputValue.trim().length() < 1) return true;
        return false;
    }
	public static Document getCommonCodeListInputForCodeValue(String sOrgCode,
			String codeValue) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeValue", codeValue);
		return docOutput;
	}
	
	
	public static String getCommonCodeLongDescriptionByCodeValue(YFSEnvironment env,
			String sOrgCode, String sCodeName)
			throws ParserConfigurationException {
		String sCommonCodeValue = "";
		Document docgetCCListInput = getCommonCodeListInputForCodeValue(
				sOrgCode, sCodeName);
		Document docCCList = getCommonCodeList(env,
				docgetCCListInput);
		Element eleComCode = null;
		if (docCCList != null) {
			eleComCode = (Element) docCCList.getElementsByTagName("CommonCode").item(0);
		}
		if (eleComCode != null) {
			sCommonCodeValue = eleComCode.getAttribute("CodeLongDescription");
		}
		return getEmptyCheckedString(sCommonCodeValue);
	}

	public static Document getCommonCodeList(YFSEnvironment env,
			Document docApiInput) {
		Document docApiOutput = null;
		try {
			YIFApi api;
			api = YIFClientFactory.getInstance().getApi();
			docApiOutput = api.invoke(env, "getCommonCodeList", docApiInput);
				
		} catch (Exception e) {
			e.printStackTrace();
		}

		return docApiOutput;
	}
	
	/**
	 * Gets the common code list for the given codeType/codeValue combination
	 * 
	 * @param env Environment variable
	 * @param codeType The code type for the common code, ie: REMORSE_PERIOD
	 * @param codeValue The code value for the common code, ie: Vendor
	 * @param strOrgCode The organization code the common code is defined under ie: CVSHealth
	 * @return Common code list for the given codeType/codeValue combination
	 * @throws Exception Exception
	 */
	public static ArrayList<Element> getCommonCodeList(YFSEnvironment env, String codeType, String codeValue, String strOrgCode) throws Exception {

		// Build XML input for getCommonCodeList API
		Document docGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
		Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, strOrgCode);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, codeType);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE, codeValue);

		// Call getCommonCodeList API with with the input: <CommonCode CodeType="<codeType>"  CodeValue="<codeValue>"/> to get the common codes
		docGetCommonCodeList = VSIUtils.invokeAPI(env, VSIConstants.API_COMMON_CODE_LIST, docGetCommonCodeList);

		// Get the list of common codes from the response document
		eleCommonCode = docGetCommonCodeList.getDocumentElement();

		ArrayList<Element> alCommonCodeList = SCXmlUtil.getChildren(eleCommonCode, VSIConstants.ELEMENT_COMMON_CODE);

		return alCommonCodeList;

	} // end getCommonCodeList method
	
	public static ArrayList<Element> getCommonCodeListWithCodeType(YFSEnvironment env, String codeType, String codeValue, String strOrgCode) throws Exception {

		// Build XML input for getCommonCodeList API
		Document docGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
		Element eleCommonCode = docGetCommonCodeList.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_ORG_CODE, strOrgCode);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, codeType);

		// Call getCommonCodeList API with with the input: <CommonCode CodeType="<codeType>"  CodeValue="<codeValue>"/> to get the common codes
		docGetCommonCodeList = VSIUtils.invokeAPI(env, VSIConstants.API_COMMON_CODE_LIST, docGetCommonCodeList);

		// Get the list of common codes from the response document
		eleCommonCode = docGetCommonCodeList.getDocumentElement();

		ArrayList<Element> alCommonCodeList = SCXmlUtil.getChildren(eleCommonCode, VSIConstants.ELEMENT_COMMON_CODE);

		return alCommonCodeList;

	} 
	
	
	public static String getEmptyCheckedString(String str) {
		if (isEmpty(str)) {
			return EMPTY_STRING;
		} else {
			return str.trim();
		}
	}
	
	public static final String EMPTY_STRING = "";

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0) ? true : false;
	}

	/**
	 * Creates and returns a YFSException using the given exception
	 * 
	 * @param e Exception
	 * @return YFSException
	 */
	public static YFSException getYFSException(Exception e) {
		YFSException yfsException = new YFSException();
		yfsException.setStackTrace(e.getStackTrace());
		yfsException.setErrorDescription(e.getLocalizedMessage());

		return yfsException;
	}

	/**
	 * Creates and returns a YFSException using the given exception, error code and error desc
	 * 
	 * @param e Original exception
	 * @param strErrorCode Error code
	 * @param strErrorDesc Error description
	 * @return YFSException
	 */
	public static YFSException getYFSException (Exception e, String strErrorCode, String strErrorDesc) {
		YFSException yfsException = new YFSException();
		yfsException.setStackTrace(e.getStackTrace());
		yfsException.setErrorCode(strErrorCode);
		yfsException.setErrorDescription(strErrorDesc);

		return yfsException;
	}
	
	/**
	 * This method will convert an exception stack trace to a string
	 * 
	 * @param exception Exception
	 * @return Exception stack trace as a string.
	 */	
	public static String strStackTrace(Exception exception){
		StringWriter strWriter = new StringWriter();
		PrintWriter printWriter = new PrintWriter(strWriter);
		exception.printStackTrace(printWriter);
		return strWriter.toString();
	} // end strStackTrace
	
	/**
	 * This method accepts a date and a number of days as Strings (days should be a String representation of an integer). 
	 * It calculates the new date by adding the number of days passed to the original date passed and returns this new
	 * date.
	 * 
	 * @param strDate The original date
	 * @param strDays The number of hours to add to the original date
	 * @return The new date calculated by adding the days passed to the original date passed
	 * @throws Exception Exception
	 */
	public static String addDaysToPassedDateTime(String strDate, String strDays) throws Exception {

		String newDate = null;
		// Input date will be passed to sterling in below format
		DateFormat sdf_tz = new SimpleDateFormat(VSIConstants.DT_STR_TS_FORMAT);
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD_T_HH_MM_SS);
		Calendar cal = Calendar.getInstance();
		int intDays = Integer.parseInt(strDays);
		Date parsedDate = null;

		// If input date is passed use it else use the current time stamp
		if (strDate != null && !"".equals(strDate)) {
			try {
				try {
					parsedDate = sdf_tz.parse(strDate);
				} catch (ParseException pe) {
					parsedDate = sdf.parse(strDate);
				}
			} catch (ParseException pe) {
				DateFormat sdf2 = new SimpleDateFormat(VSIConstants.YYYY_MM_DD); // time will become 00:00:00
				parsedDate = sdf2.parse(strDate);
				String strTemp = sdf.format(parsedDate);
				parsedDate = sdf.parse(strTemp);
			}
		} else {
			Date date = new Date();
			String strCurrDate = sdf.format(date);
			parsedDate = sdf.parse(strCurrDate);
		} // end if/else

		cal.setTime(parsedDate);
		cal.add(Calendar.DAY_OF_MONTH, intDays);
		newDate = sdf.format(cal.getTime());

		return newDate;
	} // end addDaysToPassedDateTime
	
	/**
	 * This method accepts a date and a number of days as Strings (days should be a String representation of an integer). 
	 * It calculates the new date by adding the number of days passed to the original date passed and returns this new
	 * date, using the date format passed in the argument.
	 * 
	 * @param strDate The original date
	 * @param strDays The number of hours to add to the original date
	 * @param strDateFormat The desired output date format
	 * @return The new date calculated by adding the days passed to the original date passed
	 * @throws Exception Exception
	 */
	public static String addDaysToPassedDateTime(String strDate, String strDays, String strDateFormat) throws Exception {

		String newDate = null;
		// Input date will be passed to sterling in below format
		DateFormat sdf_tz = new SimpleDateFormat(VSIConstants.DT_STR_TS_FORMAT);
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD_T_HH_MM_SS);
		Calendar cal = Calendar.getInstance();
		int intDays = Integer.parseInt(strDays);
		Date parsedDate = null;

		// If input date is passed use it else use the current time stamp
		if (strDate != null && !"".equals(strDate)) {
			try {
				try {
					parsedDate = sdf_tz.parse(strDate);
				} catch (ParseException pe) {
					parsedDate = sdf.parse(strDate);
				}
			} catch (ParseException pe) {
				DateFormat sdf2 = new SimpleDateFormat(VSIConstants.YYYY_MM_DD); // time will become 00:00:00
				parsedDate = sdf2.parse(strDate);
				String strTemp = sdf.format(parsedDate);
				parsedDate = sdf.parse(strTemp);
			}
		} else {
			Date date = new Date();
			String strCurrDate = sdf.format(date);
			parsedDate = sdf.parse(strCurrDate);
		} // end if/else

		cal.setTime(parsedDate);
		cal.add(Calendar.DAY_OF_MONTH, intDays);

		DateFormat sdf_custom = new SimpleDateFormat(strDateFormat);
		newDate = sdf_custom.format(cal.getTime());

		return newDate;
	} // end addDaysToPassedDateTime
	
	/**
	 * Adds specified interval to input date. Valid values for Interval are
	 * Calendar.YEAR, Calendar.MONTH, Calendar.DATE etc. See Calendar API for
	 * more information
	 * 
	 * @param inputDate
	 *            Input Date
	 * @param interval
	 *            Interval
	 * @param amount
	 *            Amount to add(use negative numbers to subtract
	 * @return Date after addition
	 * @throws IllegalArgumentException
	 *             for Invalid input
	 * @throws Exception
	 *             for all others
	 */
	public static Date addToDate(Date inputDate, int interval, int amount)
			throws IllegalArgumentException, Exception {

		// Validate Input date
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot be"
					+ " null in DateUtils.addToDate method");
		}

		// Get instance of calendar
		Calendar calendar = Calendar.getInstance();

		// Set input date to calendar
		calendar.setTime(inputDate);

		// Add amount to interval
		calendar.add(interval, amount);

		// Return result date;
		return calendar.getTime();
	}

	public static int differenceBetweenDates(String strPD,String strESD) throws ParseException{

		// Input date will be passed to sterling in below format
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
		
		Date dDatePD = sdf.parse(strPD);
		Date dDateESD = sdf.parse(strESD);
		
		int diffInDays = (int)( (dDateESD.getTime() - dDatePD.getTime()) 
                / (1000 * 60 * 60 * 24) );
		
		return Math.abs(diffInDays);
	}
	
	public static int signedDifferenceBetweenDates(String strPD,String strESD) throws ParseException{

		// Input date will be passed to sterling in below format
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD);
		
		Date dDatePD = sdf.parse(strPD);
		Date dDateESD = sdf.parse(strESD);
		
		int diffInDays = (int)( (dDateESD.getTime() - dDatePD.getTime()) 
                / (1000 * 60 * 60 * 24) );
		
		return diffInDays;
	}
	
	
	public static Document getCountryCode(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, YIFClientCreationException, TransformerException {
		
		
		HashMap<String, String> countryMap = new HashMap<String, String>();
		//Element eleCustomer = inXML.getDocumentElement();
		Element eleCustomer = XMLUtil.getElementByXPath(inXML,"//Customer");
		
		Element eleCustomerContactList = SCXmlUtil.getChildElement(eleCustomer, VSIConstants.ELE_CUST_CONTACT_LIST);
		if(!YFCObject.isNull(eleCustomerContactList)){
			Element eleCustomerContact = SCXmlUtil.getChildElement(eleCustomerContactList, VSIConstants.ELE_CUST_CONTACT);
			if(!YFCObject.isNull(eleCustomerContact)){
				Element eleCustomerAdditionalAddressList = SCXmlUtil.getChildElement(eleCustomerContact, VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS_LIST);
				
				if(!YFCObject.isNull(eleCustomerAdditionalAddressList)){
					ArrayList<Element> alAddressList = SCXmlUtil.getChildren(eleCustomerAdditionalAddressList, VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS);
					for(Element eleAdd:alAddressList){
						Element elePersonInfo = SCXmlUtil.getChildElement(eleAdd, VSIConstants.ELE_PERSON_INFO);
						if(!YFCObject.isNull(elePersonInfo)){
							String updatedCountry = null;
							String strCountry = elePersonInfo.getAttribute(VSIConstants.ATTR_COUNTRY);
							if(countryMap.containsKey(strCountry)){
								
								updatedCountry = countryMap.get(strCountry);
								
							}//end of if hasmap
							else{
								updatedCountry = VSIUtils.getCountryCode(env, strCountry);
								countryMap.put(strCountry, updatedCountry);
							}//end of calling commoncodeList
							elePersonInfo.setAttribute(VSIConstants.ATTR_COUNTRY,updatedCountry );
							
						}//end of person info
						
					}//end of for
					
				}//customer additional addresslist
			}//end of customer contact
		}//end of customercontactlist
		
		
		return inXML;
		
	}
	
	public static String getCountryCode(YFSEnvironment env, String strCountry)throws YFSException, RemoteException, YIFClientCreationException, TransformerException{
		
		Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_COUNTRYCODE_MAP");
		if(strCountry.length()==2){
		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION, strCountry);
		}
		if(strCountry.length()==3){
			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION, strCountry);
		}
		
		Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_COMMONCODELIST,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
		Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
		
		if(!YFCObject.isVoid(commonCodeListElement)){
			Element commonCodeElement = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
			if(!YFCObject.isVoid(commonCodeElement)){
				String strCodeShortDescription = SCXmlUtil.getAttribute(commonCodeElement, VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				String strCodeLongDescription = SCXmlUtil.getAttribute(commonCodeElement, VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
				
				if(strCountry.equals(strCodeShortDescription)){
					strCountry = strCodeLongDescription;
				}//end of updating country to long desc
				else if(strCountry.equals(strCodeLongDescription)){
					strCountry = strCodeShortDescription;
				}//end of updating country to long desc
				else{
					strCountry = "FGN";
				}
			}
			else
			{
				strCountry = "FGN";
			}
			
		}
		return strCountry;
	}
	
	/**
	 * Returns current date-time string in desired format
	 *
	 * @param outputFormat Desired output date-time format
	 * @return Current date-time string in desired format
	 * @throws IllegalArgumentException for Invalid input
	 * @throws Exception for all others
	 */
	public static String getCurrentDate(String outputFormat)
			throws IllegalArgumentException, Exception {
		//Create current date object
		Date currentDateTime = new Date();

		//Apply formatting
		return formatDate(currentDateTime, outputFormat);
	}

	/**
	 * Converts date object to date-time string
	 * 
	 * @param inputDate Date object to be converted
	 * @param outputFormat Output format.
	 * Refer to <code>java.text.SimpleDateFormat</code> for date format
	 * codes
	 * @return          Formatted date-time string
	 * @throws IllegalArgumentException for Invalid input
	 * @throws Exception for all others
	 */
	public static String formatDate(java.util.Date inputDate,
			String outputFormat) throws IllegalArgumentException, Exception {
		//Validate input date value
		if (inputDate == null) {
			throw new IllegalArgumentException("Input date cannot "
					+ " be null in DateUtils.formatDate method");
		}

		//Validate output date format
		if (outputFormat == null) {
			throw new IllegalArgumentException("Output format cannot"
					+ " be null in DateUtils.formatDate method");
		}

		//Apply formatting
		SimpleDateFormat formatter = new SimpleDateFormat(outputFormat);
		return formatter.format(inputDate);
	}
	
	/**
	 * Compares the given dates. Returns 0 if the two dates are equal, a value less than 0 if strDate1 is less than strDate2 or
	 * 	a value greater than 0 if strDate1 is greater than strDate2
	 * 
	 * @param strDate1
	 * @param strDate2
	 * @return 
	 * @throws ParseException 
	 */
	public static int compareDates(String strDate1, String strDate2) throws ParseException {
		// Input date will be passed to sterling in below format
		DateFormat sdf_tz = new SimpleDateFormat(VSIConstants.DT_STR_TS_FORMAT);
		DateFormat sdf = new SimpleDateFormat(VSIConstants.YYYY_MM_DD_T_HH_MM_SS);
		Calendar cal = Calendar.getInstance();
		int iOutput = 0;

		// If input date is passed use it else use the current time stamp
		if (!YFCObject.isVoid(strDate1) && !YFCObject.isVoid(strDate2)) {
			try {
				Date parsedDate1 = sdf_tz.parse(strDate1);
				Date parsedDate2 = sdf_tz.parse(strDate2);

				Long lDate1 = parsedDate1.getTime();
				Long lDate2 = parsedDate2.getTime();
				
				iOutput = lDate1.compareTo(lDate2);
			} catch (ParseException pe) {
				Date parsedDate1 = sdf.parse(strDate1);
				Date parsedDate2 = sdf.parse(strDate2);
				
				Long lDate1 = parsedDate1.getTime();
				Long lDate2 = parsedDate2.getTime();
				
				iOutput = lDate1.compareTo(lDate2);
			}
		}

		return iOutput;
	}
	
	/**
	    * This method compares two statuses passed as the arguments as String
	    * It returns 0 if Status1 = Status2
	    * It returns 1 if Status1 < Status2
	    * It returns 2 if Status1 > Status2
	    * It returns -1 in case of Error
    */
	
	public static int compareStatus(String status1, String status2)
			  throws Exception
	{
		boolean bLoop = true;
		boolean bStatus1 = false;
		boolean bStatus2 = false;
		int i=-1;
		int status1temp = 0;
		int status2temp = 0;
		int statuslength = 0;

		try
		{
			if(("".equals(status1))||("".equals(status1)))
				return -1;

			String[] status1break = null;
			status1break = status1.split("\\.");
			int status1breaklength = status1break.length;

			String[] status2break = null;
			status2break = status2.split("\\.");
			int status2breaklength = status2break.length;	

			if(status1breaklength>status2breaklength){
				statuslength = status2breaklength-1;
				bStatus1 = true;
			}
			else if (status1breaklength<status2breaklength){
				statuslength = status1breaklength-1;
				bStatus2 = true;
			}
			else
				statuslength = status1breaklength-1;

			while(bLoop){			
				i++;	

				status1temp=Integer.parseInt(status1break[i]);
				status2temp=Integer.parseInt(status2break[i]);

				if(i<statuslength){						
					if(status1temp<status2temp)return 1;
					if(status1temp>status2temp)return 2;
				}
				if ((i==statuslength)){				
					if(status1temp<status2temp)return 1;
					if(status1temp>status2temp)return 2;
					if((status2temp==status1temp)&& bStatus1) return 2;
					if((status2temp==status1temp)&& bStatus2) return 1;
					if(status2temp==status1temp) return 0;
					bLoop = false;
				}
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return -1;
	}
	
	public static String getUniqueID(){
		
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		return String.valueOf(timestamp.getTime());
	}
}
