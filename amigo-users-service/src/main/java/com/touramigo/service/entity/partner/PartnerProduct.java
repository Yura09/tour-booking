package com.touramigo.service.entity.partner;

import com.touramigo.service.entity.global_config.AssignedProduct;
import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.Product;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class PartnerProduct implements Serializable, AssignedProduct {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "pp_permission", joinColumns = {@JoinColumn(name = "pp_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private Set<Permission> permissions;
}
