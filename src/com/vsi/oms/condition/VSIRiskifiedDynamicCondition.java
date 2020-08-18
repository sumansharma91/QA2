package com.vsi.oms.condition;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIRiskifiedDynamicCondition implements YCPDynamicConditionEx {
	
	private YFCLogCategory log = YFCLogCategory.instance(VSIRiskifiedDynamicCondition.class);

	@Override
	/**
	 * Condition to decide whether it is partial or full cancel or return
	 */
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			Map mapData, Document inXML) {
		
		log.debug("VSIRiskifiedDynamicCondition: evaluateCondition Begins ");
		log.debug("VSIRiskifiedDynamicCondition: evaluateCondition: input is : " + XMLUtil.getXMLString(inXML));
		
		boolean isFullCancelOrReturn = true;
				
		try{
			Element eleOrder = inXML.getDocumentElement();
			String strDocumentType = eleOrder.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
			if(VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equals(strDocumentType)){
				
				String strOrderHeaderKey = eleOrder.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
				
				Document getOrderListOp = callGetOrderList(env,
						strOrderHeaderKey);
				
				Element eleOrderListOp = getOrderListOp.getDocumentElement();
				Element eleOrderOp = SCXmlUtil.getChildElement(eleOrderListOp, VSIConstants.ELE_ORDER);
				Element eleOrderLinesOp = SCXmlUtil.getChildElement(eleOrderOp, VSIConstants.ELE_ORDER_LINES);
				List<Element> listOrderLineOp = new ArrayList<>();
				listOrderLineOp = SCXmlUtil.getChildren(eleOrderLinesOp, VSIConstants.ELE_ORDER_LINE);
				HashMap<String, String> hmapOrderLine = new HashMap<String, String>();
				
				for (Element eleOrderLineOp : listOrderLineOp) {
					String strOrderLineKey = SCXmlUtil.getAttribute(eleOrderLineOp,
							VSIConstants.ATTR_ORDER_LINE_KEY);
					String strMaxLineStatus = SCXmlUtil.getAttribute(eleOrderLineOp,
							VSIConstants.ATTR_MAX_LINE_STATUS);
					hmapOrderLine.put(strOrderLineKey, strMaxLineStatus);
				}
				Set set = hmapOrderLine.entrySet();
				Iterator iterator = set.iterator();
				while (iterator.hasNext()) {
					Map.Entry maxLineStatus = (Map.Entry) iterator.next();
					if (!(maxLineStatus.getValue().equals(VSIConstants.STATUS_CANCEL))) {
						isFullCancelOrReturn = false;
						break;
					}
				}				
			}
			else if (VSIConstants.RETURN_DOCUMENT_TYPE.equals(strDocumentType)){
				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
				Element eleOrderLinefirst = SCXmlUtil.getFirstChildElement(eleOrderLines);
				String strDerivedFromOrderHeaderKey = SCXmlUtil.getAttribute(eleOrderLinefirst,
						VSIConstants.ATTR_DERIVED_FROM_ORDER_HEADER_KEY);
				
				Document getOrderListOp = callGetOrderList(env,
						strDerivedFromOrderHeaderKey);
				
				Element eleOrderListOp = getOrderListOp.getDocumentElement();
				Element eleOrderOp = SCXmlUtil.getChildElement(eleOrderListOp, VSIConstants.ELE_ORDER);
				String strMaxOrderStatus = SCXmlUtil.getAttribute(eleOrderOp, VSIConstants.ATTR_MAX_ORDER_STATUS);
				String strMinOrderStatus = SCXmlUtil.getAttribute(eleOrderOp, VSIConstants.ATTR_MIN_ORDER_STATUS);
				if (!strMaxOrderStatus.equals(strMinOrderStatus)) {
					isFullCancelOrReturn = false;
				}
			}
		} catch(Exception e){
			
			log.error("Exception occurred in VSIRiskifiedDynamicCondition class "+ e.toString());
			throw new YFSException();
			
		}
		
		return isFullCancelOrReturn;
	}

	/**
	 * @param env
	 * @param strOrderHeaderKey
	 * @return
	 * @throws ParserConfigurationException
	 * @throws YIFClientCreationException
	 * @throws RemoteException
	 */
	private Document callGetOrderList(YFSEnvironment env,
			String strOrderHeaderKey) throws ParserConfigurationException,
			YIFClientCreationException, RemoteException {
		
		// Call getOrderList API
		Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
		getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
				strOrderHeaderKey);
		
		log.debug("VSIRiskifiedDynamicCondition: callGetOrderList: getOrderList api input is : " + XMLUtil.getXMLString(getOrderListIp));
		Document getOrderListOp = VSIUtils.invokeAPI(env,
				"global/template/api/VSIGetOrderListSO_Riskified.xml", VSIConstants.API_GET_ORDER_LIST,
				getOrderListIp);
		log.debug("VSIRiskifiedDynamicCondition: callGetOrderList: getOrderList api output is : " + XMLUtil.getXMLString(getOrderListOp));
		
		return getOrderListOp;
	}

	@Override
	public void setProperties(Map arg0) {		
	}
}
