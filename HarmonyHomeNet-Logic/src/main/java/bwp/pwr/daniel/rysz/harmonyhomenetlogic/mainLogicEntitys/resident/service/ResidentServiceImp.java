package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.repository.ResidentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResidentServiceImp implements ResidentService{

    private final ResidentRepository residentRepository;

    @Override
    public List<Resident> findAll() {
        return residentRepository.findAll();
    }

    @Override
    public Optional<Resident> findById(UUID id) {
        return residentRepository.findById(id);
    }

    @Override
    public Optional<Resident> findByLogin(String login) {
        return residentRepository.findResidentByLogin(login);
    }

    @Override
    public void save(Resident resident) {
        residentRepository.save(resident);
    }

    @Override
    public void deleteById(UUID id) {
        residentRepository.deleteById(id);
    }

    @Override
    public Optional<Resident> findByPESELNumber(String PESELNumber) {
        return residentRepository.findResidentByPESELNumber(PESELNumber);
    }
}
