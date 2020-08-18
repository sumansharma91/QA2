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

<% boolean isAgainstOrder = isTrue("xml:/OrderRelease/@HasDerivedParent"); 
   String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("OrderRelease", "xml:/OrderRelease/Order/@EnterpriseCode"));
%>
<yfc:makeXMLInput name="FindReceiptKey" >
	<yfc:makeXMLKey binding="xml:/Receipt/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey" />
	<yfc:makeXMLKey binding="xml:/Receipt/@DocumentType" value="xml:/OrderRelease/Order/@DocumentType" />
	<yfc:makeXMLKey binding="xml:/Receipt/@ReceivingNode" value="xml:/OrderRelease/@ShipNode" />
	<yfc:makeXMLKey binding="xml:/Receipt/Shipment/@EnterpriseCode" value="xml:/OrderRelease/Order/@EnterpriseCode"/>
</yfc:makeXMLInput>
<input type="hidden" name="findReceiptKey" value="<%=getParameter("FindReceiptKey")%>"/>

<table class="table" width="100%" editable="true" ID="ReleaseLines">
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" value="checkbox" name="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" nowrap="true" style="width:30px">&nbsp;</td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
        <% if (isAgainstOrder == true) {%>
        <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderRelease/Order/@OrderNo")%>"><yfc:i18n>Order_No/Line_No</yfc:i18n></td>
        <%}%>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
        <td class="tablecolumnheader" ><yfc:i18n>Description</yfc:i18n></td>
        <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderRelease/OrderLine/@ReturnReason")%>"><yfc:i18n>Reason_Code</yfc:i18n></td>
        <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderRelease/OrderLine/@LineType")%>"><yfc:i18n>Line_Type</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@StatusQuantity")%>"><yfc:i18n>Line_Qty</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderRelease/@OrderLine" id="OrderLine">
        <tr>
            <yfc:makeXMLInput name="orderLineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrigOrderLineKey" value="xml:/OrderLine/@OrigOrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderReleaseKey" value="xml:/OrderRelease/@OrderReleaseKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderRelease/@OrderHeaderKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReceivingNode" value="xml:/OrderRelease/@ReceivingNode"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@ShipNode" value="xml:/OrderRelease/@ShipNode"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@DocumentType" value="xml:/OrderRelease/Order/@DocumentType"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@EnterpriseCode" value="xml:/OrderRelease/@EnterpriseCode"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@ReleaseNo" value="xml:/OrderRelease/Order/@ReleaseNo"/>
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="statusKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderReleaseKey" value="xml:/OrderReleaseDetail/@OrderReleaseKey"/>
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="derivedFromLineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@DerivedFromOrderLineKey"/>
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="derivedFromOrderKey">
                <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderLine/@DerivedFromOrderHeaderKey"/>
            </yfc:makeXMLInput>
            <td class="checkboxcolumn">
                <input type="checkbox" value='<%=getParameter("orderLineKey")%>' name="chkEntityKey"/>
                <input type="hidden" <%=getTextOptions("xml:/OrderRelease/OrderLines/OrderLine_" + OrderLineCounter + "/@OrderLineKey", "xml:/OrderLine/@OrderLineKey")%>/>
            </td>
            <td class="tablecolumn" nowrap="true">              
                <yfc:hasXMLNode binding="xml:/OrderLine/KitLines/KitLine">
                    <a <%=getDetailHrefOptions("L05", getParameter("orderLineKey"), "")%>>
                        <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.KIT_COMPONENTS_COLUMN, "Kit_Components")%>>
                    </a>
                </yfc:hasXMLNode>
            </td>
            <td class="tablecolumn" sortValue="<%=getNumericValue("xml:OrderLine:/OrderLine/@PrimeLineNo")%>">
				<% if(showOrderLineNo("Order","Order")) {%>
	                <a <%=getDetailHrefOptions("L01",getParameter("orderLineKey"),"")%>>
						<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
					</a>
				<%} else {%>
					<yfc:getXMLValue binding="xml:/OrderLine/@PrimeLineNo"/>
				<%}%>
			</td>
            <% if (isAgainstOrder == true) {%>
            <td class="tablecolumn">
				<% if(showOrderNo("Order","Order")) {%>
                    <a <%=getDetailHrefOptions("L02", getParameter("derivedFromOrderKey"), "")%>>
                        <yfc:getXMLValue binding="xml:/OrderLine/DerivedFromOrder/@OrderNo"/>
					</a>&nbsp;
				<%} else {%>
					<yfc:getXMLValue binding="xml:/Order/@OrderNo"/>
				<%}%>
            </td>
            <% } %>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemID"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ProductClass"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/Item/@ItemShortDesc"/></td>
             <td class="tablecolumn"><%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode" ,"CodeShortDescription" ,"CodeValue" ,"xml:/OrderLine/@ReturnReason",true)%>
			 </td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/@LineType"/></td>
            <td class="tablecolumn"><yfc:getXMLValue binding="xml:/OrderLine/OrderLineTranQuantity/@StatusQuantity"/></td>
            <td class="tablecolumn">
                <a <%=getDetailHrefOptions("L04",getParameter("statusKey"),"ShowReleaseNo=N")%>>
                <%=displayOrderStatus(getValue("OrderLine","xml:/OrderLine/@MultipleStatusesExist"),getValue("OrderLine","xml:/OrderLine/@MaxLineStatusDesc"),true)%></a>
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
<yfc:hasXMLNode binding="xml:/OrderRelease/DerivedFromOrderHeader/@OrderNo">
    <tfoot>
        <tr style='display:none' TemplateRow="true">
            <td class="checkboxcolumn">
            </td>
            <td class="tablecolumn">
                <input type="hidden" <%=getTextOptions("xml:/OrderRelease/OrderLine_/@Action", "", "CREATE")%> />
			</td>
            <td class="tablecolumn" nowrap="true">
                <input type="text" <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/Item/@ItemID", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "text")%>/>
                <img class="lookupicon" onclick="templateRowCallItemLookup(this,'ItemID','ProductClass','TransactionalUOM','item','<%=extraParams%>')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item")%>/>
            </td>
            <td class="tablecolumn">
                <select <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/Item/@ProductClass", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "combo")%>>
                    <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" name="CodeValue"
                    value="CodeValue" selected="xml:/OrderRelease/OrderLine/Item/@ProductClass"/>
                </select>
            </td>
            <td class="tablecolumn">
                <select <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/OrderLineTranQuantity/@TransactionalUOM", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "combo")%>>
                    <yfc:loopOptions binding="xml:UnitOfMeasureList:/ItemUOMMasterList/@ItemUOMMaster" name="UnitOfMeasure"
                    value="UnitOfMeasure" selected="xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/>
                </select>
            </td>
            <td class="tablecolumn">&nbsp;</td>
            <td class="tablecolumn">
                <select <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/@ReturnReasonCode", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "combo")%>>
                    <yfc:loopOptions binding="xml:ReasonCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderRelease/OrderLine/@ReturnReasonCode" isLocalized="Y"/>
                </select>
            </td>
            <td class="tablecolumn">
                <select <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/@ReceiptType", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "combo")%>>
                    <yfc:loopOptions binding="xml:ReceiptTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderRelease/OrderLine/@ReceiptType" isLocalized="Y"/>
                </select>
            </td>
            <td class="numerictablecolumn">
                <input type="text" <%=yfsGetTemplateRowOptions("xml:/OrderRelease/OrderLines/OrderLine_/OrderLineTranQuantity/@OrderedQty", "xml:/OrderRelease/AllowedModifications", "ADD_LINE", "text")%>>
            </td>
            <td class="tablecolumn">&nbsp;</td>
            <td class="tablecolumn">&nbsp;</td>
        </tr>
		<%if (isModificationAllowed("xml:/@AddLine","xml:/OrderRelease/AllowedModifications")) { %>
        <tr>
        	<td nowrap="true" colspan="13">
        		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
        	</td>
        </tr>
        <%}%>
    </tfoot>
</yfc:hasXMLNode>
</table>
