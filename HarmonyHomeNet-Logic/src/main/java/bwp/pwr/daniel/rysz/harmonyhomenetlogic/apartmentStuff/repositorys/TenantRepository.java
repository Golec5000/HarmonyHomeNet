package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TenantRepository extends JpaRepository<Tenant, UUID> {
    Optional<Tenant> findTenantByFirstNameAndLastName(String firstName, String lastName);
}
