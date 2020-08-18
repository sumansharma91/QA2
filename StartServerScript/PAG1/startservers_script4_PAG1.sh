#nohup /opt/sterling/build/startIntegrationServer.sh VSIComFullSyncAgent "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSIPriceSync  "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSIManageOrganization  "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSIManageCalendar   "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIOrderPurge  "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIShipmentPurge  "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIInboxPurge  "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIReprocessPurge  "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIAuditPurge "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSITransferOrdPurge "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIPersonInfoPurge "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSIInventoryAuditPurge  "-Xms256m -Xmx512m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/agentserver_mod.sh VSICatalogIndex  "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &

#nohup /opt/sterling/build/startIntegrationServer.sh VSIInitialAdjustInventory  "-Xms512m -Xmx1024m -XX:PermSize=128m -XX:MaxPermSize=256m" > /dev/null 2>&1 &