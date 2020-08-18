<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/console/jsp/order.jspf" %>

<%
YFCElement releaseElem = (YFCElement)request.getAttribute("OrderRelease");
setHistoryFlags( releaseElem );

/* Construct a value to use as a displaybinding for start receipt action*/
String dcmRealTime = resolveValue("xml:ShipNode:/ShipNodeList/ShipNode/@DcmIntegrationRealTime");
if(equals("Y",releaseElem.getAttribute("isNotHistory"))) {
    releaseElem.setAttribute("ShowStartReceipt",dcmRealTime);
}
%>

<yfc:callAPI apiID="AP1"/> <!-- getShipmentListForOrder -->

<script language="javascript">
	function displayShipmentDetail() {
		 var sVal = document.all("hiddenShipmentKeyInput");
		callPopupWithEntity('poshipment',sVal.value, 'true','true');
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
//Here, take the output of findReceipt API for open receipts to 
//set the visible binding for start receipt &  receive options.
double isOpen = getNumericValue("xml:ReceiptList:/ReceiptList/@TotalNumberOfRecords");
//System.out.println("TotalNumberOfRecords-->" + isOpen);
YFCElement elem = (YFCElement) request.getAttribute("ReceiptList");

if(elem != null){
	elem.setAttribute("StartReceiptFlag", false);
	elem.setAttribute("ReceiveFlag", false);
	if ( isOpen > 0) {
		elem.setAttribute("ReceiveFlag", true);
	}
	else {
		elem.setAttribute("StartReceiptFlag", true);
	}
}
//System.out.println("StartReceiptFlag-->" + resolveValue("xml:/ReceiptList/@StartReceiptFlag") + " elem-->" + elem.getAttribute("StartReceiptFlag"));
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
    <td height="100%" width="75%">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I06"/>
            <jsp:param name="HideGiftFlag" value="Y"/>
        </jsp:include>
    </td>
    <td height="100%" width="25%" addressip="true" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true">
            <jsp:param name="CurrentInnerPanelID" value="I03"/>
            <jsp:param name="Path" value="xml:/OrderRelease/PersonInfoShipTo"/>
            <jsp:param name="DataXML" value="OrderRelease"/>
            <jsp:param name="AllowedModValue" value='<%=getModificationAllowedValueWithPermission("ShipToAddress", "xml:/OrderRelease/AllowedModifications")%>'/>
        </jsp:include>
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
