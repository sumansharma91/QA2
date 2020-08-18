<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/order.jspf" %>

<% /* This is the inner table for the order detail audit list */ %>

<%
	boolean showLineInfo = true;
	if(equals("Y", request.getParameter("HideLineInfo"))) {
		showLineInfo = false;
	}
%>

<table class="table" >
<tbody>
    <tr>
        <td width="10%" >
            &nbsp;
        </td>
        <td width="80%" class="bordertablecolumn">
            <table class="table" SuppressRowColoring="true">
            <thead>
                <td class="checkboxheader" sortable="no">
                    <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
                </td>
                <td nowrap="true" class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AuditLevel/@DetailNo")%>">
                    <yfc:i18n>Detail_#</yfc:i18n>
                </td> 
			<% if(showLineInfo) {%>
                <td class="tablecolumnheader" >
                    <yfc:i18n>Order_Line_Related_Info</yfc:i18n>
                </td>
			<% } %>
                <td class="tablecolumnheader" >
                    <yfc:i18n>Modification_Level</yfc:i18n>
                </td>
                <td class="tablecolumnheader" >
                    <yfc:i18n>Modification_Type</yfc:i18n>
                </td> 
            </thead>
            <tbody>
                <yfc:loopXML binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel"> 
                    <yfc:makeXMLInput name="OrderAuditKey" >
                        <yfc:makeXMLKey binding="xml:/OrderAuditEntity/@OrderAuditKey" value="xml:/OrderAudit/@OrderAuditKey" />
                        <yfc:makeXMLKey binding="xml:/OrderAuditEntity/@OrderAuditLevelKey" value="xml:/OrderAuditLevel/@OrderAuditLevelKey" />
                    </yfc:makeXMLInput>
                    <tr>
                        <td class="checkboxcolumn"> 
                            <input type="checkbox" value='<%=getParameter("OrderAuditKey")%>' name="chkAuditEntityKey"/>
                        </td>
                        <td class="tablecolumn" sortValue="<%=getNumericValue(String.valueOf(OrderAuditLevelCounter))%>">
                            <a <%=getDetailHrefOptions("L01",getParameter("OrderAuditKey"),"")%> >
                                <%=OrderAuditLevelCounter %>
                            </a>
                        </td>
				<% if(showLineInfo) {%>
                        <% if(equals("ORDER_LINE", resolveValue("xml:/OrderAuditLevel/@ModificationLevel") ) )	{
                            String orderLineItem = resolveValue("xml:/OrderAuditLevel/OrderLine/Item/@ItemID");
                            String orderLineNo = resolveValue("xml:/OrderAuditLevel/OrderLine/@PrimeLineNo");
                            String orderLineLink="L02";
                            String itemGroupCode = resolveValue("xml:/OrderAuditLevel/OrderLine/@ItemGroupCode");
                            if(equals("PS",itemGroupCode)) {
                                orderLineLink="L03";
                            } else if(equals("DS",itemGroupCode)) {
                                orderLineLink="L04";
                            }
                            %>
                            <yfc:makeXMLInput name="OrderLineKey">
                                <yfc:makeXMLKey binding="xml:/OrderLineDetail/@OrderLineKey" value="xml:/OrderAuditLevel/@OrderLineKey" />
                            </yfc:makeXMLInput>
                            <td class="tablecolumn" sortValue="<%=orderLineItem%>">
                                <%=orderLineItem%><br/>
                                <yfc:getXMLValue name="OrderAuditLevel" binding="xml:/OrderAuditLevel/OrderLine/Item/@ItemDesc" /><br/>
                                <yfc:i18n>Line_#</yfc:i18n>:&nbsp;<a <%=getDetailHrefOptions(orderLineLink,getParameter("OrderLineKey"),"")%> ><%=orderLineNo%></a>
                            </td>
                        <% } else { %>
                            <td class="tablecolumn">
                                &nbsp;
                            </td>
                        <% } %>
					<% } %>
                        <td class="tablecolumn">
                        <% if( isVoid(resolveValue("xml:/OrderAuditLevel/@ModificationLevelScreenName"))){
                        	if( equals("ORDER_LINE",resolveValue("xml:/OrderAuditLevel/@ModificationLevel"))) { %>
                        		<yfc:i18n>SCREEN_NAME_ORDER_LINE</yfc:i18n>
                        <% }
                        	else if( equals("ORDER_RELEASE_LINE", resolveValue("xml:/OrderAuditLevel/@ModificationLevel"))){ %>
                        		<yfc:i18n>SCREEN_NAME_ORDER_RELEASE_LINE</yfc:i18n>
                        <%	}
                        } else { %>
                            <yfc:getXMLValue name="OrderAuditLevel" binding="xml:/OrderAuditLevel/@ModificationLevelScreenName"/>
                            &nbsp;
                            <% } %>
                        </td>
                        <td class="tablecolumn">
                            <% boolean hasTypes = false; %>
                            <yfc:loopXML binding="xml:/OrderAuditLevel/ModificationTypes/@ModificationType" id="ModificationType" >
                            <% if( isVoid(resolveValue("xml:/ModificationType/@ScreenName"))){
                            	if( equals("CHANGE_STATUS", resolveValue("xml:/ModificationType/@Name"))) { %>
                            	<yfc:i18n>Change_Status</yfc:i18n>
                            <%	}
                            } else { %>
                                <yfc:getXMLValue name="ModificationType" binding="xml:/ModificationType/@ScreenName"/><br/>
                                <% } %>
                                <% hasTypes = true; %>
                            </yfc:loopXML> 
                            <% if( ! hasTypes ) { %>
                                &nbsp;
                            <% } %>
                        </td>
                    </tr>
                </yfc:loopXML> 
            </tbody>
            </table>
        </td>
        <td width="10%" >
            &nbsp;
        </td>
    </tr>
</tbody>
</table>

