package pgdp.searchengine.networking;

import pgdp.searchengine.pagerepository.*;
import pgdp.searchengine.util.Date;

import java.util.List;

public final class PageCrawling {

    // Another useless constructor for SCA
    private PageCrawling() {}

    public static void crawlPages(LinkedDocumentCollection collection, int number) {
        int count = 0;
        while (count <= number){
            if (collection.getNextUncrawledAddress() != null){
                if (crawlPage(collection, collection.getNextUncrawledAddress())){
                    count++;
                }
                else {
                    if (collection.find(collection.getNextUncrawledAddress())instanceof DummyLinkedDocument dummy){
                        collection.removeDummy(dummy);
                    }
                }
            }
        }
    }

    public static void crawlPages(LinkedDocumentCollection collection, int number, String startingAddress) {
        crawlPage(collection, startingAddress);
        crawlPages(collection, number);
    }

    public static boolean crawlPage(LinkedDocumentCollection collection, String address) {

        String[] hostPath = address.split("/");
        String host = hostPath[0];
        String path = hostPath[1];

        HTTPRequest request = new HTTPRequest(host, path);
        HTTPResponse response = request.send(443);

        if (response.getStatus() != HTTPStatus.OK){
            return false;
        }
        else {
            List<HTMLToken> tokens = HTMLProcessing.tokenize(response.getHtml());
            String title = HTMLProcessing.filterTitle(tokens);
            String content = HTMLProcessing.filterText(tokens);
            String[] outgoingLinks = HTMLProcessing.filterLinks(tokens, host);
            Date date = new Date(15,1,2022);
            Date date1 = new Date(5,3,2002);
            Author author = new Author("Özgür", "Yilmaz", "","",date1);
            LinkedDocument linkedDocument = new LinkedDocument(title, "", content, date, author, address, 10);
            collection.addToResultCollection(linkedDocument, outgoingLinks);
            return true;
        }
    }



    // -------- main() zum Testen -------- //

    public static void main(String... args) {
        String host = "man7.pgdp.sse.in.tum.de";
        String path = "iso_8859_1.7.html";

        LinkedDocumentCollection ldc = new LinkedDocumentCollection(1000);
        crawlPages(ldc, 5, host + "/" + path);
    }

}
