<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<%
	String curAuditLevelKey="";
    String orderAuditLevelKey=getValue("OrderAuditEntity","xml:/OrderAuditEntity/@OrderAuditLevelKey");
    boolean isFirst=true;


%>

<table width="100%" class="view">
    <yfc:loopXML name="OrderAudit" binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel" > 
        <%  curAuditLevelKey= getValue("OrderAuditLevel","xml:/OrderAuditLevel/@OrderAuditLevelKey");
        if(equals(curAuditLevelKey, orderAuditLevelKey) || equals("Y", request.getParameter("IgnoreHoldLevel") ) )	{

			String sOLlink = "L01";
			String sORlink = "L02";
			String sOLRlink = "L01";
			String sItemGrpCode = resolveValue("xml:/OrderAuditLevel/OrderLine/@ItemGroupCode");
			if(equals("DS", sItemGrpCode) )
			{
				sOLlink = "L05";
				sORlink = "L06";
				sOLRlink = "L05";
			}
			else if(equals("PS", sItemGrpCode) )
			{
				sOLlink = "L03";
				sORlink = "L04";
				sOLRlink = "L03";
			}

			String[][] idGlobalArray = {
				{"ORDER_LINE", sOLlink, "OrderLineKey", "Line_#", "xml:/OrderAuditLevel/OrderLine/@PrimeLineNo", "xml:/OrderAuditLevel/OrderLine/@SubLineNo"},
				{"ORDER_RELEASE", sORlink, "OrderReleaseKey", "Release_#", "xml:/OrderAuditLevel/OrderRelease/@ReleaseNo", ""}, 
				{"ORDER_RELEASE_LINE", sOLRlink, "OrderLineKey", "Line_#", "xml:/OrderAuditLevel/OrderLine/@PrimeLineNo", "xml:/OrderAuditLevel/OrderLine/@SubLineNo"}
			};
 

			String modificationLevel=getValue("OrderAuditLevel","xml:/OrderAuditLevel/@ModificationLevel");
            String[] idArray=null;
            for(int i=0; i<3; i++){
                if(equals(modificationLevel, idGlobalArray[i][0]))
                    idArray=idGlobalArray[i];
            }%> 

            <yfc:makeXMLInput name="OrderLineKey" >
                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderAuditLevel/@OrderLineKey" />
            </yfc:makeXMLInput>
            <yfc:makeXMLInput name="OrderReleaseKey" >
                <yfc:makeXMLKey binding="xml:/OrderReleaseDetail/@OrderReleaseKey" value="xml:/OrderAuditLevel/@OrderReleaseKey" />
            </yfc:makeXMLInput>

            <tr>
                <td class="detaillabel" >
                    <yfc:i18n>Order_#</yfc:i18n>
                </td>
                <td class="protectedtext">
                    <yfc:makeXMLInput name="OrderHeaderKey">
		              <yfc:makeXMLKey binding="xml:/Order/@OrderHeaderKey" value="xml:/OrderAudit/@OrderHeaderKey" />
                    </yfc:makeXMLInput>
                    <a <%=getDetailHrefOptions("L07",getParameter("OrderHeaderKey"),"")%> >
                        <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/Order/@OrderNo"/>
                    </a>
                </td>
                <td class="detaillabel" >
                    <yfc:i18n>Enterprise_Code</yfc:i18n>
                </td>
                <td class="protectedtext">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/Order/@EnterpriseCode"/>
                </td>
                <td class="detaillabel" >
                    <yfc:i18n>Audit_Date</yfc:i18n>
                </td>
                <td class="protectedtext" >
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Createts" />
                </td>
            </tr>
            <tr>
				<td class="detaillabel">
					<yfc:i18n>Modified_By</yfc:i18n>
				</td> 
                <td class="protectedtext" >
					<%
						String sUser = resolveValue("xml:/OrderAudit/@CreateUserName");
						if(isVoid(sUser) )
							sUser = resolveValue("xml:/OrderAudit/@Createuserid");
					%>
					<%=sUser%>
                </td>
                <td class="detaillabel">
					<yfc:i18n>Modification_Level</yfc:i18n>
                </td>
                <td class="protectedtext">
                <% if( isVoid(resolveValue("xml:/OrderAuditLevel/@ModificationLevelScreenName"))) {
                	if( equals("ORDER_LINE", resolveValue("xml:/OrderAuditLevel/@ModificationLevel"))) {%>
                		<yfc:i18n>SCREEN_NAME_ORDER_LINE</yfc:i18n> <br/>
                	<% }
                	else if(equals("ORDER_RELEASE_LINE", resolveValue("xml:/OrderAuditLevel/@ModificationLevel"))) {%>
                		<yfc:i18n>SCREEN_NAME_ORDER_RELEASE_LINE</yfc:i18n> <br/>
                	<% }
                } else { %>
                    <yfc:getXMLValue name="OrderAuditLevel" binding="xml:/OrderAuditLevel/@ModificationLevelScreenName"/>
                    <% } %>
                </td>
            <% if(idArray != null){ %>
                <td class="detaillabel" >
                    <yfc:i18n><%=idArray[3]%></yfc:i18n>
                </td>
                <td class="protectedtext">
                    <a <%=getDetailHrefOptions(idArray[1],getParameter(idArray[2]),"")%> >
                        <yfc:getXMLValue name="OrderAuditLevel" binding="<%=idArray[4]%>"/>
                    </a>
                </td>
            <%}else{%>        
                <td>&nbsp;</td>
                <td>&nbsp;</td> 
            <%}%>        
            </tr>

            <% /* If the ItemID is not void, display item information */ %>
            <% if( !isVoid(resolveValue("xml:/OrderAuditLevel/OrderLine/Item/@ItemID"))) { %>
                <tr>
                    <td class="detaillabel" >
                        <yfc:i18n>Item_ID</yfc:i18n>
                    </td>
                    <td class="protectedtext">
                        <yfc:getXMLValue binding="xml:/OrderAuditLevel/OrderLine/Item/@ItemID"/>
                    </td>
                    <td class="detaillabel" >
                        <yfc:i18n>Unit_Of_Measure</yfc:i18n>
                    </td>
                    <td class="protectedtext">
                        <yfc:getXMLValue binding="xml:/OrderAuditLevel/OrderLine/Item/@UnitOfMeasure"/>
                    </td>
                    <td class="detaillabel" >
                        <yfc:i18n>Product_Class</yfc:i18n>
                    </td>
                    <td class="protectedtext">
                        <yfc:getXMLValue binding="xml:/OrderAuditLevel/OrderLine/Item/@ProductClass"/>
                    </td>
                </tr>
                <tr>
                    <td class="detaillabel" >
                        <yfc:i18n>Description</yfc:i18n>
                    </td>
                    <td class="protectedtext" colspan="5">
                        <yfc:getXMLValue binding="xml:/OrderAuditLevel/OrderLine/Item/@ItemDesc"/>
                    </td>
                </tr>
            <% } %>
            <tr>
                <td class="detaillabel"><yfc:i18n>Modification_Type</yfc:i18n></td>    
                <td class="protectedtext" colspan="5">
                    <yfc:loopXML binding="xml:/OrderAuditLevel/ModificationTypes/@ModificationType" id="ModificationType" ><% 
                        if(!isFirst){
                            %><yfc:i18n>,</yfc:i18n> <%
                        } else { 
                            isFirst=false;
                        }
                        %>
                        <% if( isVoid(resolveValue("xml:/ModificationType/@ScreenName"))){
                        	if( equals("CHANGE_STATUS", resolveValue("xml:/ModificationType/@Name"))) { %>
                        	<yfc:i18n>Change_Status</yfc:i18n> <br/>
                        <%} } else { %>
                        	<yfc:getXMLValue name="ModificationType" binding="xml:/ModificationType/@ScreenName"/><%
                        }
                    %></yfc:loopXML> 
                </td>
            </tr>
            <tr>
                <td class="detaillabel">
                    <yfc:i18n>Reason_Code</yfc:i18n>
                </td>    
                <td class="protectedtext">
                   <%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderAudit/@ReasonCode",true)%>
                </td>
                <td class="detaillabel">
                    <yfc:i18n>Reason_Text</yfc:i18n>
                </td>    
                <td colspan="3" class="protectedtext">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@ReasonText"/>
                </td>
            </tr>
            <tr>
                <td class="detaillabel">
                    <yfc:i18n>Reference_1</yfc:i18n>
                </td>    
                <td class="protectedtext">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Reference1"/>
                </td>
                <td class="detaillabel">
                    <yfc:i18n>Reference_2</yfc:i18n>
                </td>        
                <td class="protectedtext">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Reference2"/>
                </td>
                <td class="detaillabel">
                    <yfc:i18n>Reference_3</yfc:i18n>
                </td>    
                <td class="protectedtext">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Reference3"/>
                </td>
             </tr>
             <tr>
                <td class="detaillabel">
                    <yfc:i18n>Reference_4</yfc:i18n>
                </td>    
                <td class="protectedtext" colspan="5">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Reference4"/>
                </td>
            </tr>
        <%}%>         
    </yfc:loopXML> 
</table>
