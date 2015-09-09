SET JAVA_HOME=C:\Program Files\java\jdk1.8.0_45
SET ANT_HOME=C:\apache-ant-1.9.2
SET PATH=%JAVA_HOME%\bin;%ANT_HOME%\bin;%PATH%
SET ANT_OPTS=-Xms1024m  -Xmx1024m
ant -f build-demo.xml