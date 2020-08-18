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
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@TrackingNo")%>">
            <yfc:i18n>Tracking_#</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@CarrierServiceCode")%>">
            <yfc:i18n>Carrier_/_Service</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ExpectedShipmentDate")%>">
            <yfc:i18n>Expected_Ship_Date</yfc:i18n>
        </td>
		<td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/@ShipmDate")%>">
            <yfc:i18n>Actual_Ship_Date</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipNode")%>">
            <yfc:i18n>Ship_Node</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ReceivingNode")%>">
            <yfc:i18n>Recv_Node</yfc:i18n>            
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/@ShipmentNo")%>">
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/ShipmentList/Shipment/Status/@Description")%>">
            <yfc:i18n>Status</yfc:i18n>
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

		<!-- For each ShipmentDetails record loop over Container records -->
		<yfc:loopXML binding="xml:ShipmentDetails:/Shipment/Containers/@Container" id="Container">
			<tr>
				<yfc:makeXMLInput name="shipmentKey" >
					<yfc:makeXMLKey binding="xml:/Shipment/@ShipmentKey" value="xml:/Shipment/@ShipmentKey" />
				</yfc:makeXMLInput>
				<yfc:makeXMLInput name="ShipmentContainerKey" >
					<yfc:makeXMLKey binding="xml:/Container/@ShipmentContainerKey" value="xml:/Container/@ShipmentContainerKey" />
				</yfc:makeXMLInput>

				<td class="checkboxcolumn"> 
					<input type="checkbox" value='<%=getParameter("ShipmentContainerKey")%>' name="chkEntityKey" <%if(isHistoryShipment){%>disabled="true"<%}%>/>
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

				<td class="tablecolumn" sortValue="<%=getDateValue("xml:ShipmentDetails:/Shipment/@ExpectedShipmentDate")%>">
					<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ExpectedShipmentDate"/>
				</td>
				<td class="tablecolumn" sortValue="<%=getDateValue("xml:ShipmentDetails:/Shipment/@ActualShipmentDate")%>">
					<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ActualShipmentDate"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ShipNode"/>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValue name="ShipmentDetails" binding="xml:ShipmentDetails:/Shipment/@ReceivingNode"/>
				</td>
				<td class="tablecolumn">
                    <% if(isHistoryShipment) { %>
                        <yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
                    <% } else { %>
    					<a <%=getDetailHrefOptions("L01",getValue("Shipment", "xml:/Shipment/@DocumentType"),getParameter("shipmentKey"),"")%> >
    						<yfc:getXMLValue name="Shipment" binding="xml:/Shipment/@ShipmentNo"/>
	    				</a>
                    <% } %>
				</td>
				<td class="tablecolumn">
					<yfc:getXMLValueI18NDB name="Shipment" binding="xml:/Shipment/Status/@Description"/>
                    <% if (isHistoryShipment) { %>
                        <img class="columnicon" onmouseover="this.style.cursor='default'" <%=getImageOptions(YFSUIBackendConsts.HISTORY_ORDER, "This_is_an_archived_shipment")%>>
                    <% } %>
				</td>
			</tr>
		</yfc:loopXML> 
	</yfc:loopXML> 
</tbody>
</table>
