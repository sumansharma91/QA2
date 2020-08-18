/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!extn/return/acknowledge/VSIAcknowledgeReturnArticleDisposition", "scbase/loader!sc/plat/dojo/controller/ServerDataController"], function(
_dojodeclare, _dojokernel, _dojotext, _extnVSIAcknowledgeReturnArticleDisposition, _scServerDataController) {
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnArticleDispositionBehaviorController", [_scServerDataController], {
        screenId: 'extn.return.acknowledge.VSIAcknowledgeReturnArticleDisposition',
        mashupRefs: [{
            mashupId: 'acknowledgeReturn_getReturnReasonCodes',
            mashupRefId: 'getInnerReturnReasonList',
			sourceNamespace: 'getReturnReasonList_output',
			callSequence: ''
        },{
            cached: 'PAGE',
            sourceNamespace: 'getReturnDispositionList_output',
            mashupRefId: 'getReturnDispositionList',
            sequence: '',
            mashupId: 'acknowledgeReturn_getReturnDispositionList',
            callSequence: '',
            sourceBindingOptions: ''
        },{
            cached: 'PAGE',
            mashupRefId: 'returns_receiveOrder_service',
            sequence: '',
            mashupId: 'acknowledgeReturn_receivereturnorder',
            callSequence: '',
            sourceBindingOptions: ''
        },
		{
			mashupId: 'acknowledgeReturn_getReturnReasonCodesForWholesale',
			mashupRefId: 'getReturnReasonCodesForWholesale',
            sourceNamespace: 'getReturnReasonList_output',
			callSequence: ''
		},
		{
            cached: 'PAGE',
            mashupRefId: 'returns_createAndreceive_service',
            sequence: '',
            mashupId: 'acknowledgeReturn_createAndreceivereturnorder',
            callSequence: '',
            sourceBindingOptions: ''
        },{
            cached: 'PAGE',
            mashupRefId: 'returns_checkAndreceive_service',
            sequence: '',
            mashupId: 'acknowledgeReturn_checkAndreceivereturnorder',
            callSequence: '',
            sourceBindingOptions: ''
        }]	
    });
});