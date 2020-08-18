<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file="/yfsjspcommon/yfsutil.jspf" %>

<script language="javascript">
    var myObject = new Object();
    myObject = dialogArguments;
    var parentWindow = myObject.currentWindow;

    function setClickedAttribute() {

        var retVal = new Object();
        retVal["xml:/ScheduleOrder/@AllocationRuleID"] = document.all["xml:/ScheduleOrder/@AllocationRuleID"].value;
        if (document.all["xml:/ScheduleOrder/@IgnoreReleaseDate"].checked) {
            retVal["xml:/ScheduleOrder/@IgnoreReleaseDate"] = "Y";
        }
        else {
            retVal["xml:/ScheduleOrder/@IgnoreReleaseDate"] = "N";
        }
        if (document.all["xml:/ScheduleOrder/@ScheduleAndRelease"].checked) {
            retVal["xml:/ScheduleOrder/@ScheduleAndRelease"] = "Y";
        }
        else {
            retVal["xml:/ScheduleOrder/@ScheduleAndRelease"] = "N";
        }

        window.dialogArguments["OMReturnValue"] = retVal;
        window.dialogArguments["OKClicked"] = "YES";
        window.close();
    }
</script>

<table width="100%" class="view">
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Scheduling_Rule</yfc:i18n> 
        </td>
        <td>
            <select name="xml:/ScheduleOrder/@AllocationRuleID" class="combobox">
                <yfc:loopOptions binding="xml:AllocationRuleList:/AllocationRuleList/@AllocationRule" 
                    name="Description" value="AllocationRuleId" selected="xml:/Order/@AllocationRuleID" isLocalized="Y"/>
            </select>
        </td>
    </tr>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Release_Immediately</yfc:i18n>
        </td>
        <td>
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/ScheduleOrder/@ScheduleAndRelease", "N", "Y")%>/>
        </td>
    </tr>
    <tr>
        <td class="detaillabel">
            <yfc:i18n>Override_Release_Date</yfc:i18n>
        </td>
        <td>
            <input class="checkbox" type="checkbox" <%=getCheckBoxOptions("xml:/ScheduleOrder/@IgnoreReleaseDate", "N", "Y")%>/>
        </td>
    <tr>
        <td></td>
        <td align="right">
            <input type="button" class="button" value='<%=getI18N("Ok")%>' onclick="setClickedAttribute();return true;"/>
            <input type="button" class="button" value='<%=getI18N("Cancel")%>' onclick="window.close();"/>
        <td>
    <tr>
</table>
