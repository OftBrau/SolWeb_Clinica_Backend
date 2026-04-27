package edu.upn.clinica.backend.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

// ============================================================
//  LoginRequest.java
//  DTO — lo que Angular envía al hacer login
//  POST /api/auth/login
//  Body: { "email": "...", "password": "..." }
// ============================================================
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    public String getEmail()                { return email;                }
    public void   setEmail(String email)    { this.email = email;          }
    public String getPassword()             { return password;             }
    public void   setPassword(String pass)  { this.password = pass;        }
}