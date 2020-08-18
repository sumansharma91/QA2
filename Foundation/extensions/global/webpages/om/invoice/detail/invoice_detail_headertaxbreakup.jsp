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
String chargeCategory="";
String chargeName="";
String chargeAmount="";
%>

<table class="table" cellpadding="0" cellspacing="0" width="100%" editable="false">
<thead>
    <tr>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Tax_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Tax_Percentage</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Tax_Amount</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/HeaderTaxes/@HeaderTax" id="HeaderTax">
    <tr>
        <td class="tablecolumn">
			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/HeaderTax/@ChargeCategory", true)%>
		</td>

        <td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/HeaderTax/@ChargeCategory"), resolveValue("xml:/HeaderTax/@ChargeName"), 
				(YFCElement)request.getAttribute("ChargeNameList") )%>

            <% if (equals(isChargeBillable(getValue("HeaderTax","xml:/HeaderTax/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %> 
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
       </td>

        <%chargeCategory=getValue("HeaderTax","xml:/HeaderTax/@ChargeCategory");%>
        <%chargeName=getValue("HeaderTax","xml:/HeaderTax/@ChargeName");%>
        <yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/HeaderCharges/@HeaderCharge" id="HeaderCharge">
            <%if(equals(chargeCategory,getValue("HeaderCharge","xml:/HeaderCharge/@ChargeCategory")) && equals(chargeName,getValue("HeaderCharge","xml:/HeaderCharge/@ChargeName"))) {
                chargeAmount=getValue("HeaderCharge","xml:/HeaderCharge/@ChargeAmount");
                break;
            } else {
                chargeAmount=getLocalizedStringFromComputedDouble(getLocale(),0.00);
            }%>                         
        </yfc:loopXML>

        <td class="numerictablecolumn" sortValue="<%=getNumericValue(chargeAmount)%>">
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>&nbsp;<%=chargeAmount%>&nbsp;<%=curr0[1]%>
        </td>
        <td class="tablecolumn"><%=getComboText("xml:TaxNameList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/HeaderTax/@TaxName",true)%></td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:HeaderTax:/HeaderTax/@TaxPercentage")%>">
            <yfc:getXMLValue binding="xml:/HeaderTax/@TaxPercentage"/>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:HeaderTax:/HeaderTax/@Tax")%>">
            <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%>&nbsp;<yfc:getXMLValue binding="xml:/HeaderTax/@Tax"/>&nbsp;<%=curr1[1]%>
        </td>
    </tr>
    </yfc:loopXML>
</tbody>
</table>
