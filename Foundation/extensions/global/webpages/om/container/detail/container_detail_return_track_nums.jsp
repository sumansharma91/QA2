<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<table class="table" width="100%">
<thead>
    <tr> 

		 <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>

        <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/Container/ContainerReturnTrackingList/ContainerReturnTracking/@ReturnTrackingNumber")%>">
            <yfc:i18n>Return_Tracking_Number</yfc:i18n>
        </td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML name="Container" binding="xml:/Container/ContainerReturnTrackingList/@ContainerReturnTracking" id="ContainerReturnTracking" > 
		<yfc:makeXMLInput name="returnTrackingNumberPrintKey">
					<yfc:makeXMLKey binding="xml:/Print/Container/@ShipmentContainerKey" value="xml:/Container/@ShipmentContainerKey" />	
					<yfc:makeXMLKey binding="xml:/Print/Container/ContainerReturnTrackingList/ContainerReturnTracking/@ReturnTrackingNumber" value="xml:/ContainerReturnTracking/@ReturnTrackingNumber" />
					<yfc:makeXMLKey binding="xml:/Print/Container/@EnterpriseCode" value="xml:/Container/Shipment/@EnterpriseCode" />
					<yfc:makeXMLKey binding="xml:/Print/Container/@SCAC" value="xml:/Container/Shipment/@SCAC" />
		</yfc:makeXMLInput>
		<tr> 
			<td width="1%"> 
				<input type="checkbox" value='<%=getParameter("returnTrackingNumberPrintKey")%>' name="chkEntityKey" PrintEntityKey='<%=getParameter("returnTrackingNumberPrintKey")%>' /> 
			</td>			
			<td class="tablecolumn">
				<yfc:getXMLValueI18NDB name="ContainerReturnTracking" binding="xml:/ContainerReturnTracking/@ReturnTrackingNumber"/>
			</td>
		</tr>
    </yfc:loopXML> 
</tbody>
</table>
