
scDefine(["scbase/loader!dojo/_base/declare","scbase/loader!extn/return/create/lines/ReturnOrderLinesExtnUI", "scbase/loader!sc/plat/dojo/utils/GridxUtils", "scbase/loader!sc/plat/dojo/utils/ModelUtils", "scbase/loader!isccs/utils/SearchUtils", "scbase/loader!isccs/utils/OrderUtils", "scbase/loader!sc/plat/dojo/utils/ScreenUtils"]
,
function(			 
			    _dojodeclare
			 ,
			    _extnReturnOrderLinesExtnUI, _scGridxUtils, _scModelUtils, _isccsSearchUtils, _isccsOrderUtils, _scScreenUtils
){ 
	return _dojodeclare("extn.return.create.lines.ReturnOrderLinesExtn", [_extnReturnOrderLinesExtnUI],{
	// custom code here
	
	extn_listGrid_afterPagingload : function (event, bEvent, ctrl, args) {
			_scScreenUtils.setModel(this, "getReturnReasonList_output", _scScreenUtils.getModel(this, "getReturnReasonList_output"), null);
		}
	
});
});

