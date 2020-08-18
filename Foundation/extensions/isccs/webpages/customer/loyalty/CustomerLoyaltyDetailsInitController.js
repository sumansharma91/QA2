/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", 
          "scbase/loader!dojo/_base/kernel", 
          "scbase/loader!dojo/text", 
          "scbase/loader!extn/customer/loyalty/CustomerLoyaltyDetails",
          "scbase/loader!sc/plat/dojo/controller/ScreenController"], 
          function(
        		  _dojodeclare, 
        		  _dojokernel, 
        		  _dojotext, 
        		  _isccsManageMembership, 
        		  _scScreenController) {
    return _dojodeclare("extn.customer.loyalty.CustomerLoyaltyDetailsInitController", [_scScreenController], {
        screenId: 'extn.customer.loyalty.CustomerLoyaltyDetails'
    });
});
