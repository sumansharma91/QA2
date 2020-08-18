<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:oms="http://www.example.org/OMS_PRICING/">
   <soapenv:Header/>
   <soapenv:Body>
    <pricingRequest>
	<Order>
		<xsl:variable name="sourcingClass" select="/Order/@SourcingClassification"/>		
      <xsl:attribute name="OrderDate"><xsl:value-of select="/Order/@OrderDate"/></xsl:attribute>
<PersonInfoShipTo>
			<xsl:attribute name="AddressLine1">
				<xsl:value-of select="Order/PersonInfoShipTo/@AddressLine1"/>
			</xsl:attribute>
			
			<xsl:attribute name="City">
				<xsl:value-of select="Order/PersonInfoShipTo/@City"/>
			</xsl:attribute>
			
			<xsl:attribute name="State">
				<xsl:value-of select="Order/PersonInfoShipTo/@State"/>
			</xsl:attribute>
			
			<xsl:attribute name="Country">
				<xsl:value-of select="Order/PersonInfoShipTo/@Country"/>
			</xsl:attribute>
			
			<xsl:attribute name="ZipCode">
				<xsl:value-of select="Order/PersonInfoShipTo/@ZipCode"/>
			</xsl:attribute>
</PersonInfoShipTo>

<Extn>

			<xsl:attribute name="ExtnPricingDate">
				<xsl:value-of select="Order/Extn/@ExtnPricingDate"/>
			</xsl:attribute>
			
</Extn>
<OrderLines>
			<xsl:for-each select="Order/OrderLines/OrderLine">
			<OrderLine>
					<xsl:attribute name="DeliveryMethod">
						<xsl:value-of select="@DeliveryMethod"/>
					</xsl:attribute>
					<xsl:attribute name="CarrierServiceCode">
					<xsl:choose>
					<xsl:when test="$sourcingClass='INTERNATIONAL'">				
						<xsl:value-of select="$sourcingClass"/>					
					</xsl:when>
					<xsl:otherwise>					
						<xsl:value-of select="@CarrierServiceCode"/>
					</xsl:otherwise>
					</xsl:choose>
					</xsl:attribute>
					
					<xsl:attribute name="OrderedQty">
						<xsl:value-of select="@OrderedQty"/>
					</xsl:attribute>
					
					<xsl:attribute name="OrderLineKey">
						<xsl:value-of select="@OrderLineKey"/>
					</xsl:attribute>
					
					<xsl:attribute name="ShipNode">
						<xsl:value-of select="@ShipNode"/>
					</xsl:attribute>
				<Item>
				
					<xsl:attribute name="ItemID">
						<xsl:value-of select="Item/@ItemID"/>
					</xsl:attribute>
					
				</Item>
				<LinePriceInfo>
				
					<xsl:attribute name="UnitPrice">
						<xsl:value-of select="LinePriceInfo/@UnitPrice"/>
					</xsl:attribute>
					
				</LinePriceInfo>
				</OrderLine>
			</xsl:for-each>
 </OrderLines>
 <Promotions>
	 <xsl:for-each select="Order/Promotions/Promotion">
	 <Promotion>
					<xsl:attribute name="PromotionId">
						<xsl:value-of select="@PromotionId"/>
					</xsl:attribute>
			<Extn>
					<xsl:attribute name="ExtnCouponID">
						<xsl:value-of select="Extn/@ExtnCouponID"/>
					</xsl:attribute>
			</Extn>			
	 </Promotion>
	 </xsl:for-each>
 </Promotions>
 </Order>
 </pricingRequest> 
 </soapenv:Body>
</soapenv:Envelope>
</xsl:template>
</xsl:stylesheet>