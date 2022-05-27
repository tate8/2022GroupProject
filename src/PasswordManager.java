import java.security.*;
import javax.crypto.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.spec.SecretKeySpec;
import java.lang.Object.*;
import javax.crypto.spec.IvParameterSpec;

public class PasswordManager {

  // convert password from string to byte array, call MessageDigest.digest on byte array, call MessageDigest.toString on byte array
  public String hash(String password) throws NoSuchAlgorithmException {     
    MessageDigest digest = MessageDigest.getInstance("SHA3-256");
    byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));

    String shaHash = Base64.getEncoder().encodeToString(hashBytes);
    return shaHash;
  }

  /*
   Use a encrypting key in the .env file to encrypt a string and return it
  */
  public String encrypt(String password, IvParameterSpec iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {

    // using encryptKey to kind of seed the encrypting algorithm
    String encryptKey = System.getProperty("ENCRYPT_KEY");
    // String encryptKey = System.getenv("ENCRYPT_KEY");

    byte[] keyByteBuffer = new byte[16];
    byte[] encryptKeyBytes = encryptKey.getBytes();

    for (int i = 0; i < encryptKeyBytes.length; i++)
    {
      keyByteBuffer[i] = encryptKeyBytes[i];
    }

    SecretKeySpec secretKeySpec = new SecretKeySpec(keyByteBuffer, "AES");
    
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
    byte[] cipherText = cipher.doFinal(password.getBytes());
    return Base64.getEncoder().encodeToString(cipherText);
  }

  
  public String decrypt(String password, IvParameterSpec iv)  throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    
    // String encryptKey = System.getenv("ENCRYPT_KEY");
    String encryptKey = System.getProperty("ENCRYPT_KEY");

    byte[] keyByteBuffer = new byte[16];
    byte[] encryptKeyBytes = encryptKey.getBytes();

    for (int i = 0; i < encryptKeyBytes.length; i++)
    {
      keyByteBuffer[i] = encryptKeyBytes[i];
    }
    
    SecretKeySpec secretKeySpec = new SecretKeySpec(keyByteBuffer, "AES");
    
    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
    byte[] plainText = cipher.doFinal(Base64.getDecoder().decode(password));
  return new String(plainText);

  }
}
