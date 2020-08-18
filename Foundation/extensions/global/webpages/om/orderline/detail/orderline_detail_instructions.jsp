<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@include file="/yfsjspcommon/yfsutil.jspf"%>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>

<% boolean isHistory=equals(resolveValue("xml:/OrderLine/@isHistory"),"Y"); %>

<table class="table" width="100%" <%if (isModificationAllowed("xml:/@AddInstruction","xml:/OrderLine/AllowedModifications")) {%> initialRows="3" <%}%>>
<thead>
    <tr>
        <td class="checkboxheader" sortable="no">
            <%if( !isHistory ) { /* Then checkboxes are useless, since the only action has been removed */ %>
                <input type="checkbox" name="checkbox" value="checkbox" onclick="doCheckAll(this);"/>
            <%}else { %>
                &nbsp;
            <%}%>
        </td>
        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Instruction_Type</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Text</yfc:i18n></td>
    </tr>
</thead>
<tbody>
    <yfc:loopXML binding="xml:/OrderLine/Instructions/@Instruction" id="Instruction">
        <tr>
        <yfc:makeXMLInput name="InstructionKey">
            <yfc:makeXMLKey binding="xml:/Instruction/@InstructionDetailKey" value="xml:/Instruction/@InstructionDetailKey" />
            <yfc:makeXMLKey binding="xml:/Instruction/@OrderHeaderKey" value="xml:/OrderLine/@OrderHeaderKey" />
            <yfc:makeXMLKey binding="xml:/Instruction/@OrderLineKey" value="xml:/OrderLine/@OrderLineKey" />
        </yfc:makeXMLInput>
        <td class="checkboxcolumn">
            <%if( !isHistory ) {%>
                <input type="checkbox" value='<%=getParameter("InstructionKey")%>' name="chkInstructionKey"/>
            <%}else { %>
                &nbsp;
            <%}%>
		</td>
            <td class="tablecolumn">
                <% String instTargetBinding = "xml:/Order/OrderLines/OrderLine/Instructions/Instruction_" + InstructionCounter + "/@InstructionType"; %>
                <select <%=yfsGetComboOptions(instTargetBinding, "xml:/Instruction/@InstructionType", "xml:/OrderLine/AllowedModifications")%>>
                    <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Instruction/@InstructionType" isLocalized="Y" targetBinding="<%=instTargetBinding%>"/>
                </select>
            </td>
            <td class="tablecolumn">
                <table class="view" cellspacing="0" cellpadding="0">
                    <tr>
                        <td>
                            <textarea class="unprotectedtextareainput" rows="3" cols="100" <%=yfsGetTextAreaOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_" + InstructionCounter + "/@InstructionText","xml:/Instruction/@InstructionText", "xml:/OrderLine/AllowedModifications")%>><yfc:getXMLValue binding="xml:/Instruction/@InstructionText"/></textarea>
                        </td>
                    </tr>
                    <tr>
                        <td>
							<img align="absmiddle" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTION_URL, "Instruction_URL")%>/>
                            <input type="text" <%=yfsGetTextOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_" + InstructionCounter + "/@InstructionURL", "xml:/Instruction/@InstructionURL","xml:/OrderLine/AllowedModifications")%>/>                            
                            <input type="button" class="button" value="GO" onclick="javascript:goToURL('xml:/Order/OrderLines/OrderLine/Instructions/Instruction_<%=InstructionCounter%>/@InstructionURL');"/>
                        </td>
                        <td>
                            <input type="hidden" <%=getTextOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_" + InstructionCounter + "/@InstructionDetailKey", "xml:/Instruction/@InstructionDetailKey")%>/>
                        </td>
                    </tr>
                     <tr>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                        <td>&nbsp;</td>
                    </tr>
                </table>
            </td>
        </tr>
    </yfc:loopXML> 
</tbody>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="tablecolumn">
            <select <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_/@InstructionType", "xml:/OrderLine/AllowedModifications", "ADD_INSTRUCTION", "combo")%>>
                <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="tablecolumn">
            <table border="0" cellspacing="0" cellpadding="0">
                <td>
                    <textarea rows="3" cols="100" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_/@InstructionText", "xml:/OrderLine/AllowedModifications", "ADD_INSTRUCTION", "textarea")%>/></textarea>
                </td>
                <tr>
                    <td>
						<img align="absmiddle" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTION_URL, "Instruction_URL")%>/>
                        <input type="text" <%=yfsGetTemplateRowOptions("xml:/Order/OrderLines/OrderLine/Instructions/Instruction_/@InstructionURL", "xml:/OrderLine/AllowedModifications", "ADD_INSTRUCTION", "text")%>/>
                    </td>
                </tr>
                <tr>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                    <td>&nbsp;</td>
                </tr>
            </table>
        </td>
    </tr>
    <%if (isModificationAllowed("xml:/@AddInstruction","xml:/OrderLine/AllowedModifications")) {%> 
    <tr>
    	<td nowrap="true" colspan="3">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
    <%}%>
</tfoot>
</table>
