<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf" %>
<%@page import="com.yantra.yfc.dom.*" %>
<%@include file="/console/jsp/order.jspf" %>

<% setHistoryFlags( (YFCElement)request.getAttribute("OrderRelease") ); %>

<yfc:callAPI apiID="AP1"/> <!-- getShipmentListForOrder -->


<script language="javascript">
	function displayShipmentDetail() {
		var sVal = document.all("hiddenShipmentKeyInput");
		callPopupWithEntity('returnshipment',sVal.value, 'true', 'true');
	}
</script>

<%	YFCElement shipmentCreatedElem = (YFCElement) request.getAttribute("ShipmentCreated");
		
	if (shipmentCreatedElem != null) {
		String shipmentKey = shipmentCreatedElem.getAttribute("ShipmentKey");
		if (!isVoid(shipmentKey)) { 			
			YFCDocument shipmentDoc = YFCDocument.createDocument("Shipment");
			shipmentDoc.getDocumentElement().setAttribute("ShipmentKey",shipmentKey); %>
			<input type="hidden" name="hiddenShipmentKeyInput" value='<%=shipmentDoc.getDocumentElement().getString(false)%>' />
			<script>
				window.attachEvent("onload",displayShipmentDetail);				
			</script>			
		<%}
	}
%>

<%
//Here, take the output of receipt list API for open receipts to 
//set the visible binding for start receipt & close receipts options.
double isOpen = getNumericValue("xml:OpenReceiptList:/ReceiptList/@TotalNumberOfRecords");
YFCElement elem = (YFCElement) request.getAttribute("OpenReceiptList");
if(elem != null){
	if ( isOpen > 0) {
		elem.setAttribute("ShouldStart", false);
	}
	else {
		elem.setAttribute("ShouldStart", true);
	}
}
//Here, take the output of findReceipt API for open receipts to 
//set the visible binding for start receipt &  receive options.
isOpen = getNumericValue("xml:/Receipts/@TotalNumberOfRecords");

elem = (YFCElement) request.getAttribute("Receipts");
if(elem != null){
	if (getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime").equals("Y")) {
		elem.setAttribute("DcmIntegrationRealTime",true);
	} else {
		elem.setAttribute("DcmIntegrationRealTime",false);
	}
}

if(elem != null){
	elem.setAttribute("StartReceiptFlag", false);
	elem.setAttribute("ReceiveFlag", false);
	elem.setAttribute("ReceiveKitFlag", false);
	if ( isOpen > 0) {
		if (!getValue("ShipNode","xml:/ShipNodeList/ShipNode/@DcmIntegrationRealTime").equals("Y")) {
			elem.setAttribute("ReceiveFlag", true);
		}
		else {
			elem.setAttribute("ReceiveFlag", false);
		}
		elem.setAttribute("ReceiveKitFlag", true);
	}
	else {
		elem.setAttribute("StartReceiptFlag", true);
	}
}

	String additionalAttributesIP = null;
	if(equals("DEL", resolveValue("xml:/OrderRelease/@DeliveryMethod") ) ) 	{
		additionalAttributesIP = "I06";
	}
	else {
		additionalAttributesIP = "I02";
	}

    //set the displaybinding for the receive actions
    if(elem != null){
        boolean receiveFlag = elem.getBooleanAttribute("ReceiveFlag");
        boolean dcmIntegrationRealTime = elem.getBooleanAttribute("DcmIntegrationRealTime");

        YFCElement orderReleaseElem = (YFCElement)request.getAttribute("OrderRelease");
        boolean isNotHistory = orderReleaseElem.getBooleanAttribute("isNotHistory");

        String isReceiveEnabled = "N";
        if(receiveFlag && isNotHistory){
            isReceiveEnabled = "Y";
        }

        String isHsdeReceiveEnabled = "N";
        if(dcmIntegrationRealTime && isNotHistory){
            isHsdeReceiveEnabled = "Y";
        }
        
        elem.setAttribute("IsReceiveEnabled", isReceiveEnabled);
        elem.setAttribute("IsHsdeReceiveEnabled", isHsdeReceiveEnabled);
    }
%>

<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
        </jsp:include>
    </td>
</tr>
<tr>
	<td width="20%" valign="top" height="100%">
		<table height="100%">
			<tr>
				<td width="100%" height="50%" addressip="true" valign="top">
				<jsp:include page="/yfc/innerpanel.jsp" flush="true">
					<jsp:param name="CurrentInnerPanelID" value="I03"/>
					<jsp:param name="Path" value="xml:/OrderRelease/PersonInfoShipTo"/>
					<jsp:param name="DataXML" value="OrderRelease"/>
					<jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/OrderRelease/AllowedModifications")%>'/>
				</jsp:include>
			</td>
		</tr>
		<tr>
			<td width="100%" valign="top" height="50%" >
				<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
					<jsp:param name="CurrentInnerPanelID" value="<%=additionalAttributesIP%>"/>
				</jsp:include>
			</td>
		</tr>
		</table>
	</td>
    <td width="80%" valign="top" height="100%">
		<table height="100%">
			<tr>
				<td>
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I05"/>
						<jsp:param name="allowedBinding" value='xml:/OrderRelease/AllowedModifications'/>
						<jsp:param name="getBinding" value='xml:/OrderRelease'/>
						<jsp:param name="saveBinding" value='xml:/OrderRelease'/>
					</jsp:include>
				</td>
			</tr>
		</table>
    </td>
</tr>
<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I04"/>
        </jsp:include>
    </td>
</tr>
</table>
