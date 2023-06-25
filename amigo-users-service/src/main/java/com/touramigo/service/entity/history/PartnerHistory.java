package com.touramigo.service.entity.history;

import com.touramigo.service.entity.partner.Partner;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import java.time.ZonedDateTime;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

@Entity
@Getter
@Setter
@RequiredArgsConstructor
@Table(name = "partner_history")
@EqualsAndHashCode(of = "id")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PartnerHistory {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @Column(name = "modified_by", nullable = false)
    private UUID modifiedBy;

    @Column(name = "date_log", nullable = false)
    private ZonedDateTime dateLog = ZonedDateTime.now();

    @ManyToOne
    @JoinColumn(name = "partner_id")
    private Partner partner;

    @Type(type = "jsonb")
    @Column(name = "data")
    private Object data;
}
