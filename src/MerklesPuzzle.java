import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

import javax.crypto.SecretKey;
/**
 * The main class that demonstrates that Merkle's puzzle is working
 * @author Achilleas Rousiamanis
 *
 */
public class MerklesPuzzle {
	
	public static void main(String[] args){
		System.out.println("-----ALICE-----");
		//Alice's turn: She generates and then encodes the puzzles
		byte[][] puzzles = Alice.generatePuzzles();
		byte[][] encoded = Alice.encodePuzzles(puzzles);
		
		//writes encoded puzzles to the encodedPuzzles.txt file
		try {
			File file = new File("encodedPuzzles.txt");
			FileOutputStream os = new FileOutputStream(file);
			if(!file.exists()){
				file.createNewFile();
			}
			//each puzzle is written on different line
			for(int i=0; i<encoded.length; i++){
				os.write(encoded[i]);
				os.write("\n".getBytes());
			}
			os.flush();
			os.close();
			System.out.println("Puzzles were encrypted and can be found in "+ file);
			System.out.println("Continue as Bob?(y/n)");
			Scanner scan = new Scanner(System.in);
			String answer = scan.nextLine();
			if(answer.equals("y")){
				System.out.println("\n");
				System.out.println("-----BOB-----");
				
				//Bob selects a random puzzle from the file
				String selectedPuzzle = Bob.selectRandomLine(file);
				System.out.println("SelectedLine: "+(Bob.index+1));
				System.out.println("Selected encoded puzzle: "+selectedPuzzle+"\n");
				
				//Bob uses brute force to decrypt the selected puzzle
				String decrypted = Bob.decryptPuzzle(selectedPuzzle);
				System.out.println("Decrypted text: "+decrypted+"\n");
				
				//Bob extracts the key from the decrypted puzzle
				String extractedKey = Bob.extractKey(decrypted);
				System.out.println("Extracted key: "+extractedKey+"\n"); 
				
				//Creates a SecretKey that will be used a shared key with Alice from the extracted key 
				byte[] extrKey = CryptoLib.stringToByteArray(extractedKey);
				SecretKey bobsKey = CryptoLib.createKey(extrKey);
				
				//Bob extracts puzzle number from the decrypted puzzle
				int extractedPuzzleNumber = Bob.extractPuzzleNo(decrypted);
				System.out.println("Extracted puzzle number: "+extractedPuzzleNumber+"\n");
				System.out.println("Send Alice puzzle number?(y/n)");
				String answer2 = scan.nextLine();
				
				if(answer2.equals("y")){
					System.out.println("Sending puzzle number "+extractedPuzzleNumber+" to Alice.....\n");
					System.out.println("-----ALICE-----");
					
					//Alice finds the find the puzzle with the given number and extracts the key
					byte[] foundPuzzleKey = Alice.findPuzzleKeyByNo(puzzles,extractedPuzzleNumber);
					SecretKey alicesKey = CryptoLib.createKey(foundPuzzleKey);
					if(foundPuzzleKey==null){
						System.out.println("No matches");
					}else{
						System.out.println("Match found!!");
						System.out.println("Key found: "+alicesKey);
					}
					System.out.println("Send message to Bob?(y/n)");
					String answer3 = scan.nextLine();
					if(answer3.equals("y")){
						//Alice encrypts a message with the extracted key
						String msg = Alice.sendMessage(alicesKey);
						System.out.println("Continue as Bob?(y/n)");
						String answer4 = scan.nextLine();
						System.out.println("\n");
						if(answer4.equals("y")){
							//Bob gets the encrypted string from Alice
							System.out.println("-----BOB-----");
							System.out.println("New message: "+msg);
							System.out.println("Decrypt message?(y/n)");
							String answer5 = scan.nextLine();
							if(answer5.equals("y")){
								//Bob decrypts the string with the key he extracted earlier
								String decodedMsg = Bob.decryptMsg(msg, bobsKey);
								System.out.println("Decrypted message: " + decodedMsg);
							}
						}
					}
				}
				
			}
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
