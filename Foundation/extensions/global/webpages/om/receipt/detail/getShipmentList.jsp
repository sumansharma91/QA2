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
		tempElem = YFCDocument.parse("<Shipments TotalNumberOfRecords=\"\"><Shipment BolNo=\"\" BuyerOrganizationCode=\"\" DocumentType=\"\" DoNotVerifyCaseContent=\"\" DoNotVerifyPalletContent=\"\" EnterpriseCode=\"\" ExpectedDeliveryDate=\"\" OrderHeaderKey=\"\" OrderNo=\"\" OrderReleaseKey=\"\" PodNo=\"\" ProNo=\"\" ReceivingNode=\"\" ReleaseNo=\"\" SCAC=\"\" SealNo=\"\" SellerOrganizationCode=\"\" ShipNode=\"\" ShipmentNo=\"\" ShipmentKey=\"\" TrackingNo=\"\" TrailerNo=\"\"></Shipment></Shipments>").getDocumentElement();
	}
	
%>
	
	<yfc:callAPI apiName="getShipmentList" inputElement="<%=inElem%>" 
	templateElement="<%=tempElem%>" outputNamespace=""/>

<%
	YFCElement outElem = (YFCElement)request.getAttribute("Shipments");
	response.getWriter().write(outElem.getString());
	response.getWriter().flush();
	response.getWriter().close();
%>
