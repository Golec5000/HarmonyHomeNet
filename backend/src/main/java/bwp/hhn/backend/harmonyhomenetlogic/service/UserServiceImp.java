package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;
import bwp.hhn.backend.harmonyhomenetlogic.repository.mainTables.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;

    @Override
    public User save(User user) {
        return null;
    }

    @Override
    public User findAll() {
        return null;
    }

    @Override
    public User findById(Long id) {
        return null;
    }

    @Override
    public User findByEmail(String email) {
        return null;
    }

    @Override
    public User findByFirstAndLastName(String firstName, String lastName) {
        return null;
    }
}
