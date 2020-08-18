<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view">

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderRelease/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderRelease/Order/@EnterpriseCode"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
       // Product Classes and Unit of Measures are refreshed.
    %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>

<tr>
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@SalesOrderNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@SalesOrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@SalesOrderNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Release_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ReleaseNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ReleaseNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ReleaseNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Buyer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/Order/@BuyerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@BuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@BuyerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Seller</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/Order/@SellerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/Order/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/Order/@SellerOrganizationCode")%> />
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/OrderRelease/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Ship_Node</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ShipNodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ShipNodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ShipNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/@ReceivingNodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/@ReceivingNodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/@ReceivingNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Item_ID</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/OrderLine/Item/@ItemIDQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/OrderLine/Item/@ItemIDQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/OrderLine/Item/@ItemID")%> />
        <% String extraParams = getExtraParamsForTargetBinding("xml:/Item/@CallingOrganizationCode", getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode")); %>
        <img class="lookupicon" onclick="callItemLookup('xml:/OrderRelease/OrderLine/Item/@ItemID','xml:/OrderRelease/OrderLine/Item/@ProductClass','xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@TransactionalUOM','item','<%=extraParams%>')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Item_ID") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>UOM</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@TransactionalUOM" class="combobox" >
            <yfc:loopOptions binding="xml:UOMList:/ItemUOMMasterList/@ItemUOMMaster" 
                name="UnitOfMeasure" value="UnitOfMeasure" selected="xml:/OrderRelease/OrderLine/OrderLineTranQuantity/@TransactionalUOM"/>
        </select>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Product_Class</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderRelease/OrderLine/Item/@ProductClass" class="combobox" >
            <yfc:loopOptions binding="xml:ProductClassList:/CommonCodeList/@CommonCode" 
                name="CodeValue" value="CodeValue" selected="xml:/OrderRelease/OrderLine/Item/@ProductClass"/>
        </select>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Item_Description</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/OrderRelease/OrderLine/Item/@ItemDescQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/OrderRelease/OrderLine/Item/@ItemDescQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/OrderRelease/OrderLine/Item/@ItemDesc")%> />
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Supplier_Item_ID</yfc:i18n>
    </td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/OrderRelease/OrderLine/Item/@SupplierItemQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderRelease/OrderLine/Item/@SupplierItemQryType"/>
        </select>
        <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderRelease/OrderLine/Item/@SupplierItem")%>/>
    </td>
</tr> 
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Manufacturer_Item_ID</yfc:i18n>
    </td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <select name="xml:/OrderRelease/OrderLine/Item/@ManufacturerItemQryType" class="combobox">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
            value="QueryType" selected="xml:/OrderRelease/OrderLine/Item/@ManufacturerItemQryType"/>
        </select>
        <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/OrderRelease/OrderLine/Item/@ManufacturerItem")%>/>
    </td>
</tr>  
</table>
