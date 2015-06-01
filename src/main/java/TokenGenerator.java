import java.util.Random;

/**
 * Rotina de geração de tokens de seis dígitos
 * 
 * @author Beatriz Sechin Zazulla
 *
 */

public class TokenGenerator
{
	public static void main(String[] args)
	{
		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println("                                Rotina de geração de tokens de seis dígitos                                  ");
		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println();
		System.out.println("                                                  "+generateToken()+"                                        ");
		System.out.println();
		System.out.println("-------------------------------------------------------------------------------------------------------------");
	}

	public static String generateToken()
	{
		long minutes = System.currentTimeMillis() / 60000;
		Random rd = new Random(minutes);

		int token = rd.nextInt(1000000);

		return String.format("%06d", token);
	}
}
