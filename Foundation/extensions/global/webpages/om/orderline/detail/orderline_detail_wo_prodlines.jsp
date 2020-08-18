<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="javascript">
    document.body.attachEvent("onunload", processSaveForWOProductOrderLines);
</script>
<table class="table" ID="ProductOrderLines" cellspacing="0" width="100%" >
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/WorkOrderProdDelivery/@ItemID")%>"><yfc:i18n>Item_Related_Info</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderProdDelivery/@Uom")%>"><yfc:i18n>UOM</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderProdDelivery/@QuantityRequired")%>"><yfc:i18n>Required_Quantity</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/WorkOrderProdDelivery/@QuantityDelivered")%>"><yfc:i18n>Delivered_Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="WorkOrder" binding="xml:/WorkOrder/WorkOrderProdDeliveries/@WorkOrderProdDelivery" id="WorkOrderProdDelivery">
			<tr>
				<yfc:makeXMLInput name="ProdWorkOrderLineKey">
					<yfc:makeXMLKey binding="xml:/WorkOrderLineDetail/@WorkOrderProdDeliveryKey" value="xml:/WorkOrderProdDelivery/@WorkOrderProdDeliveryKey"/>
					<yfc:makeXMLKey binding="xml:/WorkOrderLineDetail/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey"/>
				</yfc:makeXMLInput>
					
				<yfc:makeXMLInput name="orderLineKey">
					<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/WorkOrderProdDelivery/OrderLine/@OrderLineKey"/>
				</yfc:makeXMLInput>

				<% String itemId = resolveValue("xml:/WorkOrderProdDelivery/@ItemID"); %>
				<td class="tablecolumn" sortValue="<%=itemId%>">
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@ItemID"/><br/>
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@ItemShortDescription"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@ProductClass"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@Uom"/>
				</td>
				<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:WorkOrderProdDelivery:/WorkOrderProdDelivery/@QuantityRequired")%>">
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@QuantityRequired"/>
					</td>
				<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:WorkOrderProdDelivery:/WorkOrderProdDelivery/@QuantityRequired")%>">
					<yfc:getXMLValue binding="xml:/WorkOrderProdDelivery/@QuantityDelivered"/>
				</td>
			</tr>
		</yfc:loopXML>
    </tbody>
</table>
