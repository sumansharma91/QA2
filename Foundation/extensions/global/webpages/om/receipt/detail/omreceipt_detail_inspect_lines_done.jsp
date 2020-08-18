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
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@ReceiptLineNo")%>">
            <yfc:i18n>Line</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@SerialNo")%>"> 				
			<yfc:i18n>Serial_#</yfc:i18n>
		</td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@ShipByDate")%>"> 				
			<yfc:i18n>Ship_By_Date</yfc:i18n>
		</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@Quantity")%>">
            <yfc:i18n>Quantity</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/ReceiptLineList/ReceiptLine/@DispositionCode")%>">
            <yfc:i18n>Disposition_Code</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@InspectionDate")%>">
            <yfc:i18n>Inspection_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/ReceiptLineList/ReceiptLine/@InspectedBy")%>">
            <yfc:i18n>User</yfc:i18n>
        </td>        
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ReceiptLineList/ReceiptLine/@InspectionComments")%>">
            <yfc:i18n>Comments</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
	<yfc:loopXML binding="xml:getReceiptLineListInspected:/ReceiptLineList/@ReceiptLine" id="ReceiptLine"> 
			<td class="checkboxcolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ReceiptLineNo"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@SerialNo"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@ShipByDate"/>
			</td>
			<td class="numerictablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@OriginalQuantity"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@DispositionCode"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@InspectionDate"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@InspectedBy"/>
			</td>
			<td class="tablecolumn">
				<yfc:getXMLValue name="ReceiptLine" binding="xml:/ReceiptLine/@InspectionComments"/>
			</td>
		</tr>
	</yfc:loopXML> 
</tbody>
</table>
