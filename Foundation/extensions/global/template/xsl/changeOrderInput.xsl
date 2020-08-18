<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />
	<xsl:template match="/">
		<Order ModificationReasonCode="LOSTINTRANSIT" ModificationReasonText="Cancel">
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Shipments/Shipment/ShipmentLines/ShipmentLine/OrderLine/@ChainedFromOrderHeaderKey"/>
			</xsl:attribute>
			<OrderLines>
				<xsl:for-each select="Shipments/Shipment/ShipmentLines/ShipmentLine">	
					<xsl:for-each select="OrderLine">	
						<OrderLine Action="CANCEL">
							<xsl:attribute name="OrderLineKey">
								<xsl:value-of select="@ChainedFromOrderLineKey" />
							</xsl:attribute> 					
							<xsl:attribute name="OrderedQty">
								<xsl:value-of select="../@Quantity" />
							</xsl:attribute>							
					</OrderLine>
					</xsl:for-each>	
				</xsl:for-each>	
			</OrderLines>
		</Order>	
	</xsl:template>	
</xsl:stylesheet>