///////////////////////////////////////////////////////////////////////////////
//Function Type: Public
//This function is used to post the form to server after command button is clicked or
//onblur on a field requiring validation
///////////////////////////////////////////////////////////////////////////////
<!--
function doFormSubmit(passed_action) 
{
    document.body.style.cursor="wait";
	sc.csrf.addSecureTokenToForm(window.document.forms[0]);
	window.document.forms[0].action= passed_action;
	var html = "<input  name=\"ymaAction\"  type=\"hidden\"  value=\""+passed_action+"\" size=\"20\" />" ;
	window.document.forms[0].insertAdjacentHTML("beforeEnd",html) ; 
	window.document.forms[0].submit();
	return false ;
}
function doValidation(passed_action, validate, oldvalue, newvalue) 
{
	if (validate == "always" || (oldvalue != newvalue))
	{
		doFormSubmit(passed_action) ;
	}
	return false ;
}

//-->