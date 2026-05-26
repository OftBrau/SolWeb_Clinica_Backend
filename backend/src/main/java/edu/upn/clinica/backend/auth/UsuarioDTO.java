package edu.upn.clinica.backend.auth;

public class UsuarioDTO {

    private Integer id;
    private String  nombre;
    private String  apellido;
    private String  email;
    private String  telefono;
    private String  rol;
    private String  estado;

    public UsuarioDTO() {}

    public UsuarioDTO(Usuario u) {
        this.id       = u.getId();
        this.nombre   = u.getNombre();
        this.apellido = u.getApellido();
        this.email    = u.getEmail();
        this.telefono = u.getTelefono();
        this.rol      = u.getRol();
        this.estado   = u.getEstado();
    }

    public Integer getId()             { return id;       }
    public void   setId(Integer id)    { this.id = id;    }

    public String getNombre()          { return nombre;   }
    public void   setNombre(String nom) { this.nombre = nom; }

    public String getApellido()        { return apellido; }
    public void   setApellido(String ap) { this.apellido = ap; }

    public String getEmail()           { return email;    }
    public void   setEmail(String mail) { this.email = mail; }

    public String getTelefono()        { return telefono; }
    public void   setTelefono(String t) { this.telefono = t; }

    public String getRol()             { return rol;      }
    public void   setRol(String rol)   { this.rol = rol;  }

    public String getEstado()          { return estado;   }
    public void   setEstado(String est) { this.estado = est; }
}
