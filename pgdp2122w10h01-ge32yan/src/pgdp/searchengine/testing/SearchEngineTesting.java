package pgdp.searchengine.testing;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import pgdp.searchengine.networking.HTMLProcessing;
import pgdp.searchengine.networking.HTMLToken;

import java.util.Arrays;
import java.util.List;

public class SearchEngineTesting {

    // Here I tested the tokenize method. I created a string that only consisted of tags. Then I called the tokenize
    // method with this string as parameter. I checked if tokenize returned a list that only consisted of HTMLToken
    // elements which only had the token type TAG. I also checked if an upper-cased tag would be lower cased. I finally
    // checked if the contents of the tags had "<" or ">" in them, because they shouldn't.
    @Test
    void testTokenize(){
        String st = "<html>\n" +
                "    <BODY>\n" +
                "        <h1><a href=\"https://www.youtube.com/watch?v=OJOV1vf1zYk\"></a></h1>\n" +
                "        <br>\n" +
                "    </body>\n" +
                "</html>";

        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        for (HTMLToken token : list){
            assertEquals(HTMLToken.TokenType.TAG, token.getTokenType());
            assertFalse(token.getContentAsString().contains("<"));
            assertFalse(token.getContentAsString().contains(">"));
            assertEquals("body", list.get(1).getContentAsString());
        }
    }

    // Here I added two texts to the html code from the previous test. I checked if they were added as TEXT elements
    // to the list. I also checked if they were also lower cased and were matching 100% to the original texts.
    @Test
    void testTokenize2(){
        String st = "<html>\n" +
                "<HEAD>\n" +
                "        <TITLE>pingu    pingu</TITLE>\n" +
                "    </HEAD>\n" +
                "    <body>\n" +
                "        <h1>SOME TEXT here <a href=\"https://www.youtube.com/watch?v=OJOV1vf1zYk\"></a></h1>\n" +
                "        <br>\n" +
                "    </body>\n" +
                "</html>";

        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals(HTMLToken.TokenType.TEXT, list.get(3).getTokenType());
        assertEquals("pingu    pingu", list.get(3).getContentAsString());
        assertEquals(HTMLToken.TokenType.TEXT, list.get(8).getTokenType());
        assertEquals("some text here ", list.get(8).getContentAsString());
    }

    // Here I tested some links in tags. Links are strings in tags in html code. First I created a tag with the attribute
    // "A HREF=...". When tokenized, the token should have a content where A HREF was lower cased. So first I tested
    // that case.
    // Secondly, I created the same tag and this time included upper case characters in the string. All the characters
    // that are in strings should be added to the contents of the corresponding tokens regardless of their situation
    // of being upper or lower case. So I tested if the token had the same string with upper-cased characters in its content.
    // According to the aufgabe, strings start and end with either " or '. In the first case the string can have a ' in it
    // and in the second case a string can have a " in it. So, I also tested one of these cases to see if tokenize
    // tokenized a string with a ' in it and added it to the content of the corresponding token without a problem.
    // Finally, I tested if strings handle "<" and ">" in them. Normally "<" indicates the start of a tag and ">" indicates
    // the end of it. But inside strings those characters don't have those functionalities, tey are just characters.
    // So, I tested if the tokenize method would see them as only characters (when in a string) and not tag starting and ending commands.
    @Test
    void testTokenize3(){
        String st = "<A HREF=\"https://pgdp.sse.in.tum.de/index.html\">";
        HTMLToken token = HTMLProcessing.tokenize(st).get(0);
        assertEquals("a href=\"https://pgdp.sse.in.tum.de/index.html\"", token.getContentAsString());

        String st2 = "<A HREF=\"HTTPS://PGDP.sse.In.Tum.DE/indeX.HtMl\">";
        HTMLToken token2 = HTMLProcessing.tokenize(st2).get(0);
        assertEquals("a href=\"HTTPS://PGDP.sse.In.Tum.DE/indeX.HtMl\"", token2.getContentAsString());

        String st3 = "<A HREF=\"https://pgdp.ss'e.in.tum.de/index.html\">";
        HTMLToken token3 = HTMLProcessing.tokenize(st3).get(0);
        assertEquals("a href=\"https://pgdp.ss'e.in.tum.de/index.html\"", token3.getContentAsString());

        String st4 = "<A HREF=\"https://pgdp.sse.in.tu<m.de/index.h>tml\">";
        HTMLToken token4 = HTMLProcessing.tokenize(st4).get(0);
        assertEquals("a href=\"https://pgdp.sse.in.tu<m.de/index.h>tml\"", token4.getContentAsString());
    }

    // Here I tested the filterlinks method. In Artemis it is said that only three types of links would be tested.
    // So, first I tested the case where a link with "https://" was in the html code. I checked if the filterlinks
    // method returned an array with the element that was the same with the link without "https://".
    @Test
    void testFilterLinks(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE>Man page of RSTART</TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        <H1>RSTART</H1>\n" +
                "        ...\n" +
                "        <A HREF=\"https://pgdp.sse.in.tum.de/index.html\">Return to Main Contents</A>\n" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";

        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals(1, HTMLProcessing.filterLinks(list, "host").length);
        assertEquals("pgdp.sse.in.tum.de/index.html", HTMLProcessing.filterLinks(list, "host")[0]);
    }

    // Then I tested the case of a link without a host, because the host was equal to the one we call the method with.
    // I checked if filterlinks added the host to the beginning of the link.
    @Test
    void testFilterLinks2(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE>Man page of RSTART</TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        <H1>RSTART</H1>\n" +
                "        ...\n" +
                "        <A HREF=\"/cgi-bin/man/man2html\">man2html</A>" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";

        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals(1, HTMLProcessing.filterLinks(list, "host").length);
        assertEquals("host/cgi-bin/man/man2html", HTMLProcessing.filterLinks(list, "host")[0]);
    }

    // Lastly, I tested the case where there were only invalid links in the code. I checked if the array filterlinks
    // returned had no elements in it. Those invalid links were also taken from Artemis.
    @Test
    void testFilterLinks3(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE>Man page of RSTART</TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        <H1>RSTART</H1>\n" +
                "        ...\n" +
                "        <A NAME=\"lbAB\">&nbsp;</A>\n" +
                "<a href=\"#index\">\n" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";

        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals(0, HTMLProcessing.filterLinks(list, "host").length);
    }

    // Here I tested the filterText method. I simply added a text to my code. Then checked if the method returned that text
    @Test
    void testFilterText(){
        String st = "<html>\n" +
                "    <body>\n" +
                "        in sibirien ist es recht kühlschrank und schreibtisch stehen im zimmer\n" +
                "    </body>\n" +
                "</html>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("in sibirien ist es recht kühlschrank und schreibtisch stehen im zimmer", HTMLProcessing.filterText(list));
    }

    // Then I checked if there was a space between texts when they were seperated with tags.
    @Test
    void testFilterText2(){
        String st = "<html>\n" +
                "    <body>\n" +
                "        in sibirien ist es recht kühl<br>schrank und schreibtisch stehen im zimmer\n" +
                "    </body>\n" +
                "</html>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("in sibirien ist es recht kühl schrank und schreibtisch stehen im zimmer", HTMLProcessing.filterText(list));
    }

    // Finally, I checked if more than one tag between texts still added only one space between them when returning a new string.
    @Test
    void testFilterText3(){
        String st = "<html>hallo\n" +
                "    <body>\n" +
                "        in sibirien ist es recht kühl<br>\n" +
                "    </body>\n" +
                "schrank und schreibtisch stehen im zimmer\n" +
                "</html>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("hallo in sibirien ist es recht kühl schrank und schreibtisch stehen im zimmer", HTMLProcessing.filterText(list));
    }

    // Here I tested the filtertitle method in a general case from Artemis. I added a title between <title> tags and checked
    // if the method returned the correct title.
    @Test
    void testFilterTitle(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE>Man page of ISO_8859-1</TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("man page of iso_8859-1", HTMLProcessing.filterTitle(list));
    }

    // Then I tested the case where a tag interrupted the title. I checked if the correct title was returned in which
    // the interrupting tag was replaced with a space.
    @Test
    void testFilterTitle2(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE>Man page of<some tag>ISO_8859-1</TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("man page of iso_8859-1", HTMLProcessing.filterTitle(list));
    }

    // Lastly I included <title> and </title> tags in my code but didn't include any true title between those. I checked if
    // "" was returned, because there is no title.
    @Test
    void testFilterTitle3(){
        String st = "<HTML>\n" +
                "    <HEAD>\n" +
                "        <TITLE></TITLE>\n" +
                "    </HEAD>\n" +
                "    <BODY>\n" +
                "        ...\n" +
                "    </BODY>\n" +
                "</HTML>";
        List<HTMLToken> list = HTMLProcessing.tokenize(st);
        assertEquals("", HTMLProcessing.filterTitle(list));
    }
}
