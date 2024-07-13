package com.phucx.repository;

import java.sql.Connection;
import java.sql.SQLException;

public interface UserRoleDAO {
    public String assignUserRole(String username, String rolename, Connection c) throws SQLException;
    public String deleteUserRole(String username, String rolename, Connection c) throws SQLException;
}
