<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<DistributionRule>
<xsl:attribute name="ItemGroupCode">PROD</xsl:attribute>
<xsl:attribute name="DistributionRuleId">CUTOVERSTORES</xsl:attribute>
<xsl:attribute name="OwnerKey">VSI</xsl:attribute>
<xsl:attribute name="Purpose">SOURCING</xsl:attribute>
<ItemShipNodes>
<ItemShipNode >
<xsl:attribute name="ItemId">ALL</xsl:attribute>
<xsl:attribute name="ShipnodeKey">
<xsl:value-of select="/Organization/@OrganizationKey"/>
</xsl:attribute>
<xsl:attribute name="ActiveFlag">
<xsl:value-of select="/Organization/Extn/@ExtnBopusEnabled"/>
</xsl:attribute>
</ItemShipNode >
</ItemShipNodes>
</DistributionRule>
</xsl:template>
</xsl:stylesheet>