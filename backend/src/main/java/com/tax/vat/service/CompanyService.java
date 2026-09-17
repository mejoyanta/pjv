package com.tax.vat.service;

import com.tax.vat.dto.projection.CompanyTableProjection;
import com.tax.vat.dto.request.CompanyCreateRequest;
import com.tax.vat.dto.request.CompanyUpdateRequest;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.response.CompanyResponse;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.entity.Company;
import com.tax.vat.entity.CompanyBranch;
import com.tax.vat.entity.CompanyCategory;
import com.tax.vat.entity.UsernameChangeLog;
import com.tax.vat.exception.BadRequestException;
import com.tax.vat.exception.ResourceNotFoundException;
import com.tax.vat.helper.CompanyIdFormat;
import com.tax.vat.helper.SlugGenerator;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyCategoryRepository;
import com.tax.vat.repository.CompanyRepository;
import com.tax.vat.repository.UsernameChangeLogRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyCategoryRepository categoryRepository;
    private final CompanyBranchRepository branchRepository;
    private final UsernameChangeLogRepository changeLogRepository;
    private final FileStorageService fileStorageService;
    private final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();

    public CompanyService(CompanyRepository companyRepository,
                          CompanyCategoryRepository categoryRepository,
                          CompanyBranchRepository branchRepository,
                          UsernameChangeLogRepository changeLogRepository,
                          FileStorageService fileStorageService) {
        this.companyRepository = companyRepository;
        this.categoryRepository = categoryRepository;
        this.branchRepository = branchRepository;
        this.changeLogRepository = changeLogRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * High-performance active companies listing with DTO projection.
     */
    @Transactional(readOnly = true)
    public DataTableResponse<CompanyTableProjection> loadDataTable(DataTableRequest request) {
        int length = (request.getLength() != null && request.getLength() > 0) ? request.getLength() : 10;
        int start = (request.getStart() != null && request.getStart() >= 0) ? request.getStart() : 0;
        int page = start / length;

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(request.getSortColumn());
        Pageable pageable = PageRequest.of(page, length, Sort.by(direction, sortProperty));

        String search = (request.getSearchValue() != null) ? request.getSearchValue().trim() : "";

        Page<CompanyTableProjection> resultPage = search.isEmpty()
                ? companyRepository.findActiveCompanies(pageable)
                : companyRepository.searchActiveCompanies(search, pageable);

        long totalCount = companyRepository.countActive();
        return DataTableResponse.of(request.getDraw(), totalCount, resultPage.getTotalElements(), resultPage.getContent());
    }

    /**
     * High-performance archive companies listing.
     */
    @Transactional(readOnly = true)
    public DataTableResponse<CompanyTableProjection> loadArchiveDataTable(DataTableRequest request) {
        int length = (request.getLength() != null && request.getLength() > 0) ? request.getLength() : 10;
        int start = (request.getStart() != null && request.getStart() >= 0) ? request.getStart() : 0;
        int page = start / length;

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(request.getSortColumn());
        Pageable pageable = PageRequest.of(page, length, Sort.by(direction, sortProperty));

        String search = (request.getSearchValue() != null) ? request.getSearchValue().trim() : "";

        Page<CompanyTableProjection> resultPage = search.isEmpty()
                ? companyRepository.findArchivedCompanies(pageable)
                : companyRepository.searchArchivedCompanies(search, pageable);

        long totalCount = companyRepository.countArchived();
        return DataTableResponse.of(request.getDraw(), totalCount, resultPage.getTotalElements(), resultPage.getContent());
    }

    private String resolveSortProperty(Integer sortColumn) {
        if (sortColumn == null) return "id";
        return switch (sortColumn) {
            case 1 -> "name";
            case 2 -> "bin";
            case 3 -> "email";
            case 4 -> "phone";
            case 5 -> "username";
            default -> "id";
        };
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyBySlug(String slug) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with slug: " + slug));
        return CompanyResponse.fromEntity(company);
    }

    @Transactional(readOnly = true)
    public CompanyResponse getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        return CompanyResponse.fromEntity(company);
    }

    @Transactional
    public CompanyResponse createCompany(CompanyCreateRequest request) {
        // Validate Uniqueness
        if (companyRepository.existsByBin(request.getBin())) {
            throw new BadRequestException("BIN number already exists: " + request.getBin());
        }
        if (companyRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        String normalizedUsername = CompanyIdFormat.normalize(request.getUsername());
        if (!CompanyIdFormat.matches(normalizedUsername)) {
            throw new BadRequestException(CompanyIdFormat.validationMessage());
        }
        if (companyRepository.existsByUsername(normalizedUsername)) {
            throw new BadRequestException("Company ID / Username already exists: " + normalizedUsername);
        }

        CompanyCategory category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));

        Company company = new Company();
        mapRequestToCompany(request, company);
        company.setCategory(category);
        company.setUsername(normalizedUsername);
        company.setSlug(SlugGenerator.generateSlug(request.getName()));

        // File uploads
        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            company.setLogo(fileStorageService.storeFile(request.getLogoFile(), "companies/logo"));
        }
        if (request.getCompanySealFile() != null && !request.getCompanySealFile().isEmpty()) {
            company.setCompanySeal(fileStorageService.storeFile(request.getCompanySealFile(), "companies/seal"));
        }
        if (request.getCompanyPadFile() != null && !request.getCompanyPadFile().isEmpty()) {
            company.setCompanyPad(fileStorageService.storeFile(request.getCompanyPadFile(), "companies/pad"));
        }

        Company savedCompany = companyRepository.save(company);

        // Automatically create main company branch
        CompanyBranch mainBranch = new CompanyBranch();
        mainBranch.setCompanyId(savedCompany.getId());
        mainBranch.setCategoryId(savedCompany.getCategory() != null ? savedCompany.getCategory().getId() : 1L);
        mainBranch.setPassword(savedCompany.getPassword());
        mainBranch.setStatus("active");
        mainBranch.setName(savedCompany.getName() + " (Main Branch)");
        mainBranch.setSlug(SlugGenerator.generateSlug(savedCompany.getName() + "-main"));
        mainBranch.setUsername(savedCompany.getUsername());
        mainBranch.setIsMain(true);
        mainBranch.setEmail(savedCompany.getEmail());
        mainBranch.setPhone(savedCompany.getPhone());
        mainBranch.setAddress(savedCompany.getAddress());
        branchRepository.save(mainBranch);

        return CompanyResponse.fromEntity(savedCompany);
    }

    @Transactional
    public CompanyResponse updateCompany(String slug, CompanyUpdateRequest request, String clientIp) {
        Company company = companyRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with slug: " + slug));

        // Validate uniqueness excluding current record
        if (companyRepository.existsByBinAndIdNot(request.getBin(), company.getId())) {
            throw new BadRequestException("BIN number already exists: " + request.getBin());
        }
        if (companyRepository.existsByEmailAndIdNot(request.getEmail(), company.getId())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        String oldUsername = company.getUsername();
        String newUsername = CompanyIdFormat.normalize(request.getUsername());

        if (!oldUsername.equalsIgnoreCase(newUsername)) {
            if (!CompanyIdFormat.matches(newUsername)) {
                throw new BadRequestException(CompanyIdFormat.validationMessage());
            }
            if (companyRepository.existsByUsernameAndIdNot(newUsername, company.getId())) {
                throw new BadRequestException("Company ID already in use: " + newUsername);
            }

            // Log username change
            UsernameChangeLog changeLog = new UsernameChangeLog(
                    company,
                    oldUsername,
                    newUsername,
                    null,
                    request.getUsernameChangeReason() != null ? request.getUsernameChangeReason() : "Company ID updated",
                    clientIp
            );
            changeLogRepository.save(changeLog);

            // Sync main branch username
            branchRepository.findByCompanyIdAndIsMainTrue(company.getId()).ifPresent(branch -> {
                branch.setUsername(newUsername);
                branchRepository.save(branch);
            });
        }

        if (request.getCategoryId() != null) {
            CompanyCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with ID: " + request.getCategoryId()));
            company.setCategory(category);
        }

        mapRequestToCompany(request, company);
        company.setUsername(newUsername);

        // Handle File Uploads (replace old files if new ones provided)
        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            fileStorageService.deleteFile(company.getLogo());
            company.setLogo(fileStorageService.storeFile(request.getLogoFile(), "companies/logo"));
        }
        if (request.getCompanySealFile() != null && !request.getCompanySealFile().isEmpty()) {
            fileStorageService.deleteFile(company.getCompanySeal());
            company.setCompanySeal(fileStorageService.storeFile(request.getCompanySealFile(), "companies/seal"));
        }
        if (request.getCompanyPadFile() != null && !request.getCompanyPadFile().isEmpty()) {
            fileStorageService.deleteFile(company.getCompanyPad());
            company.setCompanyPad(fileStorageService.storeFile(request.getCompanyPadFile(), "companies/pad"));
        }

        Company updatedCompany = companyRepository.save(company);
        return CompanyResponse.fromEntity(updatedCompany);
    }

    @Transactional
    public void softDelete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        company.setDeletedAt(LocalDateTime.now());
        companyRepository.save(company);
    }

    @Transactional
    public void restore(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));
        company.setDeletedAt(null);
        companyRepository.save(company);
    }

    @Transactional
    public void forceDelete(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + id));

        // Delete associated files
        fileStorageService.deleteFile(company.getLogo());
        fileStorageService.deleteFile(company.getCompanySeal());
        fileStorageService.deleteFile(company.getCompanyPad());

        companyRepository.delete(company);
    }

    private void mapRequestToCompany(CompanyCreateRequest req, Company c) {
        c.setName(req.getName());
        String rawPassword = (req.getPassword() != null && !req.getPassword().isBlank()) ? req.getPassword() : "12345678";
        c.setPassword(passwordEncoder.encode(rawPassword));
        c.setNameOfEntry(req.getNameOfEntry());
        c.setEmail(req.getEmail());
        c.setCompanyLevel(req.getCompanyLevel());
        c.setBin(req.getBin());
        c.setTin(req.getTin());
        c.setPhone(req.getPhone());
        c.setAltPhone(req.getAltPhone());
        c.setTrustCode(req.getTrustCode());
        c.setEffectiveDate(req.getEffectiveDate());
        c.setSubscriptionExpireDate(req.getSubscriptionExpireDate());
        c.setStatus(req.getStatus());

        c.setContactPersonName(req.getContactPersonName());
        c.setContactPersonPhone(req.getContactPersonPhone());
        c.setContactPersonDesignation(req.getContactPersonDesignation());

        c.setOwnerName(req.getOwnerName());
        c.setOwnerPhone(req.getOwnerPhone());
        c.setOwnerAddress(req.getOwnerAddress());
        c.setOwnershipType(req.getOwnershipType());
        c.setOwnerFatherName(req.getOwnerFatherName());
        c.setOwnerMotherName(req.getOwnerMotherName());
        c.setOwnerDob(req.getOwnerDob());
        c.setOwnerAge(req.getOwnerAge());

        c.setEmergencyContactName(req.getEmergencyContactName());
        c.setEmergencyContactNid(req.getEmergencyContactNid());
        c.setEmergencyContactPassport(req.getEmergencyContactPassport());
        c.setEmergencyContactPhone(req.getEmergencyContactPhone());
        c.setEmergencyContactRelation(req.getEmergencyContactRelation());

        c.setAddress(req.getAddress());
        c.setPresentAddress(req.getPresentAddress());
        c.setPermanentAddress(req.getPermanentAddress());
        c.setVatOfficeAddress(req.getVatOfficeAddress());
        c.setDivisionAddress(req.getDivisionAddress());
        c.setCircleAddress(req.getCircleAddress());

        c.setServiceCharge(req.getServiceCharge() != null ? req.getServiceCharge() : 0.0);
        c.setRentVatCharge(req.getRentVatCharge() != null ? req.getRentVatCharge() : 0.0);
        c.setConsultancyCharge(req.getConsultancyCharge() != null ? req.getConsultancyCharge() : 0.0);

        c.setPadHeadingColor(req.getPadHeadingColor());
        c.setDebitInvoiceHeadingColor(req.getDebitInvoiceHeadingColor());
        c.setCreditInvoiceHeadingColor(req.getCreditInvoiceHeadingColor());
        c.setCommercialInvoiceHeadingColor(req.getCommercialInvoiceHeadingColor());
        c.setProformaInvoiceHeadingColor(req.getProformaInvoiceHeadingColor());
        c.setBillOfLadingInvoiceHeadingColor(req.getBillOfLadingInvoiceHeadingColor());
        c.setPackageListHeadingColor(req.getPackageListHeadingColor());
        c.setPurchaseOrderHeadingColor(req.getPurchaseOrderHeadingColor());

        c.setIsSales(req.getIsSales() != null ? req.getIsSales() : false);
        c.setIsNewDashboard(req.getIsNewDashboard() != null ? req.getIsNewDashboard() : false);
        c.setCompanyIsGold(req.getCompanyIsGold() != null ? req.getCompanyIsGold() : false);
        c.setCompanyIsTobacco(req.getCompanyIsTobacco() != null ? req.getCompanyIsTobacco() : false);
        c.setAllow63PriceType(req.getAllow63PriceType() != null ? req.getAllow63PriceType() : false);
        c.setAllowRevenueShare(req.getAllowRevenueShare() != null ? req.getAllowRevenueShare() : false);
        c.setBkashPayBillEnabled(req.getBkashPayBillEnabled() != null ? req.getBkashPayBillEnabled() : false);
        c.setBkashPayBillReferenceId(req.getBkashPayBillReferenceId());
        c.setStockUpdateDate(req.getStockUpdateDate());
        c.setNotes(req.getNotes());
    }
}
