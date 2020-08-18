<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/inventory.jspf" %>

<script language="javascript" src="../console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">
    document.body.attachEvent("onunload", processSaveForAcceptRejectOrderLines);
	window.attachEvent("onload", attachActionQtyEvents);
</script>

<%if(isShipNodeUser()){%>

<table class="view" width="30%">
    <tbody>

        <tr>
			<td class="detaillabel" nowrap="true">
					<yfc:i18n>Procure_From_Node</yfc:i18n>
			 </td>
			<td class="protectedtext">
				<yfc:getXMLValue binding="xml:CurrentUser:/User/@Node"></yfc:getXMLValue>
			</td>
		</tr>
    </tbody>
</table>

<table class="table" ID="OrderStatusLines" cellspacing="0" width="100%" >
    <thead>
        <tr>
			<td class=tablecolumnheader nowrap="true"><yfc:i18n>Order_#/Line_#</yfc:i18n></td>
			<td class=tablecolumnheader nowrap="true"><yfc:i18n>Item_ID</yfc:i18n></td>
	        <td class=tablecolumnheader nowrap="true"><yfc:i18n>PC</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true"><yfc:i18n>UOM</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Ship_Node</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true"><yfc:i18n>Quantity</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no"><yfc:i18n>Accept_Reject_Action</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" sortable="no"><yfc:i18n>Action_Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="OrderLineStatusList" binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
				<tr>
					<yfc:makeXMLInput name="OrderReleaseStatusKey">
						<yfc:makeXMLKey binding="xml:/OrderLineStatusList/OrderLineStatus/@OrderReleaseStatusKey" value="xml:/OrderStatus/@OrderReleaseStatusKey"/>
						<yfc:makeXMLKey binding="xml:/OrderLineStatusList/OrderLineStatus/@OrderReleaseStatusKey" value="xml:/OrderStatus/@OrderReleaseStatusKey"/>
					</yfc:makeXMLInput>
					<% String orderReleaseStatusKey = "xml:/OrderLineStatusList/OrderLineStatus_" + OrderStatusCounter + "/@OrderReleaseStatusKey"; %>
					<% String orderNo = resolveValue("xml:/OrderStatus/OrderLine/Order/@OrderNo") + "_" + resolveValue("xml:/OrderStatus/OrderLine/@PrimeLineNo"); %>
					<td class="tablecolumn" sortValue="<%=orderNo%>">
						<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Order/@OrderNo"/>
						&nbsp;<yfc:i18n>Show_/</yfc:i18n>&nbsp;
						<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
					</td>
					<% String itemId = resolveValue("xml:/OrderStatus/OrderLine/Item/@ItemID"); %>
					<td class="tablecolumn" sortValue="<%=itemId%>"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemID"/>
					<% if(!isVoid(resolveValue("xml:/OrderStatus/OrderLine/Item/@ItemDesc"))){ %>
						<br/><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemDesc"/>
					<% } %>
					</td>
    				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ProductClass"/>
					</td>
                <td class="tablecolumn">
                        <yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@UnitOfMeasure"/>
                    </td>
                    <td class="tablecolumn">
                        <yfc:getXMLValue binding="xml:/OrderStatus/@ShipNode"/>
                    </td>
					<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty")%>">
						<yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"/>
					</td>
					<td class="tablecolumn">
						<select class="combobox" <%=getComboOptions("xml:/TempOrderStatus/Qty_" + OrderStatusCounter + "/@AcceptRejectAction", "NONE")%> onchange="enableDisableActionQtyField(this.value,'<%=OrderStatusCounter%>')">
							<OPTION value="NONE" selected></OPTION> 
							<OPTION value="ACCEPT"><yfc:i18n>Accept_Transfer</yfc:i18n></OPTION> 
							<OPTION value="REJECT"><yfc:i18n>Reject_Transfer</yfc:i18n></OPTION> 
						</select>
					</TD>
					<td class="numerictablecolumn">
						<input type="hidden" <%=getTextOptions(orderReleaseStatusKey, resolveValue("xml:/OrderStatus/@OrderReleaseStatusKey"))%>/>
						<% String hiddenInputName = "xml:/OrderLineStatusList/OrderLineStatus_" + OrderStatusCounter + "/OrderLineTranQuantity/@Quantity"; %>
						<input type="hidden" <%=getTextOptions(hiddenInputName,resolveValue("xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"))%> UpdateQty="Y" />
						<% String textInputName = "xml:/OrderStatus/ActionQty" + OrderStatusCounter + "/@StatusQty"; 
						   String value = resolveValue("xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"); 	
						%>
						<input type="text" class='unprotectedinput' <%=getTextOptions(textInputName, value)%> DefaultValue='<%=value%>'/>
					</td>
                </tr>
        </yfc:loopXML>
    </tbody>
</table>
<%}else{%>
<script>
	alert('<%=getI18N("Only_Node_User_can_perform_this_Operation")%>');
	callDtlEntView('home');
</script>
<%}%>
