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
            <input type="hidden" name="xml:/OrderRelease/@StatusQryType" value="BETWEEN"/>
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="DocumentTypeBinding" value="xml:/OrderRelease/Order/@DocumentType"/>
        <jsp:param name="EnterpriseCodeBinding" value="xml:/OrderRelease/Order/@EnterpriseCode"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
       // Statuses are refreshed.
    %>
    <yfc:callAPI apiID="AP1"/>

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
		<td class="searchcriteriacell" >
			<yfc:i18n>Delivery_Method</yfc:i18n>
		</td>
	</tr> 
	<tr> 
		<td>
			<select class="combobox" name="xml:/OrderRelease/@DeliveryMethod">
                <yfc:loopOptions binding="xml:DeliveryMethodList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/OrderRelease/@DeliveryMethod" isLocalized="Y"/>
			</select>
		</td>
	</tr> 

	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_Release_Line_Status</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" >
            <select name="xml:/OrderRelease/@FromStatus" class="combobox">
                <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description"
                value="Status" selected="xml:/OrderRelease/@FromStatus" isLocalized="Y"/>
            </select>
            <span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
        </td>   
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/OrderRelease/@ToStatus" class="combobox">
                <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description"
                value="Status" selected="xml:/OrderRelease/@ToStatus" isLocalized="Y"/>
            </select>
        </td>
    </tr>
</table>
