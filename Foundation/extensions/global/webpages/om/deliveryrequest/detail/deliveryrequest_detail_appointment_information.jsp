<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ include file="/console/jsp/order.jspf" %>
<% getTotalLineQuantity((YFCElement) request.getAttribute("OrderLine")); %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>

<table class="view" width="100%">
	<tr>
		<td width="50%">
			<table cellspacing="0" cellpadding="0" border="0" width="100%" class="view">
				<tr>
					<td class="detaillabel"><yfc:i18n>First_Available_Appointment</yfc:i18n></td>
				<%
					String timeWindowString = displayTimeWindow(getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptStartTimestamp"),getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptEndTimestamp"),getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone"));
				%>	
					<td class="protectedtext">
						<%=timeWindowString%>
						<%-- cr 34417 --%>
						<% if(!isVoid(timeWindowString) ) { 	%>
							<%=showTimeZoneIcon(getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone"), getLocale().getTimezone())%>
						<%} %>
					</td>
				</tr>
				<yfc:hasXMLNode binding="xml:OrderLine/OrderLineOptions/OrderLineOption">
				<tr>
					<td class="detaillabel">
						<yfc:i18n>Total_Quantity_Including_Options</yfc:i18n>
					</td>
					<td class="protectedtext">
						<yfc:getXMLValue binding="xml:/OrderLine/@TotalQuantity"></yfc:getXMLValue>
					</td>
				</tr>
				</yfc:hasXMLNode>
				<%	if(equals(getValue("Promise","xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@CannotSchedule"),"N"))	{	%>
					<tr>
						<td class="detaillabel">
							<yfc:i18n>Schedule_Immediately</yfc:i18n>
							<input name="CannotSchedule" type="hidden" value="N" />
						</td>
						<td >
							<input class="checkbox" type="checkbox"  <%=getCheckBoxOptions("xml:/Promise/@CanScheduleImmediately", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@CannotSchedule","N") %> name="chkEntityFlag"  />
						</td>
					</tr>
					<tr>
						<td class="detaillabel">
							<yfc:i18n>Release_Immediately</yfc:i18n>
						</td>
						<td>
							<input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/Promise/@CanReleaseImmediately", "N", "Y")%>/>
						</td>
					</tr>
				<%	}	else	{	%>
					<tr>
						<td>&nbsp</td>
					</tr>
					<tr>
						<td class="detaillabel" style="text-align:center" colspan="2" >
							<yfc:i18n>Currently_Not_Available_For_Scheduling</yfc:i18n>
							<input name="CannotSchedule" type="hidden" value="Y" />
						</td>
					</tr>
				<%	}	%>					
			</table>
		</td>
		<td width="50%" height="100%">
			<fieldset>
				<legend><yfc:i18n>Constraints</yfc:i18n></legend> 
				<jsp:include page="/om/servicerequest/detail/servicerequest_detail_appointment_constraints.jsp" flush="true"/>
			</fieldset>
		</td>
	</tr>
</table>

