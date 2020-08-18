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

<%
	String tagControlFlag = "N";
	YFCElement itemDetailsElem = (YFCElement) request.getAttribute("ItemDetails");
	tagControlFlag = getValue("ItemDetails","xml:/Item/InventoryParameters/@TagControlFlag");		
%>
<%
    boolean isHistory=equals(resolveValue("xml:/OrderLine/@isHistory"),"Y")
                   || equals(resolveValue("xml:/OrderRelease/@isHistory"),"Y");
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
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@ProcureFromNode")%>"><yfc:i18n>Procure_From_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@MergeNode")%>"><yfc:i18n>Merge_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@ReceivingNode")%>"><yfc:i18n>Receiving_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusDate")%>"><yfc:i18n>Last_Changed_On</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ExpectedDeliveryDate")%>"><yfc:i18n>ETD</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/OrderLineTranQuantity/@StatusQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        
        <% if ((equals(tagControlFlag, "Y")) || (equals(tagControlFlag, "S"))) { %>
            <jsp:include page="/im/inventory/detail/inventory_detail_tagattributes.jsp" flush="true">
				<jsp:param name="TagContainer" value="OrderStatus"/>
				<jsp:param name="TagElement" value="Schedule"/>
                <jsp:param name="IdentifiersOnly" value="true"/>
                <jsp:param name="ScreenType" value="list"/>
				<jsp:param name="ListHeadersOnly" value="true"/>
			</jsp:include>
        <% } %>

        <td class=tablecolumnheader sortable="no" style="width:<%=getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ExpectedShipmentDate")%>"><yfc:i18n>ETS</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <%
    ArrayList orderStatusList = getLoopingElementList("xml:/OrderLineStatusList/@OrderStatus");
    for (int OrderStatusCounter = 0; OrderStatusCounter < orderStatusList.size(); OrderStatusCounter++) {
        
        YFCElement singleOrderStatus = (YFCElement) orderStatusList.get(OrderStatusCounter);
        pageContext.setAttribute("OrderStatus", singleOrderStatus);
		String sEditTagAttr = "false";
    %>
        <tr>
            <yfc:makeXMLInput name="orderReleaseKey">
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderStatus/@OrderReleaseKey" />
            </yfc:makeXMLInput>
            <% if(displayReleaseNo){ %>
                <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderRelease/@ReleaseNo")%>">
                    <a <%=getDetailHrefOptions("L01",getParameter("orderReleaseKey"),"")%>>
                    <yfc:getXMLValue binding="xml:/OrderStatus/OrderRelease/@ReleaseNo"/></a>
                </td>
            <% } %>
            <td class="tablecolumn">
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineKey", "xml:/OrderStatus/@OrderLineKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderReleaseKey", "xml:/OrderStatus/@OrderReleaseKey")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromStatus", "xml:/OrderStatus/@Status")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromTagNo", "xml:/OrderStatus/Schedule/@TagNumber")%>/>
                <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineScheduleKey", "xml:/OrderStatus/Schedule/@OrderLineScheduleKey")%>/>
                <yfc:getXMLValue binding="xml:/OrderStatus/@ShipNode" />
            </td>
            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/OrderStatus/@ProcureFromNode" />
            </td>
            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/OrderStatus/@MergeNode" />
            </td>
            <td class="tablecolumn">
                <yfc:getXMLValue binding="xml:/OrderStatus/@ReceivingNode" />
            </td>
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/@StatusDate")%>">
                <yfc:getXMLValue binding="xml:/OrderStatus/@StatusDate" />
            </td>
            <td nowrap="true" class="tablecolumn">
                <%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%>
            </td>            
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/Schedule/@ExpectedDeliveryDate")%>">
                <yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ExpectedDeliveryDate"/>
            </td>
            <td nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderStatusTranQuantity/@StatusQty")%>">
                <yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"/>&nbsp;
                <%String ordRelKey=getValue("OrderStatus","xml:/OrderStatus/@OrderReleaseKey");
                if ((!isVoid(ordRelKey)) && (!isHistory) && (isModificationAllowed("xml:/@Quantity","xml:/OrderStatus/OrderLine/AllowedModifications"))) { 
					sEditTagAttr = "true"; %>
                    <br>
                    <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/ToScheduleTranQuantity/@Quantity", "")%>/>
                <% } %>
            </td>
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
                    <jsp:param name="Modifiable" value='<%=sEditTagAttr%>'/>
                    <jsp:param name="CreateNewTextField" value="true"/>
                    <jsp:param name="BindingPrefix" value="xml:/OrderStatus/Schedule"/>
                    <jsp:param name="TargetBindingAttributePrefix" value="To"/>
                    <jsp:param name="TargetBindingPrefix" value="<%=targetBinding%>"/>
					<jsp:param name="ResetTagAttribute" value="true"/>
                </jsp:include>
            <% } %>
            
            <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/Schedule/@ExpectedShipmentDate")%>">
                <%if (scheduleElem != null){
                    if(scheduleElem.hasAttribute("ExpectedShipmentDate")) {%>
                        <yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ExpectedShipmentDate"/>
                	<%}%>
	            <%}%>
                &nbsp;
                <%if ((!isVoid(ordRelKey)) && (!isHistory) && (isModificationAllowed("xml:/@Quantity","xml:/OrderStatus/OrderLine/AllowedModifications"))) {  %>
                    <br>
                    <input type="text" <%=yfsGetTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate_YFCDATE", "", "xml:/OrderStatus/OrderLine/AllowedModifications")%>>
                    <img class="lookupicon" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                    <input type="text" <%=yfsGetTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate_YFCTIME", "", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                    <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                    <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromExpectedShipDate", "xml:/OrderStatus/Schedule/@ExpectedShipmentDate")%>/>
                <%}%>
            </td>
        </tr>
    <% } %>
</tbody>
</table>
