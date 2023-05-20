package ch.uzh.ifi.hase.soprafs23.service;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private UserService userService;

  private User testUser;

  @BeforeEach
  public void setup() {
    MockitoAnnotations.openMocks(this);

    // given
    testUser = new User();
    testUser.setStatus(UserStatus.ONLINE);
    testUser.setId(1L);
    testUser.setPassword("testName");
    testUser.setUsername("testUsername");
    testUser.setRoomId(1);
    testUser.setLoss(0);
    testUser.setWins(0);

    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    Mockito.when(userRepository.save(Mockito.any())).thenReturn(testUser);
  }

  @Test
  public void createUser_validInputs_success() {
    // when -> any object is being save in the userRepository -> return the dummy
    // testUser
    User createdUser = userService.createUser(testUser);

    // then
    Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any());

    assertEquals(testUser.getId(), createdUser.getId());
    assertEquals(testUser.getPassword(), createdUser.getPassword());
    assertEquals(testUser.getUsername(), createdUser.getUsername());
    assertNotNull(createdUser.getToken());
    assertEquals(UserStatus.ONLINE, createdUser.getStatus());
  }


  // no longer need to validate name
//  @Test
//  public void createUser_duplicateName_throwsException() {
//    // given -> a first user has already been created
//    userService.createUser(testUser);
//
//    // when -> setup additional mocks for UserRepository
//    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
//    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(null);
//
//    // then -> attempt to create second user with same user -> check that an error
//    // is thrown
//    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
//  }

  @Test
  public void createUser_duplicateInputs_throwsException() {
    // given -> a first user has already been created
    userService.createUser(testUser);

    // when -> setup additional mocks for UserRepository
//    Mockito.when(userRepository.findByName(Mockito.any())).thenReturn(testUser);
    Mockito.when(userRepository.findByUsername(Mockito.any())).thenReturn(testUser);

    // then -> attempt to create second user with same user -> check that an error
    // is thrown
    assertThrows(ResponseStatusException.class, () -> userService.createUser(testUser));
  }

  @Test
  public void test_getOnlineUsers(){
      List<User> users = new ArrayList<>();

      users.add(testUser);
      userService.createUser(testUser);
      Mockito.when(userRepository.findAll()).thenReturn(users);
      List<User> actual = userService.getOnlineUsers();

      Mockito.verify(userRepository, Mockito.times(1)).findAll();
      assertEquals(users, actual);
  }

  @Test
  public void test_findUserByID(){
      userService.createUser(testUser);
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      User actual = userService.findUserById(testUser.getId());
      //Mockito.verify(userRepository, Mockito.times(2)).findById(Mockito.any());

      assertEquals(testUser, actual);
  }

  @Test
  public void test_updateUsername_valid(){
      userService.createUser(testUser);
      String newUsername = "test";
      Mockito.when(userRepository.findByUsername(newUsername)).thenReturn(null);
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      userService.updateUsername(newUsername, 1L);
      assertEquals(newUsername, testUser.getUsername());

  }
  @Test
  public void test_updateUsername_invalid(){
      userService.createUser(testUser);
      String newUsername = "test";
      Mockito.when(userRepository.findByUsername(newUsername)).thenReturn(Mockito.any());
      assertThrows(ResponseStatusException.class, () -> userService.updateUsername(newUsername, 1L));
  }

  @Test
  public void test_updateRoomID(){
      userService.createUser(testUser);
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);

      userService.updateRoomId(3, 1L);

      assertEquals(3, testUser.getRoomId());
  }

  @Test
  public void test_updateUserStatus(){
      userService.createUser(testUser);
      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      assertEquals(UserStatus.ONLINE, testUser.getStatus());

      userService.updateUserStatus(UserStatus.OFFLINE, 1L);

      assertEquals(UserStatus.OFFLINE, testUser.getStatus());
  }

  @Test
  public void test_updateStatistics(){
      //create additional user
      User loser = new User();
      loser.setId(2L);
      loser.setLoss(999);
      loser.setWins(0);

      userService.createUser(testUser);
      userService.createUser(loser);

      Mockito.when(userRepository.findById(1L)).thenReturn(testUser);
      Mockito.when(userRepository.findById(2L)).thenReturn(loser);

      userService.updateStatistics(1L, 2L);

      assertEquals(1, testUser.getWins());
      assertEquals(0, testUser.getLoss());
      assertEquals(0, loser.getWins());
      assertEquals(1000, loser.getLoss());
  }

  @Test
  public void test_authorize_valid(){
      userService.createUser(testUser);
      Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
      User actual = userService.authorize(testUser);
      assertEquals(testUser, actual);
  }

  @Test
  public void test_authorize_invalid(){
      userService.createUser(testUser);
      Mockito.when(userRepository.findByUsername(testUser.getUsername())).thenReturn(testUser);
      //create user for input method with a different password
      User input = new User();
      input.setPassword("test");
      input.setUsername("testUsername");
      assertThrows(ResponseStatusException.class, () -> userService.authorize(input));
  }
}
