<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>

<% // Call getWorkOrderDetails to get information about the work order and its appointment
   // if WorkOrderKey is populated on the OrderRelease.
   if (!isVoid(resolveValue("xml:/OrderRelease/@WorkOrderKey"))) {
%>
       <yfc:callAPI apiID="AP1"/>

       <% // If the OrderRelease also has a work order appointment populated, then set the
          // appropriate WorkOrderAppointment element into the request with the 
          // "CurrentWorkOrderAppointment" namespace.
          String appointmentKey = resolveValue("xml:/OrderRelease/@WorkOrderApptKey");
          if (!isVoid(appointmentKey)) {
       %>
              <yfc:loopXML binding="xml:/WorkOrder/WorkOrderAppointments/@WorkOrderAppointment" id="WorkOrderAppointment">
                  <%
                     if (equals(appointmentKey, resolveValue("xml:/WorkOrderAppointment/@WorkOrderApptKey"))) {
                         request.setAttribute("CurrentWorkOrderAppointment", (YFCElement) pageContext.getAttribute("WorkOrderAppointment"));
                     }
                  %>
              </yfc:loopXML>
       <% }
   } %>

<table class="view" width="100%">
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Ship_Node</yfc:i18n>
		</td>
		<td class="protectedtext"><yfc:getXMLValue binding="xml:/OrderRelease/@ShipNode"></yfc:getXMLValue></td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Delivery_Method</yfc:i18n>
		</td>
		<td class="protectedtext">
			<yfc:getXMLValue binding="xml:/OrderRelease/@DeliveryMethod"></yfc:getXMLValue>
		</td>
    </tr>
    <tr>
		<td class="detaillabel" >
			<yfc:i18n>Work_Order_#</yfc:i18n>
		</td>
		<yfc:makeXMLInput name="workOrderKey">
			<yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/OrderRelease/@WorkOrderKey" ></yfc:makeXMLKey>
		</yfc:makeXMLInput>
		<td class="protectedtext">
			<a <%=getDetailHrefOptions("L01", getParameter("workOrderKey"),"")%>><yfc:getXMLValue name="WorkOrder" binding="xml:/WorkOrder/@WorkOrderNo"/></a>
		</td>
	</tr>
	<tr>
		<td class="detaillabel" >
			<yfc:i18n>Appointment</yfc:i18n>
		</td>
		<td class="protectedtext" title='<%=getI18N("Appointment_Sequence")%>:<%=resolveValue("xml:CurrentWorkOrderAppointment:/WorkOrderAppointment/@ApptSeq")%>'>
            <%
                String sTimeWindow = displayTimeWindow(resolveValue("xml:CurrentWorkOrderAppointment:/WorkOrderAppointment/@PromisedApptStartDate"), resolveValue("xml:CurrentWorkOrderAppointment:/WorkOrderAppointment/@PromisedApptEndDate"), resolveValue("xml:/WorkOrder/@Timezone"));
                if (!isVoid(sTimeWindow)) {
            %>
                    <%=sTimeWindow%>
                    <%=showTimeZoneIcon(resolveValue("xml:/WorkOrder/@Timezone"), getLocale().getTimezone())%>
            <%	} %>
		</td>
	</tr>
</table>

