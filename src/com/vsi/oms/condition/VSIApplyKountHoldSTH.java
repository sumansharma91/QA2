package com.vsi.oms.condition;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIApplyKountHoldSTH implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIApplyKountHoldSTH.class);
	@Override
	/**
	 * Condition to apply KountHold on STH
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {

		boolean bApplyKountHold = false;
		boolean bCreditCardOrder = false;
		double dAmount=0;
		String strDTCOFlag=null;
		Element eleOrder = inXML.getDocumentElement();
		log.info("VSIApplyKountHoldSTH.evaluateCondition Input XML :" + SCXmlUtil.getString(inXML));
		log.info("==============Inside VSIApplyKountHoldSTH================================");
		if(!YFCObject.isNull(eleOrder)){

			try {
				//OMS-2332 Start
				Element elePaymentMethods = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_PAYMENT_METHODS);
				NodeList nlPaymentMethod = elePaymentMethods.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);
				for(int i=0; i<nlPaymentMethod.getLength(); i++){
					Element elePaymentMethod = (Element) nlPaymentMethod.item(i);
					String strPaymentType = elePaymentMethod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE);
					if(VSIConstants.PAYMENT_MODE_CC.equals(strPaymentType)){
						log.info("Credit Card is used in this Order");
						bCreditCardOrder=true;
						String strAmount = elePaymentMethod.getAttribute(VSIConstants.ATTR_MAX_CHARGE_LIMIT);
						if(!YFCCommon.isVoid(strAmount)){
							dAmount = Double.parseDouble(strAmount);
						}
						if((dAmount==0) || (dAmount==0.0) || (dAmount==0.00)){
							bCreditCardOrder=false;
							log.info("Credit Card Request Amount is zero");
						}						
						break;
					}
				}
				if(!bCreditCardOrder){
					log.info("Non Credit Card Order, hold will not be applied");
					log.info("VSIApplyFraudVerificationHold.evaluateCondition output: " +bApplyKountHold);
					return bApplyKountHold;
				}
				//OMS-2332 End
				//OMS-1707 start
				ArrayList<Element> listDTCOrderFlag;
				listDTCOrderFlag = VSIUtils.getCommonCodeList(env, "VSI_DTC_FLAG", "DTC", "DEFAULT");
				if(!listDTCOrderFlag.isEmpty()){
					Element eleCommonCode=listDTCOrderFlag.get(0);
					strDTCOFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strDTCOFlag)&&"Y".equalsIgnoreCase(strDTCOFlag)){

				//OMS-1707 End
				String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
				NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				Element eleOrderLine = (Element)orderLineList.item(0);	
				String lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);	
				Element elePriceInfo=XMLUtil.getFirstElementByName(eleOrder, VSIConstants.ELE_PRICE_INFO);
				NodeList nlPaymtMthd = eleOrder.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);

				Element elePaymentMthod = (Element) nlPaymtMthd.item(0);
				if(!YFCCommon.isStringVoid(strOrderType) && ((VSIConstants.ATTR_ORDER_TYPE_POS).equalsIgnoreCase(strOrderType) 
						||(VSIConstants.MARKETPLACE).equalsIgnoreCase(strOrderType)||(VSIConstants.WHOLESALE).equalsIgnoreCase(strOrderType)) ){
					//order is a POS or Marketplace order
					bApplyKountHold=false;
				}
				else if(elePriceInfo!=null && SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT)<=0.00
						&& !YFCCommon.isStringVoid(lineType) && (("SHIP_TO_HOME").equals(lineType) || ("PICK_IN_STORE").equals(lineType)|| ("SHIP_TO_STORE").equals(lineType))){
					//order is originated from call center
					
						bApplyKountHold=false;
					
				}
				else if (!YFCCommon.isStringVoid(strOrderType)&&!YFCCommon.isStringVoid(lineType)&&("PICK_IN_STORE".equals(lineType)||"SHIP_TO_STORE".equals(lineType))){

					bApplyKountHold=false;
				}
				//oms-2227 start

				
				else if (elePaymentMthod != null&&!YFCCommon.isStringVoid(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))
						&& (VSIConstants.PAYMENT_MODE_CASH.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))
								|| VSIConstants.PAYMENT_MODE_CHECK.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))||VSIConstants.STR_AR_CREDIT.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE)))) {

					bApplyKountHold=false;

				}
					//oms-2227 end
				else{
					bApplyKountHold=true;
				}
				// end of if for eleOrder
			}
				}
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new YFSException();
			}
		}
		log.info("VSIApplyKountHoldSTH.evaluateCondition output: " +bApplyKountHold );
		return bApplyKountHold;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
