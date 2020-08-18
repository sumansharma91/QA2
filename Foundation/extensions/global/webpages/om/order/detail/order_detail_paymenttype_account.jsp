<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>

<%@ page import="org.slf4j.*" %>
<%@ page import="com.yantra.yfc.log.*" %>

<%  String incomplete = request.getParameter("Incomplete");
	String customerAccountEncrypted = request.getParameter("CustomerAccountEncrypted");

	String path = request.getParameter("Path");
    if (isVoid(path)) {
        path = "xml:/Order/PaymentMethods/PaymentMethod";
    }
	//YFCLogCategory catSecurity = YFCLogCategory.instance("com.stercomm.SecurityLogger");
	Logger catSecurity = LoggerFactory.getLogger("com.stercomm.SecurityLogger");
%>

<% if (!equals(incomplete,"Y")) {%> 
<!-- reverted removal of DecryptedPaymentAttributesPermissions - 205880 -->
<tr>
    <td class="detaillabel" ><yfc:i18n>Customer_Account_#</yfc:i18n></td>
    <% if (userHasDecryptedPaymentAttributesPermissions()) {%>
		<% if (equals(customerAccountEncrypted,"Y")) {%>
			<%
				String encryptedString = getValue("PaymentMethod", "xml:/PaymentMethod/@CustomerAccountNo");
				YFCElement inputElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+encryptedString+"\"/>").getDocumentElement();
				YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();
			%>
			<% logPaymentInfo(catSecurity,"CustomerAccountNo","DisplayCustomerAccountNo",getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayCustomerAccountNo"), (YFCElement)request.getAttribute("Order")); %>
			<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedString"/>
			<td>
				<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/GetDecryptedString/@DecryptedString", "xml:/GetDecryptedString/@DecryptedString")%>/>
			</td>
	    <% } else { %>
			<td>
				<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@CustomerAccountNo", "xml:/PaymentMethod/@CustomerAccountNo")%>/>
			</td>
	    <% } %>
    <% } else { %>
        <td class="protectedtext">
			<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayCustomerAccountNo"))%>&nbsp;
		</td>
    <% } %>
</tr>

<tr>
    <td class="detaillabel" ><yfc:i18n>Customer_PO_#</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CustomerPONo"/></td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>

    <% if (userHasDecryptedPaymentAttributesPermissions()) {%>
		<% if (equals(customerAccountEncrypted,"Y")) {%>
			<%
				String encryptedString = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentReference1");
				YFCElement inputElem = YFCDocument.parse("<GetDecryptedString StringToDecrypt=\""+encryptedString+"\"/>").getDocumentElement();
				YFCElement templateElem = YFCDocument.parse("<GetDecryptedString DecryptedString=\"\"/>").getDocumentElement();
			%>
			<% logPaymentInfo(catSecurity,"PaymentReference1","DisplayPaymentReference1",getValue("PaymentMethod","xml:/PaymentMethod/@DisplayPaymentReference1"),(YFCElement)request.getAttribute("Order")); %>

			<yfc:callAPI apiName="getDecryptedString" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="GetDecryptedString"/>
			<td>
				<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/GetDecryptedString/@DecryptedString", "xml:/GetDecryptedString/@DecryptedString")%>/>
			</td>
	    <% } else { %>
			<td>
				<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@PaymentReference1", "xml:/PaymentMethod/@PaymentReference1")%>/>
			</td>
		<% } %>
	<% } else { %>
        <td class="protectedtext">
			<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayPaymentReference1"))%>&nbsp;
		</td>
    <% } %>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#2</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference2"/></td>
</tr>

<% } else { %>

<tr>
    <td class="detaillabel" ><yfc:i18n>Customer_Account_#</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@CustomerAccountNo","xml:/PaymentMethod/@CustomerAccountNo", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Customer_PO_#</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@CustomerPONo","xml:/PaymentMethod/@CustomerPONo", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@PaymentReference1","xml:/PaymentMethod/@PaymentReference1", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#2</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@PaymentReference2","xml:/PaymentMethod/@PaymentReference2", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>

<% } %>
