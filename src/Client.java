import java.util.Scanner;
import java.security.*;
import java.io.*;
import javax.crypto.spec.IvParameterSpec;

/*
The Client class handles the sign in of users and the
creation of user accounts
It also handles browsing passwords and adding passwords
If a user is authenticated, userAuth will be 'true'
*/
public class Client
{
  private PasswordManager passwordManager;
  private FileManager fileManager;
  private boolean userAuth;
  private String userIDHash;
  private Scanner sc;
  private IvParameterSpec iv;
  
  public Client()
  {
    passwordManager = new PasswordManager();
    fileManager = new FileManager();
    userAuth = false;
    userIDHash = "";
    sc = new Scanner(System.in); 

    // create IV
    byte[] seedBuffer = System.getProperty("ENCRYPT_KEY").getBytes();
    byte[] ivBuffer = new byte[16];
    for (int i=0; i<seedBuffer.length;i++) {
      ivBuffer[i] = seedBuffer[i];
    }
    iv = new IvParameterSpec(ivBuffer);
  }
  public void setUserAuth(boolean newState)
  {
    userAuth = newState;
  }
  public boolean getUserAuth()
  {
    return userAuth;
  }
  
  public void signIn()
  {
    System.out.println("\033[H\033[2J"); // clear terminal
    System.out.println("" +
"   _____ _               _____       \n" +
"  / ____(_)             |_   _|      \n" +
" | (___  _  __ _ _ __     | |  _ __  \n" +
"  \\___ \\| |/ _` | '_ \\    | | | '_ \\ \n" +
"  ____) | | (_| | | | |  _| |_| | | |\n" +
" |_____/|_|\\__, |_| |_| |_____|_| |_|\n" +
"            __/ |                    \n" +
"           |___/                     \n" +
"");
    System.out.print("Enter your userID: ");
    String userID = sc.nextLine();

    try {
      // hash and maybe salt ID
      String hash = passwordManager.hash(userID);
  
      // check for hash in datafile
      boolean signInSuccessful = fileManager.hashPresent(hash);
  
      // if not present, restart auth process
      if (!signInSuccessful)
      {
        System.out.println("Sign in unsuccessful\nTry again? ('y'/'n')");
        char option = sc.next().charAt(0);
        sc.nextLine();
        switch (option)
        {
          case 'y':
            startAuth();
            break;
          case 'n':
            System.exit(0);
          default:
            System.exit(0);
        }
      }
      else
      {
        // sign in successful
        this.userIDHash = hash;
        setUserAuth(true);
      }
    } catch (Exception e) {
      System.out.println("Error, Exception: " + e);
    }
  }

  
  public void createAccount()
  {
    System.out.println("\033[H\033[2J");
    System.out.println("" +
"   _____                _                                           _   \n" +
"  / ____|              | |           /\\                            | |  \n" +
" | |     _ __ ___  __ _| |_ ___     /  \\   ___ ___ ___  _   _ _ __ | |_ \n" +
" | |    | '__/ _ \\/ _` | __/ _ \\   / /\\ \\ / __/ __/ _ \\| | | | '_ \\| __|\n" +
" | |____| | |  __/ (_| | ||  __/  / ____ \\ (_| (_| (_) | |_| | | | | |_ \n" +
"  \\_____|_|  \\___|\\__,_|\\__\\___| /_/    \\_\\___\\___\\___/ \\__,_|_| |_|\\__|\n" +
    "");
    System.out.print("Enter a new userID: ");
    String newUserID = sc.nextLine();

    try {
      String newUserIDHash = passwordManager.hash(newUserID);
      fileManager.saveUserIDHash(newUserIDHash);

      // now that the new ID is present, the user will be able to sign in
      signIn();
    } catch (Exception e) {
      System.out.println("Error, Exception: " + e);
    }
    
  }

  public void startAuth()
  {
    this.userIDHash = "";
    System.out.println("\033[H\033[2J"); // clear terminal
    System.out.println("Do you have an account? ('y'/'n')");
    String input = sc.next().charAt(0) + "";
    sc.nextLine();
    if (input.equals("y"))
    {
      signIn();
    }
    else if (input.equals("n"))
    {
      createAccount();
    }
    else
    {
      System.exit(0);
    }
  }

  /*
    User is authenticated in mainLoop()
    Allows user to browse sites and passwords
    Allows user to add new sites and passwords
  */
  public void mainLoop()
  {
    while (true)
    {
      System.out.println("\033[H\033[2J");
      System.out.println("" +
"  ___                              _   __  __                             \n" +
" | _ \\__ _ _______ __ _____ _ _ __| | |  \\/  |__ _ _ _  __ _ __ _ ___ _ _ \n" +
" |  _/ _` (_-<_-< V  V / _ \\ '_/ _` | | |\\/| / _` | ' \\/ _` / _` / -_) '_|\n" +
" |_| \\__,_/__/__/\\_/\\_/\\___/_| \\__,_| |_|  |_\\__,_|_||_\\__,_\\__, \\___|_|  \n" +
"                                                            |___/         \n" +
      "");
      System.out.println("Enter '1' to browse passwords or '2' to add a new password. To quit press '3'.");
  
      int choice = sc.nextInt();
      sc.nextLine();
      switch (choice)
      {
        case 1:
          displayInfo();
          break;
        case 2:
          addNewPassword();
          break;
        default:
          System.exit(0);
      }  
    }
  }

  public void displayInfo()
  {
    try {
      for(String entry : fileManager.getAllInfo(this.userIDHash, iv))
      {
        System.out.println(entry);
      }
    } catch (Exception e) {
      
      System.out.println(e);
    }
    System.out.println("Press enter to stop browsing...");
    // will wait till enter pressed
    sc.nextLine();
  }
  
  public void addNewPassword()
  {
    System.out.println("Enter new site name: ");
    String site = sc.nextLine();
    
    System.out.println("Enter a new password for " + site + ": ");
    String password = sc.nextLine();
    try {
      String passwordEncrypted = passwordManager.encrypt(password, iv);
      fileManager.saveToFile(site, passwordEncrypted, this.userIDHash);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      System.exit(0);
    }
  }

  protected void destructor() {  
    sc.close(); 
  }  
}