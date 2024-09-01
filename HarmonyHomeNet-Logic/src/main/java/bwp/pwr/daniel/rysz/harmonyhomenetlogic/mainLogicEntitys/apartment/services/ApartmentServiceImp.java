package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.services;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.ApartmentNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.entitys.Apartment;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.apartment.repositorys.ApartmentRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.Resident;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.UserRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.buildingStaff.ApartmentResponse;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ApartmentServiceImp implements ApartmentService {

    private final ApartmentRepository apartmentRepository;
    private final UserRepository userRepository;

    @Override
    public List<ApartmentResponse> findAll() {
        return apartmentRepository.findAll().stream().map(apartment -> ApartmentResponse.builder()
                .id(apartment.getId())
                .apartmentNumber(apartment.getApartmentNumber())
                .area(apartment.getArea())
                .build()
        ).toList();
    }

    @Override
    public ApartmentResponse findById(UUID id) throws ApartmentNotFoundException {
        return apartmentRepository.findById(id).map(apartment -> ApartmentResponse.builder()
                .id(apartment.getId())
                .apartmentNumber(apartment.getApartmentNumber())
                .area(apartment.getArea())
                .build()
        ).orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));
    }

    @Override
    public ApartmentResponse save(@NonNull Apartment newApartment) {

        apartmentRepository.save(newApartment);

        return ApartmentResponse.builder()
                .id(newApartment.getId())
                .apartmentNumber(newApartment.getApartmentNumber())
                .area(newApartment.getArea())
                .build();
    }

    @Override
    public void deleteById(UUID id) throws ApartmentNotFoundException {

        if (apartmentRepository.existsById(id)) apartmentRepository.deleteById(id);
        else throw new ApartmentNotFoundException("wrong apartment id");

    }

    @Override
    public ApartmentResponse findByApartmentNumber(int apartmentNumber, UUID buildingId) throws ApartmentNotFoundException {
        return apartmentRepository.findByApartmentNumberAndBuildingId(apartmentNumber, buildingId).map(apartment -> ApartmentResponse.builder()
                .id(apartment.getId())
                .apartmentNumber(apartment.getApartmentNumber())
                .area(apartment.getArea())
                .build()
        ).orElseThrow(() -> new ApartmentNotFoundException("wrong apartment number"));
    }

    @Override
    public List<UserResponse> findUserByApartmentId(UUID apartmentId) throws ApartmentNotFoundException {
        return apartmentRepository.findById(apartmentId).map(apartment -> apartment.getResidents().stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build()
                ).toList()
        ).orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));
    }

    @Override
    @Transactional
    public UserResponse addResidentTenantToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));

        if(!(user instanceof Resident)) throw new UserNotFoundException("user with this ID isn't a resident");

        Apartment apartmentToUpdate = apartmentRepository.findById(apartmentId)
                .orElseThrow(() -> new ApartmentNotFoundException("wrong apartment id"));

//        user.getRole().add()
        return null;
    }

    @Override
    @Transactional
    public UserResponse addResidentOwnerToApartment(UUID userId, UUID apartmentId) throws ApartmentNotFoundException, UserNotFoundException {
        return null;
    }
}
