<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<%
	String sFutureAvailabilityDateComments="";
	String sInstructionDetailKey="";

	YFCElement orderLineElement = (YFCElement)request.getAttribute("OrderLine");
	YFCElement instructionsElement = orderLineElement.getChildElement("Instructions");

	if (instructionsElement != null)	{
		Iterator instructionsIter = instructionsElement.getChildren();
		YFCElement instructionElement = null;

		while (instructionsIter.hasNext()) {
			instructionElement = (YFCElement)instructionsIter.next();
			if (!(instructionElement.getTagName().equals("Instruction"))) continue;

			if (equals("AVAILABLTY", instructionElement.getAttribute("InstructionType"))) {
				sFutureAvailabilityDateComments = instructionElement.getAttribute("InstructionText");
				sInstructionDetailKey = instructionElement.getAttribute("InstructionDetailKey");
				break;
			}
		}
	}
%>
<table class="view" width="100%">
	<tr>
		<td class="detaillabel" ><yfc:i18n>Future_Availability_Date</yfc:i18n></td>
		<td nowrap="true" >
			<input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@FutureAvailabilityDate", "xml:/OrderLine/@FutureAvailabilityDate", "xml:/OrderLine/AllowedModifications")%> >
			<img class="lookupicon" onclick="invokeCalendar(this);return false" name="search" <%=yfsGetImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar", "xml:/OrderLine/@FutureAvailabilityDate", "xml:/OrderLine/AllowedModifications") %> />&nbsp;
		</td>
		<td/>
	</tr>
        <tr>
			<td class="detaillabel" ><yfc:i18n>Comments</yfc:i18n></td>
            <td>
				
				<textarea class="unprotectedtextareainput" rows="3" cols="80" <%=yfsGetTextAreaOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction/@InstructionText","xml:/OrderLine/Instructions/Instruction/@InstructionText", "xml:/OrderLine/AllowedModifications")%>><%=sFutureAvailabilityDateComments%></textarea>

				<input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction/@InstructionDetailKey", sInstructionDetailKey)%>/>
				<input type="hidden" name="xml:/Order/OrderLines/OrderLine/Instructions/Instruction/@InstructionType" value="AVAILABLTY"/>
            </td>
        </tr>
</table>
