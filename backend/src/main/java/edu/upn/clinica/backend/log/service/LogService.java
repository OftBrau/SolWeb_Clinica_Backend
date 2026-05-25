package edu.upn.clinica.backend.log.service;

import edu.upn.clinica.backend.log.model.LogActividad;
import edu.upn.clinica.backend.log.repository.LogRepository;
import edu.upn.clinica.backend.shared.PageResult;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogService {

    @Autowired
    private LogRepository logRepository;

    public void registrar(String email, Integer idUsuario, String accion, String detalle, String ip) {
        LogActividad log = new LogActividad();
        log.setEmail(email);
        log.setIdUsuario(idUsuario);
        log.setAccion(accion);
        log.setDetalle(detalle);
        log.setIp(ip);
        logRepository.save(log);
    }

    public void registrar(String email, String accion, String detalle, HttpServletRequest request) {
        String ip = request != null ? request.getRemoteAddr() : "unknown";
        registrar(email, null, accion, detalle, ip);
    }

    public PageResult<LogActividad> listar(int page, int size) {
        List<LogActividad> logs = logRepository.findAll(page, size);
        long total = logRepository.count();
        return new PageResult<>(logs, total, page, size);
    }

    public PageResult<LogActividad> listarPorEmail(String email, int page, int size) {
        List<LogActividad> logs = logRepository.findByEmail(email, page, size);
        long total = logRepository.count();
        return new PageResult<>(logs, total, page, size);
    }

    public PageResult<LogActividad> listarPorAccion(String accion, int page, int size) {
        List<LogActividad> logs = logRepository.findByAccion(accion, page, size);
        long total = logRepository.count();
        return new PageResult<>(logs, total, page, size);
    }
}
