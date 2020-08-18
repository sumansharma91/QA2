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
        <input type="hidden" <%=getTextOptions("xml:/OrderRelease/Order/@DocumentType", "xml:/CurrentEntity/@DocumentType")%> />
    </td>
</tr>   
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
    <td class="searchlabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
</tr>
<tr>
    <td nowrap="true" class="searchcriteriacell">
        <select name="xml:/OrderRelease/Order/@EnterpriseCode" class="combobox">
            <% if (!isHub()) { %>
                <yfc:loopOptions binding="xml:/OrganizationList/Organization/EnterpriseOrgList/@OrgEnterprise" name="EnterpriseOrganizationKey"
                value="EnterpriseOrganizationKey" selected='<%=getSelectedValue("xml:/OrderRelease/Order/@EnterpriseCode")%>' />
            <% } else { %>
                <yfc:loopOptions binding="xml:EnterpriseList:/OrganizationList/@Organization" name="OrganizationCode"
                value="OrganizationCode" selected='<%=getSelectedValue("xml:/OrderRelease/Order/@EnterpriseCode")%>' />
            <% } %>
        </select>
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
</table>
