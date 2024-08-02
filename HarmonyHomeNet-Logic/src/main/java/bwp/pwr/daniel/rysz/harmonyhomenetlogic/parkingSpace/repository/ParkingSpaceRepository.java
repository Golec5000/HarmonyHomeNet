package bwp.pwr.daniel.rysz.harmonyhomenetlogic.parkingSpace.repository;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.parkingSpace.entity.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParkingSpaceRepository extends JpaRepository<ParkingSpace, UUID> {
    Optional<ParkingSpace> findByNumber(int parkingSpaceNumber);
}
