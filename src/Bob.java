import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * A collection of methods that represent actions that Bob can perform
 * 
 * @author Achilleas Rousiamanis
 *
 */
public class Bob {

	private final static double MAX_POSS_KEYS = Math.pow(2, 16);// The maximum amount of possible keys to try
	public static int index; // The index of the selected puzzle

	/**
	 * Decrypts a string using the given DES key
	 * 
	 * @param msg The encrypted string to decrypt
	 * @param key The DES key used to decrypt the string
	 * @return The decrypted string
	 */
	public static String decryptMsg(String msg, SecretKey key) {
		Cipher dCipher;
		String decodedMsg = null;
		try {
			// Initialises DES decryption with given key
			dCipher = Cipher.getInstance("DES");
			dCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] bytes = CryptoLib.stringToByteArray(msg);

			// Decrypts the byte array and converts it back to string
			byte[] decoded = dCipher.doFinal(bytes);
			decodedMsg = CryptoLib.byteArrayToString(decoded);
		} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BadPaddingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return decodedMsg;

	}

	/**
	 * Uses brute force. Tries all possible 2^16 keys to decrypt the given
	 * string
	 * 
	 * @param puzzle The string to decrypt
	 * @return The decrypted string
	 */
	public static String decryptPuzzle(String puzzle) {
		byte[] poss_decoded = null;// a decrypted puzzle(can be wrong)
		byte[] bytes = null;
		ArrayList<String> poss_decodedMsg = new ArrayList<String>();// An arralist containing the correctly decrypted puzzles
		SecureRandom rnd = new SecureRandom();
		Cipher dCipher;
		try {
			// Initialises DES decryption
			dCipher = Cipher.getInstance("DES");

			System.out.println("Decrypting.....");

			for (int i = 0; i < MAX_POSS_KEYS; i++) {
				try {
					//Constructs an array of bytes that follows the same pattern of the key that was used to encrypt the puzzle
					byte[] keyBytes = new byte[2]; //the actual key bytes
					byte[] zeroBytes = new byte[6]; //the 6 zero bytes at the end of the key
					byte[] keyArray = new byte[keyBytes.length + zeroBytes.length]; 
					rnd.nextBytes(keyBytes);
					Arrays.fill(zeroBytes, (byte) 0);
					
					//Appends the keyBytes and zeroBytes arrays to construct the possible key
					System.arraycopy(keyBytes, 0, keyArray, 0, keyBytes.length);
					System.arraycopy(zeroBytes, 0, keyArray, keyBytes.length, zeroBytes.length);
					SecretKey possibleKey = CryptoLib.createKey(keyArray);
					
					//attempts to decrypt the puzzle using the key constructed
					dCipher.init(Cipher.DECRYPT_MODE, possibleKey);
					bytes = CryptoLib.stringToByteArray(puzzle);
					poss_decoded = dCipher.doFinal(bytes);
					boolean areZeroes = true;
					byte[] firstbytes = Arrays.copyOfRange(poss_decoded, 0, 16);// the first 16 bytes of the decrypted byte array
					
					//Checks if the first 16 bytes are zeros
					for (int count = 0; count < firstbytes.length; count++) {
						if (firstbytes[count] != 0) {
							areZeroes = false;
						}
					}
					
					//If the first 16 bytes are zeros then adds the current decrypted puzzle to an ArrayList
					if (areZeroes) {
						poss_decodedMsg.add(CryptoLib.byteArrayToString(poss_decoded));
					}

				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BadPaddingException e) {
					// TODO Auto-generated catch block
					// e.printStackTrace();
				}
			}

		} catch (NoSuchAlgorithmException | NoSuchPaddingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		boolean areEqual = true;
		String first = poss_decodedMsg.get(0);
		for (int i = 1; i < poss_decodedMsg.size(); i++) {
			if (poss_decodedMsg.get(i) != first) {
				areEqual = false;
			}
		}
		if (areEqual) {
			System.out.println("Decrypted text: " + first);
		}
		return first;
	}

	/**
	 * Extracts the key from a given puzzle
	 * 
	 * @param puzzle A string representing the puzzle
	 * @return A string representing the puzzles key
	 */
	public static String extractKey(String puzzle) {
		System.out.println("Extracting key.....");
		byte[] puzzleBytes = CryptoLib.stringToByteArray(puzzle);
		byte[] keyBytes = Arrays.copyOfRange(puzzleBytes, 18, puzzleBytes.length);
		return CryptoLib.byteArrayToString(keyBytes);
	}

	/**
	 * Extracts the puzzle number from the given puzzle
	 * 
	 * @param puzzle A string representing the puzzle
	 * @return An integer representing the puzzle number
	 */
	public static int extractPuzzleNo(String puzzle) {
		System.out.println("Extracting puzzle number.....");
		byte[] puzzleBytes = CryptoLib.stringToByteArray(puzzle);
		byte[] puzzleNo = Arrays.copyOfRange(puzzleBytes, 16, 18);
		return CryptoLib.byteArrayToSmallInt(puzzleNo);

	}

	/**
	 * Selects a random puzzle from the given file by selecting a random line
	 * 
	 * @param file The file from which it gets a random line
	 * @return The string representing the selected puzzle
	 */
	public static String selectRandomLine(File file) {
		String result = null;
		try {
			FileInputStream in = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			ArrayList<String> array = new ArrayList<>();
			String line;
			//reads every line from the file and adds them to an ArrayList
			while ((line = reader.readLine()) != null) {
				array.add(line);
			}
			
			//Selects a random line
			Random rnd = new Random();
			int randomIndx = rnd.nextInt(array.size());
			index = randomIndx - 1;
			result = array.get(index);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

}
