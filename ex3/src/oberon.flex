import java.io.*;
import exceptions.*;
import java_cup.runtime.*;

%%
%class OberonScanner

%public
%eofval{
	return symbol(Symbol.EOF);
%eofval}
%cupsym Symbol
%cup
%ignorecase
%line
%column

%yylexthrow LexicalException
%type java_cup.runtime.Symbol
%{
  int get_line(){	return yyline;}
  int get_column(){	return yycolumn;}

StringBuffer string = new StringBuffer();
  private java_cup.runtime.Symbol symbol(int type) {
    return new java_cup.runtime.Symbol(type, yyline, yycolumn);
  }
  private java_cup.runtime.Symbol symbol(int type, Object value) {
    return new java_cup.runtime.Symbol(type, yyline, yycolumn, value);
  }
%}


Number = 0[0-7]*  |  [1-9][0-9]*

Identifier = [:jletter:][:jletterdigit:]*

WhiteSpace = " " | \r | \n | \r\n | [ \t\f]

Comment = "(*" ~ "*)"

IllegalComment = "(*" ([^\*] | "*"+[^\)])* | ([^\(]|"("+[^\*])* "*)"

IllegalOctal = 0[0-7]*[9|8]+[0-9]*

IllegalInteger = {Number} + {Identifier}+


%%

<YYINITIAL> {

  "MODULE"                       { return symbol(Symbol.MODULE); }
  "BEGIN"                        { return symbol(Symbol.BEGIN); }
  "END"                          { return symbol(Symbol.END); }
  "CONST"                        { return symbol(Symbol.CONST); }
  "TYPE"                         { return symbol(Symbol.TYPE); }
  "VAR"                          { return symbol(Symbol.VAR); }
  "PROCEDURE"                    { return symbol(Symbol.PROCEDURE); }
  "RECORD"                       { return symbol(Symbol.RECORD); }
  "ARRAY"                        { return symbol(Symbol.ARRAY); }
  "OF"                           { return symbol(Symbol.OF); }
  "WHILE"                        { return symbol(Symbol.WHILE); }
  "DO"                           { return symbol(Symbol.DO); }
  "IF"                           { return symbol(Symbol.IF); }
  "ELSE"                         { return symbol(Symbol.ELSE); }
  "ELSIF"                        { return symbol(Symbol.ELSIF); }
  "THEN"                         { return symbol(Symbol.THEN); }
  "BOOLEAN"                      { return symbol(Symbol.BOOLEAN); }
  "INTEGER"                      { return symbol(Symbol.INTEGER); }
  "TRUE"                         { return symbol(Symbol.TRUE); }
  "FALSE"                        { return symbol(Symbol.FALSE); }
  ";"                            { return symbol(Symbol.SEMICOLON); }
  "."                            { return symbol(Symbol.POINT); }
  ":"                            { return symbol(Symbol.COLON); }
  "("                            { return symbol(Symbol.LeftParenthesis); }
  ")"                            { return symbol(Symbol.RightParenthesis); }
  "["                            { return symbol(Symbol.LeftBracket); }
  "]"                            { return symbol(Symbol.RightBracket); }
  ","                            { return symbol(Symbol.COMMA); }
  "DIV"                          { return symbol(Symbol.DIV); }
  "MOD"                          { return symbol(Symbol.MOD); }
  "OR"                           { return symbol(Symbol.OR); }
  "="                            { return symbol(Symbol.EQUAL); }
  ":="                           { return symbol(Symbol.ASSIGNMENT); }
  ">"                            { return symbol(Symbol.GreatThan); }
  "<"                            { return symbol(Symbol.LessThan); }
  "~"                            { return symbol(Symbol.NOT); }
  "<="                           { return symbol(Symbol.LessThanOrEqual); }
  ">="                           { return symbol(Symbol.GreatThanOrEqual); }
  "#"                            { return symbol(Symbol.NOTEQUAL); }
  "&"                            { return symbol(Symbol.AND); }
  "+"                            { return symbol(Symbol.PLUS); }
  "-"                            { return symbol(Symbol.MINUS); }
  "*"                            { return symbol(Symbol.TIMES ); }
  "READ"		                     { return symbol(Symbol.READ);}
  "WRITE"		                     { return symbol(Symbol.WRITE);}
  "WRITELN"		     { return symbol(Symbol.WRITELN);}

  {WhiteSpace}                   {  }

  {Comment}                      {  }

  {IllegalComment}                 { throw new MismatchedCommentException(); }


  {Identifier}                   { if(yylength() > 24) throw new IllegalIdentifierLengthException();
                                        else return  symbol(Symbol.IDENTIFIER,yytext()); 
                                      }  


  {Number}                      { if(yylength() > 12) throw new IllegalIntegerRangeException();
                                        else return  symbol(Symbol.IDENTIFIER,yytext()); 
                                        }

  {IllegalInteger}               { throw new IllegalIntegerException(); }

  {IllegalOctal}                 { throw new IllegalOctalException(); }

  .                              { throw new IllegalSymbolException(); } 
}
[^]		                        {throw new IllegalSymbolException();}
