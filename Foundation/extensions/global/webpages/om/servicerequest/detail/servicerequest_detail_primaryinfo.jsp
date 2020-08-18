<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>

<table class="view" width="100%">
	<tr>
		<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
		<input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
		<input type="hidden" name="xml:/Order/@ModificationReasonText"/>
		<input type="hidden" name="xml:/Order/@Override" value="N"/>
		<input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
		<input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
		<input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>
		<input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("OrderLine", "xml:/OrderLine/Order/@DraftOrderFlag")%>'/>
		<input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey","xml:/OrderLine/@OrderHeaderKey")%> />
		<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderLineKey","xml:/OrderLine/@OrderLineKey")%> />

		<yfc:makeXMLInput name="statusKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<yfc:makeXMLInput name="workOrderKey">
			<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/OrderLine/WorkOrders/WorkOrder/@WorkOrderKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<input type="hidden" name="hiddenWorkOrderKey" value='<%=getParameter("workOrderKey")%>' />
		<td class="detaillabel" >
			<yfc:i18n>Order_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<% if(showOrderNo("OrderLine","Order")) {%>
				<a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
					<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
				</a>
			<%} else {%>
				<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
			<%}%>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Line_#</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Status</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%>
		</td>
	</tr>

	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Item_ID</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>UOM</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"></yfc:getXMLValue>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Line_Quantity</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%	if(isTrue("xml:/OrderLine/@IsStandaloneService") )	{	%>
					<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/AllowedModifications")%>/>
			<%	}	else	{	%>
					<yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"></yfc:getXMLValue>
			<%	}	%>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Description</yfc:i18n>
		</td>
		<td class="protectedtext" colspan="3">
			<yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
		</td>
	<%  
		boolean modifyView = equals(request.getParameter("ModifyView"), "true")? true : false;
		if(modifyView)
		{
	%>
		<td class="detaillabel" >
			<yfc:i18n>Document_Type</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description" />
		</td>
	<%  }	%>
	</tr>
	<tr>
		<%
			String timeWindowString = displayTimeWindow(getValue("OrderLine", "xml:/OrderLine/@PromisedApptStartDate"), getValue("OrderLine", "xml:/OrderLine/@PromisedApptEndDate"), resolveValue("xml:/OrderLine/@Timezone") );
		%>
		<td class="detaillabel" >
			<yfc:i18n>Appointment</yfc:i18n>
		</td>
		<td class="protectedtext">
			<%=timeWindowString%>
			<%-- cr 34417 --%>
			<% if(!isVoid(timeWindowString) ) { %>
				<%=showTimeZoneIcon(resolveValue("xml:/OrderLine/@Timezone"), getLocale().getTimezone())%>
			<% } %>
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Ship_Node</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"></yfc:getXMLValue>
		</td>
        <td/>
        <td/>
	</tr>
</table>
