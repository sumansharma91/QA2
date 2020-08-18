/*
 * .
 */

/*
 * 
 */

/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/
 
 Ext.namespace('sc.sma');

sc.sma.homeUIConfig = function() {
    return {
        xtype: "screen",
        sciId: "sc.sma.home",
        header: false,
        items: [{
            xtype: "container",
            sciId: "basePanel",
            border: false
		}]
    };
}
/*******************************************************************************
   IBM Confidential 
   OCO Source Materials 
   IBM Sterling Selling and Fullfillment Suite
   (c) Copyright IBM Corp. 2001, 2013 All Rights Reserved.
   The source code for this program is not published or otherwise divested of its trade secrets, 
   irrespective of what has been deposited with the U.S. Copyright Office. 
 *******************************************************************************/

var log = console ? console.log: Ext.log;
Ext.namespace('sc.sma');

sc.sma.home = function(config) {
    sc.sma.home.superclass.constructor.call(this, config);
    this.bindingData = {
        repeatable: false,
        sourceBinding: ['blank:_'],
        targetBinding: ['t:_']
    };
}
Ext.extend(sc.sma.home, sc.plat.ui.ExtensibleScreen, {
    classId: 'sc.sma.home',
    getUIConfig: sc.sma.homeUIConfig,
	namespaces: {
		source: ['blank'],
		target: ['t']
	},
    get: function(sciId) {
        return this.find('sciId', sciId).pop();
    }
});
