package com.phucx.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Users {
    private String userID;
    private String email;
    private String username;
    private String password;
    
    public Users() {
    }


    public Users(String userID, String email, String username, String password) {
        this.userID = userID;
        this.email = email;
        this.username = username;
        this.password = password;
    }


    private static List<Users> convertUser(ResultSet rs) throws SQLException{
        List<Users> users = new ArrayList<>();
        while (rs.next()) {
            String fusername = rs.getString("username");
            String fuserID = rs.getString("userID");
            String fpassword = rs.getString("password");
            String femail = rs.getString("email");

            // Create a new Users object
            Users user = new Users(fuserID, femail, fusername, fpassword);
            users.add(user);
        }
        return users;
    }

    private static List<Roles> convertRoles(ResultSet rs) throws SQLException{
        List<Roles> roles = new ArrayList<>();
        while (rs.next()) {
            Integer roleID = rs.getInt("roleID");
            String roleName = rs.getString("roleName");
            // Create a new Roles object
            Roles role = new Roles(roleID, roleName); 
            roles.add(role);
        }
        return roles;
    }

    public Users saveUser(Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement(
            "insert into Users(userID, username, password, email) values(?,?,?,?)");
        st.setString(1, this.userID);
        st.setString(2, this.username);
        st.setString(3, this.password);
        st.setString(4, this.email);
        int rs = st.executeUpdate();
        if(rs>0) return this;
        return null;
    }

    public boolean deleteUser(Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement(
            "exec deleteUser ?");
        st.setString(1, this.userID);
        return !st.execute();
    }

    public boolean updateEmailAttribute(String value, Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("update Users set email = ? where userID=?");
        st.setString(1, value);
        st.setString(2, this.userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }

    public boolean updatePassword(String value, Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("update Users set password = ? where userID=?");
        st.setString(1, value);
        st.setString(2, this.userID);
        int rs = st.executeUpdate();
        if(rs>0){
            return true;
        }
        return false;
    }



    public List<Roles> getRoles(Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement(
            "select r.*\n"+
            "from Users u join (UserRole ur join Roles r on ur.roleID=r.roleID) on u.userID=ur.userID\n"+
            "where u.userID=?");
        st.setString(1, this.userID);
        ResultSet rs = st.executeQuery();
        List<Roles> roles = convertRoles(rs);
        return roles;
    }

    public static List<Users> getListUsersLike(String opusername, Connection c) throws SQLException{
        String search = "%"+opusername+"%";
        PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username");
        st.setString(1, search);
        ResultSet rs = st.executeQuery();
        List<Users> users = convertUser(rs);
        return users;
    }

    public static List<Users> getListUsersAtrtLike(String attributeName, String attributeValue, Connection c) throws SQLException{
        String search = "%"+attributeValue+"%";
        PreparedStatement st = c.prepareStatement("select * from users where ? like ? order by username");
        st.setString(1, attributeName);
        st.setString(2, search);
        ResultSet rs = st.executeQuery();
        List<Users> users = convertUser(rs);
        return users;
    }

    public static Users getUserByUsername(String username, Connection c) throws SQLException{
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where username=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();

        // ResultSetMetaData rsmd = rs.getMetaData();
        // int columnCount = rsmd.getColumnCount();

        List<Users> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }

    public static List<Users> getListUsers(int maxResults, int firstResult, Connection c) throws SQLException{
        PreparedStatement st = c.prepareStatement("select * from users order by username offset ? rows fetch next ? rows only");
        st.setInt(1, firstResult);
        st.setInt(2, maxResults);
        ResultSet rs = st.executeQuery();
        List<Users> users = convertUser(rs);
        return users;
    }

    public static List<Users> getListUsersLike(String opusername, int maxResults, int firstResult, Connection c) throws SQLException{
        String search = "%"+opusername+"%";
        PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username offset ? rows fetch next ? rows only");
        st.setString(1, search);
        st.setInt(2, firstResult);
        st.setInt(3, maxResults);
        ResultSet rs = st.executeQuery();
        List<Users> users = convertUser(rs);
        return users;
    }

    public static Users getUserByEmail(String email, Connection c) throws SQLException{
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where email=?");
        ps.setString(1, email);
        ResultSet rs = ps.executeQuery();
        
        List<Users> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }

    public static int getCountUsers(Connection c) throws SQLException{
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery("select count(*) from Users");
        rs.next();
        return rs.getInt(1);

    }

    public static Users getUserByID(String userID, Connection c) throws SQLException{
        PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where userID=?");
        ps.setString(1, userID);
        ps.executeQuery();
        ps.execute();
        ResultSet rs = ps.getResultSet();
        
        List<Users> users = convertUser(rs);
        if(users.size()>0) return users.get(0);
        return null;
    }



    // getter vs setter
    public void setUserID(String userID){
        this.userID=userID;
    }
    public String getUserID(){
        return this.userID;
    }

    public void setUsername(String username){
        this.username=username;
    }
    public String getUsername(){
        return this.username;
    }

    public void setPassword(String password){
        this.password=password;
    }
    public String getPassword(){
        return this.password;
    }

    public void setEmail(String email){
        this.email=email;
    }
    public String getEmail(){
        return this.email;
    }
}
