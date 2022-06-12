package proyecto03;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Vector;
import java.util.LinkedList;
import java.lang.StringBuffer;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.CipherInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.Character;
import java.nio.charset.StandardCharsets;

//import javax.xml.bind.DatatypeConverter;


//import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import java.lang.NumberFormatException;
import java.util.regex.PatternSyntaxException;
import java.io.FileNotFoundException;
import java.lang.SecurityException;

public class CifradorSecretoCompartido{

	private static String  HASH = "SHA-256";
	private static String CIFRADO = "AES/ECB/PKCS5Padding";
	private static String ALGORITMO_CIFRADO = "AES";

	private static final BigInteger PRIMOZP= 
		new BigInteger(
			"208351617316091241234326746312124448251235562226470491514186331217050270460481"
		);

	private CifradorSecretoCompartido(){}

	public static void descifrarArchivoConLlave( String dirArchivo , byte[] llave){

		Cipher descifrador = null;
		SecretKeySpec llaveSecreta = null;
		CipherInputStream flujoEntrada = null;

		FileOutputStream flujoSalida = null;

		//Obtener el nombre original del archivo. Por implementar.
		String nombreArchivoDescifrado = "secreto.txt";

		try{
			//Descifrar el archivo 
			descifrador = Cipher.getInstance(CIFRADO);	
			llaveSecreta = new SecretKeySpec(
				llave,
			       	ALGORITMO_CIFRADO 
			);
			descifrador.init( Cipher.DECRYPT_MODE, llaveSecreta );

			flujoSalida = new FileOutputStream( nombreArchivoDescifrado , true );
			System.out.println( dirArchivo );
			flujoEntrada = new CipherInputStream(
				new FileInputStream( dirArchivo),
			       	descifrador 
			);
			//meter contenido descifrado
			int estado;
			while( (estado = flujoEntrada.read() ) != -1){
			System.out.println( "ENTRA" );
				flujoSalida.write(estado);
			System.out.println( "SALE" );
			}
			flujoSalida.close();
			flujoEntrada.close();

		}catch( FileNotFoundException e){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivo+"'. No fue encontrado");
		}catch( SecurityException ee){
			terminaEjecucion( "Error al leer el archivo '"+dirArchivo+"'. Permiso denegado");
		}
		catch( Exception eee ){
			System.err.println(eee);
			terminaEjecucion( "Error al escribir el archivo cifrado" );
		}	

	
	
	}

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
			System.out.println( dirArchivo );
			flujoEntrada = new CipherInputStream(
				new FileInputStream( dirArchivo),
			       	cifrador 
			);
			//Meter nombre a archivo
			
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

	public static byte[] obtenerLlaveSHA256( String entrada ){
		
		byte[] llaveFinal = null;


		try{
			MessageDigest hasher = MessageDigest.getInstance(HASH);
			//hasher.update( entrada.getBytes() );
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
			System.out.println(llaveFinalBI);

			llaveFinal = llaveFinalBI.toByteArray();

			

		}catch( NoSuchAlgorithmException e){
			System.out.println( "Error al generar hash256" );
			System.exit(1);
		}

		return  llaveFinal;
	}
	private static String unificadorHashConBigInteger( byte[] hash ){

		StringBuffer llave = new StringBuffer();

		for( byte b : hash ){

			/*
			if( (b & 0xff) < 0x10)
				llave.append("0");
			*/

			String v = Integer.toString( ((b & 0xff) +0X100) , 16 );
			llave.append( v.substring(1));
		}
		//BigInteger num = new BigInteger( llave.toString() , 16);
		//StringBuilder hexString = new StringBuilder( num.to

		return llave.toString(); 

	}
	public static byte[] unificadorBigIntegerConHash( BigInteger llaveBI ){
		
		String llaveString = llaveBI.toString(16);
		/*
		int len = llaveString.length();
		System.out.println( len );
		byte[] llave = new byte[len/2];
		System.out.println( llave.length );
		for( int i = 0; i<len ; i+=2 ){
			llave[i/2] =
			       	(byte) ( (Character.digit(llaveString.charAt(i),16) << 4)+
					Character.digit(llaveString.charAt(i+1),16) );
		}
		*/
		return llaveBI.toByteArray();

	}

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

		//POderosa interpolación de lagrange
		BigInteger llaveObtenidaPorLasLlaves = 
			interpolarConLagrangeEnX( coordenadas , BigInteger.ZERO ).abs();
		System.out.println(llaveObtenidaPorLasLlaves);
		byte[] llave = llaveObtenidaPorLasLlaves.toByteArray();
		
		/*
		byte[] llave = 
			unificadorBigIntegerConHash( llaveObtenidaPorLasLlaves );
		*/
			
		
		return llave;
	}	

	public static  BigInteger interpolarConLagrangeEnX( LinkedList< Vector<BigInteger> > puntos , BigInteger x){
		
		BigInteger resultado = BigInteger.ZERO;
		
		//Interpolación de Lagrange
		//for ( int i = 0 ; i < puntos.length ; i++){
		int i = 0;
		for( Vector< BigInteger> punto : puntos ){

			BigInteger coordXi = punto.elementAt(0);
			BigInteger coordYi = punto.elementAt(1);

			BigInteger numerador = BigInteger.ONE;
			BigInteger denominador = BigInteger.ONE;
			
			int j =0;
			//for( int j = 0 ; j <puntos.length ; j++){
			for( Vector< BigInteger> punto2: puntos){
				//Base de Lagrange
				if(i!=j){
					BigInteger coordXj = punto2.elementAt(0);
					
					//Operaciones en el campo Zp
					numerador = productoEnZp(
							numerador , 
							restaEnZp( x , coordXj) 
					);
					denominador = productoEnZp( 
							denominador , 
							restaEnZp( coordXi , coordXj) 
					);


				}
				j++;
			}	

			//Forma de Lagrange
			BigInteger baseDeLagrange = productoEnZp(
					numerador , 
					denominador.modInverse(PRIMOZP)
			);
			BigInteger formaDeLagrangePi = productoEnZp( 
					coordYi , 
					baseDeLagrange 
			);

			resultado = sumaEnZp( resultado , formaDeLagrangePi );
			i++;	
		}
		return resultado;
		
	}

	public static void generarArchivoConLlaves( byte[] valorAOcultar, int numeroLlaves, int llavesRequeridas , String directorio ){
		if(numeroLlaves < llavesRequeridas)
			terminaEjecucion( "Se requiere que las llaves requeridas sean menor o iguales a las totales");

		try{
			/*
			String valorInicial = 
				unificadorHashConBigInteger( valorAOcultar );
			*/
			

			BigInteger valorInicial = new BigInteger( valorAOcultar );
			//SYSTEM	

			/*System.out.println( 
				"Amtes" + MessageDigest.isEqual( valorAOcultar, valorInicial.toByteArray())
			);*/

			Polinomio polinomio = new Polinomio(llavesRequeridas-1 , valorInicial );
			BigInteger[] valoresAleatorios = polinomio.obtenerBigNumsAleatorios( numeroLlaves );

			LinkedList< Vector<BigInteger> > puntosDelPolinomio = new LinkedList< Vector<BigInteger> >();

			//Obtener puntos con valores aleatorios
			for(int i = 0 ; i< numeroLlaves ; i++){
				Vector<BigInteger> vector = new Vector<BigInteger>();
				BigInteger valorY = polinomio.evaluarEnX( valoresAleatorios[i] );

				vector.add( valoresAleatorios[i] );
				vector.add( valorY );

				puntosDelPolinomio.add( vector );
			}	

			//Probar el interp 
			BigInteger despues = 
				interpolarConLagrangeEnX(puntosDelPolinomio,BigInteger.ZERO);

			/*System.out.println( 
				"Despues" + MessageDigest.isEqual( valorAOcultar, despues.toByteArray())
			);*/



			String llavesString = coordenadasATexto(puntosDelPolinomio );
			
			ManejadorArchivos.escribirArchivo( llavesString , directorio);	
			
			

		}catch( NumberFormatException e){ //Si a longitud de bytes es 0
			terminaEjecucion( "Error al generar valor numérico de hash" );
		}




	}
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

	private static BigInteger sumaEnZp( BigInteger a , BigInteger b){

		BigInteger suma = a.subtract(b);
		suma = suma.mod(PRIMOZP);

		return suma;
	}

	private static BigInteger restaEnZp( BigInteger a , BigInteger b){

		BigInteger resta = a.subtract(b);
		resta = resta.mod(PRIMOZP);

		return resta;
	}
	
	private static BigInteger  productoEnZp( BigInteger a , BigInteger b){

		BigInteger producto = a.multiply(b);
		producto = producto.mod(PRIMOZP);

		return producto;
	}

	private static BigInteger divisionEnZp( BigInteger a , BigInteger b){

		BigInteger division = a.divide(b);
		division = division.mod(PRIMOZP);
		
		return division;
	}


	private static void terminaEjecucion( String mensaje ){
		System.out.println( mensaje );
		System.exit(1);
		
	}

}

