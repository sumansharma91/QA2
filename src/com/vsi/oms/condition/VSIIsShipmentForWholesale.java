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

public class VSIIsShipmentForWholesale implements VSIConstants,YCPDynamicConditionEx {


	private YFCLogCategory log = YFCLogCategory
			.instance(VSIIsShipmentForWholesale.class);
	
	@Override
	public boolean evaluateCondition(YFSEnvironment env, String conditionName, Map mapData, Document inputXML) {
		log.beginTimer("VSIIsShipmentForWholesale.evaluateCondition");
		
		boolean bIsWholeSaleOrder = false;
		if(log.isDebugEnabled()) {
			log.debug("Printing Input XML for IsWholeSaleOrder Condition:" + XmlUtils.getString(inputXML));
		}
	
		Element eleShipment = (Element)inputXML.getDocumentElement().getElementsByTagName(ELE_SHIPMENT).item(0);
		String strOrderNo = eleShipment.getAttribute(ATTR_ORDER_NO);
		if(log.isDebugEnabled()){
			log.debug("VSIIsShipmentForWholesale.evaluateCondition--- reading from input - OrderNo is  :"+strOrderNo);
		}
		
		if(!YFCCommon.isVoid(strOrderNo)) {
			Document docGetOrderListInput;
			try {
				docGetOrderListInput = XMLUtil.createDocument(ELE_ORDER);
				
				Element eleOrder = docGetOrderListInput.getDocumentElement();
				eleOrder.setAttribute(ATTR_ORDER_NO,strOrderNo);
	
				//Calling getOrderlist API to get the orderType
				Document docGetOrderListOutput=VSIUtils.invokeService(env,SERVICE_GET_ORDER_LIST, docGetOrderListInput);
				if(!YFCCommon.isVoid(docGetOrderListOutput)) {
					Element eleOutputOrder = (Element)(XMLUtil.getElementsByTagName(docGetOrderListOutput.getDocumentElement(), ELE_ORDER).get(0));
					String strOrderType = eleOutputOrder.getAttribute(ATTR_ORDER_TYPE);
					if(log.isDebugEnabled()){
						log.debug("VSIIsShipmentForWholesale.evaluateCondition: OrderType from getOrderList is"+ strOrderType);
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
			log.debug("VSIIsShipmentForWholesale.evaluateCondition: Flag returned:"+ bIsWholeSaleOrder);
		}
		log.endTimer("VSIIsShipmentForWholesale.evaluateCondition");
		return bIsWholeSaleOrder;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}



}
