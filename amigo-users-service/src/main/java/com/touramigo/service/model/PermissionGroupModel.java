package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionGroupModel {
    private UUID id;
    private String name;

    public static PermissionGroupModel create(PermissionGroup permissionGroup){
        PermissionGroupModel permissionGroupModel = new PermissionGroupModel();

        permissionGroupModel.setId(permissionGroup.getId());
        permissionGroupModel.setName(permissionGroup.getName());
        return permissionGroupModel;
    }
}

