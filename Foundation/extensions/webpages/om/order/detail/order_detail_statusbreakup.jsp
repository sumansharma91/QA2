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

<%
    /* This page is used for both Order and OrderRelease screens */
    boolean isHistory=equals(resolveValue("xml:/Order/@isHistory"),"Y")
                   || equals(resolveValue("xml:/OrderRelease/@isHistory"),"Y");
%>

<table class="table" width="100%" ID="StatusBreakup">
<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/@OrderHeaderKey", "xml:/OrderLineStatusList/OrderStatus/@OrderHeaderKey")%>/>
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonCode" />
<input type="hidden" name="xml:/ChangeSchedule/@ModificationReasonText"/>
<input type="hidden" name="xml:/ChangeSchedule/@Override" value="N"/>
<thead>
    <tr>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <% if(displayReleaseNo){ %>
            <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderRelease/@ReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <% } %>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/OrderLineTranQuantity/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@ProcureFromNode")%>"><yfc:i18n>Procure_From_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@MergeNode")%>"><yfc:i18n>Merge_Node</yfc:i18n></td>
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@StatusDate")%>"><yfc:i18n>Last_Changed_On</yfc:i18n></td>
		
		<td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/OrderLine/@CancelReasonCode")%>"><yfc:i18n>Cancel_Reason</yfc:i18n></td>
		
		
        <td class=tablecolumnheader style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@TagNumber")%>"><yfc:i18n>Tag_#</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/OrderLineStatusList/OrderStatus/Schedule/@ExpectedShipmentDate")%>"><yfc:i18n>ETS</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
		
	<% if (equals(getValue("OrderStatus","xml:/OrderStatus/OrderLine/@ItemGroupCode"), "PROD") )
			//display line in this inner panel only if ItemGroupCode is PROD
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
					<input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@OrderLineScheduleKey", "xml:/OrderStatus/Schedule/@OrderLineScheduleKey")%>/>
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
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@TransactionalUOM"/>
                    <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/ToScheduleTranQuantity/@TransactionalUOM", "xml:/OrderStatus/OrderStatusTranQuantity/@TransactionalUOM")%>/>
				</td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/@ShipNode"/>
				</td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/@ProcureFromNode"/>
				</td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/@MergeNode"/>
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/@StatusDate")%>">
					<yfc:getXMLValue binding="xml:/OrderStatus/@StatusDate" />
				</td>
				
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderStatus/OrderLine/@CancelReasonCode"/>					
				</td>
				
				
				<td nowrap="true" class="tablecolumn"><%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%>
				</td>
                <td class="tablecolumn">
					<%YFCElement orderStatusElem = (YFCElement)pageContext.getAttribute("OrderStatus");
					YFCElement scheduleElem = orderStatusElem.getChildElement("Schedule");
					if (scheduleElem != null) {
						if (scheduleElem.hasAttribute("TagNumber")) {%>
							<%	
								YFCElement getItemDetailsInputElem = YFCDocument.createDocument("Item").getDocumentElement();		
								String orderHeaderKey = getValue("OrderStatus","xml:/OrderStatus/@OrderHeaderKey");
								String orderLineScheduleKey = getValue("OrderStatus","xml:/OrderStatus/@OrderLineScheduleKey");
								String itemID = getValue("OrderStatus","xml:/OrderStatus/OrderLine/Item/@ItemID");
								String unitOfMeasure = getValue("OrderStatus","xml:/OrderStatus/OrderLine/Item/@UnitOfMeasure");
								String organizationCode = getValue("OrderStatus","xml:/OrderStatus/OrderLine/Order/@SellerOrganizationCode");								

								getItemDetailsInputElem.setAttribute("ItemID",itemID);
								getItemDetailsInputElem.setAttribute("UnitOfMeasure",unitOfMeasure);
								getItemDetailsInputElem.setAttribute("OrganizationCode",organizationCode);
								getItemDetailsInputElem.setAttribute("OrderHeaderKey",orderHeaderKey);
								getItemDetailsInputElem.setAttribute("OrderLineScheduleKey",orderLineScheduleKey);

								String getItemDetailsInputStringEncoded = java.net.URLEncoder.encode(getItemDetailsInputElem.getString());
								getItemDetailsInputStringEncoded = "getItemDetailsInput=" + getItemDetailsInputStringEncoded;
	
							%>
                            <a <%=getDetailHrefOptions("L03", getParameter("orderLineKey"),getItemDetailsInputStringEncoded)%>>
							<yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@TagNumber"/></a>
					<%  }
					} %>
				</td>
                <td nowrap="true" class="numerictablecolumn" sortValue="<%=getNumericValue("xml:OrderStatus:/OrderStatus/OrderStatusTranQuantity/@StatusQty")%>">
                    <yfc:getXMLValue binding="xml:/OrderStatus/OrderStatusTranQuantity/@StatusQty"/>&nbsp;
					<% 	String ordRelKey = getValue("OrderStatus","xml:/OrderStatus/@OrderReleaseKey"); 
					if ((!isVoid(ordRelKey)) && (!isHistory) && (isModificationAllowed("xml:/@Quantity","xml:/OrderStatus/OrderLine/AllowedModifications"))) { %>
							<br>
							<input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/ToScheduleTranQuantity/@Quantity", "")%>/>
					<%  } %>
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderStatus:/OrderStatus/Schedule/@ExpectedShipmentDate")%>" nowrap="true">
					<%
						if (scheduleElem != null){
							if(scheduleElem.hasAttribute("ExpectedShipmentDate")){
					%>
								<yfc:getXMLValue binding="xml:/OrderStatus/Schedule/@ExpectedShipmentDate"/>
					<%      }
						}
					%>&nbsp;
					<% 	if(!isVoid(ordRelKey) && (!isHistory)){ %>
						<br>
						<input type="text" <%=yfsGetTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate_YFCDATE", "", "xml:/OrderStatus/OrderLine/AllowedModifications")%> >
						<img class="lookupicon" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                        <input type="text" <%=yfsGetTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate_YFCTIME", "", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                        <img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup", "xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/ToSchedules/ToSchedule/@ToExpectedShipDate", "xml:/OrderStatus/OrderLine/AllowedModifications")%>/>
                        <input type="hidden" <%=getTextOptions("xml:/ChangeSchedule/FromSchedules/FromSchedule_" + OrderStatusCounter + "/@FromExpectedShipDate", "xml:/OrderStatus/Schedule/@ExpectedShipmentDate")%>/>
					<%}%>
				</td>
			</tr>
		<%}%>
    </yfc:loopXML>
</tbody>
</table>
