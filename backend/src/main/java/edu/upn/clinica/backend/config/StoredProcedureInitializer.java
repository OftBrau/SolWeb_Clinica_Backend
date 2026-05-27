package edu.upn.clinica.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class StoredProcedureInitializer {

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void initStoredProcedures() {
        try {
            var resource = new ClassPathResource("stored-procedures.sql");
            if (resource.exists()) {
                try (var conn = dataSource.getConnection()) {
                    ScriptUtils.executeSqlScript(conn,
                            new org.springframework.core.io.support.EncodedResource(resource),
                            false, true, new String[]{"--"}, "$$", null, null);
                }
            }
        } catch (Exception e) {
            System.err.println(">>> [INIT] No se pudieron cargar stored procedures: " + e.getMessage());
        }
    }
}
