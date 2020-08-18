<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<table class="anchor" width="100%" cellpadding="7px">
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="IsInspection" value="Y"/>
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
            <jsp:param name="IPHeight" value="150"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td>
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
            <jsp:param name="IsInspection" value="Y"/>
        </jsp:include>
    </td>
</tr>
</table>
