package com.vsi.oms.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Iterator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sterlingcommerce.baseutil.SCXmlUtil;
import com.vsi.oms.utils.VSIConstants;

import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfc.util.YFCCommon;
import com.yantra.yfs.japi.YFSEnvironment;
// this class is used for checking if order is eligible for release
public class VSICheckReleaseEligibility {
	private YFCLogCategory log = YFCLogCategory.instance(VSICheckReleaseEligibility.class);
	YIFApi api;

	public void checkReleaseEligible (YFSEnvironment env, Document docInput) {
		log.beginTimer("VSICheckReleaseEligibility.checkReleaseEligible : START");

		if ( !YFCCommon.isVoid(docInput)) {
			String strOrderHeaderKey=null;
			try {
				    Element eleOrder = docInput.getDocumentElement();
					strOrderHeaderKey = docInput.getDocumentElement().getAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY);
					if(!YFCObject.isVoid(eleOrder)){

						Element eleOrderLines = SCXmlUtil.getChildElement(eleOrder, VSIConstants.ELE_ORDER_LINES);
						Iterator<Element> itrOrderLines = SCXmlUtil.getChildren(eleOrderLines);
						Element eleOrderLine=null;
						Element eleOrderStatuses=null;
						if (!YFCObject.isVoid(itrOrderLines)) {
							while (itrOrderLines.hasNext()) {
								eleOrderLine = itrOrderLines.next();
								eleOrderStatuses = SCXmlUtil.getChildElement(eleOrderLine, VSIConstants.ELE_ORDER_STATUSES);
								Iterator<Element> itrOrderstatus = SCXmlUtil.getChildren(eleOrderStatuses);
								String strStatus=null;
								String strExpectedShipmentDate=null;
								Element eleOrderStatus=null;
								if (!YFCObject.isVoid(itrOrderstatus)) {
									while (itrOrderstatus.hasNext()) {
										eleOrderStatus = itrOrderstatus.next();
										strStatus=eleOrderStatus.getAttribute(VSIConstants.ATTR_STATUS);
										String strExpectedShipmentDateFormat=null;
										Element eleDeatils=null;
										if("1500".equalsIgnoreCase(strStatus)){
											eleDeatils= (Element) eleOrderStatus.getElementsByTagName(VSIConstants.ELE_DETAILS).item(0);
											if (!YFCObject.isVoid(eleDeatils)) {
												strExpectedShipmentDate=eleDeatils.getAttribute(VSIConstants.ATTR_EXPECTED_SHIPMENT_DATE);

												strExpectedShipmentDateFormat=strExpectedShipmentDate.substring(0, strExpectedShipmentDate.indexOf("T")).replaceAll("-", "");
												
												DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
												Date date = new Date();
												String currentdate = dateFormat.format(date);
												Date dcurrentdate = dateFormat.parse(currentdate);
												Date dstrExpectedShipmentDateFormat = dateFormat.parse(strExpectedShipmentDateFormat);
												if(dstrExpectedShipmentDateFormat.before(dcurrentdate)||dstrExpectedShipmentDateFormat.equals(dcurrentdate)){
													Document docReleaseOrderInput = XMLUtil.createDocument("ReleaseOrder");
													Element eleReleaseOrder = docReleaseOrderInput.getDocumentElement();
													eleReleaseOrder.setAttribute(VSIConstants.ATTR_ORDER_HEADER_KEY,strOrderHeaderKey);
													api = YIFClientFactory.getInstance().getApi();
													api.executeFlow(env, "VSIReleaseOrder_Q", docReleaseOrderInput);
													break;

												}

											}					}
									}
								}
							}
						}
					}
				}
						catch (Exception e) {
				log.error("Exception in VSICheckReleaseEligibility.checkReleaseEligible() : " , e);

			}
		}
	}
}
