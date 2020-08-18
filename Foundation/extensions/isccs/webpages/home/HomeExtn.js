
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/home/HomeExtnUI",
          	"scbase/loader!isccs/utils/UIUtils","scbase/loader!sc/plat/dojo/utils/ModelUtils",
          	"scbase/loader!sc/plat/dojo/utils/BaseUtils","scbase/loader!sc/plat/dojo/utils/ScreenUtils"]

,
function(			 
			    _dojodeclare
			 ,
			    _extnHomeExtnUI
			    ,_isccsUIUtils
			    ,_scModelUtils
				,_scBaseUtils
			    ,_scScreenUtils
){ 
	return _dojodeclare("extn.home.HomeExtn", [_extnHomeExtnUI],
	{
	
		getTNSSession: function(
		        event, bEvent, ctrl, args) 
		{
			this.getSessionModule();
		},
		
		getSessionModule: function(){
			var inputModel = {};
			//inputModel.GetProperty = {};
			inputModel = {};
			//console.log("getProperty",inputModel);
			_isccsUIUtils.callApi(this, inputModel, "extn_getProperty_referenceid");
			},
			
	handleMashupOutput: function( mashupRefId, modelOutput, mashupInput, mashupContext, applySetModel ) {
		if ( mashupRefId == "extn_getProperty_referenceid" ) 
		{
			//console.log("getPropertyOut: ",modelOutput);
			// START - Importing session.js module 
/*
			var jsElm = document.createElement("script");
			jsElm.type = "application/javascript";
			jsElm.src = _scModelUtils.getStringValueFromPath("GetProperty.PropertyValue", modelOutput);
			//console.log("Element",jsElm);						
			document.getElementsByTagName("head")[0].appendChild(jsElm);
*/
			var sessionResponseCode =_scModelUtils.getStringValueFromPath("SessionResponse.ResponseCode", modelOutput);
			var sessionResponseError =_scModelUtils.getStringValueFromPath("SessionResponseError.Error", modelOutput);
			var sessionResponseText =_scModelUtils.getStringValueFromPath("SessionResponse.ResponseText", modelOutput);
			if (!_scBaseUtils.isVoid(sessionResponseCode)){
//				alert("sessionResponseCode == "+sessionResponseCode);
				console.log("sessionResponseCode: ",sessionResponseCode);
				if(_scBaseUtils.equals(
						sessionResponseCode, "00000")){				
				}else{
					_scScreenUtils.showErrorMessageBox(this,sessionResponseText,null,null,null);
				}
			}else if (!_scBaseUtils.isVoid(sessionResponseError)){
				_scScreenUtils.showErrorMessageBox(this,sessionResponseError,null,null,null);
			}
			
	
		}
		
	}
		
		
});
});

