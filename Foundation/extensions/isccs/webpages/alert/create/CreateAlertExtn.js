
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/alert/create/CreateAlertExtnUI","scbase/loader!sc/plat/dojo/utils/WidgetUtils","scbase/loader!sc/plat/dojo/utils/BaseUtils",
"scbase/loader!sc/plat/dojo/utils/ModelUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils", "scbase/loader!isccs/utils/ModelUtils","scbase/loader!isccs/utils/UIUtils",
"scbase/loader!isccs/utils/OrderUtils"]
,
function(			 
			    _dojodeclare,
			    _extnCreateAlertExtnUI,
				_scWidgetUtils,
				_scBaseUtils,
				_scModelUtils,
				_scScreenUtils,
                _isccsModelUtils,
                _isccsUIUtils,
				_isccsOrderUtils
){ 
	return _dojodeclare("extn.alert.create.CreateAlertExtn", [_extnCreateAlertExtnUI],{
		
	// custom code here
	extn_init: function(){
		_scWidgetUtils.hideWidget( this, "cmbEnterprise", false);
		//Start Coding 1B - Alert DLD
		var radioObject = this.getWidgetByUId("radioSetAssociations");
		//console.log("radioObject>>>",radioObject);
		var optionNone = radioObject.options[0];
		optionNone.label = "General";
		radioObject.updateOption(optionNone);
	},
	handleExceptionTypeSelected: function(
        event, bEvent, ctrl, args) {
            var exception = null;
            var optionBean = null;
            optionBean = {};
            _scBaseUtils.setAttributeValue("includeInModel", true, optionBean);
            exception = _scBaseUtils.getTargetModel(
            this, "createAlert_Input", optionBean);
            var sExceptionType = null;
            sExceptionType = _scModelUtils.getStringValueFromPath("Inbox.ExceptionType", exception);
            var exceptionTypeList = null;
            exceptionTypeList = _scScreenUtils.getModel(
            this, "getExceptionTypeList");
            var length = 0;
            var exceptionTypes = null;
            exceptionTypes = _scModelUtils.getModelListFromPath("ExceptionTypeList.ExceptionType", exceptionTypeList);
            length = _scBaseUtils.getAttributeCount(
            exceptionTypes);
            for (
            var index = 0;
            index < length;
            index = index + 1) {
                var eExceptionType = null;
                eExceptionType = exceptionTypes[
                index];
                var curExcepType = null;
                curExcepType = _scModelUtils.getStringValueFromPath("ExceptionType", eExceptionType);
                if (
                _scBaseUtils.equals(
                sExceptionType, curExcepType)) {
                    exception = _scModelUtils.createNewModelObjectWithRootKey("Inbox");
                    var priority = null;
                    priority = _scModelUtils.getStringValueFromPath("Priority", eExceptionType);
                    var queueId = null;
                    queueId = _scModelUtils.getStringValueFromPath("QueueId", eExceptionType);
                    _scModelUtils.setStringValueAtModelPath("Inbox.Priority", priority, exception);
                    _scModelUtils.setStringValueAtModelPath("Inbox.QueueId", queueId, exception);
                                        var queueList=null;
                    queueList = _scScreenUtils.getModel(
                    this, "getQueueList");
                    var lengthQueueList = 0;
                    var queues = null;
                    queues = _scModelUtils.getModelListFromPath("QueueList.Queue", queueList);
                    lengthQueueList = _scBaseUtils.getAttributeCount(
                    queues);
                    for (
                    var indexForQ = 0;
                    indexForQ < lengthQueueList;
                    indexForQ = indexForQ + 1) {
                        var eQueue = null;
                        eQueue = queues[indexForQ];
                        var curQueueId = null;
                        curQueueId = _scModelUtils.getStringValueFromPath("QueueId", eQueue);
                        if (
                        _scBaseUtils.equals(
                        queueId, curQueueId)) {
                            var queueKey=null;
                            queueKey = _scModelUtils.getStringValueFromPath("QueueKey", eQueue);
                            _scModelUtils.setStringValueAtModelPath("Inbox.QueueKey", queueKey, exception);

                            var queueName=null;
                            queueName = _scModelUtils.getStringValueFromPath("QueueName", eQueue);
                             _scModelUtils.setStringValueAtModelPath("Inbox.QueueName", queueName, exception);
                            break;
                        }   
                    }
                    optionBean = {};
                    _scBaseUtils.setAttributeValue("clearOldVals", false, optionBean);
                    _scScreenUtils.setModel(
                    this, "screenInput", exception, optionBean);
                    return;
                }
            }
        },
        save: function(
        event, bEvent, ctrl, args) {
            var createException = null;
            var optionBean = null;
            optionBean = {};
            _scBaseUtils.setAttributeValue("includeInModel", true, optionBean);
            createException = _scBaseUtils.getTargetModel(
            this, "createAlert_Input", optionBean);
            var associationType = null;
            associationType = _scModelUtils.getStringValueFromPath("Inbox.AssocType", createException);
            var orderElem = null;
            orderElem = _scModelUtils.getModelObjectFromPath("Inbox.Order", createException);
            if (
            _scBaseUtils.equals(
            associationType, "SALES_ORDER")) {
                _scModelUtils.addStringValueToModelObject("DocumentType", _isccsOrderUtils.getSalesOrderDocumentType(), orderElem);
            } else if (
            _scBaseUtils.equals(
            associationType, "RETURN_ORDER")) {
                _scModelUtils.addStringValueToModelObject("DocumentType", _isccsOrderUtils.getReturnOrderDocumentType(), orderElem);
            }
            _isccsModelUtils.removeAttributeFromModel("Inbox.AssocType", createException);
            var screenInputMoldel=null;
            screenInputMoldel = _scScreenUtils.getModel(
                            this, "screenInput");   
            var queueKey=null;
            queueKey = _scModelUtils.getStringValueFromPath("Inbox.QueueKey", screenInputMoldel);
            _scModelUtils.setStringValueAtModelPath("Inbox.QueueKey", queueKey, createException);
            _isccsUIUtils.callApi(
            this, createException, "createAlert", null);
        }
});
});

