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
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ShipmentNo"/>
	</td>
	<td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td> 
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@OrderNo"/></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@EnterpriseCode"/></td>
	<td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/></td>
	<td class="detaillabel" ><yfc:i18n>Receiving_Dock</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceivingDock"/></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValueI18NDB binding="xml:/Receipt/@StatusName"/></td>
	<td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@CaseId"/></td>
	<td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@PalletId"/></td>
</tr>
</table>
