<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<table class="view" width="100%">
	<tr>
		<td class="detaillabel" nowrap="true" ><yfc:i18n>Ship_Node</yfc:i18n></td>	
		<td class="protectedtext" nowrap="true" >
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/@ShipNode"/>&nbsp;
		</td>
		<td class="detaillabel" nowrap="true" ><yfc:i18n>Ship_To_Address</yfc:i18n></td>	
		<td class="protectedtext" nowrap="true" >
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@AddressLine1"/>&nbsp;
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@AddressLine2"/>&nbsp;
		</td>
	</tr>
	<tr>
		<td class="detaillabel" nowrap="true" ><yfc:i18n>Receiving_Node</yfc:i18n></td>	
		<td class="protectedtext" nowrap="true" >
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/@ReceivingNode"/>&nbsp;
		</td>

		<td>&nbsp;</td>
		<td class="protectedtext" nowrap="true" >	
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@City"/>&nbsp;
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@State"/>&nbsp;
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@Country"/>&nbsp;
			<yfc:getXMLValue name="DeliveryServiceGroup" binding="xml:/DeliveryServiceGroup/PersonInfoShipTo/@ZipCode"/>&nbsp;
		</td>
	</tr>
	<tr>
		<td colspan="4" style="border:1px solid black">
			<jsp:include page="/om/deliveryrequest/detail/deliveryrequest_detail_add_singledelivery_table.jsp" flush="true"/>
		</td>
	</tr>
</table>
