package zork;

public class Hostile {
    
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

    public String Rdescription(){
        return description;
    }
// fight method (determines if likely hood of a attack landing and the damage delt by the attack according to json )
    public int fight(){
    int attacklanded = (int)(Math.random()*50)+1; 
        attacklanded = attacklanded += 0.3*agility;

    if (attacklanded >= 50){
        int attackdamage = 15 + strength;
      return attackdamage;

    }
    else 
    return 0;  
    }




    

}
