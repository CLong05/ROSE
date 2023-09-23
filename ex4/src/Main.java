import java.io.*;

/**
 * The main class
 * @author 陈泷 19335019
 */
public class Main {
	/**
     * main函数
	 * @param argv 源文件
	 */
	public static void main(String[] argv) throws Exception{
		for (int i = 0; i < argv.length; i++) {
			OberonScanner scanner = new OberonScanner(new java.io.FileReader(argv[i]));
			OberonParser parser = new OberonParser(scanner);
			System.out.println(argv[i] + ":");
			try {
				parser.parser();
			} catch (Exception ex) {
				int line = scanner.get_line()+1;
			int column = scanner.get_column() +1;
			System.out.println("Error position : Line "+line+"  Column "+ column + " " + ex.getMessage() +"\n");							
			}
		}		
	}

}
