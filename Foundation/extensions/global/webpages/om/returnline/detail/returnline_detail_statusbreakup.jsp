<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript">
    window.attachEvent("onload", IgnoreChangeNames);
    document.body.attachEvent("onunload", processSaveRecordsForOrder);
</script>
<%
    boolean displayReleaseNo = false;
	// not used?
	/*
    String showReleaseNo = request.getParameter("ShowReleaseNo");
    if("Y".equals(showReleaseNo) || "true".equals(showReleaseNo)){
        displayReleaseNo = true;
    }
	*/
%>

<%
	String tagControlFlag = "N";
	YFCElement itemDetailsElem = (YFCElement) request.getAttribute("ItemDetails");
	tagControlFlag = getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag");		
%>

<table class="table" width="100%" ID="StatusBreakup">
<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/@OrderHeaderKey", "xml:/OrderLineStatusList/OrderStatus/@OrderHeaderKey")%>/>
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonCode" />
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonText"/>
<input type="hidden" name="xml:/ChangeSchedule/@Override" value="N"/>
<thead>
    <tr>
        <% if(displayReleaseNo){ %>
            <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderRelease/@ReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <% } %>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusDate")%>"><yfc:i18n>Last_Changed_On</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>

        <% if ((equals(tagControlFlag, "Y")) || (equals(tagControlFlag, "S"))) { %>
            <jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
				<jsp:param name="TagContainer" value="OrderStatus"/>
				<jsp:param name="TagElement" value="Schedule"/>
                <jsp:param name="IdentifiersOnly" value="true"/>
                <jsp:param name="ScreenType" value="list"/>
				<jsp:param name="ListHeadersOnly" value="true"/>
			</jsp:include>
        <% } %>
    </tr>
</thead>
<tbody>
    <%
    ArrayList orderStatusList = getLoopingElementList("xml:/OrderLineStatusList/@OrderStatus");
    for (int OrderStatusCounter = 0; OrderStatusCounter < orderStatusList.size(); OrderStatusCounter++) {
        
        YFCElement singleOrderStatus = (YFCElement) orderStatusList.get(OrderStatusCounter);
        pageContext.setAttribute("OrderStatus", singleOrderStatus);
    %>
        <tr>
            <yfc:makeXMLInput name="orderReleaseKey">
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderStatus/@OrderReleaseKey" />
            </yfc:makeXMLInput>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderStatus/@OrderLineKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderReleaseKey", "xml:/OrderStatus/@OrderReleaseKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromExpectedShipDate", "xml:/OrderStatus/Schedule/@ExpectedShipmentDate")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromStatus", "xml:/OrderStatus/@Status")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromTagNo", "xml:/OrderStatus/Schedule/@TagNumber")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineScheduleKey", "xml:/OrderStatus/Schedule/@OrderLineScheduleKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@Quantity", "xml:/OrderStatus/OrderLine/@StatusQuantity")%>/>
            <% if(displayReleaseNo){ %>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderRelease/@ReleaseNo")%>">
                    <a <%=getDetailHrefOptions("L01",getParameter("orderReleaseKey"),"")%>>
                    <yfc:getXMLValue binding="xml:/OrderStatus/OrderRelease/@ReleaseNo"/></a>
                </td>
            <% } %>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/@StatusDate")%>"><yfc:getXMLValue binding="xml:/OrderStatus/@StatusDate" />
            </td>
            <td nowrap="true" class="tablecolumn"><%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%>
            </td>
            <td nowrap="true" class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/@StatusQty"/></td>
            <%	
            	YFCElement orderStatusElem = (YFCElement)pageContext.getAttribute("OrderStatus");
	            YFCElement scheduleElem = orderStatusElem.getChildElement("Schedule");
	        %>
            <% if ((equals(tagControlFlag, "Y")) || (equals(tagControlFlag, "S"))) {
                String targetBinding = "xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule";
                request.setAttribute("OrderStatus", singleOrderStatus);
            %>
                <jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
                    <jsp:param name="TagContainer" value="OrderStatus"/>
                    <jsp:param name="TagElement" value="Schedule"/>
                    <jsp:param name="IdentifiersOnly" value="true"/>
                    <jsp:param name="ScreenType" value="list"/>
                    <jsp:param name="Modifiable" value='false'/>
                    <jsp:param name="CreateNewTextField" value="true"/>
                    <jsp:param name="BindingPrefix" value="xml:/OrderStatus/Schedule"/>
                    <jsp:param name="TargetBindingAttributePrefix" value="To"/>
                    <jsp:param name="TargetBindingPrefix" value="<%=targetBinding%>"/>
                </jsp:include>
            <% } %>
        </tr>
    <% } %>
</tbody>
</table>
