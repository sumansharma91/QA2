package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIMarketplaceOrderOnCreate {
	
	 private YFCLogCategory log = YFCLogCategory.instance(VSIMarketplaceOrderOnCreate.class);

/**invoking the service VSIMarketplaceOrderCreateOnSuccess 
 * 	 
 * @param env
 * @param inXML
 * @throws Exception 
 */
	 public Document validateMarketplaceOrder(YFSEnvironment env, Document inXML)throws Exception {
		 
		 if(log.isDebugEnabled()){
			 log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			 log.info("================Inside validateMarketplaceOrder================================");
		 }
		 
		    Document outdoc = null;
		    Document outdoc1 = null;
		    Document docgetCommonCodeOutput = null;
			Element orderElement = inXML.getDocumentElement();
			
			Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			Element elePersonInfoShipTo= SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			
			//Getting the state and country from the /Order/OrderLines/OrderLine/PersonInfoShipTo
			String strState = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_STATE);
			String strCountry = elePersonInfoShipTo.getAttribute(VSIConstants.ATTR_COUNTRY);
			
			// START ARS-35
			/**String sAddressLine1 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_1);
 			String sAddressLine2 =  SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_2);
 			if((sAddressLine1.length() > 29) || (sAddressLine2.length() > 29) || (strCountry.length() > 2)){
	 			
 				//creating input doc to createOrder Exception
 				Document doccreateExceptionInput = SCXmlUtil.createDocument("Inbox"); 
    			Element eleInbox = doccreateExceptionInput.getDocumentElement();
    			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(orderElement, VSIConstants.ATTR_ORDER_NO));
    			eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_ORDER_HOLD");
    			eleInbox.setAttribute( VSIConstants.ATTR_EXCEPTION_TYPE, "MARKETPLACE_ORDER_HOLD");
    			eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Please Verify the ship to address of OrderNo." + SCXmlUtil.getAttribute(orderElement, VSIConstants.ATTR_ORDER_NO));
    			
    			Element inboxOrderEle = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
    			inboxOrderEle.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(orderElement, VSIConstants.ATTR_ENTERPRISE_CODE));
    			inboxOrderEle.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(orderElement, VSIConstants.ATTR_ORDER_NO));
    			inboxOrderEle.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, SCXmlUtil.getAttribute(orderElement, VSIConstants.ATTR_DOCUMENT_TYPE));
    			
    			VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, doccreateExceptionInput);
 			}**/
 			// END ARS-35
			
			//creating input for getCommonCodeList API
			Document docgetCommonCodeInput = XMLUtil.createDocument("CommonCode");
    		Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_ORG_CODE, "DEFAULT");
    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_RESTRICT_COUNTRY");
    		eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, strCountry);
    		
    		docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
    		Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
    		
    		//If the Country is restricted cancel the entire order
    		if(commonCodeListElement.hasChildNodes()){
    			Document docChangeOrder = XMLUtil.createDocument("Order");
    			Element orderElement1 = docChangeOrder.getDocumentElement();
    			orderElement1.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
    			orderElement1.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "CouldNotShip");
    			orderElement1.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    			orderElement1.setAttribute(VSIConstants.ATTR_ORDER_NO, orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
    			orderElement1.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, orderElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
    			orderElement1.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
    			
    			outdoc1 = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
    			
    			return inXML;
    			
    		}
			//NodeList orderLines = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			Element eleOrderLines1 = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines1, VSIConstants.ELE_ORDER_LINE);
			
			 //creating document for changeOrder for could not ship
			Document docChangeOrder = XMLUtil.createDocument("Order");
    		Element eleRootElement = docChangeOrder.getDocumentElement();
    		eleRootElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    		eleRootElement.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
    		eleRootElement.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, orderElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
    		eleRootElement.setAttribute(VSIConstants.ATTR_ORDER_NO, orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
			Element eleOrderLines2 = SCXmlUtil.createChild(eleRootElement, VSIConstants.ELE_ORDER_LINES);
			
			 //creating document for changeOrder for invalid sku
			Document docChangeOrder1 = XMLUtil.createDocument("Order");
    		Element eleRootElement1 = docChangeOrder1.getDocumentElement();
    		eleRootElement1.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    		eleRootElement1.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE));
    		eleRootElement1.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, orderElement.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE));
    		eleRootElement1.setAttribute(VSIConstants.ATTR_ORDER_NO, orderElement.getAttribute(VSIConstants.ATTR_ORDER_NO));
			Element eleOrderLines3 = SCXmlUtil.createChild(eleRootElement1, VSIConstants.ELE_ORDER_LINES);
			
			
			 for(Element eleOrderLine1:alOrderLines){
				 try{
					Element eleItem=SCXmlUtil.getChildElement(eleOrderLine1, VSIConstants.ELE_ITEM, true);
					 
					String strItemId = eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID); 
 
					Document docgetItemListInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
					SCXmlUtil.setAttribute(docgetItemListInput.getDocumentElement(), VSIConstants.ATTR_ITEM_ID, strItemId);
					Document docgetItemListOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_ITEM_LIST,VSIConstants.API_GET_ITEM_LIST, docgetItemListInput);
						if (!docgetItemListOutput.getDocumentElement().hasChildNodes())
						{
							//System.out.println("Hellooooo");
							    Element eleOrderLine2 = SCXmlUtil.createChild(eleOrderLines3, "OrderLine");
							    eleOrderLine2.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
							    eleOrderLine2.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
							  // SCXmlUtil.importElement(eleOrderLines3, eleOrderLine1);
							   
						}
						else
						{
							 //creating document for VSIShipRestrictedItem
							 Document docRestrictedItem = XMLUtil.createDocument("VSIShipRestrictedItem");
							 Element eleRestricted=docRestrictedItem.getDocumentElement();
							 eleRestricted.setAttribute(VSIConstants.ATTR_STATE, strState);
							 eleRestricted.setAttribute(VSIConstants.ATTR_COUNTRY, strCountry);
							 eleRestricted.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
							 
						     outdoc= VSIUtils.invokeService(env,"VSIGetShipRestrictedItem", docRestrictedItem);
						     
						     if(log.isDebugEnabled()){
						    	 log.debug("Restricted Item = " +SCXmlUtil.getString(outdoc));
						     }
						     
						     //if the item is Restricted cancel the line
						     if(!YFCObject.isVoid(outdoc)){
						    	 Element eleOrderLine2 = SCXmlUtil.createChild(eleOrderLines2, "OrderLine");
							     eleOrderLine2.setAttribute(VSIConstants.ATTR_ACTION, "CANCEL");
							     eleOrderLine2.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine1.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
							   // SCXmlUtil.importElement(eleOrderLines2, eleOrderLine1);
  
						      }//end if if
							
						}
						
				
			         } //end of try
				 catch(Exception e){
					 e.printStackTrace();
					 throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIMarketplaceOrderOnCreate.validateMarketplaceOrder()");
					 
				 }//end of catch
				 
				}//end of for
			 
		     if(eleOrderLines2.hasChildNodes()){
		    	 eleRootElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, VSIConstants.COULD_NOT_SHIP);
			     VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder);	
		      }//end of if
		     
		     if(eleOrderLines3.hasChildNodes()){
		    	 eleRootElement1.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "InvalidSku");
			     VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder1);	
		      }//end of if

			 
			return inXML;
			
	}//end of method validateMarketplaceOrder
	 
}//end of class VSIMarketplaceOrderOnCreate
