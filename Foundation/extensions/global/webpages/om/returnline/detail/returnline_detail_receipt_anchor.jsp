<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%
YFCElement receiptLines = getElement("ReceiptLineList");

request.setAttribute("ReceiptLines", receiptLines);
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>

<%//This JSP will actually group all the receipt lines by transaction and include the inner panel multiple times for each transaction. %>

<jsp:include page="/om/receipt/detail/receipt_detail_receiptlines_anchor.jsp" flush="true"/>
</table>
