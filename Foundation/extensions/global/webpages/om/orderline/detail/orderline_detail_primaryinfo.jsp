<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.core.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%  
    String modifyView = request.getParameter("ModifyView");
    modifyView = modifyView == null ? "" : modifyView;

    String driverDate = getValue("OrderLine", "xml:/OrderLine/Order/@DriverDate");
%>

<table class="view" width="100%">
<tr>
    <td>
        <input type="hidden" name="xml:/Order/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/Order/@ModificationReasonText"/>
        <input type="hidden" name="xml:/Order/@Override" value="N"/>
		<input type="hidden" name="userHasOverridePermissions" value='<%=userHasOverridePermissions()%>'/>
        <input type="hidden" name="xml:/OrderRelease/@ModificationReasonCode"/>
        <input type="hidden" name="xml:/OrderRelease/@ModificationReasonText"/>
        <input type="hidden" name="xml:/OrderRelease/@Override" value="N"/>
        <input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("OrderLine", "xml:/OrderLine/Order/@DraftOrderFlag")%>'/>
        <input type="hidden" <%=getTextOptions("xml:/Order/@OrderHeaderKey","xml:/OrderLine/@OrderHeaderKey")%> />
        <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@OrderLineKey","xml:/OrderLine/@OrderLineKey")%> />

		<yfc:makeXMLInput name="orderKey">
			<yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" ></yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/Order/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<input type="hidden" name="chkEntityKey" value='<%=getParameter("orderKey")%>' />
    </td>
</tr>
<tr>
    <yfc:makeXMLInput name="statusKey">
        <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" ></yfc:makeXMLKey>
    </yfc:makeXMLInput>
    <yfc:makeXMLInput name="invItemKey">
        <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:/OrderLine/Item/@ItemID" ></yfc:makeXMLKey>
        <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:/OrderLine/Item/@ProductClass" ></yfc:makeXMLKey>
			<yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:/OrderLine/Item/@UnitOfMeasure" ></yfc:makeXMLKey>
        <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/OrderLine/Order/@SellerOrganizationCode" ></yfc:makeXMLKey>
		<% if(isShipNodeUser()) { %>
			<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node"/>
		<%} else {%>
	        <yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:/OrderLine/@ShipNode" ></yfc:makeXMLKey>
		<%}%>
    </yfc:makeXMLInput>
    <td class="detaillabel" >
        <yfc:i18n>Order_#</yfc:i18n>
    </td>
    <td class="protectedtext">
		<% if(showOrderNo("OrderLine","Order")) {%>
		    <a <%=getDetailHrefOptions("L01",getParameter("orderKey"),"")%>>
				<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
			</a>&nbsp;
		<%} else {%>
			<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
		<%}%>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Line_#</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" >
        <yfc:i18n>Line_Quantity</yfc:i18n>
    </td>
    <% if (!isVoid(modifyView)) {%>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/OrderLineTranQuantity/@OrderedQty", "xml:/OrderLine/AllowedModifications")%> title='<%=getI18N("Open_Qty")%>: <%=getValue("OrderLine", "xml:/OrderLine/OrderLineTranQuantity/@OpenQty")%>'/>&nbsp;
        </td>
    <% } else { %>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@OrderedQty"/>&nbsp;</td>
    <% } %>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td class="protectedtext">
        <% 
            String sNode = resolveValue("xml:/OrderLine/@ShipNode");
            if(!YFCObject.equals(resolveValue("xml:/OrderLine/@KitCode"),"LK") && !YFCObject.equals(resolveValue("xml:/OrderLine/@KitCode"),"BUNDLE")) {
                if(isVoid(sNode)) {%>
                    <a <%=getDetailHrefOptions("L03",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>&nbsp;
                <%} else {%>
                    <a <%=getDetailHrefOptions("L04",getParameter("invItemKey"),"")%>><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>&nbsp;
                <% } 
            } else {%>
		<yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue>&nbsp;
	        <% } %>

    </td>
    <td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"></yfc:getXMLValue>&nbsp;
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Description</yfc:i18n>
    </td>
    <% if (!isVoid(modifyView)) {%>
        <td nowrap="true" colspan="3">
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Item/@ItemDesc", "xml:/OrderLine/Item/@ItemDesc", "xml:/OrderLine/AllowedModifications")%> />
        </td>
    <% } else { %>
        <td class="protectedtext" colspan="3"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>&nbsp;
        </td>
    <% } %>
    <td class="detaillabel" >
        <yfc:i18n>Status</yfc:i18n>
    </td>
    <td class="protectedtext">
        <a <%=getDetailHrefOptions("L02",getParameter("statusKey"),"ShowReleaseNo=Y")%>>
        <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></a>&nbsp;
		<% if (!isVoid(resolveValue("xml:/OrderLine/SchedFailureReasonCode"))) {%>
			<yfc:i18n>(</yfc:i18n>
			<yfc:i18n><yfc:getXMLValue binding="xml:/OrderLine/SchedFailureReasonCode"></yfc:getXMLValue></yfc:i18n>
			<yfc:i18n>)</yfc:i18n>
		<%}%>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Receiving_Node</yfc:i18n>
    </td>
    <% if (!isVoid(modifyView)) {%>
        <td nowrap="true" >
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReceivingNode", "xml:/OrderLine/@ReceivingNode", "xml:/OrderLine/AllowedModifications")%> />
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node", "xml:/OrderLine/@ReceivingNode", "xml:/OrderLine/AllowedModifications") %> />&nbsp;
        </td>
    <% } else { %>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@ReceivingNode"/>&nbsp;</td>
    <% } %>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Node</yfc:i18n>
    </td>
    <% if (!isVoid(modifyView)) {%>
        <td nowrap="true" >
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ShipNode", "xml:/OrderLine/@ShipNode", "xml:/OrderLine/AllowedModifications")%> />
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node", "xml:/OrderLine/@ShipNode", "xml:/OrderLine/AllowedModifications") %> />&nbsp;
        </td>
    <% } else { %>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@ShipNode"/>&nbsp;</td>
    <% } %>
    <% // Show ReqDeliveryDate if that is the driver date (determined by the "DriverDate" attribute output by getOrderDetails)
       // else show ReqShipDate as the driver date.
       if (equals(driverDate, "02")) { %>
        <td class="detaillabel">
            <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
        </td>
        <% if (!isVoid(modifyView)) {%>
            <td nowrap="true" >
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReqDeliveryDate", "xml:/OrderLine/@ReqDeliveryDate", "xml:/OrderLine/AllowedModifications")%> >
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/OrderLine/@ReqDeliveryDate", "xml:/OrderLine/AllowedModifications") %> />&nbsp;
            </td>
        <% } else { %>
            <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@ReqDeliveryDate"/>&nbsp;</td>
        <% } %>
    <% } else { %>
        <td class="detaillabel">
            <yfc:i18n>Requested_Ship_Date</yfc:i18n>
        </td>
        <% if (!isVoid(modifyView)) {%>
            <td nowrap="true" >
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReqShipDate", "xml:/OrderLine/@ReqShipDate", "xml:/OrderLine/AllowedModifications")%> >
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/OrderLine/@ReqShipDate", "xml:/OrderLine/AllowedModifications") %> />&nbsp;
            </td>
        <% } else { %>
            <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/@ReqShipDate"/>&nbsp;</td>
        <% } %>
    <% } %>
</tr>
<tr>
	<%  
		if(equals("true", modifyView) )
		{
	%>
		<td class="detaillabel" >
			<yfc:i18n>Document_Type</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"/>
		</td>
	<%  }	%>
</tr>
</table>
