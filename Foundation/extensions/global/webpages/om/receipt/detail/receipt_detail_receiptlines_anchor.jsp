<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<%
YFCElement receiptLines = (YFCElement) request.getAttribute("ReceiptLines");
Map tranMap = new HashMap();
YFCElement receiptLine = null;
String tranid = null;
double qty = 0;

if (receiptLines != null) {
    for (Iterator i = receiptLines.getChildren(); i.hasNext();){
        receiptLine = (YFCElement) i.next();
        tranid = receiptLine.getAttribute("TransactionId");
        qty = receiptLine.getDoubleAttribute("Quantity");
        if ((!isVoid(tranid)) && (qty > 0)) {
            if (!tranMap.containsKey(tranid)) {
                tranMap.put(tranid, receiptLine.getAttribute("TransactionName"));
            }
        }
    }
}
%>

<%
//For each transaction id, do a loop on receipt lines, showing records only for that transaction id at a time.
String tranName = null;
tranid = null;
for (Iterator i = tranMap.keySet().iterator(); i.hasNext();) {
    tranid = (String) i.next();
    tranName = (String) tranMap.get(tranid);
    if (isVoid(tranName)) {
        tranName = tranid;
    }
    request.setAttribute("TransactionId", tranid);
    
    tranName = getI18N("Lines_For_Transaction_" + tranid);
%>

<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Title" value="<%=tranName%>" />
        </jsp:include>
    </td>
</tr>
<%}%>
