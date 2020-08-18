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
	document.body.attachEvent("onunload", processSaveForTemplateOrder);
</script>


<%
    // Default the enterprise code if it is not passed
    String enterpriseCode = (String) request.getParameter("xml:/Order/@EnterpriseCode");
    if (isVoid(enterpriseCode)) {
        enterpriseCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@PrimaryEnterpriseKey");
        request.setAttribute("xml:/Order/@EnterpriseCode", enterpriseCode);
    }

	String orderHeaderKey = resolveValue("xml:/Order/@OrderHeaderKey");
%>

<script language="javascript">
<%  if (!isVoid(orderHeaderKey)) {	 
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
        <jsp:param name="ColumnLayout" value="1"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
        <jsp:param name="ShowOnlyTemplateDocTypes" value="true"/>
    </jsp:include>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code)
       // Currency and Order Type %>
    <yfc:callAPI apiID="AP1"/>
    <yfc:callAPI apiID="AP2"/>

    <tr>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
			<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Order_Name</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderName")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Currency</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Order/PriceInfo/@Currency")%>>
                <yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription"
                value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
            </select>
        </td>
    </tr>
</table>
