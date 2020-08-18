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
		inElem.setAttribute("TransactionId","RECEIVE_RECEIPT");
        inElem.setAttribute("OpenReceiptFlag","Y");
		tempElem = YFCDocument.parse("<Receipts><Receipt/></Receipts>").getDocumentElement();
	}

%>

	<yfc:callAPI apiName="findReceipt" inputElement="<%=inElem%>" 
	templateElement="<%=tempElem%>" outputNamespace=""/>

<%
	
	YFCElement outElem = (YFCElement)request.getAttribute("Receipts");
	response.getWriter().write(outElem.getString());
	response.getWriter().flush();
	response.getWriter().close();

%>
