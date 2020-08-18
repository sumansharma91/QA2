<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
	String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("Order", "xml:/Order/@EnterpriseCode"));
%>
<table class="table" width="100%" initialRows="5">
<thead>
<tr>
    <td sortable="no" class="checkboxheader">&nbsp;</td>
    <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/Order/OrderLines/OrderLine/KitLines/KitLine/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
    <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/Order/OrderLines/OrderLine/KitLines/KitLine/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
    <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/Order/OrderLines/OrderLine/KitLines/KitLine/KitLineTranQuantity/@TransactionalUOM")%>"><yfc:i18n>UOM</yfc:i18n></td>
    <td class=tablecolumnheader sortable="no" style="width:<%= getUITableSize("xml:/Order/OrderLines/OrderLine/KitLines/KitLine/KitLineTranQuantity/@KitQty")%>"><yfc:i18n>Qty_Per_Kit</yfc:i18n></td>
</tr>
</thead>
<tbody>
    <% /* hidden input added temporarily since api does not generate prime line no */ %>
    <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@Action", "CREATE")%>/>
    <yfc:loopXML binding="xml:/Order/OrderLines/OrderLine/KitLines/@KitLine" id="KitLine">
        <tr>
            <td class="checkboxcolumn" >&nbsp;</td>
            <td nowrap="true" class="tablecolumn">
                <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_" + KitLineCounter + "/@ItemID","xml:/KitLine/@ItemID")%> />
                <img class="lookupicon" onclick="callItemLookup('xml:/Order/OrderLines/OrderLine/KitLines/KitLine_' + <%=KitLineCounter%> +  '/@ItemID','xml:/Order/OrderLines/OrderLine/KitLines/KitLine_' + <%=KitLineCounter%> + '/@ProductClass','xml:/Order/OrderLines/OrderLine/KitLines/KitLine_' + <%=KitLineCounter%> + '/KitLineTranQuantity/@TransactionalUOM','item','<%=extraParams%>')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item") %> />
			</td>
            <td nowrap="true" class="tablecolumn">
                <select name="xml:/Order/OrderLines/OrderLine/KitLines/KitLine_<%=KitLineCounter%>/@ProductClass" binding="xml:/KitLine/@ProductClass" class="combobox" >
                    <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" 
                        name="CodeValue" value="CodeValue" selected="xml:/KitLine/@ProductClass"/>
                </select>
            </td>
            <td nowrap="true" class="tablecolumn">
                <select name="xml:/Order/OrderLines/OrderLine/KitLines/KitLine_<%=KitLineCounter%>/KitLineTranQuantity/@TransactionalUOM" binding="xml:/KitLine/KitLineTranQuantity/@TransactionalUOM" class="combobox" >
                    <yfc:loopOptions binding="xml:UOMList:/ItemUOMMasterList/@ItemUOMMaster" 
                        name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/KitLine/KitLineTranQuantity/@TransactionalUOM"/>
                </select>
            </td>
            <td class="tablecolumn" nowrap="true">
                <input type="text" class="numericunprotectedinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_" + KitLineCounter + "/KitLineTranQuantity/@KitQty", "xml:/KitLine/KitLineTranQuantity/@KitQty")%>/>
            </td>
        </tr>
    </yfc:loopXML>
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn" >
        </td>
        <td nowrap="true" class="tablecolumn">
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_/@ItemID", "xml:/Order/AllowedModifications", "ADD_LINE", "text")%> />
            <img class="lookupicon" onclick="templateRowCallItemLookup(this, 'ItemID','ProductClass','TransactionalUOM','item','<%=extraParams%>')" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item","xml:/KitLine/@AddLine","xml:/Order/AllowedModifications") %> />
        </td>
        <td nowrap="true" class="tablecolumn">
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_/@ProductClass", "xml:/Order/AllowedModifications", "ADD_LINE", "combo")%>>
                <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" 
                    name="CodeValue" value="CodeValue" selected=""/>
            </select>
        </td>
        <td nowrap="true" class="tablecolumn">
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_/KitLineTranQuantity/@TransactionalUOM", "xml:/Order/AllowedModifications", "ADD_LINE", "combo")%>>
                <yfc:loopOptions binding="xml:UOMList:/ItemUOMMasterList/@ItemUOMMaster" 
                    name="UnitOfMeasure" value="UnitOfMeasure" selected=""/>
            </select>
        </td>
        <td class="tablecolumn" nowrap="true">
            <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/KitLines/KitLine_/KitLineTranQuantity/@KitQty", "xml:/Order/AllowedModifications", "ADD_LINE", "text")%>>
        </td>
    </tr>
    <tr>
    	<td nowrap="true" colspan="6">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
</tfoot>
</table>
