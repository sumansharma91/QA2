<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.dom.*" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<script language="Javascript" >
	IgnoreChangeNames();
	yfcDoNotPromptForChanges(true);
</script>

<%
	String orderNoVal = resolveValue("xml:NewOrder:/Order/@OrderNo");	
	String copyOrderHeaderKey = resolveValue("xml:/Order/@OrderHeaderKey");
%>

<script language="javascript">
    <% if (!isVoid(copyOrderHeaderKey)) {	 
		if (!isVoid(orderNoVal)) {	 
			YFCDocument orderDoc = YFCDocument.createDocument("Order");
        	orderDoc.getDocumentElement().setAttribute("OrderHeaderKey",resolveValue("xml:NewOrder:/Order/@OrderHeaderKey"));
    %>
				function showOrderDetailPopup() {
					window.CloseOnRefresh = "Y";
		            callPopupWithEntity('order', '<%=orderDoc.getDocumentElement().getString(false)%>');
					window.close();
				}

				function changeToOrderDetailView() {
					showDetailFor('<%=orderDoc.getDocumentElement().getString(false)%>');
				}

	<% 
			if (equals(request.getParameter(YFCUIBackendConsts.YFC_IN_POPUP), "Y")) { 
	%>
				window.attachEvent("onload", showOrderDetailPopup);
	<%		} else {
	%>
				window.attachEvent("onload", changeToOrderDetailView);		
	<%		}
		}
	   }
	%>
</script>

<!-- call getOrderDetails api for getting original order details -->
<yfc:callAPI apiID="AP1"/>
<yfc:callAPI apiID="AP2"/>

<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="Y"/>
            <input type="hidden" name="xml:/Order/@EnteredBy" value='<%=resolveValue("xml:CurrentUser:/User/@Loginid")%>'/>
			<input type="hidden" name="xml:/Order/@CopyFromOrderHeaderKey" value="<%=copyOrderHeaderKey%>"/>
		</td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Copy_From_Order_#</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@OrderNo"/></td>
        <td class="detaillabel" ><yfc:i18n>Document_Type</yfc:i18n></td>
		<td class="protectedtext">
			<yfc:getXMLValueI18NDB binding="xml:DocumentParamsList:/DocumentParamsList/DocumentParams/@Description"></yfc:getXMLValueI18NDB>
		</td>
        <td class="detaillabel" ><yfc:i18n>Enterprise</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@EnterpriseCode"/></td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Buyer</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@BuyerOrganizationCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Seller</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/Order/@SellerOrganizationCode"/></td>
        <td class="detaillabel" ><yfc:i18n>Order_#</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo","")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel"><yfc:i18n>Order_Name</yfc:i18n></td>
        <td>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderName")%>/>
        </td>
        <td class="detaillabel" ><yfc:i18n>Order_Date</yfc:i18n></td>
        <td nowrap="true">
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCDATE")%>/>
			<img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@OrderDate_YFCTIME")%>/>
			<img class="lookupicon" name="search" onclick="invokeTimeLookup(this);return false" <%=getImageOptions(YFSUIBackendConsts.TIME_LOOKUP_ICON, "Time_Lookup") %>/>
        </td>
    </tr>
</table>
