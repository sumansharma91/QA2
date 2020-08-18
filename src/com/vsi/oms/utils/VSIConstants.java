package com.vsi.oms.utils;
/**
 * 
 * @author nish.pingle
 * 
 */
public interface VSIConstants {
	//Element Names
	public static final String ELE_ORDER_LINE = "OrderLine";
	public static final String ELE_CHARGE_TRANSACTION_DETAILS="ChargeTransactionDetail";
	public static final String ELE_ORDER_LINES = "OrderLines";
	public static final String ELE_ORDER = "Order";
	public static final String ELE_PERSON_INFO = "PersonInfo";
	public static final String ELE_CONTACT_PERSON_INFO = "ContactPersonInfo";
	public static final String ELE_CHAINED_FROM = "ChainedFrom";
	public static final String ELE_CONTAINER_DETAIL = "ContainerDetail";
	public static final String ELE_SHIPMENT_LINE = "ShipmentLine";
	public static final String ELE_CHARGE_TRANSACTION_REQ = "ChargeTransactionRequest";

	public static final String ELE_ITEM = "Item";
	public static final String ELE_CONTAINER = "Container";
	public static final String ELE_CONTAINERS = "Containers";
	public static final String ELE_CONTAINER_DETAILS = "ContainerDetails";
	public static final String ELE_RECEIPT_LINES = "ReceiptLines";
	public static final String ELE_RECEIPT_LINE = "ReceiptLine";
	public static final String ELE_SHIPMENT_LINES ="ShipmentLines";
	public static final String ELE_SHIPMENT = "Shipment";
	public static final String ELE_ORDER_STATUS = "OrderStatus";
	public static final String ELE_ORDER_STATUSES = "OrderStatuses";
	public static final String ELE_PAYMENT_METHOD ="PaymentMethod";
	public static final String ELE_TRANSACTION_LINE ="TransactionCharge";
	public static final String ELE_ORGANIZATION ="Organization";
	public static final String ELE_BUYER_ORGANIZATION ="BuyerOrganization";
	public static final String ELE_EXTN ="Extn";
	public static final String ATTR_SVC_NO = "SvcNo";     
	public static final String ATTR_EXTN_TOTAL_PRICE = "ExtnTotalPrice";    
	public static final String ATTR_MAX_CHARGE_LIMIT = "MaxChargeLimit";    
	public static final String ATTR_PIPELINE_ID = "PipelineId"; 
	public static final String ELE_ORDER_LIST = "OrderList";
	public static final String ELE_LINE_PRICE = "LinePriceInfo";
	public static final String ELE_PROMISE = "Promise";
	public static final String ELE_SHIP_ADDRESS = "ShipToAddress";
	public static final String ELE_NODE ="Node";
	public static final String ELE_SHIP_NODE_PERSON = "ShipNodePersonInfo";
	public static final String ELE_HEADER_CHARGES = "HeaderCharges";
	public static final String ELE_HEADER_CHARGE = "HeaderCharge";
	public static final String ELE_HEADER_TAXES = "HeaderTaxes";
	public static final String ELE_HEADER_TAX = "HeaderTax";
	public static final String ELE_LINE_TAXES = "LineTaxes";
	public static final String ELE_LINE_TAX = "LineTax";
	public static final String ELE_PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static final String ELE_ITEM_DETAILS = "ItemDetails";
	public static final String ELE_PAYMENT_METHODS="PaymentMethods";
	public static final String ELE_PAYMENT_DETAILS="PaymentDetails";
	public static final String ELE_CHARGE_TRANSACTION_REQUEST_LIST="ChargeTransactionRequestList";
	public static final String ELE_CHARGE_TRANSACTION_REQUEST="ChargeTransactionRequest";
	public static final String ELE_CUST_ADDITIONAL_ADDRESS = "CustomerAdditionalAddress";
	public static final String ELE_GET_DELIVERY_LEAD_TIME = "GetDeliveryLeadTime";
	//public static final String ELE_PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	
	//User Elements
	public static final String ELE_USER="User";

	//Inv Attributes
	public static final String ELE_PROMISE_LINE = "PromiseLine";
	public static final String ELE_PROMISE_LINES = "PromiseLines";
	public static final String ELE_INVENTORY = "Inventory";
	public static final String ATTR_NODE = "Node"; 
	public static final String ATTR_EXTN_TRANSFER_NO = "ExtnJDATransferNumber"; 

	public static final String ATTR_HOLD_TYPE = "HoldType";     
	public static final String ATTR_REASON_TEXT = "ReasonText";     
	public static final String ATTR_PIPELINE_KEY = "PipelineKey";     

	//XML Attributes
	public static final String ATTR_ORDER_NO = "OrderNo";
	public static final String ATTR_CUST_PO_NO = "CustomerPONo";
	public static final String ATTR_ENTERPRISE_CODE = "EnterpriseCode";
	public static final String ATTR_DOCUMENT_TYPE = "DocumentType";
	public static final String ATTR_PRIME_LINE_NO = "PrimeLineNo";
	public static final String ATTR_SUB_LINE_NO = "SubLineNo";
	public static final String ATTR_SHIP_NODE = "ShipNode";
	public static final String ATTR_SEQUENCE = "AttrSeq";
	public static final int ATTR_SEQ = 1;
	public static final String ATTR_PAYMENT_RULE_ID="PaymentRuleId";
	public static final String ATTR_MAX_ORDER_STATUS="MaxOrderStatus";
	public static final String ATTR_TOTAL_AUTHORIZED="TotalAuthorized";
	public static final String ATTR_EXTN_IS_SIGN_REQD_ITEM = "ExtnIsSignReqdItem";
	public static final String ATTR_IS_FIRM_PRE_NODE="IsFirmPredefinedNode";
	public static final String ATTR_REQUEST_AMOUNT = "RequestAmount";
	public static final String ATTR_OPEN_AUTHORIZED_AMOUNT= "OpenAuthorizedAmount";
	public static final String ATTR_ORDER_DATE = "OrderDate";
	public static final String ATTR_CUST_CUST_PO_NO = "CustCustPONo";
	public static final String ATTR_ORG_CODE = "OrganizationCode";
	public static final String ATTR_EMAIL_ID = "EmailID";
	public static final String ATTR_ERROR_CODE = "ErrorCode";
	public static final String ATTR_ERROR_DESC = "ErrorDescription";
	public static final String ATTR_ERROR_MESSAGE = "ErrorMessage";
	public static final String ATTR_PERSON_INFO_SHIP_TO = "PersonInfoShipTo";
	public static final String ATTR_TRANSFER_NO = "TransferNo";
	public static final String ATTR_STATUS = "Status";
	public static final String ATTR_CONTAINER_NO = "ContainerNo";
	public static final String ATTR_TRACKING_NO = "TrackingNo";
	public static final String ATTR_QUANTITY = "Quantity";
	public static final String ATTR_ITEM_ID = "ItemID";
	public static final String ATTR_UOM = "UnitOfMeasure";
	public static final String ATTR_PRODUCT_CLASS = "ProductClass";
	public static final String ATTR_RELEASE_NO = "ReleaseNo";
	public static final String ATTR_SHIPMENT_LINE_NO = "ShipmentLineNo";
	public static final String ATTR_SHIPMENT_SUB_LINE_NO = "ShipmentSubLineNo";
	public static final String ATTR_STORE_ID = "StoreID";
	public static final String ATTR_MAX_LINE_STATUS = "MaxLineStatus";
	public static final String ATTR_MODIFICATION_CODE ="ModificationCode";
	public static final String ATTR_MODIFICATION_TEXT ="ModificationText";
	public static final String ATTR_FULFILLMENT_TYPE ="FulfillmentType";
	public static final String ATTR_PROCURE_FROM_NODE ="ProcureFromNode";
	public static final String ATTR_MIN_LINE_STATUS = "MinLineStatus";
	public static final String ATTR_SHIPMENT_KEY = "ShipmentKey";
	public static final String ATTR_RECEIVING_NODE = "ReceivingNode";
	public static final String ATTR_RECEIPT_LINE_NO = "ReceiptLineNo";
	public static final String ATTR_ORDER_HEADER_KEY = "OrderHeaderKey";
	public static final String ATTR_CHARGE_TRAN_REQ_KEY="ChargeTransactionRequestKey";
	public static final String ATTR_MAX_REQ_AMT = "MaxRequestAmount";
	public static final String ATTR_CHARGE_TRAN_REQ_ID="ChargeTransactionRequestId";
	public static final String ATTR_CHARGE_TRANSACTION_KEY = "ChargeTransactionKey";
	public static final String ATTR_ORDER_LINE_KEY = "OrderLineKey";
	public static final String ATTR_CHAINED_FROM_ORDER_HEADER_KEY = "ChainedFromOrderHeaderKey";
	public static final String ATTR_CONFIRM_SHIP = "ConfirmShip";
	public static final String ATTR_SHIPMENT_LINE_KEY = "ShipmentLineKey";
	public static final String ATTR_MIN_ORDER_STATUS = "MinOrderStatus";
	public static final String ATTR_BASE_DROP_STATUS = "BaseDropStatus";
	public static final String ATTR_CHANGE_FOR_ALL_AVAILABLE_QTY = "ChangeForAllAvailableQty";
	public static final String ATTR_SELECT_METHOD = "SelectMethod";
	public static final String ATTR_TRANSACTION_ID = "TransactionId";
	public static final String ATTR_LINE_TYPE ="LineType";
	public static final String ATTR_ORDER_RELEASE_KEY = "OrderReleaseKey";
	public static final String ATTR_ORDER_TYPE = "OrderType";
	public static final String ATTR_PICK_TICKET_NO = "PickticketNo";
	public static final String ATTR_REFERENCE_1 = "Reference_1";
	public static final String ATTR_SHIP_CUST_PO_NO = "CustomerPoNo";
	public static final String ATTR_ALLOCATION_RULE_ID = "AllocationRuleID";
	public static final String ATTR_LINE_KEY ="LineKey";
	public static final String ATTR_ENTRY_TYPE = "EntryType";
	public static final String ATTR_CUSTOMER_REWARDS_NO="CustomerRewardsNo";
	public static final String ATTR_PAYMENT_TYPE = "PaymentType";
	public static final String ATTR_TOTAL_CHARGED = "TotalCharged";
	public static final String ATTR_PAYMENT_REFERENCE_1 = "PaymentReference1";
	public static final String ATTR_REDEEM_STATUS = "RedeemStatus";
	public static final String ATTR_PROCESSED_AMOUNT = "ProcessedAmount";
	public static final String ATTR_ACTIVE_FLAG = "ActiveFlag";
	public static final String ATTR_ERROR_REASON = "ErrorReason";
	public static final String ATTR_ERROR_TYPE = "ErrorType";
	public static final String ATTR_EXCEPTION_TYPE = "ExceptionType";
	public static final String ATTR_EXPIRATION_DAYS = "ExpirationDays";
	public static final String ATTR_QUEUE_ID = "QueueId";
	public static final String ATTR_NAME = "Name";
	public static final String ATTR_REFERENCE_TYPE = "ReferenceType";
	public static final String ATTR_VALUE = "Value";
	public static final String ATTR_DESCRIPTION = "Description";
	public static final String ATTR_ACTION ="Action";
	public static final String ATTR_PAYPAL_TRANSACTION_AMT_QRY = "TransactionAmountQryType";
	public static final String ATTR_PAYPAL_TRANSACTION_ID = "TransactionID";
	public static final String ATTR_PAYPAL_TRANSACTION_AMT = "TransactionAmount";
	public static final String ATTR_PAYPAL_TRANSACTION_Key ="TranKey";
	public static final String STORE_NO ="StoreNumber";
	public static final String CURRENCY_CODE="CurrencyCode";
	public static final String AUTHORIZATION_NUMBER="AuthorizationNumber";
	public static final String TRANSACTION_NUMBER="TransactionNumber";
	public static final String TRANSACTION_AMOUNT="TransactionAmount";
	public static final String MESSAGE_TYPE="MessageType";
	public static final String ACTION_CODE="ActionCode";
	public static final String TNS_ORDER_Id="TNSOrderID";
	public static final String CHARGE_TRAN_KEY="ChargeTransactionKey";
	public static final String REGISTER_NUMBER="RegisterNumber";
	public static final String TRANSACTION_TYPE="TransactionType";
	public static final String TRANSACTION_DATE="TransactionDate";
	public static final String IS_SETTLED="SettledFlag";
	public static final String GIFT_CARD = "GIFT_CARD";
	public static final String GIFT_CARD_VAR = "GIFT_CARD_VARIABLE";
	public static final String ATTR_EXTN_ITEM_TYPE="ExtnItemType";
	public static final String LINETYPE_PUS="PICK_IN_STORE";
	public static final String LINETYPE_STS="SHIP_TO_STORE";
	public static final String ATTR_LINE_TOTAL="LineTotal";
	public static final String ATTR_TAX_PAYER_ID = "TaxpayerId";
	public static final String ATTR_AUTH_CODE = "AuthCode";
	public static final String ATTR_AUTH_TRANSACTION_ID = "TransactionId";
	public static final String ATTR_ORDER_TOTAL = "OrderTotal";
	public static final String ATTR_DUPLICATE_ORDER="DuplicateOrder";
	public static final String ATTR_SELLING_OUTLET = "EnteredBy";
	public static final String ATTR_CUSTOMER_FIRST_NAME = "CustomerFirstName";
	public static final String ATTR_CUSTOMER_LAST_NAME = "CustomerLastName";
	public static final String ATTR_DISTANCE_UOM= "DistanceUOMToConsider";
	public static final String ATTR_DISTANCE_CONSIDER = "DistanceToConsider";
	public static final String ATTR_ZIPCODE = "ZipCode";
	public static final String ATTR_COUNTRY = "Country";
	public static final String ATTR_CITY = "City";
	public static final String ATTR_STATE = "State";
	public static final String ATTR_ADDRESS1 = "AddressLine1";
	public static final String ATTR_ADDRESS2 = "AddressLine2";
	public static final String ATTR_NODE_TYPE = "NodeType";
	public static final String ATTR_MODIFICATION_REASON_CODE = "ModificationReasonCode";
	public static final String ATTR_MODIFICATION_REASON_TEXT = "ModificationReasonText";
	public static final String ATTR_UPCCODE ="UPCCode";
	public static final String ATTR_EXTN_ACT_ITEMID ="ExtnActItemID";
	public static final String ATTR_EXTN_MKTPLACELINE_SHIPAMT ="ExtnMktplaceLineShipAmt";
	public static final String ATTR_EXTN_MKTPLACELINE_SHIPDISCOUNT ="ExtnMktplaceLineShipDiscount";
	public static final String ATTR_EXTN_MKTPLACELINE_SHIPTAX ="ExtnMktplaceLineShipTax";
	public static final String ATTR_MODIFICATION_REASON ="ModificationReasonCode";
	public static final String ATTR_EXTN_IS_DISCONTINUED_ITEM="ExtnIsDiscontinuedItem";
	public static final String ATTR_EXTN_CHANNEL_ITEM_ID="ExtnChannelItemID";
	public static final String ATTR_PROMO_NAME="PromoName";
	public static final String ATTR_PROMO_DESC="PromoDesc";
	public static final String ATTR_CHARGE_CATEGORY="ChargeCategory";
	public static final String ATTR_CHARGE_AMOUNT="ChargeAmount";
	public static final String ATTR_TAX_NAME="TaxName";
	public static final String ATTR_TAX="Tax";
	public static final String ATTR_EXTN_LINE_TAX_PER_UNIT = "ExtnTaxPerUnit";
	//public static final String ATTR_PAYMENT_RULE_ID = "PaymentRuleID";
	public static final String ATTR_AUTHORIZATION_ID = "AuthorizationID";
	public static final String ATTR_REQ_CANCEL_DATE = "ReqCancelDate";
	public static final String ATTR_CHARGE_TYPE="ChargeType";
	public static final String ATTR_CHARGE_TRANSACTION_REQUEST_SEQUENCE="ChargeTransactionRequestSequence";
	public static final String ATTR_OPERATION="Operation";
	public static final String ATTR_ADDR_LINE_1 = "AddressLine1";
//User attributes
	public static final String ATTR_LOGIN_ID="Loginid";
public static final String ATTR_USER_KEY="UserKey";
	public static final String ATTR_ADDR_LINE_2 = "AddressLine2";
	public static final String ATTR_CUST_TYPE = "CustomerType";
	public static final String ATTR_DELIVERY_METHOD = "DeliveryMethod";
	public static final String ATTR_EMAIL_OPT_IN = "EmailOptIn";
	public static final String ATTR_PHONE_OPT_IN = "PhoneOptIn";
	public static final String ATTR_POSTAL_OPT_IN = "PostalOptIn";
	public static final String ATTR_DAY_FAX_NO = "DayFaxNo";
	public static final String ATTR_EVENING_FAX_NO = "EveningFaxNo";
	public static final String ATTR_EVENING_PHONE  = "EveningPhone";
	public static final String ATTR_JOB_TITLE  = "JobTitle";
	public static final String ATTR_MIDDLE_NAME  = "MiddleName";
	public static final String ATTR_MOBILE_PHONE  = "MobilePhone";
	public static final String ATTR_CUSTOMER_EMAIL_ID  = "CustomerEMailID";
	public static final String ATTR_IS_SHIP_TO  = "IsShipTo";
	public static final String ATTR_RESET  = "Reset";
	public static final String ATTR_IS_COMMERCIAL_ADDRESS  = "IsCommercialAddress";
	public static final String VSI_REMORSE_DURATION = "VSI_REMORSE_DURATION";

	// Added for Wrong Store Received(Ship To Store)
	public static final String ATTR_WRONG_STORE = "WrongStore";
	public static final String API_CREATE_ORDER = "createOrder";
	public static final String ATTR_ORIGINAL_ORDERED_QTY = "OriginalOrderedQty";
	public static final String ATTR_ORDER_TYPE_VALUE = "WEB";
	public static final String ATTR_ENTRY_TYPE_VALUE = "DUMMY";
	// API Names
	public static final String API_GET_ORDER_LIST = "getOrderList";
	public static final String API_CHANGE_ORDER_STATUS = "changeOrderStatus";
	public static final String API_GET_ORGANIZATION_LIST = "getOrganizationList";
	public static final String API_CREATE_CHAINED_ORDER = "createChainedOrder";
	public static final String API_GET_ORDER_LINE_LIST = "getOrderLineList";
	public static final String API_GET_SHIPMENT_LIST = "getShipmentList";
	public static final String API_CHANGE_SHIPMENT = "changeShipment";
	public static final String API_CONFIRM_SHIPMENT = "confirmShipment";
	public static final String API_RECEIVE_ORDER = "receiveOrder";
	public static final String API_CHANGE_ORDER = "changeOrder";
	public static final String API_CREATE_SHIPMENT = "createShipment";
	public static final String API_RELEASE_ORDER = "releaseOrder";
	public static final String API_GET_ORDER_RELEASE_LIST = "getOrderReleaseList";
	public static final String API_CREATE_EXCEPTION = "createException";
	public static final String API_GET_ORDER_DETAILS = "getOrderDetails";
	public static final String API_GET_CHARGE_TRANSACTION_LIST = "getChargeTransactionList";
	public static final String API_MANAGE_CHARGE_TRAN_REQ = "manageChargeTransactionRequest";
	public static final String API_UNSCHEDULE_ORDER ="unScheduleOrder";
	public static final String API_SCHEDULE_ORDER ="scheduleOrder";
	public static final String API_GET_CARRIER_SERVICE_LIST="getCarrierServiceList";
	public static final String API_GET_SCAC_LIST="getScacList";
	public static final String API_MANAGE_CUSTOMER="manageCustomer";
	
	//User APIs
	public static final String API_MOD_USER_HIERARCHY="modifyUserHierarchy";
	public static final String API_CREATE_USER_HIERARCHY="createUserHierarchy";
	public static final String API_GET_USER_LIST="getUserList";

	// Templates
	public static final String TEMPLATE_GET_ITEM_LIST = "global/template/api/getItemList.xml";
	public static final String TEMPLATE_MANAGECUSTOMER_CRMFAIL = "global/template/api/manageCustomer_CRMfailCreateSterlingCustomer.xml";
	public static final String TEMPLATE_GET_ORDER_LIST = "global/template/api/getOrderList.xml";
	public static final String TEMPLATE_GET_ORDER_LIST_INTERNATIONAL_HOLD = "global/template/api/getOrderListIntHold.xml";
	public static final String TEMPLATE_CHANGE_ORDER = "global/template/api/VSIChangeOrder.xml";
	public static final String TEMPLATE_GET_CARRIER_SERVICE_LIST="global/template/api/getCarrierServiceList_VSIMarketplaceFulfillment.xml";
	public static final String TEMPLATE_GET_ITEM_LIST_VSIModifyCategoryItem = "global/template/api/getItemList_VSIModifyCategoryItem.xml";
	public static final String TEMPLATE_MANAGE_CUSTOMER_VSIManageCustomerUE = "global/template/api/manageCustomer_VSIManageCustomerUE.xml";
	public static final String TEMPLATE_ORDER_ORDER_HEADER_KEY = "global/template/api/order_OrderHeaderKey.xml";
	public static final String TEMPLATE_ORDER_LIST_ORDER_MONITOR = "global/template/api/order_OrderList_OrderMonitor.xml";
	public static final String TEMPLATE_VSI_GET_CUSTOMER_LIST = "global/template/api/getCustomerList_VSIGetCustomerListUE.xml";
	public static final String TEMPLATE_VSI_GET_CUSTOMER_DETAILS = "global/template/api/getCustomerDetails_VSIGetCustomerDetailsUE.xml";
	public static final String TEMPLATE_VSI_GET_PAYMENT_METHODS_LIST="global/template/api/VSIGetPaymentMethodsList.xml";
	public static final String TEMPLATE_GET_ORD_LIST_VSI_CNCL_EMAIL = "global/template/api/VSICancelEmailTemplate.xml";
	public static final String TEMPLATE_GET_SHIPMENT_LIST_VSI_CHECK_SHIPMENT = "global/template/api/getShipmentList_VSICheckShipmentDetailsAPI.xml";
	public static final String TEMPLATE_GET_ORDER_LIST_VSI_TRIM_DTC_CFM_MSG = "global/template/api/getOrderList_VSITrimDTCOrderConfirmationMsg.xml";
	public static final String TEMPLATE_GET_CALENDAR_LIST_VSI_TRIM_DTC_CFM_MSG = "global/template/api/getCalendarList_VSITrimDTCOrderConfirmationMsg.xml";
	
	//User API template
	public static final String TEMPLATE_VSI_LOAD_USER="global/template/api/VSILoadUser.xml";

	//SERVICE Names
	public static final String SERVICE_CALCULATE_REWARDS_INPUT="VSICalculateRewardsInput";
	public static final String SERVICE_MODIFY_CUSTOMER_INPUT="VSISaveCustInput";
	public static final String SERVICE_CUSTOMER_SEARCH = "VSICustomerService";
	public static final String SERVICE_CREATE_CUSTOMER_INPUT = "TestXSL";
	public static final String SERVICE_GET_VOUCHER_LIST = "VSIGetVouchers";
	public static final String SERVICE_CHANGE_VOUCHER = "VSIChangeVoucher";

	public static final String SERVICE_CREATE_PAYPAL_TRANSACTION = "VSICreatePayPalTransaction";
	public static final String SERVICE_CREATE_AJB_SETTLEMENT = "VSICreateAJBSettlement";
	public static final String SERVICE_VOUCHER_FEED = "VSIVoucherFeedToSalesAudit";

	public static final String SERVICE_JDA_XSL_REQUEST = "VSIInvokeJDAXSL";
	public static final String SERVICE_JDA_FORCE_ALLOCATION_WEB_SERVICE="VSIInvokeJDAForceAllocationWebService";

	public static final String SERVICE_GET_PAYPAL_TRANSACTION = "VSIGetPayPalTransaction";
	public static final String SERVICE_CHANGE_PAYPAL_TRANSACTION = "VSIChangePayPalTransaction";
	public static final String SERVICE_AUTHORIZED_ORDER = "VSIAuthorizedOrders";
	public static final String SERVICE_AWAIT_CUST_PICKUP = "VSIAwaitCustPickup";
	
	public static final String SERVICE_VSI_STH_ORDER_CONFIRMATION = "VSISTHOrderConfirmationSyncService";
	public static final String SERVICE_VSI_SEND_CONFIRM_EMAIL = "VSISendConfirmEmail";
	public static final String SERVICE_VSI_BOPS_EMAIL_ORDER = "VSIBOPSEmailOrderSyncService";

	public static final String ATTR_HOLD_FLAG = "HoldFlag";
	public static final String ATTR_HOLD_REASON_CODE = "HoldReasonCode";
	public static final String SERVICE_CREATE_TNS_AUTH="VSICreateTNSAuthRecords";
	public static final String SERVICE_GET_TNS_AUTH="VSIGetTNSAuthRecords";
	public static final String SERVICE_GET_SHIP_RESTRICTED_ITEM ="VSIGetShipRestrictedItem";

	//API Templates

	//Queue ID

	//Other String Constants
	public static final String REG_NUMBER = "88";
	public static final String GROUP_NUMBER = "91";
	public static final String LINE_ITEM_STATUS_NUMBER = "00";
	public static final String ORDER_STATUS_NUMBER = "3200";
	public static final String VSICOM_ENTERPRISE_CODE = "VSI.com";
	public static final String DOCUMENT_TYPE = "0001";
	public static final String SELECT_METHOD_WAIT = "WAIT";
	public static final String HOLD_PAYMENT_FAILURE = "VSI_PAYMENT_FAILURE";
	public static final String REASON_PAYMENT_FAILED ="Payment Failed";     
	public static final String DATE_STAMP_FORMAT = "yyyyMMddHHmmss";    
	public static final String HOLD_SVS_UNAVAILABLE = "HOST_SVS_UNAVAILABLE";     
	public static final String REASON_SVS_UNAVAILABLE ="SVS is Unavailable";    
	public static final String NEW_PIPELINE_ID = "VSISTORE";
	public static final String STATUS_SCHEDULED = "Scheduled";
	public static final String STATUS_CREATED = "Created";
	public static final String STATUS_CANCELLED = "Cancelled";
	public static final String STATUS_STORE_ACK = "Store Acknowledged";
	public static final String STATUS_RELEASED = "Released";
	public static final String STATUS_AWAIT_CUST = "Awaiting Customer Pickup";
	public static final String STATUS_PICK_PACK = "Pick/Pack";
	public static final String STATUS_RESTOCK = "Restock";
	public static final String HOLD_SVS_DECLINE = "HOST_SVS_DECLINE";
	public static final String STATUS_PICKED = "Picked";
	public static final String STATUS_NOACTION = "No Action";
	public static final String STATUS_ACKNOWLEDGE = "Acknowledge";
	public static final String STATUS_STS_COMPLETE = "STS Complete";
	public static final String STATUS_PARTL_STS_COMPLETE = "Partially STS Complete";
	public static final String STATUS_STORE_RECEIVED = "Store Received";
	public static final String STATUS_PARTL_STORE_RECEIVED = "Partially Store Received";
	public static final String ENT_VS_AMAZON ="VS_AMAZON";
	public static final String ENT_NUTRITION_DEPOT ="NUTRITION_DEPOT";
	public static final String ENT_EBAY ="EBAY";
	public static final String ENT_WALMART ="WALMART";
	public static final String ENT_JET ="JET";
	public static final String ZERO_DOUBLE = "0.00";

	public static final String STATUS_CODE_SCHEDULED = "1400";

	//Payments
	public static final String PAYMENT_MODE_CUS_ACCOUNT = "CUSTOMER_ACCOUNT";
	public static final String PAYMENT_MODE_VOUCHERS = "VOUCHERS";
	public static final String PAYMENT_MODE_GC = "GIFT_CARD";
	public static final String PAYMENT_MODE_CC = "CREDIT_CARD";
	public static final String PAYMENT_MODE_PP = "PAYPAL";
	public static final String PAYMENT_MODE_CASH = "CASH";
	public static final String PAYMENT_MODE_CHECK = "CHECK";
	public static final String PAYMENT_STATUS_CHARGE = "CHARGE";
	public static final String PAYMENT_STATUS_AUTHORIZATION = "AUTHORIZATION";
	public static final String PAYMENT_FAILURE = "PAYMENT_FAILURE";
	public static final String PAYMENT_FAILURE_MSG = "Invalid Payment Tender";
	public static final String ZEROTOTAL = "ZEROTOTAL";
	public static final String ORDER_PAYMENTMETHOD_XPATH = "Order/PaymentMethods";
	public static final String AUTH_SUCCESS = "AuthSuccess";
	public static final String CHARGE = "CHARGE"; 
	public static final String PAYMENT_CHARGED_MSG = "CHARGED";
	public static final String PAYMENT_MODE_PAYPAL = "PAYPAL";
	public static final String AUTHORIZATION = "AUTHORIZATION";
	public static final String PAYMENT_AUTHORIZED_MSG = "AUTHORIZED"; 
	
	//ShipNodes
	public static final String SHIP_NODE_CC="CALL_CENTER";
	//LineTypes
	public static final String LINETYPE_STH = "SHIP_TO_HOME";

	//DB Constants
	public static final String SEQ_ID = "SEQ_ID";
	public static final String FROM_DUAL = ".NEXTVAL SEQ_ID FROM DUAL";
	public static final String SELECT = "SELECT ";
	public static final String SEQ_VSI_TRAN_NO = "SEQ_VSI_TRAN_NO";
	public static final String SEQ_VSI_CUST_ID = "VSI_CUSTOMER_ID";
	public static final String SEQ_VSI_CC_NO = "SEQ_VSI_CC_NO";
	public static final String SEQ_CC_CTR_ID = "SEQ_CC_CTR_ID";
	public static final String SEQ_CC_CTR = "SEQ_CC_CTR";

	//Exception Code and Messages
	public static final String ERROR_CODE_0001 = "EXTN_0001";
	public static final String ERROR_CODE_0001_DESC = "Unable to get DB connection";
	public static final String ERROR_CODE_0002 = "EXTN_0002";
	public static final String ERROR_CODE_0002_DESC = "Unable to get Transaction Number sequence";

	public static final String DT_STR_TS_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
	//Return Orders related constants
	public static final String RETURN_DOCUMENT_TYPE = "0003";
	public static final String FLAG_Y = "Y";
	public static final String FLAG_N = "N";
	//Added new constants - 7thAug 2014 for findInventoryformultiple DC service 
	public static final String ATTR_REQUIRED_QTY = "RequiredQty";
	//Constants for Remorse Hold
	public static final String ELE_ORDER_DATES = "OrderDates";
	public static final String ELE_ORDER_DATE = "OrderDate";
	public static final String ATTR_DATE_TYPE_ID = "DateTypeId";
	public static final String ATTR_ACTUAL_DATE = "ActualDate";
	public static final String ELE_ORDER_HOLD_TYPES = "OrderHoldTypes";
	public static final String ELE_ORDER_HOLD_TYPE = "OrderHoldType";
	public static final String ATTR_CODE_TYPE = "CodeType";
	public static final String ELEMENT_COMMON_CODE = "CommonCode"; 
	public static final String API_COMMON_CODE_LIST = "getCommonCodeList";
	public static final String ATTR_CODE_VALUE = "CodeValue";
	public static final String ATTR_CODE_SHORT_DESCRIPTION = "CodeShortDescription";
	public static final String VSI_STS_REMORSE_PERD="VSI_REMORSE_PERD";
	public static final String STATUS_CREATE="1100";

	//Constants added on 21/8/2014 for Returns - ES POS STH 
	public static final String POS_ENTRY_TYPE="STORE";
	public static final String ENTERPRISE_SELLING= "ES";
	public static final String VSI_INCLD_RETURN_REASON="INCLD_RETURN_REASON";
	public static final String ATTR_RETURN_REASON="ReturnReason";
	public static final String SHIPPING_CHARGE_CTGY="Shipping";
	public static final String SHIPPING_TAX="ShippingTax";
	public static final String ATTR_ORD_QTY="OrderedQty";
	public static final String ATTR__ORG_ORD_QTY="OriginalOrderedQty";

	public static final String VSI_CC_HLD_PRD="VSI_CC_HLD_PRD";

	//WEB related constants
	public static final String ELE_PROMOTIONS="Promotions";
	public static final String ELE_PROMOTION="Promotion";
	public static final String PAYMENT_MODE_OGC = "ONLINE_GIFT_CARD";
	//Image Id related attributes
	public static final String ATTR_IMAGE_ID = "ImageID";
	public static final String ATTR_IMAGE_LOCATION = "ImageLocation";
	public static final String ELE_PRIMARY_INFORMATION = "PrimaryInformation";
	public static final String IMAGE_LOCATION = "imageLocation";
	public static final String IMAGE_SUFFIX = "imageSuffix";
	//START - Tokenization properties
	public static final String TNS_MERCHANT_ID = "TNS_MERCHANT_ID";
	public static final String TNS_METHOD_NAME = "TNS_METHOD_NAME";
	public static final String TNS_END_POINT = "TNS_END_POINT_URL";
	public static final String TNS_AVOID_SSL = "TNS_AVOID_SSL";
	//END - Tokenization properties 

	public static final String API_GET_ITEM_LIST ="getItemList";
	public static final String API_GET_ORG_HIERARCHY ="getOrganizationHierarchy";

	//Templates
	public static final String TEMPLATE_GET_ITEM_LIST_VSIManageCustomerUE = "global/template/api/manageCustomer_VSIManageCustomerUE.xml";

	//Constants added on Feb. 06, 2017 for VSIModifyCategoryItem
	public static final String ATTR_CAT_PATH = "CategoryPath";
	public static final String ELE_CAT = "Category";
	public static final String ELE_CAT_ITEM_LIST = "CategoryItemList";
	public static final String ELE_CAT_ITEM = "CategoryItem";
	public static final String ENT_VSI_CAT = "VSI-Cat";
	public static final String VSI_CAT_PATH = "/VSI-CatMasterCatalog/ALL_PRODS";
	public static final String ELE_MOD_CAT_ITEM = "ModifyCategoryItems";
	public static final String ATTR_CALL_ORG_CODE = "CallingOrganizationCode";
	public static final String UOM_EACH = "EACH";
	public static final String ACTION_CREATE = "Create";
	public static final String ACTION_CAPS_CREATE = "CREATE";
	public static final String API_MODIFY_CATEGORY_ITEM = "modifyCategoryItem";
	public static final String API_GET_CUSTOMER_LIST = "getCustomerList";
	public static final String API_GET_CUSTOMER_DETAILS = "getCustomerDetails";
	public static final String ELE_CUSTOMER = "Customer";
	public static final String ATTR_CUSTOMER_ID = "CustomerID";
	public static final String ATTR_CUSTOMER_KEY = "CustomerKey";
	public static final String ATTR_DEFAULT = "DEFAULT";
	public static final String ELE_CategoryList = "CategoryList";;
	public static final String INT_ZER0_NUM = "0";
	public static final String ATTR_EXTERNAL_CUSTOMER_ID = "ExternalCustomerID";
	public static final String ATTR_BILL_TO_ID = "BillToID";
	public static final String ELE_BILL_TO_ID = "BillToID";
	public static final String API_MANAGE_CUST = "manageCustomer";
	public static final String API_VSI_SAVE_NEW_CUST = "VSISaveNewCustomer";
	public static final String API_VSI_MODIFY_CUST = "VSIModifyCustomer";

	//Constants added on Feb. 08, 2017 for VSIGetCustomerListUE
	public static final String ELE_CUST_CONTACT_LIST = "CustomerContactList";
	public static final String ELE_CUST_CONTACT = "CustomerContact";
	public static final String ATTR_HEALTHY_AWARDS_NO = "HealthyAwardsNo";
	public static final String ATTR_HEALTHY_REWARD_PTS = "HealthyRewardPoints";
	public static final String ELE_CUST_LIST = "CustomerList";
	public static final String ATTR_CUST_KEY = "CustomerKey";

	//Constants added on Feb. 15, 2017 for VSIManageCustomerUE
	public static final String ATTR_OPERATION_VALUE_MANAGE = "Manage";
	public static final String ATTR_BUYER_ORG_CODE = "BuyerOrganizationCode";
	//County Load attributes
	public static final String ATTR_EXTN_CITY="ExtnCity";
	public static final String ATTR_EXTN_STATE="ExtnState";
	public static final String ATTR_EXTN_ZIP_CODE="ExtnZipCode";
	public static final String ATTR_EXTN_COUNTRY="ExtnCountry";
	public static final String ATTR_EXTN_COUNTY="ExtnCounty";
	public static final String ATTR_COUNTY="County";
	public static final String ATTR_EXTN_CITY_COUNTY_KEY="ExtnCityCountyKey";
	//County Elements
	public static final String ELE_VSI_COUNTY_DATA="VSICountyData";
	//County API services
	public static final String SERVICE_VSI_GET_COUNTY_DATA="VSIGetCountyData";
	public static final String SERVICE_VSI_MODIFY_COUNTY_DATA="VSIModifyCountyData";
	public static final String SERVICE_VSI_CREATE_COUNTY_DATA="VSICreateCountyData";
	
	//Constants added on Feb. 21, 2017 for VSIUpdateUserContactDetails
	public static final String ELE_CUST_ADDITIONAL_ADDRESS_LIST = "CustomerAdditionalAddressList";
	public static final String ATTR_BUYER_ORG = "BuyerOrganization";
	public static final String API_GET_ORG_HEIRARCHY = "getOrganizationHierarchy";
	public static final String TEMPLATE_VSI_GET_ORDER_LIST = "global/template/api/getOrderList_VSICRMCustomerSync.xml";
	public static final String ATTR_EMPTY = "";
	public static final String TEMPLATE_VSI_GET_CUSTOMER_CONDITION = "global/template/api/CustomerCondition_getOrganizationHierarchy.xml";

	public static final String ELE_CUST_ADDL_ADDR_LIST = "CustomerAdditionalAddressList";
	public static final String ELE_CUST_ADDL_ADDR = "CustomerAdditionalAddress";
	public static final String ELE_DEFAULT_SHIP_TO_ADDR = "DefaultShipToAddress";
	public static final String ELE_DEFAULT_BILL_TO_ADDR = "DefaultBillToAddress";
	public static final String ELE_SOAP_ENVELOPE = "soap:Envelope";
	public static final String ELE_SOAP_BODY = "soap:Body";
	public static final String ELE_SOAP_FAULT = "soap:Fault";
	public static final String ELE_GET_CUST_DATA_RESP = "GetCustomerDataResponse";
	public static final String ELE_GET_CUST_DATA_RESULT = "GetCustomerDataResult";
	public static final String ELE_CUST_ADDRES = "CustomerAddresses";
	public static final String ELE_CUST_ADDR_WS = "CustomerAddressWS";
	public static final String ELE_ITEMS = "Items";
	public static final String ELE_NUMBER = "Number";

	public static final String ATTR_IS_BILL_TO = "IsBillTo";
	public static final String ATTR_IS_DEFAULT_SHIP_TO = "IsDefaultShipTo";
	public static final String ATTR_IS_DEFAULT_BILL_TO = "IsDefaultBillTo";
	public static final String ATTR_IS_CUST_AVAIL = "IsCustomerAvailable";
	public static final String ATTR_FIRST_NAME = "FirstName";
	public static final String ATTR_LAST_NAME = "LastName";
	public static final String ATTR_ADDR_1 = "Address1";
	public static final String ATTR_ADDR_2 = "Address2";
	public static final String ATTR_POSTAL_CODE = "PostalCode";
	public static final String ATTR_TELEPHONE_NUMBER = "TelephoneNumber";
	public static final String ATTR_EMAIL_ADDR = "EmailAddress";
	public static final String ATTR_CUST_CONTACT_ID = "CustomerContactID";
	public static final String ATTR_DAY_PHONE = "DayPhone";
	public static final String ATTR_CUST_ADDL_ADDR_ID = "CustomerAdditionalAddressID";
	public static final String ATTR_ADDR_ID = "AddressID";
	
	public static final String STATUS_10 = "10";

	public static final String CUSTOMER_UNAVAILABLE = "<Customer IsCustomerAvailable=\"N\" />";
	public static final String CUSTOMER_LIST_UNAVAILABLE = "<CustomerList IsCustomerAvailable=\"N\" />";

	// Get Delivery Lead Time UE Constants
	public static final String TEMPLATE_GET_ORG_HIER_CUST_COND = "global/template/api/CustomerCondition_getOrganizationHierarchy.xml";
	public static final String STANDARD = "STANDARD";
	public static final String ATTR_SCAC = "SCAC";
	public static final String ATTR_DEL_LEAD_TIME = "DeliveryLeadTime";
	public static final String ATTR_TRANSIT_UNIT_OF_MEASURE = "TransitUnitOfMeasure";
	public static final Object SCAC_2DAY = "TWODAY";
	public static final Object SCAC_NEXTDAY = "NEXTDAY";
	public static final String TWO = "2";
	public static final String ONE = "1";
	public static final String SERVICE_GET_TIME_IN_TRANSIT = "VSIGetExtnTimeInTransit";
	public static final String ELE_EXTN_TIME_IN_TRANSIT = "ExtnTimeInTransit";
	public static final String FIVE = "5";
	public static final String ATTR_SELLING_ORG_CODE = "SellingOrganizationCode";

	public static final String ATTR_OVERRIDE = "Override";
	public static final String ACTION_CANCEL = "Cancel";
	public static final String ELE_ORDER_AUDIT = "OrderAudit";
	public static final String ELE_ORDER_RELEASE = "OrderRelease";
	public static final String TEMPLATE_GET_ORD_REL_LIST_SEND_RELEASE = "/global/template/api/getOrderReleaseList_VSISendRelease.xml";
	public static final String SERVICE_PROCESS_RELEASE = "VSIProcessRelease";
	public static final String ELE_ORDER_STATUS_CHANGE = "OrderStatusChange";
	public static final String STATUS_CODE_SENT_TO_NODE = "3200.600";
	public static final String TRANS_REL_ACK_SALES = "RELEASE_ACKNOWLEDGEMENT.0001.ex";
	public static final String TRANS_REL_ACK_TO = "RELEASE_ACKNOWLEDGEMENT.0006.ex";
	public static final String TRANS_SEND_RELEASE = "VSI_SENT_TO_WHNode.0001.ex";
	public static final String TRANS_SEND_RELEASE_TO = "VSI_SENT_TO_WHNode.0006.ex";
	public static final String SERVICE_INVK_JDA_FRCE_ALLCTN_SERVICE = "VSIInvokeJDAForceAllocationWebService";
	public static final String TEMPLATE_ORD_REL_ORD_REL_KEY = "/global/template/api/common_OrderRelease_OrderReleaseKey.xml";
	public static final String API_CHANGE_RELEASE = "changeRelease";
	public static final String SERVICE_SEND_RELEASE_TO_WMS = "VSISendReleaseToWMS";
	public static final String TEMPLATE_GET_ITEM_LIST_PREPARE_SEND_RELEASE = "/global/template/api/getItemList_VSIProcessSendRelease.xml";
	public static final String ATTR_STS_SHIP_NODE = "STSShipNode";
	public static final String DOC_TYPE_TO = "0006";
	public static final String TEMPLATE_GET_ORG_HIER_PREPARE_SEND_REL = "/global/template/api/getOrganizationHierarchy_VSIPrepareSendRelease.xml";
	public static final String ATTR_REJECT_ASSIGNMENT = "RejectAssignment";
	public static final String ELE_SCHEDULES = "Schedules";
	public static final String ELE_SCHEDULE = "Schedule";
	public static final String TRUE = "true";
	public static final String FALSE = "false";
	public static final String ATTR_RELEASE_ACK = "RELEASE_ACKNOWLEDGED.0001.ex";
	public static final String TEMPLATE_ORDER_STATUS_CHANGE_ORDER_RELEASE_KEY = "/global/template/api/common_OrderStatusChange_OrderReleaseKey.xml";
	public static final String SERVICE_RAISE_RELEASE_REJECTED_ALERT = "VSIRaiseReleaseRejectedAlertSyncService";
	public static final String TEMPLATE_GET_ORDER_LIST_PRCSS_CNLD_DRNG_WAVING = "/global/template/api/getOrderList_VSIProcessCanceledDuringWavingMessage.xml";
	public static final String US = "US";
	public static final String TEMPLATE_GET_SHIP_LIST_PRCSS_WMS_SHIP_CONF = "/global/template/api/getShipmentList_VSIProcessShipConfirmFromWMS.xml";
	public static final String ELE_TO_ADDRESS = "ToAddress";
	public static final String STATUS_PARTL_SHIP_2 = "2";
	public static final String STATUS_PARTL_SHIP_5 = "5";
	public static final String STATUS_PARTL_SHIP_3 = "3";
	public static final String ATTR_PARTL_SHP_CNF_STATUS = "PartialShipConfirmStatus";
	public static final String ATTR_CNCL_NON_SHIPPED_QTY = "CancelNonShippedQty";
	public static final String TEMPLATE_GET_ITEM_LIST_PRCSS_WMS_SHIP_CONF = "/global/template/api/getItemList_VSIProcessShipConfirmFromWMS.xml";
	public static final String ATTR_EXTN_IS_DISCNTND_ITEM = "ExtnIsDiscontinuedItem";
	public static final String DELETE = "Delete";
	public static final String ATTR_BCKORD_NON_SHIPPED_QTY = "BackOrderNonShippedQty";
	public static final String STATUS_BACKORDERED = "1300";
	public static final int STATUS_INT_BACKORDERED = 1300;
	public static final String ELE_LINE_PRICE_INFO = "LinePriceInfo";
	public static final String STATUS_CODE_RELEASED = "3200";
	public static final String STATUS_NO_ACTION = "1500";
	public static final int STATUS_INT_NO_ACTION = 1500;
	public static final String ENT_VSI = "VSI";
	public static final String CNCL_WINDOW = "CANCELLATION_WINDOW";
	public static final String ATTR_EXTENDED_PRICE = "ExtendedPrice";
	public static final String TEMPLATE_GET_ORDER_LIST_CONF_ASSIGNMENT_UE = "/global/template/api/getOrderList_VSIConfirmAssignmentUE.xml";
	public static final String TRANCKING_URLS = "TRANCKING_URLS";
	public static final String ATTR_CARRIER = "Carrier";
	public static final String ELE_TRACKING_NUMBERS = "TrackingNumbers";
	public static final String ELE_TRACKING_NUMBER = "TrackingNumber";
	public static final String ATTR_REQUEST_NO = "RequestNo";
	public static final String ATTR_URL = "URL";
	public static final String ATTR_CARRIER_SERVICE_CODE = "CarrierServiceCode";
	public static final String ATTR_EXTN_CARRIER = "ExtnCarrier";
	public static final String ATTR_EXTN_CARRIER_SERVICE_CODE = "ExtnCarrierServiceCode";
	public static final String ATTR_EXTN_SHIP_TO_ZIP_CODE = "ExtnShipToZipCode";
	public static final String ATTR_SHORT_ZIP_CODE= "ShortZipCode";
	public static final String ATTR_EXTN_SHIP_FROM_ZIP_CODE = "ExtnShipFromZipCode";
	public static final String ELE_PERSON_INFO_SHIP_FROM = "PersonInfoShipFrom";
	public static final String ELE_MULTI_API = "MultiApi";
	public static final String ELE_API = "API";
	public static final String ELE_INPUT = "Input";
	public static final String ATTR_IS_EXTN_DB_API = "IsExtendedDbApi";
	public static final String API_GET_EXTN_TIME_IN_TRANSIT_LIST = "getExtnTimeInTransitList";
	public static final String API_MULTI_API = "multiApi";
	public static final String ATTR_TNT_LINE_KEY = "ExtnTNTLineKey";
	public static final String API_CHANGE_EXTN_TIME_IN_TRANSIT = "changeExtnTimeInTransit";
	public static final String API_CREATE_EXTN_TIME_IN_TRANSIT = "createExtnTimeInTransit";
	public static final String ELE_OUTPUT = "Output";
	public static final String ELE_EXTN_TIME_IN_TRANSIT_LIST = "ExtnTimeInTransitList";
	public static final String ATTR_EXTN_TIME_IN_TRANSIT = "ExtnTimeInTransit";
	public static final String TEMPLATE_GET_SHIP_NODE_LIST_BEFORECHANGEORDERUE = "global/template/api/VSIBeforeChangeOrderUE_GetShipNodeList.xml";
	public static final String ATTR_LAST_RECORD_SET = "LastRecordSet";
	public static final String ATTR_MAX_RECORDS = "MaximumRecords";
	public static final String ATTR_IGNORE_ORDERING = "IgnoreOrdering";
	public static final String ELE_COMPLEX_QUERY = "ComplexQuery";
	public static final String AND_QUERY = "And";
	public static final String ATTR_OPERATOR = "Operator";
	public static final String ELE_EXP = "Exp";
	public static final String ATTR_QUERY_TYPE = "QryType";
	public static final String GT_QRY_TYPE = "GT";
	public static final String AND = "AND";
	public static final String ATTR_LAST_ORDER_RELEASE_KEY = "LastOrderReleaseKey";
	public static final String ATTR_EXTN_JDA_TRANS_NO = "ExtnJDATransferNumber";
	public static final String ATTR_JDA_TRANS_NO = "JDATransferNumber";
	public static final String STATUS_CODE_JDA_ACK = "3200.300";
	public static final String ACTION_CAPS_MODIFY = "MODIFY";
	public static final String ACTION_CANCEL_BACKORDER = "CANCEL_BACKORDER";
	public static final String ATTR_SCAC_AND_SERVICE = "ScacAndService";
	public static final String ATTR_ACTUAL_SHIP_DATE = "ActualShipmentDate";
	public static final String ATTR_CONTAINER_GROSS_WEIGHT = "ContainerGrossWeight";
	public static final String ATTR_CONTAINER_GROSS_WEIGHT_UOM = "ContainerGrossWeightUOM";
	public static final String ATTR_USER_CANCELED_QTY = "UserCanceledQuantity";
	public static final String ATTR_CREATETS = "Createts";
	//OMS-2405 -- Start
	public static final String ATTR_MODIFYTS = "Modifyts";
	//OMS-2405 -- End
	public static final String ATTR_REQ_SHIP_DATE = "ReqShipDate";
	public static final String ATTR_REQ_DEL_DATE = "ReqDeliveryDate";
	public static final String ATTR_SIGNATURE_TYPE = "SignatureType";
	public static final String ATTR_ENTERED_BY = "EnteredBy";
	public static final String ELE_PERSON_INFO_BILL_TO = "PersonInfoBillTo";
	public static final String ELE_PRICE_INFO = "PriceInfo";
	public static final String ATTR_ORG_ALLOCATED_QTY = "OriginalAllocatedQty";
	public static final String VSI_FTC_RULES = "VSI_FTC_RULES";
	public static final String DATE_TYPE_FTC_PROMISE_DATE = "YCD_FTC_PROMISE_DATE";
	public static final String DATE_TYPE_FTC_CANCEL_DATE = "YCD_FTC_CANCEL_DATE";
	public static final String ATTR_CODE_LONG_DESCRIPTION = "CodeLongDescription";
	public static final String SERVICE_INVOKE_SVS_WEB_SERVICE = "VSIInvokeSVSSyncService";
	public static final String TEMPLATE_SHIPMENT_SHIPMENT_KEY = "global/template/api/common_Shipment_ShipmentKey.xml";
	public static final String TEMPLATE_SHIPMENT_LIST = "global/template/api/getShipmentList_orderMonitor.xml";
	public static final String GROUND = "GROUND";
	public static final String HR = "HR";
	public static final String TXN_SCHEDULE = "SCHEDULE.0001";
	public static final String TXN_RELEASE = "RELEASE.0001";
	public static final String TXN_BACKORDER = "BACKORDER.0001";
	public static final String STATUS_CODE_RELEASE_REJECTED = "3200.800";
	public static final String STATUS_CODE_RELEASE_ACKNOWLEDGED = "3200.700";
	public static final String ATTR_USER_CANCELLED_QTY = "UserCanceledQuantity";
	public static final String ATTR_EXTN_SIGNATURE_TYPE = "ExtnSignatureType";
	public static final String ATTR_EXPECTED_DELIVERY_DATE = "ExpectedDeliveryDate";
	//Added for Prorating Header Discounts
	public static final String DISCOUNT = "Discount";
	public static final String CUSTOMER_APPEASEMENT = "CUSTOMER_APPEASEMENT";
	public static final String CHARGE_DETAILS = "ChargeDetails";
	public static final String ATTR_IS_NEW_CHARGE = "IsNewCharge";
	public static final String ATTR_CHARGE_NAME_KEY = "ChargeNameKey";
	public static final String ACTION_MODIFY = "Modify";
	public static final String ELE_LINE_CHARGES = "LineCharges";
	public static final String ELE_LINE_CHARGE = "LineCharge";
	public static final String ATTR_CHARGE_NAME = "ChargeName";
	public static final String MANUAL_DISCOUNT_$ = "Manual Discount $";
	public static final String ATTR_CHARGE_PER_LINE = "ChargePerLine";
	public static final String ATTR_CHARGE_PER_UNIT = "ChargePerUnit";
	public static final String ELE_OVERALL_TOTALS = "OverallTotals";
	public static final String ATTR_LINE_OVERALL_TOTALS = "LineOverallTotals";
	public static final String TEMPLATE_GET_ORDER_LIST_PRORATE_LINE_CHARGES = "global/template/api/getOrderList_VSIProrateLineChargesAPI.xml";
	public static final String TEMPLATE_GET_ORDER_LIST_PRORATE_LINE_TAXES = "global/template/api/getOrderList_VSIProrateLineTaxesAPI.xml";
	public static final String ATTR_GRAND_TOTAL = "GrandTotal";
	public static final String ATTR_LINE_SUB_TOTAL = "LineSubTotal";
	public static final String YYYY_MM_DD_T_HH_MM_SS_XXX = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
	public static final String YYYY_MM_DD = "yyyy-MM-dd";

	//Constants added for Call Center Ack Screen for VSIGetOrderLinesForAckSyncService
	public static final String XPATH_ORDERLIST_ORDER = "OrderList/Order";
	public static final String ATTR_MAX_LINE_STATUS_DESC = "MaxLineStatusDesc";
	public static final String ATTR_RECEIVED_QTY = "ReceivedQty";
	public static final String ATTR_REMAINING_QTY = "RemainingQty";
	public static final String ATTR_SHIPPED_QTY = "ShippedQuantity";
	public static final String ATTR_STATUS_QTY = "StatusQuantity";

	public static final String ELE_ITEM_DETAILS_INFO = "ItemDetails";
	public static final String ELE_RETURN_ORDER_LINES = "ReturnOrderLines";
	public static final String ELE_RETURN_ORDERS = "ReturnOrders";
	public static final String ELE_ORDER_LINE_DETAIL = "OrderLineDetail";
	public static final String ELE_RETURN_ORDER = "ReturnOrder";
	public static final String API_GET_COMPLETE_ORDER_DETAILS = "getCompleteOrderDetails";
	public static final String ATTR_DRAFT_ORDER_FLAG = "DraftOrderFlag";
	public static final String XPATH_ORDERLINE_RETURN_ORDER = "//OrderLine/ReturnOrderLines";
	public static final String XPATH_ORDERLINES_ORDER = "//Order/OrderLines";
	public static final String ATTR_RETURNABLE_QTY = "ReturnableQty";
	public static final String ATTR_RETURN_CREATED = "ReturnCreated";
	public static final String ATTR_EXTENDED_DSIPLAY_ITEM_DESC = "ExtendedDisplayDescription";
	public static final String ELE_DERIVED_FROM = "DerivedFrom";
	public static final String ATTR_TOTAL_NUMBER_OF_RECORDS = "TotalNumberOfRecords";
	public static final String XPATH_ORDERLIST = "//OrderList";
	public static final String XPATH_ORDERLIST_ORDER_ORDERLINES = "//OrderList/Order/OrderLines";

	//Constants added for Call Center GetReturnOrderPriceUE
	public static final String ATTR_DERIVED_FROM_ORDER_HEADER_KEY = "DerivedFromOrderHeaderKey";
	public static final String ATTR_DERIVED_FROM_ORDER_LINE_KEY = "DerivedFromOrderLineKey";
	public static final String ATTR_REMAINING_CHARGE_AMOUNT = "RemainingChargeAmount";
	public static final String ATTR_REMAINING_CHARGE_PER_UNIT = "RemainingChargePerUnit";
	public static final String ATTR_INVOICED_TAX="InvoicedTax";
	public static final String ATTR_IS_RETURNABLE="IsReturnable";
	public static  String ELE_COMMON_CODE = "CommonCode";
	public static  String ATTR_CODE_LONG_DESC = "CodeLongDescription";

	//Constants added for call center CreateAndConfirmReturnOrder
	public static final String ATTR_SALES_ORDER_HEADER_KEY = "SalesOrderHeaderKey";
	public static final String XPATH_ORDER = "//Receipt/Order";
	public static final String XPATH_ORDER_PERSON_INFO_BILLTO = "//OrderList/Order/PersonInfoBillTo";
	public static final String XPATH_ORDER_PERSON_INFO_SHIPTO = "//OrderList/Order/PersonInfoShipTo";
	public static final String XPATH_ORDER_PRICEINFO = "//OrderList/Order/PriceInfo";
	public static final String ELE_CONFIRM_DRAFT_ORDER = "ConfirmDraftOrder";
	public static final String XPATH_RECEIPT_SHIPMENT = "//Receipt/Shipment";
	public static final String XPATH_RECEIPT_RECEIPTLINES_RECEIPTLINE = "//Receipt/ReceiptLines/ReceiptLine";
	public static final String XPATH_ORDER_LINE = "//Order/OrderLines/OrderLine";
	public static final String XPATH_RECEIPT_RETURN_ORDER_LINES = "//Receipt/ReceiptLines/ReceiptLine/ReturnOrderLines";
	public static final String ATTR_REASON_CODE = "ReasonCode";
	public static final String ATTR_RECEIPT = "Receipt";
	public static final String ATTR_DISPOSITION_CODE = "DispositionCode";
	public static final String ATTR_SELLER_ORG_CODE = "SellerOrganizationCode";
	public static final String XPATH_ORDERSTATUS_ORDERLINES = "//OrderStatusChange/OrderLines";
	
	//changes for pickup email service
	public static final String ATTR_PICKUP_EMAIL_FLAG = "ExtnPickupEmailFlag";
	public static final String ORDER_LINE_LIST_PICKUP_TEMPLATE = "global/template/api/getOrderLineList_VSICustomerPickupEmail.xml";
	//1B Marketplace create order change
	public static final String EXTN_ACT_SKU_ID =  "ExtnActSkuID";
	public static final String ATTR_UPC =  "UPC";
	public static final String ATTR_ALIAS_NAME =  "AliasName";
	public static final String ATTR_ALIAS_VALUE =  "AliasValue";
	public static final String ELE_ITEM_ALIAS =  "ItemAlias";
	public static final String ELE_ITEM_ALIAS_LIST = "ItemAliasList";
	
	// Added for VSIActivateGiftCardAPI and VSIContainerizeGiftCardAPI
	public static final String TEMPLATE_CHANGE_SHIP_ACTIVATE_GIFT_CARD = "/global/template/api/changeShipment_VSIActivateGiftCardAPI.xml";
	public static final String ATTR_SHIPMENT_TYPE = "ShipmentType";
	public static final String ACTION_CREATE_MODIFY = "Create-Modify";
	public static final String ATTR_SHIPMENT_CONTAINERIZED_FLAG = "ShipmentContainerizedFlag";
	public static final String ELE_VSI_ONLINE_GC = "VSIOnlineGiftCard";
	public static final String FLOW_GIFT_CARD = "GiftCard";
	public static final String ATTR_STORE_NO = "StoreNo";
	public static final String ATTR_CALLING_FLOW = "CallingFlow";
	public static final String ATTR_GIFT_CARD_NO = "GiftCardNo";
	public static final String ATTR_TOTAL_AMOUNT = "TotalAmount";
	public static final String ATTR_UNIT_PRICE = "UnitPrice";
	public static final String ATTR_PIN_NO = "PinNo";
	public static final String ATTR_EXTN_GIFT_CARD_NO = "ExtnGiftCardNo";
	public static final String TEMPLATE_GET_SHIP_LIST_CONTAINERIZE_GC = "/global/template/api/getShipmentList_VSIContainerizeGiftCardAPI.xml";
	public static final String TEMPLATE_CHANGE_SHIPMENT_CONTAINERIZE_GC = "/global/template/api/changeShipment_VSIContainerizeGiftCardAPI.xml";
	public static final Object FULLY_CONTAINERIZED = "03";
	public static final String PHY_GC = "PHY_GC";
	public static final String ATTR_ITEM_DESC = "ItemDesc";
	public static final String STATUS_SHIPPED = "1400";
	
	public static final String STATUS_BACKORDERED_FROM_NODE = "1400";
	public static final String STATUS_CANCEL = "9000";
	public static final String ACTION_RELEASE_CANCEL = "CANCEL";
	public static final String ACTION_RELEASE_BO = "BACKORDER";
	public static final String XPATH_CONTAINER_SHIPMENT_LINES = "//Shipment/Containers/Container/ContainerDetails/ContainerDetail/ShipmentLine";
	public static final String ATTR_DISTRIBUTION_ORDER_ID = "DistributionOrderId";
	public static final String ATTR_SOURCING_CLASSIFICATION = "SourcingClassification";
	public static final String INTERNATIONAL_ORDER = "INTERNATIONAL";
	
	//Below Constants Added for Customer Appeasement Module 
	//Templates
	public static final String TEMPLATE_GET_ORDER_LIST_FOR_VGC_DETAILS = "global/template/api/VSIGetOrdListForVGCDetails.xml";
	public static final String TEMPLATE_GET_COMPLETE_ORDER_DETAILS = "global/template/api/VSIGetComplateOrderDetails.xml";
	public static final String TEMPLATE_GET_ORDER_INVOICE_LIST = "global/template/api/VSIGetOrderInvoiceList.xml";
	public static final String API_GET_ORDER_INVOICE_LIST = "getOrderInvoiceList";
	public static final String API_RECORD_INVOICE_CREATION = "recordInvoiceCreation";
	
	//Services
	public static final String SERVICE_VSI_FETCH_ONLINE_GCDETAILS = "VSIFetchOnlineGCDetails";
	public static final String SERVICE_VSI_UPDATE_ONLINE_GCDETAILS = "VSIUpdateOnlineGCDetails";
	public static final String SERVICE_VSI_INVOKE_SVS_FOR_OGC_ACTIVATION = "VSIInvokeSVSForOGCActivation"; 
	public static final String SERVICE_VSI_TRIGGER_EMAIL_ON_VGC_REFUND = "VSITriggerEmailOnVGCRefund";
	public static final String SERVICE_VSI_SEND_VGC_ACTIVATION_REQ_TO_SVS = "VSISendVGCActivationReqToSVS";
	
	//String Constants
	public static final String STR_CHARGE_CAT_CUSTOMER_SATISFACTION = "CUST_SATISFACTION";
	public static final String STR_APPEASEMENT_REASON_SHIPPING_APPEASEMENT = "Shipping Appeasement";
	public static final String STR_APPEASEMENT_REASON_TAX_ISSUE = "Tax issue";
	public static final String STR_VSI_APPEASEMENT_APPROVAL = "VSI_APPEASEMENT_APPROVAL";	
	public static final String STR_SALES_TAX = "Sales Tax";
	public static final String STR_CODE_NAME_APPEASE_LIMIT = "APPEASE_LIMIT";
	public static final String STR_RETURN = "Return";
	public static final String STR_INFO = "INFO";
	public static final String STR_CUST_APPEASE = "CustAppease";
	public static final String STR_VSI_ALPINE_RESOURCE="VSIALPINERESTR";
	public static final String STR_VSI_SVS_OFFLINE_QUEUE= "VSI_SVS_OFFLINE";
	public static final String STR_REPROCESS = "Reprocess";
	
	//Elements
	public static final String ELE_LINE_DETAILS = "LineDetails";
	public static final String ELE_LINE_DETAIL = "LineDetail";
	public static final String ELE_APPEASEMENT_REASON = "AppeasementReason";
	public static final String ELE_LINE_INVOICED_TOTALS = "LineInvoicedTotals";
	public static final String ELE_ORDER_INVOICE = "OrderInvoice";
	public static final String ELE_HEADER_CHARGE_LIST = "HeaderChargeList";
	public static final String ELE_LINE_TAX_LIST = "LineTaxList";
	public static final String ELE_LINE_CHARGE_LIST = "LineChargeList";
	public static final String ELE_LINE_OVERALL_TOTALS = "LineOverallTotals";
	public static final String ELE_APPEASMENT_OFFER = "AppeasementOffer";
	public static final String ELE_APPEASMENT_OFFERS = "AppeasementOffers";
	public static final String ELE_NOTES = "Notes";
	public static final String ELE_INVOKE_UE = "InvokeUE";
	public static final String ELE_XML_DATA = "XMLData";
	public static final String ELE_RETURN_CODE = "returnCode";
	public static final String ELE_INBOX = "Inbox";
	public static final String ELE_TRANSACTION_ID = "transactionID";
	public static final String ELE_APPROVED_AMOUNT = "approvedAmount";
	public static final String ELE_CONSOLIDATE_TEMPLATE = "ConsolidationTemplate";
	public static final String ELE_INBOX_REFERANCES = "InboxReferences";
	public static final String ELE_INBOX_REFERANCES_LIST = "InboxReferencesList";
	
	//Attributes
	public static final String ATTR_IS_LINE_FULLY_REFUNDED = "isLineFullyRefunded";
	public static final String ATTR_DISPLAY_STATUS = "DisplayStatus";
	public static final String ATTR_IS_ALPINE_USER = "IsAlpineUser";
	public static final String ATTR_INVOICE_TYPE = "InvoiceType";
	public static final String ATTR_IS_CARD_USED = "IsCardUsed";
	public static final String ATTR_GIFT_CARD_KEY = "GiftCardKey";
	public static final String ATTR_OFFER_AMOUNT = "OfferAmount";
	public static final String ATTR_DATE_TODAY = "dateToday";
	public static final String ATTR_INVOICE_NO = "InvoiceNo";
	public static final String ATTR_REFUND_AMOUNT = "RefundAmount";
	public static final String ATTR_USE_ORDER_LINE_CHARGES = "UseOrderLineCharges";
	public static final String ATTR_INVOICE_CREATION_REASON = "InvoiceCreationReason";
	public static final String ATTR_LINE_OFFER_AMOUNT = "LineOfferAmount";
	public static final String ATTR_EXTN_APPEASE_AMOUNT = "ExtnAppeaseAmount";
	public static final String ATTR_APPEASEMENT_TYPE = "AppeasementType";
	public static final String ATTR_OFFER_TYPE = "OfferType";
	public static final String ATTR_AMOUNT = "Amount";
	public static final String ATTR_PIN = "Pin";
	public static final String ATTR_CONSOLIDATE = "Consolidate";
	public static final String ATTR_DETAIL_DESCRIPTION = "DetailDescription";
	public static final String ATTR_RETURN_DESCRIPTION = "ReturnDescription";
	public static final String ATTR_IS_ACTIVATED = "IsActivated";
	public static final String ATTR_CARD_AMOUNT = "CardAmount";
	public static final String ATTR_CHANGE_IN_QTY = "ChangeInQuantity";
	public static final String ATTR_FROM_STATUS = "FromStatus";
	public static final String ATTR_TO_STATUS = "ToStatus";
	public static final String ATTR_ALERT_MSG_DESCRIPTION = "Pricing service is down or not reachable";
	
	//Start: Added for Settlement Load : 04/07/2017
	public static final String ELE_SETTLEMENT="Settlement";
	public static final String ELE_SETL_TRANSACTION="VSISetlTransaction";
	public static final String ELE_SETL_TRANS_CHAREGE_LIST="VSISetlTransChargeList";
	public static final String ELE_SETL_TRANS_CHAREGE="VSISetlTransCharge";
	//public static final String ATTR_TOTAL_AMOUNT="TotalAmount";
	public static final String ATTR_TRANSATION_TYPE="TransactionType";
	public static final String ATTR_SETTLEMENT_ID="SettlementId";
	public static final String ATTR_ORDER_ID="OrderId";
	public static final String ATTR_FULFILLMENT_ID="FulfillmentId";
	public static final String TRANS_TYPE_ORDER="Order";
	public static final String TRANS_TYPE_REFUND="Refund";
	public static final String TRANS_TYPE_A_Z_GURANTEE_REFUND="A-to-z Guarantee Refund";
	public static final String ATTR_TRANSACTION_KEY="TransactionKey";
	public static final String ATTR_PRICE_TYPE="PriceType";
	public static final String ATTR_PRICE_AMOUNT="PriceAmount";
	public static final String ATTR_ITEM_RELATED_FEE_TYPE="ItemRelatedFeeType";
	public static final String ATTR_ITEM_RELATED_FEE_AMOUNT="ItemRelatedFeeAmount";
	public static final String ATTR_PROMOTION_AMOUNT="PromotionAmount";
	public static final String ATTR_QUANTITY_PURCHASED="QuantityPurchased";
	public static final String ATTR_PROMOTION_ID="PromotionId";
	public static final String ATTR_PROMOTION_TYPE="PromotionType";
	public static final String ATTR_OTHER_AMOUNT="OtherAmount";
	public static final String ATTR_OTHER="Other";
	public static final String ATTR_SKU="Sku";
	//public static final String ATTR_CHARGE_TYPE="ChargeType";
	//public static final String ATTR_CHARGE_AMOUNT="ChargeAmount";
	public static final String ATTR_SETTLEMENT_START_DATE="SettlementStartDate";
	public static final String ATTR_SETTLEMENT_END_DATE="SettlementEndDate";
	public static final String ATTR_DEPOSIT_DATE="DepositDate";
	public static final String ATTR_SETTLEMENT_DEPOSIT_DATE="SettlementDepositDate";
	public static final String ELE_VSI_SETTLEMENT="VSISettlement";
	public static final String ATTR_EXTN_STORE_FRONT="ExtnStoreFront";
	public static final String VS_AMAZON="VS_AMAZON";
	public static final String NUTRITION_DEPOT="NUTRITION_DEPOT";
	
	//End: Added for Settlement Load : 04/07/2017
	
	//Repricing Constants
	public static final String ATTR_EXTN_COUPON_ID = "ExtnCouponID";
	public static final String ATTR_COUPON_NUMBER = "CouponNumber";
	public static final String ATTR_EXTN_CHARGE_ID = "ExtnChargeId";
	public static final String ATTR_IS_NEW_ORDER = "IsNewOrder";
	public static final String ATTR_PROMOTION_APPLIED = "PromotionApplied";
	public static final String ATTR_EXTN_DESCRIPTION = "ExtnDescription";
	public static final String ELE_AWARDS = "Awards";
	public static final String ATTR_PROMO_NUM = "PromoNum";
	public static final String ELE_AWARD = "Award";
	public static final String ATTR_AWARD_TYPE = "AwardType";
	public static final String ATTR_AWARD_AMOUNT = "AwardAmount";
	public static final String ATTR_AWARD_ID = "AwardId";
	public static final String ATTR_IS_DISCOUNT = "IsDiscount";
	public static final String CHARGE_NAME_SHIPPING_DISCOUNT="Shipping Discount";
	public static final String PROG_ID_CALL_CENTER="ISCCSSYS001";
	public static final String PROG_ID_STORE="WSCSYS00001";
	public static final String ATTR_EXTN_IS_GWP = "ExtnIsGWP";
	public static final String ELE_PRICING_RESPONSE = "pricingResponse";
	public static final String ELE_SOAP_ENV_BODY = "soapenv:Body";
	public static final String GOOD = "GOOD";
	public static final String TEMPLATE_ORDER_LINE_ORDER_LINE_KEY = "/global/template/api/common_OrderLine_OrderLineKey.xml";
	public static final String API_ADD_LINE_TO_ORDER = "addLineToOrder";
	public static final String REMOVE = "REMOVE";
	public static final String REPLACEMENT_CHARGE_CTGY = "Replacement charge";
	public static final String REPLACEMENT_CHARGE_NAME = "Replacement charge";
	
	public static final String SC_CONTROLLER_INPUT = "scControllerInput";
	public static final String YCD_CNCL_REASON = "YCD_CANCEL_REASON";
	public static final String SPACE = " ";
	public static final String ATTR_DATA_KEY = "DataKey";
	
	// Added for Data Migration
	public static final String MO_PAYMENT_HOLD = "MO_PAYMENT_HOLD";
	public static final Object DOC_TYPE_RETURN = "0003";
	public static final String ATTR_EXTN_IS_MIGRATED = "ExtnIsMigrated";
	public static final String ELE_NOTE = "Note";
	public static final String ATTR_NOTE_TEXT = "NoteText";
	public static final String API_IMPORT_ORDER = "importOrder";
	public static final String API_IMPORT_SHIPMENT = "importShipment";
	public static final String ELE_SHIPMENTS = "Shipments";
	public static final String ELE_PAYMENT_DETAILS_LIST = "PaymentDetailsList";
	public static final String ELE_MIGRATION_DATA = "MigrationData";
	public static final String SCAC_UPSN = "UPSN";
	public static final String ELE_INVENTORY_NODE_CONTROL = "InventoryNodeControl";
	public static final String ATTR_NODE_CONTROL_TYPE = "NodeControlType";
	public static final String ATTR_INVENTORY_PICTURE_CORRECT = "InventoryPictureCorrect";
	public static final String ATTR_INVENTORY_PICTURE_INCORRECT_DATE = "InvPictureIncorrectTillDate";
	public static final String API_MANAGE_INVENTORY_NODE_CONTROL = "manageInventoryNodeControl";
	public static final String API_GET_INVENTORY_NODE_CONTROL_LIST = "getInventoryNodeControlList";
	

	
	//Constants Added for Call Center OrderCapture
	//Templates
	public static final String TEMPLATE_VSI_GET_ORDER_LIST_PAYMENT = "global/template/api/VSIGetOrderList_Payment.xml";
	
	//Elements
	public static final String ELE_REFERENCES = "References";
	public static final String ELE_REFERENCE = "Reference";
	
	//Attributes
	public static final String ATTR_REFERENCE_ID = "ReferenceId";
	public static final String ATTR_COUPON_STATUS_MSG_CODE = "CouponStatusMsgCode";
	public static final String ATTR_SUSPEND_ANYMORE_CHARGES = "SuspendAnyMoreCharges";
	public static final String ATTR_PAYMENT_KEY = "PaymentKey";
	public static final String ATTR_INCOMPLETE_PAYMENT = "IncompletePaymentType";
	public static final String ATTR_PLANNED_REFUND_AMOUNT = "PlannedRefundAmount";
	public static final String ATTR_PAYMENT_REFERENCE_2 = "PaymentReference2";
	
	
	
	//String Constants
	public static final String STR_VSIPAYMTHDSUP = "VSIPAYMTHDSUP";
	public static final String STR_CASH = "CASH";
	public static final String STR_CHECK = "CHECK";
	public static final String STR_AR_CREDIT = "AR_CREDIT";
	public static final String STR_VOUCHERS = "VOUCHERS";
	public static final String STR_CREDIT_CARD = "CREDIT_CARD";
	public static final String STR_PAYPAL = "PAYPAL";
	
	//Added for VSI Tag Alert
	public static final String ATTR_UNIT_WEIGHT = "UnitWeight";
	public static final String ATTR_UNIT_WEIGHT_UOM = "UnitWeightUOM";
	public static final String ATTR_PRIORITY =  "Priority";
	public static final String STR_VSI_CALL_TAG = "VSI_CALL_TAG";
	public static final String STR_VSI_CALL_TAG_QUEUE = "VSI_CALL_TAG_Q";
	
	//Constants for Tracking UE
	public static final String SCAC_LSHIP = "LSHIP";
	public static final String SCAC_ONTRAC = "ONTRAC";
	public static final String SCAC_USPS = "USPS";
	
	public static final String CARRIER_SERVICE_CODE_SUREPOST = "SUREPOST";
	public static final String CARRIER_SERVICE_CODE_MI = "MI";

	public static final String TRACKING_COMMON_CODE_SURE = "Sure";
	public static final String TRACKING_COMMON_CODE_MI = "UPS Mail Innovations";
	public static final String TRACKING_COMMON_CODE_UPS = "UPS";
	public static final String TRACKING_COMMON_CODE_LSHIP = "Lasership";
	public static final String TRACKING_COMMON_CODE_ONTRAC = "OnTrac";
	public static final String TRACKING_COMMON_CODE_USPS = "USPS";
	//Agent Modification Hold
	public static final String AGENT_MOD_HOLD = "VSI_AGENT_MOD_HOLD";
	public static final String ATTR_PROCESSED_HOLD_TYPES="ProcessedHoldTypes";
	
	//Entered By Constants
	public static final String VIRTUAL_STORE_COM_CODE="VSI_VIRTUAL_STORE_NO";
	
	//Template for VSIMArketplaceFulfillment
	public static final String TEMPLATE_VSI_GET_ORDER_LIST_MARKETPLACE = "global/template/api/getOrderList_VSIMarketplaceFulfillment.xml";

	//START - 1B CRM Enhancement
	public static final String ATTR_CUSTOMER_TYPE = "CustomerType";
	public static final String ATTR_CUSTOMER_TYPE_02 = "02";
	public static final String ATTR_ORGANIZATION_CODE = "OrganizationCode";
	public static final String ATTR_CUSTOMER_ADDRESS_ID = "ID";
	public static final String ATTR_EXTN_GENDER = "ExtnGender";
	public static final String ATTR_GENDER = "Gender";
	public static final String ATTR_ADDRESS3 = "AddressLine3";
	public static final String ATTR_COMPANY = "Company";
	public static final String ATTR_USER_ID = "UserID";
	public static final String ATTR_EXTN_PREFERRED_CARRIER = "ExtnPreferredCarrier";
	public static final String ATTR_EXTN_TAX_EXAMPT_FLAG = "ExtnTaxExemptFlag";
	public static final String ATTR_COUNTRY_CODE = "CountryCode";
	
	public static final String ELE_PHONE_OPT_IN_FLAG_CODE = "PhoneOptInFlagCode";
	public static final String ELE_MAIL_OPT_IN_FLAG_CODE = "MailOptInFlagCode";
	public static final String ELE_EMAIL_OPT_IN_FLAG_CODE = "EmailOptInFlagCode";
	//END - 1B CRM Enhancement
	
	//Start: Added for Payment Changes 04/27/2017
	// POS OrderType
	public static final String ATTR_ORDER_TYPE_POS = "POS";
	
	//Delivery Methods
	public static final String ATTR_DEL_METHOD_SHP = "SHP";
	public static final String ATTR_DEL_METHOD_PICK = "PICK";
	
	//Templates 
	public static final String TEMPLATE_GET_DERIVED_FROM_OHK="global/template/api/getOrderList_getDerivedFromOHK.xml";
	//Sequence constants
	public static final String SEQ_VSI_SEQ_TRANID = "VSI_SEQ_TRANID_";
	public static final String ATTR_TRAN_SEQ_WEB_PICK = "89_";
	public static final String ATTR_TRAN_SEQ_POS_PICK = "88_";
	public static final String ATTR_TRAN_SEQ_SHP = "5_";
	public static final String ATTR_EXTN_TRANS_NO="ExtnTransactionNo";
	public static final String VALUE_SEQ_PADDING="00000";
	//END: Added for Payment Changes 04/27/2017
	
	public static final String ATTR_STATUS_BREAKUP_BO = "StatusBreakupForBackOrderedQty";
	public static final String ATTR_BO_FROM = "BackOrderedFrom";
	public static final String ATTR_BO_QTY = "BackOrderedQuantity";
	public static final String ATTR_PRODUCT_AVAILABILITY_DATE = "ProductAvailabilityDate";
	public static final String ATTR_EXPECTED_SHIPMENT_DATE = "ExpectedShipmentDate";
	public static final String ELE_ORDER_RELEASE_LIST ="OrderReleaseList";
	
	
	//Start: Added for Pricing Changes 05/02/2017
	
	public static final String SERVICE_VSI_ATG_PRICING_SERVICE="VSIATGPricingService";
	
	public static final String ATTR_EXCEPTION_TYPE_VSI_WEB_TECHNICAL = "VSI_WEB_TECHNICAL";
	
	public static final String URL_VSI_PRICING_SERVICE="VSI_PRICING_SERVICE_URL";
	//END: Added for Pricing Changes 05/02/2017

	public static final String API_GETCARRIERSERVICEOPTIONS_FORORDERING  = "getCarrierServiceOptionsForOrdering";
	public static final String  ENTRYTYPE_CC = "Call Center";
	public static final String  ATTR_PAYMENT_STATUS = "PaymentStatus";
	
	
	// Start Returns
	public static final String A_VENDOR_ADDRESS = "VendorAddress";
	public static final String XPATH_FOR_CHAINEDFROMORDERHEADERKEY = "//OrderLines/OrderLine/@ChainedFromOrderHeaderKey";
	public static final String ITEM_ID = "ItemID";
	public static final String E_COMPUTED_PRICE = "ComputedPrice";
	public static final String A_IS_DISCOUNT = "IsDiscount";
	public static final String A_IS_SHIPPING_CHARGE = "IsShippingCharge";
	public static final String A_SCCONTROLLER_INPUT = "scControllerInput";
	public static final String A_RETURN_ORDERNO = "ReturnOrderNo";
	public static final String A_RETURN_ORDER_HEADER_KEY = "ReturnOrderHeaderKey";
	public static final String A_RESHIP_PARENTLINEKEY_QRYTYPE = "ReshipParentLineKeyQryType";
	public static final String A_RESHIP_PARENT_LINE_KEY = "ReshipParentLineKey";
	public static final String E_OUTPUT = "Output";
	public static final String E_ORDER_LINE_LIST = "OrderLineList";
	public static final String E_DERIVED_FROM_ORDERLINE = "DerivedFromOrderLine";
	public static final String A_RETURNABLE_QTY = "ReturnableQty";
	public static final String E_RETURN_ORDER_LINES = "ReturnOrderLines";
	public static final String E_RETURN_POLICY_VIOLATIONS = "ReturnPolicyViolations";
	public static final String E_RETURN_POLICY_VIOLATION = "ReturnPolicyViolation";
	public static final String E_NON_APPROVAL_TRANSACTION_VIOLATIONLIST = "NonApprovalTransactionViolationList";
	public static final String E_TRANSACTION_APPROVER_LIST = "TransactionApproverList";
	public static final String E_TRANSACTION_VIOLATION = "TransactionViolation";
	public static final String A_HAS_VIOLATIONS = "HasViolations";
	public static final String E_TRANSACTION_APPROVAL_STATUS_LIST = "TransactionApprovalStatusList";
	public static final String E_APPEASEMENT_OFFERS = "AppeasementOffers";
	public static final String E_APPEASEMENT_OFFER = "AppeasementOffer";
	public static final String A_APPEASEMENT_TYPE = "AppeasementType";
	public static final String E_APPEASEMENT_REASON = "AppeasementReason";
	public static final String E_INVOKE_UE = "InvokeUE";
	public static final String E_XML_DATA = "XMLData";
	public static final String A_IS_VARIABLE = "IsVariable";
	public static final String A_IS_AMOUNT = "IsAmount";
	public static final String A_IS_PERCENT = "IsPercent";
	public static final String A_IS_FUTURE = "IsFuture";
	public static final String E_OVERALL_TOTALS = "OverallTotals";
	public static final String A_LINE_SUB_TOTAL = "LineSubTotal";
	public static final String A_OFFER_TYPE = "OfferType";
	public static final String A_PREFFERED = "Preferred";
	public static final String A_OFFER_AMOUNT = "OfferAmount";
	public static final String A_DISCOUNT_PERCENT = "DiscountPercent";
	public static final String E_HEADER_CHARGES = "HeaderCharges";
	public static final String E_HEADER_CHARGE = "HeaderCharge";
	public static final String XPATH_LINE_CHARGE = "//LineCharge[@ChargeCategory='";
	public static final String XPATH_CHARGE_AMOUNT = "']/@ChargeAmount";
	public static final String A_REFUND_TYPE = "RefundType";
	public static final String A_OFFER_COUNT = "OfferCount";
	public static final String A_SELECTED_COUNT = "SelectedCount";
	public static final String A_LINE_OFFER_AMOUNT = "LineOfferAmount";
	public static final String A_HEADER_OFFER_AMOUNT = "HeaderOfferAmount";
	public static final String XPATH_APPEASEMENT_OFFER_TYPE = "AppeasementOffer[@OfferType='";
	public static final String XPATH_CLOSING_PREFERRED = "']/@Preferred";
	public static final String E_LINE_OVERALL_TOTALS = "LineOverallTotals";
	public static final String A_EXTENDED_PRICE = "ExtendedPrice";	
	public static final String A_RETURN_WINDOW = "ReturnWindow";
	public static final String XPATH_FOR_ORDERSTATUSDATE = "OrderStatus[@Status='3700']/@StatusDate";
	public static final String A_OUTSIDE_RET_WINDOW = "OutsideReturnWindow";
	public static final String A_MESSAGE_CODE = "MessageCode";
	public static final String A_MESSAGE_CODE_DESC = "MessageCodeDesc";
	public static final String E_ORDER_LIST = "OrderList";
	public static final String A_CUSTOMER_NAME = "CustomerName";
	
	public static final String DATE_FORMAT1 = "MM/dd/yyyy";
	public static final String DATE_FORMAT2 = "yyyy-MM-dd";
	public static final String A_SHOW_OVERRIDE = "ShowOverride";
	public static final String OUTSIDE_RETURN_WINDOW = "OUTSIDE_RETURN_WINDOW";
	public static final String OUTSIDE_RETURN_WINDOW_DESC = "This item is outside of VSI Return policy window";
	
	
	public static final String ATTR_ADDRESS6 = "AddressLine6";
	public static final String ATTR_EXTN_IS_PO_BOX = "ExtnIsPOBox";
	public static final String ATTR_SALES_ORDER_NO = "SalesOrderNo";
	public static final String ATTR_EXPECTED_DATE = "ExpectedDate";
	public static final String ATTR_STATUS_QUANTITY = "StatusQty";
	public static final String ATTR_QUANTITY_NOT_SHIPPED = "QuantityNotShipped";
	public static final String ATTR_QUANTITY_TO_CANCEL = "QuantityToCancel";
	public static final String API_CANCEL_ORDER = "cancelOrder";
	
	
	//Start: 05/17/2017 International Hold
	
	public static final String ATTR_VSI_ORDER_HOLD="VSI_ORDER_HOLD";
	public static final String ATTR_EXCEPTION_VSI_INTERNATIONAL_ORDER_HOLD="VSI_INTERNATIONAL_ORDER_HOLD";
	public static final String ATTR_HOLD_TYPE_VSI_INTERNAT_HOLD="VSI_INTERNAT_HOLD";
	public static final String STRING_INTERNATIONAL_ALERT_MESSAGE=" is being shipped to International Address";
	
	//End: 05/17/2017 International Hold
	
	public static final String TEMPLATE_BEFOREORDERCHANGE_GETORDERLIST = "global/template/api/VSIBeforeOrderChange_getOrderList.xml";
	
	// Start: Ship To Store Item Restriction
	public static final String ATTR_SHIPNODES = "ShipNodes";
	public static final String ATTR_SHIP_DATE = "ShipDate";
	public static final String ATTR_ASSIGNED_QTY = "AssignedQty";
	public static final String XPATH_GET_ORG_LIST_SHIP_NODE_PERSON_INFO = "/OrganizationList/Organization/Node/ShipNodePersonInfo";
	public static final String ATTR_ITEM_GROUP_CODE = "ItemGroupCode";
	public static final String ITEM_GROUP_CODE_PROD = "PROD";
	public static final String ELE_VSI_SHIP_RESTRICTED_ITEM_LIST = "VSIShipRestrictedItemList";
	public static final String SERVICE_GET_PICKUP_STORE_ADDRESS = "VSIGetPickupStoreAddress";
	public static final String SERVICE_GET_ITEM_LIST_WITH_SHIP_RESTRICTIONS = "VSIGetItemListWithShipRestrictions";
	public static final String TXN_OBJ_STS_ITEM_RESTRICTIONS = "STS_ITEM_RESTRICTIONS";
	// End: Ship To Store Item Restriction
	
	//template for customer
	public static final String TEMPLATE_COMMONCODELIST = "global/template/api/getCommonCodeList.xml";
	public static final String DEC_FORMAT = "#.00";
	public static final String ATTR_TOTAL_ASSIGNED_QTY = "TotalAssignedQty";

	public static final String SERVICE_VSI_FTC_DELAY_EMAIL = "VSIFTCDelayEmail";
	public static final String SERVICE_VSI_SEND_FTC_DELAY_NOTIFICATION = "VSISendFTCFirstDelayNotification";
	
	public static final String TEMPLATE_GET_ORDER_LIST_VSI_FTC_EMAIL = "/global/template/api/VSI_getOrderDetails_getOrderDetailsForMonitor.xml";
	
	public static final String AUTOCANCEL = "AUTOCANCEL";
	public static final String ATTR_EMAIL_TYPE = "EmailType";
	public static final String NO_INV_CANCEL = "NOINVCANCEL";
	public static final String ATTR_MESSAGE_TYPE = "MessageType";
	public static final String VSI_CNCL_PUBLISH_TO_JDA = "VSICancelPublishToJDA";
	public static final String ACTION_CAPS_CANCEL = "CANCEL";
	public static final String AUTHFAIL = "AUTHFAIL";
	public static final String AUTH_CANCEL_ORDER = "AUTH_CANCEL_ORDER";
	public static final String STS_BO_CANCEL = "STSBOCANCEL";
	public static final String LOST_IN_TRANSIT = "LOSTINTRANSIT";
	public static final String NO_CUSTOMER_PICK = "NO_CUSTOMER_PICK";
	public static final String NO_STORE_PICK = "NO_STORE_PICK";
	public static final String MIS_SHIP = "MISSHIP";
	public static final String ES = "ES";
	public static final String ATTR_CUSTOMER_PO_NO = "CustomerPONo";
	public static final String BACKORDERED = "BACKORDERED";
	public static final String DISCONTINUED_ITEM = "DISCONTINUED_ITEM";
	public static final String SHIP_RESTRICTED = "STS Ship Restriction";
	public static final String VSICANCEL = "VSICANCEL";
	public static final String NO_INVENTORY = "NoInventory";
	public static final String COULD_NOT_SHIP = "CouldNotShip";
	public static final String FTC_CANCEL = "FTC_CANCEL";
	public static final String SYSTEM_ISSUES = "System Issues";
	public static final String WEB = "WEB";
	public static final String SERVICE_VSI_TRIM_STS_SHIP_CONF_MSG = "VSITrimSTSShipConfMsgSyncService";
	public static final String SERVICE_VSI_TRIM_STH_SHIP_CONF_MSG = "VSITrimSTHShipConfMsgSyncService";
	public static final String MARKETPLACE = "Marketplace";
	public static final String API_GET_CALENDAR_LIST = "getCalendarList";
	public static final String ELE_CALENDAR = "Calendar";
	public static final String DISCOUNT_CATEGORY_ADJ = "Adjustments";
	
	//getCustomerDetailsTemplate for WEB customer
	public static final String TEMPLATE_GET_CUSTOMER_LIST_CRM_FAIL = "/global/template/api/getCustomerList_CRMFail.xml";

	public static final String BOGO = "BOGO";
	public static final String ATTR_EXTN_DISC_TYPE = "ExtnDiscType";
	public static final String ATTR_EXTN_TYPE = "ExtnType";
	
	public static final String VSI_REFUND = "VSI_REFUND";
	public static final String VSI_REFUNDS = "VSI_REFUNDS";
	public static final String STRING_MANUAL_RETURN_ALERT_MSG =" : Order that was tendered with Cash or Check was cancelled."
			+ "Please contact Customer to issue an e-gift card using the appeasement option or to obtain a credit card number"
			+ "and submit a manual refund form to Customer Care Management.";
	
	//ARS-272
	public static final String VSI_GC_FULFILLMENT_QUEUE_ID = "VSI_PHY_GC_FULFILL";
	public static final String VSI_GC_FULFILLMENT_EXCEPTION_TYPE = "VSI_PHYSICAL_GC_FULFILLMENT";
	
	//ARS-268
	public static final String ATTR_CHAINED_FROM_ORDER_LINE_KEY = "ChainedFromOrderLineKey";
	
	// Changes to VSIGetAvailableInventory
	public static final String API_GET_FUL_OPTS_FOR_LINES = "getFulfillmentOptionsForLines";
	public static final String ATTR_PROMISE = "Promise";
	public static final String ELE_EVALUATE_OPTIONS = "EvaluateOptions";
	public static final String ELE_EVALUATE_OPTION = "EvaluateOption";
	public static final String TEMPLATE_GET_FUL_OPTS_VSI_GET_AVAIL_INV = "/global/template/api/getFulfillmentOptionsForLines_VSIGetAvailableInventory.xml";
	public static final String TEMPLATE_GET_ORDER_DETAILS_BEFORE_CHANGE_ORDER = "/global/template/api/getOrderDetails_beforeChangeOrder.xml";
	public static final String VSI_STH = "VSI_STH";
	public static final String ATTR_CHECK_INVENTORY = "CheckInventory";
	public static final String VSI_POS_FIND_INV = "POS_FINDIN";
	public static final String ATTR_LINE_ID = "LineId";
	public static final String ELE_OPTIONS = "Options";
	public static final String ELE_OPTION = "Option";
	public static final String ATTR_HAS_ANY_UNAVAIL_QTY = "HasAnyUnavailableQty";
	public static final String ELE_AVAILABILITY = "Availability";
	public static final String ELE_AVAILABLE_INVENTORY = "AvailableInventory";
	public static final String ELE_SHIP_NODE_AVAIL_INV = "ShipNodeAvailableInventory";
	public static final String ELE_ASSIGNMENTS = "Assignments";
	public static final String ELE_ASSIGNMENT = "Assignment";
	public static final String ATTR_AVAILABLE_FROM_UNPLANNED_INV = "AvailableFromUnplannedInventory";
	public static final String ATTR_AVAILABLE_FUTURE_QTY = "AvailableFutureQuantity";
	public static final String ATTR_AVAILABLE_ONHAND_QTY = "AvailableOnhandQuantity";
	public static final String ATTR_AVAILABLE_QTY = "AvailableQuantity";
	public static final String ATTR_START_DATE = "StartDate";
	public static final String ATTR_END_DATE = "EndDate";
	public static final String AVAILABLE_QTY_INTERVAL = "60";
	public static final String DC_9001 = "9001";
	public static final String TEMPLATE_GET_ORDER_LIST_VSI_CHECK_RECEIVE_RETURNS = "/global/template/api/getOrderList_VSICheckAndReceiveReturns.xml";
	public static final String ELE_SUPPLIES = "Supplies";
	public static final String ELE_SUPPLY = "Supply";
	
	//Condition template
	public static final String TEMPLATE_GET_ORDER_LIST_ORDERHOLD = "global/template/api/getOrderList_OrderModsHold.xml";
	public static final String ATTR_AWARD_APPLIED = "AwardApplied";
	
	public static final String ELE_NODE_LIST = "NodeList";
	public static final String ATTR_AVAILABLE_DATE = "AvailableDate";
	public static final String ATTR_IS_AVAILABLE = "IsAvailable";
	public static final String ATTR_IS_FUTURE_AVAILABILITY = "IsFutureAvailability";
	public static final String NA = "NA";
	public static final String ATTR_TO = "To";
	public static final String ATTR_CHANGE_IN_TOTAL_AMOUNT = "ChangeInTotalAmount";
	public static final String ATTR_PREVIOUS_ORDER_TOTAL = "PreviousOrderTotal";
	public static final String ATTR_NEW_ORDER_TOTAL = "NewOrderTotal";
	public static final String ATTR_CUSTOMER_CREDIT = "CustomerCredit";
	public static final String JDA_CNCL_MSG = "JDACancelMessage";
	
	//START: Fix for ARE-443
	public static final String ATTR_ORDER_INVOICE_KEY = "OrderInvoiceKey";
	public static final String ATTR_INVOICE_KEY = "InvoiceKey";
	public static final String ELE_GET_ORDER_INVOICE_DTLS = "GetOrderInvoiceDetails";
	public static final String API_GET_ORDER_INVOICE_DTLS = "getOrderInvoiceDetails";
	public static final String TEMPLATE_GET_ORDER_INVOICE_DTLS_EMAIL = "/global/template/api/shipConfirmEmail_GetInvoiceDetails.xml";
	public static final String ELE_INVOICE_HEADER = "InvoiceHeader";
	public static final String ELE_SHIP_NODE = "ShipNode";
	public static final String ELE_COLLECTION_DETAIL = "CollectionDetail";
	public static final String ATTR_TOTAL_TAX = "TotalTax";
	public static final String ATTR_OVERALL_TOTALS = "OverallTotals";
	public static final String ATTR_GRAND_SHIPPING_CHARGE = "GrandShippingCharges";
	public static final String ATTR_EXTN_IS_SHIPPING_FREE = "ExtnIsShippingFree";
	public static final String ATTR_GRAND_TAX = "GrandTax";
	public static final String INVOICE_TYPE_SHIPMENT = "SHIPMENT";
	
	// Added for VSIOrderCancelReport : BEGIN
	public static final String ATTR_CANCELED_FROM = "CanceledFrom";
	public static final String ATTR_CANCEL_REASON_CODE = "CancelReasonCode";
	public static final String ATTR_CANCELLED_QTY = "CancelledQty";
	public static final String ATTR_CANCELLED_DATE = "CancelledDate";

	public static final String ELE_VSI_ORDER_CANCEL_REPORT = "VSIOrderCancelReport";
	public static final String ELE_STATUS_BREAKUP_FOR_CANCELED_QTY = "StatusBreakupForCanceledQty";
	
	public static final String API_CREATE_VSI_ORDER_CANCEL_REPORT = "createVSIOrderCancelReport";
	// Added for VSIOrderCancelReport : END
	
	public static final String ATTR_AMOUNT_COLLECTED = "AmountCollected";
	public static final String ALERT_CRM_ISSUE_DESC = "Connectivity issue with CRM system";
	public static final String VSI_CRM_ISSUE = "VSI_CRM_ISSUE";
	
	public static final String ATTR_DISTRIBUTION_RULE_ID = "DistributionRuleId";
	public static final String VSI_COM_DISTR_RULE_ID = "VSI_DC";
	public static final String ATTR_REQ_START_DATE = "ReqStartDate";
	public static final String ATTR_REQ_END_DATE = "ReqEndDate";
	public static final String SERVICE_GET_AVAILABLE_INVENTORY_FOR_UI = "VSIGetAvailableInventoryforUI";
	public static final String ATTR_FIRST_FUTURE_AVAILABLE_DATE = "FirstFutureAvailableDate";
	public static final String ATTR_FUTURE_AVAILABLE_DATE = "FutureAvailableDate";
	public static final String ATTR_FUTURE_AVAILABLE_QTY = "FutureAvailableQuantity";
	public static final String ATTR_ONHAND_AVAILABLE_DATE = "OnhandAvailableDate";
	public static final String FUTURE_DATE = "2500-01-01";
	public static final String PAST_DATE = "1900-01-01";
	public static final String ATTR_CURRENT_AVAILABLE_QTY = "CurrentAvailableQty";
	
	// MCL Constants
	public static final String ENT_MCL = "MCL";
	public static final String MCL_STH = "MCL_STH";
	public static final String ATTR_EXTN_IS_MCL_ITEM = "ExtnIsMCLItem";
	public static final String ELE_AVAILABILITY_CHANGES = "AvailabilityChanges";
	public static final String ELE_AVAILABILITY_CHANGE = "AvailabilityChange";
	public static final String ATTR_ONHAND_AVAILABLE_QUANTITY = "OnhandAvailableQuantity";
	public static final String ELE_VSI_MCL_INV_AVAILABILITY_LIST = "VSIMCLInvAvailabilityList";
	public static final String ELE_VSI_MCL_INV_AVAILABILITY = "VSIMCLInvAvailability";
	public static final String ATTR_AVAIl_QTY = "AvailableQty";
	public static final String ATTR_INV_AVAIL_KEY = "InvAvailKey";
	public static final String SERVICE_GET_MCL_INV_AVAILABILITY_LIST = "VSIGetMCLInvAvailabilityList";
	public static final String SERVICE_GET_MCL_INV_AVAILABILITY_DETAILS = "VSIGetMCLInvAvailabilityDetails";
	public static final String SERVICE_CREATE_MCL_INV_AVAILABILITY = "VSICreateMCLInvAvailability";
	public static final String SERVICE_CHANGE_MCL_INV_AVAILABILITY = "VSIChangeMCLInvAvailability";
	public static final String SERVICE_DELETE_MCL_INV_AVAILABILITY = "VSIDeleteMCLInvAvailability";
	public static final String CODE_TYPE_VSI_UPDATE_MCL_INV = "VSI_UPDATE_MCL_INV";
	public static final String CODE_VALUE_UPDATE_MCL_ITEM_INVENTORY = "UPDATE_MCL_ITEM_INVENTORY";
	public static final String API_MANAGE_ITEM = "manageItem";
	public static final String SERVICE_GET_ITEM_LIST = "VSIGetItemList";
	public static final String SERVICE_GET_MCL_ITEM_PRICE = "VSIGetMCLItemPrice";
	public static final String API_MONITOR_ITEM_AVAILBILITY = "monitorItemAvailability";
	public static final String ELE_MONITOR_ITEM_AVAILBILITY = "MonitorItemAvailability";
	
	public static final String ENT_ADP = "ADP";
	public static final String ELE_DETAILS = "Details";
	public static final String ELE_TAXES = "Taxes";
	public static final String ELE_EFFECTIVE_RATE = "EffectiveRate";
	public static final String ELE_CALCULATED_TAX = "CalculatedTax";
	
	public static final String VSI_SYS_CNCL_REASON = "VSI_SYS_CNCL_REASON";
	public static final String ATTR_REASON_CODE_NAME = "ReasonCodeName";
	public static final String MASHUP_GET_COMMON_CODE_LIST_VSI_CANCEL_REASON = "getCommonCodeList_VSICancelReason";
	public static final String ATTR_MODIFICATION_TYPE = "ModificationType";
	
	public static final String ATTR_IS_AVAIL_ON_STORE = "IsAvailableOnStore";
	public static final String ATTR_ONHAND_QTY = "OnHandQuantity";
	public static final String ATTR_ON_STORE_AVAIL_DATE = "OnStoreAvailableDate";
	public static final String ATTR_PROCURED_QTY = "ProcuredQty";
	public static final String ELE_AVAILABILITY_LIST = "AvailabilityList";
	public static final String ATTR_FUTURE_AVAIL_DATE = "FutureAvailableDate";
	public static final String DOUBLE_ZERO = "0.0";
	public static final String DOUBLE_ONE = "1.0";

	public static final String ATTR_SHIP_QTY = "ShippedQty";
	public static final String ELE_ORDER_LINE_LIST = "OrderLineList";
	public static final String API_GET_COMPLETE_ORDER_LINE_LIST = "getCompleteOrderLineList";
	public static final String TEMPLATE_GET_COMP_ORD_LINE_LIST_GET_STS_TRACK_INFO = "global/template/api/getCompleteOrderLineList_VSIGetSTSTrackingInfoAPI";
	public static final String ELE_TRACKING_INFO_LIST = "TrackingInfoList";
	
	//CRM Upgrade
	public static final String ATTR_SOAP_PREFIX = "b:";
	
	//OMS-875:Start
	public static final String FLAG_A = "A";
	//OMS-875:End
	
	//OMS -957:End
	public static final String COULD_NOT_SHIP_EMAIL = "COULD_NOT_SHIP";
	//OMS-957: End
	
	//OMS-978 : Start
	public static final String VAL_DEFAULT_IPAD = "10.0.0.1";
	//OMS-978 : End
	
	//OMS-1073  : Start 
    public static final String BETWEEN_QUERY = "BETWEEN";
    public static final String ATTR_STATUS_QRY_TYPE="StatusQryType";
    //OMS-1073 : End
    
    //OMS-1098 :Start
    public static final String HOLD_FRAUD_HOLD="VSI_FRAUD_HOLD";
    public static final String HOLD_FRD_REVIEW_HOLD="VSI_FRD_REVIEW_HOLD";
    public static final String ELE_RESOLUTION_DETAILS="ResolutionDetails";
    //OMS-1098:End

	//OMS-1169 : Start
    public static final String TEMPLATE_GET_ORD_LINE_LIST="global/template/api/getOrderLineList_VSIResolveStoreReceivedHoldOnCancellation.xml";
    public static final String STORE_RECEIVE_HOLD="STORE_RECEIVE_HOLD";
    public static final String STATUS_RESOLVED="1300";
    //OMS-1169 : End

	//OMS-915 :Stat
    public static final String TEMPLATE_GET_ORDER_LIST_CREATE_SHIPMENT="global/template/api/getOrderList_VSIBeforeShipmentCreation.xml";
    //OMS-915: End

    //OMS-864
    public static final String ATTR_SIMPLE = "simple";
	
	//Changes for Print Pick Pack for SOM
	public static final String SHIPMENT_LIST_PICK_PACK_TEMPLATE = "global/template/api/getShipmentList_SOM_PrintPickPack.xml";
	
	//Changes for Print Pick Pack for SOM: NewChanges-14Aug2020
	public static final String ELE_BILL_TO_ADDRESS = "BillToAddress";
    //Parcel logic :Start determineRouting 
    public static final String TEMPLATE_DETERMINE_ROUTING="global/template/api/determineRouting.xml";
    public static final double VAL_CONTAINER_WEIGHT_CONVERTER=1.3;
    //Parcel logic: End
	
  //OMS-1653 start
  	public static final String ATTR_LOYALTY_TIER = "ExtnLoyaltyTier";
  	public static final String ATTR_LOYALTY_SILVER = "Silver";
  	public static final String ATTR_LOYALTY_GOLD = "Gold";
  	public static final String ATTR_LOYALTY_BRONZE = "Bronze";
  	
  	//OMS-1653 end
  	//OMS-1683  start
  	public static final String ATTR_CONTACT_TIME ="ContactTime";
  	public static final String ELE_NOTE_LIST="NoteList";
  	//OMS-1683  end
  	
  //OMS-1654 start
  	public static final String ELE_ITEM_LIST = "ItemList";
  	public static final String ATTR_EXTN_CLASS = "ExtnDept";
  	public static final String ATTR_EXTN_DEPT = "ExtnClass";
  	
  //OMS-1654 End
	
	// Wholesale
    public static final String WHOLESALE = "WHOLESALE";    
    public static final String VSI_WHOLESALE_ORDER_ISSUE = "VSI_WHOLESALE_ORDER_ISSUE";
    public static final String ALERT_WHOLESALE_ORDER_ISSUE_DESC = "Price & Item Discrepancy";
    public static final String ENT_NAVY_EXCHANGE = "NAVY_EXCHANGE";
    public static final String ELE_PRICE_LIST_HEADER = "PricelistHeader";
    public static final String ATTR_PRICING_STATUS = "PricingStatus";
    public static final String API_GET_PRICE_LIST_LINE_LIST_FOR_ITEM="getPricelistLineListForItem";
    public static final String ELE_PRICELIST_LINE = "PricelistLine";
    
    public static final String VSI_WH_SCHED_RULE="VSI_WH_SCHED_RULE";
    public static final String VSI_WH_VALIDATE_ITEM="VSI_WH_VALIDATE_ITEM";
    public static final String VSI_WH_CHECK_PRICE="VSI_WH_CHECK_PRICE";
    public static final String ELE_REGIONS = "Regions";
    public static final String ELE_REGION = "Region";
    public static final String ATTR_REGION_SCHEMA_KEY = "RegionSchemaKey";
    public static final String ATTR_REGION_NAME = "RegionName";
    public static final String ALL_US = "ALL_US";
    public static final String API_GET_REGION_LIST = "getRegionList";
    public static final String VSI_WH_CNCL_ON_REJ ="VSI_WH_CNCL_ON_REJ";
    
    public static final String VSI_WH_ALLOC_CASEQTY = "VSI_WH_ALLOC_CASEQTY";
    public static final String ATTR_ORDER_REFERENCE="OrderReference";
    public static final String ATTR_TOTAL_REQ_QTY = "TotalRequiredQty";
    public static final String ATTR_EXTN_CASE_QTY="ExtnCaseQuantity";
    public static final String SERVICE_GET_ORDER_LIST= "VSIWholeSaleGetOrderList";
    public static final String SERVICE_GET_ITEM_LIST_FOR_AVAILABILITY= "VSIWholeSaleGetItemList";  
    public static final String ATTR_SOFT_ASSIGNED_QTY = "SoftAssignedQty";
	public static final String VSI_WH_INVOICE_LEVEL_REQ = "VSI_WH_INV_LEVEL_REQ";
	public static final String ATTR_WH_INVOICE_LEVEL_SHIP="SHIPMENT";
	public static final String ATTR_WH_INVOICE_LEVEL_ORDER="ORDER";
	public static final String ATTR_WH_INVOICE_LEVEL_TRAILER="TRAILER";
	public static final String ATTR_VSI_WH_INVCREATE_HLD="VSI_WH_INVCREATE_HLD";
	public static final String SERVICE_CHANGE_ORDER="VSIWholeSaleChangeOrder";
	public static final String SERVICE_GET_WHOLESALE_CUSTOMER_DETAILS="VSIGetDetailsForWholesaleCustomer";
	public static final String ELE_PERSON_INFO_MARK_FOR = "PersonInfoMarkFor";
	public static final String ELE_IGNORE_STATUS_CHECK = "IgnoreStatusCheck";
	public static final String ELE_IGNORE_TRANSACTION_DEPENDENCIES = "IgnoreTransactionDependencies";
	public static final String ELE_VSI_CREATE_ORDER_INVOICE ="VSI_Crt_Ord_Inv.0001.ex";
	public static final String ATTR_EXTN_MARK_FOR_STORE_NO = "ExtnMarkForStoreNo";
	public static final String ATTR_EXTN_LAST_TRAILER_SHIP ="ExtnLastTrailerShip";
	public static final String ATTR_TRAILER_NO = "TrailerNo";
	public static final String ATTR_EXTN_TRAILER_NO = "ExtnTrailerNo";
	public static final String ATTR_EXTN_SEND_TRAILER_INVOICE = "ExtnSendTrailerInvoice";
	public static final String API_CHANGE_ORDER_INVOICE = "changeOrderInvoice";
	public static final String TEMPLATE_GET_SHIPMENT_LIST_VSI_UPDATE_TRAILER = "global/template/api/getShipmentList_VSIUpdateTrailerInInvoice.xml";
	public static final String ELE_ORDER_INVOICE_DTL = "OrderInvoiceDetail";
	public static final String API_ORDER_INVOICE_DTL_LIST = "getOrderInvoiceDetailList";
	public static final String API_GET_SHIPMENT_LIST_FOR_ORDER = "getShipmentListForOrder";
	public static final String ATTR_ADDR_TYPE = "AddressType";
	public static final String ATTR_REMIT = "REMIT";
	public static final String ELE_PERSON_INFO_REMIT_TO = "PersonInfoRemitTo";
	public static final String ATTR_PREFERRED_SHIP_ADD = "PreferredShipAddress";
	public static final String VSI_WH_SEND_INV_MODE = "VSI_WH_SEND_INV_MODE";
	public static final String ATTR_SHIPMENT_NO = "ShipmentNo";
	public static final String ATTR_ORDER_INVOICE_DTL_LIST = "OrderInvoiceDetailList";
	public static final String SERVICE_GET_ORDER_INVOICE_DTL_LIST = "VSIWholeSaleGetOrdInvoiceDtlList";
	public static final String SERVICE_GET_SHIPMENT_LIST_FOR_ORDER = "VSIWholeSaleGetShipmentListForOrder";
	public static final String ATTR_SEND_INVOICE = "SendInvoice";
	public static final String SERVICE_CHANGE_ORDER_INVOICE="VSIWholeSaleChangeOrderInvoice";
	
	//Credit Limit Constants
	public static final String ELE_VSI_WH_CUST_CREDIT_DETAILS="VSIWhCustCreditDetails";
	public static final String SERVICE_GET_CUST_CREDIT_DTLS_LIST = "VSIWholesaleGetCustCreditDetailsList";
	public static final String SERVICE_UPDATE_CUST_CREDIT_DTLS = "VSIWholeSaleUpdateCustCreditLimitDtls";
	public static final String SERVICE_CREATE_CUST_CREDIT_DTLS = "VSIWholeSaleCreateCustCreditLimitDtls";
	public static final String SERVICE_CREDIT_DISCREPANCY_EMAIL="VSIWholesaleCreditDiscrepancyEmail";
	public static final String ATTR_CREDIT_LIMIT="CreditLimit";
	public static final String ATTR_CREDIT_AVAILED="CreditAvailed";
	public static final String ATTR_CURRENT_BALANCE="CurrentBalance";
	public static final String ATTR_VSI_WH_CRDT_DIS_HOLD = "VSI_WH_CRDT_DIS_HOLD";
	public static final String ATTR_CUST_CREDIT_DTLS_KEY="CustCreditDetailsKey";
	public static final String ATTR_ORIGINAL_TOTAL_AMOUNT="OriginalTotalAmount";
	public static final String ATTR_VSI_VIRTUAL_STORE_NO ="VSI_VIRTUAL_STORE_NO";
	public static final String LIKE="LIKE";
	public static final String ATTR_CREDIT_HOLD_FLAG = "CreditHoldFlag";
	public static final String ATTR_CUST_ID_QRY_TYPE="CustomerIDQryType";
		
	public static final String ATTR_VSI_WH_ASN_TYPE="VSI_WH_ASN_TYPE";
	public static final String ELE_SHIPMENT_LIST="ShipmentList";
	public static final String ATTR_ARG_1="Arg1";
	public static final String STR_TRAILER="trailer";
	public static final String ATTR_TRAILER="Trailer";
	public static final String SERVICE_GET_SHIPMENT_LIST_FOR_ASN="VSIWholesaleGetShipmentListForASN";
	public static final String SERVICE_POST_ASN_MSG_TO_IIB = "VSIWholesalePostASNMessageToIIB";
	public static final String SERVICE_CHANGE_SHIPMENT_FOR_ASN ="VSIWholesaleChangeShipmentForASN";
	public static final String ATTR_PAYMENT_TERMS ="PaymentTerms";
	public static final String ATTR_TERMS_CODE ="TermsCode";
	public static final String STRING_VSI_AR_CREDIT_UPDATE="VSI_AR_CREDIT_UPDATE";
	public static final String STRING_VSI_WHOLESALE_AR_CREDIT_Q="VSI_WHOLESALE_AR_CREDIT_Q";
	public static final String TRAN_ID_CREATE_SHMNT_INVOICE_0001 = "CREATE_SHMNT_INVOICE.0001";
	public static final String API_CREATE_SHIPMENT_INVOICE = "createShipmentInvoice";
	public static final String SERVICE_GET_ORDER_INVOICE_LIST="VSIWholeSaleGetOrderInvoiceList";
	public static final String STATUS_CODE_SHIPPED = "3700";
	public static final String SERVICE_GET_ORDER_INVOICE_DTL_LIST_RETURN="VSIWholeSaleGetOrderInvoiceListReturn";
	public static final String WHOLESALE_ORG_LEVEL_CASE_QTY = "WHOLESALE_ORG_LEVEL_CASE_QTY";
	public static final String ATTR_ATTRIBUTE_GROUP_ID = "AttributeGroupID";
	public static final String ATTR_ATTRIBUTE_DOMAIN_ID = "AttributeDomainID";
	public static final String ELE_ADDNL_ATTRIBUTE_LIST = "AdditionalAttributeList";
	public static final String ELE_ADDNL_ATTRIBUTE = "AdditionalAttribute";
	public static final String ITEM_ATTRIBUTE = "ItemAttribute";
	public static final String API_GET_CHARGE_NAME_LIST = "getChargeNameList";
	public static final String UPPER_CASE_SHIPPING = "SHIPPING";
	public static final String ATTR_CHARGE_CATEGORY_AND_NAME = "ChargeCategoryAndName";
	public static final String SERVICE_GET_CHARGE_NAME_LIST = "VSIGetChargeNameList";
	
	public static final String ATTR_DISPLAY_LOCALIZED_FIELD_IN_LOCALE = "DisplayLocalizedFieldInLocale";
	public static final String ATTR_EN_US_EST = "en_US_EST";
	public static final String ELE_ORG_ROLE = "OrgRole";
	public static final String ELE_ORG_ROLE_LIST = "OrgRoleList";
	public static final String ATTR_ROLE_KEY = "RoleKey";
	public static final String ATTR_ENTERPRISE = "ENTERPRISE";
	public static final String ATTR_SELLER = "SELLER";
	public static final String ELE_DATA_ACCESS_FILTER = "DataAccessFilter";
	public static final String ELE_ORDER_BY = "OrderBy";
	public static final String ELE_ATTRIBUTE = "Attribute";
	public static final String ATTR_DESC = "Desc";
	public static final String ATTR_ORGANIZATION_NAME = "OrganizationName";
	public static final String ATTR_APPLY_QUERY_TIMEOUT = "ApplyQueryTimeout";
	public static final String ATTR_DOCUMENT_TYPE_SALES = "0001";
	public static final String ATTR_ORDER_DATE_QRY_TYPE = "OrderDateQryType";
	public static final String ATTR_DATERANGE = "DATERANGE";
	public static final String ATTR_QUERY_TIMEOUT = "QueryTimeout";
	public static final String TIME_OUT_60 = "60";
	public static final String ATTR_READ_FROM_HISTORY = "ReadFromHistory";
	public static final String OR_QUERY = "Or";
	public static final String ATTR_EXTN_UNIQUE_TRAILER_ID = "ExtnUniqueTrailerID";
	public static final String ATTR_USER_Id = "UserId";
	public static final String SERVICE_GET_ORG_LIST = "VSIGetOrganizationList";
	public static final String ELE_INVOICE_DETAIL_LIST = "InvoiceDetailList";
	public static final String SERVICE_GET_ORDER_INVOICE_DETAILS = "VSIWholesaleGetOrderInvoiceDetails";
	public static final String CREDIT_MEMO = "CREDIT_MEMO";
	public static final String DEBIT_MEMO = "DEBIT_MEMO";
	public static final String SERVICE_WHOLESALE_GET_SHIPMENT_LIST = "VSIWholesaleGetShipmentList";
	public static final String ATTR_EXTN_IS_LAST_INVOICE_FOR_ORDER = "ExtnIsLastInvoiceForOrder";
	public static final String ATTR_NEW_VALUE = "NewValue";
	public static final String ATTR_OLD_VALUE = "OldValue";
	public static final String ATTR_INVOICE_COMPLETE = "InvoiceComplete";
	public static final String ATTR_EXTN_VENDOR_ID = "ExtnVendorID";
	public static final String ATTR_EXTN_COST_CENTER = "ExtnCostCenter";
	public static final String ATTR_BYPASS_PRICING = "BypassPricing";
	public static final String ATTR_TERMS_DISCOUNT_DUE_DAYS = "TermsDiscountDueDays";
	public static final String ATTR_TERMS_NET_DUE_DAYS = "TermsNetDueDays";
	public static final String ATTR_TERMS_DISCOUNT_PERCENTAGE = "TermsDiscountPercentage";
	public static final String ATTR_NUM_OF_CARTONS = "NumOfCartons";
	public static final String WHOLESALE_ORG_ITEM_EXPIRY_DAYS = "WHOLESALE_ORG_ITEM_EXPIRY_DAYS";
	public static final String ATTR_EXPIRY_DAYS = "ExtnExpiryDays";
	public static final String ATTR_CALLING_ORGANIZATION_CODE = "CallingOrganizationCode";
	public static final String ELE_INVOICED_TOTALS = "InvoicedTotals";
	public static final String ATTR_ENTERPRISE_KEY = "EnterpriseKey";
	public static final String ATTR_INTEGER_VALUE = "IntegerValue";
	public static final String ATTR_EXTN_IS_WHOLESALE_ORG = "ExtnIsWholesaleOrg";
	public static final String ATTR_CHARGES = "Charges";
	public static final String UPPER_CASE_SWEETENED = "SWEETENED";
	public static final String ALERT_STATUS_OPEN = "OPEN";
	public static final String ALERT_STATUS_CLOSED = "CLOSED";
	public static final String ATTR_USER_GROUP = "UserGroup";
	public static final String ATTR_USER_GROUP_ID = "UsergroupId";
	public static final String ATTR_CONSOLIDATION_WINDOW = "ConsolidationWindow";
	public static final String CONSOLIDATION_WINDOW_FOREVER = "FOREVER";
	public static final String ELE_LINE_PRICE_DETAILS = "LinePriceDetails";
	public static final String ELE_LINE_PRICE_DETAIL = "LinePriceDetail";
	public static final String LINE_TYPE_CREDIT = "Credit";
	public static final String CODE_TYPE_RETURN_REASON = "RETURN_REASON";
	public static final String ATTR_HOLD_TYPE_USER_GROUP_LIST = "HoldTypeUserGroupList";
	public static final String ATTR_HOLD_TYPE_USER_GROUP = "HoldTypeUserGroup";
	public static final String ATTR_PERMISSION = "Permission";
	public static final String HOLD_PERMISSION_RESOLVE = "RESOLVE";
	public static final String API_GET_HOLD_TYPE_LIST = "getHoldTypeList";
	public static final String ATTR_ENABLED = "Enabled";
		//OMS-1779 START
	public static final String STATUS_RESERVED = "Reserved";
	//OMS-1779 END
  //OMS-1734: Start
  	public static final String ATTR_CONS_WINDOW = "ConsolidationWindow";
  	public static final String VAL_FOREVER="FOREVER";	
  //OMS-1734: End	
  //OMS-1572: Start
  	public static final String API_GET_SHIPNODE_LIST="getShipNodeList";
  	public static final String TEMPLATE_GET_SHIPNODE_LIST="global/template/api/VSIGetShipNodeList.xml";
  	public static final String EMAIL_HEADER="THE VITAMIN SHOPPE ";
  	public static final String ATTR_ORG_NAME="OrganizationName";
  //OMS-1572: End
  	//OMS-1797 : Start
	public static final String CODETYPE_WH_ORD_PREFIX ="WHOLESALE_ORD_PREFIX";
	public static final String SEQ_NAVY_EXCHANGE_INVOICE="VSI_INVOICE_SEQ_NAVY_EXCHANGE";
	//OMS-1797 : End
		//OMS-1801 : Start
  	public static final String ATTR_EXTN_COLOR = "ExtnColor";
  	public static final String ATTR_EXTN_STYLE = "ExtnStyle";
//OMS-1801 : End
	//OMS-1689
	public static final String CODETYPE_MARKETPLACE_ENT="VSI_MARKET_PLACE_ENT";
	public static final String ENT_TYPE_1="1";
	public static final String ENT_TYPE_2="2";
	//OMS-1689
	//OMS-1804:Start
	public static final String CAN_CODE_PRICE_ISSUE="Price Discrepancy Issue";
	public static final String ATTR_IS_CANCELLED="IsCancelled";
	//OMS-1804: End
  
  //OMS-1631 START
	public static final String ATTR_FROM_ORDER_DATE="FromOrderDate";
	public static final String ATTR_TO_ORDER_DATE="ToOrderDate";
	//OMS-1631 END
//OMS-1693: Start
	public static final String DATE_TYPE_FTC_FIRST_PROMISE_DATE="YCD_FTC_FIRST_PROMISE_DATE";
	//OMS-1693: End
	//OMS-1875: Start
		public static final String ATTR_CUSTOMER_NUMBER="customerNumber";
		public static final String ATTR_CUSTOMER_EMAIL="customerEmail";
//OMS-1875: End
	//OMS-1892: start
		public static final String ATTR_ACTUAL_SHIPDATE="ActualShipDate";
		public static final String ATTR_EXTN_SHIP_DATE="ExtnShipDate";
		public static final String ATTR_SHIPMENT_DATE="ShipmentDate";
		public static final String CODE_TYPE_VSI_SHIPDATE_RULE="VSI_SHIPDATE_RULE";
		public static final String CODE_VALUE_STAMP_WMS_DATE="StampWMSDate";
		public static final String ATTR_EXTN_DTC_ORDER="ExtnDTCOrder";
		public static final String ATTR_EXTN_SUBSCRIPTION_ORDER="ExtnSubscriptionOrder";
		public static final String ATTR_EXTN_ORIGINAL_ADP_ORDER="ExtnOriginalADPOrder";
		public static final String ATTR_EXTN_RE_ORDER="ExtnReOrder";
		public static final String ATTR_CURRENT_SHIPDATE="CurrentShipDate";
		public static final String ATTR_EXTN_SHIPMENT_SEQ_NO="ShipmentSeqNo";
	//OMS-1892: End
//OMS-1881: Start
		 public static final String CSR_CANCELLED_HOLD="CSR_CANCELLED_HOLD";
		//OMS-1881: end
		//OMS-1901: Start
		public static final String ATTR_GOOGLE_EXPRESS = "GOOGLE_EXPRESS";
		//OMS-1901: end
		//OMS-1913 : Start
		public static final String STATUS_STS_NO_ACTION="3200.401";
		public static final String TEMPLATE_GET_ORD_LIST_ORDSTATUS="global/template/api/VSIGetOrderListForOrderStatus.xml";
		//OMS-1913 : End
		//OMS-1896: Start
		public static final String ELE_ITEM_SHIP_NODE = "ItemShipNode";
		public static final String ATTR_SHIPNODE_KEY = "ShipnodeKey";
		public static final String ATTR_SHIPNODE_PRIORITY = "2.00";
		public static final String ATTR_CSC = "TWODAY";
		public static final String ATTR_REGION_SCHEMA_NAME = "RegionSchemaName";
		public static final String ELE_DISTRIBUTION_RULE = "DistributionRule";
		public static final String API_GET_DISTRIBUTION_RULELIST = "getDistributionRuleList";
		public static final String CODE_TYPE_VSI_NEW_FLOW="VSI_NEW_FLOW";
		public static final String CODE_VALUE_STAMP_CSC="StampCSC";
		public static final String TEMPLATE_GET_REGION_LIST="global/template/api/VSIRegionSchemaLoad_getRegionList.xml";
		public static final String TEMPLATE_GET_DISTRIBUTION_RULE_LIST="global/template/api/VSIGetDistributionRuleList.xml";
		public static final String ATTR_NAVY_EAST_REGION = "US_NAVY_EXCHANGE_EAST";
		public static final String ATTR_NAVY_WEST_REGION = "US_NAVY_EXCHANGE_WEST";		
		//OMS-1896: end
		
		//OMS-1937: Start
		public static final String TEMPLATE_GET_ORDER_LIST_IVR="global/template/api/VSIGetOrderListIVR.xml";
		public static final String ATTR_TOTAL_ORDER_LIST = "TotalOrderList";
		//OMS-1937: 
		//OMS-1345: Start
        public static final String CODE_VALUE_CCURL="CCURL";
        public static final String VALUE_TNS="TNS";
        public static final String VALUE_DP="DP";
        public static final String CODE_VALUE_TNS_USER_ID="TNS_USER_ID";
        public static final String CODE_VALUE_TNS_PASSWORD="TNS_PASSWORD";
        
        //OMS-1345: End
		//OMS-1742: Start
		public static final String ATTR_VSI_ALERT_GIFT_CARD="VSI_ALERT_GIFT_CARD";
		public static final String ATTR_GCTHRESHOLD = "GCTHRESHOLD";
		//OMS-1742: 
		//OMS-1970: Start
		public static final String STATUS_STS_STORE_RECEIVED="2160.400";
		public static final String STATUS_STS_TO_CREATED="2160";
		public static final String STATUS_STS_SENT_TO_WH="2160.10";
		public static final String STATUS_STS_RELEASE_ACK="2160.20";
		public static final String STATUS_STS_COMPLT="2160.200";
		public static final String STATUS_STS_JDA_STS_ACK="3200.411";
		//OMS-1970: End
		//OMS-1951: Start
		public static final String ATTR_EXTN_TNS_AUTHORIZED="ExtnTnsAuthorized";
		//OMS-1951: End
		//OMS-2005 : Start
		public static final String STATUS_ACK="1500.100";
		//OMS-2005 : End
		//OMS-2010: Start	
		public static final String ATTR_TRAN_SEQ_WHOLESALE = "1_";
		//OMS-2010: end	
		//OMS-2046: Start
		public static final String ATTR_CURRENCY="USD";
		public static final String ATTR_GRAND_DISCOUNT="GrandDiscount";
		public static final String ATTR_PHONE="phone";
		public static final String ATTR_SUBSCRIPTION="subscription";
		public static final String ATTR_EXTN_SESSION_ID="ExtnSessionID";
		public static final String ATTR_EXTN_BRAND_TITLE="ExtnBrandTitle";
		public static final String ATTR_WEB="web";
		public static final String ATTR_STORE_PICK_UP="store pick up";
		public static final String ATTR_CREDIT_CARD_TYPE="CreditCardType";
		public static final String ATTR_PAYMENT_REFERENCE_3 = "PaymentReference3";
		public static final String ATTR_EXTN_BROWSER_IP="ExtnBrowserIP";
		public static final String ATTR_EXTN_CUSTOMER_CREATETS="ExtnCustomerCreatets";
		public static final String ATTR_EXTN_ACCEPT_USER="ExtnAcceptLang";
		public static final String ATTR_USER_AGENT="ExtnUserAgent";
		public static final String ATTR_STATUS_APPROVE="approved";
		public static final String ATTR_STATUS_DECLINED="declined";
		public static final String ATTR_RISKIFIED="RISKIFIED";
		public static final String ATTR_CHECKOUT="CHECKOUT";
		public static final String ATTR_RISKIFIED_CODE_TYPE="VSI_RISKIFIED_FLAG";
		public static final String ATTR_INITIAL_SUBSCRIPTION="initial_subscription";
		public static final String ATTR_EXTN_CHECKOUTID="ATGOrderId";
        public static final String ATTR_CREATE_DATE="CreateDate";	
        public static final String ATTR_AQR_CODE="ACQUIRERCODE";
	    public static final String ATTR_BOTH="BOTH";	
        public static final String ATTR_EXTN_RISKIFIED_SESSION_ID="ExtnRiskifiedSession";		
				//OMS-2046: End
				
		  //Riskified OMS-2054, OMS-2053, OMS-2051 START
        public static final String ELE_COLLECTION_DEATILS="CollectionDetails";
        public static final String ATTR_TOTAL_REFUNDED_AMOUNT="TotalRefundedAmount";
        public static final String ATTR_FULFILL_SHIPMENTS="SHIPMENTS";
        public static final String FULL_RETURN_AND_CANCEL="FULL_RETURN_AND_CANCEL";
        public static final String PARTIAL_CANCEL_AND_RETURN="PARTIAL_CANCEL_AND_RETURN";
        public static final String ELE_PAYMENT_RECORDS="PaymentRecords";
        public static final String ATTR_RECORD="Record";
        public static final String API_VSI_PAYEMENT_RECORDS="VSIPaymentRecords";
        public static final String STATUS_RETURN_CREATED="3700.01";
        public static final String SUCCESS="success";
        public static final String VSI_KOUNT_STH_HOLD="VSI_KOUNT_STH_HOLD";
        public static final String VSI_FRAUD_CHECK_DECLINED="Fraud Check Declined";
        public static final String VSI_FULL_RETURN="FullReturn";
        public static final String VSI_FULL_CANCEL="FullCancel";
        public static final String  VSI_FULFILL_SHIPMENT="FulFillShipment";
        public static final String  VSI_PARTIAL_RETURN="PartialReturn";
        public static final String VSI_PARTIAL_CANCEL="PartialCancel";
        public static final String VSI_SEQ_PRTL_CNCLORRTN="VSI_SEQ_RISK_PRTL_CNCLORRTN";
        //Riskified OMS-2054, OMS-2053, OMS-2051 END
        
        //OMS-2150 Start     	
     	public static final String ATTR_EXTN_ITEM_SIZE = "ExtnItemSize";
        //OMS-2150 End
		
		//OMS-2177 Start     	
     	public static final String ATTR_EXTN_MOBILE_ORDER = "ExtnMobileOrder";
     	public static final String ATTR_EXTN_MOBILE_TYPE = "ExtnMobileType";
        //OMS-2177 End
		
		//OMS-2175 Start
     	public static final String STATUS_RETURN_CUSTCANKEEP="3700.01.20.ex";
     	//OMS-2175 End
		
		//OMS-2192 Start
     	public static final String ATTR_REORDER="Reorder";
     	//OMS-2192 End
		
		//OMS-2213 Start
     	public static final String ATTR_GUEST_CHECKOUT="ExtnIsGuestCheckout";
     	public static final String ATTR_CHECKOUT_TYPE_GUEST="guest";
     	public static final String ATTR_CHECKOUT_TYPE_REGISTERED="registered";
     	//OMS-2213 End
		
		//OMS-2216 Start
     	public static final String ELE_DERIVED_FROM_ORDER="DerivedFromOrder";
     	//OMS-2216 End
		//OMS-2088 start
     	public static final String FRAUD_RESULT = "FraudResult";
		//OMS-2225: Start
     	public static final String ATTR_SHIPMENT_FLOW="ShipmentNewFlow";
		public static final String ATTR_STS_SHIPMENT_FLOW="STSNewFlow";
     	public static final String ATTR_SHIPMENT_CODE_TYPE="VSI_SHIPMENT_FLAG";
     	//OMS-2225 : End
		
		//OMS-2375: Start
		public static final String ATTR_CUSTOMER_TIER="ExtnCustomerTier";
		//OMS-2375: End

	// OMS -2345 Start

    	public static final String TEMPLATE_ORDER_LINE_LIST = "global/template/api/getOrderLineList_VSIIsBackordered.xml";
    	public static final String ATTR_IS_BACKORDERED = "ExtnIsBackordered";
    	public static final String ATTR_MODIFY_REASON_TEXT = "VSI_BACKORDERED";
    	public static final String ATTR_BACKORDERED_HOLD_TYPE = "BACKORDER_HOLD";
    	public static final String ALERT_EXCEPTION_TYPE = "VSI_BACKORDER_ALERT";
    	public static final String ALERT_DETAIL_DESCRIPTION = "Orders are in Backorder Hold, Kindly take necessary action.";
    	public static final String ALERT_BO_QUEUE = "VSI_BACKORDER_ALERT_Q";
    	public static final String ATTR_ITEMID = "ItemId";
    	public static final String RESOLVE_EXCEPTION_API = "resolveException";
    	public static final String ATTR_RESOLVE_MODIFY_REASON_TEXT = "Resolved after intimation timeframe is completed";
    	public static final String API_GET_ORDER_HOLD_TYPE_LIST = "getOrderHoldTypeList";
    	public static final String TEMPLATE_GET_ORDER_HOLD_TYPE_LIST = "global/template/api/getOrderHoldTypeList_VSIIsBackordered.xml";
    	public static final String BO_COMMON_CODE = "BO_BIZ_CONFIRM_AWAIT";
    	public static final String ATTR_LAST_HOLD_TYPE_DATE = "LastHoldTypeDate";
    	public static final String EST_TIME_ZONE = "EST5EDT";
    	
    	//OMS -2345 End

//OMS - 2342 Start
    	
    	public static final String ELE_ERROR_RESPONSE="ErrorResponse";
    	public static final String ELE_JDA_SKU="JdaSku";
    	public static final String ALERT_SEND_RELEASE_JDA_EXCEPTION_TYPE = "VSI_SEND_RELEASE_JDA_ALERT";
    	public static final String ALERT_SEND_RELEASE_JDA_DETAIL_DESCRIPTION = "Send Release JDA Exception Alert";
    	public static final String ALERT_SEND_RELEASE_JDA_QUEUE = "VSI_SEND_RELEASE_JDA_ALERT_Q";
    	public static final String ALERT_SEND_RELEASE_JDA_WMS_EXCEPTION_TYPE = "VSI_SEND_RELEASE_JDA_WMS_ALERT";
    	public static final String ALERT_SEND_RELEASE_JDA_WMS_DETAIL_DESCRIPTION = "Send Release JDA WMS Exception Alert";
    	public static final String ALERT_SEND_RELEASE_JDA_WMS_QUEUE = "VSI_SEND_RELEASE_JDA_WMS_ALERT_Q";
    	public static final String ALERT_JDA_REVERSE_EXCEPTION_TYPE = "VSI_JDA_REVERSE_ALLOCATION_ALERT";
    	public static final String ALERT_JDA_REVERSE_DETAIL_DESCRIPTION = "JDA Reverse Allocation Exception Alert";
    	public static final String ALERT_JDA_REVERSE_QUEUE = "VSI_JDA_REVERSE_ALERT_Q";
    	public static final String ALERT_JDA_SERVICE_CALL_FAILURE_EXCEPTION_TYPE = "VSI_JDA_SERVICE_CALL_FAILURE_ALERT";
    	public static final String ALERT_JDA_SERVICE_CALL_FAILURE_DETAIL_DESCRIPTION = "JDA Service Failure Alert";
    	public static final String ALERT_JDA_SERVICE_CALL_FAILURE_QUEUE = "VSI_JDA_SERVICE_FAILURE_ALERT_Q";
    	public static final String ATTR_REVERSE_ALLOCATION = "ReverseAllocation";
    	public static final String ATTR_SEND_RELEASE = "SendRelease";
    	public static final String PROP_FORCED_ALLOCATION_URL = "FORCED_ALLOCATIOIN_URL";
    	public static final String XPATH_ORDER_RELEASE_EXTN = "//OrderRelease/Extn";
    	public static final String UTF8_ENCODING = "UTF-8";
    	public static final String JDA_POST_METHOD = "POST";
    	public static final String JDA_REQUEST_PROP = "Content-Length";
    	public static final String SERVICE_JDA_ALLOCATION_RESPONSE = "VSIInvokeJDAoutput_DB";
    	public static final String JDA_ALLOCATION_TYPE = "JdaAllocationType";
    	public static final String JDA_REVERSE_ALLOCATION_TYPE = "Send Release - Reverse Allocation";
    	public static final String JDA_FORCE_ALLOCATION_TYPE = "Send Release - Force Allocation";
    	public static final String JDA_EXCEPTION_ERROR_CODE = "EXTN_SEND_RELEASE_ERROR";
    	public static final String JDA_EXCEPTION_ERROR_MSG = "Posting Send Release XML to WMS Queue got failed";
    	public static final String JDA_ALLOCATION_EXCEPTION_ERROR_CODE = "EXTN_JDA_ERROR";
    	public static final String JDA_ALLOCATION_EXCEPTION_ERROR_MSG = "JDA Force/Reverse Allocation failed Kindly Check Alerts for More Information";
    	public static final String ELE_REGISTER_PROCESS_COMPLETION_INPUT = "RegisterProcessCompletionInput";
    	public static final String API_REGISTER_PROCESS_COMPLETION = "registerProcessCompletion";
    	public static final String ATTR_KEEPTASKOPEN = "KeepTaskOpen";
    	public static final String ATTR_CURRENTTASK = "CurrentTask";
    	public static final String ATTR_TASKQKEY = "TaskQKey";
    	
    	//OMS - 2342 End
		
        //OMS-2075 Start
     	public static final String EXTN_AURUS_TOKEN = "ExtnAurusToken";
     	//OMS-2075 End
		
		//OMS - 2388 Start
    	
    	public static final String CANCELLATION_MSG = "Negative Order Total";
    	public static final String CANCELLATION_ALERT_EXCEPTION_TYPE = "VSI_NEGATIVE_ORDER_TOTAL_CANCEL_ALERT";
    	public static final String CANCELLATION_ALERT_DETAIL_DESCRIPTION = "Cancelling the order due to negative order total, please contact customer for new order placement.";
    	public static final String CANCELLATION_ALERT_QUEUE = "VSI_NEGATIVE_ORDER_TOTAL_CANCEL_ALERT_Q";
    	public static final String CANCEL_STATUS_FLAG = "NegativeOrderTotalStatusFlag";
    	
    	//OMS - 2388 End
		
		//OMS - 2441 START
    	
    	public static final String ATTR_EXTN_LAST_PICK_DATE = "ExtnLastPickDate";
    	public static final String TEMPLATE_GET_ORDER_LIST_FOR_ORDER_MONITOR = "global/template/api/VSIGetOrderList.xml";
    	public static final String ATTR_EXTN_LAST_PICK_DATE_FOR_CANCEL = "ExtnLastPickDateForCancel";
    	public static final String BOPUS_CANCEL_COMMON_CODE = "BOPUS_CANCEL_PICK_DT";
    	public static final String EXTN_CANCEL_LPT_COMMON_CODE = "ExtnCancelLastPickDate";
    	
    	//OMS - 2441 END
		
	//OMS - 2469 START
    	
    	public static final String SOM_ENABLED_COMMON_CODE_TYPE = "SOM_ENABLED";
    	public static final String SOM_ENABLED_COMMON_CODE = "SOMEnabled";
    	public static final String SOM_ENABLED_COMPLETE = "COMPLETE";
    	public static final String SOM_ENABLED_PARTIAL = "PARTIAL";
    	public static final String SOM_CUTOVER_COMMON_CODE_TYPE = "SOM_CUTOVER";
    	public static final String ATTR_CONDITION_VARIBALE1 = "ConditionVariable1";
    	public static final String STATUS_SOM_READY_FOR_PICKUP = "3350.30";
    	public static final String STATUS_SOM_READY_FOR_PICKUP_PROCESS = "3350";
    	public static final String ATTR_SOM_SHIPMENT_TYPE = "SOM_SHIPMENT";
	public static final String ARG_SOM_PIPELINE_ID = "SOM_PIPELINE_ID";
    	
    	//OMS - 2469 END

       //OMS - 2491 START
    	
    	public static final String ELE_SHIPNODE = "Shipnode";
    	public static final String STORE = "Store";
	public static final String ATTR_SFS_ALLOCATION = "SfsAllocation";
    	public static final String SHIP_NODE_9004_VALUE = "9004";
    	public static final String SHIP_NODE_9005_VALUE = "9005";
	public static final String JDA_SFS_FORCE_ALLOCATION_TYPE = "Send Release - SFS - Force Allocation";
    	
    	//OMS - 2491 END
		
				//OMS - 2485 START
       
        public static final String NO_SFS_CLASSIFICATION = "NO_SFS";
        public static final String CSC_NEXTDAY = "NEXTDAY";
        public static final String CSC_TWODAY = "TWODAY";  
        public static final String ATTR_EXTN_SFS_ELIGIBLE = "ExtnSFSEligible";		
       
        //OMS - 2485 END
		
		//OMS - 2617 START
        
        public static final String SCAC_SERVICE_NEXTDAY = "UPSN-NEXTDAY";
        public static final String SCAC_SERVICE_TWODAY = "UPSN-TWODAY";  
        
        //OMS - 2617 END
		
		//OMS - 2502 START
       
        public static final String API_GET_SHIPMENT_CONTAINER_LIST = "getShipmentContainerList";
        public static final String TEMPLATE_GET_SHIPMENT_CONTAINER_LIST = "global/template/api/VSIGetShipmentContainerList.xml";
        public static final String ATTR_EXTN_PARCEL_TYPE = "ExtnParcelType";
        public static final String PARCEL_WEIGHT_COMMON_CODE_TYPE = "PARCEL_WEIGHT";
        public static final String PARCEL_VOLUME_COMMON_CODE_TYPE = "PARCEL_VOLUME";
        public static final String PARCEL_LENGTH_COMMON_CODE_TYPE = "PARCEL_LENGTH";
        public static final String PARCEL_WIDTH_COMMON_CODE_TYPE = "PARCEL_WIDTH";
        public static final String PARCEL_HEIGHT_COMMON_CODE_TYPE = "PARCEL_HEIGHT";
        public static final String ATTR_CONTAINER_LENGTH = "ContainerLength";
        public static final String ATTR_CONTAINER_WIDTH = "ContainerWidth";
        public static final String ATTR_CONTAINER_HEIGHT = "ContainerHeight";
        public static final String ATTR_CONTAINER_NET_WEIGHT = "ContainerNetWeight";
        public static final String ATTR_EXTN_CONTAINER_VOLUME = "ExtnContainerVolume";
        public static final String ATTR_ACTUAL_WEIGHT = "ActualWeight";
               
        //OMS - 2502 END
		
		//SFS - MiniSoft Changes -- Start
        public static final String ELE_NODE_TFR_SCHEDULE = "NodeTransferSchedule";
        public static final String ATTR_TO_NODE = "ToNode";
        public static final String API_GET_NODE_TFR_SCHEDULE_LIST="getNodeTransferScheduleList";
        public static final String ATTR_FROM_NODE= "FromNode";
        //SFS - MiniSoft Changes -- End
		
		//OMS-2510: Start
    	public static final String VSI_VADC="9004";
    	public static final String VSI_AZDC="9005";
    	//OMS-2510: End
		
		//OMS - SFS Inventory Ownership Changes START
       
        public static final String ATTR_SHIPMENT_CONTAINER_KEY = "ShipmentContainerKey";
        public static final String API_GET_SHIPMENT_CONTAINER_DETAILS = "getShipmentContainerDetails";
        public static final String TEMPLATE_GET_SHIPMENT_CONTAINER_DETAILS = "global/template/api/VSIGetShipmentContainerDetails.xml";
        public static final String ELE_INVENTORY_SUPPLY = "InventorySupply";
        public static final String ATTR_SUPPLY_TYPE = "SupplyType";
        public static final String ONHAND_SUPPLY_TYPE = "ONHAND";
        public static final String VSI_INV = "VSIINV";
        public static final String ADP_INV = "ADPINV";
        public static final String MCL_INV = "MCLINV";
        public static final String DTC_INV = "DTCINV";
        public static final String ATTR_FROM_ORG_CODE = "FromOrganizationCode";
        public static final String ATTR_TO_ORG_CODE = "ToOrganizationCode";
        public static final String API_TRANSFER_INV_OWNERSHIP = "transferInventoryOwnership";
       
        //OMS -  SFS Inventory Ownership Changes END
		//OMS-2719: Start
    	public static final String  SCAC_FEDEX="FEDEX";
    	public static final String TRACKING_COMMON_CODE_FEDEX="FEDEX";
    	//OMS-2719: End
		
		//OMS - 2745 START
    	
    	public static final String TEMPLATE_GET_SHIPMENT_LIST = "global/template/api/getShipmentListTemplate.xml";
    	public static final String ATTR_PICKED_QTY = "PickedQty";
    	
    	//OMS - 2745 END
}