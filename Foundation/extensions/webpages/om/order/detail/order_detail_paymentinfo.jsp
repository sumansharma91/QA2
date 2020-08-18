<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<jsp:include page="/om/order/detail/order_detail_paymentinfo_include.jsp" flush="true"/>

<%
	String sRequestDOM = request.getParameter("getRequestDOM");
    preparePaymentStatusList(getValue("Order", "xml:/Order/@PaymentStatus"), (YFCElement) request.getAttribute("PaymentStatusList"));
    computePaymentAmounts((YFCElement) request.getAttribute("Order"));
%>

<table class="view" width="100%">
    <tr>
        <td class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
        <td>
            <select <% if (equals(sRequestDOM,"Y")) {%>OldValue="<%=resolveValue("xml:OrigAPIOutput:/Order/@PaymentStatus")%>" <%}%> <%=yfsGetComboOptions("xml:/Order/@PaymentStatus", "xml:/Order/AllowedModifications")%>>
                <yfc:loopOptions binding="xml:/PaymentStatusList/@PaymentStatus" name="DisplayDescription"
                value="CodeType" selected="xml:/Order/@PaymentStatus"/>
            </select>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Type</yfc:i18n></td>
        <% if (equals(getValue("Order","xml:/Order/@MultiplePaymentMethods"),"Y")) { %>    
        <td class="protectedtext"><yfc:i18n>MULTIPLE</yfc:i18n></td>
        <%} else {%>
        <td class="protectedtext">	<%=getComboText("xml:/PaymentTypeList/@PaymentType","PaymentTypeDescription","PaymentType","xml:/Order/PaymentMethods/PaymentMethod/@PaymentType",true)%>
		</td>
        <%}%>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Authorized</yfc:i18n></td>
        <td class="protectednumber"><span class="protectednumber" ><%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalOpenAuthorizations")%>&nbsp;</span><span class="protectedtext"  style="width:8px"></span></td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Collected</yfc:i18n></td>
        <td class="protectednumber"><span class="protectednumber" ><%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@TotalCredits")%>&nbsp;</span><span class="protectedtext"  style="width:8px"></span></td>
    </tr>

	<% if(equals(getValue("Order", "xml:/Order/@OrderPurpose"), "EXCHANGE")){ %>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Funds_Available_From_Return</yfc:i18n></td>
        <td class="protectednumber"><span class="protectednumber" ><%=getValue("Order", "xml:/Order/ChargeTransactionDetails/@FundsAvailableFromReturn")%>&nbsp;</span><span class="protectedtext"  style="width:8px"></span></td>
    </tr>
	<% } %>
</table>
