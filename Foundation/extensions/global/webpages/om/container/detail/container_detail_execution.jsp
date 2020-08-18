<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%
 boolean showManifest = false;
   if(!isVoid(resolveValue("xml:/Container/ContainerStatusAudits/@TotalNumberOfRecords"))){
        if(!equals(resolveValue("xml:/Container/ContainerStatusAudits/@TotalNumberOfRecords"),"0")){
            showManifest = true;
        }
   }
%>
<table width="100%" class="view">
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Container_Location</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:getXMLValue binding="xml:/Container/LPN/LPNLocation/@LocationId"/>	
		</td>
		<td class="detaillabel" >
			<yfc:i18n>Container_Status</yfc:i18n>
		</td>
		<td class="protectedtext" >
			<yfc:getXMLValueI18NDB binding="xml:/Container/Status/@StatusName"/>	
		</td>	

		<%if(showManifest){%>
		<td class="detaillabel" >
			<yfc:i18n>Is_Manifested</yfc:i18n> 
		</td>
	    <td class="protectedtext">
			<yfc:i18n><yfc:getXMLValue name="Container" binding="xml:/Container/@IsManifested"/></yfc:i18n>
        </td>
		<%}%>
	</tr>
</table>
