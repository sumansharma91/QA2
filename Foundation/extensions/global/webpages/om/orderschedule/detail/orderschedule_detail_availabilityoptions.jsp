<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%	
    // Default some of the search criteria if not already set.
    String optimizationType = resolveValue("xml:/Promise/@OptimizationType");
    if (isVoid(optimizationType)) {
        optimizationType = "03";
    }
%>
<table width="100%" >
<tr>
	<td>
		<table width="33%" >
		<tr>
			<td class="detaillabel" nowrap="true"><yfc:i18n>Optimize_On</yfc:i18n></td>
			<td class="protectedtext" nowrap="true">
				<input type="radio" <%=getRadioOptions("xml:/Promise/@OptimizationType", optimizationType, "01")%>><yfc:i18n>Date</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td class="protectedtext" nowrap="true">
				<input type="radio" <%=getRadioOptions("xml:/Promise/@OptimizationType", optimizationType, "03")%>><yfc:i18n>Number_Of_Shipments</yfc:i18n>
			</td>
		</tr>
		</table>
	</td>
	<td>
		<table width="33%"  >
		<tr>
			<td class="detaillabel" ><yfc:i18n>Delay_Window</yfc:i18n></td>
			<td nowrap="true">
			<input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Promise/@DelayWindow", "xml:/Promise/@ProductSearchWindow" ) %> />	</td>
		</tr>
		<tr>
			<td class="detaillabel" nowrap="true" ><yfc:i18n>Distribution_Group</yfc:i18n></td>	
			<td nowrap="true">
				<select class="combobox" <%=getComboOptions("xml:/Promise/Overrides/@DistributionRuleId")%>>
					<yfc:loopOptions binding="xml:/DistributionRuleList/@DistributionRule" 
						name="Description" value="DistributionRuleId" selected="xml:/Promise/Overrides/@DistributionRuleId"/>
				</select>
			</td>
		</tr>
		</table>
	</td>
	<td>
		<table width="33%" >
		<tr>
			<td class="detaillabel" ><yfc:i18n>ScacAndService</yfc:i18n></td>
			<td>
				<select class="combobox" <%=getComboOptions("xml:/Promise/Overrides/@ScacAndServiceKey")%>>
					<yfc:loopOptions binding="xml:/ScacAndServiceList/@ScacAndService" name="ScacAndServiceDesc"
					value="ScacAndServiceKey" selected="xml:/Promise/Overrides/@ScacAndServiceKey" isLocalized="Y"/>
				</select>
			</td>
		</tr>
		<tr>
			<td>&nbsp;</td>
			<td align="right">
			<input type="button" class="button" value="<%=getI18N("Search")%>"  onclick="if(validateControlValues())yfcChangeDetailView(getCurrentViewId())"/>
			</td>
		</tr>
		</table>
	</td>
</tr>
</table>
