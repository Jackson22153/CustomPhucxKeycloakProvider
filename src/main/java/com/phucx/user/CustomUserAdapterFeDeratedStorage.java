package com.phucx.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import org.keycloak.common.util.MultivaluedHashMap;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.adapter.AbstractUserAdapterFederatedStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phucx.DbUtil;
import com.phucx.model.User;
import com.phucx.role.CustomRoleAdapter;

public class CustomUserAdapterFeDeratedStorage extends AbstractUserAdapterFederatedStorage{
    private final String USEREX_ID_ATTRIBUTE = "userexID";
    private final String ENABLED_ATTRIBUTE="enabled";
    private final String EMAIL_VERIFIED_ATTRIBUTE="emailVerified";
    private final String EMAIL_VERIFIED_NAME="EMAIL_VERIFIED";

    private Logger logger = LoggerFactory.getLogger(CustomUserAdapterFeDeratedStorage.class);
    private User user;

    public CustomUserAdapterFeDeratedStorage(KeycloakSession session, RealmModel realm,
            ComponentModel storageProviderModel, User user) {
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

    public String getEnabled(){
        return String.valueOf(this.user.getEnabled());
    }

    public void setEnabled(String enabled){
        Boolean enableValue = Boolean.valueOf(enabled);
        this.user.setEnabled(enableValue);
    }

    public String getEmailVerified(){
        return String.valueOf(this.user.getEmailVerified());
    }

    public void setEmailVerified(String emailVerified){
        Boolean emailVerifiedValue = Boolean.valueOf(emailVerified);
        this.user.setEmailVerified(emailVerifiedValue);
    }


    @Override
    public String getEmail() {
        return this.user.getEmail();
    }

    @Override
    public void setEmail(String email) {
        logger.info("Email: {}", email);
        super.setEmail(email);
        this.user.setEmail(email);
    }

    @Override
    public Map<String, List<String>> getAttributes() {


        // add more custome attribues for a user
        MultivaluedHashMap<String, String> attributes = new MultivaluedHashMap<>();
        attributes.add(UserModel.USERNAME, getUsername());
        attributes.add(UserModel.EMAIL, getEmail());
        attributes.add(UserModel.EMAIL_VERIFIED, getEmailVerified());
        attributes.add(UserModel.ENABLED, getEnabled());
        // attributes.add(UserModel.ENABLED, String.valueOf(true));
        // attributes.add(UserModel.EMAIL_VERIFIED, String.valueOf(true));
        attributes.add(USEREX_ID_ATTRIBUTE, getUserID());

        logger.info("getAttributes: {}",attributes.toString());
        return attributes;
    }

    @Override
    protected Set<RoleModel> getRoleMappingsInternal() {
        return Collections.emptySet();
    }

    @Override
    public Stream<RoleModel> getRoleMappingsStream() {
        return Stream.concat(this.getRoleMappingStream(), this.realm.getDefaultRole().getCompositesStream());
    }

    private Stream<RoleModel> getRoleMappingStream(){
        // logger.info("getUserMapping");
        try (Connection connection = DbUtil.getConnection(storageProviderModel)){
            return this.user.getRoles(connection).stream()
                .map(role -> new CustomRoleAdapter(role, realm, session, storageProviderModel).saveRole());
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        logger.info("setSingleAttribute(name={}, value={})", name, value);
        if(name.equalsIgnoreCase(UserModel.ENABLED)){
            setEnabled(value);
        }else if(name.equalsIgnoreCase(UserModel.EMAIL_VERIFIED)){
            setEmailVerified(value);
        }
        // set enabled and email verified
        this.updateSingleAttribute(name, value);
        // super.setSingleAttribute(name, value);
    }

    // update enable and email verified of User
    private void updateSingleAttribute(String name, String value){
        try (Connection connection = DbUtil.getConnection(storageProviderModel)){
            if(this.ENABLED_ATTRIBUTE.equalsIgnoreCase(name)){
                this.user.updateEnabledAttribute(value, connection);
            }else if(this.EMAIL_VERIFIED_NAME.equalsIgnoreCase(name)){
                this.user.updateEmailVerifiedAttribute(value, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getFirstAttribute(String name) {
        logger.info("getFirstAttribute(name={})", name);
        
        if(name.equalsIgnoreCase(this.ENABLED_ATTRIBUTE)){
            name = this.ENABLED_ATTRIBUTE;           
        }else if(name.equalsIgnoreCase(this.EMAIL_VERIFIED_NAME)){
            name = this.EMAIL_VERIFIED_ATTRIBUTE;
        }
        List<String> list =  getAttributes().getOrDefault(name, Collections.emptyList());
        logger.info("Name: {}, value: {}", name, !list.isEmpty()? list.get(0): null);
        return !list.isEmpty()? list.get(0): null; 
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        // set others user attribute like email,....
        // super.setAttribute(name, values);
        logger.info("setAttribute({}, {})", name, values.get(0));
        try (Connection c = DbUtil.getConnection(storageProviderModel)){
            boolean check = false;

            switch (name) {
                case UserModel.EMAIL:
                    check = this.user.updateEmailAttribute(values.get(0), c);
                    if(check) setEmail(values.get(0));
                    break;
                // other attributes
                default:
                    break;
            }
            if(check) logger.info("update successfully");
            else logger.info("update failed");
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void grantRole(RoleModel role) {
        // super.grantRole(role);
        logger.info("grantRole({})", role.getName());
        try (Connection c = DbUtil.getConnection(this.storageProviderModel)){
            String check =User.assignUserRole(this.user.getUsername(), role.getName(), c);
            if(check!=null){
                logger.info("role: {} has been assigned to {}", role.getName(), this.user.getUsername());
            }else{
                logger.info("role: {} can not be assigned to {}", role.getName(), this.user.getUsername());
            }
        } catch (SQLException e) {
            super.grantRole(role);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void deleteRoleMapping(RoleModel role) {
        logger.info("deleteRoleMapping({})", role.getName());
        try (Connection c = DbUtil.getConnection(this.storageProviderModel)){
            String check =User.deleteUserRole(this.user.getUsername(), role.getName(), c);
            if(check!=null){
                logger.info("role: {} has been unassigned from {}", role.getName(), this.user.getUsername());
            }else{
                logger.info("role: {} can not be unassigned from {}", role.getName(), this.user.getUsername());
            }
        } catch (SQLException e) {
            super.deleteRoleMapping(role);
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    protected boolean appendDefaultRolesToRoleMappings() {
        return super.appendDefaultRolesToRoleMappings();
    }
    
}
