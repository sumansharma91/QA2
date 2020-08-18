package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.VSIUtils;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSIShipmentRiskified {
	private YFCLogCategory log = YFCLogCategory.instance(VSIShipmentRiskified.class);
	YIFApi api;

	/**
	 * @param env
	 * @param inXML
	 * 
	 * This class is invoked on Create and Confirm Shipment OnSuccess
	 * Event.
	 */
	public void fullShipmentRiskified(YFSEnvironment env, Document inXML) {
		log.debug("fullShipmentRiskified input is : " + XMLUtil.getXMLString(inXML));
		try {
			Element eleShipment = inXML.getDocumentElement();
			Element eleShipmentLines = SCXmlUtil.getChildElement(eleShipment,
					VSIConstants.ELE_SHIPMENT_LINES);
			Element eleFistShipmentLine=SCXmlUtil.getFirstChildElement(eleShipmentLines);
			Element eleOrderIp=SCXmlUtil.getChildElement(eleFistShipmentLine, VSIConstants.ELE_ORDER);
			String strEntryType = SCXmlUtil.getAttribute(eleOrderIp, VSIConstants.ATTR_ENTRY_TYPE);
			boolean isRiskifiedCallRequired = true;
			boolean isBOPUSOrSTS = false;
			// strEntryType not POS, Marketplace and wholesale order.
			if (!YFCObject.isVoid(strEntryType) && !VSIConstants.POS_ENTRY_TYPE.equalsIgnoreCase(strEntryType)
					&& !VSIConstants.MARKETPLACE.equalsIgnoreCase(strEntryType)
					&& !VSIConstants.WHOLESALE.equalsIgnoreCase(strEntryType)) {
				//  Check Common code flag for SHIPMENTS API enable
				ArrayList<Element> listShipmentFlag = VSIUtils.getCommonCodeList(env,
						VSIConstants.ATTR_RISKIFIED_CODE_TYPE, VSIConstants.ATTR_FULFILL_SHIPMENTS,
						VSIConstants.ATTR_DEFAULT);
				if (!listShipmentFlag.isEmpty()) {
					Element eleCommonCode = listShipmentFlag.get(0);
					String strShipmentFlag = eleCommonCode.getAttribute(VSIConstants.ATTR_CODE_SHORT_DESCRIPTION);
					if (!YFCCommon.isStringVoid(strShipmentFlag)
							&& VSIConstants.FLAG_Y.equalsIgnoreCase(strShipmentFlag)) {
						
						String strOrderHeaderKey = eleShipment.getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
						Document getOrderListIp = XMLUtil.createDocument(VSIConstants.ELE_ORDER);
						getOrderListIp.getDocumentElement().setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,
								strOrderHeaderKey);
						
						List<Element> listShipmentLine = new ArrayList<Element>();
						listShipmentLine = SCXmlUtil.getElements(eleShipmentLines, VSIConstants.ELE_SHIPMENT_LINE);
						
						for (Element eleShipmentLineIp : listShipmentLine) {
							Element eleOrderLine = SCXmlUtil.getChildElement(eleShipmentLineIp,
									VSIConstants.ELE_ORDER_LINE);
							String strLineType = SCXmlUtil.getAttribute(eleOrderLine, VSIConstants.ATTR_LINE_TYPE);
							// Check for Line type- Ship to Store or Pick In Store Orders
							if (VSIConstants.LINETYPE_PUS.equalsIgnoreCase(strLineType)
									|| VSIConstants.LINETYPE_STS.equalsIgnoreCase(strLineType)) {
								isRiskifiedCallRequired = true;
								isBOPUSOrSTS = true;
							}
						}
						if (!isBOPUSOrSTS) {
							// Invoke getOrderList API
							Document getOrderListOp = VSIUtils.invokeAPI(env,
									"global/template/api/VSIGetOrderListSO_Riskified.xml",
									VSIConstants.API_GET_ORDER_LIST, getOrderListIp);
							Element eleOrderListOp = getOrderListOp.getDocumentElement();
							Element eleOrderOp = SCXmlUtil.getChildElement(eleOrderListOp, VSIConstants.ELE_ORDER);
							Element eleOrderHoldTypes = SCXmlUtil.getChildElement(eleOrderOp,
									VSIConstants.ELE_ORDER_HOLD_TYPES);
							List<Element> listOrderHoldType = SCXmlUtil.getElements(eleOrderHoldTypes,
									VSIConstants.ELE_ORDER_HOLD_TYPE);
							for (Element eleOrderHoldType : listOrderHoldType) {
								String strHoldType = SCXmlUtil.getAttribute(eleOrderHoldType,
										VSIConstants.ATTR_HOLD_TYPE);
								if ((VSIConstants.HOLD_FRAUD_HOLD.equalsIgnoreCase(strHoldType)
										|| VSIConstants.VSI_KOUNT_STH_HOLD.equalsIgnoreCase(strHoldType))) {
									isRiskifiedCallRequired = true;
								} else {
									isRiskifiedCallRequired = false;
								}
							}
						}
						if(isRiskifiedCallRequired){
							//Call the service to post the input XML to queue for preparing Riskified Shipment request
							VSIUtils.invokeService(env, "VSIPostRiskifiedShipmentRequest", inXML);
						}
					}
				}
			}
		} catch (

		YFSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}