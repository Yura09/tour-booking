package com.touramigo.service.entity.user;

import com.touramigo.service.entity.global_config.AssignedProduct;
import com.touramigo.service.entity.global_config.Permission;
import com.touramigo.service.entity.global_config.Product;
import com.touramigo.service.entity.partner.Partner;
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
public class UserProduct implements Serializable, AssignedProduct {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "up_permission", joinColumns = {@JoinColumn(name = "up_id", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "permission_id", referencedColumnName = "id")})
    private Set<Permission> permissions;
}
