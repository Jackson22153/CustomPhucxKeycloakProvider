package com.phucx.role;

import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RoleContainerModel;
import org.keycloak.models.RoleModel;
import org.keycloak.models.utils.KeycloakModelUtils;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import com.phucx.model.Role;

public class CustomRoleAdapter implements RoleModel {
    // private Logger logger = LoggerFactory.getLogger(CustomRoleAdapter.class);
    private Role role;
    private RealmModel realm;
    protected KeycloakSession session;
    protected ComponentModel storageProviderModel;
    protected StorageId storageId;

    public CustomRoleAdapter(Role role, RealmModel realm, KeycloakSession session, ComponentModel storageProviderModel,
            StorageId storageId) {
        this.role = role;
        this.realm = realm;
        this.session = session;
        this.storageProviderModel = storageProviderModel;
        this.storageId = storageId;
    }

    public CustomRoleAdapter(Role role, RealmModel realm, KeycloakSession session, ComponentModel storageProviderModel) {
        this.role = role;
        this.realm = realm;
        this.session = session;
        this.storageProviderModel = storageProviderModel;
    }

    public RoleModel saveRole(){
        RoleModel role = KeycloakModelUtils.getRoleFromString(realm, this.getName());
        if(role==null){
            role =this.realm.addRole(this.getName());
        }
        return role;
    }

    @Override
    public void addCompositeRole(RoleModel role) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public Stream<String> getAttributeStream(String name) {
        return Stream.empty();
    }

    @Override
    public Map<String, List<String>> getAttributes() {
        throw new UnsupportedOperationException("Unimplemented method 'getAttributes'");
    }

    @Override
    public Stream<RoleModel> getCompositesStream(String arg0, Integer arg1, Integer arg2) {
        return Stream.empty();
    }

    @Override
    public RoleContainerModel getContainer() {
        return realm;
    }

    @Override
    public String getContainerId() {
        return this.realm.getId();
    }

    @Override
    public String getDescription() {
        return "Custom role";
    }

    @Override
    public String getId() {
        if(storageId==null){
            storageId = new StorageId(storageProviderModel.getId(), getName());
        }
        return storageId.getId();
    }

    @Override
    public String getName() {
        return this.role.getRoleName();
    }

    @Override
    public boolean hasRole(RoleModel role) {
        return true;
    }

    @Override
    public boolean isClientRole() {
        return false;
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public void removeAttribute(String name) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public void removeCompositeRole(RoleModel role) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public void setAttribute(String name, List<String> values) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public void setDescription(String description) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public void setName(String name) {
        this.role.setRoleName(name);
    }

    @Override
    public void setSingleAttribute(String name, String value) {
        throw new ReadOnlyException("Role is read only");
    }

    @Override
    public String getFirstAttribute(String name) {
        // TODO Auto-generated method stub
        return RoleModel.super.getFirstAttribute(name);
    }
    
}
