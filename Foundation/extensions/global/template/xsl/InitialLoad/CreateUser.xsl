<!--
<User Activateflag="Y" OrganizationKey="Matrix-B" Localecode="en_US_EST" Loginid="TEST" Password="test"  Username="TEST" DataSecurityGroupId="test" Localecode="en_US_EST" Theme="sapphire" MenuId="DEFAULT_MENU">
	<QueueSubscriptionList>
		<QueueSubscription QueueKey="Matrix-B_Q1"/>
	</QueueSubscriptionList>
	<UserGroupLists>
		<UserGroupList IsPrimary="Y" UsergroupId="STORE-CSR"/>
	</UserGroupLists>
</User>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:func="com.yantra.ysc.business.masterupload.YSCMasterUploadUtils" exclude-result-prefixes="func">
	
	<xsl:output method="xml" version="1.0" indent="yes" encoding="UTF-8" omit-xml-declaration="yes" />
	
	<xsl:variable name="lower">abcdefghijklmnopqrstuvwxyz</xsl:variable> 
	<xsl:variable name="upper">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable> 
	<xsl:variable name="businessCustTypeString"><xsl:text>business</xsl:text></xsl:variable>
	<xsl:variable name="consumerCustTypeString"><xsl:text>consumer</xsl:text></xsl:variable>
	<xsl:variable name="locale"><xsl:text>en_US_EST</xsl:text></xsl:variable>
	<xsl:variable name="status"><xsl:text>10</xsl:text></xsl:variable>
	
	<xsl:template match="/">
		<xsl:apply-templates select="UserList"/>
	</xsl:template>
	
	<xsl:template match="UserList">
		<User>	
			<xsl:apply-templates select="User"/>
		</User>
	</xsl:template>
	
	<xsl:template match="User">	
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
		<QueueSubscriptionList>
			<QueueSubscription>
				<xsl:attribute name="QueueKey">
					<xsl:value-of select="@QueueKey"/>
				</xsl:attribute>
			</QueueSubscription>
		</QueueSubscriptionList>
		<UserGroupLists>
			<UserGroupList>
				<xsl:attribute name="UsergroupId">
					<xsl:value-of select="@UsergroupId"/>
				</xsl:attribute>
			</UserGroupList>
		</UserGroupLists>
	</xsl:template>
	
	
</xsl:stylesheet>