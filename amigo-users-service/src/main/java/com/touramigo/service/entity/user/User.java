package com.touramigo.service.entity.user;

import com.touramigo.service.entity.partner.OperationalGroup;
import com.touramigo.service.entity.partner.Partner;
import com.touramigo.service.entity.supplier.Supplier;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@ToString(exclude = {"image", "partner", "supplier", "userProducts", "operationalGroups"})
public class User implements Serializable {

    public User(User user) {
        this.password = user.getPassword();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.credentialsNotExpired = user.isCredentialsNotExpired();
    }

    @Id
    @Type(type = "org.hibernate.type.PostgresUUIDType")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "enabled_before")
    private boolean enabledBefore;

    @Column(name = "credentials_not_expired")
    private boolean credentialsNotExpired;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "contact_number")
    private String contactNumber;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "image_id")
    private Image image;

    @ManyToOne
    private Partner partner;

    @ManyToOne
    private Supplier supplier;

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<UserProduct> userProducts;

    @ManyToMany(mappedBy = "users", fetch = FetchType.EAGER)
    @Fetch(value = FetchMode.SUBSELECT)
    private List<OperationalGroup> operationalGroups;
}
