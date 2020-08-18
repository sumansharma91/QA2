<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf" %>
<%@include file="/console/jsp/order.jspf" %>

<% setHistoryFlags( (YFCElement)request.getAttribute("OrderRelease") ); %>

<yfc:callAPI apiID="AP1"/> <!-- getShipmentListForOrder -->

<script language="javascript">
	function displayShipmentDetail() {
		var sVal = document.all("hiddenShipmentKeyInput");
		callPopupWithEntity('shipment',sVal.value, 'false', 'true');
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


<table class="anchor" cellpadding="7px" cellSpacing="0">
<tr>
    <td colspan="2" >
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="I01"/>
            <jsp:param name="ModifyView" value="true"/>
        </jsp:include>
    </td>
</tr>

<%	
	String sInnerPanel = null;
	if(isTrue("xml:/OrderRelease/@IsProvidedService") )	{
		sInnerPanel = "I05";
	}
	else if(equals("DEL", resolveValue("xml:/OrderRelease/@DeliveryMethod") ) ) 	{
		sInnerPanel = "I02";
	}
	else {
		sInnerPanel = "I06";
	}
%>

<tr>
    <td height="100%" width="75%">
        <jsp:include page="/yfc/innerpanel.jsp" flush="true" >
            <jsp:param name="CurrentInnerPanelID" value="<%=sInnerPanel%>"/>
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

<%	//System.out.println("[" + resolveValue("xml:/OrderRelease/@IsProvidedService") + "]" );
	if(!isTrue("xml:/OrderRelease/@IsProvidedService") )	{%>
	<tr>
		<td colspan="2" >
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I04"/>
			</jsp:include>
		</td>
	</tr>
<%	}%>

</table>
