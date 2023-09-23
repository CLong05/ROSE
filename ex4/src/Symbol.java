/**
 * Symbol类负责生成词法分析的token，并传递给OberonParser。
 * @author 陈泷 19335019
 */
public class Symbol {	
	/**
	 * Token内的编号信息
	 */
	public int sym;
	
	/**
	 * Token内的类型名称信息
	 */
	public String name;
	
	public Symbol(int sym, String name) {
		this.sym = sym;
		this.name = name;
	}
	
	public Symbol(int sym) {
		this.sym = sym;
		this.name = null;
	}
	
	public Symbol() {
		this.sym = 0;
		this.name = null;
	}
	
}
