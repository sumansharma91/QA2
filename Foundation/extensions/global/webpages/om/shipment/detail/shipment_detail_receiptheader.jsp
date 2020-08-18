<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table width="100%" class="view">
<tr>
		
    <td class="detaillabel" >
        <yfc:i18n>Shipment_#</yfc:i18n>
    </td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/GetLinesToReceive/@ShipmentNo"/>
	</td>
	
	<td class="detaillabel">
			<yfc:i18n>Enterprise</yfc:i18n>
	</td>
	<td class="protectedtext">
		<yfc:getXMLValue  binding="xml:/GetLinesToReceive/@EnterpriseCode"/>
	</td>


</table>
