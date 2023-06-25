package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class CreateAssignedProductModel {

    private UUID productId;

    private List<UUID> permissionIds;

}
