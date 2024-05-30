package zork;

public class Hostile extends NPC {
    
    private String description; 
    private int health;
    private int agility;
    private String Name;
    private int strength;


    public Hostile(String n, String d, int h, int a, int s){
        description = d;
        health = h;
        agility = a;
        Name = n; 
        strength = s;
    }

    public String Rname(){
        return Name;
    }

    public int Rhealth(){
        return health;
    }

    public int fight(){
    int attacklanded = (int)(Math.random()*50)+1;
        attacklanded = attacklanded += 0.3*agility;

    if (attacklanded >= 50){
        int attackdamage = 10 + strength;
      return attackdamage;

    }
    else 
    return 0;  
    }




    

}
