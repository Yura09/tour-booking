package com.touramigo.service.entity.partner;

import com.touramigo.service.entity.enumeration.CompanyBusinessType;
import com.touramigo.service.entity.history.PartnerHistory;
import com.touramigo.service.entity.note.Note;
import com.touramigo.service.entity.supplier.Supplier;
import com.touramigo.service.entity.user.User;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import javax.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Partner implements Serializable {

    @OneToMany(mappedBy = "partner")
    List<User> users;
    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "city")
    private String city;
    @Column(name = "country")
    private String country;
    @Column(name = "zip")
    private String zip;
    @Column(name = "emails")
    private String emails;
    @Column(name = "contact_phones")
    private String contactPhones;
    @Column(name = "website")
    private String website;

    @Column(name = "time_zone")
    private String timezone;

    @Column(name = "master")
    private boolean master;

    @Column(name = "enabled")
    private boolean enabled;

    @ManyToOne
    @JoinColumn(name = "ta_contact_id")
    private User taContact;

    @ManyToOne
    private PartnerGroup partnerGroup;

    @Column(name = "business_type")
    @Enumerated(EnumType.STRING)
    private CompanyBusinessType businessType;

    @ManyToMany
    @JoinTable(name = "partner_supplier",
        joinColumns = @JoinColumn(name = "partner_id"),
        inverseJoinColumns = @JoinColumn(name = "supplier_id"))
    @Where(clause = "enabled is true")
    private Set<Supplier> suppliers;

    @OneToMany(mappedBy = "partner")
    private List<ConnectionValue> connectionValues;

    @OneToMany(mappedBy = "partner", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<PartnerProduct> partnerProducts;

    @ManyToOne
    @JoinColumn(name = "account_manager_id")
    private User accountManager;

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    @Where(clause = "deleted is false")
    private Set<Note> notes = new HashSet<>();

    @OneToMany(mappedBy = "partner", cascade = CascadeType.ALL)
    private Set<PartnerHistory> partnerHistories = new HashSet<>();

    @Column(name = "bookings_enabled")
    private boolean bookingsEnabled;
}
