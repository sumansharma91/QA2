package com.vsi.oms.api;

import java.net.SocketTimeoutException;
import java.util.Properties;

import org.w3c.dom.Document;

import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetCustomerData implements YIFCustomApi{
	
	public final String crmUrl= YFSSystem.getProperty("CRM_WEBSERVICE_URL");
	public String crmActionUrl= YFSSystem.getProperty("CRM_ACTION_URL");
	public int iretryCount1 = 0;
	public int iretryCount2 = 0;
	public int iretryCount3 = 0;
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIGetCustomerData.class);
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}
	public Document getCustomerDataWithCustomerNo(YFSEnvironment env, Document inXML) throws NumberFormatException {
		
		 Document srchOut = null;
		 crmActionUrl = crmActionUrl + "GetCustomerData";
		 if(log.isDebugEnabled()){
			 log.info(" *** Entering Method : getCustomerDataWithCustomerNo ");
			 log.debug(" *** Entering Method : getCustomerDataWithCustomerNo Input XML is: \n "+XMLUtil.getXMLString(inXML));
		 }
	     try {
	    	 
	    	 if(log.isDebugEnabled()){
	    		 log.debug(" *** Invoking Webservice with Input Params :\n CRM Webservice URL: "+crmUrl+"\n CRM Action URL: "+crmActionUrl);
	    	 }
	    	 srchOut=InvokeWebservice.invokeCustomerSearchWebService(XMLUtil.getXMLString(inXML), crmUrl,crmActionUrl) ;
	    	}
	     catch (Exception e) {
				e.printStackTrace();
				//System.out.print(e);
				 String _xml=null;
				if(e instanceof SocketTimeoutException)
				{
					if(iretryCount1 < 3){
						
						if(log.isDebugEnabled()){
							log.debug("** Printing retry count : "+iretryCount1);
						}
						iretryCount1 ++;
						getCustomerDataWithCustomerNo( env,  inXML);
						
					}
//					_xml="<Customer IsCallSuccesful=\"N\" />";
					_xml="<Customer CRM_FAIL=\"Y\"  />";
				}
				else 
//					_xml="<Customer IsCustomerAvailable=\"N\" />";
					_xml="<Customer/>";
				YFCDocument ifcallFailed=YFCDocument.getDocumentFor(_xml);
				return ifcallFailed.getDocument();  
				
			}
		
		
		return srchOut;
		
	}
	
	public Document getCustomerDataWithoutCustomerNo(YFSEnvironment env, Document inXML) throws NumberFormatException {
		
		 Document srchOut = null;
		 crmActionUrl = crmActionUrl + "FindCustomers";
		 if(log.isDebugEnabled()){
			 log.debug(" *** Entering Method : getCustomerDataWithoutCustomerNo Input XML is: \n "+XMLUtil.getXMLString(inXML));
		 }
	     try {
	    	 
	    	 if(log.isDebugEnabled()){
	    		 log.debug(" *** Invoking Webservice with Input Params :\n CRM Webservice URL: "+crmUrl+"\n CRM Action URL: "+crmActionUrl);
	    	 }
	    	 srchOut=InvokeWebservice.invokeCustomerSearchWebService(XMLUtil.getXMLString(inXML), crmUrl,crmActionUrl) ;
	    	}
	     catch (Exception e) {
				e.printStackTrace();
				//System.out.print(e);
				 String _xml=null;
				if(e instanceof SocketTimeoutException){
					if(iretryCount2 < 3){
						if(log.isDebugEnabled()){
							log.debug("** Printing retry count : "+iretryCount2);
						}
						iretryCount2 ++;
						getCustomerDataWithoutCustomerNo( env,  inXML);
						
					}
//					_xml="<Customer IsCallSuccesful=\"N\" />";
					_xml="<Customer CRM_FAIL=\"Y\"  />";
				}
				else{ 
//					_xml="<Customer IsCustomerAvailable=\"N\" />";
					_xml="<Customer/>";
				}	
				YFCDocument ifcallFailed=YFCDocument.getDocumentFor(_xml);
				return ifcallFailed.getDocument();  
				
			}
		
		
		return srchOut;
		
	}
	
	public Document getCustomerAvlbleRewardPoints(YFSEnvironment env, Document inXML) throws NumberFormatException {
		
		 Document srchOut = null;
		 crmActionUrl = crmActionUrl + "GetCustomerRewardAvailablePoints";
		 if(log.isDebugEnabled()){
			 log.info(" *** Entering Method : getCustomerAvlbleRewardPoints Input XML is: \n "+XMLUtil.getXMLString(inXML));
		 }
	     try {
	    	 
	    	 if(log.isDebugEnabled()){
	    		 log.debug(" *** Invoking Webservice with Input Params :\n CRM Webservice URL: "+crmUrl+"\n CRM Action URL: "+crmActionUrl);
	    	 }
	    	 srchOut=InvokeWebservice.invokeCustomerSearchWebService(XMLUtil.getXMLString(inXML), crmUrl,crmActionUrl) ;
	    	}
	     catch (Exception e) {
				e.printStackTrace();
				//System.out.print(e);
				 String _xml=null;
				if(e instanceof SocketTimeoutException) {
					if(iretryCount3 < 3){
						if(log.isDebugEnabled()){
							log.debug("** Printing retry count : "+iretryCount3);
						}
						iretryCount3 ++;
						getCustomerAvlbleRewardPoints( env,  inXML);
						
					}
//					_xml="<Customer IsCallSuccesful=\"N\" />";
					_xml="<Customer CRM_FAIL=\"Y\"  />";
				}
				else {
//					_xml="<Customer IsCustomerAvailable=\"N\" />";
					_xml="<Customer/>";
				}
				YFCDocument ifcallFailed=YFCDocument.getDocumentFor(_xml);
				return ifcallFailed.getDocument();  
				
			}
		
		
		return srchOut;
		
	}
}
