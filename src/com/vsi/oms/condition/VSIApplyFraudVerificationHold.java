package com.vsi.oms.condition;

import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class VSIApplyFraudVerificationHold implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSIApplyFraudVerificationHold.class);
	@Override
	/**
	 * Condition to apply FraudVerificationHold
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {

		boolean bApplyFraudVerHold = true;
		boolean bCreditCardOrder = false;
		double dCCAmount=0;
		Element eleOrder = inXML.getDocumentElement();
		if(log.isDebugEnabled()){
			log.info("VSIApplyFraudVerificationHold.evaluateCondition Input XML :" + SCXmlUtil.getString(inXML));
			log.info("================Inside VSIApplyFraudVerificationHold================================");
		}
		if(!YFCObject.isNull(eleOrder)){
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
						dCCAmount = Double.parseDouble(strAmount);
					}
					if((dCCAmount==0) || (dCCAmount==0.0) || (dCCAmount==0.00)){
						bCreditCardOrder=false;
						log.info("Credit Card Request Amount is zero");
					}						
					break;
				}
			}
			if(!bCreditCardOrder){
				log.info("Non Credit Card Order, hold will not be applied");
				bApplyFraudVerHold=false;
				log.info("VSIApplyFraudVerificationHold.evaluateCondition output: " +bApplyFraudVerHold);
				return bApplyFraudVerHold;
			}
			//OMS-2332 End
			String strOrderType = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			String strEntryType=eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			NodeList orderLineList = inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			Element eleOrderLine = (Element)orderLineList.item(0);	
			String lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);	
			//OMS-1669 : Start
			boolean bFraudHNotAllowed=false;
			boolean bFraudHAllowed=false;
			String strAmount=null;
			Double dAmount=0.0;
			try {
			ArrayList<Element> listFraudHoldOrdType;
			listFraudHoldOrdType = VSIUtils.getCommonCodeList(env, "FRAUD_HOLD_ORD_TYPE", "BOPUS", "DEFAULT");
			
			if (listFraudHoldOrdType.isEmpty())
			{
				
				bFraudHNotAllowed=true;
				if(log.isDebugEnabled()){
					log.debug("CommonCode is not configured bFraudHNotAllowed"+bFraudHNotAllowed);
				}
			}
			else
			{	
				Element eleCommonCode=listFraudHoldOrdType.get(0);
				strAmount=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				dAmount=Double.parseDouble(strAmount);
				if(log.isDebugEnabled()){
					log.debug("dAmount"+dAmount);
				}
				if(dAmount==0.0)
				{
						bFraudHAllowed=true;
						if(log.isDebugEnabled()){
							log.debug("CommonCode is  configured with 0, bFraudHAllowed"+bFraudHAllowed);
						}
				}
				
			}
			Element elePriceInfo=XMLUtil.getFirstElementByName(eleOrder, VSIConstants.ELE_PRICE_INFO);
			NodeList nlPaymtMthd = eleOrder.getElementsByTagName(VSIConstants.ELE_PAYMENT_METHOD);

			Element elePaymentMthod = (Element) nlPaymtMthd.item(0);
			//OMS-1669 : End
			if(!YFCCommon.isStringVoid(strOrderType) && ((VSIConstants.ATTR_ORDER_TYPE_POS).equalsIgnoreCase(strOrderType) 
					||(VSIConstants.MARKETPLACE).equalsIgnoreCase(strOrderType) || (VSIConstants.WHOLESALE).equalsIgnoreCase(strOrderType)) ){
				//order is a POS or Marketplace order or wholesale order
				bApplyFraudVerHold=false;
			 }
			//OMS-1669 : Start
			else if(elePriceInfo!=null && SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT)<=0.00
						&& !YFCCommon.isStringVoid(lineType) && (("SHIP_TO_HOME").equals(lineType) || ("PICK_IN_STORE").equals(lineType)|| ("SHIP_TO_STORE").equals(lineType)) )
				{
					bApplyFraudVerHold=false;
				}
			//oms-2227 start


			else if (elePaymentMthod != null&&!YFCCommon.isStringVoid(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))
					&& (VSIConstants.PAYMENT_MODE_CASH.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))
							|| VSIConstants.PAYMENT_MODE_CHECK.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE))||VSIConstants.STR_AR_CREDIT.equals(elePaymentMthod.getAttribute(VSIConstants.ATTR_PAYMENT_TYPE)))) {

				bApplyFraudVerHold=false;

			}
				//oms-2227 end
			
			else if (!YFCCommon.isStringVoid(lineType)&&(("PICK_IN_STORE").equals(lineType)||("SHIP_TO_STORE").equals(lineType)))
			{
				if(bFraudHNotAllowed)
				{	
					bApplyFraudVerHold=false;
				}	
				else if(bFraudHAllowed==false)
				{
					if(dAmount>0.0 && elePriceInfo!=null &&  SCXmlUtil.getDoubleAttribute(elePriceInfo, VSIConstants.ATTR_TOTAL_AMOUNT) <dAmount)
						bApplyFraudVerHold=false;
				}
				
			}

		}
		catch (Exception e) {
				e.printStackTrace();
				throw new YFSException();
				//throw new RemoteException();
				//throw new YIFClientCreationException(sOrderDate);
		}
		//OMS-1669 : End		
		// end of if callcenter
		// end of if for eleOrder
		}
		if(log.isDebugEnabled()){
			log.info("VSIApplyFraudVerificationHold.evaluateCondition output:bApplyFraudVerHold " +bApplyFraudVerHold );
		}
		return bApplyFraudVerHold;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
