<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/currencyutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript" src="../console/scripts/om.js"></script>
<table class="table" cellpadding="0" cellspacing="0" width="100%">
<thead> 
    <tr> 
        <td sortable="no" class="checkboxheader">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader"><yfc:i18n>Order_#</yfc:i18n></td>            
        <td class="tablecolumnheader"><yfc:i18n>Line_#</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Item_Description</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Recv_Node</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Ship_Date</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Line_Qty</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Amount</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="OrderLineList" binding="xml:/OrderLineList/@OrderLine" id="OrderLine"> 
	<% if(!isVoid(resolveValue("xml:OrderLine:/OrderLine/@Status"))) {%>
        <yfc:makeXMLInput name="orderLineKey">
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
        </yfc:makeXMLInput>
        <tr> 
        <td class="checkboxcolumn" >
            <input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="EntityKey">
			<input type="hidden" name='OrderLineKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderLineKey")%>' />
			<input type="hidden" name='OrderHeaderKey_<%=OrderLineCounter%>' value='<%=resolveValue("xml:/OrderLine/@OrderHeaderKey")%>' />

        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></td>
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
            <a href="javascript:showDetailFor('<%=getParameter("orderLineKey")%>');">
	            <yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
            </a>
        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
        <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderLine:/OrderLine/@ReqShipDate")%>">
            <yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>">
            <yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@LineTotal")%>">
            <%=displayAmount(getValue("OrderLine", "xml:/OrderLine/LinePriceInfo/@LineTotal"), (YFCElement) request.getAttribute("CurrencyList"), getValue("OrderLine", "xml:/OrderLine/Order/PriceInfo/@Currency"))%>
        </td>
        <td class="tablecolumn">
            <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%>
            <% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@HoldFlag"))) { %>
                <img class="icon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_line_is_held")%>/>
            <% } %>
        </td>        
        </tr>
	<%} else {%> 
	<yfc:makeXMLInput name="orderLineKey">
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
        </yfc:makeXMLInput>
        <tr> 
        <td class="checkboxcolumn" ></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"/></td>
        <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
                <yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
        </td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/></td>
        <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderLine:/OrderLine/@ReqShipDate")%>">
            <yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/OrderLineTranQuantity/@OrderedQty")%>">
            <yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@LineTotal")%>">
            <%=displayAmount(getValue("OrderLine", "xml:/OrderLine/LinePriceInfo/@LineTotal"), (YFCElement) request.getAttribute("CurrencyList"), getValue("OrderLine", "xml:/OrderLine/Order/PriceInfo/@Currency"))%>
        </td>
        <td class="tablecolumn">
            <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%>
            <% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@HoldFlag"))) { %>
                <img class="icon" <%=getImageOptions(YFSUIBackendConsts.HELD_ORDER, "This_order_line_is_held")%>/>
            <% } %>
            <% if (equals("Y", getValue("OrderLine", "xml:/OrderLine/@isHistory") )){ %>
                <img class="icon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_order_line")%>/>
            <% } %>
        </td>        
        </tr>
	<%}%>
    </yfc:loopXML> 
</tbody>
</table>
