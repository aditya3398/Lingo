package com.evader.rookies.lingo;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by adityanadkarni on 10/22/16.
 * useful functions
 */
public class UDParse {
    public static String getTheDefinitionYouNeed(String word) {
        String url = "http://www.urbandictionary.com/define.php?term=";
        String str = "";
        url += word;
        Log.d("LINGOLOG","URL: " + url);
        try {
            Document document = Jsoup.connect(url).get();
            Element e = document.select("div.meaning").first();
            str = e.text();
            Log.d("LINGOLOG","URL: " + e.text());
            return e.text();
        }
        catch(NullPointerException e){
            Log.d("LINGOLOG", e.getMessage());
        }
        catch (IOException e) {
            Log.d("LINGOLOG", e.getMessage());
        }
        return "Could not determine the definition.";
    }
    public static ArrayList<String> tweetsToAnalyze(String needsReducing, Map<Character, ArrayList<String>> stopwords) {

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
            char q = list.get(d).charAt(0);
            for (String y : stopwords.get(q)) {
                if (y.equals(list.get(d))) {
                    list.remove(d);
                    d--;
                    break;
                }
            }
        }

        for (int d = 0; d < list.size(); d++) {
            if (list.get(d).equals("") || list.get(d).length() < 6) {
                list.remove(d);
                d--;
            }
        }

        ArrayList<String> terms = new ArrayList<String>();
        String current = list.get(0);
        int counter = 0;
        for (int e = 0; e <list.size(); e++){
            if(list.get(e).equals(current)){
                counter++;
                if(counter >= 3){
                    terms.add(list.get(e));
                    counter = 0;
                    if(e+1 < list.size()){
                        current = list.get(e+1);
                    }
                }
            }
            else {
                counter = 0;
                current = list.get(e);
            }
        }


        return terms;
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
