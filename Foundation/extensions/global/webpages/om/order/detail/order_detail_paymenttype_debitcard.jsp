<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="com.yantra.yfc.log.*" %>
<%@ page import="com.sterlingcommerce.security.csrf.SCUIcsrfHelper" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>
<%
	String incomplete = request.getParameter("Incomplete");

	String PaymentMethodCounter = getParameter("PaymentMethodCounter");
	String paymentType = request.getParameter("PaymentTypeBinding");
	String paymentTypeValue = "";
	if (!YFCCommon.isVoid(paymentType) && !YFCCommon.isVoid(resolveValue(paymentType)))
		paymentTypeValue = resolveValue(paymentType);

%>
<tr>
    <td class="detaillabel" ><yfc:i18n>Debit_Card_#</yfc:i18n></td>
	<td class="protectedtext">
		<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayDebitCardNo"))%>
	</td>     
</tr>

<tr>
    <td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
	<td>
		<input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@CreditCardExpDate","xml:/PaymentMethod/@CreditCardExpDate","xml:/Order/AllowedModifications")%>/>
	</td>     
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Card_Type</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CreditCardType"/></td>     
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Name_On_Card</yfc:i18n></td>
	<td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CreditCardName"/></td>     
</tr>
<tr>
<td class="detaillabel" ><yfc:i18n>Allow_Expiration_Date_Override</yfc:i18n></td>
                <td>
				<input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@AllowPaymentMethodInfoOverride","xml:/PaymentMethod/@AllowPaymentMethodInfoOverride","Y","xml:/Order/AllowedModifications")%>/>
					
                </td>
</tr>
