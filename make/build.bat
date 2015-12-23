SET JAVA_HOME=E:\Java\jdk1.8.0_66
SET ANT_HOME=E:\apache-ant-1.9.6
SET PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
SET ANT_OPTS=-Xms1024m  -Xmx1024m
ant -f build.xml
