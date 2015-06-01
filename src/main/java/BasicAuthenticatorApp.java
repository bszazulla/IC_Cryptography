import java.io.Console;
import java.util.Scanner;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.StringUtils;

/**
 * Implementação de um autenticador digital de dois fatores básico
 * 
 * @author Beatriz Sechin Zazulla
 *
 */

public class BasicAuthenticatorApp
{
	public static void main(String[] args)
	{
		// Instânciação de SecurityManager - Ele que realiza toda a comunicação e gerenciamento do framework
		// O securityManager atua como uma espécie de motor do framework, um gerenciador geral
		// Ele é instanciado a partir de uma Abstract Factory escolhida de acordo com o construtor (no caso, um INI)
		Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:dataUsers.ini");
		SecurityManager securityManager = factory.getInstance();
		SecurityUtils.setSecurityManager(securityManager);

		// obter um Subject, que é quem vai se comunicar com o securityManager - Subject será, no caso, um user
		Subject currentUser = SecurityUtils.getSubject();

		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println("                               Rotina básica de autenticação com dois fatores                                ");
		System.out.println("-------------------------------------------------------------------------------------------------------------\n");

		// Obter o usuário e a senha, do console ou da IDE mesmo
		Console console = System.console();
		Scanner scanner = new Scanner(System.in);

		// 5 tentativas no máximo
		int triesLeft = 5;
		while (!currentUser.isAuthenticated())
		{
			String username;
			
			// O Java dá preferência ao uso de char array com senhas, pois as strings são imutáveis e seu acesso pode ser facilente feito na VM
			// depois do uso. Isso é um problema no shiro, pq a cópia feita por ele é rasa (shallow copy), ele usa a mesma referência ¬¬ 
			char[] password; 
			
			if (console != null)
			{
				username = console.readLine("Digite o usuário: ");
				password = console.readPassword("Digite a senha: ");
			}
			else
			{
				System.out.print("Digite o usuário: ");
				username = scanner.nextLine();
				System.out.print("Digite a senha: ");
				password = scanner.nextLine().toCharArray();
			}
			UsernamePasswordToken userCredentials = new UsernamePasswordToken(username, password);
			
			// apontar o password para null para que não seja tão fácil recuperá-lo na VM
			password = null;
			
			// tentativa de login e tratamento da falha geral no login
			try
			{
				System.out.println();
				currentUser.login(userCredentials);
			}
			catch (AuthenticationException e)
			{
				System.err.print("\nFalha na verificação de usuário e senha.");
				triesLeft--;
			}

			if (triesLeft == 0)
			{
				System.err.println("Tentativas de login excedidas para esta execução.\nPor favor, reinicie o programa para tentar novamente.\n");
				System.out.println("-------------------------------------------------------------------------------------------------------------");
				System.exit(1);
			}
			
			if(!currentUser.isAuthenticated())
			{
				System.out.println(" Por favor, tente novamente.\n");
			}
		}
		
		System.out.println("\nUsuário e senha verificados com sucesso! :)\n");
		System.out.print("Use, agora, o aplicativo TokenGenerator e digite o código de verificação a seguir: ");
		
		String codigo;
		
		// 5 tentativas
		triesLeft = 5;
		boolean validated = false;
		while(!validated)
		{
			if (console != null)
				codigo = console.readLine();
			else
				codigo = scanner.nextLine();
		
			String token = TokenGenerator.generateToken();
		
			if (token.equals(codigo))
			{
				System.out.println("\nCódigo de verificação validado com sucesso! :)\n");
				validated = true;
			}
			else
			{
				System.out.print("\nHouve um erro na validação de seu código. Por favor, tente novamente: ");
				triesLeft--;
			}
			
			if(triesLeft == 0)
			{
				System.out.println("\nTentativas de validação excedidas para esta execução. Por favor, reinicie o programa para tentar novamente.\n");
				System.out.println("-------------------------------------------------------------------------------------------------------------");
				System.exit(1);
			}
		}
		
		System.out.println("-------------------------------------------------------------------------------------------------------------");
		System.out.println("                                       Usuário autenticado com sucesso! :)                                   ");
		System.out.println("-------------------------------------------------------------------------------------------------------------");

		scanner.close();
	}
}