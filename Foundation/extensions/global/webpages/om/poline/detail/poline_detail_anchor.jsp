<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% setHistoryFlags((YFCElement) request.getAttribute("OrderLine")); %>
<yfc:callAPI apiID='AP1'/> <% /* DerivedOrderLineList */ %>
<yfc:callAPI apiID='AP2'/> <% /* ChainedOrderLineList */ %>

<%
    String outputPath = "";
    prepareOrderLineElement((YFCElement) request.getAttribute("OrderLine"),(YFCElement) request.getAttribute("DerivedOrderLineList"),(YFCElement) request.getAttribute("ChainedOrderLineList"));
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="3">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="3">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td height="100%" width="33%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>

        </jsp:include>
    </td>
    <td height="100%" width="33%" addressip="true" >
        <%outputPath = "xml:/Order/OrderLines/OrderLine/PersonInfoShipTo";
        %>
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
            <jsp:param name="Path" value="xml:/OrderLine/PersonInfoShipTo"/>
            <jsp:param name="OutputPath" value='<%=outputPath%>'/>
            <jsp:param name="DataXML" value="OrderLine"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/OrderLine/AllowedModifications")%>'/>
        </jsp:include>
    </td>
     <td height="100%" width="33%" addressip="true" >
        <%outputPath = "xml:/Order/OrderLines/OrderLine/PersonInfoMarkFor";
        %>
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
            <jsp:param name="Path" value="xml:/OrderLine/PersonInfoMarkFor"/>
            <jsp:param name="OutputPath" value='<%=outputPath%>'/>
            <jsp:param name="DataXML" value="OrderLine"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("MarkForAddress", "xml:/OrderLine/AllowedModifications")%>'/>
        </jsp:include>
    </td>
</tr>
</table>
