package bwp.pwr.daniel.rysz.harmonyhomenetlogic.parkingSpace.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.parkingSpace.entity.ParkingSpace;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.parkingSpace.repository.ParkingSpaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingSpaceServiceImp implements ParkingSpaceService {

    private final ParkingSpaceRepository parkingSpaceRepository;


    @Override
    public void deleteById(UUID id) {
        parkingSpaceRepository.deleteById(id);
    }

    @Override
    public Optional<ParkingSpace> findById(UUID id) {
        return parkingSpaceRepository.findById(id);
    }

    @Override
    public Optional<ParkingSpace> findByNumber(int parkingSpaceNumber) {
        return parkingSpaceRepository.findByNumber(parkingSpaceNumber);
    }

    @Override
    public List<ParkingSpace> findAll() {
        return parkingSpaceRepository.findAll();
    }

    @Override
    public void save(ParkingSpace parkingSpace) {
        parkingSpaceRepository.save(parkingSpace);
    }
}
