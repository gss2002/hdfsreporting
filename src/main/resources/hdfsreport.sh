bin=`dirname "$0"`
bin=`cd "$bin"; pwd`
HADOOP_HOME_PATH=/usr/hdp/current/hadoop-client
HADOOP_CONFIG_SCRIPT=$HADOOP_HOME_PATH/libexec/hadoop-config.sh
HADOOP_CLIENT_LIBS=$HADOOP_HOME_PATH/client
if [ -e $HADOOP_CONFIG_SCRIPT ] ; then
        .  $HADOOP_CONFIG_SCRIPT
else
        echo "Hadoop Client not Installed on Node"
        exit 1
fi 
HDFSJAR=`ls -1 $bin/hdfsreporting*.jar`
if [ -e $JAVA_HOME/bin/java ] ; then
        $JAVA_HOME/bin/java -cp $CLASSPATH:$HADOOP_CLIENT_LIBS/*:$HADOOP_CLASSPATH:$HDFSJAR org.apache.hadoop.hdfs.reporting.HdfsReporting "$@"
else
        echo "Java Defined for Hadoop Missing on Node"
        exit 1
fi