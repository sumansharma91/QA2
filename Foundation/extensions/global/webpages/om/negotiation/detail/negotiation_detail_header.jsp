<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>

<%
    boolean isInitiator = isInitiatorOrganization((YFCElement) request.getAttribute("Negotiations"));
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@DocumentNo"/></td>
        <td class="detaillabel" ><yfc:i18n>Negotiation_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@NegotiationNo"/></td>
        <td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
        <td class="protectedtext"> <%=displayOrderStatus("N",getValue("Negotiations","xml:/Negotiations/Negotiation/@StatusDescription"),true)%></td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n>
            <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@NegotiationHeaderKey","xml:/Negotiations/Negotiation/@NegotiationHeaderKey")%> />
            <% if (isInitiator) { %>
                <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@OrganizationCode","xml:/Negotiations/Negotiation/@InitiatorOrgCode")%> />
            <% } else { %>
                <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@OrganizationCode","xml:/Negotiations/Negotiation/@NegotiatorOrgCode")%> />
            <% } %>
            <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@ReasonCode")%> />
            <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@Notes")%> />
            <input type="hidden" <%=getTextOptions("xml:/Responses/Response/@Override")%> />
			<input type="hidden" <%=getTextOptions("xml:/Responses/Response/@EnteredBy", "xml:CurrentUser:/User/@Loginid")%> />
        </td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@EnterpriseKey"/></td>
        <td class="detaillabel" ><yfc:i18n>Initiator</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@InitiatorOrgCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Negotiator</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Negotiations/Negotiation/@NegotiatorOrgCode"/></td>
    </tr>
</table>
