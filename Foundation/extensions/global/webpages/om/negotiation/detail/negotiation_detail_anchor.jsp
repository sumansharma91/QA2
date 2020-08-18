<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<%
    boolean isInitiator = false;
    boolean showNegotiationHeaderAttributes = false;
    boolean showNegotiationLines = false;
    boolean showNegotiatedLines = false;

	String loggedInOrg = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
	String initiatorOrg = getValue("Negotiations", "xml:/Negotiations/Negotiation/@InitiatorOrgCode");

	if (equals(loggedInOrg, initiatorOrg))
		isInitiator = true;
	else
		isInitiator = false;

	String panelName = "";
	
	YFCElement negotiableLineAttrs = getNegotiableLineAttributes((YFCElement) request.getAttribute("Negotiations"));

	String itemIdSplit = negotiableLineAttrs.getAttribute("ItemID");
	String productClassSplit = negotiableLineAttrs.getAttribute("ProductClass");
	String unitOfMeasureSplit = negotiableLineAttrs.getAttribute("UnitOfMeasure");
	String freightTermsSplit = negotiableLineAttrs.getAttribute("FreightTerms");
	String shipDateSplit = negotiableLineAttrs.getAttribute("ShipDate");
	String deliveryDateSplit = negotiableLineAttrs.getAttribute("DeliveryDate");
	String priceSplit =  negotiableLineAttrs.getAttribute("Price");

	//Find the correct driver date from the order and create the title
	String driverDateTitle = "";
	String driverDate = getValue("OrderDetails", "xml:/Order/@DriverDate");
	if (equals(driverDate, "02")) {
		driverDateTitle = getI18N("Negotiation_Lines_(Quantity_By_Delivery_Date)");	
	} else if (equals(driverDate, "01")) {
		driverDateTitle = getI18N("Negotiation_Lines_(Quantity_By_Ship_Date)");	
	} else {
		driverDateTitle = getI18N("Negotiation_Lines_(Quantity_By_Date)");		
	}
%>

<%
	// If at least one Negotiable Line Attribute exists show normal detail lines panel, else show datepanel detail lines panel.
	if ( !equals(itemIdSplit,"NULL") || !equals(productClassSplit,"NULL") || !equals(unitOfMeasureSplit,"NULL") || !equals(freightTermsSplit,"NULL") || !equals(shipDateSplit,"NULL") || !equals(priceSplit,"NULL")) {
		panelName = "I03";
	}
	else {
		panelName = "I04";
	}
%>

<% // Find out if Negotiation Header Attributes panel should be shown. %>
<yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationRule/NegotiableHdrAttrs/@NegotiableHdrAttr" id="NegotiableHdrAttr">
	<% showNegotiationHeaderAttributes = true; %>
</yfc:loopXML>

<% // Find out if Negotiation Lines and Negotiated Lines panels should be shown. %>
<yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationLines/@NegotiationLine" id="NegotiationLine">

	<yfc:hasXMLNode binding="xml:/NegotiationLine/LastInitiatorLineResponse/LineTerms/LineTermsDetail">
		<% showNegotiationLines = true; %>
	</yfc:hasXMLNode>

	<yfc:hasXMLNode binding="xml:/NegotiationLine/LastNegotiatorLineResponse/LineTerms/LineTermsDetail">
		<% showNegotiationLines = true; %>
	</yfc:hasXMLNode>

	<yfc:hasXMLNode binding="xml:/NegotiationLine/NegotiatedLineResponse/LineTerms/LineTermsDetail">
		<% showNegotiatedLines = true; %>
	</yfc:hasXMLNode>
</yfc:loopXML>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>

	<% if (showNegotiationHeaderAttributes) { %>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
	<% } %>
	
	<% if (showNegotiationLines && equals(panelName,"I04")) { %>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value='<%=panelName%>'/>
				<jsp:param name="Title" value='<%=driverDateTitle%>'/>
			</jsp:include>
		</td>
	</tr>
	<% } else if (showNegotiationLines) {%>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value='<%=panelName%>'/>
			</jsp:include>
		</td>
	</tr>
	<% } %>

	<% if (showNegotiatedLines) { %>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I05"/>
			</jsp:include>
		</td>
	</tr>
	<% } %>
</table>
