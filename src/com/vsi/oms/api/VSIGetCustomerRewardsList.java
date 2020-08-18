package com.vsi.oms.api;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.InvokeWebservice;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCIterable;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.core.YFSSystem;
import com.yantra.yfs.japi.YFSEnvironment;

/**
 * This class will be invoked from the CustomerDetails page when user clicks on
 * Loyalty Details tab. Input will be <Customer CustomerNumber=""> out put will
 * contain the list of rewards which are active for the customer.
 */
public class VSIGetCustomerRewardsList implements YIFCustomApi, VSIConstants {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIGetCustomerRewardsList.class);

	@SuppressWarnings("static-access")
	public Document getRewardsList(YFSEnvironment env, Document inputDoc)
			throws SocketTimeoutException, IOException,
			ParserConfigurationException, SAXException {
		if(log.isDebugEnabled()){
    		log.debug("InputDocument:::::::::"
				+ SCXmlUtil.getString(inputDoc));
		}

		InvokeWebservice invokeWebservice = new InvokeWebservice();
		Document outDoc = null;
		YFCIterable<YFCElement> resultsItr = null;
		Document rewardsOutDoc = null;
		Element eleCustomerRewards = null;

		String strCustomerNo = inputDoc.getDocumentElement().getAttribute(
				"CustomerNumber");
		String inputString = "<coupon_request>" + "<request>RL</request>"
				+ "<CustNumber>" + strCustomerNo + "</CustNumber>"
				+ "</coupon_request>";
		String rewardsEndPointURL = YFSSystem.getProperty("COUPONS_URL");
		// System.out.println("rewardsEndPointURL"+rewardsEndPointURL);

		outDoc = invokeWebservice.invokeURL(rewardsEndPointURL, inputString);
		if(log.isDebugEnabled()){
    		log.debug("outDoc" + SCXmlUtil.getString(outDoc));
		}

		resultsItr = YFCDocument.getDocumentFor(outDoc).getDocumentElement()
				.getChildren("coupon_code");
		rewardsOutDoc = XMLUtil.createDocument(ELE_CUSTOMER);
		eleCustomerRewards = rewardsOutDoc.getDocumentElement();
		eleCustomerRewards.setAttribute("CustomerNumber",
				YFCDocument.getDocumentFor(outDoc).getDocumentElement()
						.getChildElement("CustNumber").getNodeValue());
		Element eleRewardList = rewardsOutDoc.createElement("RewardsList");
		eleCustomerRewards.appendChild(eleRewardList);
		while (resultsItr.hasNext()) {
			YFCElement eleCouponCode = resultsItr.next();
			String strRewardId = eleCouponCode.getChildElement("coupon")
					.getNodeValue();
			String strAmount = eleCouponCode.getChildElement("amount")
					.getNodeValue();
			String strExpirationDate = eleCouponCode.getChildElement(
					"expiration").getNodeValue();
			String strStatus = eleCouponCode.getChildElement("status")
					.getNodeValue();
			String strIsReward = eleCouponCode.getChildElement("isReward")
					.getNodeValue();
			if (FLAG_Y.equalsIgnoreCase(strIsReward)) {
				Element eleReward = rewardsOutDoc.createElement("Reward");
				eleRewardList.appendChild(eleReward);
				eleReward.setAttribute("RewardId", strRewardId);
				eleReward.setAttribute("RewardAmount", strAmount);
				eleReward.setAttribute("ExpirationDate", strExpirationDate);
				eleReward.setAttribute("IsActive", strStatus);
			}
		}

		if(log.isDebugEnabled()){
    		log.debug("rewardsOutDoc" + SCXmlUtil.getString(rewardsOutDoc));
		}
		return rewardsOutDoc;
	}

	@Override
	public void setProperties(Properties arg0) throws Exception {
		// TODO Auto-generated method stub

	}
}
