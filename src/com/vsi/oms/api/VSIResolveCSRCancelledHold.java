package com.vsi.oms.api;

import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;
// this class is used for resolving CSR_CANCELLED_HOLD hold
public class VSIResolveCSRCancelledHold {
	private YFCLogCategory log = YFCLogCategory.instance(VSIResolveCSRCancelledHold.class);
	YIFApi api;
	public void resolveCSRHold(YFSEnvironment env, Document inDoc) {


		if(log.isDebugEnabled()){
			log.debug("VSIResolveCSRCancelledHold : input : " + XMLUtil.getXMLString(inDoc));
		}
		String strNewChangeFlag=null;
		try {

			ArrayList<Element> listWavingFlag;
			listWavingFlag = VSIUtils.getCommonCodeList(env, "VSI_WAVING_FLAG", "NEWCHANGE", "DEFAULT");
			if(!listWavingFlag.isEmpty()){
				Element eleCommonCode=listWavingFlag.get(0);
				strNewChangeFlag=eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
				if(!YFCCommon.isStringVoid(strNewChangeFlag)&&VSIConstants.FLAG_Y.equalsIgnoreCase(strNewChangeFlag)){
					Element eleShipment = inDoc.getDocumentElement();
					String strDocumentType = eleShipment.getAttribute(VSIConstants.ATTR_DOCUMENT_TYPE);
					String strOrderHeaderKey = eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					if(VSIConstants.ATTR_DOCUMENT_TYPE_SALES.equalsIgnoreCase(strDocumentType)){

						Document getOrderListInput = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						Element eleOrder = getOrderListInput.getDocumentElement();
						eleOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY, strOrderHeaderKey);

						//Calling the getOrderList API 
						api = YIFClientFactory.getInstance().getApi();
						env.setApiTemplate(VSIConstants.API_GET_ORDER_LIST, "global/template/api/VSIGetOrderListForOrderHold.xml");
						Document getOrderListOp = api.invoke(env, VSIConstants.API_GET_ORDER_LIST,getOrderListInput);
						env.clearApiTemplate(VSIConstants.API_GET_ORDER_LIST);
						NodeList nlOrderHoldTypeList = XMLUtil.getNodeListByXpath(
								getOrderListOp, "/OrderList/Order/OrderHoldTypes/OrderHoldType");
						if(!YFCCommon.isVoid(nlOrderHoldTypeList)){
							for(int i1=0;i1<nlOrderHoldTypeList.getLength();i1++){
								Element eleOrderHoldType = (Element)nlOrderHoldTypeList.item(i1);	
								String strStatus = eleOrderHoldType.getAttribute(VSIConstants.ATTR_STATUS);
								String strHoldType = eleOrderHoldType.getAttribute(VSIConstants.ATTR_HOLD_TYPE);

								if(VSIConstants.STATUS_CREATE.equals(strStatus) && VSIConstants.CSR_CANCELLED_HOLD.equals(strHoldType)){
									//frame change order api input for resolving hold
									Document docProcessedHold =  XMLUtil.createDocument(VSIConstants.ELE_ORDER);
									Element eleOrderHold =docProcessedHold.getDocumentElement();
									eleOrderHold.setAttribute(VSIConstants.ATTR_OVERRIDE, VSIConstants.FLAG_Y);
									eleOrderHold.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
									Element eleOrderHoldTypes = SCXmlUtil.createChild(eleOrderHold, VSIConstants.ELE_ORDER_HOLD_TYPES);
									Element eleOrderHoldType1 = SCXmlUtil.createChild(eleOrderHoldTypes, VSIConstants.ELE_ORDER_HOLD_TYPE);
									eleOrderHoldType1.setAttribute(VSIConstants.ATTR_HOLD_TYPE,
											VSIConstants.CSR_CANCELLED_HOLD);
									eleOrderHoldType1.setAttribute(VSIConstants.ATTR_STATUS, VSIConstants.STATUS_RESOLVED);
									eleOrderHoldType1.setAttribute(VSIConstants.ATTR_REASON_TEXT,
											"CSR Cancelled Request Hold");
									if(log.isDebugEnabled()){
										log.info("Printing processedHoldDoc: "
												+ XMLUtil.getXMLString(docProcessedHold));
									}
									VSIUtils.invokeAPI(env, VSIConstants.API_CHANGE_ORDER, docProcessedHold);	
									break;
								}
							}

						}
					}
				}
			}
		}catch (YFSException e) {
			e.printStackTrace();
		}catch(Exception e){
			e.printStackTrace();

		}
	}
}