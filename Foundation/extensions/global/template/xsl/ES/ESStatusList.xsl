<?xml version='1.0'?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
	<ESStatusList>
		<Status>
	<xsl:attribute name="Description">Created</xsl:attribute>
	<xsl:attribute name="Status">1100</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">Store Acknowledged</xsl:attribute>
	<xsl:attribute name="Status">1100.200</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">Awaiting Payment Settlement</xsl:attribute>
	<xsl:attribute name="Status">1100.400</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">Awaiting Customer Pickup</xsl:attribute>
	<xsl:attribute name="Status">1100.500</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">Shipped</xsl:attribute>
	<xsl:attribute name="Status">3700</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">Cancelled</xsl:attribute>
	<xsl:attribute name="Status">9000</xsl:attribute>
     </Status>
	 <Status>
	<xsl:attribute name="Description">JDA Acknowledged</xsl:attribute>
	<xsl:attribute name="Status">1100.100</xsl:attribute>
     </Status>
	 
	  <Status>
	<xsl:attribute name="Description">Store Received</xsl:attribute>
	<xsl:attribute name="Status">2160.400</xsl:attribute>
     </Status>
	 
	  <Status>
	<xsl:attribute name="Description">STS Complete</xsl:attribute>
	<xsl:attribute name="Status">2160.200</xsl:attribute>
     </Status>
	 
	 
	  <Status>
	<xsl:attribute name="Description">JDA STS Acknowledged</xsl:attribute>
	<xsl:attribute name="Status">2160.100</xsl:attribute>
     </Status>
	 
	  <Status>
	<xsl:attribute name="Description">Procurement Transfer Order Created</xsl:attribute>
	<xsl:attribute name="Status">2160</xsl:attribute>
     </Status>
	 
	 <Status>
	<xsl:attribute name="Description">Awaiting Procurement Transfer Order Creation</xsl:attribute>
	<xsl:attribute name="Status">2060</xsl:attribute>
     </Status>
	 
	 
	 
	</ESStatusList>
</xsl:template>

</xsl:stylesheet>

