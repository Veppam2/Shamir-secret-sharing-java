package proyecto03;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.io.Console;

import java.security.NoSuchAlgorithmException;

/**
 * Hello world!
 *
 */
public class Main{
	private static void imprimirUso(){
		System.out.println(
			"Esquema de secreto compartido. USO:\n"+
			"Se debe proporcionar para cifrar o descifrar un archivo con las llaves correspondientes los siguientes datos:\n"+
			"Para CIFRAR:\n"+
			"	> -c\n"+
			"	> [Nombre del archivo donde se va a guardar llaves de acceso]\n"+
			"	> [Número de llaves a generar: n]\n"+
			"	> [Mínimo de llaves para descifrar: t, t<= n]\n"+
			"	> [Nombre del archivo a cifrar]\n"+
			"Para DESCIFRAR:\n"+
			"	> -d\n"+
			"	> [Nombre del archivo con t llaves de acceso]\n"+
			"	> [Archivo Cifrado]\n"
		);
	
	}
	private static void validarEntradaParaCifrar( String[] datos){

		String bandera = datos[0];
		String stringLlavesPorHacer = datos[2];
		String stringNumeroLlavesRequeridas = datos[3];

		if( !bandera.equals("-c") ){
			imprimirUso();
			System.exit(1);
		}
		try{
			int numeroLlavesPorHacer = Integer.valueOf( stringLlavesPorHacer );
			int numeroLlavesRequeridas = Integer.valueOf( stringNumeroLlavesRequeridas );

			if( numeroLlavesRequeridas < numeroLlavesRequeridas){		
				imprimirUso();
				System.exit(1);
			}


		}catch( NumberFormatException e ){
			imprimirUso();
			System.exit(1);
		}
		
	}
	private static String pedirContrasena(){
		
		Console terminal;
		char[] contrasena = null;
	
		if( (terminal = System.console())==null || 
		    (contrasena = terminal.readPassword( "%s", "Ingrese una contraseña: "))==null ){
		    	System.out.println( "Error al leer la contraseña" );
			System.exit(1);
		    }
		return String.valueOf( contrasena );
			
	}

	public static void main( String[] args )
    	{
		if( args.length == 5 ){ //Cifrar
					//
			validarEntradaParaCifrar( args );

			String nombreArchivoLlaves = (args[1]+".fgr");
		       	int numeroLlavesTotales = Integer.valueOf( args[2] );
		       	int numeroLlavesMinimo = Integer.valueOf( args[3] );

			String contrasena = pedirContrasena();
			BigInteger contrasenaHasheada  =
				CifradorSecretoCompartido.obtenerLlaveSHA256( contrasena );

			System.out.println( contrasenaHasheada ); 
			CifradorSecretoCompartido.generarArchivoConLlaves(
					contrasenaHasheada,
					numeroLlavesTotales,
					numeroLlavesMinimo,
					nombreArchivoLlaves
			);
						

		}else if( args.length == 3 ){ //Descifrar
			String dirArchivoConLlaves = args[1];
			String dirArchivoCifrado = args[2];

			byte[] llaveEnBytes = 
				CifradorSecretoCompartido.obtenerLlaveDeDescifrado ( dirArchivoConLlaves );
			System.out.println( new BigInteger( llaveEnBytes ) ); 
		}else{
			imprimirUso();
			System.exit(1);
		}
    	}

}
