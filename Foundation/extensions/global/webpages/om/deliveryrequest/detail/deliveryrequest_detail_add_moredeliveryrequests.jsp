<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String setNotToBeShownInList = getParameter("DeliverySetAlreadyShown");
%>

	<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0">
		<thead>
			<tr>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Delivery_Request</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Ship_Node</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Receiving_Node</yfc:i18n>
				</td>
				<td class="tablecolumnheader" sortable="yes">
					<yfc:i18n>Ship_To_Address</yfc:i18n>
				</td>
			</tr>
		</thead>
		<tbody>
			<yfc:loopXML binding="xml:OrderDelivery:/Order/@DeliveryServiceGroup" id="DeliveryServiceGroup">
			<%	//System.out.println("DeliveryServiceGroupCounter = "+DeliveryServiceGroupCounter);	
					if (!(DeliveryServiceGroupCounter.intValue()  == Integer.parseInt(setNotToBeShownInList)))
					{ 	%>
						<tr>
							<td class="tablecolumn">
								<a href="javascript:addDeliveryChangeParameter('<%=DeliveryServiceGroupCounter%>','DeliverySetToShow');javascript:yfcChangeDetailView(getCurrentViewId());">
									<yfc:i18n>Delivery_Request_#</yfc:i18n>&nbsp<%=DeliveryServiceGroupCounter%>
								</a>
							</td>
							<td class="tablecolumn">
									<yfc:getXMLValue  name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/@ShipNode" />
							</td>
							<td class="tablecolumn">
									<yfc:getXMLValue  name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/@ReceivingNode" />
							</td>
							<td class="tablecolumn">
									<yfc:getXMLValue  name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@AddressLine1" />&nbsp
									<yfc:getXMLValue  name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@AddressLine2" /><br/>
									<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@City"/>&nbsp;
									<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@State"/>&nbsp;
									<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@Country"/>&nbsp;
									<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@ZipCode"/>&nbsp;
							</td>
						</tr>
			<%	}	%>
			</yfc:loopXML>
		</tbody>
	</table>
