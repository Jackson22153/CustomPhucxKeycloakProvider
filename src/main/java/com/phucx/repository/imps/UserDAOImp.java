package com.phucx.repository.imps;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.phucx.model.User;
import com.phucx.repository.UserDAO;

public class UserDAOImp implements UserDAO{

    @Override
    public String getEmail(String userID, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT email FROM Users Where userID=?");
        ps.setString(1, userID);
        ResultSet rs = ps.executeQuery();
        rs.next();
        String email = rs.getString("email");
        return email;
    }

    @Override
    public Boolean getEnabled(String userID, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT enabled FROM Users Where userID=?");
        ps.setString(1, userID);
        ResultSet rs = ps.executeQuery();
        rs.next();
        Boolean enabled = rs.getBoolean("enabled");
        return enabled;
    }

    @Override
    public Boolean getEmailVerified(String userID, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT emailVerified FROM Users Where userID=?");
        ps.setString(1, userID);
        ResultSet rs = ps.executeQuery();
        rs.next();
        Boolean emailVerified = rs.getBoolean("emailVerified");
        return emailVerified;
    }

    @Override
    public List<User> getUsersLike(String username, Connection c) throws SQLException {
        String search = "%"+username+"%";
        PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username");
        st.setString(1, search);
        ResultSet rs = st.executeQuery();
        List<User> users = convertUser(rs);
        return users;
    }

    @Override
    public List<User> getUsersByAttributeLike(String attribute, String attributeValue, Connection c) throws SQLException {
        String search = "%"+attributeValue+"%";
        PreparedStatement st = c.prepareStatement("select * from users where ? like ? order by username");
        st.setString(1, attribute);
        st.setString(2, search);
        ResultSet rs = st.executeQuery();
        List<User> users = convertUser(rs);
        return users;
    }

    @Override
    public User getUserByUsername(String username, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where username=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        
        List<User> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }

    @Override
    public List<User> getUsers(Integer firstResult, Integer maxResults, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("select * from users order by username offset ? rows fetch next ? rows only");
        st.setInt(1, firstResult);
        st.setInt(2, maxResults);
        ResultSet rs = st.executeQuery();
        List<User> users = convertUser(rs);
        return users;
    }

    @Override
    public List<User> getUsersLike(String username, Integer firstResult, Integer maxResults, Connection c) throws SQLException {
        String search = "%"+username+"%";
        PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username offset ? rows fetch next ? rows only");
        st.setString(1, search);
        st.setInt(2, firstResult);
        st.setInt(3, maxResults);
        ResultSet rs = st.executeQuery();
        List<User> users = convertUser(rs);
        return users;
    }

    @Override
    public User getUserByEmail(String email, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        
        List<User> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }

    @Override
    public User getUserByID(String userID, Connection c) throws SQLException {
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where userID=?");
        ps.setString(1, userID);
        ps.executeQuery();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        List<User> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }

    @Override
    public Integer getCountUsers(Connection c) throws SQLException {
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("select count(*) from Users");
        rs.next();
        return rs.getInt(1);
    }

    @Override
    public User saveUser(User user, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement(
            "insert into Users(userID, username, password, email, emailVerified, enabled) values(?,?,?,?,?,?)");
        st.setString(1, user.getUserID());
        st.setString(2, user.getUsername());
        st.setString(3, user.getPassword());
        st.setString(4, user.getEmail());
        st.setBoolean(5, user.getEmailVerified());
        st.setBoolean(6, user.getEnabled());
        int rs = st.executeUpdate();
        if(rs>0) return user;
        return null;
    }

    @Override
    public Boolean deleteUser(String userID, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("exec deleteUser ?");
        st.setString(1, userID);
        return !st.execute();
    }

    @Override
    public Boolean updateEmail(String userID, String email, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set email = ? where userID=?");
        st.setString(1, email);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateEmailVerified(String userID, String emailVerified, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set emailVerified = ? where userID=?");
        st.setString(1, emailVerified);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateEnabled(String userID, String enabled, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set enabled = ? where userID=?");
        st.setString(1, enabled);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean updatePassword(String userID, String password, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set password = ? where userID=?");
        st.setString(1, password);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateFirstname(String userID, String firstname, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set firstName = ? where userID=?");
        st.setString(1, firstname);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    @Override
    public Boolean updateLastname(String userID, String lastname, Connection c) throws SQLException {
        PreparedStatement st = c.prepareStatement("update Users set lastName = ? where userID=?");
        st.setString(1, lastname);
        st.setString(2, userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    private static List<User> convertUser(ResultSet rs) throws SQLException{
        List<User> users = new ArrayList<>();
        while (rs.next()) {
            String fusername = rs.getString("username");
            String fuserID = rs.getString("userID");
            String ffirstname = rs.getString("firstName");
            String flastname = rs.getString("lastName");
            String fpassword = rs.getString("password");
            String femail = rs.getString("email");
            Boolean femailVerified = rs.getBoolean("emailVerified");
            Boolean fenabled = rs.getBoolean("enabled");
            // Create a new Users object
            User user = new User(fuserID, ffirstname, flastname, femail, fusername, fpassword, femailVerified, fenabled);
        
            users.add(user);
        }
        return users;
    }
    
}
