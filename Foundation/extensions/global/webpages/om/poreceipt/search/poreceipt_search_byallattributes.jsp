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
<tr> 
    <td>
        <input type="hidden" <%=getTextOptions("xml:/Receipt/Order/@DocumentType", "xml:/CurrentEntity/@DocumentType")%> />
        <input type="hidden" name="xml:/Receipt/@ReceiptDateQryType" value="DATERANGE"/>
    </td>
</tr>    
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receipt_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/@ReceiptNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/@ReceiptNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@ReceiptNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Order_#</yfc:i18n></td>
</tr>    
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/Order/@OrderNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Order/@OrderNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Order/@OrderNo")%> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/Receipt/Order/@EnterpriseCode" class="combobox">
            <% if (!isHub()) { %>
                <yfc:loopOptions binding="xml:/OrganizationList/Organization/EnterpriseOrgList/@OrgEnterprise" name="EnterpriseOrganizationKey"
                value="EnterpriseOrganizationKey" selected='<%=getSelectedValue("xml:/Receipt/Order/@EnterpriseCode")%>' />
            <% } else { %>
                <yfc:loopOptions binding="xml:EnterpriseList:/OrganizationList/@Organization" name="OrganizationCode"
                value="OrganizationCode" selected='<%=getSelectedValue("xml:/Receipt/Order/@EnterpriseCode")%>' />
            <% } %>
        </select>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Buyer</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/Order/@BuyerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Order/@BuyerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Order/@BuyerOrganizationCode")%> />
		<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
        <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Receipt/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Seller</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/Order/@SellerOrganizationCodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/Order/@SellerOrganizationCodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Order/@SellerOrganizationCode")%> />
        <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Receipt/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Receiving_Node</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/OrderRelease/@ReceivingNodeQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/OrderRelease/@ReceivingNodeQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/Order/OrderLines/OrderLine/@ReceivingNode")%> />
        <img class="lookupicon" onclick="callLookup(this,'shipnode')" name="search" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>Release_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/OrderRelease/@ReleaseNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/NumericQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/OrderRelease/@ReleaseNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/OrderRelease/@ReleaseNo")%> />
    </td>
</tr>
<tr>
    <td class="searchlabel" >
        <yfc:i18n>Receipt_Date</yfc:i18n>
    </td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@FromReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
        <yfc:i18n>To</yfc:i18n>
        <input class="dateinput" type="text" <%=getTextOptions("xml:/Receipt/@ToReceiptDate")%> />
        <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
    </td>
</tr>
<tr> 
    <td class="searchlabel" ><yfc:i18n>BOL_#</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select class="combobox" name="xml:/Receipt/@BolNoQryType">
            <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc" value="QueryType" selected="xml:/Receipt/@BolNoQryType"/>
        </select>
        <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Receipt/@BolNo")%> />
    </td>
</tr>
<tr>
    <td class="searchcriteriacell" nowrap="true">
        <input type="checkbox" <%=getCheckBoxOptions("xml:/Receipt/@OpenReceiptFlag", "xml:/Receipt/@OpenReceiptFlag", " ")%> yfcCheckedValue='Y' yfcUnCheckedValue=''><yfc:i18n>Open_Receipts_Only</yfc:i18n></input>
    </td>
</tr>
</table>
