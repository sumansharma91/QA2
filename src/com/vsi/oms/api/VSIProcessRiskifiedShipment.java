package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.riskified.RiskifiedClient;
import com.riskified.models.FulfillmentDetails;
import com.riskified.models.FulfillmentOrder;
import com.riskified.models.LineItem;
import com.riskified.models.Response;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIProcessRiskifiedShipment {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIProcessRiskifiedShipment.class);
	
	public void processRiskifiedShipment(YFSEnvironment env, Document inXML) {
		
		log.debug("processRiskifiedShipment input is : " + XMLUtil.getXMLString(inXML));
		try {
			
			Element eleShipment = inXML.getDocumentElement();
			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment,
					VSIConstants.ELE_SHIPMENT_LINES);
			String strShipmentNo = SCXmlUtil.getAttribute(eleShipment, VSIConstants.ATTR_SHIPMENT_NO);
			String strOrderHeaderKey = eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			
			List<Element> listShipmentLine = new ArrayList<Element>();
			listShipmentLine = SCXmlUtil.getElements(eleShipmentLines, VSIConstants.ELE_SHIPMENT_LINE);
			String strOrderNoConfShip = null;
			
			String strShipDate = SCXmlUtil.getAttribute(eleShipment, VSIConstants.ATTR_SHIP_DATE);
			String strTrackingNo = SCXmlUtil.getAttribute(eleShipment, VSIConstants.ATTR_TRACKING_NO);
			String strSCAC = SCXmlUtil.getAttribute(eleShipment, VSIConstants.ATTR_SCAC);
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss-hh:mm", Locale.US);
			// Create Shipment API Request
			List<FulfillmentDetails> fulfillments = new ArrayList<FulfillmentDetails>();
			FulfillmentDetails fulfilmentDetails = new FulfillmentDetails(strShipmentNo,
					dt.parse(strShipDate), VSIConstants.SUCCESS);
			List<LineItem> lineItems = new ArrayList<LineItem>();
			for (Element eleShipmentLine : listShipmentLine) {
				Element eleOrderLine = SCXmlUtil.getChildElement(eleShipmentLine,
						VSIConstants.ELE_ORDER_LINE);
				Element eleItemDetails = (Element) eleOrderLine
						.getElementsByTagName(VSIConstants.ELE_ITEM_DETAILS).item(0);
				Element eleItemExtn = (Element) eleItemDetails.getElementsByTagName(VSIConstants.ELE_EXTN)
						.item(0);
				strOrderNoConfShip = SCXmlUtil.getAttribute(eleShipmentLine, VSIConstants.ATTR_ORDER_NO);
				Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine,
						VSIConstants.ELE_LINE_PRICE_INFO);
				String strUnitPrice = SCXmlUtil.getAttribute(eleLinePriceInfo,
						VSIConstants.ATTR_UNIT_PRICE);
				String strItemId = SCXmlUtil.getAttribute(eleShipmentLine, VSIConstants.ITEM_ID);
				String strSkuId = eleItemExtn.getAttribute(VSIConstants.EXTN_ACT_SKU_ID);
				String strBrandTitle = eleItemExtn.getAttribute(VSIConstants.ATTR_EXTN_BRAND_TITLE);
				String strItemDesc = SCXmlUtil.getAttribute(eleLinePriceInfo, VSIConstants.ATTR_ITEM_DESC);
				int intOuqntity = SCXmlUtil.getIntAttribute(eleShipmentLine, VSIConstants.ATTR_QUANTITY);
				Double dUnitPrice=0.00;
				if (!YFCObject.isVoid(strUnitPrice)) {
					dUnitPrice = Double.parseDouble(strUnitPrice);
				}
					LineItem lineItem = new LineItem(dUnitPrice, intOuqntity, strItemDesc, strSkuId);
					lineItem.setTitle(strBrandTitle);
					lineItem.setSku(strItemId);
					lineItem.setProductType("physical");
					
					//Changes for OMS-2217 -- Start
					lineItem.setProperties(null);
					lineItem.setTaxLines(null);
					//Changes for OMS-2217 -- End
					
					lineItems.add(lineItem);
			}
			fulfilmentDetails.setLineItems(lineItems);
			fulfilmentDetails.setTrackingCompany(strSCAC);
			fulfilmentDetails.setTrackingNumbers(strTrackingNo);
			fulfillments.add(fulfilmentDetails);

			FulfillmentOrder fulfillmentOrder = new FulfillmentOrder(strOrderNoConfShip, fulfillments);
			String strFulFillShipment = VSIConstants.VSI_FULFILL_SHIPMENT;
			String strFulFillmentOrder = com.riskified.JSONFormater.toJson(fulfillmentOrder);
			log.debug("Riskified Request: " + strFulFillmentOrder);
			storeRequestandResponse(env, strOrderHeaderKey, strFulFillmentOrder, strFulFillShipment);
			RiskifiedClient client = new RiskifiedClient();
			Response resOutput = client.fulfillOrder(fulfillmentOrder);
			String strResponseOutput = com.riskified.JSONFormater.toJson(resOutput);
			log.debug("Riskified Response: " + strResponseOutput);
			
		}catch (YFSException e) {
			e.printStackTrace();
		}catch (RemoteException e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/**
	 * @param env
	 * @param ohk
	 * @param request
	 * @param chargeType
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws YFSException
	 * @throws RemoteException
	 * 
	 * This method store the request sent to
	 * Riskified system
	 */
	public void storeRequestandResponse(YFSEnvironment env, String ohk, String request, String chargeType)
			throws ParserConfigurationException, YIFClientCreationException, YFSException, RemoteException {
		Document doc = XMLUtil.createDocument(VSIConstants.ELE_PAYMENT_RECORDS);
		Element elePayRecrds = doc.getDocumentElement();
		elePayRecrds.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
		elePayRecrds.setAttribute(VSIConstants.ATTR_RECORD, request);
		elePayRecrds.setAttribute(VSIConstants.ATTR_CHARGE_TYPE, chargeType);
		YIFApi api;
		api = YIFClientFactory.getInstance().getApi();
		api.executeFlow(env, VSIConstants.API_VSI_PAYEMENT_RECORDS, doc);
	}
}
