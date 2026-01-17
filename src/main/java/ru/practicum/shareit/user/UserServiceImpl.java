package ru.practicum.shareit.user;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

/**
 * In-memory реализация {@link UserService}.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalStateException("Email already in use: " + userDto.getEmail());
        }

        User user = UserMapper.toUser(userDto);
        User created = userRepository.create(user);
        return UserMapper.toUserDto(created);
    }

    @Override
    public UserDto update(Long userId, UserDto userDto) {
        User existing = userRepository.getById(userId);
        if (existing == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }

        if (userDto.getName() != null) {
            existing.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (userRepository.existsByEmailExcludingId(userDto.getEmail(), userId)) {
                throw new IllegalStateException("Email already in use: " + userDto.getEmail());
            }
            existing.setEmail(userDto.getEmail());
        }

        userRepository.update(existing);
        return UserMapper.toUserDto(existing);
    }

    @Override
    public UserDto getById(Long userId) {
        User user = userRepository.getById(userId);
        if (user == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .toList();
    }

    @Override
    public void delete(Long userId) {
        User removed = userRepository.delete(userId);
        if (removed == null) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
    }
}