<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<table class="anchor" cellpadding="7px" cellSpacing="0">
	<tr>
		<td>
			<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
				<jsp:param name="CurrentInnerPanelID" value="I01"/>
			</jsp:include>
		</td>
	</tr>
	<tr>
	   <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes">
	   <yfc:hasXMLNode binding="xml:ItemDetails:/Item/InventoryTagAttributes/Extn">
<%
	YFCElement root = (YFCElement) request.getAttribute("Receipt");
	YFCDocument rcptLineInDoc = YFCDocument.createDocument("ReceiptLine");
	YFCElement apiInDocRoot = rcptLineInDoc.getDocumentElement(); 
	apiInDocRoot.setAttribute("ReceiptLineKey", root.getAttribute("ReceiptLineKey"));
	YFCElement tempElem = YFCDocument.parse("<ReceiptLineList><ReceiptLine>						<OrderLine OrderLineKey=\"\"><Item ItemShortDesc=\"\"/></OrderLine><Extn/>					  </ReceiptLine></ReceiptLineList>").getDocumentElement();
%>			
	<yfc:callAPI apiName="getReceiptLineList" inputElement="<%=apiInDocRoot%>" 
				   	     templateElement="<%=tempElem%>" outputNamespace=""/>
		
<%
	YFCElement elemReceiptLineList = (YFCElement)request.getAttribute("ReceiptLineList");
	if(!isVoid(elemReceiptLineList)){
		YFCElement elemRcptLine = elemReceiptLineList.getChildElement("ReceiptLine");
		if(!isVoid(elemRcptLine) && !isVoid(elemRcptLine.getChildElement("Extn"))){
			YFCElement tagElem = root.getChildElement("Tag");
			YFCElement extnTagElem = tagElem.getChildElement("Extn", true);
			extnTagElem.setAttributes(elemRcptLine.getChildElement("Extn").getAttributes());
		}
	}
%>
	   </yfc:hasXMLNode>
	<% YFCElement tagElem =(  YFCElement) request.getAttribute("Item");
	   if(tagElem!=null){
		   request.setAttribute("Item", tagElem);
	   }
	%>
			<td >
					<jsp:include page="/yfc/innerpanel.jsp" flush="true" >
						<jsp:param name="CurrentInnerPanelID" value="I02"/>
						<jsp:param name="Modifiable" value='false'/>
						<jsp:param name="LabelTDClass" value='detaillabel'/>
						<jsp:param name="TagContainer" value='Receipt'/>
						<jsp:param name="TagElement" value='Tag'/>
					</jsp:include>
			</td>
		</yfc:hasXMLNode>
	</tr>
</table>
