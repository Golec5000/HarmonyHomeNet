package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository residentRepository;

    @Override
    public List<User> findAll() {
        return residentRepository.findAll();
    }

    @Override
    public Optional<User> findById(UUID id) {
        return residentRepository.findById(id);
    }

    @Override
    public Optional<User> findByLogin(String login) {
        return residentRepository.findUserByLogin(login);
    }

    @Override
    public void save(User resident) {
        residentRepository.save(resident);
    }

    @Override
    public void deleteById(UUID id) {
        residentRepository.deleteById(id);
    }

    @Override
    public Optional<User> findByPESELNumber(String PESELNumber) {
        return residentRepository.findUserByPESELNumber(PESELNumber);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return residentRepository.findUserByEmail(email);
    }
}
