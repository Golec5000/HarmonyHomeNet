package bwp.hhn.backend.harmonyhomenetlogic.controller;

import bwp.hhn.backend.harmonyhomenetlogic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

}
