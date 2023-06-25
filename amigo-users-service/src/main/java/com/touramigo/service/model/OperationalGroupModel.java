package com.touramigo.service.model;

import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.partner.OperationalGroup;
import com.touramigo.service.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OperationalGroupModel {
    private UUID id;

    private UUID partnerId;

    private String name;

    private List<String> usernames;

    private List<Permission> permissions;

    public static OperationalGroupModel create(OperationalGroup oGroup){
        OperationalGroupModel oGroupModel = new OperationalGroupModel();
        oGroupModel.setId(oGroup.getId());
        oGroupModel.setPartnerId(oGroup.getPartner().getId());
        oGroupModel.setName(oGroup.getName());
        if (Objects.nonNull(oGroup.getUsers())) {
            oGroupModel.setUsernames(oGroup.getUsers().stream().map(User::getUserName).
                    collect(Collectors.toList()));
        } else {
            oGroupModel.setUsernames(new ArrayList<>());
        }

        oGroupModel.setPermissions(oGroup.getPermissions());
        return oGroupModel;
    }
}
