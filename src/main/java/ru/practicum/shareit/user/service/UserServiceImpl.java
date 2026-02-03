package ru.practicum.shareit.user.service;

import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * In-memory реализация {@link UserService}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email already in use: " + userDto.getEmail());
        }

        User user = UserMapper.toUser(userDto);
        User created = userRepository.save(user);
        log.debug("created userId={}", created.getId());
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), userId)) {
                throw new IllegalStateException("Email already in use: " + userDto.getEmail());
            }
            existing.setEmail(userDto.getEmail());
        }

        User saved = userRepository.save(existing);
        log.debug("updated userId={}", userId);
        return UserMapper.toUserDto(saved);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        log.debug("returned userId={}", userId);
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
        log.debug("returned usersCount={}", users.size());
        return users;
    }

    @Override
    public void delete(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        userRepository.deleteById(userId);
        log.debug("deleted userId={}", userId);
    }
}