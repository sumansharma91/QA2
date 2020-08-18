<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file = "/yfsjspcommon/yfsutil.jspf"%>
<%@ include file = "/dm/deliveryplan/detail/deliveryplan_detail_include.jspf"%>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js">
</script>

<script language = "javascript">
    yfcDoNotPromptForChanges(true);

    function showWaveDetail()
            {
            var sEntityKey = document.all("hidWaveDetailKey");
            var sVal = document.all("hidWaveKey").value;

            if (sVal != "")
                {
                showDetailForOnAdvancedList('wave', ' ', sEntityKey.value);
                }
            }

    function checkBlankAttribute()
            {
            if (!document.all("xml:/Wave/@Node").value)
                {
                alert(YFCMSG077); //Node_not_Passed
                return false;
                }

            else
                {
                var tmp = document.all("createOrModifyWaveRadio");

                if (tmp[0].checked)
                    {
                    if (!document.all("xml:/Wave/@ShipmentGroupId").value)
                        {
                        alert(YFCMSG099); //Shipment_Group_not_Passed
                        return false;
                        }
                    }

                else if (tmp[1].checked)
                    {
                    if (!document.all("xml:/Wave/@WaveNo").value)
                        {
                        alert(YFCMSG100); //Wave Number Not Passed
                        return false;
                        }
                    }
                }
            return true;
            }


function handleFieldForProfile()
{
var tmp = document.all("text");
if (tmp[1].checked)
	{
	document.all("text1").disabled=false;
	}
	if (tmp[0].checked)
	{
		document.all("text1").disabled=true;
	}
}

</script>

<script>
    window.attachEvent("onload", showWaveDetail);
</script>

<table width = "50%">
    <tr>
        <td align = "left" >
            <table width = "20%">
                <jsp:include page = "/yfsjspcommon/common_fields.jsp" flush = "true">
                    <jsp:param name = "ScreenType" value = "detail" />

                    <jsp:param name = "ShowDocumentType" value = "false" />

                    <jsp:param name = "ShowEnterpriseCode" value = "false" />

                    <jsp:param name = "ShowNode" value = "true" />

                    <jsp:param name = "NodeBinding" value = "xml:/Wave/@Node" />

                    <jsp:param name = "RefreshOnNode" value = "true" />
                </jsp:include>
            </table>
        </td>

        <% if (isShipNodeUser())
            { %>

            <yfc:callAPI apiID = "AP2" />

        <% }

        else
            { %>

            <yfc:callAPI apiID = "AP1" />

        <% }%>

        <% resolveValue("xml:/Shipments/Shipment_1/@ShipmentNo");
		%>

            <yfc:loopXML binding = "xml:/Shipments/@Profile" id = "Profile">
                <input type = "hidden"
                    <%= getTextOptions("xml:/Wave/ShipmentCriteria/ComplexQuery/Or/Exp_" + ProfileCounter + "/@Value", "xml:/Profile/@ProfileId") %> />

                <input type = "hidden"
                    <%= getTextOptions("xml:/Wave/ShipmentCriteria/ComplexQuery/Or/Exp_" + ProfileCounter + "/@Name", "ProfileID") %> />

                <input type = "hidden"
                    <%= getTextOptions("xml:/Wave/ShipmentCriteria/ComplexQuery/Or/Exp_" + ProfileCounter + "/@QryType", "EQ") %> />
            </yfc:loopXML>

            <yfc:makeXMLInput name = "waveDetailKey">
                <yfc:makeXMLKey binding = "xml:/Wave/@WaveNo" value = "xml:SelectedWave:/Wave/@WaveNo" />

                <yfc:makeXMLKey binding = "xml:/Wave/@WaveKey" value = "xml:SelectedWave:/Wave/@WaveKey" />

                <yfc:makeXMLKey binding = "xml:/Wave/@Node" value = "xml:SelectedWave:/Wave/@Node" />
            </yfc:makeXMLInput>

            <input type = "hidden" name = "hidWaveDetailKey" value = '<%= getParameter("waveDetailKey") %>' />

            <input type = "hidden"<%= getTextOptions("hidWaveKey", "xml:SelectedWave:/Wave/@WaveKey") %> />

            <tr>
               
                    <% String isDisabled = "false";
                    String val = resolveValue("xml:/Wave/@Action");

                    if (isVoid(val))
                        {
                        val = "CREATE";
                        isDisabled = "true";
                        }%>

                   
                

                <td class = "searchlabel" nowrap>
				 <input type = "radio" id = "createOrModifyWaveRadio" onclick = "createWaveEnableDisableFields()"
                        <%= getRadioOptions("xml:/Wave/@Action", val, "CREATE") %>>
                    <yfc:i18n>
                        Create_New_Wave_With_Shipment_Group
                    </yfc:i18n>
                  <select<%= getComboOptions("xml:/Wave/@ShipmentGroupId") %>class = "combobox">
                        <yfc:loopOptions binding = "xml:ShipmentGroupList:/ShipmentGroupList/@ShipmentGroup"
                            name = "Description" value = "ShipmentGroupId" isLocalized = "Y" />
                    </select>
                </td>
            </tr>

            <tr>
               
                <td class = "searchlabel">
				 <input type = "radio" id = "createOrModifyWaveRadio" onclick = "createWaveEnableDisableFields()"
                        <%= getRadioOptions("xml:/Wave/@Action", "xml:/Wave/@Action", "MODIFY") %>>
                    <yfc:i18n>
                        Add_To_Wave_#	
                    </yfc:i18n>
               

                <% String extraParam =
    getExtraParamsForTargetBinding("xml:/Wave/@Node", resolveValue("xml:/Wave/@Node")); %>

               
                    <input type = "text" class = "unprotectedinput"
                        <%= getTextOptions("xml:/Wave/@WaveNo") %>disabled = "<%= isDisabled %>" />

                    <img class = "lookupicon" id = "imageId"
                        onclick = "callLookup(this,'wavelookup', '<%=extraParam%>')"
                        <%= getImageOptions(YFSUIBackendConsts.LOOKUP_ICON, "Search_for_Wave") %>
                        disabled = "<%= isDisabled %>" />
                </td>
            </tr>

            <tr>
                <td class = "searchlabel">
                    <yfc:i18n>
                        Consider_
                    </yfc:i18n>
                </td>
			</tr>
			<tr>
                <td class = "searchlabel">
                    <input type = "radio" id = "text" name = "xml:/Wave/ShipmentCriteria/@ALLElgibleShipments"
                        value = "Y" onclick = "handleFieldForProfile()"> </input>

                    <yfc:i18n>
                        All_Elgible_Shipments
                    </yfc:i18n>
                </td>
			</tr>
			<tr>
                <td class = "searchlabel" nowrap>
                    <input type = "radio" id = "text" name = "xml:/Wave/ShipmentCriteria/@ALLElgibleShipments"
                        value = "N" onclick = "handleFieldForProfile()">
				  <input type = "hidden" id = "text" name = "xml:/Wave/ShipmentCriteria/OrderBy/ByExpectedShipmentDate/@Name"
                        value = "ExpectedShipmentDate" >
					<yfc:i18n>
                        Create_Wave_First
                    </yfc:i18n>
                    <input type = "text" id = "text1" disabled
                        class = "unprotectedinput" <%=getTextOptions("xml:/Wave/ShipmentCriteria/@MaximumRecords")%>/>
                    <yfc:i18n>
                        Create_Wave_Shipments_Based_On_Expected_Shipment_Date
                    </yfc:i18n>
                </td>
			</tr>
    
</table>
