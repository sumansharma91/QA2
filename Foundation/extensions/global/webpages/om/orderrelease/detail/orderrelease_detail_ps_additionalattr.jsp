<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>


<table class="view" width="100%">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Node</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"></yfc:getXMLValue></td>

	<td class="detaillabel" >
		<yfc:i18n>Line_#</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue binding="xml:/OrderRelease/OrderLine/@PrimeLineNo"></yfc:getXMLValue>
	</td>
</tr>

<tr>
	<td class="detaillabel" >
		<yfc:i18n>Status</yfc:i18n>
	</td>
	<td class="protectedtext">
		<%=displayOrderStatus(getValue("OrderRelease","xml:/OrderRelease/OrderLine/@MultipleStatusesExist"),getValue("OrderRelease","xml:/OrderRelease/OrderLine/@MaxLineStatusDesc"),true)%>
	</td>

    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/OrderLine/Item/@ItemID"></yfc:getXMLValue>
    </td>
</tr>

<tr>
	<td class="detaillabel" >
		<yfc:i18n>Description</yfc:i18n>
	</td>
	<td class="protectedtext" colspan="3">
		<yfc:getXMLValue binding="xml:/OrderRelease/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
	</td>
</tr>

<tr>
	<td class="detaillabel" >
		<yfc:i18n>Appointment</yfc:i18n>
	</td>

	<td class="protectedtext">
	<%-- cr 34417 --%>
	<%
		String sTimeWindow = displayTimeWindow(resolveValue("xml:/OrderRelease/OrderLine/@PromisedApptStartDate"), resolveValue("xml:/OrderRelease/OrderLine/@PromisedApptEndDate"), resolveValue("xml:/OrderRelease/OrderLine/@Timezone") );
		
		if(!isVoid(sTimeWindow) )
		{
	%>
			<%=sTimeWindow%>
			<%=showTimeZoneIcon(resolveValue("xml:/OrderRelease/OrderLine/@Timezone"), getLocale().getTimezone())%>
	<%	} %>
	</td>
	<td class="detaillabel" >
		<yfc:i18n>Appointment_Status</yfc:i18n>
	</td>
	<td class="protectedtext">
		<%=getI18N(resolveValue("xml:/OrderRelease/OrderLine/@ApptStatus") )%>
	</td>
</tr>

</table>
