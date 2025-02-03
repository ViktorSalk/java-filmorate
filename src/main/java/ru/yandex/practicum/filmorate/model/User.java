package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private Long id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^\\S+$")
    private String login;

    private String name;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
    private Map<Long, FriendshipStatus> friendshipStatuses = new HashMap<>();

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? this.login : name;
    }

    public FriendshipStatus getFriendshipStatus(Long friendId) {
        return friendshipStatuses.getOrDefault(friendId, FriendshipStatus.UNCONFIRMED);
    }
}