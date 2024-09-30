package com.phucx.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserRoleDAO {
    public Boolean assignUserRole(String username, String rolename, Connection c) throws SQLException;
    public Boolean deleteUserRole(String username, String rolename, Connection c) throws SQLException;
}
