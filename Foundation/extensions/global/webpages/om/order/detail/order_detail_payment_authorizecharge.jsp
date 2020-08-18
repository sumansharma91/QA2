<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/chargeutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<table cellspacing="0" cellpadding="0" border="0" width="100%" >
<tr>
    <td>        
        <input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:OrderDetail:/Order/@DraftOrderFlag")%>'/>
        <input type="hidden" <%=getTextOptions("xml:/RecordExternalCharges/PaymentMethod/@PaymentKey","xml:/Order/PaymentMethods/PaymentMethod/@PaymentKey")%>/>
    </td>
</tr>
<tr>
    <td valign="top">
        <table class="view" width="100%">            
            <tr>
                <td class="detaillabel" ><yfc:i18n>Authorization_ID</yfc:i18n></td>
                <td>
                    <input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthorizationID","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthorizationID","xml:OrderDetail:/Order/AllowedModifications")%>>
                </td>
            </tr>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Code</yfc:i18n></td>
                <td>
                    <input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthCode","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthCode","xml:OrderDetail:/Order/AllowedModifications")%>>
                </td>
            </tr>
            <tr >
                <td class="detaillabel" ><yfc:i18n>Expiration_Date</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthorizationExpirationDate","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthorizationExpirationDate","xml:OrderDetail:/Order/AllowedModifications")%> />
                    <img class="lookupicon" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@AuthorizationExpirationDate", "xml:OrderDetail:/Order/AllowedModifications") %> />
                </td>
            </tr>            
            <tr>
                <td class="detaillabel" ><yfc:i18n>Processed_Amount</yfc:i18n></td>
                <td>
                    <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> 
                    <input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@ProcessedAmount","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@ProcessedAmount", "xml:OrderDetail:/Order/AllowedModifications")%>>
                    <%=curr0[1]%>
                </td>
            </tr>
            <tr>
                <td class="detaillabel" ><yfc:i18n>Requested_Amount</yfc:i18n></td>
                <td>
                    <% String[] curr1 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr1[0]%> 
                    <input <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@RequestAmount","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@RequestAmount", "xml:OrderDetail:/Order/AllowedModifications")%>>
                    <%=curr1[1]%>
                </td>
            </tr>
            <tr >
                <td class="detaillabel" ><yfc:i18n>Collection_Date</yfc:i18n></td>
                <td>
                    <input type="text" <%=yfsGetTextOptions("xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@CollectionDate","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@CollectionDate","xml:OrderDetail:/Order/AllowedModifications")%> />
                    <img class="lookupicon" onclick="invokeCalendar(this);return false" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar","xml:/RecordExternalCharges/PaymentMethod/PaymentDetailsList/PaymentDetails/@CollectionDate", "xml:OrderDetail:/Order/AllowedModifications") %> />               
                </td>
            </tr>
        </table>
    <td>
</tr>
</table>
