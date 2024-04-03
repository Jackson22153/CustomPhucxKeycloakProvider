package com.phucx.event;

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomEventListenerProvider implements EventListenerProvider{
    private Logger logger = LoggerFactory.getLogger(CustomEventListenerProvider.class);

    private KeycloakSession kSession;
    private final static String ROLE_CUSTOMER = "CUSTOMER";

	public CustomEventListenerProvider(KeycloakSession kSession) {
		this.kSession = kSession;
	}

	@Override
	public void close() {
        logger.info("CustomEventListenerProvider is closed");
	}

	@Override
	public void onEvent(Event event) {
		logger.info("onEvent({})", event.getType().name());
        if(event.getType().equals(EventType.REGISTER)){
            RealmModel realm = this.kSession.realms().getRealm(event.getRealmId());
            UserModel user = this.kSession.users().getUserById(realm, event.getUserId());
            logger.info("userId: {} username: {} is created", user.getId(), user.getUsername());
            RoleModel defaultUserRole = this.getDefaultUserRole(realm, ROLE_CUSTOMER);
            user.grantRole(defaultUserRole);
        }
	}

    private RoleModel getDefaultUserRole(RealmModel realm, String defaultUserRole){
        RoleModel role = KeycloakModelUtils.getRoleFromString(realm, defaultUserRole);
        if(role==null){
            role = realm.addRole(defaultUserRole);
        }
        return role;
    }

	@Override
	public void onEvent(AdminEvent adminEvent, boolean includeRepresentation) {
		logger.info("onEvent({}, {})", adminEvent.getResourceTypeAsString(), includeRepresentation);

	}
    
}
