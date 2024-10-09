package ru.practicum.shareit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.exception.EmailAlreadyExist;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotEnoughRightsToChangeData;

@RestController
@RequestMapping("/test")
public class TestControllerTest {

    @GetMapping("/not-found")
    public void throwNotFound() {
        throw new EntityNotFoundException("Test entity not found");
    }

    @PostMapping("/email-exists")
    public void throwEmailAlreadyExists() {
        throw new EmailAlreadyExist("Test email already exists");
    }

    @PostMapping("/not-enough-rights")
    public void throwNotEnoughRights() {
        throw new NotEnoughRightsToChangeData("User does not have enough rights");
    }
}
