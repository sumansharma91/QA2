<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>

<table class="view" width="33%">
<tr>
	<td class="detaillabel" nowrap="true" ><yfc:i18n>Order_Created_In_Yantra</yfc:i18n></td>
	<td class="protectedtext" nowrap="true" >  <yfc:getXMLValue name="OrderLine" binding="xml:/OrderLine/Order/@Createts"/></td>
</tr>			
</table>
<table class="table" width="100%">
<thead>
    <tr> 
        <td class="checkboxheader" sortable="no">
            <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
        </td>
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/AuditLevel/@DetailNo")%>">
            <yfc:i18n>Audit_#</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" style="width:<%=getUITableSize("xml:/OrderAudit/@Createts")%>">
            <yfc:i18n>Date</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" >
            <yfc:i18n>Modified_By</yfc:i18n>
        </td> 
		<td class="tablecolumnheader" >
            <yfc:i18n>Reason</yfc:i18n>
        </td> 
        <td class="tablecolumnheader" >            <yfc:i18n>Modification_Type</yfc:i18n>        </td> 
    </tr>
</thead> 
<tbody>
    <% String LineNo = resolveValue("xml:/OrderLine/@PrimeLineNo"); %>
    <% String SubLineNo = resolveValue("xml:/OrderLine/@SubLineNo"); %>
    <yfc:loopXML name="OrderAuditList" binding="xml:/OrderAuditList/@OrderAudit" id="OrderAudit" > 
        <yfc:loopXML binding="xml:/OrderAudit/OrderAuditLevels/@OrderAuditLevel" id="OrderAuditLevel"> 
            <% if(   equals(resolveValue("xml:/OrderAuditLevel/OrderLine/@PrimeLineNo"), LineNo)
                  && equals(resolveValue("xml:/OrderAuditLevel/OrderLine/@SubLineNo"), SubLineNo) ){ %>
                <yfc:makeXMLInput name="OrderAuditKey" >
                    <yfc:makeXMLKey binding="xml:/OrderAuditEntity/@OrderAuditKey" value="xml:/OrderAudit/@OrderAuditKey" />
                    <yfc:makeXMLKey binding="xml:/OrderAuditEntity/@OrderAuditLevelKey" value="xml:/OrderAuditLevel/@OrderAuditLevelKey" />
                </yfc:makeXMLInput>
                <tr>
                <td class="checkboxcolumn"> 
                    <input type="checkbox" value='<%=getParameter("OrderAuditKey")%>' name="chkAuditEntityKey"/>
                </td>
                <td class="tablecolumn" sortValue="<%=OrderAuditCounter%>">
                    <a <%=getDetailHrefOptions("L01",getParameter("OrderAuditKey"),"")%> >
                        <%=OrderAuditCounter %>
                    </a>
                </td>
                <td class="tablecolumn" sortValue="<%=getDateValue("xml:OrderAudit:/OrderAudit/@Createts")%>">
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@Createts"/>
                </td>
                <td class="tablecolumn">
					<%
						String sUser = resolveValue("xml:/OrderAudit/@CreateUserName");
						if(isVoid(sUser) )
							sUser = resolveValue("xml:/OrderAudit/@Createuserid");
					%>
                    <%=sUser%>
                </td>
				<td class="tablecolumn">
                    <yfc:hasXMLNode binding="xml:/OrderAudit/@ReasonCode">
                        <%=getComboText("xml:ReasonCodeList:/CommonCodeList/@CommonCode", "CodeShortDescription", "CodeValue", "xml:/OrderAudit/@ReasonCode",true)%>
                        <br>
                    </yfc:hasXMLNode>
                    <yfc:getXMLValue name="OrderAudit" binding="xml:/OrderAudit/@ReasonText"/>
                </td>
                <td class="tablecolumn">
                    <yfc:loopXML binding="xml:/OrderAuditLevel/ModificationTypes/@ModificationType" id="ModificationType" > 
                    <% if( isVoid(resolveValue("xml:/ModificationType/@ScreenName"))) {
                    	if( equals("CHANGE_STATUS", resolveValue("xml:/ModificationType/@Name"))) { %>
                    	<yfc:i18n>Change_Status</yfc:i18n> <br/>
                    <% }
                    } else { %>
                        <yfc:getXMLValue name="ModificationType" binding="xml:/ModificationType/@ScreenName"/><br/> 
                        <% } %>
                    </yfc:loopXML> 
                </td>
                </tr>
            <% } %>
        </yfc:loopXML> 
    </yfc:loopXML> 
</tbody>
</table>
