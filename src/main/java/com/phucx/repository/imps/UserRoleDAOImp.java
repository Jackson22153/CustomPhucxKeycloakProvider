package com.phucx.repository.imps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.phucx.repository.UserRoleDAO;

public class UserRoleDAOImp implements UserRoleDAO{

    @Override
    public String assignUserRole(String username, String rolename, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("exec assignUserRole ?, ?");
        ps.setString(1, username);
        ps.setString(2, rolename);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        String result = rs.getString(1);
        return result;
    }

    @Override
    public String deleteUserRole(String username, String rolename, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("exec deleteUserRole ?, ?");
        ps.setString(1, username);
        ps.setString(2, rolename);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        String result = rs.getString(1);
        return result;
    }
    
}
