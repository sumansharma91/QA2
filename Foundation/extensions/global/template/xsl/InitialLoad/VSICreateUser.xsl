<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
<xsl:template match="/">
<xsl:for-each select="UserList/User">
<User>
<xsl:attribute name="Activateflag">Y</xsl:attribute>
<xsl:attribute name="Usertype">INTERNAL</xsl:attribute>
<xsl:attribute name="Password">password</xsl:attribute>
<xsl:attribute name="MenuId">
<xsl:value-of select="@MenuId"/>
</xsl:attribute>
<xsl:attribute name="OrganizationKey">
<xsl:value-of select="@OrganizationKey"/>
</xsl:attribute>
<xsl:attribute name="Loginid">
<xsl:value-of select="@Loginid"/>
</xsl:attribute>
<xsl:attribute name="Username">
<xsl:value-of select="@Username"/>
</xsl:attribute>
<xsl:attribute name="DataSecurityGroupId">
<xsl:value-of select="@DataSecurityGroupId"/>
</xsl:attribute>
<xsl:attribute name="Theme">
<xsl:value-of select="@Theme"/>
</xsl:attribute>
<xsl:attribute name="Localecode">
<xsl:value-of select="@Localecode"/>
</xsl:attribute>
<xsl:if test="@Action='Delete'">
<xsl:attribute name="Action">
<xsl:value-of select="@Action"/>
</xsl:attribute>
</xsl:if>
<UserGroupLists>
<xsl:attribute name="Reset">Y</xsl:attribute>
<UserGroupList>
<xsl:attribute name="UsergroupId">
<xsl:value-of select="@UsergroupId"/>
</xsl:attribute>
</UserGroupList>
</UserGroupLists>

</User>
</xsl:for-each>
</xsl:template>
</xsl:stylesheet>