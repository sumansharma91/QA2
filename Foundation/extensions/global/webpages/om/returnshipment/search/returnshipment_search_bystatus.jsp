<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js"></script>

<table class="view">
<jsp:include page="/yfsjspcommon/common_fields.jsp" flush="true">
  <jsp:param name="ShowDocumentType" value="true"/>
   <jsp:param name="RefreshOnDocumentType" value="true"/>
  <jsp:param name="DocumentTypeBinding" value="xml:/Shipment/@DocumentType"/>
  <jsp:param name="EnterpriseCodeBinding" value="xml:/Shipment/@EnterpriseCode"/>
  <jsp:param name="RefreshOnEnterpriseCode" value="true"/>
 </jsp:include>
<% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP3"/>
   
    <%
		String docType = resolveValue("xml:CommonFields:/CommonFields/@DocumentType");
		
		if(!isTrue("xml:/Rules/@RuleSetValue") )	{
    %>
			<yfc:callAPI apiID="AP4"/>
    
	<%
			YFCElement listElement = (YFCElement)request.getAttribute("HoldTypeList");

			YFCDocument document = listElement.getOwnerDocument();
			YFCElement newElement = document.createElement("HoldType");

			newElement.setAttribute("HoldType", " ");
			newElement.setAttribute("HoldTypeDescription", getI18N("All_Held_Shipments"));

			YFCElement eFirst = listElement.getFirstChildElement();
			if(eFirst != null)	{
				listElement.insertBefore(newElement, eFirst);
			}	else	{
				listElement.appendChild(newElement);
			}
			request.setAttribute("defaultHoldType", newElement);
		}

        //Remove Statuses 'Draft Order Created' and 'Held' from the Status Search Combobox.
		//prepareOrderSearchByStatusElement((YFCElement) request.getAttribute("StatusList"));
    %>

  <yfc:callAPI apiID="AP2"/>
  <tr> 
        <td>
            <input type="hidden" name="xml:/Shipment/@StatusQryType" value="BETWEEN"/>
			
			<input type="hidden" name="xml:/Shipment/ShipmentHoldType/@Status" value=""/>
			<input type="hidden" name="xml:/Shipment/ShipmentHoldType/@StatusQryType" value="" />
        </td>
    </tr>    
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Shipment_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/@ShipmentNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/@ShipmentNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipmentNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNoQryType"/>
            </select>
            <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Shipment/ShipmentLines/ShipmentLine/@OrderNo")%>/>
        </td>
    </tr>

    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Plan_#</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/DeliveryPlan/@DeliveryPlanNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/DeliveryPlan/@DeliveryPlanNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/DeliveryPlan/@DeliveryPlanNo")%>/>
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Origin_Node</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/@ShipNodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/@ShipNodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ShipNode")%>/>
	    <img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Ship_Node") %> />
        </td>
    </tr>
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Destination_Node</yfc:i18n>
        </td>
    </tr>
 	<tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/@ReceivingNodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Shipment/@ReceivingNodeQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Shipment/@ReceivingNode")%>/>
            <img class="lookupicon" onclick="callLookup(this,'shipnode')" <%=getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Receiving_Node") %> />
        </td>
    </tr>
 	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Status</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <select name="xml:/Shipment/@FromStatus" class="combobox">
                <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/Shipment/@FromStatus" isLocalized="Y"/>
            </select>
            <span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Shipment/@ToStatus" class="combobox">
                <yfc:loopOptions binding="xml:/StatusList/@Status" name="Description" value="Status" selected="xml:/Shipment/@ToStatus" isLocalized="Y"/>
            </select>
        </td>
    </tr>
	<td class="searchcriteriacell">
		<input type="checkbox" <%=getCheckBoxOptions("xml:/Shipment/@ShipmentClosedFlag", "xml:/Shipment/@ShipmentClosedFlag", "Y")%>><yfc:i18n>Closed_Shipments_Only</yfc:i18n></input>	
    </td>
	<%	if(isTrue("xml:/Rules/@RuleSetValue") )	{	%>
		<tr>
			<td class="searchcriteriacell">
				<input type="checkbox" onclick="manageHoldOpts(this)" <%=getCheckBoxOptions("xml:/Shipment/@HoldFlag", "xml:/Shipment/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' ><yfc:i18n>Held_Shipments</yfc:i18n></input>
			</td>
		</tr>    
		<tr>
			<td class="searchlabel" >
				<yfc:i18n>Hold_Reason_Code</yfc:i18n>
			</td>
		</tr>
		<tr>
			<td class="searchcriteriacell">
				<select name="xml:/Shipment/@HoldReasonCode" class="combobox">
					<yfc:loopOptions binding="xml:HoldReasonCodeList:/CommonCodeList/@CommonCode" name="CodeShortDescription"
					value="CodeValue" selected="xml:/Shipment/@HoldReasonCode" isLocalized="Y"/>
				</select>
			</td>
		</tr>
	<%	}	else	{	%>
		<tr>
			<td class="searchcriteriacell">
				<input type="checkbox" <%=getCheckBoxOptions("xml:/Shipment/@HoldFlag", "xml:/Shipment/@HoldFlag", "Y")%> yfcCheckedValue='Y' yfcUnCheckedValue=' ' onclick="manageHoldOpts(this)" ><yfc:i18n>Held_Shipments_With_Hold_Type</yfc:i18n></input>
			</td>
		</tr>
		<tr>
	        <td class="searchcriteriacell">
				<select resetName="<%=getI18N("All_Held_Shipments")%>" onchange="resetObjName(this, 'xml:/Shipment/ShipmentHoldType/@HoldType')" name="xml:/Shipment/ShipmentHoldType/@HoldType" class="combobox" <%if(isTrue("xml:/Shipment/@HoldFlag") ) {%> ENABLED <%} else {%> disabled="true" <%}%> >
					<yfc:loopOptions binding="xml:/HoldTypeList/@HoldType" name="HoldTypeDescription" value="HoldType" suppressBlank="Y" selected="xml:/Shipment/ShipmentHoldType/@HoldType" isLocalized="Y"/>
				</select>

			</td>
		</tr>
	<%	}	%>


</table>
