package bwp.hhn.backend.harmonyhomenetlogic.service;

import bwp.hhn.backend.harmonyhomenetlogic.entity.mainTables.User;

public interface UserService {

    User save(User user);

    User findAll();

    User findById(Long id);

    User findByEmail(String email);

    User findByFirstAndLastName(String firstName, String lastName);



}
