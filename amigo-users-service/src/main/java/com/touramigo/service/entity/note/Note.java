package com.touramigo.service.entity.note;

import com.touramigo.service.entity.contact.Contact;
import com.touramigo.service.entity.global_config.AbstractAuditingEntity;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import java.io.Serializable;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@ToString
@SQLDelete(sql = "update users.note set deleted = true where id = ?")
public class Note extends AbstractAuditingEntity implements Serializable {
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @ManyToOne
    @JoinColumn(name = "contact_id")
    private Contact contact;

    @Column(name = "rule_id")
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID ruleId;

    @Column(name = "content", nullable = false)
    private String content;
}
