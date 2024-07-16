package bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.apartmentStuff.repositorys.ApartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentServiceImp implements ApartmentService{

    private final ApartmentRepository apartmentRepository;

    @Override
    public List<Apartment> findAll() {
        return apartmentRepository.findAll();
    }

    @Override
    public Optional<Apartment> findById(UUID id) {
        return apartmentRepository.findById(id);
    }

    @Override
    public void save(Apartment apartment) {
        apartmentRepository.save(apartment);
    }

    @Override
    public void deleteById(UUID id) {
        apartmentRepository.deleteById(id);
    }

    @Override
    public Optional<Apartment> findByApartmentNumber(int apartmentNumber) {
        return apartmentRepository.findByApartmentNumber(apartmentNumber);
    }
}
