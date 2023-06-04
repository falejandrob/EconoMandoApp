package com.economando.economandoapp.entities;

public class User {
    // Aquí puedes añadir los campos correspondientes a la respuesta del servidor
    // Por ejemplo:
    private String email;
    private String token;
    private String home_url;

    public User(String email, String token, String home_url) {
        this.email = email;
        this.token = token;
        this.home_url = home_url;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getHome_url() {
        return home_url;
    }

    public void setHome_url(String home_url) {
        this.home_url = home_url;
    }
}

