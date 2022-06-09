package proyecto03;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.BufferedReader;

import java.util.LinkedList;

import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.FileWriter;
	import java.io.BufferedWriter;

import java.io.IOException;
	import java.io.FileNotFoundException;

public class ManejadorArchivos{

	private ManejadorArchivos(){}

	public static void escribirArchivo( String contenido, String directorio ){

		BufferedWriter writer =null;
		try{
			FileOutputStream arch = new FileOutputStream( directorio.replaceAll("/", "//") );
			writer = new BufferedWriter( new OutputStreamWriter(arch) );

			writer.write( contenido );

		}catch(IOException e){
			System.err.println("Error al escribiri archivo");
		}finally{
			if(writer != null){
				try{
					writer.close();
				}catch(IOException ee){
					
				}
			}
		}
		
	}	

	public static LinkedList<String> leerArchivo(String ruta){

		BufferedReader reader;
		FileReader arch;
		LinkedList<String> lineasLeidas = new LinkedList<String>();
		try{	
			arch = new FileReader( ruta.replace("/", "//") ); 
			reader = new BufferedReader( arch );
			
			String lineaLeida = new String();

			while( (lineaLeida = reader.readLine() ) != null){
				lineasLeidas.add(lineaLeida);
			}


		}catch( FileNotFoundException e ){
			System.out.println("No se pudo encontrar el archivo"+ruta);
			System.exit(1);
		}catch( IOException ee){
			System.out.println("No se pudo encontrar el archivo"+ruta);
			System.exit(1);
		
		}

		return lineasLeidas;

	}
	
}
