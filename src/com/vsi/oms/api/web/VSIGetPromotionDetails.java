package com.vsi.oms.api.web;

import java.util.HashMap;
import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.vsi.oms.api.order.VSITransferOrderCreateShipmentBR2;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetPromotionDetails {
	private YFCLogCategory log = YFCLogCategory.instance(VSITransferOrderCreateShipmentBR2.class);
	YIFApi api;
	private Properties props;
	//YIFApi newApi;
	//YIFApi shipApi;
	/**
	 * 
	 * @param env
	 * 	Required. Environment handle returned by the createEnvironment
	 * 	API. This is required for the user exit to either raise errors
	 * 	or access environment information.
	 * @param inXML
	 * 	Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *   This exception is thrown whenever system errors that the
	 *   application cannot handle are encountered. The transaction is
	 *   aborted and changes are rolled back.
	 */
	public Document getPromotionDetails(YFSEnvironment env, Document inXML)
	throws Exception {

		try{
			
			Element rootElement = inXML.getDocumentElement();
			NodeList nHeaderPromotion = XMLUtil.getNodeListByXpath(inXML, "Order/Promotions/Promotion");
			HashMap<Integer,String> mHeaderPromo = new HashMap<Integer, String>();

			for (int k = 0; k < nHeaderPromotion.getLength(); k++) {
				Element elePromotion = (Element) nHeaderPromotion.item(k);
				String promotionID=elePromotion.getAttribute("PromotionId");
				mHeaderPromo.put(k, promotionID);
			}
			


			NodeList nPromotionLine = inXML.getElementsByTagName(VSIConstants.ELE_PROMOTION);
			for (int i = 0; i < nPromotionLine.getLength(); i++) {
				Element elePromotion = (Element) nPromotionLine.item(i);
				String promotionID=elePromotion.getAttribute("PromotionId");
				
				Element elePromoExtn=(Element)elePromotion.getElementsByTagName("Extn").item(0);
				//String origPromoDesc=elePromoExtn.getAttribute("ExtnDescription");
				Document createPricingRuleInput = XMLUtil.createDocument("PricingRule");
				Element createPricingRuleRootElement = createPricingRuleInput.getDocumentElement();
				createPricingRuleRootElement.setAttribute(VSIConstants.ATTR_ORG_CODE, "VSI-Cat");
				Element couponEle = XMLUtil.appendChild(createPricingRuleInput, createPricingRuleRootElement, "Coupon", "");
				couponEle.setAttribute("CouponID", promotionID);
				env.setApiTemplate("getPricingRuleList", "global/template/api/getPricingRule.xml");

				api = YIFClientFactory.getInstance().getApi();
				Document priceRuleDoc = api.invoke(env,"getPricingRuleList",createPricingRuleInput);
				env.clearApiTemplates();

				Element pricingRuleEle = (Element) priceRuleDoc.getElementsByTagName("PricingRule").item(0);
				Element pricingRuleActionEle = (Element) priceRuleDoc.getElementsByTagName("PricingRuleAction").item(0);
                String strChargeName=pricingRuleActionEle.getAttribute("ChargeName");
				String promotionDescription=pricingRuleEle.getAttribute("Description");
			
				NodeList nLineCharge = inXML.getElementsByTagName("LineCharge");
				for (int k = 0; k < nLineCharge.getLength(); k++) {
					Element eleLineCharge = (Element) nLineCharge.item(k);
					String chargeName=eleLineCharge.getAttribute("ChargeName");
					Element eExtn=(Element)eleLineCharge.getElementsByTagName("Extn").item(0);
					String chargeId=eExtn.getAttribute("ExtnChargeId");
					if(chargeId.equalsIgnoreCase(promotionID)){
						if(!YFCObject.isVoid(strChargeName)){
							eleLineCharge.setAttribute("ChargeName", strChargeName);

						}
						else{
							eleLineCharge.setAttribute("ChargeName", promotionDescription);

						}
						if(promotionDescription.contains("BOGO")){
							eExtn.setAttribute("ExtnDiscType", "MXMH");
							eExtn.setAttribute("ExtnType", "MXMH");
							eleLineCharge.setAttribute("ChargeName", promotionDescription);

						}
						if(mHeaderPromo.containsValue(promotionID)){
							eExtn.setAttribute("IsTranLevel", "Y");
						}
					}

					
				}
				
				

				
			}
            
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return inXML;
	}
}
