package com.evader.rookies.lingo;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.ArrayList;


/**
 * Created by adityanadkarni on 10/22/16.
 */
public class UDParse {
    public static String getTheDefinitionYouNeed(String word) {
        String url = "http://www.urbandictionary.com/define.php?term=";
        String str = "";
        for (int x = 0; x < word.length(); x++) {
            if (word.charAt(x) == ' ') {
                word = word.substring(0,x) + "+" + word.substring(x + 1, word.length());
            }
        }
        url += word;
        try {
            Document document = Jsoup.connect(url).get();
            Element e = document.select("class.meaning").first();
            str = e.text();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }
    public static ArrayList<String> tweetsToAnalyze(String needsReducing) {
        String [] stopwords = {"a", "are", "as", "at", "because", "but", "by",
                "do", "every", "for", "i", "is", "me", "of",
                "or", "so", "the", "this", "up", "very", "was", "what",
                "who", "were", "you"};
        needsReducing = needsReducing.toLowerCase();
        for (int x = 0; x < needsReducing.length(); x++) {
            int r = (int)(needsReducing.charAt(x));
            if (!(r >= 97 && r <= 122)) {
                needsReducing = needsReducing.substring(0, x) + " " + needsReducing.substring(x + 1);
            }
        }
        String[] listToSort = needsReducing.split(" ");
        ArrayList<String> list = new ArrayList<String>();
        for (int r = 0; r < listToSort.length; r++) {
            list.add(listToSort[r]);
        }
        sortStrings(list);
        //int x = 0;
        for (int d = 0; d < list.size(); d++) {
            // while(list.size() > 0  && list.get(d).charAt(0) == stopwords[x].charAt(0)) {
            //     if (list.get(d).equals(stopwords[x])) {
            //         list.set(d, "shukie");
            //         d--;
            //     } else{
            //         x++;
            //     }
            // }
            for (int x = 0; x < stopwords.length; x++) {
                if (list.get(d).equals(stopwords[x])) {
                    list.remove(d);
                    d--;
                }
            }
        }
        for (int d = 0; d< list.size(); d++) {
            if (list.get(d).equals("")) {
                list.remove(d);
                d--;
            }
        }
        return list;
    }

    private static void sortStrings(ArrayList<String> list) {
        for (int x = 0; x < list.size(); x++) {
            for (int y = x; y < list.size(); y++) {
                if (list.get(x).compareTo(list.get(y)) > 0) {
                    String s = list.get(y);
                    list.set(y, list.get(x));
                    list.set(x, s);
                }
            }
        }
    }
}
