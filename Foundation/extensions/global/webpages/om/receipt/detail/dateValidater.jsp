<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.*" %>

<%
	
	String inputStr = request.getParameter("YFCInput");
	YFCDocument inDoc = null;
	YFCElement inElem = null;
	YFCElement tempElem = null;
	
	if(!isVoid(inputStr)) {
		inDoc = YFCDocument.parse(inputStr);
		inElem = inDoc.getDocumentElement();

		if(inElem != null) {

            String sDate = inElem.getAttribute("Date");
			// Defect ID : 159104 :The boolean passed in YDate is ignoreTime. ignoreTime When true, the time component of a date is ignored. The time is set to 00:00:00. This is because when expiration date is added in YFS_LOCATION_INVENTORY table, it goes with 00:00:00 time set. YDate may return incorrect date if time is added to Date.
			YDate yd = new YDate(sDate, getLocale(),true);
			if(!YFCCommon.isVoid(yd.getString())) {
				inElem.setAttribute("ShipByDate",yd.getString());
			}

		}
	}

	response.getWriter().write(inElem.getString());
	response.getWriter().flush();
	response.getWriter().close();

%>
