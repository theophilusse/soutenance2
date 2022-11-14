package com.example.exojavafx;

import java.io.*;
import java.util.*;

/** La classe Terminal permet de r�aliser ses premiers programmes Java en permettant d'afficher dans la console d'ex�cution des donn�es de type diff�rents, et en permettant de saisir au clavier des donn�es de type diff�rents.<BR>
    Elle permet aussi de lire et �crire un fichier texte
    Cette classe contient que des m�thodes statiques. */
public class Terminal{    

    // Le buffer standard  de lecture = le clavier
    private static BufferedReader in =
        new BufferedReader(new InputStreamReader(System.in));

    /** Cette m�thode lit un fichier texte et retourne le contenu du fichier sous la forme d'un  tableau de String dont chaque element est une ligne du fichier.
        @param nomFichier le nom du fichier qui doit �tre dans le r�pertoire courant.
        @return String[] le contenu du fichier. 
        @exception TerminalException (de type RuntimeException) si erreur d'�criture<BR>
        Rappel : Une exception de type RuntimeException n'a pas l'obligation d'�tre captur�e.
*/
    public static String[] lireFichierTexte(String nomFichier)
    {
        try{
            File fichier = new File(nomFichier);
            FileInputStream fis = new FileInputStream(new File(nomFichier));
            
            byte[] buffer = new byte[(int)fichier.length()];
            fis.read(buffer);
            fis.close();
            String str = new String(buffer);

            // On enleve le caract�re '\r' code 13 qui est ajout� en Windows
            // Les fins de ligne dans un fichier texte cr�� sous Windows
            //  se termine par \r\n.
            // Il faut enlever le \r car il a des effets perturbant sur
            //  la m�thode System.out.print et est pris comme un caract�re de plus
            //  qu'il faut �liminer
            //  
            String texte = str.replaceAll(""+(char)(13),"");
            
            // Les lignes du fichier sont mises dans un tableau
            //
            String[] mots = texte.split("\n");

            return(mots);
        }
        catch(Exception ex)
            {
                exceptionHandler(ex);
            }
        return null;
    }

    /** Cette m�thode permet de cr�er un fichier texte � partir du contenu d'un StringBuffer.
        @param nomFichier Le nom du fichier qui est cr�� dans le r�pertoire courant
        @param strbuf Le StringBuffer contenant le texte � �crire. 
        @exception TerminalException (de type RuntimeException) si erreur d'�criture
    */
    public static void ecrireFichier(String nomFichier,
                                     StringBuffer strbuf)
    {
        try{
            File fichier = new File(nomFichier);
            FileOutputStream fos = new FileOutputStream(new File(nomFichier));
            
            byte[] buffer = strbuf.toString().getBytes();
            fos.write(buffer);
            fos.close();
        }
        catch(Exception ex)
            {
                exceptionHandler(ex);
            }
    }

    /** Cette m�thode lit une cha�ne de caract�re
        @return String la cha�ne saisie dans la console d'ex�cution
       @exception TerminalException (de type RuntimeException) si erreur de lecture
    */
    public static String lireString(String prompt) // Lire un String
    {
        String tmp="";
        char C='\0';
        if (prompt != null)
            ecrireString(prompt);
        try {
            tmp = in.readLine();
        }
        catch (IOException e)
            {
                exceptionHandler(e);
            }
        return tmp;
    }

    /** Cette m�thode lit un entier
        @return int L'entier saisi dans la console d'ex�cution
       @exception TerminalException (de type RuntimeException) si la saisie n'est pas un entier ou erreur de lecture
    */
    public static int lireInt(String prompt)  // Lire un entier
    {
        int x=0;
        if (prompt != null)
            ecrireString(prompt);
        try {
            x=Integer.parseInt(lireString(null));
        }
        catch (NumberFormatException e) {
            exceptionHandler(e);
        }	
        return x ;
    }

    /** Cette m�thode lit un boolean (false ou true)
        @return boolean Le boolean saisi dans la console d'ex�cution
       @exception TerminalException (de type RuntimeException) si erreur de lecture. <BR>
       Tout autre valeur que TRUE, FALSE, true ou false, retourne la valeur false
    */
    public static boolean lireBoolean(String prompt)  // Lire un entier
    {
        boolean b = true;
        if (prompt != null)
            ecrireString(prompt);
        try {
            b = Boolean.valueOf(lireString(null)).booleanValue();
        }
        catch (NumberFormatException e) {
            exceptionHandler(e);
        }	
        return b;
    }

    /** Cette m�thode lit un double
        @return double Le double saisi dans la console d'ex�cution
       @exception TerminalException (de type RuntimeException) si la valeur saisie n'est pas un double ou ereur de lecture.
    */
    public  static double lireDouble(String prompt)  // Lire un double
    {
        double x=0.0;
        if (prompt != null)
            ecrireString(prompt);
        try {
            x=Double.valueOf(lireString(null)).doubleValue();
        }
        catch (NumberFormatException e) {
            exceptionHandler(e);
        }	
        return x ;
    }

    public  static float lireFloat(String prompt)  // Lire un double
    {
        float x=0.0f;
        if (prompt != null)
            ecrireString(prompt);
        try {
            x=Double.valueOf(lireString(null)).floatValue();
        }
        catch (NumberFormatException e) {
            exceptionHandler(e);
        }	
        return x ;
    }

    /** Cette m�thode lit un caract�re.
       @exception TerminalException (de type RuntimeException) si erreur de lecture.<BR>
       Si on saisit plus d'1 caract�re alors le caract�re retourn� est le premier.
    */
    public  static char lireChar(String prompt)  // Lire un caractere
    {
        String tmp;
        if (prompt != null)
            ecrireString(prompt);
        tmp=lireString(null);
        if (tmp.length()==0)
            return '\n';
        else 
            {
                return tmp.charAt(0);
            }
    }

    /** Cette m�thode �crit une chaine et ne revient pas � la ligne.
        @param s la chaine &agrave; &eacute;crire
    */
    public static void ecrireString(String s){ // Afficher un String
        System.out.print(s);
    }

    /** Cette m�thode �crit une chaine et revient � la ligne.
        @param s la chaine &agrave; &eacute;crire
    */
    public static void ecrireStringln(String s) // Afficher un String
    {
        ecrireString(s);
        sautDeLigne();
    }

    /** Cette m�thode �crit un entier et ne revient pas � la ligne.
        @param i l'entier � �crire
    */
    public static void ecrireInt(int i)  // Afficher un entier
    {
        ecrireString(""+i);
    }

    /** Cette m�thode �crit un entier et revient � la ligne.
        @param i l'entier � �crire
    */
    public static void ecrireIntln(int i)  // Afficher un entier
    {
        ecrireString(""+i);
        sautDeLigne();
    }

    /** Cette m�thode �crit un bool�an et ne revient pas � la ligne.
        @param b le bool�en � �crire
    */
    public static void ecrireBoolean(boolean b){
        ecrireString(""+b);
    }

    /** Cette m�thode �crit un bool�an et revient � la line.
        @param b le bool�en � �crire
    */
    public static void ecrireBooleanln(boolean b){
        ecrireString(""+b);
        sautDeLigne();
    }

    /** Cette m�thode �crit un double et ne revient pas � la ligne.
        @param d le double � �crire
    */
    public  static void ecrireDouble(double d)  // Afficher un double
    {
        ecrireString(""+d);
    }

    /** Cette m�thode �crit un double et revient � la ligne.
        @param d le double � �crire
    */
    public  static void ecrireDoubleln(double d)  // Afficher un double
    {
        ecrireDouble(d);
        sautDeLigne();
    }

    /** Cette m�thode �crit un caract�re et ne revient pas � la ligne.
        @param c le caract�re � �crire
    */
    public  static void ecrireChar(char c)  // Afficher un caractere
    {
        ecrireString(""+c);
    }  

    /** Cette m�thode �crit un caract�re et revient � la ligne.
        @param c le caract�re � �crire
    */
    public  static void ecrireCharln(char c)  // Afficher un caractere
    {
        ecrireChar(c);
        sautDeLigne();
    }

    /** Cette m�thode revient � la ligne.
    */
    public static void sautDeLigne(){
        try{
            System.out.println();
        }catch(Exception ex){
            exceptionHandler(ex);
        }
    }

    /** Cette m�thode retourne l'exception TerminalException
    */
    protected static void exceptionHandler(Exception ex){
        TerminalException err = new TerminalException(ex);
        throw err;
    }

    /** Cette m�thode �crit une exception avec la pile dans la console
        @param ex l'exception � �crire
    */
    public static void ecrireException(Throwable ex){
        ecrireString(ex.toString());
        ex.printStackTrace(System.out);
    }
}

/** Classe de d�finition de l'exception TerminalException qui peut �tre retourn�e dans l'usage des m�thodes de la classe Terminal. */
class TerminalException extends RuntimeException{
    Exception ex;
    TerminalException(Exception e){
        ex = e;
    }
}



