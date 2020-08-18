package com.vsi.oms.utils;

import java.rmi.RemoteException;

import org.apache.soap.providers.com.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import com.vsi.oms.utils.VSIConstants;

/**
 * @author Sindhura
 *
 */
public class VSIRestrictedItemValidation {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRestrictedItemValidation.class);
	/**
	 * @param itemID
	 * @param Country
	 * @param State
	 * @param ZipCode
	 * @return
	 * @throws Exception 
	 */
	public Document vsiRestrictedItemValidation(YFSEnvironment env, String itemid, String strCountry, String strState, String strZipCode){
		
		if(log.isDebugEnabled()){
			log.info("================Inside vsiRestrictedItemValidation================================");
		}
		
		Document inDocItemList = createInputForGetItemList(itemid);
		Document outDoc = null;
		try {
			outDoc = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ITEM_LIST, VSIConstants.API_GET_ITEM_LIST, inDocItemList);
			 Element eleItemList = outDoc.getDocumentElement();
				
			 Element eleItem1 = SCXmlUtil.getChildElement(eleItemList, "Item");	
			 Element eleExtn = SCXmlUtil.getChildElement(eleItem1, "Extn");			  
			 Element eleVSIShipRestrictedItemList = SCXmlUtil.getChildElement(eleExtn, "VSIShipRestrictedItemList");
				 		 
				 if(!YFCObject.isVoid(eleVSIShipRestrictedItemList)){
					 
					 Element eleVSIShipRestrictedItem  = SCXmlUtil.getChildElement(eleVSIShipRestrictedItemList, "VSIShipRestrictedItem");
					 
					 if(!YFCObject.isVoid(eleVSIShipRestrictedItem)){
						 
							 String sCountry =SCXmlUtil.getAttribute(eleVSIShipRestrictedItem, "Country");
							 String sState =SCXmlUtil.getAttribute(eleVSIShipRestrictedItem, "State");
							 String sZipCode =SCXmlUtil.getAttribute(eleVSIShipRestrictedItem, "ZipCode");
							 
							 if(sZipCode.equalsIgnoreCase(strZipCode) &&(YFCObject.isNull(sState) || (sState.equalsIgnoreCase(strState)))
									 && (YFCObject.isNull(sCountry) ||(sCountry.equalsIgnoreCase(strCountry)))){
								 
								   throw new YFSException("Some of the items on this order cannot be shipped to new shipping address.");
								 
							 }//end of if
							 
							 else if(YFCObject.isNull(sZipCode) && sState.equalsIgnoreCase(strState) &&
									 (YFCObject.isNull(sCountry) || sCountry.equalsIgnoreCase(strCountry))){
								 
								 throw new YFSException("Some of the items on this order cannot be shipped to new shipping address.");
								 
							 }//end of if
							 
							 else if(YFCObject.isNull(sZipCode) && YFCObject.isNull(sState) && sCountry.equalsIgnoreCase(strCountry)){
								 
								 throw new YFSException("Some of the items on this order cannot be shipped to new shipping address.");
								 
							 }//end of if
					}//end of if
						 
				}//end of hasChildNodes 
			 //} // end of checking flag
			 
		} // end of try
		
		catch (YFSException | RemoteException | YIFClientCreationException e) {
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
			"Some of the items on this order cannot be shipped to new shipping address.");
		}
		
		return null;
		
	}
	
	
	/**
	 * @param itemID
	 * @return
	 */
	private Document createInputForGetItemList(String itemID){
		// Create a new document with root element as OrderInvoiceDetail
				Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
				Element eleItem = docInput.getDocumentElement();
				eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID,itemID);
				eleItem.setAttribute("OrganizationCode","VSI-Cat");
				eleItem.setAttribute("UnitOfMeasure","EACH");
				return docInput;
	}


}
