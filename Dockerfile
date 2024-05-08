FROM quay.io/keycloak/keycloak:23.0.7 as builder
ADD --chown=keycloak:keycloak target/customkeycloakprovider-1.0-SNAPSHOT.jar /opt/keycloak/providers/customkeycloakprovider-1.0-SNAPSHOT.jar
RUN /opt/keycloak/bin/kc.sh build