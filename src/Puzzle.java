import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * This class represents the Puzzle object that is used to generate a single puzzle
 * @author Achilleas Rousiamanis
 *
 */
public class Puzzle {
	
	public static byte[] zeros;//The zero bytes in the puzzle
	public static byte[] puzzleNo;//The bytes representing the puzzle number
	public static byte[] keyData;// The bytes representing the puzzle key
	
	/**
	 * The Puzzle object constructor which initialises the byte arrays above.
	 */
	public Puzzle(){
		
		zeros = new byte[16]; 
		puzzleNo = new byte[2];
		keyData = new byte[8];
	}
	
	/**
	 * Generates a single puzzle by appending the zero, puzzleNo and keyData byte arrays.
	 * @return A byte array of size 26 representing the puzzle.
	 */
	public byte[] generatePuzzle(){
		SecretKey key;
		Arrays.fill(zeros, (byte)0);
		try {
			int number = SecureRandom.getInstanceStrong().nextInt(65536);
			puzzleNo = CryptoLib.smallIntToByteArray(number);
			key = KeyGenerator.getInstance("DES").generateKey();
			keyData = key.getEncoded();
		} catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		byte[] puzzle = new byte[zeros.length + puzzleNo.length + keyData.length];
		System.arraycopy(zeros, 0, puzzle, 0, zeros.length);
		System.arraycopy(puzzleNo, 0, puzzle, zeros.length, puzzleNo.length);
		System.arraycopy(keyData, 0, puzzle, (zeros.length + puzzleNo.length), keyData.length);
		
		return puzzle;
	}

}
