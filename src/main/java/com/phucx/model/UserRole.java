package com.phucx.model;

public class UserRole {
    private String userID;
    private Integer roleID;
    public UserRole(String userID, Integer roleID) {
        this.userID = userID;
        this.roleID = roleID;
    }
    
    public void setUserID(String userID) {
        this.userID = userID;
    }
    public void setRoleID(Integer roleID) {
        this.roleID = roleID;
    }
    public String getUserID() {
        return userID;
    }
    public Integer getRoleID() {
        return roleID;
    }
}
