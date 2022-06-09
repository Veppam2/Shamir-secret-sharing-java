package proyecto03;

import java.math.BigInteger; 

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class TestPolinomio{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void polinomioEvaluadoEn0()
    {
	BigInteger cero = BigInteger.ZERO;
	BigInteger num = BigInteger.valueOf(45);
	    
	Polinomio p = new Polinomio(1, num);
	BigInteger v = p.evaluarEnX( cero );
        assertTrue( v.equals(num) );

	p = new Polinomio(10, num);
	v = p.evaluarEnX(cero);
        assertTrue( v.equals(num) );
	
	p = new Polinomio(50, num);
	v = p.evaluarEnX(cero);
        assertTrue( v.equals(num) );
    }
	


}
