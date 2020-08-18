<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:output version="1.0" indent="yes" encoding="UTF-8" method="xml" />
   <xsl:template match="/">
     <Order>
	 <xsl:attribute name="Currency">USD</xsl:attribute>
	 <xsl:attribute name="EnterpriseCode">VSI-Cat</xsl:attribute>
   	 <xsl:attribute name="IgnoreOrdering">Y</xsl:attribute>
   	 <xsl:attribute  name = "OrganizationCode" >VSI-Cat</xsl:attribute>
   	 <xsl:attribute name="SuppressRuleExecution">N</xsl:attribute>
   	 <xsl:attribute name="SuppressShipping">N</xsl:attribute>
   	 
   	 <Coupons>
   	 <xsl:for-each select = "/Order/Promotions/Promotion">
   	 <Coupon>
   	 
   	 <xsl:attribute  name = "CouponID" >
   		<xsl:value-of select="@PromotionId" />
   	 </xsl:attribute>
   	 
   	 </Coupon>
   	 </xsl:for-each>
   	 </Coupons>
   	 
   	 <OrderLines>
   	 
   	  <xsl:for-each select = "/Order/OrderLines/OrderLine">
   	 <OrderLine>
   	 <xsl:attribute name="IsPriceLocked">N</xsl:attribute>
   	 <xsl:attribute  name = "ItemID" >
   		<xsl:value-of select="Item/@ItemID" />
   	 </xsl:attribute>
   	 <xsl:attribute  name = "Quantity" >
   		<xsl:value-of select="@OrderedQty" />
   	 </xsl:attribute>
   	 <xsl:attribute  name = "UnitOfMeasure" >
   		<xsl:value-of select="Item/@UnitOfMeasure" />
   	 </xsl:attribute>
	 <xsl:attribute  name = "LineID" >
   		<xsl:value-of select="@OrderLineKey" />
   	 </xsl:attribute>
   	 
   	 </OrderLine>
   	 </xsl:for-each>
   	 
   	 </OrderLines>
	 </Order>
   </xsl:template>
</xsl:stylesheet>
