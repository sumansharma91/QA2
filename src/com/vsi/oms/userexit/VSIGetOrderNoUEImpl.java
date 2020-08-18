package com.vsi.oms.userexit;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIDBUtil;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
import com.yantra.yfs.japi.YFSUserExitException;
import com.yantra.yfs.japi.ue.YFSGetOrderNoUE;


import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;


/**
 * @author Perficient, Inc.
 * 
 *Generation of COM OrderNo's. This UE is invoked when OrderNo is NOT passed to createOrder API 
 * At Vitamin Shoppe, the requirement is to have only 8 Characters. 
 * So, the order number should be "Y" followed by 7 characters. Ex: Y1234567.
 */
public class VSIGetOrderNoUEImpl implements YFSGetOrderNoUE 
{

@SuppressWarnings("unused")
private YFCLogCategory log = YFCLogCategory.instance(VSIGetOrderNoUEImpl.class);
YIFApi api;

	String OrderNo ="";
	public String getOrderNo(YFSEnvironment env, Map inMap)
			throws YFSUserExitException {
		
		try{
			// Second Check for OrderNo.
			if(orderNoCheck(env,inMap)){
				
		
				 OrderNo = comOrderNo(env, inMap);
				 
			}
		
		return OrderNo;
	}catch (Exception e) {
		e.printStackTrace();
		throw new YFSException(
				e.getMessage(),
				e.getMessage(),
				"YFS Exception");
			
	} 
}
    

	 public String comOrderNo(YFSEnvironment env , Map inMap) throws Exception
	 {
		 //OMS-1797:Start
			String strEnterpriseCode = (String) inMap.get(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strDocumentType = (String) inMap.get(VSIConstants.ATTR_DOCUMENT_TYPE);
			Document docGetCommonCodeInput = SCXmlUtil.createDocument(VSIConstants.ELEMENT_COMMON_CODE);
			Element eleCommonCodeElement = docGetCommonCodeInput.getDocumentElement();
			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_TYPE, VSIConstants.CODETYPE_WH_ORD_PREFIX);
			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_CODE_VALUE, strEnterpriseCode);
			eleCommonCodeElement.setAttribute(VSIConstants.ATTR_ORG_CODE, VSIConstants.ATTR_DEFAULT);
			Document docgetCommonCodeOutput = VSIUtils.invokeAPI(env,VSIConstants.API_COMMON_CODE_LIST, docGetCommonCodeInput);
			Element commonCodeListElement = docgetCommonCodeOutput.getDocumentElement();
			boolean isWholeSale=false;
			if(commonCodeListElement.hasChildNodes()){
				isWholeSale=true;
			}
			//OMS-1797:End
			if(!isWholeSale)
			{	
			String sOrderNoPrefix = orderNoPrefix(env,inMap);
			DateFormat df = new SimpleDateFormat("yy"); // Just the year, with 2 digits
			String formattedYear = df.format(Calendar.getInstance().getTime());
		    String seqNum = "VSI_SEQUENCE_ORDER_NUMBER";
		    String sequenceNumber = VSIDBUtil.getNextSequence(env, seqNum);
		    String sCOMOrderNo = sOrderNoPrefix + formattedYear + sequenceNumber ;
		    return sCOMOrderNo;
			}
			else	
			{
				String strSeqName=null;
				String strPrefix=null;
				if(commonCodeListElement.hasChildNodes()){
						Element eleCommonCode = SCXmlUtil.getChildElement(commonCodeListElement, VSIConstants.ELE_COMMON_CODE);
						strSeqName = SCXmlUtil.getAttribute(eleCommonCode, VSIConstants.ATTR_CODE_LONG_DESC);
						strPrefix = SCXmlUtil.getAttribute(eleCommonCode, VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				}
				String strSeqNum = VSIDBUtil.getNextSequence(env, strSeqName);
				String strSeqNumPadded =String.format("%1$" + 5 + "s", strSeqNum).replace(' ', '0');
				String strOrderNo=strPrefix+strSeqNumPadded;
				if(log.isDebugEnabled()){
						log.debug("strOrderNo :" + strOrderNo);
				}
				if(VSIConstants.RETURN_DOCUMENT_TYPE.equals(strDocumentType))
				{
					return "R"+strOrderNo;
				}
				else
					return "B"+strOrderNo;
			}
	    }
	private boolean orderNoCheck(YFSEnvironment env ,Map inMap) {
		@SuppressWarnings("rawtypes")
		Set objKeys = inMap.keySet();
		
		if (objKeys.contains(VSIConstants.ATTR_ORDER_NO)){
			String orderNo=(String) inMap.get(VSIConstants.ATTR_ORDER_NO); 
			
			if (null == orderNo || orderNo.equals("")){
				
				return true;
				
			}else{
				return false;
			}    	       	   
		}
		else{
			return true;
		}
	}//end orderNoCheck
	
	/**
	 * Code for the prefix of OrderNo depending on DocumentType
	 * @param inMap
	 * @return
	 * @throws Exception 
	 */
	private String orderNoPrefix(YFSEnvironment env ,Map inMap) throws Exception{
		String documentType = (String) inMap.get(VSIConstants.ATTR_DOCUMENT_TYPE);
		String entryType = (String) inMap.get(VSIConstants.ATTR_ENTRY_TYPE);
		if(VSIConstants.DOC_TYPE_TO.equals(documentType))
		{
			
			return "Y";
		}
		else if(VSIConstants.ATTR_ENTRY_TYPE_VALUE.equals(entryType))
		{
			return "D";
		}
		if(VSIConstants.RETURN_DOCUMENT_TYPE.equals(documentType)){
			
			return "R";
		}
		
		if(entryType.equals("Marketplace")){
			
			return "M";
		}
		else if(entryType.equals("Call Center")){
			
		return "C";
		
		}
		else
		{
			return "Y";
		}
		
	}//end of orderNoPrefix
}// end class