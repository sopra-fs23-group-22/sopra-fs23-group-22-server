package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.HeaderResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.springframework.http.RequestEntity.head;
import static org.springframework.http.RequestEntity.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @MockBean
    private SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;



  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus(UserStatus.OFFLINE);

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers()).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
        .andExpect(jsonPath("$[0].password", is(user.getPassword())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus().toString())));
  }

  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("Test User");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus(UserStatus.ONLINE);

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("Test User");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.password", is(user.getPassword())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.status", is(user.getStatus().toString())));

    Mockito.verify(simpMessagingTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
  }

  @Test
  public void testGetOnlineUsers() throws Exception {

      User user = new User();
      user.setId(1L);
      user.setPassword("Test User");
      user.setUsername("testUsername");
      user.setStatus(UserStatus.ONLINE);

      UserGetDTO userGetDTO = new UserGetDTO();
      userGetDTO.setId(1L);
      userGetDTO.setPassword("Test User");
      userGetDTO.setUsername("testUsername");
      userGetDTO.setStatus(UserStatus.ONLINE);
      ArrayList<User> users = new ArrayList<>();
      users.add(user);
      MockHttpServletRequestBuilder getRequest = get("/users/online");


      given(userService.getOnlineUsers()).willReturn(users);


      mockMvc.perform(getRequest)
              .andExpect(status().isOk())
              .andExpect(jsonPath("$[0].id", is((int) userGetDTO.getId())))
              .andExpect(jsonPath("$[0].password", is(userGetDTO.getPassword())))
              .andExpect(jsonPath("$[0].username", is(userGetDTO.getUsername())))
              .andExpect(jsonPath("$[0].status", is(userGetDTO.getStatus().toString())));


      Mockito.verify(simpMessagingTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
  }

  @Test
  public void testUpdateInfo_NOCHANGES() throws Exception {
      UserPutDTO userPutDTO = new UserPutDTO();
      long id = 1L;
      ArrayList<User> users = new ArrayList<>();
      given(userService.getOnlineUsers()).willReturn(users);

      MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/users/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent());
      Mockito.verify(simpMessagingTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
      Mockito.verify(userService, Mockito.times(0)).updateUsername(Mockito.anyString(), Mockito.anyLong());
      Mockito.verify(userService, Mockito.times(0)).updateUserStatus((UserStatus) Mockito.any(), Mockito.anyLong());
  }

  @Test
  public void testUpdateInfo_CHANGEINFO() throws Exception {
      User user = new User();
      user.setUsername("test");
      user.setToken("test");
      user.setStatus(UserStatus.OFFLINE);
      long id = 1L;
      ArrayList<User> users = new ArrayList<>();
      given(userService.getOnlineUsers()).willReturn(users);

      MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/users/" + id)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .content(asJsonString(user));

      mockMvc.perform(putRequest)
              .andExpect(status().isNoContent());

      Mockito.verify(simpMessagingTemplate, Mockito.times(1)).convertAndSend(Mockito.anyString(), (Object) Mockito.any());
      Mockito.verify(userService, Mockito.times(1)).updateUsername(Mockito.anyString(), Mockito.anyLong());
      Mockito.verify(userService, Mockito.times(1)).updateUserStatus((UserStatus) Mockito.any(), Mockito.anyLong());
      Mockito.verify(userService, Mockito.times(1)).updateRoomId(null, id);
  }

  @Test
  public void testGetUserById() throws Exception {
      //setup user
      long id = 1L;
      User user = new User();

      given(userService.findUserById(Mockito.anyLong())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/" + id);
      mockMvc.perform(getRequest)
              .andExpect(status().isOk());

      Mockito.verify(userService, Mockito.times(1)).findUserById(id);
  }

  @Test
  public void testGetUserByUsername() throws Exception {
      String username = "test";
      User user = new User();
      given(userService.findUserByUsername(Mockito.anyString())).willReturn(user);

      MockHttpServletRequestBuilder getRequest = get("/users/" + username + "/login");
      mockMvc.perform(getRequest)
              .andExpect(status().isOk());

      Mockito.verify(userService, Mockito.times(1)).findUserByUsername(username);
  }

  @Test
  public void testUserLogin() throws Exception {
      User user = new User();
      user.setToken("test");
      UserPutDTO userPutDTO = new UserPutDTO();
      given(userService.authorize(Mockito.any())).willReturn(user);
      MockHttpServletResponse response = new MockHttpServletResponse();

      MockHttpServletRequestBuilder putRequest = MockMvcRequestBuilders.put("/users/login",response)
              .contentType(MediaType.APPLICATION_JSON)
              .accept(MediaType.APPLICATION_JSON)
              .content(asJsonString(userPutDTO));

      mockMvc.perform(putRequest)
              .andExpect(status().isOk())
              .andExpect(header().string("Authorization", user.getToken()));

      Mockito.verify(userService, Mockito.times(1)).authorize(Mockito.any());
  }

  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {
      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }
}