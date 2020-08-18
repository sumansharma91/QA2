<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<!--	<xsl:output omit-xml-declaration="yes" indent="yes" encoding="utf-8"/>-->
	<xsl:template match="/">
		<xsl:variable name="omsrequest">
			<xsl:choose>
				<xsl:when test="contains(//MCLError/OMSRequest, '?>')">
					<xsl:value-of select="substring-after(//MCLError/OMSRequest, '?>')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="//MCLError/OMSRequest"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="string-length($omsrequest) &gt; 0">
				<xsl:element name="OMSRequest">
					<xsl:copy-of select="//MCLError/@*"/>
					<xsl:value-of select="$omsrequest" disable-output-escaping="yes"/>
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="errormessage">
					<xsl:value-of select="//@ErrorMessage"/>
				</xsl:variable>
				<xsl:variable name="messageType">
					<xsl:value-of select="//@MessageType"/>
				</xsl:variable>
				<xsl:variable name="orderNo">
					<xsl:value-of select="//@OrderNo"/>
				</xsl:variable>
				<xsl:variable name="description">
					<xsl:choose>
						<xsl:when test="$messageType='ACK'">
							<xsl:value-of select="concat(concat(concat('The Order Acknowledgement update to MCL for Order No. ', $orderNo), ' failed in IIB. The error message is: '), $errormessage)"/>
						</xsl:when>
						<xsl:when test="$messageType='ADJ'">
							<xsl:value-of select="concat(concat(concat('The Order Adjustment update to MCL for Order No. ', $orderNo), ' failed in IIB. The error message is: '), $errormessage)"/>
						</xsl:when>
						<xsl:when test="$messageType='SHIP'">
							<xsl:value-of select="concat(concat(concat('The Shipment Confirmation update to MCL for Order No. ', $orderNo), ' failed in IIB. The error message is: '), $errormessage)"/>
						</xsl:when>
					</xsl:choose>
				</xsl:variable>
				<xsl:element name="Inbox">
					<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="//@OrderHeaderKey"/></xsl:attribute>
					<xsl:attribute name="OrderNo"><xsl:value-of select="$orderNo"/></xsl:attribute>
					<xsl:attribute name="EnterpriseKey"><xsl:value-of select="//@EnterpriseCode"/></xsl:attribute>
					<xsl:attribute name="ExceptionType"><xsl:value-of select="'VSI_MCL_ERRORS'"/></xsl:attribute>
					<xsl:attribute name="QueueId"><xsl:value-of select="'VSI_MCL_ERRORS'"/></xsl:attribute>
					<xsl:attribute name="ErrorReason"><xsl:value-of select="$errormessage"/></xsl:attribute>
					<xsl:attribute name="Description"><xsl:value-of select="$description"/></xsl:attribute>
					<xsl:attribute name="DetailDescription"><xsl:value-of select="$description"/></xsl:attribute>
					<xsl:element name="Order">
						<xsl:attribute name="DocumentType"><xsl:value-of select="//@DocumentType"/></xsl:attribute>
						<xsl:attribute name="EnterpriseCode"><xsl:value-of select="//@EnterpriseCode"/></xsl:attribute>
						<xsl:attribute name="OrderHeaderKey"><xsl:value-of select="//@OrderHeaderKey"/></xsl:attribute>
						<xsl:attribute name="OrderNo"><xsl:value-of select="$orderNo"/></xsl:attribute>
					</xsl:element>
					<xsl:element name="OMSRequest">
						<xsl:value-of select="substring-after(//MCLError/OMSRequest, '?>')" disable-output-escaping="yes"/>
					</xsl:element>
				</xsl:element>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template match="@* | node()">
		<xsl:copy>
			<xsl:apply-templates select="@* | node()"/>
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>
