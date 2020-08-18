<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/orderentry.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
    // Default the enterprise code if it is not passed
    String enterpriseCode = (String) request.getParameter("xml:/Order/@EnterpriseCode");
    if (isVoid(enterpriseCode)) {
        enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
        request.setAttribute("xml:/Order/@EnterpriseCode", enterpriseCode);
    }
    
    // Default the buyer to logged in organization if it plays a role of buyer
    String buyerOrgCode = (String) request.getParameter("xml:/Order/@BuyerOrganizationCode");
    if (isVoid(buyerOrgCode)) {
        if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))){
            buyerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
        }
    }

	String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");	
%>
<script language="javascript">
<%    if (!isVoid(orderHeaderKeyVal)) {	 
	YFCDocument orderDoc = YFCDocument.createDocument("Order");
	orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:/Order/@OrderHeaderKey"));
	%>
	showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
<%	}	%>
</script>

<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
            <input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
        </td>
    </tr>
    
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="ScreenType" value="detail"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP1"/>
    <yfc:callAPI apiID="AP2"/>
    
    <tr>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode", "xml:/Order/@BuyerOrganizationCode", buyerOrgCode)%>/>
			<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
                <yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
        <td nowrap="true">
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME")%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Order_Name</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderName")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
                <yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription" value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
            </select>
        </td>
        <td/>
        <td>
        </td>
    </tr>
</table>
