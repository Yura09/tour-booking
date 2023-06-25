package com.touramigo.service.controller;

import com.touramigo.service.entity.global_config.PermissionGroup;
import com.touramigo.service.model.ModifyPermissionModel;
import com.touramigo.service.model.PermissionGroupModel;
import com.touramigo.service.model.PermissionModel;
import com.touramigo.service.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.touramigo.service.entity.enumeration.Permission.Authority.*;

@RestController
@RequestMapping("/permissions")

public class PermissionsController {

    @Autowired
    private PermissionService permissionService;

    @GetMapping
    @PreAuthorize(ADMINISTRATE_GLOBAL + OR + ADMINISTRATE_PARTNERS+ OR + MANAGE_USERS)
    public List<PermissionModel> getAllPermissions(@RequestParam("product") Optional<UUID> productId){
        if(productId.isPresent())
            return permissionService.getAllPermissionsFromProduct(productId.get());

        return permissionService.getAllPermissions();
    }


    @PutMapping("/{id}")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public PermissionModel updatePermission(@PathVariable("id") UUID id, @RequestBody ModifyPermissionModel model) {
        return permissionService.updatePermission(id, model);
    }

    @PutMapping("/{id}/groups/{groupId}")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public PermissionModel assignPermissionGroup(@PathVariable("id") UUID permissionId, @PathVariable("groupId") UUID groupId) {
        return permissionService.assignPermissionGroup(permissionId, groupId);
    }

    @GetMapping("/groups")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public List<PermissionGroupModel> getAllPermissionGroups() {
        return permissionService.getAllPermissionGroups();
    }

    @PostMapping("/groups")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public ResponseEntity<PermissionGroup> createPermissionGroup(@RequestBody String name) {
        PermissionGroup permissionGroup = permissionService.createPermissionGroup(name);
        return ResponseEntity.ok(permissionGroup);
    }

    @DeleteMapping("/groups/{id}")
    @PreAuthorize(ADMINISTRATE_GLOBAL)
    public ResponseEntity<Void> deletePermissionGroup(@PathVariable("id") UUID id) {
        permissionService.deletePermissionGroup(id);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
