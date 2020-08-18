<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:template match="/">
		<Order>
			<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="/MonitorConsolidation/Order[1]/@OrderHeaderKey" /></xsl:attribute>
			<!--<xsl:attribute name="DocumentType">0001</xsl:attribute>
			<xsl:attribute name="EnterpriseCode">VSI.com</xsl:attribute> -->
			<xsl:attribute name="Override">Y</xsl:attribute>			
			<xsl:attribute name="SelectMethod">WAIT</xsl:attribute>
			<OrderLines>
			
			<xsl:for-each select="/MonitorConsolidation/Order/OrderStatuses/OrderStatus">
										<OrderLine>
											<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
						</xsl:attribute>
											
											<xsl:attribute name="Action">CANCEL</xsl:attribute>
											</OrderLine>
									</xsl:for-each>
			
			
			</OrderLines>
		</Order>
		
	</xsl:template>
</xsl:stylesheet>