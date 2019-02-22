FROM jetty:9.3.11-jre8-alpine

# The relative path to the shared object for the codec shared object
ARG CODEC_SO_PATH=target/libper-xer-codec.so

# Password for the certificate keystore
ENV JETTY_KEYSTORE_PASSWORD=CHANGEME

# The relative path from $JETTY_HOME/etc/keystore_mount your keystore is placed in
ENV JETTY_KEYSTORE_RELATIVE_PATH=keystore

# Mount the keystore here
VOLUME $JETTY_HOME/etc/keystore_mount

WORKDIR $JETTY_HOME

# Copy in war file and codec binary
COPY ${CODEC_SO_PATH} /usr/lib/libper-xer-codec.so
COPY target/validator.war $JETTY_HOME/webapps/validator.war

# Add in new entrypoint script
RUN mv /docker-entrypoint.sh /jetty-docker-entrypoint.sh
COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh