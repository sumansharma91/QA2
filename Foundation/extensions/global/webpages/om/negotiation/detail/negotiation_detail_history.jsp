<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<% 	boolean isInitiator = isInitiatorOrganization((YFCElement) request.getAttribute("Negotiations"));

	YFCElement responseList = sortNegotiationHistoryElement((YFCElement) request.getAttribute("NegotiationHistory"));
	request.setAttribute("NegotiationHistoryResponseList", responseList);
%>

<table class="table" width="100%">
    <thead>
		<tr>
			<td class="tablecolumnheader" ><yfc:i18n>Date</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>Resp_#</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>For_Resp_#</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>Action_1</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>Freight_Terms</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>Payment_Terms</yfc:i18n></td>
			<td class="tablecolumnheader" ><yfc:i18n>User</yfc:i18n></td>
			<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/HeaderResponse/@Notes")%>"><yfc:i18n>Reason</yfc:i18n></td>
		</tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:NegotiationHistoryResponseList:/NegotiationHistory/Responses/HeaderResponses/@HeaderResponse" id="HeaderResponse">
			<tr>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/HeaderResponse/@ResponseDate"/></td>
				<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/HeaderResponse/@ResponseNo"/></td>
				<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/HeaderResponse/@ForResponseNo"/></td>
				<td class="tablecolumn"><%=displayOrderStatus("N",getValue("HeaderResponse","xml:/HeaderResponse/@ResponseActionDesc"),true)%></td>
				<td class="tablecolumn"><%=getComboText("xml:FreightTermsList:/FreightTermsList/@FreightTerms","ShortDescription","FreightTerms","xml:/HeaderResponse/HeaderTerms/@FreightTerms",true)%></td>    
				<td class="tablecolumn"><%=getComboText("xml:PaymentTermsList:/CommonCodeList/@CommonCode","CodeShortDescription","CodeValue","xml:/HeaderResponse/HeaderTerms/@PaymentTerms",true)%></td>
				<td class="tablecolumn"><yfc:getXMLValue binding="xml:/HeaderResponse/@EnteredBy"/></td>
				<td class="tablecolumn">
					<yfc:getXMLValue binding="xml:/HeaderResponse/@ReasonCode"/>
				   <%//If both reason code & notes are there, only then we need a line break in between %>
				   <%if ((!isVoid(getValue("HeaderResponse", "xml:/HeaderResponse/@ReasonCode")))
				   && (!isVoid(getValue("HeaderResponse", "xml:/HeaderResponse/@Notes")))) {%>
				   </br>
				   <%}%>
				   <yfc:getXMLValue binding="xml:/HeaderResponse/@Notes"/>
				</td>
			 </tr>
		</yfc:loopXML>
	</tbody>
</table>
