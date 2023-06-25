package com.touramigo.service.service.impl;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.PermissionGroup;
import com.touramigo.service.exception.InvalidDataException;
import com.touramigo.service.exception.UnexistedDataException;
import com.touramigo.service.model.ModifyPermissionModel;
import com.touramigo.service.model.PermissionGroupModel;
import com.touramigo.service.model.PermissionModel;
import com.touramigo.service.repository.PermissionGroupRepository;
import com.touramigo.service.repository.PermissionRepository;
import com.touramigo.service.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl implements PermissionService {
    private static final UUID PGROUP_OTHER = UUID.fromString("95119f08-8509-4c81-b5e6-8845a9c856cb");

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PermissionGroupRepository pgRepository;

    @Override
    public List<PermissionModel> getAllPermissions() {
        final List<Permission> permissions = permissionRepository.findAll();
        return permissions.stream().map(PermissionModel::create).collect(Collectors.toList());
    }

    @Override
    public List<PermissionGroupModel> getAllPermissionGroups() {
        final List<PermissionGroup> permissionGroups = pgRepository.findAll();
        return permissionGroups.stream().map(PermissionGroupModel::create).collect(Collectors.toList());
    }

    @Override
    public PermissionGroup createPermissionGroup(String name) {
        if (name == null || name.isEmpty()) {
            throw new InvalidDataException("Please provide a valid permission group name");
        } else {
            PermissionGroup permissionGroup = new PermissionGroup();
            permissionGroup.setName(name);
            pgRepository.save(permissionGroup);
            return permissionGroup;
        }
    }

    @Override
    @Transactional
    public void deletePermissionGroup(UUID groupId) {
        if (groupId.equals(PGROUP_OTHER)) {
            throw new InvalidDataException("Permission group 'OTHER' cannot be deleted");
        }

        PermissionGroup pGroup = pgRepository.findById(groupId)
                .orElseThrow(() -> new UnexistedDataException("Permission group not found"));

        PermissionGroup defaultGroup = pgRepository.findById(PGROUP_OTHER)
                .orElseThrow(() -> new UnexistedDataException("Permission group not found"));
        /* We now want to fetch all dependent Permissions on this Group
         *   After that, we set it back to default, which is OTHER group
         * */
        List<Permission> permissions = permissionRepository.findByPermissionGroup(groupId);
        permissions.forEach(permission -> permission.setGroup(defaultGroup));

        permissionRepository.save(permissions);
        pgRepository.delete(pGroup);
    }

    public List<PermissionModel> getAllPermissionsFromProduct(UUID productId) {
        final List<Permission> permissions = permissionRepository.findByProductId(productId);
        return permissions.stream().map(PermissionModel::create).collect(Collectors.toList());
    }

    @Override
    public PermissionModel assignPermissionGroup(UUID permissionId, UUID groupId) {
        Permission permissionToBeUpdated = permissionRepository.findById(permissionId).get();

        PermissionGroup pgToBeAssigned = pgRepository.findById(groupId).get();

        permissionToBeUpdated.setGroup(pgToBeAssigned);

        permissionRepository.save(permissionToBeUpdated);
        return PermissionModel.create(permissionToBeUpdated);
    }

    @Override
    @Transactional
    public PermissionModel updatePermission(UUID id, ModifyPermissionModel model) {
        Permission entity = permissionRepository.findById(id).orElseThrow(() -> new UnexistedDataException("Permission not found."));

        if (Objects.nonNull(model.getName())) {
            entity.setName(model.getName());
        }

        if (Objects.nonNull(model.getDescription())) {
            entity.setDescription(model.getDescription());
        }

        return PermissionModel.create(permissionRepository.save(entity));
    }
}
