package com.tax.vat.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tax.vat.dto.request.DataTableRequest;
import com.tax.vat.dto.request.GroupCreateRequest;
import com.tax.vat.dto.request.GroupUpdateRequest;
import com.tax.vat.dto.response.DataTableResponse;
import com.tax.vat.dto.response.GroupResponse;
import com.tax.vat.entity.Group;
import com.tax.vat.entity.GroupAccess;
import com.tax.vat.exception.BadRequestException;
import com.tax.vat.exception.ResourceNotFoundException;
import com.tax.vat.helper.SlugGenerator;
import com.tax.vat.repository.GroupAccessRepository;
import com.tax.vat.repository.GroupRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupAccessRepository groupAccessRepository;
    private final ObjectMapper objectMapper;

    public GroupService(GroupRepository groupRepository,
                        GroupAccessRepository groupAccessRepository,
                        ObjectMapper objectMapper) {
        this.groupRepository = groupRepository;
        this.groupAccessRepository = groupAccessRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional(readOnly = true)
    public DataTableResponse<GroupResponse> loadDataTable(DataTableRequest request) {
        int page = request.getStart() / request.getLength();
        int size = request.getLength();

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));

        Page<Group> resultPage = groupRepository.findActiveGroups(request.getSearchValue(), pageable);
        List<GroupResponse> dtos = resultPage.getContent().stream()
                .map(g -> {
                    GroupResponse res = GroupResponse.fromEntity(g);
                    res.setUsersCount(groupRepository.countActiveUsers(g.getId()));
                    return res;
                })
                .collect(Collectors.toList());

        long total = groupRepository.countActive();
        return DataTableResponse.of(request.getDraw(), total, resultPage.getTotalElements(), dtos);
    }

    @Transactional(readOnly = true)
    public DataTableResponse<GroupResponse> loadArchiveDataTable(DataTableRequest request) {
        int page = request.getStart() / request.getLength();
        int size = request.getLength();

        Sort.Direction direction = "asc".equalsIgnoreCase(request.getSortDirection()) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, "id"));

        Page<Group> resultPage = groupRepository.findArchivedGroups(request.getSearchValue(), pageable);
        List<GroupResponse> dtos = resultPage.getContent().stream()
                .map(g -> {
                    GroupResponse res = GroupResponse.fromEntity(g);
                    res.setUsersCount(groupRepository.countAssignedUsers(g.getId()));
                    return res;
                })
                .collect(Collectors.toList());

        long total = groupRepository.countArchived();
        return DataTableResponse.of(request.getDraw(), total, resultPage.getTotalElements(), dtos);
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupById(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
        GroupResponse res = GroupResponse.fromEntity(group);
        res.setUsersCount(groupRepository.countActiveUsers(group.getId()));
        return res;
    }

    @Transactional(readOnly = true)
    public GroupResponse getGroupBySlug(String slug) {
        Group group = groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));
        GroupResponse res = GroupResponse.fromEntity(group);
        res.setUsersCount(groupRepository.countActiveUsers(group.getId()));
        return res;
    }

    @Transactional(readOnly = true)
    @Cacheable("groups")
    public List<GroupResponse> getAllActiveGroups() {
        return groupRepository.findByDeletedAtIsNullOrderByNameAsc().stream()
                .map(GroupResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public GroupResponse createGroup(GroupCreateRequest request) {
        if (groupRepository.existsByName(request.getName())) {
            throw new BadRequestException("Group name already exists: " + request.getName());
        }

        Group group = new Group();
        group.setName(request.getName());
        group.setSlug(SlugGenerator.generateSlug(request.getName()));
        group.setDescription(request.getDescription());
        group.setIsAdmin(request.getIsAdmin() != null ? request.getIsAdmin() : false);

        if (request.getParentGroupId() != null) {
            groupRepository.findById(request.getParentGroupId()).ifPresent(group::setParent);
        }

        Group saved = groupRepository.save(group);
        return GroupResponse.fromEntity(saved);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public GroupResponse updateGroup(Long id, GroupUpdateRequest request) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));

        if (groupRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new BadRequestException("Group name already exists: " + request.getName());
        }

        group.setName(request.getName());
        group.setDescription(request.getDescription());

        if (request.getParentGroupId() != null && !request.getParentGroupId().equals(id)) {
            groupRepository.findById(request.getParentGroupId()).ifPresent(group::setParent);
        } else {
            group.setParent(null);
        }

        Group updated = groupRepository.save(group);
        return GroupResponse.fromEntity(updated);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public void deleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));

        if (id == 1 || Boolean.TRUE.equals(group.getIsAdmin())) {
            throw new BadRequestException("Admin or default system groups cannot be deleted.");
        }

        long activeUsers = groupRepository.countActiveUsers(id);
        if (activeUsers > 0) {
            throw new BadRequestException("This group has " + activeUsers + " active user(s). Reassign or remove users first.");
        }

        long activeChildren = groupRepository.countActiveChildren(id);
        if (activeChildren > 0) {
            throw new BadRequestException("This group has " + activeChildren + " child group(s). Reassign or delete them first.");
        }

        group.setDeletedAt(LocalDateTime.now());
        groupRepository.save(group);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public void restoreGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));
        group.setDeletedAt(null);
        groupRepository.save(group);
    }

    @Transactional
    @CacheEvict(value = "groups", allEntries = true)
    public void forceDeleteGroup(Long id) {
        Group group = groupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with ID: " + id));

        if (id == 1 || Boolean.TRUE.equals(group.getIsAdmin())) {
            throw new BadRequestException("Admin or default system groups cannot be deleted.");
        }

        long assignedUsers = groupRepository.countAssignedUsers(id);
        if (assignedUsers > 0) {
            throw new BadRequestException("This group has " + assignedUsers + " assigned user(s) (including archived). Reassign them first.");
        }

        long assignedChildren = groupRepository.countAssignedChildren(id);
        if (assignedChildren > 0) {
            throw new BadRequestException("This group has " + assignedChildren + " child group(s). Delete or reassign them first.");
        }

        groupAccessRepository.deleteByGroupId(id);
        groupRepository.delete(group);
    }

    @Transactional(readOnly = true)
    public List<String> getGroupPermissions(String slug) {
        Group group = groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));

        return groupAccessRepository.findByGroupId(group.getId())
                .map(ga -> {
                    try {
                        return objectMapper.readValue(ga.getGroupAccess(), new TypeReference<List<String>>() {});
                    } catch (Exception e) {
                        return new ArrayList<String>();
                    }
                })
                .orElseGet(ArrayList::new);
    }

    @Transactional
    public void saveGroupPermissions(String slug, List<String> permissions) {
        Group group = groupRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found with slug: " + slug));

        try {
            String json = objectMapper.writeValueAsString(permissions);
            GroupAccess access = groupAccessRepository.findByGroupId(group.getId())
                    .orElse(new GroupAccess(group.getId(), json));
            access.setGroupAccess(json);
            groupAccessRepository.save(access);
        } catch (Exception e) {
            throw new RuntimeException("Error saving group permissions: " + e.getMessage(), e);
        }
    }
}
