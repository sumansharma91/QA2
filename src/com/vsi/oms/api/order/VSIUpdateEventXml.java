package com.vsi.oms.api.order;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIUpdateEventXml {
	
	private static YFCLogCategory log = YFCLogCategory.instance(VSIUpdateEventXml.class);
	private static final String TAG = VSIUpdateEventXml.class.getSimpleName();
	YIFApi api;
	
	public Document updateEventXml(YFSEnvironment env, Document inXML){
		
		printLogs("================Inside VSIUpdateEventXml Class================================");
		printLogs("Printing Input XML :"+SCXmlUtil.getString(inXML));
		
		Document outXML=SCXmlUtil.createDocument("OrderStatusChange");
		
		try{			
			
			Element eleoutXML=outXML.getDocumentElement();
			Element eleOrderAudit=SCXmlUtil.createChild(eleoutXML, "OrderAudit");
			
			Element eleinXML=inXML.getDocumentElement();
			String strOrderLineKey=eleinXML.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
			String strCustPONo=eleinXML.getAttribute(VSIConstants.ATTR_CUST_PO_NO);
			String strPrmLnNo=eleinXML.getAttribute(VSIConstants.ATTR_PRIME_LINE_NO);
			String strSubLnNo=eleinXML.getAttribute(VSIConstants.ATTR_SUB_LINE_NO);
			Element eleOrderIn=SCXmlUtil.getChildElement(eleinXML, VSIConstants.ELE_ORDER);
			String strOrderHeaderKey=eleOrderIn.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
			eleOrderAudit.setAttribute("AuditTransactionId", "");
			eleOrderAudit.setAttribute("OrderAuditKey", "");
			eleOrderAudit.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			eleOrderAudit.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			Element eleFromStss=SCXmlUtil.getChildElement(eleinXML, "FromStatuses");
			Element eleFrmOrdSts=SCXmlUtil.getChildElement(eleFromStss, "OrderStatus");
			String strOrderReleaseKey="";
			if(!YFCObject.isVoid(eleFrmOrdSts)){
				strOrderReleaseKey=eleFrmOrdSts.getAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY);
			}			
			eleOrderAudit.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, strOrderReleaseKey);
			eleOrderAudit.setAttribute("ReasonCode", "");
			eleOrderAudit.setAttribute("ReasonText", "");
			eleOrderAudit.setAttribute("Reference1", "");
			eleOrderAudit.setAttribute("Reference2", "");
			eleOrderAudit.setAttribute("Reference3", "");
			eleOrderAudit.setAttribute("Reference4", "");
			eleOrderAudit.setAttribute("XMLFlag", "");
			
			Element eleOrderOut=SCXmlUtil.createChild(eleOrderAudit, VSIConstants.ELE_ORDER);
			String strEnterpriseCode=eleOrderIn.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
			String strOrderNo=eleOrderIn.getAttribute(VSIConstants.ATTR_ORDER_NO);
			String strDocType=eleOrderIn.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			String strMinOrdSts=eleOrderIn.getAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS);
			String strMaxOrdSts=eleOrderIn.getAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS);
			eleOrderOut.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEnterpriseCode);
			eleOrderOut.setAttribute(VSIConstants.ATTR_ORDER_NO, strOrderNo);
			eleOrderOut.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE, strDocType);
			eleOrderOut.setAttribute(VSIConstants.ATTR_MIN_ORDER_STATUS, strMinOrdSts);
			eleOrderOut.setAttribute(VSIConstants.ATTR_MAX_ORDER_STATUS, strMaxOrdSts);
			eleOrderOut.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPONo);
			eleOrderOut.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);
			eleOrderOut.setAttribute(VSIConstants.ATTR_ORDER_TYPE, "");
			
			Element eleOrderAuditLevels=SCXmlUtil.createChild(eleOrderAudit, "OrderAuditLevels");
			Element eleOrderAuditLevel=SCXmlUtil.createChild(eleOrderAuditLevels, "OrderAuditLevel");
			eleOrderAuditLevel.setAttribute("ModificationLevel", "");
			eleOrderAuditLevel.setAttribute(VSIConstants.ATTR_ORDER_RELEASE_KEY, strOrderReleaseKey);
			eleOrderAuditLevel.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			
			Element eleOrderLine=SCXmlUtil.createChild(eleOrderAuditLevel, VSIConstants.ELE_ORDER_LINE);
			eleOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey);
			eleOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY, "");		
			eleOrderLine.setAttribute(VSIConstants.ATTR_PRIME_LINE_NO, strPrmLnNo);
			eleOrderLine.setAttribute(VSIConstants.ATTR_SUB_LINE_NO, strSubLnNo);
			eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustPONo);
			Element eleItemOut=SCXmlUtil.createChild(eleOrderLine, VSIConstants.ELE_ITEM);
			Element eleItemIn=SCXmlUtil.getChildElement(eleinXML, VSIConstants.ELE_ITEM);
			String strItemId=eleItemIn.getAttribute(VSIConstants.ATTR_ITEM_ID);
			String strPrdctCls=eleItemIn.getAttribute(VSIConstants.ATTR_PRODUCT_CLASS);
			String strUOM=eleItemIn.getAttribute(VSIConstants.ATTR_UOM);
			eleItemOut.setAttribute(VSIConstants.ATTR_ITEM_ID, strItemId);
			eleItemOut.setAttribute(VSIConstants.ATTR_PRODUCT_CLASS, strPrdctCls);
			eleItemOut.setAttribute(VSIConstants.ATTR_UOM, strUOM);
			eleItemOut.setAttribute(VSIConstants.ATTR_ORG_CODE, "");
			
			printLogs("Printing Output XML :"+SCXmlUtil.getString(outXML));
			printLogs("================Exiting VSIUpdateEventXml Class================================");
		
		}
		catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException();
		} catch (Exception e){
			e.printStackTrace();
			throw new YFSException();
		}
		
		return outXML;
		
	}
	
	private void printLogs(String mesg) {
		if(log.isDebugEnabled()){
			log.debug(TAG +" : "+mesg);
		}
	}

}
