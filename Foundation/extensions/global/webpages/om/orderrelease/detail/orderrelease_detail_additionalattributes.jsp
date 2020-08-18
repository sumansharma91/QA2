<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="view" width="100%">
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Node</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel"  >
        <yfc:i18n>Receiving_Node</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ReceivingNode"></yfc:getXMLValue>&nbsp;</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Packlist_Type</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@PacklistType"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" >
        <yfc:i18n>Requested_Delivery_Date</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ReqDeliveryDate"></yfc:getXMLValue>&nbsp;</td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Requested_Ship_Date</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ReqShipDate"></yfc:getXMLValue>&nbsp;</td>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Together</yfc:i18n>
    </td>
    <td class="protectedtext"><%=displayFlagAttribute(getValue("OrderRelease","xml:/OrderRelease/@ShipCompleteFlag"))%>&nbsp;
    </td>
</tr>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Ship_Line_Complete</yfc:i18n>
    </td>
    <td class="protectedtext"><%=displayFlagAttribute(getValue("OrderRelease","xml:/OrderRelease/@ShipLineComplete"))%>&nbsp;
    </td>
    <td class="detaillabel" >
        <yfc:i18n>Merge_Node</yfc:i18n>
    </td>
    <td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@MergeNode"></yfc:getXMLValue>&nbsp;
    </td>
</tr>
<tr>
	<td class="detaillabel" >
		<yfc:i18n>Carrier_Service</yfc:i18n>
	</td>
	<td>
		<select <%=yfsGetComboOptions("xml:/OrderRelease/@ScacAndServiceKey", "xml:/OrderRelease/AllowedModifications")%> >
			<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" 
				name="ScacAndServiceDesc" value="ScacAndServiceKey" selected="xml:/OrderRelease/@ScacAndServiceKey" isLocalized="Y"/>
		</select>
	</td>
	<td class="detaillabel" >
		<yfc:i18n>Carrier_Account_#</yfc:i18n>
	</td>
	<td>
		<input type="text" <%=yfsGetTextOptions("xml:/OrderRelease/@CarrierAccountNo", "xml:/OrderRelease/AllowedModifications")%>>
	</td>

</tr>
<tr>
	<td class="detaillabel" >
		<yfc:i18n>Shipping_Paid_By</yfc:i18n>
	</td>
	<td>
		<select <%=yfsGetComboOptions("xml:/OrderRelease/@DeliveryCode", "xml:/OrderRelease/AllowedModifications")%> >
			<yfc:loopOptions binding="xml:DeliveryCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
			value="CodeValue" selected="xml:/OrderRelease/@DeliveryCode" isLocalized="Y"/>
		</select>
	</td>

	<td class="detaillabel" >
		<yfc:i18n>Freight_Terms</yfc:i18n>
	</td>
	<td>
		<select <%=yfsGetComboOptions("xml:/OrderRelease/@FOB", "xml:/OrderRelease/AllowedModifications")%> >
			<yfc:loopOptions binding="xml:FreightTermsList:/FreightTermsList/@FreightTerms" name="ShortDescription"
			value="FreightTerms" selected="xml:/OrderRelease/@FOB" isLocalized="Y"/>
		</select>
	</td>
</tr>
<% if(!equals(getParameter("HideGiftFlag"),"Y")) {%>
<tr>
    <td class="detaillabel" >
        <yfc:i18n>Gift</yfc:i18n>
    </td>
    <td class="protectedtext"><%=displayFlagAttribute(getValue("OrderRelease","xml:/OrderRelease/@GiftFlag"))%>&nbsp;
    </td>
    <td colspan="2">&nbsp;</td>
</tr>
<% } %>
</table>
