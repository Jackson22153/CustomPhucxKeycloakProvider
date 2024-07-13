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
import com.phucx.repository.RoleDAO;
import com.phucx.repository.UserDAO;
import com.phucx.repository.UserRoleDAO;
import com.phucx.repository.imps.RoleDAOImp;
import com.phucx.repository.imps.UserDAOImp;
import com.phucx.repository.imps.UserRoleDAOImp;
import com.phucx.role.CustomRoleAdapter;

public class CustomUserAdapterFeDeratedStorage extends AbstractUserAdapterFederatedStorage{
    public static final String USEREX_ID_ATTRIBUTE = "userexID";
    public static final String ROLE = "role";
    private final String EMAIL_VERIFIED_NAME="EMAIL_VERIFIED";

    private Logger logger = LoggerFactory.getLogger(CustomUserAdapterFeDeratedStorage.class);
    private User user;
    private RoleDAO roleDAO;
    private UserDAO userDAO;
    private UserRoleDAO userRoleDAO;

    public CustomUserAdapterFeDeratedStorage(KeycloakSession session, RealmModel realm,
            ComponentModel storageProviderModel, User user) {
        super(session, realm, storageProviderModel);
        this.user = user;
        this.roleDAO = new RoleDAOImp();
        this.userDAO = new UserDAOImp();
        this.userRoleDAO = new UserRoleDAOImp();
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

    public String getFirstName(){
        return this.user.getFirstName();
    }

    public String getLastName(){
        return this.user.getLastName();
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
        attributes.add(UserModel.FIRST_NAME, getFirstName());
        attributes.add(UserModel.LAST_NAME, getLastName());
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
            return this.roleDAO.getRoles(this.user.getUserID(), connection).stream()
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
            if(UserModel.ENABLED.equalsIgnoreCase(name)){
                this.userDAO.updateEnabled(this.user.getUserID(), value, connection);
            }else if(this.EMAIL_VERIFIED_NAME.equalsIgnoreCase(name)){
                this.userDAO.updateEmailVerified(this.user.getUserID(), value, connection);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getFirstAttribute(String name) {
        logger.info("getFirstAttribute(name={})", name);
        
        if(name.equalsIgnoreCase(UserModel.ENABLED)){
            name = UserModel.ENABLED;           
        }else if(name.equalsIgnoreCase(this.EMAIL_VERIFIED_NAME)){
            name = UserModel.EMAIL_VERIFIED;
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
                    check = this.userDAO.updateEmail(this.user.getUserID(), values.get(0), c);
                    if(check) setEmail(values.get(0));
                    break;
                case UserModel.FIRST_NAME:
                    check = this.userDAO.updateFirstname(this.user.getUserID(), values.get(0), c);
                    if(check) setFirstName(values.get(0));
                    break;
                case UserModel.LAST_NAME:
                    check = this.userDAO.updateLastname(this.user.getUserID(), values.get(0), c);
                    if(check) setLastName(values.get(0));
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
            String check =this.userRoleDAO.assignUserRole(this.user.getUsername(), role.getName(), c);
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
            String check =this.userRoleDAO.deleteUserRole(this.user.getUsername(), role.getName(), c);
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

    @Override
    public void setEmailVerified(boolean verified) {
        super.setEmailVerified(verified);
        this.user.setEmailVerified(verified);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.user.setEnabled(enabled);
    }

    @Override
    public void setFirstName(String firstName) {
        super.setFirstName(firstName);
        this.user.setFirstName(firstName);
    }

    @Override
    public void setLastName(String lastName) {
        super.setLastName(lastName);
        this.user.setLastName(lastName);
    }
}
