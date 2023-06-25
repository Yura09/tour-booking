package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.PermissionGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;
//Springboot annotation
//lombok
//hibernate-jpa
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionModel {
    private UUID id;

    private boolean isActive;

    private String name;

    private String description;

    private String codeKey;

    private ProductModel product;

    private PermissionGroupModel group;

    private boolean isMaster;

    /**
     * Mapping from Entity --> Model
     * (mapping from database to GUI)
     * @param permission
     * @return
     */
    public static PermissionModel create(Permission permission){
        PermissionModel permissionModel = new PermissionModel();

        permissionModel.setId(permission.getId());
        permissionModel.setActive(permission.isActive());
        permissionModel.setName(permission.getName());
        permissionModel.setDescription(permission.getDescription());
        permissionModel.setCodeKey(permission.getCodeKey());
        permissionModel.setProduct(ProductModel.create(permission.getProduct()));
        permissionModel.setGroup(PermissionGroupModel.create(permission.getGroup()));
        permissionModel.setMaster(permission.isMaster());

        return permissionModel;
    }
}