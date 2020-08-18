<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
   <xsl:template match="/">
     <MultiApi>
     <xsl:for-each select="Items/Item">
<API Name="adjustInventory">
<Input>     
     <Items>  
     <Item> 
     <xsl:copy-of select = "./@*"/>
        
     </Item>
     </Items>
 </Input>
</API>
</xsl:for-each>
</MultiApi>
     </xsl:template>
</xsl:stylesheet>