package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 * test message
 */
@RestController
public class UserController {


      private final UserService userService;
      UserController(UserService userService, SimpMessagingTemplate template) {
        this.userService = userService;
    //    this.roomService = roomService;
          this.template = template;
      }
    final SimpMessagingTemplate template;

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers() {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers();
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

    @GetMapping("/users/online")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<UserGetDTO> getOnlineUsers(/*@RequestBody User thisUser*/) {
        // fetch all online users in the internal representation

        List<User> users = userService.getOnlineUsers();
//        long thisUserId = getId();
        //List<User> filteredUsers = users.stream().filter(user -> user.getId() != thisUserId).collect(Collectors.toList());
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        template.convertAndSend("/topic/users/online","get online users successfully");
        return userGetDTOs;
    }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

    @PutMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ResponseBody
    public void updateInfo(@PathVariable long userId, @RequestBody User user) {
//        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        if(user.getUsername()!=null) {
            userService.updateUsername(user.getUsername(), userId);
        }
        if (user.getStatus()!=null) {
            userService.updateUserStatus(user.getStatus(), userId);
        }
    }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserById(@PathVariable long userId){
        User user = userService.findUserById(userId);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

    @GetMapping("/users/{username}/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUserByUsername(@PathVariable String username){
        User user = userService.findUserByUsername(username);
        return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }
//    @PostMapping("users/{userId}/friends")
//    @ResponseStatus(HttpStatus.CREATED)
//    @ResponseBody
//    public GetDTO (@RequestBody)

}
