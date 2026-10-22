package pgdp.searchengine.networking;

import java.util.*;
import java.util.stream.Collectors;

public final class HTMLProcessing {

    // Useless constructor for SCA
    private HTMLProcessing() {}

    public static List<HTMLToken> tokenize(String rawHTML) {
        rawHTML = rawHTML.lines().map(String::trim).collect(Collectors.joining());

        ArrayList<HTMLToken> list = new ArrayList<>();
        boolean isString;
        boolean isString2;
        int count = 0;
        int count2 = 0;

        for (int i = 0; i < rawHTML.length(); i++){
            char ch = rawHTML.charAt(i);

            isString = Math.abs(count) % 2 == 1;
            isString2 = Math.abs(count2) % 2 == 1;

            if ((isString || isString2) && (ch == '<' || ch == '>')){
                list.get(list.size()-1).addCharacter(rawHTML.charAt(i));
            }
            else if (ch == '<'){
                HTMLToken token = new HTMLToken(HTMLToken.TokenType.TAG);
                list.add(token);
            }
            else if (ch == '>' && rawHTML.length() != i+1 && rawHTML.charAt(i+1) != '<'){
                HTMLToken token = new HTMLToken(HTMLToken.TokenType.TEXT);
                list.add(token);
            }
            else if (list.get(list.size()-1).getTokenType().equals(HTMLToken.TokenType.TAG) && !isString2 && ch == '"'){
                count++;
            }
            else if (list.get(list.size()-1).getTokenType().equals(HTMLToken.TokenType.TAG) && !isString && ch == '\''){
                count2++;
            }


            if ((isString || isString2) && ch != '<' && ch != '>'){
                list.get(list.size()-1).addCharacter(rawHTML.charAt(i));
            }
            else {
                if (ch != '\n' && ch != '<' && ch != '>'){
                    list.get(list.size()-1).addCharacter(rawHTML.toLowerCase(Locale.ROOT).charAt(i));
                }
            }
        }

        return list;
    }

    public static String[] filterLinks(List<HTMLToken> tokens, String host) {
        return tokens
                .stream()
                .filter(token -> token.getTokenType().equals(HTMLToken.TokenType.TAG))
                .map(HTMLToken::getContentAsString)
                .filter(contentAsString -> contentAsString.startsWith("a href="))
                .filter(contentAsString -> !contentAsString.contains("#index"))
                .filter(contentAsString -> contentAsString.contains("/"))
                .map(contentAsString -> contentAsString.replace("a href=", ""))
                .map(contentAsString -> contentAsString.startsWith("\"") ? contentAsString.replaceAll("\"", "") : contentAsString.replaceAll("'", ""))
                .map(contentAsString -> contentAsString.startsWith("https://") ? contentAsString.replace("https://", "") : host+contentAsString)
                .toArray(String[]::new);

    }

    public static String filterText(List<HTMLToken> tokens) {
        return tokens
                .stream()
                .filter(token -> token.getTokenType().equals(HTMLToken.TokenType.TEXT))
                .map(HTMLToken::getContentAsString)
                .collect(Collectors.joining(" "));
    }

    public static String filterTitle(List<HTMLToken> tokens) {
        StringBuilder title = new StringBuilder();

        for (int i = 0; i < tokens.size(); i++){
            if (!title.isEmpty()){
                break;
            }
            if (tokens.get(i).getTokenType().equals(HTMLToken.TokenType.TAG) && tokens.get(i).getContentAsString().startsWith("title")){
                for (int j = i+1; j < tokens.size(); j++){
                    if (tokens.get(j).getContentAsString().equals("/title") && tokens.get(j).getTokenType().equals(HTMLToken.TokenType.TAG)){
                        break;
                    }
                    else if (tokens.get(j).getTokenType().equals(HTMLToken.TokenType.TEXT)){
                        title.append(tokens.get(j).getContentAsString());
                    }
                    else if (tokens.get(j).getTokenType().equals(HTMLToken.TokenType.TAG)){
                        title.append(" ");
                    }
                }
            }
        }
        return title.toString();
    }


    public static void main(String[] args) {
        String st = "<a href=\"/iyi\">";
        System.out.println(Arrays.toString(filterLinks(tokenize(st), "host")));
    }

}
