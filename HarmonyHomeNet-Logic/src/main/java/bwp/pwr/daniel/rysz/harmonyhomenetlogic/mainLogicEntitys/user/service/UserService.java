package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(UUID id);

    Optional<User> findByLogin(String login);

    void save(User resident);

    void deleteById(UUID id);

    Optional<User> findByPESELNumber(String PESELNumber);

}
