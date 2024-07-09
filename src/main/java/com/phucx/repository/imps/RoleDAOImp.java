package com.phucx.repository.imps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.phucx.model.Role;
import com.phucx.repository.RoleDAO;

public class RoleDAOImp implements RoleDAO{

    @Override
    public List<Role> getRoles(String userID, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement(
            "select r.*\n"+
            "from Users u join (UserRole ur join Roles r on ur.roleID=r.roleID) on u.userID=ur.userID\n"+
            "where u.userID=?");
        st.setString(1, userID);
        ResultSet rs = st.executeQuery();
        List<Role> roles = convertRoles(rs);
        return roles;
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
    
}
