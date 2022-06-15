package proyecto03;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import java.util.LinkedList;


import java.io.IOException;
	import java.io.FileNotFoundException;
/**
*Clase que trabaja con archivos de texto 
*/
public class ManejadorArchivos{
	
	/**
	 * Constructor vacio
	 */
	private ManejadorArchivos(){}
	
	/**
	 * Metodo que escribe un archivo y su directorio 
	 * @param contenido el texto que se escribirá
	 * @param directorio directorio donde se encontrará el archivo 
	 */
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

	/**
	 * Metodo que lee el archivo 
	 * @param ruta directorio donde se encuntra el archivo
	 * @return las lineas que se leyeron del archivo
	 */
	public static LinkedList<String> leerArchivo(String ruta){

		FileInputStream file = null;
		DataInputStream  input = null;
		BufferedReader reader = null;

		LinkedList<String> lineasLeidas = new LinkedList<String>();

		try{	
			file = 
				new FileInputStream(ruta.replace("/","//"));
			input =
				new DataInputStream(file);

			reader = new BufferedReader(
				new InputStreamReader(input)
			);
	
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
