<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view" width="100%">
    <tr>
        <td>
            <input type="hidden" name="xml:/RecordExternalCharges/@ModificationReasonCode"/>
            <input type="hidden" name="xml:/RecordExternalCharges/@ModificationReasonText"/>
            <input type="hidden" name="hiddenDraftOrderFlag" value='<%=getValue("Order", "xml:/Order/@DraftOrderFlag")%>'/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel" ><yfc:i18n>Amount</yfc:i18n></td>
        <td>
            <% String[] curr0 = getLocalizedCurrencySymbol( getValue("CurrencyList", "xml:/CurrencyList/Currency/@PrefixSymbol"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@Currency"), getValue("CurrencyList", "xml:/CurrencyList/Currency/@PostfixSymbol"));%><%=curr0[0]%> 
            <input type="text" onblur="updateAPIQty(this, 'xml:/RecordExternalCharges/Memo/@ChargeAmount');" <%=yfsGetTextOptions("xml:/Temp/@ChargeAmount","xml:/Order/AllowedModifications")%> 
            <%=curr0[1]%>
        </td>
	</tr>
	<tr>
	<td class="detaillabel" ><yfc:i18n>Reference</yfc:i18n></td>
        <td>
            <input <%=yfsGetTextOptions("xml:/RecordExternalCharges/Memo/@Reference1", "xml:/Order/AllowedModifications")%>>
        </td>
    </tr>
    <tr>
    <td>
        <input type="hidden" class="unprotectedinput" <%=getTextOptions("xml:/RecordExternalCharges/Memo/@ChargeAmount")%> />
    </td>
</tr>
</table>
