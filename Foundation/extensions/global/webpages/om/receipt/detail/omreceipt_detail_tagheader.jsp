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
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<table class="view" width="100%">
<tr>
    <td class="detaillabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceiptNo"/></td>
	<td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/Shipment/@ShipmentNo"/>
	</td>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/Shipment/@EnterpriseCode"/></td>

</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/></td>
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
	<td class="detaillabel" ><yfc:i18n>Receiving_Dock</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceivingDock"/></td>
<% } %>
	<td class="detaillabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@StatusName"/></td>	<%-- cr 36553 --%>
<tr>
	<td class="detaillabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ItemID"/></td>
	<td class="detaillabel" ><yfc:i18n>Item_Description</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ShortDescription"/></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>PC</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ProductClass"/></td>
	<td class="detaillabel" ><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@UnitOfMeasure"/></td> 
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@OrderNo"/></td> 
	<td class="detaillabel" ><yfc:i18n>Line_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@PrimeLineNo"/></td> 
	<td class="detaillabel" ><yfc:i18n>Release_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReleaseNo"/></td> 
</tr>
</table>
