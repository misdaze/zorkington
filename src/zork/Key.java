package zork;

public class Key extends Item {
  private String keyId;

  public Key(String keyId, int weight, String keyName,  String description, String itemId, String type, String roomId) {
    super(weight, keyName, false, description, itemId, type, roomId);
    this.keyId = keyId;
  }

  public String getKeyId() {
    return keyId;
  }
}
