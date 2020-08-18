/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!extn/return/acknowledge/VSIAcknowledgeReturnOrderLines", "scbase/loader!sc/plat/dojo/controller/ScreenController"], function(
_dojodeclare, _dojokernel, _dojotext, _extnVSIAcknowledgeReturnOrderLines, _scScreenController) {
    return _dojodeclare("extn.return.acknowledge.VSIAcknowledgeReturnOrderLinesInitController", [_scScreenController], {
        screenId: 'extn.return.acknowledge.VSIAcknowledgeReturnOrderLines'
		
		,
        childControllers: [{
            screenId: 'extn.return.acknowledge.VSIAcknowledgeReturnArticleDisposition',
            controllerId: 'extn.return.acknowledge.VSIAcknowledgeReturnArticleDispositionInitController'
        }]
		
    });
});