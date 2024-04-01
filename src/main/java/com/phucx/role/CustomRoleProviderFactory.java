package com.phucx.role;

import java.util.List;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.provider.ProviderConfigProperty;
import org.keycloak.provider.ProviderConfigurationBuilder;
import org.keycloak.storage.role.RoleStorageProviderFactory;
import org.keycloak.storage.role.RoleStorageProviderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phucx.user.CustomUserStorageProviderConstants;

public class CustomRoleProviderFactory implements RoleStorageProviderFactory<CustomRoleProvider>{
    private static final Logger log = LoggerFactory.getLogger(CustomRoleProviderFactory.class);    
    protected final List<ProviderConfigProperty> configMetadata;

    public CustomRoleProviderFactory() {
        log.info("[I24] CustomUserStorageProviderFactory created");
      // Create config metadata
      configMetadata = ProviderConfigurationBuilder.create()
        .property().name(ROLE_NAME)
            .type(ProviderConfigProperty.STRING_TYPE)
            .label("Hardcoded Role Name")
            .helpText("Only this role naem is available for lookup")
            .defaultValue("hardcoded-role")
            .add()
        .property().name(DELAYED_SEARCH)
            .type(ProviderConfigProperty.BOOLEAN_TYPE)
            .label("Delayes provider by 5s.")
            .helpText("If true it delayes search for clients within the provider by 5s.")
            .defaultValue("false")
            .add()
        .property()
          .name(CustomUserStorageProviderConstants.CONFIG_KEY_JDBC_URL)
          .label("JDBC URL")
          .type(ProviderConfigProperty.STRING_TYPE)
          .defaultValue("jdbc:sqlserver://PHUCY\\SQLEXPRESS:1433;databaseName=food;encrypt=true;trustServerCertificate=true;")
          .helpText("JDBC URL used to connect to the user database")
          .add()
        .property()
          .name(CustomUserStorageProviderConstants.CONFIG_KEY_DB_USERNAME)
          .label("Database User")
          .type(ProviderConfigProperty.STRING_TYPE)
          .helpText("Username used to connect to the database")
          .add()
        .property()
          .name(CustomUserStorageProviderConstants.CONFIG_KEY_DB_PASSWORD)
          .label("Database Password")
          .type(ProviderConfigProperty.PASSWORD)
          .helpText("Password used to connect to the database")
          .secret(true)
          .add()
        .property()
          .name(CustomUserStorageProviderConstants.CONFIG_KEY_VALIDATION_QUERY)
          .label("SQL Validation Query")
          .type(ProviderConfigProperty.STRING_TYPE)
          .helpText("SQL query used to validate a connection")
          .defaultValue("select 1")
          .add()
        .build();
    }

    public static final String PROVIDER_ID = "hardcoded-role";
    public static final String ROLE_NAME = "role_name";
    public static final String DELAYED_SEARCH = "delayed_search";

    @Override
    public CustomRoleProvider create(KeycloakSession kSession, ComponentModel componentModel) {
        return new CustomRoleProvider(new RoleStorageProviderModel(componentModel), kSession);
    }

    @Override
    public String getId() {
        return "custom-role-storage";
    }
    
}
