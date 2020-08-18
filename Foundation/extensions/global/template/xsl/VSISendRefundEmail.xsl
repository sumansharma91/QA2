<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

 <xsl:output method="xml" encoding="utf-8" indent="no"/>

 <xsl:template match="/">
	<Order>
		<xsl:attribute name="EntryType">
            <xsl:value-of select="OrderInvoice/Order/@EntryType" />
         </xsl:attribute>
		 <xsl:attribute name="OrderNo">
			<xsl:value-of select="OrderInvoice/Order/@OrderNo" />
		</xsl:attribute>
		 <PersonInfoBillTo>
			<xsl:attribute name="FirstName">
				<xsl:value-of select="OrderInvoice/Order/PersonInfoBillTo/@FirstName" />
			</xsl:attribute>
		 </PersonInfoBillTo>
		 <OverallTotals>
			<xsl:attribute name="GrandTotal">
				<xsl:value-of select="OrderInvoice/Order/OverallTotals/@GrandTotal" />
			</xsl:attribute>
		 </OverallTotals>
		<OrderLines>
			<OrderLine>
				<xsl:attribute name="CustomerPONo">
					<xsl:value-of select="OrderInvoice/LineDetails/LineDetail/OrderLine/@CustomerPONo" />
				</xsl:attribute>
			</OrderLine>
		</OrderLines>
	</Order>
 </xsl:template>
 
</xsl:stylesheet>