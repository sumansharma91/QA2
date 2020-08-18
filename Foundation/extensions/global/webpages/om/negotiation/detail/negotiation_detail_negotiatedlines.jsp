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

<table class="table" ID="OrderLines" cellspacing="0" width="100%">
    <thead>
        <tr>
            <td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/@PrimeLineNo")%>"><yfc:i18n>Line</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Action_1</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Organization_Code</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>Resp_#</yfc:i18n></td>
            <td class="tablecolumnheader" nowrap="true"><yfc:i18n>For_Resp_#</yfc:i18n></td>
			<td class="tablecolumnheader" nowrap="true" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
            <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderLine/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>

			<% if (!equals(freightTermsSplit,"NULL")) { %>
				<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@FreightTerms")%>"><yfc:i18n>Freight_Terms</yfc:i18n></td>
			<% } %>
				
			<% if (!equals(shipDateSplit,"NULL")) {%>
				<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@ReqShipDate")%>"><yfc:i18n>Ship_Date</yfc:i18n></td>
			<% } %>

			<% if (!equals(deliveryDateSplit,"NULL")) {%>
				<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@DeliveryDate")%>"><yfc:i18n>Delivery_Date</yfc:i18n></td>
			<% } %>

			<% if (!equals(priceSplit,"NULL")) { %>
				<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@UnitPrice")%>"><yfc:i18n>Price</yfc:i18n></td>
			<% } %>

			<td class="tablecolumnheader" nowrap="true" sortable="no" style="width:<%=getUITableSize("xml:/OrderLine/@OrderedQty")%>"><yfc:i18n>Quantity</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/Negotiations/Negotiation/NegotiationLines/@NegotiationLine" id="NegotiationLine">
            <yfc:loopXML binding="xml:/NegotiationLine/NegotiatedLineResponse/LineTerms/@LineTermsDetail" id="LineTermsDetail">
                <tr>
                    <% // Don't repeat the line number for additional Line Terms
                       if (LineTermsDetailCounter.intValue() > 1) { %>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                        <td class="tablecolumn"/>
                    <%} else {%>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/NegotiationLine/@PrimeLineNo"/></td>
                        <td class="tablecolumn"> <%=displayOrderStatus("N",getValue("NegotiationLine","xml:/NegotiationLine/NegotiatedLineResponse/@ResponseActionDesc"),true)%></td>
                        <td class="tablecolumn"><yfc:getXMLValue binding="xml:/NegotiationLine/@LastResponseOrgCode"/></td>
                        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/NegotiationLine/NegotiatedLineResponse/@ResponseNo"/></td>
                        <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/NegotiationLine/NegotiatedLineResponse/@ForResponseNo"/></td>
                    <%}%>
					<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ItemID"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ProductClass"/></td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@UnitOfMeasure"/></td>

					<% if (!equals(freightTermsSplit,"NULL")) { %>
						<td class="tablecolumn"><%=getComboText("xml:FreightTermsList:/FreightTermsList/@FreightTerms","ShortDescription","FreightTerms","xml:/LineTermsDetail/@FreightTerms",true)%></td>						
					<% } %>

					<% if (!equals(shipDateSplit,"NULL")) {%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@ShipDate"/></td>
					<% } %>						

					<% if (!equals(deliveryDateSplit,"NULL")) {%>
						<td class="tablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@DeliveryDate"/></td>
					<% } %>						

					<% if (!equals(priceSplit,"NULL")) { %>
						<td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Price"/></td>
					<% } %>

                    <td class="numerictablecolumn"><yfc:getXMLValue binding="xml:/LineTermsDetail/@Quantity"/></td>
				</tr>
            </yfc:loopXML>
        </yfc:loopXML>
    </tbody>
</table>
