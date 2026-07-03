package edu.upn.clinica.backend.doctor.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String subirFoto(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "doctores"));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo foto a Cloudinary: " + e.getMessage());
        }
    }

    public String subirArchivo(MultipartFile file, String folder) {
        try {
            String originalName = file.getOriginalFilename();
            String resourceType = "auto";
            if (originalName != null) {
                String lower = originalName.toLowerCase();
                if (lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx"))
                    resourceType = "raw";
                else if (lower.endsWith(".mp4") || lower.endsWith(".webm") || lower.endsWith(".mov"))
                    resourceType = "video";
            }
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", folder, "resource_type", resourceType));
            return (String) result.get("secure_url");
        } catch (Exception e) {
            throw new RuntimeException("Error subiendo archivo a Cloudinary: " + e.getMessage());
        }
    }
}
