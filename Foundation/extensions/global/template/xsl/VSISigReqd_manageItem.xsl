<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
   <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes" />

   <xsl:template match="/">
        <ItemList>

			<xsl:for-each select="VSIItemListHierarchy/ItemList">
               
				<Item>
			 
					<xsl:attribute name="Action">Manage</xsl:attribute>
					<xsl:attribute name="OrganizationCode">VSI-Cat</xsl:attribute>
					<xsl:attribute name="UnitOfMeasure">EACH</xsl:attribute>
					<xsl:attribute name="DefaultProductClass">GOOD</xsl:attribute>
					<xsl:attribute name="ItemID"><xsl:value-of select="@ItemID" /></xsl:attribute>
									
					<Extn>				
						<xsl:attribute name="ExtnIsSignReqdItem">Y</xsl:attribute>
						
						<VSISignatureRequiredItemList>
						<VSISignatureRequiredItem>
							<xsl:attribute name="OrganizationCode">VSI-Cat</xsl:attribute>
							<xsl:attribute name="UnitOfMeasure">EACH</xsl:attribute>
							<xsl:attribute name="ItemID"><xsl:value-of select="@ItemID" /></xsl:attribute>
							<xsl:attribute name="State"><xsl:value-of select="@State" /></xsl:attribute>
							<xsl:if test="(@Age='18')">
								<xsl:attribute name="Extn18SignReqd">Y</xsl:attribute>
							</xsl:if>
							<xsl:if test="(@Age='21')">
								<xsl:attribute name="Extn21SignReqd">Y</xsl:attribute>
							</xsl:if>
						</VSISignatureRequiredItem>
						</VSISignatureRequiredItemList>
				
					</Extn>
					
				</Item>
			</xsl:for-each><!-- End of for loop -->
      </ItemList>
   </xsl:template>
</xsl:stylesheet>                       