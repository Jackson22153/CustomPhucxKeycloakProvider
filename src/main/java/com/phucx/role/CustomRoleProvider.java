package com.phucx.role;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Stream;

import org.keycloak.models.ClientModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleModel;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.role.RoleStorageProvider;
import org.keycloak.storage.role.RoleStorageProviderModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.phucx.DbUtil;
import com.phucx.model.Roles;


public class CustomRoleProvider implements RoleStorageProvider{
    private static final Logger log = LoggerFactory.getLogger(CustomRoleProvider.class);
    private RoleStorageProviderModel model;
    private KeycloakSession ksession;


    public CustomRoleProvider(RoleStorageProviderModel model, KeycloakSession ksession){
        this.model = model;
        this.ksession = ksession;
    }

    @Override
    public void close() {
        log.info("[I160] close");
    }

    @Override
    public RoleModel getClientRole(ClientModel client, String name) {
        log.info("[I161] getRealmRole: realm={}, role={}", client.getRealm().getName(), name);
        try (Connection connection = DbUtil.getConnection(this.model)){
            Roles role = Roles.getRoleByName(name, connection);
            return new CustomRoleAdapter(role, client.getRealm(), ksession, model);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RoleModel getRealmRole(RealmModel realm, String name) {
        log.info("[I161] getRealmRole: realm={}, role={}", realm.getName(), name);
        try (Connection connection = DbUtil.getConnection(this.model)){
            Roles role = Roles.getRoleByName(name, connection);
            return new CustomRoleAdapter(role, realm, ksession, model);
        } catch (SQLException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public RoleModel getRoleById(RealmModel realm, String id) {
        log.info("[I162] getRoleById: realm={}, roleId={}", realm.getName(), id);
        log.info("id={}", id);
        StorageId sid = new StorageId(id);
        return getRealmRole(realm, sid.getExternalId());
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(ClientModel client, String search, Integer first, Integer max) {
        log.info("[I163] searchForClientRolesStream: realm={}, search={}", client.getRealm().getName(), search);
        try (Connection connection = DbUtil.getConnection(model)){
            List<Roles> roles = Roles.getListRolesLike(search, max, first, connection);
            return roles.stream().map(role -> 
                new CustomRoleAdapter(role, client.getRealm(), this.ksession, this.model));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(RealmModel realm, Stream<String> ids, String search, Integer first,
            Integer max) {
        log.info("[I163] searchForClientRolesStream: realm={}, search={}", realm.getName(), search);
        return this.searchForClientRolesStream(realm, ids, search, first, max, false);
    }

    @Override
    public Stream<RoleModel> searchForClientRolesStream(RealmModel realm, String search, Stream<String> excludedIds, Integer first,
            Integer max) {
        log.info("[I163] searchForClientRolesStream: realm={}, search={}", realm.getName(), search);
        return this.searchForClientRolesStream(realm, excludedIds, search, first, max, true);
    }

    private Stream<RoleModel> searchForClientRolesStream(RealmModel realm, Stream<String> ids, String search, Integer first, Integer max, boolean negateIds) {
        log.info("[I164] searchForClientRolesStream: realm={}, search={}", realm.getName(), search);
        ids.forEach(System.out::println);
        return Stream.empty();
    }

    @Override
    public Stream<RoleModel> searchForRolesStream(RealmModel realm, String search, Integer first, Integer max) {
        log.info("[I163] searchForClientRolesStream: realm={}, search={}", realm.getName(), search);
        try (Connection connection = DbUtil.getConnection(model)){
            List<Roles> roles = Roles.getListRolesLike(search, max, first, connection);
            return roles.stream().map(role -> 
                new CustomRoleAdapter(role, realm, this.ksession, this.model));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    
}
