<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.util.YFCDate" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script> 
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="table" width="100%" ID="ReceivableLines">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@LotNumber")%>"><yfc:i18n>Lot_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ShipByDate")%>"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusQty")%>"><yfc:i18n>Available_To_Receive</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
        <tr>
            <yfc:makeXMLInput name="shipmentKey">
                    <yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderLineKey" value="xml:/OrderStatus/OrderLine/@OrderLineKey"/>
                    <yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderHeaderKey" value="xml:/OrderStatus/OrderLine/@OrderHeaderKey"/>
                    <yfc:makeXMLKey binding="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderReleaseKey" value="xml:/OrderStatus/OrderRelease/@OrderReleaseKey"/>
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="orderLineKey">
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderStatus/OrderLine/@OrderLineKey"/>
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@PrimeLineNo" value="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
                    <yfc:makeXMLKey binding="xml:/OrderLineDetail/@SubLineNo" value="xml:/OrderStatus/OrderLine/@SubLineNo"/>
            </yfc:makeXMLInput>
            <td class="checkboxcolumn">
                <input type="checkbox" value='<%=getParameter("shipmentKey")%>' name="chkEntityKey" yfcMultiSelectCounter='<%=OrderStatusCounter%>' yfcMultiSelectValue='<%=getValue("OrderStatus", "xml:/OrderStatus/OrderLine/@OrderLineKey")%>'
                />
            </td>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderLine/@PrimeLineNo")%>">
				<% if(showOrderLineNo("Order","Order")) {%>
	                <a <%=getDetailHrefOptions("L01",getParameter("orderLineKey"),"")%>><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/></a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
				<%}%>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemID"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ProductClass"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@UnitOfMeasure"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemDesc"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@LotNumber"/></td>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/Schedule/@ShipByDate")%>"><yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ShipByDate"/></td>
			<td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/@StatusQty")%>"><yfc:getXMLValue binding="xml:/OrderStatus/@StatusQty"/></td>
        </tr>
    </yfc:loopXML>
</tbody>
</table>
