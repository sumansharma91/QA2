/*
 * Licensed Materials - Property of IBM
 * IBM Call Center for Commerce (5725-P82)
 * (C) Copyright IBM Corp. 2013 All Rights Reserved.
 * US Government Users Restricted Rights - Use, duplication or disclosure restricted by GSA ADP Schedule Contract with IBM Corp.
 */
scDefine(["scbase/loader!dojo/_base/declare", "scbase/loader!dojo/_base/kernel", "scbase/loader!dojo/text", "scbase/loader!sc/plat/dojo/controller/ServerDataController"], function(
_dojodeclare, _dojokernel, _dojotext, _scServerDataController) {
    return _dojodeclare("extn.order.giftCard.GiftCardFulfillmentScreenBehaviorController", [_scServerDataController], {
        screenId: 'extn.order.giftCard.GiftCardFulfillmentScreen',
        mashupRefs: [{
            mashupId: 'giftCardFulfillment_getOrderReleaseList',
            mashupRefId: 'giftCardFulfillment_getOrderReleaseList_RefId'
        },{
            mashupId: 'giftCardFulfillment_getShipmentLineList',
            mashupRefId: 'giftCardFulfillment_getShipmentLineList_RefId'
        },{
            mashupId: 'giftCardFulfillment_getShipmentContainerList',
            mashupRefId: 'giftCardFulfillment_getShipmentContainerList_RefId'
        },{
            mashupId: 'extn_activateGiftCard',
            mashupRefId: 'extn_activateGiftCard_RefId'
        },{
            mashupId: 'extn_containerizeGiftCard',
            mashupRefId: 'extn_containerizeGiftCard_RefId'
        }]
    });
});