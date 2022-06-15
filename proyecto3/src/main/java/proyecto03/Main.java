package proyecto03;

import java.io.Console;


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

			if( numeroLlavesPorHacer < numeroLlavesRequeridas){		
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
	private static String obtenerNombreArchivo( String arch){
		
		int archivo = arch.lastIndexOf("/");
		
		String nom= arch;
		if(archivo!=-1)
			nom = arch.substring(archivo+1,arch.length());

		return nom;
		
	}
	

	public static void main( String[] args )
    	{
		if( args.length == 5 ){ //Cifrar
			
			validarEntradaParaCifrar( args );
			
			//Obtener la llave y hacer el archivo con las llaves
			String nombreArchivoLlaves = (args[1]+".fgr");
		       	int numeroLlavesTotales = Integer.valueOf( args[2] );
		       	int numeroLlavesMinimo = Integer.valueOf( args[3] );

			String contrasena = pedirContrasena();
			byte[] contrasenaHasheada  =
				CifradorSecretoCompartido.obtenerLlaveSHA256( contrasena );
			//Cifrar el archivo con la llave creada

			String nombreArchivo = obtenerNombreArchivo( args[4] );	

			CifradorSecretoCompartido.cifrarArchivoConLLave(
					args[4],
					nombreArchivo,
					contrasenaHasheada
			);
			

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
			
			CifradorSecretoCompartido.descifrarArchivoConLlave(
					dirArchivoCifrado,
					llaveEnBytes
			);

		}else{
			imprimirUso();
			System.exit(1);
		}
    	}

}
