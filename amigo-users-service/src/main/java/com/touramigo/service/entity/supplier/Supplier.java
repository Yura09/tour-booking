package com.touramigo.service.entity.supplier;

import com.touramigo.service.entity.contact.Contact;
import com.touramigo.service.entity.enumeration.SupplierType;
import com.touramigo.service.entity.note.Note;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.user.User;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "supplier")
public class Supplier implements Serializable {
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "zip")
    private String zip;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Column(name = "emails")
    private String emails;

    @Column(name = "contact_phones")
    private String contactPhones;

    @Column(name = "website_url")
    private String websiteUrl;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "code_variable", unique = true)
    private String codeVariable;

    @Column(name = "supplier_type")
    @Enumerated(EnumType.STRING)
    private SupplierType supplierType;

    @ManyToMany(mappedBy = "suppliers")
    private Set<Partner> partners;

    @OneToMany(mappedBy = "supplier")
    private Set<SupplierConnection> connectionKeys = new HashSet<>();

    @OneToMany(mappedBy = "supplier")
    private Set<TermsAndConditions> termsAndConditionsList = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "account_manager_id")
    private User accountManager;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    private Set<Contact> contacts = new HashSet<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL)
    @Where(clause = "deleted is false")
    private Set<Note> notes = new HashSet<>();
}
