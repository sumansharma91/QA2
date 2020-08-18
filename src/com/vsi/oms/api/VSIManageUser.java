package com.vsi.oms.api;

import java.rmi.RemoteException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;


import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;

public class VSIManageUser {
	
	private YFCLogCategory log = YFCLogCategory
			.instance(VSIManageUser.class);
	
	public void  manageUser (YFSEnvironment yfsEnv, Document docInXML)
			throws YFSUserExitException, YFSException, RemoteException, YIFClientCreationException {
		if(log.isDebugEnabled()){
			log.debug("*** Inside Method manageUser");
			log.info("***Updated UE Docuement"+SCXmlUtil.getString(docInXML));
		}
		//get the Loginid for the user
		Element eleRootEleInXML = docInXML.getDocumentElement();
		String attrLoginIdInXML = eleRootEleInXML.getAttribute(VSIConstants.ATTR_LOGIN_ID);

		//create getUserList API input 
		Document userListIP = SCXmlUtil.createDocument(VSIConstants.ELE_USER);
		Element eleUser = userListIP.getDocumentElement();
		eleUser.setAttribute(VSIConstants.ATTR_LOGIN_ID,attrLoginIdInXML);
	try{
		String strAction = eleRootEleInXML.getAttribute("Action");
		if(!YFCCommon.isVoid(strAction))
		{
			VSIUtils.invokeAPI(yfsEnv, "deleteUserHierarchy",docInXML); 
		}
		else
		{
			//Check User existence
			Document getUserListDoc=VSIUtils.invokeAPI(yfsEnv,VSIConstants.TEMPLATE_VSI_LOAD_USER, VSIConstants.API_GET_USER_LIST,userListIP); 
			Element eleRootEleUserList = getUserListDoc.getDocumentElement();
			Boolean hasChildUser = eleRootEleUserList.hasChildNodes();
			docInXML = userQueueSubscription(yfsEnv,docInXML);
			//Element User null check
			if(hasChildUser==true){
				if(log.isDebugEnabled()){
					log.debug("***User Exist: Modifying Existing User");
				}
					VSIUtils.invokeAPI(yfsEnv, VSIConstants.API_MOD_USER_HIERARCHY,docInXML); 
			}
			else{
				if(log.isDebugEnabled()){
					log.debug("***User Does not Exist: Creating new User");
				}
				VSIUtils.invokeAPI(yfsEnv, VSIConstants.API_CREATE_USER_HIERARCHY,docInXML);
			}
		}
		
	}
	 catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN001",
					"User Upload Failure",
					e.getMessage());
	}
	 catch (YIFClientCreationException e) {
			e.printStackTrace();
			throw new YFSException(
					"EXTN001",
					"User Upload Failure",
					e.getMessage());
	}
	 catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException();
	}
	 catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();
	}

	}
	private Document userQueueSubscription(YFSEnvironment env, Document docInXML) throws YFSException, RemoteException, YIFClientCreationException{
		String strUserGroupID = SCXmlUtil.getXpathAttribute(docInXML.getDocumentElement(),"/User/UserGroupLists/UserGroupList/@UsergroupId");
		Element eleQueueSubscriptionList = (Element) docInXML.getDocumentElement().getElementsByTagName("QueueSubscriptionList").item(0);
		if(YFCObject.isVoid(eleQueueSubscriptionList)){
			eleQueueSubscriptionList = docInXML.createElement("QueueSubscriptionList");
			docInXML.getDocumentElement().appendChild(eleQueueSubscriptionList);
		}
		eleQueueSubscriptionList.setAttribute(VSIConstants.ATTR_RESET, "Y");
		Document docCommonCodeListOutput = callCommonCodeList(env,strUserGroupID);
		if(!YFCObject.isVoid(docCommonCodeListOutput)){
			NodeList nlCommonCodeList = docCommonCodeListOutput.getElementsByTagName(VSIConstants.ELE_COMMON_CODE);
			for(int i=0;i<nlCommonCodeList.getLength();i++){
				Element eleCommonCode = (Element) nlCommonCodeList.item(i);
				String strSubscriptionQueueID = eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_LONG_DESCRIPTION);
				Document docQueueListInput = SCXmlUtil.createDocument();
				Element eleQueueListInput  = docQueueListInput.createElement("Queue");
				docQueueListInput.appendChild(eleQueueListInput);
				eleQueueListInput.setAttribute("QueueId", strSubscriptionQueueID);
				Document docQueueListOutput = VSIUtils.invokeAPI(env,"<QueueList><Queue QueueKey=''/></QueueList>" ,"getQueueList", docQueueListInput);
				String strQueueKey = SCXmlUtil.getXpathAttribute(docQueueListOutput.getDocumentElement(),"/QueueList/Queue/@QueueKey");
				Element eleQueueSubscription = docInXML.createElement("QueueSubscription");
				eleQueueSubscriptionList.appendChild(eleQueueSubscription);
				eleQueueSubscription.setAttribute("QueueKey", strQueueKey);
			}
		}
		return docInXML;
	}
	
	private Document callCommonCodeList(YFSEnvironment env, String strUserGroupID) {
		Document docCommonCodeListOutput = null;
		Document docCommonCodeListInput = null;
		
		docCommonCodeListInput = SCXmlUtil.createDocument();
		Element eleCommonCode = docCommonCodeListInput.createElement(VSIConstants.ELE_COMMON_CODE);
		docCommonCodeListInput.appendChild(eleCommonCode);
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, strUserGroupID+"_Q");
		try {
			docCommonCodeListOutput = VSIUtils.invokeAPI(env, VSIConstants.API_COMMON_CODE_LIST, docCommonCodeListInput);
		} catch (YFSException | RemoteException | YIFClientCreationException e) {
			return docCommonCodeListOutput;
		}
		
		return docCommonCodeListOutput;
	}
}
