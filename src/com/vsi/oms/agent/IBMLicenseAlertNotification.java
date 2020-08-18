package com.vsi.oms.agent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.util.YCPBaseAgent;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
/**
 * This class get the count of total order lines created in OMS based on common code configuration value(documentType and EnterpriseCode).
 * Class checks the percentage of total order line count based on percentage value mentioned in agent criteria parameter
 * @author Balawant.Singh
 *
 */
public class IBMLicenseAlertNotification extends YCPBaseAgent{
	
	private static YFCLogCategory log = YFCLogCategory
			.instance(IBMLicenseAlertNotification.class.getName());
/*
 * This method get the required value from input document and call getTransactionSummary list API 
 * to get total no of order lines count created in OMS. Then return the document in a list format.
 */
public final List<Document> getJobs(final YFSEnvironment env, final Document inDoc, final Document lastMsg)
	            throws Exception {
	if(log.isDebugEnabled()){
		log.debug("getJobs method :- Begins");
	}
		 Element eleRootInDoc = inDoc.getDocumentElement();
		 List<Document> discList = new ArrayList<Document>();
		 if(null==lastMsg){
		 String strLicenseTotalNoOfLine = eleRootInDoc.getAttribute("LicenseTotalNoOfLines");
		 String strAlertNotificationLimit = eleRootInDoc.getAttribute("AlertTriggerLimitInPercentage");
		 String strIncludeShipmentLines= eleRootInDoc.getAttribute("IncludeShipmentLines");
		Calendar now = Calendar.getInstance();
		 String year = String.valueOf(now.get(Calendar.YEAR));
		//Prepare the input XML for  getTransactionSummary API.
		 Document docGetTransSummaryInput = XMLUtil.createDocument("getTransactionSummary");
		 Element eleGetTransSummaryInput = docGetTransSummaryInput.getDocumentElement();
		 eleGetTransSummaryInput.setAttribute("Year", year);
		 // Call getTransactionSummary API.
	    YIFApi	 api = YIFClientFactory.getInstance().getApi();
		 Document docgetTransSummaryOutput =  api.invoke(env, "getTransactionSummary",docGetTransSummaryInput);
		//Document  docgetTransSummaryOutput = XMLUtil.getXmlFromFile("C:/VSI/LicenseCode/getTransactionSummary.xml");
	     Element elegetTransSummaryOutput = docgetTransSummaryOutput.getDocumentElement();
	     elegetTransSummaryOutput.setAttribute("LicenseTotalNoOfLines", strLicenseTotalNoOfLine);
	     elegetTransSummaryOutput.setAttribute("AlertTriggerLimitInPercentage", strAlertNotificationLimit);
	     elegetTransSummaryOutput.setAttribute("IncludeShipmentLines", strIncludeShipmentLines);
	     discList.add(docgetTransSummaryOutput);
	     }
		
		 if(log.isDebugEnabled()){
			 log.debug("getJobs method :- Ends");
		 }
	     return discList;
	}           
	@Override
	public void executeJob(YFSEnvironment env, Document docInput) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("executeJob method :- Begin");
		}
	    Element eleInput = docInput.getDocumentElement();
		NodeList nlTransSumList  = eleInput.getElementsByTagName("TransactionSummary");
		int lenTransSumList = nlTransSumList.getLength();
		String strTotalNoOfLine = eleInput.getAttribute("LicenseTotalNoOfLines");
		String strAlertNotificationLimit = eleInput.getAttribute("AlertTriggerLimitInPercentage");
		String strIncludeShipmentLines= eleInput.getAttribute("IncludeShipmentLines");
		//Creating common code list input to get enterprise code and document type for which mentioned in License agreement 
		 Document docCommonCodeListInput= XMLUtil.createDocument("CommonCode");
		 Element eleCommonCodeInput= docCommonCodeListInput.getDocumentElement();
		 eleCommonCodeInput.setAttribute("CodeType", "LICENSE_ORG_CODE");
		YIFApi api = YIFClientFactory.getInstance().getApi();
		 Document docGetCommonCodeOP =  api.invoke(env, "getCommonCodeList",docCommonCodeListInput);
	//	 Document docGetCommonCodeOP = XMLUtil.getXmlFromFile("C:/VSI/LicenseCode/getCommonCodeList.xml");
		 Element eleCommonCodeList = docGetCommonCodeOP.getDocumentElement();
		 NodeList ndCommonCode = eleCommonCodeList.getElementsByTagName("CommonCode");
		 int commonCodeListLen = ndCommonCode.getLength();
		 HashMap<String, String> hmDocumentType = new HashMap<String, String>();
		 HashMap<String, String> hmEnterpriseCode = new HashMap<String, String>();
		 //Iterate common code list and get enterprise code and document type for which order line count has to be 
		 //added and compare with license limit and then put into a map .
		 for(int commonCode=0;commonCode<commonCodeListLen;commonCode++){
			 Element eleCommonCode = (Element) ndCommonCode.item(commonCode);
			 String strShortDesc = eleCommonCode.getAttribute("CodeShortDescription");
			 String strLongDesc = eleCommonCode.getAttribute("CodeLongDescription");
			 String strArrayEnterpriseCode [] = strLongDesc.split(",");
			 //This loop get the enterprise code from sterling array and add them into a map.
			 for (String enterpriseCode : strArrayEnterpriseCode) {
				 if(!hmEnterpriseCode.containsKey(enterpriseCode)){
				 hmEnterpriseCode.put(enterpriseCode, null);
				 }
				}
		hmDocumentType.put(strShortDesc, null);
		 }
		int  alertTrigger = (Integer.parseInt(strTotalNoOfLine) * Integer.parseInt(strAlertNotificationLimit) )/100;
		int totalNoOfLines = 0;
		for(int transSummary=0;transSummary<lenTransSumList; transSummary++){
			Element eleTransSummary = (Element) nlTransSumList.item(transSummary);
			String strEnterpriseCode = eleTransSummary.getAttribute("EnterpriseCode");
			if(hmEnterpriseCode.containsKey(strEnterpriseCode)){
				String strDocumentType = eleTransSummary.getAttribute("DocumentType");
			if(hmDocumentType.containsKey(strDocumentType)){
					Element eleYTDSummary = (Element) eleTransSummary.getElementsByTagName("YTDSummary").item(0);
					String   strYTDNoOfOrdLineCreated= eleYTDSummary.getAttribute("YTDNoOfOrderLinesCreated");
					totalNoOfLines = totalNoOfLines+ Integer.parseInt(strYTDNoOfOrdLineCreated);
					//If criteria parameter allows to add ShipmentLines also then this If condition executes.
					if("YES".equalsIgnoreCase(strIncludeShipmentLines)){
						String   strNoOfShipmentsCreated= eleYTDSummary.getAttribute("YTDNoOfShipmentsCreated");
						totalNoOfLines = totalNoOfLines+ Integer.parseInt(strNoOfShipmentsCreated);
					}
				}
		}
		}
		if(log.isDebugEnabled()){
			log.debug("OMS totalNoOfLines" +totalNoOfLines);
		}
		if(alertTrigger<=totalNoOfLines){
			 Document createExInput = XMLUtil.createDocument("Inbox");
		        Element InboxElement = createExInput.getDocumentElement();
						  
	        InboxElement.setAttribute(VSIConstants.ATTR_DESCRIPTION, "No of OrderLines reached to 75 percentage of total licensed lines");
	        InboxElement.setAttribute(VSIConstants.ATTR_ERROR_REASON, "IBM License");
	        InboxElement.setAttribute(VSIConstants.ATTR_ERROR_TYPE, "IBM License");
	        InboxElement.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE, "IBM License Alert");

	        InboxElement.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_IBM_LICENSE_ALERT");
	                
	        Element InboxReferencesListElement = createExInput.createElement("InboxReferencesList");
	        
	        InboxElement.appendChild(InboxReferencesListElement);
	        Element InboxReferencesElement = createExInput.createElement("InboxReferences");
	        InboxReferencesElement.setAttribute(VSIConstants.ATTR_NAME, "NoOfOrderLine");
	        InboxReferencesElement.setAttribute(VSIConstants.ATTR_REFERENCE_TYPE, "TEXT");
	        InboxReferencesElement.setAttribute(VSIConstants.ATTR_VALUE, Integer.toString(totalNoOfLines));
	        
	        InboxReferencesListElement.appendChild(InboxReferencesElement);
	        
	        api = YIFClientFactory.getInstance().getApi();
	        api.invoke(env, VSIConstants.API_CREATE_EXCEPTION, createExInput);
			
				}
		if(log.isDebugEnabled()){
			log.debug("executeJob method :- Ends");
		}
	} 
}
