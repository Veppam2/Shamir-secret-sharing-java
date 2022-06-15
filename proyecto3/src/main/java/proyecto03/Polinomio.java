package proyecto03;

import java.math.BigInteger; //Para el manejo de números descomunales 
import java.security.SecureRandom; //Generar coeficientes aleatorios

import java.util.Vector;
import java.util.LinkedList;

/**
 * Clase que crea polinomios y evalua la interpolacion de la
 */
public class Polinomio{

	private static final int LONGITUD_NUMERO_BITS_HEX = 16; 

	private int grado;
	private BigInteger[] coeficientes;
	private static final BigInteger PRIMOZP=
		new BigInteger(
				"208351617316091241234326746312124448251235562226470491514186331217050270460481"
	);
	/**
	 * Constructor con parametros para un Polinomio
	 * @param grado grado del polinomio
	 * @param valorInicial cadena con el valor inicial
	 */
	public Polinomio(int grado, String valorInicial){
		
		this(
			grado,
			new BigInteger(valorInicial, LONGITUD_NUMERO_BITS_HEX) 	
			
	    	    );

	}
	/**
	 * Constructor con parametros para un Polinomio
	 * @param grado grado del polinomio
	 * @param valorInicial el valor inicial del polinomio
	 */
	public Polinomio(int grado, BigInteger valorInicial){
		/**
		 * Un polinomio de grado n sobre una estructura C se define como:
		 * 	p(x) = a_0*x^0 + a_1*x^1 + ... + a_(n-1)*x^(n-1) + a_n*x^n
		 * donde cada a_i está en C y n>= 0
		 **/

		this.grado = grado;
		this.coeficientes = new BigInteger[ this.grado+1 ]; //para cada i en {0,..n}
		
		/**
		 *
		 * En nuestra implementación de polinomio, el valor a_0, es decir, el valor inicial ya está dado, pues es el que nos interesa para el esquema de secreto compartido.
		 * 
		 * Para cada a_i con i de {0,...,n} guardamos en ese orden en nuestro arreglo 'coeficientes'. El índice 0, i.e a_0 es el valor inicial dado en la instanciación.
		 * */

		this.coeficientes[0] = valorInicial;

		/**
		 * Usando el esquema de secreto compartido, dado un valor entero D que queremos esconder, escogemos un número primo p (en nuestra implementación 'PRIMOZP') mayor que D y el número de partes en la que queremos dividir la información ( por lo tanto, mayor al grado del polinomio). 
		 *
		 * Dado p ('PRIMOZP') escogemos los coeficientes a_i con i de {1,..,n} de manera aleatoria en el intervalo [0,PRIMOZP)
		 * 
		 * OBSERVACION] Dada la implementación, no importa demasiado si es aleatoria o no. En este caso seguimos el esquema propuesto por A.Shamir en "How to Share a Secret".
		 *
		 * Utilizamos la biblioteca SecureRandom para generar números aleatorios no deterministas.
		 *
		 * */
		
		/**
		 *OBSERVACION: 
		 *	De acuerdo a nuestra implementación, cada coeficiente del polinomio debe estar distribuido sobre el intervalo [0,PRIMOZP]. De acuerdo a la implementación del método 'valueOf()' de SecureRandom, el número que regresa es de a lo más 31 bits y mayor a 0 bits, por lo que el coeficiente es menor a PRIMOZP y se mantiene en el intervalo que buscamos. 
		 *
		 *Implementación de valueOf():
		 *	https://docs.oracle.com/javase/8/docs/api/java/util/Random.html#nextInt--
		 **/

		BigInteger[] coeficientesAleatorios = this.obtenerBigNumsAleatorios( this.coeficientes.length-1 );

		for(int i = 0; i<coeficientesAleatorios.length; i++){

			this.coeficientes[i+1] = coeficientesAleatorios[i];
		}

	}
	/**
	 * Metodo que genera y obtiene numeros aleatorios
	 * @param cantidadNumerosAleatorios numeros aleatorios
	 * @return la cantidad de numeros aleatorios
	 */
	public static BigInteger[] obtenerBigNumsAleatorios( int cantidadNumerosAleatorios){

		SecureRandom generadorAleatorios = new SecureRandom();

		BigInteger[] numerosAleatorios = 
			new BigInteger[cantidadNumerosAleatorios];

		for(int i = 0; i<cantidadNumerosAleatorios; i++){

			int enteroRandom = generadorAleatorios.nextInt();
			BigInteger BIRandom = BigInteger.valueOf(enteroRandom).mod(PRIMOZP);	

			numerosAleatorios[i] = BIRandom;
		}

		return numerosAleatorios;
	}

	/**
	 * Metodo que evalua la evaluaciones del Polinomio
	 * @param x evaluaciones 
	 * @return devuelve las evaluaciones
	 */
	public BigInteger evaluarEnX( BigInteger x){
		/*
		 * Usando el algoritmo de Horner
		 * */
		BigInteger resultado = BigInteger.ZERO;
		for( int i = this.grado; i>=0  ; i--){
			//resultado = resultado.multiply( x ).mod(PRIMOZP).add(this.coeficientes[i]).mod(PRIMOZP);
			//resultado = this.coeficientes[i].add(x.multiply(resultado));
			resultado = this.coeficientes[i].add(x.multiply(resultado).mod(PRIMOZP) ).mod(PRIMOZP);
			
		}

		return resultado;
	}
	


}
