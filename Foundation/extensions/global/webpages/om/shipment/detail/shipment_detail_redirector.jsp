<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String sViewGrp = "YOMD710";	// initial
	if(isTrue("xml:/Shipment/@IsProvidedService") )	
	{
		sViewGrp = "YOMD333";	// initial
	}

	goToDetailView(response, sViewGrp);
%>

