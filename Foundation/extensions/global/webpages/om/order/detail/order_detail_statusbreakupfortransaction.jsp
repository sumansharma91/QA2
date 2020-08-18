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
<script language="javascript">
    window.attachEvent("onload", IgnoreChangeNames);
    document.body.attachEvent("onunload", processSaveRecordsForOrder);
</script>
<%
    boolean displayReleaseNo = true;
    String showReleaseNo = request.getParameter("ShowReleaseNo");
    if(showReleaseNo != null && showReleaseNo.equals("N")){
        displayReleaseNo = false;
    }
%>


<table class="table" width="100%" ID="StatusBreakup">
<input type="hidden" <%=getTextOptions("xml:/OrderStatusChange/@OrderHeaderKey", "xml:/OrderLineStatusList/OrderStatus/@OrderHeaderKey")%>/>
<input type="hidden" name="xml:/OrderStatusChange/@ModificationReasonCode" />
<input type="hidden" name="xml:/OrderStatusChange/@ModificationReasonText"/>
<input type="hidden" name="xml:/OrderStatusChange/@Override" value="N"/>
<thead>
    <tr>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <% if(displayReleaseNo){ %>
            <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderRelease/@ReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <% } %>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderStatusTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@LotNumber")%>"><yfc:i18n>Lot_#</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusDate")%>"><yfc:i18n>Last_Changed_On</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ExpectedShipmentDate")%>"><yfc:i18n>ETS</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderStatusTranQuantity/@TotalQuantity")%>"><yfc:i18n>Quantity</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
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
						<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
					</a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@PrimeLineNo"/>
				<%}%>
                <input type="hidden" <%=getTextOptions("xml:/OrderStatusChange/@TransactionId", "xml:/OrderLineStatusList/DropStatuses/@TransactionId")%>/>
                <input type="hidden" <%=getTextOptions("xml:/OrderStatusChange/OrderLines/OrderLine_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderStatus/@OrderLineKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/OrderStatusChange/OrderLines/OrderLine_" + OrderStatusCounter + "/@OrderReleaseKey", "xml:/OrderStatus/@OrderReleaseKey")%>/>
            </td>
            <% if(displayReleaseNo){ %>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderRelease/@ReleaseNo")%>">
                    <a <%=getDetailHrefOptions("L02",getParameter("orderReleaseKey"),"")%>>
                    <yfc:getXMLValue binding="xml:/OrderStatus/OrderRelease/@ReleaseNo"/></a>
                </td>
            <% } %>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ItemID"/>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/Item/@ProductClass"/>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@TransactionalUOM"/>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ShipNode"/>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@LotNumber"/>
            </td>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/@StatusDate")%>">
				<yfc:getXMLValue binding="xml:/OrderStatus/@StatusDate" />
            </td>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/Schedule/@ExpectedShipmentDate")%>"> 
                <%
                    YFCElement orderStatusElem = (YFCElement)pageContext.getAttribute("OrderStatus");
                    YFCElement scheduleElem = orderStatusElem.getChildElement("Schedule");
                    if (scheduleElem != null){
                        if(scheduleElem.hasAttribute("ExpectedShipmentDate")){
                %>
                            <yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ExpectedShipmentDate"/>
                <%      }
                    }
                %>&nbsp;
            </td>
            <td nowrap="true" class="tablecolumn">
                <%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%>&nbsp;<br>
                <yfc:hasXMLNode binding="xml:/OrderLineStatusList/DropStatuses/DropStatus">
                    <select name="xml:/OrderStatusChange/OrderLines/OrderLine_<%= OrderStatusCounter %>/@BaseDropStatus" class="combobox" OldValue="xml:/OrderStatus/@Status">
                        <yfc:loopOptions binding="xml:/OrderLineStatusList/DropStatuses/@DropStatus" 
                            name="Description" value="Status" selected="xml:/OrderStatus/@Status" isLocalized="Y"/>
                    </select>
                </yfc:hasXMLNode>
            </td>
            <td nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderStatusTranQuantity/@TotalQuantity")%>">
                <yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@TotalQuantity"/>&nbsp;<br>
                <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/OrderStatusChange/OrderLines/OrderLine_" + OrderStatusCounter + "/OrderLineTranQuantity/@Quantity", "xml:/OrderStatus/OrderStatusTranQuantity/@TotalQuantity")%>/>
            </td>
        </tr>
    </yfc:loopXML>
</tbody>
</table>
