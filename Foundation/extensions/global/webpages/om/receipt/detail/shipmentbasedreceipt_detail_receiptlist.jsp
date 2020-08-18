<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

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
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/@ShipmentNo")%>">
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipts/Receipt/@SellerOrganizationCode")%>">
            <yfc:i18n>Seller_Organization_Code</yfc:i18n>
        </td>
   </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:ReceiptList:/Receipts/@Receipt" id="receipt"> 
    <tr> 
		<yfc:makeXMLInput name="ReceiptKey">
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceiptHeaderKey" value="xml:receipt:/Receipt/@ReceiptHeaderKey"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:receipt:/Receipt/@ReceivingNode"/>
			<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:receipt:/Receipt/@DocumentType"/>
		</yfc:makeXMLInput>
		<td class="checkboxcolumn">
			<input type="checkbox" value='<%=getParameter("ReceiptKey")%>' name="chkEntityKey"/>
		</td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/@ReceiptNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/@EnterpriseCode"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@ShipmentNo"/></td>
        <td class="tablecolumn"><yfc:getXMLValue binding="xml:receipt:/Receipt/Shipment/@SellerOrganizationCode"/></td>
    </tr>
    </yfc:loopXML> 
</tbody>
</table>
