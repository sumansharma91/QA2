package com.vsi.oms.condition;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.ycp.japi.YCPDynamicConditionEx;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIConditionToCheckShipToHomeOrderStatus implements YCPDynamicConditionEx{

	private YFCLogCategory log = YFCLogCategory.instance(VSIConditionToCheckShipToHomeOrderStatus.class);
	

	@Override
	public boolean evaluateCondition(YFSEnvironment env, String condName,
			@SuppressWarnings("rawtypes") Map mapData, Document inXML) {
		
		if(log.isDebugEnabled()){
			log.verbose("Printing Input XML :" + XmlUtils.getString(inXML));
			log.info("================Inside VSIConditionToCheckShipToHomeOrderStatus================================");
		}
		
		boolean isMaxOrderStatus = false;
		Document getOrderDetailsOutput = null;
		try {
			Element eleOrder = inXML.getDocumentElement();
			String strOrderType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ORDER_TYPE);
			String strEntryType = SCXmlUtil.getAttribute(eleOrder, VSIConstants.ATTR_ENTRY_TYPE);
			if(strOrderType.equalsIgnoreCase(VSIConstants.WHOLESALE) || strEntryType.equalsIgnoreCase(VSIConstants.WHOLESALE)){
				return false;
			}
			Document getOrderListInput = null;
			getOrderListInput=SCXmlUtil.createDocument("OrderRelease");
			String fromOrderRepricing =  inXML.getDocumentElement().getAttribute("FromOrderRepricing");
			getOrderListInput.getDocumentElement().setAttribute("OrderHeaderKey", inXML.getDocumentElement().getAttribute("OrderHeaderKey"));
			getOrderDetailsOutput = VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORDER_LIST_ORDERHOLD, VSIConstants.API_GET_ORDER_RELEASE_LIST,getOrderListInput );			
	 		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
	 		if(!YFCObject.isNull(eleOrderLines))
	 		{
	 			Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
	 			if(!YFCObject.isVoid(eleOrderLine)){
	 				String sLineType = eleOrderLine.getAttribute("LineType");
		 			//Element eleOrder = inXML.getDocumentElement();
		 			if(!YFCCommon.isVoid((fromOrderRepricing)))
		 			{
		 				if(!getOrderDetailsOutput.getDocumentElement().hasChildNodes())
		 				{
		 					return true;
		 				}
		 			}
		 			else 
					{
		 				if(!getOrderDetailsOutput.getDocumentElement().hasChildNodes() && "SHIP_TO_HOME".equalsIgnoreCase(sLineType))
		 				{
		 					return true;
		 				}
					}
	 			}
	 			else
	 			{
	 				return true;
	 			}
	 		}
			
			/**if(!YFCObject.isNull(eleOrder)){
				String sOrderType = eleOrder.getAttribute("OrderType");
		 		Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
		 		if(!YFCObject.isNull(eleOrderLines)){
			 		Element eleOrderLine = SCXmlUtil.getChildElement(eleOrderLines, VSIConstants.ELE_ORDER_LINE);
			 		if(!YFCObject.isNull(eleOrderLine)){
				 		String sLineType = eleOrderLine.getAttribute("LineType");
			
						if(sOrderType.equalsIgnoreCase("WEB") && sLineType.equalsIgnoreCase("SHIP_TO_HOME")){
							
							Element eleOrderStatuses = SCXmlUtil.getChildElement(eleOrder, "OrderStatuses");
							
							ArrayList<Element> alOrderStatus= SCXmlUtil.getChildren(eleOrderStatuses, "OrderStatus");
							
							for(Element eleOrderStatus:alOrderStatus)
							{
							 	double dStatus = SCXmlUtil.getDoubleAttribute(eleOrderStatus, "Status");
						
							 	if(dStatus == 3200){
							 		isMaxOrderStatus = false;
							 		break;
							 	}//end of if
							 	
							}//end of for
							
						}// end of if
			 		}
		 		}
			}	**/			
		} catch (YFSException | RemoteException | YIFClientCreationException e) {
			
			e.printStackTrace();
			throw new YFSException();
			
		}
		return isMaxOrderStatus;
	}

	@Override
	public void setProperties(Map arg0) {
		// TODO Auto-generated method stub
		
	}

	
}
