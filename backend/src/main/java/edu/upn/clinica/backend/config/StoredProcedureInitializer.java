package edu.upn.clinica.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Component
public class StoredProcedureInitializer {

    private final DataSource dataSource;

    public StoredProcedureInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void initStoredProcedures() {
        List<String> statements = new ArrayList<>();

        try (var is = getClass().getClassLoader().getResourceAsStream("stored-procedures.sql")) {
            if (is == null) {
                System.err.println(">>> [INIT] No se encontró stored-procedures.sql");
                return;
            }

            String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);

            StringBuilder current = new StringBuilder();
            for (String line : content.split("\n")) {
                String trimmed = line.trim();
                if (trimmed.endsWith("$$")) {
                    current.append(line, 0, line.lastIndexOf("$$"));
                    current.append("\n");
                    statements.add(current.toString());
                    current = new StringBuilder();
                } else if (trimmed.startsWith("--") || trimmed.isEmpty()) {
                    if (current.length() > 0) current.append(line).append("\n");
                } else {
                    current.append(line).append("\n");
                }
            }
            if (!current.isEmpty()) {
                statements.add(current.toString());
            }

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                for (String sql : statements) {
                    if (!sql.isBlank()) {
                        stmt.execute(sql);
                    }
                }
                System.out.println(">>> [INIT] Stored procedures cargados exitosamente (" + statements.size() + ")");
            }

        } catch (Exception e) {
            System.err.println(">>> [INIT] No se pudieron cargar stored procedures: " + e.getMessage());
        }
    }
}
