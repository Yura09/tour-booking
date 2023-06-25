package com.touramigo.service.repository;

import com.touramigo.service.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findById(UUID id);

    Optional<User> findByEmail(String email);

    Stream<User> findByPartnerId(UUID partnerId);

    @Query(value = "SELECT u FROM User u WHERE u.partner.master = true")
    List<User> findByMastersPartner();
}
