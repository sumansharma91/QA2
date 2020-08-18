<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ page import="com.yantra.yfs.ui.backend.*" %>
<%@include file="/console/jsp/order.jspf" %>
<%@ include file="/console/jsp/paymentutils.jspf" %>
<%  
	YFCDate oEndDate = new YFCDate(); 
	oEndDate.setEndOfDay();
%>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>


<table class="view">
    <tr>
        <td>
		    <input type="hidden" name="xml:/Order/@OrderDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@ReqDeliveryDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@ReqShipDateQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@StatusQryType" value="BETWEEN"/>
            <input type="hidden" name="xml:/Order/@DraftOrderFlag" value="N"/>

			<input type="hidden" name="xml:/Order/OrderHoldType/@Status" value=""/>
			<input type="hidden" name="xml:/Order/OrderHoldType/@StatusQryType" value="" />
        </td>
    </tr>

   
    <% // Now call the APIs that are dependent on the common fields (Doc Type & Enterprise Code) %>
    <yfc:callAPI apiID="AP2"/>
    <yfc:callAPI apiID="AP4"/>
    <yfc:callAPI apiID="AP5"/>
	<yfc:callAPI apiID="AP8"/>

    <%
		if(!isTrue("xml:/Rules/@RuleSetValue") )	{
    %>
			<yfc:callAPI apiID="AP7"/>
    
	<%
			YFCElement listElement = (YFCElement)request.getAttribute("HoldTypeList");

			YFCDocument document = listElement.getOwnerDocument();
			YFCElement newElement = document.createElement("HoldType");

			newElement.setAttribute("HoldType", " ");
			newElement.setAttribute("HoldTypeDescription", getI18N("All_Held_Orders"));

			YFCElement eFirst = listElement.getFirstChildElement();
			if(eFirst != null)	{
				listElement.insertBefore(newElement, eFirst);
			}	else	{
				listElement.appendChild(newElement);
			}
			request.setAttribute("defaultHoldType", newElement);
		}

        //Remove Statuses 'Draft Order Created' and 'Held' from the Status Search Combobox.
		prepareOrderSearchByStatusElement((YFCElement) request.getAttribute("StatusList"));
		
		
    %>
     <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order Number</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@OrderNoQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@OrderNoQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@OrderNo")%>/>
        </td>
    </tr>
	
	<tr>
      
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            
             <%
			                                   String DocumentType ="0001";
											   
                                                if(DocumentType!=null){
                                                %>
                                                <input type="hidden" name="xml:/Order/@DocumentType" value="<%=DocumentType%>"/>
                                                <%
                                                                } else {
                                                %>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@DocumentType")%> />
                                                <%}
                                                %>
        </td>
    </tr>
	
	
	
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>First Name</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@CustomerFirstNameQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@CustomerFirstNameQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@CustomerFirstName")%>/>
        </td>
    </tr>
	
	
	
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Last Name</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@CustomerLastNameQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/@CustomerLastNameQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@CustomerLastName")%>/>
        </td>
    </tr>
	
	
	
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            
			 <%
			                                   String ESEnterpriseCode ="VSI.com";
											   
                                                if(ESEnterpriseCode!=null){
                                                %>
                                                <input type="hidden" name="xml:/Order/@EnterpriseCode" value="<%=ESEnterpriseCode%>"/>
                                                <%
                                                                } else {
                                                %>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@EnterpriseCode")%> />
                                                <%}
                                                %>
        </td>
    </tr>
	
	
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Customer Id</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@BillToIDQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/BillToIDQryType"/>
            </select>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/@BillToID")%>/>
        </td>
    </tr>
			
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>PickUp Store</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
		<select name="xml:/Order/OrderLine/@ShipNodeQryType" class="combobox">
                <yfc:loopOptions binding="xml:/QueryTypeList/StringQueryTypes/@QueryType" name="QueryTypeDesc"
                value="QueryType" selected="xml:/Order/OrderLine/ShipNodeQryType"/>
            </select>
		
            
             <%
			                                   String sellingStore = request.getParameter("sellingStoreId");
											   
                                                if(sellingStore!=null){
                                                %>
                                                <input type="text" name="xml:/Order/OrderLine/@ShipNode" value="<%=sellingStore%>"/>
                                               
            
                                                <%}
												else
												{%>
            <input type="text" class="unprotectedinput" <%=getTextOptions("xml:/Order/OrderLine/@ShipNode")%>/>
			<% }%>

												
                                                
        </td>
    </tr>
	           
    <tr>
        <td class="searchlabel" >
            <yfc:i18n>Order Status</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <select name="xml:/Order/@FromStatus" class="combobox">
                <yfc:loopOptions binding="xml:/ESStatusList/@Status" name="Description"
                value="Status" selected="xml:/Order/@FromStatus" isLocalized="Y"/>
            </select>
            <span class="searchlabel" ><yfc:i18n>To</yfc:i18n></span>
        </td>
    </tr>
    <tr>
        <td nowrap="true" class="searchcriteriacell">
            <select name="xml:/Order/@ToStatus" class="combobox">
                <yfc:loopOptions binding="xml:/ESStatusList/@Status" name="Description"
                value="Status" selected="xml:/Order/@ToStatus" isLocalized="Y"/>
            </select>
        </td>
    </tr>
    <tr>
   		
	<tr>
        <td class="searchlabel" >
            <yfc:i18n>Start Date</yfc:i18n>
        </td>
    </tr>
    <tr>
        <td nowrap="true">
            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@FromOrderDate_YFCDATE","xml:/Order/@FromOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
            <input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@FromOrderDate_YFCTIME","xml:/Order/@FromOrderDate_YFCTIME","")%>/>
			</td>
    </tr>
			<tr>
        <td class="searchlabel" >
            <yfc:i18n>End Date</yfc:i18n>
        </td>
    </tr>
	<tr>
        <td nowrap="true">
	            <input class="dateinput" type="text" <%=getTextOptions("xml:/Order/@ToOrderDate_YFCDATE","xml:/Order/@ToOrderDate_YFCDATE","")%>/>
            <img class="lookupicon" name="search" onclick="invokeCalendar(this);return false" <%=getImageOptions(YFSUIBackendConsts.DATE_LOOKUP_ICON, "Calendar") %> />
			<input class="dateinput" type="hidden" <%=getTextOptions("xml:/Order/@ToOrderDate_YFCTIME","xml:/Order/@ToOrderDate_YFCTIME",oEndDate.getString(getLocale().getTimeFormat()))%>/>
        </td>
    </tr>
	
    
</table>
