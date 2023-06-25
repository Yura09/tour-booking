package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.PermissionGroup;
import com.touramigo.service.entity.global_config.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class ModifyPermissionModel {
    private boolean isActive;
    private String name;
    private String description;
    private String codeKey;
    private UUID productId;
    private UUID groupId;
}
