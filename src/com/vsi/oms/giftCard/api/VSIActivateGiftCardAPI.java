package com.vsi.oms.giftCard.api;

import java.rmi.RemoteException;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.api.order.VSIIssueEGiftCardForAppeasements;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIActivateGiftCardAPI {

	private YFCLogCategory log = YFCLogCategory.instance(VSIActivateGiftCardAPI.class);
	
	public Document activateGiftCard(YFSEnvironment env, Document docInput) throws YFSException {
		log.beginTimer("VSIActivateGiftCardAPI.activateGiftCard : START");
		
		Document docShipOutput = null;
		Document docShipmentInput = SCXmlUtil.createDocument(VSIConstants.ELE_SHIPMENT);
		Element eleShipOutput = null;
		
		try {
			Element eleOrderLine = docInput.getDocumentElement();
			// Check whether an open shipment exists
			Element eleShipmentInput = docShipmentInput.getDocumentElement();
			// There should only be one gift card shipment in Created (1100) status for this order
			eleShipmentInput.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			eleShipmentInput.setAttribute(VSIConstants.ATTR_SHIPMENT_TYPE, VSIConstants.PHY_GC);
			eleShipmentInput.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_CREATE);
			eleShipmentInput.setAttribute(VSIConstants.ATTR_CARRIER_SERVICE_CODE, VSIConstants.STANDARD);
			eleShipmentInput.setAttribute(VSIConstants.ATTR_SCAC, VSIConstants.SCAC_UPSN);
			// Create GC Activation input
			Document docActivateGC = SCXmlUtil.createDocument(VSIConstants.ELE_VSI_ONLINE_GC);
			Element eleActivateGC = docActivateGC.getDocumentElement();
			//String giftCardArray[] = eleOrderLine.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO).split(":");
			
			String giftCardNo = eleOrderLine.getAttribute(VSIConstants.ATTR_GIFT_CARD_NO);
			String trackData1 = eleOrderLine.getAttribute("TrackData1");
			if(!YFCCommon.isVoid(trackData1))
			{
				eleActivateGC.setAttribute("TrackData1", trackData1);
			}
			String trackData2 = eleOrderLine.getAttribute("TrackData2");
			if(!YFCCommon.isVoid(trackData2))
			{
				eleActivateGC.setAttribute("TrackData2", trackData2);
			}
			eleActivateGC.setAttribute(VSIConstants.ATTR_GIFT_CARD_NO, giftCardNo);
			
			//eleActivateGC.setAttribute(VSIConstants.ATTR_GIFT_CARD_NO, giftCardArray[0]);
			//eleActivateGC.setAttribute(VSIConstants.ATTR_PIN_NO, giftCardArray[1]);
			eleActivateGC.setAttribute(VSIConstants.ATTR_STORE_NO, eleOrderLine.getAttribute(VSIConstants.ATTR_SHIP_NODE));
			eleActivateGC.setAttribute(VSIConstants.ATTR_CALLING_FLOW, VSIConstants.FLOW_GIFT_CARD);
			
			//eleActivateGC.setAttribute(VSIConstants.ATTR_PIN_NO, VSIConstants.STATUS_CREATE);
			Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_LINE_PRICE_INFO);
			// Invoke SVS web service to activate the gift card
			if(log.isDebugEnabled()){
				log.debug("docActivateGC sent to VSIIssueCustomerAppeasement.invokeSVSForOGCActivation(): " + SCXmlUtil.getString(docActivateGC));
			}
			VSIIssueEGiftCardForAppeasements objICA = new VSIIssueEGiftCardForAppeasements();
			objICA.invokeSVSForOGCActivation(env, docActivateGC, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_NO),
					eleLinePriceInfo.getAttribute(VSIConstants.ATTR_UNIT_PRICE),
					eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
			
			if (!YFCObject.isVoid(eleOrderLine.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY))) {
				// If an open shipment exists, change Shipment to add shipment line
				eleShipmentInput.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY));
				Element eleShipLines = SCXmlUtil.createChild(eleShipmentInput, VSIConstants.ELE_SHIPMENT_LINES);
				Element eleShipLine = SCXmlUtil.createChild(eleShipLines, VSIConstants.ELE_SHIPMENT_LINE);
				Element eleExtn = SCXmlUtil.createChild(eleShipLine, VSIConstants.ELE_EXTN);
				eleShipLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CREATE);
				eleShipLine.setAttribute(VSIConstants.ATTR_QUANTITY, VSIConstants.ONE);
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
				eleShipLine.setAttribute(VSIConstants.ATTR_ITEM_ID, eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID));
				eleShipLine.setAttribute(VSIConstants.ATTR_ITEM_DESC, eleItem.getAttribute(VSIConstants.ATTR_ITEM_DESC));
				eleShipLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
				eleShipLine.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY));
				eleShipLine.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				//eleExtn.setAttribute(VSIConstants.ATTR_EXTN_GIFT_CARD_NO,giftCardArray[0]);
				eleExtn.setAttribute(VSIConstants.ATTR_EXTN_GIFT_CARD_NO,giftCardNo);
				docShipOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_SHIP_ACTIVATE_GIFT_CARD,
						VSIConstants.API_CHANGE_SHIPMENT, docShipmentInput);
				eleShipOutput = docShipOutput.getDocumentElement();
				Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipOutput, VSIConstants.ELE_SHIPMENT_LINES);
				ArrayList<Element> alShipmentList = SCXmlUtil.getChildren(eleShipmentLines,
						VSIConstants.ELE_SHIPMENT_LINE);
				for (Element eleShipmentLine : alShipmentList) {
					Element eleExtnOut = SCXmlUtil.getChildElement(eleShipmentLine, VSIConstants.ELE_EXTN);
					if (eleExtn.getAttribute(VSIConstants.ATTR_EXTN_GIFT_CARD_NO).equals(eleExtnOut.getAttribute(VSIConstants.ATTR_EXTN_GIFT_CARD_NO))) {
						eleShipLine.setAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY,
								eleShipmentLine.getAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY));
					}
				}
				eleShipLine.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, eleShipOutput.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY));
			} else {
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
				Element eleShipLinesIn = SCXmlUtil.createChild(eleShipmentInput, VSIConstants.ELE_SHIPMENT_LINES);
				Element eleShipLineIn = SCXmlUtil.createChild(eleShipLinesIn, VSIConstants.ELE_SHIPMENT_LINE);
				Element eleExtn = SCXmlUtil.createChild(eleShipLineIn, VSIConstants.ELE_EXTN);
				eleShipLineIn.setAttribute(VSIConstants.ATTR_QUANTITY, VSIConstants.ONE);
				eleShipLineIn.setAttribute(VSIConstants.ATTR_ITEM_ID, eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID));
				eleShipLineIn.setAttribute(VSIConstants.ATTR_ITEM_DESC, eleItem.getAttribute(VSIConstants.ATTR_ITEM_DESC));
				eleShipLineIn.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
				eleShipLineIn.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY));
				eleShipLineIn.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				eleExtn.setAttribute(VSIConstants.ATTR_EXTN_GIFT_CARD_NO,giftCardNo);
				docShipOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_SHIP_ACTIVATE_GIFT_CARD,
						VSIConstants.API_CREATE_SHIPMENT, docShipmentInput);
				eleShipOutput = docShipOutput.getDocumentElement();
				Element eleShipLine = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(eleShipOutput,
						VSIConstants.ELE_SHIPMENT_LINES), VSIConstants.ELE_SHIPMENT_LINE);
				eleShipLineIn.setAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY,
						eleShipLine.getAttribute(VSIConstants.ATTR_SHIPMENT_LINE_KEY));
				eleShipLineIn.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, eleShipOutput.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY));
			}
			eleShipmentInput.setAttribute(VSIConstants.ATTR_SHIPMENT_KEY, eleShipOutput.getAttribute(VSIConstants.ATTR_SHIPMENT_KEY));
			
		} catch (RemoteException re) {
			log.error("RemoteException in VSIActivateGiftCardAPI.activateGiftCard() : " , re);
			throw VSIUtils.getYFSException(re, "RemoteException occurred", "RemoteException in VSIActivateGiftCardAPI.activateGiftCard() : ");
		} catch (YIFClientCreationException yife) {
			log.error("YIFClientCreationException in VSIActivateGiftCardAPI.activateGiftCard() : " , yife);
			throw VSIUtils.getYFSException(yife, "YIFClientCreationException occurred", "YIFClientCreationException in VSIActivateGiftCardAPI.activateGiftCard() : ");
		} catch (YFSException yfse) {
			log.error("YFSException in VSIActivateGiftCardAPI.activateGiftCard() : " , yfse);
			throw yfse;
		} catch (Exception e) {
			log.error("Exception in VSIActivateGiftCardAPI.activateGiftCard() : " , e);
			throw VSIUtils.getYFSException(e, "Exception occurred", "Exception in VSIActivateGiftCardAPI.activateGiftCard()");
		}
		log.endTimer("VSIActivateGiftCardAPI.activateGiftCard : END");
		return docShipmentInput;
	}
	
}