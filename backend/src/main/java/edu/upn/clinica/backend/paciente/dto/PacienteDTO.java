package edu.upn.clinica.backend.paciente.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

// ============================================================
//  PacienteDTO.java
//  Contrato JSON entre Angular y el backend
//  Nunca expone passwordHash
// ============================================================
public class PacienteDTO {

    private Integer   idPaciente;

    @NotBlank(message = "El nombre es obligatorio")
    private String    nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String    apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    private String    email;

    // Solo se usa al crear — nunca se devuelve
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String    password;

    private String    telefono;
    private String    estado;
    private String    codigoEstudiante;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El género es obligatorio")
    private String    genero;

    private String    tipoSangre;
    private String    alergias;

    // --- Getters y Setters ---
    public Integer   getIdPaciente()                          { return idPaciente;                        }
    public void      setIdPaciente(Integer idPaciente)        { this.idPaciente = idPaciente;             }

    public String    getNombre()                              { return nombre;                            }
    public void      setNombre(String nombre)                 { this.nombre = nombre;                     }

    public String    getApellido()                            { return apellido;                          }
    public void      setApellido(String apellido)             { this.apellido = apellido;                 }

    public String    getEmail()                               { return email;                             }
    public void      setEmail(String email)                   { this.email = email;                       }

    public String    getPassword()                            { return password;                          }
    public void      setPassword(String password)             { this.password = password;                 }

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