package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Tenant;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys.TenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TenantServiceImp implements TenantService{

    private final TenantRepository tenantRepository;


    @Override
    public Optional<Tenant> getTenantById(UUID id) {
        return tenantRepository.findById(id);
    }

    @Override
    public Tenant saveTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    @Override
    public void deleteTenant(UUID id) {
        tenantRepository.deleteById(id);
    }

    @Override
    public Optional<Tenant> findTenantByFirstNameAndLastName(String firstName, String lastName) {
        return tenantRepository.findTenantByFirstNameAndLastName(firstName, lastName);
    }
}
