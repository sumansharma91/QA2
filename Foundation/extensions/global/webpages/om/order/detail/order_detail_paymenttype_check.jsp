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
	String otherPaymentTypeEncrypted = request.getParameter("OtherPaymentTypeEncrypted");
    
	String path = request.getParameter("Path");
	if (isVoid(path)) {
        path="xml:/Order/PaymentMethods/PaymentMethod";
    }
	//YFCLogCategory catSecurity = YFCLogCategory.instance("com.stercomm.SecurityLogger");
	Logger catSecurity = LoggerFactory.getLogger("com.stercomm.SecurityLogger");
%>

<% if (!equals(incomplete,"Y")) {%> 

<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>
    <% if (userHasDecryptedPaymentAttributesPermissions()) {%>
		<% if (equals(otherPaymentTypeEncrypted,"Y")) {%>
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
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference3"/></td> 
</tr>

<% } else { %>

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
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@PaymentReference3","xml:/PaymentMethod/@PaymentReference3", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>

<% } %>
