<?xml version="1.0"?>
<!--
    Created on : Aug 21st, 2014
    Author     : Perficient
    Description: It sets input xml for changeOrderStatus API to implement 'Customer Keep Item' functionality.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:template match="/">
		<OrderStatusChange>
		<xsl:variable name="NoOfOrderLinesWithLineTypeCredit">
				<xsl:value-of select="count(Order/OrderLines/OrderLine[@LineType='Credit'])">
				</xsl:value-of>
			</xsl:variable>
			
			<xsl:attribute name="TransactionId">
				<xsl:text>ChangeCCKOrder.0003.ex</xsl:text>
			</xsl:attribute>
			<xsl:attribute name="OrderHeaderKey">
				<xsl:value-of select="Order/@OrderHeaderKey"></xsl:value-of>
			</xsl:attribute>
<xsl:attribute name="OrderHasCCKLines">N</xsl:attribute>
			<xsl:choose>
			<xsl:when test="number($NoOfOrderLinesWithLineTypeCredit) > 0">
			<xsl:attribute name="OrderHasCCKLines">Y</xsl:attribute>
			</xsl:when>
			</xsl:choose>
			<OrderLines>
				<xsl:for-each select="Order/OrderLines/OrderLine[@LineType='Credit']">
					<OrderLine>
						<xsl:attribute name="OrderLineKey">
							<xsl:value-of select="@OrderLineKey"></xsl:value-of>
						</xsl:attribute>
						<xsl:attribute name="ChangeForAllAvailableQty">
							<xsl:text>Y</xsl:text>
						</xsl:attribute>
						<xsl:attribute name="BaseDropStatus">3900.10.ex</xsl:attribute>
					</OrderLine>
				</xsl:for-each>
			</OrderLines>
			
			
			
							
		</OrderStatusChange>
	</xsl:template>
</xsl:stylesheet>