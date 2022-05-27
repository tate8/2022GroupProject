import java.io.*;
import java.util.*;
import javax.crypto.spec.IvParameterSpec;

public class FileManager {
  
  public void saveToFile(String siteName, String passwordEncrypted, String hash) throws FileNotFoundException { 
    String saveState = siteName + " : " + passwordEncrypted + "\n";
    File pass = new File("pass.dat");
    Scanner in = new Scanner(pass);
  
    String temp = "";
    String beforeContents = "";
    String afterContents = "";
    boolean found = false;

    
    while(in.hasNextLine()) {
      temp = in.nextLine();
      if (found == true) {
        afterContents = afterContents + temp + "\n";
      } else {
        beforeContents = beforeContents + temp + "\n";
      }
      if(temp.equals(hash)) {
        found = true;
      } 
    }

    afterContents = afterContents.substring(0, afterContents.length()-2);
    PrintWriter outPut = new PrintWriter(pass);
    outPut.println(beforeContents + saveState + afterContents);
    in.close();
    outPut.close();
    return;
  }

  /*
    Fetch all info from file
    Decrypt passwords
    Format into a ArrayList<String>
  */
  public ArrayList<String> getAllInfo(String hash, IvParameterSpec iv) throws FileNotFoundException {
    ArrayList<String> outPut = new ArrayList<String>();
    File pass = new File("pass.dat");
    Scanner in = new Scanner(pass);
    
    String passwordHash;
    String splitter = "";
    
    String temp = in.nextLine();
    boolean found = false;

    while(in.hasNextLine()){
      if(temp.equals(hash)){
        found = true;
      } else if(temp.equals("TwistCrystalSubwayBehaviorClassifyOriginTentDressSandQualified") && found == true) {
        in.close();
        return outPut;       
      } else if(found == true) {
        splitter = temp;
        String a = "";
        for(int i = 0; i < splitter.length(); i ++){
          if(splitter.substring(i, i + 1).equals(":")){
           
            passwordHash = splitter.substring(i+2, splitter.length());
            //decode
          outPut.add(splitter.substring(0, i-1) + " : " + decode(passwordHash, iv));
            outPut.add("\n");
          }
        }
      }
      temp = in.nextLine();
    }

    in.close();
    return outPut;
  }

  public String decode(String passHash, IvParameterSpec iv) {
    PasswordManager pm = new PasswordManager();
    String sendingBack = passHash;
    try {
      sendingBack = pm.decrypt(sendingBack, iv);
    } catch (Exception e) {
      System.out.println("Exception: " + e);
      System.exit(0);
    } 
    return sendingBack;
  }

  // below methods handle client IDs
  // (these are the IDs used to sign in to have access to their other passwords)

  // functionality to save userHash to a datafile
  public void saveUserIDHash(String userIDHash) throws FileNotFoundException {
    File pass = new File("pass.dat");
    String temp = "";
    Scanner in = new Scanner(pass);
    while(in.hasNextLine()){
      temp = temp + in.nextLine() + "\n";
    }
    in.close();

    PrintWriter out = new PrintWriter(pass);
    out.println(temp);
    out.println(userIDHash + "\nTwistCrystalSubwayBehaviorClassifyOriginTentDressSandQualified");
    out.close();
    return;
  }

  // check if hash is present in this applications users
  public boolean hashPresent(String hash) throws FileNotFoundException {
    System.out.println(hash);
    File pass = new File("pass.dat");
    Scanner in = new Scanner(pass);

    String temp = in.nextLine();
    boolean found = false;

    while(in.hasNextLine() && found == false){
      if(temp.equals(hash)){
        found = true;
      }
      temp = in.nextLine();
    }

    in.close();
    return found;
  }
}