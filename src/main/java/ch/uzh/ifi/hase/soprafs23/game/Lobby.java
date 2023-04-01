package ch.uzh.ifi.hase.soprafs23.game;

import java.util.*;

public class Lobby {
    // Singleton Pattern
    private static final Lobby lobby = new Lobby();
    public static Lobby getInstance() { return lobby; }

    // HashMap to store Room instances, keyed by room ID
    private HashMap<Integer, Room> roomMap = new HashMap<Integer, Room>();
    private int nextRoomId = 1; // Counter for auto-generated room IDs
    private Set<Integer> closedRoomIds = new HashSet<>(); // Set of closed room IDs

    // Add a new Room to the Lobby with an auto-generated or reused room ID
    public void createRoom() {
        int roomId;
        if (!closedRoomIds.isEmpty()) {
            // Reuse the ID of a closed room if available
            roomId = closedRoomIds.iterator().next();
            closedRoomIds.remove(roomId);
        } else {
            // Generate a new auto-generated ID
            roomId = nextRoomId;
            nextRoomId++;
        }
        // Create a new Room instance and add it to the roomMap with the chosen room ID
        Room room = new Room(roomId);
        roomMap.put(roomId, room);
    }

    public Room getRoomByRoomId(int roomId) { return roomMap.get(roomId); }

    public HashMap<Integer, Room> getRooms() { return this.roomMap; }

    public void removeRoom(int roomId) {
        closedRoomIds.add(roomId);
        roomMap.remove(roomId);
    }
}