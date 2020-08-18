package com.vsi.oms.api;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

//import com.sterlingcommerce.tools.datavalidator.XmlUtils;
import com.vsi.oms.utils.VSIConstants;
import com.vsi.oms.utils.XMLUtil;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFClientCreationException;
import com.yantra.interop.japi.YIFClientFactory;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.dom.YFCElement;
import com.yantra.yfc.dom.YFCNodeList;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;
import com.yantra.yfs.japi.YFSException;

public class VSICalendarUpdates {

	private YFCLogCategory log = YFCLogCategory
			.instance(VSICalendarUpdates.class);

	/**
	 * 
	 * @param env
	 *            Required. Environment handle returned by the createEnvironment
	 *            API. This is required for the user exit to either raise errors
	 *            or access environment information.
	 * @param inXML
	 *            Required. This contains the input message from ESB.
	 * @return
	 * 
	 * @throws Exception
	 *             This exception is thrown whenever system errors that the
	 *             application cannot handle are encountered. The transaction is
	 *             aborted and changes are rolled back.
	 */
	public void calendarUpdates(YFSEnvironment env, Document inXML)
			throws Exception {

		try {

			Element rootElement = inXML.getDocumentElement();
			String calendarId = rootElement.getAttribute("CalendarId");
			rootElement.setAttribute("OrganizationCode", calendarId);
			String SCalendarKey = rootElement.getAttribute("CalendarKey");
			/*******************************************************************
			 * *****CREATING DOCUMENT "inputDoc" FOR getCalendarList API WITH
			 * ONLY THREE ATTRIBUTES CalendarId,OrganizationCode,CalendarKey
			 * inXML DOCUMENT will be used as same.
			 ******************************************************************/
			Document inputDoc = XMLUtil.createDocument("Calendar");
			Element eleTemplate = inputDoc.getDocumentElement();
			eleTemplate.setAttribute("CalendarId", calendarId);
			eleTemplate.setAttribute("CalendarKey", SCalendarKey);
			eleTemplate.setAttribute("OrganizationCode", calendarId);
			env.setApiTemplate("getCalendarList",
					"global/template/api/VSIGetCalendarList.xml");
			YIFApi api = YIFClientFactory.getInstance().getApi();
			Document calendarListOutDoc = api.invoke(env, "getCalendarList",
					inputDoc);
			if (log.isVerboseEnabled()) {
				log.verbose(" output : \n"
						+ XMLUtil.getXMLString(calendarListOutDoc));
			}

			env.clearApiTemplate("getCalendarList");
			Element calendarList = (Element) calendarListOutDoc
					.getElementsByTagName("Calendars").item(0);
			String totalNoOfrecords = calendarList
					.getAttribute("TotalNumberOfRecords");
			if (totalNoOfrecords.equalsIgnoreCase("0")) {
				api.invoke(env, "createCalendar", inXML);

			}

			else {
				NodeList shiftList = XMLUtil
						.getNodeListByXpath(calendarListOutDoc,
								"Calendars/Calendar/EffectivePeriods/EffectivePeriod/Shifts/Shift");
				if (shiftList.getLength() > 0) {
					for (int i = 0; i < shiftList.getLength(); i++) {
						Element shiftElement = (Element) shiftList.item(i);
						String shiftKey = shiftElement.getAttribute("ShiftKey");
						String shiftName = shiftElement
								.getAttribute("ShiftName");

						NodeList calendarshiftList = XMLUtil
								.getNodeListByXpath(inXML,
										"Calendar/EffectivePeriods/EffectivePeriod/Shifts/Shift");
						for (int k = 0; k < calendarshiftList.getLength(); k++) {
							Element calendarShiftElement = (Element) calendarshiftList
									.item(k);
							String calendarShiftName = calendarShiftElement
									.getAttribute("ShiftName");
							String calendarShiftKey = null;
							if (calendarShiftName.equalsIgnoreCase(shiftName)) {
								calendarShiftKey = shiftKey;
								Element eleShift = (Element) inXML
										.getElementsByTagName("Shift").item(k);
								eleShift.setAttribute("ShiftKey",
										calendarShiftKey);
							}
						}
					}

					YIFApi api2 = YIFClientFactory.getInstance().getApi();
					if (log.isVerboseEnabled()) {
						log.verbose("InXML document : \n"
								+ XMLUtil.getXMLString(inXML));
					}
					api.invoke(env, "changeCalendar", inXML);
				} else {
					YIFApi api3 = YIFClientFactory.getInstance().getApi();
					if (log.isVerboseEnabled()) {
						log.verbose("InXML document : \n"
								+ XMLUtil.getXMLString(inXML));
					}
					api.invoke(env, "changeCalendar", inXML);
				}

			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR", "Parse Error");

		} catch (YFSException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR", "Random Error");
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new YFSException("EXTN_ERROR", "EXTN_ERROR",
					"Remote Exception");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
