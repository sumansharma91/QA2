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

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

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
%>

<table class="view" width="100%">
<tr>
    <td valign="top">
        <table class="view" width="100%">
            <tr>
                <td>
                    <yfc:makeXMLInput name="paymentKey">
                        <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey"/>
                        <yfc:makeXMLKey binding="xml:/Order/PaymentMethods/PaymentMethod/@PaymentKey" value="xml:/PaymentMethod/@PaymentKey"/>
                    </yfc:makeXMLInput>
                    <td class="checkboxcolumn" >
                    <input type="hidden" value='<%=getParameter("paymentKey")%>' name="<%=getDefaultEntityKeyName()%>"/>
                    <input type="hidden" value='N' yName="AllowedModValue"/>
                    <input type="hidden" <%=getTextOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@PaymentKey","xml:/PaymentMethod/@PaymentKey")%>/>
                </td>
            </tr>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Payment_Type</yfc:i18n></td>
                <td class="protectedtext">	<%=getComboText("xml:/PaymentTypeList/@PaymentType","PaymentTypeDescription","PaymentType","xml:/PaymentMethod/@PaymentType",true)%>
				</td>
             </tr>
             <% if (equals(paymentTypeGroup,"CREDIT_CARD")) { %>
                <tbody>
                <jsp:include page="/om/order/detail/order_detail_paymenttype_creditcard.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="CreditCardEncrypted" value='<%=creditCardEncrypted%>'/>
                    <jsp:param name="CreditCardAddnlEncrypted" value='<%=creditCardAddnlEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
					<jsp:param name="PaymentTypeBinding" value="xml:/PaymentMethod/@PaymentType"/>
					<jsp:param name="PaymentMethodKey" value="xml:/PaymentMethod/@PaymentKey"/>
                    <jsp:param name="ModifyFlag" value='N'/>
                </jsp:include>
                </tbody>
            <% } else if (equals(paymentTypeGroup,"CUSTOMER_ACCOUNT")) { %> 
                <tbody>
                <jsp:include page="/om/order/detail/order_detail_paymenttype_account.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="CustomerAccountEncrypted" value='<%=customerAccountEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
                </jsp:include>
                </tbody>
            <% } else if (equals(paymentTypeGroup,"CHECK")) { %> 
                <tbody>
                <jsp:include page="/om/order/detail/order_detail_paymenttype_check.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
                    <jsp:param name="OtherPaymentTypeEncrypted" value='<%=otherPaymentTypeEncrypted%>'/>
                    <jsp:param name="Path" value='<%=path%>'/>
                </jsp:include>
                </tbody>
			<% } else if (equals(paymentTypeGroup,"STORED_VALUE_CARD")) { %> 
                <tbody>
                <jsp:include page="/om/order/detail/order_detail_paymenttype_svc.jsp" flush="true">
                    <jsp:param name="Incomplete" value='<%=incomplete%>'/>
						<jsp:param name="StoredValueCardEncrypted" value='<%=storedValueCardEncrypted%>'/>
						<jsp:param name="PaymentTypeBinding" value="xml:/PaymentMethod/@PaymentType"/>
						<jsp:param name="Path" value='<%=path%>'/>
						<jsp:param name="PaymentMethodKey" value="xml:/PaymentMethod/@PaymentKey"/>
						<jsp:param name="ModifyFlag" value='N'/>
                </jsp:include>
                </tbody>
            <% } else  { %> 
                <tbody>
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
		                <input class="checkbox" type="checkbox" disabled="true" <%=getCheckBoxOptions("xml:/Order/PaymentMethods/PaymentMethod_" + PaymentMethodCounter + "/@UnlimitedCharges","xml:/PaymentMethod/@UnlimitedCharges","Y")%>/>
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
</tr>
</table>
