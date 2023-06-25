package com.touramigo.service.model;


import com.touramigo.service.entity.user.User;
import lombok.*;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountManagerModel {

    @NonNull
    private UUID userId;

    private String userName;

    private String email;

    public static AccountManagerModel create(User entity) {
        AccountManagerModel model = new AccountManagerModel();

        if (Objects.nonNull(entity.getUserName())) {
            model.setUserName(entity.getUserName());
            model.setUserId(entity.getId());
            model.setEmail(entity.getEmail());
        }

        return model;
    }
}
