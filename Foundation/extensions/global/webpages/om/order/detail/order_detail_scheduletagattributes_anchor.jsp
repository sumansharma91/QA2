<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<%

	String getItemDetailsInputStringEncoded = (String) request.getParameter("getItemDetailsInput");
	String getItemDetailsInputString = java.net.URLDecoder.decode(getItemDetailsInputStringEncoded);
	YFCElement getItemDetailsInputElem = YFCDocument.parse(getItemDetailsInputString).getDocumentElement();

	//get necessary attributes from getItemDetailsInputElem and remove additional ones not needed for getItemDetails
	String orderHeaderKey = getItemDetailsInputElem.getAttribute("OrderHeaderKey");
	String orderLineScheduleKey = getItemDetailsInputElem.getAttribute("OrderLineScheduleKey");
	getItemDetailsInputElem.removeAttribute("OrderHeaderKey");
	getItemDetailsInputElem.removeAttribute("OrderLineScheduleKey");

	YFCElement getOrderLineStatusListInputElem = YFCDocument.createDocument("OrderLineStatus").getDocumentElement();
	getOrderLineStatusListInputElem.setAttribute("OrderHeaderKey",orderHeaderKey);

	//prepare template for getOrderLineStatusList
	String getOrderLineStatusListTemplateString = "<OrderLineStatusList>" +
                   "<OrderStatus OrderLineScheduleKey=\"\" >" +
                      "<Schedule BatchNo=\"\" ExpectedShipmentDate=\"\" LotNumber=\"\" OrderLineScheduleKey=\"\" RevisionNo=\"\" ShipByDate=\"\" TagNumber=\"\" >" +
						"<Extn/>" +
					 "</Schedule>" +
				  "</OrderStatus>" +
			   "</OrderLineStatusList>";

	YFCElement getOrderLineStatusListTemplateElem = YFCDocument.parse(getOrderLineStatusListTemplateString).getDocumentElement();

%>
	<yfc:callAPI apiName="getOrderLineStatusList" inputElement="<%=getOrderLineStatusListInputElem%>" templateElement="<%=getOrderLineStatusListTemplateElem%>" outputNamespace="OrderLineStatusList"/>

<%
	//Find the right schedule element that was passed from the previous screen
	YFCElement orderLineStatusListElem = (YFCElement)request.getAttribute("OrderLineStatusList");
	for (Iterator i = orderLineStatusListElem.getChildren(); i.hasNext();) {
		YFCElement orderStatusElem = (YFCElement)i.next();
		String tempOrderLineScheduleKey = orderStatusElem.getAttribute("OrderLineScheduleKey");
		if (YFCCommon.equals(tempOrderLineScheduleKey,orderLineScheduleKey)) {
			request.setAttribute("TagContainer",orderStatusElem);
		}
	}
	String itemDetailsTemplateTxt = "<Item>" +
				"<InventoryTagAttributes ItemTagKey=\"\" ItemKey=\"\" LotNumber=\"\" LotKeyReference=\"\" ManufacturingDate=\"\" LotExpirationDate=\"\" LotAttribute1=\"\" LotAttribute2=\"\" LotAttribute3=\"\" RevisionNo=\"\" BatchNo=\"\" >" +
					"<Extn />" +
				"</InventoryTagAttributes>" +
			"</Item>" ;
	YFCElement itemDetailsTemplateElem = YFCDocument.parse(itemDetailsTemplateTxt).getDocumentElement();
%>
	<yfc:callAPI apiName="getItemDetails" inputElement="<%=getItemDetailsInputElem%>" templateElement="<%=itemDetailsTemplateElem%>" outputNamespace="ItemDetails"/>
<%
	YFCElement ItemDetailsElem = (YFCElement)request.getAttribute("ItemDetails");
%>

<table class="anchor" cellpadding="7px"  cellSpacing="0" >
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
	            <jsp:param name="TagElement" value="Schedule"/>
				<jsp:param name="IdentifiersOnly" value="true"/>	
				<jsp:param name="Modifiable" value='false'/>
			</jsp:include>
		</td>
	</tr>
</table>
