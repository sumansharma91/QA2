<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
    String chkBoxCounterPrefix = "chkOrderRelease_" ;
%>
<table class="table" editable="false" width="100%" cellspacing="0">
<thead> 
    <tr> 
        <td sortable="no" class="checkboxheader">
            <input type="checkbox" name="checkbox" value="checkbox" onclick='doCheckAll(this);'/>
			<yfc:makeXMLInput name="shipmentKey">
				<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:CreateShipment:/Shipment/@ShipmentKey" />
			</yfc:makeXMLInput>
			<input type="hidden" name="hidShipmentEntityKey" value='<%=getParameter("shipmentKey")%>' />&nbsp;
			<input type="hidden" <%=getTextOptions("hidShipmentKey","xml:CreateShipment:/Shipment/@ShipmentKey")%> />&nbsp;
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/Order/@OrderNo")%>"><yfc:i18n>Order_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/@ReleaseNo")%>"><yfc:i18n>Release_#</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/@ShipNode")%>"><yfc:i18n>Ship_Node</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/PersonInfoShipTo/@City")%>"><yfc:i18n>Ship_To</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/@ReqDeliveryDate")%>"><yfc:i18n>Expected_Delivery_Date</yfc:i18n></td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderReleaseList/@OrderRelease" id="OrderRelease" >
        <tr>
            <yfc:makeXMLInput name="orderReleaseKey">
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" />
            </yfc:makeXMLInput>
            <td class="checkboxcolumn"><input type="checkbox" value='<%=getParameter("orderReleaseKey")%>' name="EntityKey"
                yfcMultiSelectCounter='<%=OrderReleaseCounter%>' yfcMultiSelectValue1='<%=getValue("OrderRelease", "xml:/OrderRelease/@OrderReleaseKey")%>'
                yfcMultiSelectValue2='Add'/>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/Order/@OrderNo"/></td>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderRelease:/OrderRelease/@ReleaseNo")%>">
                <a href="javascript:showDetailFor('<%=getParameter("orderReleaseKey")%>');">
                <yfc:getXMLValue binding="xml:/OrderRelease/@ReleaseNo"/></a>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderRelease/PersonInfoShipTo/@City"/></td>

			<td class="tablecolumn" sortValue="<%=getDateValue("xml:/OrderRelease/@ReqDeliveryDate")%>"><yfc:getXMLValue binding="xml:/OrderRelease/@ReqDeliveryDate"/></td>
			<td class="tablecolumn"><%=displayOrderStatus(getValue("OrderRelease","xml:/OrderRelease/@MultipleStatusesExist"),getValue("OrderRelease","xml:/OrderRelease/@MaxOrderReleaseStatusDesc"),true)%></td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
