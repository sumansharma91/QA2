<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>


<table class="anchor" cellpadding="7px"  cellSpacing="0" >
<tr>
    <td colspan="2">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>

<%
	if(isVoid(resolveValue("xml:/ReturnOrder/@ReturnOrderHeaderKeyForExchange"))){
%>
<tr>
    <td height="100%" width="50%" addressip="true">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoShipTo"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/Order/AllowedModifications")%>'/>
        </jsp:include>
    </td>
    <td height="100%" width="50%" addressip="true">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
            <jsp:param name="Path" value="xml:/Order/PersonInfoBillTo"/>
            <jsp:param name="DataXML" value="Order"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("BillToAddress", "xml:/Order/AllowedModifications")%>'/>
        </jsp:include>
    </td>
</tr>
<%	} %>

</table>
	
