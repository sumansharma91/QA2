package com.vsi.oms.userexit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.pca.ycd.japi.ue.YCDEmailOrderUE;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSUserExitException;

/**
 * 
 * @author IBM
 */
public class VSIEmailOrderUE implements YCDEmailOrderUE, VSIConstants {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIEmailOrderUE.class);

	/**
	 * Calls the correct email service based on the delivery method, PICK or SHP
	 * 
	 * Input: 
	 * <Order DeliveryMethod="" DocumentType="0001" EnterpriseCode=""
	 * 	OrderHeaderKey="" OrderNo="" To="<email address>"/>
	 * 
	 * @param arg0
	 * @param arg1
	 * @return
	 * @throws YFSUserExitException
	 */
	@Override
	public Document emailOrder(YFSEnvironment env, Document docInput) throws YFSUserExitException {
		log.beginTimer("VSIEmailOrderUE.emailOrder() : START");
		if(log.isDebugEnabled()){
			log.debug("EmailOrder input doc:\n" + SCXmlUtil.getString(docInput));
		}
		
		Element eleOrderIn = docInput.getDocumentElement();
		try {
			if (ATTR_DEL_METHOD_SHP.equals(eleOrderIn.getAttribute(ATTR_DELIVERY_METHOD))) {
				VSIUtils.invokeService(env, SERVICE_VSI_STH_ORDER_CONFIRMATION, docInput);
			} else {
				Document docGetOrderList = SCXmlUtil.createDocument(ELE_ORDER);
				Element eleGetOrderList = docGetOrderList.getDocumentElement();
				eleGetOrderList.setAttribute(ATTR_ORDER_HEADER_KEY, eleOrderIn.getAttribute(ATTR_ORDER_HEADER_KEY));
				Document docOrderList = VSIUtils.invokeAPI(env,"global/template/api/VSIConfEmailOrderTemplate.xml", API_GET_ORDER_LIST, docGetOrderList);
				Element eleOrderList = docOrderList.getDocumentElement();
				Element eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
				eleOrder.setAttribute(ATTR_CUSTOMER_EMAIL_ID, eleOrderIn.getAttribute(ATTR_TO));
				
				VSIUtils.invokeService(env, SERVICE_VSI_BOPS_EMAIL_ORDER, docOrderList);
			}
		} catch (Exception e) {
			log.error("Exception in VSIEmailOrderUE.emailOrder() : " + e.getMessage());
			throw VSIUtils.getYFSException(e);
		}

		log.endTimer("VSIEmailOrderUE.emailOrder() : START");
		return docInput;
	}

} // End of VSIGetCustomerDetails Class
