<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script>
function validateandchange() {
	if (yfcAllowSingleSelection('chkEntityKey')) {
		var checks = document.all("chkEntityKey");
    for ( var i =0; i < checks.length; i++ ) {
        if ( checks[i].checked ) {
			var entkey = document.all("EntityKey");
			var curentkey = document.all("CurrentEntityKey");
			if (entkey != null) entkey.value = checks[i].value;
			if (curentkey != null) curentkey.value = checks[i].value;
        }
    }
		yfcChangeDetailView(getCurrentViewId());
	}
}
</script>

<table class="table" width="100%" editable="false">
<thead>
   <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/@ReceiptNo")%>">
            <yfc:i18n>Receipt_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/@EnterpriseCode")%>">
            <yfc:i18n>Enterprise</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/Shipment/@ShipmentNo")%>">
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/Shipment/@ExpectedShipmentDate")%>">
            <yfc:i18n>Expected_Shipment_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/Shipment/@SellerOrganizationCode")%>">
            <yfc:i18n>Seller_Organization_Code</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/Receipts/@Receipt" id="receipt"> 
    <tr> 
		<yfc:makeXMLInput name="ReceiptKey">
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderReleaseKey" value="xml:/OrderLineDetail/@OrderReleaseKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLineDetail/@OrderLineKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReceiptHeaderKey" value="xml:receipt:/Receipt/@ReceiptHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReceivingNode" value="xml:receipt:/Receipt/@ReceivingNode"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@DocumentType" value="xml:receipt:/Receipt/Shipment/@DocumentType"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@EnterpriseCode" value="xml:receipt:/Receipt/Shipment/@EnterpriseCode"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/@ShipmentKey" value="xml:receipt:/Receipt/@ShipmentKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/Shipment/@OrderHeaderKey" value="xml:receipt:/Receipt/Shipment/@OrderHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/Shipment/@OrderReleaseKey" value="xml:receipt:/Receipt/Shipment/@OrderReleaseKey"/>
			<yfc:makeXMLKey binding="xml:/OrderLineDetail/Shipment/@ReleaseNo" value="xml:receipt:/Receipt/Shipment/@ReleaseNo"/>
		</yfc:makeXMLInput>
		<td class="checkboxcolumn">
			<input type="checkbox" value='<%=getParameter("ReceiptKey")%>' name="chkEntityKey"/>
		</td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/@ReceiptNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@EnterpriseCode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@ShipmentNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@ExpectedShipmentDate"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@SellerOrganizationCode"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
