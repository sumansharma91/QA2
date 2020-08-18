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
    <td>
        <yfc:makeXMLInput name="orderLineKey">
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
            <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("orderLineKey")%>' name="OrderLineEntityKey"/>
    </td>
</tr>
<tr>
    <td colspan="3">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td height="100%" width="50%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
    <td height="100%" width="25%" addressip="true" >
        <%outputPath = "xml:/Order/OrderLines/OrderLine/PersonInfoShipTo";
        %>
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I05"/>
            <jsp:param name="Path" value="xml:/OrderLine/PersonInfoShipTo"/>
            <jsp:param name="OutputPath" value='<%=outputPath%>'/>
            <jsp:param name="DataXML" value="OrderLine"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/OrderLine/AllowedModifications")%>'/>
            <jsp:param name="ShowAnswerSetOptions" value='<%=resolveValue("xml:/QuestionType/@AnswerSetFound")%>'/>
            <jsp:param name="AnswerSetOptionsBinding" value="xml:/Order/OrderLines/OrderLine/@RetainAnswerSets"/>
        </jsp:include>
    </td>
     <td height="100%" width="25%" addressip="true" >
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
<tr>
    <td colspan="3">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
        </jsp:include>
    </td>
</tr>
</table>
