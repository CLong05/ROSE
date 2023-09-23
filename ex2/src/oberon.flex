import java.io.*;
import exceptions.*;

%%
%class OberonScanner

%public
%eofval{
	return "EOF";
%eofval}
%ignorecase
%line
%column
%yylexthrow LexicalException
%type String
%{
  int get_line(){	return yyline;}
  int get_column(){	return yycolumn;}
%}


Number = 0[0-7]*  |  [1-9][0-9]*

Identifier = [:jletter:][:jletterdigit:]*

WhiteSpace = " " | \r | \n | \r\n | [ \t\f]

ReservedWord = "module"|"procedure"|"begin"|"end"|"if"|"then"|"elsif"|"else"|"while"|"do"|"of"

Keyword = "integer"|"boolean"|"var"|"type"|"record"|"const"|"array"|"read"|"write"|"writeln"

Comment = "(*" ~ "*)"

Operator = "+"|"-"|"*"|"div"|"mod"|":="| "="|"#"|">"|"<"|">="|"<="| "("|")"|"["|"]"|":"|"&"|"or"|"~"

Punctuation = ";" | "," | "."

IllegalComment = "(*" ([^\*] | "*"+[^\)])* | ([^\(]|"("+[^\*])* "*)"

IllegalOctal = 0[0-7]*[9|8]+[0-9]*

IllegalInteger = {Number} + {Identifier}+


%%

<YYINITIAL> {

  {ReservedWord}	    {return "ReservedWord";}

  {Punctuation}	    {return "PUNCTUATION";}

  {Keyword}	    {return "Keyword";}

  {Operator}                      { return "OPERATOR"; }

  {WhiteSpace}                   {  }

  {Comment}                      { return "COMMENT"; }

  {IllegalComment}                 { throw new MismatchedCommentException(); }


  {Identifier}                   { if(yylength() > 24) throw new IllegalIdentifierLengthException();
                                        else return "Identifier"; 
                                      }  


  {Number}                      { if(yylength() > 12) throw new IllegalIntegerRangeException();
                                        else return "NUMBER"; 
                                        }

  {IllegalInteger}               { throw new IllegalIntegerException(); }

  {IllegalOctal}                 { throw new IllegalOctalException(); }

  .                              { throw new IllegalSymbolException(); } 
}
[^]		                        {throw new IllegalSymbolException();}
