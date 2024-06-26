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
import javax.print.attribute.standard.MediaSize.NA;
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
  private int healthPoints = 250;
  private int hunger = 100;
  private int sanity = 100;
  private String wellbeingHunger = "Perfect";
  private String wellbeingSanity = "Perfect";
  private boolean heal = false;

  /**
   * Create the game and initialise gits internal map.
   */
  public Game() {
    try {
      coordsort();
      initRooms("src\\zork\\data\\rooms.json");
      //initItems("src\\zork\\data\\items.json"); FIX UR INVENTORY / ITEM PARSER DRAKE AND RYAN!!!!!!!!!!!!!!!!
    
     System.out.println(roomMap.get("Courtyard") + " t1");
      currentRoom = roomMap.get("Courtyard");
       initNPCs("src\\zork\\data\\NPC.json");
      //System.out.println(currentRoom + " t2");

    } catch (Exception e) {
      e.printStackTrace();
    }
    parser = new Parser();

  }

//creates and adds all the NPC to rooms
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
    System.out.println("Thank you for playing. :) ");
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

      //heals player (only one use per a room)
      else if (commandWord.equals("heal")){
      playerHeal();
      }

      
    else if (commandWord.equals("go")){
   
      //if there is a hostile in the room player won't be able to leave
    if (currentRoom.hasHostiles() == true)
    System.out.println("there is a Hostile in the room, you can't leave");
      else{
        goRoom(command);
        heal = false;
      }
    }
      //fights hostile in the room 
    else if (commandWord.equals("fight")){
      playerfight(currentRoom.Ghostile());
    }

    //returns description of hostiles in the room if there are no hostiles returns message
    else if (commandWord.equals("look")){
      if (currentRoom.hasHostiles() == false)
      System.out.println("there are no hostiles in this room");
      else
      HostileDescription(currentRoom.Ghostile());
    }

    else if (commandWord.equals("status"))
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
    //System.out.println(array[0]);

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
    else if (type.equals("health")){
      System.out.println(healthPoints);
    }
  }


  // player heal method heals player by random number between 1 and 100
  private void playerHeal(){
    if (heal == false){
      int Healamount = (int)(Math.random() * 100) + 1; 
      healthPoints += Healamount;
      System.out.println(" with the kingdoms blessing you have healed  " + Healamount );
      heal = true;
    }

    else if (heal == true){
      System.out.println(" you need to rest before you can use your powers again ");
    }

  }



  //player fight function takes hostile as paramtere
 private void playerfight(Hostile hostile){

int Hhealth = hostile.Rhealth();
boolean block = false;
int punkingS = 0;

//this while loop will keep running unit either the player dies or the hostile dies
while(Hhealth > 0 && healthPoints > 0){
  boolean mrbcrit = false;
  boolean MochSlain = false;
  boolean NardP = false;
  boolean MrBHeal = false;
  boolean BossKo = false;
  boolean BossHeal = false;
  boolean BossAttack = false;
  int damage = hostile.fight();
  String RED = "\u001B[31m";
  String RESET = "\u001B[0m";
  String Blue = "\u001B[34m";
  String Purple = "\u001B[45m";
  String Yellow = "\u001B[43m";
  String Green = "\u001B[42m";

// special attacks (calculates the odds of a hostile using a special attack)
  if (hostile.Rname().equals("Mr. B")){
    int rng = (int)(Math.random()*5) + 1;
    int HealRNG = (int)(Math.random() * 3) +1;

    if (rng >= 3 ){
      mrbcrit = true;
      System.out.println(" ");
      System.out.println(hostile.Rname() + " has a terrifying glint in his eye.");

    }

    else if (HealRNG <= 3){
      MrBHeal = true;
      System.out.println(hostile.Rname() + " pulls out a shiny " + Yellow + "trumpet" + RESET);
    }
  }

  else if (hostile.Rname().equals("remains of Manocha")){
    int rngMoch = (int)(Math.random()*2) + 1;
    if (rngMoch == 2 ){
      MochSlain = true;
      System.out.println(hostile.Rname() + " mutters a " + Blue + "Ancient Verse" + RESET);
    }
  }

  else if (hostile.Rname().equals("wandering nard")){
    int rngNard = (int)(Math.random()*3) + 1;
    if (rngNard == 2 ){
      NardP = true;
      System.out.println(" ");
      System.out.println(hostile.Rname() + " dances around as he is preparing for something  " + Purple + " devious " + RESET);
     
    }

  }

  else if (hostile.Rname().equals("The old god of fear and hunger")){
    int rngBossKo = (int)(Math.random()*10) + 1;
    int rngBossHeal = (int)(Math.random()*5) + 1;
    int rngBossAttack = (int)(Math.random()*5) + 1;
    if (rngBossKo == 5 ){
      BossKo = true;
      System.out.println(" ");
      System.out.println(Purple + hostile.Rname() + " prepares for its next strike, you feel the presence of death  " + RESET);
    }

    else if (rngBossHeal == 2 || rngBossHeal == 3 ){
      BossHeal = true;
      System.out.println(" ");
      System.out.println(Green + hostile.Rname() + " begins to meditate as a white aura surrounds the beast " + RESET);
    }

    else if (rngBossAttack == 2 || rngBossAttack == 3 ){
      BossAttack = true;
      System.out.println(" ");
      System.out.println(Yellow + hostile.Rname() + " enters a unknown fighting position, you should be careful " + RESET);
    }

// end special attack
  }
  
// returns if hostile missed or not
  if(damage == 0)
  System.out.println(hostile.Rname() + " has attacked you but missed!");


  //if block is true no damage is taken from hostile
  else if (block == true){
    System.out.println("you have blocked " + hostile.Rname());
      block = false;
  }

  else{
    System.out.println(" ");
    System.out.println(hostile.Rname() + " has smacked you ");
    
    //hostile special attacks (what the actual special attacks do)
    if (punkingS == 2){
      System.out.println("With one last " + RED + " Punking Strike " + RESET + " The wandering Nard blows your head off clean. ");
      System.out.println(" ");
      System.out.println(RED + " you have been punked." + RESET);
      System.exit(0);
    }

    if (BossKo){
      System.out.println(hostile.Rname() + " Strikes you with a cursed technique only known to the old gods. You collapse as your body slowly disintegrate");
      System.out.println(" ");
      System.out.println("all that is left of you is a small pile of ashes." + RED + "you have died" + RESET);
      System.exit(0);
    }

    else if (BossHeal){
      System.out.println("as " + hostile.Rname() + " meditates its wounds start to heal, with every second healing more " );
     System.out.println(" ");
     int heal = (int)(Math.random() * 100) + 1;
     Hhealth += heal; 
    }

    else if (BossAttack){
      System.out.println(hostile.Rname() + " attacks you with several strikes each strike different from the pervious. You try to dodge but its futile" );
     System.out.println(" ");
     int attack = (int)(Math.random() * 100) + 1;
     healthPoints -= attack; 
    }



    if (mrbcrit){
      System.out.println("Mr. B has landed a" + RED + " critical hit! " + RESET + "You feel dread wash over you.");
      healthPoints -= 30;
    }

    else if (MrBHeal){
      System.out.println(" ");
        System.out.println("Mr. B starts dancing around while playing his Trumpet " + Green + "Mr. B heals 15 health" + RESET);
        Hhealth += 10;
        
      
      }

    else if (MochSlain){
      System.out.println(" ");
      System.out.println("with one last strike the Remains of Manocha impales your soul, as you yell in terror your soul shatters. ");
      System.out.println(" ");
    System.out.println(RED +"you are now dead" + RESET);
    System.exit(0);
    }

    else if (NardP){

      System.out.println(" ");
      System.out.println("The wandering Nard has landed a " + RED + " Punking Strike! " + RESET + "  You shiver uncontrollably");
      
      punkingS ++;

    }
    //end of special attack


    //deals the damage to the player
    healthPoints = healthPoints - damage;
   
  }
  


    // give options
    System.out.println("what would you like to do");
    System.out.println(" ");
    System.out.println("1 to attack ");
    System.out.println(" ");
    System.out.println("2 to heal ");
    System.out.println(" ");
    System.out.println("3 to block");
    int player = parser.getOption(1, 2, 3);
 
// deals damage to hostile
      if (player == 1){
        int playerD = 45;
        System.out.println(" ");
        System.out.println(" you have attacked " + hostile.Rname() );
        Hhealth = Hhealth - playerD;
      }
// heals player from a random number from 1 to 100
      if (player == 2){
        int Pheal = (int)(Math.random() * 100) + 1; 
        healthPoints += Pheal;
        System.out.println(" ");
        System.out.println(" using magical skills you have healed " + Pheal);
      }
//blocks next landed hostile attack 
      if (player == 3){
        System.out.println(" you have blocked ");
            block = true;           
      }

      //displays health of player and hostile after both player and hostile went 
      System.out.println(" ");
      System.out.println("you have " + healthPoints + " health points");
      System.out.println(hostile.Rname() + " has " + Hhealth + " health points");
    
    }

    // message that pops up after you killed hostile or the boss
      if (Hhealth <= 0){
        if (hostile.Rname().equals("The old god of fear and hunger")){
          System.out.println("With one last blow you have slain the king of the dugeon, congrats you have conquered the dungeon of fear and hunger ");
          System.out.println(" ");
          System.out.println("you now may leave the dungeon of fear and hunger");
          System.out.println(" ");
          System.out.println("type quit to leave.");
          System.out.println("or you can stick around ;)");
          currentRoom.RemoveH();
        }

        else{
        System.out.println(" ");
      System.out.println(" you have slain " + hostile.Rname());
        currentRoom.RemoveH();
        }
        


        
      }
      //different death messages randomized 

      else if (healthPoints <= 0){
        int deathM = (int)(Math.random() * 100) + 1;
        
        if (deathM <= 20){
     System.out.println("Your neck snaps from " + hostile.Rname() + " attack, you are now dead.");
     System.exit(0);    
    }

     else if (deathM <= 40){
     System.out.println(hostile.Rname() + " has ripped your arms off, blood gushes out as you start to lose vision. You have been killed ");
        System.exit(0);
     }

     else if (deathM <= 60){
      System.out.println("You feel your bones shatter as the " + hostile.Rname() + " jaw clamp down, tearing you limb from limb in a grotesque feast. What a tragic death" );
         System.exit(0);
      }

      else if (deathM <= 80) {
        System.out.println("you scream as " + hostile.Rname() + "  crushes your chest, rupturing organs and splintering bones. You die drowning in your own blood." );
           System.exit(0);
        }

        else {
          System.out.println(hostile.Rname() + " knocks you unconscious, you are then eaten alive");
          System.exit(0);
        }

    }
  }

  // histile description method returns the description of the hostile in the room 

private void HostileDescription(Hostile hostile){
if (currentRoom.hasHostiles()){
  System.out.println(hostile.Rdescription());
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