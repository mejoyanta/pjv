package com.tax.vat.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class StorageConfig {

    @Value("${app.upload.dir:./uploads}")
    private String uploadDir;

    @PostConstruct
    public void init() {
        File baseDir = new File(uploadDir);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }

        String[] subDirs = {"companies/logo", "companies/pad", "companies/seal", "companies/documents", "users"};
        for (String sub : subDirs) {
            File dir = new File(baseDir, sub);
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }
    }

    public String getUploadDir() {
        return uploadDir;
    }
}
