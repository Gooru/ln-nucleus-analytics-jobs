#!/bin/sh
cd `dirname $0`
ROOT_PATH=`pwd`
java -Xms256M -Xmx1024M -cp .:$ROOT_PATH:$ROOT_PATH/../lib/routines.jar:$ROOT_PATH/../lib/log4j-1.2.16.jar:$ROOT_PATH/../lib/dom4j-1.6.1.jar:$ROOT_PATH/../lib/apache-httpcomponents-httpcore.jar:$ROOT_PATH/../lib/talendcsv.jar:$ROOT_PATH/../lib/json-1.5-20090211.jar:$ROOT_PATH/../lib/jakarta-oro-2.0.8.jar:$ROOT_PATH/../lib/talend_file_enhanced_20070724.jar:$ROOT_PATH/../lib/filecopy.jar:$ROOT_PATH/../lib/apache-httpcomponents-httpclient.jar:$ROOT_PATH/../lib/httpclient-4.0.2.jar:$ROOT_PATH/postevents_0_1.jar: learning.postevents_0_1.PostEvents --context=Default "$@" 