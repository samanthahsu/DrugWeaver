package main;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

public class MedTreeGraph extends JPanel {
    Vector<String> names = new Vector<String>();
    Vector<Pair> pairs = new Vector<Pair>();

    @FXML
    TextField medNameTxt;
    @FXML
    Label wrngLbl;

    @Override
    public void paintComponent(Graphics g) {

        int originX = 250;
        int originY = 250;
        Vector<Dataset> data = new Vector<Dataset>();
        double theta = 0;
        int r = 100;
        int x;
        int y;
        for (int i = 0; i < names.size(); i++) {
            x = originX + (int) (r * Math.cos(theta));
            y = originY - (int) (r * Math.sin(theta));
            theta += (2 * Math.PI) / (names.size());
            g.drawString(names.get(i), x, y);
            data.add(new Dataset(names.get(i), x, y));
        }
        String name;
        int index;
        for (int i = 0; i < pairs.size(); i++) {
            name = pairs.get(i).getKey().toString().toUpperCase();
            index = 0;
            for (int j = 0; j < names.size(); j++) {
                if (data.get(j).getName().equals(name)) {
                    index = j;
                    break;
                }
            }
            x = data.get(index).getX();
            y = data.get(index).getY();
            name = pairs.get(i).getValue().toString().toUpperCase();
            index = 0;
            for (int j = 0; j < names.size(); j++) {
                if (data.get(j).getName().equals(name)) {
                    index = j;
                    break;
                }
            }
            g.drawLine(x, y, data.get(index).getX(), data.get(index).getY());
        }
    }
    public void readFile(String filePath) throws IOException {
        File file = new File(filePath);
        BufferedReader br = new BufferedReader(new FileReader(file));
        String st;
        while ((st = br.readLine()) != null) {
            String[] words = st.split(" ");
            pairs.add(new Pair(words[0], words[1]));
            names.addAll(Arrays.asList(words));
        }
    }
    public void draw() throws IOException {
        Set<String> nameSet = new HashSet<>();
        nameSet.addAll(names);
        Data d = null;
        try {
            d = new Data(nameSet);
        } catch (invalidInputException e) {
            wrngLbl.setText("Did not recognize drug name, please try again.");
        }
        assert d != null;
        pairs.addAll(d.getInteractions());

        for(Pair p : d.getInteractions()) {
            System.out.println(p.getKey() + " " + p.getValue());
        }
// todo close previous jframe window
//        todo clear all previously entered data
        drawGraph();
    }

    public void addMedList() {
        names.add(medNameTxt.getText().toUpperCase());
        medNameTxt.clear();
        medNameTxt.requestFocus();
    }

    public void drawGraph() {
        JFrame jFrame = new JFrame();
        jFrame.add(new MedTreeGraph());
        jFrame.setSize(500, 500);
        jFrame.setVisible(true);
        paintComponent(jFrame.getGraphics());
    }
}