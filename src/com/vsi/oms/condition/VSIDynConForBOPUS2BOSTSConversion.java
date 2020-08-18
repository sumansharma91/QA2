package com.vsi.oms.condition;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.ycp.japi.YCPDynamicCondition;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfs.japi.YFSEnvironment;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;

public class VSIDynConForBOPUS2BOSTSConversion implements YCPDynamicCondition{
	
	
	

	YIFApi api;
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, String inXML) {
	
		boolean bOut = false;
		
		 try{
		 // Start- For common code control over Split to Bosts functionality-Ashutosh
			String bostsFullSplitFlag="Y";
		 // End- For common code control over Split to Bosts functionality-Ashutosh
			String val="";
			YFCDocument inputXML = YFCDocument.getDocumentFor(inXML);
			YFCElement eleInput = inputXML.getDocumentElement();
			String strEnterpriseCode = 	 "VSI";
			String strModificationReasonText = eleInput.getAttribute("ModificationReasonText");
			String strNoStockCode = getCommonCodeLongDescriptionByCodeValue(env,strEnterpriseCode,"REASON_NO_INVENTORY");
			
			if(!YFCObject.isVoid(strModificationReasonText) && (strNoStockCode.equalsIgnoreCase(strModificationReasonText)) ){
				bOut = true;
			}
		// Start- For common code control over Split to Bosts functionality-Ashutosh
			bostsFullSplitFlag = getBostsFlag(env,"VSI_FULL_SPLIT_BOSTS");
			
			if (bostsFullSplitFlag.equalsIgnoreCase("N")) {
			bOut = false;	
			} 
		// End- For common code control over Split to Bosts functionality-Ashutosh
			
		} catch (Exception e){
		e.printStackTrace();
	}
	
		
		return bOut;
		
	}
	

	public static Document getCommonCodeListInputForCodeValue(String sOrgCode,
			String codeType) throws ParserConfigurationException {
		Document docOutput = XMLUtil.createDocument("CommonCode");
		Element eleRootItem = docOutput.getDocumentElement() ;
		eleRootItem.setAttribute("OrganizationCode", sOrgCode);
		eleRootItem.setAttribute("CodeType", codeType);
		return docOutput;
	}
	
	
	public static String getCommonCodeLongDescriptionByCodeValue(YFSEnvironment env,
			String sOrgCode, String sCodeName)
			throws ParserConfigurationException {
		String sCommonCodeValue = "";
		Document docgetCCListInput = getCommonCodeListInputForCodeValue(
				sOrgCode, sCodeName);
		Document docCCList = getCommonCodeList(env,
				docgetCCListInput);
		Element eleComCode = null;
		if (docCCList != null) {
			eleComCode = (Element) docCCList.getElementsByTagName("CommonCode").item(0);
		}
		if (eleComCode != null) {
			sCommonCodeValue = eleComCode.getAttribute("CodeLongDescription");
		}
		return getEmptyCheckedString(sCommonCodeValue);
	}
	
	public static String getEmptyCheckedString(String str) {
		if (isEmpty(str)) {
			return EMPTY_STRING;
		} else {
			return str.trim();
		}
	}

	public static Document getCommonCodeList(YFSEnvironment env,
			Document docApiInput) {
		Document docApiOutput = null;
		try {
			YIFApi api;
			api = YIFClientFactory.getInstance().getApi();
			docApiOutput = api.invoke(env, "getCommonCodeList", docApiInput);
				
		} catch (Exception e) {
			e.printStackTrace();
		}

		return docApiOutput;
	}
	
	public static final String EMPTY_STRING = "";

	public static boolean isEmpty(String str) {
		return (str == null || str.trim().length() == 0) ? true : false;
	}
	
	public static String getBostsFlag(YFSEnvironment env, String splitType) {
		String bostsFlag = null;
		String newSplitType = splitType;
		try{
		Document docForGetCommonCodeList = XMLUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);

		Element eleCommonCode = docForGetCommonCodeList.getDocumentElement();
			    
		eleCommonCode.setAttribute(VSIConstants.ATTR_CODE_TYPE, newSplitType);
	
		Document docAfterGetCommonCodeList = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST,docForGetCommonCodeList);
		
		
		if (docAfterGetCommonCodeList != null) {
			Element eleOutCommonCode=(Element) docAfterGetCommonCodeList.getElementsByTagName(VSIConstants.ELEMENT_COMMON_CODE).item(0);
			if(eleOutCommonCode != null){
				bostsFlag = eleOutCommonCode.getAttribute(VSIConstants.ATTR_CODE_VALUE);
			}
		
			
		}}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		
		return bostsFlag;
	}

	

}
