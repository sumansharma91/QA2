<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>
<%	// cr 40099
	YFCElement eOrderLine = (YFCElement)request.getAttribute("OrderLine");
	if(eOrderLine == null)
		return;

    prepareOrderElement(eOrderLine.getChildElement("Order"));
%>
<% setHistoryFlags( (YFCElement)request.getAttribute("OrderLine") ); %>
<%
	YFCElement orderLineElem = (YFCElement) request.getAttribute("OrderLine");
	if (orderLineElem!=null) {
		boolean	isHistory = orderLineElem.getBooleanAttribute("isHistory");
		String currentWorkOrderKey = orderLineElem.getAttribute("CurrentWorkOrderKey");
		
		if(!isHistory && !isVoid(currentWorkOrderKey)) {
			orderLineElem.setAttribute("ShowWorkOrder",true);
		} else {
			orderLineElem.setAttribute("ShowWorkOrder",false);
		}
	}
%>

<table class="anchor" cellSpacing="0" cellpadding="7px">
    <tr>
        <td colspan="3">
            <% // Create a key that is used for the work order icon %>
            <yfc:makeXMLInput name="workOrderKey">
                <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/OrderLine/@CurrentWorkOrderKey"/>
            </yfc:makeXMLInput>
            <input type="hidden" name="workOrderKey" value='<%=getParameter("workOrderKey")%>' />
            <% // Create a key that is used for the address popups %>
            <yfc:makeXMLInput name="singleOrderLineKey">
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey"/>
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey"/>
            </yfc:makeXMLInput>
            <input type="hidden" value='<%=getParameter("singleOrderLineKey")%>' name="SingleOrderLineEntityKey"/>
            <jsp:include page="/yfc/innerpanel.jsp" flush='true' >
                <jsp:param name="CurrentInnerPanelID" value="I01" />
                <jsp:param name="ModifyView" value="true" />
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td height="100%" width="25%">
            <jsp:include page="/yfc/innerpanel.jsp" flush='true' >
                <jsp:param name="CurrentInnerPanelID" value="I02" />
            </jsp:include>
        </td>
        <td height="100%" width="50%">
            <jsp:include page="/yfc/innerpanel.jsp" flush='true' >
                <jsp:param name="CurrentInnerPanelID" value="I03" />
            </jsp:include>
        </td>
        <td height="100%" width="25%" addressip="true">
            <%String outputPath = "xml:/Order/OrderLines/OrderLine/PersonInfoShipTo";%>
            <jsp:include page="/yfc/innerpanel.jsp" flush='true' >
                <jsp:param name="CurrentInnerPanelID" value="I04" />
                <jsp:param name="Path" value="xml:/OrderLine/PersonInfoShipTo" />
                <jsp:param name="OutputPath" value='<%=outputPath%>'/>
                <jsp:param name="DataXML" value="OrderLine" />
                <jsp:param name='AllowedModValue' value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/OrderLine/AllowedModifications")%>' />
                <jsp:param name="ShowAnswerSetOptions" value='<%=resolveValue("xml:/QuestionType/@AnswerSetFound")%>'/>
                <jsp:param name="AnswerSetOptionsBinding" value="xml:/Order/OrderLines/OrderLine/@RetainAnswerSets"/>
            </jsp:include>
        </td>
    </tr>
    <tr>
        <td colspan="3">
            <jsp:include page="/yfc/innerpanel.jsp" flush='true' >
                <jsp:param name="CurrentInnerPanelID" value="I05" />
            </jsp:include>
        </td>
    </tr>
</table>
