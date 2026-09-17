package com.tax.vat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.vat.dto.projection.UserTableProjection;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.request.UserCreateRequest;
import com.tax.vat.dto.request.UserUpdateRequest;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.UserResponse;
import com.tax.vat.entity.*;
import com.tax.vat.exception.BadRequestException;
import com.tax.vat.exception.ResourceNotFoundException;
import com.tax.vat.helper.NIDValidator;
import com.tax.vat.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CompanyRepository companyRepository;
    private final CompanyBranchRepository branchRepository;
    private final DesignationRepository designationRepository;
    private final DepartmentRepository departmentRepository;
    private final FileStorageService fileStorageService;
    private final ObjectMapper objectMapper;

    public UserService(UserRepository userRepository,
                       GroupRepository groupRepository,
                       CompanyRepository companyRepository,
                       CompanyBranchRepository branchRepository,
                       DesignationRepository designationRepository,
                       DepartmentRepository departmentRepository,
                       FileStorageService fileStorageService,
                       ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
        this.companyRepository = companyRepository;
        this.branchRepository = branchRepository;
        this.designationRepository = designationRepository;
        this.departmentRepository = departmentRepository;
        this.fileStorageService = fileStorageService;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public DataTableResponse<UserTableProjection> loadDataTable(DataTableRequest request, Long companyId) {
        int length = (request.getLength() != null && request.getLength() > 0) ? request.getLength() : 10;
        int start = (request.getStart() != null && request.getStart() >= 0) ? request.getStart() : 0;
        int page = start / length;

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(request.getSortColumn());
        Pageable pageable = PageRequest.of(page, length, Sort.by(direction, sortProperty));

        String search = (request.getSearchValue() != null) ? request.getSearchValue().trim() : "";

        Page<UserTableProjection> resultPage = search.isEmpty()
                ? userRepository.findActiveUsers(companyId, pageable)
                : userRepository.searchActiveUsers(companyId, search, pageable);

        long total = userRepository.countTotalUsers(companyId);
        return DataTableResponse.of(request.getDraw(), total, resultPage.getTotalElements(), resultPage.getContent());
    }

    @Transactional(readOnly = true)
    public DataTableResponse<UserTableProjection> loadArchiveDataTable(DataTableRequest request, Long companyId) {
        int length = (request.getLength() != null && request.getLength() > 0) ? request.getLength() : 10;
        int start = (request.getStart() != null && request.getStart() >= 0) ? request.getStart() : 0;
        int page = start / length;

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortProperty = resolveSortProperty(request.getSortColumn());
        Pageable pageable = PageRequest.of(page, length, Sort.by(direction, sortProperty));

        String search = (request.getSearchValue() != null) ? request.getSearchValue().trim() : "";

        Page<UserTableProjection> resultPage = search.isEmpty()
                ? userRepository.findArchivedUsers(companyId, pageable)
                : userRepository.searchArchivedUsers(companyId, search, pageable);

        long total = resultPage.getTotalElements();
        return DataTableResponse.of(request.getDraw(), total, resultPage.getTotalElements(), resultPage.getContent());
    }

    private String resolveSortProperty(Integer sortColumn) {
        if (sortColumn == null) return "id";
        return switch (sortColumn) {
            case 2 -> "name";
            case 4 -> "designation_id";
            case 5 -> "department_id";
            case 6 -> "username";
            case 7 -> "email";
            case 8 -> "group_id";
            case 10 -> "is_online";
            case 11 -> "status";
            default -> "id";
        };
    }

    @Transactional(readOnly = true)
    public Map<String, Long> getUserStats(Long companyId) {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", userRepository.countTotalUsers(companyId));
        stats.put("online", userRepository.countOnlineUsers(companyId));
        stats.put("active", userRepository.countActiveUsers(companyId));
        return stats;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return UserResponse.fromEntity(user);
    }

    @Transactional
    public UserResponse createUser(UserCreateRequest request, Long actorCompanyId) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already in use: " + request.getUsername());
        }
        if (!NIDValidator.isValid(request.getNid())) {
            throw new BadRequestException(NIDValidator.getErrorMessage());
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername().trim());
        user.setPassword(request.getPassword()); // In production bcrypt hashed
        user.setPasswordChangedAt(LocalDate.now());
        user.setContact(request.getContact());
        user.setNid(request.getNid());
        user.setStatus(request.getStatus());

        Group group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + request.getGroupId()));
        user.setGroup(group);

        if (request.getDesignationId() != null) {
            designationRepository.findById(request.getDesignationId()).ifPresent(user::setDesignation);
        }
        if (request.getDepartmentId() != null) {
            departmentRepository.findById(request.getDepartmentId()).ifPresent(user::setDepartment);
        }

        Long targetCompanyId = actorCompanyId != null ? actorCompanyId : request.getCompanyId();
        if (targetCompanyId != null) {
            companyRepository.findById(targetCompanyId).ifPresent(user::setCompany);
        }
        if (request.getCompanyBranchId() != null) {
            branchRepository.findById(request.getCompanyBranchId()).ifPresent(user::setCompanyBranch);
        }

        try {
            if (request.getAccessCompanyId() != null) {
                user.setAccessCompanyId(objectMapper.writeValueAsString(request.getAccessCompanyId()));
            }
            if (request.getAccessCompanyBranchId() != null) {
                user.setAccessCompanyBranchId(objectMapper.writeValueAsString(request.getAccessCompanyBranchId()));
            }
        } catch (Exception ignored) {
        }

        if (request.getPictureFile() != null && !request.getPictureFile().isEmpty()) {
            user.setPicture(fileStorageService.storeFile(request.getPictureFile(), "users"));
        }

        User saved = userRepository.save(user);
        return UserResponse.fromEntity(saved);
    }

    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request, Long actorCompanyId) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        if (userRepository.existsByEmailAndIdNot(request.getEmail(), id)) {
            throw new BadRequestException("Email already in use: " + request.getEmail());
        }
        if (userRepository.existsByUsernameAndIdNot(request.getUsername(), id)) {
            throw new BadRequestException("Username already in use: " + request.getUsername());
        }
        if (!NIDValidator.isValid(request.getNid())) {
            throw new BadRequestException(NIDValidator.getErrorMessage());
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername().trim());
        user.setContact(request.getContact());
        user.setNid(request.getNid());
        user.setStatus(request.getStatus());

        if (request.getPassword() != null && !request.getPassword().trim().isEmpty()) {
            user.setPassword(request.getPassword());
            user.setPasswordChangedAt(LocalDate.now());
        }

        if (request.getGroupId() != null) {
            Group group = groupRepository.findById(request.getGroupId())
                    .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + request.getGroupId()));
            user.setGroup(group);
        }

        if (request.getDesignationId() != null) {
            designationRepository.findById(request.getDesignationId()).ifPresent(user::setDesignation);
        } else {
            user.setDesignation(null);
        }

        if (request.getDepartmentId() != null) {
            departmentRepository.findById(request.getDepartmentId()).ifPresent(user::setDepartment);
        } else {
            user.setDepartment(null);
        }

        Long targetCompanyId = actorCompanyId != null ? actorCompanyId : request.getCompanyId();
        if (targetCompanyId != null) {
            companyRepository.findById(targetCompanyId).ifPresent(user::setCompany);
        } else {
            user.setCompany(null);
        }

        if (request.getCompanyBranchId() != null) {
            branchRepository.findById(request.getCompanyBranchId()).ifPresent(user::setCompanyBranch);
        } else {
            user.setCompanyBranch(null);
        }

        if (request.getPictureFile() != null && !request.getPictureFile().isEmpty()) {
            fileStorageService.deleteFile(user.getPicture());
            user.setPicture(fileStorageService.storeFile(request.getPictureFile(), "users"));
        }

        User updated = userRepository.save(user);
        return UserResponse.fromEntity(updated);
    }

    @Transactional
    public void softDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void restore(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        user.setDeletedAt(null);
        userRepository.save(user);
    }

    @Transactional
    public void forceDelete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        fileStorageService.deleteFile(user.getPicture());
        userRepository.delete(user);
    }
}
