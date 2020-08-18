<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@include file="/console/jsp/modificationutils.jspf"%>
<%@ include file="/console/jsp/order.jspf" %>

<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js"></script>
<script language="javascript" src="<%=request.getContextPath()%>/console/scripts/om.js"></script>
<script language="javascript">
    function window.onload() {
        if (!yfcBodyOnLoad() && (!document.all('YFCDetailError'))) {
            return;
        }       
        <% 	YFCElement orderLineElem = (YFCElement) request.getAttribute("OrderLine");
        	if (orderLineElem.getDoubleAttribute("OrderedQty") == 0) { %>
				window.close();
		<%}%>        
    }
</script>

<%
    prepareSubstitutionStatusList((YFCElement) request.getAttribute("OrderLine"));

    boolean calculated = false;    
    if  (!isVoid(getParameter("xml:/Order/OrderLines/OrderLine/@QuantityToSplit"))) {   
        calculateSubstitutionQty((YFCElement) request.getAttribute("AssociationList"),getParameter("xml:/Order/OrderLines/OrderLine/@QuantityToSplit"));
        calculated = true;
    }
    
    int assocIndex = 0;
%>


<table class="anchor">
<tr>
    <td valign="top">
        <table class="view" width="100%" cols="6">
            <tr>
                <td colspan="1" class="detaillabel" ><yfc:i18n>Status</yfc:i18n></td>
                <td colspan="1" sortable="no">
                    <select class="combobox" <%=getComboOptions("xml:/Order/OrderLines/OrderLine/@FromStatus")%> onchange="setStatusQuantity(this,'<%=resolveValue("xml:/OrderLine/@OrderedQty")%>');">
                        <option value="" statusQty="" selected ></option> 
                        <yfc:loopXML name="OrderLine" binding="xml:/OrderLine/SubstitutionStatusList/@OrderStatus" id="OrderStatus" >
                            <option value='<%=getValue("OrderStatus", "xml:/OrderStatus/@Status")%>' statusQty='<%=getValue("OrderStatus", "xml:/OrderStatus/@StatusQty")%>'><%=getDBString(getValue("OrderStatus","xml:/OrderStatus/@StatusDescription"))%></option>
                        </yfc:loopXML>                                                                  
                    </select>
                </td>                  
                <td colspan="1" class="detaillabel" ><yfc:i18n>Quantity</yfc:i18n></td>
                <td colspan="1" class="protectedtext">
                    <input type="text" class="protectedinput" contenteditable="false" name="StatusQuantity" <%=getTextOptions("StatusQuantity","xml:/OrderLine/@OrderedQty")%>/> 
                </td>
                <td colspan="1" class="detaillabel" ><yfc:i18n>Quantity_To_Substitute</yfc:i18n></td>
                <td colspan="2" class="unprotectednumber">
                    <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/@QuantityToSplit","xml:/AssociationList/@QuantityToSubstitute","xml:/OrderLine/AllowedModifications")%>/>
                    <input type="button" class="button" value="GO" onclick="if(validateControlValues()) updateCurrentView(getCurrentViewId())"/>
                </td>    
                <td colspan="1">&nbsp;</td>
            </tr>
        </table>
    </td>
</tr>
<tr>
    <td valign="top">
        <table width="100%" class="table">
        <thead>
            <tr> 
               <td class="tablecolumnheader" nowrap="true" sortable="no" style="width:30px"><yfc:i18n>&nbsp;</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/Item/@ItemID")%>"><yfc:i18n>Item_ID</yfc:i18n> </td>
                <td class="tablecolumnheader" sortable="no" style="width:<%= getUITableSize("xml:/OrderLine/Item/@ProductClass")%>"><yfc:i18n>PC</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/Item/@UnitOfMeasure")%>"><yfc:i18n>UOM</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/Item/PrimaryInformation/@Description")%>"><yfc:i18n>Description</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/@Priority")%>"><yfc:i18n>Priority</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/@AssociatedQuantity")%>"><yfc:i18n>Associated_Qty</yfc:i18n></td>
                <td class="tablecolumnheader" style="width:<%= getUITableSize("xml:/AssociatedList/Association/@AssociatedQuantity")%>"><yfc:i18n>Substitution_Qty</yfc:i18n></td>
            </tr>            
        </thead>
        <tbody>
            <yfc:loopXML name="AssociationList" binding="xml:/AssociationList/@Association" id="Association">
            	<yfc:loopXML binding="xml:/Association/@Item" id="Item">
            	    <% assocIndex++; %>
	                <yfc:makeXMLInput name="itemKey" >
	                        <yfc:makeXMLKey binding="xml:/InventoryItem/@ItemID" value="xml:/Item/@ItemID" />
	                        <yfc:makeXMLKey binding="xml:/InventoryItem/@UnitOfMeasure" value="xml:/Item/@UnitOfMeasure" />
	                        <yfc:makeXMLKey binding="xml:/InventoryItem/@ProductClass" value="xml:/OrderLine/Item/@ProductClass" />
	                        <yfc:makeXMLKey binding="xml:/InventoryItem/@OrganizationCode" value="xml:/OrderLine/Order/@SellerOrganizationCode" />
							<% if(isShipNodeUser()) { %>
								<yfc:makeXMLKey binding="xml:/InventoryItem/@ShipNode" value="xml:CurrentUser:/User/@Node"/>
							<%}%>
	                </yfc:makeXMLInput>
	                <tr>
	                    <td class="checkboxcolumn"> 
	                        <input type="radio" name="SubstitutionSelect" class="radiobutton" <%if(!calculated) {%> disabled="true" <%}%> value="SubstitutionSelect_<%=assocIndex%>" onclick="setSubstitutionValues('<%=assocIndex%>')"/>
	                    </td>
	                    <td class="tablecolumn">
	                        <a<%=getDetailHrefOptions("L01",getParameter("itemKey"),"")%>>
	                            <yfc:getXMLValue binding="xml:/Item/@ItemID"/>
	                        </a>
	                        <input id="ItemIDInput_<%=assocIndex%>" class="protectedinput" type="hidden" <%=getTextOptions("xml:/Item/@ItemID")%>/>        
	                    </td>
	                    <td class="tablecolumn">
	                        <yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@DefaultProductClass"/>
	                        <input id="ProductClassSelect_<%=assocIndex%>" class="protectedinput" type="hidden" <%=getTextOptions("xml:/Item/PrimaryInformation/@DefaultProductClass")%>/>
	                    </td>                    
	                    <td class="tablecolumn">  
	                        <yfc:getXMLValue binding="xml:/Item/@UnitOfMeasure"/>
	                        <input id="UnitOfMeasureInput_<%=assocIndex%>" class="protectedinput" type="hidden" <%=getTextOptions("xml:/Item/@UnitOfMeasure")%>/>
	                    </td>
	                    <td class="tablecolumn">
	                        <yfc:getXMLValue binding="xml:/Item/PrimaryInformation/@Description"/>
	                    </td>
	                    <td class="numerictablecolumn">
	                        	<yfc:getXMLValue binding="xml:/Association/@Priority"/>
	                    </td>
	                    <td class="numerictablecolumn">
	                        	<yfc:getXMLValue binding="xml:/Association/@AssociatedQuantity"/>
	                    </td>
	                    <td class="numerictablecolumn">
	                        <yfc:getXMLValue binding="xml:/Association/@SubstitutionQuantity"/>
	                        <input id="SubstitutionQtyInput_<%=assocIndex%>" class="numericprotectedinput" type="hidden" <%=getTextOptions("xml:/Association/@SubstitutionQuantity")%>/>                        
	                    </td>				
	                </tr>
                </yfc:loopXML> 
            </yfc:loopXML> 
        </tbody>
        </table>
    </td>
</table>
