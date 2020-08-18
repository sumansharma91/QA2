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

<% // Call the API to get instruction types now because it is based on the common fields
   // (Doc Type and Enterprise Code) that are selected on the other inner panel %>
<yfc:callAPI apiID="AP1"/>

<table class="table" ID="TemplateOrder" width="100%" cellspacing="0" initialRows="3" >
<thead>
    <tr>
        <td class="checkboxheader">&nbsp;</td>
        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Instruction_Type</yfc:i18n></td>
        <td class="tablecolumnheader" sortable="no"><yfc:i18n>Text</yfc:i18n></td>
    </tr>
</thead>
<tfoot>
    <tr style='display:none' TemplateRow="true">
        <td class="checkboxcolumn">&nbsp;</td>
        <td class="tablecolumn">
            <select class="combobox" <%=getComboOptions("xml:/Order/Instructions/Instruction_/@InstructionType")%>>
                <yfc:loopOptions binding="xml:InstructionTypeList:/CommonCodeList/@CommonCode" name="CodeShortDescription" value="CodeValue" isLocalized="Y"/>
            </select>
        </td>
        <td class="tablecolumn">
            <table class="view" width="100%" cellspacing="0" cellpadding="0">
                <td>
                    <textarea class="unprotectedtextareainput" rows="3" style="width:100%" <%=getTextAreaOptions("xml:/Order/Instructions/Instruction_/@InstructionText")%>></textarea>
                </td>
                <tr>
                    <td>
						<img align="absmiddle" <%=getImageOptions(YFSUIBackendConsts.INSTRUCTION_URL, "Instruction_URL")%>/>
                        <input class="unprotectedinput" type="text" <%=getTextOptions("xml:/Order/Instructions/Instruction_/@InstructionURL")%>/>
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
    <tr>
    	<td nowrap="true" colspan="3">
    		<jsp:include page="/common/editabletbl.jsp" flush="true"/>
    	</td>
    </tr>
</tfoot>
</table>
