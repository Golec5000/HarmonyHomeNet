package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.service;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.exeptions.customErrors.UserNotFoundException;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.entity.User;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.user.repository.UserRepository;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.utils.response.userStaff.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImp implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("wrong user id"));
    }

    @Override
    public User findByLogin(String login) {
        return userRepository.findUserByEmail(login)
                .orElseThrow(() -> new UserNotFoundException("wrong user login"));
    }

    @Override
    public UserResponse save(User user) {
        userRepository.save(user);
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .build();
    }

    @Override
    public void deleteById(UUID id) {
        if (userRepository.existsById(id)) userRepository.deleteById(id);
        else throw new UserNotFoundException("wrong user id");
    }

    @Override
    public User findByPESELNumber(String PESELNumber) {
        return userRepository.findUserByPESELNumber(PESELNumber)
                .orElseThrow(() -> new UserNotFoundException("wrong user PESEL number"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("wrong user email"));
    }

    @Override
    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .baseRole(user.getBaseRole())
                .gender(user.getUserGender())
                .build();
    }

    @Override
    public List<UserResponse> mapUserListToUserResponseList(List<User> userList) {
        return userList.stream()
                .map(user -> UserResponse.builder()
                        .id(user.getId())
                        .lastName(user.getLastName())
                        .firstName(user.getFirstName())
                        .email(user.getEmail())
                        .baseRole(user.getBaseRole())
                        .gender(user.getUserGender())
                        .build()
                ).toList();
    }
}
