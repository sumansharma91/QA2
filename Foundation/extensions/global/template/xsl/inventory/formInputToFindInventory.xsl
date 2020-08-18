<?xml version="1.0"?>
<!--
    Created on : Aug 21st, 2014
    Author     : Perficient
    Description: It sets input xml for findInventory API
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<Promise AllocationRuleID="VSI_FIND_I" CheckInventory="Y" DeliveryDateBased="Y" IgnorePromised="Y" IgnoreUnpromised="Y" MaximumRecords="20" OrganizationCode="{/Item/@OrganizationCode}" >						<PromiseLines>
							 <PromiseLine FulfillmentType="VSI_FIND_INVENTORY" ItemID="{/Item/@ItemID}" RequiredQty="{/Item/@RequiredQty}" UnitOfMeasure="{/Item/@UnitOfMeasure}" ProductClass="GOOD" />
							</PromiseLines>
			
			
			
			
							
		</Promise>
	</xsl:template>
</xsl:stylesheet>