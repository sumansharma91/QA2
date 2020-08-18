<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
	
<%
	String toTz = resolveValue("xml:LineConstraints:/OrderLine/@Timezone");
	if(isVoid(toTz) )
		toTz = YFCLocale.getDefaultLocale().getTimezone();

	String sLocale = getI18N("Timezone_Message_Format");
	sLocale=replaceString(sLocale,"{0}", toTz);

	if(!equals(toTz, getLocale().getTimezone() ) )
	{
%>
		<table>
			<tr>
				<td class="protectedtext">
					<%=sLocale%>
				</td>
			</tr>
		</table>
<%	} %>
<table  editable="false" width="100%" class="table" cellspacing="0" cellpadding="0">

	<thead>
		<tr>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("PrimeLineNo")%>">
				<yfc:i18n>Constraining_Line_#</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ItemID")%>">
				<yfc:i18n>Item_ID</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("UnitOfMeasure")%>">
				<yfc:i18n>UOM</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ItemDesc")%>">
				<yfc:i18n>Description</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ItemID")%>">
				<yfc:i18n>Constraint_Type</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ConstraintDate")%>">
				<yfc:i18n>Constraint_Date</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ConstraintReason")%>">
				<yfc:i18n>Reason</yfc:i18n>
			</td>
			<td class="tablecolumnheader" sortable="yes" nowrap="true" style="width:<%=getUITableSize("ConstrainingDate")%>">
				<yfc:i18n>Constraining_Date</yfc:i18n>
			</td>
		</tr>
	</thead>
	<tbody>
		<yfc:loopXML binding="xml:LineConstraints:/OrderLine/Constraints/CannotScheduleBeforeConstraints/@Constraint" id="Constraint">
			<yfc:makeXMLInput  name="orderLineKey"  >
				<yfc:makeXMLKey  value="xml:/Constraint/OrderLine/@OrderLineKey" binding="xml:/OrderLineDetail/@OrderLineKey" />
				<yfc:makeXMLKey  value="xml:LineConstraints:/OrderLine/@OrderHeaderKey" binding="xml:/OrderLineDetail/@OrderHeaderKey" />
			</yfc:makeXMLInput>
			<tr>
				<td class="tablecolumn">
				<%	String linkDependingOnItemGroupCode = null;
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PROD")) 
							linkDependingOnItemGroupCode = "L02";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PS")) 
							linkDependingOnItemGroupCode = "L01";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"DS")) 
							linkDependingOnItemGroupCode = "L03";
				%>
					<a   <%=getDetailHrefOptions(linkDependingOnItemGroupCode,getParameter("orderLineKey"),"")%>   >
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/@PrimeLineNo" />
					</a>
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemID" /> 
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@UnitOfMeasure" />
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemDesc" />
				</td>
				<td>
						<yfc:i18n>Cannot_Schedule_Before</yfc:i18n>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstraintDate") )%>
				</td>
				<td class="tablecolumn">
					<%	String constraintReasonText=getValue("Constraint", "xml:/Constraint/@ConstraintReason");	%>
					<%=getI18N(constraintReasonText)%>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstrainingDate") )%>
				</td>
			</tr>
		</yfc:loopXML>
		<yfc:loopXML binding="xml:LineConstraints:/OrderLine/Constraints/CannotScheduleAfterConstraints/@Constraint" id="Constraint">
			<yfc:makeXMLInput  name="orderLineKey"  >
				<yfc:makeXMLKey  value="xml:/Constraint/OrderLine/@OrderLineKey" binding="xml:/OrderLineDetail/@OrderLineKey" />
				<yfc:makeXMLKey  value="xml:LineConstraints:/OrderLine/@OrderHeaderKey" binding="xml:/OrderLineDetail/@OrderHeaderKey" />
			</yfc:makeXMLInput>
			<tr>
				<td class="tablecolumn">
				<%	String linkDependingOnItemGroupCode = null;
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PROD")) 
							linkDependingOnItemGroupCode = "L02";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PS")) 
							linkDependingOnItemGroupCode = "L01";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"DS")) 
							linkDependingOnItemGroupCode = "L03";
				%>
					<a   <%=getDetailHrefOptions(linkDependingOnItemGroupCode,getParameter("orderLineKey"),"")%>   >
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/@PrimeLineNo" />
					</a>
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemID" /> 
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@UnitOfMeasure" />
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemDesc" />
				</td>
				<td>
					<yfc:i18n>Cannot_Schedule_After</yfc:i18n>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstraintDate") )%>
				</td>
				<td class="tablecolumn">
					<%	String constraintReasonText=getValue("Constraint", "xml:/Constraint/@ConstraintReason");	%>
					<%=getI18N(constraintReasonText)%>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstrainingDate") )%>
				</td>
			</tr>
		</yfc:loopXML>
		<yfc:loopXML binding="xml:LineConstraints:/OrderLine/Constraints/CannotScheduleConstraints/@Constraint" id="Constraint">
			<yfc:makeXMLInput  name="orderLineKey"  >
				<yfc:makeXMLKey  value="xml:/Constraint/OrderLine/@OrderLineKey" binding="xml:/OrderLineDetail/@OrderLineKey" />
				<yfc:makeXMLKey  value="xml:LineConstraints:/OrderLine/@OrderHeaderKey" binding="xml:/OrderLineDetail/@OrderHeaderKey" />
			</yfc:makeXMLInput>
			<tr>
				<td class="tablecolumn">
				<%	String linkDependingOnItemGroupCode = null;
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PROD")) 
							linkDependingOnItemGroupCode = "L02";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"PS")) 
							linkDependingOnItemGroupCode = "L01";							
						if (equals(getValue("Constraint","xml:/Constraint/OrderLine/@ItemGroupCode"),"DS")) 
							linkDependingOnItemGroupCode = "L03";
				%>
					<a   <%=getDetailHrefOptions(linkDependingOnItemGroupCode,getParameter("orderLineKey"),"")%>   >
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/@PrimeLineNo" />
					</a>
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemID" /> 
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@UnitOfMeasure" />
				</td>
				<td class="tablecolumn">
						<yfc:getXMLValue  binding="xml:/Constraint/OrderLine/Item/@ItemDesc" />
				</td>
				<td>
						<yfc:i18n>Cannot_Schedule</yfc:i18n>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstraintDate") )%>
				</td>
				<td class="tablecolumn">
					<%	String constraintReasonText=getValue("Constraint", "xml:/Constraint/@ConstraintReason");	%>
					<%=getI18N(constraintReasonText)%>
				</td>
				<td class="tablecolumn">
					<%=convertUserTimezoneDateIntoTargetTimezoneDate( toTz, resolveValue("xml:/Constraint/@ConstrainingDate") )%>
				</td>
			</tr>
		</yfc:loopXML>
	</tbody>
</table>
