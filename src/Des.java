import java.util.Random;

public class Des {
	public static Random alea = new Random();
	public static int[] tab_decalage = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1 };

	public static int[] PI_inv = {40, 8, 48, 16, 56, 24, 64, 32,
            39, 7, 47, 15, 55, 23, 63, 31,
            38, 6, 46, 14, 54, 22, 62, 30,
            37, 5, 45, 13, 53, 21, 61, 29,
            36, 4, 44, 12, 52, 20, 60, 28,
            35, 3, 43, 11, 51, 19, 59, 27,
            34, 2, 42, 10, 50, 18, 58, 26,
            33, 1, 41, 9, 49, 17, 57, 25};
	
	public int[] perm_initiale = {58, 50, 42, 34, 26, 18, 10, 2,
            60, 52, 44, 36, 28, 20, 12, 4,
            62, 54, 46, 38, 30, 22, 14, 6,
            64, 56, 48, 40, 32, 24, 16, 8,
            57, 49, 41, 33, 25, 17, 9, 1,
            59, 51, 43, 35, 27, 19, 11, 3,
            61, 53, 45, 37, 29, 21, 13, 5,
            63, 55, 47, 39, 31, 23, 15, 7
			};
	
	
	public static int[] PC1 = {57,49,41,33,25,17,9,
			1,58,50,42,34,26,18,
			10,2,59,51,43,35,27,
			19,11,3,60,52,44,36,
			
			63,55,47,39,31,23,15,
			7,62,54,46,38,30,22,
			
			14,6,61,53,45,37,29,
			
			21,13,5,28,20,12,4};
	public static int[] PC2 = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
			41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32 };
	public int[][] tab_cles;

	static int[][] s1 = { { 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
			{ 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
			{ 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
			{ 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 } };

	// Table S2
	static int[][] s2 = { { 15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10 },
			{ 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
			{ 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
			{ 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 } };

	// Table S3
	static int[][] s3 = { { 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
			{ 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
			{ 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
			{ 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 } };

	// Table S4
	static int[][] s4 = { { 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
			{ 13, 6, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
			{ 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
			{ 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 } };

	// Table S5
	static int[][] s5 = { { 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
			{ 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
			{ 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
			{ 11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3 } };

	// Table S6
	static int[][] s6 = { { 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
			{ 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
			{ 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
			{ 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 } };

	// Table S7
	static int[][] s7 = { { 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
			{ 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
			{ 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
			{ 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 } };

	// Table S8
	static int[][] s8 = { { 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
			{ 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
			{ 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
			{ 2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11 } };
	
	public int[] E = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18,
			19, 20, 21, 20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1 };

	public static int[][][] s_list = new int[][][] { s1, s2, s3, s4, s5, s6, s7, s8 };
	public static int[] P = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 17, 2, 8, 24, 14, 32, 27, 3, 9,
			19, 13, 30, 6, 22, 11, 4, 25 };
	
	int nb_rond =0;
	/**
     * 
     * Constructeur
     */
	public Des (){
        
        nb_rond =0;  
        tab_cles= new int[16][48];
        for(int i=0;i<16;i++) {
        	genereCle(i);
        	
        	
        }
            
        // tab_decalage
        // perm_initiale
    }
	/**
     *Convertie un string en talbeau de binaire
     * 
     * @param String message : un message clair
     * @return int[]: le message clair écrit en binaire (un charactère = 8bit)
      */
	 public  int[] stringToBits(String message) {
		 
		 int[] message_binaire = new  int[message.length()*8];//chaque caractère fera 8 bit
         int indice=0; //permet de déterminer la position de la lettre dans le tableau
		 for (char c : message.toCharArray()) {
	            // Convertir le caractère en entier (code ASCII/Unicode)
			   
	            String binaryChar = Integer.toBinaryString(c);
	            
	            // Remplir avec des zéros à gauche pour obtenir 8 bits
	            while (binaryChar.length() < 8) {
	                binaryChar = "0" + binaryChar;
	            }

	            //converti la chaine binaire en liste d'entier entre 0 et 1
	            for (int i =0; i<8;i++) {
	            	message_binaire[indice+i] = binaryChar.charAt(i)- '0';
	            }
	            
	            indice+=8;
	            
	        }
         
         
         return message_binaire;
     }
	 /**
	     *Convertie un albeau de binaire en string
	     * 
	     * @param int[] message_binaire: message écrit en binaire
 	     * @return String: retourne le message en clair
	      */
     public  String bitsToString(int[] message_binaire ) {
         
         String message = "";
         
         for (int i = 0; i < message_binaire.length; i += 8) {
             String valeur = "";

             // Construire un entier à partir des 8 bits
             for (int j = 0; j < 8; j++) {
                 valeur +=  message_binaire[i + j]+"";
             }
             int valeurEntiere = Integer.parseInt(valeur, 2);
             // Convertir l'entier en caractère
             char caractere = (char) valeurEntiere;
             	
             // Ajouter le caractère au résultat
             message+=caractere;
         }
        
         
         return message;
     }
     /**
      *Convertie un string en talbeau de binaire
      * 
      
      * @return  int[]: Créer une clef de 64 bits généré aléatoirement
       */
     public int[] genereMasterKey() {
         int[] masterkey = new  int[64];
         for (int i =0;i<16;i++) {
            
             masterkey[i]=alea.nextInt(2);

         }
         
         return masterkey;
     }
     
     /**
      *Permute les élément d'un premier tableau en fonction de la poisition indiquer 
      *par le deuxieme tableau
      * @param int[]tab_permutation = tableau de permutation
      * 		int[] bloc :tableau a permuter 
     
      * @return int[] une tableau eyant subit la permutation
       */
     public  int[] permutation(int[]tab_permutation, int[]bloc) {
    	 
    	 
         int[] bloc_permuter = new int[tab_permutation.length] ;
         
         for (int i =0; i<tab_permutation.length;i++){
        	 
             bloc_permuter[i]=bloc[tab_permutation[i]-1];
             
             
         }
         
         return  bloc_permuter;
     }
     /**
      *Permet de faire la permutation inverse de la fonction permuation
      *
      * @param int[]tab_permutation = tableau de permutation
      * 		int[] bloc :tableau a permuter 
     
      * @return int[] tableau d'entier entre 0 et 1 
       */
     public int[] invpermutation(int[] tab_permutation, int[] bloc) {
         int[] bloc_permuter = new int[bloc.length] ;
         for (int i =0;i<bloc.length;i++){
        	 
             bloc_permuter[tab_permutation[i]-1]=bloc[i];
             
             
         }
                    
         return  bloc_permuter;
     }
     /**
      *Permet de faire de couper un tableau en sous tableau de taille égale à 
      *"tailleBlocs"
      *
      * @param int[] bloc: tableau a découper
      * 		int tailleBlocs:taille des sous tableau
     
      * @return int[][] tableau d'entier entre 0 et 1 
       */
     public  int[][] decoupage (int[]bloc,int tailleBlocs) {
     	
     	int taille = (bloc.length/tailleBlocs)+(bloc.length%tailleBlocs!=0 ? 1:0);//divise par le nombre de bloc la table 
     																		//pour savoir combien il y aura de tableau
     	                 													// et il en rajouteras 1 si le bloc d'origine
     	                                                                    //ne possède pas asser de bit pour faire un tableau contenant toues les valeurs

     	int [][] tableau_decouper = new int[taille][tailleBlocs];
     	// boucle qui permet de pacourir le tableau bloc
     	for (int i =0; i<taille;i++) {
     		for (int j =0; j<tailleBlocs;j++) {
     			if (i*tailleBlocs+j<bloc.length) {
     				//range dans le nouveau tableau de tableau les éléments 
     				// de l'ancien tableau
     				tableau_decouper[i][j]=bloc[i*tailleBlocs+j];
     			}
     			
     			
     			
         		
         	}
     	}
     	return tableau_decouper;
     	
     }
     /**
      *Permet de fusinner l'enssemble des sous tableau en un seul
     
      *
      * @param int[][] bloc: tableau a recoller
      * 		
     
      * @return int[] tableau d'entier entre 0 et 1 
       */
     public int[] recollage_bloc(int[][] blocs) {
     	int [] tableau_recoller = new int[blocs.length*blocs[0].length];
     	for (int i =0;i<tableau_recoller.length;i++) {
     		tableau_recoller[i]= blocs[i/blocs[0].length][i%blocs[0].length];
     	}
     	return tableau_recoller;
     }
     /**
      *Permet de décoller les éléments d'un tableau vers la gauche de nb_rang
      *
      * @param int[] bloc: tableau d'entier entre 0 et 1
      *  		int nbCran: entier qui indique le nombre de cran nécéssaire pour décaller
      *  le tableau
     
      * @return int[] tableau d'entier entre 0 et 1 
       */
     public int[] decalle_gauche(int[] bloc, int nbCran) {
     	int[] decalle = new int[bloc.length];
     	for (int i=0;i<bloc.length;i++) {
     		decalle[i]=bloc[(i+nbCran)%bloc.length];
     	}
     	return decalle;
     }
     /**
      *Génère une n ieme clef capable de crypter de et de décrypter un message
      *
      * @param int n: entier qui quel numéro de clef généré
      *  le tableau
     
      * 
       */
     public void genereCle(int n) {
    	 //créer une clef aléatoire
     	int[] masterKey= genereMasterKey();;
     	//fait une permutation avec la table PC1
     	int[] erPermutation = permutation(PC1  , masterKey); 
     	//découpe le tableau en deux
     	int[][] decoupage = decoupage(erPermutation,28);
     	
     	decoupage[0] = decalle_gauche(decoupage[0],tab_decalage[n] );
     	decoupage[1]= decalle_gauche(decoupage[1],tab_decalage[n] ) ;
     	//recolle les élément du tableau
     	int[] recollage =recollage_bloc(decoupage);
     	//fini par permuter un dernière fois avec PC2
     	int[] erPermutation_2 = permutation(PC2  , recollage); 
     	//enregistre dans tab_cles a la position n
     	tab_cles[n]=erPermutation_2;
     	
     	
     }
     /*
      * Cette fonction permet de réaliser une opération xor entre deux tableau 
      * 
      * @param int[] tab1 tab2: deux tableau contenant des 0 et des 1 
      * 
      * @return int[] : qui est le résultat de d'une opéraiton mathématique xor(tab1,tab2)
       * */
     public int[] xor (int[] tab1, int[] tab2 ) {
    	 //créer la nouvelle table ou les informations seront enregistrer
     	int[] tab3 = new int[tab1.length];
     	//réalise une boucle for et fait un xor de chaque valeur 
     	//des deux tableaus
     	for (int i=0;i<tab1.length;i++) {
     			tab3[i]= tab1[i]!= tab2[i] ? 1:0;;
     		
     	}//xor(tab1,tab2) 
     	return tab3;
     }
     /*
      * FONCTION_S
      * S permet de passer de 6 bits `a 4 bits
      * 
      * @param int[6] tab: tableau d'entier entre 0 et 1*
      * @return int[4] un tableau d'entier sur 4 bits
      */
     public int[] fonction_S (int[] tab) {
         
     	
     	int colone=8*tab[1]+4*tab[2]+2*tab[3]+tab[4] ;
     	int ligne= 2*tab[0]+tab[5] ;
     	//cherche dans la table Sn (n est le nombre de round) l'indice a 
     	//la colone et a la ligne trouvé précédément
     	int nombre = s_list[this.nb_rond%8][ligne][colone];
     	//coverti en un string binaire du nombre trouver
     	String binary = Integer.toBinaryString(nombre);
     	while (binary.length()<4) {
     		binary ="0"+binary;
     	}
     	//retourne un tableau de binaire
         return new int[] {binary.charAt(0)-48,
         		binary.charAt(1)-48,
         		binary.charAt(2)-48,
         		binary.charAt(3)-48} ;
     			
     	
     	
     }
     /*
      * FONCTION_F
      * 
      * 
      * @param int[31] un: tableau d'entier entre 0 et 1
      * @return le résltat de la fonction F entre (Dn et Kn)
      */
     public int[] fonction_F ( int[] unD) {
    	 //récupération de la clef
    	int[] uneCLe=tab_cles[nb_rond];
     	unD= permutation(E , unD);//extention du bloc Dn    
     	//fait un xor entre unD et une clef
     	unD=xor(unD,uneCLe);
        // decoupe unD en sous tableau de taille 6
     	int[][] avent_S = decoupage(unD, 6);
     	//S est le résultat de la fonction S
     	int[][] S = new int[avent_S.length][4];
         for (int i=0; i<=7;i++) {
             S[i]=fonction_S(avent_S[i]);

         }
     	
     	
        
     	 return permutation(P , recollage_bloc(S)); 
     }
     /*
      * Crypte un message en clair
      * 
      * @entré: String message_clair: message a crypter
      * @return int[] : message_clair qui a été crypter avec l'algorythme Des
      * 
      * */
     public int[] crypte(String message_clair) {
    	    // Transformation du message en binaire
    	    int[] message_binaire = stringToBits(message_clair);
    	    // Division en blocs de 64 bits
    	    int[][] decoupage_64 = decoupage(message_binaire, 64);

    	    int[][] tab_32;
    	    int[] D, G, D1, G1;
    	    
    	    for (int i = 0; i < decoupage_64.length; i++) {
    	        // Permutation initiale
    	        decoupage_64[i] = permutation(perm_initiale, decoupage_64[i]);
    	        // Division en deux parties de 32 bits (G et D)
    	        tab_32 = decoupage(decoupage_64[i], 32);
    	        G = tab_32[0];
    	        D = tab_32[1];
    	        
    	        // 16 itérations
    	        for (int j = 0; j < 16; j++) {
    	        	nb_rond=j;
    	            
    	            D1 = xor(G, fonction_F( D)); // Dn+1 = Gn XOR F(Kn, Dn)
    	            G1 = D; // Gn+1 = Dn
    	            G = G1;
    	            D = D1;
    	        }
    	        
    	        // Recombinaison des deux parties G15 et D15
    	        tab_32[0] = G;
    	        tab_32[1] = D;
    	        
    	        // Recollage et permutation finale
    	        decoupage_64[i] = recollage_bloc(tab_32);
    	        decoupage_64[i] = permutation(PI_inv , decoupage_64[i]);
    	    }
    	    
    	    return recollage_bloc(decoupage_64);
    	}
     
     /*
      * Permet de décrypter un message coder en message clair
      * 
      * @param int[] messageCodé : message crypté en 0 et en 1
      * @return String: le message est retourner en clair
      */
     public String decrypte( int[] messageCodé) {
    	
    	 int[][] decoupage_64= decoupage(messageCodé,64);
    	 
    	 
    	 int[][] tab_32 ;
		 int[] D1;
		 int[] G1;
		 int[] D;
		 int[] G;
		 for (int i=0;i<decoupage_64.length;  i++) {
			 decoupage_64[i]=permutation(perm_initiale, decoupage_64[i]);
    		 tab_32 =decoupage(decoupage_64[i],32);
    		 
    		 for (int j=15;j>=0;  j--) {
    			 nb_rond=j;
    			 G=tab_32[0];
	    		 D=tab_32[1];
	    		 D1=G;
	    		 
	    				 
	    				 
	    		 G1=xor(D,fonction_F(D1));
	    		 tab_32[0]=G1;
	    		 tab_32[1]=D1;
    		 }
    		 decoupage_64[i]=recollage_bloc(tab_32);
    		 decoupage_64[i]=permutation(PI_inv, decoupage_64[i]);
    	 }
    	 return bitsToString(recollage_bloc(decoupage_64));
    	 
     }
      
     
     
     
     
  
}
