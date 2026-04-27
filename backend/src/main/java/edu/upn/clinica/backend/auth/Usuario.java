package edu.upn.clinica.backend.auth;

// ============================================================
//  Usuario.java
//  Modelo del dominio — mapea la tabla usuarios
// ============================================================
public class Usuario {

    private Integer id;
    private String  nombre;
    private String  apellido;
    private String  email;
    private String  passwordHash;
    private String  telefono;
    private String  rol;
    private String  estado;

    public Usuario() {}

    // --- Getters y Setters ---
    public Integer getId()           { return id;           }
    public void    setId(Integer id) { this.id = id;        }

    public String getNombre()              { return nombre;               }
    public void   setNombre(String nombre) { this.nombre = nombre;        }

    public String getApellido()                { return apellido;              }
    public void   setApellido(String apellido) { this.apellido = apellido;     }

    public String getEmail()             { return email;             }
    public void   setEmail(String email) { this.email = email;       }

    public String getPasswordHash()                    { return passwordHash;              }
    public void   setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getTelefono()                { return telefono;              }
    public void   setTelefono(String telefono) { this.telefono = telefono;     }

    public String getRol()           { return rol;           }
    public void   setRol(String rol) { this.rol = rol;       }

    public String getEstado()              { return estado;              }
    public void   setEstado(String estado) { this.estado = estado;       }
}