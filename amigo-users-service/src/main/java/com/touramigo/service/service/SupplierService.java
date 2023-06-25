package com.touramigo.service.service;

import com.touramigo.service.entity.enumeration.SupplierType;
import com.touramigo.service.model.CreateUpdateSupplierModel;
import com.touramigo.service.model.SupplierConnectionModel;
import com.touramigo.service.model.SupplierModel;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

public interface SupplierService {

    List<SupplierModel> getAllSuppliers();

    List<SupplierModel> getActiveSuppliers();

    List<SupplierModel> getPartnerSuppliers(UUID partnerId);

    SupplierModel getSupplierById(UUID id);

    SupplierModel getSupplierByCode(String code);

    List<SupplierType> getAllSupplierTypes();

    @Transactional
    SupplierModel createSupplier(CreateUpdateSupplierModel model);

    @Transactional
    SupplierModel updateSupplier(CreateUpdateSupplierModel model, UUID id);

    List<SupplierConnectionModel> getConnectionsBySupplierId(UUID supplierId);

    @Transactional
    SupplierModel enableOrDisableUser(UUID id);
}
