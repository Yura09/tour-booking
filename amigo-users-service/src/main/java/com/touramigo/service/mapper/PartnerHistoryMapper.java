package com.touramigo.service.mapper;

import com.touramigo.service.entity.history.PartnerHistory;
import com.touramigo.service.entity.user.User;
import com.touramigo.service.model.history.PartnerHistoryViewModel;
import com.touramigo.service.repository.UserRepository;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PartnerHistoryMapper {
    private final UserRepository userRepository;

    public void copyFieldsFromTo(@NotNull PartnerHistory from,
                                 @NonNull PartnerHistoryViewModel to) {
        to.setFullName(getCreatorFullName(from.getModifiedBy()));
    }

    private String getCreatorFullName(UUID userId) {
        return userId == null
            ? null
            : userRepository.findById(userId).map(User::getFullName).orElse(null);
    }
}
