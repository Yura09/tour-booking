package com.touramigo.service.entity.partner;

import com.touramigo.service.entity.supplier.SupplierConnection;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ConnectionValue implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "value")
    private String value;

    @ManyToOne
    private Partner partner;

    @ManyToOne
    private SupplierConnection supplierConnection;

}
