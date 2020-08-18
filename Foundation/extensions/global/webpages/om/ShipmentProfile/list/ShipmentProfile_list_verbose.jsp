<%/*******************************************************************************
Licensed Materials - Property of IBM
IBM Sterling Selling And Fulfillment Suite
(C) Copyright IBM Corp. 2005, 2013 All Rights Reserved.
US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 ********************************************************************************/%>
<%@ include file = "/yfsjspcommon/yfsutil.jspf"%>
<%@ page import = "com.yantra.yfs.ui.backend.*"%>
<%@ include file = "/console/jsp/modificationutils.jspf"%>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/dm.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/tools.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/shipmentprofile.js">
</script>

<script language = "javascript" src="<%=request.getContextPath()%>/console/scripts/modificationreason.js">
</script>

   <% String destNode = "";

   String chkBoxCounterPrefix = "chkShipment_"; %>

<script language = "javascript">
    function showSearchPopup2(ProfileId)
            {

            var oParams = '&xml:/Shipment/@ProfileID=' + ProfileId;
            yfcShowListPopupWithParams('', '', 900, 550, '', 'shipment', oParams);
            }

    function showSearchPopup(ProfileId)
            {
            var qrytype = 'VOID';
            var oParams =
                '&xml:/Shipment/@ProfileID=' + ProfileId + '&xml:/Shipment/ShipmentLines/ShipmentLine/@WaveNo='
                + '&xml:/Shipment/ShipmentLines/ShipmentLine/@WaveNoQryType=' + qrytype;
            yfcShowListPopupWithParams('', '', 900, 550, '', 'shipment', oParams);
            }

    function showSearchPopup3(ProfileId)
            {
            var qrytype = 'NOTVOID';
            var oParams =
                '&xml:/Shipment/@ProfileID=' + ProfileId + '&xml:/Shipment/ShipmentLines/ShipmentLine/@WaveNo='
                + '&xml:/Shipment/ShipmentLines/ShipmentLine/@WaveNoQryType=' + qrytype;
            yfcShowListPopupWithParams('', '', 900, 550, '', 'shipment', oParams);
             }
</script>

<table class = "table" editable = "false" width = "100%" cellspacing = "0">
    <thead>
        <tr>
            <td sortable = "no" class = "checkboxheader">
                <input type = "checkbox" name = "checkbox" value = "checkbox" onclick = 'doCheckAll(this);' />
            </td>

            <td class = "tablecolumnheader">
                <yfc:i18n>
                    Profile_Id
                </yfc:i18n>
            </td>

            <td class = "tablecolumnheader">
                <yfc:i18n>
                    No_of_shipments
                </yfc:i18n>
            </td>

            <td class = "tablecolumnheader">
                <yfc:i18n>
                    No_of_shipments_in_the_wave   
                </yfc:i18n>
            </td>

            <td class = "tablecolumnheader">
                <yfc:i18n>
                   No_of_shipments_not_in_the_wave
                </yfc:i18n>
            </td>
        </tr>
    </thead>

    <tbody>
        <yfc:loopXML binding = "xml:/ShipmentProfileSummary/@Profile" id = "Profile">
            <tr>
                <yfc:makeXMLInput name = "shipmentProfileWaveKey">
                    <yfc:makeXMLKey binding = "xml:/Profile/@ProfileId" value = "xml:/Profile/@ProfileId" />

                    <yfc:makeXMLKey binding = "xml:/Profile/@ShipNode" value = "xml:/Shipment/@ShipNode" />
                </yfc:makeXMLInput>

                <td class = "checkboxcolumn">
                    <input type = "checkbox" value = '<%= getParameter("shipmentProfileWaveKey") %>' name = "EntityKey"
                        yfcMultiSelectCounter = '<%= ProfileCounter %>'
                        yfcMultiSelectValue1 = '<%= getValue("Profile", "xml:/Profile/@ProfileId") %>' />
                </td>

                <td class = "tablecolumn">
                    <yfc:getXMLValueI18NDB binding = "xml:/Profile/@ProfileId" />
                </td>

                <td class = "tablecolumn">
                    <% double NoOfShipments = getNumericValue("xml:/Profile/@NumberOfShipments");

                    if (NoOfShipments != 0)
                        { %>

                        <A href = " "
                            onclick = "javascript:showSearchPopup2('<%=resolveValue("xml:/Profile/@ProfileId")%>');return false;">

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipments" /></A>

                    <% }

                    else
                        { %>

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipmentsWaved" />

                    <% }%>
                </td>

                <td class = "tablecolumn">
                    <% double NoOfShipmentsWaved = getNumericValue("xml:/Profile/@NumberOfShipmentsWaved");

                    if (NoOfShipmentsWaved != 0)
                        { %>

                        <A href = " "
                            onclick = "javascript:showSearchPopup3('<%=resolveValue("xml:/Profile/@ProfileId")%>');return false;">

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipmentsWaved" /></A>

                    <% }

                    else
                        { %>

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipmentsWaved" />

                    <% }%>
                </td>

                <td class = "tablecolumn">
                    <% double NoOfShipmentsNotWaved = getNumericValue("xml:/Profile/@NumberOfShipmentsNotWaved");

                    if (NoOfShipmentsNotWaved != 0)
                        { %>

                        <A href = " "
                            onclick = "javascript:showSearchPopup('<%=resolveValue("xml:/Profile/@ProfileId")%>');return false;">

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipmentsNotWaved" /></A>

                    <% }

                    else
                        { %>

                        <yfc:getXMLValue binding = "xml:/Profile/@NumberOfShipmentsNotWaved" />

                    <% }%>
                </td>
            </tr>
        </yfc:loopXML>
    </tbody>
</table>
