<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%@include file="/console/jsp/paymentutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<% // Since this JSP is used for the order entity as well as the order line entity,
   // the actual XML we will use in each case is different. When used in the order screen,
   // the XML comes directly from the WorkOrders namespace. When used in the order line screen,
   // the XML comes from a child element of the OrderLine namespace. To handle this, we
   // just place the appropriate element into the "WorkOrders" namespace. If the OrderLine
   // namespace is valid, then we assume we are coming from the order line screen.
   YFCElement orderLineElem = (YFCElement) request.getAttribute("OrderLine");
   Object backupWorkOrders = null;
   if (null != orderLineElem) {
       // Make a backup of the current "WorkOrders" namespace (just incase it is already in use)
       backupWorkOrders = request.getAttribute("WorkOrders");
       YFCElement orderLineWorkOrders = orderLineElem.getChildElement("WorkOrders", true);
       request.setAttribute("WorkOrders", orderLineWorkOrders);
   }
%>

<table class="table" cellpadding="0" cellspacing="0" width="100%"  suppressFooter="true">
<tbody>
    <tr>
    <td>
        <input type="button" class="button" value="<%=getI18N("Expand_All")%>" <% if(false){ %> disabled="true" <%}%>  onclick="expandAll('TR','yfsWorkOrderDetails_','<%=replaceI18N("Click_To_Hide_Payment_Info")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>' )" style="cursor:hand" />
        <input type="button" class="button" value="<%=getI18N("Collapse_All")%>"   <% if(false){ %> disabled="true" <%}%> onclick="collapseAll('TR','yfsWorkOrderDetails_','<%=replaceI18N("Click_To_See_Payment_Info")%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>' )" style="cursor:hand"/>
    </td>
    </tr>
</tbody>
</table>

<table class="table" editable="false" width="100%" cellspacing="0">
    <thead> 
        <tr>
            <td sortable="no" class="checkboxheader">
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            </td>
            <td class="tablecolumnheader"><yfc:i18n>Work_Order_#</yfc:i18n></td>
			<td class="tablecolumnheader"><yfc:i18n>Ship_Node</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Status</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Appointment</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Pre_Call_Status</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/WorkOrders/@WorkOrder" id="WorkOrder">
            <% if ((equals("DS", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode"))) || (equals("PS", resolveValue("xml:/WorkOrder/@ServiceItemGroupCode")))) {
            %>
               <% request.setAttribute("WorkOrder", (YFCElement)pageContext.getAttribute("WorkOrder"));%>
                <tr>
                    <yfc:makeXMLInput name="WorkOrderKey">
                        <yfc:makeXMLKey binding="xml:/WorkOrder/@WorkOrderKey" value="xml:/WorkOrder/@WorkOrderKey" />
                    </yfc:makeXMLInput>                
                    <td class="checkboxcolumn">
                        <input type="checkbox" value='<%=getParameter("WorkOrderKey")%>' name="serviceWorkOrderEntityKey" />
                    </td>
					<% String divToDisplay = "yfsWorkOrderDetails_" + WorkOrderCounter; %>
	

                    <td class="tablecolumn">
                        <% if (equals("Y", getValue("WorkOrder", "xml:/WorkOrder/@isHistory"))) { %>
                            <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                        <% } else { %>
                            <a <%=getDetailHrefOptions("L01", getParameter("WorkOrderKey"), "")%>>
                                <yfc:getXMLValue binding="xml:/WorkOrder/@WorkOrderNo"/>
                            </a>
                        <% } %>
						<img id="<%=divToDisplay+"_img"%>" onclick="expandCollapseDetails('<%=divToDisplay%>','<%=replaceI18N("Click_To_See_Work_Order_Lines")%>','<%=replaceI18N("Click_To_Hide_Work_Order_Lines")%>','<%=YFSUIBackendConsts.FOLDER_COLLAPSE%>','<%=YFSUIBackendConsts.FOLDER_EXPAND%>')" style="cursor:hand" <%=getImageOptions(YFSUIBackendConsts.FOLDER_COLLAPSE,"Click_To_See_Work_Order_Lines")%> />
                    </td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@NodeKey"/></td>
                    <td class="tablecolumn"><%=getDBString(getValue("WorkOrder","xml:/WorkOrder/@StatusDescription"))%></td>
                    <td class="tablecolumn">
                        <%  String sTimeWindow = displayTimeWindow(getValue("WorkOrder", "xml:/WorkOrder/@PromisedApptStartDate"), getValue("WorkOrder", "xml:/WorkOrder/@PromisedApptEndDate"), resolveValue("xml:/WorkOrder/@Timezone"));
							
							if (!isVoid(sTimeWindow)) {
						%>
								<%=sTimeWindow%>
								<%=showTimeZoneIcon(resolveValue("xml:/WorkOrder/@Timezone"), getLocale().getTimezone())%>
						<% } %>
                    </td>
                    <td class="tablecolumn"><yfc:getXMLValue binding="xml:/WorkOrder/@PreCallStatusDescription"/></td>
                </tr>

                <tr id='<%=divToDisplay%>' style="display:inline;padding-top:5px;" BypassRowColoring='true'>
                    <td/>
                    <td colspan="5" class="tablecolumn">
                        <table class="table" >
                        <tbody>
                        <tr BypassRowColoring='true'>
                            <td width="4%">&nbsp;</td>

                            <td width="45%" class="tablecolumn">&nbsp;
		                        <fieldset>
				                    <legend>
										<yfc:i18n>Service_Lines</yfc:i18n>
									</legend>
									<jsp:include page="/om/orderline/detail/orderline_detail_wo_servicelines.jsp" flush="true">
										<jsp:param name="PrePathId" value="WorkOrder"/>
										<jsp:param name="ShowAdditionalParams" value="Y"/>
									</jsp:include>
								</fieldset> 
                            </td>                       
                            <td width="1%">&nbsp;</td>
                            <td width="48%" class="tablecolumn">&nbsp;
                                <fieldset>
									<legend>
										<yfc:i18n>Products_Being_Delivered</yfc:i18n>
									</legend>
                                    <jsp:include page="/om/orderline/detail/orderline_detail_wo_prodlines.jsp" flush="true">
										<jsp:param name="PrePathId" value="WorkOrder"/>
                                    </jsp:include>
                                </fieldset>
                            </td>                       
                            <td width="2%">&nbsp;</td>
                        </tr>
						<tr BypassRowColoring='true'>
							<td width="4%">&nbsp;</td>
    						<td width="45%">&nbsp;</td>
							<td width="1%">&nbsp;</td>
							<td width="48%">&nbsp;</td>
							<td width="2%">&nbsp;</td>
						</tr>
						</tbody>
                        </table>
                    </td>
				</tr>
            <% } %>
        </yfc:loopXML>
   </tbody>

   <%
       // Put the backup work orders element back into the "WorkOrders" namespace
       if (null != orderLineElem) {
           request.setAttribute("WorkOrders", backupWorkOrders);
       }
   %>
</table>
