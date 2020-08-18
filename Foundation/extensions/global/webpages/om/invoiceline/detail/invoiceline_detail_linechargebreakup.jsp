<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>

<%
String orderLineKey = getValue("GetOrderInvoiceDetails","xml:/GetOrderInvoiceDetails/@OrderLineKey");
%>
<yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/LineDetails/@LineDetail" id="LineDetail">
<%
if(equals(orderLineKey,getValue("LineDetail","xml:/LineDetail/@OrderLineKey"))) {
    break;
}
%>
</yfc:loopXML> 

<table class="view" cellpadding="0" cellspacing="0" width="100%">
    <tr>
        <td class="detaillabel" ><yfc:i18n>Invoiced_Quantity</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/LineDetail/@PricingQty"/></td>
        <td class="detaillabel" ><yfc:i18n>Pricing_UOM</yfc:i18n></td>
        <td class="protectedtext"><yfc:getXMLValue binding="xml:/LineDetail/OrderLine/LinePriceInfo/@PricingUOM"/></td>
        <td class="detaillabel" ><yfc:i18n>Unit_Price</yfc:i18n></td>
        <td class="protectedtext"><% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<yfc:getXMLValue binding="xml:/LineDetail/@UnitPrice"/>&nbsp;<%=curr0[1]%></td>
        <td class="detaillabel" ><yfc:i18n>Extended_Price</yfc:i18n></td>
        <td class="protectedtext"><% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue binding="xml:/LineDetail/@ExtendedPrice"/>&nbsp;<%=curr1[1]%></td>
    </tr>
</table>
<table class="table" cellpadding="0" cellspacing="0" width="100%" editable="false">
<thead>
    <tr>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>	
    </tr>
</thead>
<tbody>
<yfc:loopXML name="LineDetail" binding="xml:/LineDetail/LineCharges/@LineCharge" id="LineCharge"> 
    <tr>
		<td class="tablecolumn">
			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/LineCharge/@ChargeCategory", true)%>
		</td>

        <td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/LineCharge/@ChargeCategory"), resolveValue("xml:/LineCharge/@ChargeName"), 
				(YFCElement)request.getAttribute("ChargeNameList") )%>

			<% if (equals(isChargeBillable(getValue("LineCharge","xml:/LineCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %> 
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
        </td>
		<%
			double dChargeAmt = getNumericValue("xml:LineCharge:/LineCharge/@ChargeAmount");
			if(equals("Y", isChargeCategoryDiscount(resolveValue("xml:/LineCharge/@ChargeCategory"), getElement("ChargeCategoryList") ) ) )	{
				dChargeAmt *= -1;
			}
		%>
        <td class="numerictablecolumn" sortValue="<%=dChargeAmt%>">
            <% String[] curr2 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr2[0]%>&nbsp;
            <%=getFormattedDouble(dChargeAmt)%>&nbsp;
            <%=curr2[1]%>
        </td>                                        
    </tr>                                                        
</yfc:loopXML>
</tbody>
</table>
