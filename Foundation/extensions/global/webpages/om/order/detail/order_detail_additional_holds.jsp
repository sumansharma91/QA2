<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ page import="com.yantra.yfc.dom.YFCElement" %>

<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<script language="javascript">
	function setEnableReasonText(obj, sReasonID)	{
		var oReasonID = document.all(sReasonID);
		oReasonID.disabled=(obj.value == '');
		if(obj.value == '')	{
			oReasonID.value='';
		}
	}

</script>

<%
	String sComboBinding = "xml:/AvailableHolds/@HoldType";

	String sAllowModElementName = "xml:/Order/AllowedModifications";
	String sOverride = "xml:/Order/@Override";
	String sDefaultTemplateRootName = "Order";
	String sDefaultTemplateHoldTypeNodeName = "OrderHoldTypes";
	String sComboName = "xml:/Order/OrderHoldTypes/OrderHoldType/@HoldType";
	String sReasonTextBinding = "xml:/Order/OrderHoldTypes/OrderHoldType/@ReasonText";

	String sForWorkOrder = getParameter("ForWorkOrder");
	if(YFCCommon.equals("Y", sForWorkOrder) )	{
		sAllowModElementName = "xml:/WorkOrder/AllowedModifications";
		sDefaultTemplateRootName = "WorkOrder";
		sDefaultTemplateHoldTypeNodeName = "WorkOrderHoldTypes";
		sComboName = "xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@HoldType";
		sReasonTextBinding = "xml:/WorkOrder/WorkOrderHoldTypes/WorkOrderHoldType/@ReasonText";
		sOverride = "xml:/WorkOrder/@OverrideModificationRules";
	}

	YFCElement eAdditionalHolds = formAdditionalHoldsXML(getElement(sDefaultTemplateRootName).getChildElement(sDefaultTemplateHoldTypeNodeName), getElement("HoldTypeList"), getElement("HoldTypeCommonCodeList"), sForWorkOrder );


	request.setAttribute("AvailableHolds", eAdditionalHolds);
%>

<table class="view" cellspacing="0" width="100%" ID="specialChange">

        <tr>
            <td class="detaillabel" ><yfc:i18n>Hold_Type</yfc:i18n></td>
			<td   >
	            <input type="hidden" name='<%=sOverride%>' value="" />
				
				<select name="<%=sComboName%>" class="combobox" OldValue="" <%=getModTypeComboOptions(sComboName, sAllowModElementName)%> onChange="setEnableReasonText(this, '<%=sReasonTextBinding%>');" >
					<yfc:loopOptions binding="<%=sComboBinding%>" name="HoldTypeDescription"
					value="HoldType" selected=" " isLocalized="Y" />
				</select>
			</td>
			<td></td>
        </tr>

		<tr>
            <td class="detaillabel" ><yfc:i18n>Reason</yfc:i18n></td>

			<td  >
				<textarea rows="2" disabled cols="50" <%=getModTypeTextAreaOptions(sReasonTextBinding, sAllowModElementName, "HOLD_TYPE")%> <%=getTextAreaOptions(sReasonTextBinding )%>></textarea>
			</td>
			<td></td>
		</tr>

</table>
