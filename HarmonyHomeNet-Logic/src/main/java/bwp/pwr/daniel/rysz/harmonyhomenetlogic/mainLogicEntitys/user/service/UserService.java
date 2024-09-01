package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {

    List<UserResponse> findAll();

    User findById(UUID id) throws UserNotFoundException;

    User findByLogin(String login) throws UserNotFoundException;

    UserResponse save(User user);

    void deleteById(UUID id) throws UserNotFoundException;

    User findByPESELNumber(String PESELNumber) throws UserNotFoundException;

    User findByEmail(String email) throws UserNotFoundException;

}
