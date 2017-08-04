import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
/**
 * A collection of methods representing the actions that Alice can perform
 * @author Achilleas Rousiamanis
 *
 */
public class Alice {
	
	private final static int PUZZLE_NO = 1024;//The number of puzzles to create
	private final static int PUZZLE_SIZE = 26;//The size of each puzzle
	/**
	 * Generates the puzzles
	 * @return A 2D byte array with dimensions of 1024*26 representing the generated plaintext puzzles.
	 */
	public static byte[][] generatePuzzles(){
		System.out.println("Generating puzzles.....");
		byte[][] puzzles = new byte[PUZZLE_NO][PUZZLE_SIZE];
		
		for(int count=0; count<PUZZLE_NO; count++){
			
			Puzzle puzzle = new Puzzle();
			puzzles[count] = puzzle.generatePuzzle();
			
		} 
		System.out.println("Puzzle generated!!");
		return puzzles;
	}
	/**
	 * Encodes all 1024 puzzles
	 * @param puzzles A 2D array of bytes with dimensions 1024*26 containing all puzzles that are to be encrypted
	 * @return A 2D byte array with dimensions 1024*26 containing all the encrypted puzzles
	 */
	public static byte[][] encodePuzzles(byte[][] puzzles){
		byte[][] encoded = new byte[PUZZLE_NO][PUZZLE_SIZE];
		SecureRandom rnd = new SecureRandom();
		SecretKey[] puzzleKey = new SecretKey[PUZZLE_NO];
		System.out.println("Ecrypting puzzles.....");
		
		for(int i=0; i<puzzles.length; i++){
			try {
				byte[] keyBytes = new byte[2];//a byte array of size 2 containing the key bytes
				rnd.nextBytes(keyBytes);
				byte[] zeroBytes = new byte[6];//a zero byte array of size 6 
				Arrays.fill(zeroBytes, (byte)0);
				
				//Appends the keyBytes and zeroBytes to construct the actual key
				byte[] keyArray = new byte[keyBytes.length + zeroBytes.length];
				System.arraycopy(keyBytes, 0, keyArray, 0, keyBytes.length);
				System.arraycopy(zeroBytes, 0, keyArray, keyBytes.length, zeroBytes.length);
				puzzleKey[i] = CryptoLib.createKey(keyArray);
				Cipher eCipher = Cipher.getInstance("DES");
				
				//Encrypts the puzzle using DES
				eCipher.init(Cipher.ENCRYPT_MODE, puzzleKey[i]);
				encoded[i] = eCipher.doFinal(puzzles[i]);
				encoded[i] = Base64.getEncoder().encode(encoded[i]);
				
			} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
			} catch (InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return encoded;
	}
	
	/**
	 * Scans all of the puzzles to find the one with the given key
	 * @param puzzle A 2D byte array containing all the plaintext puzzles to scan
	 * @param puzzleNo An int representing the puzzle number that we are searching for 
	 * @return  A byte array of size 8 representing representing the extracted key
	 */
	public static byte[] findPuzzleKeyByNo(byte[][] puzzle,int puzzleNo){
		byte[] thisKey = new byte[puzzle[0].length];
		System.out.println("Searching for puzzle no "+puzzleNo+".....");
		for(int i=0; i<puzzle.length; i++){
			byte[] thisNo = Arrays.copyOfRange(puzzle[i], 16, 18);//extracted puzzle number
			int thisPuzzleNo = CryptoLib.byteArrayToSmallInt(thisNo);
			
			//If current puzzle number is equal to the given one then it extracts the key from the current puzzle
			if(thisPuzzleNo==puzzleNo){
				thisKey = Arrays.copyOfRange(puzzle[i], 18, puzzle[i].length);
			}
		}
		System.out.println("Extracted key length: "+thisKey.length);
		return thisKey;
	}
	
	/**
	 * Encrypts the string "HeyBobHowAreYouDoing" given a SecretKey. For any message with a byte size 
	 * different to a product of 8 padding is added and, when decrypted, the last characters are altered.
	 * @param key A DES key used to encrypt the string
	 * @return The encrypted string 
	 */
	public static String sendMessage(SecretKey key){
		String msg = "HeyBobHowAreYouDoing";//the message that is to be encrypted
		System.out.println("Sending message: "+msg);
		byte[] msgBytes = CryptoLib.stringToByteArray(msg);
		byte[] encodedMsg = null;
		try {
			//Initialise DES encryption using the given key
			Cipher eCipher = Cipher.getInstance("DES");
			eCipher.init(Cipher.ENCRYPT_MODE, key);
			//Encrypt message 
			encodedMsg = eCipher.doFinal(msgBytes);
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
		return CryptoLib.byteArrayToString(encodedMsg);
		
	}

}
