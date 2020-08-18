<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="/">
<Promise>
                                <xsl:for-each select="Promise">
                                                <xsl:copy-of select="(@*)"/>
                                                  <PromiseLines>        
                                                                                            
                                                  <xsl:for-each select="PromiseLines/PromiseLine">
                                                  <PromiseLine>
                                                                <xsl:copy-of select="(@*)"/>
                                                                <Availability>
                                                                <AvailableInventory>
                                                                <xsl:for-each select="Availability/AvailableInventory">
                                                                  <xsl:copy-of select="(@*)"/>
                                                                  <ShipNodeAvailableInventory>
                                                      
                                                      <xsl:for-each select="ShipNodeAvailableInventory/Inventory">
                                                                    <xsl:sort data-type="number" select="@Distance" order="ascending"/>
                                                      <Inventory>
                                                                <xsl:copy-of select="(@*)"/>
                                                                </Inventory>
                                                      </xsl:for-each>
                                                      
                                                      </ShipNodeAvailableInventory>
                                                </xsl:for-each>
                                                                </AvailableInventory>
                                                </Availability>
                                                </PromiseLine>
                                                  </xsl:for-each>
                                      
                                      </PromiseLines>
                </xsl:for-each>
</Promise>
</xsl:template>

</xsl:stylesheet>
