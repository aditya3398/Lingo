package com.evader.rookies.lingo;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

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
}
