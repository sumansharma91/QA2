<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfc.util.*" %>
<table cellspacing="0" cellpadding="0" border="0" width="100%" height="100%" class="view">
	<yfc:makeXMLInput  name="lineConstraintKey"  >
		<yfc:makeXMLKey  value="xml:/OrderLine/@OrderLineKey" binding="xml:/OrderLine/@OrderLineKey" />
		<yfc:makeXMLKey  value="xml:/OrderLine/@OrderHeaderKey" binding="xml:/OrderLine/@OrderHeaderKey" />
	</yfc:makeXMLInput>
<% 	
	String sHasBeforeDate = resolveValue("xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@CannotScheduleBeforeDate");

	String sHasAfterDate = resolveValue("xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/@CannotScheduleAfterDate");

	String toTz = resolveValue("xml:/Promise/SuggestedOption/Option/PromiseServiceLines/PromiseServiceLine/Slots/@TimeZone");
	if(isVoid(toTz) )
		toTz = YFCLocale.getDefaultLocale().getTimezone();

	if(!equals(sHasBeforeDate,"")) 
	{ %>
		<tr>
			<td class="detaillabel"><yfc:i18n>Cannot_Schedule_Before</yfc:i18n></td>
			<td class="protectedtext">
				<a <%=getDetailHrefOptions("L01",getParameter("lineConstraintKey"),"")%> ><%=convertUserTimezoneDateIntoTargetTimezoneDate(toTz, sHasBeforeDate)%></a>
			</td>
		</tr>
<%	
//System.out.println("converting " + sHasBeforeDate + " in timezone " + toTz );
}	else	{	 %>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>	
<%	}

	if(!equals(sHasAfterDate,"")) 
	{	%>
		<tr>
			<td class="detaillabel"><yfc:i18n>Cannot_Schedule_After</yfc:i18n></td>
			<td class="protectedtext" >
				<a <%=getDetailHrefOptions("L01",getParameter("lineConstraintKey"),"")%> ><%=convertUserTimezoneDateIntoTargetTimezoneDate(toTz, sHasAfterDate)%></a>
			</td>
		</tr>
<%	}	else	{	 %>
			<tr>
				<td colspan="2">&nbsp;</td>
			</tr>	
<%	}

		if(equals(sHasAfterDate,"") && equals(sHasBeforeDate,"")) 
		{	%>
		<tr>
			<td class="detaillabel" ><yfc:i18n>No_Constraints_Apply_Currently</yfc:i18n></td>
			<td>&nbsp;</td>
		</tr>
<%	}	%>	
</table>
