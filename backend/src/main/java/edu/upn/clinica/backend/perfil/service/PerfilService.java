package edu.upn.clinica.backend.perfil.service;

import edu.upn.clinica.backend.perfil.repository.PerfilRepository;
import edu.upn.clinica.backend.perfil.model.PerfilProfesional;
import edu.upn.clinica.backend.practicante.repository.PracticanteRepository;
import edu.upn.clinica.backend.shared.AppException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PerfilService {

    @Autowired private PerfilRepository repo;
    @Autowired private PracticanteRepository practicanteRepository;

    public Map<String, Object> getPerfil(Integer idPracticante) {
        PerfilProfesional p = repo.findByPracticante(idPracticante)
                .orElseThrow(() -> new AppException("Perfil no encontrado. Completa tu perfil primero.", HttpStatus.NOT_FOUND));
        return buildResponse(p);
    }

    public Map<String, Object> getOrCreate(Integer idPracticante) {
        Optional<PerfilProfesional> opt = repo.findByPracticante(idPracticante);
        PerfilProfesional p;
        if (opt.isPresent()) {
            p = opt.get();
        } else {
            p = new PerfilProfesional();
            p.setIdPracticante(idPracticante);
            p.setActivo(true);
            p = repo.save(p);
        }
        return buildResponse(p);
    }

    public Map<String, Object> updatePerfil(Integer idPracticante, Map<String, Object> body) {
        PerfilProfesional p = repo.findByPracticante(idPracticante)
                .orElseGet(() -> {
                    PerfilProfesional np = new PerfilProfesional();
                    np.setIdPracticante(idPracticante); np.setActivo(true);
                    return np;
                });
        if (body.containsKey("tituloProfesional")) p.setTituloProfesional((String) body.get("tituloProfesional"));
        if (body.containsKey("universidad")) p.setUniversidad((String) body.get("universidad"));
        if (body.containsKey("anioGraduacion")) p.setAnioGraduacion(body.get("anioGraduacion") != null ? ((Number) body.get("anioGraduacion")).intValue() : null);
        if (body.containsKey("biografia")) p.setBiografia((String) body.get("biografia"));
        if (body.containsKey("linkedinUrl")) p.setLinkedinUrl((String) body.get("linkedinUrl"));
        repo.save(p);
        return buildResponse(repo.findByPracticante(idPracticante).orElse(p));
    }

    public Map<String, Object> addCertificacion(Integer idPerfil, Map<String, String> body) {
        return repo.saveCertificacion(idPerfil, body.get("nombre"), body.get("institucion"),
                body.get("fechaEmision"), body.get("fechaVencimiento"), body.get("archivoUrl"));
    }

    public Map<String, Object> addExperiencia(Integer idPerfil, Map<String, Object> body) {
        return repo.saveExperiencia(idPerfil, (String) body.get("empresa"), (String) body.get("cargo"),
                (String) body.get("fechaInicio"), (String) body.get("fechaFin"),
                body.get("actualmente") != null ? (Boolean) body.get("actualmente") : false,
                (String) body.get("descripcion"));
    }

    public Map<String, Object> addEducacion(Integer idPerfil, Map<String, String> body) {
        return repo.saveEducacion(idPerfil, body.get("institucion"), body.get("titulo"),
                body.get("fechaInicio"), body.get("fechaFin"));
    }

    public Map<String, Object> addHabilidad(Integer idPerfil, Map<String, String> body) {
        return repo.saveHabilidad(idPerfil, body.get("nombre"));
    }

    public List<Map<String, Object>> getDoctorView() {
        List<PerfilProfesional> perfiles = repo.findAllActivos();
        List<Map<String, Object>> result = new ArrayList<>();
        for (PerfilProfesional pp : perfiles) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("perfil", pp);
            m.put("certificaciones", repo.findCertificaciones(pp.getIdPerfil()));
            m.put("experiencias", repo.findExperiencias(pp.getIdPerfil()));
            m.put("educaciones", repo.findEducaciones(pp.getIdPerfil()));
            m.put("habilidades", repo.findHabilidades(pp.getIdPerfil()));
            result.add(m);
        }
        return result;
    }

    private Map<String, Object> buildResponse(PerfilProfesional p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("perfil", p);
        m.put("certificaciones", repo.findCertificaciones(p.getIdPerfil()));
        m.put("experiencias", repo.findExperiencias(p.getIdPerfil()));
        m.put("educaciones", repo.findEducaciones(p.getIdPerfil()));
        m.put("habilidades", repo.findHabilidades(p.getIdPerfil()));
        return m;
    }
}
