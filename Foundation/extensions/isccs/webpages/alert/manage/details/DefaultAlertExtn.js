
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/alert/manage/details/DefaultAlertExtnUI","scbase/loader!sc/plat/dojo/utils/ScreenUtils","scbase/loader!sc/plat/dojo/utils/WidgetUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnDefaultAlertExtnUI,
				_scScreenUtils,
				_scWidgetUtils
				
){ 
	return _dojodeclare("extn.alert.manage.details.DefaultAlertExtn", [_extnDefaultAlertExtnUI],
	{
	

		
		massageOutputForCallTagalert: function(
        dataValue, screen, widget, namespace, modelObj, options) 
		{
			if(modelObj.Inbox.ExceptionType == 'VSI_CALL_TAG')
			{
				_scWidgetUtils.hideWidget(this, "lblAlertDetails", true);
				_scWidgetUtils.showWidget(this, "extn_textarea", true, null);
			}
		return dataValue;
		}
	// custom code here
});
});

