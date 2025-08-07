package common.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import common.domain.entity.base.BaseTime;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseTime {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Basic(fetch = FetchType.EAGER)
    @Column(name= "email", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name= "password")
    private String password;


    @Column(name= "username")
    private String username;


    @Column(name= "first_name")
    private String firstName;


    @Column(name= "last_name")
    private String lastName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "gender",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_users_gender",
                    foreignKeyDefinition =
                            "FOREIGN KEY (gender) REFERENCES genders(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private Gender gender;

    @Column(name= "dob")
    private LocalDateTime dob;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "role",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(
                    name = "fk_users_role",
                    foreignKeyDefinition =
                            "FOREIGN KEY (role) REFERENCES roles(id) " +
                                    "ON DELETE RESTRICT ON UPDATE CASCADE"
            )
    )
    @JsonBackReference
    private Role role;

    // relation many to many


    // Add any additional methods or annotations as needed


    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
} // end class
