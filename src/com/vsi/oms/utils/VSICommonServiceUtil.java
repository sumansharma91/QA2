/**
 * 
 */
package com.vsi.oms.utils;

import static com.vsi.oms.utils.VSIConstants.API_CHANGE_ORDER;
import static com.vsi.oms.utils.VSIConstants.ATTR_ACTION;
import static com.vsi.oms.utils.VSIConstants.ATTR_DELIVERY_METHOD;
import static com.vsi.oms.utils.VSIConstants.ATTR_DRAFT_ORDER_FLAG;
import static com.vsi.oms.utils.VSIConstants.ATTR_EXTN_IS_GWP;
import static com.vsi.oms.utils.VSIConstants.ATTR_ITEM_ID;
import static com.vsi.oms.utils.VSIConstants.ATTR_LINE_TYPE;
import static com.vsi.oms.utils.VSIConstants.ATTR_ORDER_HEADER_KEY;
import static com.vsi.oms.utils.VSIConstants.ATTR_ORDER_LINE_KEY;
import static com.vsi.oms.utils.VSIConstants.ATTR_ORD_QTY;
import static com.vsi.oms.utils.VSIConstants.ATTR_OVERRIDE;
import static com.vsi.oms.utils.VSIConstants.ATTR_PAYMENT_TYPE;
import static com.vsi.oms.utils.VSIConstants.ATTR_PLANNED_REFUND_AMOUNT;
import static com.vsi.oms.utils.VSIConstants.ATTR_PRODUCT_CLASS;
import static com.vsi.oms.utils.VSIConstants.ATTR_SCAC;
import static com.vsi.oms.utils.VSIConstants.ATTR_SUSPEND_ANYMORE_CHARGES;
import static com.vsi.oms.utils.VSIConstants.ATTR_UOM;
import static com.vsi.oms.utils.VSIConstants.ELE_EXTN;
import static com.vsi.oms.utils.VSIConstants.ELE_ITEM;
import static com.vsi.oms.utils.VSIConstants.ELE_ORDER;
import static com.vsi.oms.utils.VSIConstants.ELE_ORDER_LINE;
import static com.vsi.oms.utils.VSIConstants.ELE_ORDER_LINES;
import static com.vsi.oms.utils.VSIConstants.ELE_PAYMENT_METHOD;
import static com.vsi.oms.utils.VSIConstants.ELE_PAYMENT_METHODS;
import static com.vsi.oms.utils.VSIConstants.FLAG_N;
import static com.vsi.oms.utils.VSIConstants.FLAG_Y;
import static com.vsi.oms.utils.VSIConstants.GOOD;
import static com.vsi.oms.utils.VSIConstants.PAYMENT_MODE_CC;
import static com.vsi.oms.utils.VSIConstants.PAYMENT_MODE_OGC;
import static com.vsi.oms.utils.VSIConstants.PAYMENT_MODE_PAYPAL;
import static com.vsi.oms.utils.VSIConstants.UOM_EACH;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.userexit.VSIOrderRepricingUEImpl;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * This class will be invoked by VSIGetItemDetailForItemSyncService, which is used to set the Vitamine Shoppe Website Url.
 * 
 * @author IBM
 */
public class VSICommonServiceUtil implements YIFCustomApi {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIOrderRepricingUEImpl.class);
	
	/**
	 * getUpdatedItemDetailsForItem() method is used to set the ItemWebSiteUrl getting up from the property file
	 * with adding ExtnActSkuID at the end. Also, We are disabling the fulfillment method shipping and delivery here.
	 * 
	 * @param env
	 * @param inXML
	 * @return
	 */
	public Document getUpdatedItemDetailsForItem(YFSEnvironment env, Document inXML){
		Properties properties = YFSSystem.getProperties();
		Element eleItemList = inXML.getDocumentElement();
		ArrayList<Element> arrListItem = SCXmlUtil.getChildren(eleItemList, ELE_ITEM);
		for(Element eleItem : arrListItem){
			// Updating the primaryInformation for Item.
			Element elePrimaryInformation = SCXmlUtil.getChildElement(eleItem, "PrimaryInformation");
			Element eleItemExtn = SCXmlUtil.getChildElement(eleItem, ELE_EXTN);
			elePrimaryInformation.setAttribute("ItemWebSiteUrl", properties.getProperty("ImageLink")+SCXmlUtil.getAttribute(eleItemExtn, "ExtnActSkuID"));
			
			elePrimaryInformation.setAttribute("IsShippingAllowed", "N");
			elePrimaryInformation.setAttribute("IsDeliveryAllowed", "N");
			
			/*Element eleAssetList = SCXmlUtil.getChildElement(eleItem, "AssetList", true);
			Element eleAsset = SCXmlUtil.getChildElement(eleAssetList, "Asset", true);
			eleAsset.setAttribute("ContentID", eleItem.getAttribute(ATTR_ITEM_ID)+"_"+properties.getProperty("ImageSuffix"));
			eleAsset.setAttribute("ContentLocation", properties.getProperty("ImageLocation"));
			eleAsset.setAttribute("Type", "ITEM_IMAGE_1");	*/		
		}
		return inXML;
	}
	
	@Override
	public void setProperties(Properties arg0) throws Exception {
		
	}
	
	/**
	 * ARS-297 : Checks for any free lines in the ATG pricing service output and adds or updates the quantity on the
	 * free lines on the order
	 * 
	 * @param yfsEnv 
	 * @param mapFreeItems 
	 * @param strOrderHeaderKey 
	 * @param strDeliveryMethod 
	 * @param strLineType 
	 * @param eleHoldOLK 
	 * @param webServiceOutput
	 * @throws YIFClientCreationException 
	 * @throws RemoteException 
	 * @throws YFSException 
	 */
	public void processATGPricingOutputForFreeItems(YFSEnvironment yfsEnv,Document docInXML) {
		if(!YFCCommon.isVoid(yfsEnv.getTxnObject("REPRICING_OUTPUT"))){
		
			Document docATGOutXML = (Document)yfsEnv.getTxnObject("REPRICING_OUTPUT");
			//form change order input
			Document docChangeOrder = SCXmlUtil.createDocument(ELE_ORDER);
			Element eleChangeOrder = docChangeOrder.getDocumentElement();
			eleChangeOrder.setAttribute(ATTR_OVERRIDE, FLAG_Y);
			//eleChangeOrder.setAttribute("BypassPricing", "Y");
			Element eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, ELE_ORDER_LINES);
			Element eleChangeOrder1= SCXmlUtil.getChildElement(eleChangeOrderLines, ELE_ORDER_LINE);
			
			eleChangeOrder1.getAttribute(ATTR_SUSPEND_ANYMORE_CHARGES);
			
			//Loop through input xml to find out any differences in quantity.
			Element eleInXML = docInXML.getDocumentElement();
			String strDraftOrder = eleInXML.getAttribute(ATTR_DRAFT_ORDER_FLAG);
			Element eleInXMLOrderLines = SCXmlUtil.getChildElement(eleInXML, ELE_ORDER_LINES);
			ArrayList<Element> arrInXMLOrderLines = SCXmlUtil.getChildren(eleInXMLOrderLines, ELE_ORDER_LINE);
			String strOrderHeaderKey = eleInXML.getAttribute(ATTR_ORDER_HEADER_KEY);
			eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			
			
			Element eleATGOutXml=docATGOutXML.getDocumentElement();
			String strSCAC = "";
			
			for(Element eleInXMLOrderLine:arrInXMLOrderLines){
				Element eleInOrderLineExtn = SCXmlUtil.getChildElement(eleInXMLOrderLine, ELE_EXTN);
				String strInXMLExtnIsGWP = eleInOrderLineExtn.getAttribute(ATTR_EXTN_IS_GWP);
				strSCAC = eleInXMLOrderLine.getAttribute(ATTR_SCAC);
				
				if(strInXMLExtnIsGWP.equalsIgnoreCase(FLAG_Y)){
					Element eleItem = SCXmlUtil.getChildElement(eleInXMLOrderLine, ELE_ITEM);
					String strItemID= eleItem.getAttribute(ATTR_ITEM_ID);
					String strOrderLineXPath = "//Order/OrderLines/OrderLine[@ItemID='"+strItemID+"']";
					//Check if this item exists in the input XML.
					Element eleATGOrderLine = SCXmlUtil.getXpathElement(eleATGOutXml, strOrderLineXPath);
					
					if(!YFCCommon.isVoid(eleATGOrderLine)){
						String strATGQuantity = eleATGOrderLine.getAttribute(ATTR_ORD_QTY);
						Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, ELE_ORDER_LINE);
						eleChangeOrderLine.setAttribute(ATTR_ORD_QTY, strATGQuantity);
						eleChangeOrderLine.setAttribute(ATTR_ACTION, "MODIFY");
						eleChangeOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, eleInXMLOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
					}else{
						Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, ELE_ORDER_LINE);
						eleChangeOrderLine.setAttribute(ATTR_ORDER_LINE_KEY, eleInXMLOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
						if(strDraftOrder.equalsIgnoreCase(FLAG_Y)){	
							eleChangeOrderLine.setAttribute(ATTR_ACTION, "REMOVE");
						}else{
							eleChangeOrderLine.setAttribute(ATTR_ACTION, "CANCEL");
						}
						
					}
				}
			}
			
			//Loop through ATG xml output to find any new line being added.
			
			Element eleOrderLines = SCXmlUtil.getChildElement(eleATGOutXml, ELE_ORDER_LINES);
			ArrayList<Element> arrATGOrderLines = SCXmlUtil.getChildren(eleOrderLines, ELE_ORDER_LINE);
			
			for(Element eleOrderLine:arrATGOrderLines){
				Element eleOrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);
				String strExtnIsGWP = eleOrderLineExtn.getAttribute(ATTR_EXTN_IS_GWP);
				if(strExtnIsGWP.equalsIgnoreCase(FLAG_Y)){
					String strItemID= eleOrderLine.getAttribute(ATTR_ITEM_ID);
					String strOrderLineXPath = "//Order/OrderLines/OrderLine[./Item/@ItemID='"+strItemID+"']";
					//Check if this item exists in the input XML.
					Element eleInXMLOrderLine = SCXmlUtil.getXpathElement(eleInXML, strOrderLineXPath);
							
					
					if(YFCCommon.isVoid(eleInXMLOrderLine)){
						
						Element eleFreeLine = SCXmlUtil.importElement(eleChangeOrderLines, eleOrderLine);
						eleFreeLine.setAttribute(ATTR_ACTION, "CREATE");
						eleFreeLine.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
						eleFreeLine.setAttribute(ATTR_LINE_TYPE, "SHIP_TO_HOME");
						eleFreeLine.setAttribute(ATTR_DELIVERY_METHOD, "SHP");
						eleFreeLine.setAttribute(ATTR_SCAC, strSCAC);
						eleFreeLine.setAttribute("BypassPricing", FLAG_Y);
						Element eleItem = SCXmlUtil.createChild(eleFreeLine, ELE_ITEM);
						eleItem.setAttribute(ATTR_ITEM_ID, eleOrderLine.getAttribute(ATTR_ITEM_ID));
						eleItem.setAttribute(ATTR_UOM, UOM_EACH);
						eleItem.setAttribute(ATTR_PRODUCT_CLASS, GOOD);
					}
				}
			}
			if(eleChangeOrderLines.hasChildNodes()){
				// Call changeOrder
				try {
					yfsEnv.setTxnObject("BYPASS_PRICING", FLAG_Y);
					yfsEnv.setTxnObject("REPRICING_OUTPUT", null);
					Document docChangeOrderOut= VSIUtils.invokeAPI(yfsEnv,API_CHANGE_ORDER, docChangeOrder);
					if(log.isDebugEnabled()){
			    		log.debug(SCXmlUtil.getString(docChangeOrderOut));
					}
				} catch (YFSException e) {
					log.error("YFSException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(docChangeOrder), e);
					//throw VSIUtils.getYFSException(e);
				} catch (RemoteException e) {
					log.error("RemoteException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(docChangeOrder), e);
					//throw VSIUtils.getYFSException(e);
				} catch (YIFClientCreationException e) {
					log.error("YIFClientCreationException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(docChangeOrder), e);
					//throw VSIUtils.getYFSException(e);
				} catch (Exception e) {
					log.error("Exception in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(docChangeOrder), e);
					//throw VSIUtils.getYFSException(e);
				}
	
			}
		}
	}
	
	public  Document massageProcessReturnOrder(YFSEnvironment env,Document docInput) {
		Element outEl = docInput.getDocumentElement();
		if (!(YFCCommon.isVoid(outEl))) {
			Element eOrderLines = SCXmlUtil.getChildElement(outEl, "OrderLines");

			ArrayList<Element> eOrderLineList = SCXmlUtil.getChildren(eOrderLines, "OrderLine");
			if(log.isDebugEnabled()){
	    		log.debug("Input is");
			}

			String sIsReturnedByGiftRecipient = "N";
			for (Element eOrderLine : eOrderLineList) {
				String sGiftFlag = eOrderLine.getAttribute("GiftFlag");
				if ((!(YFCCommon.isVoid(sGiftFlag))) && (YFCCommon.equals(sGiftFlag, "Y"))) {
					sIsReturnedByGiftRecipient = "Y";
					break;
				}
			}

			String strIsDraftOrder = outEl.getAttribute(ATTR_DRAFT_ORDER_FLAG);
			
			Element overallTotals = SCXmlUtil.getChildElement(outEl, "OverallTotals");
			String hdrCharges = "";
			Double dblHdrCharges=0.00;
			String hdrTax = "";
			Double dblHdrTax = 0.00;
			if(!YFCCommon.isVoid(overallTotals)){
				hdrCharges = overallTotals.getAttribute("HdrCharges");
				dblHdrCharges = Double.parseDouble(hdrCharges);
				hdrTax = overallTotals.getAttribute("HdrTax");
				dblHdrTax = Double.parseDouble(hdrTax);
			}
			//dblHdrCharges = 2.5;
			boolean hdrTaxIncluded = false;
			if (strIsDraftOrder.equalsIgnoreCase(FLAG_Y)) {
				Document docChangeOrder = SCXmlUtil.createDocument(ELE_ORDER);
				Element eleChangeOrder = docChangeOrder.getDocumentElement();
				eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY, outEl.getAttribute(ATTR_ORDER_HEADER_KEY));
				SCXmlUtil.setAttribute(outEl, "IsReturnedByGiftRecipient", sIsReturnedByGiftRecipient);
				Element elePaymentMethods = SCXmlUtil.getChildElement(outEl, ELE_PAYMENT_METHODS);
				ArrayList<Element> elePaymentMethodList = SCXmlUtil.getChildren(elePaymentMethods, ELE_PAYMENT_METHOD);
				boolean bOGCRequired = false;
				double dRequiredPlannedRefund = 0.00;
				boolean bInvokeChangeOrder = false;
				boolean otherPaymentExists = false;
				
				for (Element elePaymentMethod : elePaymentMethodList) {
					
					String strPaymentType = elePaymentMethod.getAttribute(ATTR_PAYMENT_TYPE);
					String strPlannedRefundAmount = null;
					strPlannedRefundAmount = elePaymentMethod.getAttribute(ATTR_PLANNED_REFUND_AMOUNT);
					if (YFCCommon.isVoid(strPlannedRefundAmount)) {
						strPlannedRefundAmount = "0.00";
					}
					//double dPlannedRefundAmount = Double.parseDouble(strPlannedRefundAmount);
					
					double dPlannedRefundAmount = Double.parseDouble(strPlannedRefundAmount);

					if (!YFCCommon.isVoid(strPaymentType) && !(strPaymentType.equalsIgnoreCase(PAYMENT_MODE_CC)
							|| strPaymentType.equalsIgnoreCase(PAYMENT_MODE_PAYPAL))) {
						bOGCRequired = true;
						elePaymentMethod.setAttribute(ATTR_ACTION, "REMOVE");
						if(!hdrTaxIncluded && dblHdrCharges > 0 && dblHdrTax > 0 && dPlannedRefundAmount > 0)
						{
							dPlannedRefundAmount = dPlannedRefundAmount+dblHdrTax;
							elePaymentMethod.setAttribute(ATTR_PLANNED_REFUND_AMOUNT,String.valueOf(dPlannedRefundAmount));
							hdrTaxIncluded = true;
						}
						bInvokeChangeOrder = true;
						dRequiredPlannedRefund = dRequiredPlannedRefund + dPlannedRefundAmount;
					}
					else
					{
						otherPaymentExists = true;
					}					
				}
				if (bOGCRequired && dRequiredPlannedRefund > 0) {
					Element elePaymentMethod = SCXmlUtil.createChild(elePaymentMethods, ELE_PAYMENT_METHOD);
					elePaymentMethod.setAttribute(ATTR_PAYMENT_TYPE, PAYMENT_MODE_OGC);
					elePaymentMethod.setAttribute(ATTR_SUSPEND_ANYMORE_CHARGES, FLAG_N);
					elePaymentMethod.setAttribute(ATTR_PLANNED_REFUND_AMOUNT, Double.toString(dRequiredPlannedRefund));
				}
				
				if(!hdrTaxIncluded && otherPaymentExists && dblHdrCharges > 0 && dblHdrTax > 0 )
				{
					for (Element elePaymentMethod : elePaymentMethodList) 
					{	
						String strPaymentType = elePaymentMethod.getAttribute(ATTR_PAYMENT_TYPE);
						String strPlannedRefundAmount = null;
						strPlannedRefundAmount = elePaymentMethod.getAttribute(ATTR_PLANNED_REFUND_AMOUNT);
						if (YFCCommon.isVoid(strPlannedRefundAmount)) {
							strPlannedRefundAmount = "0.00";
						}
						//double dPlannedRefundAmount = Double.parseDouble(strPlannedRefundAmount);
						
						double dPlannedRefundAmount = Double.parseDouble(strPlannedRefundAmount);

						if (!YFCCommon.isVoid(strPaymentType) && (strPaymentType.equalsIgnoreCase(PAYMENT_MODE_CC)
								|| strPaymentType.equalsIgnoreCase(PAYMENT_MODE_PAYPAL))) {
							
							if(dPlannedRefundAmount > 0)
							{
								dPlannedRefundAmount = dPlannedRefundAmount+dblHdrTax;
								elePaymentMethod.setAttribute(ATTR_PLANNED_REFUND_AMOUNT,String.valueOf(dPlannedRefundAmount));
								hdrTaxIncluded = true;
								bInvokeChangeOrder = true;
								break;
							}
						}	
					}
				}
				
				SCXmlUtil.importElement(eleChangeOrder, elePaymentMethods);
				if(log.isDebugEnabled()){
		    		log.debug("Input to ChangeReturn is: " + SCXmlUtil.getString(docChangeOrder));
				}
				if (bInvokeChangeOrder) {
						try {
							VSIUtils.invokeAPI(env, API_CHANGE_ORDER, docChangeOrder);
						} catch (YFSException | RemoteException | YIFClientCreationException e) {
							e.printStackTrace();
						}
					}
				}
				

			}
		return docInput;
	}

}
