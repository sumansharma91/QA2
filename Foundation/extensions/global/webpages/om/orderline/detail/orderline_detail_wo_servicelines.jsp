<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
    document.body.attachEvent("onunload", processSaveRecordsForOrderLines);
</script>
<table class="table" ID="OrderLines" cellspacing="0" width="100%" >
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true" style="width:10px">&nbsp;</td>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/WorkOrderServiceLine/@ItemID")%>"><yfc:i18n>Item_Related_Info</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/WorkOrderServiceLine/@ItemGroupCode")%>"><yfc:i18n>Item_Group_Code</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderServiceLine/@QuantityRequired")%>"><yfc:i18n>Required_Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="WorkOrder" binding="xml:/WorkOrder/WorkOrderServiceLines/@WorkOrderServiceLine" id="WorkOrderServiceLine">
			<tr>
				<yfc:makeXMLInput name="ServiceWorkOrderLineKey">
					<yfc:makeXMLKey binding="xml:/WorkOrderLineDetail/@WorkOrderServiceLineKey" value="xml:/WorkOrderServiceLine/@WorkOrderServiceLineKey"/>
					<yfc:makeXMLKey binding="xml:/WorkOrderLineDetail/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey"/>
				</yfc:makeXMLInput>
				<td style="valign:center;align:center" nowrap="true">
					<% if (equals(getValue("WorkOrderServiceLine","xml:/WorkOrderServiceLine/@IsComplete"),"Y") ){ %>
						<img <%=getImageOptions(request.getContextPath() + "/console/icons/appointmentready.gif", "Work_Order_Quantity_Completed")%>/>
					<% } %>
				</td>

				<yfc:makeXMLInput name="orderLineKey">
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/WorkOrderServiceLine/OrderLine/@OrderLineKey"/>
				</yfc:makeXMLInput>

				<% String itemId = resolveValue("xml:/WorkOrderServiceLine/@ItemID"); %>
				<td class="tablecolumn" sortValue="<%=itemId%>">
					<yfc:getXMLValue binding="xml:/WorkOrderServiceLine/@ItemID"/><br/>
					<yfc:getXMLValue binding="xml:/WorkOrderServiceLine/@ItemShortDescription"/>
				</td>
				<%
				String serviceItemGroupCodeLabel = "Service_Item_Group_Code_" +
								getValue("WorkOrderServiceLine", "xml:/WorkOrderServiceLine/@ItemGroupCode");
				%>
				<td class="tablecolumn">
					<yfc:i18n><%=serviceItemGroupCodeLabel%></yfc:i18n>
				</td>
				<td class="numerictablecolumn" nowrap="true" sortValue="<%=getNumericValue("xml:WorkOrderServiceLine:/WorkOrderServiceLine/@QuantityRequired")%>">
					<yfc:getXMLValue binding="xml:/WorkOrderServiceLine/@QuantityRequired"/>
				</td>
			</tr>
        </yfc:loopXML>
	</tbody>
</table>
