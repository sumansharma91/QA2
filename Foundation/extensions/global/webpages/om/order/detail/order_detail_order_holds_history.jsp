<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>

<%
	String sPassedHoldType = getParameter("HoldType");
	String sForWorkOrder = getParameter("ForWorkOrder");

	String sLoopName = "OrderHistory";
	String sLoopBinding = "xml:OrderHistory:/Order/OrderHoldTypes/@OrderHoldType";
	String sID = "OrderHoldType";

	String sInnerLoopName = "OrderHoldType";
	String sHoldTypeLog = "xml:HoldType:/OrderHoldType/OrderHoldTypeLogs/@OrderHoldTypeLog";
	String sLogID = "OrderHoldTypeLog";

	if(YFCCommon.equals("Y", sForWorkOrder) )	{
		sLoopName = "WorkOrderHistory";
		sLoopBinding = "xml:WorkOrderHistory:/WorkOrder/WorkOrderHoldTypes/@WorkOrderHoldType";
		sID = "WorkOrderHoldType";

		sInnerLoopName = "WorkOrderHoldType";
		sHoldTypeLog = "xml:HoldType:/WorkOrderHoldType/WorkOrderHoldTypeLogs/@WorkOrderHoldTypeLog";
		sLogID = "WorkOrderHoldTypeLog";
		
	}


	String sCreatetsBinding = "xml:HoldTypeLog:/OrderHoldTypeLog/@Createts";
	String sAuditKey = "xml:HoldTypeLog:/OrderHoldTypeLog/@OrderAuditKey";
	String sUserId = "xml:HoldTypeLog:/OrderHoldTypeLog/@UserId";
	String sStatus = "xml:HoldTypeLog:/OrderHoldTypeLog/@Status";
	String sReasonText = "xml:HoldTypeLog:/OrderHoldTypeLog/@ReasonText";
	String sTransactionId = "xml:HoldTypeLog:/OrderHoldTypeLog/@TransactionId";

	boolean bForWorkOrder = YFCCommon.equals("Y", sForWorkOrder);
	if( bForWorkOrder )	{
		sCreatetsBinding = "xml:HoldTypeLog:/WorkOrderHoldTypeLog/@Createts";
		sUserId = "xml:HoldTypeLog:/WorkOrderHoldTypeLog/@UserId";
		sStatus = "xml:HoldTypeLog:/WorkOrderHoldTypeLog/@Status";
		sReasonText = "xml:HoldTypeLog:/WorkOrderHoldTypeLog/@ReasonText";
		sTransactionId = "xml:HoldTypeLog:/WorkOrderHoldTypeLog/@TransactionId";
	}
%>

<table class="view" width="20%">
<tr>
    <td class="detaillabel" nowrap="true" ><yfc:i18n>Hold_Type</yfc:i18n></td>
	<td class="protectedtext" >
		 <%=getComboText("xml:/HoldTypeList/@HoldType" ,"HoldTypeDescription" ,"HoldType" , sPassedHoldType, true)%>
	</td>
</tr>
</table>

<table class="table" cellspacing="0" width="100%" ID="specialChange">
    <thead>
        <tr>
            <td class="tablecolumnheader" ><yfc:i18n>Date/Time</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>User_ID</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Comment</yfc:i18n></td>
            <td class="tablecolumnheader" ><yfc:i18n>Hold_Transaction</yfc:i18n></td>
			<%	if(!bForWorkOrder)	{	%>
	            <td class="tablecolumnheader" ><yfc:i18n>Audit</yfc:i18n></td>
			<%	}	%>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML name="<%=sLoopName%>" binding="<%=sLoopBinding%>" id="HoldType">
			<%	
			if(YFCCommon.equals(sPassedHoldType, resolveValue("xml:HoldType:/" + sID + "/@HoldType") ) )	{	

				// first sort the element based on createts
				YFCElement eHoldType = getElement("HoldType");
				request.setAttribute("CurrentHistoryRecord", eHoldType);

				YFCElement eHoldTypeLogs = eHoldType.getChildElement("OrderHoldTypeLogs");
				if(YFCCommon.equals("Y", sForWorkOrder) )	{
					eHoldTypeLogs = eHoldType.getChildElement("WorkOrderHoldTypeLogs");
				}
				eHoldTypeLogs.sortChildren(new String[]{"Createts"} );

			%>
		        <yfc:loopXML name="HoldType" binding="<%=sHoldTypeLog%>" id="HoldTypeLog">
				<tr>
					<td class="tablecolumn" >
						<yfc:getXMLValue binding='<%=sCreatetsBinding%>'/>
					</td>
					<td class="tablecolumn" >
						<yfc:getXMLValue binding='<%=sUserId%>'/>
					</td>
					<td class="tablecolumn" >
						 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription", "CodeValue", sStatus, true)%>
					</td>
					<td class="tablecolumn" >
						<yfc:getXMLValue binding='<%=sReasonText%>'/>
					</td>
					<td class="tablecolumn" >
						 <%=getComboText("xml:/TransactionList/@Transaction" ,"Tranname", "Tranid", sTransactionId, true)%>
					</td>
					<%	if(!bForWorkOrder)	{	%>
				
						<td class="tablecolumn" >
							<%	if(!isVoid(resolveValue(sAuditKey) ) )	{	%>
									<yfc:makeXMLInput name="auditKey" >
										<yfc:makeXMLKey binding="xml:/OrderAudit/@OrderAuditKey" value="<%=sAuditKey%>" />
										<yfc:makeXMLKey binding="xml:/OrderAudit/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
									</yfc:makeXMLInput>
									<a <%=getDetailHrefOptions("L01", getParameter("auditKey"), "")%> ><yfc:i18n>View_Audit</yfc:i18n></a>
							<%	}	%>
						</td>

					<%	}	%>
				</tr>
	        </yfc:loopXML>
			<%	}	%>
        </yfc:loopXML>

<%
	if(YFCCommon.equals("Y", sForWorkOrder) )	{
		sCreatetsBinding = "xml:CurrentHistoryRecord:/WorkOrderHoldType/@Modifyts";
		sUserId = "xml:CurrentHistoryRecord:/WorkOrderHoldType/@Modifyuserid";
		sStatus = "xml:CurrentHistoryRecord:/WorkOrderHoldType/@Status";
		sReasonText = "xml:CurrentHistoryRecord:/WorkOrderHoldType/@ReasonText";
		sTransactionId = "xml:CurrentHistoryRecord:/WorkOrderHoldType/@TransactionId";
	}	else	{
		sCreatetsBinding = "xml:CurrentHistoryRecord:/OrderHoldType/@Modifyts";
		sAuditKey = "xml:CurrentHistoryRecord:/OrderHoldType/@OrderAuditKey";
		sUserId = "xml:CurrentHistoryRecord:/OrderHoldType/@Modifyuserid";
		sStatus = "xml:CurrentHistoryRecord:/OrderHoldType/@Status";
		sReasonText = "xml:CurrentHistoryRecord:/OrderHoldType/@ReasonText";
		sTransactionId = "xml:CurrentHistoryRecord:/OrderHoldType/@TransactionId";
	}
%>
		<tr>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding='<%=sCreatetsBinding%>'/>
			</td>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding='<%=sUserId%>'/>
			</td>
			<td class="tablecolumn" >
				 <%=getComboText("xml:HoldStatus:/CommonCodeList/@CommonCode" ,"CodeShortDescription", "CodeValue", sStatus, true)%>
			</td>
			<td class="tablecolumn" >
				<yfc:getXMLValue binding='<%=sReasonText%>'/>
			</td>
			<td class="tablecolumn" >
				 <%=getComboText("xml:/TransactionList/@Transaction" ,"Tranname", "Tranid", sTransactionId, true)%>
			</td>
			<%	if(!bForWorkOrder)	{	%>
					<td class="tablecolumn" >
						<%	if(!isVoid(resolveValue(sAuditKey) ) )	{	%>
								<yfc:makeXMLInput name="auditKey" >
									<yfc:makeXMLKey binding="xml:/OrderAudit/@OrderAuditKey" value="<%=sAuditKey%>" />
									<yfc:makeXMLKey binding="xml:/OrderAudit/@OrderHeaderKey" value="xml:/Order/@OrderHeaderKey" />
								</yfc:makeXMLInput>
								<a <%=getDetailHrefOptions("L01", getParameter("auditKey"), "")%> ><yfc:i18n>View_Audit</yfc:i18n></a>
						<%	}	%>
					</td>

			<%	}	%>
		</tr>
    </tbody>
</table>
