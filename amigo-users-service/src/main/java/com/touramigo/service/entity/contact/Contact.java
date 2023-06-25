package com.touramigo.service.entity.contact;

import com.touramigo.service.entity.note.Note;
import com.touramigo.service.entity.supplier.Supplier;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contact")
public class Contact implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "created_by", nullable = false, updatable = false)
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    private UUID createdBy;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "email")
    private String email;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "telephone")
    private String telephone;

    @Column(name = "linkedin")
    private String linkedIn;

    @Column(name = "social_media")
    private String socialMedia;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @OneToMany(mappedBy = "contact", cascade = CascadeType.ALL)
    @Where(clause = "deleted is false")
    private Set<Note> notes = new HashSet<>();
}

