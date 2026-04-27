package edu.upn.clinica.backend.auth;

// ============================================================
//  LoginResponse.java
//  DTO — lo que el backend devuelve a Angular tras el login
//  { "token": "...", "rol": "DOCTOR", "nombre": "Carlos Mendoza" }
// ============================================================
public class LoginResponse {

    private String token;
    private String rol;
    private String nombre;
    private String email;

    public LoginResponse(String token, String rol, String nombre, String email) {
        this.token  = token;
        this.rol    = rol;
        this.nombre = nombre;
        this.email  = email;
    }

    public String getToken()  { return token;  }
    public String getRol()    { return rol;    }
    public String getNombre() { return nombre; }
    public String getEmail()  { return email;  }
}