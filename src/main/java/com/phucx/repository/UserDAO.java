package com.phucx.repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.phucx.model.User;

public interface UserDAO {
    public String getEmail(String userID, Connection c) throws SQLException;
    public Boolean getEnabled(String userID, Connection c) throws SQLException;
    public Boolean getEmailVerified(String userID, Connection c) throws SQLException;

    public List<User> getUsersLike(String username, Connection c) throws SQLException;
    public List<User> getUsersByAttributeLike(String attribute, String attributeValue, Connection c) throws SQLException;
    public List<User> getUsers(Integer firstResult, Integer maxResults, Connection c) throws SQLException;
    public List<User> getUsersLike(String username, Integer firstResult, Integer maxResults, Connection c) throws SQLException;
    public User getUserByUsername(String username, Connection c) throws SQLException;
    public User getUserByEmail(String email, Connection c) throws SQLException;
    public User getUserByID(String userID, Connection c) throws SQLException;
    public Integer getCountUsers(Connection c) throws SQLException;

    public User saveUser(User user, Connection c) throws SQLException;
    public Boolean deleteUser(String userID, Connection c) throws SQLException;
    public Boolean updateEmail(String userID, String email, Connection c) throws SQLException;
    public Boolean updateEmailVerified(String userID, String emailVerified, Connection c) throws SQLException;
    public Boolean updateEnabled(String userID, String enabled, Connection c) throws SQLException;
    public Boolean updatePassword(String userID, String password, Connection c) throws SQLException;
    public Boolean updateFirstname(String userID, String firstname, Connection c) throws SQLException;
    public Boolean updateLastname(String userID, String lastname, Connection c) throws SQLException;

}
