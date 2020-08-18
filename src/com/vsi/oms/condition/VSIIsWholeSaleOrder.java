package com.vsi.oms.condition;

import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;

public class VSIIsWholeSaleOrder  implements YCPDynamicConditionEx,VSIConstants{

	private YFCLogCategory log = YFCLogCategory
			.instance(VSIIsWholeSaleOrder.class);
	
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String conditionName, Map mapData, Document inputXML) {
		log.beginTimer("VSIIsWholeSaleOrder.evaluateCondition");
		
		boolean bIsWholeSaleOrder = false;
		if(log.isDebugEnabled()) {
			log.debug("Printing Input XML for IsWholeSaleOrder Condition:" + XmlUtils.getString(inputXML));
		}
		String strOHKey = mapData.get(ATTR_ORDER_NO).toString();
		if(!YFCCommon.isVoid(strOHKey)) {
			Document docGetOrderListInput;
			try {
				docGetOrderListInput = XMLUtil.createDocument(ELE_ORDER);
				
				Element eleOrder = docGetOrderListInput.getDocumentElement();
				eleOrder.setAttribute(ATTR_ORDER_NO,strOHKey);
	
				//Calling getOrderlist API to get the orderType
				Document docGetOrderListOutput=VSIUtils.invokeService(env,SERVICE_GET_ORDER_LIST, docGetOrderListInput);
				if(!YFCCommon.isVoid(docGetOrderListOutput)) {
					Element eleOutputOrder = (Element)(XMLUtil.getElementsByTagName(docGetOrderListOutput.getDocumentElement(), ELE_ORDER).get(0));
					String strOrderType = eleOutputOrder.getAttribute(ATTR_ORDER_TYPE);
					if(log.isDebugEnabled()){
						log.debug("VSIIsWholeSaleOrder.evaluateCondition: OrderType from getOrderList is"+ strOrderType);
					}
					if(!YFCCommon.isVoid(strOrderType) && strOrderType.equalsIgnoreCase(WHOLESALE)) {
						bIsWholeSaleOrder = true;
					}
				}
			}
			 catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		if(log.isDebugEnabled()){
			log.debug("VSIIsWholeSaleOrder.evaluateCondition: Flag returned:"+ bIsWholeSaleOrder);
		}
		log.endTimer("VSIIsWholeSaleOrder.evaluateCondition");
		return bIsWholeSaleOrder;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

}
