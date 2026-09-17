package com.tax.vat.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.vat.dto.response.DocumentResponse;
import com.tax.vat.entity.Company;
import com.tax.vat.exception.ResourceNotFoundException;
import com.tax.vat.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CompanyDocumentService {

    private final CompanyRepository companyRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public CompanyDocumentService(CompanyRepository companyRepository,
                                  FileStorageService fileStorageService,
                                  ObjectMapper objectMapper) {
        this.companyRepository = companyRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocuments(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with slug: " + slug));

        return parseDocuments(company.getDocument());
    }

    @Transactional
    public List<DocumentResponse> uploadDocument(String slug, String title, String expiryDate, MultipartFile file) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with slug: " + slug));

        String filePath = fileStorageService.storeFile(file, "companies/documents");
        List<DocumentResponse> docs = parseDocuments(company.getDocument());

        String docId = UUID.randomUUID().toString();
        String originalName = file.getOriginalFilename();
        String mimeType = file.getContentType();
        Long size = file.getSize();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        DocumentResponse newDoc = new DocumentResponse(
                docId,
                title != null ? title : originalName,
                originalName,
                "/api/v1/files/" + filePath,
                mimeType,
                size,
                expiryDate,
                now
        );
        docs.add(newDoc);

        saveDocuments(company, docs);
        return docs;
    }

    @Transactional
    public List<DocumentResponse> deleteDocument(String slug, String documentId) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with slug: " + slug));

        List<DocumentResponse> docs = parseDocuments(company.getDocument());
        docs.removeIf(d -> d.getId().equals(documentId));

        saveDocuments(company, docs);
        return docs;
    }

    private List<DocumentResponse> parseDocuments(String json) {
        if (json == null || json.trim().isEmpty()) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<DocumentResponse>>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private void saveDocuments(Company company, List<DocumentResponse> docs) {
        try {
            String json = objectMapper.writeValueAsString(docs);
            company.setDocument(json);
            companyRepository.save(company);
        } catch (Exception e) {
            throw new RuntimeException("Error saving documents JSON: " + e.getMessage(), e);
        }
    }
}
