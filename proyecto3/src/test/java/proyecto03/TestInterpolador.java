package proyecto03;

import java.math.BigInteger; 
import java.util.LinkedList;
import java.util.Vector;

import static org.junit.Assert.assertTrue;

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
	


}
