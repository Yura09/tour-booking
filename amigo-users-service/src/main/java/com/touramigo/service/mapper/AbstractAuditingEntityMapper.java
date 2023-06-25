package com.touramigo.service.mapper;

import com.touramigo.service.entity.global_config.AbstractAuditingEntity;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.model.AbstractAuditingModel;
import com.touramigo.service.repository.UserRepository;
import com.touramigo.service.util.DateUtil;
import java.util.UUID;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AbstractAuditingEntityMapper {

    private final UserRepository userRepository;

    public void copyFieldsFromTo(@NonNull AbstractAuditingEntity from,
                                 @NonNull AbstractAuditingModel to) {
        to.setCreatedBy(from.getCreatedBy());
        to.setLastModifiedBy(getAuditorFullName(from.getLastModifiedBy()));
        to.setLastModifiedDate(DateUtil.render(from.getLastModifiedDate()));
    }

    private String getAuditorFullName(UUID userId) {
        return userId == null
            ? null
            : userRepository.findById(userId).map(User::getFullName).orElse(null);
    }
}
