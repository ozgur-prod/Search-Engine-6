package pgdp.searchengine.networking;

public class HTMLToken {

    private final TokenType tokenType;
    private final StringBuilder content;

    public HTMLToken(TokenType tokenType){
        this.tokenType = tokenType;
        content = new StringBuilder();
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public String getContentAsString(){
        return content.toString();
    }

    public void addCharacter(char c){
        content.append(c);
    }

    public String toString(){
        if (getTokenType() == TokenType.TAG){
            return "Tag: " + getContentAsString();
        }
        else {
            return "Text: " + getContentAsString();
        }
    }

    public enum TokenType {
        TAG, TEXT
    }
}
