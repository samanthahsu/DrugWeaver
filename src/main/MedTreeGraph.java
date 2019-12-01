package main;

import exceptions.emptyListException;
import exceptions.invalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import static javafx.scene.paint.Color.GREEN;
import static javafx.scene.paint.Color.RED;

/**
 * controller for main.fxml
 * */
public class MedTreeGraph extends JPanel {
    Vector<String> names = new Vector<String>();
    Vector<Pair> pairs = new Vector<Pair>();

    @FXML
    TextField medNameTxt;
    @FXML
    Label warningLb;
    JFrame jFrame;

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
        for (Pair pair : pairs) {
            name = pair.getKey().toString().toUpperCase();
            index = 0;
            for (int j = 0; j < names.size(); j++) {
                if (data.get(j).getName().equals(name)) {
                    index = j;
                    break;
                }
            }
            x = data.get(index).getX();
            y = data.get(index).getY();
            name = pair.getValue().toString().toUpperCase();
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

    // on action for button
    public void draw() throws IOException {
        warningLb.setTextFill(RED);
        Set<String> nameSet = new HashSet<>(names);
        APIParser d;
        try {
            d = new APIParser(nameSet);
            pairs.addAll(d.getInteractions());

            for(Pair p : d.getInteractions()) {
                System.out.println(p.getKey() + " " + p.getValue());
            }

            drawGraph();
            warningLb.setText("Graph drawn successfully!");
            warningLb.setTextFill(GREEN);

        } catch (invalidInputException e) {
            warningLb.setText("Did not recognize drug name, please try again.");
            clearData();
        } catch (emptyListException e) {
            warningLb.setText("Please enter at least one drug.");
            clearData();
        }
    }

    // on action for button
    public void addMedList() {
        names.add(medNameTxt.getText().toUpperCase());
        medNameTxt.clear();
        medNameTxt.requestFocus();
    }

    public void drawGraph() {
        if (jFrame != null) jFrame.dispose();

        jFrame = new JFrame();
        jFrame.add(new MedTreeGraph());
        jFrame.setSize(500, 500);
        jFrame.setVisible(true);
        paintComponent(jFrame.getGraphics());

        clearData();
    }

    private void clearData() {
        names.clear();
        pairs.clear();
    }
}