package com.vsi.oms.userexit;

import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.api.order.VSITobTaxJurisdiction;
import com.vsi.oms.api.web.VSIGetPTandLPTdetails;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfc.util.YFCDoubleUtils;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSBeforeCreateOrderUE;

/**
 * @author Perficient, Inc.
 * 
 *         VSI Before Create Order UE for generating CustomerPONo and stamping
 *         the EMailID in PersonInfoShipTo which is acquired after calling
 *         getOrganizationList using the shipNode.
 * 
 *         Logic for CustomerPoNo 5-digit Store Number ShipNode --->add leading
 *         zeros depending on shipNodelength to total 5 
 *         4-digit Transaction Date
 *         (YY MM/ Order Create Date)--->OrderDate YYMM 2-digit Register Number---->88 hard code 
 *         5-digit Transaction Number -----> database generator 
 *         2-digit Send Group Number ------> constant 01 
 *         2-digit Line Item Status Number ------> constant 00
 * 
 *         Checking no.of lines and line types. If there is more than one line
 *         and line types are different then multiple ES/POS order No.s need to
 *         be generated. ex: if there is one PICK_IN_STORE and one SHIP_TO_STORE
 *         lines then two order numbers are generated and stored in the
 *         respective CustomerPONo's.
 * 
 *         Generating transaction number as well and store in CustCustPoNo.
 *
 */
public class VSIBeforeCreateOrderUEImpl implements YFSBeforeCreateOrderUE,VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIBeforeCreateOrderUEImpl.class);
	YIFApi api;

	public VSIBeforeCreateOrderUEImpl() {

	}

	public Document beforeCreateOrder(YFSEnvironment env, Document inXML)
			throws YFSException {

		log.info("Input XML in BCOUE : "+ XMLUtil.getXMLString(inXML));
		//int iShip = 0;
		//int iPick = 0;
		boolean isSTH = false;
		Element orderElement = inXML.getDocumentElement();
		//OMS-1767 : Defaulting SCAC value to UPSN if its null : START
		if(orderElement.hasAttribute(ATTR_SCAC))
		{
			String strSCAC = SCXmlUtil.getAttribute(orderElement, ATTR_SCAC);
			if(YFCCommon.isVoid(strSCAC))
			{
				orderElement.setAttribute(ATTR_SCAC, SCAC_UPSN);
			}	
		}
		else
			orderElement.setAttribute(ATTR_SCAC, SCAC_UPSN);	
		//OMS-1767 : END
		Element eleExtn = SCXmlUtil.getChildElement(orderElement, ELE_EXTN);
		//OMS-1350: Start//
		Element eleOrderLine1 = (Element) inXML.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		String strLineType1=null;
		if (null != eleOrderLine1) {
		strLineType1 = eleOrderLine1
					.getAttribute(VSIConstants.ATTR_LINE_TYPE);
		}
			
		if(!YFCObject.isVoid(eleExtn))
		{
			String strIsSubscriptionOrder = SCXmlUtil.getAttribute(eleExtn, "ExtnSubscriptionOrder");
			if(strIsSubscriptionOrder.equalsIgnoreCase("Y") && !YFCObject.isVoid(strLineType1) && VSIConstants.LINETYPE_STH.equals(strLineType1)){
				
				orderElement.setAttribute(ATTR_ENTERPRISE_CODE, ENT_ADP);
				orderElement.setAttribute(ATTR_SELLER_ORG_CODE, ENT_ADP);
			}
		}
		//OMS-1350: End//
		String enterpriseCode = orderElement.getAttribute(ATTR_ENTERPRISE_CODE);
		String strEntryType = orderElement.getAttribute(ATTR_ENTRY_TYPE);		
		String strDocumentType = orderElement.getAttribute(ATTR_DOCUMENT_TYPE);
		//IBM 1B web order capture coding starts here
		String sEnterpriseCode = orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
		//OMS-1981: Start
		String strBillToID=orderElement.getAttribute(ATTR_BILL_TO_ID);
		if (YFCCommon.isVoid(strBillToID) && ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType) && !YFCObject.isVoid(strLineType1) && LINETYPE_STH.equals(strLineType1) && (VSICOM_ENTERPRISE_CODE.equalsIgnoreCase(sEnterpriseCode) || ENT_ADP.equalsIgnoreCase(sEnterpriseCode)))
					throw new YFSException(
							"Customer BillToID is missing",
							"Customer BillToID is missing",
							"Customer BillToID is missing");
				
		//OMS-1981: End
		//Commenting as dummy email id is getting declined in Riskify system
		//OMS-1992: Start
		/*String strEmailID=orderElement.getAttribute(ATTR_CUSTOMER_EMAIL_ID);
		String strRegex= "^(.+)@(.+)$";
		String strOrderType = orderElement.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
		if ((!YFCCommon.isStringVoid(strOrderType)&&!(VSIConstants.ATTR_ORDER_TYPE_POS.equalsIgnoreCase(strOrderType)||VSIConstants.MARKETPLACE.equalsIgnoreCase(strOrderType)))
				&&(YFCCommon.isVoid(strEmailID)||!strEmailID.matches(strRegex)) && ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType) 
				&& !YFCObject.isVoid(strLineType1) && (LINETYPE_STH.equals(strLineType1)||LINETYPE_STS.equals(strLineType1)||LINETYPE_PUS.equals(strLineType1))
				&& (VSICOM_ENTERPRISE_CODE.equalsIgnoreCase(sEnterpriseCode) || ENT_ADP.equalsIgnoreCase(sEnterpriseCode)))
			throw new YFSException(
					"Invalid CustomerEmailId",
					"Invalid CustomerEmailId",
					"Invalid CustomerEmailId");*/

		//OMS-1992: End
		
		//OMS-1033 PennyDiscount : Start
		if("0001".equals(strDocumentType)&& ("VSI.com".equalsIgnoreCase(sEnterpriseCode) || sEnterpriseCode.equals(ENT_ADP))){
			applyPennyDiscount(env,inXML);
			if(log.isDebugEnabled()){
				log.debug("Input XML after applyPennyDiscount : "+ XMLUtil.getXMLString(inXML));
			}
		}
		//OMS-1033 -PennyDiscount :End
		
		if(!"VSI.com".equalsIgnoreCase(sEnterpriseCode) && !sEnterpriseCode.equals(ENT_ADP))
		{
			orderElement.setAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID, "NOPAYMENT");
		}
		if("0003".equals(strDocumentType))
		{
			return inXML;
		}
		
		String	poBoxPatternString = ".*[Pp][ ]*[.]?[ ]*[Oo][ ]*[-.]?[ ]*([Bb][Oo][Xx])+.*";
		
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + XmlUtils.getString(inXML));
			log.info("================Inside beforeCreateOrderUE================================");
		}
		try {
			//OMS-1590:Start
			Set<String> setKitLineItems=null;
			boolean isKitEligible=false;
			//OMS-2418 -- Start
			boolean isCallCenterAurus = false;
			//OMS-2418 -- End
			if ("VSI.com".equals(enterpriseCode) || "ADP".equals(enterpriseCode))
			{
				setKitLineItems=fetchKitLineItemList(env);
				isKitEligible=true;
			}
			//OMS-1590:End
			if(VSIConstants.ENTRYTYPE_CC.equals(strEntryType))
			{
				String virtualStoreNo = getVirtualStore(env, strEntryType,VSIConstants.VIRTUAL_STORE_COM_CODE);
				orderElement.setAttribute(
						VSIConstants.ATTR_ENTERED_BY, virtualStoreNo);
				orderElement.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "WEB");
				//Aurus Start
				//OMS-2418 -- Start
				isCallCenterAurus = isCallCenterAurus(env);
				if(isCallCenterAurus){
					Element eleExtn1 = SCXmlUtil.createChild(orderElement, "Extn");
					eleExtn1.setAttribute("ExtnAurusToken", "Y");
				}	
				//OMS-2418 -- End
				//Aurus End

				return inXML;
			}
			
			
			

			if (null != inXML.getDocumentElement()
					&& null != inXML
					.getElementsByTagName(VSIConstants.ELE_ORDER_LINE)) {
				Element rootElement = inXML.getDocumentElement();
				Element eleCreateOrderExtn = SCXmlUtil.getChildElement(rootElement, "Extn");
				String orderType = rootElement
						.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				String entryType = rootElement
						.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);

				NodeList orderLineList = inXML
						.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				int orderLineLength = orderLineList.getLength();
				
				//if("WEB".equals(strEntryType))
				//{
					//update GC paymentmethod
					updateGCPaymentMethods(env,inXML, orderLineList,strEntryType);
				//}
				//OMS-1767 :Check  CarrierServiceCode value in OrderLine & find the value to stamp for blank CarrierServiceCode : START
					HashSet<String> setCarrierServiceCode = new HashSet<String>();
					String strCarrierServiceCode=null;
					if (orderLineList != null && orderLineLength > 0) {
						for (int i = 0; i < orderLineLength; i++) {
							Element eleOrderLine = (Element) orderLineList.item(i);
							if (eleOrderLine.hasAttribute(ATTR_CARRIER_SERVICE_CODE))
							strCarrierServiceCode=eleOrderLine.getAttribute(ATTR_CARRIER_SERVICE_CODE);
							if(!YFCCommon.isVoid(strCarrierServiceCode)) 
							{
								setCarrierServiceCode.add(strCarrierServiceCode);
							}
						}
					}
					String strCarrierServiceCodeVal="STANDARD";
					if(!setCarrierServiceCode.isEmpty() && setCarrierServiceCode.contains("NEXTDAY"))
					{
						strCarrierServiceCodeVal="NEXTDAY";
					}
					else if (!setCarrierServiceCode.isEmpty() && setCarrierServiceCode.contains("TWODAY"))
					{
						strCarrierServiceCodeVal="TWODAY";
					}
				//OMS-1767 : Check  CarrierServiceCode value in OrderLine & find the value to stamp for blank CarrierServiceCode: END
				// jira 710 for making ship to store line noinv so that ther
				// else no inv check
				if (orderLineList != null && orderLineLength > 0) {
					for (int i = 0; i < orderLineLength; i++) {
						Element eleOrderLine = (Element) orderLineList.item(i);
						
						String strLineType = eleOrderLine
								.getAttribute("LineType");
						String strCServiceCode=eleOrderLine.getAttribute(ATTR_CARRIER_SERVICE_CODE);
						String strDeliveryMethod = eleOrderLine.getAttribute(ATTR_DELIVERY_METHOD);
						//OMS-1767 : Check  CarrierServiceCode value in OrderLine & find the value to stamp for blank CarrierServiceCode: END
						if(YFCCommon.isVoid(strCServiceCode) 
								&& (!YFCCommon.isVoid(strDeliveryMethod) && !ATTR_DEL_METHOD_PICK.equals(strDeliveryMethod)))
						{
							eleOrderLine.setAttribute(ATTR_CARRIER_SERVICE_CODE, strCarrierServiceCodeVal);
						}
						//OMS-1767 : Check  CarrierServiceCode value in OrderLine & find the value to stamp for blank CarrierServiceCode: END
						//OMS-1821: Start
						if(!YFCCommon.isVoid(strDeliveryMethod) && ATTR_DEL_METHOD_PICK.equals(strDeliveryMethod) && "0001".equals(strDocumentType))
						{
							eleOrderLine.setAttribute(ATTR_CARRIER_SERVICE_CODE,"");
						}
						//OMS-1821: End
						Element eleOLPromotions = SCXmlUtil.getChildElement(eleOrderLine, ELE_PROMOTIONS);
						if(!YFCCommon.isVoid(eleOLPromotions)){
							eleOLPromotions.setAttribute(ATTR_RESET, FLAG_Y);
							ArrayList<Element> arrPromotions = SCXmlUtil.getChildren(eleOLPromotions, ELE_PROMOTION);
							for(Element elePromotion:arrPromotions){
								Element elePromotionExtn = SCXmlUtil.getChildElement(elePromotion, ELE_EXTN);
								String strCouponId = elePromotionExtn.getAttribute("ExtnCouponID");
								if(!YFCCommon.isVoid(strCouponId)){
									String strPromotionId = elePromotion.getAttribute(ATTR_PROMOTION_ID);
									//System.out.println("PromotionId is :"+strPromotionId);
									String atgXMLPromotionXPath = "/Order/Promotions/Promotion[@PromotionId="+strPromotionId+"]";
									if(YFCCommon.isVoid(SCXmlUtil.getXpathElement(rootElement,atgXMLPromotionXPath))){
										Element eleATGOrderPromotions = SCXmlUtil.getChildElement(rootElement, ELE_PROMOTIONS);
										if(YFCCommon.isVoid(eleATGOrderPromotions)){
											eleATGOrderPromotions= SCXmlUtil.createChild(rootElement, ELE_PROMOTIONS);
										}
										SCXmlUtil.importElement(eleATGOrderPromotions, elePromotion);
										SCXmlUtil.removeNode(elePromotion);
									}
								}
							}
							if(!eleOLPromotions.hasChildNodes()){
								SCXmlUtil.removeNode(eleOLPromotions);
							}
						}
						
						if (strLineType.equalsIgnoreCase(LINETYPE_STS) || strLineType.equalsIgnoreCase(LINETYPE_PUS)) 
						{
							ArrayList<Element> somEnabledValue = VSIUtils.getCommonCodeList(env, SOM_ENABLED_COMMON_CODE_TYPE , SOM_ENABLED_COMMON_CODE, ATTR_DEFAULT);
							if(!somEnabledValue.isEmpty())
							{
								Element eleCommonCode=somEnabledValue.get(0);
								String SOMEnabledCodeValue=eleCommonCode.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
								log.info("B4CreateOrderUE - SOMEnabledCodeValue => " +SOMEnabledCodeValue);
    						    if(SOMEnabledCodeValue.equalsIgnoreCase(SOM_ENABLED_COMPLETE))
    						    	eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_Y);
    						     else if(SOMEnabledCodeValue.equalsIgnoreCase(SOM_ENABLED_PARTIAL))
    						    {
    						    	ArrayList<Element> storeIds = VSIUtils.getCommonCodeList(env, SOM_CUTOVER_COMMON_CODE_TYPE, eleOrderLine.getAttribute(ELE_SHIP_NODE), ATTR_DEFAULT);
    						    	if(!storeIds.isEmpty())
    						    		eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_Y);
    						    	else
    						    		eleOrderLine.setAttribute(ATTR_CONDITION_VARIBALE1,FLAG_N);
    						    }
							}
							log.info("B4CreateOrderUE - ConditionVariable1 => "+eleOrderLine.getAttribute(ATTR_CONDITION_VARIBALE1));
						}					
						
						// Removing code to stamp 9001 as ProcureFromNode for STS lines 
						// ARE-230 making this generic for any STS orders (not just POS)
						if (strLineType.equalsIgnoreCase("SHIP_TO_STORE")) {
							//eleOrderLine.setAttribute("IsProcurementAllowed",
							//	"Y");
						//eleOrderLine
						//.setAttribute("ProcureFromNode", "9001");
						eleOrderLine
						.setAttribute("FulfillmentType", "SHIP_TO_STORE");
						// ARS-82 Setting InventoryCheckCode as NOINV for lines where input LineType is SHIP_TO_STORE
						// This will force Sterling to create a transfer, even if inventory exists at the pickup store
						Element eleOrderLineSourcingControls = SCXmlUtil.createChild(eleOrderLine, "OrderLineSourcingControls");
						Element eleOrderLineSourcingCntrl =  SCXmlUtil.createChild(eleOrderLineSourcingControls, "OrderLineSourcingCntrl");
						eleOrderLineSourcingCntrl.setAttribute("Node", eleOrderLine.getAttribute("ShipNode"));
						eleOrderLineSourcingCntrl.setAttribute("InventoryCheckCode", "NOINV");
						eleOrderLineSourcingCntrl.setAttribute("ReasonText", "SHIP_TO_STORE");						
					}
						
						//GC Line Capture 
						if (!YFCObject.isVoid(strLineType)&&strLineType.equalsIgnoreCase(VSIConstants.LINETYPE_STH) )
						{
						eleOrderLine.setAttribute(VSIConstants.ATTR_DELIVERY_METHOD, VSIConstants.ATTR_DEL_METHOD_SHP);
						eleOrderLine.setAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE,strLineType);
						if(log.isDebugEnabled()){
							log.debug("Inside GC Line Capture");
						}
						Element eleItem = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
						if (eleItem.hasAttributes()){
						String attrItemID = eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID);
						//OMS-1590: Start
						if (null!=setKitLineItems && setKitLineItems.contains(attrItemID) && isKitEligible)
						{
							SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_EXTN).setAttribute("ExtnSDKLine","Y");
						}
						//OMS-1590: End
						Document docgetItemListInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
						SCXmlUtil.setAttribute(docgetItemListInput.getDocumentElement(), VSIConstants.ATTR_ITEM_ID, attrItemID);
						Document docgetItemListOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_ITEM_LIST,VSIConstants.API_GET_ITEM_LIST, docgetItemListInput);
						if (docgetItemListOutput.getDocumentElement().hasChildNodes()){
						String attrExtnItemType = SCXmlUtil.getChildElement(SCXmlUtil.getChildElement(docgetItemListOutput.getDocumentElement(), VSIConstants.ELE_ITEM), VSIConstants.ELE_EXTN).getAttribute(VSIConstants.ATTR_EXTN_ITEM_TYPE);
							if(attrExtnItemType.equalsIgnoreCase(VSIConstants.GIFT_CARD)||attrExtnItemType.equalsIgnoreCase(VSIConstants.GIFT_CARD_VAR)){
								eleOrderLine.setAttribute(VSIConstants.ATTR_SHIP_NODE,VSIConstants.SHIP_NODE_CC);
								eleOrderLine.setAttribute(VSIConstants.ATTR_IS_FIRM_PRE_NODE, VSIConstants.FLAG_Y);
							}
						}
						
						}
						
						if("POS".equalsIgnoreCase(orderType) || orderType.equalsIgnoreCase("Marketplace"))
						{
							if(YFCObject.isVoid(eleCreateOrderExtn))
							{
								eleCreateOrderExtn = SCXmlUtil.createChild(rootElement, ELE_EXTN);							
							}
							eleCreateOrderExtn.setAttribute("ExtnDTCOrder", "Y");
						}
					}//end of GC Line Capture
					}
				}

				// jira 710 end

				
				

				//Checking for marketplace organisation 
				//OMS-1689:Start
				boolean isMarketPlaceOrder=isMarketPlaceOrder(env, enterpriseCode);
				if (isMarketPlaceOrder) {
					
					String virtualStoreNo = getVirtualStore(env, orderElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE),VSIConstants.VIRTUAL_STORE_COM_CODE);
					
					rootElement.setAttribute(
							VSIConstants.ATTR_ENTERED_BY, virtualStoreNo);
					inXML = setLineAttrforMarketplace(env, orderLineList, inXML);
					//OMS-1868 START
					if("0001".equals(strDocumentType)){
						String strSeqName = "VSI_SEQ_MARKETPLACE_BILLTOID";
						if(rootElement.hasAttribute(VSIConstants.ATTR_BILL_TO_ID)&&!YFCObject.isVoid(rootElement.getAttribute(VSIConstants.ATTR_BILL_TO_ID))){
							String strBillTOId = rootElement.getAttribute(VSIConstants.ATTR_BILL_TO_ID);
						rootElement.setAttribute(VSIConstants.ATTR_BILL_TO_ID,strBillTOId);
							}
						
						else{
							
					    	String strSeqNumber = VSIDBUtil.getNextSequence(env, strSeqName);
					    	String strSeqNumPadded =String.format("%1$" + 11 + "s", strSeqNumber).replace(' ', '0');
							String strBillTOID="M"+strSeqNumPadded;
							rootElement.setAttribute(VSIConstants.ATTR_BILL_TO_ID,strBillTOID);
						}
					}
						
					if(log.isDebugEnabled()){
						log.debug("Marketplace Orders" +SCXmlUtil.getString(inXML));
					
				}
				}
				//OMS-1868 END
				//OMS-1689:End
				// 1B Development Marketplace Order create changes - End

				// jira 710 end

				
				
				// JIRA 692
				// Get the Line Type and check if it is SHIP TO HOME
				// If Yes, set isSTH as true
				// No Need to iterate for all the lines as SHIP TO HOME order
				// will not have any other line type.
				Element orderLineEle = (Element) inXML.getElementsByTagName(
						VSIConstants.ELE_ORDER_LINE).item(0);
				if (null != orderLineEle) {
					String sLineType = orderLineEle
							.getAttribute(VSIConstants.ATTR_LINE_TYPE);
					String deliveryMethod = orderLineEle.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
					if ((!YFCObject.isVoid(sLineType) && sLineType.equals(VSIConstants.LINETYPE_STH)) ||
							(!YFCObject.isVoid(deliveryMethod) && deliveryMethod.equals(VSIConstants.ATTR_DEL_METHOD_SHP))) {
						if(log.isDebugEnabled()){
							log.info("Order has SHIP TO HOME Line");
						}
						isSTH = true;
						//Start OMS-1464
						Element elePersonInfoShipTo1 = SCXmlUtil.getChildElement(orderLineEle, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
						String sCountry1 = SCXmlUtil.getAttribute(elePersonInfoShipTo1, VSIConstants.ATTR_COUNTRY);
						if(!sCountry1.equals("US"))
	 						rootElement.setAttribute( VSIConstants.ATTR_ALLOCATION_RULE_ID,"VSI_INT");
	 					else
						rootElement.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, "VSI_STH");
						//End OMS-1464
					}
					
				}
				// End

				if (orderType.equalsIgnoreCase("WEB")
						|| entryType.equalsIgnoreCase("Call Center")) {
					if (null != rootElement) {

						

						/**
						 * if(orderType.equalsIgnoreCase("WEB") &&
						 * entryType.equalsIgnoreCase("WEB")){ for(int i=0;i<
						 * orderLineLength;i++){ Element orderLineElement =
						 * (Element) inXML.getElementsByTagName(VSIConstants.
						 * ELE_ORDER_LINE).item(i); String sLineType =
						 * orderLineElement
						 * .getAttribute(VSIConstants.ATTR_LINE_TYPE);
						 * 
						 * if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
						 * 
						 * iShip++; }else iPick++;
						 * 
						 * } if(iShip==0 || iPick==0){
						 * 
						 * String generatedCustomerPoNo =
						 * generateOrderNo(env,inXML); for(int
						 * j=0;j<orderLineLength;j++){ Element orderLineElement
						 * = (Element) inXML.getElementsByTagName(VSIConstants.
						 * ELE_ORDER_LINE).item(j);
						 * orderLineElement.setAttribute
						 * (VSIConstants.ATTR_CUST_PO_NO,generatedCustomerPoNo);
						 * 
						 * } }
						 * 
						 * else { String genCustomerPoNo1 =
						 * generateOrderNo(env,inXML);
						 * 
						 * 
						 * 
						 * String custPoNo1=genCustomerPoNo1; String custPoNo2 =
						 * genCustomerPoNo1.substring(0,
						 * genCustomerPoNo1.length()-1)+ "1";
						 * 
						 * System.out.println("custPoNo1"+custPoNo1+
						 * "\n custPoNo2"+ custPoNo2);
						 * 
						 * for(int k=0;k<orderLineLength;k++){
						 * 
						 * Element orderLineElement = (Element)
						 * inXML.getElementsByTagName
						 * (VSIConstants.ELE_ORDER_LINE).item(k); String
						 * sLineType =
						 * orderLineElement.getAttribute(VSIConstants
						 * .ATTR_LINE_TYPE);
						 * 
						 * if(sLineType.equalsIgnoreCase("SHIP_TO_STORE")){
						 * 
						 * orderLineElement.setAttribute(VSIConstants.
						 * ATTR_CUST_PO_NO,custPoNo1); } else{
						 * 
						 * orderLineElement.setAttribute(VSIConstants.
						 * ATTR_CUST_PO_NO,custPoNo2);
						 * 
						 * } }//end for
						 * 
						 * } }
						 **/
					}
					//orderElement.setAttribute("BypassPricing", "Y");
				} else {

					String orderNo = orderElement.getAttribute("OrderNo");
					NodeList orderLines = inXML
							.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
					int lineLength = orderLines.getLength();
					for (int i = 0; i < lineLength; i++) {

						Element orderLineElement = (Element) inXML
								.getElementsByTagName(
										VSIConstants.ELE_ORDER_LINE).item(i);
						orderLineElement.setAttribute("CustomerPONo", orderNo);
						//System.out.println("CustomerPONo is " + orderNo);

						Element inItemEle = (Element) orderLineElement
								.getElementsByTagName("Item").item(0);
						String itemId = inItemEle.getAttribute("ItemID");
						String extnTaxProductCode = getItemTaxGroupId(env,
								itemId);
						String insText = "IsVAT=False;TaxAuthNum=1,TaxGroupID="
								+ extnTaxProductCode + ",,,,,";

						Element Instructions = XMLUtil.appendChild(inXML,
								orderLineElement, "Instructions", "");
						Element Instruction = XMLUtil.appendChild(inXML,
								Instructions, "Instruction", "");
						Instruction.setAttribute("InstructionType", "Comments");
						Instruction.setAttribute("InstructionText", insText);

					}

					//orderElement.setAttribute("BypassPricing", "Y");
					
				}
				if (orderType != null && orderType.equalsIgnoreCase("POS")) {

					if(log.isDebugEnabled()){
						log.info("Order was created from POS");
					}
					// JIRA 692
					// If order is created from POS and is a SHIP TO HOME order,
					// apply VSIScheduleHold to stop the order getting scheduled
					// and released.
					// ARS-45 POS Ship to Home orders should not have Schedule Hold applied
					/*if (isSTH) {
						log.info("VSIScheduleHold needs to be applied at order level");
						inXML = applyVSIScheduleHold(inXML);
					}*/

					// the following impl is to stamp the ExtnOrderDate at Order
					// level and ExtnPickDate & ExtnLastPickDate at OrderLine
					// level

					// System.out.println("**inXML :\n"+XMLUtil.getXMLString(inXML));
					inXML = VSIGetPTandLPTdetails
							.stampExtnOrderDate(env, inXML);
					// System.out.println("**After Invoking  stampExtnOrderDate inXML :\n"+XMLUtil.getXMLString(inXML));
					inXML = VSIGetPTandLPTdetails
							.getPTandLPTdetails(env, inXML);
					// System.out.println("**After Invoking  getPTandLPTdetails inXML :\n"+XMLUtil.getXMLString(inXML));
					if(log.isDebugEnabled()){
						log.info("**After Invoking  getPTandLPTdetails and stampExtnOrderDate: inXML :\n"
								+ XMLUtil.getXMLString(inXML));
					}
				}

				//IBM 1B web order capture coding starts here
			/*String sEnterpriseCode = rootElement.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			if(!"VSI.com".equalsIgnoreCase(sEnterpriseCode) && !sEnterpriseCode.equals(ENT_ADP)){
				rootElement.setAttribute(VSIConstants.ATTR_PAYMENT_RULE_ID, "NOPAYMENT");
			}*/

			//orderType
			
			//Element elePersonInfoBillTo = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_PERSON_INFO_BILL_TO);
			
	 		Document docgetCommonCodeOutput = null;
	 		Element orderLinesElement = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_ORDER_LINES);
	 		Element eleOrderLine = SCXmlUtil.getChildElement(orderLinesElement, VSIConstants.ELE_ORDER_LINE);
	 		Element elePersonInfoShipTo = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
	 		if(!YFCObject.isNull(orderLinesElement)){
	 			
	 			if(log.isDebugEnabled()){
	 				log.debug("orderLinesElement has Child Nodes" +SCXmlUtil.getString(orderLinesElement));
	 			}
	 			String sDeliveryMethod = eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD);
	 			
	 			if(sDeliveryMethod.equalsIgnoreCase("SHP")){
	 				
	 				String sCountry = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY);
	 				String sStateIp = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_STATE);
			 		String sOrderDate = SCXmlUtil.getAttribute(rootElement,VSIConstants.ATTR_ORDER_DATE);
			 		String addressLine1 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS1);
			 		String addressLine2 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS2);
			 		if(YFCObject.isVoid(SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_PERSON_INFO_SHIP_TO))){
			 			rootElement.appendChild(inXML.importNode(elePersonInfoShipTo.cloneNode(true), true));
			 		}
	 				Element elePersonInfoShipToHeader = SCXmlUtil.getChildElement(rootElement, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
	 				if(Pattern.matches(poBoxPatternString, addressLine1)
	 						|| Pattern.matches(poBoxPatternString, addressLine2)){
	 					//elePersonInfoShipTo.setAttribute("AddressLine6", "Y");
	 					elePersonInfoShipToHeader.setAttribute("AddressLine6", "Y");
	 				}
	 			boolean flag = false;
	 			//String sEnterpriseCode = SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_ENTERPRISE_CODE);
	 			
	 			// Get the ZoneId and RuleId from //OrderLine/LineTaxes/LineTax/Extn
	 			String strRuleID = "";
				String strZoneID = "";
				Boolean updateZoneAndRuleId = false;
	 			String strZipCode = "";
	 			String strTaxType = "";
	 			String strTaxRateCode = "0";
	 			
	 			Element eleLineTaxes = SCXmlUtil.getChildElement(eleOrderLine, "LineTaxes");
	 			Element eleLineTax = null;
	 			Element eleExtnTax = null;
	 			if(!YFCCommon.isVoid(eleLineTaxes)){
	 				if(eleLineTaxes.hasChildNodes()){
	 					
	 					eleLineTax = SCXmlUtil.getChildElement(eleLineTaxes, "LineTax");
	 					eleExtnTax = SCXmlUtil.getChildElement(eleLineTax, "Extn");
	 					if(!YFCCommon.isVoid(eleExtnTax)){
	 						
	 						strZoneID = eleExtnTax.getAttribute("ZoneId");
	 						strRuleID = eleExtnTax.getAttribute("RuleId");
	 						strTaxType = eleExtnTax.getAttribute("TaxType");
	 						if(!YFCCommon.isVoid(strTaxType)){
	 							
	 							if(strTaxType.equals("Standard")){
	 								strTaxRateCode = "1";
	 							}else if(strTaxType.equals("Alternate")){
	 								strTaxRateCode = "2";
	 							}
	 						}
	 						if("Marketplace".equals(orderType))
	 						{
	 							strTaxRateCode = "1";
	 						}
	 					}
	 				}
	 			}
	 			
	 			// If ZoneId and RuleId are not found on order, get from DB
	 			if(YFCCommon.isVoid(strZoneID) && YFCCommon.isVoid(strRuleID)){

	 				strZipCode = elePersonInfoShipTo.getAttribute("ZipCode");
	 				VSITobTaxJurisdiction tob = new VSITobTaxJurisdiction();
	 				Document tobDetailsXML = tob.fetchTobDetails(
	 						env, "", strTaxRateCode, strZipCode, "SHIP_TO_HOME", api);
	 				if(!YFCCommon.isVoid(tobDetailsXML)){

	 					Element eleTobTaxJurisdictionList = 
	 							tobDetailsXML.getDocumentElement();
	 					if(!YFCCommon.isVoid(eleTobTaxJurisdictionList)){
	 						NodeList nlTobTaxJurisdiction = 
	 								eleTobTaxJurisdictionList.
	 								getElementsByTagName("TobTaxJurisdiction");
	 						if(nlTobTaxJurisdiction.getLength()>0)
	 						{
	 							updateZoneAndRuleId = true;
	 							Element eleTobTaxJurisdiction = 
	 									(Element) nlTobTaxJurisdiction.item(0); 
	 							strZoneID = eleTobTaxJurisdiction.
	 									getAttribute("TaxZoneId");
	 							strRuleID = eleTobTaxJurisdiction.
	 									getAttribute("TaxRuleId");
	 							if(log.isDebugEnabled()){
	 								log.debug("TaxZoneId: " + strZoneID);
	 								log.debug("TaxRuleID: " + strRuleID);
	 							}
	 						}
	 					}
	 				}
	 			}
		 			
		 			
		 			ArrayList<Element> alOrderLines = SCXmlUtil.getChildren(orderLinesElement, VSIConstants.ELE_ORDER_LINE);
		 			
		 			//creating the INPUT document for getCommonCodeList API
		 			Document docgetCommonCodeInput = SCXmlUtil.createDocument("CommonCode");
					Element eleCommonCodeElement = docgetCommonCodeInput.getDocumentElement();
					
					//Call getCommonCode to get the list of FTC values for marketplace.
		 			String strFDD = null;
		 			String strCD = null;
	 				ArrayList<Element> arrFTCRules = VSIUtils.getCommonCodeList(env, VSI_FTC_RULES, sEnterpriseCode, ATTR_DEFAULT);
	 				if(arrFTCRules.size()>0){
	 					Element eleFTCRule = arrFTCRules.get(0);
	 					strFDD = eleFTCRule.getAttribute(ATTR_CODE_SHORT_DESCRIPTION);
	 					strCD = eleFTCRule.getAttribute(ATTR_CODE_LONG_DESCRIPTION);
	 				}
					
					for(Element orderLineElement1:alOrderLines){	 				
		 				SCXmlUtil.setAttribute(orderLineElement1, VSIConstants.ATTR_LINE_TYPE, "SHIP_TO_HOME");
		 				SCXmlUtil.setAttribute(orderLineElement1, VSIConstants.ATTR_FULFILLMENT_TYPE, "SHIP_TO_HOME");
						
		 				String strOrderLineKey = orderLineElement1.getAttribute(ATTR_ORDER_LINE_KEY);
		 				
		 				// Add logic to set TaxableFlag at LinePriceInfo level
		 				Element eleLinePriceInfo = SCXmlUtil.getChildElement(orderLineElement1, ELE_LINE_PRICE_INFO);
		 				if(!YFCObject.isVoid(eleLinePriceInfo)){
		 					// If TaxableFlag is not already set at LinePriceInfo
	 						if(YFCObject.isVoid(eleLinePriceInfo.getAttribute("TaxableFlag"))){
	 							eleLineTaxes = SCXmlUtil.getChildElement(orderLineElement1, "LineTaxes");
	 							if(!YFCCommon.isVoid(eleLineTaxes)){
	 								if(eleLineTaxes.hasChildNodes()){

	 									eleLineTax = SCXmlUtil.getChildElement(eleLineTaxes, "LineTax");
	 									String taxableFlag  = eleLineTax.getAttribute("TaxableFlag");
	 									if(!YFCObject.isVoid(taxableFlag) && !"Marketplace".equals(orderType)){
	 										eleLinePriceInfo.setAttribute("TaxableFlag", taxableFlag);
	 									}else{
	 										String tax = eleLineTax.getAttribute("Tax");
	 										if(!YFCObject.isVoid(tax)){
	 											if(Double.parseDouble(tax) > 0){
	 												eleLinePriceInfo.setAttribute("TaxableFlag", "Y");
	 											}else{
	 												eleLinePriceInfo.setAttribute("TaxableFlag", "N");
	 											}
	 										}	
	 									}
	 								}else{
		 								eleLinePriceInfo.setAttribute("TaxableFlag", "N");
		 							}
	 							}else{
	 								eleLinePriceInfo.setAttribute("TaxableFlag", "N");
	 							}
	 						}
	 					}
		 				
						// If ZoneId and RuleId have to be updated
		 				if(updateZoneAndRuleId){
							
							eleLineTaxes = SCXmlUtil.getChildElement(
									orderLineElement1, "LineTaxes");
							if(!YFCCommon.isVoid(eleLineTaxes)){
								
								Element eleItem = (Element) orderLineElement1
										.getElementsByTagName("Item").item(0);
								String itemId = eleItem.getAttribute("ItemID");
								String strTaxProductCode = getItemTaxGroupId(env,
										itemId);
								updateLineTaxes(eleLineTaxes, strZoneID, strRuleID, strTaxProductCode);
							}
						}
		 				
		 				if(flag == false){
		 					if(!sCountry.equals("US")){
		 						SCXmlUtil.setAttribute(rootElement, "SourcingClassification", "INTERNATIONAL");
		 						flag = true;
		 					}//end of inner if
		 					else{
		 						
		 						String carrierServiceCode = orderLineElement1.getAttribute(ATTR_CARRIER_SERVICE_CODE);
		 						String orderTypeValue = rootElement.getAttribute(ATTR_ORDER_TYPE);
		 						Element elemPersonInfoShipTo = SCXmlUtil.getChildElement(rootElement, ELE_PERSON_INFO_SHIP_TO);
		 						String addressLine6 = elemPersonInfoShipTo.getAttribute(ATTR_ADDRESS6);		 		 						 						
		 						//calling getCommonCodeList for CodeType="VSI_APO_FPO_ZIP"
		 						String sZipCode = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ZIPCODE);
		 						eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_APO_FPO_ZIP");
		 						eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sZipCode);
		 						docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
					    	
		 						Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
		 						// Setting SourcingClassification=APO_FPO if
		 						// Zip Code is APO FPO DOP zip code
		 						// AddressLine1 or AddressLine2 have "PO Box" in them
		 						if(commonCodeListElement.hasChildNodes()){
		 							SCXmlUtil.setAttribute(rootElement, "SourcingClassification", "APO_FPO");
		 						}//end of if
		 						//OMS-2031 starts
		 						else if (!YFCObject.isVoid(sStateIp)){
								//calling getCommonCodeList for CodeType="VSI_US_TERRITORIE_SR"
		 							eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, "VSI_US_TERRITORIE_SR");
			 						eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, sStateIp);
			 						docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docgetCommonCodeInput);
						    	
			 						commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
			 						// Setting SourcingClassification=US_Territories
			 						// AddressLine1 or AddressLine2 have "PO Box" in them
			 						if(commonCodeListElement.hasChildNodes()){
			 							SCXmlUtil.setAttribute(rootElement, "SourcingClassification", "US_Territories");
			 						}//end of if
			 						else if((addressLine6.equalsIgnoreCase(FLAG_Y)) || (orderTypeValue.equalsIgnoreCase(ATTR_ORDER_TYPE_POS)) || (!carrierServiceCode.isEmpty() && (carrierServiceCode.equalsIgnoreCase(CSC_NEXTDAY) || carrierServiceCode.equalsIgnoreCase(CSC_TWODAY))))
			 						{		 							
			 							SCXmlUtil.setAttribute(rootElement, ATTR_SOURCING_CLASSIFICATION, NO_SFS_CLASSIFICATION);
			 						}
			 						else{
			 							SCXmlUtil.setAttribute(rootElement, "SourcingClassification", "");
			 						}//end of inner else
			 						flag = true;
			 					}//end of else if
		 						//OMS-2031 ends
		 						else{
		 							SCXmlUtil.setAttribute(rootElement, "SourcingClassification", "");
		 						}//end of inner else
		 						flag = true;
		 					}//end of else	 
			 			
		 				}//end of if for checking flag
		 				
		 				// Setting AddressLine6 = Y if AddressLine1 or AddressLine2 have "PO Box" in them
		 				Element elePersonInfoShipToLine = SCXmlUtil.getChildElement(orderLineElement1, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
		 				if(!YFCObject.isVoid(elePersonInfoShipToLine)){
		 					
		 					addressLine1 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS1);
			 		 		addressLine2 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDRESS2);
		 					if(Pattern.matches(poBoxPatternString, addressLine1)
		 							|| Pattern.matches(poBoxPatternString, addressLine2)){
		 						elePersonInfoShipToLine.setAttribute("AddressLine6", "Y");

		 					}
		 				}
		 				
			 			
			 			
		 				
		 				if(arrFTCRules.size()>0){
							
		 					if(orderType.equals("WEB")){
								Element eleOrderDates = SCXmlUtil.getChildElement(orderLineElement1, VSIConstants.ELE_ORDER_DATES );
								if(!YFCObject.isNull(eleOrderDates)){
									ArrayList<Element> arrOrderDates = SCXmlUtil.getChildren(eleOrderDates, ELE_ORDER_DATE);
									for(Element eleOrderDate : arrOrderDates){
										String sDateTypeId = SCXmlUtil.getAttribute(eleOrderDate, VSIConstants.ATTR_DATE_TYPE_ID);
										if(sDateTypeId.equals("YCD_FTC_PROMISE_DATE")){
											String sActualDate = SCXmlUtil.getAttribute(eleOrderDate, VSIConstants.ATTR_ACTUAL_DATE);
											if(YFCCommon.isVoid(sActualDate)){
												sActualDate = SCXmlUtil.getAttribute(eleOrderDate, VSIConstants.ATTR_EXPECTED_DATE);
											}
											if(!YFCCommon.isVoid(sActualDate)&& VSIUtils.differenceBetweenDates(sActualDate,sOrderDate)>0){
												Element eleFirstFTCPromiseDate = XMLUtil.getElementByXPath(inXML,
														"Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_FIRST_PROMISE_DATE']");
												if(YFCCommon.isVoid(eleFirstFTCPromiseDate)){
													eleFirstFTCPromiseDate = SCXmlUtil.createChild(eleOrderDates, ELE_ORDER_DATE);
												}	
												eleFirstFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_FIRST_PROMISE_DATE");
												eleFirstFTCPromiseDate.setAttribute(ATTR_EXPECTED_DATE, sActualDate);
											}else{
												eleOrderDate.setAttribute(VSIConstants.ATTR_DATE_TYPE_ID, "YCD_FTC_FIRST_PROMISE_DATE");
												eleOrderDate.setAttribute(ATTR_EXPECTED_DATE, sActualDate);
											}
											//Set ReqCancelDate
											String sReqCancelDate = VSIUtils.addDaysToPassedDateTime(sActualDate, strCD);
											SCXmlUtil.setAttribute(orderLineElement1, VSIConstants.ATTR_REQ_CANCEL_DATE, sReqCancelDate);
										}
										if(sDateTypeId.equals("YCD_FTC_FIRST_PROMISE_DATE")){
											String sExpectedDate = SCXmlUtil.getAttribute(eleOrderDate, ATTR_EXPECTED_DATE);
											
											if(YFCCommon.isVoid(sExpectedDate)){
												sExpectedDate = SCXmlUtil.getAttribute(eleOrderDate, VSIConstants.ATTR_ACTUAL_DATE);
											}
											
											if(!YFCCommon.isVoid(sExpectedDate) && VSIUtils.differenceBetweenDates(sExpectedDate,sOrderDate)>0){
												Element eleFTCPromiseDate = XMLUtil.getElementByXPath(inXML,
														"Order/OrderLines/OrderLine[@OrderLineKey='"+strOrderLineKey+"']/OrderDates/OrderDate[@DateTypeId='YCD_FTC_PROMISE_DATE']");
												if(YFCCommon.isVoid(eleFTCPromiseDate)){
													eleFTCPromiseDate = SCXmlUtil.createChild(eleOrderDates, ELE_ORDER_DATE);
												}
												eleFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_PROMISE_DATE");
												eleFTCPromiseDate.setAttribute(VSIConstants.ATTR_ACTUAL_DATE, sExpectedDate);
											}
											
											//Set ReqCancelDate
											String sReqCancelDate = VSIUtils.addDaysToPassedDateTime(sExpectedDate, strCD);
											SCXmlUtil.setAttribute(orderLineElement1, VSIConstants.ATTR_REQ_CANCEL_DATE, sReqCancelDate);
										}
									}// end of checking if for sDateTypeId	
								}//end of checking eleOrderDates
							}//end of if checking for enterprise code and orderType
							
							if(orderType.equals("Marketplace")||orderType.equals("POS") ){
								Element eleOrderDates = SCXmlUtil.createChild(orderLineElement1, ELE_ORDER_DATES);
								
								//set First FTC date
								Element eleFirstFTCPromiseDate = SCXmlUtil.createChild(eleOrderDates, ELE_ORDER_DATE);
								eleFirstFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_FIRST_PROMISE_DATE");
								eleFirstFTCPromiseDate.setAttribute(ATTR_EXPECTED_DATE, sOrderDate);
								
								//set FTC Promise date only for POS. this is to make sure FTC Delay email is sent right then and there post schedule. 
								if(!orderType.equals("Marketplace"))
								{
									Element eleFTCPromiseDate = SCXmlUtil.createChild(eleOrderDates, ELE_ORDER_DATE);
									eleFTCPromiseDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_PROMISE_DATE");
									if(!YFCCommon.isVoid(strFDD)){
										eleFTCPromiseDate.setAttribute(ATTR_ACTUAL_DATE, VSIUtils.addDaysToPassedDateTime(sOrderDate, strFDD)); 
									}	
								}	
								
								//set FTC cancel date
								Element eleFTCCancelDate = SCXmlUtil.createChild(eleOrderDates, ELE_ORDER_DATE);
								eleFTCCancelDate.setAttribute(ATTR_DATE_TYPE_ID, "YCD_FTC_CANCEL_DATE");
								if(!YFCCommon.isVoid(strCD)){
									eleFTCCancelDate.setAttribute(ATTR_ACTUAL_DATE, VSIUtils.addDaysToPassedDateTime(sOrderDate, strCD)); 
								}	
								
								SCXmlUtil.setAttribute(orderLineElement1, VSIConstants.ATTR_REQ_CANCEL_DATE, VSIUtils.addDaysToPassedDateTime(sOrderDate, strCD));
							
							}//end of checking marketplace enterprise code
						}//end of checking commonCodeListElement
						
						//creating document to call getItemList API for checking if the item IsSignReqdItem
						Document docgetItemListOutput = null;
						Element ItemElement = SCXmlUtil.getChildElement(orderLineElement1, VSIConstants.ELE_ITEM);
						String sItemID = SCXmlUtil.getAttribute(ItemElement, VSIConstants.ATTR_ITEM_ID);
						Document docgetItemListInput = SCXmlUtil.createDocument("Item");
						Element itemElement = docgetItemListInput.getDocumentElement();
						SCXmlUtil.setAttribute(itemElement, VSIConstants.ATTR_ITEM_ID, sItemID);
						SCXmlUtil.setAttribute(itemElement, VSIConstants.ATTR_ORG_CODE, "VSI-Cat");
						SCXmlUtil.setAttribute(itemElement, VSIConstants.ATTR_UOM, "EACH");
						docgetItemListOutput = VSIUtils.invokeAPI(env,VSIConstants.TEMPLATE_GET_ITEM_LIST,VSIConstants.API_GET_ITEM_LIST, docgetItemListInput);
						
						Element itemListElement = docgetItemListOutput.getDocumentElement();
						if(itemListElement.hasChildNodes())
						{
							Element itemElement1 = SCXmlUtil.getChildElement(itemListElement, VSIConstants.ELE_ITEM);
							if(!YFCObject.isNull(itemElement1)){
								Element extnElement = SCXmlUtil.getChildElement(itemElement1, VSIConstants.ELE_EXTN);
								if(!YFCObject.isNull(extnElement)){
									String sExtnIsSignReqdItem = SCXmlUtil.getAttribute(extnElement, VSIConstants.ATTR_EXTN_IS_SIGN_REQD_ITEM);


									if(!YFCObject.isVoid(sExtnIsSignReqdItem) && sExtnIsSignReqdItem.equals("Y")){
										Document docVSISignatureRequiredItemListOutput = null;

										//creating document to invoke VSISignatureRequiredItemList service 
										String sState = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_STATE);
										Document docVSISignatureRequiredItemInput  = SCXmlUtil.createDocument("VSISignatureRequiredItem");
										Element eleVSISignatureRequiredItem = docVSISignatureRequiredItemInput.getDocumentElement();
										SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_ITEM_ID, sItemID);
										//OMS-875:Start
										//SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_ORG_CODE, sEnterpriseCode);
										SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_ORG_CODE, "VSI-Cat");
										//OMS-875:End
										SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_UOM, "EACH");
										SCXmlUtil.setAttribute(eleVSISignatureRequiredItem, VSIConstants.ATTR_STATE, sState);

										docVSISignatureRequiredItemListOutput = VSIUtils.invokeService(env,"VSISignatureRequiredItemList", docVSISignatureRequiredItemInput);
										Element eleVSISignatureRequiredItemList = docVSISignatureRequiredItemListOutput.getDocumentElement();
										if(!YFCObject.isNull(eleVSISignatureRequiredItemList)){
											Element eleVSISignatureRequiredItem1 = SCXmlUtil.getChildElement(eleVSISignatureRequiredItemList, "VSISignatureRequiredItem");
											String sExtn21SignReqd = SCXmlUtil.getAttribute(eleVSISignatureRequiredItem1, "Extn21SignReqd");
											String sExtn18SignReqd = SCXmlUtil.getAttribute(eleVSISignatureRequiredItem1, "Extn18SignReqd");

											Element extnElement1 = SCXmlUtil.getChildElement(orderLineElement1, VSIConstants.ELE_EXTN); 
											if(!YFCObject.isVoid(sExtn21SignReqd) && sExtn21SignReqd.equals("Y")){

												SCXmlUtil.setAttribute(extnElement1, "ExtnSignatureType", "A");

											}//end of checking sExtn21SignReqd
											else if(!YFCObject.isVoid(sExtn18SignReqd) && sExtn18SignReqd.equals("Y")){

												SCXmlUtil.setAttribute(extnElement1, "ExtnSignatureType", "Y");

											}//end of checking sExtn18SignReqd
										}//end of if for checking eleVSISignatureRequiredItemList
									}//end of if for checking ExtnIsSignReqdItem
								}//end of if for checking extnElement
							}//end of checking itemElement
						}
		 			}//end of for 
		 		}//end of if
	 		}//end of Checking orderlines
	 		
	 		/**
	 		 * code to check marketplace address length
	 		 * if the condition for checking the length fails apply MARKETPLACE_ORDER_HOLD
	 		 */
	 		
	 		if(orderType.equals("Marketplace")){
	 			rootElement.setAttribute("ValidateItem", "N");
	 			String sAddressLine1 = SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_1);
	 			String sAddressLine2 =  SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_ADDR_LINE_2);
	 			String sCountry =  SCXmlUtil.getAttribute(elePersonInfoShipTo, VSIConstants.ATTR_COUNTRY);
	 			//if((sAddressLine1.length() > 29) || (sAddressLine2.length() > 29) || (sCountry.length() > 2)){
	 			
	 				//Element eleOrderHoldTypes = SCXmlUtil.createChild(rootElement, VSIConstants.ELE_ORDER_HOLD_TYPES);
	 				//rootElement.setAttribute("ValidateItem", "N");
	 				//Element eleOrderHoldType = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
	 				//SCXmlUtil.setAttribute(eleOrderHoldType, VSIConstants.ATTR_HOLD_TYPE,"VSI_MARKETPLACE_HOLD");
	 				
	 				//creating input doc to createOrder Exception
	 				// START ARS-35
	 				/*Document doccreateExceptionInput = SCXmlUtil.createDocument("Inbox"); 
	    			Element eleInbox = doccreateExceptionInput.getDocumentElement();
	    			eleInbox.setAttribute(VSIConstants.ATTR_ORDER_NO, SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_ORDER_NO));
	    			eleInbox.setAttribute(VSIConstants.ATTR_QUEUE_ID, "VSI_ORDER_HOLD");
	    			eleInbox.setAttribute( VSIConstants.ATTR_EXCEPTION_TYPE, "MARKETPLACE_ORDER_HOLD");
	    			eleInbox.setAttribute(VSIConstants.ATTR_DESCRIPTION, "Please Verify the ship to address of OrderNo." + SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_ORDER_NO));
	    			
	    			Element inboxOrderEle = SCXmlUtil.createChild(eleInbox,VSIConstants.ELE_ORDER);
	    			inboxOrderEle.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(rootElement, VSIConstants.ATTR_ENTERPRISE_CODE));
	    			
	    			VSIUtils.invokeAPI(env,VSIConstants.API_CREATE_EXCEPTION, doccreateExceptionInput);*/
	 				// END ARS-35
	 	 			
	 			//}// end of if for checking the length of address
	 		}// end of if for checking orderType = Marketplace
	 		
			
			}
			
		
		//OMS-1092 : Start
		stampExtnAttributes(env,inXML);	
	    //OMS-1092: End
		//OMS- 1137 : Stamp Reservation : Start
		stampOrderLineReservation(env,inXML);
		//OMS-117 : Stamp Reservation : End
		
		/*NodeList orderLineNode = inXML.getElementsByTagName(ELE_ORDER_LINE);
		int isSfsCount = 0;
		for(int l=0; l< orderLineNode.getLength(); l++)
		{
			Element orderLineEle = (Element) inXML.getElementsByTagName(ELE_ORDER_LINE).item(l);
			Element itemEle = (Element) orderLineEle.getElementsByTagName(ELE_ITEM).item(0);
			String itemId = itemEle.getAttribute(ITEM_ID);
			Document docItemInput = SCXmlUtil.createDocument(ELE_ITEM);
			Element eleItem = docItemInput.getDocumentElement();
			eleItem.setAttribute(ATTR_ITEM_ID, itemId);
			Document itemListOutDoc = VSIUtils.invokeAPI(env, TEMPLATE_GET_ITEM_LIST, API_GET_ITEM_LIST, docItemInput);
			Element eleItemList = (Element) itemListOutDoc.getDocumentElement().getElementsByTagName(ELE_ITEM).item(0);
			if(!YFCObject.isNull(eleItemList))
			{
			Element extnEle = (Element) eleItemList.getElementsByTagName(ELE_EXTN).item(0);		  
			String isSfsEligible = extnEle.getAttribute(ATTR_EXTN_SFS_ELIGIBLE);
			log.info("isSfsEligible => "+isSfsEligible);
			if(isSfsEligible.equalsIgnoreCase(FLAG_Y))
				isSfsCount++;
			}
		}
		log.info("isSfsCount => "+isSfsCount);
		if(isSfsCount != 0)
		{
			Element rootElement = inXML.getDocumentElement();
			SCXmlUtil.setAttribute(rootElement, ATTR_SOURCING_CLASSIFICATION, NO_SFS_CLASSIFICATION);
			log.info("sourc class => "+rootElement.getAttribute(ATTR_SOURCING_CLASSIFICATION));
		}*/
		}		
		catch (YFSException e) {
			e.printStackTrace();
		//	YFSException e1 = new YFSException();
			String error = e.getMessage();
			String errorCode = e.getErrorCode();
			String errorDesc = e.getErrorDescription();
			throw new YFSException(
					errorCode,
					errorDesc,
					error);
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
					"Exception");
		} 	
		    	return inXML;
	}
	
	//OMS-2418 -- Start
	private boolean isCallCenterAurus(YFSEnvironment env) throws ParserConfigurationException {
		
		boolean isCallCenterAurus=false;
		Document getCommonCodeListInputForCallCenterAurus = getCommonCodeListInputForCodeType("DEFAULT","CALL_CENTER_AURUS");
		Document getCommonCodeOutCallCenterAurus = getCommonCodeList(env, getCommonCodeListInputForCallCenterAurus);
		Element eleCOmmonCodeOut = (Element)getCommonCodeOutCallCenterAurus.getElementsByTagName("CommonCode").item(0);
		String strCallCenterAurus = eleCOmmonCodeOut.getAttribute("CodeLongDescription");
		if(strCallCenterAurus.equalsIgnoreCase(VSIConstants.FLAG_Y)) {
			isCallCenterAurus=true;
		}
		
		return isCallCenterAurus;
	}

	private Document getCommonCodeList(YFSEnvironment env,
			Document docApiInput) {
		Document docApiOutput = null;
		try {
			YIFApi api;
			api = YIFClientFactory.getInstance().getApi();
			docApiOutput = api.invoke(env, "getCommonCodeList", docApiInput);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return docApiOutput;
	}

	private Document getCommonCodeListInputForCodeType(String sOrgCode,
			String codeType) throws ParserConfigurationException {
		
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeType", codeType);
		return docOutput;
	}
	//OMS-2418 -- End
	
	private void updateAurusFlaginOH(String orderHeaderKey ,YFSEnvironment env) {
		String orderHeaderKeyStr = orderHeaderKey;
		env.clearApiTemplates();
		try {
			Document inputChangeOrder = createChangeOrderForExtnAurusToken(orderHeaderKeyStr);
			Document docChangeOrderOutput = VSIUtils.invokeAPI(env, "changeOrder", inputChangeOrder);
		} catch (Exception Ex) {
			Ex.printStackTrace();
			throw new YFSException();
		}
	}

	private Document createChangeOrderForExtnAurusToken(String sOHK) {
		Document docOrder = SCXmlUtil.createDocument("Order");
		Element eleOrder = docOrder.getDocumentElement();
		eleOrder.setAttribute("Action", "MODIFY");
		eleOrder.setAttribute("OrderHeaderKey", sOHK);
		eleOrder.setAttribute("Override", "Y");
		Element eleExtn = SCXmlUtil.createChild(eleOrder, "Extn");
		eleExtn.setAttribute("ExtnAurusToken", "Y");
		return docOrder;		
	}

	private void updateGCPaymentMethods(YFSEnvironment env, Document inXML, NodeList orderLineList, String strEntryType) throws ParserConfigurationException 
	{
		int orderLineLength = orderLineList.getLength();
		String deliveryMethod = null;
		if (orderLineList != null && orderLineLength > 0) 
		{
			for (int i = 0; i < orderLineList.getLength(); i++) {
				Element eleOrderLine = (Element) orderLineList.item(i);
				deliveryMethod = eleOrderLine.getAttribute("DeliveryMethod");
				break;
			}
		}
			
		// TODO Auto-generated method stub
		Element eleOrder = inXML.getDocumentElement();
		Element elePaymentMethods = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PAYMENT_METHODS);
		
			if(!YFCCommon.isVoid(elePaymentMethods)){
				ArrayList<Element> alPaymentMethodsElement = SCXmlUtil.getChildren(elePaymentMethods, VSIConstants.ELE_PAYMENT_METHOD);
				
				Map<String, Document> mapGCPaymentMethods = new HashMap<>();
				for(Element paymentMethodElement:alPaymentMethodsElement)
				{
					String paymentType = paymentMethodElement.getAttribute("PaymentType");
					if("GIFT_CARD".equals(paymentType) && "PICK".equals(deliveryMethod) && "WEB".equalsIgnoreCase(strEntryType))
					{
						Date todaysDate = new Date(); 
						DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); 
						String dateToday = formatter.format(todaysDate);
						
						String orderNo = eleOrder.getAttribute("OrderNo");
						double dRequestAmount = 0.0;
						String sRequestAmount = Double.toString(dRequestAmount);
						String svcNo = paymentMethodElement.getAttribute("SvcNo");
						Element paymentDetails = SCXmlUtil.getChildElement(paymentMethodElement, "PaymentDetails");
						if(!YFCCommon.isVoid(paymentDetails))
						{
							
							String authID = paymentDetails.getAttribute("AuthorizationID");
							
							String shipNode = "6101";
							String shipNodePadded = ("0000000000" + shipNode).substring(shipNode.length());
							
							Document GiftCardInput = XMLUtil.createDocument("GiftCard");
							Element GCElement = GiftCardInput.getDocumentElement();
							GCElement.setAttribute("storeNumber", shipNodePadded);
							GCElement.setAttribute("cardNumber", svcNo);
							//GCElement.setAttribute("pinNumber", sPinNo);
							GCElement.setAttribute("date", dateToday);
							GCElement.setAttribute("invoiceNumber", orderNo);
							GCElement.setAttribute("amount", sRequestAmount);
							GCElement.setAttribute("transactionID", authID);
							
							mapGCPaymentMethods.put(authID, GiftCardInput);
							paymentMethodElement.removeChild(paymentDetails);
						}
							
					}
					
					if("PAYPAL".equals(paymentType) && "WEB".equalsIgnoreCase(strEntryType))
					{
						String maxChargeLimit = "0.00";
						maxChargeLimit = paymentMethodElement.getAttribute("MaxChargeLimit");
						double dblMaxChargeLimit = Double.parseDouble(maxChargeLimit);
						dblMaxChargeLimit = dblMaxChargeLimit + 200;
						DecimalFormat dd = new DecimalFormat();
						paymentMethodElement.setAttribute("MaxChargeLimit", String.valueOf(dd.format(dblMaxChargeLimit)));	
					}

					if("CREDIT_CARD".equals(paymentType))
					{
						double dblMaxChargeLimit = SCXmlUtil.getDoubleAttribute(paymentMethodElement, "MaxChargeLimit");
						if(YFCDoubleUtils.greaterThan(dblMaxChargeLimit, 0))
						{
							paymentMethodElement.setAttribute("UnlimitedCharges","N");
						}
						//OMS-1951: Start
						Element eleExtn = SCXmlUtil.getChildElement(eleOrder, ELE_EXTN);
						eleExtn.setAttribute(ATTR_EXTN_TNS_AUTHORIZED, FLAG_Y);
						//OMS-1951: End
						//OMS-1350: Start//	
					}
					
				}
				
				if(!mapGCPaymentMethods.isEmpty() && "WEB".equalsIgnoreCase(strEntryType))
				{
					env.setTxnObject("GC_VOID_REQUEST", mapGCPaymentMethods);
				}
			}
		
	}

private String getVirtualStore(YFSEnvironment env, String codeValue, String codetype) {
		
		String virtualStore = null;
		try{
		Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

		Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_VALUE,codeValue);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE,codetype);

		Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);
		
		if (docAfterGetCommonCodeList.getDocumentElement().hasChildNodes()) {
			Element eleOutCommonCode=(Element) docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
			virtualStore = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
		}}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		////System.out.println("***remorsePrd " +remorsePrd);
		
		// TODO Auto-generated method stub
		return virtualStore;
	}

	   //creating input for getItemList
		private Document createInputForGetItemList(String itemID, String actItemID) 
		{
			// Create a new document with root element as Item
			Document docInput = SCXmlUtil.createDocument(VSIConstants.ELE_ITEM);
			
			Element eleItem = docInput.getDocumentElement();
			
			if(!YFCCommon.isVoid(itemID))
			{
				
				eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID, itemID);
			}
			
			if(!YFCCommon.isVoid(actItemID))
			{
				Element extnItem = SCXmlUtil.createChild(eleItem, VSIConstants.ELE_EXTN);
				extnItem.setAttribute(VSIConstants.EXTN_ACT_SKU_ID,actItemID);
			}

			return docInput;
		}
	    
		/**
		 * SKU ID for AMAZON and Nutrition Depot Marketplace and JET and EBAY
		 * 
		 * @param env
		 * @param orderLineList
		 * @param outdoc1
		 * @param inXML
		 */

		private Document setLineAttrforMarketplace(YFSEnvironment env,
				NodeList orderLineList, Document inXML) {

			try {
				
				Document getItemListOutput = null;
				int orderLineLength = orderLineList.getLength();
				String enterpriseCode = inXML.getDocumentElement().getAttribute(ATTR_ENTERPRISE_CODE);
				
				if (orderLineList != null && orderLineLength > 0) 
				{
					for (int i = 0; i < orderLineLength; i++) {
						Element eleOrderLine = (Element) orderLineList.item(i);

						// Getting the Extn element from order line. 
						Element ele_OrderLineExtn = SCXmlUtil.getChildElement(eleOrderLine,
								VSIConstants.ELE_EXTN);

						String strExtnActItemId = null;
						if(!YFCCommon.isVoid(ele_OrderLineExtn))
						{
							if(log.isDebugEnabled()){
								log.debug("Empty OrderLineExtn" +SCXmlUtil.getString(ele_OrderLineExtn));
							}
							strExtnActItemId = ele_OrderLineExtn
									.getAttribute(VSIConstants.ATTR_EXTN_ACT_ITEMID);
						}

						Document getItemListInput = null;
						// if ACT Item ID is present in the input fetch the corresponding Item ID to be stamped to the order line. 
						if (!YFCCommon.isVoid(strExtnActItemId))
						{
							getItemListInput = createInputForGetItemList(null,strExtnActItemId);
							if(log.isDebugEnabled()){
								log.debug("Input for getItemList: " +SCXmlUtil.getString(getItemListInput));
							}
						}

						// Getting the Extn element from order line. 
						Element eleItem = SCXmlUtil.getChildElement(eleOrderLine,
								VSIConstants.ELE_ITEM);

						String itemid = null;
						if(!YFCCommon.isVoid(eleItem))
						{
							itemid = eleItem
									.getAttribute(VSIConstants.ATTR_ITEM_ID);
						}

						// if ACT Item ID is present in the input fetch the corresponding Item ID to be stamped to the order line. 
						if (!YFCCommon.isVoid(itemid))
						{
							getItemListInput = createInputForGetItemList(itemid,null);
							if(log.isDebugEnabled()){
								log.debug("Input for getItemList: " +SCXmlUtil.getString(getItemListInput));
							}
						}

						getItemListOutput = VSIUtils.invokeAPI(env,
								VSIConstants.TEMPLATE_GET_ITEM_LIST,
								VSIConstants.API_GET_ITEM_LIST, getItemListInput);
						
						if(log.isDebugEnabled()){
							log.debug("Output for getItemList: " +SCXmlUtil.getString(getItemListOutput));
						}
						
						Element eleItemList = getItemListOutput
								.getDocumentElement();
						if (eleItemList.hasChildNodes())
						{
							Element ele_Item = SCXmlUtil.getChildElement(eleItemList,
									VSIConstants.ELE_ITEM);
							Element eleItemAliasList = SCXmlUtil.getChildElement(
									ele_Item,VSIConstants.ELE_ITEM_ALIAS_LIST);

							Element ele_ItemExtn = SCXmlUtil.getChildElement(ele_Item,
									VSIConstants.ELE_EXTN);


							if(eleItemAliasList.hasChildNodes())
							{
								ArrayList<Element> alItemAliasList = SCXmlUtil.getChildren(eleItemAliasList, VSIConstants.ELE_ITEM_ALIAS);
								for(Element eleItemAlias:alItemAliasList){
									//Element eleItemAlias = SCXmlUtil.getChildElement(eleItemAliasList, VSIConstants.ELE_ITEM_ALIAS);

									String strAliasName = eleItemAlias
											.getAttribute(VSIConstants.ATTR_ALIAS_NAME);

									// getting the alias
									// value for UPC
									String aliasValue = eleItemAlias.getAttribute(VSIConstants.ATTR_ALIAS_VALUE); // getting
									// the
									// alias
									// value
									// for
									// UPC

									// checking if aliasname=UPC
									if (strAliasName.equals(VSIConstants.ATTR_UPC)) {
										eleItem.setAttribute(VSIConstants.ATTR_UPCCODE,
												aliasValue);// setting the alias value to
										// UPCCode
									}

								}


							}

							String updatedActItemID = null;

							if(!YFCCommon.isVoid(ele_ItemExtn))
							{
								updatedActItemID = ele_ItemExtn.getAttribute(VSIConstants.EXTN_ACT_SKU_ID);
								if(!YFCCommon.isVoid(updatedActItemID) && !YFCCommon.isVoid(ele_OrderLineExtn))
								{
									ele_OrderLineExtn.setAttribute(VSIConstants.ATTR_EXTN_ACT_ITEMID,
											updatedActItemID);
								}
							}

							String updatedItemID = ele_Item.getAttribute(VSIConstants.ATTR_ITEM_ID);
							if(!YFCCommon.isVoid(updatedItemID))
							{

								eleItem.setAttribute(VSIConstants.ATTR_ITEM_ID,
										updatedItemID);
							}

						}
						
						// TODO Add logic to get price for MCL items and update
						
						if(enterpriseCode.equals(ENT_MCL)){
							
							itemid = SCXmlUtil.getAttribute(eleItem, ATTR_ITEM_ID);
							String unitPrice = getUnitPriceForMCLItem(env, itemid);
							Element eleLinePriceInfo = SCXmlUtil.getChildElement(eleOrderLine, ELE_LINE_PRICE_INFO);
							if(!YFCObject.isVoid(eleLinePriceInfo)){
								
								SCXmlUtil.setAttribute(eleLinePriceInfo, ATTR_UNIT_PRICE, unitPrice);
							}
						}

					}// end of for
				}// end of if
			}// end of try
			catch (Exception e) {
				e.printStackTrace();

			}
			return inXML;
		}
	    

	// JIRA 692
	// Add Order Hold Type tag at order level
	private Document applyVSIScheduleHold(Document inXML) {
		Element orderEle = inXML.getDocumentElement();
		Element holdTypesEle = XMLUtil.appendChild(inXML, orderEle,
				"OrderHoldTypes", "");
		Element holdTypeEle = XMLUtil.appendChild(inXML, holdTypesEle,
				"OrderHoldType", "");

		holdTypeEle.setAttribute("HoldType", "VSI_SCHEDULE_HOLD");
		holdTypeEle.setAttribute("Status", "1100");

		return inXML;
	}

	private String getItemTaxGroupId(YFSEnvironment env, String itemID)
			throws ParserConfigurationException, YIFClientCreationException,
			YFSException, RemoteException {
		String extnTaxGroupId = null;
		Document getItemId = XMLUtil.createDocument("Item");
		Element ItemEle = getItemId.getDocumentElement();
		ItemEle.setAttribute("ItemID", itemID);
		ItemEle.setAttribute("OrganizationCode", "VSI-Cat");

		api = YIFClientFactory.getInstance().getApi();
		env.setApiTemplate("getItemList", "global/template/api/getItemList.xml");
		Document outDocItemList = api.invoke(env, "getItemList", getItemId);
		env.clearApiTemplates();
		Element rootElement = outDocItemList.getDocumentElement();
		Element itemEle = (Element) rootElement.getElementsByTagName("Extn")
				.item(0);
		if(!YFCCommon.isVoid(itemEle)){
		extnTaxGroupId = itemEle.getAttribute("ExtnTaxProductCode");
		}

		return extnTaxGroupId;
	}

	/*private String generateOrderNo(YFSEnvironment env, Document inXML)
			throws Exception {
		Element rootElement = inXML.getDocumentElement();
		String tranDate = rootElement
				.getAttribute(VSIConstants.ATTR_ORDER_DATE);

		tranDate = tranDate.replaceAll("\\-", "");// Removing the "-" from the
		// input OrderDate.
		tranDate = tranDate.substring(2, 6);// Getting YYMM from the input
		Element orderLineElement = (Element) inXML.getElementsByTagName(
				VSIConstants.ELE_ORDER_LINE).item(0);
		String shipNode = orderLineElement
				.getAttribute(VSIConstants.ATTR_SHIP_NODE);// enteredBy
		String shipNodePadded = ("00000" + shipNode).substring(shipNode
				.length());// Adding Leading Zeros

		// Get the email address after calling getOrgList using the shipNode

		
		 * Document getOrganizationListInput;
		 * 
		 * getOrganizationListInput = XMLUtil.createDocument("Organization");
		 * Element eleOrder = getOrganizationListInput.getDocumentElement();
		 * eleOrder.setAttribute(VSIConstants.ATTR_ORG_CODE, shipNode); api =
		 * YIFClientFactory.getInstance().getApi(); Document outDoc =
		 * api.invoke(env,
		 * VSIConstants.API_GET_ORGANIZATION_LIST,getOrganizationListInput);
		 * Element contactPersonInfoElement = (Element)
		 * outDoc.getElementsByTagName
		 * (VSIConstants.ELE_CONTACT_PERSON_INFO).item(0); String emailID =
		 * contactPersonInfoElement.getAttribute(VSIConstants.ATTR_EMAIL_ID);
		 

		String tranNumber = "";
		String seqNum = "VSI_SEQ_" + shipNode;
		tranNumber = VSIDBUtil.getNextSequence(env, seqNum);
		String tranNumberPadded = ("00000" + tranNumber).substring(tranNumber
				.length()); // Adding Leading Zeros
		String tranNumberCustCustPO = ("000000" + tranNumber)
				.substring(tranNumber.length());
		log.info("=====>Printing Padded Transaction Number======="
				+ tranNumberPadded);

		String regNumber = VSIConstants.REG_NUMBER;
		String grpNumber = VSIConstants.GROUP_NUMBER;
		String itemStatusNumber = VSIConstants.LINE_ITEM_STATUS_NUMBER;

		String customerPoNo = shipNodePadded + tranDate + regNumber
				+ tranNumberPadded + grpNumber + itemStatusNumber;
		// rootElement.setAttribute(VSIConstants.ATTR_CUST_PO_NO, customerPoNo);
		// Also need to set another attribute for transaction number TBD for now
		// stored in CustCustPoNo
		rootElement.setAttribute(VSIConstants.ATTR_CUST_CUST_PO_NO,
				tranNumberCustCustPO);
		// rootElement.setAttribute(VSIConstants.ATTR_EMAIL_ID, emailID);
		// Element elePersonInfoShipTo =
		// (Element)inXML.getElementsByTagName(VSIConstants.ATTR_PERSON_INFO_SHIP_TO).item(0);
		// elePersonInfoShipTo.setAttribute(VSIConstants.ATTR_EMAIL_ID,
		// emailID);

		return customerPoNo;
	}// end generateOrderNo
*/
	public String beforeCreateOrder(YFSEnvironment arg0, String arg1)
			throws YFSUserExitException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void updateLineTaxes(Element eleLineTaxes, String strZoneID, 
			String strRuleID, String strTaxProductCode){
		
		Element eleLineTax = null;
		Element eleExtn = null;
		NodeList nlLineTax = eleLineTaxes.getElementsByTagName("LineTax");
		for(int i = 0; i < nlLineTax.getLength(); i++){
		
			eleLineTax = (Element) nlLineTax.item(i);
			if(!YFCCommon.isVoid(eleLineTax)){
				
				eleExtn = SCXmlUtil.getChildElement(eleLineTax, "Extn");
				if(YFCCommon.isVoid(eleExtn)){
					eleExtn = SCXmlUtil.createChild(eleLineTax, "Extn");
				}
				
				eleExtn.setAttribute("ZoneId", strZoneID);
				eleExtn.setAttribute("RuleId", strRuleID);
				eleExtn.setAttribute("GroupId", strTaxProductCode);
				if(!eleExtn.hasAttribute("TaxType")){
					eleExtn.setAttribute("TaxType", "Standard");
				}
			}
		}
	}
	
	private String getUnitPriceForMCLItem(YFSEnvironment env, String itemID) throws Exception{
		
		Document getPriceListLineListInXML = SCXmlUtil.createDocument("PricelistLine");
		Element elePricelistLine = getPriceListLineListInXML.getDocumentElement();
		SCXmlUtil.setAttribute(elePricelistLine, ATTR_ORGANIZATION_CODE, ENT_MCL);
		Element eleItem = SCXmlUtil.createChild(elePricelistLine, ELE_ITEM);
		SCXmlUtil.setAttribute(eleItem, ATTR_ITEM_ID, itemID);
		SCXmlUtil.setAttribute(eleItem, ATTR_UOM, UOM_EACH);
		
		Document getPriceListLineListOutXML = VSIUtils.invokeService(env, SERVICE_GET_MCL_ITEM_PRICE, getPriceListLineListInXML);
		Element elePricelistLineList = getPriceListLineListOutXML.getDocumentElement();
		if(elePricelistLineList.hasChildNodes()){
			
			elePricelistLine = SCXmlUtil.getChildElement(elePricelistLineList, "PricelistLine");
			return elePricelistLine.getAttribute(ATTR_UNIT_PRICE);
		}
		
		return ZERO_DOUBLE;
	}
	
	
	//OMS-1033 PennyDiscount : Start
	private void applyPennyDiscount(YFSEnvironment env,Document inXML){
		
		try {
			
			double dOrderTotal=0.00;
			double dHeaderChargeTotal=0.00;
			double dLineTotal=0.00;
			double dOrderMaxChargeLimit=0.00;
			double dDifference=0.00;
			double dHeaderTaxTotal=0.00;
			
			// calculating  dHeaderChargeTotal
			List<Element> lHeaderCharge=XMLUtil.getElementListByXpath(inXML, "/Order/HeaderCharges/HeaderCharge");
			for (Element eleHeaderCharge : lHeaderCharge) {
				double dChargeAmountHC=SCXmlUtil.getDoubleAttribute(eleHeaderCharge,ATTR_CHARGE_AMOUNT , 0.00);
				dHeaderChargeTotal+=dChargeAmountHC;
			}
			if(log.isDebugEnabled()){
				log.debug("HeaderCharge total : "+dHeaderChargeTotal);
			}
			// calculating  dHeaderTaxTotal
			List<Element> lHeaderTax=XMLUtil.getElementListByXpath(inXML, "/Order/HeaderTaxes/HeaderTax");
			for (Element eleHeaderTax : lHeaderTax) {
				double dTaxHT=SCXmlUtil.getDoubleAttribute(eleHeaderTax,ATTR_TAX , 0.00);
				dHeaderTaxTotal+=dTaxHT;
			}
			if(log.isDebugEnabled()){
				log.debug("HeaderTax total : "+dHeaderTaxTotal);
			}
			//calculating dLineTotal
			List<Element> lOrderLine=XMLUtil.getElementListByXpath(inXML, "/Order/OrderLines/OrderLine");
			for (Element eleOrderLine : lOrderLine) {
				double dOrderedQty=SCXmlUtil.getDoubleAttribute(eleOrderLine, ATTR_ORD_QTY, 0.00);
				Element eleLinePriceInfo=SCXmlUtil.getChildElement(eleOrderLine, ELE_LINE_PRICE_INFO);
				double dUnitPrice=SCXmlUtil.getDoubleAttribute(eleLinePriceInfo, ATTR_UNIT_PRICE, 0.00);
				double dLinePrice=dOrderedQty*dUnitPrice;
				dLineTotal+=dLinePrice;
				
				List<Element> lLineTax=XMLUtil.getElementListByXpath(eleOrderLine, "OrderLine/LineTaxes/LineTax");
				for (Element eleLineTax : lLineTax) {
					double dTaxLT=SCXmlUtil.getDoubleAttribute(eleLineTax,ATTR_TAX , 0.00);
					dLineTotal+=dTaxLT;
				}
				
				List<Element> lLineCharge=XMLUtil.getElementListByXpath(eleOrderLine, "OrderLine/LineCharges/LineCharge");
				for (Element eleLineCharge : lLineCharge) {
					double dChargeAmountLC=SCXmlUtil.getDoubleAttribute(eleLineCharge,ATTR_CHARGE_PER_LINE , 0.00);
					String strChargeCategory=eleLineCharge.getAttribute(ATTR_CHARGE_CATEGORY);
					if(log.isDebugEnabled()){
						log.debug("ChargePerLine : "+dChargeAmountLC + " ChargeCategory" + strChargeCategory + "Line total before adding the line charge: "+ dLineTotal);
					}
					if(DISCOUNT.equals(strChargeCategory))
						dLineTotal-=dChargeAmountLC;
					else
						dLineTotal+=dChargeAmountLC;
				}	
			} // end of OrderLine loop
			if(log.isDebugEnabled()){
				log.debug("Line total : "+dLineTotal);
			}
			dOrderTotal=dLineTotal+dHeaderChargeTotal+dHeaderTaxTotal;
			if(log.isDebugEnabled()){
				log.debug("Order total : "+dOrderTotal);
			}
			
			List<Element> lPaymentMethod=XMLUtil.getElementListByXpath(inXML, "/Order/PaymentMethods/PaymentMethod");
			for(Element elePaymentMethod:lPaymentMethod){
				double dMaxChargeLimit=SCXmlUtil.getDoubleAttribute(elePaymentMethod, ATTR_MAX_CHARGE_LIMIT, 0.00);
				dOrderMaxChargeLimit+=dMaxChargeLimit;
			}
			if(log.isDebugEnabled()){
				log.debug("MaxChargeLimit total is : " + dOrderMaxChargeLimit);
			}
			
			dDifference=dOrderTotal-dOrderMaxChargeLimit;
			dDifference=Math.round(dDifference*100D)/100D;
			if(log.isDebugEnabled()){
				log.debug("Difference is : "+dDifference);
			}
			if(dDifference>=0.01 && dDifference<=0.99){
				//difference is between 0.01 and 0.99 so adding a discount
				//<LineCharge ChargeAmount="0.01" ChargeCategory="Discount" ChargeName="OrderDifferenceDiscount" ChargePerLine="0.01" />
				Element eleLineCharges=XMLUtil.getElementByXPath(inXML, "/Order/OrderLines/OrderLine/LineCharges");
				if(eleLineCharges==null){
					Element eleOrderLine=XMLUtil.getElementByXPath(inXML, "/Order/OrderLines/OrderLine");
					eleLineCharges=SCXmlUtil.createChild(eleOrderLine, ELE_LINE_CHARGES);
				}
				Element eleLineCharge=SCXmlUtil.createChild(eleLineCharges, ELE_LINE_CHARGE);
				eleLineCharge.setAttribute(ATTR_CHARGE_AMOUNT, String.valueOf(dDifference));
				eleLineCharge.setAttribute(ATTR_CHARGE_CATEGORY, DISCOUNT);
				eleLineCharge.setAttribute(ATTR_CHARGE_NAME, "OrderDifferenceDiscount");
				eleLineCharge.setAttribute(ATTR_CHARGE_PER_LINE, String.valueOf(dDifference));
				
			}
			
		} catch (Exception e) {
			log.error("Error in VSIBeforeCreateOrderUEImpl:applyPennyDiscount" + e.getMessage());
		}
	}
	//OMS-1033 PennyDiscount : End
	
	//OMS-1092 : Start
	public void stampExtnAttributes(YFSEnvironment env,Document inXML){
		
		try {
			double dExtnShipmentCharge=0.00;
			double dExtnShipmentDiscount=0.00;
			double dExtnOrderDiscount=0.00;
			double dExtnOrderTax=0.00;
			
			Element eleOrder=inXML.getDocumentElement();
			Element eleExtn=SCXmlUtil.getChildElement(eleOrder, ELE_EXTN);
			
			// calculating  dHeaderChargeTotal
			List<Element> lHeaderCharge=XMLUtil.getElementListByXpath(inXML, "/Order/HeaderCharges/HeaderCharge");
			for (Element eleHeaderCharge : lHeaderCharge) {
				double dChargeAmountHC=SCXmlUtil.getDoubleAttribute(eleHeaderCharge,ATTR_CHARGE_AMOUNT , 0.00);
				//add the code for dExtnShipmentCharge, dExtnOrderDiscount,dExtnShipmentDiscount
				String strChargeCategoryHeader=eleHeaderCharge.getAttribute(ATTR_CHARGE_CATEGORY);
				if(log.isDebugEnabled()){
					log.debug("ChargeAmount : "+dChargeAmountHC + " ChargeCategory" + strChargeCategoryHeader);
				}
				if(SHIPPING_CHARGE_CTGY.equals(strChargeCategoryHeader)){
					dExtnShipmentCharge+=dChargeAmountHC;
				}
				else if(DISCOUNT.equals(strChargeCategoryHeader)){
					dExtnOrderDiscount+=dChargeAmountHC;
				}
			}
			if(log.isDebugEnabled()){
				log.debug("ExtnShipmentCharge : "+dExtnShipmentCharge);
			}
			dExtnShipmentCharge=Math.round(dExtnShipmentCharge*100D)/100D;
			eleExtn.setAttribute("ExtnShipmentCharge", String.valueOf(dExtnShipmentCharge));
			
			if(log.isDebugEnabled()){
				log.debug("ExtnOrderDiscount : "+dExtnOrderDiscount);
			}
			dExtnOrderDiscount=Math.round(dExtnOrderDiscount*100D)/100D;
			eleExtn.setAttribute("ExtnOrderDiscount", String.valueOf(dExtnOrderDiscount));
			
			// calculating  dExtnOrderTax
			List<Element> lHeaderTax=XMLUtil.getElementListByXpath(inXML, "/Order/HeaderTaxes/HeaderTax");
			for (Element eleHeaderTax : lHeaderTax) {
				double dTaxHT=SCXmlUtil.getDoubleAttribute(eleHeaderTax,ATTR_TAX , 0.00);
				dExtnOrderTax+=dTaxHT;
			}
			if(log.isDebugEnabled()){
				log.debug("ExtnOrderTax : "+dExtnOrderTax);
			}
			dExtnOrderTax=Math.round(dExtnOrderTax*100D)/100D;
			eleExtn.setAttribute("ExtnOrderTax", String.valueOf(dExtnOrderTax));
			
			//calculating dLineTotal
			List<Element> lOrderLine=XMLUtil.getElementListByXpath(inXML, "/Order/OrderLines/OrderLine");
			for (Element eleOrderLine : lOrderLine) {
				double dExtnLineTotal=0.00;
				double dExtnTotalTax=0.00;
				double dExtnTotalDiscount=0.00;
				double dTotalCharge=0.00;
				double dOrderedQty=SCXmlUtil.getDoubleAttribute(eleOrderLine, ATTR_ORD_QTY, 0.00);
				Element eleLinePriceInfo=SCXmlUtil.getChildElement(eleOrderLine, ELE_LINE_PRICE_INFO);
				double dUnitPrice=SCXmlUtil.getDoubleAttribute(eleLinePriceInfo, ATTR_UNIT_PRICE, 0.00);
				double dLinePrice=dOrderedQty*dUnitPrice;
				
				List<Element> lLineTax=XMLUtil.getElementListByXpath(eleOrderLine, "OrderLine/LineTaxes/LineTax");
				for (Element eleLineTax : lLineTax) {
					double dTaxLT=SCXmlUtil.getDoubleAttribute(eleLineTax,ATTR_TAX , 0.00);
					dExtnTotalTax+=dTaxLT;
				}
				
				List<Element> lLineCharge=XMLUtil.getElementListByXpath(eleOrderLine, "OrderLine/LineCharges/LineCharge");
				for (Element eleLineCharge : lLineCharge) {
					double dChargeAmountLC=SCXmlUtil.getDoubleAttribute(eleLineCharge,ATTR_CHARGE_PER_LINE , 0.00);
					String strChargeCategory=eleLineCharge.getAttribute(ATTR_CHARGE_CATEGORY);
					if(log.isDebugEnabled()){
						log.debug("ChargePerLine : "+dChargeAmountLC + " ChargeCategory" + strChargeCategory);
					}
					if(DISCOUNT.equals(strChargeCategory))
						dExtnTotalDiscount+=dChargeAmountLC;
					else
						dTotalCharge+=dChargeAmountLC;
				}
				
				Element eleOrderLineExtn=SCXmlUtil.getChildElement(eleOrderLine, ELE_EXTN);
				dExtnLineTotal=dLinePrice+dTotalCharge+dExtnTotalTax-dExtnTotalDiscount;
				dExtnLineTotal=Math.round(dExtnLineTotal*100D)/100D;
				if(log.isDebugEnabled()){
					log.debug("ExtnLineTotal : "+dExtnLineTotal);
				}
				eleOrderLineExtn.setAttribute("ExtnLineTotal", String.valueOf(dExtnLineTotal));
				
				dExtnTotalTax=Math.round(dExtnTotalTax*100D)/100D;
				if(log.isDebugEnabled()){
					log.debug("ExtnTotalTax : "+dExtnTotalTax);
				}
				eleOrderLineExtn.setAttribute("ExtnTotalTax", String.valueOf(dExtnTotalTax));
				
				dExtnTotalDiscount=Math.round(dExtnTotalDiscount*100D)/100D;
				if(log.isDebugEnabled()){
					log.debug("ExtnTotalDiscount : "+dExtnTotalDiscount);
				}
				eleOrderLineExtn.setAttribute("ExtnTotalDiscount", String.valueOf(dExtnTotalDiscount));
				
			} // end of OrderLine loop
			
			
		} catch (Exception e) {
			log.error("Error in VSIBeforeCreateOrderUEImpl:stampExtnAttributes" + e.getMessage());
		}
	}
	//OMS-1092: End
	//OMS- 1137 : Stamp Reservation : Start
	private void stampOrderLineReservation(YFSEnvironment env,Document inXML){
		if(log.isDebugEnabled()){
			log.debug("Input for stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
		}
		try {
			Element eleOrder=inXML.getDocumentElement();
			String strOrganizationCode=eleOrder.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strAllocationRuleID=eleOrder.getAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID);
			
			Element elePersonInfoShipTo=SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PERSON_INFO_SHIP_TO);
			
			NodeList nlOrderLine = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iLineCount=nlOrderLine.getLength();
			for (int i = 0; i < iLineCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				Element eleOrderLineReservations=SCXmlUtil.createChild(eleOrderLine, "OrderLineReservations");
				String strDeliveryMethod=eleOrderLine.getAttribute("DeliveryMethod");
				if(!YFCCommon.isStringVoid(strDeliveryMethod)&& "SHP".equals(strDeliveryMethod)){
					Document docReserveInvOP=reserveAvailableInvetory(env,eleOrderLine,elePersonInfoShipTo,strOrganizationCode,
							strAllocationRuleID);
					NodeList nlReservation=docReserveInvOP.getElementsByTagName("Reservation");
					int iReservationCount=nlReservation.getLength();
					for (int j = 0; j < iReservationCount; j++) {
						Element eleOrderLineReservation=SCXmlUtil.createChild(eleOrderLineReservations, "OrderLineReservation");
						Element eleReservation=(Element)nlReservation.item(j);
						eleOrderLineReservation.setAttribute("ReservationID", eleReservation.getAttribute("ReservationID"));
						eleOrderLineReservation.setAttribute("ItemID", eleReservation.getAttribute("ItemID"));
						eleOrderLineReservation.setAttribute("ProductClass", eleReservation.getAttribute("ProductClass"));
						eleOrderLineReservation.setAttribute("UnitOfMeasure", eleReservation.getAttribute("UnitOfMeasure"));
						eleOrderLineReservation.setAttribute("Quantity", eleReservation.getAttribute("ReservedQty"));
						eleOrderLineReservation.setAttribute("Node", eleReservation.getAttribute("ShipNode"));
						eleOrderLineReservation.setAttribute("RequestedReservationDate", eleReservation.getAttribute("ReservationNodeShipDate"));
					}
				}
				
			}
			if(log.isDebugEnabled()){
				log.debug("output from  stampOrderLineReservation : "+XMLUtil.getXMLString(inXML));
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	private Document reserveAvailableInvetory(YFSEnvironment env,Element eleOrderLine,
			Element elePersonInfoShipTo,String strOrganizationCode, String strAllocationRuleID){
		Document docReserveInvIP=null;
		Document docReserveInvOP=null;
		try {
			docReserveInvIP=SCXmlUtil.createDocument("Promise");
			Element elePromise=docReserveInvIP.getDocumentElement();
			elePromise.setAttribute(VSIConstants.ATTR_ORGANIZATION_CODE, strOrganizationCode);
			if(!YFCCommon.isStringVoid(strAllocationRuleID)){
				elePromise.setAttribute(VSIConstants.ATTR_ALLOCATION_RULE_ID, strAllocationRuleID);
			}
			Element eleReservationParameters=SCXmlUtil.createChild(elePromise, "ReservationParameters");
			
			String strSeqName = "VSI_SEQ_RESERVATION_ID";
	    	String strSeqNumber = VSIDBUtil.getNextSequence(env, strSeqName);
	    	eleReservationParameters.setAttribute("ReservationID", strSeqNumber);
	    	
	    	Element elePromiseLines=SCXmlUtil.createChild(elePromise, "PromiseLines");
	    	Element elePromiseLine=SCXmlUtil.createChild(elePromiseLines, "PromiseLine");
	    	
	    	Element eleItem=SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ITEM);
	    	elePromiseLine.setAttribute("LineId", "1");
	    	elePromiseLine.setAttribute("ItemID", eleItem.getAttribute(VSIConstants.ATTR_ITEM_ID));
	    	elePromiseLine.setAttribute("RequiredQty", eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY));
	    	elePromiseLine.setAttribute("UnitOfMeasure", eleItem.getAttribute(VSIConstants.ATTR_UOM));
	    	elePromiseLine.setAttribute("ProductClass", eleItem.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS));
	    	//elePromiseLine.setAttribute("DistributionRuleId", "VSI_DC");
	    	elePromiseLine.setAttribute("FulfillmentType", eleOrderLine.getAttribute(VSIConstants.ATTR_FULFILLMENT_TYPE));
	    	elePromiseLine.setAttribute("DeliveryMethod", eleOrderLine.getAttribute(VSIConstants.ATTR_DELIVERY_METHOD));
	    	String strShipNode=eleOrderLine.getAttribute(VSIConstants.ATTR_SHIP_NODE);
	    	if(!YFCCommon.isStringVoid(strShipNode))
	    		elePromiseLine.setAttribute(VSIConstants.ATTR_SHIP_NODE, strShipNode);
	    	
	    	Element eleShipToAddress=SCXmlUtil.createChild(elePromise, "ShipToAddress");
	    	eleShipToAddress.setAttribute("AddressLine1",  elePersonInfoShipTo.getAttribute("AddressLine1"));
	    	eleShipToAddress.setAttribute("City",  elePersonInfoShipTo.getAttribute("City"));
	    	eleShipToAddress.setAttribute("Country",  elePersonInfoShipTo.getAttribute("Country"));
	    	eleShipToAddress.setAttribute("DayPhone",  elePersonInfoShipTo.getAttribute("DayPhone"));
	    	eleShipToAddress.setAttribute("FirstName",  elePersonInfoShipTo.getAttribute("FirstName"));
	    	eleShipToAddress.setAttribute("LastName",  elePersonInfoShipTo.getAttribute("LastName"));
	    	eleShipToAddress.setAttribute("State",  elePersonInfoShipTo.getAttribute("State"));
	    	eleShipToAddress.setAttribute("ZipCode", elePersonInfoShipTo.getAttribute("ZipCode"));
	    	
	    	if(log.isDebugEnabled()){
	    		log.debug("input for reserveAvailableInventory API : "+SCXmlUtil.getString(docReserveInvIP));
	    	}
	    	
	    	//invoking reserveAvailableInventory 
	    	docReserveInvOP=VSIUtils.invokeAPI(env, "reserveAvailableInventory", docReserveInvIP);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		if(log.isDebugEnabled()){
			log.debug("reserveAvailableInvetory output : "+SCXmlUtil.getString(docReserveInvOP));
		}
		return docReserveInvOP;
	}
	
	//OMS-1137 : Stamp Reservation : End
	//OMS-1590: Start
		/**We are calling getCommonCodeList to fetch the list of KitLine Items.
		 * 
		 * @return
		 * @throws ParserConfigurationException 
		 * @throws YIFClientCreationException 
		 * @throws RemoteException 
		 * @throws YFSException 
		 */
		private Set<String> fetchKitLineItemList(YFSEnvironment env)  throws ParserConfigurationException, 
		YFSException, RemoteException, YIFClientCreationException {
			HashSet<String> setKitItemList=new HashSet<String>();
			Document docInput=XMLUtil.createDocument(ELE_COMMON_CODE);
			Element eleCommonCode = docInput.getDocumentElement();
			eleCommonCode.setAttribute(ATTR_CODE_TYPE, "VSI_KIT_LINE_ITEM");
			if(log.isDebugEnabled()){
				log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
			}
			Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInput);
			if(log.isDebugEnabled()){
				log.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
			}
			NodeList nlCommonCode = docCommonCodeListOP
					.getElementsByTagName(ELE_COMMON_CODE);
			int iCount=nlCommonCode.getLength();
			for (int i = 0; i < iCount; i++) {
				Element eleCommonCodeOutput=((Element) nlCommonCode.item(i));
				String strShortDesc=eleCommonCodeOutput.getAttribute(ATTR_CODE_VALUE);
				setKitItemList.add(strShortDesc);
			}
			return setKitItemList;
		}
		//OMS-1590: End
		//OMS-1689: Start : Marketplace order change
		/**We are calling getCommonCodeList to check if orde is an Marketplace order.
		 * 
		 * @return
		 * @throws ParserConfigurationException 
		 * @throws YIFClientCreationException 
		 * @throws RemoteException 
		 * @throws YFSException 
		 */
		private boolean isMarketPlaceOrder(YFSEnvironment env,String strEnterpriseCode)  throws ParserConfigurationException, 
		YFSException, RemoteException, YIFClientCreationException {
			Document docInput=XMLUtil.createDocument(ELE_COMMON_CODE);
			Element eleCommonCode = docInput.getDocumentElement();
			eleCommonCode.setAttribute(ATTR_CODE_TYPE, CODETYPE_MARKETPLACE_ENT);
			eleCommonCode.setAttribute(ATTR_CODE_VALUE, strEnterpriseCode);
			if(log.isDebugEnabled()){
			log.debug("Input for getCommonCodeList :" +XMLUtil.getXMLString(docInput));
			}
			Document docCommonCodeListOP=VSIUtils.invokeAPI(env,API_COMMON_CODE_LIST, docInput);
			if(log.isDebugEnabled()){
			log.debug("Output for getCommonCodeList :" +XMLUtil.getXMLString(docCommonCodeListOP));
			}
			Element commonCodeListElement = docCommonCodeListOP.getDocumentElement();
			if(commonCodeListElement.hasChildNodes()){
				return true;
			}
			return false;
		}
		//OMS-1689: End
}
