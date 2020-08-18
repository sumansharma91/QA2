<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
double quantity = 0;
//This screen doubles for receiving and inspection.  Based on a parameter, display can change.
String isInsp = (String) request.getParameter("IsInspection");
boolean isInspection = false;
String root = "Receipt/ReceiptLines/ReceiptLine";
if (equals(isInsp, "Y")) {
    isInspection = true;
    root = "InspectOrder/ReceiptLines/FromReceiptLine/ToReceiptLines/ToReceiptLine";
}
%>
<yfc:loopXML binding="xml:/OrderLineStatusList/@OrderStatus" id="OrderStatus">
    <%quantity += getNumericValue("xml:/OrderStatus/@StatusQty");%>
</yfc:loopXML>


<table class="view" width="100%">
<tr>
    <yfc:makeXMLInput name="orderKey">
        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey" ></yfc:makeXMLKey>
    </yfc:makeXMLInput>
    <yfc:makeXMLInput name="orderLineKey">
        <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" ></yfc:makeXMLKey>
    </yfc:makeXMLInput>
    <yfc:makeXMLInput name="orderReleaseKey">
        <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" ></yfc:makeXMLKey>
    </yfc:makeXMLInput>
    <td class="detaillabel" >
        <yfc:i18n>Order_#</yfc:i18n>
    </td>
    <td class="protectedtext">
		<% if(showOrderNo("OrderLine","Order")) {%>
	        <a <%=getDetailHrefOptions("L01", getParameter("orderKey"),"")%>>
				<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
			</a>
		<%} else {%>
			<yfc:getXMLValue binding="xml:/OrderLine/Order/@OrderNo"></yfc:getXMLValue>
		<%}%>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Release_#</yfc:i18n>
    </td>
    <td class="protectedtext">
        <a <%=getDetailHrefOptions("L02",getParameter("orderReleaseKey"),"")%>>
        <yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"></yfc:getXMLValue>
        </a>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Line_#</yfc:i18n>
    </td>
    <td class="protectedtext">
		<% if(showOrderLineNo("OrderLine","Order")) {%>
	        <a <%=getDetailHrefOptions("L03",getParameter("orderLineKey"),"")%>>
				<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue>
			</a>
		<%} else {%>
			<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"></yfc:getXMLValue>
		<%}%>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Item_ID</yfc:i18n>
    </td>
    <td class="protectedtext">
        <yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"></yfc:getXMLValue></a>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Product_Class</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"></yfc:getXMLValue>
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderLine/Item/@UnitOfMeasure"></yfc:getXMLValue></td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Description</yfc:i18n>
    </td>
    <td class="protectedtext" colspan="3"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemDesc"></yfc:getXMLValue>
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Line_Quantity</yfc:i18n>
    </td>
    <td class="protectednumber">
        <yfc:getXMLValue binding="xml:/OrderLine/@OrderedQty"/>
    </td>
    <td class="detaillabel" >
        <%if (isInspection) {%>
        <yfc:i18n>Open_For_Inspection</yfc:i18n>
        <%} else {%>
        <yfc:i18n>Open_For_Receipt</yfc:i18n>
        <%}%>
    </td>
    <td class="protectednumber">
        <%=getLocalizedStringFromInt(getLocale(), (int)quantity)%>
    </td>
</tr>
</table>
