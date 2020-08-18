package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfc.util.YFCException;

public class VSIWholesaleChangeException implements VSIConstants{
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIWholesaleChangeException.class);
	
	public Document changeAlert(YFSEnvironment env, Document inXML) throws Exception{
		
		log.beginTimer("VSIWholesaleChangeException.changeAlert()");
		Element eleInboxList = inXML.getDocumentElement();
		Element eleInbox = SCXmlUtil.getChildElement(eleInboxList, ELE_INBOX);
		String strStatus = SCXmlUtil.getAttribute(eleInbox, ATTR_STATUS);
		if(!YFCObject.isVoid(strStatus) && strStatus.equalsIgnoreCase(ALERT_STATUS_CLOSED)){
			
			String strExceptionType = SCXmlUtil.getAttribute(eleInbox, ATTR_EXCEPTION_TYPE);
			if(!YFCObject.isVoid(strExceptionType) && strExceptionType.trim().equalsIgnoreCase("VSI_WH_RETURN_RECEIVED_ALERT")){
				
				String loginid = env.getUserId();
				Document getUserHierarchyInXML = SCXmlUtil.createDocument(ELE_USER);
				getUserHierarchyInXML.getDocumentElement().setAttribute(ATTR_LOGIN_ID, loginid);
				Document getUserHierarchyOutXML = VSIUtils.invokeService(env, "VSIWholesaleGetUserHierarchy", getUserHierarchyInXML);
				
				if(log.isDebugEnabled()){
					log.debug("Output of getUserHierarchy: " + SCXmlUtil.getString(getUserHierarchyOutXML));
				}
				
				NodeList nlUserGroup = getUserHierarchyOutXML.getElementsByTagName(ATTR_USER_GROUP);
				Boolean isWholesaleManager = false;
				Boolean isSystemUser = false;
				
				for(int i = 0; i < nlUserGroup.getLength(); i++){
					
					Element eleUserGroup = (Element) nlUserGroup.item(i);
					String strUserGroupID = SCXmlUtil.getAttribute(eleUserGroup, ATTR_USER_GROUP_ID);
					if(strUserGroupID.trim().equalsIgnoreCase("VSI_WHOLESALE_GROUP")){
						isWholesaleManager = true;
					}else if(strUserGroupID.trim().equalsIgnoreCase("SYSTEM")){
						isSystemUser = true;
					}
				}
				
				if(!isWholesaleManager && !isSystemUser){
					log.error("User: " + loginid + " cannot close this alert");
					YFCException e = new YFCException();
					e.setAttribute(ATTR_ERROR_CODE, "EXTNWH01");
					e.setAttribute(ATTR_ERROR_DESC, "This user is not allowed to close this alert");
					e.setAttribute(ATTR_USER_ID, loginid);
					e.setAttribute(ATTR_EXCEPTION_TYPE, strExceptionType);
					throw e;
				}
				
				String strOrderHeaderKey = SCXmlUtil.getAttribute(eleInbox, ATTR_ORDER_HEADER_KEY);				
				
				if(!YFCObject.isVoid(strOrderHeaderKey)){
					
					Document getOrderListInXML = SCXmlUtil.createDocument(ELE_ORDER);
					SCXmlUtil.setAttribute(getOrderListInXML.getDocumentElement(), ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
					Document getOrderListOutXML = VSIUtils.invokeService(env, SERVICE_GET_ORDER_LIST, getOrderListInXML);
					
					Element eleOrderList = getOrderListOutXML.getDocumentElement();
					Element eleOrder = SCXmlUtil.getChildElement(eleOrderList, ELE_ORDER);
					String strMinOrderStatus = SCXmlUtil.getAttribute(eleOrder, ATTR_MIN_ORDER_STATUS);
					String strMaxOrderStatus = SCXmlUtil.getAttribute(eleOrder, ATTR_MAX_ORDER_STATUS);
					if(!strMinOrderStatus.equalsIgnoreCase(STATUS_CANCEL) && !strMaxOrderStatus.equalsIgnoreCase(STATUS_CANCEL)){
						
						if(!strMinOrderStatus.equalsIgnoreCase("3950")){

							if(!strMinOrderStatus.equalsIgnoreCase("3950.10")){
								log.error("Invalid status to close alert");
								YFCException e = new YFCException();
								e.setAttribute(ATTR_ERROR_CODE, "EXTNWH02");
								e.setAttribute(ATTR_ERROR_DESC, "This alert cannot be closed unless the return is fully received.");
								e.setAttribute(ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
								e.setAttribute(ATTR_ORDER_NO, SCXmlUtil.getAttribute(eleOrder, ATTR_ORDER_NO));
								e.setAttribute(ATTR_ENTERPRISE_CODE, SCXmlUtil.getAttribute(eleOrder, ATTR_ENTERPRISE_CODE));
								e.setAttribute(ATTR_EXCEPTION_TYPE, strExceptionType);
								throw e;
							}

							// call changeOrderStatus
							Document changeOrderStatusInXML = SCXmlUtil.createDocument(ELE_ORDER_STATUS_CHANGE);
							Element eleOrderStatusChange = changeOrderStatusInXML.getDocumentElement();
							SCXmlUtil.setAttribute(eleOrderStatusChange, ATTR_TRANSACTION_ID, "VSI_WH_HOLD_INVOICE.0003.ex");
							SCXmlUtil.setAttribute(eleOrderStatusChange, ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
							SCXmlUtil.setAttribute(eleOrderStatusChange, ATTR_SELECT_METHOD, SELECT_METHOD_WAIT);
							SCXmlUtil.setAttribute(eleOrderStatusChange, ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY, FLAG_Y);
							SCXmlUtil.setAttribute(eleOrderStatusChange, ATTR_BASE_DROP_STATUS, "3950");

							if(log.isDebugEnabled()){
								log.debug("Input to changeOrderStatus: " + SCXmlUtil.getString(changeOrderStatusInXML));
							}

							try{
								VSIUtils.invokeAPI(env, API_CHANGE_ORDER_STATUS, changeOrderStatusInXML);
							} catch (Exception e){
								log.error(e.getMessage());
								e.printStackTrace();
								throw new YFCException(e.getMessage());
							}
						}
					}
				}
			}
		}
		
		log.endTimer("VSIWholesaleChangeException.changeAlert()");
		return inXML;
	}

}
