import java.io.*;
import exceptions.*;

/**
 * This class is the Main class.
 */
public class Main {
	/**
	 * main函数
	 *
	 * @param 源文件目录
	 */
	public static void main(String[] args) {
		String token;
		if (args.length == 0) {
			System.out.println("Usage : java Main <inputfile>");
		}
		for (int i = 0; i < args.length; i++) {
			try {
				OberonScanner scanner = new OberonScanner(new java.io.FileReader(args[i]));				
				while (true) {
					try {
						token = scanner.yylex();
						System.out.println(token + " : " + scanner.yytext());
					} catch (LexicalException excep) {
						System.out.println(args[i]+ " : "+ excep.getMessage());
						System.out.print("Line "+ scanner.get_line() + ", Colume " + scanner.get_column() + ": ");
						System.out.println(scanner.yytext());
						System.out.println("-------------------------------------------------------------------------------------------------"+ "\n");						
						break;
					}
					if (token.equals("EOF")) {
						System.out.println("Scanning Finished, No Error Found.");
						break;
					}
				}
			} catch (Exception excep) {
				excep.printStackTrace();
			}
		}
	}
}
