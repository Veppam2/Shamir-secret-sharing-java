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

		BigInteger x1 = new BigInteger("3");
		BigInteger y1 = new BigInteger("1");
		
		BigInteger x2 = new BigInteger("7");
		BigInteger y2 = new BigInteger("5");

		Vector<BigInteger> p1 = new Vector<>();
		p1.add(x1);
		p1.add(y1);

		Vector<BigInteger> p2 = new Vector<>();
		p2.add(x2);
		p2.add(y2);

		l.add(p1);
		l.add(p2);

		BigInteger r = 
			CifradorSecretoCompartido.interpolarConLagrangeEnX( l ,BigInteger.ZERO );
		assertTrue( r.equals( new BigInteger("2") ) );

    	}	
	/**
	* Rigorous Test :-)
	*/
	@Test
    	public void interpoladorHash(){
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
		//representación hex del hash
		String hexString = hex.toString();

		//Llave final
		BigInteger llaveFinal = new BigInteger( hexString,16).abs();
		System.out.println( "llavefinal = "+llaveFinal );
		
		System.out.println( "llavefinalToBI = "+
					(new BigInteger( llaveFinal.toByteArray() ) ) 
		);
		AssertTrue( 
			llaveFinal.equals( 
				new BigInteger( llaveFinal.toByteArray() )
			)
		);

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

		System.out.println( aOcultar );	
		System.out.println( desOcultado );	
	

		//---
		assertTrue( aOcultar.equals(desOcultado) );
		
		//PRUEBA CON LLAVE INTERPOLADA
			
		int minimoPuntosNeesarios = 4;

		BigInteger[] valoresAleatorios =
			Polinomio.obtenerBigNumsAleatorios(minimoPuntosNeesarios);
		Polinomio p = new Polinomio( minimoPuntosNeesarios-1, 
		LinkedList< Vector<BigInteger> > puntos= new LinkedList<>();
		for( BigInteger bi : valoresAleatorios ){
			Big
		}



		assertTrue( true);
		
	}


}
