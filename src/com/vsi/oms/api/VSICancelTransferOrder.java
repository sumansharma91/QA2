package com.vsi.oms.api;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCUtil;
import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
//this class is used for cancelling the transfer order if sales order is getting cancelled from call center
public class VSICancelTransferOrder {

	private YFCLogCategory log = YFCLogCategory.instance(VSICancelTransferOrder.class);
	YIFApi api;
	public void vsiCancelTO(YFSEnvironment env, Document XML)
			throws Exception {
		if(log.isDebugEnabled()){
			log.info("================Inside VSICancelTransferOrder================================");
			log.debug("Printing Input XML:" + XmlUtils.getString(XML));	
		}

		if (!SCUtil.isVoid(XML)) {

			try {
				Element eleOrderLine=null;
				String lineType=null;
				String strAction=null;
				Element eleStatusBreakupForCanceledQty=null;
				Element eleCanceledFrom=null;
				String strOrdQty=null;
				String strStatus=null;
				String strOrderHeaderKey=null;
				String strOrderLineKey =null;
				Document docGetOrderListInput=null;
				Element eleGetOrderList=null;
				Element eleOrderLineChild=null;
				Document docChangeOrder=null;
				Element eleChangeOrder =null;
				Element eleChangeOrderLines=null;


				Element eleOrder = XML.getDocumentElement();
				Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
				NodeList nlorderLineList = eleOrderLines.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
				for(int i=0;i<nlorderLineList.getLength();i++){
					eleOrderLine = (Element)nlorderLineList.item(i);	
					lineType = eleOrderLine.getAttribute(VSIConstants.ATTR_LINE_TYPE);
					strAction = eleOrderLine.getAttribute(VSIConstants.ATTR_ACTION);
					eleStatusBreakupForCanceledQty = SCXmlUtil.getChildElement(eleOrderLine,
							VSIConstants.ELE_STATUS_BREAKUP_FOR_CANCELED_QTY);
					eleCanceledFrom = SCXmlUtil.getChildElement(eleStatusBreakupForCanceledQty, VSIConstants.ATTR_CANCELED_FROM);
					strOrdQty = eleOrderLine.getAttribute(VSIConstants.ATTR_ORD_QTY);
					strStatus = eleCanceledFrom.getAttribute(VSIConstants.ATTR_STATUS);
					if("SHIP_TO_STORE".equals(lineType)&&"CANCEL".equals(strAction)&&Double.parseDouble(strStatus)<2160.10){


						strOrderHeaderKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
						strOrderLineKey = eleOrderLine.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);

						docGetOrderListInput = XMLUtil
								.createDocument(VSIConstants.ELE_ORDER);
						eleGetOrderList = docGetOrderListInput
								.getDocumentElement();
						eleOrderLineChild = docGetOrderListInput.createElement(VSIConstants.ELE_ORDER_LINE);
						eleOrderLineChild.setAttribute(
								VSIConstants.ATTR_CHAINED_FROM_ORDER_HEADER_KEY,
								strOrderHeaderKey);
						eleOrderLineChild.setAttribute(VSIConstants.ATTR_CHAINED_FROM_ORDER_LINE_KEY,
								strOrderLineKey);
						eleGetOrderList.appendChild(eleOrderLineChild);

						api = YIFClientFactory.getInstance().getApi();
						env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST,
								"global/template/api/VSIGetOrderListTransferOrder.xml");
						Document NewOutDoc = api.invoke(env,
								VSIConstants.API_GET_ORDER_LIST,
								docGetOrderListInput);
						env.clearApiTemplates();


						NodeList newOrderElement =  NewOutDoc
								.getElementsByTagName(VSIConstants.ELE_ORDER)
								;

						//start forming changeOrder document to cancel the lines.
						docChangeOrder = SCXmlUtil.createDocument(VSIConstants.ELE_ORDER);
						eleChangeOrder = docChangeOrder.getDocumentElement();
						eleChangeOrderLines = SCXmlUtil.createChild(eleChangeOrder, VSIConstants.ELE_ORDER_LINES);
						eleChangeOrder.setAttribute(VSIConstants.ATTR_MODIFICATION_REASON_CODE, "Customer Care Cancel - No Email");
						eleChangeOrder.setAttribute(VSIConstants.ATTR_OVERRIDE, "Y");

						for(int j=0;j<newOrderElement.getLength();j++){
							Element eleOrderLinenl = (Element)newOrderElement.item(j);
							if(log.isDebugEnabled()){
							log.debug("Output for orderLiShipToAddressneEle :" +XMLUtil.getElementString(eleOrderLinenl));
							}
							NodeList nlorderLineList1= eleOrderLinenl.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);
							for(int k=0;k<nlorderLineList1.getLength();k++){
								Element eleOrderLinen = (Element)nlorderLineList1.item(k);
								String strChOrderLineKey1 = eleOrderLinen.getAttribute(VSIConstants.ATTR_CHAINED_FROM_ORDER_LINE_KEY);
								String strStatusDesc = eleOrderLinen.getAttribute(VSIConstants.ATTR_STATUS);


								if((VSIConstants.STATUS_RELEASED.equals(strStatusDesc)||VSIConstants.STATUS_RESERVED.equals(strStatusDesc))&&!VSIConstants.STATUS_CANCELLED.equals(strStatusDesc)&&strChOrderLineKey1.equals(strOrderLineKey)){
									String strOrderLineKey1 = eleOrderLinen.getAttribute(VSIConstants.ATTR_ORDER_LINE_KEY);
									String strOHK=eleOrderLinenl.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
									eleChangeOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOHK );
									Element eleChangeOrderLine = SCXmlUtil.createChild(eleChangeOrderLines, VSIConstants.ELE_ORDER_LINE);
									eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORDER_LINE_KEY, strOrderLineKey1);
									eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ACTION, VSIConstants.ACTION_CAPS_CANCEL);			
									eleChangeOrderLine.setAttribute(VSIConstants.ATTR_ORD_QTY,strOrdQty);

								}

							}



						}
						NodeList nlchangeOrderLineList = eleChangeOrderLines
								.getElementsByTagName(VSIConstants.ELE_ORDER_LINE);

						if (nlchangeOrderLineList.getLength() > 0){
							VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docChangeOrder);
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
