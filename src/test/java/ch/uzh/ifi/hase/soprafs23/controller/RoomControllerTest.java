package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.game.Room;
import ch.uzh.ifi.hase.soprafs23.rest.dto.RoomGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.RoomService;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest(RoomController.class)
class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private SimpMessagingTemplate template;
    @MockBean
    private RoomService roomService;
    @MockBean
    private Room testRoom;
    @MockBean
    private UserService userService;
    @MockBean
    private User user;
    @MockBean
    private DTOMapper dtoMapper;
    private final int TEST_ROOM_ID = 1;
    private final Long TEST_USER_ID = 1L;
    private List<RoomGetDTO> testRoomsList = new ArrayList<>();
    private RoomGetDTO testRoomDTO;
    private ArrayList<Long> userIds;
    private List<UserGetDTO> userGetDTOS;
    private UserGetDTO userGetDTO;
    @BeforeEach
    void setUp() {
        userGetDTO = new UserGetDTO();
        userGetDTO.setId(TEST_USER_ID);

        userGetDTOS = new ArrayList<UserGetDTO>();
        userGetDTOS.add(userGetDTO);

        user = new User();
        user.setId(TEST_USER_ID);

        userIds = new ArrayList<Long>();
        userIds.add(TEST_USER_ID);

        testRoom = new Room(TEST_ROOM_ID);
        testRoom.setUserIds(userIds);

        testRoomDTO = new RoomGetDTO();
        testRoomDTO.setRoomId(1);
        testRoomDTO.setUserIds(userIds);

        testRoomsList.add(testRoomDTO);

        given(roomService.findRoomById(TEST_ROOM_ID)).willReturn(testRoom);
        given(roomService.getAllRooms()).willReturn(testRoomsList);
        given(roomService.getUserInRoom(TEST_ROOM_ID)).willReturn(userGetDTOS);
        given(dtoMapper.convertEntityToUserGetDTO(Mockito.any())).willReturn(userGetDTO);
    }

    @Test
    void givenAUser_createRoom_success() throws Exception{
        given(roomService.createRoom(TEST_USER_ID)).willReturn(testRoomDTO);

        MockHttpServletRequestBuilder postRequest = post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(postRequest)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.roomId", is(testRoomDTO.getRoomId())))
                .andExpect(jsonPath("$.userIds[0]", is(testRoomDTO.getUserIds().get(0).intValue())));

        verify(template).convertAndSend("/topic/rooms", testRoomsList);
        verify(roomService, Mockito.times(1)).createRoom(TEST_USER_ID);
        verify(roomService, Mockito.times(1)).getAllRooms();
    }

    @Test
    void addAUserToARoomWithValidRoomId_success() throws Exception {
        doNothing().when(roomService).addAUserToRoom(TEST_ROOM_ID, TEST_USER_ID);

        System.out.println(userGetDTO);
        System.out.println(dtoMapper.convertEntityToUserGetDTO(user));

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/add", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        verify(roomService, Mockito.times(1)).addAUserToRoom(TEST_ROOM_ID, TEST_USER_ID);
        verify(roomService, Mockito.times(1)).getUserInRoom(TEST_ROOM_ID);
        verify(roomService, Mockito.times(1)).getAllRooms();
        verify(template).convertAndSend("/topic/rooms", testRoomsList);
        verify(template).convertAndSend("/topic/room/1", userGetDTOS);
    }

    @Test
    void removeAUserFromRoom_thenDeleteTheRoom_success() throws Exception {
        doNothing().when(roomService).removeAUserFromRoom(TEST_ROOM_ID, TEST_USER_ID);


        List<UserGetDTO> emptyUserList = new ArrayList<UserGetDTO>();
        given(roomService.getUserInRoom(TEST_ROOM_ID)).willReturn(emptyUserList);

        // mocks when there's no one left after moving all players -> should delete the room from lobby
        doNothing().when(roomService).removeRoomFromLobby(TEST_ROOM_ID);

        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/remove", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        verify(roomService, Mockito.times(1)).removeAUserFromRoom(TEST_ROOM_ID, TEST_USER_ID);
        verify(roomService, Mockito.times(1)).getUserInRoom(TEST_ROOM_ID);
        verify(roomService, Mockito.times(1)).removeRoomFromLobby(TEST_ROOM_ID);
        verify(roomService, Mockito.times(1)).getAllRooms();
        verify(template).convertAndSend("/topic/rooms", testRoomsList);
    }

    @Test
    void removeAUserFromRoom_noRoomIsDeleted_success() throws Exception {
        doNothing().when(roomService).removeAUserFromRoom(TEST_ROOM_ID, TEST_USER_ID);

        // mocks when there's player left after moving the other player -> should send the user list using web socket
        MockHttpServletRequestBuilder putRequest = put("/rooms/{roomId}/remove", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(user));

        mockMvc.perform(putRequest)
                .andExpect(status().isOk());

        verify(roomService, Mockito.times(1)).removeAUserFromRoom(TEST_ROOM_ID, TEST_USER_ID);
        verify(roomService, Mockito.times(1)).getUserInRoom(TEST_ROOM_ID);
        verify(roomService, Mockito.times(1)).getAllRooms();
        verify(template).convertAndSend("/topic/room/1", userGetDTOS);
        verify(template).convertAndSend("/topic/rooms", testRoomsList);
    }

    @Test
    void givenValidRoomId_returnRoomGETDTO() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roomId", is(testRoomDTO.getRoomId())))
                .andExpect(jsonPath("$.userIds[0]", is(testRoomDTO.getUserIds().get(0).intValue())));

        verify(roomService, Mockito.times(1)).findRoomById(TEST_ROOM_ID);
    }

    @Test
    void givenValidRoomId_returnPlayersInRoom() throws Exception {
        MockHttpServletRequestBuilder getRequest = get("/rooms/{roomId}/players", TEST_ROOM_ID)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is((int)userGetDTO.getId())));

        verify(roomService, Mockito.times(1)).getUserInRoom(TEST_ROOM_ID);
    }

    @Test
    void getAllRoomsInLobby_success() throws Exception{

        MockHttpServletRequestBuilder getRequest = get("/rooms")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].roomId", is(testRoomsList.get(0).getRoomId())))
                .andExpect(jsonPath("$[0].userIds[0]", is(testRoomsList.get(0).getUserIds().get(0).intValue())));

        verify(roomService, Mockito.times(1)).getAllRooms();
    }

    private String asJsonString(final Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    String.format("The request body could not be created.%s", e.toString()));
        }
    }

}