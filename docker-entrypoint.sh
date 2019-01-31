#!/bin/sh -xe

KEYSTORE_MOUNT=$JETTY_HOME/etc/keystore_mount
UTIL_JAR=$JETTY_HOME/lib/jetty-util-*.jar
PASSWORD_CLASS=org.eclipse.jetty.util.security.Password
START_INI="$JETTY_HOME/start.ini"
DEFAULT_KEYSTORE_PATH=etc/keystore
NEW_KEYSTORE_PATH=$KEYSTORE_MOUNT/$JETTY_KEYSTORE_RELATIVE_PATH
#KEYSTORE_PATH_REPLACER='s|$DEFAULT_KEYSTORE_PATH|$NEW_KEYSTORE_PATH|g'
OBF_PASSWORD=$(java -cp $UTIL_JAR $PASSWORD_CLASS $JETTY_KEYSTORE_PASSWORD 2>&1 | grep OBF)

echo '--module=https' >> "$JETTY_HOME/start.ini"
echo '--module=ssl' >> "$JETTY_HOME/start.ini"
echo '--module=resources' >> "$JETTY_HOME/start.ini"

([ "$OBF_PASSWORD" ] || (echo "No password provided" && exit -1))
echo jetty.truststore.password=$OBF_PASSWORD >> $START_INI
echo jetty.keystore.password=$OBF_PASSWORD >> $START_INI
echo jetty.keymanager.password=$OBF_PASSWORD >> $START_INI
ln -s $NEW_KEYSTORE_PATH $DEFAULT_KEYSTORE_PATH

/jetty-docker-entrypoint.sh $@