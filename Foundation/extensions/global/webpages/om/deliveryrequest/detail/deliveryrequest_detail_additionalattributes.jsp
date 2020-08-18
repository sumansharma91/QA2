<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<table class="view" width="100%">
    <tr>
		<td class="detaillabel" >
			<yfc:i18n>Fulfillment_Type</yfc:i18n>
		</td>
		<td>
			<select class="combobox" <%=getComboOptions("xml:/Order/OrderLines/OrderLine/@FulfillmentType", "xml:/OrderLine/@FulfillmentType")%> >
				<yfc:loopOptions binding="xml:FulfillmentTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
				value="CodeValue" selected="xml:/OrderLine/@FulfillmentType" isLocalized="Y" targetBinding="xml:/Order/OrderLines/OrderLine/@FulfillmentType"/>
			</select>
		</td>  
		<td></td>
		<td></td>
	</tr>
</table>
