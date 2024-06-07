package zork;

import org.json.simple.parser.ParseException;

public class Zork {
  public static void main(String[] args) throws ParseException {
    Game game = new Game();
    game.play();
  }
}
