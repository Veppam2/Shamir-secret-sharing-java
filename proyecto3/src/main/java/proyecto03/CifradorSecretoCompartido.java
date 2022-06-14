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

	public static final BigInteger PRIMOZP= 
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
			flujoEntrada = new CipherInputStream(
				new FileInputStream( dirArchivo),
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
	private static BigInteger productoEntradas( BigInteger[] vals ){
		BigInteger acum = BigInteger.ONE;
		for( int i = 0; i<vals.length; i++){
			BigInteger v = vals[i];
			acum = acum.multiply(v);
		}
		return acum;
	}
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

	private static BigInteger divmod( BigInteger num, BigInteger den){
		BigInteger inv = den.modInverse(PRIMOZP);
		return num.multiply(inv);
	}

	public static void generarArchivoConLlaves( byte[] valorAOcultar, int numeroLlaves, int llavesRequeridas , String directorio ){
		if(numeroLlaves < llavesRequeridas)
			terminaEjecucion( "Se requiere que las llaves requeridas sean menor o iguales a las totales");

		try{

			BigInteger valorInicial = new BigInteger( valorAOcultar );

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
	private static void terminaEjecucion( String mensaje ){
		System.out.println( mensaje);
		System.exit(1);
	}

}

