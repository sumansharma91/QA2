package com.vsi.oms.api.order;


import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIBaseCustomAPI;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;


/**
 * This class is invoked by changeOrder OnCancel event to resolve STORE_RECEIVED_HOLD if there are no lines in the orderline for a specific PO
 * which are in less than StoreReceived Status or all the lines are cancelled except the one in StoreReceived Status for ShipToStore orders. 
 * This code also incoraporate the fix for OMS-1169 defect.
 * 
 * Input: changeOrder OnCancel event xml
 * 
 * 
 * @author IBM
 *
 */
public class VSIResolveStoreReceivedHoldOnCancellation extends VSIBaseCustomAPI {

	private static YFCLogCategory log = YFCLogCategory
			.instance(VSIResolveStoreReceivedHoldOnCancellation.class.getName());
	public void resolveStoreReceivedHold (YFSEnvironment env, Document docInput) throws Exception {
		if(log.isDebugEnabled()){
			log.debug("================Inside VSIResolveStoreReceivedHoldOnCancellation================================");
			log.debug("Printing Input XML :" + XmlUtils.getString(docInput));
		}
		log.beginTimer("VSIResolveStoreReceivedHoldOnCancellation.resolveStoreReceivedHold : START");
		try{
			Element eleRoot = docInput.getDocumentElement();
			Element eleOrdLine = (Element)docInput.getElementsByTagName(VSIConstants.ELE_ORDER_LINE).item(0);
			if(VSIConstants.LINETYPE_STS.equals(eleOrdLine.getAttribute(VSIConstants.ATTR_LINE_TYPE)))
			{
				String strCustomerPoNo=eleOrdLine.getAttribute(VSIConstants.ATTR_CUSTOMER_PO_NO);
				if(log.isDebugEnabled()){
					log.debug("CustomerPONo="+strCustomerPoNo);
				}
				String strEntCode=eleRoot.getAttribute(VSIConstants.ATTR_ENTERPRISE_CODE);
				//Preparing Input for getOrderLineList 
				Document docGetOrderLineListInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER_LINE);
				Element eleOrderLine = docGetOrderLineListInput.getDocumentElement();
				eleOrderLine.setAttribute(VSIConstants.ATTR_CUST_PO_NO, strCustomerPoNo);
				eleOrderLine.setAttribute(VSIConstants.ATTR_ENTERPRISE_CODE, strEntCode);
				eleOrderLine.setAttribute(VSIConstants.ATTR_DOCUMENT_TYPE,VSIConstants.DOCUMENT_TYPE);
				if(log.isDebugEnabled()){
					log.debug("Input for getOrderLineList API"+XmlUtils.getString(docGetOrderLineListInput) );
				}
				//Calling the getOrderLineList API with CustomerPONo,EnterpriseCode and DocumentType		
				Document docOut =VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_GET_ORD_LINE_LIST, VSIConstants.API_GET_ORDER_LINE_LIST, docGetOrderLineListInput);
				if(log.isDebugEnabled()){
					log.debug("Output of getOrderLineList API"+XmlUtils.getString(docOut));
				}
				List<Element> eleOrderLineList = VSIUtils.getElementListByXpath(docOut,"/OrderLineList/OrderLine[@MinLineStatus < '2160.400']");
				if(eleOrderLineList.size()==0)
				{
					if(log.isDebugEnabled()){
						log.debug("Order doesn't have orderlines with MinLineStatus less than StoreReceived ");
					}
					List<Element> eleOrdHoldTypeList =XMLUtil.getElementsByTagName(docOut.getDocumentElement(), VSIConstants.ELE_ORDER_HOLD_TYPE);
					if(!eleOrdHoldTypeList.isEmpty()) 
					{
						Set<String> setOrderLineKey = new HashSet<String>();
						boolean isChangeOrderReqd=false;
						for (Element eleOrdHoldType : eleOrdHoldTypeList) 
						{
							String strStatus=eleOrdHoldType.getAttribute(VSIConstants.ATTR_STATUS);
							String strHoldType=eleOrdHoldType.getAttribute(VSIConstants.ATTR_HOLD_TYPE);
							if(VSIConstants.STORE_RECEIVE_HOLD.equals(strHoldType) && VSIConstants.STATUS_CREATE.equals(strStatus))
							{
								isChangeOrderReqd=true;
								setOrderLineKey.add(eleOrdHoldType.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY));
							}
						}
						if(isChangeOrderReqd)
						{
							if(log.isDebugEnabled()){
								log.debug("ChangeOrderFlag"+isChangeOrderReqd);
							}
							Document docChangeOrder=XMLUtil.createDocument(VSIConstants.ELE_ORDER);
							Element eleDocChangeOrder = docChangeOrder.getDocumentElement();
							eleDocChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, eleRoot.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY));
							eleDocChangeOrder.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_MODIFY);
							eleDocChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
							eleDocChangeOrder.setAttribute(VSIConstants.ATTR_SELECT_METHOD, VSIConstants.FLAG_Y);
							Element eleOrderLines = docChangeOrder.createElement(VSIConstants.ELE_ORDER_LINES);
							eleDocChangeOrder.appendChild(eleOrderLines);
							for(String strOrderLineKey: setOrderLineKey)
							{
								Element eleOrdrLine = docChangeOrder.createElement(VSIConstants.ELE_ORDER_LINE);
								eleOrdrLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY,strOrderLineKey);
								Element eleOrderHoldTypes = docChangeOrder.createElement(VSIConstants.ELE_ORDER_HOLD_TYPES);
								eleOrdrLine.appendChild(eleOrderHoldTypes);
								Element eleOrderHoldType = docChangeOrder.createElement(VSIConstants.ELE_ORDER_HOLD_TYPE);
								eleOrderHoldType.setAttribute(VSIConstants.ATTR_HOLD_TYPE,VSIConstants.STORE_RECEIVE_HOLD);
								eleOrderHoldType.setAttribute(VSIConstants.ATTR_REASON_TEXT,"Store Received OrderLine Hold");
								eleOrderHoldType.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_RESOLVED);
								eleOrderHoldTypes.appendChild(eleOrderHoldType);
								eleOrderLines.appendChild(eleOrdrLine);	
							}
							if(log.isDebugEnabled()){
								log.debug("changeOrder Input XML"+XmlUtils.getString(docChangeOrder ));
							}
							//Calling the changeOrder API to resolve STORE_RECEIVED_HOLD	
							Document docChangeOrderOutput=VSIUtils.invokeAPI(env, VSIConstants.TEMPLATE_CHANGE_ORDER, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
							if(log.isDebugEnabled()){
								log.debug("Printing chageOrder output XML :" + XmlUtils.getString(docChangeOrderOutput));
							}
						}
					}
					}
					else
					{
						if(log.isDebugEnabled()){
							log.debug("No OrderLines are eligible for Hold Resolution");
						}
					}
				}

			}
				catch (Exception e){
					e.printStackTrace();
				}
				log.endTimer("VSIResolveStoreReceivedHoldOnCancellation.resolveStoreReceivedHold : END");	
			}
		}
