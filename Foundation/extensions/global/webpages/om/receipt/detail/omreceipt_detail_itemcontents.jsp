<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%> 
<table class="table">
<thead>
    <tr> 
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@PalletId")%>">
            <yfc:i18n>Pallet_ID</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@CaseId")%>">
            <yfc:i18n>Case_ID</yfc:i18n>
        </td>
<% } %>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@SerialNo")%>">
            <yfc:i18n>Serial_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:ReceiptLineList:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
	</tr>
</thead> 
<tbody>
    <yfc:loopXML binding="xml:ReceiptLineList:/ReceiptLineList/@ReceiptLine" id="ReceiptLine"> 
	<tr> 
<% if (equals("Y",getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))) { %>
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@PalletId"/>
	    </td>
		<td class="tablecolumn">
			<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@CaseId"/>
	    </td> 
<% } %>
		<td class="tablecolumn">
	        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
	    </td> 
		<td class="tablecolumn">
	        <yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@Quantity"/>
	    </td> 
	</tr>
	 </yfc:loopXML> 
</tbody>
</table>
