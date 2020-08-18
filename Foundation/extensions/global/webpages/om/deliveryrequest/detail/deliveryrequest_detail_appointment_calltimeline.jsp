<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/om/deliveryrequest/detail/deliveryrequest_detail_appointment_calltimeline_include.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>

<%  
		YFCElement rootElement = getElement("Promise");
		YFCDocument timeLineInputDocument = formTimeLineInputDocumentForDelAppt(rootElement);

		//System.out.println(timeLineInputDocument.getString());

		YFCElement timeLineInputElement = timeLineInputDocument.getDocumentElement();
		request.setAttribute("TimeLineInput", timeLineInputElement);
%>


<table class="view" width="100%" >
<tr>
	<td colspan="6">
		<jsp:include page="/yfsjspcommon/timelineview.jsp" flush="true" />
	</td>
</tr>
<tr>
	<td height="100%">
	<fieldset>
	<legend><yfc:i18n>Legend</yfc:i18n></legend> 
		<table height="100%" width="100%" >
			<tr>
				<td align="right"><img class="nonclickablecolumnicon" <%=getImageOptions(request.getContextPath() + "/console/icons/reservations.gif", "Product_Availability_Date")%> />
				&nbsp;</td>
				<td class="protectedtext"><yfc:i18n>Product_Availability_Date</yfc:i18n></td>
				<td align="right"><img class="nonclickablecolumnicon" <%=getImageOptions(request.getContextPath() + "/console/icons/shipments.gif", "Expected_Delivery_Date")%> />
				&nbsp;</td>
				<td class="protectedtext"><yfc:i18n>Earliest_Delivery_Date</yfc:i18n></td>
				<td/>
				<td/>
			</tr>
			<tr>
				<td class="timelineInRangeDay" title='<%=getI18N("Line_Has_Constraints")%>' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td class="protectedtext"><yfc:i18n>Line_Has_Constraints</yfc:i18n></td>
				<td class="timelineNonWorkingDay" title='<%=getI18N("Non_Working_Day")%>' >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td class="protectedtext"><yfc:i18n>Non_Working_Day</yfc:i18n></td>
				<td class="numerictablecolumnshortagerow"  title='<%=getI18N("Line_Omitted")%>'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</td>
				<td class="protectedtext"><yfc:i18n>Line_Omitted</yfc:i18n></td>
			</tr>
		</table>
	</fieldset>
	</td>
</tr>
</table>
