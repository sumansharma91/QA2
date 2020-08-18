package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIGeneralUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;


public class VSIScheduleBOPUSOrder {
	private YFCLogCategory log = YFCLogCategory.instance(VSIScheduleBOPUSOrder.class);
	YIFApi api;
	public void vsiScheduleBOPUS(YFSEnvironment env, Document XML)
			throws Exception {
		if(log.isDebugEnabled()){
		log.info("================Inside VSIScheduleBOPUSOrder================================");
		}
		 if (!SCUtil.isVoid(XML)) {
			 if(log.isDebugEnabled()){
				 log.debug("Printing Input XML:" + XmlUtils.getString(XML));
			 }
		try {
						
			Element rootElement = XML.getDocumentElement();
			String orderHeaderKey = rootElement.getAttribute("OrderHeaderKey");
			Document getOrderListInput = XMLUtil.createDocument("Order");
			Element eleOrder = getOrderListInput.getDocumentElement();
			eleOrder.setAttribute("OrderHeaderKey", orderHeaderKey);
			
	//Calling the getOrderList API 
				api = YIFClientFactory.getInstance().getApi();
				env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderListSOForHold.xml");
				Document getOrderListOp = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
				if(log.isDebugEnabled()){
					log.debug("*** NOW PRINTING getOrderListOp "+XMLUtil.getXMLString(getOrderListOp));
				}
				if(getOrderListOp != null){
					
					Element ipOrderEle = (Element) getOrderListOp.getDocumentElement().getElementsByTagName("Order").item(0);
					if(log.isDebugEnabled()){
						log.debug("*** NOW PRINTING ipOrderEle "+XMLUtil.getElementString(ipOrderEle));
					}
					NodeList nlOrderHoldTypeList = XMLUtil.getNodeListByXpath(
							getOrderListOp, "/OrderList/Order/OrderHoldTypes/OrderHoldType");

					boolean shouldInvokeApi = true;
						for(int i1=0;i1<nlOrderHoldTypeList.getLength();i1++){
							Element eleOrderHoldType = (Element)nlOrderHoldTypeList.item(i1);	
							if(log.isDebugEnabled()){
								log.debug("*** NOW PRINTING eleOrderHoldType "+XMLUtil.getElementString(eleOrderHoldType));
							}
							String status = eleOrderHoldType.getAttribute(VSIConstants.ATTR_STATUS);
							String holdType = eleOrderHoldType.getAttribute(VSIConstants.ATTR_HOLD_TYPE);
							
							if(!("1300".equals(status) && ("VSI_SCHEDULE_HOLD".equals(holdType)||"VSI_ORD_VERIFY_HOLD".equals(holdType)||"VSI_FRAUD_HOLD".equals(holdType)))){
								shouldInvokeApi = false;
								break;
							}
						}
						if(shouldInvokeApi){
								NodeList orderLineList = getOrderListOp.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
								for(int i=0;i<orderLineList.getLength();i++){
									Element eleOrderLine = (Element)orderLineList.item(i);	
									String lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
									if(lineType.equals("PICK_IN_STORE")){
										Document scheduleOrderInput = XMLUtil.createDocument("ScheduleOrder");
										Element sOrderElement = scheduleOrderInput.getDocumentElement();
										sOrderElement.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, orderHeaderKey);
										sOrderElement.setAttribute("CheckInventory", "Y");
										if(log.isDebugEnabled()){
										log.debug("*** NOW PRINTING scheduleOrderInput "+XMLUtil.getXMLString(scheduleOrderInput));
										}
										api = YIFClientFactory.getInstance().getApi();
										api.invoke(env, VSIConstants.API_SCHEDULE_ORDER,scheduleOrderInput);
										break;
									}
				
								}

							}
						}
	              }
				catch (YFSException e) {
					e.printStackTrace();
				}catch(Exception e){
					e.printStackTrace();
					
			}
         }
     }
}
