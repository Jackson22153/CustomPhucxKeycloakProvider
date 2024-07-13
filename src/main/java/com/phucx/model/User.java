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
