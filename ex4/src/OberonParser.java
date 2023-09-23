import exceptions.*;
import java.util.*;
import flowchart.*;

/**
 * Parser类是oberon-0的parser，负责语法分析和语义分析。
 * @author 陈泷 19335019
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
	 * Parser类的生成函数，负责初始化相关的变量。
	 * @param scanner 接受词法分析器词法分析后的结果
	 * @throws Exception异常
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
	 * 进行语法分析和语义分析，并返回分析后的结果。
	 * @return 分析的结果，即源代码是否合法
	 * @throws Exception异常
	 */
	public boolean parser()throws Exception {
		/* 进行module板块的语法分析 */
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

		/* 分析处理结果 */
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
	 * 处理 module_begin。
	 * @throws Exception
	 */
	public void module_begin() throws Exception {
		// 自动后读
		if(lookahead.sym != Token.BEGIN)
			throw new SyntacticException();
		
		proc = testModule.add("Main");
		
		statement();	
	}

	/**
	 * 处理 declaration。
	 * @throws Exception
	 */
	public void declaration() throws Exception {
		// 自动后读
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
	 * 处理 procedure declaration。
	 * @throws Exception
	 */
	public void proc_declaration() throws Exception {
		// 不自动后读
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
	 * 处理 procedure body。
	 * @return the procedure'sy name
	 * @throws Exception
	 */
	public String proc_body() throws Exception {
		// 不自动后读
		declaration();
		if (lookahead.sym == Token.BEGIN)
			proc_begin();
		
		if(lookahead.sym != Token.END)
			throw new SyntacticException();
		
		lookahead = next_token();
		return lookahead.name;
	}
	
	/**
	 * 处理 procedure begin。
	 * @throws Exception
	 */
	public void proc_begin() throws Exception {
		// 自动后读
		if(lookahead.sym != Token.BEGIN)
			throw new SyntacticException();
		
		statement();
	}
	
	/**
	 * 处理 procedure heading。
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
	 * 处理 formal_parameters，即procedure的参数。
	 * @param name
	 * @return parameters procedure的参数
	 * @throws Exception
	 */
	public Type formal_parameters(String name) throws Exception {
		// 不自动后读
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
	 * 处理fp_section，即调用的参数。
	 * @param func_p 调用的参数
	 * @throws Exception 异常
	 */
	public void fp_section(Type func_p) throws Exception{
		// 自动后读
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
	 * 处理 const_declare。
	 * @throws Exception 异常
	 */
	public void const_declare()throws Exception {
		// 不自动后读，自动前取
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
	 * 处理 var declaration。
	 * @throws Exception 异常
	 */
	public void var_declare() throws Exception {
		Type tmp;
		Vector<String> id_v = new Vector<String>();
		
		//if (lookahead.sym == Token.VAR)
		lookahead = next_token();
		if (lookahead.sym != Token.IDENTIFIER)
			return;
		
		// lookahead = next_token();
		list_id(id_v, lookahead);  // 后
		
		if(lookahead.sym != Token.COLON)
			throw new SyntacticException();
		
		tmp = type_id();  //前 不后
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
	 * 处理 type declaration。
	 * @throws Exception 异常
	 */
	public void type_declare()throws Exception {
		// 自动前取，不自动后读
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
	 * 处理 type kind。
	 * @return type_kind 输入符的类型，处理的类型种类有INTEGER,BOOLEAN,RECORD or ARRAY。
	 * @throws Exception 异常
	 */
	public Type type_id() throws Exception {
		// 自动前取，不自动后读
		lookahead = next_token();
		if (lookahead.sym == Token.INTEGER)
			return new Type(1);		//int
		
		else if (lookahead.sym == Token.BOOLEAN)
			return new Type(2);		//bool
		
		else if (lookahead.sym == Token.IDENTIFIER) {  // 判断该变量是否已经声明
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
	 * 处理 array type。
	 * @return arr 处理好的array类型
	 * @throws Exception 异常
	 */
	public Type array_type() throws Exception {
		// 不自动后读
		if(lookahead.sym != Token.ARRAY)
			throw new SyntacticException();
		
		Type arr = new Type(3);  // 声明为数组类型
		
		Symbol tmp = expression();  //前后
		if (tmp.sym != 1)
			throw new TypeMismatchedException();
		
		if(lookahead.sym != Token.OF)
			throw new SyntacticException();
		
		arr.arrayElement = new Type(type_id());  // 前 不后
		return arr;
	}

	/**
	 * 处理 record type。
	 * @return 处理好的record类型
	 * @throws Exception 异常
	 */
	public Type record_type() throws Exception {
		// 不自动后读
		if(lookahead.sym != Token.RECORD)
			throw new SyntacticException();
		
		Type tmp = new Type(4);
		tmp.recordElement = new Vector<Type>();
		
		lookahead = next_token();
		if (lookahead.sym == Token.END)  // field_list为空的情况
			return tmp;
		else 							// field_list不为空的情况
			return field_list(tmp);
	}

	/**
	 * 处理 filed list。
	 * @param filed
	 * @return record_information 处理后记录的信息
	 * @throws Exception 异常
	 */
	public Type field_list(Type filed) throws Exception {
		// 自动后读
		Vector<String> tmp = new Vector<String>();
		list_id(tmp, lookahead);	// 自动后读
		
		if (lookahead.sym == Token.END)
			return filed;
		
		else if (lookahead.sym == Token.SEMI) {
			lookahead = next_token();
			return field_list(filed);
		
		}  
		
		if (tmp.isEmpty() == false) {  // 处理field one不为空的情况
			if(lookahead.sym != Token.COLON)  
				throw new SyntacticException();
			
			Type t = type_id();   // 自动前取，不自动后读
			for (int i=0; i<tmp.size(); i++) {
				t.name = tmp.elementAt(i);
				filed.recordElement.addElement(new Type(t));
			}
		}
		
		lookahead = next_token();
		return field_list(filed);
	}

	/**
	 * 处理 identifier list。
	 * @param id_v
	 * @param id
	 * @throws Exception
	 */
	public void list_id(Vector<String> id_v, Symbol id) throws Exception {
		// 自动后读
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
	 * 处理 statement 和 statement_sequence。
	 * @throws Exception 异常
	 */
	public void statement() throws Exception {
		// 自动后读，自动前取
		Type type_of_ap;
		String name;
		String ap = new String();
				
		if (lookahead.sym == Token.END)  // empty的情况
			return;
			
		/* 非空情况 */
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
					ap = ap_list(type_of_ap);  // 不自动后读，自动前取
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
			statement();  // 自动后读，自动前取
	}

	
	/**
	 * 处理 assign statement。
	 * @param  name 赋值变量的名字
	 * @throws Exception 异常
	 */
	public void assign(String name) throws Exception {
		// 自动后读
		Symbol l, r;
		Type id_t;
		
		l = new Symbol(0, new String());
		for (int i=0; i<type_v.size(); i++) {
			if (type_v.elementAt(i).name.equals(name)) {
				id_t = type_v.elementAt(i);
				selector(id_t, l);  // 自动后读
				break;
			}
		}
		/* 未找到对应变量的情况 */
		if (l.sym == 0)
			throw new SemanticException("varible( "+ name +" ) hasn't been declared!");
		/* 找到对应变量的情况 */
		else {
			if (lookahead.sym != Token.ASSIGN)
				throw new SyntacticException();
			
			r = expression();// 自动后读，自动前取
			
			if (l.sym != r.sym)  // 检查赋值语句的类型是否匹配
				throw new TypeMismatchedException();
				
			add_stmt(new PrimitiveStatement(name + " := " + r.name));
		}	
	}

	/**
	 * 处理 actual parameter。
	 * @param ap_type 参数类型
	 * @return parameter_information 处理后参数的信息
	 * @throws Exception 异常
	 */
	public String ap_list(Type ap_type) throws Exception{
		// 不自动后读，自动前取
		Symbol expr;
		expr = expression();   // 自动后读，自动前取
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
	 * 处理 parse write/read/writeln statement。
	 * @param sym
	 * @throws Exception
	 */
	public void rw_statement(int sym) throws Exception {
		// 自动后读
		String t;
		Symbol expr = new Symbol(0);
		
		/* WRITELN 开头语句 */ 
		if (sym == Token.WRITELN) {   
			lookahead = next_token();		
			if (lookahead.sym == Token.LPAREN) { 
				
				expr = expression();  // 自动后读，自动前取	
				
				if (lookahead.sym != Token.RPAREN)
					throw new MissingRightParenthesisException();
				
				lookahead = next_token();	
				t = new String("Write("+expr.name+")");
			}
			else
				t = new String("Writeln");
		}
		
		/* READ 开头语句 */
		else if (sym == Token.READ) {  
			
			lookahead = next_token();
			if (lookahead.sym != Token.LPAREN)
				throw new MissingLeftParenthesisException();
			
			expr = expression();    // 自动后读，自动前取
			
			if (expr.sym == 0)   // 若读取的内容为空则报错
				throw new MissingOperatorException();
			
			if (lookahead.sym != Token.RPAREN)
				throw new MissingRightParenthesisException();
			
			t = new String("Read( "+expr.name+" )");
			
			lookahead = next_token();	
		}
		
		/* WRITE 开头语句 */ 
		else if (sym == Token.WRITE){
			lookahead = next_token();
			if (lookahead.sym != Token.LPAREN)
				throw new MissingLeftParenthesisException();
			
			expr = expression();  // 自动后读，自动前取	
			if (expr.sym == 0)
				throw new MissingOperatorException();
			
			if (lookahead.sym != Token.RPAREN)
				throw new MissingRightParenthesisException();
				
			t = new String("Write( "+expr.name+" )");
			lookahead = next_token();
		} 
		
		/* 其他情况视为错误 */
		else {
			throw new SyntacticException();
		}
		
		add_stmt(new PrimitiveStatement(t));            
	}

	/**
	 * 处理 while statement。
	 * @throws Exception 异常
	 */
	public void while_statement() throws Exception {
		// 自动后读
		Symbol expr;
		
		if (lookahead.sym != Token.WHILE)
				throw new SyntacticException();
		
		expr = expression();  // 自动后读，自动前取
		
		if (lookahead.sym != Token.DO)
				throw new SyntacticException();
		
		whileStmt = new WhileStatement(expr.name);  // 增加流程图中的对应方框
		add_stmt(whileStmt);
		
		sign.push(whileStmt.getLoopBody());	
		statement();	// 自动后读，自动前取	
		sign.pop();	
		
		if (lookahead.sym != Token.END)
				throw new SyntacticException();
		
		lookahead = next_token();
	}

	
	/**
	 * 处理 if statement。
	 * @throws Exception 异常
	 */
	public void if_statement() throws Exception {
		// 自动后读
		Symbol expr;
		
		if (lookahead.sym != Token.IF)
			throw new MissingLeftParenthesisException();
		
		expr = expression();  // 自动后读，自动前取
		
		if (lookahead.sym != Token.THEN)
			throw new MissingLeftParenthesisException();
		
		/* 添加到流程图 */
		ifStmt = new IfStatement(expr.name);
		add_stmt(ifStmt);

		sign.push(ifStmt.getFalseBody());
		sign.push(ifStmt.getTrueBody());
		
		statement();  // 自动后读，自动前取
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
	 * 处理 elsif statement。
	 * @throws Exception 异常
	 */
	public void elsif_statement() throws Exception {
		// 自动后读
		if (lookahead.sym != Token.ELSIF)
			throw new MissingLeftParenthesisException();
		
		Symbol expr;
		expr = expression();	// 自动后读，自动前取
		
		IfStatement elsif = new IfStatement(expr.name);
		add_stmt(elsif);	
		sign.pop();		
		
		sign.push(elsif.getFalseBody());
		sign.push(elsif.getTrueBody());	
		
		if (lookahead.sym != Token.THEN)
				throw new MissingLeftParenthesisException();
		
		statement();   // 自动后读，自动前取
		sign.pop();		
		// System.out.println(lookahead.name+"123");     //???
		if (lookahead.sym == Token.ELSIF)
			elsif_statement();
		else return;
	}
	
	
	/**
	 * 处理 else statement。
	 * @throws Exception 异常
	 */
	public void else_statement() throws Exception {	
		if (lookahead.sym != Token.ELSE)
			throw new MissingLeftParenthesisException();
		
		statement();   // 自动后读，自动前取
		
		if (lookahead.sym != Token.END)
			throw new MissingLeftParenthesisException();
	}

	/**
	 * 处理 expression。
	 * @return Symbol Symbol中记录有表达式的类型和内容
	 * @throws Exception 异常
	 */
	public Symbol expression() throws Exception {
		// 自动后读，自动前取
		Symbol expr = new Symbol(2);	// 默认为 "+"  ？？？
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
	 * 处理 simple expression，涉及的运算有"+", "-", "OR"。
	 * @return Symbol Symbol中记录有simple expression的类型和内容
	 * @throws Exception 异常 
	 */
	public Symbol simple_expression() throws Exception {
		// 自动后读，自动前取
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
	 * 处理 term，涉及的运算有"\*", "DIV", "MOD", "&"。
	 * @return Symbol Symbol中记录有term的类型和内容
	 * @throws Exception 异常
	 */
	public Symbol term() throws Exception {
		// 自动后读，自动前取
		Symbol expr = new Symbol(2);
		expr = factor();
		
		int t = lookahead.sym;
		if (t==Token.TIMES || t==Token.DIVIDE || t==Token.MOD || t==Token.AND) {
			if (expr.sym != 2 && t==Token.AND)  // 逻辑运算符和非逻辑操作数
				throw new TypeMismatchedException();
				
			if (expr.sym != 1 && (t==Token.TIMES || t==Token.DIVIDE || t==Token.MOD))  // 算术运算符和非算术操作数
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
		else if(t==Token.IDENTIFIER || t==Token.NUMBER || t==Token.BOOLEAN)  // 确实运算符
			throw new MissingOperatorException();
		
		return expr;
	}
	
	
	/**
	 * 处理 factor，即表达式中的操作数。
	 * @return Symbol 表达式中的操作数
	 * @throws Exception 异常
	 */
	public Symbol factor() throws Exception {    
		// 自动前取 自动后读
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
				sy = new Symbol(1, lookahead.name);  // 1 代表算术表达式因子
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
	 * 处理 selector，即{"." identifier | "[" expression "]"}。    
	 * @param t 源变量sy的元素（record 或者 array类型的元素）
	 * @param sy 源变量（record 或者 array类型）
	 * @throws Exception
	 */
	public void selector(Type t, Symbol sy) throws Exception {
		// 自动后读
		Symbol expr;
		if (t.name != null)  // 不是record 或者 array类型的情况
			sy.name = sy.name + t.name;
		
		if (lookahead.sym == Token.IDENTIFIER) 
			lookahead = next_token();
		
		if (lookahead.sym == Token.PERIOD) {  // "." identifier 
			int match = 0;
			
			lookahead = next_token();
			for (int i=0; i<t.recordElement.size(); i++) {  // 查看当前变量是否声明过
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
		else if (lookahead.sym == Token.LBRACKET) {  // 处理array类型: "[" expression "]"
			if (t.id != 3)  // 3 代表array类型
				throw new SemanticException("THE VARIBLE IS NOT AN ARRAY");
			
			expr = expression();
			
			if (expr.sym != 1)  // 1 代表算术表达式因子
				throw new TypeMismatchedException();
			
			sy.name = sy.name + ("[" + expr.name + "]");
			
			t = t.arrayElement;  // t转变为数组的元素
			selector(t, sy);	 // 继续处理
			return ;	
		}
		else
			sy.sym = t.id;	 // 处理到最终结果
		
		if (lookahead.sym == Token.RBRACKET)
			lookahead = next_token();
	}
	

	/**
	 * 负责将 statement 添加到流程图中。
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
	 * 获取下一个token。
	 * @return Symbol Scanner词法分析后获得的Token
	 * @throws Exception 异常
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






