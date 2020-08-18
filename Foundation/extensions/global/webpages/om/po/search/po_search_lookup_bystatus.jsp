<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/om/po/search/po_search_bystatus.jsp" %>
<%if(isShipNodeUser()) {%>
<table class="view">
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Receiving_Node</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td class="searchcriteriacell">
            <input type="text" class="protectedinput" disabled <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@ReceivingNode","xml:CurrentUser:/User/@Node")%>/>
		<input type="hidden" name="xml:/Order/OrderLine/@ReceivingNode" value='<%=resolveValue("xml:CurrentUser:/User/@Node")%>'/>
        </td>
    </tr>
</table>
<%}%>
