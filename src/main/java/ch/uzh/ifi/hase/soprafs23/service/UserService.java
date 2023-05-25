package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * User Service
 * This class is the "worker" and responsible for all functionality related to
 * the user
 * (e.g., it creates, modifies, deletes, finds). The result will be passed back
 * to the caller.
 */
@Service
@Transactional
public class UserService {

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    @Autowired
    public UserService(@Qualifier("userRepository") UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getUsers() {
        return this.userRepository.findAll();
    }

    public List<User> getOnlineUsers() {
        List<User> allUsers = this.userRepository.findAll();
        List<User> onlineUsers = new ArrayList<>();
        for (User user : allUsers) {
            if (user.getStatus() == UserStatus.ONLINE) {
                onlineUsers.add(user);
            }
        }
        return onlineUsers;
    }

    public User createUser(User newUser) {
        newUser.setToken(UUID.randomUUID().toString());
        newUser.setStatus(UserStatus.ONLINE);
        checkIfUserExists(newUser);
        // saves the given entity but data is only persisted in the database once
        // flush() is called
        newUser = userRepository.save(newUser);
        userRepository.flush();

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    /**
     * This is a helper method that will check the uniqueness criteria of the
     * username and the name
     * defined in the User entity. The method will do nothing if the input is unique
     * and throw an error otherwise.
     *
     * @param userToBeCreated
     * @throws org.springframework.web.server.ResponseStatusException
     * @see User
     */
    private void checkIfUserExists(User userToBeCreated) {
        User userByUsername = userRepository.findByUsername(userToBeCreated.getUsername());
        String baseErrorMessage = "The %s provided %s not unique. Therefore, the user could not be created!";
        if (userByUsername != null) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username", "is"));
        }
    }

    private void checkIfIdExists(long id) {
        User userById = userRepository.findById(id);
        String baseErrorMessage = "User with id %s was not found";
        if (userById == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, id));
        }
    }

    private void checkIfUserExistsByUserName(String username) {
        User userByUsername = userRepository.findByUsername(username);
        String baseErrorMessage = "User with username %s was not found";
        if (userByUsername == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, String.format(baseErrorMessage, username));
        }
    }

    public User findUserById(long id) {
        checkIfIdExists(id);
        return userRepository.findById(id);
    }

    public User findUserByUsername(String username) {
        checkIfUserExistsByUserName(username);
        return userRepository.findByUsername(username);
    }

    public void updateUsername(String username, long userId) {
        User userByUsername = userRepository.findByUsername(username);
        String baseErrorMessage = "The %s provided is not unique. Please try another one";
        if (userByUsername == null) {
            User user = findUserById(userId);
            user.setUsername(username);
            userRepository.flush();
        }
        else {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format(baseErrorMessage, "username"));
        }
    }

    public void updateUserStatus(UserStatus status, long userId) {
        User user = findUserById(userId);
        user.setStatus(status);
        userRepository.flush();
    }

    public void updateRoomId(Integer roomId, long userId) {
        User user = findUserById(userId);
        user.setRoomId(roomId);
        userRepository.flush();
    }

    // update user stats at the end of the game;
    public void updateStatistics(long userIdOfWiner, long userIdOfLoser) {
        User winner = findUserById(userIdOfWiner);
        User loser = findUserById(userIdOfLoser);
        winner.setWins(winner.getWins() + 1);
        loser.setLoss(loser.getLoss() + 1);
    }

    public User authorize(User userInput) {
        User user = findUserByUsername(userInput.getUsername());
        String password = user.getPassword();
        if (Objects.equals(userInput.getPassword(), password)) {
            user.setToken(UUID.randomUUID().toString());
            userRepository.flush();
            return user;
        }
        else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong password! Please try again.");
        }
    }
}
