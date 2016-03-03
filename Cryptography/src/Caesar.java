import java.util.*;

public class Caesar {

	public static void main(String[] args) {
		@SuppressWarnings("resource")
		Scanner fin = new Scanner(System.in);
		int length;
		String input;
		char[] charInput;
		
		//Ask User for Cypher Text and Read in
        System.out.println("Enter Cypher Text: ");
        input = fin.nextLine();
        charInput = input.toCharArray();
        
        //Decrypt
        length = input.length();
        Decrypt(charInput, length);
	}
	
	static void Decrypt(char[] cypher, int length){
	char[] decrypted = new char[length];
	
	    // Go through Every Key
		for(int key = 1; key < 26; key++){
	      System.arraycopy(cypher, 0, decrypted, 0, length);
		  
	      // Go through Every Character
		  for(int i = 0; i < length; i++){
			  
			  // Handle a - z
			  if(decrypted[i] >= 'a' && decrypted[i] <= 'z'){
		        decrypted[i] -= key;
		        if(decrypted[i] < 'a')
		        	decrypted[i] += 26;
			  }
			  
			  // Handle A - Z
			  if(decrypted[i] >= 'A' && decrypted[i] <= 'Z'){
			        decrypted[i] -= key;
			        if(decrypted[i] < 'A')
			        	decrypted[i] += 26;
			  }
		  }
		  
		  // Print Key and Message
		  System.out.println("Key: " + key );
		  System.out.print("Message: ");
		  System.out.println(decrypted);
		  System.out.println();
		}
		
	}

}
