<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>

<table class="table" cellpadding="0" cellspacing="0" width="100%" editable="false">
<thead>
	<tr>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Category</yfc:i18n></td>
        <td class="tablecolumnheader"><yfc:i18n>Charge_Name</yfc:i18n></td>
        <td class="numerictablecolumnheader"><yfc:i18n>Charge_Amount</yfc:i18n></td>
    </tr>
</thead>
<tbody>
<yfc:loopXML name="InvoiceDetail" binding="xml:/InvoiceDetail/InvoiceHeader/HeaderCharges/@HeaderCharge" id="HeaderCharge">
    <tr>
		<td>
			<%=getComboText("xml:/ChargeCategoryList/@ChargeCategory", "Description", "ChargeCategory", "xml:/HeaderCharge/@ChargeCategory", true)%>
		</td>

		<td class="tablecolumn">
			<%=displayChargeNameDesc(resolveValue("xml:/HeaderCharge/@ChargeCategory"), resolveValue("xml:/HeaderCharge/@ChargeName"), (YFCElement)request.getAttribute("ChargeNameList") )%>

            <% if (equals(isChargeBillable(getValue("HeaderCharge","xml:/HeaderCharge/@ChargeCategory"), (YFCElement) request.getAttribute("ChargeCategoryList")),"N")) { %> 
                <img class="columnicon" <%=getImageOptions(YFSUIBackendConsts.INFO_CHARGE, "Informational_Charge")%>>
            <%}%>
        </td>
        <td class="numerictablecolumn" sortValue="<%=getNumericValue("xml:HeaderCharge:/HeaderCharge/@ChargeAmount")%>">
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%>
            &nbsp;<yfc:getXMLValue binding="xml:/HeaderCharge/@ChargeAmount"/>
            &nbsp;<%=curr0[1]%>
        </td>
    </tr>
</yfc:loopXML>
</tbody>
</table>
