import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DesTest {
	 public static void print(int[] tableau) {
		 //permet d'imprimer un tableau de 0 et de 1
         for (int i=0;i<tableau.length-1;i++) {
             System.out.print(tableau[i]);
             System.out.print(",");
         }
         
         System.out.println(tableau[tableau.length-1]); 

     }
     public static void print(int[][] tableau) {
    	//permet d'imprimer un tableau de tableau  de 0 et de 1
         for (int i=0;i<tableau.length;i++) {
         	for (int j=0;j<tableau[i].length-1;j++) {
             System.out.print(tableau[i][j]);
             System.out.print(",");
         	}
         	System.out.println(tableau[i][tableau[i].length-1]); 
         }
         
         

     }

	@Test
	public void bitsToString() {
        // Cas de test : Conversion du binaire "01000001" (65) en 'A'
		Des  C = new Des();
        int[] binaire = {0,1,0,0,0,0,0,1};
        String attendu = "A";
        String resultat = C.bitsToString(binaire);
        
        // Vérifier que le résultat est bien 'A'
       
        assertEquals("Erreur: le binaire ne correspond pas à 'A'", attendu, resultat);
        
        binaire = new int[]{
                // 'S' -> 83 -> 01010011
                0, 1, 0, 1, 0, 0, 1, 1,
                // 'o' -> 111 -> 01101111
                0, 1, 1, 0, 1, 1, 1, 1,
                // 'l' -> 108 -> 01101100
                0, 1, 1, 0, 1, 1, 0, 0,
                // 'e' -> 101 -> 01100101
                0, 1, 1, 0, 0, 1, 0, 1,
                // 'i' -> 105 -> 01101001
                0, 1, 1, 0, 1, 0, 0, 1,
                // 'l' -> 108 -> 01101100
                0, 1, 1, 0, 1, 1, 0, 0,
                //'!' -> 00100001
                0, 0, 1, 0, 0, 0, 0, 1
            };
        attendu = "Soleil!";
        resultat = C.bitsToString(binaire);
        
        // Vérifier que le résultat est bien 'Soleil'
              
        assertEquals("Erreur: le binaire ne correspond pas à 'Soleil'", attendu, resultat);

        
    }
	@Test
	public void StringTobits() {
        // Cas de test : Conversion du binaire "01000001" (65) en 'A'
		Des  C = new Des();
        String message = "A";
        int[] attendu = {0,1,0,0,0,0,0,1};
        int[] resultat = C.stringToBits(message);
        
        // Vérifier que le résultat est bien 'A'
        
        
        assertArrayEquals("Erreur: le binaire ne correspond pas à 'A'", attendu, resultat);
        message = "Soleil";
        attendu  = new int[] {
                // 'S' -> 83 -> 01010011
                0, 1, 0, 1, 0, 0, 1, 1,
                // 'o' -> 111 -> 01101111
                0, 1, 1, 0, 1, 1, 1, 1,
                // 'l' -> 108 -> 01101100
                0, 1, 1, 0, 1, 1, 0, 0,
                // 'e' -> 101 -> 01100101
                0, 1, 1, 0, 0, 1, 0, 1,
                // 'i' -> 105 -> 01101001
                0, 1, 1, 0, 1, 0, 0, 1,
                // 'l' -> 108 -> 01101100
                0, 1, 1, 0, 1, 1, 0, 0
            };
        resultat = C.stringToBits(message);
      
        assertArrayEquals("Erreur: le binaire ne correspond pas à 'Soleil'", attendu, resultat);

        
    }
	@Test
	public void TestPermutation() {
        // Cas de test : permutation
		Des  C = new Des();
		int[] tab_permutation = {1,3,5,2,8,4,6,7};
		int[] bloc = {0,1,2,4,8,16,32,64};
        int[] attendu = {0,2,8,1,64,4,16,32};
        int[] resultat = C.permutation(tab_permutation, bloc);
        
  
        
        
        assertArrayEquals("Erreur:'", attendu, resultat);
       
        
    }
	@Test
	public void TestinvPermutation() {
        // Cas de test : permutation
		Des  C = new Des();
		int[] tab_permutation = {1,3,5,2,8,4,6,7};
		int[] bloc = {0,2,8,1,64,4,16,32};
        int[] attendu = {0,1,2,4,8,16,32,64};      
        int[] resultat = C.invpermutation(tab_permutation, bloc);
        
        // Vérifier que le résultat est bien 'A'
        
        
        assertArrayEquals("Erreur:", attendu, resultat);
       
        
    }
	@Test
	public void TestDecouper() {
        // Cas de test : decoupe
		
		Des  C = new Des();
		int[] bloc = {0,2,8,1,64,4};
        int[][] attendu = {{0,2,8,1},{64,4,0,0}};      
        int[][] resultat = C.decoupage(bloc,4);
             
      
        assertArrayEquals("Erreur:", attendu, resultat);
        
       
        attendu = new int[][]{{0,2},{8,1},{64,4}};      
        resultat = C.decoupage(bloc,2);
    
        
        assertArrayEquals("Erreur:", attendu, resultat);
       
        bloc = new int[]{0,2,8,1,64,4,16,32};
        attendu = new int[][]{{0,2,8,1},{64,4,16,32}};       
        resultat = C.decoupage(bloc,4);
    
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        
       
       
       
    }
	@Test
	public void Testrecoller() {
        // Cas de test : decoupe
		
		Des  C = new Des();
		int[] attendu = {0,2,8,1,64,4,16,32};
        int[][] bloc = {{0,2,8,1},{64,4,16,32}};      
        int[] resultat = C. recollage_bloc(bloc);
      
      
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
       
        bloc = new int[][]{{0,2},{8,1},{64,4},{16,32}};      
        resultat = C. recollage_bloc(bloc);
      
        
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
       
       
       
    }
	@Test
	public void Testdecale_gauche() {
        // Cas de test : decalle à gauche
		
		Des  C = new Des();
		int[] attendu = {8,1,64,4,16,32,0,2};
        int[] bloc = {0,2,8,1,64,4,16,32};      
        int[] resultat = C.decalle_gauche(bloc,2); 
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        
        attendu = new int[]{0,2,8,1,64,4,16,32}; 
        resultat = C.decalle_gauche(bloc,8);
              
        assertArrayEquals("Erreur:", attendu, resultat);
        
       
       
       
    }
	@Test
	public void TestXor() {
        // Cas de test : decoupe
		
		
		int[] attendu = {0,0,0,0,0,0,0,0};
        int[] bloc1 = {0,1,0,1,0,1,0,1}; 
        int[] bloc2 = {0,1,0,1,0,1,0,1}; 
        Des  C = new Des();
        int[] resultat =  C.xor( bloc1, bloc2); 
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        attendu = new int[]{1,1,1,1,1,1,1,1};
        bloc1 = new int[]{1,0,1,0,1,0,1,0}; 
        bloc2 = new int[]{0,1,0,1,0,1,0,1}; 
       
        resultat =  C.xor( bloc1, bloc2); 
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        attendu = new int[]{1,0,1,1,0,0,1,0};
        bloc1 = new int[]{1,1,1,0,0,1,1,1}; 
        bloc2 = new int[]{0,1,0,1,0,1,0,1}; 
       
        resultat =  C.xor( bloc1, bloc2); 
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        
       
       
       
    }
	@Test
	public void TestFonctionS() {
        // Cas de test : decoupe
		
		
		int[] attendu = {1,1,0,0};
        int[] bloc1 = {0,1,0,1,0,1}; 
        Des  C = new Des();
         
        int[] resultat =  C.fonction_S( bloc1); 
     
        
        assertArrayEquals("Erreur:", attendu, resultat);
        
        
       
       
       
    }
	@Test
	public void TestFonctionF() {
        // Cas de test : decoupe
		Des encodage = new Des();
		String message = "Soleil";
        int[] message_binaire = encodage.stringToBits(message);
        int[][] message_couper = encodage.decoupage(message_binaire,64);;
       
        //message_couper[0]=encodage.fonction_F(encodage.tab_cles[0],message_couper[0]);
       
        
		
        
        
       
       
       
    }
	@Test
	public void TestCrypt() {
		Des encodage = new Des();
		encodage.tab_cles[0]=new int[] {0, 1, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 1, 1, 1, 0, 0, 1, 0, 1, 1, 0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 1, 0, 1, 1, 1, 0, 1, 1, 0, 0, 1, 1, 0, 0, 0, 1, 0, 1, 1, 0, 1, 0, 1, 0};
		
		int[] messageCrypt=(encodage.crypte ("Cela fesais 40h que j'était sur ce problème"));
		
		
		
		System.out.println(encodage.bitsToString(messageCrypt));
		System.out.println("==============================");
		System.out.println(encodage.decrypte(messageCrypt));
	}
	

}
