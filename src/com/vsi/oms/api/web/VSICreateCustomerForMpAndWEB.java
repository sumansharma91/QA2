package com.vsi.oms.api.web;


import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICreateCustomerForMpAndWEB {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSICreateCustomerForMpAndWEB.class);
	/**
	 * Creating customer using the agent based process 
	 * @param env
	 * @param inXML
	 * @return
	 * @throws YFSException
	 * @throws RemoteException
	 * @throws YIFClientCreationException
	 * @throws ParserConfigurationException 
	 */
	
	public Document createCustomerForMpAndWEB(YFSEnvironment env, Document inXML) throws YFSException, RemoteException, YIFClientCreationException, ParserConfigurationException{

		
		
		Element rootElement = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside createCustomerForMpAndWEB================================");
		}
		
		String orderType = SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_ORDER_TYPE);
		Element elePersonInfoShipTo = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
		Element elePersonInfoBillTo = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_PERSON_INFO_BILL_TO);
		//Element extnEle = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_EXTN);
		//String sExtnCRMCustomer = null;
		
					if(orderType.equalsIgnoreCase("Marketplace")){
							String sequence = null;
						try {
							sequence = VSIDBUtil.getNextSequence(env, VSIConstants.SEQ_VSI_CUST_ID);
						} catch (Exception e) {
							
							e.printStackTrace();
							
							log.error("DB Error: VSICreateSaveNewCustomerWebservice VSIDBUtil.getNextSequence(env, " +  VSIConstants.SEQ_VSI_CUST_ID );
						}
						//Creating input to manageCustomer API
						Document docManageCustomerInput = SCXmlUtil.createDocument("Customer");
						Element customerEle = docManageCustomerInput.getDocumentElement();
						customerEle.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
						customerEle.setAttribute(VSIConstants.ATTR_CUSTOMER_ID,  sequence);
						customerEle.setAttribute(VSIConstants.ATTR_EXTERNAL_CUSTOMER_ID, VSIConstants.INT_ZER0_NUM);
						customerEle.setAttribute(VSIConstants.ATTR_CUST_TYPE, "02");
						
						//checking for orderType as Marketplace or WEB
						if(orderType.equals("Marketplace")){
							customerEle.setAttribute(VSIConstants.ATTR_EMAIL_OPT_IN, "N");
							customerEle.setAttribute(VSIConstants.ATTR_PHONE_OPT_IN, "N");
							customerEle.setAttribute(VSIConstants.ATTR_POSTAL_OPT_IN, "N");
						}else if(orderType.equals("WEB")){
							customerEle.setAttribute(VSIConstants.ATTR_EMAIL_OPT_IN, "Y");
							customerEle.setAttribute(VSIConstants.ATTR_PHONE_OPT_IN, "Y");
							customerEle.setAttribute(VSIConstants.ATTR_POSTAL_OPT_IN, "Y");
							
						}
						Element customerContactListEle = SCXmlUtil.createChild(customerEle, VSIConstants.ELE_CUST_CONTACT_LIST);
						Element customerContactEle = SCXmlUtil.createChild(customerContactListEle, VSIConstants.ELE_CUST_CONTACT);
						customerContactEle.setAttribute(VSIConstants.ATTR_DAY_FAX_NO, "");
						customerContactEle.setAttribute(VSIConstants.ATTR_DAY_PHONE, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_DAY_PHONE));
						
						customerContactEle.setAttribute(VSIConstants.ATTR_EMAIL_ID,SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_CUSTOMER_EMAIL_ID));
						customerContactEle.setAttribute(VSIConstants.ATTR_EVENING_FAX_NO,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_EVENING_FAX_NO));
						customerContactEle.setAttribute(VSIConstants.ATTR_EVENING_PHONE,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_EVENING_PHONE));
						customerContactEle.setAttribute(VSIConstants.ATTR_FIRST_NAME,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_FIRST_NAME));
						customerContactEle.setAttribute(VSIConstants.ATTR_JOB_TITLE,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_JOB_TITLE));
						customerContactEle.setAttribute(VSIConstants.ATTR_LAST_NAME,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_LAST_NAME));
						customerContactEle.setAttribute(VSIConstants.ATTR_MIDDLE_NAME,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_MIDDLE_NAME));
						customerContactEle.setAttribute(VSIConstants.ATTR_MOBILE_PHONE,SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_MOBILE_PHONE));
						Element customerAdditionalAddressListEle = SCXmlUtil.createChild(customerContactEle, VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS_LIST);
						customerAdditionalAddressListEle.setAttribute(VSIConstants.ATTR_RESET,"Y");
						//bill to
						Element customerAdditionalAddressEle = SCXmlUtil.createChild(customerAdditionalAddressListEle, VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS);
						customerAdditionalAddressEle.setAttribute(VSIConstants.ATTR_IS_BILL_TO, "Y");
						customerAdditionalAddressEle.setAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO, "Y");
						customerAdditionalAddressEle.setAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO, "N");
						customerAdditionalAddressEle.setAttribute(VSIConstants.ATTR_IS_SHIP_TO, "N");
						Element personInfoEle = SCXmlUtil.createChild(customerAdditionalAddressEle, VSIConstants.ELE_PERSON_INFO);
						personInfoEle.setAttribute(VSIConstants.ATTR_ADDR_ID, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_ADDR_ID));
						personInfoEle.setAttribute(VSIConstants.ATTR_ADDR_LINE_1, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_ADDR_LINE_1));
						personInfoEle.setAttribute(VSIConstants.ATTR_ADDR_LINE_2, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_ADDR_LINE_2));
						personInfoEle.setAttribute(VSIConstants.ATTR_CITY, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_CITY));
						personInfoEle.setAttribute(VSIConstants.ATTR_COUNTRY, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_COUNTRY));
						personInfoEle.setAttribute(VSIConstants.ATTR_IS_COMMERCIAL_ADDRESS, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_IS_COMMERCIAL_ADDRESS));
						personInfoEle.setAttribute(VSIConstants.ATTR_STATE, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_STATE));
						personInfoEle.setAttribute(VSIConstants.ATTR_ZIPCODE, SCXmlUtil.getAttribute(elePersonInfoBillTo, VSIConstants.ATTR_ZIPCODE));
						//shipto
						Element customerAdditionalAddressEle1 = SCXmlUtil.createChild(customerAdditionalAddressListEle, VSIConstants.ELE_CUST_ADDITIONAL_ADDRESS);
						customerAdditionalAddressEle1.setAttribute(VSIConstants.ATTR_IS_BILL_TO, "N");
						customerAdditionalAddressEle1.setAttribute(VSIConstants.ATTR_IS_DEFAULT_BILL_TO, "N");
						customerAdditionalAddressEle1.setAttribute(VSIConstants.ATTR_IS_DEFAULT_SHIP_TO, "Y");
						customerAdditionalAddressEle1.setAttribute(VSIConstants.ATTR_IS_SHIP_TO, "Y");
						Element personInfoEle1 = SCXmlUtil.createChild(customerAdditionalAddressEle1, VSIConstants.ELE_PERSON_INFO);
						personInfoEle1.setAttribute(VSIConstants.ATTR_ADDR_ID, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_ID));
						personInfoEle1.setAttribute(VSIConstants.ATTR_ADDR_LINE_1, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_1));
						personInfoEle1.setAttribute(VSIConstants.ATTR_ADDR_LINE_2, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_2));
						personInfoEle1.setAttribute(VSIConstants.ATTR_CITY, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_CITY));
						personInfoEle1.setAttribute(VSIConstants.ATTR_COUNTRY, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY));
						personInfoEle1.setAttribute(VSIConstants.ATTR_IS_COMMERCIAL_ADDRESS, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_IS_COMMERCIAL_ADDRESS));
						personInfoEle1.setAttribute(VSIConstants.ATTR_STATE, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_STATE));
						personInfoEle1.setAttribute(VSIConstants.ATTR_ZIPCODE, SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE));
						
						Document docManageCustomerOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_MANAGE_CUSTOMER_VSIManageCustomerUE, VSIConstants.API_MANAGE_CUST, docManageCustomerInput);
						if(log.isDebugEnabled()){
							log.debug("Output of ManageCustomer" +	SCXmlUtil.getString(docManageCustomerOutput));
						}
	
						Element eleCustomer = docManageCustomerOutput.getDocumentElement();
						String sCustomerID = SCXmlUtil.getAttribute(eleCustomer,VSIConstants.ATTR_CUSTOMER_ID);
						env.setTxnObject("MKPCustomerID", sCustomerID);
						
						Document outDoc = null;
						Document docChangeOrder = XMLUtil.createDocument("Order");
		    			Element orderElement1 = docChangeOrder.getDocumentElement();
		    			orderElement1.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, rootElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
		    			orderElement1.setAttribute(VSIConstants.ATTR_ORDER_NO, rootElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
		    			orderElement1.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, rootElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
		    			orderElement1.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
		    			orderElement1.setAttribute(VSIConstants.ATTR_BILL_TO_ID,sCustomerID);
		    			VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
		    			
					}//end of if	
		
		return inXML;
		
	}//end of method

}//end of class
