package pgdp.searchengine.networking;

import java.util.Scanner;

public class HTTPResponse {

    private final HTTPStatus status;
    private final String html;

    public HTTPResponse(String answer){
        Scanner scanner = new Scanner(answer);
        String first = scanner.nextLine();
        String[] words = first.split(" ");
        int code = Integer.parseInt(words[1]);
        this.status = HTTPStatus.getByCode(code);

        String[] sentences = answer.split("\n");

        int index = -1;
        int index2 = -1;
        for (int i = 0; i < sentences.length; i++){
            if (sentences[i].toLowerCase().contains("<html>") || sentences[i].toLowerCase().startsWith("<html ")){
                index = i;
            }
            if (sentences[i].toLowerCase().contains("</html>")){
                index2 = i;
            }
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int j = index; j <= index2; j++){
            stringBuilder.append(sentences[j]).append("\n");
        }

        this.html = stringBuilder.toString();
    }

    public HTTPStatus getStatus() {
        return status;
    }

    public String getHtml() {
        return html;
    }

    public static void main(String[] args) {
        String st = "HTTP/1.1 200 OK\n" +
                "\n" +
                "<HTML LANG=\"de\"><HEAD><TITLE>Title</TITLE></HEAD><BODY>Some Text</BODY></HTML>";
        HTTPResponse response = new HTTPResponse(st);
        System.out.println(response.getHtml());
    }
}
