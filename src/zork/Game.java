package zork;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Game {

  public static HashMap<String, Room> roomMap = new HashMap<String, Room>();

  private Parser parser;
  private Room currentRoom;
  private int healthPoints = 100;
  private int hunger = 100;
  private int sanity = 100;
  private String wellbeingHunger = "Perfect";
  private String wellbeingSanity = "Perfect";

  /**
   * Create the game and initialise its internal map.
   */
  public Game() {
    try {
      initRooms("src\\zork\\data\\rooms.json");
      initItems("src\\zork\\data\\items.json");
      initNPCs("src\\zork\\data\\npcs.json");

      currentRoom = roomMap.get("Bedroom");
    } catch (Exception e) {
      e.printStackTrace();
    }
    parser = new Parser();

  }

  private void initNPCs(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    
    JSONArray jsonNPCs = (JSONArray) json.get("npcs");

    for (Object npcObj : jsonNPCs) {
      String name = (String) ((JSONObject) npcObj).get("name");
      String roomId = (String) ((JSONObject) npcObj).get("room_id");
      String description = (String) ((JSONObject) npcObj).get("description");
      String npcType = (String) ((JSONObject) npcObj).get("type");
      String aglity = (String) ((JSONObject) npcObj).get("aglity");
      String health = (String) ((JSONObject) npcObj).get("health");

    if (npcType.equals("0")){
      Hostile hostile = new Hostile(name, description, 0, 0);
    }

    else {
      Trader trader = new Trader(name, description);
    }
     
    }
  }

  private void initItems(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);
  }
  
  private void initRooms(String fileName) throws Exception {
    Path path = Path.of(fileName);
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);

    
    JSONArray jsonRooms = (JSONArray) json.get("rooms");

    for (Object roomObj : jsonRooms) {
      Room room = new Room();
      String roomName = (String) ((JSONObject) roomObj).get("name");
      String roomId = (String) ((JSONObject) roomObj).get("id");
      String roomDescription = (String) ((JSONObject) roomObj).get("description");
      room.setDescription(roomDescription);
      room.setRoomName(roomName);

      JSONArray jsonExits = (JSONArray) ((JSONObject) roomObj).get("exits");
      ArrayList<Exit> exits = new ArrayList<Exit>();
      for (Object exitObj : jsonExits) {
        String direction = (String) ((JSONObject) exitObj).get("direction");
        String adjacentRoom = (String) ((JSONObject) exitObj).get("adjacentRoom");
        String keyId = (String) ((JSONObject) exitObj).get("keyId");
        Boolean isLocked = (Boolean) ((JSONObject) exitObj).get("isLocked");
        Boolean isOpen = (Boolean) ((JSONObject) exitObj).get("isOpen");
        Exit exit = new Exit(direction, adjacentRoom, isLocked, keyId, isOpen);
        exits.add(exit);
      }
      room.setExits(exits);
      roomMap.put(roomId, room);
    }
  }

  /**
   * Main play routine. Loops until end of play.
   */
  public void play() {
    printWelcome();

    boolean finished = false;
    while (!finished) {
      Command command;
      try {
        command = parser.getCommand();
        finished = processCommand(command);
      } catch (IOException e) {
        e.printStackTrace();
      }

    }
    System.out.println("Thank you for playing.  Good bye.");
  }

  /**
   * Print out the opening message for the player.
   */
  private void printWelcome() {
    System.out.println();
    System.out.println("Welcome to Zork!");
    System.out.println("Zork is a new, incredibly boring adventure game.");
    System.out.println("Type 'help' if you need help.");
    System.out.println();
    System.out.println(currentRoom.longDescription());
  }

  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   */
  private boolean processCommand(Command command) {
    if (command.isUnknown()) {
      System.out.println("I don't know what you mean...");
      return false;
    }

    String commandWord = command.getCommandWord();
    if (commandWord.equals("help"))
      printHelp();
    else if (commandWord.equals("go"))
      goRoom(command);
    else if (commandWord.equals("status"))
      stati(command);
    else if (commandWord.equals("quit")) {
      if (command.hasSecondWord())
        System.out.println("Quit what?");
      else
        return true; // signal that we want to quit
    } else if (commandWord.equals("eat")) {
      System.out.println("Do you really think you should be eating at a time like this?");
    }
    return false;
  }

  // implementations of user commands:

  /**
   * Print out some help information. Here we print some stupid, cryptic message
   * and a list of the command words.
   */
  private void printHelp() {
    System.out.println("You are lost. You are alone. You wander");
    System.out.println("around at Monash Uni, Peninsula Campus.");
    System.out.println();
    System.out.println("Your command words are:");
    parser.showCommands();
  }
  public int getKeyStats(String statusInput){
    if (statusInput == "Hunger"){
      return hunger;
    }
    else if (statusInput == "Sanity"){
      return sanity;
    }
    else if (statusInput == "Health"){
      return healthPoints;
    }
    return 0;
  }
  private int finalHungerTick(){
    if (wellbeingHunger == "Ravenous"){
      return (int)((Math.random()*25)+15);
    }
    return 0;
  }

  public String hungerStatus(){
    if (hunger >= 95){
      wellbeingHunger = "Perfect";
      return "You feel sated.";
    }
    else if (hunger >= 55){
      wellbeingHunger = "Decent";
      return "You could go for a snack.";
    }
    else if (hunger >= 35){
      wellbeingHunger = "Hungry";
      return "You are hungry. Eat soon.";
    }
    else if (hunger >= 5){
      wellbeingHunger = "Starving";
      return "You are starving. Find food immediately.";
    }
    else{
    healthPoints-=finalHungerTick();
    wellbeingHunger = "Ravenous";
    return "The world begins to fade around you. If you have scraps, save yourself now.";
    }
  }
  private void hungerPerTurn(){
    int perTurn = (int)((Math.random()*5)+1);
    hunger-=perTurn;
  }

  private void stati(Command command){
    String type = command.getSecondWord();
    type.toLowerCase();
    if (type.equals("hunger")){
      System.out.println(hungerStatus());
    }
    else if (type.equals("sanity")){
      System.out.println("ur fine tough it out kid");
    }
  }


  /**
   * Try to go to one direction. If there is an exit, enter the new room,
   * otherwise print an error message.
   */
  private void goRoom(Command command) {
    if (!command.hasSecondWord()) {
      // if there is no second word, we don't know where to go...
      System.out.println("Go where?");
      return;
    }

    String direction = command.getSecondWord();

    // Try to leave current room.
    Room nextRoom = currentRoom.nextRoom(direction);

    if (nextRoom == null)
      System.out.println("There is no door!");
    else {
      currentRoom = nextRoom;
      System.out.println(currentRoom.longDescription());
      hungerPerTurn();
    }
  }
}
