<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
    String driverDate = getValue("OrderLine", "xml:/OrderLine/Order/@DriverDate");
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Carrier_Service</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/@ScacAndServiceKey", "xml:/OrderLine/AllowedModifications")%> >
                <yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/OrderLine/@ScacAndServiceKey" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@ScacAndServiceKey"/>
            </select>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Carrier_Account_#</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@CarrierAccountNo", "xml:/OrderLine/@CarrierAccountNo", "xml:/OrderLine/AllowedModifications")%>>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Shipping_Paid_By</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@DeliveryCode", "xml:/OrderLine/@DeliveryCode", "xml:/OrderLine/AllowedModifications")%> >
                <yfc:loopOptions binding="xml:DeliveryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@DeliveryCode" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@DeliveryCode"/>
            </select>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Freight_Terms</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@FreightTerms", "xml:/OrderLine/@FreightTerms", "xml:/OrderLine/AllowedModifications")%> >
                <yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription" value="FreightTerms" selected="xml:/OrderLine/@FreightTerms" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@FreightTerms"/>
            </select>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Packlist_Type</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@PackListType", "xml:/OrderLine/@PackListType", "xml:/OrderLine/AllowedModifications")%>>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Department_Code</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@DepartmentCode", "xml:/OrderLine/@DepartmentCode", "xml:/OrderLine/AllowedModifications")%>>
        </td>        
    </tr>    
    <tr>
        <% // Show ReqShipDate if ReqDeliveryDate is the driver date (determined by the "DriverDate" attribute output by getOrderLineDetails)
           // because the Delivery Date field would be shown on the main order line detail screen.
           if (equals(driverDate, "02")) { %>
            <td class="detaillabel">
                <yfc:i18n>Requested_Ship_Date</yfc:i18n>
            </td>
            <td nowrap="true">
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReqShipDate","xml:/OrderLine/@ReqShipDate", "xml:/OrderLine/AllowedModifications")%> >
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/OrderLine/@ReqShipDate", "xml:/OrderLine/AllowedModifications")%>/>
            </td>
        <% } else { %>
            <td class="detaillabel">
                <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
            </td>
            <td nowrap="true">
                <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReqDeliveryDate","xml:/OrderLine/@ReqDeliveryDate", "xml:/OrderLine/AllowedModifications")%> >
                <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/OrderLine/@ReqDeliveryDate", "xml:/OrderLine/AllowedModifications")%>/>
            </td>
        <% } %>
        <td class="detaillabel" ><yfc:i18n>Reservation_ID</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/@ReservationID"/></td>
        <td class="detaillabel" >
        	<yfc:i18n>Unit_Price</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/LinePriceInfo/@UnitPrice", "xml:/OrderLine/LinePriceInfo/@UnitPrice", "xml:/OrderLine/AllowedModifications")%>>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" >
            <yfc:i18n>Minimum_Fill_Quantity</yfc:i18n>
        </td>
        <td>
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/OrderLineTranQuantity/@FillQuantity", "xml:/OrderLine/OrderLineTranQuantity/@FillQuantity", "xml:/OrderLine/AllowedModifications")%>>
        </td>
        <td class="detaillabel">
            <yfc:i18n>Distribution_Group</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@DistributionRuleId", "xml:/OrderLine/@DistributionRuleId", "xml:/OrderLine/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/DistributionRuleList/@DistributionRule" name="Description"
                value="DistributionRuleId" selected="xml:/OrderLine/@DistributionRuleId" targetBinding="xml:/Order/OrderLines/OrderLine/@DistributionRuleId"/>
            </select>
        </td>
        <td class="detaillabel">
            <yfc:i18n>Requested_Cancel_Date</yfc:i18n>
        </td>
        <td nowrap="true">
            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@ReqCancelDate","xml:/OrderLine/@ReqCancelDate", "xml:/OrderLine/AllowedModifications")%>>
            <img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/OrderLine/@ReqCancelDate", "xml:/OrderLine/AllowedModifications") %> />
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Line_Type</yfc:i18n>
        </td>
        <td>
            <select <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@LineType", "xml:/OrderLine/@LineType", "xml:/OrderLine/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/OrderLineTypeList/@OrderLineType" name="LineTypeDesc" value="LineType" selected="xml:/OrderLine/@LineType" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@LineType"/>
            </select>
        </td>
        <td class="detaillabel" >
            <yfc:i18n>Shipped_Quantity</yfc:i18n>
        </td>
        <td class="protectedtext">
			<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/OrderLineTranQuantity/@ShippedQuantity"/>
		</td>
        <td class="detaillabel" >
            <yfc:i18n>Received_Quantity</yfc:i18n>
        </td>
        <td class="protectedtext">
			<yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/OrderLineTranQuantity/@ReceivedQty"/>
		</td>
    </tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Fulfillment_Type</yfc:i18n>
		</td>
		<td>
            <select class="combobox" <%=yfsGetComboOptions("xml:/Order/OrderLines/OrderLine/@FulfillmentType", "xml:/OrderLine/@FulfillmentType", "xml:/OrderLine/AllowedModifications")%> >
				<yfc:loopOptions binding="xml:FulfillmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderLine/@FulfillmentType" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@FulfillmentType"/>
			</select>
		</td>
    </tr>
    <tr>
    <%  String languageItemDesc = getLocalizedOrderLineDescription("OrderLine");
        if (!equals(getValue("OrderLine", "xml:/OrderLine/Item/@ItemDesc"), languageItemDesc)) { %>
			<td class="detaillabel">
				<yfc:i18n>Localized_Description</yfc:i18n>
			</td>
			<td class="protectedtext" colspan="3"><%=languageItemDesc%></td>
    <% } %>
    </tr>
</table>
