import java.io.*;
import exceptions.*;

/**
 * The main class
 *
 */
public class Main {
	/**
     * main函数
	 * @param 源文件
	 */
	public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            OberonScanner scanner = null;
            try {
                scanner = new OberonScanner(new java.io.FileReader(args[i]));
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
			Parser p = new Parser(scanner);
            System.out.print(args[i]+":");
            try {	
                p.parse();
            }
            catch(Exception ex){
				int line = scanner.get_line()+1;
				int column = scanner.get_column() +1;
                System.out.println("Error position : Line "+line+"  Column "+ column + ex +"\n");
                
            }
        }

	}

}
