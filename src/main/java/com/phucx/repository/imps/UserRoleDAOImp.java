package com.phucx.repository.imps;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import com.phucx.repository.UserRoleDAO;

public class UserRoleDAOImp implements UserRoleDAO{

    @Override
    public Boolean assignUserRole(String username, String rolename, Connection c) throws SQLException {
        CallableStatement cs = c.prepareCall("call assignUserRole(?, ?, ?)");
        cs.setString(1, username);
        cs.setString(2, rolename);
        cs.registerOutParameter(3, java.sql.Types.BIT);

        cs.execute();
        Boolean result = cs.getBoolean(3);

        return result;
    }

    @Override
    public Boolean deleteUserRole(String username, String rolename, Connection c) throws SQLException {

        CallableStatement cs = c.prepareCall("call deleteUserRole(?, ?, ?)");
        cs.setString(1, username);
        cs.setString(2, rolename);
        cs.registerOutParameter(3, java.sql.Types.BIT);

        cs.execute();

        Boolean result = cs.getBoolean(3);
        return result;
    }
    
}
