package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.BasementNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.entity.Basement;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.basment.repository.BasementRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.BasementResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BasementServiceImp implements BasementService {
    private final BasementRepository basementRepository;


    @Override
    public void deleteById(UUID id) throws BasementNotFoundException {
        if (basementRepository.existsById(id)) basementRepository.deleteById(id);
        else throw new BasementNotFoundException("wrong basement id");
    }

    @Override
    public Basement findById(UUID id) throws BasementNotFoundException {
        return basementRepository.findById(id)
                .orElseThrow(() -> new BasementNotFoundException("wrong basement id"));
    }

    @Override
    public Basement findByBasementNumber(int basementNumber) throws BasementNotFoundException {
        return basementRepository.findByBasementNumber(basementNumber)
                .orElseThrow(() -> new BasementNotFoundException("wrong basement number"));
    }

    @Override
    public List<Basement> findAll() {
        return basementRepository.findAll();
    }

    @Override
    public BasementResponse save(@NonNull Basement basement) {
        basementRepository.save(basement);
        return mapBasementToBasementResponse(basement);
    }

    @Override
    public BasementResponse mapBasementToBasementResponse(@NonNull Basement basement) {
        return BasementResponse.builder()
                .id(basement.getId())
                .basementNumber(basement.getBasementNumber())
                .area(basement.getArea())
                .build();
    }

    @Override
    public List<BasementResponse> mapBasementListToBasementResponseList(@NonNull List<Basement> basementList) {
        return basementList.stream()
                .map(this::mapBasementToBasementResponse)
                .toList();
    }
}
