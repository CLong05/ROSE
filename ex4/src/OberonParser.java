import exceptions.*;
import java.util.*;
import flowchart.*;

/**
 * Parser����oberon-0��parser�������﷨���������������
 * @author ���� 19335019
 */
public class OberonParser 
{
	/**
	 * The private Stack of statement.
	 */
	public Stack<StatementSequence> sign;
	
	public Vector<Type>	caller;
	public Vector<Type>	callee;
	public Vector<Type> type_v;
	public OberonScanner scanner;
	public Symbol lookahead;
	int LeftParen;
	int RightParen;
	
	/**
	 * The instance of a parser
	 */
	flowchart.Module testModule;
	
	/**
	 * The instance of a procedure
	 */
	Procedure proc;

	/**
	 * The instance of a WHILE statement
	 */
	WhileStatement whileStmt;

	/**
	 * The instance of a IF statement
	 */
	IfStatement ifStmt;  

	/**
	 * Parser������ɺ����������ʼ����صı�����
	 * @param scanner ���ܴʷ��������ʷ�������Ľ��
	 * @throws Exception�쳣
	 * 
	 */
	public OberonParser(OberonScanner scanner) throws Exception {
		sign = new Stack<StatementSequence>(); 
		
		LeftParen = 0;
		RightParen = 0;
		callee = new Vector<Type>();
		type_v = new Vector<Type>();
		caller = new Vector<Type>();
		
		testModule = null;
		proc = null;	
		whileStmt = null;
		ifStmt = null;
		
		this.scanner = scanner;

	}
	
	/**
	 * �����﷨��������������������ط�����Ľ����
	 * @return �����Ľ������Դ�����Ƿ�Ϸ�
	 * @throws Exception�쳣
	 */
	public boolean parser()throws Exception {
		/* ����module�����﷨���� */
		String name1, name2;
		
		lookahead = next_token();
		if(lookahead.sym != Token.MODULE)
			throw new SyntacticException();
		
		lookahead = next_token();
		name1 = lookahead.name;
		testModule = new flowchart.Module(name1);
		System.out.println("Parsing " + name1 + " ...");
				
		lookahead = next_token();
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		lookahead = next_token();
		declaration();
		
		// lookahead = next_token();
		if (lookahead.sym == Token.BEGIN){
			module_begin();
			// lookahead = next_token();
		}		
		
		if(lookahead.sym != Token.END)
			throw new SyntacticException();
		
		lookahead = next_token();
		name2 = lookahead.name;
		if (name1.equals(name2) == false)
			throw new ModuleNameMismatchedException();
		
		lookahead = next_token();
		if(lookahead.sym != Token.PERIOD)
			throw new SyntacticException();
		
		if (LeftParen < RightParen)
			throw new MissingLeftParenthesisException();
		else if (LeftParen > RightParen)
			throw new MissingRightParenthesisException();

		/* ���������� */
		int name_match = 0;
		for (int i=0; i<callee.size(); i++) {
			for (int j=0; j<caller.size(); j++) {
				if (callee.elementAt(i).name.equals(caller.elementAt(j).name)) {
					if (callee.elementAt(i).id != caller.elementAt(j).id)
						throw new MissingOperandException();

					for (int k=0; k<callee.elementAt(i).id; k++)
						if (callee.elementAt(i).recordElement.elementAt(k).id !=
							caller.elementAt(j).recordElement.elementAt(k).id)
							throw new TypeMismatchedException();
					name_match = 1;
				}
			}
			if (name_match == 0)
				throw new SemanticException(" "+callee.elementAt(i).name+" is not declared");
			name_match = 0;
		}
		System.out.println("-- Parse Finished. No error found. ");
		testModule.show();
		return true;
	}

	
	/**
	 * ���� module_begin��
	 * @throws Exception
	 */
	public void module_begin() throws Exception {
		// �Զ����
		if(lookahead.sym != Token.BEGIN)
			throw new SyntacticException();
		
		proc = testModule.add("Main");
		
		statement();	
	}

	/**
	 * ���� declaration��
	 * @throws Exception
	 */
	public void declaration() throws Exception {
		// �Զ����
		if(lookahead.sym != Token.CONST && lookahead.sym != Token.TYPE && lookahead.sym != Token.VAR &&
		   lookahead.sym != Token.PROCEDURE && lookahead.sym != Token.BEGIN && lookahead.sym != Token.END)
		   throw new SyntacticException();
		
		if (lookahead.sym == Token.CONST)
			const_declare();
		else if (lookahead.sym == Token.TYPE)
			type_declare();
		else if (lookahead.sym == Token.VAR)
			var_declare();
		else if (lookahead.sym == Token.PROCEDURE)
			proc_declaration();
		if (lookahead.sym == Token.BEGIN || lookahead.sym == Token.END)
			return;
		else declaration();
	}
	
	/**
	 * ���� procedure declaration��
	 * @throws Exception
	 */
	public void proc_declaration() throws Exception {
		// ���Զ����
		String name1, name2;
		name1 = proc_head();
		
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		lookahead = next_token();
		name2 = proc_body();
		
		if (name1.equals(name2) == false){
			// System.out.println(name1+"  "+name2);
			throw new ProcedureNameMismatchedException();}
		
		lookahead = next_token();
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		lookahead = next_token();
	}
	
	/**
	 * ���� procedure body��
	 * @return the procedure'sy name
	 * @throws Exception
	 */
	public String proc_body() throws Exception {
		// ���Զ����
		declaration();
		if (lookahead.sym == Token.BEGIN)
			proc_begin();
		
		if(lookahead.sym != Token.END)
			throw new SyntacticException();
		
		lookahead = next_token();
		return lookahead.name;
	}
	
	/**
	 * ���� procedure begin��
	 * @throws Exception
	 */
	public void proc_begin() throws Exception {
		// �Զ����
		if(lookahead.sym != Token.BEGIN)
			throw new SyntacticException();
		
		statement();
	}
	
	/**
	 * ���� procedure heading��
	 * @return the proceduer'sy name
	 * @throws Exception
	 */
	public String proc_head() throws Exception {
		Type func_p;
		String name;
		if(lookahead.sym != Token.PROCEDURE)
			throw new SyntacticException();
		
		lookahead = next_token();
		name = new String (lookahead.name);
		
		lookahead = next_token();
		if (lookahead.sym == Token.LPAREN) {
			func_p = formal_parameters(name);
			lookahead = next_token();
		}
		else func_p = new Type();
		func_p.name = name;
		caller.addElement(func_p);
		
		proc = testModule.add(name);
		
		return name;
	}
	
	/**
	 * ���� formal_parameters����procedure�Ĳ�����
	 * @param name
	 * @return parameters procedure�Ĳ���
	 * @throws Exception
	 */
	public Type formal_parameters(String name) throws Exception {
		// ���Զ����
		if(lookahead.sym != Token.LPAREN)
			throw new SyntacticException();
		
		Type func_p = new Type(0, name, new Vector<Type>());
		
		lookahead = next_token();
		if (lookahead.sym == Token.RPAREN)
			return func_p;
		
		fp_section(func_p);

		if(lookahead.sym != Token.RPAREN)  
			throw new SyntacticException();
	
		return func_p;
	}
	
	
	/**
	 * ����fp_section�������õĲ�����
	 * @param func_p ���õĲ���
	 * @throws Exception �쳣
	 */
	public void fp_section(Type func_p) throws Exception{
		// �Զ����
		Type fp_t;
		Vector<String> fp_name = new Vector<String>();
		
		if (lookahead.sym == Token.VAR)
			lookahead = next_token();
		
		list_id(fp_name, lookahead);
		
		if(lookahead.sym != Token.COLON)
			throw new SyntacticException();
		
		fp_t =  type_id();
		
		for (int i=0; i<fp_name.size(); i++)      // ?????????
		{
			//the para just get the type, not the para name
			func_p.recordElement.addElement(new Type(fp_t));     
			
			fp_t.name = fp_name.elementAt(i);
			type_v.addElement(new Type(fp_t));
		}
		func_p.id += fp_name.size();
		
		lookahead = next_token();
		if (lookahead.sym == Token.SEMI)
		{
			lookahead = next_token();
			fp_section(func_p);
		}
	}
	
	/**
	 * ���� const_declare��
	 * @throws Exception �쳣
	 */
	public void const_declare()throws Exception {
		// ���Զ�������Զ�ǰȡ
		Type tmp = new Type();
		Symbol t;
		
		//if(lookahead.sym != Token.CONST)
		//	throw new SyntacticException();
		
		lookahead = next_token();
		if (lookahead.sym != Token.IDENTIFIER)
			return ;
		
		tmp.name = lookahead.name;
		
		lookahead = next_token();
		if(lookahead.sym != Token.EQ)
			throw new SyntacticException();
		
		t = expression();
		tmp.id = t.sym;		
		type_v.addElement(tmp);
		
		// lookahead = next_token();
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		const_declare();
	}
	
	/**
	 * ���� var declaration��
	 * @throws Exception �쳣
	 */
	public void var_declare() throws Exception {
		Type tmp;
		Vector<String> id_v = new Vector<String>();
		
		//if (lookahead.sym == Token.VAR)
		lookahead = next_token();
		if (lookahead.sym != Token.IDENTIFIER)
			return;
		
		// lookahead = next_token();
		list_id(id_v, lookahead);  // ��
		
		if(lookahead.sym != Token.COLON)
			throw new SyntacticException();
		
		tmp = type_id();  //ǰ ����
		for (int i=0; i<id_v.size(); i++) {
			tmp.name = id_v.elementAt(i);
			type_v.addElement(new Type(tmp));
		}
		
		lookahead = next_token();
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		var_declare();
	}
	
	/**
	 * ���� type declaration��
	 * @throws Exception �쳣
	 */
	public void type_declare()throws Exception {
		// �Զ�ǰȡ�����Զ����
		Type tmp;
		
		lookahead = next_token();
		if (lookahead.sym != Token.IDENTIFIER)
			return ;
		String name = new String(lookahead.name);
		
		lookahead = next_token();
		if(lookahead.sym != Token.EQ)
			throw new SyntacticException();
		
		tmp = type_id();
		tmp.name = name;
		type_v.addElement(tmp);
		
		lookahead = next_token();
		if(lookahead.sym != Token.SEMI)
			throw new SyntacticException();
		
		type_declare();
	}
	
	/**
	 * ���� type kind��
	 * @return type_kind ����������ͣ����������������INTEGER,BOOLEAN,RECORD or ARRAY��
	 * @throws Exception �쳣
	 */
	public Type type_id() throws Exception {
		// �Զ�ǰȡ�����Զ����
		lookahead = next_token();
		if (lookahead.sym == Token.INTEGER)
			return new Type(1);		//int
		
		else if (lookahead.sym == Token.BOOLEAN)
			return new Type(2);		//bool
		
		else if (lookahead.sym == Token.IDENTIFIER) {  // �жϸñ����Ƿ��Ѿ�����
			for (int i=0; i<type_v.size(); i++) {
				if (lookahead.name.equals(type_v.elementAt(i).name))
					return type_v.elementAt(i);
			}
			throw new SemanticException("Error: varible( " + lookahead.name + " ) hasn't been declared!");
		}
		else if (lookahead.sym == Token.ARRAY)
			return array_type();
		
		else if (lookahead.sym == Token.RECORD)
			return record_type();
		
		else
			throw new SyntacticException();
	}
	
	/**
	 * ���� array type��
	 * @return arr ����õ�array����
	 * @throws Exception �쳣
	 */
	public Type array_type() throws Exception {
		// ���Զ����
		if(lookahead.sym != Token.ARRAY)
			throw new SyntacticException();
		
		Type arr = new Type(3);  // ����Ϊ��������
		
		Symbol tmp = expression();  //ǰ��
		if (tmp.sym != 1)
			throw new TypeMismatchedException();
		
		if(lookahead.sym != Token.OF)
			throw new SyntacticException();
		
		arr.arrayElement = new Type(type_id());  // ǰ ����
		return arr;
	}

	/**
	 * ���� record type��
	 * @return ����õ�record����
	 * @throws Exception �쳣
	 */
	public Type record_type() throws Exception {
		// ���Զ����
		if(lookahead.sym != Token.RECORD)
			throw new SyntacticException();
		
		Type tmp = new Type(4);
		tmp.recordElement = new Vector<Type>();
		
		lookahead = next_token();
		if (lookahead.sym == Token.END)  // field_listΪ�յ����
			return tmp;
		else 							// field_list��Ϊ�յ����
			return field_list(tmp);
	}

	/**
	 * ���� filed list��
	 * @param filed
	 * @return record_information ������¼����Ϣ
	 * @throws Exception �쳣
	 */
	public Type field_list(Type filed) throws Exception {
		// �Զ����
		Vector<String> tmp = new Vector<String>();
		list_id(tmp, lookahead);	// �Զ����
		
		if (lookahead.sym == Token.END)
			return filed;
		
		else if (lookahead.sym == Token.SEMI) {
			lookahead = next_token();
			return field_list(filed);
		
		}  
		
		if (tmp.isEmpty() == false) {  // ����field one��Ϊ�յ����
			if(lookahead.sym != Token.COLON)  
				throw new SyntacticException();
			
			Type t = type_id();   // �Զ�ǰȡ�����Զ����
			for (int i=0; i<tmp.size(); i++) {
				t.name = tmp.elementAt(i);
				filed.recordElement.addElement(new Type(t));
			}
		}
		
		lookahead = next_token();
		return field_list(filed);
	}

	/**
	 * ���� identifier list��
	 * @param id_v
	 * @param id
	 * @throws Exception
	 */
	public void list_id(Vector<String> id_v, Symbol id) throws Exception {
		// �Զ����
		if (id.sym != Token.IDENTIFIER)
			return;
		
		id_v.addElement(id.name);
		
		lookahead = next_token();
		if (lookahead.sym != Token.COMMA)
			return ;
		else {
			lookahead = next_token();
			list_id(id_v, lookahead);
		}
	}
	
	/**
	 * ���� statement �� statement_sequence��
	 * @throws Exception �쳣
	 */
	public void statement() throws Exception {
		// �Զ�������Զ�ǰȡ
		Type type_of_ap;
		String name;
		String ap = new String();
				
		if (lookahead.sym == Token.END)  // empty�����
			return;
			
		/* �ǿ���� */
		lookahead = next_token();
		if (lookahead.sym == Token.WHILE)
			while_statement();
		
		else if (lookahead.sym == Token.IF)
			if_statement();
		
		else if (lookahead.sym == Token.READ || 
				lookahead.sym == Token.WRITE || lookahead.sym == Token.WRITELN)
			rw_statement(lookahead.sym);
			
		else if (lookahead.sym == Token.IDENTIFIER) {
			name = new String (lookahead.name);
			
			lookahead = next_token();
			if (lookahead.sym == Token.LPAREN || lookahead.sym == Token.SEMI) {  // procedure call
				type_of_ap = new Type(0, name, new Vector<Type>());
				
				if (lookahead.sym == Token.LPAREN) {  
					ap = ap_list(type_of_ap);  // ���Զ�������Զ�ǰȡ
					lookahead = next_token();
				}
				callee.addElement(type_of_ap);
				
				String t = new String(name+"( "+ap+" )");
				add_stmt(new PrimitiveStatement(t));
			}
			else assign(name);  // assignment
		}
		
		if (lookahead.sym == Token.ELSE || lookahead.sym == Token.ELSIF)
			return ;
		
		
		if(lookahead.sym == Token.SEMI)
			statement();  // �Զ�������Զ�ǰȡ
	}

	
	/**
	 * ���� assign statement��
	 * @param  name ��ֵ����������
	 * @throws Exception �쳣
	 */
	public void assign(String name) throws Exception {
		// �Զ����
		Symbol l, r;
		Type id_t;
		
		l = new Symbol(0, new String());
		for (int i=0; i<type_v.size(); i++) {
			if (type_v.elementAt(i).name.equals(name)) {
				id_t = type_v.elementAt(i);
				selector(id_t, l);  // �Զ����
				break;
			}
		}
		/* δ�ҵ���Ӧ��������� */
		if (l.sym == 0)
			throw new SemanticException("varible( "+ name +" ) hasn't been declared!");
		/* �ҵ���Ӧ��������� */
		else {
			if (lookahead.sym != Token.ASSIGN)
				throw new SyntacticException();
			
			r = expression();// �Զ�������Զ�ǰȡ
			
			if (l.sym != r.sym)  // ��鸳ֵ���������Ƿ�ƥ��
				throw new TypeMismatchedException();
				
			add_stmt(new PrimitiveStatement(name + " := " + r.name));
		}	
	}

	/**
	 * ���� actual parameter��
	 * @param ap_type ��������
	 * @return parameter_information ������������Ϣ
	 * @throws Exception �쳣
	 */
	public String ap_list(Type ap_type) throws Exception{
		// ���Զ�������Զ�ǰȡ
		Symbol expr;
		expr = expression();   // �Զ�������Զ�ǰȡ
		ap_type.recordElement.addElement(new Type(expr.sym));
		ap_type.id++;
		
		if (lookahead.sym == Token.RPAREN)
			return expr.name;
		
		else if (lookahead.sym == Token.COMMA) {
			expr.name += ", ";
			expr.name += ap_list(ap_type);
		}
		else throw new SyntacticException();
		
		return expr.name;
	}
	
	
	/**
	 * ���� parse write/read/writeln statement��
	 * @param sym
	 * @throws Exception
	 */
	public void rw_statement(int sym) throws Exception {
		// �Զ����
		String t;
		Symbol expr = new Symbol(0);
		
		/* WRITELN ��ͷ��� */ 
		if (sym == Token.WRITELN) {   
			lookahead = next_token();		
			if (lookahead.sym == Token.LPAREN) { 
				
				expr = expression();  // �Զ�������Զ�ǰȡ	
				
				if (lookahead.sym != Token.RPAREN)
					throw new MissingRightParenthesisException();
				
				lookahead = next_token();	
				t = new String("Write("+expr.name+")");
			}
			else
				t = new String("Writeln");
		}
		
		/* READ ��ͷ��� */
		else if (sym == Token.READ) {  
			
			lookahead = next_token();
			if (lookahead.sym != Token.LPAREN)
				throw new MissingLeftParenthesisException();
			
			expr = expression();    // �Զ�������Զ�ǰȡ
			
			if (expr.sym == 0)   // ����ȡ������Ϊ���򱨴�
				throw new MissingOperatorException();
			
			if (lookahead.sym != Token.RPAREN)
				throw new MissingRightParenthesisException();
			
			t = new String("Read( "+expr.name+" )");
			
			lookahead = next_token();	
		}
		
		/* WRITE ��ͷ��� */ 
		else if (sym == Token.WRITE){
			lookahead = next_token();
			if (lookahead.sym != Token.LPAREN)
				throw new MissingLeftParenthesisException();
			
			expr = expression();  // �Զ�������Զ�ǰȡ	
			if (expr.sym == 0)
				throw new MissingOperatorException();
			
			if (lookahead.sym != Token.RPAREN)
				throw new MissingRightParenthesisException();
				
			t = new String("Write( "+expr.name+" )");
			lookahead = next_token();
		} 
		
		/* ���������Ϊ���� */
		else {
			throw new SyntacticException();
		}
		
		add_stmt(new PrimitiveStatement(t));            
	}

	/**
	 * ���� while statement��
	 * @throws Exception �쳣
	 */
	public void while_statement() throws Exception {
		// �Զ����
		Symbol expr;
		
		if (lookahead.sym != Token.WHILE)
				throw new SyntacticException();
		
		expr = expression();  // �Զ�������Զ�ǰȡ
		
		if (lookahead.sym != Token.DO)
				throw new SyntacticException();
		
		whileStmt = new WhileStatement(expr.name);  // ��������ͼ�еĶ�Ӧ����
		add_stmt(whileStmt);
		
		sign.push(whileStmt.getLoopBody());	
		statement();	// �Զ�������Զ�ǰȡ	
		sign.pop();	
		
		if (lookahead.sym != Token.END)
				throw new SyntacticException();
		
		lookahead = next_token();
	}

	
	/**
	 * ���� if statement��
	 * @throws Exception �쳣
	 */
	public void if_statement() throws Exception {
		// �Զ����
		Symbol expr;
		
		if (lookahead.sym != Token.IF)
			throw new MissingLeftParenthesisException();
		
		expr = expression();  // �Զ�������Զ�ǰȡ
		
		if (lookahead.sym != Token.THEN)
			throw new MissingLeftParenthesisException();
		
		/* ��ӵ�����ͼ */
		ifStmt = new IfStatement(expr.name);
		add_stmt(ifStmt);

		sign.push(ifStmt.getFalseBody());
		sign.push(ifStmt.getTrueBody());
		
		statement();  // �Զ�������Զ�ǰȡ
		sign.pop();	
		
		if (lookahead.sym == Token.ELSIF)
			elsif_statement();
		else if (lookahead.sym == Token.ELSE)
			else_statement();
		
		if (lookahead.sym == Token.END)	
			lookahead = next_token();

		sign.pop();		
	}
	
	
	/**
	 * ���� elsif statement��
	 * @throws Exception �쳣
	 */
	public void elsif_statement() throws Exception {
		// �Զ����
		if (lookahead.sym != Token.ELSIF)
			throw new MissingLeftParenthesisException();
		
		Symbol expr;
		expr = expression();	// �Զ�������Զ�ǰȡ
		
		IfStatement elsif = new IfStatement(expr.name);
		add_stmt(elsif);	
		sign.pop();		
		
		sign.push(elsif.getFalseBody());
		sign.push(elsif.getTrueBody());	
		
		if (lookahead.sym != Token.THEN)
				throw new MissingLeftParenthesisException();
		
		statement();   // �Զ�������Զ�ǰȡ
		sign.pop();		
		// System.out.println(lookahead.name+"123");     //???
		if (lookahead.sym == Token.ELSIF)
			elsif_statement();
		else return;
	}
	
	
	/**
	 * ���� else statement��
	 * @throws Exception �쳣
	 */
	public void else_statement() throws Exception {	
		if (lookahead.sym != Token.ELSE)
			throw new MissingLeftParenthesisException();
		
		statement();   // �Զ�������Զ�ǰȡ
		
		if (lookahead.sym != Token.END)
			throw new MissingLeftParenthesisException();
	}

	/**
	 * ���� expression��
	 * @return Symbol Symbol�м�¼�б��ʽ�����ͺ�����
	 * @throws Exception �쳣
	 */
	public Symbol expression() throws Exception {
		// �Զ�������Զ�ǰȡ
		Symbol expr = new Symbol(2);	// Ĭ��Ϊ "+"  ������
		expr = simple_expression();
		
		int t = lookahead.sym;
		if (t==Token.EQ || t==Token.NEQ || t==Token.LT ||
				t==Token.LE || t==Token.GT || t==Token.GE ) {
			if (t == Token.EQ)	expr.name += " = ";
			if (t == Token.NEQ) expr.name += " # ";
			if (t == Token.LT)	expr.name += " &lt ";
			if (t == Token.LE)	expr.name += " &lt = ";
			if (t == Token.GT)	expr.name += " &gt ";
			if (t == Token.GE)	expr.name += " &gt = ";
			expr.name = expr.name.toString() + simple_expression().name.toString();
			expr.sym = 2;
		}
		return expr;
	}

	
	/**
	 * ���� simple expression���漰��������"+", "-", "OR"��
	 * @return Symbol Symbol�м�¼��simple expression�����ͺ�����
	 * @throws Exception �쳣 
	 */
	public Symbol simple_expression() throws Exception {
		// �Զ�������Զ�ǰȡ
		Symbol expr = new Symbol(2);     
		expr = term();
		int t = lookahead.sym;
		if (t==Token.PLUS || t==Token.MINUS || t==Token.OR) {
			if (t == Token.PLUS)	expr.name += "+";
			if (t == Token.MINUS)	expr.name += "-";
			if (t == Token.OR) {
				expr.sym = 2;
				expr.name += "OR";
			}
			expr.name += simple_expression().name;
		}
		return expr;
	}

	
	/**
	 * ���� term���漰��������"\*", "DIV", "MOD", "&"��
	 * @return Symbol Symbol�м�¼��term�����ͺ�����
	 * @throws Exception �쳣
	 */
	public Symbol term() throws Exception {
		// �Զ�������Զ�ǰȡ
		Symbol expr = new Symbol(2);
		expr = factor();
		
		int t = lookahead.sym;
		if (t==Token.TIMES || t==Token.DIVIDE || t==Token.MOD || t==Token.AND) {
			if (expr.sym != 2 && t==Token.AND)  // �߼�������ͷ��߼�������
				throw new TypeMismatchedException();
				
			if (expr.sym != 1 && (t==Token.TIMES || t==Token.DIVIDE || t==Token.MOD))  // ����������ͷ�����������
				throw new TypeMismatchedException();
				
			if (t == Token.TIMES)	expr.name += " * ";
			if (t == Token.DIVIDE)	expr.name += " DIV ";
			if (t == Token.MOD)		expr.name += " MOD ";
			
			if (t == Token.AND) {
				expr.name += " & ";
				expr.sym = 2;
			}
			expr.name += term().name;
		}
		else if(t==Token.IDENTIFIER || t==Token.NUMBER || t==Token.BOOLEAN)  // ȷʵ�����
			throw new MissingOperatorException();
		
		return expr;
	}
	
	
	/**
	 * ���� factor�������ʽ�еĲ�������
	 * @return Symbol ���ʽ�еĲ�����
	 * @throws Exception �쳣
	 */
	public Symbol factor() throws Exception {    
		// �Զ�ǰȡ �Զ����
		Symbol sy = new Symbol(0, new String());  
		
		lookahead = next_token();
		int sym = lookahead.sym;
		int neg = 0;
		
		if (sym==Token.PLUS || sym==Token.MINUS){  //term_head
			if(sym == Token.MINUS) {
				neg=1;
				sy = new Symbol(0, "-");
			}
			lookahead = next_token();
			sym = lookahead.sym;
		}
		
		if (sym == Token.NUMBER) {
			if(neg == 0)
				sy = new Symbol(1, lookahead.name);  // 1 �����������ʽ����
			else sy = new Symbol(1, "-" + lookahead.name);
			lookahead = next_token();
		}
		
		else if (sym == Token.NOT) {
			if(neg == 1) throw new TypeMismatchedException(); 
			sy = factor();
			lookahead = next_token();
		}
		
		else if (sym == Token.IDENTIFIER) {
			int i;
			for (i=0; i<type_v.size(); i++) {
				if (lookahead.name.equals(type_v.elementAt(i).name)) {
					selector(type_v.elementAt(i), sy);
					break;
				}
			}
			if (i >= type_v.size())
				throw new SemanticException("varible( "+ lookahead.name +" ) hasn't been declared!");
			if (sy.sym == 0) {
				sy = new Symbol(type_v.elementAt(i).id, type_v.elementAt(i).name);
				lookahead = next_token();
			}
			
		}
		
		else if (sym == Token.LPAREN) {
			sy = expression();	
			
			if (lookahead.sym != Token.RPAREN)
				throw new MissingRightParenthesisException();
			
			if(neg == 0)
				sy.name = "( " + sy.name + " )";
		    else sy.name = "-( " + sy.name + " )";
			lookahead = next_token();	
		}
		
		else if (sym==Token.PLUS || sym==Token.MINUS || sym==Token.TIMES || sym==Token.DIVIDE ||
				sym==Token.AND || sym==Token.OR || sym==Token.NOT || sym==Token.LT || 
				sym==Token.LE ||sym==Token.GT || sym==Token.GE || sym==Token.NEQ || sym==Token.EQ)
			throw new MissingOperandException();
		
		if (sy.sym == 0) throw new MissingOperandException();
		
		return sy;
	}
	
	
	/**
	 * ���� selector����{"." identifier | "[" expression "]"}��    
	 * @param t Դ����sy��Ԫ�أ�record ���� array���͵�Ԫ�أ�
	 * @param sy Դ������record ���� array���ͣ�
	 * @throws Exception
	 */
	public void selector(Type t, Symbol sy) throws Exception {
		// �Զ����
		Symbol expr;
		if (t.name != null)  // ����record ���� array���͵����
			sy.name = sy.name + t.name;
		
		if (lookahead.sym == Token.IDENTIFIER) 
			lookahead = next_token();
		
		if (lookahead.sym == Token.PERIOD) {  // "." identifier 
			int match = 0;
			
			lookahead = next_token();
			for (int i=0; i<t.recordElement.size(); i++) {  // �鿴��ǰ�����Ƿ�������
				t = t.recordElement.elementAt(i);
				if (t.name.equals(lookahead.name)) {
					match = 1;
					sy.name = sy.name + ("."+lookahead.name);
					selector(t, sy);
					//lookahead = next_token();
				}
			}
			if (match == 0)
				throw new SemanticException("varible( "+ lookahead.name +" ) in RECORD"+ t.name + "without declaration!");
		}
		else if (lookahead.sym == Token.LBRACKET) {  // ����array����: "[" expression "]"
			if (t.id != 3)  // 3 ����array����
				throw new SemanticException("THE VARIBLE IS NOT AN ARRAY");
			
			expr = expression();
			
			if (expr.sym != 1)  // 1 �����������ʽ����
				throw new TypeMismatchedException();
			
			sy.name = sy.name + ("[" + expr.name + "]");
			
			t = t.arrayElement;  // tת��Ϊ�����Ԫ��
			selector(t, sy);	 // ��������
			return ;	
		}
		else
			sy.sym = t.id;	 // �������ս��
		
		if (lookahead.sym == Token.RBRACKET)
			lookahead = next_token();
	}
	

	/**
	 * ���� statement ��ӵ�����ͼ�С�
	 * @param t: AbstractStatement
	 */
	public void add_stmt(AbstractStatement t) {
		StatementSequence stmt;
		
		if (sign.isEmpty())
			proc.add(t);
		else {
			stmt = sign.peek();
			stmt.add(t);
		}	
	}

	
	/**
	 * ��ȡ��һ��token��
	 * @return Symbol Scanner�ʷ��������õ�Token
	 * @throws Exception �쳣
	 */
	public Symbol next_token() throws Exception {
		Symbol tem;
		try {
			tem = scanner.next_token();
			if (tem.sym == Token.LPAREN)
				LeftParen++;
			if (tem.sym == Token.RPAREN)
				RightParen++;
		}
		catch(Exception ex) {
			throw ex;
		}
		return tem;
	}
	
}






