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
    document.body.attachEvent("onunload", processSaveRecordsForReleaseLines);
</script>

<table class="table" ID="ReleaseLines" width="100%" editable="true">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" nowrap="true" style="width:30px">&nbsp;</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@ItemDesc")%>"><yfc:i18n>Description</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/@StatusQuantity")%>"><yfc:i18n>Line_Qty</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/@Status")%>"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderRelease/@OrderLine" id="OrderLine">
        <tr>
            <yfc:makeXMLInput name="orderLineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReceivingNode" value="xml:/OrderRelease/@ReceivingNode"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@DocumentType" value="xml:/OrderRelease/Order/@DocumentType"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@EnterpriseCode" value="xml:/OrderRelease/@EnterpriseCode"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReleaseNo" value="xml:/OrderRelease/Order/@ReleaseNo"/>
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="statusKey">
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/OrderLine/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/OrderLine/@PrimeLineNo" value="xml:/OrderLine/@PrimeLineNo"/>
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderReleaseDetail/@OrderReleaseKey"/>
            </yfc:makeXMLInput>
            <td class="checkboxcolumn">
                <input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="ChkEntityKey"/>
                <input type="hidden" <%=getTextOptions("xml:/OrderRelease/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%>/>
            </td>
            <td class="tablecolumn" nowrap="true">              
                <yfc:hasXMLNode binding="xml:/OrderLine/KitLines/KitLine">
                    <a <%=getDetailHrefOptions("L03", getParameter("orderLineKey"), "")%>>
                        <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Kit_Components")%>>
                    </a>
                </yfc:hasXMLNode>
            </td>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
				<% if(showOrderLineNo("OrderRelease","Order")) {%>
	                <a <%=getDetailHrefOptions("L01",getParameter("orderLineKey"),"")%>>
						<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
					</a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
				<%}%>
            </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemShortDesc"/></td>
            <td class="numerictablecolumn" nowrap="true">
                <input type="text" <%=yfsGetTextOptions("xml:/OrderRelease/OrderLines/OrderLine_" + OrderLineCounter + "/OrderLineTranQuantity/@StatusQuantity", "xml:/OrderLine/OrderLineTranQuantity/@StatusQuantity", "xml:/OrderLine/AllowedModifications")%> style='width:40px' title='<%=getI18N("Open_Qty")%>: <%=getValue("OrderLine", "xml:/OrderLine/OrderLineTranQuantity/@OpenQty")%>'/>
            </td>
            <td class="tablecolumn">
                <a <%=getDetailHrefOptions("L02",getParameter("statusKey"),"ShowReleaseNo=N")%>>
                <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></a>
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
</table>
