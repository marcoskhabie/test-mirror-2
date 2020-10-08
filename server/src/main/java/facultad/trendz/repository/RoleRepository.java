package facultad.trendz.repository;

import facultad.trendz.model.ERole;
import facultad.trendz.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByEnumRole(ERole enumRole);
}
