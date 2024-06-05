package zork;

public class Item extends OpenableObject {
  public int weight;
  public String name;
  public boolean isOpenable;
  public String description;
  public String itemId;
  public String type;
  public String roomId;
  public Item(int weight, String name, Boolean isOpenable, String description, String itemId, String type, String roomId){
    this.weight = weight;
    this.name = name;
    this.isOpenable = isOpenable;
    this.description = description;
    this.itemId = itemId;
    this.type = type;
    this.roomId = roomId;
  }
  


public void open() {
    if (!isOpenable)
      System.out.println("The " + name + " cannot be opened.");

  }

  public int getWeight() {
    return weight;
  }

  public void setWeight(int weight) {
    this.weight = weight;
  }

  public String getDescription(){
    return description;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isOpenable() {
    return isOpenable;
  }

  public void setOpenable(boolean isOpenable) {
    this.isOpenable = isOpenable;
  }

}
