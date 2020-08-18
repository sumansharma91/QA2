package com.vsi.oms.condition;

import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIExecuteNewDesign  implements YCPDynamicConditionEx {
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckDTCFlag.class);
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		log.verbose("VSIExecuteNewDesign.evaluateCondition Input XML :" + SCXmlUtil.getString(inXML));
		String strCSCFlag=null;
		String strSTHFlag=null;
		String strOrderType=null;
		String strEntryType=null;
		String strEnterpriseCode=null;
		String strLineType=null;
		boolean bCSCFlag = false;
		boolean bSTHFlag = false;
		boolean bLineTypeFlag = false;
		boolean bUpdateCSCFlag = false;
		try {
			Element eleOrderReleaseDoc = inXML.getDocumentElement();
			strOrderType=eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_ORDER_TYPE);
			strEnterpriseCode=eleOrderReleaseDoc.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			//Fetch entry type
			NodeList nlOrder=eleOrderReleaseDoc.getElementsByTagName(VSIConstants.ELE_ORDER);
			Element eleOrder=(Element)nlOrder.item(0);
			strEntryType=eleOrder.getAttribute(VSIConstants.ATTR_ENTRY_TYPE);
			
			//check new design flag
			ArrayList<Element> listStampCSCFlag;
			listStampCSCFlag = VSIUtils.getCommonCodeList(env, VSIConstants.CODE_TYPE_VSI_NEW_FLOW, VSIConstants.CODE_VALUE_STAMP_CSC, VSIConstants.ATTR_DEFAULT);
			if(!listStampCSCFlag.isEmpty()){
				Element eleCommonCode=listStampCSCFlag.get(0);
				strCSCFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				if(!YFCCommon.isStringVoid(strCSCFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strCSCFlag)){
					bCSCFlag = true;
				}
			}
			//check line type

			NodeList nlOrderLine=inXML.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
			int iCount=nlOrderLine.getLength();
			for (int i = 0; i < iCount; i++) {
				Element eleOrderLine=(Element)nlOrderLine.item(i);
				strLineType=eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
				if(!YFCCommon.isStringVoid(strLineType) && VSIConstants.LINETYPE_STH.equalsIgnoreCase(strLineType)){
					bLineTypeFlag = true;
					break;

				}
			}

			//condition for POS/MARKETPLACE order
			if(!YFCCommon.isStringVoid(strOrderType) && (VSIConstants.ATTR_ORDER_TYPE_POS).equalsIgnoreCase(strOrderType)||(VSIConstants.MARKETPLACE).equalsIgnoreCase(strOrderType))
			{
				ArrayList<Element> listOrderTypeFlag;
				listOrderTypeFlag = VSIUtils.getCommonCodeList(env, VSIConstants.CODE_TYPE_VSI_NEW_FLOW, strOrderType, VSIConstants.ATTR_DEFAULT);
				if(!listOrderTypeFlag.isEmpty()){
					Element eleCommonCode1=listOrderTypeFlag.get(0);
					strSTHFlag=eleCommonCode1.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strSTHFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strSTHFlag)){

						bSTHFlag = true;
					}
				}
			}

			//condition for Call Center order 
			else if(!YFCCommon.isStringVoid(strEntryType) && (VSIConstants.ENTRYTYPE_CC).equalsIgnoreCase(strEntryType))
			{
				ArrayList<Element> listOrderTypeFlag;
				listOrderTypeFlag = VSIUtils.getCommonCodeList(env, VSIConstants.CODE_TYPE_VSI_NEW_FLOW, strEntryType, VSIConstants.ATTR_DEFAULT);
				if(!listOrderTypeFlag.isEmpty()){
					Element eleCommonCode1=listOrderTypeFlag.get(0);
					strSTHFlag=eleCommonCode1.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strSTHFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strSTHFlag)){

						bSTHFlag = true;
					}
				}
			}
			//condition for ADP/VSI.com WEB order 
			else if(!YFCCommon.isStringVoid(strEnterpriseCode) && (VSIConstants.ENT_ADP.equalsIgnoreCase(strEnterpriseCode)||VSIConstants.VSICOM_ENTERPRISE_CODE.equalsIgnoreCase(strEnterpriseCode)))
			{
				ArrayList<Element> listOrderTypeFlag;
				listOrderTypeFlag = VSIUtils.getCommonCodeList(env, VSIConstants.CODE_TYPE_VSI_NEW_FLOW, strEnterpriseCode, VSIConstants.ATTR_DEFAULT);
				if(!listOrderTypeFlag.isEmpty()){
					Element eleCommonCode1=listOrderTypeFlag.get(0);
					strSTHFlag=eleCommonCode1.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if(!YFCCommon.isStringVoid(strSTHFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strSTHFlag)){

						bSTHFlag = true;
					}
				}
			}



			if(bCSCFlag && bSTHFlag && bLineTypeFlag){

				bUpdateCSCFlag=true;
			}

		}
		catch (Exception e) {
			e.printStackTrace();
			throw new YFSException();
		}
		log.verbose("VSIExecuteNewDesign.evaluateCondition output:bCSCFlag " +bCSCFlag );
		return bUpdateCSCFlag;
	}
	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub

	}

}
