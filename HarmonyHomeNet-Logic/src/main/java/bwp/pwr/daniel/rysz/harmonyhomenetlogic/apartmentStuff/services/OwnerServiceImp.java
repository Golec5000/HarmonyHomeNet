package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;


import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Owner;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OwnerServiceImp implements OwnerService {

    private final OwnerRepository ownerRepository;

    @Override
    public Optional<Owner> getOwnerById(UUID id) {
        return ownerRepository.findById(id);
    }

    @Override
    public Owner saveOwner(Owner owner) {
        return ownerRepository.save(owner);
    }

    @Override
    public void deleteOwner(UUID id) {
        ownerRepository.deleteById(id);
    }

    @Override
    public Optional<Owner> findOwnerByFirstNameAndLastName(String firstName, String lastName){
        return ownerRepository.findOwnerByFirstNameAndLastName(firstName, lastName);
    }

}
