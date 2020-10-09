package facultad.logistics.repository;

import facultad.logistics.model.ERole;
import facultad.logistics.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role getByEnumRole(ERole enumRole);
}
