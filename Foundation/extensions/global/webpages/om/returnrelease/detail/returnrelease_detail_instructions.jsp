<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>
<%@ include file="/console/jsp/modificationutils.jspf" %>
<%@ page import="com.yantra.yfs.ui.backend.*" %>

<script language="javascript" src="../console/scripts/om.js"></script>

<table class="table" width="100%" cellspacing="0" <%//if (isModificationAllowed("xml:/@AddInstruction","xml:/Order/AllowedModifications")) {%> initialRows="1" <%//}%>>
    <thead>
        <tr>
            <td class="checkboxheader">&nbsp;</td>
            <td class="tablecolumnheader"><yfc:i18n>Instruction_Type</yfc:i18n></td>
            <td class="tablecolumnheader"><yfc:i18n>Text</yfc:i18n></td>
        </tr>
    </thead>
    <tbody>
        <yfc:loopXML binding="xml:/OrderRelease/Instructions/@Instruction" id="Instruction">
            <tr>
                <td class="checkboxcolumn">&nbsp;</td>
                <td class="tablecolumn">
                    <% String instTypeBinding = "xml:/Order/Instructions/Instruction_" + InstructionCounter + "/@InstructionType"; %>
                    <select class="protectedinput" disabled="true" <%=getComboOptions(instTypeBinding, "xml:/Instruction/@InstructionType")%>>
                        <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" selected="xml:/Instruction/@InstructionType" isLocalized="Y" targetBinding="<%=instTypeBinding%>"/>
                    </select>
                </td>
                <td class="tablecolumn">
                    <yfc:getXMLValue binding="xml:/Instruction/@InstructionText"/>
                </td>
            </tr>
        </yfc:loopXML> 
    </tbody>
    <tfoot>
        <tr style='display:none' TemplateRow="true">
            <td class="checkboxcolumn">&nbsp;</td>
            <td class="tablecolumn">
                <select <%//=yfsGetTemplateRowOptions("xml:/Order/Instructions/Instruction_/@InstructionType", "xml:/Order/AllowedModifications", "ADD_INSTRUCTION", "combo")%>>
                    <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
                </select>
            </td>
            <td class="tablecolumn">
                <textarea rows="4" cols="100" <%//=yfsGetTemplateRowOptions("xml:/Order/Instructions/Instruction_/@InstructionText", "xml:/Order/AllowedModifications", "ADD_INSTRUCTION", "textarea")%>></textarea>
            </td>
        </tr>
        <%//if (isModificationAllowed("xml:/@AddInstruction","xml:/Order/AllowedModifications")) {%> 
        <tr>
            <td nowrap="true" colspan="3">
                    <jsp:include page="/common/editabletbl.jsp" flush="true"/>
            </td>
        </tr>
        <%//}%>
    </tfoot>
</table>
