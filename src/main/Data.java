package main;

import javafx.util.Pair;
import org.xml.sax.SAXException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class Data {

    private final String API_URL_BASE = "https://rxnav.nlm.nih.gov/REST/interaction/list?rxcuis=";
    private Set<Pair<String, String>> interactions;
    private Set<String> drugs;


    public Data(Set<String> stringSet) throws IOException, invalidInputException {
        interactions = new HashSet<>();
        drugs = stringSet;
            readAPI();
    }

    public void readAPI() throws IOException, invalidInputException {

        String formattedDrugCodes = inputListMaker();

        StringBuilder getHtmlStr = new StringBuilder();
        URL url = new URL(API_URL_BASE + formattedDrugCodes);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        while ((line = rd.readLine()) != null) {
            getHtmlStr.append(line);
        }
        htmlStrToPairs(getHtmlStr);
    }

    private void htmlStrToPairs(StringBuilder getHtmlStr) {
        String[] splitByIntrCon = getHtmlStr.toString().split("interactionConcept");
        for (String s : splitByIntrCon) {
            extractRxcui(s);
        }
    }

    private void extractRxcui(String s) {
        String[] splitname = s.split("<name>");
        List<String> list = new ArrayList<>();
        for(String section : splitname) {
            if (section.charAt(0) != '<' && section.charAt(0) != '>') {
                list.add(getFirstSplitName(section)); // returns splitted names
            }
        }
        if (list.isEmpty()) return;
        Pair<String, String> p = new Pair<String, String>(list.get(0), list.get(1));
        interactions.add(p);
    }

    private String getFirstSplitName(String section) {
        String[] splitResult = section.split("</name>");
        return splitResult[0];
    }

    private String inputListMaker() throws IOException, invalidInputException {
        String returnS = "";
        for(String s : drugs) {
            returnS += getDrugCode(s);
            returnS += "+";
        }
        return returnS.substring(0, returnS.length()-1);
    }

    private String getDrugCode(String drug) throws IOException, invalidInputException {
        StringBuilder result = new StringBuilder();
        URL url = new URL("https://rxnav.nlm.nih.gov/REST/rxcui?name=" + drug);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line;
        String[] array = null;
        while ((line = rd.readLine()) != null) {
            result.append(line);
            array = line.split("<rxnormId>");
        }
        rd.close();

        if (array == null || array.length < 2) {
            throw new invalidInputException();
        }

        String y = array[1].split("</rxnormId>")[0];
        return y;
    }


    //    read in file data and populate the two sets
    public void readFile(String filePath) throws IOException {
        File file = new File(filePath);

        BufferedReader br = new BufferedReader(new FileReader(file));

        String st;
        while ((st = br.readLine()) != null) {
            String [] words = st.split(" ");
            interactions.add(new Pair<String, String>(words[0], words[1]));
             drugs.addAll(Arrays.asList(words));
        }
    }

    public Set<Pair<String, String>> getInteractions() {
        return interactions;
    }

    public Set<String> getDrugs() {
        return drugs;
    }

}
