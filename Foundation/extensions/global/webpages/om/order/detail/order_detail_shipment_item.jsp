<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>

<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String rootNodeName = "ShipmentList";
%>

<table class="table" width="100%">
<thead>
    <tr> 
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@OrderNo")%>">
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipmentNo")%>">
            <yfc:i18n>Order_Line</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@PickticketNo")%>">
            <yfc:i18n>Item</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ExpectedShipmentDate")%>">
            <yfc:i18n>UOM</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipMode")%>">
            <yfc:i18n>PC</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ExpectedShipmentDate")%>">
            <yfc:i18n>Actual_Ship_Date</yfc:i18n>
        </td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ReceivingNode")%>">
            <yfc:i18n>Tracking_#</yfc:i18n>            
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/Status/@Description")%>">
            <yfc:i18n>Carrier_/_Service</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipmentNo")%>">
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@PickticketNo")%>">
            <yfc:i18n>Shippers_Ref_#</yfc:i18n>
        </td>
    </tr>
</thead> 
<tbody>
	<!-- Loop over Shipment records from getShipmentListForOrder() API output list. -->
    <yfc:loopXML binding='<%=buildBinding("xml:/",rootNodeName,"/@Shipment")%>' id="Shipment"> 
        <yfc:makeXMLInput name="shipmentKey" >
            <yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
        </yfc:makeXMLInput>

		<!-- For each Shipment record in the list, call getShipmentDetails() API. -->
		<% request.setAttribute("Shipment", pageContext.getAttribute("Shipment")); %>
		<yfc:callAPI apiID="AP1"/>

        <%  boolean isHistoryShipment = equals("Y",resolveValue("xml:/Shipment/@isHistory")); %>

		<%	// set correct link id for line detail view.
			String sLinkId = "L01";
			if (equals(resolveValue("xml:ShipmentDetails:/Shipment/ServiceLine/@ItemGroupCode"),"PS"))	{	
				sLinkId = "L03";
		}	%>

		<%
			String shpCarrierAndService = "";
			if (!isVoid(resolveValue("xml:ShipmentDetails:/Shipment/@ScacAndServiceKey"))) {
				String shpScacAndServiceKey = resolveValue("xml:ShipmentDetails:/Shipment/@ScacAndServiceKey");

				YFCElement shpInputElem = YFCDocument.parse("<ScacAndService ScacAndServiceKey=\""+shpScacAndServiceKey+"\"/>").getDocumentElement();

				YFCElement shpTemplateElem = YFCDocument.parse("<ScacAndService ScacAndServiceDesc=\"\"/>").getDocumentElement();
		%>

				<yfc:callAPI apiName="getScacAndServiceList" inputElement="<%=shpInputElem%>" templateElement="<%=shpTemplateElem%>" outputNamespace="ScacAndServiceList"/>

				<yfc:loopXML binding="xml:/ScacAndServiceList/@ScacAndService" id="ScacAndService">
					<% shpCarrierAndService = resolveValue("xml:/ScacAndService/@ScacAndServiceDesc"); %>
				</yfc:loopXML>
		<%
			} else {
				shpCarrierAndService = resolveValue("xml:ShipmentDetails:/Shipment/@SCAC") + " "
											+ resolveValue("xml:ShipmentDetails:/Shipment/@CarrierServiceCode");
			}
		%>

		<!-- For each ShipmentDetails record loop over ShipmentLine records -->
		<yfc:loopXML binding="xml:ShipmentDetails:/Shipment/ShipmentLines/@ShipmentLine" id="ShipmentLine">

			<yfc:makeXMLInput name="orderLineKey">
				<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/ShipmentLine/@OrderLineKey"/>
				<yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderHeaderKey" value="xml:/ShipmentLine/@OrderHeaderKey"/>
			</yfc:makeXMLInput>

			<% boolean containerDetailsExist=false; %>

			<!-- For each ShipmentLine record, loop over Container records -->
			<yfc:loopXML binding="xml:ShipmentDetails:/Shipment/Containers/@Container" id="Container">

				<!-- For each Container record, loop over ContainerDetail records -->
				<yfc:loopXML binding="xml:/Container/ContainerDetails/@ContainerDetail" id="ContainerDetail">

					<!-- If ShipmentLine.OrderLineKey = ContainerDetail.OrderLineKey, show the row. -->
					<%
						if (equals(resolveValue("xml:/ShipmentLine/@OrderLineKey"), resolveValue("xml:/ContainerDetail/@OrderLineKey"))) {
							containerDetailsExist = true;
					%>
						<!-- Show the panel row with container data. -->
						<tr>
                            <td class="tablecolumn" >
                                <yfc:getXMLValue binding="xml:/ShipmentLine/@OrderNo"/>
                            </td>
							<td class="tablecolumn" >
								<% if(showOrderLineNo("Order","Order")) {%>
								<a <%=getDetailHrefOptions(sLinkId, getParameter("orderLineKey"), "")%>>
									<yfc:getXMLValue binding="xml:/ShipmentLine/@PrimeLineNo"/></a>
								<%} else {%>
									<yfc:getXMLValue binding="xml:/ShipmentLine/@PrimeLineNo"/>
								<%}%>
							</td>

							<td class="tablecolumn">
								<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
							</td>
							<td class="tablecolumn">
								<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
							</td>
							<td class="tablecolumn" sortValue="<%=getDateValue("xml:ShipmentDetails:/Shipment/@ActualShipmentDate")%>">
								<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ActualShipmentDate"/>
							</td>
							<td class="tablecolumn">
								<input class="protectedinput" <%=getTextOptions( "xml:/Container/@TrackingNo", "xml:/Container/@TrackingNo")%>/>
							</td>


							<%
								String carrierAndService = "";
								
								if (!isVoid(resolveValue("xml:/Container/@ScacAndServiceKey"))) {
									String contScacAndServiceKey = resolveValue("xml:/Container/@ScacAndServiceKey");

									YFCElement contInputElem = YFCDocument.parse("<ScacAndService ScacAndServiceKey=\""+contScacAndServiceKey+"\"/>").getDocumentElement();

									YFCElement contTemplateElem = YFCDocument.parse("<ScacAndService ScacAndServiceDesc=\"\"/>").getDocumentElement();
							%>
									<yfc:callAPI apiName="getScacAndServiceList" inputElement="<%=contInputElem%>" templateElement="<%=contTemplateElem%>" outputNamespace="ContScacAndServiceList"/>

									<yfc:loopXML binding="xml:ContScacAndServiceList:/ScacAndServiceList/@ScacAndService" id="ScacAndService">
										<% carrierAndService = resolveValue("xml:/ScacAndService/@ScacAndServiceDesc"); %>
									</yfc:loopXML>
							<%
								} else if ((!isVoid(resolveValue("xml:/Container/@SCAC"))) ||
									      (!isVoid(resolveValue("xml:/Container/@CarrierServiceCode")))) {
									carrierAndService = resolveValue("xml:/Container/@SCAC") + " "
														+ resolveValue("xml:/Container/@CarrierServiceCode");
								} else {
									carrierAndService = shpCarrierAndService;
								}
							%>

							<td class="tablecolumn">
								<yfc:i18ndb><%=carrierAndService%></yfc:i18ndb>
							</td>

							<td class="tablecolumn">
                                <% if (isHistoryShipment) { %>
                                    <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
                                <% } else { %>
    								<a <%=getDetailHrefOptions("L02",getValue("Shipment", "xml:/Shipment/@DocumentType"),getParameter("shipmentKey"),"")%> >
	    								<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
		    						</a>
                                <% } %>
							</td>
							<td class="tablecolumn">
								<input class="protectedinput" <%=getTextOptions( "xml:/Shipment/@PickticketNo", "xml:/Shipment/@PickticketNo")%>/>
							</td>
						</tr>
					<% } %>
				</yfc:loopXML>
			</yfc:loopXML>

			<% if (!containerDetailsExist) { %>
				<!-- Show the panel row WITHOUT container data. -->
				<tr>
                    <td class="tablecolumn" >
                        <yfc:getXMLValue binding="xml:/ShipmentLine/@OrderNo"/>
                    </td>
					<td class="tablecolumn" >
						<% if(showOrderLineNo("Order","Order")) {%>
						<a <%=getDetailHrefOptions(sLinkId, getParameter("orderLineKey"), "")%>>
							<yfc:getXMLValue binding="xml:/ShipmentLine/@PrimeLineNo"/></a>
						<%} else {%>
							<yfc:getXMLValue binding="xml:/ShipmentLine/@PrimeLineNo"/>
						<%}%>
					</td>

					<td class="tablecolumn">
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ItemID"/>
					</td>
					<td class="tablecolumn">
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@UnitOfMeasure"/>
					</td>
					<td class="tablecolumn">
						<yfc:getXMLValue name="ShipmentLine" binding="xml:/ShipmentLine/@ProductClass"/>
					</td>
					<td class="tablecolumn" sortValue="<%=getDateValue("xml:ShipmentDetails:/Shipment/@ActualShipmentDate")%>">
						<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ActualShipmentDate"/>
					</td>
					<td>
						<!-- TrackingNo -->
					</td>

					<td class="tablecolumn">
						<yfc:i18ndb><%=shpCarrierAndService%></yfc:i18ndb>
					</td>

					<td class="tablecolumn">
                        <% if (isHistoryShipment) { %>
                            <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
                        <% } else { %>
    						<a <%=getDetailHrefOptions("L02",getValue("Shipment", "xml:/Shipment/@DocumentType"),getParameter("shipmentKey"),"")%> >
	    						<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
		    				</a>
                        <% } %>
					</td>
					<td class="tablecolumn">
						<input class="protectedinput"  contenteditable="false" <%=getTextOptions( "xml:/Shipment/@PickticketNo", "xml:/Shipment/@PickticketNo")%>/>
					</td>
				</tr>
			<% } %>
		</yfc:loopXML> 
	</yfc:loopXML> 
</tbody>
</table>
