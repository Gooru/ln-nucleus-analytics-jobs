%~d0
cd %~dp0
java -Xms256M -Xmx1024M -cp .;../lib/routines.jar;../lib/log4j-1.2.16.jar;../lib/dom4j-1.6.1.jar;../lib/apache-httpcomponents-httpcore.jar;../lib/talendcsv.jar;../lib/json-1.5-20090211.jar;../lib/jakarta-oro-2.0.8.jar;../lib/talend_file_enhanced_20070724.jar;../lib/filecopy.jar;../lib/apache-httpcomponents-httpclient.jar;../lib/httpclient-4.0.2.jar;postevents_0_1.jar; learning.postevents_0_1.PostEvents --context=Default %* 