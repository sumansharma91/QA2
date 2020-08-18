<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<script language="javascript">
	yfcDoNotPromptForChanges(true);
</script>
<table class="table" width="100%">
<thead>
    <tr>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ShipmentLineNo")%>"><yfc:i18n>Shipment_Line</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderNo")%>"><yfc:i18n><%=resolveValue("xml:/Receipt/@DocumentType") + "_"%>Order_#</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@OrderReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@UnitOfMeasure")%>"><yfc:i18n>Unit_Of_Measure</yfc:i18n></td>
        <td class="tablecolumnheader"  sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ProductClass")%>"><yfc:i18n>Product_Class</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@TotalQuantity")%>"><yfc:i18n>Expected_Quantity</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/GetLinesToReceive/ReceivableLineList/ReceivableLine/@ReceivedQuantity") %>"><yfc:i18n>Received_Quantity</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/GetLinesToReceive/ReceivableLineList/@ReceivableLine" id="receivableline">
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@ShipmentLineNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@OrderNo"/>
			</td>
            <td class="tablecolumn" >
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@ReleaseNo"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@ItemID"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@UnitOfMeasure"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@ProductClass"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@TotalQuantity"/>
			</td>
            <td class="tablecolumn">
				<yfc:getXMLValue binding="xml:receivableline:/ReceivableLine/@ReceivedQuantity"/>
			</td>
		</tr>
    </yfc:loopXML> 
</tbody>
</table>
