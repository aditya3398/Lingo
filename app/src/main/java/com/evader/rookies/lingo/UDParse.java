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
    public static ArrayList<String> tweetsToAnalyze(String needsReducing) {
        //TODO: hashmap implementation <char, string[]>
        Map<Character, ArrayList<String>> stopwords = new HashMap<>(26, 0.75f);
        for (int x = 97; x < 123; x++) {
            stopwords.put((char)x, new ArrayList<String>());
        }
        stopwords.get('a').addAll(new ArrayList<String>(Arrays.asList("a", "and", "am", "are", "as", "at")));
        stopwords.get('b').addAll(new ArrayList<String>(Arrays.asList("because", "but", "by")));
        stopwords.get('c').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('d').addAll(new ArrayList<String>(Arrays.asList("do")));
        stopwords.get('e').addAll(new ArrayList<String>(Arrays.asList("every", "eight")));
        stopwords.get('f').addAll(new ArrayList<String>(Arrays.asList("for", "four", "five")));
        stopwords.get('g').addAll(new ArrayList<String>(Arrays.asList("go")));
        stopwords.get('h').addAll(new ArrayList<String>(Arrays.asList("here", "how", "have", "he")));
        stopwords.get('i').addAll(new ArrayList<String>(Arrays.asList("i", "is", "in")));
        stopwords.get('j').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('k').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('l').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('m').addAll(new ArrayList<String>(Arrays.asList("me", "my")));
        stopwords.get('n').addAll(new ArrayList<String>(Arrays.asList("nine")));
        stopwords.get('o').addAll(new ArrayList<String>(Arrays.asList("of","or", "on", "okay", "ok", "our", "one")));
        stopwords.get('p').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('q').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('r').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('s').addAll(new ArrayList<String>(Arrays.asList("so", "six", "seven")));
        stopwords.get('t').addAll(new ArrayList<String>(Arrays.asList("the","this", "that", "there", "then", "too", "two", "three", "ten", "them")));
        stopwords.get('u').addAll(new ArrayList<String>(Arrays.asList("up")));
        stopwords.get('v').addAll(new ArrayList<String>(Arrays.asList("very")));
        stopwords.get('w').addAll(new ArrayList<String>(Arrays.asList("we", "was", "what","who", "were", "went", "where", "why", "with", "will")));
        stopwords.get('x').addAll(new ArrayList<String>(Arrays.asList("")));
        stopwords.get('y').addAll(new ArrayList<String>(Arrays.asList("you")));
        stopwords.get('z').addAll(new ArrayList<String>(Arrays.asList("")));

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
