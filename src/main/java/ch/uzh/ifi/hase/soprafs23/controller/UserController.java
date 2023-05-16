package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
    UserController(UserService userService) {
        this.userService = userService;
      }

    @Autowired
    SimpMessagingTemplate template;

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
    public List<UserGetDTO> getOnlineUsers() {
        // fetch all online users in the internal representation

        List<User> users = userService.getOnlineUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        // convert each user to the API representation
        for (User user : users) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
        }
        template.convertAndSend("/topic/users/online",userGetDTOs);
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
            if(user.getStatus() == UserStatus.OFFLINE) {
                userService.updateUserStatus(user.getStatus(), userId);
                // make sure that roomId will be removed if the user logout;
                userService.updateRoomId(null, userId);
            }
            else {
                userService.updateUserStatus(user.getStatus(), userId);
            }
        }
        List<User> onlineUsers = userService.getOnlineUsers();
        List<UserGetDTO> userGetDTOs = new ArrayList<>();

        for (User user1 : onlineUsers) {
            userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user1));
        }
        template.convertAndSend("/topic/users/online",userGetDTOs);
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

    @PutMapping("/users/login")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPutDTO userLogin(@RequestBody UserPostDTO userPostDTO, HttpServletResponse response){
        User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
        User authorizedUser = userService.authorize(userInput);
//        response.addHeader("Authorization",token);
        System.out.println(authorizedUser.getToken());
        response.addHeader("Authorization", authorizedUser.getToken());
//        HttpHeaders responseHeaders = new HttpHeaders();
//        responseHeaders.set("Authorization", authorizedUser.getToken());
//        request.setAttribute("Authorization", "Bearer " + token);
//        try{
//
//        } catch (ResponseStatusException e) {
//            throw e;
//        }
        return DTOMapper.INSTANCE.convertEntityToUserPutDTO(authorizedUser);
    }

}
