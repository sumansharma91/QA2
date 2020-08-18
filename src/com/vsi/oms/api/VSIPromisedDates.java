package com.vsi.oms.api;
/**********************************************************************************
 * File Name        : PromisedDates.java
 *
 * Description      : This code will show the promised date to the customer for Ship to Store lines.
 * 					  Calculation
 * 					  PromiseDate = sysdate + Lead Date + Buffer (from common code)
 * 
 * Modification Log :
 * -----------------------------------------------------------------------
 * Ver #    Date          Author                   Modification
 * -----------------------------------------------------------------------
 * 0.00a    18/08/2014                             This code will show the promised date to the customer for Ship to Store lines. 
 **********************************************************************************/


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


public class VSIPromisedDates {
 
	private YFCLogCategory log = YFCLogCategory.instance(VSIPromisedDates.class);
		
 public Document promiseDate(YFSEnvironment env, Document docInXML) throws Exception
 
 {
	 	int iLeadDate=0;
	 	Document getOrganizationHierarchyOutput = null;
			
	 	YIFApi api = (YIFApi)YIFClientFactory.getInstance().getApi();
	 	
		Document templateOrganizatioHierarchy = templateOrganizationHierarchy();
		 	
		/*Setting the template for the API*/
		env.setApiTemplate("getOrganizationHierarchy", templateOrganizatioHierarchy);
		getOrganizationHierarchyOutput = api.invoke(env, "getOrganizationHierarchy", docInXML);
		env.clearApiTemplate("getOrganizationHierarchy");
		 	
		/* ExtnLeadTime attribute from the output of getOrganizationHierarchy API */
		NodeList nlExtnList = (NodeList) getOrganizationHierarchyOutput.getElementsByTagName("Extn");
			
		if(null!=nlExtnList && nlExtnList.getLength()>0)
		{
			Element eleExtn = (Element) nlExtnList.item(0);
			String sLeadTime = eleExtn.getAttribute("ExtnLeadTime");
			
			/* Checking the value of ExtnLeadTime. If it is not null/blank then parse it to iLeadDate.
			 *  if it is null or blank then iLeadDate will be 0 */
			if(sLeadTime!=null && !(sLeadTime.trim().equals("")))
			{
				iLeadDate = Integer.parseInt(sLeadTime);
			}
			if(log.isDebugEnabled()){
				log.info("=====>Printing Lead Date from getOrganizationHierarchy API=======" + iLeadDate);
			}
			
			/* Taking the Input Document and template document for getCommonCodeList API */
			Document templateCommonCode = templateCommonCode();
			Document inputDoc = inputCommonCode(docInXML);
			
			/* Calling getCommonCodeList API */
			env.setApiTemplate("getCommonCodeList", templateCommonCode);
		 	Document getCommonCodeOutput = api.invoke(env, "getCommonCodeList", inputDoc);
		 	env.clearApiTemplate("getCommonCodeList");
		 	
		 	/* CodeValue attribute from the output of getCommonCodeList API */
			
			NodeList nlCommonCode = (NodeList) getCommonCodeOutput.getElementsByTagName("CommonCode");
			
			if(null!=nlCommonCode && nlCommonCode.getLength() > 0)
			{
				Element eleCommonCode = (Element) nlCommonCode.item(0);
				String sCodeValue = eleCommonCode.getAttribute("CodeValue");
				if(log.isDebugEnabled()){
					log.info("=====>Printing CodeValue from getCommonCodeList API=======" + sCodeValue);
				}
				int iCodeValue = Integer.parseInt(sCodeValue);
				
			
				/* Total days to be added to sysdate(Current Date) */
				int iDays = iLeadDate + iCodeValue;
			
				/* getting the sysdate and adding number of days "iDays" to it*/
				Date currentDate = new Date();
				if(log.isDebugEnabled()){
					log.info("=====>Printing Current Date=======" + currentDate);
				}
				
				SimpleDateFormat modifiedDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
				Calendar c = Calendar.getInstance();
				c.setTime(new Date());
				c.add(Calendar.DATE, iDays);
				String sPromisedDate = modifiedDate.format(c.getTime());
				StringBuffer out = new StringBuffer(sPromisedDate);
				sPromisedDate = out.insert(sPromisedDate.length()-2, ':').toString();
			
				/* Setting the attribute in the Input Document */
				Element eleRoot = docInXML.getDocumentElement();
				eleRoot.setAttribute("PromisedDate", sPromisedDate);
				
							
			}
				
		}
	return docInXML;
			
  
}
 /**********************************************************************************
  * Method Name        : templateCommonCode
  *
  * Description      : This method will prepare the template document for getCommonCodeList API
  * 					  
  * 
  * Modification Log :
  * -----------------------------------------------------------------------
  * Ver #    Date          Author                   Modification
  * -----------------------------------------------------------------------
  * 0.00a    18/08/2014                           This method will prepare the template document for getCommonCodeList API
  **********************************************************************************/
 public Document templateCommonCode() throws ParserConfigurationException
 {
	 /* Creating Input document for getCommonCodeList API */
	 	
	 YFCDocument templateDoc = YFCDocument.createDocument("CommonCodeList");
	 YFCElement eleRootElement = templateDoc.getDocumentElement();
	 YFCElement eleCommoncode = eleRootElement.createChild("CommonCode");
	 eleCommoncode.setAttribute("CodeType", "");
	 eleCommoncode.setAttribute("OrganizationCode", "");
	 eleCommoncode.setAttribute("CodeValue", "");
	 
	 return templateDoc.getDocument();
		
 }
 
 /**********************************************************************************
  * Method Name        : InputCommonCode
  *
  * Description      : This method will prepare the input document for getCommonCodeList API
  * 					  
  * 
  * Modification Log :
  * -----------------------------------------------------------------------
  * Ver #    Date          Author                   Modification
  * -----------------------------------------------------------------------
  * 0.00a    18/08/2014                             This method will prepare the input document for getCommonCodeList API
  **********************************************************************************/
 public Document inputCommonCode(Document docInXML) throws ParserConfigurationException
 {
	 /* Taking the value of EnterpriseCode attribute from the input xml*/
	 Element eleEnterpriseCode = docInXML.getDocumentElement();
	 String sOrganizationCode = eleEnterpriseCode.getAttribute("EnterpriseCode");
	 
	 /* Creating Input document for getCommonCodeList API */
	 			 
	 YFCDocument inputDoc = YFCDocument.createDocument("CommonCode");
	 YFCElement eleRootElement = inputDoc.getDocumentElement();
	 eleRootElement.setAttribute("CodeType", "Buffer");
	 eleRootElement.setAttribute("OrganizationCode", sOrganizationCode);
	 
	 return inputDoc.getDocument();
		
 }
 
 /**********************************************************************************
  * Method Name        : InputCommonCode
  *
  * Description      : This method will prepare the template document for getOrganizationHierarchy API
  * 					  
  * 
  * Modification Log :
  * -----------------------------------------------------------------------
  * Ver #    Date          Author                   Modification
  * -----------------------------------------------------------------------
  * 0.00a    18/08/2014                             This method will prepare the template document for getOrganizationHierarchy API
  **********************************************************************************/
 
 public Document templateOrganizationHierarchy() throws ParserConfigurationException
 {	 
	 /* Creating template for getOrganizationHierarchy API*/
	 YFCDocument templateOrganization = YFCDocument.createDocument("Organization");
	 YFCElement eleRootElement = templateOrganization.getDocumentElement();
	 YFCElement eleExtn = eleRootElement.createChild("Extn");
	 eleExtn.setAttribute("ExtnLeadTime", "");
	 
	 return templateOrganization.getDocument();	 		
 } 
}