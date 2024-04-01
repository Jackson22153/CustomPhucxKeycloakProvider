package com.phucx.passwordHashing;

import org.keycloak.Config.Scope;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.credential.hash.PasswordHashProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BcryptPasswordHashingProviderFactory implements PasswordHashProviderFactory{
    private Logger log = LoggerFactory.getLogger(BcryptPasswordHashingProviderFactory.class);
    public static final String ID = "bcrypt";
    public static final int DEFAULT_ITERATIONS = 10;
    @Override
    public void close() {
        log.info("PasswordHashing is closed");
    }

    @Override
    public PasswordHashProvider create(KeycloakSession kSession) {
        log.info("PasswordHashing is created");
        return new BcryptPasswordHashingProvider(ID, DEFAULT_ITERATIONS);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public void init(Scope scope) {
       
    }

    @Override
    public void postInit(KeycloakSessionFactory kSessionFactory) {

    }
    
}
