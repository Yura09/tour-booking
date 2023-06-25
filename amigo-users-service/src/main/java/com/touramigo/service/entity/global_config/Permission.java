package com.touramigo.service.entity.global_config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "permission", schema = "users")
@Data
@NoArgsConstructor
public class Permission implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "active")
    private boolean isActive;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "codekey")
    private String codeKey;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToOne
    @JoinColumn(name = "group_id")
    private PermissionGroup group;

    @Column(name = "master")
    private boolean isMaster;
}
