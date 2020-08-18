<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table cellSpacing=0 class="anchor" cellpadding="7px">
<tr>
    <td >
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<% if (!equals(getValue("getReceiptLineListToBeInspected","xml:/ReceiptLineList/@TotalNumberOfRecords"),"0")) //add following inner panel only if there are receipt lines that still need to be inspected in the receipt
	{%>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
<%	}%>
<% if (!equals(getValue("getReceiptLineListInspected","xml:/ReceiptLineList/@TotalNumberOfRecords"),"0")) //add following inner panel only if there are receipt lines already completely inspected in the receipt
	{%>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<%	}%>
</table>
