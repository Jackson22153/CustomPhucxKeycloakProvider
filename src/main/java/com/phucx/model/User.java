package com.phucx.model;

public class User {
    private String userID;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Boolean emailVerified;
    private Boolean enabled;

    public User() {
    }

    public User(String userID, String firstName, String lastName, String email, String username, String password, Boolean emailVerified, Boolean enabled) {
        this.userID = userID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.emailVerified=emailVerified;
        this.enabled=enabled;
    }

    // extract user from result set of sql query
    // private static List<User> convertUser(ResultSet rs) throws SQLException{
    //     List<User> users = new ArrayList<>();
    //     while (rs.next()) {
    //         String fusername = rs.getString("username");
    //         String fuserID = rs.getString("userID");
    //         String ffirstname = rs.getString("firstName");
    //         String flastname = rs.getString("lastName");
    //         String fpassword = rs.getString("password");
    //         String femail = rs.getString("email");
    //         Boolean femailVerified = rs.getBoolean("emailVerified");
    //         Boolean fenabled = rs.getBoolean("enabled");
    //         // Create a new Users object
    //         User user = new User(fuserID, ffirstname, flastname, femail, fusername, fpassword, femailVerified, fenabled);
        
    //         users.add(user);
    //     }
    //     return users;
    // }

    // private static List<Role> convertRoles(ResultSet rs) throws SQLException{
    //     List<Role> roles = new ArrayList<>();
    //     while (rs.next()) {
    //         Integer roleID = rs.getInt("roleID");
    //         String roleName = rs.getString("roleName");
    //         // Create a new Roles object
    //         Role role = new Role(roleID, roleName); 
    //         roles.add(role);
    //     }
    //     return roles;
    // }

    // public User saveUser(Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement(
    //         "insert into Users(userID, username, password, email, emailVerified, enabled) values(?,?,?,?,?,?)");
    //     st.setString(1, this.userID);
    //     st.setString(2, this.username);
    //     st.setString(3, this.password);
    //     st.setString(4, this.email);
    //     st.setBoolean(5, true);
    //     st.setBoolean(6, true);
    //     int rs = st.executeUpdate();
    //     if(rs>0) return this;
    //     return null;
    // }

    // public boolean deleteUser(Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement(
    //         "exec deleteUser ?");
    //     st.setString(1, this.userID);
    //     return !st.execute();
    // }

    // public boolean updateEmailAttribute(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set email = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean updateEmailVerifiedAttribute(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set emailVerified = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean updateEnabledAttribute(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set enabled = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean updatePassword(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set password = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean updateFirstName(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set firstName = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public boolean updateLastName(String value, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("update Users set lastName = ? where userID=?");
    //     st.setString(1, value);
    //     st.setString(2, this.userID);
    //     int rs = st.executeUpdate();
    //     if(rs>0){
    //         return true;
    //     }
    //     return false;
    // }

    // public List<Role> getRoles(Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement(
    //         "select r.*\n"+
    //         "from Users u join (UserRole ur join Roles r on ur.roleID=r.roleID) on u.userID=ur.userID\n"+
    //         "where u.userID=?");
    //     st.setString(1, this.userID);
    //     ResultSet rs = st.executeQuery();
    //     List<Role> roles = convertRoles(rs);
    //     return roles;
    // }

    // public static List<User> getListUsersLike(String opusername, Connection c) throws SQLException{
    //     String search = "%"+opusername+"%";
    //     PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username");
    //     st.setString(1, search);
    //     ResultSet rs = st.executeQuery();
    //     List<User> users = convertUser(rs);
    //     return users;
    // }

    // public static List<User> getListUsersAtrtLike(String attributeName, String attributeValue, Connection c) throws SQLException{
    //     String search = "%"+attributeValue+"%";
    //     PreparedStatement st = c.prepareStatement("select * from users where ? like ? order by username");
    //     st.setString(1, attributeName);
    //     st.setString(2, search);
    //     ResultSet rs = st.executeQuery();
    //     List<User> users = convertUser(rs);
    //     return users;
    // }

    // public static User getUserByUsername(String username, Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where username=?");
    //     ps.setString(1, username);
    //     ResultSet rs = ps.executeQuery();

    //     List<User> users = convertUser(rs);
    //     if(users.size()>0) return users.get(0);
    //     return null;
    // }

    // public static List<User> getListUsers(int maxResults, int firstResult, Connection c) throws SQLException{
    //     PreparedStatement st = c.prepareStatement("select * from users order by username offset ? rows fetch next ? rows only");
    //     st.setInt(1, firstResult);
    //     st.setInt(2, maxResults);
    //     ResultSet rs = st.executeQuery();
    //     List<User> users = convertUser(rs);
    //     return users;
    // }

    // public static List<User> getListUsersLike(String opusername, int maxResults, int firstResult, Connection c) throws SQLException{
    //     String search = "%"+opusername+"%";
    //     PreparedStatement st = c.prepareStatement("select * from users where username like ? order by username offset ? rows fetch next ? rows only");
    //     st.setString(1, search);
    //     st.setInt(2, firstResult);
    //     st.setInt(3, maxResults);
    //     ResultSet rs = st.executeQuery();
    //     List<User> users = convertUser(rs);
    //     return users;
    // }

    // public static User getUserByEmail(String email, Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where email=?");
    //     ps.setString(1, email);
    //     ResultSet rs = ps.executeQuery();
        
    //     List<User> users = convertUser(rs);
    //     if(users.size()>0) return users.get(0);
    //     return null;
    // }

    // public static int getCountUsers(Connection c) throws SQLException{
    //     Statement st = c.createStatement();
    //     ResultSet rs = st.executeQuery("select count(*) from Users");
    //     rs.next();
    //     return rs.getInt(1);

    // }

    // public static User getUserByID(String userID, Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT * FROM Users where userID=?");
    //     ps.setString(1, userID);
    //     ps.executeQuery();
    //     ps.execute();
    //     ResultSet rs = ps.getResultSet();
        
    //     List<User> users = convertUser(rs);
    //     if(users.size()>0) return users.get(0);
    //     return null;
    // }

    // public String getEmail(Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT email FROM Users Where userID=?");
    //     ps.setString(1, this.userID);
    //     ResultSet rs = ps.executeQuery();
    //     rs.next();
    //     String email = rs.getString("email");
    //     return email;
    // }

    // public Boolean getEnabled(Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT enabled FROM Users Where userID=?");
    //     ps.setString(1, this.userID);
    //     ResultSet rs = ps.executeQuery();
    //     rs.next();
    //     Boolean enabled = rs.getBoolean("enabled");
    //     return enabled;
    // }

    // public Boolean getEmailVerified(Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("SELECT emailVerified FROM Users Where userID=?");
    //     ps.setString(1, this.userID);
    //     ResultSet rs = ps.executeQuery();
    //     rs.next();
    //     Boolean emailVerified = rs.getBoolean("emailVerified");
    //     return emailVerified;
    // }

    // public static String assignUserRole(String username, String roleName, Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("exec assignUserRole ?, ?");
    //     ps.setString(1, username);
    //     ps.setString(2, roleName);
        
    //     ResultSet rs = ps.executeQuery();
    //     rs.next();
    //     String result = rs.getString(1);
    //     return result;
    // }

    // public static String deleteUserRole(String username, String roleName, Connection c) throws SQLException{
    //     PreparedStatement ps = c.prepareStatement("exec deleteUserRole ?, ?");
    //     ps.setString(1, username);
    //     ps.setString(2, roleName);
        
    //     ResultSet rs = ps.executeQuery();
    //     rs.next();
    //     String result = rs.getString(1);
    //     return result;
    // }



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
    public void setEmailVerified(Boolean emailVerified) {
        this.emailVerified = emailVerified;
    }
    public Boolean getEmailVerified() {
        return emailVerified;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
