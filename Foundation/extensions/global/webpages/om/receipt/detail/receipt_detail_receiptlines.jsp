<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<%
//This inner panel can be called for a specific tran id.  In that case, it will only show records for that tran id.
String tranid = (String) request.getAttribute("TransactionId");
%>
<table class="table" width="100%">
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true" style="width:30px">&nbsp;</td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@LineNumber")%>"><yfc:i18n>Line_#</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/OrderLine/InventoryItem/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/OrderLine/InventoryItem/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/OrderLine/InventoryItem/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@SerialNo")%>"><yfc:i18n>Serial_#</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@LotNumber")%>"><yfc:i18n>Lot_#</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@ShipByDate")%>"><yfc:i18n>Ship_By_Date</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@Quantity")%>"><yfc:i18n>Qty</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@DispositionCode")%>"><yfc:i18n>Disposition</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Receipt/ReceiptLines/ReceiptLine/@InspectedBy")%>"><yfc:i18n>User</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Comments</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/Receipt/ReceiptLines/@ReceiptLine" id="ReceiptLine">
    <% double quantity = getNumericValue("xml:/ReceiptLine/@Quantity");
       String lineKey = getValue("ReceiptLine", "xml:/ReceiptLine/@OrigReceiptLineKey");
        String currentTranId = getValue("ReceiptLine", "xml:/ReceiptLine/@TransactionId");
        // Only records with quantity > 0 are relavant at this point.  Others are all history records.
        // Also, we are trying to group by transaction.  Therefore, show only those records that belong
        // to the current transaction.
       if ((quantity > 0) && (equals(currentTranId, tranid) || isVoid(tranid)) ){ %>	
            <tr>
                <yfc:makeXMLInput name="receiptLineKey">
                    <yfc:makeXMLKey binding="xml:/ReceiptHistory/@ReceiptHeaderKey" value="xml:/Receipt/@ReceiptHeaderKey" />
                    <yfc:makeXMLKey binding="xml:/ReceiptHistory/@ReceiptLineKey" value="xml:/ReceiptLine/@ReceiptLineKey" />
                </yfc:makeXMLInput>
                <td class="tablecolumn" nowrap="true">
                    <% //If OrigReceiptLineKey is not void, then this record has history.
                    if (!isVoid(lineKey)) {%>
                    <a <%=getDetailHrefOptions("L01", getParameter("receiptLineKey"), "")%>>
                    <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.RECEIPT_LINE_HISTORY, "Disposition_History")%>></a>
                    <%}%>
                </td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:ReceiptLine:/ReceiptLine/@ReceiptLineNo")%>">
                    <yfc:getXMLValue binding="xml:/ReceiptLine/@ReceiptLineNo"/>
                </td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/OrderLine/InventoryItem/Item/@ItemID"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/OrderLine/InventoryItem/Item/@ProductClass"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/OrderLine/InventoryItem/Item/@UnitOfMeasure"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@SerialNo"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@LotNumber"/></td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:ReceiptLine:/ReceiptLine/@ShipByDate")%>">
                    <yfc:getXMLValue binding="xml:/ReceiptLine/@ShipByDate"/>
                </td>
                <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:ReceiptLine:/ReceiptLine/@Quantity")%>">
                    <yfc:getXMLValue binding="xml:/ReceiptLine/@Quantity"/>
                </td>
                <td class="tablecolumn">
					<!--<yfc:getXMLValue binding="xml:/ReceiptLine/@DispositionCode"/>-->
					<%=getComboText("xml:/ReturnDispositionList/@ReturnDisposition" ,"Description" ,"DispositionCode" ,"xml:/ReceiptLine/@DispositionCode",true)%>
				</td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@InspectedBy"/></td>
                <td class="tablecolumn"><yfc:getXMLValue binding="xml:/ReceiptLine/@InspectionComments"/></td>
            </tr>
    <% } %>
        </yfc:loopXML> 
    </tbody>
</table>
