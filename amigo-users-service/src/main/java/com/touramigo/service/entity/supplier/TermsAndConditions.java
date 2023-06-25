package com.touramigo.service.entity.supplier;

import com.touramigo.service.entity.user.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "terms_and_conditions")
public class TermsAndConditions implements Serializable {

    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne
    private Supplier supplier;

    @Column(name = "version")
    private int version;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @Column(name = "published_at", columnDefinition = "TIMESTAMP")
    private LocalDateTime publishedAt;

    @ManyToOne
    @JoinColumn(name = "published_by")
    private User publishedBy;

    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private TermsAndConditionsState state;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private TermsAndConditionsType type;

    @Column(name = "description")
    private String description;

    @Column(name = "content")
    private String content;
}
