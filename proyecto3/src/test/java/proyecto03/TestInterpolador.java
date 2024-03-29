package proyecto03;

import java.math.BigInteger; 
import java.util.LinkedList;
import java.util.Vector;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class TestInterpolador{
    /**
     * Rigorous Test :-)
     */
    @Test
	public void interpoladorRecta(){
		
		LinkedList<Vector<BigInteger>> l = new LinkedList<>();

		BigInteger x1 = new BigInteger("4");
		BigInteger y1 = new BigInteger("12");
		
		BigInteger x2 = new BigInteger("8");
		BigInteger y2 = new BigInteger("20");

		Vector<BigInteger> p1 = new Vector<>();
		p1.add(x1);
		p1.add(y1);

		Vector<BigInteger> p2 = new Vector<>();
		p2.add(x2);
		p2.add(y2);

		l.add(p1);
		l.add(p2);

		BigInteger r = CifradorSecretoCompartido.interpolarConLagrangeEnX( l ,BigInteger.ZERO );

		assertTrue( r.equals( new BigInteger("4") ) );

	}
	/**
	* Rigorous Test :-)
	*/
	@Test
	public void interpoladorCuadratico(){
		
		LinkedList<Vector<BigInteger>> l = new LinkedList<>();

		BigInteger x1 = new BigInteger("1");
		BigInteger y1 = new BigInteger("13");
		
		BigInteger x2 = new BigInteger("2");
		BigInteger y2 = new BigInteger("38");
		
		BigInteger x3 = new BigInteger("3");
		BigInteger y3 = new BigInteger("93");
		
		BigInteger x4 = new BigInteger("4");
		BigInteger y4 = new BigInteger("190");

		Vector<BigInteger> p1 = new Vector<>();
		p1.add(x1);
		p1.add(y1);

		Vector<BigInteger> p2 = new Vector<>();
		p2.add(x2);
		p2.add(y2);
		
		Vector<BigInteger> p3 = new Vector<>();
		p3.add(x3);
		p3.add(y3);
		
		Vector<BigInteger> p4 = new Vector<>();
		p4.add(x4);
		p4.add(y4);

		l.add(p1);
		l.add(p2);
		l.add(p3);
		l.add(p4);

		BigInteger r = CifradorSecretoCompartido.interpolarConLagrangeEnX( l ,BigInteger.ZERO );

		assertTrue( r.equals( new BigInteger("6") ) );

	}
	/**
	* Rigorous Test :-)
	*/
	@Test
    	public void llaveHash(){
		String pwd = "Contraseña muy compleja 1234!!123#$/&$/41245$%&$";
		byte[] hashA = CifradorSecretoCompartido.obtenerLlaveSHA256( pwd );
		BigInteger bi = new BigInteger(hashA);

		byte[] hashB  = bi.toByteArray();

		assertTrue( MessageDigest.isEqual( hashA , hashB ) );

		String pwd2= "Adios";
		byte[] hashD= CifradorSecretoCompartido.obtenerLlaveSHA256( pwd2 );
		
		assertFalse( MessageDigest.isEqual( hashA , hashD ) );
		

	}			
	/**
	* Rigorous Test :-)
	*/
	@Test
	public void testInterpoladorHash() throws Exception{
		
		String pwd = "Hola mundo.1234!#$";
		String aOcultar = "Secreto porfavof funciona 1231 1$%#&#$";

		MessageDigest md = MessageDigest.getInstance( "SHA-256" );
		//Hash original
		byte[] hash = md.digest(
				pwd.getBytes( StandardCharsets.UTF_8) 
		);

		
		//Representación numérica del hash
		BigInteger num = new BigInteger( 1, hash );
		
		
		StringBuilder hex = new StringBuilder( num.toString(16) );

		while(hex.length() <32 ){
			hex.insert(0,'0');
		}
		
		/*	
		StringBuffer hex = new StringBuffer();
		for(byte b : hash){
			hex.append(
				Integer.toString( ((b&0xff)+0x100),16).substring(1)	
			);
		}*/


		//representación hex del hash
		String hexString = hex.toString();

		//Llave final
		BigInteger llaveFinal = new BigInteger( hexString,16).abs();
		
		//Test para verificar que la transformación a byte y de regreso no altera el valor
		assertTrue( 
			llaveFinal.equals( 
				new BigInteger( llaveFinal.toByteArray() )
			)
		);
		//Verifica que la llave sea menor al primo p del campo Zp
		assertTrue( llaveFinal.compareTo( CifradorSecretoCompartido.PRIMOZP ) == -1); 

		//Ocultar 
		Cipher cifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec sec = new SecretKeySpec( 
			llaveFinal.toByteArray()
			,"AES"
		);
		cifrador.init( Cipher.ENCRYPT_MODE, sec);
		byte[] secretoOculto = cifrador.doFinal( aOcultar.getBytes() );
		

		//descifrar
		Cipher descifrador = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec sec2 = new SecretKeySpec( 
			llaveFinal.toByteArray()
			,"AES"
		);
		descifrador.init( Cipher.DECRYPT_MODE, sec2);
		byte[] secretoDescifrado = descifrador.doFinal( secretoOculto );

		String desOcultado = new String ( secretoDescifrado );

	

		//Verifica que el método general de cifrado y descifrado conserva la información cifrada y descifrada
		assertTrue( aOcultar.equals(desOcultado) );
		
		//PRUEBA CON LLAVE INTERPOLADA
		int minimoPuntosNeesarios = 4;
		BigInteger valorInicial = llaveFinal;

		BigInteger[] valoresAleatorios =
			Polinomio.obtenerBigNumsAleatorios(minimoPuntosNeesarios+1);
		Polinomio p = new Polinomio( minimoPuntosNeesarios-1, valorInicial); 
		//Verifica que la evaluación en 0 sea el valor a ocultar
		assertTrue( p.evaluarEnX( BigInteger.ZERO).equals(valorInicial) );
		
		LinkedList< Vector<BigInteger> > puntos= new LinkedList<>();
		for( BigInteger X : valoresAleatorios ){
			BigInteger Y = p.evaluarEnX( X );

			//Verificar que los coeficientes sean menores a el primo de Zp
			assertTrue( X.compareTo( CifradorSecretoCompartido.PRIMOZP) ==-1);
			assertTrue( Y.compareTo( CifradorSecretoCompartido.PRIMOZP) ==-1);

			Vector<BigInteger> v = new Vector<>();
			v.add(X);
			v.add(Y);

			puntos.add(v);
		}

		BigInteger llaveInterpolada = 
			CifradorSecretoCompartido.interpolarConLagrangeEnX( puntos , BigInteger.ZERO );
		

		//Verifica que la llave interpolada sea el valor que queremos ocultar
		assertTrue( llaveInterpolada.equals(valorInicial) );

		
		//descifrar con llave interpolada
		Cipher descifrador2 = Cipher.getInstance("AES/ECB/PKCS5Padding");
		SecretKeySpec sec3 = new SecretKeySpec( 
			llaveInterpolada.toByteArray()
			,"AES"
		);
		descifrador2.init( Cipher.DECRYPT_MODE, sec3);
		byte[] secretoDescifrado2 = descifrador2.doFinal( secretoOculto );

		String desOcultado2 = new String ( secretoDescifrado2 );


		assertTrue(  aOcultar.equals(desOcultado2) );
		
	}


}
