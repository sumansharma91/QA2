<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/om/shipment/detail/shipment_detail_include.jspf" %>


<%

setHoldFuncFlags((YFCElement) request.getAttribute("Shipment"), isTrue("xml:/Rules/@RuleSetValue"));

YFCElement shipElem = (YFCElement) request.getAttribute("Shipment");
String useholdvalue = shipElem.getAttribute("UseNewHoldFunctionality");
//Here, take the output of findReceipt API for open receipts to 
//set the visible binding for start receipt &  receive options.
double isOpen = getNumericValue("xml:/Receipts/@TotalNumberOfRecords");

YFCElement elemShip = (YFCElement) request.getAttribute("Shipment");
YFCElement elem = (YFCElement) request.getAttribute("Receipts");

if(elemShip != null){
	if (elemShip.getAttribute("ShipmentConfirmUpdatesDone").equals("Y")) {
		elemShip.setAttribute("ConfirmFlag", false);
	}
	else {
		elemShip.setAttribute("ConfirmFlag", true);
	}
	elemShip.setAttribute("WMSNode", false);
	elemShip.setAttribute("NonWMSNode", true);
	
	if (equals("Y",resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime"))&&(!isVoid(resolveValue("xml:/Shipment/@ShipNode")))) {
		elemShip.setAttribute("WMSNode", true);
		elemShip.setAttribute("NonWMSNode", false);
	}
}
if(elem != null){
	elem.setAttribute("ReceiveFlag", false);
	elem.setAttribute("StartReceiptFlag", false); 
	if(!(equals("1100", elemShip.getAttribute("Status")))){
		if (equals("Y",resolveValue("xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime")) && (!isVoid(resolveValue("xml:/Shipment/@ReceivingNode")))) {
			elem.setAttribute("HSDEReceiveFlag", true);
		}
		elem.setAttribute("StartReceiptFlag", true); 
		elem.setAttribute("ReceiveFlag", true);
		if ( isOpen > 0) {
			elem.setAttribute("StartReceiptFlag", false);
			elem.setAttribute("ReceiveFlag", true);
		}else{
			elem.setAttribute("StartReceiptFlag", true);
		}
	}
}
%>
<%	 double iBlankTemplates = 8;
		if (!isVoid(resolveValue("xml:/Shipment/@OrderHeaderKey"))) { %>
			<yfc:callAPI apiID='AP1'/>
<%		if (0 != getNumericValue("xml:Order:/Order/OrderLines/@TotalNumberOfRecords")) {
			iBlankTemplates = getNumericValue("xml:Order:/Order/OrderLines/@TotalNumberOfRecords");
	}
}	%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td>
        <yfc:makeXMLInput name="shipmentKey">
            <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey"/>
        </yfc:makeXMLInput>
        <input type="hidden" value='<%=getParameter("shipmentKey")%>' name="ShipmentEntityKey"/>
        <input type="hidden" <%=getTextOptions("xml:/Shipment/@ShipmentKey")%>/>
    </td>
</tr>
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
			<jsp:param name="ModifyView" value="true"/>
		    <jsp:param name="getRequestDOM" value="Y"/>
		    <jsp:param name="RootNodeName" value="Shipment"/>
			<jsp:param name="NoOfBlankTemplates" value="<%=String.valueOf(iBlankTemplates)%>"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td width="33%" height="100%" addressip="true">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I07"/>
            <jsp:param name="Path" value="xml:/Shipment/ToAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ToAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>  

    <td width="33%" height="100%" addressip="true">
	    <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I02"/>
            <jsp:param name="Path" value="xml:/Shipment/FromAddress"/>
            <jsp:param name="DataXML" value="Shipment"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("FromAddress", "xml:/Shipment/AllowedModifications")%>'/>
        </jsp:include>
    </td>
    <td width="34%" height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
        </jsp:include>
    </td>
</tr>
<%	
	String sInnerPanel = null;
	if(equals("DEL", resolveValue("xml:/Shipment/@DeliveryMethod") ) ) 	{
		sInnerPanel = "I08";
	}
	else {
		sInnerPanel = "I05";
	}
%>

<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="<%=sInnerPanel%>" />
        </jsp:include>
    </td>
	<td  height="100%" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
</tr>
<tr>
    <td colspan="3" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
            <jsp:param name="ApplicationCode" value="omr"/>
		    <jsp:param name="RootNodeName" value="Shipment"/>
		    <jsp:param name="ChildLoopXMLSecondaryKeyName" value="ShipmentSubLineNo"/>
			<jsp:param name="NoOfBlankTemplates" value="<%=String.valueOf(iBlankTemplates)%>"/>
        </jsp:include>
    </td>
</tr>
</table>

