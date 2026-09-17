package com.tax.vat.service;

import com.tax.vat.dto.request.CompanyBranchCreateRequest;
import com.tax.vat.entity.Company;
import com.tax.vat.entity.CompanyBranch;
import com.tax.vat.exception.BadRequestException;
import com.tax.vat.exception.ResourceNotFoundException;
import com.tax.vat.helper.SlugGenerator;
import com.tax.vat.repository.CompanyBranchRepository;
import com.tax.vat.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyBranchService {

    private final CompanyBranchRepository branchRepository;
    private final CompanyRepository companyRepository;

    public CompanyBranchService(CompanyBranchRepository branchRepository, CompanyRepository companyRepository) {
        this.branchRepository = branchRepository;
        this.companyRepository = companyRepository;
    }

    @Transactional(readOnly = true)
    public List<CompanyBranch> getBranchesByCompany(Long companyId) {
        return branchRepository.findByCompanyIdAndDeletedAtIsNull(companyId);
    }

    @Transactional
    public CompanyBranch createBranch(CompanyBranchCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with ID: " + request.getCompanyId()));

        CompanyBranch branch = new CompanyBranch();
        branch.setCompanyId(company.getId());
        branch.setCategoryId(company.getCategory() != null ? company.getCategory().getId() : 1L);
        branch.setPassword(company.getPassword());
        branch.setStatus("active");
        branch.setName(request.getName());
        branch.setSlug(SlugGenerator.generateSlug(company.getName() + "-" + request.getName()));
        branch.setUsername(request.getUsername() != null ? request.getUsername() : company.getUsername());
        branch.setPhone(request.getPhone());
        branch.setEmail(request.getEmail());
        branch.setAddress(request.getAddress());
        branch.setIsMain(request.getIsMain() != null ? request.getIsMain() : false);

        return branchRepository.save(branch);
    }
}
