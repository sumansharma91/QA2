package com.vsi.oms.userexit;

import java.math.RoundingMode;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.condition.VSIConditionToCheckShipToHomeOrderStatus;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCException;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSOrderRepricingUE;

public class VSIOrderRepricingUEImpl implements YFSOrderRepricingUE,
		VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIOrderRepricingUEImpl.class);

	public Document orderReprice(YFSEnvironment yfsEnv, Document docInXML)
			throws YFSUserExitException {
		if(log.isDebugEnabled()){
			log.debug("*** Inside Method orderReprice");
		}
		
		if (FLAG_Y.equals(yfsEnv.getTxnObject("BYPASS_PRICING"))) {
			return null;
		}
		
		if("MigratedOrder".equals(docInXML.getDocumentElement().getAttribute("OrderName"))){
			return docInXML;
		}
		Document outdoc = null;
		String strExceptionString = "";

		// START - Fix for UUAT-3
		HashMap<String, String> mapPromotion = new HashMap<String, String>();

		Element eleOrder = docInXML.getDocumentElement();
		// Used in VSIConditionToCheckShipToHomeOrderStatus.evaluateCondition()
		eleOrder.setAttribute("FromOrderRepricing", "Y");
		Element elePromotions = (Element) eleOrder.getElementsByTagName(
				VSIConstants.ELE_PROMOTIONS).item(0);
		if (!YFCObject.isVoid(elePromotions)) {
			NodeList nlPromotions = elePromotions
					.getElementsByTagName(VSIConstants.ELE_PROMOTION);
			for (int i = 0; i < nlPromotions.getLength(); i++) {
				Element elePromotion = (Element) nlPromotions.item(i);
				String strPromotionID = elePromotion
						.getAttribute(VSIConstants.ATTR_PROMOTION_ID);
				Element eleExtnPromotion = (Element) elePromotion
						.getElementsByTagName(VSIConstants.ELE_EXTN).item(0);
				String strExtnCouponID = eleExtnPromotion
						.getAttribute(VSIConstants.ATTR_EXTN_COUPON_ID);
				mapPromotion.put(strPromotionID, strExtnCouponID);
			}
		}

		NodeList nlOrderLines = eleOrder
				.getElementsByTagName(VSIConstants.ELE_ORDER_LINES);
		for (int i = 0; i < nlOrderLines.getLength(); i++) {
			Element eleOrderLine = (Element) nlOrderLines.item(i);
			NodeList nlLineCharges = eleOrderLine
					.getElementsByTagName(VSIConstants.ELE_LINE_CHARGE);
			if (nlLineCharges.getLength() > 0) {
				for (int j = 0; j < nlLineCharges.getLength(); j++) {
					Element eleCharge = (Element) nlLineCharges.item(j);
					String strChargeName = eleCharge
							.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
					Element eleExtnCharge = (Element) eleCharge
							.getElementsByTagName(VSIConstants.ELE_EXTN)
							.item(0);
					String strCouponNumber = eleExtnCharge
							.getAttribute(VSIConstants.ATTR_COUPON_NUMBER);
					String strExtnChargeId = eleExtnCharge
							.getAttribute(VSIConstants.ATTR_EXTN_CHARGE_ID);

					Iterator itr = mapPromotion.entrySet().iterator();
					while (itr.hasNext()) {
						Map.Entry promotionEntry = (Map.Entry) itr.next();
						String strPromotionNo = (String) promotionEntry
								.getKey();
						String strCouponNo = (String) promotionEntry.getValue();
						if (strChargeName.contains(strPromotionNo)) {
							if (YFCObject.isVoid(strCouponNumber)) {
								eleExtnCharge.setAttribute(
										VSIConstants.ATTR_COUPON_NUMBER,
										strCouponNo);
							}
							if (YFCObject.isVoid(strExtnChargeId)) {
								eleExtnCharge.setAttribute(
										VSIConstants.ATTR_EXTN_CHARGE_ID,
										strPromotionNo);
							}
						}
					}
				}
			}
		}
		// END - Fix for UUAT-3

		// get condition attributes
		Element rootElement = docInXML.getDocumentElement();
		String attrOrderType = rootElement
				.getAttribute(VSIConstants.ATTR_ORDER_TYPE);

		String attrIsNewOrder = rootElement
				.getAttribute(VSIConstants.ATTR_IS_NEW_ORDER);
		String attrDraftOrderFlag = rootElement
				.getAttribute(VSIConstants.ATTR_DRAFT_ORDER_FLAG);
		String progId = yfsEnv.getProgId();
		/*OMS-1171 changes: Start   */
		//double attrMaxOrderStatus = SCXmlUtil.getDoubleAttribute(rootElement,VSIConstants.ATTR_MAX_ORDER_STATUS);
		//double attrMinOrderStatus = SCXmlUtil.getDoubleAttribute(rootElement,VSIConstants.ATTR_MIN_ORDER_STATUS);
		String strMaxOrderStatus=SCXmlUtil.getAttribute(rootElement,VSIConstants.ATTR_MAX_ORDER_STATUS);
		String strMinOrderStatus=SCXmlUtil.getAttribute(rootElement,VSIConstants.ATTR_MIN_ORDER_STATUS);
		double attrMaxOrderStatus =0.0;
		double attrMinOrderStatus=0.0;
		if (strMaxOrderStatus != null && 
				("3700.01.10.ex".equals(strMaxOrderStatus) || "3700.01.20.ex".equals(strMaxOrderStatus)
				)) {
			strMaxOrderStatus=strMaxOrderStatus.substring(0,7);
			if(log.isDebugEnabled()){
				log.debug("trimmed strMaxOrderStatus"+strMaxOrderStatus);
			}
			attrMaxOrderStatus=Double.parseDouble(strMaxOrderStatus);
		}
		else
		{	
		 attrMaxOrderStatus = SCXmlUtil.getDoubleAttribute(rootElement,VSIConstants.ATTR_MAX_ORDER_STATUS);
		}
		if ( strMinOrderStatus!= null && 
				("3700.01.10.ex".equals(strMinOrderStatus) || "3700.01.20.ex".equals(strMinOrderStatus)
				)) {
			strMinOrderStatus=SCXmlUtil.getAttribute(rootElement,VSIConstants.ATTR_MIN_ORDER_STATUS).substring(0, 7);
			attrMaxOrderStatus=Double.parseDouble(strMinOrderStatus);
		}
		else
		{	
			attrMinOrderStatus = SCXmlUtil.getDoubleAttribute(rootElement,VSIConstants.ATTR_MIN_ORDER_STATUS);
		}
		/*OMS-1171 changes: End   */
		VSIConditionToCheckShipToHomeOrderStatus VSIConditionToCheckShipToHomeOrderStatus = new VSIConditionToCheckShipToHomeOrderStatus();
		// Get deliever method for first Orderline
		NodeList nlOrderLine = SCXmlUtil.getXpathNodes(rootElement,
				"OrderLines/OrderLine");
		int nlOrderLineLength = nlOrderLine.getLength();
		String attrDelMeth = ((Element) nlOrderLine.item(0))
				.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);

		// If order IS NEWORDER and IS a DRAFT Order
		if (!YFCObject.isVoid(attrIsNewOrder)
				&& VSIConstants.FLAG_Y.equalsIgnoreCase(attrIsNewOrder)
				&& !YFCObject.isVoid(attrDraftOrderFlag)
				&& VSIConstants.FLAG_Y.equalsIgnoreCase(attrDraftOrderFlag)) {
			if(log.isDebugEnabled()){
				log.debug("*** Inside Order is a NewOrder and the order is a Draft order");
				log.info("***Updated UE Docuement" + SCXmlUtil.getString(docInXML));
			}
			return docInXML;
		}
		// Inside Order is a NewOrder and not a Draft Order
		if (!YFCObject.isVoid(attrIsNewOrder)
				&& VSIConstants.FLAG_Y.equalsIgnoreCase(attrIsNewOrder)
				&& !YFCObject.isVoid(attrDraftOrderFlag)
				&& VSIConstants.FLAG_N.equalsIgnoreCase(attrDraftOrderFlag)) {
			if(log.isDebugEnabled()){
				log.debug("*** Inside Order is a NewOrder and not a Draft Order");
			}
			rootElement.setAttribute("ValidatePromotionAward", FLAG_N);

			NodeList nlPromotion = SCXmlUtil.getXpathNodes(rootElement,
					"Promotions/Promotion");
			int nlPromotionLength = nlPromotion.getLength();
			if (nlPromotionLength < 1) {
				if(log.isDebugEnabled()){
					log.debug("*** No Order Level Promotions");
				}
			}
			for (int i = 0; i < nlPromotionLength; i++) {
				Element elePromotion = (Element) nlPromotion.item(i);

				if (nlPromotionLength > 0) {
					HashMap<String, String> hmPromo = new HashMap<String, String>();
					String attrPromotionApplied = elePromotion
							.getAttribute(VSIConstants.ATTR_PROMOTION_APPLIED);
					String attrPromotionId = elePromotion
							.getAttribute(VSIConstants.ATTR_PROMOTION_ID);
					Element eleExtn = SCXmlUtil.getChildElement(elePromotion,
							VSIConstants.ELE_EXTN);
					String attrExtnDescription = eleExtn
							.getAttribute(VSIConstants.ATTR_EXTN_DESCRIPTION);

					if (!YFCObject.isVoid(attrPromotionApplied)
							&& VSIConstants.FLAG_Y
									.equalsIgnoreCase(attrPromotionApplied)) {
						hmPromo.put(VSIConstants.ATTR_PROMOTION_ID + i,
								attrPromotionId);
						hmPromo.put(VSIConstants.ATTR_EXTN_DESCRIPTION + i,
								attrExtnDescription);
						elePromotion.setAttribute(
								VSIConstants.ATTR_DESCRIPTION,
								attrExtnDescription);
					}
					if(log.isDebugEnabled()){
						log.debug("***Order Level Promotions that have been applied"
								+ hmPromo);
					}
				}

			}

			for (int i = 0; i < nlOrderLineLength; i++) {
				HashMap<String, String> hmPromo = new HashMap<String, String>();
				Element eleOrderLine = (Element) nlOrderLine.item(i);
				double attrOrderedQty = SCXmlUtil.getDoubleAttribute(
						eleOrderLine, VSIConstants.ATTR_ORD_QTY);

				Element eleOLPromotions = SCXmlUtil.getChildElement(
						eleOrderLine, ELE_PROMOTIONS);
				eleOLPromotions.setAttribute(ATTR_RESET, FLAG_Y);
				ArrayList<Element> arrPromotions = SCXmlUtil.getChildren(
						eleOLPromotions, ELE_PROMOTION);
				for (Element elePromotion : arrPromotions) {
					Element elePromotionExtn = SCXmlUtil.getChildElement(
							elePromotion, ELE_EXTN);
					String strCouponId = elePromotionExtn
							.getAttribute("ExtnCouponID");
					if (!YFCCommon.isVoid(strCouponId)) {
						String strPromotionId = elePromotion
								.getAttribute(ATTR_PROMOTION_ID);
						// log.debug("PromotionId is :"+strPromotionId);
						String atgXMLPromotionXPath = "/Order/Promotions/Promotion[@PromotionId="
								+ strPromotionId + "]";
						if (YFCCommon.isVoid(SCXmlUtil.getXpathElement(
								rootElement, atgXMLPromotionXPath))) {
							Element eleATGOrderPromotions = SCXmlUtil
									.getChildElement(rootElement,
											ELE_PROMOTIONS);
							if (YFCCommon.isVoid(eleATGOrderPromotions)) {
								eleATGOrderPromotions = SCXmlUtil.createChild(
										rootElement, ELE_PROMOTIONS);
							}
							SCXmlUtil.importElement(eleATGOrderPromotions,
									elePromotion);
							SCXmlUtil.removeNode(elePromotion);
						}
					}
				}

				NodeList nlOLPromotion = SCXmlUtil.getXpathNodes(eleOrderLine,
						"Promotions/Promotion");
				int nlOLPromotionLength = nlOLPromotion.getLength();
				if (nlOLPromotionLength < 1) {
					if(log.isDebugEnabled()){
						log.debug("***No Promotions available for this OrderLine"
								+ SCXmlUtil.getString(eleOrderLine));
					}
				}
				for (int k = 0; k < nlOLPromotionLength; k++) {
					Element eleOLPromotion = (Element) nlOLPromotion.item(k);

					String attrPromotionApplied = eleOLPromotion
							.getAttribute(VSIConstants.ATTR_PROMOTION_APPLIED);
					String attrPromotionId = eleOLPromotion
							.getAttribute(VSIConstants.ATTR_PROMOTION_ID);
					Element eleExtn = SCXmlUtil.getChildElement(eleOLPromotion,
							VSIConstants.ELE_EXTN);
					String attrExtnDescription = eleExtn
							.getAttribute(VSIConstants.ATTR_EXTN_DESCRIPTION);

					if (!YFCObject.isVoid(attrPromotionApplied)
							&& !VSIConstants.FLAG_N
									.equalsIgnoreCase(attrPromotionApplied)) {
						hmPromo.put(VSIConstants.ATTR_PROMOTION_ID + k,
								attrPromotionId);
						hmPromo.put(VSIConstants.ATTR_EXTN_DESCRIPTION + k,
								attrExtnDescription);

					}
					if(log.isDebugEnabled()){
						log.debug("*** Order Line Promotions that have been applied"
								+ hmPromo);
					}
				}
				NodeList nlLineCharge = SCXmlUtil.getXpathNodes(eleOrderLine,
						"LineCharges/LineCharge");
				if (nlLineCharge.getLength() < 1) {
					if(log.isDebugEnabled()){
						log.debug("***No LineCharges available for this OrderLine"
								+ SCXmlUtil.getString(eleOrderLine));
					}
				}
				if (nlLineCharge.getLength() > 0) {
					int nlLineChargeLength = nlLineCharge.getLength();

					for (int k = 0; k < nlLineChargeLength; k++) {
						Element eleLineCharge = (Element) nlLineCharge.item(k);

						Element eleExtn = SCXmlUtil.getChildElement(
								eleLineCharge, VSIConstants.ELE_EXTN);
						String attrChargeCategory = eleLineCharge
								.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
						String attrChargeName = eleLineCharge
								.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
						double attrChargePerLine = SCXmlUtil
								.getDoubleAttribute(eleLineCharge,
										VSIConstants.ATTR_CHARGE_PER_LINE);
						double attrChargePerUnit = SCXmlUtil
								.getDoubleAttribute(eleLineCharge,
										VSIConstants.ATTR_CHARGE_PER_UNIT);

						// Set Awards based on line Charges
						String attrPromoNum = eleExtn
								.getAttribute(VSIConstants.ATTR_PROMO_NUM);
						String attrCouponNumber = eleExtn
								.getAttribute(VSIConstants.ATTR_COUPON_NUMBER);
						String attrPromoName = eleExtn
								.getAttribute(VSIConstants.ATTR_PROMO_NAME);
						String promoDescription = hmPromo
								.get(VSIConstants.ATTR_EXTN_DESCRIPTION + k);
						// set promoDescription as Promo number if it is null or
						// empty
						if (YFCObject.isVoid(promoDescription)) {
							promoDescription = attrPromoNum;
						}

						String strLineChargePromoId = eleExtn
								.getAttribute(ATTR_EXTN_CHARGE_ID);
						if (!YFCCommon.isVoid(strLineChargePromoId)) {
							Element eleAwards = SCXmlUtil.getChildElement(
									eleOrderLine, ELE_AWARDS);
							if (YFCCommon.isVoid(eleAwards)) {
								eleAwards = SCXmlUtil.createChild(eleOrderLine,
										ELE_AWARDS);
							}
							eleAwards.setAttribute(ATTR_RESET, FLAG_Y);
							String strAwardXPath = "/Awards/Award[@PromotionId="
									+ strLineChargePromoId + "]";
							Element eleAward = SCXmlUtil.getXpathElement(
									eleAwards, strAwardXPath);
							if (YFCCommon.isVoid(eleAward)) {
								eleAward = SCXmlUtil.createChild(eleAwards,
										ELE_AWARD);
								eleAward.setAttribute(
										ATTR_AWARD_AMOUNT,
										eleLineCharge
												.getAttribute(ATTR_CHARGE_PER_LINE));
								eleAward.setAttribute(ATTR_AWARD_APPLIED,
										FLAG_Y);
								eleAward.setAttribute(ATTR_AWARD_ID,
										String.valueOf(i));
								eleAward.setAttribute(
										ATTR_AWARD_TYPE,
										eleLineCharge
												.getAttribute(ATTR_CHARGE_CATEGORY));
								eleAward.setAttribute(
										ATTR_CHARGE_CATEGORY,
										eleLineCharge
												.getAttribute(ATTR_CHARGE_CATEGORY));
								eleAward.setAttribute(ATTR_CHARGE_NAME,
										eleLineCharge
												.getAttribute(ATTR_CHARGE_NAME));
								eleAward.setAttribute(ATTR_DESCRIPTION,
										eleLineCharge
												.getAttribute(ATTR_CHARGE_NAME));
								eleAward.setAttribute(ATTR_PROMOTION_ID,
										strLineChargePromoId);
							}
						}
						if(log.isDebugEnabled()){
							log.debug("***Awards for this OrderLine"
									+ SCXmlUtil.getString(eleOrderLine));
						}

						if (!YFCObject.isVoid(attrOrderType)
								&& VSIConstants.ATTR_ORDER_TYPE_VALUE
										.equalsIgnoreCase(attrOrderType)) {
							Element elePromotionIsLC = SCXmlUtil
									.getXpathElement(rootElement,
											"Promotions/Promotion[@PromotionId ='"
													+ attrCouponNumber + "']");
							if (!YFCObject.isVoid(SCXmlUtil
									.getString(elePromotionIsLC))) {
								elePromotionIsLC.setAttribute(
										VSIConstants.ATTR_EXTN_DESCRIPTION,
										attrPromoName);
							}
						}

						// ARS-230 : Stamp ExtnType="MXMH" if line charge is
						// BOGO
						prorateLineCharges(eleLineCharge, attrOrderedQty,
								attrChargePerLine, attrChargePerUnit);
						stampExtnTypeForBOGO(eleLineCharge);
					}
				}
				prorateLineTaxes(eleOrderLine, true);
			}

			if(log.isDebugEnabled()){
				log.info("***Updated UE Docuement" + SCXmlUtil.getString(docInXML));
			}
			return docInXML;
		}

		// (Delivery Method='Picked' and Is not draft order) OR
		// (DelieveryMethod=SHP and Not Draft Order and (ProgID=Call Center or
		// WebStore)) OR(Not Draft Order and Status is >1500 and <=9000
		if ((!YFCObject.isVoid(attrDelMeth)
				&& !YFCObject.isVoid(attrDraftOrderFlag)
				&& ((VSIConstants.ATTR_DEL_METHOD_PICK
						.equalsIgnoreCase(attrDelMeth) && VSIConstants.FLAG_N
						.equalsIgnoreCase(attrDraftOrderFlag)) && (!VSIConstants.PROG_ID_CALL_CENTER
						.equalsIgnoreCase(progId) && !VSIConstants.PROG_ID_STORE
						.equalsIgnoreCase(progId)))
				|| (("SHP".equalsIgnoreCase(attrDelMeth)
						&& VSIConstants.FLAG_N
								.equalsIgnoreCase(attrDraftOrderFlag) && (!VSIConstants.PROG_ID_CALL_CENTER
						.equalsIgnoreCase(progId) && !VSIConstants.PROG_ID_STORE
						.equalsIgnoreCase(progId)))) || (("SHP"
				.equalsIgnoreCase(attrDelMeth)
				&& VSIConstants.FLAG_N.equalsIgnoreCase(attrDraftOrderFlag)
				&& !YFCObject.isVoid(attrMaxOrderStatus)
				&& VSIConditionToCheckShipToHomeOrderStatus.evaluateCondition(
						yfsEnv, "VSIConditionToCheckShipToHomeOrderStatus",
						null, docInXML) == false && attrMaxOrderStatus <= 9000)))) {
			if(log.isDebugEnabled()){
				log.debug("*** (Delivery Method='Picked' and Is not draft order) OR (DelieveryMethod=SHP and Not Draft Order and (ProgID=Call Center or WebStore)) OR(Not Draft Order and Status is >1500 and <=9000");
			}
			
			for (int i = 0; i < nlOrderLineLength; i++) {
				Element eleOrderLine = (Element) nlOrderLine.item(i);
				// adjust order pricing based on Order Change
				double attrOrderedQty = SCXmlUtil.getDoubleAttribute(
						eleOrderLine, VSIConstants.ATTR_ORD_QTY);
				NodeList nlLineCharge = SCXmlUtil.getXpathNodes(eleOrderLine,
						"LineCharges/LineCharge");
				int nlLineChargeLength = nlLineCharge.getLength();
				for (int k = 0; k < nlLineChargeLength; k++) {
					Element eleLineCharge = (Element) nlLineCharge.item(k);
					String attrChargeCategory = eleLineCharge
							.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
					String attrChargeName = eleLineCharge
							.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
					if (attrOrderedQty == 0) {
						eleLineCharge.setAttribute(
								VSIConstants.ATTR_CHARGE_PER_UNIT,
								VSIConstants.ZERO_DOUBLE);
						eleLineCharge.setAttribute(
								VSIConstants.ATTR_CHARGE_PER_LINE,
								VSIConstants.ZERO_DOUBLE);
					} else if (!YFCObject.isVoid(attrChargeCategory)
							&& VSIConstants.REPLACEMENT_CHARGE_CTGY
									.equalsIgnoreCase(attrChargeCategory)
							&& !YFCObject.isVoid(attrChargeName)
							&& VSIConstants.REPLACEMENT_CHARGE_NAME
									.equalsIgnoreCase(attrChargeName)) {

						double attrChargePerLine = SCXmlUtil
								.getDoubleAttribute(eleLineCharge,
										VSIConstants.ATTR_CHARGE_PER_LINE);
						double attrChargePerUnit = SCXmlUtil
								.getDoubleAttribute(eleLineCharge,
										VSIConstants.ATTR_CHARGE_PER_UNIT);

						prorateLineCharges(eleLineCharge, attrOrderedQty,
								attrChargePerLine, attrChargePerUnit);
					}
				}

				prorateLineTaxes(eleOrderLine, false);
			}

			if ((!YFCObject.isVoid(attrMaxOrderStatus) && !YFCObject
					.isVoid(attrMinOrderStatus))
					&& (attrMaxOrderStatus == 9000 && attrMinOrderStatus == 9000)) {

				NodeList nlHeaderCharge = SCXmlUtil.getXpathNodes(rootElement,
						"HeaderCharges/HeaderCharge");
				int nlHeaderChargeLength = nlHeaderCharge.getLength();
				for (int k = 0; k < nlHeaderChargeLength; k++) {
					Element eleHeaderCharge = (Element) nlHeaderCharge.item(k);
					eleHeaderCharge.setAttribute(
							VSIConstants.ATTR_CHARGE_AMOUNT,
							String.valueOf(0.00));
				}

				Element headerTaxes = SCXmlUtil.getChildElement(rootElement,
						"HeaderTaxes");
				if (!YFCCommon.isVoid(headerTaxes)) {
					rootElement.removeChild(headerTaxes);
				}
				/**
				 * NodeList nlHeaderTax= SCXmlUtil.getXpathNodes(rootElement,
				 * "HeaderTaxes/HeaderTax"); int nlHeaderTaxLength =
				 * nlHeaderTax.getLength(); for(int k = 0; k <
				 * nlHeaderTaxLength; k ++) { Element eleHeaderTax =(Element)
				 * nlHeaderTax.item(k);
				 * eleHeaderTax.setAttribute(VSIConstants.ATTR_TAX
				 * ,String.valueOf(0.00)); }
				 **/
			}
			if(log.isDebugEnabled()){
				log.info("***Updated UE Docuement" + SCXmlUtil.getString(docInXML));
			}
			return docInXML;
		}

		// Is Draft order OR (not draft Order && (ProgID = call center or
		// webstore) && (MinOrderstatus is <=1500 or is canceled(9000)
		if (!YFCObject.isVoid(attrDraftOrderFlag)
				&& attrDraftOrderFlag.equalsIgnoreCase(VSIConstants.FLAG_Y)
				|| ((attrDraftOrderFlag.equalsIgnoreCase(VSIConstants.FLAG_N) && (VSIConstants.PROG_ID_CALL_CENTER
						.equalsIgnoreCase(progId) || VSIConstants.PROG_ID_STORE
						.equalsIgnoreCase(progId))) && (VSIConditionToCheckShipToHomeOrderStatus
						.evaluateCondition(yfsEnv,
								"VSIConditionToCheckShipToHomeOrderStatus",
								null, docInXML) == true))) {
			if(log.isDebugEnabled()){
				log.debug("*** Is Draft order OR (not draft Order && (ProgID = call center or webstore) && (Order Status is <=1500 or is canceled(9000)");
			}
			HashMap<String, String> lineChargehm = new HashMap<String, String>();
			HashMap<String, String> hmItems = new HashMap<String, String>();
			Document docATGWebServiceResponse = null;
			for (int i = 0; i < nlOrderLineLength; i++) {
				Element eleOrderLine = (Element) nlOrderLine.item(i);
				String orderLineKey = eleOrderLine.getAttribute("OrderLineKey");
				int intOrderedQty = Integer.parseInt(eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY));
				// String status = eleOrderLine.getAttribute("OrderLineKey");
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine,
						"Item");
				if (!YFCObject.isVoid(eleItem)) {
					String itemID = eleItem.getAttribute("ItemID");
					// If hashmap doesn't contain the order line SKU id, add to
					// hashmap
					// If hashmap already contains the SKU ID, throw error
					// because it's duplicate
					if (hmItems.containsKey(itemID) && intOrderedQty>0) {
						hmItems.clear();
						strExceptionString = "Duplicate Item found on order: "
								+ itemID;
						YFSException ex = new YFSException("EXTN_ERROR",
								"EXTN_ERROR", strExceptionString);
						throw new YFCException(ex, "EXTN_ERROR",
								strExceptionString);
						// throw new YFSException(ex1.getXMLErrorBuf());
					} else if(intOrderedQty>0){
						hmItems.put(itemID, orderLineKey);
					}
				}
				Element eleLineCharges = SCXmlUtil.createChild(eleOrderLine,
						VSIConstants.ELE_LINE_CHARGES);
				// make changes to order if orderline is canceled or if
				// orderline status is
				NodeList nlLineCharge = SCXmlUtil.getXpathNodes(eleOrderLine,
						"LineCharges/LineCharge[@IsManual='Y']");
				int nlLineChargeLength = nlLineCharge.getLength();
				for (int k = 0; k < nlLineChargeLength; k++) {

					Element eleLineCharge = (Element) nlLineCharge.item(k);
					double attrOrderedQty = SCXmlUtil.getDoubleAttribute(
							eleOrderLine, VSIConstants.ATTR_ORD_QTY);

					double attrChargePerLine = SCXmlUtil.getDoubleAttribute(
							eleLineCharge, VSIConstants.ATTR_CHARGE_PER_LINE);
					double attrChargePerUnit = SCXmlUtil.getDoubleAttribute(
							eleLineCharge, VSIConstants.ATTR_CHARGE_PER_UNIT);

					prorateLineCharges(eleLineCharge, attrOrderedQty,
							attrChargePerLine, attrChargePerUnit);
					String attrOrderLineKey = eleOrderLine
							.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);

					lineChargehm.put(VSIConstants.ATTR_ORDER_LINE_KEY + i,
							attrOrderLineKey);
					lineChargehm.put(VSIConstants.ELE_LINE_CHARGES + i,
							SCXmlUtil.getString(eleLineCharges));
				}
			}
			Element eleInputHeaderCharges = SCXmlUtil.getChildElement(
					rootElement, VSIConstants.ELE_HEADER_CHARGES);
			Element eleInputHeaderTaxes = SCXmlUtil.getChildElement(
					rootElement, VSIConstants.ELE_HEADER_TAXES);
			Double dblShippingTaxRate = getShippingTaxRate(
					eleInputHeaderCharges, eleInputHeaderTaxes);

			hmItems.clear();

			try {
				// ARS-297 : Process free line addition/deletion : BEGIN
				HashMap<String, Element> mapFreeItems = new HashMap<>();
				Document docATGPricingInput = formATGPricingInput(docInXML,
						mapFreeItems);
				Element eleATGPricingInput = docATGPricingInput
						.getDocumentElement();
				String strLineType = eleATGPricingInput
						.getAttribute(ATTR_LINE_TYPE);
				String strDeliveryMethod = eleATGPricingInput
						.getAttribute(ATTR_DELIVERY_METHOD);
				eleATGPricingInput.removeAttribute(ATTR_LINE_TYPE);
				eleATGPricingInput.removeAttribute(ATTR_DELIVERY_METHOD);
				Document webServiceOutput = VSIUtils.invokeService(yfsEnv,
						VSIConstants.SERVICE_VSI_ATG_PRICING_SERVICE,
						docATGPricingInput);
				// ARS-297 : Process free line addition/deletion : END

				Element eleOutputOrder = (Element) webServiceOutput
						.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				docATGWebServiceResponse = SCXmlUtil.createFromString(SCXmlUtil
						.getString(eleOutputOrder));
				//OMS-1579:Start
				if(log.isDebugEnabled()){
					log.debug("Before appending:"+XMLUtil.getXMLString(docATGWebServiceResponse) );
				}
				appendCouponNumber(docATGWebServiceResponse);
				if(log.isDebugEnabled()){
					log.debug("After appending:"+XMLUtil.getXMLString(docATGWebServiceResponse) );
				}
				//OMS-1579:END				
				Element rootElementAtg = (Element) docATGWebServiceResponse
						.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
				rootElementAtg.setAttribute(ATTR_ORDER_HEADER_KEY,
						rootElement.getAttribute(ATTR_ORDER_HEADER_KEY));
				rootElementAtg.setAttribute("ValidatePromotionAward", FLAG_N);

				Element eleHeaderChargesAtg = SCXmlUtil.getChildElement(
						rootElementAtg, VSIConstants.ELE_HEADER_CHARGES);

				NodeList nlOrderLineAtg = SCXmlUtil.getXpathNodes(
						rootElementAtg, "OrderLines/OrderLine");
				int nlOrderLineLengthAtg = nlOrderLineAtg.getLength();
				if (rootElementAtg.hasChildNodes()) {
					for (int i = 0; i < nlOrderLineLengthAtg; i++) {
						Element eleOrderLine = (Element) nlOrderLineAtg.item(i);
						Element eleOrderLineCharges = SCXmlUtil
								.getChildElement(eleOrderLine, ELE_LINE_CHARGES);
						if (YFCCommon.isVoid(eleOrderLineCharges)) {
							eleOrderLineCharges = SCXmlUtil.createChild(
									eleOrderLine, ELE_LINE_CHARGES);
						}
						SCXmlUtil.setAttribute(eleOrderLineCharges, ATTR_RESET,
								FLAG_Y);
						String strOrderLineKey = eleOrderLine
								.getAttribute(ATTR_ORDER_LINE_KEY);

						String strOrderLineXPath = "/Order/OrderLines/OrderLine[@OrderLineKey='"
								+ strOrderLineKey + "']";

						Element eleOrderLineIncoming = SCXmlUtil
								.getXpathElement(eleOrder, strOrderLineXPath);
						Element eleOrderLineIncomingCharges = SCXmlUtil
								.getChildElement(eleOrderLineIncoming,
										ELE_LINE_CHARGES);
						ArrayList<Element> arrOrderLineIncomingCharges = SCXmlUtil
								.getChildren(eleOrderLineIncomingCharges,
										ELE_LINE_CHARGE);
						for (Element eleOrderLineIncomingCharge : arrOrderLineIncomingCharges) {

							String strChargeCategory = eleOrderLineIncomingCharge
									.getAttribute(ATTR_CHARGE_CATEGORY);
							// If charge category is manual then its a manual
							// charge
							//OMS-1033, copy the OrderDifferenceDiscount discount to the ATG response
							//as it is manually added during order creation 
							String strChargeName=eleOrderLineIncomingCharge
									.getAttribute(ATTR_CHARGE_NAME);
							if (strChargeCategory
									.equalsIgnoreCase(DISCOUNT_CATEGORY_ADJ) 
									||("OrderDifferenceDiscount").equals(strChargeName)) {
								SCXmlUtil.importElement(eleOrderLineCharges,
										eleOrderLineIncomingCharge);
							}
						}

						Element eleOLAwards = SCXmlUtil.getChildElement(
								eleOrderLine, ELE_AWARDS);
						if (!YFCCommon.isVoid(eleOLAwards)) {
							ArrayList<Element> arrAwards = SCXmlUtil
									.getChildren(eleOLAwards, ELE_AWARD);
							for (Element eleAward : arrAwards) {
								eleAward.setAttribute(ATTR_DESCRIPTION,
										eleAward.getAttribute(ATTR_CHARGE_NAME));
							}
						}

						Element eleOLPromotions = SCXmlUtil.getChildElement(
								eleOrderLine, ELE_PROMOTIONS);
						if (!YFCCommon.isVoid(eleOLPromotions)) {
							ArrayList<Element> arrPromotions = SCXmlUtil
									.getChildren(eleOLPromotions, ELE_PROMOTION);
							for (Element elePromotion : arrPromotions) {
								Element elePromotionExtn = SCXmlUtil
										.getChildElement(elePromotion, ELE_EXTN);
								String strCouponId = elePromotionExtn
										.getAttribute("ExtnCouponID");
								if (!YFCCommon.isVoid(strCouponId)) {
									String strPromotionId = elePromotion
											.getAttribute(ATTR_PROMOTION_ID);
									// log.debug("PromotionId is :"+strPromotionId);
									String atgXMLPromotionXPath = "/Order/Promotions/Promotion[@PromotionId="
											+ strPromotionId + "]";
									Element eleATGOrderLevelPromotion = SCXmlUtil
											.getXpathElement(rootElementAtg,
													atgXMLPromotionXPath);
									if (YFCCommon
											.isVoid(eleATGOrderLevelPromotion)) {
										Element eleATGOrderPromotions = SCXmlUtil
												.getChildElement(
														rootElementAtg,
														ELE_PROMOTIONS);
										if (YFCCommon
												.isVoid(eleATGOrderPromotions)) {
											eleATGOrderPromotions = SCXmlUtil
													.createChild(
															rootElementAtg,
															ELE_PROMOTIONS);
										}
										SCXmlUtil.importElement(
												eleATGOrderPromotions,
												elePromotion);
										SCXmlUtil.removeNode(elePromotion);
									}
								}
							}
						}

						// ARS-235 : Modified to loop through the line charges
						// to set the attributes on the line charge it is
						// associated with : BEGIN
						Element eleLineCharges = SCXmlUtil.getChildElement(
								eleOrderLine, ELE_LINE_CHARGES);
						ArrayList<Element> alLineChargeList = SCXmlUtil
								.getChildren(eleLineCharges, ELE_LINE_CHARGE);
						for (Element eleLineCharge : alLineChargeList) {
							double attrOrderedQty = SCXmlUtil
									.getDoubleAttribute(eleOrderLine,
											VSIConstants.ATTR_ORD_QTY);
							double attrChargePerUnit = SCXmlUtil
									.getDoubleAttribute(
											((Element) eleLineCharge),
											VSIConstants.ATTR_CHARGE_PER_UNIT);
							double attrChargePerLine = SCXmlUtil
									.getDoubleAttribute(
											((Element) eleLineCharge),
											VSIConstants.ATTR_CHARGE_PER_LINE);

							// eleLineCharge.setAttribute("IsManual", FLAG_N);
							prorateLineCharges(eleLineCharge, attrOrderedQty,
									attrChargePerLine, attrChargePerUnit);

							// ARS-230 : Stamp ExtnType="MXMH" if line charge is
							// BOGO
							stampExtnTypeForBOGO(eleLineCharge);
						}
						// ARS-235 : Modified to loop through the line charges
						// to set the attributes on the line charge it is
						// associated with : END

						Element eleATGLinePromotions = SCXmlUtil
								.getChildElement(eleOrderLine, ELE_PROMOTIONS);
						if (YFCCommon.isVoid(eleATGLinePromotions)) {
							eleATGLinePromotions = SCXmlUtil.createChild(
									eleOrderLine, ELE_PROMOTIONS);
						}

					}

					String attrChargeAmount = null;
					if (!YFCCommon.isVoid(eleHeaderChargesAtg)) {
						Element eleHeaderChargeATG = SCXmlUtil.getChildElement(
								eleHeaderChargesAtg,
								VSIConstants.ELE_HEADER_CHARGE);
						if (!YFCCommon.isVoid(eleHeaderChargeATG)) {
							attrChargeAmount = eleHeaderChargeATG
									.getAttribute(VSIConstants.ATTR_CHARGE_AMOUNT);
						}
					} else {
						Element eleHeaderChargsSATG = SCXmlUtil
								.createChild(rootElementAtg,
										VSIConstants.ELE_HEADER_CHARGES);
						eleHeaderChargsSATG.setAttribute("Reset", "Y");
					}

					NodeList nlHeaderChargeManual = SCXmlUtil.getXpathNodes(
							eleInputHeaderCharges,
							"HeaderCharge[@IsManual='Y']");
					double dblDiscount = 0.0;
					if (nlHeaderChargeManual.getLength() > 0) {
						for (int i = 0; i < nlHeaderChargeManual.getLength(); i++) {

							Element eleHeaderChargeManual = (Element) nlHeaderChargeManual
									.item(i);
							String attrIsDiscount = eleHeaderChargeManual
									.getAttribute(VSIConstants.ATTR_IS_DISCOUNT);
							String attrChargeCategory = eleHeaderChargeManual
									.getAttribute(VSIConstants.ATTR_CHARGE_CATEGORY);
							String attrChargeName = eleHeaderChargeManual
									.getAttribute(VSIConstants.ATTR_CHARGE_NAME);
							if (!YFCObject.isVoid(attrIsDiscount)
									&& attrIsDiscount
											.equalsIgnoreCase(VSIConstants.FLAG_Y)
									&& !YFCObject.isVoid(attrChargeCategory)
									&& attrChargeCategory
											.equalsIgnoreCase(VSIConstants.SHIPPING_CHARGE_CTGY)
									&& !YFCObject.isVoid(attrChargeName)
									&& attrChargeName
											.equalsIgnoreCase(VSIConstants.CHARGE_NAME_SHIPPING_DISCOUNT)) {
								dblDiscount += dblDiscount;
							} else if (!YFCObject.isVoid(attrChargeCategory)
									&& !attrChargeCategory
											.equalsIgnoreCase(VSIConstants.SHIPPING_CHARGE_CTGY)) {
								SCXmlUtil.importElement(eleHeaderChargesAtg,
										eleHeaderChargeManual);
							}

						}

						if (!YFCCommon.isVoid(attrChargeAmount)
								&& Double.valueOf(attrChargeAmount) <= dblDiscount) {
							Element headerChargeOut = SCXmlUtil.createChild(
									eleHeaderChargesAtg, "HeaderCharge");
							headerChargeOut.setAttribute(
									VSIConstants.ATTR_CHARGE_CATEGORY,
									VSIConstants.SHIPPING_CHARGE_CTGY);
							headerChargeOut.setAttribute(
									VSIConstants.ATTR_CHARGE_NAME,
									VSIConstants.CHARGE_NAME_SHIPPING_DISCOUNT);
							headerChargeOut.setAttribute(
									VSIConstants.ATTR_CHARGE_AMOUNT,
									String.valueOf(attrChargeAmount));
						}

					}

					Double outputShippingCharge = 0.0;
					if (!YFCObject.isVoid(eleHeaderChargesAtg)) {

						outputShippingCharge = getTotalShippingCharge(eleHeaderChargesAtg);
					}
					if (!YFCObject.isVoid(eleInputHeaderTaxes)
							&& eleInputHeaderTaxes.hasChildNodes()
							&& outputShippingCharge > 0) {

						Element eleATGHeaderTaxes = SCXmlUtil.importElement(
								rootElementAtg, eleInputHeaderTaxes);
						// eleATGHeaderTaxes.removeChild("TaxSummary");
						Element eleHeaderTax = SCXmlUtil.getChildElement(
								eleATGHeaderTaxes, ELE_HEADER_TAX);
						if (!YFCObject.isVoid(eleHeaderTax)) {

							SCXmlUtil.setAttribute(eleHeaderTax,
									"TaxPercentage", dblShippingTaxRate);
							Double dblHeaderTax = (dblShippingTaxRate * outputShippingCharge) / 100;
							SCXmlUtil.setAttribute(eleHeaderTax, ATTR_TAX,
									dblHeaderTax);
						}
					}

					// Promotions stuff

					NodeList listInputPromotion = SCXmlUtil.getXpathNodes(
							rootElement, "Promotions/Promotion");
					HashMap<String, Element> hmInputPromo = new HashMap<String, Element>();
					HashMap<String, Element> hmLinePromo = new HashMap<String, Element>();
					int listInputPromotionlength = listInputPromotion
							.getLength();
					for (int k = 0; k < listInputPromotionlength; k++) {
						Element elePromotion = (Element) listInputPromotion
								.item(k);

						String attrPromotionID = elePromotion
								.getAttribute(VSIConstants.ATTR_PROMOTION_ID);
						hmInputPromo.put(attrPromotionID, elePromotion);
					}

					Element eleOrderLines = SCXmlUtil.getChildElement(
							rootElementAtg, ELE_ORDER_LINES);
					ArrayList<Element> arrOrderLines = SCXmlUtil.getChildren(
							eleOrderLines, ELE_ORDER_LINE);

					// ARS-297 : Process free line addition/deletion : BEGIN
					String strOrderHeaderKey = eleOrder
							.getAttribute(ATTR_ORDER_HEADER_KEY);
					for (Element eleOrderLine : arrOrderLines) {
						processATGPricingOutput(yfsEnv, eleOrderLine,
								mapFreeItems, strOrderHeaderKey, strLineType,
								strDeliveryMethod);
						Element eleOLPromotions = SCXmlUtil.getChildElement(
								eleOrderLine, ELE_PROMOTIONS);
						ArrayList<Element> arrPromotions = SCXmlUtil
								.getChildren(eleOLPromotions, ELE_PROMOTION);
						for (Element elePromotion : arrPromotions) {
							String strPromotionId = elePromotion
									.getAttribute(ATTR_PROMOTION_ID);
							elePromotion.setAttribute("Action", "CREATE");
							// elePromotion.setAttribute("Action", "CREATE");
							if (hmInputPromo.containsKey(strPromotionId)) {
								hmInputPromo.remove(strPromotionId);
								hmLinePromo.put(strPromotionId, elePromotion);
							}
						}
					}

					// Delete any free lines that are no longer included on this
					// order
					if (!mapFreeItems.isEmpty()) {
						deleteLinesFromOrder(yfsEnv, eleOrderLines,
								mapFreeItems, strOrderHeaderKey);
					}
					// ARS-297 : Process free line addition/deletion : END

					Element eleOLPromotions = SCXmlUtil.getChildElement(
							rootElementAtg, ELE_PROMOTIONS);
					ArrayList<Element> arrPromotions = SCXmlUtil.getChildren(
							eleOLPromotions, ELE_PROMOTION);
					for (Element elePromotion : arrPromotions) {
						String strPromotionId = elePromotion
								.getAttribute(ATTR_PROMOTION_ID);
						if (hmInputPromo.containsKey(strPromotionId)) {
							hmInputPromo.remove(strPromotionId);
						}
					}

					if (hmInputPromo.isEmpty() == true) {
						if(log.isDebugEnabled()){
							log.debug("*****Promotions HashMap empty");
						}
					}

					Element elePromotionsAtg = SCXmlUtil.getChildElement(
							rootElementAtg, "Promotions");
					if (YFCCommon.isVoid(elePromotionsAtg)) {
						elePromotionsAtg = SCXmlUtil.createChild(
								rootElementAtg, "Promotions");
					}

					// change unapplied promotions to
					// PromotionApplied=VSIConstants.FLAG_N
					if (!hmInputPromo.isEmpty()) {
						Iterator<Entry<String, Element>> itPromo = hmInputPromo
								.entrySet().iterator();
						while (itPromo.hasNext()) {

							Map.Entry<String, Element> pair = (Map.Entry) itPromo
									.next();

							Element eleUnappliedPromotion = (Element) pair
									.getValue();
							if (!YFCCommon.isVoid(eleUnappliedPromotion)) {
								eleUnappliedPromotion.setAttribute(
										VSIConstants.ATTR_PROMOTION_APPLIED,
										VSIConstants.FLAG_N);
								Element impUnappliedPromotion = (Element) docATGWebServiceResponse
										.importNode(eleUnappliedPromotion, true);
								elePromotionsAtg
										.appendChild(impUnappliedPromotion);
							}
							itPromo.remove();
						}
					}
					if (!hmLinePromo.isEmpty()) {
						Iterator<Entry<String, Element>> itPromo = hmLinePromo
								.entrySet().iterator();
						while (itPromo.hasNext()) {

							Map.Entry<String, Element> pair = (Map.Entry) itPromo
									.next();

							Element linePromotion = (Element) pair.getValue();
							if (!YFCCommon.isVoid(linePromotion)) {

								Element implinePromotion = (Element) docATGWebServiceResponse
										.importNode(linePromotion, true);
								implinePromotion.setAttribute("Action",
										"REMOVE");
								implinePromotion.setAttribute(
										VSIConstants.ATTR_PROMOTION_APPLIED,
										VSIConstants.FLAG_Y);
								elePromotionsAtg.appendChild(implinePromotion);
							}
							itPromo.remove();
						}
					}
					if (elePromotionsAtg.hasChildNodes()) {
						elePromotionsAtg.setAttribute("Reset", "Y");
					}
				}
			} catch (Exception e) {

				Document createExceptionIP = null;
				try {
					createExceptionIP = XMLUtil
							.createDocument(VSIConstants.ELE_INBOX);
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Element eleInbox = createExceptionIP.getDocumentElement();
				Element eleOrderCreateExcept = SCXmlUtil.createChild(eleInbox,
						VSIConstants.ELE_ORDER);
				eleOrderCreateExcept
						.setAttribute(
								VSIConstants.ATTR_ORDER_HEADER_KEY,
								eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				eleInbox.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
						VSIConstants.ATTR_EXCEPTION_TYPE_VSI_WEB_TECHNICAL);
				eleInbox.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
				eleInbox.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION,
						VSIConstants.ATTR_ALERT_MSG_DESCRIPTION);
				eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION,
						VSIConstants.ATTR_ALERT_MSG_DESCRIPTION);
				eleInbox.setAttribute(VSIConstants.ATTR_CONSOLIDATE, "Y");
				Element ConsoltempEle = createExceptionIP
						.createElement(VSIConstants.ELE_CONSOLIDATE_TEMPLATE);
				eleInbox.appendChild(ConsoltempEle);
				Element InboxCpyEle = createExceptionIP.createElement(VSIConstants.ELE_INBOX);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
						eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO));
				InboxCpyEle.setAttribute(VSIConstants.ATTR_CONSOLIDATE, "Y");
				InboxCpyEle.setAttribute(VSIConstants.ATTR_ACTIVE_FLAG, "Y");
				InboxCpyEle.setAttribute(VSIConstants.ATTR_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.ATTR_ALERT_MSG_DESCRIPTION);
				
				InboxCpyEle.setAttribute(VSIConstants.ATTR_DETAIL_DESCRIPTION, VSIConstants.ATTR_ORDER_NO + SPACE
						+ SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_NO)
						+ VSIConstants.ATTR_ALERT_MSG_DESCRIPTION);
				//InboxCpyEle.setAttribute(VSIConstants.ATTR_ERROR_TYPE,VSI_REFUND);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_EXCEPTION_TYPE,
						VSIConstants.ATTR_EXCEPTION_TYPE_VSI_WEB_TECHNICAL);
				InboxCpyEle.setAttribute(VSIConstants.ATTR_EXPIRATION_DAYS, "0");
				ConsoltempEle.appendChild(InboxCpyEle);
				try {
					VSIUtils.invokeAPI(yfsEnv,
							VSIConstants.API_CREATE_EXCEPTION,
							createExceptionIP);
				} catch (YFSException | RemoteException
						| YIFClientCreationException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				for (int i = 0; i < nlOrderLineLength; i++) {
					Element eleOrderLine = (Element) nlOrderLine.item(i);
					double attrOrderedQty = SCXmlUtil.getDoubleAttribute(
							eleOrderLine, VSIConstants.ATTR_ORD_QTY);
					NodeList nlLineCharge = SCXmlUtil.getXpathNodes(
							eleOrderLine, "LineCharges/LineCharge");
					int nlLineChargeLength = nlLineCharge.getLength();
					boolean isFullyCancelled = true;
					for (int k = 0; k < nlLineChargeLength; k++) {
						if (attrOrderedQty == 0) {

							Element eleLineCharge = (Element) nlLineCharge
									.item(k);
							eleLineCharge.setAttribute(
									VSIConstants.ATTR_CHARGE_PER_UNIT,
									String.valueOf(0.00));
							eleLineCharge.setAttribute(
									VSIConstants.ATTR_CHARGE_PER_LINE,
									String.valueOf(0.00));

						} else {
							isFullyCancelled = false;
						}
					}
					if (isFullyCancelled) {
						NodeList nlHeaderCharges = SCXmlUtil.getXpathNodes(
								rootElement, "HeaderCharges/HeaderCharge");
						int nlHeaderChargesLength = nlHeaderCharges.getLength();
						for (int l = 0; l < nlHeaderChargesLength; l++) {
							Element eleHeaderCharge = (Element) nlHeaderCharges
									.item(l);

							eleHeaderCharge.setAttribute(
									VSIConstants.ATTR_CHARGE_AMOUNT,
									String.valueOf(0.00));
						}
					}
				}

				return docInXML;

			}
			return docATGWebServiceResponse;

		}
		if(log.isDebugEnabled()){
			log.info("***Updated UE Docuement" + SCXmlUtil.getString(docInXML));
		}
		return docInXML;

	}

	/**
	 * Added for ARS-230 to stamp ExtnType="MXMH" if line charge is BOGO
	 * 
	 * @param eleLineCharge
	 */
	private void stampExtnTypeForBOGO(Element eleLineCharge) {
		if (eleLineCharge.getAttribute(ATTR_CHARGE_NAME).contains(BOGO)) {
			Element eleLineChargeExtn = SCXmlUtil.getChildElement(
					eleLineCharge, ELE_EXTN, true);
			eleLineChargeExtn.setAttribute(ATTR_EXTN_DISC_TYPE, "MXMH");
			eleLineChargeExtn.setAttribute(ATTR_EXTN_TYPE, "MXMH");
		}
	}

	private void prorateLineCharges(Element eleLineCharge,
			double attrOrderedQty, double attrChargePerLine,
			double attrChargePerUnit) {
		if ((YFCCommon.isDoubleVoid(attrChargePerUnit) || !(attrChargePerUnit > 0))
				&& attrChargePerLine > 0.00) {
			DecimalFormat dfRMDown = new DecimalFormat(DEC_FORMAT);
			dfRMDown.setRoundingMode(RoundingMode.DOWN);

			DecimalFormat dfRMUp = new DecimalFormat(DEC_FORMAT);

			double costperunit = attrChargePerLine / attrOrderedQty;
			String strCostperunit = dfRMDown.format(costperunit);

			eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_UNIT,
					strCostperunit);

			Double dbChargePerLine = attrChargePerLine
					- ((Double.valueOf(strCostperunit)) * attrOrderedQty);

			if (dbChargePerLine > 0) {
				eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,
						dfRMUp.format(dbChargePerLine));
			} else {
				eleLineCharge.setAttribute(VSIConstants.ATTR_CHARGE_PER_LINE,
						VSIConstants.ZERO_DOUBLE);
			}
		}
	}

	private void prorateLineTaxes(Element eleOrderLine, Boolean isNewOrder) {

		DecimalFormat df = new DecimalFormat(DEC_FORMAT);
		df.setRoundingMode(RoundingMode.DOWN);
		DecimalFormat dfRMUp = new DecimalFormat(DEC_FORMAT);

		Double dblOrderedQty = SCXmlUtil.getDoubleAttribute(eleOrderLine,
				ATTR_ORD_QTY);
		NodeList nlLineTax = eleOrderLine.getElementsByTagName(ELE_LINE_TAX);
		for (int i = 0; i < nlLineTax.getLength(); i++) {

			Element eleLineTax = (Element) nlLineTax.item(i);
			Element eleLineTaxExtn = null;
			String strExtnTaxPerUnit = "";
			Double dblTax = 0.0;
			Double dblTaxPerUnit = 0.0;
			String strChargeName = eleLineTax.getAttribute(ATTR_CHARGE_NAME);

			// For new orders, set the ExtnTaxPerUnit if not already set
			if (isNewOrder) {

				eleLineTaxExtn = SCXmlUtil.getChildElement(eleLineTax,
						ELE_EXTN, true);
				strExtnTaxPerUnit = SCXmlUtil.getAttribute(eleLineTaxExtn,
						ATTR_EXTN_LINE_TAX_PER_UNIT);
				if (YFCObject.isVoid(strExtnTaxPerUnit)) {

					dblTax = SCXmlUtil.getDoubleAttribute(eleLineTax, ATTR_TAX);
					if (dblOrderedQty > 0) {

						dblTaxPerUnit = dblTax / dblOrderedQty;
						SCXmlUtil.setAttribute(eleLineTaxExtn,
								ATTR_EXTN_LINE_TAX_PER_UNIT,
								df.format(dblTaxPerUnit));

						Double dbTAxPerLine = dblTax
								- ((Double.valueOf(df.format(dblTaxPerUnit))) * dblOrderedQty);

						if (dbTAxPerLine > 0) {
							SCXmlUtil.setAttribute(eleLineTaxExtn,
									"ExtnRemTaxPerLine",
									dfRMUp.format(dbTAxPerLine));
						} else {
							SCXmlUtil.setAttribute(eleLineTaxExtn,
									"ExtnRemTaxPerLine",
									VSIConstants.ZERO_DOUBLE);

						}

					} else {

						SCXmlUtil.setAttribute(eleLineTaxExtn,
								ATTR_EXTN_LINE_TAX_PER_UNIT, ZERO_DOUBLE);
					}
				}
			} else { // Not a new order

				if (!YFCObject.isNull(strChargeName)
						&& !"Tax issue".equals(strChargeName)) {
					if (dblOrderedQty > 0) { // If orderedQty is (+) ve,
												// calculate
						// and set tax amount

						eleLineTaxExtn = SCXmlUtil.getChildElement(eleLineTax,
								ELE_EXTN, true);
						dblTaxPerUnit = SCXmlUtil.getDoubleAttribute(
								eleLineTaxExtn, ATTR_EXTN_LINE_TAX_PER_UNIT);
						Double dblTaxPerLine = SCXmlUtil.getDoubleAttribute(
								eleLineTaxExtn, "ExtnRemTaxPerLine");
						dblTax = (dblTaxPerUnit * dblOrderedQty)
								+ dblTaxPerLine;
						SCXmlUtil.setAttribute(eleLineTax, ATTR_TAX,
								dfRMUp.format(dblTax));
					} else { // If OrderedQty = 0, set tax = 0

						SCXmlUtil.setAttribute(eleLineTax, ATTR_TAX,
								ZERO_DOUBLE);
					}
				}
			}
		}
	}

	private Double getShippingTaxRate(Element eleHeaderCharges,
			Element eleHeaderTaxes) {

		Double dblShippingTax = 0.0;
		if (!YFCObject.isVoid(eleHeaderTaxes) && eleHeaderTaxes.hasChildNodes()) { // Input
																					// has
																					// tax
																					// records

			NodeList nlHeaderTax = eleHeaderTaxes
					.getElementsByTagName(ELE_HEADER_TAX);

			for (int i = 0; i < nlHeaderTax.getLength(); i++) {

				Element eleHeaderTax = (Element) nlHeaderTax.item(i);
				Double dblTaxPercentage = SCXmlUtil.getDoubleAttribute(
						eleHeaderTax, "TaxPercentage");
				if (dblTaxPercentage > 0.00) {

					return dblTaxPercentage;
				}

				dblShippingTax = dblShippingTax
						+ SCXmlUtil.getDoubleAttribute(eleHeaderTax, ATTR_TAX);
			}
		}

		Double dblTotalShippingCharge = getTotalShippingCharge(eleHeaderCharges);

		if (dblShippingTax > 0.0 && dblTotalShippingCharge > 0.00) {

			DecimalFormat df = new DecimalFormat(".##");
			df.setRoundingMode(RoundingMode.DOWN);
			Double dblTaxRate = (dblShippingTax * 100) / dblTotalShippingCharge;
			String strTaxRate = df.format(dblTaxRate);
			return Double.valueOf(strTaxRate);
		}

		return 0.0;
	}

	private Double getTotalShippingCharge(Element eleHeaderCharges) {

		if (!YFCObject.isVoid(eleHeaderCharges)) {
			NodeList nlHeaderCharge = eleHeaderCharges
					.getElementsByTagName(ELE_HEADER_CHARGE);
			Double dblShippingCharge = 0.0;
			Double dblShippingDiscount = 0.0;
			for (int j = 0; j < nlHeaderCharge.getLength(); j++) {

				Element eleHeaderCharge = (Element) nlHeaderCharge.item(j);
				Double dblChargeAmount = SCXmlUtil.getDoubleAttribute(
						eleHeaderCharge, ATTR_CHARGE_AMOUNT);
				String isDiscount = SCXmlUtil.getAttribute(eleHeaderCharge,
						ATTR_IS_DISCOUNT);

				if (isDiscount.equalsIgnoreCase(FLAG_Y)
						|| SCXmlUtil.getAttribute(eleHeaderCharge,
								ATTR_CHARGE_NAME).contains(DISCOUNT)) {

					dblShippingDiscount = dblShippingDiscount + dblChargeAmount;
				} else {

					dblShippingCharge = dblShippingCharge + dblChargeAmount;
				}
			}

			return dblShippingCharge - dblShippingDiscount;
		}

		return 0.0;
	}

	/**
	 * ARS-297 : Loops through the lines of the input document to form the ATG
	 * pricing service input without any free lines
	 * 
	 * @param docInput
	 * @param mapFreeItems
	 * @return
	 */
	private Document formATGPricingInput(Document docInput,
			HashMap<String, Element> mapFreeItems) {
		Document docOutput = SCXmlUtil.createDocument();
		Element eleOrderIn = docInput.getDocumentElement();
		Element eleOrderOut = SCXmlUtil.importElement(docOutput, eleOrderIn);

		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrderOut,
				ELE_ORDER_LINES);
		ArrayList<Element> alOrderLineList = SCXmlUtil.getChildren(
				eleOrderLines, ELE_ORDER_LINE);
		boolean bGetLineType = true;
		for (Element eleOrderLine : alOrderLineList) {
			Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN,
					true);
			if (FLAG_Y.equals(eleExtn.getAttribute(ATTR_EXTN_IS_GWP))) {
				Element eleItem = SCXmlUtil.getChildElement(eleOrderLine,
						ELE_ITEM);
				mapFreeItems.put(eleItem.getAttribute(ATTR_ITEM_ID),
						eleOrderLine);
				eleOrderLines.removeChild(eleOrderLine);
			} else {
				if (bGetLineType) {
					String strLineType = eleOrderLine
							.getAttribute(ATTR_LINE_TYPE);
					String strDeliveryMethod = eleOrderLine
							.getAttribute(ATTR_DELIVERY_METHOD);
					if (!YFCObject.isVoid(strLineType)
							&& !YFCObject.isVoid(strDeliveryMethod)) {
						eleOrderOut.setAttribute(ATTR_LINE_TYPE, strLineType);
						eleOrderOut.setAttribute(ATTR_DELIVERY_METHOD,
								strDeliveryMethod);
						bGetLineType = false;
					}
				}
			}
		}

		return docOutput;
	}

	/**
	 * ARS-297 : Checks for any free lines in the ATG pricing service output and
	 * adds or updates the quantity on the free lines on the order
	 * 
	 * @param yfsEnv
	 * @param mapFreeItems
	 * @param strOrderHeaderKey
	 * @param strDeliveryMethod
	 * @param strLineType
	 * @param eleHoldOLK
	 * @param webServiceOutput
	 */
	private void processATGPricingOutput(YFSEnvironment yfsEnv,
			Element eleOrderLine, HashMap<String, Element> mapFreeItems,
			String strOrderHeaderKey, String strLineType,
			String strDeliveryMethod) {

		Element eleExtn = SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);

		if (FLAG_Y.equals(eleExtn.getAttribute(ATTR_EXTN_IS_GWP))) {
			String strItemID = (eleOrderLine.getAttribute(ATTR_ITEM_ID));
			if (mapFreeItems.containsKey(strItemID)) {
				String strQty = mapFreeItems.get(strItemID).getAttribute(
						ATTR_ORD_QTY);
				String strOrdQty = eleOrderLine.getAttribute(ATTR_ORD_QTY);
				if (!strOrdQty.equals(strQty)) {
					Document docChangeOrder = SCXmlUtil
							.createDocument(ELE_ORDER);
					Element eleChangeOrder = docChangeOrder
							.getDocumentElement();
					eleChangeOrder.setAttribute(ATTR_ORDER_HEADER_KEY,
							strOrderHeaderKey);
					Element eleChangeOrderLines = SCXmlUtil.createChild(
							eleChangeOrder, ELE_ORDER_LINES);
					Element eleFreeOrderLine = mapFreeItems.get(strItemID);
					Element eleNewFreeLine = SCXmlUtil.createChild(
							eleChangeOrderLines, ELE_ORDER_LINE);
					eleNewFreeLine.setAttribute(ATTR_ORDER_LINE_KEY,
							eleFreeOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
					eleNewFreeLine
							.setAttribute(ATTR_ACTION, ACTION_CAPS_MODIFY);
					eleNewFreeLine.setAttribute(ATTR_ORD_QTY, strOrdQty);
					/*
					 * try { // TODO: find out why changeOrder throws an
					 * exception here VSIUtils.invokeAPI(yfsEnv,
					 * TEMPLATE_ORDER_ORDER_HEADER_KEY, API_CHANGE_ORDER,
					 * docChangeOrder); } catch (YFSException e) { log.debug(
					 * "YFSException in VSIOrderRepricingUEImpl.processATGPricingOutput() while calling changeOrder with input: "
					 * + SCXmlUtil.getString(docChangeOrder), e); //throw
					 * VSIUtils.getYFSException(e); } catch (RemoteException e)
					 * { log.debug(
					 * "RemoteException in VSIOrderRepricingUEImpl.processATGPricingOutput() while calling changeOrder with input: "
					 * + SCXmlUtil.getString(docChangeOrder), e); //throw
					 * VSIUtils.getYFSException(e); } catch
					 * (YIFClientCreationException e) { log.debug(
					 * "YIFClientCreationException in VSIOrderRepricingUEImpl.processATGPricingOutput() while calling changeOrder with input: "
					 * + SCXmlUtil.getString(docChangeOrder), e); //throw
					 * VSIUtils.getYFSException(e); } catch (Exception e) {
					 * log.debug(
					 * "Exception in VSIOrderRepricingUEImpl.processATGPricingOutput() while calling changeOrder with input: "
					 * + SCXmlUtil.getString(docChangeOrder), e); //throw
					 * VSIUtils.getYFSException(e); }
					 */
				}
				mapFreeItems.remove(strItemID);
			} else {
				addLinetoOrder(yfsEnv, eleOrderLine, strOrderHeaderKey,
						strLineType, strDeliveryMethod);
			}
		}
	}

	/**
	 * ARS-297 : Adds free line to the order using addLineToOrder api
	 * 
	 * @param yfsEnv
	 * @param eleOrderLine
	 * @param strOrderHeaderKey
	 * @param strLineType
	 * @param strDeliveryMethod
	 */
	private void addLinetoOrder(YFSEnvironment yfsEnv, Element eleOrderLine,
			String strOrderHeaderKey, String strLineType,
			String strDeliveryMethod) {
		Document docOrderLine = SCXmlUtil.createDocument();
		// Element eleOrderLineN = docOrderLine.getDocumentElement();
		Element eleFreeLine = SCXmlUtil.importElement(docOrderLine,
				eleOrderLine);
		eleFreeLine.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		eleFreeLine.setAttribute(ATTR_LINE_TYPE, strLineType);
		eleFreeLine.setAttribute(ATTR_DELIVERY_METHOD, strDeliveryMethod);
		eleFreeLine.setAttribute("TransactionalLineId", "99");
		eleFreeLine.setAttribute("BypassPricing", FLAG_Y);
		// eleFreeLine.setAttribute(ATTR_ITEM_ID,
		// eleOrderLine.getAttribute(ATTR_ITEM_ID));
		// eleFreeLine.setAttribute(ATTR_ACTION, ACTION_CAPS_CREATE);
		Element eleItem = SCXmlUtil.createChild(eleFreeLine, ELE_ITEM);
		eleItem.setAttribute(ATTR_ITEM_ID,
				eleOrderLine.getAttribute(ATTR_ITEM_ID));
		eleItem.setAttribute(ATTR_UOM, UOM_EACH);
		eleItem.setAttribute(ATTR_PRODUCT_CLASS, GOOD);

		// Call addLineToOrder
		try {
			VSIUtils.invokeAPI(yfsEnv, TEMPLATE_ORDER_LINE_ORDER_LINE_KEY,
					API_ADD_LINE_TO_ORDER, docOrderLine);
		} catch (YFSException e) {
			log.error(
					"YFSException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(eleOrderLine), e);
			// throw VSIUtils.getYFSException(e);
		} catch (RemoteException e) {
			log.error(
					"RemoteException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(eleOrderLine), e);
			// throw VSIUtils.getYFSException(e);
		} catch (YIFClientCreationException e) {
			log.error(
					"YIFClientCreationException in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(eleOrderLine), e);
			// throw VSIUtils.getYFSException(e);
		} catch (Exception e) {
			log.error(
					"Exception in VSIOrderRepricingUEImpl.addLinetoOrder() while calling addLineToOrder with input: "
							+ SCXmlUtil.getString(eleOrderLine), e);
			// throw VSIUtils.getYFSException(e);
		}
	}
	/*OMS-1579 : Item level and Order level coupon codes are not displayed in the order line summary for Call Center orders
	 * 
	 * Appending CouponCodes in OrderLine/LineCharges/LineCharge/@CouponNum
	 * 
	 * 
	 */
	
	private void appendCouponNumber(Document docOutATG)
	{
		if(log.isDebugEnabled()){
			log.debug("docOutATG"+XMLUtil.getXMLString(docOutATG));
		}
			Element eleOrder = docOutATG.getDocumentElement();
			NodeList nlOrderLines = eleOrder
					.getElementsByTagName("OrderLines");
			for (int i = 0; i < nlOrderLines.getLength(); i++) {
				Element eleOrderLine = (Element) nlOrderLines.item(i);
				NodeList nlLineCharges = eleOrderLine
						.getElementsByTagName("LineCharge");
				if (nlLineCharges.getLength() > 0) {
					for (int j = 0; j < nlLineCharges.getLength(); j++) {
						Element eleCharge = (Element) nlLineCharges.item(j);
						Element eleExtnCharge = (Element) eleCharge
								.getElementsByTagName("Extn")
								.item(0);
						String strPromoNum = eleExtnCharge
								.getAttribute("PromoNum");
						if(!YFCCommon.isVoid(strPromoNum))
						{	
							if(log.isDebugEnabled()){
								log.debug("PromoNum"+strPromoNum);
							}
							String atgXMLPromotionXPath = "/Order/Promotions/Promotion[@PromotionId="
								+ strPromoNum + "]";
							if(log.isDebugEnabled()){
								log.debug("PromoNum XPATH"+atgXMLPromotionXPath);
							}
						if (!YFCCommon.isVoid(SCXmlUtil.getXpathElement(
								eleOrder, atgXMLPromotionXPath))) {
							if(log.isDebugEnabled()){		
								log.debug("OrderLevel promotion");
							}
							Element eleATGOrderPromotion = SCXmlUtil.getXpathElement(
									eleOrder, atgXMLPromotionXPath);
							if(!YFCCommon.isVoid(eleATGOrderPromotion))
							{
							Element elePromotionExtn=(Element) eleATGOrderPromotion.getElementsByTagName("Extn").item(0);
							String strExtnCouponID=elePromotionExtn.getAttribute("ExtnCouponID");
							if(log.isDebugEnabled()){
								log.debug("strExtnCouponID"+strExtnCouponID);
							}
							eleExtnCharge.setAttribute("CouponNumber", strExtnCouponID);
							}
						}
						else
						{
							if(log.isDebugEnabled()){
								log.debug("Item Level Promotion");
							}
							String atgXMLLinePromotionXPath = "OrderLine/Promotions/Promotion[@PromotionId="
									+ strPromoNum + "]";	
							if(log.isDebugEnabled()){
								log.debug("atgXMLLinePromotionXPath"+atgXMLLinePromotionXPath);
							}
							if (!YFCCommon.isVoid(SCXmlUtil.getXpathElement(
									eleOrderLine, atgXMLLinePromotionXPath))) {
								
								if(log.isDebugEnabled()){
									log.debug("OrderLineLevel Promotion");
								}
								Element eleATGOrderLinePromotion = SCXmlUtil.getXpathElement(
										eleOrderLine, atgXMLLinePromotionXPath);
								Element elePromotionExtn=(Element) eleATGOrderLinePromotion.getElementsByTagName("Extn").item(0);
								String strExtnCouponID=elePromotionExtn.getAttribute("ExtnCouponID");
								if(log.isDebugEnabled()){
									log.debug("strExtnCouponID"+strExtnCouponID);
								}
								eleExtnCharge.setAttribute("CouponNumber", strExtnCouponID);
							
							
						}
						}
					}
				}
			}
	}
			
			if(log.isDebugEnabled()){
				log.debug("Updated DOCOUTPUT"+XMLUtil.getXMLString(docOutATG));
			}
	}
	/**
	 * ARS-297 : Removes free line from order using changeOrder api
	 * 
	 * @param env
	 * @param eleOrderLines
	 * @param mapFreeItems
	 * @param strOrderHeaderKey
	 */
	private void deleteLinesFromOrder(YFSEnvironment env,
			Element eleOrderLines, HashMap<String, Element> mapFreeItems,
			String strOrderHeaderKey) {
		Document docCnclOrder = SCXmlUtil.createDocument(ELE_ORDER);
		Element eleCnclOrder = docCnclOrder.getDocumentElement();
		eleCnclOrder.setAttribute("BypassPricing", FLAG_Y);
		// eleCnclOrder.setAttribute(ATTR_OVERRIDE, FLAG_Y);
		eleCnclOrder.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
		Element eleCnclOrderLines = SCXmlUtil.createChild(eleCnclOrder,
				ELE_ORDER_LINES);
		Collection<Element> colFreeOrderLineList = (Collection<Element>) mapFreeItems
				.values();
		Iterator<Element> interFreeOrderLineList = colFreeOrderLineList
				.iterator();
		while (interFreeOrderLineList.hasNext()) {
			Element eleFreeOrderLine = interFreeOrderLineList.next();
			if (!INT_ZER0_NUM.equals(eleFreeOrderLine
					.getAttribute(ATTR_ORD_QTY))) {
				// Element eleCnclOrderLine =
				// SCXmlUtil.createChild(eleCnclOrderLines, ELE_ORDER_LINE);
				Element eleCnclOrderLine = SCXmlUtil.createChild(
						eleCnclOrderLines, ELE_ORDER_LINE);
				eleCnclOrderLine.setAttribute(ATTR_ORDER_LINE_KEY,
						eleFreeOrderLine.getAttribute(ATTR_ORDER_LINE_KEY));
				eleCnclOrderLine.setAttribute(ATTR_ACTION, REMOVE);
				// eleCnclOrderLine.setAttribute(ATTR_ORD_QTY, "0");
			}
		}
		if (eleCnclOrderLines.hasChildNodes()) {
			/*
			 * try { env.setTxnObject("BYPASS_PRICING", FLAG_Y); // TODO: find
			 * out why changeOrder is throwing an exception here
			 * VSIUtils.invokeAPI(env, TEMPLATE_ORDER_ORDER_HEADER_KEY,
			 * API_CHANGE_ORDER, docCnclOrder); } catch (YFSException e) {
			 * log.debug(
			 * "YFSException in VSIOrderRepricingUEImpl.deleteLinesFromOrder() while calling cancelOrder with input: "
			 * + SCXmlUtil.getString(docCnclOrder), e); //throw
			 * VSIUtils.getYFSException(e); } catch (RemoteException e) {
			 * log.debug(
			 * "RemoteException in VSIOrderRepricingUEImpl.deleteLinesFromOrder() while calling cancelOrder with input: "
			 * + SCXmlUtil.getString(docCnclOrder), e); //throw
			 * VSIUtils.getYFSException(e); } catch (YIFClientCreationException
			 * e) { log.debug(
			 * "YIFClientCreationException in VSIOrderRepricingUEImpl.deleteLinesFromOrder() while calling cancelOrder with input: "
			 * + SCXmlUtil.getString(docCnclOrder), e); //throw
			 * VSIUtils.getYFSException(e); } catch (Exception e) { log.debug(
			 * "Exception in VSIOrderRepricingUEImpl.deleteLinesFromOrder() while calling cancelOrder with input: "
			 * + SCXmlUtil.getString(docCnclOrder), e); //throw
			 * VSIUtils.getYFSException(e); }
			 */
		}
	}
}