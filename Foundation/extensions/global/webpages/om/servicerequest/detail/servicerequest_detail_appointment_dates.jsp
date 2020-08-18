<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>
<%@ page import="com.yantra.yfc.date.*" %>

<table >
	<tr>
		<td width="35%">
			<table cellspacing="0" cellpadding="0" border="0" width="100%" class="view">
				<tr>

					<td class="detaillabel"><yfc:i18n>Currently_Chosen_Appointment_Slot</yfc:i18n></td>
				<%	String currentAppointmentString = null;
						if (isVoid(getValue("OrderLine","xml:/OrderLine/@PromisedApptEndDate"))) {	
							currentAppointmentString = displayTimeWindow(getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptStartTimestamp"),getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptEndTimestamp"),getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone"));
						%>
							<input type="hidden" class="dateinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptStartTimestamp") %> />
							<input type="hidden" class="dateinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Assignments/Assignment/@ApptEndTimestamp") %> />
							<input type="hidden" class="dateinput" <%=getTextOptions("xml:/WorkOrder/@WorkOrderKey","xml:/Promise/@WorkOrderKey") %> />
				<%	}	else	{	
							currentAppointmentString = displayTimeWindow(getValue("OrderLine", "xml:/OrderLine/@PromisedApptStartDate"),getValue("OrderLine", "xml:/OrderLine/@PromisedApptEndDate"),getValue("Promise", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone"));%>
							<input type="hidden" class="dateinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@PromisedApptStartDate", "xml:/OrderLine/@PromisedApptStartDate") %> />
							<input type="hidden" class="dateinput" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/@PromisedApptEndDate", "xml:/OrderLine/@PromisedApptEndDate")%> />
				<%	}%>
						<input type="hidden" <%=getTextOptions("xml:/dummy/@DBStartDate","xml:/OrderLine/@PromisedApptStartDate") %> />
						<input type="hidden" <%=getTextOptions("xml:/dummy/@DBEndDate", "xml:/OrderLine/@PromisedApptEndDate") %> />
						<input type="hidden" <%=getTextOptions("xml:/dummy/@TimeZone", "xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone") %> />
						<input type="hidden" <%=getTextOptions("xml:/WorkOrder/@IsRoutePlanningComplete", "N")%>/>

					<td class="protectedtext" > 
						<input name="displayAppointmentDate" size="40" maxLength="40" type="text" class="protectedinput" value="<%=currentAppointmentString%>" />		
						<%-- cr 34417 --%>
						<% if(!isVoid(currentAppointmentString) ) {  %>
							<%=showTimeZoneIcon( resolveValue("xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone"), getLocale().getTimezone())%>
						<% } %>
					</td>

				</tr>

			</table>
		</td>

		<td width="65%" height="100%">
			<fieldset>
			<table cellspacing="0" cellpadding="0" border="0" width="100%" class="view">
				<tr>

					<td class="detaillabel"><yfc:i18n>Start_Date</yfc:i18n></td>
					<td class="protectedtext">
						<input type="text" class="protectedinput" <%=getTextOptions("xml:/Promise/Overrides/@ReqDeliveryDate","xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@DeliveryStartSearchDate")%>/><img class="lookupicon" onclick="invokeCalendar(this)" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar")%>/>
					</td>
					
					<td class="detaillabel" ><yfc:i18n>Number_Of_Days_To_Consider</yfc:i18n></td>
					<td nowrap="true">
						<%	String inBinding = null;
							if(equals(resolveValue("xml:/OrderLine/@ItemGroupCode"), "PROD") )
								inBinding = "xml:/Promise/@ProductSearchWindow";
							else
								inBinding = "xml:/Promise/@ServiceSearchWindow";
						%>

						<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/@DelayWindow", inBinding) %> />
						<%	if(!(equals("Y", getParameter("WorkOrderAppointment") ) && isTrue("xml:/WorkOrder/@IsMultiApptRequired") ) )	{	%>
							<input type="button" class="button" value="<%=getI18N("GO")%>" onclick="if(validateControlValues())yfcChangeDetailView(getCurrentViewId())"/>
						<%	}	%>
					</td>
				</tr>
				<%	if(equals("Y", getParameter("WorkOrderAppointment") ) && isTrue("xml:/WorkOrder/@IsMultiApptRequired") )	{	%>
					<tr>
						<td class="detaillabel" ><yfc:i18n>Requested_Capacity</yfc:i18n></td>
						<td nowrap="true">
						<%
							String sourceBinding = "xml:/Promise/@RequestedCapacity";
							if(equals("Y", getParameter("AppointmentFound") ) && getNumericValue(sourceBinding) <= 0 )	{
								sourceBinding = "xml:/OrderLine/@RequestedQuantity";
							}
						%>
							<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/@RequestedCapacity", sourceBinding) %> />
						</td>
						<td>
							<input type="button" class="button" value="<%=getI18N("GO")%>" onclick="if(validateControlValues())yfcChangeDetailView(getCurrentViewId())"/>
						</td>
					</tr>
				<%	}	%>
				</tr>
			</table>
			</fieldset>
		</td>

	<%	if(!equals("Y", getParameter("WorkOrderAppointment") ) || equals("Y", getParameter("HasAnInteraction") ) )	{	%>
		<tr>
			<td width="100%" colspan="6" >
				<jsp:include page="/om/servicerequest/detail/servicerequest_detail_appointment_slotlist.jsp" flush="true">
					<jsp:param name="displayAppointmentDate" value='<%=currentAppointmentString%>'/>
				</jsp:include>
			</td>
		</tr>
	<%	}	%>
	</tr>
</table>

