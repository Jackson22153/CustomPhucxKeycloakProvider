package com.phucx.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Role{
    private Integer roleID;
    private String roleName;
    public Role(Integer roleID, String roleName){
        this.roleID = roleID;
        this.roleName = roleName;
    }

    private static List<Role> convertRoles(ResultSet rs) throws SQLException{
        List<Role> roles = new ArrayList<>();
        while (rs.next()) {
            Integer roleID = rs.getInt("roleID");
            String roleName = rs.getString("roleName");
            // Create a new Roles object
            Role role = new Role(roleID, roleName); 
            roles.add(role);
        }
        return roles;
    }

    public static List<Role> getListRoles(Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("select * from roles");
        ResultSet rs = st.executeQuery();
        List<Role> users = convertRoles(rs);
        return users;
    }   
    public static Role getRoleByID(Integer roleID, Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("select * from roles where roleID=?");
        st.setInt(1, roleID);
        ResultSet rs = st.executeQuery();
        List<Role> roles = convertRoles(rs);
        if(roles.size()>0) return roles.get(0);
        else return null;
    }  
    public static Role getRoleByName(String roleName, Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("select * from roles where roleName=?");
        st.setString(1, roleName);
        ResultSet rs = st.executeQuery();
        List<Role> roles = convertRoles(rs);
        if(roles.size()>0) return roles.get(0);
        else return null;
    }  

    public static List<Role> getListRolesLike(String opusername, int maxResults, int firstResult, Connection c) throws SQLException{
        String search = "%"+opusername+"%";
        PreparedStatement st = c.prepareStatement("select * from roles where roleName like ? order by roleName offset ? rows fetch next ? rows only");
        st.setString(1, search);
        st.setInt(2, firstResult);
        st.setInt(3, maxResults);
        ResultSet rs = st.executeQuery();
        List<Role> roles = convertRoles(rs);
        return roles;
    }

    public static List<Role> getListRolesLikeIn(String opusername, List<String> ids, int maxResults, int firstResult, Connection c) throws SQLException{
        String search = "%"+opusername+"%";
        PreparedStatement st = c.prepareStatement("select * from roles where roleName like ? order by roleName offset ? rows fetch next ? rows only");
        st.setString(1, search);
        st.setInt(2, firstResult);
        st.setInt(3, maxResults);
        ResultSet rs = st.executeQuery();
        List<Role> roles = convertRoles(rs);
        return roles;
    }

    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
    public Integer getRoleID() {
        return roleID;
    }
    public String getRoleName() {
        return roleName;
    }
}
