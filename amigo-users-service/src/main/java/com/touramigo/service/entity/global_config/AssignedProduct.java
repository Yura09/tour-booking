package com.touramigo.service.entity.global_config;

import java.util.Set;

public interface AssignedProduct  {
    Product getProduct();

    Set<Permission> getPermissions();
}
