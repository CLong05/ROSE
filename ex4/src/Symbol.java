/**
 * Symbol�ฺ�����ɴʷ�������token�������ݸ�OberonParser��
 * @author ���� 19335019
 */
public class Symbol {	
	/**
	 * Token�ڵı����Ϣ
	 */
	public int sym;
	
	/**
	 * Token�ڵ�����������Ϣ
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
