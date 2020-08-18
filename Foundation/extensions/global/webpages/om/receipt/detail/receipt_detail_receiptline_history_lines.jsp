<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<table class="table" width="100%">
<thead>
    <tr>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@LineNumber")%>"><yfc:i18n>Line_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@TransactionName")%>"><yfc:i18n>Transaction</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>"><yfc:i18n>Disposition</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@InspectionDate")%>"><yfc:i18n>Date</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@InspectedBy")%>"><yfc:i18n>User</yfc:i18n></td>
        <td class="tablecolumnheader" ><yfc:i18n>Comments</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine">
		<%// Only records that satisfy the history criteria need to be shown.
        if (getNumericValue("xml:/ReceiptLine/@UIAncestorLevel") > 0) { %>  
        <tr>
            <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:ReceiptLine:/ReceiptLine/@ReceiptLineNo")%>">
                <yfc:getXMLValue binding="xml:/ReceiptLine/@ReceiptLineNo"/>
            </td>
            <td class="tablecolumn" ><yfc:getXMLValue binding="xml:/ReceiptLine/@TransactionName"/></td>
            <td class="tablecolumn">
				<!--<yfc:getXMLValue binding="xml:/ReceiptLine/@DispositionCode"/>-->
				<%=getComboText("xml:/ReturnDispositionList/@ReturnDisposition" ,"Description" ,"DispositionCode" ,"xml:/ReceiptLine/@DispositionCode",true)%>
			</td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@InspectionDate"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@InspectedBy"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@InspectionComments"/></td>
        </tr>
        <%}%>
    </yfc:loopXML> 
</tbody>
</table>
