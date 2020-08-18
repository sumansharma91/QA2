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

<%
    String draftOrderFlag = request.getParameter("DraftOrderFlag");
	String listViewId = request.getParameter("yfcListViewGroupId");
    if (isVoid(draftOrderFlag)) {
        draftOrderFlag = "N";
    }
%>

<table class="view">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="<%=HTMLEncode.htmlEscape(draftOrderFlag)%>"/>
			<input type="hidden" name="yfcListViewGroupId" value="<%=HTMLEncode.htmlEscape(listViewId)%>"/>
        </td>
    </tr>

    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
	    <jsp:param name="ScreenType" value="search"/>
    </jsp:include>

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@BuyerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@BuyerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCode = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Seller</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@SellerOrganizationCodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@SellerOrganizationCodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" name="search" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCode%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Buyer_Account_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/PaymentMethod/@CustomerAccountNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/PaymentMethod/@CustomerAccountNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/PaymentMethod/@CustomerAccountNo")%>/>
        </td>
    </tr>    
</table>
