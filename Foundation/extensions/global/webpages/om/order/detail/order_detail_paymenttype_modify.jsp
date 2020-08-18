<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%@ include file="/console/jsp/paymenttokenizerutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<%
boolean tokenizeCC = isCreditCardNoTokenized();
boolean tokenizeSVC = isStoreValueCardNoTokenized();
%>
<% 
String PaymentMethodCounter = getParameter("PaymentMethodCounter");  
String paymentType = getValue("PaymentMethod", "xml:/PaymentMethod/@PaymentType");
String paymentTypeGroup = "";
boolean disableUnlimitedCharges = false;
if(!isVoid(paymentType)){
	YFCElement paymentTypeListElem = (YFCElement)request.getAttribute("PaymentTypeList");
	for (Iterator i = paymentTypeListElem.getChildren(); i.hasNext();) {
		YFCElement childElem = (YFCElement) i.next();
		if(equals(paymentType, childElem.getAttribute("PaymentType"))){
			paymentTypeGroup = childElem.getAttribute("PaymentTypeGroup");
			disableUnlimitedCharges = childElem.getBooleanAttribute("ChargeUpToAvailable");
			break;
		}
	}
}
String incomplete = getValue("PaymentMethod", "xml:/PaymentMethod/@IncompletePaymentType");
String creditCardEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CreditCardRule"));
String creditCardAddnlEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CreditCardAddnlRule"));
String storedValueCardEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("StoredValueCardRule"));
String customerAccountEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("CustomerAccountRule"));
String otherPaymentTypeEncrypted = isAttributeEncrypted((YFCElement) request.getAttribute("OtherPaymentTypeRule"));

String path = "xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter;
String viewType = "view";
if(equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"Y") ||
   equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"B"))
   viewType = "disabledview";
%>

<table class="<%=viewType%>" width="100%">
<tr>
    <td valign="top">
		<input type="hidden" id="callcreatetokencc" name='<%=path%>/@callcreatetokencc' />
		<input type="hidden" id="callcreatetokensvc" name='<%=path%>/@callcreatetokensvc' />
        <table class="view" width="100%">
            <tr>
                <td class="detaillabel" ><yfc:i18n>Payment_Type</yfc:i18n></td>
                <td class="protectedtext">				<%=getComboText("xml:/PaymentTypeList/@PaymentType","PaymentTypeDescription","PaymentType","xml:/PaymentMethod/@PaymentType",true)%>
				</td>
             </tr>
             <% if (equals(paymentTypeGroup,"CREDIT_CARD")) { 
				 String servererror = validateTokenizerURL("CREDIT_CARD", tokenizeCC, "CREDIT_CARD");%>
                <tbody <%=getCurrentStyle("CREDIT_CARD", "Order", path + "/@PaymentType", "CREDIT_CARD")%> >
                <jsp:include page="/om/order/detail/order_detail_paymenttype_creditcard.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="CreditCardEncrypted" value='<%=creditCardEncrypted%>'/>
                    <jsp:param name="CreditCardAddnlEncrypted" value='<%=creditCardAddnlEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
					<jsp:param name="PaymentTypeBinding" value="xml:/PaymentMethod/@PaymentType"/>
                     <jsp:param name="ModifyFlag" value='Y'/>
					 <jsp:param name="paymentservererror" value='<%=servererror%>'/>
               </jsp:include>
                </tbody>
			<% } else if (equals(paymentTypeGroup,"CUSTOMER_ACCOUNT")) { %> 
                <tbody  <%=getCurrentStyle("CUSTOMER_ACCOUNT", "Order", path + "/@PaymentType","CUSTOMER_ACCOUNT")%> >
                <jsp:include page="/om/order/detail/order_detail_paymenttype_account.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="CustomerAccountEncrypted" value='<%=customerAccountEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
                </jsp:include>
                </tbody>
            <% } else if (equals(paymentTypeGroup,"CHECK")) { %> 
                <tbody <%=getCurrentStyle("CHECK", "Order", path + "/@PaymentType","CHECK")%> >
                <jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="OtherPaymentTypeEncrypted" value='<%=otherPaymentTypeEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
                </jsp:include>
                </tbody>
			<% } else if (equals(paymentTypeGroup,"STORED_VALUE_CARD")) { 
				 String servererror = validateTokenizerURL("STORED_VALUE_CARD", tokenizeSVC, "STORED_VALUE_CARD");%>
				<tbody <%=getCurrentStyle("STORED_VALUE_CARD", "Order", path + "/@PaymentType","STORED_VALUE_CARD")%> >
					<jsp:include page="/om/order/detail/order_detail_paymenttype_svc.jsp" flush="true">
						<jsp:param name="Incomplete" value='<%=incomplete%>'/>
						<jsp:param name="StoredValueCardEncrypted" value='<%=storedValueCardEncrypted%>'/>
						<jsp:param name="Path" value='<%=path%>'/>
						<jsp:param name="PaymentTypeBinding" value="xml:/PaymentMethod/@PaymentType"/>
						<jsp:param name="ModifyFlag" value='Y'/>
						<jsp:param name="paymentservererror" value='<%=servererror%>'/>
					</jsp:include>
				</tbody>
            <% } else  { %> 
                <tbody  <%=getCurrentStyle("OTHER", "Order", path + "/@PaymentType",paymentTypeGroup)%>>
                <jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="OtherPaymentTypeEncrypted" value='<%=otherPaymentTypeEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
                </jsp:include>
                </tbody>
            <% } %>
        </table>
    </td>
    <td valign="top">
        <table class="view" width="100%">
            <tr>
                <td class="detaillabel" ><yfc:i18n>Charge_Sequence</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@ChargeSequence","xml:/PaymentMethod/@ChargeSequence","xml:/Order/AllowedModifications")%>/>
                </td>
            </tr>   
            <tr>
                <td class="detaillabel" ><yfc:i18n>Unlimited_Charges</yfc:i18n></td>
                <td>
					<% if (disableUnlimitedCharges) { %>
	                    <input class="checkbox" type="checkbox" disabled="true"  <%=getCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@UnlimitedCharges","xml:/PaymentMethod/@UnlimitedCharges","Y")%>/>
					<%} else { %>
	                    <input class="checkbox" type="checkbox" <%=yfsGetCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@UnlimitedCharges","xml:/PaymentMethod/@UnlimitedCharges","Y","xml:/Order/AllowedModifications")%>/>
					<% } %>
                </td>
            </tr>     
            <tr>
                <td class="detaillabel" ><yfc:i18n>Max_Charge_Limit</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@MaxChargeLimit","xml:/PaymentMethod/@MaxChargeLimit","xml:/Order/AllowedModifications")%>/>
                </td>
            </tr>
        </table>
    </td>
    <td valign="top">
        <table class="view" width="100%">
			<%
						double dAmount = getNumericValue("xml:/PaymentMethod/@TotalCharged") - getNumericValue("xml:/PaymentMethod/@TotalRefundedAmount");
			%>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Collected_Amount</yfc:i18n></td>
                <td class="protectednumber">
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> 
						<%=YFCI18NUtils.getFormattedDouble(getLocale(),dAmount)%>
                          <%=curr0[1]%>
                </td>
            </tr>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Refunded_Amount</yfc:i18n></td>
                <td class="protectednumber">
                    <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%> 
                    <yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@TotalRefundedAmount"/>
                    <%=curr1[1]%>
                </td>
            </tr>
			<% if(!equals(getParameter("ShowAuthorized"),"N")) {%>
				<tr>
					<td class="detaillabel" ><yfc:i18n>Authorized_Amount</yfc:i18n></td>
					<td class="protectednumber">
						<% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%> 
						<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@TotalAuthorized"/>
						<%=curr2[1]%>
					</td>
				</tr>
			<%}%>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Awaiting_Collections</yfc:i18n></td>
                <td class="protectednumber">
                    <% String[] curr3 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr3[0]%> 
                    <yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CombinedCharges"/>
                    <%=curr3[1]%>
                </td>
            </tr>
			<% if(!equals(getParameter("ShowAuthorized"),"N")) {%>
				<tr>
					<td class="detaillabel" ><yfc:i18n>Awaiting_Authorizations</yfc:i18n></td>
					<td class="protectednumber">
						<% String[] curr4 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr4[0]%> 
						<yfc:getXMLValue name="PaymentMethod" binding="xml:/PaymentMethod/@CombinedAuthorizations"/>
						<%=curr4[1]%>
					</td>
				</tr>            
			<%}%>
        </table>

<%	//	address.jsp included to help 'Show Bill To Address' icon.  Modify_Address jsp requires that the address 
	// is present in parent window.. hence hidden address
	
	String personInfoElement = "xml:/Order/PersonInfoBillTo";
	String sDataXML = "Order";
	if(!isVoid(resolveValue("xml:/PaymentMethod/PersonInfoBillTo/@PersonInfoKey") ) )	{
		personInfoElement = "xml:/PaymentMethod/PersonInfoBillTo";
		sDataXML = "PaymentMethod";
	}
%>
		<jsp:include page="/yfsjspcommon/address.jsp" flush="true">
			<jsp:param name="Path" value="<%=personInfoElement%>"/>
			<jsp:param name="AllowedModValue" value='N'/>
			<jsp:param name="DataXML" value="<%=sDataXML%>"/>
			<jsp:param name="style" value='display:none'/>
		</jsp:include>
    </td>

</tr>
<tr>
	<td class="detaillabel" colspan="3" >
		<FIELDSET>
			 <LEGEND class="detaillabel"><yfc:i18n>Payment_Type_Status</yfc:i18n></LEGEND>
			  <table class="view" width="100%">
				<tr>
					<td>
						 <input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"Y")) {%> CHECKED <%}%>
						 <%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@SuspendAnyMoreCharges","","Y","xml:/Order/AllowedModifications")%>>
						<yfc:i18n>Suspended_For_Charge</yfc:i18n> 
					</td>
					<td>
						 <input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"B")) {%> CHECKED <%}%>
						 <%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + 	"/@SuspendAnyMoreCharges","","B","xml:/Order/AllowedModifications")%>>
						<yfc:i18n>Suspended_For_Charge_And_Refund</yfc:i18n> 
					</td>
					<td>
						 <input type="radio" class="radiobutton" <%if (equals(resolveValue("xml:/PaymentMethod/@SuspendAnyMoreCharges"),"N")) {%> CHECKED <%}%>
						 <%=yfsGetRadioOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@SuspendAnyMoreCharges","","N","xml:/Order/AllowedModifications")%>>
						 <yfc:i18n>Active</yfc:i18n> 
					</td>
				</tr>
			</table>
		 </FIELDSET>
	</td>
</tr>
</table>
