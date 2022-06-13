package proyecto03;

import java.math.BigInteger;
import java.util.Vector;
import java.util.LinkedList;
import java.io.*;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.security.MessageDigest;

/*
*
*/
public class Cifrado{

	private Polinomio polinomio;
	private int n;
	private int t;
	private String documentoOriginal;
	private String documentoCifrado;
	private String mensaje;
	private String nEvaluacionesTotales;
	private String nombreArchivoNEvaluaciones;
	private String contrasenia;
	private String hashedContrasenia;

	/** 
    *Construye un polinomio con el grado requerido y contraseña.
    */
    private void nPolinomio(){
        polinomio = new Polinomio(t-1,hashedContrasenia);
    }

    /** 
    *Realiza un hashing a una contraseña usando el algoritmo SHA-256.
    */
    private void hashing(){

    	 byte [] almacenaBytes = contrasenia.getBytes();
        
        try{    

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            
            md.update(almacenaBytes);
            /**
             *convierte de una array de Bytes a String 
             */
            hashedContrasenia = new String(md.digest());
        
        }catch(Exception e){

            throw new RuntimeException(e);
        }
    }
    
    /**
    *Metodo que Cifra un mensaje 
    *@param ciMensaje mensaje a cifrar
    */
    public void cifrarMensaje(String ciMensaje){

        ManejadorArchivos texto = new ManejadorArchivos();
        
        mensaje = ciMensaje;
        hashing();
        nPolinomio();       

        texto.escribirArchivo(polinomio.evaluarEnX(n), nombreArchivoNEvaluaciones + ".frag");
        
        algoritmoAES(new BigInteger(hashedContrasenia,16).abs().toByteArray());
        
    }
    //Ajuste al metodo de la clase ManejadorArchivos
    /**
    *Metodo que descifra el mensaje.
    *@param textoEvaluaciones texto que contiene las evaluaciones.
    *@param LlaveAES llave para descrifrar  
    */
    public void descifrarMensaje(String textoEvaluaciones, String LlaveAES){

        byte[] conBytes = getClaveCifradoK();
        archivoDescifrado(conBytes);
    
    }

	/** 
    *Encripta con una contraseña usando el algoritmo AES-256.
    *@param conEnBytes contraseña en bytes.
    */
    private void algoritmoAES(byte[] conEnBytes){

        int escribeCon;
        String archivo;
        Cipher cifrado;
        CipherInputStream cis;
        FileOutputStream fos; 
        SecretKeySpec sks;
        
        try {

            archivo = documentoOriginal;
            cifrado = Cipher.getInstance("AES");

            fos = new FileOutputStream(archivo+".aes",true);
            sks = new SecretKeySpec(conEnBytes,0,16, "AES");
            
            cifrado.init(Cipher.ENCRYPT_MODE,sks);
            cis = new CipherInputStream(new FileInputStream(documentoOriginal),cifrado);
            
            while((escribeCon=cis.read()) != -1){
                fos.escribirArchivo(escribeCon);
            }
            
            System.out.println("Se creo el archivo: " +archivo+ ".aes");
        	
        	cis.close();
            fos.close();

        }catch(Exception e) {

            System.err.println(e);
            System.exit(1);
        }
    }
    //Ajustes en SecretKeySpec y nombres de varibles
    /**
     * Crea un documento claro utilizando el archivo encriptado y la contraseña AES
     * @param conEnBytes contraseñan en bytes
     */ 
    public void archivoDescifrado(byte[] conEnBytes) {

        int escribeCon;
        String archivo;
        Cipher cifrado;
        CipherInputStream cis;
        FileOutputStream fos; 
        SecretKeySpec sks;
        
        try {

            if(documentoCifrado.lastIndexOf('.') == -1){
                archivo = documentoCifrado;
            
            }else if(documentoCifrado.lastIndexOf('.') != -1){

                archivo = documentoCifrado.substring(0,documentoCifrado.lastIndexOf('.'));
            }

            fos = new FileOutputStream(archivo+".desencriptado",true);
            cifrado = Cipher.getInstance("AES");
            sks = new SecretKeySpec(conEnBytes,0,16, "AES");

            cifrado.init(Cipher.DECRYPT_MODE,sks);
            cis = new CipherInputStream(new FileInputStream(documentoCifrado),cifrado);

            while((escribeCon=cis.read()) != -1){
                fos.escribirArchivo(escribeCon);
            }

            System.out.println("Se ha creado el archivo: "+archivo +".desencriptado");
        
            cis.close();
            fos.close();

        }catch(Exception e) {

            System.err.println(e);
            System.exit(1);
        }
    }
    //cambios en el metodo estaba mal nombrada
    /**
    * Metodo que lee el archivo con las evaluaciones del polinomio.
    * @return Contraseña escondida 
    */
    private byte[] getClaveCifradoK() {

        Vector[] vector = null;
        byte[] b = null;
        MessageDigest md;
        LinkedList<Vector> lista = new LinkedList<Vector>();
        Polinomio polinomio = new Polinomio();
        try {

            String strLine;
            FileInputStream fis = new FileInputStream(nEvaluacionesTotales);
            DataInputStream dis = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(dis));
            
            System.out.println("Total de evaluaciones "+nEvaluacionesTotales);
            
            while((strLine=br.readLine()) != null) {
            
                lista.add(new Vector(2));

                ((Vector)lista.getLast()).add(0,new BigInteger(strLine.substring(0,strLine.indexOf(','))));
                ((Vector)lista.getLast()).add(1,new BigInteger(strLine.substring(strLine.indexOf(',')+1,strLine.length())));
            }
            
            dis.close();
            vector = new Vector[lista.size()];
            
            for(int i = 0; i < vector.length; i++){
                vector[i] = (Vector)lista.get(i);
            }
            
            System.out.println("Analizando el polinomio de interpolación de Lagrange");
            
            return polinomio.interpolarConLagrangeEnX(new BigInteger("0"),vector).toByteArray();

        }catch (Exception e) {

            System.err.println(e);
            System.exit(1);
        }
        return null;
    }

    /** 
    *define el nombre del archivo en el que seran guardadas las n evaluaciones.
    *@param eGuardadas nombre que se usará
    */
    public void nombreNEvaluaciones(String eGuardadas){
        nombreArchivoNEvaluaciones =eGuardadas;
    }

    /** 
    *Metodo que define el nombre del archivo en el que seran guardado el texto cifrado.
    *@param docCifrado con el nombre a usar.
    */
    public void NombreDocumentoCifrado(String docCifrado){
        documentoCifrado =docCifrado;
    }

    /** 
    *Define el nombre del archivo con las evaluaciones totales
    *@param nombreETotales nombre que se usará
    */
    public void nombreEvaluaciones(String nombreETotales){
        nEvaluacionesTotales = nombreETotales;
    }

    /** 
    *Define el nombre del documento original
    *@param nombreDocOriginal nombre que se usará
    */
    public void NombreOriginal(String nombreDocOriginal){
        documentoOriginal =nombreDocOriginal;

    }

    /**
    *Metodo que define la contraseña.
    *@param contrasenia contraseña a usar
    */
    public void asignarContrasenia(String contrasenia){
        this.contrasenia = contrasenia;
    }

    

}
