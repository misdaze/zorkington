package zork;

public class Hostile extends NPC {
    
    private String description; 
    private int health;
    private int agility;
    private String Name;


    public Hostile(String n, String d, int h, int a){
        description = d;
        health = h;
        agility = a;
        Name = n; 
    }

    public void fight(){

    }


    

}
