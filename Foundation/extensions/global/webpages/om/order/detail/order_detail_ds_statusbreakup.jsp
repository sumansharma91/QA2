<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    window.attachEvent("onload", IgnoreChangeNames);
    document.body.attachEvent("onunload", processSaveRecordsForOrder);
</script>

<table class="table" width="100%" ID="StatusBreakup">
<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/@OrderHeaderKey", "xml:/OrderLineStatusList/OrderStatus/@OrderHeaderKey")%>/>
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonCode" />
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonText"/>
<input type="hidden" name="xml:/ChangeSchedule/@Override" value="N"/>
<thead>
    <tr>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>

        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
		<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
        
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusDate")%>"><yfc:i18n>Last_Changed_On</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@PromisedApptStartDate")%>"><yfc:i18n>Appointment</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
		
	<% if (equals(getValue("OrderStatus","xml:/OrderStatus/OrderLine/@ItemGroupCode"), "DS") )
		{%>
			<tr>
				<yfc:makeXMLInput name="orderLineKey">
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderStatus/OrderLine/@OrderLineKey"/>
				</yfc:makeXMLInput>
				<yfc:makeXMLInput name="orderReleaseKey">
					<yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderStatus/@OrderReleaseKey" />
				</yfc:makeXMLInput>
				<td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderLine/@PrimeLineNo")%>">
					<% if(showOrderLineNo("Order","Order")) {%>
						<a <%=getDetailHrefOptions("L01",getParameter("orderLineKey"),"")%>>
							<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/></a>
					<%} else {%>
						<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
					<%}%>
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderStatus/@OrderLineKey")%>/>
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderReleaseKey", "xml:/OrderStatus/@OrderReleaseKey")%>/>
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromStatus", "xml:/OrderStatus/@Status")%>/>
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromTagNo", "xml:/OrderStatus/Schedule/@TagNumber")%>/>
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineScheduleKey", "xml:/OrderStatus/Schedule/@OrderLineScheduleKey")%>/>
				</td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemID"/></td>

				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@UnitOfMeasure"/></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ShipNode"/></td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/@StatusDate")%>">
					<yfc:getXMLValue binding="xml:/OrderStatus/@StatusDate" />
				</td>
				<td nowrap="true" class="tablecolumn"><%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%></td>

				<td nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/@StatusQty")%>">
					<yfc:getXMLValue binding="xml:/OrderStatus/@StatusQty"/>
				</td>
				<td class="tablecolumn">
				<%
					String sTimeWindow = displayTimeWindow(resolveValue("xml:/OrderStatus/OrderLine/@PromisedApptStartDate"), resolveValue("xml:/OrderStatus/OrderLine/@PromisedApptEndDate"), resolveValue("xml:/OrderStatus/OrderLine/@Timezone") );
					
					if (!isVoid(sTimeWindow)){
				%>
						<%=sTimeWindow%>
						<%=showTimeZoneIcon(resolveValue("xml:/OrderStatus/OrderLine/@Timezone"), getLocale().getTimezone())%>
				<%	} %>
				</td>
			</tr>
		<%}%>
    </yfc:loopXML>
</tbody>
</table>
