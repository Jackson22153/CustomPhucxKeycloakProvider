package com.phucx.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phucx.DbUtil;
import com.phucx.model.Users;
import com.phucx.role.CustomRoleAdapter;

public class CustomUserAdapterFeDeratedStorage extends AbstractUserAdapterFederatedStorage{


    private Logger logger = LoggerFactory.getLogger(CustomUserAdapterFeDeratedStorage.class);
    private Users user;

    public CustomUserAdapterFeDeratedStorage(KeycloakSession session, RealmModel realm,
            ComponentModel storageProviderModel, Users user) {
        super(session, realm, storageProviderModel);
        this.user = user;
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    @Override
    public void setUsername(String username) {
        this.user.setUsername(username);
    }

    public String getUserID(){
        return this.user.getUserID();
    }

    public String getPassword(){
        return this.user.getPassword();
    }

    public void setPassword(String password){
        this.user.setPassword(password);
    }

    @Override
    public String getEmail() {
        return this.user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        super.setEmail(email);
        this.user.setEmail(email);
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        return attributes;
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        return this.getRoleMappingStream().collect(Collectors.toSet());
    }

    @Override
    public Stream<RoleModel> getRoleMappingsStream() {
        // return this.getRoleMappingStream();
        return super.getRoleMappingsStream();
    }

    private Stream<RoleModel> getRoleMappingStream(){
        logger.info("getUserMapping");
        try (Connection connection = DbUtil.getConnection(storageProviderModel)){
            return this.user.getRoles(connection).stream()
                .map(role -> new CustomRoleAdapter(role, realm, session, storageProviderModel).saveRole());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public String getFirstAttribute(String name) {
        List<String> list =  getAttributes().getOrDefault(name, Collections.emptyList());
        return !list.isEmpty()? list.get(0): null; 
    }
    
}
