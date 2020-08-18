/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!extn/return/acknowledge/VSIAcknowledgeReturnHeader", "scbase/loader!sc/plat/dojo/controller/ScreenController"], function(
_dojodeclare, _dojokernel, _dojotext, _isccsVSIAcknowledgeReturnHeader, _scScreenController) {
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnHeaderInitController", [_scScreenController], {
        screenId: 'extn.return.acknowledge.VSIAcknowledgeReturnHeader',
        childControllers: [{
            screenId: 'extn.return.acknowledge.VSIAcknowledgeReturnOrderLines',
            controllerId: 'extn.return.acknowledge.VSIAcknowledgeReturnOrderLinesInitController'
        }]
		,
        mashupRefs: [{
            mashupId: 'acknowledgeReturn_getReturnReasonCodes',
            mashupRefId: 'getInnerReturnReasonList',
			sourceNamespace: 'getReturnReasonList_output',
			callSequence: ''
        },{
            mashupId: 'acknowledgeReturn_getReturnDCList',
            mashupRefId: 'getReturnDCList',
			sourceNamespace: 'acknowledgeReturn_getReturnDCList_output',
			callSequence: ''
        },
		{
            cached: 'PAGE',
            sourceNamespace: 'getReturnDispositionList_output',
            mashupRefId: 'getReturnDispositionList',
            sequence: '',
            mashupId: 'acknowledgeReturn_getReturnDispositionList',
            callSequence: '',
            sourceBindingOptions: ''
        }]	
    });
});