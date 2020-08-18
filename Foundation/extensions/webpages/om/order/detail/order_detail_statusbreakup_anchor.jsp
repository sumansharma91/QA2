<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<table class="anchor">
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true">
                <jsp:param name="CurrentInnerPanelID" value="I02"/>
            </jsp:include>
        </td>
    </tr>
	<%	if (isTrue("xml:/Order/@HasProductLines") )	{	%>
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true">
                <jsp:param name="CurrentInnerPanelID" value="I01"/>
            </jsp:include>
        </td>
    </tr>
	<%	}
	if (isTrue("xml:/Order/@HasServiceLines") )	{	%>
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true">
                <jsp:param name="CurrentInnerPanelID" value="I03"/>
            </jsp:include>
        </td>
    </tr>
	<%  }
	if (isTrue("xml:/Order/@HasDeliveryLines") )	{	%>
    <tr>
        <td>
            <jsp:include page="/yfc/innerpanel.jsp" flush="true">
                <jsp:param name="CurrentInnerPanelID" value="I04"/>
            </jsp:include>
        </td>
    </tr>
<% } %>
</table>
