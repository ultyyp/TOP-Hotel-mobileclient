package com.example.top_hotel_mobileapp.model;

import java.util.List;

public class User {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private List<Role> roles;

    // Inner Role class for the roles list
    public static class Role {
        private String id;
        private String name;

        // Constructors, Getters, and Setters
        public Role(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
    }

    // Getters and Setters for User fields
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public List<Role> getRoles() { return roles; }
    public void setRoles(List<Role> roles) { this.roles = roles; }
}
