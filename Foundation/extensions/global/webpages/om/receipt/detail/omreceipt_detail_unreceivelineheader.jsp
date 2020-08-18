<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<table class="view" width="100%">
	<yfc:makeXMLInput name="receiptLineKey">
        <yfc:makeXMLKey binding="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptLineKey" value="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptLineKey"> </yfc:makeXMLKey>
        </yfc:makeXMLInput>
        <yfc:makeXMLInput name="receiptHeaderKey">
        <yfc:makeXMLKey binding="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptHeaderKey" value="xml:/Receipt/ReceiptLines/ReceiptLine/@ReceiptHeaderKey"> </yfc:makeXMLKey>
	</yfc:makeXMLInput>
<tr>
    <td class="detaillabel" ><yfc:i18n>Receipt_Line_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@ReceiptLineNo"/></td>
	<td class="detaillabel" ><yfc:i18n>Receipt_Date</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceiptDate"/></td>
	<td class="detaillabel" ><yfc:i18n>Receipt_Status</yfc:i18n></td>
	<td class="protectedtext"><%if(equals("Y",resolveValue("xml:/Receipt/@OpenReceiptFlag"))){%>
	Open<%}else{%>Close<%}%></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@EnterpriseCode"/></td>
    <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@OrderNo"/></td>
    <td class="detaillabel" ><yfc:i18n>Release_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@ReleaseNo"/></td>
</tr>	
<tr>
 	<td class="detaillabel" ><yfc:i18n>Shipment_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ShipmentNo"/></td>
	<td class="detaillabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/Receipt/@ReceivingNode"/></td>
</tr>
<tr>
	<td class="detaillabel" ><yfc:i18n>Case_ID</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@CaseId"/></td>
	<td class="detaillabel" ><yfc:i18n>Pallet_ID</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/ReceiptLineList/ReceiptLine/@PalletId"/></td>
</tr> 
</table> 
