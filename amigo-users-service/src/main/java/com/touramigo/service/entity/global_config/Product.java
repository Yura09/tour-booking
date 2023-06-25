package com.touramigo.service.entity.global_config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "product", schema = "users")
@Data
@NoArgsConstructor
public class Product implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name="name", nullable=false, unique=true)
    private String name;

    @Column(name = "description")
    private String description;

    /**
     * Should this be the same as CodeKey with Permissions ??
     * @author: Quang Van Tran
     */
    @Column(name = "codevariable")
    private String codeVariable;
}
