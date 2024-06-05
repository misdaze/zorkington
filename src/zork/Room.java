package zork;

import java.util.ArrayList;

public class Room {

  private String roomName;
  private int roomnumber;
  private String description;
  private ArrayList<Exit> exits;
  private Inventory inventory;
  private ArrayList<NPC> npcs;

  
  public ArrayList<Exit> getExits() {
    return exits;
  }

  public Inventory getInventory(){
    return inventory;
  }

  public void setExits(ArrayList<Exit> exits) {
    this.exits = exits;
  }

  public void addNPC(NPC npc){
    npcs.add(npc);
  }

  public boolean hasHostiles(){
    if (npcs.size()>0){
      for (NPC npc : npcs) {
        if (npc instanceof Hostile)
          return true;
      }
    }

    return false;
  }

  /**
   * Create a room described "description". Initially, it has no exits.
   * "description" is something like "a kitchen" or "an open court yard".
   */
  public Room(String description) {
    this.description = description;
    exits = new ArrayList<Exit>();
    npcs = new ArrayList<NPC>();
  }

  public Room() {
    roomName = "DEFAULT ROOM";
    description = "DEFAULT DESCRIPTION";
    exits = new ArrayList<Exit>();

    inventory = new Inventory(Integer.MAX_VALUE);

    npcs = new ArrayList<NPC>();


  }

  public void addExit(Exit exit) throws Exception {
    exits.add(exit);
  }

  /**
   * Return the description of the room (the one that was defined in the
   * constructor).
   */
  public String shortDescription() {
    return "Room: " + roomName + "\n\n" + description;
  }

  /**
   * Return a long description of this room, on the form: You are in the kitchen.
   * Exits: north west
   */
  public String longDescription() {

    return "Room: " + roomName + "\n\n" + description + "\n" + exitString();
  }

  /**
   * Return a string describing the room's exits, for example "Exits: north west
   * ".
   */
  public String exitString() {
    String returnString = "";
    for (Exit exit : exits) {
      returnString += exit.getDirection() + " ";
    }

    return returnString;
  }

  /**
   * Return the room that is reached if we go from this room in direction
   * "direction". If there is no room in that direction, return null.
   */
  public Room nextRoom(String direction) {
    try {
      for (Exit exit : exits) {

        if (exit.getDirection().equalsIgnoreCase(direction)) {
          String adjacentRoom = exit.getAdjacentRoom();
          System.out.println(Game.roomMap.get(adjacentRoom));
          return Game.roomMap.get(adjacentRoom);
        }else{
          return null;
        }
        

      }
    } catch (IllegalArgumentException ex) {
      System.out.println(direction + " is not a valid direction.");
      return null;
    }

    System.out.println(direction + " is not a valid direction.");
    return null;
  }

  
    //private int getDirectionIndex(String direction) { int dirIndex = 0; for
    //(String dir : directions) { if (dir.equals(direction)) return dirIndex; else
    //dirIndex++; }
    
    //throw new IllegalArgumentException("Invalid Direction"); }
   
  public String getRoomName() {
    return roomName;
  }
  public void setRoomName(String roomName) {
    this.roomName = roomName;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
  public int getNum(){
    return roomnumber;
  }
  public void setroomnum(int rn){
    this.roomnumber = rn;
  }
}