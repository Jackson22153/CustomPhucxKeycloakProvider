package com.phucx.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.phucx.model.Role;

public interface RoleDAO {
    public List<Role> getRoles(String userID, Connection c) throws SQLException;
    
} 
