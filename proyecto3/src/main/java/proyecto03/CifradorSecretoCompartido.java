package proyecto03;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Vector;
import java.util.LinkedList;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

//import javax.xml.bind.DatatypeConverter;


//import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.lang.NumberFormatException;
import java.util.regex.PatternSyntaxException;
import java.io.FileNotFoundException;
import java.lang.SecurityException;
/**
 * Clase que cifra/descifra un mensaje utilizando el algoritmo de El Esquema de Secreto Compartido de Shamir
 * 
 */
public class CifradorSecretoCompartido{

	private static String  HASH = "SHA-256";
	private static String CIFRADO = "AES/ECB/PKCS5Padding";
	private static String ALGORITMO_CIFRADO = "AES";

	public static final BigInteger PRIMOZP= 
		new BigInteger(
			"208351617316091241234326746312124448251235562226470491514186331217050270460481"
		);
	/**
	 * Constructor vacio 
	 */
	private CifradorSecretoCompartido(){}

	/**
	 * Metodo que descifra un mensjae utilizando la clave generada
	 * @param dirArchivo directorio donde se encuentra el archivo a descifrar
	 * @param llave clave requerida para descifrar
	 */
	public static void descifrarArchivoConLlave( String dirArchivoCifrado , byte[] llave){

		Cipher descifrador = null;
		SecretKeySpec llaveSecreta = null;
		CipherInputStream flujoEntrada = null;

		FileOutputStream flujoSalida = null;

		//Obtener el nombre original del archivo. Por implementar.
		String nombreArchivoDescifrado = obtenerNombreArchivo(dirArchivoCifrado);
		try{
			//Descifrar el archivo 
			descifrador = Cipher.getInstance(CIFRADO);	
			llaveSecreta = new SecretKeySpec(
				llave,
			       	ALGORITMO_CIFRADO 
			);
			descifrador.init( Cipher.DECRYPT_MODE, llaveSecreta );

			flujoSalida = new FileOutputStream( nombreArchivoDescifrado , true );
			flujoEntrada = new CipherInputStream(
				new FileInputStream( dirArchivoCifrado),
			       	descifrador 
			);
			//meter contenido descifrado
			int estado;
			while( (estado = flujoEntrada.read() ) != -1){
				flujoSalida.write(estado);
			}
			flujoSalida.close();
			flujoEntrada.close();

		}catch( FileNotFoundException e){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivoCifrado+"'. No fue encontrado");
		}catch( SecurityException ee){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivoCifrado+"'. Permiso denegado");
		}
		catch( Exception eee ){
			terminaEjecucion("Se intrudujo un número incorrecto de llaves o las llaves están dañadas\nPosiblemente el archivo no se decifre correctamente" );
		}	

	
	
	}

	/**
	 * Metodo que cifra un archivo utilizando el codigo hash y AES
	 * @param dirArchivo directorio del archivo donde se encuenra
	 * @param nombreArchivo nombre del archivo que se genera con extension ".aes"
	 * @param llaveHash codigo hash de la contraseña
	 */
	public static void cifrarArchivoConLLave( String dirArchivo , String nombreArchivo , byte[] llaveHash){

		Cipher cifrador = null;
		SecretKeySpec llaveSecreta = null;
		CipherInputStream flujoEntrada = null;

		FileOutputStream flujoSalida = null;

		String nombreArchivoCifrado = (nombreArchivo+".aes");

		
		byte[] llave = llaveHash;
		try{
			cifrador = Cipher.getInstance( CIFRADO);	

			llaveSecreta = new SecretKeySpec(
				       	llave,
					ALGORITMO_CIFRADO 
			);
			cifrador.init( Cipher.ENCRYPT_MODE, llaveSecreta );

			flujoSalida = new FileOutputStream( nombreArchivoCifrado , true );
			flujoEntrada = new CipherInputStream(
				new FileInputStream( dirArchivo),
			       	cifrador 
			);
			//meter contenido cifrado
			int estado;
			while( (estado = flujoEntrada.read() ) != -1){
				flujoSalida.write(estado);
			}
			flujoSalida.close();
			flujoEntrada.close();

		}catch( FileNotFoundException e){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivo+"'. No fue encontrado");
		}catch( SecurityException ee){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivo+"'. Permiso denegado");
		}
		catch( Exception e ){
	terminaEjecucion( "Error al escribir el archivo cifrado" );
		}	

	
	}

	/**
	 * Metodo que genera el código hash de la contraseña ingresada
	 * @param entrada contrasea ingresada
	 * @return codigo hash de la contraseña
	 */
	public static byte[] obtenerLlaveSHA256( String entrada ){
		
		byte[] llaveFinal = null;


		try{
			MessageDigest hasher = MessageDigest.getInstance(HASH);
			//Hash original
			byte[] hash = hasher.digest(
				entrada.getBytes( StandardCharsets.UTF_8 )
			);
			//Representación numérica del hash
			BigInteger num = new BigInteger( 1 , hash );
			
			//Representación hexadecimal en string del hash
			StringBuilder hex = new StringBuilder( num.toString(16) );

			while(hex.length() <32 ){
				hex.insert(0,'0');
			}
			//representación hex del hash
			String hexString = hex.toString();

			//Llave final
			BigInteger llaveFinalBI = new BigInteger( hexString,16).abs();

			llaveFinal = llaveFinalBI.toByteArray();

			

		}catch( NoSuchAlgorithmException e){
			System.out.println( "Error al generar hash256" );
			System.exit(1);
		}

		return  llaveFinal;
	}
	/**
	 * Metedo que toma la llave generada en el directotio donde se encuentra
	 * @param dirLlaves directorio donde se encuentra la llave
	 * @return llave para descifrar
	 */
	public static byte[] obtenerLlaveDeDescifrado( String dirLlaves ){
		
		LinkedList<String> llaves = ManejadorArchivos.leerArchivo( dirLlaves );
		
		//NOTA: CREAR EXCEPCIÓN PARA EL CASO DE QUE ALGO QUE NO ESTÉ COMO LLAVE
		LinkedList< Vector<BigInteger> > coordenadas = new LinkedList< Vector<BigInteger> >();
		try{
			for( String llave : llaves ){

				BigInteger X = new BigInteger( llave.split(",")[0] );
				BigInteger Y = new BigInteger( llave.split(",")[1] );
				Vector<BigInteger> par = new Vector<BigInteger>();
				par.add(X);
				par.add(Y);

				coordenadas.add ( par );
			
			}

		}catch( PatternSyntaxException e ){
			terminaEjecucion("Alguna de las llaves está dañada");
		}

		//Interpolación de lagrange
		BigInteger llaveObtenidaPorLasLlaves = 
			interpolarConLagrangeEnX( coordenadas , BigInteger.ZERO );


		byte[] llave = llaveObtenidaPorLasLlaves.toByteArray();
		
		return llave;
	}	
	/**
	 * Metodo que realiza la interpolacion de Langrage
	 * @param puntos conjunto de puntos 
	 * @param x 
	 */
	public static  BigInteger interpolarConLagrangeEnX( LinkedList< Vector<BigInteger> > puntos , BigInteger x){
		BigInteger[] coordXs = new BigInteger[ puntos.size() ];
		BigInteger[] coordYs = new BigInteger[ puntos.size() ];
		int i = 0;
		for( Vector<BigInteger> v : puntos){
			coordXs[i] = v.get(0);
			coordYs[i] = v.get(1);
			i++;
		}
		return interpolarConLagrangeEnX( coordXs , coordYs , x );


	}
	/**
	 * 
	 */
	private static BigInteger productoEntradas( BigInteger[] vals ){
		BigInteger acum = BigInteger.ONE;
		for( int i = 0; i<vals.length; i++){
			BigInteger v = vals[i];
			acum = acum.multiply(v);
		}
		return acum;
	}
	/**
	 * Metodo que realiza la interpolacion de Langrage
	 * @param x_s arreglo de puntos x
	 * @param y_s arreglo de puntos y
	 * @param x 
	 */
	public static BigInteger interpolarConLagrangeEnX( BigInteger[] x_s, BigInteger[] y_s, BigInteger x){
		int k = x_s.length;

		BigInteger[] nums = new BigInteger[k];
		BigInteger[] dens = new BigInteger[k];

		for( int i = 0; i<k ; i++){
			BigInteger[] others = new BigInteger[k-1];
			BigInteger cur = null;
			//----others.pop(i)
			int cont = 0;
			for( int j = 0; j<k; j++){
				if( i!=j){
					others[cont] = x_s[j];
					cont++;
				}else
					cur = x_s[j];
			}
			//-----------
			BigInteger[] nums2 = new BigInteger[k-1];
			BigInteger[] dens2 = new BigInteger[k-1];
			for(int l = 0; l<others.length; l++){
				nums2[l] = x.subtract( others[l]);
				dens2[l] = cur.subtract( others[l]);
			}
			nums[i] = productoEntradas(nums2); 
			dens[i] = productoEntradas(dens2); 
		}
		BigInteger den = productoEntradas( dens );
		BigInteger num = BigInteger.ZERO; 
		for( int i =0; i<k; i++){
			BigInteger v = nums[i].multiply(den).multiply(y_s[i]).mod(PRIMOZP);
			num = num.add( divmod(v, dens[i]) );
		}

		BigInteger ret = divmod(num, den).add(PRIMOZP).mod(PRIMOZP);

		return ret;

}
	/**
	 * Metodo que realiza el modulo entre el numerador y el denominador
	 * @param num numerador
	 * @param den denominador
	 * @return devuleve el resto de una división
	 */
	private static BigInteger divmod( BigInteger num, BigInteger den){
		BigInteger inv = den.modInverse(PRIMOZP);
		return num.multiply(inv);
	}
	/**
	 * Metodo que genera un archivo con la llaves requeridas
	 * @param valorAOcultar bytes que se ocultaran
	 * @param numeroLlaves total de llaves
	 * @param llavesRequeridas las llaves que se requiere para descifrar
	 * @param directorio directorio donde se encuentras las llaves
	 */
	public static void generarArchivoConLlaves( byte[] valorAOcultar, int numeroLlaves, int llavesRequeridas , String directorio ){
		if(numeroLlaves < llavesRequeridas)
			terminaEjecucion( "Se requiere que las llaves requeridas sean menor o iguales a las totales");

		try{

			BigInteger valorInicial = new BigInteger( valorAOcultar );

			Polinomio polinomio = new Polinomio(llavesRequeridas-1 , valorInicial );
			BigInteger[] valoresAleatorios = Polinomio.obtenerBigNumsAleatorios( numeroLlaves );

			LinkedList< Vector<BigInteger> > puntosDelPolinomio = new LinkedList< Vector<BigInteger> >();

			//Obtener puntos con valores aleatorios
			for(int i = 0 ; i< numeroLlaves ; i++){
				Vector<BigInteger> vector = new Vector<BigInteger>();
				BigInteger valorY = polinomio.evaluarEnX( valoresAleatorios[i] );

				vector.add( valoresAleatorios[i] );
				vector.add( valorY );

				puntosDelPolinomio.add( vector );
			}	


			String llavesString = coordenadasATexto(puntosDelPolinomio );
			
			ManejadorArchivos.escribirArchivo( llavesString , directorio);	
			
			

		}catch( NumberFormatException e){ //Si a longitud de bytes es 0
			terminaEjecucion( "Error al generar valor numérico de hash" );
		}

	}

	private static String obtenerNombreArchivo( String dirArchivo ){
		int ultimaBarra = dirArchivo.lastIndexOf("/");
		int ultimoPunto = dirArchivo.lastIndexOf(".");

		String nom = dirArchivo;
		if( ultimaBarra != -1){
			nom = nom.substring( ultimaBarra+1, dirArchivo.length());
		}
		if( ultimoPunto != -1){
			nom = nom.substring(0,ultimoPunto);
		}
		return nom;
		
	}


	/**
	 * Metodo que convierte de coordenadas a texto
	 * @param coordenadas coordenadas a convertir
	 * @return texto que se convertio 
	 */
	private static String coordenadasATexto( LinkedList< Vector <BigInteger> > coordenadas){
		String texto = "";
		for( Vector< BigInteger > coordenada : coordenadas ){

			BigInteger X = coordenada.elementAt(0);
			BigInteger Y = coordenada.elementAt(1);

			texto += (
				X.toString() + ","+Y.toString()+"\n"
			);
		}
		return texto;
	
	}

	/**
	 * Metodo que termina la ejecución del mensjae
	 * @param mensaje mensaje 
	 */
	private static void terminaEjecucion( String mensaje ){
		System.out.println( mensaje);
		System.exit(1);
	}

}

