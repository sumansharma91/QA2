<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ page import="org.slf4j.*" %>
<%@ page import="com.yantra.yfc.log.*" %>
<%@ page import="com.sterlingcommerce.security.csrf.SCUIcsrfHelper" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>
<script language="javascript">
	window.attachEvent("onload", onLoadError);

	<%
		String errorString = HTMLEncode.htmlEscape(request.getParameter("paymentservererror"));
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
boolean tokenizeCC = isCreditCardNoTokenized();
String filename = "order_detail_paymenttype_creditcard_tokenizer.jsp";
java.net.URL oURL=pageContext.getServletContext().getResource("/om/order/detail/"+filename);
%>
<%
	String incomplete = request.getParameter("Incomplete");
	String modifyFlag = request.getParameter("ModifyFlag");

	String creditCardEncrypted = request.getParameter("CreditCardEncrypted");
	String creditCardAddnlEncrypted = request.getParameter("CreditCardAddnlEncrypted");
	String PaymentMethodCounter = getParameter("PaymentMethodCounter");
	String paymentType = request.getParameter("PaymentTypeBinding");
	String paymentTypeValue = "";
	if (!YFCCommon.isVoid(paymentType) && !YFCCommon.isVoid(resolveValue(paymentType)))
		paymentTypeValue = resolveValue(paymentType);


	String path = request.getParameter("Path"); 
    if (isVoid(path)) {
        path="xml:/Order/PaymentMethods/PaymentMethod";
    }

	String iframeSrc = request.getContextPath() + "/om/order/detail/order_detail_paymenttype_creditcard_tokenizer.jsp" ; 
	boolean firstParam = true;
	String paramString = "?";
	if (!YFCCommon.isVoid(paymentTypeValue)) {
		paramString += "ssdcsDataTypeDetail=" + paymentTypeValue;
		firstParam = false;
	}
	if (!YFCCommon.isVoid(resolveValue(path + "/@DisplayCreditCardNo"))) {
		if (!firstParam)
			paramString += "&";
		paramString += "ssdcsDisplayValue=" + resolveValue(path + "/@DisplayCreditCardNo");
		firstParam = false;
	}
	String allowedValue = getModificationAllowedValueFromNameOrModType( path + "/@CreditCardNo", "xml:/Order/AllowedModifications",null);
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
    <td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
	<td class="protectedtext">
		<%=showEncryptedString(getValue("PaymentMethod", "xml:/PaymentMethod/@DisplayCreditCardNo"))%>
	</td>     
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
	<td>
		<input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@CreditCardExpDate","xml:/PaymentMethod/@CreditCardExpDate","xml:/Order/AllowedModifications")%>/>
	</td>     
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Credit_Card_Type</yfc:i18n></td>
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
<% } else { 
	String servererror = HTMLEncode.htmlEscape(request.getParameter("paymentservererrorforadd"));
	boolean isServerAvailable = (servererror != null && !servererror.equals("") && !"null".equalsIgnoreCase(servererror)) ? false : true; //isPaymentServerAvailable();
%>

<tr>
    <td class="detaillabel" ><yfc:i18n>Credit_Card_#</yfc:i18n></td>
    <td>
		<input type="hidden" id="paymenttype"  name='paymenttype' value='CREDIT_CARD'/>
		<input type="hidden" id="paymentpath" name='paymentpath' value='<%=HTMLEncode.htmlEscape(path)%>'/>
		<input type="hidden" id="tokenizeCC" name='tokenizeCC' value='<%=tokenizeCC%>'/>
		<input type="hidden" id="paymentserver" name='paymentserver' value='<%=isServerAvailable%>'/>
		<% if (equals(modifyFlag,"Y")&& !"N".equals(resolveValue(path + "/@callcreatetokencc")) ) {%> 
            <% if (tokenizeCC && isServerAvailable) { %>
			        <input type="hidden" name='<%=HTMLEncode.htmlEscape(path)%>/@CreditCardNo'/>
			        <input type="hidden" name='<%=HTMLEncode.htmlEscape(path)%>/@DisplayCreditCardNo'/>
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
				<iframe style="padding:0px;margin:0px;border-width: 0px;border-style: none; border-style: hidden; border-collapse: collapse;font-size: 11;font-family: Tahoma;height: 18px;width:16em;" frameborder="0"  SCROLLING="NO" id="creditcardframe" name="creditcardframe" src="<%=framesrc%>" onload="readResults('creditcardframe', true);" doSSDCSSave="N"></iframe>
            <% } else { %>
        <input <%=yfsGetTextOptions(path + "/@CreditCardNo","xml:/PaymentMethod/@DisplayCreditCardNo", "xml:/Order/AllowedModifications")%>>
            <% } %>
		<% } else { %>
		<input class="protectedinput" contenteditable="false" <%=getTextOptions( "xml:/PaymentMethod/@CreditCardNo", "xml:/PaymentMethod/@CreditCardNo")%>/>
		<% } %>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@CreditCardExpDate","xml:/PaymentMethod/@CreditCardExpDate", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Credit_Card_Type</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@CreditCardType","xml:/PaymentMethod/@CreditCardType", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>
<tr>
    <td class="detaillabel" ><yfc:i18n>Name_On_Card</yfc:i18n></td>
    <td>
        <input <%=yfsGetTextOptions(path + "/@CreditCardName","xml:/PaymentMethod/@CreditCardName", "xml:/Order/AllowedModifications")%>>
    </td>
</tr>

<% } %>

