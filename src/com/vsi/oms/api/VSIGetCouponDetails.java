package com.vsi.oms.api;

import java.util.Properties;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIGetCouponDetails implements YIFCustomApi {

	private YFCLogCategory log = YFCLogCategory.instance(VSIGetCouponDetails.class);
	YIFApi api;

	public Document invoke(YFSEnvironment env, Document inDoc) throws NumberFormatException {
		if(log.isDebugEnabled()){
			log.debug("InDoc" + XMLUtil.getXMLString(inDoc));
		}
		Element eleCoupon = inDoc.getDocumentElement();
		String strCouponID = eleCoupon.getAttribute("CouponID");
		Document outDoc = null;

		
		String strRequestXml = "<coupon_request>" + "<request>LU</request>" + "<coupon>" + strCouponID + "</coupon>"
				+ "</coupon_request>";
		if(log.isDebugEnabled()){
			log.debug("RequestXML" + strRequestXml);
		}
		YFCDocument yfcDocRequest=YFCDocument.getDocumentFor(strRequestXml);
		if(log.isDebugEnabled()){
			log.debug("docRequest"+yfcDocRequest);
		}
		Document docRequest=yfcDocRequest.getDocument();
		
		String url = YFSSystem.getProperty("COUPONS_URL");
		try {

			outDoc = InvokeWebservice.invokeURL(url, XMLUtil.serialize(docRequest, "iso-8859-1", false));
			YFCElement eleOutDoc = YFCDocument.getDocumentFor(outDoc).getDocumentElement();
			if(log.isDebugEnabled()){
				log.debug("eleOutDoc: "+eleOutDoc);
			}
			String strIsVal = eleOutDoc.getChildElement("valid").getNodeValue();
			if(log.isDebugEnabled()){
				log.debug("strIsVal"+strIsVal);
			}
			String strIsError = eleOutDoc.getChildElement("errcode").getNodeValue();
			if ("N".compareToIgnoreCase(strIsVal) == 0 && !("003".equals(strIsError))) {
				eleCoupon.setAttribute("IsCallSuccessfull", "N");
				if(log.isDebugEnabled()){
					log.debug("docResponse: "+XMLUtil.getXMLString(inDoc));
				}
				return inDoc;
			}
			if ("003".equals(strIsError)) {
				eleCoupon.setAttribute("IsExpired", "Y");
			}
		
			String strError = eleOutDoc.getChildElement("error").getNodeValue();
			if(log.isDebugEnabled()){
				log.debug("strError:"+strError);
			}
			String strIssuedTo = eleOutDoc.getChildElement("issued_to").getNodeValue();
			String strCampaign = eleOutDoc.getChildElement("campaign").getNodeValue();
			String strCampaignDesc = eleOutDoc.getChildElement("campaign_description").getNodeValue();
			String strStartDate = eleOutDoc.getChildElement("start_date").getNodeValue();
			String strEndDate = eleOutDoc.getChildElement("end_date").getNodeValue();
			String strCampaignType = eleOutDoc.getChildElement("campaign_type").getNodeValue();
			String strCampaignTypeDesc = eleOutDoc.getChildElement("campaign_typedesc").getNodeValue();
			String strCampaignChnlDesc = eleOutDoc.getChildElement("campaign_chnldesc").getNodeValue();
			String strUsedCount = eleOutDoc.getChildElement("used_count").getNodeValue();
			String strPromotion = eleOutDoc.getChildElement("promotion").getNodeValue();
			String strPromoDesc = eleOutDoc.getChildElement("promo_dsc").getNodeValue();
			String strIsReward = eleOutDoc.getChildElement("isreward").getNodeValue();
			eleCoupon.setAttribute("Valid", strIsVal);
			eleCoupon.setAttribute("IsCallSuccessfull", "Y");
			//eleCoupon.setAttribute("ErrorCode",strIsError);
			eleCoupon.setAttribute("Reason",strError);
			//eleCoupon.setAttribute("IssuedTo",strIssuedTo);
			//eleCoupon.setAttribute("Campaign",strCampaign);
			//eleCoupon.setAttribute("CampaignDesc",strCampaignDesc);
			eleCoupon.setAttribute("StartDate",strStartDate);
			eleCoupon.setAttribute("EndDate",strEndDate);
			//eleCoupon.setAttribute("CampaignType",strCampaignType);
			//eleCoupon.setAttribute("CampaignTypeDesc",strCampaignTypeDesc);
			//eleCoupon.setAttribute("CampaignChnlDesc",strCampaignChnlDesc);
			//eleCoupon.setAttribute("UsedCount",strUsedCount);
			eleCoupon.setAttribute("PromoId",strPromotion);
			eleCoupon.setAttribute("PromoDesc",strPromoDesc);
			//eleCoupon.setAttribute("IsReward",strIsReward);
			
			
			if(log.isDebugEnabled()){
				log.debug("ResponseDoc: " + XMLUtil.getXMLString(inDoc));
			}
			return inDoc;


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e);
			eleCoupon.setAttribute("IsCallSuccessfull", "N");
			log.error("docResponse: "+XMLUtil.getXMLString(inDoc));
			return inDoc;
		}
		
		
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}

}
