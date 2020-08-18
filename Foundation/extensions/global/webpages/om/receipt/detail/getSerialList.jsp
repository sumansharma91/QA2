<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	
	String inputStr = request.getParameter("YFCInput");
	YFCDocument inDoc = null;
	YFCElement inElem = null;
	YFCElement tempElem = null;

	if (!isVoid(inputStr)) {
		inDoc = YFCDocument.parse(inputStr);
		inElem = inDoc.getDocumentElement();
		tempElem = YFCDocument.parse("<SerialList TotalNumberOfRecords=\"\" > <Serial SecondarySerial1=\"\" SecondarySerial2=\"\" 	    SecondarySerial3=\"\" SecondarySerial4=\"\" SecondarySerial5=\"\" SecondarySerial6=\"\"  SecondarySerial7=\"\" SecondarySerial8=\"\" SecondarySerial9=\"\" SerialNo=\"\"  /></SerialList>").getDocumentElement();
	}

%>

	<yfc:callAPI apiName="getSerialList" inputElement="<%=inElem%>" 
	templateElement="<%=tempElem%>" outputNamespace=""/>

<%
	
	YFCElement outElem = (YFCElement)request.getAttribute("SerialList");
	response.getWriter().write(outElem.getString());
	response.getWriter().flush();
	response.getWriter().close();

%>
