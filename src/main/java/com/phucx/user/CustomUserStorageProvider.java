/**
 * 
 */
package com.phucx.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.models.credential.PasswordCredentialModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.user.ImportedUserValidation;
import org.keycloak.storage.user.UserLookupProvider;
import org.keycloak.storage.user.UserQueryProvider;
import org.keycloak.storage.user.UserRegistrationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phucx.DbUtil;
import com.phucx.model.User;
import com.phucx.repository.UserDAO;
import com.phucx.repository.imps.UserDAOImp;

public class CustomUserStorageProvider implements 
    UserStorageProvider, 
    UserLookupProvider, 
    CredentialInputValidator,
    CredentialInputUpdater,
    UserRegistrationProvider,
    UserQueryProvider,
    ImportedUserValidation{

    private static final Logger log = LoggerFactory.getLogger(CustomUserStorageProvider.class);
    private KeycloakSession ksession;
    private ComponentModel model;

    private UserDAO userDAO;

    public static final int DEFAULT_ITERATIONS = 10;
    public static final String providerId = "bcrypt";

    private Map<String, CustomUserAdapterFeDeratedStorage> instanceUsers = new HashMap<>();

    public CustomUserStorageProvider(KeycloakSession ksession, ComponentModel model) {
        this.ksession = ksession;
        this.model = model;
        this.userDAO = new UserDAOImp();
    }

    @Override
    public void close() {
        log.info("[I30] close()");
    }

    @Override
    public UserModel getUserById(RealmModel realm, String id) {
        log.info("[I35] getUserById({})",id);
        StorageId sid = new StorageId(id);
        return getUserByUsername(realm, sid.getExternalId());
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        log.info("[I41] getUserByUsername({})",username);
        CustomUserAdapterFeDeratedStorage cachedUser = instanceUsers.get(username);
        if(cachedUser == null){
            try ( Connection c = DbUtil.getConnection(this.model)) {
                User user = userDAO.getUserByUsername(username, c);
                if(user!=null){
                    cachedUser = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    instanceUsers.put(username, cachedUser);
                } 
            }
            catch(SQLException ex) {
                throw new RuntimeException("Database error:" + ex.getMessage(),ex);
            }
        }
        return cachedUser;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        log.info("[I41] getUserByEmail({})",email);

        CustomUserAdapterFeDeratedStorage cachedUser = instanceUsers.get(email);
        if(cachedUser == null){
            try ( Connection c = DbUtil.getConnection(this.model)) {
                User user = userDAO.getUserByEmail(email, c);
                if(user!=null){
                    cachedUser = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    instanceUsers.put(email, cachedUser);
                } 
            }
            catch(SQLException ex) {
                throw new RuntimeException("Database error:" + ex.getMessage(),ex);
            }
        }
        return cachedUser;
    }

    @Override
    // set the support credential type for authenticating
    public boolean supportsCredentialType(String credentialType) {
        log.info("[I57] supportsCredentialType({}) result:{}",credentialType, PasswordCredentialModel.TYPE.endsWith(credentialType));
        return PasswordCredentialModel.TYPE.endsWith(credentialType);
    }

    @Override
    public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
        log.info("[I57] isConfiguredFor(realm={},user={},credentialType={})",realm.getName(), user.getUsername(), credentialType);
        // In our case, password is the only type of credential, so we allways return 'true' if
        // this is the credentialType
        return supportsCredentialType(credentialType);
    }

    @Override
    // validate user's credentials when user logs in
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("[I57] isValid(realm={},user={},credentialInput.type={})",realm.getName(), user.getUsername(), credentialInput.getType());
        if( !this.supportsCredentialType(credentialInput.getType())) {
            log.info("[I157] not passwordtype");
            return false;
        }
        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();
        log.info("username: {}", username);
        try ( Connection c = DbUtil.getConnection(this.model)) {
            User u = userDAO.getUserByUsername(username, c);
            if(u!=null){
                String userInputPassword = credentialInput.getChallengeResponse();
                String pwd = u.getPassword();
                PasswordHashProvider passwordHashProvider =ksession.getProvider(PasswordHashProvider.class, providerId);
            
                PasswordCredentialModel hashedPassword = PasswordCredentialModel
                    .createFromValues(providerId, new byte[0], DEFAULT_ITERATIONS, pwd);
                return passwordHashProvider.verify(userInputPassword, hashedPassword);
            }
            return false;
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }
    // UserQueryProvider implementation
    
    @Override
    public int getUsersCount(RealmModel realm) {
        log.info("[I93] getUsersCount: realm={}", realm.getName() );
        try ( Connection c = DbUtil.getConnection(this.model)) {
            return userDAO.getCountUsers(c);
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        log.info("[I113] getGroupMembersStream: realm={}, group={}, firstResult={}, maxResult={}", 
            realm.getName(), group.getName(), firstResult, maxResults);
        
        try ( Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsers(maxResults, firstResult, c);
            List<UserModel> obUser = users.stream()
                .map(user -> new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());
            return obUser.stream();
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        log.info("[I139] searchForUser: realm={}, search={}, firstResul={}, maxResult={}", 
            realm.getName(), search, firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsersLike(search, firstResult, maxResults, c);
            List<UserModel> obUser = users.stream()
                .map(user -> new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());
            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    private Stream<UserModel> searchForUserByUsernameAndRoleStream(RealmModel realm, String username, String role, Integer firstResult, Integer maxResults) {
        log.info("searchForUserByUsernameAndRoleStream: realm={}, username={}, role={}, firstResul={}, maxResult={}", 
            realm.getName(), username, role, firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsersByRoleAndUsernameLike(role, username, firstResult, maxResults, c);
            List<UserModel> obUser = users.stream()
                .map(user -> {
                    UserModel usermodel = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    log.info("attributes: {}", usermodel.getAttributes());
                    return usermodel;
                })
                .collect(Collectors.toList());
            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    private Stream<UserModel> searchForUserByEmailAndRoleStream(RealmModel realm, String email, String role, Integer firstResult, Integer maxResults) {
        log.info("searchForUserByEmailAndRoleStream: realm={}, email={}, role={}, firstResul={}, maxResult={}", 
            realm.getName(), email, role, firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsersByRoleAndEmailLike(role, email, firstResult, maxResults, c);
            List<UserModel> obUser = users.stream()
                .map(user -> {
                    UserModel userModel = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    // userModel.getAttributes();
                    return userModel;
                })
                .collect(Collectors.toList());

            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    private Stream<UserModel> searchForUserByFirstNameAndRoleStream(RealmModel realm, String firstName, String role, Integer firstResult, Integer maxResults) {
        log.info("searchForUserByFirstNameAndRoleStream: realm={}, firstName={}, role={}, firstResul={}, maxResult={}", 
            realm.getName(), firstName, role, firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsersByRoleAndFirstNameLike(role, firstName, firstResult, maxResults, c);
            List<UserModel> obUser = users.stream()
                .map(user -> {
                    UserModel userModel = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    // userModel.getAttributes();
                    return userModel;
                })
                .collect(Collectors.toList());

            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    private Stream<UserModel> searchForUserByLastNameAndRoleStream(RealmModel realm, String lastName, String role, Integer firstResult, Integer maxResults) {
        log.info("searchForUserByLastNameAndRoleStream: realm={}, lastName={}, role={}, firstResul={}, maxResult={}", 
            realm.getName(), lastName, role, firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<User> users = userDAO.getUsersByRoleAndLastNameLike(role, lastName, firstResult, maxResults, c);
            List<UserModel> obUser = users.stream()
                .map(user -> {
                    UserModel userModel = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    // userModel.getAttributes();
                    return userModel;
                })
                .collect(Collectors.toList());

            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        log.info("[I140] searchForUser: realm={}, param={}, firstResult={}, maxResult={}", 
            realm.getName(), params, firstResult, maxResults);
        String search =  getSearchParameter(params);
        Boolean exact = getExactParameter(params);
        String userExternalID = getUserExIdParameter(params);
        String email = getEmailParameter(params);
        String firstName = getFirstNameParameter(params);
        String lastName = getLastNameParameter(params);
        String username = getUsernameParameter(params);
        String role = getRoleParameter(params);
        
        if(role!=null){
            if(username!=null){
                return this.searchForUserByUsernameAndRoleStream(realm, username, role, firstResult, maxResults);
            }else if(firstName!=null){
                return this.searchForUserByFirstNameAndRoleStream(realm, firstName, role, firstResult, maxResults);
            }else if(lastName!=null){
                return this.searchForUserByLastNameAndRoleStream(realm, lastName, role, firstResult, maxResults);
            }else if(email!=null){
                return this.searchForUserByEmailAndRoleStream(realm, email, role, firstResult, maxResults);
            }else {
                return this.searchForUserByUsernameAndRoleStream(realm, search, role, firstResult, maxResults);
            }
        }else if(userExternalID!=null){
            return this.getUserByUserExIdStream(realm, userExternalID);
        }else if(exact){  
            UserModel user  = getUserByUsername(realm, search);
            return Stream.of(user);
        }else{
            return searchForUserStream(realm, search, firstResult, maxResults);
        }
    }
    // get parameters
    private Boolean getExactParameter(Map<String, String> params){
        return params.get(UserModel.EXACT)==null?
            false:Boolean.valueOf(params.get(UserModel.EXACT));
    }
    private String getUsernameParameter(Map<String, String> params){
        return params.get(UserModel.USERNAME);
    }
    private String getSearchParameter(Map<String, String> params){
        return params.getOrDefault(UserModel.USERNAME, params.get(UserModel.SEARCH));
    }
    private String getUserExIdParameter(Map<String, String> params){
        return params.get(CustomUserAdapterFeDeratedStorage.USEREX_ID_ATTRIBUTE);
    }
    private String getRoleParameter(Map<String, String> params){
        return params.get("role");
    }
    private String getFirstNameParameter(Map<String, String> params){
        return params.get(UserModel.FIRST_NAME);
    }
    private String getLastNameParameter(Map<String, String> params){
        return params.get(UserModel.LAST_NAME);
    }
    private String getEmailParameter(Map<String, String> params){
        return params.get(UserModel.EMAIL);
    }

    

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        log.info("[I141] searchForUserByUserAttributeStream: realm={}, attribute={}, attributeValue={}", realm.getName(), attrName, attrValue);
        try (Connection c = DbUtil.getConnection(this.model)){
            List<User> users = userDAO.getUsersByAttributeLike(attrName, attrValue, c);
            List<UserModel> obUser = users.stream()
                .map(user ->  new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());
            return obUser.stream();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Stream<UserModel> getUserByUserExIdStream(RealmModel realm, String userId) {
        log.info("getUserByUserExIdStream: realm={}, userId={}", realm.getName(), userId);
        try (Connection c = DbUtil.getConnection(this.model)){
            User user = userDAO.getUserByID(userId, c);
            UserModel obUser = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
            return Stream.of(obUser);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    // save user's information to database
    public UserModel addUser(RealmModel realm, String username) {
        log.info("[I142] addUser: realm={}, username={}", realm.getName(), username);
        try (Connection connection = DbUtil.getConnection(this.model)){
            PasswordHashProvider passwordHashProvider = ksession.getProvider(PasswordHashProvider.class, providerId);
            String hashedPassword = passwordHashProvider.encode(
                CustomUserStorageProviderConstants.UNSET_PASSWORD, DEFAULT_ITERATIONS);
            User user = new User();
            user.setUserID(KeycloakModelUtils.generateId());
            user.setUsername(username);
            user.setPassword(hashedPassword);
            synchronized(user){
                user = userDAO.saveUser(user, connection);
            }
            if(user!=null){
                return new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        log.info("[I143] removeUser({}, {})", realm.getName(), user.getUsername());
        try (Connection connection = DbUtil.getConnection(this.model)){
            User removeUser = userDAO.getUserByUsername(user.getUsername(), connection);
            if(removeUser!=null){
                boolean check = false;
                synchronized(removeUser){
                    check = userDAO.deleteUser(removeUser.getUserID(), connection);
                }
                return check;
            }else return false;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserModel validate(RealmModel realm, UserModel user) {
        log.info("[I144] validate: realm={}, username={}", realm.getName(), user.getUsername());
        try ( Connection c = DbUtil.getConnection(this.model)) {
            User fetcheduser = userDAO.getUserByUsername(user.getUsername(), c);
            if(fetcheduser!=null){
                return new CustomUserAdapterFeDeratedStorage(ksession, realm, model, fetcheduser);
            }
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
        return null;
    }

    @Override
    public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

    }

    @Override
    public Stream<String> getDisableableCredentialTypesStream(RealmModel realm, UserModel user) {
        return Stream.empty();
    }

    @Override
    // update user's credentials and also save the registered user's input credentials for users
    public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput credential) {
        log.info("updateCredential({}, {}, {})", realm.getName(), user.getUsername(), credential.getChallengeResponse());
        if (!supportsCredentialType(credential.getType()) || !(credential instanceof UserCredentialModel)) 
            return false;
        try (Connection c = DbUtil.getConnection(this.model)){
            User fetchedUser = userDAO.getUserByUsername(user.getUsername(), c);
            PasswordHashProvider passwordHashProvider = this.ksession.getProvider(PasswordHashProvider.class, providerId);
            String hashedPassword = passwordHashProvider.encode(credential.getChallengeResponse(), DEFAULT_ITERATIONS);
            log.info("hashedPassword: {}", hashedPassword);
            boolean check = userDAO.updatePassword(fetchedUser.getUserID(), hashedPassword, c);
            return check;
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Stream<UserModel> getRoleMembersStream(RealmModel realm, RoleModel role, Integer firstResult,
            Integer maxResults) {
        log.info("getRoleMembersStream(realm={}, role={}, firstResult={}, maxResults={})", 
            realm.getName(), role.getName(), firstResult, maxResults);
        try (Connection c = DbUtil.getConnection(this.model)){
            return this.userDAO.getUsersByRole(role.getName(), firstResult, maxResults, c).stream()
                .map(user -> {
                    UserModel userModel = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    // userModel.getAttributes();
                    return userModel;
                });
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
