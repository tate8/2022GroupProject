/*
  First: design public API, then implement actual backend functionality

  TODO:
    1.
      find library to hash, encrypt, and decrypt strings
      Implement library functionality in respective methods in PasswordManager.java
    2.
      Implement functionality to save Strings to file in FileManager.java
      (it would be nice if they weren't visible as txt bc then you could just try every hash and they would be pointless)
    3.
      Reduce number of Scanner creations (make member variable). Use destructor for Scanner.close()
*/

public class Main
{
  public static void main(String[] args) {
    System.setProperty("ENCRYPT_KEY", "32233249871");
    Client client = new Client();   
    
    do {
      client.startAuth();
    } while(!client.getUserAuth());

    // At this point the user is authenticated

    client.mainLoop();

    client.destructor(); 
  }
}