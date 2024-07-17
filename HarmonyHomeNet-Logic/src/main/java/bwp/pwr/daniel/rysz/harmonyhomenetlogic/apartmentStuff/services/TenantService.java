package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Tenant;

import java.util.Optional;
import java.util.UUID;

public interface TenantService {
    Optional<Tenant> getTenantById(UUID id);
    Tenant saveTenant(Tenant tenant);
    void deleteTenant(UUID id);
    Optional<Tenant> findTenantByFirstNameAndLastName(String firstName, String lastName);
}
