package com.phucx.event;

import org.keycloak.Config.Scope;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEventListenerProviderFactory implements EventListenerProviderFactory{
    private Logger logger = LoggerFactory.getLogger(CustomEventListenerProviderFactory.class);
	@Override
	public void close() {
		logger.info("CustomEventListenerProviderFactory is closed");
	}

	@Override
	public EventListenerProvider create(KeycloakSession kSession) {
		logger.info("CustomEventListenerProviderFactory is created");
        return new CustomEventListenerProvider(kSession);
	}

	@Override
	public String getId() {
		return "custom-event-listener";
	}

	@Override
	public void init(Scope scope) {
		
	}

	@Override
	public void postInit(KeycloakSessionFactory kSessionFactory) {
	
	}
    
}
