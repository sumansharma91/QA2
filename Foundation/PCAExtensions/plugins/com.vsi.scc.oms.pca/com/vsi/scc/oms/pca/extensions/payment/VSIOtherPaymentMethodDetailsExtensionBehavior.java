package com.vsi.scc.oms.pca.extensions.payment;

/**
 * Created on Aug 4,2014
 *
 */

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.w3c.dom.Element;

import com.yantra.util.YFCUtils;
import com.yantra.yfc.rcp.YRCApiContext;
import com.yantra.yfc.rcp.YRCExtentionBehavior;
import com.yantra.yfc.rcp.YRCPlatformUI;
import com.yantra.yfc.rcp.YRCValidationResponse;
import com.yantra.yfc.rcp.YRCXmlUtils;
import com.yantra.yfc.util.YFCDate;
import com.yantra.yfc.util.YFCDateUtils;


public class VSIOtherPaymentMethodDetailsExtensionBehavior extends YRCExtentionBehavior {
	String FORM_ID = "com.yantra.pca.ycd.rcp.tasks.payment.screens.YCDOtherPaymentMethodDetails";
	public String strCurrency = "";
	String strEnterpriseCode = "";
	String strOrderNo = "";
	String strOrderHeaderKey = "";
	String strSVCNo = "";
	String strExpYear = "";
	/**
	 * This method initializes the behavior class.
	 */
	public void init() {
		// checking for permission to enable the view pan button
		if (YRCPlatformUI.hasPermission("ycdRCP000711")||YRCPlatformUI.hasPermission("SOPSOM0712")) {
			
		}

		super.init();
	}


	public void handleApiCompletion(YRCApiContext ctxApi) {
		String apiName = ctxApi.getApiName();
		 if ("VSICheckVoucher"
				.equalsIgnoreCase(apiName)) {
			Element eleOutput = ctxApi.getOutputXml().getDocumentElement();
			if(!YRCPlatformUI.isVoid(eleOutput)){
				String strIsValid = eleOutput.getAttribute("isValid");
				Date date = new Date();
				String strDate = null;
				String strDay = null;
				if(!YRCPlatformUI.isVoid(eleOutput.getAttribute("RedeemDate"))){
					String strRedemptionDate = eleOutput.getAttribute("RedeemDate");
					
					DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
					try {
						date = formatter.parse(strRedemptionDate);
					    Calendar cal = Calendar.getInstance();
					    cal.setTime(date);
					    Locale locale = Locale.getDefault();
					    strDay = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, locale);
						SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
						strDate = sdf.format(date) + "  ( "+strDay+" )" ;
					} catch (ParseException e1) {
						e1.printStackTrace();
					}
				}

				if("Y".equalsIgnoreCase(strIsValid)){
					String strRedeemStatus = eleOutput.getAttribute("RedeemStatus");
					if(!YRCPlatformUI.isVoid(strRedeemStatus)){
						String strIssueAmount = eleOutput.getAttribute("IssueAmount");
						if(!YRCPlatformUI.isVoid(strIssueAmount)){
							setFieldValue("txtMaxChargeLimit", strIssueAmount);
							setControlEditable("txtMaxChargeLimit", false);
						}
					}else{
						YRCPlatformUI.showError("Error", "This Voucher cannot be retrieved");
						setFieldValue("txtMaxChargeLimit", "");
						setControlEditable("txtMaxChargeLimit", false);
					}
				}else{
					/* Preetham Quarterly Vouchers Code Change - start*/
					String strComments = eleOutput.getAttribute("Comments");
					String strIssueAmount = eleOutput.getAttribute("IssueAmount");
					if(!YRCPlatformUI.isVoid(strComments)){
						if(strComments.equalsIgnoreCase("QUARTERLY EXPIRATION")){
							YRCPlatformUI.showError("Error", "This is a Quarterly Voucher and it has expired.\nThe Voucher had a value of $" + strIssueAmount);
							setFieldValue("txtMaxChargeLimit", "");
							setControlEditable("txtMaxChargeLimit", false);
						}else{
							if(!YRCPlatformUI.isVoid(strDate)){
								YRCPlatformUI.showError("Error", "This Voucher was redeemed on "+strDate);	
							}
							setFieldValue("txtMaxChargeLimit", "");
							setControlEditable("txtMaxChargeLimit", false);
						}
					}else{
						if(!YRCPlatformUI.isVoid(strDate)){
							YRCPlatformUI.showError("Error", "This Voucher was redeemed on "+strDate);	
						}
						setFieldValue("txtMaxChargeLimit", "");
						setControlEditable("txtMaxChargeLimit", false);
					}					
					//YRCPlatformUI.showError("Error", "Invalid Voucher");
					/* Preetham Quarterly Vouchers Code Change - end*/
				}
				
			}
			
		}
		super.handleApiCompletion(ctxApi);
	}


	public YRCValidationResponse validateButtonClick(String fieldName) {
		if(fieldName.equals("btnConfirm")){			
			
		} 

		return super.validateButtonClick(fieldName);
	}


	public YRCValidationResponse validateTextField(String strFieldName, String svalue) {
	// TODO Auto-generated method stub
		if(("txtRef1").equalsIgnoreCase(strFieldName)){			
			if(!YRCPlatformUI.isVoid(svalue)){
				callVoucherBalanceService(svalue);
			}
		} 
		return super.validateTextField(strFieldName, svalue);
	}


	private void callVoucherBalanceService(String strCertificateNo) {
		Element eleVoucherBalanceInput = YRCXmlUtils.createFromString("<Voucher />").getDocumentElement();		
		eleVoucherBalanceInput.setAttribute("CertNo",strCertificateNo);
		YRCApiContext context = new YRCApiContext();
		context.setApiName("VSICheckVoucher");
		context.setFormId(FORM_ID);
		context.setInputXml(eleVoucherBalanceInput.getOwnerDocument());
		//context.setUserData("CodeName", strCodeType);
		callApi(context);
		
	}


	

}