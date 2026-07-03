package edu.upn.clinica.backend.perfil.service;

import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CvParserService {

    private final Tika tika = new Tika();
    private final RestTemplate rest = new RestTemplate();

    public Map<String, Object> parsear(String fileUrl) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("tituloProfesional", "");
        result.put("universidad", "");
        result.put("biografia", "");
        result.put("habilidades", List.of());
        result.put("experiencias", List.of());

        try {
            byte[] bytes = rest.getForObject(fileUrl, byte[].class);
            if (bytes == null || bytes.length == 0) return result;

            String text = tika.parseToString(new ByteArrayInputStream(bytes));
            if (text == null || text.isBlank()) return result;

            // --- Email ---
            Pattern emailPat = Pattern.compile("[\\w.%+-]+@[\\w.-]+\\.[A-Za-z]{2,}");
            Matcher em = emailPat.matcher(text);
            if (em.find()) result.put("email", em.group());

            // --- Telefono Peru ---
            Pattern telPat = Pattern.compile("(\\+?51)?[ -]?9[0-9]{8}");
            Matcher tm = telPat.matcher(text);
            if (tm.find()) result.put("telefono", tm.group().trim());

            // --- LinkedIn ---
            Pattern linkedinPat = Pattern.compile("linkedin\\.com/in/[\\w-]+");
            Matcher lm = linkedinPat.matcher(text);
            if (lm.find()) result.put("linkedinUrl", "https://www." + lm.group());

            // --- Separar texto en lineas ---
            String[] lines = text.split("\\r?\\n");
            List<String> cleanLines = new ArrayList<>();
            for (String line : lines) {
                String l = line.trim();
                if (!l.isEmpty()) cleanLines.add(l);
            }

            // --- Buscar universidad (lineas que contengan "universidad", "university", "univ.") ---
            for (String l : cleanLines) {
                String lower = l.toLowerCase();
                if ((lower.contains("universidad") || lower.contains("university") || lower.contains("univ"))
                        && l.length() < 100) {
                    // Limpiar: quitar prefijos como "Universidad" si estan al inicio
                    result.put("universidad", l.replaceAll("(?i)^(universidad|university|univ\\.?)\\s*", "").trim());
                    break;
                }
            }

            // --- Buscar titulo profesional: primeras lineas despues de "perfil" o "profesional" ---
            for (int i = 0; i < cleanLines.size(); i++) {
                String l = cleanLines.get(i).toLowerCase();
                if (l.contains("perfil") || l.contains("profesional") || l.contains("ingeniero") || l.contains("licenciado") || l.contains("bachiller") || l.contains("técnico")) {
                    if (i + 1 < cleanLines.size() && cleanLines.get(i + 1).length() > 10) {
                        result.put("tituloProfesional", cleanLines.get(i + 1));
                    } else if (cleanLines.get(i).length() > 10) {
                        result.put("tituloProfesional", cleanLines.get(i));
                    }
                    break;
                }
            }

            // --- Buscar secciones ---
            List<String> habilidades = new ArrayList<>();
            List<Map<String, String>> experiencias = new ArrayList<>();
            String currentSection = "";
            String currentExp = "";

            for (String l : cleanLines) {
                String lower = l.toLowerCase().trim();

                // Detectar secciones
                if (lower.startsWith("habilidades") || lower.startsWith("skills") || lower.startsWith("competencias") || lower.startsWith("conocimientos")) {
                    currentSection = "habilidades";
                    continue;
                }
                if (lower.startsWith("experiencia") || lower.startsWith("work experience") || lower.startsWith("professional experience")) {
                    currentSection = "experiencia";
                    continue;
                }
                if (lower.startsWith("educación") || lower.startsWith("education") || lower.startsWith("formación")) {
                    currentSection = "educacion";
                    continue;
                }
                if (lower.startsWith("resumen") || lower.startsWith("summary") || lower.startsWith("perfil profesional") || lower.startsWith("profile")) {
                    currentSection = "resumen";
                    continue;
                }

                if ("resumen".equals(currentSection) && l.length() > 20 && !lower.startsWith("http")) {
                    result.put("biografia", l);
                    currentSection = "";
                    continue;
                }

                if ("habilidades".equals(currentSection)) {
                    String[] parts = l.split("[,\\n|]");
                    for (String p : parts) {
                        String h = p.replaceAll("^[•·\\-*\\d.]+", "").trim();
                        if (h.length() > 1 && !h.toLowerCase().contains("habilidad") && !h.toLowerCase().contains("skill")) {
                            habilidades.add(h);
                        }
                    }
                    if (lower.isEmpty()) currentSection = "";
                    continue;
                }

                if ("experiencia".equals(currentSection)) {
                    if (l.matches(".*\\d{4}.*") || l.contains("-")) {
                        if (!currentExp.isEmpty()) {
                            Map<String, String> exp = new LinkedHashMap<>();
                            exp.put("cargo", currentExp.length() > 50 ? currentExp.substring(0, 50) : currentExp);
                            exp.put("empresa", "");
                            if (!experiencias.contains(exp)) experiencias.add(exp);
                        }
                        currentExp = l;
                    } else if (!currentExp.isEmpty()) {
                        currentExp += " " + l;
                    }
                    if (lower.isEmpty()) {
                        if (!currentExp.isEmpty()) {
                            Map<String, String> exp = new LinkedHashMap<>();
                            exp.put("cargo", currentExp.length() > 50 ? currentExp.substring(0, 50) : currentExp);
                            exp.put("empresa", "");
                            if (!experiencias.contains(exp)) experiencias.add(exp);
                            currentExp = "";
                        }
                    }
                }
            }
            // Si quedo pendiente
            if (!currentExp.isEmpty()) {
                Map<String, String> exp = new LinkedHashMap<>();
                exp.put("cargo", currentExp.length() > 50 ? currentExp.substring(0, 50) : currentExp);
                exp.put("empresa", "");
                if (!experiencias.contains(exp)) experiencias.add(exp);
            }

            result.put("habilidades", habilidades.stream().distinct().limit(20).collect(Collectors.toList()));
            result.put("experiencias", experiencias);

        } catch (Exception e) {
            // Si falla el parsing, devolvemos datos vacios
        }
        return result;
    }
}
