<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>

<%
    boolean isInitiator = false;

	String loggedInOrg = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
	String initiatorOrg = getValue("OrderNegotiationList", "xml:/Negotiations/Negotiation/@InitiatorOrgCode");

	if (equals(loggedInOrg, initiatorOrg))
		isInitiator = true;
	else
		isInitiator = false;
%>
<% if (isInitiator) { %>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I02"/>
			</jsp:include>
		</td>
	</tr>
<% } else { %>
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I03"/>
			</jsp:include>
		</td>
	</tr>
<% } %>

</table>
	
