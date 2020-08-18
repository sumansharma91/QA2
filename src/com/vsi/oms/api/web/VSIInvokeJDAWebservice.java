package com.vsi.oms.api.web;
import com.vsi.oms.allocation.api.VSIProcessRelease;
import com.vsi.oms.utils.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIInvokeJDAWebservice implements VSIConstants {
	/**
	 * Logger instance
	 */
	private YFCLogCategory log = YFCLogCategory.instance(VSIInvokeJDAWebservice.class);

/**
 * This service is responsible for invoking Force Allocation Web service using return document
 * from invoking VSIInvokeJDAXSL service in Sterling
 * 
 * @param env
 *				 Sterling Commerce Environment Context.
 * @param docReleasexml
 * 				Incoming Release xml
 * @return
 * @throws IOException
 * @throws ParserConfigurationException
 * @throws SAXException
 * @throws SocketTimeoutException
 */
	public  Document invokeJDAWebservice(YFSEnvironment env,Document docReleasexml ) throws IOException, ParserConfigurationException, SAXException, SocketTimeoutException   {
	
			String transferNo=null;
			Document xslJDAWebServiceRequest = null;
		//Invoking VSIInvokeJDAXSL service to translate release xml for webservice response
			try {
				xslJDAWebServiceRequest = VSIUtils.invokeService(env,VSIConstants.SERVICE_JDA_XSL_REQUEST,docReleasexml);
				String orderNo=xslJDAWebServiceRequest.getElementsByTagName(ATTR_ORDER_NO).item(0).getTextContent().toString();
				NodeList itemList = xslJDAWebServiceRequest.getElementsByTagName(ELE_ITEM);
				String itemId="";
				for (int i = 0; i < itemList.getLength(); i++) 
				{
						Element itemEle = (Element) xslJDAWebServiceRequest.getElementsByTagName(ELE_ITEM).item(i);
						if(itemList.getLength() == 1)
						{
							itemId=itemEle.getElementsByTagName(ELE_JDA_SKU).item(0).getTextContent().toString();
							break;
						}
						else
							itemId=itemEle.getElementsByTagName(ELE_JDA_SKU).item(0).getTextContent().toString()+" "+itemId;
				}
				log.info("Entire itemId =>  "+ itemId);
				if(itemId.length() >= 31)
					itemId = itemId.substring(0,31).concat("...");
				log.info("Truncated/Entire itemId =>  "+ itemId);
				boolean sendReleaseRevAllocation = false, sendReleaseAllocation = false, sfsAllocation = false;
				if(docReleasexml.getDocumentElement().getAttribute(ATTR_SEND_RELEASE).equalsIgnoreCase("Y"))
					sendReleaseRevAllocation = true;
				log.info("sendReleaseRevAllocation :  "+ sendReleaseRevAllocation);				
				if(docReleasexml.getDocumentElement().getAttribute(ATTR_REVERSE_ALLOCATION).equalsIgnoreCase("Y"))
					sendReleaseAllocation = true;
				log.info("sendReleaseAllocation :  "+ sendReleaseAllocation);
				if(docReleasexml.getDocumentElement().getAttribute(ATTR_SFS_ALLOCATION).equalsIgnoreCase("Y"))
					sfsAllocation = true;
				log.info("sfsAllocation :  "+ sfsAllocation);
			String inDocString=XMLUtil.getXMLString(xslJDAWebServiceRequest);
			if(log.isDebugEnabled()){
				log.debug("VSIInvokeJDAXSL output "+ inDocString);
			}
			//webservice url
			final String ForcedAlloHRurl= YFSSystem.getProperty(PROP_FORCED_ALLOCATION_URL);
			if(log.isDebugEnabled()){
				log.debug("FORCED_ALLOCATIOIN_URL "+ ForcedAlloHRurl);
			}
			//invoke web service method
			// OMS-881 : Start
			transferNo=invokeURLPOST(env,ForcedAlloHRurl,inDocString,orderNo,itemId,sendReleaseRevAllocation,sendReleaseAllocation,sfsAllocation);
			
			log.info("Final transferNo :  "+ transferNo);			
			if(log.isDebugEnabled()){
				log.debug("transferNo :  "+ transferNo);
			}
			//OMS-881:End
			
			if(transferNo == null || transferNo.equalsIgnoreCase(" ") || transferNo.equalsIgnoreCase(""))
			{
				String queueId = null, exceptionType = null, exceptionDescription = null;
				if(sendReleaseRevAllocation || sendReleaseAllocation)
				{
					exceptionType = ALERT_JDA_REVERSE_EXCEPTION_TYPE;
					exceptionDescription = ALERT_JDA_REVERSE_DETAIL_DESCRIPTION;
					queueId = ALERT_JDA_REVERSE_QUEUE;
				}
				else if((!sendReleaseRevAllocation && !sendReleaseAllocation) && sfsAllocation)
				{
					 exceptionType = ALERT_SEND_RELEASE_JDA_EXCEPTION_TYPE;
					 exceptionDescription = ALERT_SEND_RELEASE_JDA_DETAIL_DESCRIPTION;
					 queueId = ALERT_SEND_RELEASE_JDA_QUEUE;
				}
				log.info("exceptionType =>  "+ exceptionType + "exceptionDescription => "+exceptionDescription+"queueId => "+queueId);
				alertForJDASendReleaseFailure(env, itemId, orderNo, "", exceptionType, exceptionDescription, queueId);
				VSIProcessRelease processReleaseObj = new VSIProcessRelease();
				processReleaseObj.invokeChangeRelease(env, docReleasexml, "");
				log.info("Before throwing exception");
				throw new YFSException(JDA_ALLOCATION_EXCEPTION_ERROR_CODE,JDA_ALLOCATION_EXCEPTION_ERROR_CODE,JDA_ALLOCATION_EXCEPTION_ERROR_MSG);
			}
			else
			{
			Element eleOrderRelease = docReleasexml.getDocumentElement();
			Element eleExtn = XMLUtil.getElementByXPath(docReleasexml, XPATH_ORDER_RELEASE_EXTN);
			if(YFCCommon.isVoid(eleExtn)){
				eleExtn = SCXmlUtil.createChild(eleOrderRelease,ELE_EXTN); 
			}	
			eleExtn.setAttribute(VSIConstants.ATTR_EXTN_TRANSFER_NO,transferNo);
			}
			} catch (Exception e) {
				throw new YFSException(JDA_ALLOCATION_EXCEPTION_ERROR_CODE,JDA_ALLOCATION_EXCEPTION_ERROR_CODE,JDA_ALLOCATION_EXCEPTION_ERROR_MSG);
			}			
			if(log.isDebugEnabled()){
				log.info("ExtnTransferNo Updated Release Form"+SCXmlUtil.getString(docReleasexml));
			}
		return docReleasexml;
	}
	
	public void alertForJDASendReleaseFailure(YFSEnvironment env, String itemId, String orderNo, String orderHeaderKey, String exceptionType, String exceptionDescription, String queueId)
	{
		try
		{
		//Creation of Alert
		Document createExceptionDoc = SCXmlUtil.createDocument(ELE_INBOX);
		Element eleInbox = createExceptionDoc.getDocumentElement();
		eleInbox.setAttribute(ATTR_ORDER_HEADER_KEY, orderHeaderKey);
		eleInbox.setAttribute(ATTR_ITEMID, itemId);
		eleInbox.setAttribute(ATTR_ORDER_NO, orderNo);
		eleInbox.setAttribute(ATTR_EXCEPTION_TYPE, exceptionType);
		eleInbox.setAttribute(ATTR_DETAIL_DESCRIPTION, exceptionDescription);
		eleInbox.setAttribute(ATTR_QUEUE_ID, queueId);
		eleInbox.setAttribute(ATTR_CONSOLIDATE,FLAG_Y);
		eleInbox.setAttribute(ATTR_CONS_WINDOW,VAL_FOREVER);
		Element eleConsolidationTemplate = createExceptionDoc.createElement(ELE_CONSOLIDATE_TEMPLATE);
		eleInbox.appendChild(eleConsolidationTemplate);
		Element eleInboxCpy = (Element) eleInbox.cloneNode(true);
		eleConsolidationTemplate.appendChild(eleInboxCpy);
		VSIUtils.invokeAPI(env, API_CREATE_EXCEPTION,createExceptionDoc);
		}
		catch(Exception e)
		{
			if(log.isDebugEnabled())
			{
				log.info("alertForJDASendReleaseFailure - in catch block => ");
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 * @param instream
	 * 			JDA webservice response
	 * @return
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	 		private  Document parseDoc(InputStream instream) throws ParserConfigurationException, SAXException, IOException{
		 	DocumentBuilderFactory factory =DocumentBuilderFactory.newInstance();
			factory.isIgnoringElementContentWhitespace();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc=null;
		 	if(instream.markSupported()){
		 		instream.mark(Integer.MAX_VALUE);
		 		BufferedReader buff_read = new BufferedReader(new InputStreamReader(instream,"UTF-8"));
			    String  inputLine = null;

			 while((inputLine = buff_read.readLine())!= null){
				 if(log.isDebugEnabled()){
			    		log.debug(inputLine);
				 }
			    }  
			    instream.reset();
		  }
		 	doc=builder.parse(instream);
		 	if(log.isDebugEnabled()){
		 		log.info("JDA WebService Response being Parsed"+instream);
		 	}
		 	return doc;
		  }	
	 
	 // OMS-881 : Start
	 /**
	  * 
	  * @param endPointURL
	  * 				JDA webserivce URL
	  * @param xml
	  *            JDA webservice Request
	  * @return
	  * @throws Exception 
	  */

	 public  String invokeURLPOST(YFSEnvironment env,String endPointURLInital, String xml, String orderNo, String itemId, boolean sendReleaseRevAllocation, boolean sendReleaseAllocation, boolean sfsAllocation) throws Exception {

		 DataOutputStream wr=null;
		 String transferNo=null;

		 try {
			 
			 String strSplit[]=endPointURLInital.split("\\?");
			 String endPointURL=strSplit[0].concat("?");
			 String strArg1=strSplit[1];
			 log.info("endPointURL is  :"+endPointURL+ "   strArg1 :"+strArg1);
			 if(log.isDebugEnabled()){
				 log.debug("endPointURL is  :"+endPointURL+ "   strArg1 :"+strArg1);
			 }
			 String xmlencoded = URLEncoder.encode (xml,UTF8_ENCODING);
			 String strContent=strArg1+"&request="+xmlencoded;
			 log.info("xmlencoded => "+xmlencoded+"strContent => "+strContent);
			 URL url = new URL (endPointURL);	
			 //open connection using url
			 HttpURLConnection connection =  (HttpURLConnection) url.openConnection();
				 //get web service response
				 connection.setRequestMethod(JDA_POST_METHOD);
				 byte[] bContent = strContent.getBytes();	
				 connection.setRequestProperty( JDA_REQUEST_PROP, String.valueOf(bContent.length));
				 connection.setDefaultUseCaches(false);
				 connection.setDoOutput(true);

				 Document responseDoc=null;
				 log.info("Invoking the JDA web service");
				 if(log.isDebugEnabled()){
					 log.debug("Invoking the JDA web service ");
				 }
				 wr = new DataOutputStream (connection.getOutputStream());
				 wr.write(bContent);
				 log.info("Invoked the JDA service");				 
				 if(connection.getResponseCode() == -1 || connection.getResponseMessage() == null)
					 alertForJDASendReleaseFailure(env, itemId, orderNo, "", ALERT_JDA_SERVICE_CALL_FAILURE_EXCEPTION_TYPE, ALERT_JDA_SERVICE_CALL_FAILURE_DETAIL_DESCRIPTION, ALERT_JDA_SERVICE_CALL_FAILURE_QUEUE);
				 if(connection.getResponseCode() != 200)
				 {
					 log.info(connection.getResponseCode()+" - "+connection.getResponseMessage());
					 alertForJDASendReleaseFailure(env, itemId, orderNo, "", ALERT_JDA_SERVICE_CALL_FAILURE_EXCEPTION_TYPE, connection.getResponseCode()+" - "+connection.getResponseMessage(), ALERT_JDA_SERVICE_CALL_FAILURE_QUEUE);
				 }				 
				 if(connection.getResponseCode() == 200)
				 {
					 //retrieve transferNo from webservice repsone
					 responseDoc=parseDoc(connection.getInputStream());
					 log.info("Response from JDA WebService:"+XMLUtil.getXMLString(responseDoc));
					 if(log.isDebugEnabled()){
						 log.debug("Output from JDA is: "+XMLUtil.getXMLString(responseDoc));
						 log.info("Response from JDA WebService:"+XMLUtil.getXMLString(responseDoc));
					 }
					 if(responseDoc.getElementsByTagName(ELE_ERROR_RESPONSE).item(0) != null)
					 {
						 Element eleErrorResp = (Element) responseDoc.getElementsByTagName(ELE_ERROR_RESPONSE).item(0);
						 Element eleOrderNo = SCXmlUtil.createChild(eleErrorResp,ATTR_ORDER_NO);
						 eleOrderNo.setTextContent(orderNo);
						 Element eleAllocationType = SCXmlUtil.createChild(eleErrorResp,JDA_ALLOCATION_TYPE);
						 if(sendReleaseRevAllocation)
							 eleAllocationType.setTextContent(JDA_REVERSE_ALLOCATION_TYPE);
						 else if(!sendReleaseRevAllocation && !sendReleaseAllocation)
							 eleAllocationType.setTextContent(JDA_FORCE_ALLOCATION_TYPE);
						 else if(sfsAllocation)
							 eleAllocationType.setTextContent(JDA_SFS_FORCE_ALLOCATION_TYPE);
						 VSIUtils.invokeService(env, SERVICE_JDA_ALLOCATION_RESPONSE, responseDoc);
					 }
					 else if(responseDoc.getElementsByTagName(ELE_ERROR_RESPONSE).item(0) == null)
					 {
						 VSIUtils.invokeService(env, SERVICE_JDA_ALLOCATION_RESPONSE, responseDoc);
						 transferNo=responseDoc.getElementsByTagName(ATTR_TRANSFER_NO).item(0).getTextContent().toString();
						 log.info("transferNo in Success Response => " +transferNo+"sfsAllocation in success response => "+sfsAllocation);
						 if(sfsAllocation && (transferNo.equalsIgnoreCase("")))
							 transferNo="Nil";
						 log.info("transferNo in SFS Success Response => " +transferNo);
					}
					else
					{
						 log.debug("Final Else Response code is " +connection.getResponseCode());
						 VSIUtils.invokeService(env, SERVICE_JDA_ALLOCATION_RESPONSE, responseDoc);
						 log.debug("Response code is " +connection.getResponseCode());
						 if(log.isDebugEnabled()){
							 log.debug("Response code is " +connection.getResponseCode());
						 }
					 transferNo=" ";
					 }
				 }

		 } catch (Exception e) 
		 {
			 log.info("Exception thrown within VSIInvokeJDAWebservice.invokeURLPOST() as below =>");
			 e.printStackTrace();
		 }finally {
			 if(wr!=null){
				 wr.close();
			 }
		 }
		 return transferNo;		
	 }
	 // OMS-881 : End	 
}