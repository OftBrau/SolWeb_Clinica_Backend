package edu.upn.clinica.backend.paciente.model;

import java.time.LocalDate;

// ============================================================
//  Paciente.java
//  Modelo del dominio — mapea las tablas usuarios + pacientes
// ============================================================
public class Paciente {

    private Integer   idPaciente;
    private Integer   idUsuario;
    private String    nombre;
    private String    apellido;
    private String    email;
    private String    passwordHash;
    private String    telefono;
    private String    estado;
    private String    codigoEstudiante;
    private LocalDate fechaNacimiento;
    private String    genero;
    private String    tipoSangre;
    private String    alergias;

    public Paciente() {}

    // --- Getters y Setters ---
    public Integer   getIdPaciente()                          { return idPaciente;                        }
    public void      setIdPaciente(Integer idPaciente)        { this.idPaciente = idPaciente;             }

    public Integer   getIdUsuario()                           { return idUsuario;                         }
    public void      setIdUsuario(Integer idUsuario)          { this.idUsuario = idUsuario;               }

    public String    getNombre()                              { return nombre;                            }
    public void      setNombre(String nombre)                 { this.nombre = nombre;                     }

    public String    getApellido()                            { return apellido;                          }
    public void      setApellido(String apellido)             { this.apellido = apellido;                 }

    public String    getEmail()                               { return email;                             }
    public void      setEmail(String email)                   { this.email = email;                       }

    public String    getPasswordHash()                        { return passwordHash;                      }
    public void      setPasswordHash(String passwordHash)     { this.passwordHash = passwordHash;         }

    public String    getTelefono()                            { return telefono;                          }
    public void      setTelefono(String telefono)             { this.telefono = telefono;                 }

    public String    getEstado()                              { return estado;                            }
    public void      setEstado(String estado)                 { this.estado = estado;                     }

    public String    getCodigoEstudiante()                    { return codigoEstudiante;                  }
    public void      setCodigoEstudiante(String cod)          { this.codigoEstudiante = cod;              }

    public LocalDate getFechaNacimiento()                     { return fechaNacimiento;                   }
    public void      setFechaNacimiento(LocalDate fecha)      { this.fechaNacimiento = fecha;             }

    public String    getGenero()                              { return genero;                            }
    public void      setGenero(String genero)                 { this.genero = genero;                     }

    public String    getTipoSangre()                          { return tipoSangre;                        }
    public void      setTipoSangre(String tipoSangre)         { this.tipoSangre = tipoSangre;             }

    public String    getAlergias()                            { return alergias;                          }
    public void      setAlergias(String alergias)             { this.alergias = alergias;                 }
}