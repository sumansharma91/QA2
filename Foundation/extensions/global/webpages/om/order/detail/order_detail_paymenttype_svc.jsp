<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>
<%@ page import="org.slf4j.*" %>
<%@ page import="com.yantra.yfc.log.*" %>
<%@ page import="com.sterlingcommerce.security.csrf.SCUIcsrfHelper" %>
<script language="javascript">
	window.attachEvent("onload", onLoadError);

	<%String errorString = HTMLEncode.htmlEscape(request.getParameter("paymentservererror"));
		if ("".equals(errorString))
		errorString = null;
	%>

	function onLoadError(){
		var error = <%=errorString%>;
		if(error != null && error != ""){
			alert(error);
			window.close();
		}
	}
</script>

<%
boolean tokenizeSVC = isStoreValueCardNoTokenized();
%>
<%  String incomplete = request.getParameter("Incomplete");
	String modifyFlag = request.getParameter("ModifyFlag");
	String storedValueCardEncrypted = request.getParameter("StoredValueCardEncrypted");

	String path = request.getParameter("Path");
	String paymentType = request.getParameter("PaymentTypeBinding");
	String paymentTypeValue = "";
	if (!YFCCommon.isVoid(paymentType) && !YFCCommon.isVoid(resolveValue(paymentType)))
		paymentTypeValue = resolveValue(paymentType);

    if (isVoid(path)) {
        path = "xml:/Order/PaymentMethods/PaymentMethod";
    }
		String iframeSrc = request.getContextPath() + "/om/order/detail/order_detail_paymenttype_svc_tokenizer.jsp" ;
	boolean firstParam = true;
	String paramString = "?";
	if (!YFCCommon.isVoid(paymentTypeValue)) {
		paramString += "ssdcsDataTypeDetail=" + paymentTypeValue;
		firstParam = false;
	}
	if (!YFCCommon.isVoid(resolveValue(path + "/@DisplaySvcNo"))) {
		if (!firstParam)
			paramString += "&";
		paramString += "ssdcsDisplayValue=" + resolveValue(path + "/@DisplaySvcNo");
		firstParam = false;
	}
	String allowedValue = getModificationAllowedValueFromNameOrModType( path + "/@SvcNo", "xml:/Order/AllowedModifications",null);
	boolean setOverrideCss = (!YFCCommon.equals(allowedValue, "N") && !YFCCommon.equals(allowedValue, "Y")) && userHasOverridePermissions();
	if (!firstParam)
		paramString += "&";
	paramString += "setOverrideCss=" + String.valueOf(setOverrideCss);
	firstParam = false;
	//YFCLogCategory catSecurity = YFCLogCategory.instance("com.stercomm.SecurityLogger");
	Logger catSecurity = LoggerFactory.getLogger("com.stercomm.SecurityLogger");
%>

<% if (!equals(incomplete,"Y")) {%> 

<tr>
    <td class="detaillabel" ><yfc:i18n>Stored_Value_Card_#</yfc:i18n></td>
	<td class="protectedtext">
		<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplaySvcNo"))%>
	</td>     
</tr>

<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#1</yfc:i18n></td>
	<td>
		<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@PaymentReference1", "xml:/PaymentMethod/@PaymentReference1")%>/>
	</td>

</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#2</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference2"/></td> 
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
    <td class="protectedtext"><yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@PaymentReference3"/></td> 
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Funds_Available</yfc:i18n></td>
    <td class="protectedtext">
	<% if(equals(getValue("PaymentMethod", "xml:/PaymentMethod/@GetFundsAvailableUserExitInvoked"), "Y")) {%>

	<% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> <yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@FundsAvailable"/>
						<%=curr0[1]%> 
	<% } else {%>
			<yfc:i18n>Not_Applicable</yfc:i18n>
	<%} %>
    </td>
</tr>

<% } else {
	String servererror = HTMLEncode.htmlEscape(request.getParameter("paymentservererrorforadd"));
	boolean isServerAvailable = (servererror != null && !servererror.equals("") && !"null".equalsIgnoreCase(servererror)) ? false : true; //isPaymentServerAvailable();

%>

<tr>
    <td class="detaillabel" ><yfc:i18n>Stored_Value_Card_#</yfc:i18n></td>
    <td>
		<input type="hidden" id="paymenttype" name='paymenttype' value='STORED_VALUE_CARD'/>
		<input type="hidden" id="paymentpath" name='paymentpath' value='<%=HTMLEncode.htmlEscape(path)%>'/>
		<input type="hidden" id="tokenizeSVC" name='tokenizeSVC' value='<%=tokenizeSVC%>'/>
		<input type="hidden" id="paymentserver" name='paymentserver' value='<%=isServerAvailable%>'/>
		<% if (equals(modifyFlag,"Y") && !"N".equals(resolveValue(path + "/@callcreatetokensvc"))) {%> 
			<% if (tokenizeSVC && isServerAvailable) { %>
				<input type="hidden" name='<%=HTMLEncode.htmlEscape(path)%>/@SvcNo'/>
				<input type="hidden" name='<%=HTMLEncode.htmlEscape(path)%>/@DisplaySvcNo'/>
				<%
					YFCElement elem = (YFCElement)request.getSession().getAttribute("CurrentUser");
					if(elem == null)
						throw new YFCException("Current user is not authenticated.");
					String userId = elem.getAttribute("Loginid");
					YFCElement inputElem = YFCDocument.parse("<AccessToken UserId=\""+userId+"\" ApplicationName=\"YFSSYS00004\" />").getDocumentElement();
					YFCElement templateElem = YFCDocument.parse("<AccessToken  TokenExpireTS=\"\" TokenValue=\"\"/>").getDocumentElement();
				%>
				<yfc:callAPI apiName="createAccessToken" inputElement="<%=inputElem%>" templateElement="<%=templateElem%>" outputNamespace="AccessToken"/>
				<%
					if (!YFCCommon.isVoid(resolveValue("xml:/AccessToken/@TokenValue"))) {
						if (!firstParam)
							paramString += "&";
						paramString += "TokenValue=" + resolveValue("xml:/AccessToken/@TokenValue");
					}
				%>
				<script language="javascript" >
				yfcDoNotPromptForChanges(true);
				</script>
				<%=writeDocumentDomain()%>
				<%
				String framesrc = SCUIcsrfHelper.appendCsrfParam(request, response, iframeSrc+paramString);
				%>
				<iframe  style="padding:0px;margin:0px;border-width: 0px;border-style: none; border-style: hidden; border-collapse: collapse;font-size: 11;font-family: Tahoma;height: 18px;width:16em;" frameborder="0"  SCROLLING="NO" id="svcframe" name="svcframe" src="<%=framesrc%>"  onload="readResults('svcframe', true);" doSSDCSSave="N"></iframe>
			<% } else { %>
			<input <%=yfsGetTextOptions(path + "/@SvcNo","xml:/PaymentMethod/@SvcNo", "xml:/Order/AllowedModifications")%>>
			<% } %>
		<% } else { %>
		<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@SvcNo", "xml:/PaymentMethod/@SvcNo")%>/>
		<% } %>

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
<tr>
    <td class="detaillabel" ><yfc:i18n>Payment_Reference_#3</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@PaymentReference3","xml:/PaymentMethod/@PaymentReference3", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<% } %>
