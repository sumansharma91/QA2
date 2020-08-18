package com.vsi.oms.api.order;

import java.rmi.RemoteException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIMarketplaceBackorder {
	
 private YFCLogCategory log = YFCLogCategory.instance(VSIMarketplaceBackorder.class);
 
 /**invoking the service VSIMarketplaceBackorder
  * 
  * @param env
  * @param inXML
  * @throws YFSException
  * @throws RemoteException
  * @throws ParserConfigurationException
  * @throws YIFClientCreationException
  */
	 
	 public Document validateMarketplaceBackOrder(YFSEnvironment env, Document inXML)throws YFSException, RemoteException, ParserConfigurationException,
				YIFClientCreationException {
		 
		 if(log.isDebugEnabled()){
			 log.verbose("Printing Input XML :" + SCXmlUtil.getString(inXML));
			 log.info("================Inside validateMarketplaceBackOrder================================");
		 }
		 	
		    Document outdoc=null;
		    Document outdoc1=null;
		    Element orderElement = inXML.getDocumentElement();
			String enterpriseCode = orderElement.getAttribute("EnterpriseCode");
			
			Element eleOrderLines = SCXmlUtil.getChildElement(orderElement, VSIConstants.ELE_ORDER_LINES);
			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			
			Document docChangeOrder = XMLUtil.createDocument("Order");
    		Element eleRootElement = docChangeOrder.getDocumentElement();
    		eleRootElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderElement.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
    		eleRootElement.setAttribute("EnterpriseCode", orderElement.getAttribute("EnterpriseCode"));
    		eleRootElement.setAttribute("DocumentType", orderElement.getAttribute("DocumentType"));
    		eleRootElement.setAttribute("OrderNo", orderElement.getAttribute("OrderNo"));
			
			Element eleOrderLines2 = SCXmlUtil.createChild(eleRootElement, "OrderLines");
			
			//looping through back order lines
			try{
			  for(Element eleOrderLine:alOrderLines){
				  					
					SCXmlUtil.setAttribute(eleOrderLine, VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);
					if(enterpriseCode.equals(VSIConstants.ENT_MCL)){
						SCXmlUtil.importElement(eleOrderLines2, eleOrderLine);
					}
					/*OMS-1316 Start
					else{
					// String strItemId=eleOrderLine.getAttribute(VSIConstants.ATTR_ITEM_ID);
					 Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, "Item");
					 String itemid =	eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
					 Document getItemListInput = createInputForGetItemList(itemid);
					 outdoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ITEM_LIST, VSIConstants.API_GET_ITEM_LIST, getItemListInput);
					 Element eleItemList = outdoc.getDocumentElement();
						
					 Element eleItem1 = SCXmlUtil.getChildElement(eleItemList, "Item");
					 Element eleExtn = SCXmlUtil.getChildElement(eleItem1, "Extn");
					 
					 if(eleExtn.hasAttributes()){
						 
						 log.debug("Item extn attributes = " +SCXmlUtil.getString(eleExtn));	
						 
							//checking for ItemList/Item/Extn/@ExtnIsDiscontinuedItem="Y" 
						 if(eleExtn.getAttribute(VSIConstants.ATTR_EXTN_IS_DISCONTINUED_ITEM).equals(VSIConstants.FLAG_Y)){
								SCXmlUtil.importElement(eleOrderLines2, eleOrderLine);
								
							}//end of if	
						}//end of if extn has attributes.
					}
					OMS-1316 end */
			       }//end of for
			  
			  //Adding the cancelled lines and quantities to the input XML and call changeOrder.
			          if(eleOrderLines2.hasChildNodes()){
			        	  eleRootElement.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, VSIConstants.NO_INVENTORY);
						  eleRootElement.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
				            VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder);	
			            }//end of if
                }//end of try
			catch(Exception e){
				e.printStackTrace();
				 throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIMarketplaceBackorder.validateMarketplaceBackOrder()");
				 
			}
			return inXML;
    }//end of method validateMarketplaceBackOrder
	 
	 private Document createInputForGetItemList(String itemID){
			// Create a new document with root element as OrderInvoiceDetail
					Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
					Element eleItem = docInput.getDocumentElement();
					eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID,itemID);
					eleItem.setAttribute("OrganizationCode","VSI-Cat");
					eleItem.setAttribute("UnitOfMeasure","EACH");
					eleItem.setAttribute("MaximumRecords", "1");
					return docInput;
		}

}//end of class VSIMarketplaceBackorder
