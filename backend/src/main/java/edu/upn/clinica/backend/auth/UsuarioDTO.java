package edu.upn.clinica.backend.auth;

public class UsuarioDTO {

    private Integer id;
    private String  nombre;
    private String  email;
    private String  rol;

    public UsuarioDTO() {}

    public UsuarioDTO(Usuario u) {
        this.id     = u.getId();
        this.nombre = u.getNombre() + " " + u.getApellido();
        this.email  = u.getEmail();
        this.rol    = u.getRol();
    }

    public Integer getId()           { return id;    }
    public void   setId(Integer id)  { this.id = id; }

    public String getNombre()           { return nombre;              }
    public void   setNombre(String nom) { this.nombre = nom;          }

    public String getEmail()            { return email;               }
    public void   setEmail(String mail) { this.email = mail;          }

    public String getRol()             { return rol;                 }
    public void   setRol(String rol)   { this.rol = rol;             }
}
