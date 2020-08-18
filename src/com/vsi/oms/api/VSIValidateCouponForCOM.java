package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

/**
 * @author Perficient Inc.
 * 
 * Coupon  UE
 *
 */
public class VSIValidateCouponForCOM   {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIValidateCouponForCOM.class);
	YIFApi api;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public Document validateCoupon(YFSEnvironment env, Document inXML) throws YFSException {
		// TODO Auto-generated method stub
		Document outdoc = null;
		if(log.isDebugEnabled()){
			log.info("================Inside VSIValidateCouponUE================================");
			log.debug("Printing Input XML :" + XMLUtil.getXMLString(inXML));
		}
		Element eleInput = inXML.getDocumentElement();
		String orderNo = "1234";
		//String orderDate = null;
		String shipNode = "391";
		String strCustomerID = "1524";
		Calendar cal = Calendar.getInstance();
		String strCurrentDate = sdf.format(cal.getTime());
		try {
		if(eleInput != null){
			String ohk = eleInput.getAttribute("OrderReference");
			if(!YFCObject.isVoid(ohk)){
				Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/getOrderList.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				if(getOrderListOp != null){
					Element orderEle = (Element) getOrderListOp.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
					orderNo= orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
					//orderDate= orderEle.getAttribute(VSIConstants.ATTR_ORDER_DATE);
					strCustomerID = orderEle.getAttribute("BillToID");
					Element orderLineEle = (Element) getOrderListOp.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
					shipNode= orderLineEle.getAttribute(VSIConstants.ATTR_SHIP_NODE);
					if(YFCCommon.isVoid(shipNode))
					{
						shipNode = orderEle.getAttribute(VSIConstants.ATTR_ENTERED_BY);
					}
				}
			}
		}
		eleInput.setAttribute(VSIConstants.ATTR_ORDER_NO, orderNo);
		eleInput.setAttribute(VSIConstants.ATTR_SHIP_NODE, shipNode);
		eleInput.setAttribute(VSIConstants.ATTR_ORDER_DATE, strCurrentDate);
		if(!YFCObject.isVoid(strCustomerID))
			eleInput.setAttribute("CustomerID", strCustomerID);
		
			api = YIFClientFactory.getInstance().getApi();
		} catch (YIFClientCreationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			if(log.isDebugEnabled()){
				log.debug("Printing Input XML :" + XMLUtil.getXMLString(inXML));
			}
			outdoc = api.executeFlow(env, "VSIValidateCoupon",inXML);
			Element eleOut = outdoc.getDocumentElement();
			
			if(!YFCObject.isVoid(eleOut)){
				String strValid = eleOut.getAttribute("IsCouponValid");
				String strExpired = eleOut.getAttribute("IsExpired");
				if("N".equalsIgnoreCase(strValid)){
					eleOut.setAttribute("CouponStatusMsgCode", "YPM_RULE_INVALID");
					eleOut.setAttribute("Valid", "N");
				}else if("Y".equalsIgnoreCase(strValid)){
					Element elePromotion = (Element)eleOut.getElementsByTagName("Promotion").item(0);
					String strPromotionId = elePromotion.getAttribute("PromotionId");
//					Document docPricingRuleListIp = null;
//					try {
//						docPricingRuleListIp = XMLUtil.createDocument("PricingRule");
//					} catch (ParserConfigurationException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//					Element elePricingRuleListIp = docPricingRuleListIp.getDocumentElement();
//					Element eleCoupon = docPricingRuleListIp.createElement("Coupon");
//					eleCoupon.setAttribute("CouponID", strPromotionId);
//					elePricingRuleListIp.appendChild(eleCoupon);
//					try {
//						Document getPricingRuleListOp = VSIUtils.invokeAPI(env, "", "getPricingRuleList", docPricingRuleListIp);
//						if(!YFCObject.isVoid(getPricingRuleListOp)){
//							Element elePricingRuleListOp = getPricingRuleListOp.getDocumentElement();
//							if(!YFCObject.isVoid(elePricingRuleListOp)){
//								Element elePricingRuleOp = (Element)elePricingRuleListOp.getElementsByTagName("PricingRule").item(0);
//								String strPricingRuleKey = elePricingRuleOp.getAttribute("PricingRuleKey");
//								Element elePricingRules = outdoc.createElement("PricingRules");
//								Element elePricingRule = outdoc.createElement("PricingRule");
//								elePricingRule.setAttribute("PricingRuleKey", strPricingRuleKey);
//								elePricingRules.appendChild(elePricingRule);
//								eleOut.appendChild(elePricingRules);
//							}
//						}
//					} catch (YFSException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (YIFClientCreationException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
					eleOut.setAttribute("Valid", "Y");
					eleOut.setAttribute("CouponStatusMsgCode", "YPM_RULE_VALID");
					eleOut.setAttribute("CouponID", strPromotionId);
				}
				//Setting CouponStatusMsgCode="YPM_RULE_VALID" if ReferenceId="Override"
				Element eleReferences = SCXmlUtil.getChildElement(eleOut, VSIConstants.ELE_REFERENCES );
				if(!YFCObject.isNull(eleReferences)){
					Element eleReference = SCXmlUtil.getChildElement(eleReferences, VSIConstants.ELE_REFERENCE );
					if(!YFCObject.isNull(eleReference)){
						String strReferenceID = eleReference.getAttribute(VSIConstants.ATTR_REFERENCE_ID);
						if(!YFCObject.isNull(strReferenceID) && strReferenceID.equals("Override") && "Y".equals(strExpired)){
							eleOut.setAttribute("Valid", "Y");
							Element elePromotion = (Element)eleOut.getElementsByTagName("Promotion").item(0);
							String strPromotionId = elePromotion.getAttribute("PromotionId");
							eleOut.setAttribute("CouponID", strPromotionId);
							eleOut.setAttribute(VSIConstants.ATTR_COUPON_STATUS_MSG_CODE, "YPM_RULE_VALID");
							//System.out.println("Hello");
							
						}//end of if for checking string ReferenceID
						
					}//end of if for checking reference
					
				}//end of if checking for references
				
			}
			return outdoc;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new YFSException(
					"EXTN_ERROR",
					"EXTN_ERROR",
			"Coupon  Not found");
		}


		
	}	 



}
