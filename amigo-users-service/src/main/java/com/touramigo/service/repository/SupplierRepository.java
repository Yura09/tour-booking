package com.touramigo.service.repository;

import com.touramigo.service.entity.supplier.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findById(UUID id);

    Optional<Supplier> findByCodeVariable(String code);

    List<Supplier> findAllByEnabledTrue();

    @Query("select (count(supplier) > 0) from Supplier supplier " +
            "where upper(supplier.name) = upper(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("select (count(supplier) > 0) from Supplier supplier " +
            "where upper(supplier.codeVariable) = upper(:codeVariable)")
    boolean existsByCodeVariableIgnoreCase(@Param("codeVariable") String name);

    @Query("select (count(supplier) > 0) from Supplier supplier " +
            "where upper(supplier.name) = upper(:name)" +
            "  and supplier.id <> :id")
    boolean existsByNameIgnoreCaseAndIdNot(@Param("name") String name,
                                           @Param("id") UUID id);

    @Query("select (count(supplier) > 0) from Supplier supplier " +
            "where upper(supplier.codeVariable) = upper(:codeVariable)" +
            "  and supplier.id <> :id")
    boolean existsByCodeVariableIgnoreCaseAndIdNot(@Param("codeVariable") String name,
                                                   @Param("id") UUID id);


}