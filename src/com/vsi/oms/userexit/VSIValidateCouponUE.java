package com.vsi.oms.userexit;

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
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.ypm.japi.ue.YPMValidateCouponUE;

/**
 * @author Perficient Inc.
 * 
 * Coupon  UE
 *
 */
public class VSIValidateCouponUE  implements YPMValidateCouponUE {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIValidateCouponUE.class);
	YIFApi api;
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public Document validateCoupon(YFSEnvironment env, Document inXML) throws YFSUserExitException {
		// TODO Auto-generated method stub
		Document outdoc = null;
		if(log.isDebugEnabled()){
			log.info("================Inside VSIValidateCouponUE================================");
			log.debug("Printing Input XML :" + XMLUtil.getXMLString(inXML));
		}
		Element eleInput = inXML.getDocumentElement();
		String orderNo = "1234";
		String orderDate = null;
		String shipNode = "391";
		Calendar cal = Calendar.getInstance();
		String strCurrentDate = sdf.format(cal.getTime());
		try {
		if(eleInput != null){
			String ohk = eleInput.getAttribute("OrderReference");
			if(ohk != null && !ohk.trim().equalsIgnoreCase("")){
				Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
				getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, ohk);
				Document getOrderListOp = VSIUtils.invokeAPI(env, "global/template/api/getOrderList.xml", VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
				if(getOrderListOp != null){
					Element orderEle = (Element) getOrderListOp.getElementsByTagName(VSIConstants.ELE_ORDER).item(0);
					orderNo= orderEle.getAttribute(VSIConstants.ATTR_ORDER_NO);
					orderDate= orderEle.getAttribute(VSIConstants.ATTR_ORDER_DATE);
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
				String strValid = eleOut.getAttribute("Valid");
				if("N".equalsIgnoreCase(strValid))
					eleOut.setAttribute("CouponStatusMsgCode", "YPM_RULE_INVALID");
				else if("Y".equalsIgnoreCase(strValid))
					eleOut.setAttribute("CouponStatusMsgCode", "YPM_RULE_VALID");
				
				//1B Development for Call Center Order Capture starts here
				
				//Setting CouponStatusMsgCode="YPM_RULE_VALID" if ReferenceId="Override"
				Element eleReferences = SCXmlUtil.getChildElement(eleOut, VSIConstants.ELE_REFERENCES );
				if(!YFCObject.isNull(eleReferences)){
					Element eleReference = SCXmlUtil.getChildElement(eleReferences, VSIConstants.ELE_REFERENCE );
					if(!YFCObject.isNull(eleReference)){
						String strReferenceID = eleReference.getAttribute(VSIConstants.ATTR_REFERENCE_ID);
						if(!YFCObject.isNull(strReferenceID) && strReferenceID.equals("Override")){
							eleOut.setAttribute(VSIConstants.ATTR_COUPON_STATUS_MSG_CODE, "YPM_RULE_VALID");
							//System.out.println("Hello");
							
						}//end of if for checking string ReferenceID
						
					}//end of if for checking reference
					
				}//end of if checking for references
				
				//1B Development for Call Center Order Capture ends here
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
