
public class Token{

    public String name;
    public String type;

    public Token(String name,String type){

        this.name=name;
        this.type=type;
    }

    public Token(){

        this.name="";
        this.type="";
    }

    public Token(Token e){

        this.name=e.name;
        this.type=e.type;
    }
}