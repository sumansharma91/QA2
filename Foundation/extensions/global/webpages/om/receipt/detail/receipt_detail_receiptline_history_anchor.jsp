<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/om/receipt/detail/receipt_detail_linehistory_include.jspf" %>

<%
    //Get the orig receipt line key that has been passed from the previous screen.
    //This will be the starting point for this screen.
    String lineKey = getValue("ReceiptHistory", "xml:/ReceiptHistory/@ReceiptLineKey");
    
    //Now, stamp the entire receipt with those lines that truly form the history record for the passed line.
    stampLineAsRequired(lineKey, 0);
    
    YFCElement receiptLines = getReceiptLines();
    
    //Sort the lines based on the ancestory level, such that the most recent one is first.
    
    if (receiptLines != null) {
        String [] attrs = {"UIAncestorLevel"};
        receiptLines.sortChildren(attrs);
    }
    
%>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>

