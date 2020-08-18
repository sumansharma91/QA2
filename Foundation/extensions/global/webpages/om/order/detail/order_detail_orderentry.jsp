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

	// Default the seller to logged in organization if it plays a role of seller
    String sellerOrgCode = (String) request.getParameter("xml:/Order/@SellerOrganizationCode");
    if (isVoid(sellerOrgCode)) {
        if(isRoleDefaultingRequired((YFCElement) request.getAttribute("CurrentOrgRoleList"))){
            sellerOrgCode = getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@OrganizationCode");
            //System.out.println("org:" + orgCode);
            //request.setAttribute("xml:/Order/@SellerOrganizationCode", orgCode);
        }
    }
    
    //prepareMasterDataElements(enterpriseCode, (YFCElement) request.getAttribute("OrganizationList"),
    //                        (YFCElement) request.getAttribute("EnterpriseParticipationList"),
    //                        (YFCElement) request.getAttribute("CurrencyList"),
    //                        (YFCElement) request.getAttribute("OrderTypeList"),getValue("CurrentOrganization", "xml:CurrentOrganization:/Organization/@IsHubOrganization"));

	String exchangeOrderForReturn = resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange");
	String orderHeaderKeyVal = resolveValue("xml:/Order/@OrderHeaderKey");	
%>

<script language="javascript">
    <% if (!equals(exchangeOrderForReturn, "Y")) {

        if (!isVoid(orderHeaderKeyVal)) {	 
            YFCDocument orderDoc = YFCDocument.createDocument("Order");
            orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:/Order/@OrderHeaderKey"));

            // If this screen is shown as a popup, then open the order detail view for the new order
            // as a popup as well (instead of refreshing the same screen).
            if (equals(request.getParameter(YFCUIBackendConsts.YFC_IN_POPUP), "Y")) {
            %>
                function showOrderDetailPopup() {
					window.CloseOnRefresh = "Y";
		            callPopupWithEntity('order', '<%=orderDoc.getDocumentElement().getString(false)%>');
					window.close();
				}
                window.attachEvent("onload", showOrderDetailPopup);
            <%
            } else {
            %>
                function showOrderDetail() {
				    showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
				}
                window.attachEvent("onload", showOrderDetail);
            <% }
        }
    }
	%>
</script>
<%
	//exchange order processing
	boolean isExchangeOrderCreation = false;
	if(!isVoid(exchangeOrderForReturn)){
		isExchangeOrderCreation = true;
		//call getOrderDetails api for defaulting information onto exchange order.
%>
	    <yfc:callAPI apiID="AP5"/>
<%	} %>


<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
            <input type="hidden" name="xml:/Order/@EnteredBy" value="<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>"/>
			<% if(isExchangeOrderCreation){ %>
				<input type="hidden" name="xml:/Order/@ReturnOrderHeaderKeyForExchange" value='<%=exchangeOrderForReturn%>'/>
				<input type="hidden" name="xml:/Order/@OrderPurpose" value='EXCHANGE'/>
				<input type="hidden" name="xml:/Order/@DocumentType" value='0001'/>
				<input type="hidden" name="xml:/Order/@ShipToKey" value="<%=resolveValue("xml:/Order/@ShipToKey")%>"/>
				<input type="hidden" name="xml:/Order/@BillToKey" value="<%=resolveValue("xml:/Order/@BillToKey")%>"/>
			<% } %>
		</td>
    </tr>

	<% if(isExchangeOrderCreation){	%>
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="ScreenType" value="detail"/>
        <jsp:param name="HardCodeDocumentType" value="0001"/>
        <jsp:param name="ApplicationCode" value="omd"/>
        <jsp:param name="DisableEnterprise" value="Y"/>
    </jsp:include>
	<% } else {%>
    <jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
        <jsp:param name="ScreenType" value="detail"/>
        <jsp:param name="RefreshOnDocumentType" value="true"/>
        <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
    </jsp:include>
	<% } %>
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP1"/>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP4"/>

    <tr>
		<% String enterpriseCodeFromCommon = getValue("CommonFields", "xml:/CommonFields/@EnterpriseCode");%>		

        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>

		<% if (!isExchangeOrderCreation){ %>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BuyerOrganizationCode")%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'BUYER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
		<% } else { %>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
            <input type="hidden" name="xml:/Order/@BuyerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@BuyerOrganizationCode")%>"/>
		<% } %>

        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>

		<% if (!isExchangeOrderCreation){ %>
        <td nowrap="true" >
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@SellerOrganizationCode", "xml:/Order/@SellerOrganizationCode", sellerOrgCode)%>/>
            <img class="lookupicon" onclick="callLookupForOrder(this,'SELLER','<%=enterpriseCodeFromCommon%>','xml:/Order/@DocumentType')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Organization")%>/>
        </td>
		<% } else { %>
	        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
            <input type="hidden" name="xml:/Order/@SellerOrganizationCode" value="<%=getValue("Order", "xml:/Order/@SellerOrganizationCode")%>"/>
		<% } %>

    </tr>
    <tr>

        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Type</yfc:i18n></td>
        <td>
            <select class="combobox" <%=getComboOptions("xml:/Order/@OrderType")%>>
                <yfc:loopOptions binding="xml:OrderTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" isLocalized="Y"/>
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
                <yfc:loopOptions binding="xml:/CurrencyList/@Currency" name="CurrencyDescription"
                value="Currency" selected="xml:/Order/PriceInfo/@Currency" isLocalized="Y"/>
            </select>
        </td>
		<% if(isExchangeOrderCreation){ %>
        <td class="detaillabel" >
            <yfc:i18n>Exchange_Type</yfc:i18n>
        </td>
        <td>
            <select name="xml:/Order/@ExchangeType" class="combobox">
                <yfc:loopOptions binding="xml:ExchangeTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
                value="CodeValue" selected="xml:/Order/@ExchangeType" isLocalized="Y"/>
            </select>
        </td>
		<% } %>
    </tr>
</table>
