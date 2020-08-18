<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<% if (!equals(getParameter("hidRefreshKey"),"Y")) { %>
	<yfc:callAPI apiID='AP2'/>
<% } else { %>
	<yfc:callAPI apiID='AP3'/>
<input type="hidden"  name="hidRefreshKey" value="Y"/>
<% } %>
<table class="anchor" cellpadding="7px" cellSpacing="0">
<% 
String NumberOfRecords =  resolveValue("xml:/Receipts/@TotalNumberOfRecords");
if (NumberOfRecords.equals("1")) { %>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="allowedBinding" value='xml:/OrderRelease/AllowedModifications'/>
            <jsp:param name="getBinding" value='xml:/OrderRelease'/>
            <jsp:param name="saveBinding" value='xml:/OrderRelease'/>
            <jsp:param name="IPHeight" value="130"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<% } else { %>
<input type="hidden"  name="hidRefreshKey" value="Y"/>
<tr>
    <td >
		<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
			<jsp:param name="CurrentInnerPanelID" value="I04"/>
		</jsp:include>
    </td>
</tr>
<% } %>
</table>
