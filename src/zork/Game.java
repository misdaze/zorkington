package zork;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.List;

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
   * Create the game and initialise gits internal map.
   */
  public Game() {
    try {
      coordsort();
      initRooms("src\\zork\\data\\rooms.json");
      //initItems("src\\zork\\data\\items.json"); FIX UR INVENTORY / ITEM PARSER DRAKE AND RYAN!!!!!!!!!!!!!!!!
      //initNPCs("src\\zork\\data\\NPC.json"); FIX UR NPC PARSER TING!!!! I CANT TEST THE GAME!!!! DAMN!!!!!!
     // System.out.println(roomMap.get("Courtyard") + " t1");
      currentRoom = roomMap.get("Courtyard");
      //System.out.println(currentRoom + " t2");

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
      int aglity = Integer.parseInt((String) ((JSONObject) npcObj).get("aglity"));
      int health =  Integer.parseInt((String) ((JSONObject) npcObj).get("health"));
      int strength =  Integer.parseInt((String) ((JSONObject) npcObj).get("strength"));


    if (npcType.equals("0")){
      Hostile hostile = new Hostile(name, description, health, aglity, strength);
      roomMap.get(roomId).addNPC(hostile);
    }

    else {
      Trader trader = new Trader(name, description);
      roomMap.get(roomId).addNPC(trader);

    }
     
    }
  }
  private int[] coordsgetter(int map) throws IOException, ParseException{
    Path path = Path.of("src\\zork\\data\\rooms.json");
    String jsonString = Files.readString(path);
    JSONParser parser = new JSONParser();
    JSONObject json = (JSONObject) parser.parse(jsonString);
    JSONArray jsonRooms = (JSONArray) json.get("rooms");
    JSONObject id = (JSONObject) jsonRooms.get(map);
   // int mapnum = (int)  ((JSONObject) id).get("mapIdentifier");
      Long xL = (Long)((JSONObject) id).get("x1");
      Long xxL = (Long)((JSONObject) id).get("x2");
      Long yL = (Long)((JSONObject) id).get("y1");
      Long yyL = (Long)((JSONObject) id).get("y2");
      int x = Math.toIntExact(xL);
      int xx = Math.toIntExact(xxL);
      int y = Math.toIntExact(yL);
      int yy = Math.toIntExact(yyL);
      int[] a = {x,xx,y,yy};
      return a;
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
      Long xL = (Long)((JSONObject) roomObj).get("mapIdentifier");
      int x = Math.toIntExact(xL);
      room.setDescription(roomDescription);
      room.setRoomName(roomName);
      room.setroomnum(x);

      JSONArray jsonExits = (JSONArray) ((JSONObject) roomObj).get("exits");
      //System.out.println(jsonExits);
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
    //  System.out.println(roomName);
    }
  }

 private void initItems(String itemFileName) throws Exception{
  Path path = Path.of(itemFileName);
  String JsonString = Files.readString(path);
  JSONParser parser = new JSONParser(); 
  JSONObject json = (JSONObject) parser.parse(JsonString);

  JSONArray jsonitems = (JSONArray) json.get("items");

  for(Object itemObj : jsonitems){
    String id = (String) ((JSONObject) itemObj).get("id");
    String name = (String) ((JSONObject) itemObj).get("name");
    String desc = (String) ((JSONObject) itemObj).get("description");
    String room_id = (String) ((JSONObject) itemObj).get("room_id");
    String itemtype = (String) ((JSONObject) itemObj).get("type");
    int weight = Integer.parseInt((String) ((JSONObject) itemObj).get("weight"));
    Boolean isOpenable = Boolean.parseBoolean((String) ((JSONObject) itemObj).get("isOpenable"));
    Item item = new Item(weight, name, isOpenable, desc, id, itemtype, room_id);
   // if (room_id != null)
     // roomMap.get(room_id).getInventory().addItem(item);
}
 } 
  /**
   * Main play routine. Loops until end of play.
   * @throws ParseException 
   */
  public void play() throws ParseException {
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
    System.out.println("Thank you for being freaky.  Good boy.");
  }

  /**
   * Print out the opening message for the player.
   */
  private void printWelcome() {
    System.out.println();
    System.out.println("Welcome to Zerk!");
    System.out.println("Zerk is a new, incredibly freaky adventure game.");
    System.out.println("Type 'freak' if you need ''Help'' 😏.");
    System.out.println();
    //System.out.println("Working Directory = " + System.getProperty("user.dir"));
    // mappings();
    //System.out.println(currentRoom.longDescription());
  }

  /**
   * Given a command, process (that is: execute) the command. If this command ends
   * the game, true is returned, otherwise false is returned.
   * @throws ParseException 
   * @throws IOException 
   */
  private boolean processCommand(Command command) throws IOException, ParseException {
    if (command.isUnknown()) {
      System.out.println("I don't know what you mean...");
      return false;
    }

    String commandWord = command.getCommandWord();
    if (commandWord.equals("help"))
      printHelp();
    else if (commandWord.equals("go")){
      if (currentRoom.hasHostiles()){
        System.out.println("The hostile won't let you leave.");
      }else{
        goRoom(command);
      }
    }else if (commandWord.equals("status"))
      stati(command);
      else if (commandWord.equals("map"))
      mappings();
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
  public ArrayList<int[]> things = new ArrayList<int[]>();
  private void coordsort() throws IOException, ParseException {
    for (int i = 0; i < 17; i++) {
      if (coordsgetter(i) != null){
        things.add(coordsgetter(i));
      }
    }
  }
  
  private void mappings() throws IOException, ParseException {
    //System.out.println(currentRoom);
   // ArrayList<List> one = new ArrayList<List>();
   
    int id = currentRoom.getNum();
    int[] array = things.get(id-1);
    System.out.println(array[0]);
    String dir = System.getProperty("user.dir");
    File file_open = new File(dir+"\\image.jpg");  
    Path copied = Paths.get(dir+"\\SACRIFICE.jpg");
    Path originalPath = file_open.toPath();
    File copycheck = new File(dir+"\\SACRIFICE.jpg");

    Files.deleteIfExists(copycheck.toPath());
    Files.copy(originalPath, copied, StandardCopyOption.COPY_ATTRIBUTES);

      BufferedImage img = ImageIO.read(copycheck);
      BufferedImage readtomap = new BufferedImage(
      img.getWidth(),img.getHeight(), BufferedImage.TYPE_INT_ARGB);   
      Graphics2D g = readtomap.createGraphics();
      g.drawImage(img, 0, 0, null);
      g.dispose();
      BufferedImage subimg = readtomap.getSubimage(array[0], array[2], (array[1]-array[0]), (array[3]-array[2]));
      JFrame frame = new JFrame("Minimap");
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setAlwaysOnTop(true);
        frame.getContentPane().add(new JLabel(new ImageIcon(subimg)));
        frame.pack();

  } 
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



 private void playerfight(Hostile hostile){

int Hhealth = hostile.Rhealth();

while(Hhealth > 0 && healthPoints > 0){
  int damage = hostile.fight();
    if (damage > 0)
    System.out.println(hostile.Rname() + "has smacked you");
    healthPoints = healthPoints - damage;
    // give options
    System.out.println("what would you like to do");
    System.out.println("1 to attack ");
    System.out.println("2 to block");
    int player = parser.getOption(1, 2);
    // if (player = 1){
    // s
    
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

    if (nextRoom == null){
      System.out.println("There is no door!");
      System.out.println("Your exits are: " + currentRoom.exitString());
    }
    else {
      currentRoom = nextRoom;
      System.out.println(currentRoom.longDescription());
      hungerPerTurn();
    }
  }
}
