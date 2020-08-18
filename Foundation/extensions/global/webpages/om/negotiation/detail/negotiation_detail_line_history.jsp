<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/negotiationutils.jspf" %>

<%
	YFCElement negotiableLineAttrs = getNegotiableLineAttributes((YFCElement) request.getAttribute("Negotiations"));

	String freightTermsSplit = negotiableLineAttrs.getAttribute("FreightTerms");
	String shipDateSplit = negotiableLineAttrs.getAttribute("ShipDate");
	String deliveryDateSplit =  negotiableLineAttrs.getAttribute("DeliveryDate");
	String priceSplit =  negotiableLineAttrs.getAttribute("Price");

%>

<yfc:loopXML binding="xml:NegotiationHistoryResponseList:/NegotiationHistory/Responses/LineResponses/@LineDetail" id="LineDetail">

	<table class="view">
		<tr>
			<td class="detaillabel" nowrap="true" sortable="no"><yfc:i18n>Line_#</yfc:i18n></td>
			<td class="protectedtext"><yfc:getXMLValue binding="xml:/LineDetail/@PrimeLineNo"/></td>
		</tr>
	</table>

	<table class="simpletable" cellspacing="0" width="100%">
		<thead>
			<tr>
				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Date</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Resp_#</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>For_Resp_#</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Action_1</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader" style="width:<%= getUITableSize("xml:/LineTermsDetail/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader" style="width:<%= getUITableSize("xml:/LineTermsDetail/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader" style="width:<%= getUITableSize("xml:/LineTermsDetail/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>

				<% if (!equals(freightTermsSplit,"NULL")) { %>
					<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Freight_Terms</yfc:i18n></td>
				<% } %>
				
				<% if (!equals(shipDateSplit,"NULL")) { %>
					<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Ship_Date</yfc:i18n></td>
				<% } %>

				<% if (!equals(deliveryDateSplit,"NULL")) { %>
					<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Delivery_Date</yfc:i18n></td>
				<% } %>
		
				<% if (!equals(priceSplit,"NULL")) { %>
					<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Price</yfc:i18n></td>
				<% } %>

				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>Quantity</yfc:i18n></td>
				<td nowrap="true" class="tablecolumnheader"><yfc:i18n>User</yfc:i18n></td>
				<td class="tablecolumnheader"><yfc:i18n>Reason</yfc:i18n></td>
			</tr>
		</thead>
		<tbody>
		    <% String className="oddrow"; %>
			<yfc:loopXML binding="xml:/LineDetail/@LineResponse" id="LineResponse">

			    <% //Toggle className between even and odd for each new response. 
					if (className.equals("oddrow"))
						className="evenrow";
					else
						className="oddrow";
				%>
				<yfc:loopXML binding="xml:/LineResponse/LineTerms/@LineTermsDetail" id="LineTermsDetail">
				<tr class='<%=className%>'> 
                    <% // Don't repeat the Response# for additional Line details
                       if (LineTermsDetailCounter.intValue() > 1) { %>
                        <td class="tablecolumn" colspan="4"/>
                    <%} else {%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineResponse/@ResponseDate"/></td>
						<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineResponse/@ResponseNo"/></td>
						<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineResponse/@ForResponseNo"/></td>
						<td class="tablecolumn">	<%=displayOrderStatus("N",getValue("LineResponse","xml:/LineResponse/@ResponseActionDesc"),true)%></td>
                    <%}%>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ItemID"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ProductClass"/></td>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@UnitOfMeasure"/></td>
	
					<% if (!equals(freightTermsSplit,"NULL")) { %>
						<td class="tablecolumn"><%=getComboText("xml:FreightTermsList:/FreightTermsList/@FreightTerms","ShortDescription","FreightTerms","xml:/LineTermsDetail/@FreightTerms",true)%></td>						
					<% } %>
					
					<% if (!equals(shipDateSplit,"NULL")) { %>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ShipDate"/></td>
					<% } %>

					<% if (!equals(deliveryDateSplit,"NULL")) { %>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@DeliveryDate"/></td>
					<% } %>

					<% if (!equals(priceSplit,"NULL")) { %>
						<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Price"/></td>
					<% } %>

					<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Quantity"/></td>


                    <% // Don't repeat the Response# for additional Line details
                       if (LineTermsDetailCounter.intValue() > 1) { %>
                        <td class="tablecolumn" colspan="2"/>
                    <%} else {%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineResponse/@EnteredBy"/></td>
						<td class="tablecolumn">
							<yfc:getXMLValue binding="xml:/LineResponse/@ReasonCode"/>
						   <%//If both reason code & notes are there, only then we need a line break in between %>
						   <%if ((!isVoid(getValue("LineResponse", "xml:/LineResponse/@ReasonCode")))
						   && (!isVoid(getValue("LineResponse", "xml:/LineResponse/@Notes")))) {%>
						   </br>
						   <%}%>
						   <yfc:getXMLValue binding="xml:/LineResponse/@Notes"/>
						</td>
                    <%}%>
				</tr>
				</yfc:loopXML>
			</yfc:loopXML>
		</tbody>
	</table>
	<hr/> 
</yfc:loopXML>
