package proyecto03;

import java.security.MessageDigest;
import java.math.BigInteger;
import java.util.Vector;
import java.util.LinkedList;
import java.lang.StringBuffer;
import java.nio.charset.StandardCharsets;

import java.security.NoSuchAlgorithmException;
import java.lang.NumberFormatException;
import java.util.regex.PatternSyntaxException;

public class CifradorSecretoCompartido{

	private static String CIFRADO = "SHA-256";
	private static final BigInteger PRIMOZP= 
		new BigInteger(
			"208351617316091241234326746312124448251235562226470491514186331217050270460481"
		);

	private CifradorSecretoCompartido(){}

	public static BigInteger obtenerLlaveSHA256( String entrada ){
		
		byte[] hash = null;


		try{
			MessageDigest hasher = MessageDigest.getInstance(CIFRADO);
			hash = hasher.digest(
				entrada.getBytes( StandardCharsets.UTF_8) 
			);

			

		}catch( NoSuchAlgorithmException e){
			System.out.println( "Error al generar hash256" );
			System.exit(1);
		}

		BigInteger llave = unificadorHashConBigInteger( hash );


		return  llave;
	}
	private static BigInteger unificadorHashConBigInteger( byte[] hash ){

		/*
		StringBuffer llave = new StringBuffer();

		for( byte b : hash ){
			String v = Integer.toString( ((b & 0xff) +0X100) , 16 );
			llave.append( v.substring(1));
		}
		*/
		BigInteger num = new BigInteger(1, hash);
		//StringBuilder hexString = new StringBuilder( num.to

		return num; 

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
			interpolarConLagrangeEnX( coordenadas , BigInteger.ZERO );

		return llaveObtenidaPorLasLlaves.toByteArray();

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

	public static void generarArchivoConLlaves( BigInteger valorAOcultar, int numeroLlaves, int llavesRequeridas , String directorio ){
		if(numeroLlaves < llavesRequeridas)
			terminaEjecucion( "Se requiere que las llaves requeridas sean menor o iguales a las totales");

		try{
			BigInteger valorInicial = valorAOcultar;
			//BigInteger valorInicial = new BigInteger( valorAOcultar , 16 );

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

