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
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.hash.PasswordHashProvider;
import org.keycloak.models.GroupModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
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
import com.phucx.model.Users;
import com.phucx.passwordHashing.BcryptPasswordHashingProvider;


public class CustomUserStorageProvider implements 
    UserStorageProvider, 
    UserLookupProvider, 
    CredentialInputValidator,
    UserRegistrationProvider,
    UserQueryProvider,
    ImportedUserValidation{

    private static final Logger log = LoggerFactory.getLogger(CustomUserStorageProvider.class);
    private KeycloakSession ksession;
    private ComponentModel model;

    private Map<String, CustomUserAdapterFeDeratedStorage> instanceUsers = new HashMap<>();


    public CustomUserStorageProvider(KeycloakSession ksession, ComponentModel model) {
        this.ksession = ksession;
        this.model = model;
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
                Users user = Users.getUserByUsername(username, c);
                if(user!=null){
                    cachedUser = new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
                    instanceUsers.put(username, cachedUser);
                } 
            }
            catch(SQLException ex) {
                throw new RuntimeException("Database error:" + ex.getMessage(),ex);
            }
        }
        // ksession.getProvider(UserCache.class).evict(realm, cachedUser);
        return cachedUser;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        log.info("[I41] getUserByEmail({})",email);

        CustomUserAdapterFeDeratedStorage cachedUser = instanceUsers.get(email);
        if(cachedUser == null){
            try ( Connection c = DbUtil.getConnection(this.model)) {
                Users user = Users.getUserByEmail(email, c);
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
    public boolean supportsCredentialType(String credentialType) {
        log.info("[I57] supportsCredentialType({})",credentialType);
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
    public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
        log.info("[I57] isValid(realm={},user={},credentialInput.type={})",realm.getName(), user.getUsername(), credentialInput.getType());
        if( !this.supportsCredentialType(credentialInput.getType())) {
            return false;
        }
        StorageId sid = new StorageId(user.getId());
        String username = sid.getExternalId();
 
        try ( Connection c = DbUtil.getConnection(this.model)) {
            Users u = Users.getUserByUsername(username, c);
            if(u!=null){
                String pwd = u.getPassword();
                return pwd.equals(credentialInput.getChallengeResponse());
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
            return Users.getCountUsers(c);
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public Stream<UserModel> getGroupMembersStream(RealmModel realm, GroupModel group, Integer firstResult, Integer maxResults) {
        log.info("[I113] getUsers: realm={}", realm.getName());
        
        try ( Connection c = DbUtil.getConnection(this.model)) {
            List<Users> users = Users.getListUsers(maxResults, firstResult, c);
            List<UserModel> obUser = users.stream().map(user -> 
                new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());

            return obUser.stream();
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, String search, Integer firstResult, Integer maxResults) {
        log.info("[I139] searchForUser: realm={}", realm.getName());
        try (Connection c = DbUtil.getConnection(this.model)) {
            List<Users> users = Users.getListUsersLike(search, maxResults, firstResult, c);
            List<UserModel> obUser = users.stream().map(user -> 
                new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());

            return obUser.stream();
        } catch (SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(), ex);
        }
    }

    @Override
    public Stream<UserModel> searchForUserStream(RealmModel realm, Map<String, String> params, Integer firstResult, Integer maxResults) {
        log.info("[I140] searchForUser: realm={}", realm.getName());
        String search = params.get("keycloak.session.realm.users.query.search");

        return searchForUserStream(realm, search, firstResult, maxResults);
    }

    @Override
    public Stream<UserModel> searchForUserByUserAttributeStream(RealmModel realm, String attrName, String attrValue) {
        log.info("[I141] searchForUser: realm={}", realm.getName());
        try (Connection c = DbUtil.getConnection(this.model)){
            List<Users> users = Users.getListUsersAtrtLike(attrName, attrValue, null);
            List<UserModel> obUser = users.stream().map(user -> 
                new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user))
                .collect(Collectors.toList());
            return obUser.stream();
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public UserModel addUser(RealmModel realm, String username) {
        return null;
        // log.info("[I142] addUser: realm={}, username={}", realm.getName(), username);
        // try (Connection connection = DbUtil.getConnection(this.model)){
        //     // PasswordHashProvider passwordHashProvider = ksession.getProvider(BcryptPasswordHashingProvider.class);
        //     // String hashedPassword = passwordHashProvider.encode(CustomUserStorageProviderConstants.UNSET_PASSWORD, 10);
        //     Users user = new Users();
        //     user.setUserID(KeycloakModelUtils.generateId());
        //     user.setUsername(username);
        //     user.setPassword(CustomUserStorageProviderConstants.UNSET_PASSWORD);
        //     synchronized(user){
        //         user = user.saveUser(connection);
        //     }
        //     if(user!=null){
        //         return new CustomUserAdapterFeDeratedStorage(ksession, realm, model, user);
        //     }
        //     return null;
        // } catch (SQLException e) {
        //     throw new RuntimeException(e.getMessage());
        // }
    }


    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        return false;
        // log.info("[I143] removeUser: realm={}, username={}, userID={}", realm.getName(), user.getUsername(), user.getId());
        // try (Connection connection = DbUtil.getConnection(this.model)){
        //     Users removeUser = Users.getUserByUsername(user.getUsername(), connection);
        //     if(removeUser!=null){
        //         boolean check = false;
        //         synchronized(removeUser){
        //             check = removeUser.deleteUser(connection);
        //         }
        //         return check;
        //     }else return false;
        // } catch (SQLException e) {
        //     throw new RuntimeException(e.getMessage());
        // }
    }

    @Override
    public UserModel validate(RealmModel realm, UserModel user) {
        log.info("[I144] validate: realm={}, username={}", realm.getName(), user.getUsername());
        try ( Connection c = DbUtil.getConnection(this.model)) {
            Users fetcheduser = Users.getUserByUsername(user.getUsername(), c);
            if(fetcheduser!=null){
                return new CustomUserAdapterFeDeratedStorage(ksession, realm, model, fetcheduser);
            }
        }
        catch(SQLException ex) {
            throw new RuntimeException("Database error:" + ex.getMessage(),ex);
        }
        return null;
    }
}
