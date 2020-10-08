package facultad.trendz.model;

import javax.persistence.*;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole enumRole;

    public Role() {
    }

    public Role(ERole enumRole, Long id) {
        this.enumRole = enumRole;
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getEnumRole() {
        return enumRole;
    }

    public void setEnumRole(ERole role) {
        this.enumRole = role;
    }
}
