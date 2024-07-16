package bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.entitys.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.buildingStuff.repositorys.BasementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasementServiceImp implements BasementService {
    private final BasementRepository basementRepository;


    @Override
    public void deleteById(UUID id) {
        basementRepository.deleteById(id);
    }

    @Override
    public Optional<Basement> findById(UUID id) {
        return basementRepository.findById(id);
    }

    @Override
    public Optional<Basement> findByBasementNumber(int basementNumber) {
        return basementRepository.findByBasementNumber(basementNumber);
    }

    @Override
    public List<Basement> findAll() {
        return basementRepository.findAll();
    }

    @Override
    public void save(Basement basement) {
        basementRepository.save(basement);
    }
}
