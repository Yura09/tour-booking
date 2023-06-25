package com.touramigo.service.service;

import com.touramigo.service.entity.global_config.PermissionGroup;
import com.touramigo.service.model.ModifyPermissionModel;
import com.touramigo.service.model.PermissionGroupModel;
import com.touramigo.service.model.PermissionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface PermissionService {
    List<PermissionModel> getAllPermissions();

    List<PermissionGroupModel> getAllPermissionGroups();

    @Transactional
    PermissionGroup createPermissionGroup(String name);
    
    List<PermissionModel> getAllPermissionsFromProduct(UUID productId);
    void deletePermissionGroup(UUID id);

    PermissionModel assignPermissionGroup(UUID permissionId, UUID groupId);

    @Transactional
    PermissionModel updatePermission(UUID id, ModifyPermissionModel model);
}
