package com.aquavitae.domain.models;

public class AuthenticatedUser {

    private final String uid;
    private final String email;

    public AuthenticatedUser(String uid, String email) {
        this.uid = uid;
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }
}