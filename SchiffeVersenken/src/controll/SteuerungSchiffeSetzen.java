/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controll;

import GUI.SpielGUIController;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import shapes.Grid;
import shapes.Schiff;

/**
 *
 * @author Marvin Hofmann, Emely Mayer-Walcher, Torben Doese, Lea-Marie Kindermann
 */
public class SteuerungSchiffeSetzen implements EventHandler<KeyEvent>{
    private SpielGUIController dieGui = null;

    //Spielfeld => 600x600
    private int[] schiffTypen;
    private int buffer = 0;
    private int anzSchiffe = 0;
    private Schiff[] schiffArray;
    private Grid gridSpielfeld;
    private boolean fertig = true;

    public boolean isFertig() {
        return fertig;
    }
    
    public SteuerungSchiffeSetzen(GUI.SpielGUIController gui) {
        System.out.println("SteuerungSchiffeSetzen erzeugt");
        this.dieGui = gui;
    }
    
    public SpielGUIController getDieGui() {
        return dieGui;
    }

    public void setDieGui(SpielGUIController dieGui) {
        this.dieGui = dieGui;
    }

    public int[] getSchiffTypen() {
        return schiffTypen;
    }

    public void setSchiffTypen(int[] schiffTypen) {
        this.schiffTypen = schiffTypen;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public int getAnzSchiffe() {
        return anzSchiffe;
    }

    public void setAnzSchiffe(int anzSchiffe) {
        this.anzSchiffe = anzSchiffe;
    }

    public Schiff[] getSchiffArray() {
        return schiffArray;
    }

    public void setSchiffArray(Schiff[] schiffArray) {
        this.schiffArray = schiffArray;
    }

    public Grid getGridS() {
        return gridSpielfeld;
    }

    public void setGridS(Grid gridSpielfeld) {
        this.gridSpielfeld = gridSpielfeld;
    }
    
    public void uebergebeInformationen(int spielfeldgroesse, int[] anzahlSchiffeTyp) {
        this.schiffTypen = anzahlSchiffeTyp;
        for (int i = 0; i < schiffTypen.length; i++) {
            anzSchiffe += schiffTypen[i];
        }
        /*DEBUG
        for (int i = 0; i < schiffTypen.length; i++) {
            System.out.println(schiffTypen[i]);
        }
        */
        drawAll(spielfeldgroesse);
    }
    
    private void drawAll(int gr) {
        gridSpielfeld = new Grid(gr);
        gridSpielfeld.macheGrid();
        //System.out.println(feld.length);
        //Grid Zeichnen rectangle kacheln dem Pane hinzufügen
        for (int i = 0; i < gridSpielfeld.getGrid().length; i++) {
            for (int j = 0; j < gridSpielfeld.getGrid().length / 2; j++) {
                dieGui.zeigeGrid(gridSpielfeld.getGrid()[i][j]);
            }
        }
        macheSchiffe();
    }
    
     private void macheSchiffe() {
        //Erstelle alle Schiffe und Speichere sie in einem Array voller 
        //Schiffsobjekte ab
        schiffArray = new Schiff[anzSchiffe];
        int ctn = 0;
        for (int i = 0; i < schiffTypen[0]; i++) {
            schiffArray[ctn++] = new Schiff(2 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
            //System.out.println("Erstelle typ 2");
        }
        for (int i = 0; i < schiffTypen[1]; i++) {
            schiffArray[ctn++] = new Schiff(3 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
            //System.out.println("Erstelle typ 3");
        }
        for (int i = 0; i < schiffTypen[2]; i++) {
            schiffArray[ctn++] = new Schiff(4 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
            //System.out.println("Erstelle typ 4");
        }
        for (int i = 0; i < schiffTypen[3]; i++) {
            schiffArray[ctn++] = new Schiff(5 * gridSpielfeld.getKachelgroeße(), gridSpielfeld.getKachelgroeße());
            //System.out.println("Erstelle typ 5");
        }
        //DEBUG
        /*for (int i = 0; i < schiffArray.length; i++) {
            System.out.println(schiffArray[i]);
        }*/
        //
        int m = 0; //Merker um schiffe versetzt auszugeben
        //Alle Schiffe dem Pane als rectangle hinzufügen
        //Die Schiffe auf dem Grid zeichnen und mit einer Zeile abstand im Buffer Ablegen
        for (int i = 0; i < schiffArray.length; i++) {
            dieGui.zeigeSchiffe(schiffArray[i]);
            schiffArray[i].draw(gridSpielfeld.getPxGroesse(), 2 * m);
            makeHandler(schiffArray[i]);
            m = m + gridSpielfeld.getKachelgroeße();
        }
    }
    
    private void makeHandler(Schiff s) {
        s.setOnMouseDragged(event -> dragged(event, s));
        s.setOnMouseReleased(event -> released(event, s));
    }
     
    public void dragged(MouseEvent event, Schiff s) {
        //schiffeSetzenFeld.setCursor(Cursor.CLOSED_HAND);
        //dieGui.setzeCursor(Cursor.CLOSED_HAND);
        //Berechne den Puffer zur nächsten grenze nach links und unten zurück kommt ein
        //int wert zwichen 0 und 59 welcher minus der aktuellen Position dem Objekt zum setzten 
        //Übergeben wird, so läuft ein schiff nur in den Kacheln und nicht quer darüger
        int snapX = (int) (event.getX() % gridSpielfeld.getKachelgroeße());
        int snapY = (int) (event.getY() % gridSpielfeld.getKachelgroeße());
        //setzte x,y Wert für Objetk
        int x = (int) event.getX() - snapX;
        int y = (int) event.getY() - snapY;
        boolean blockiert = false;
        
        if(x < 0){
            blockiert = true;
        }
        else if(y < 0){
            blockiert = true;
        }
        else if(x > (1200 - s.getWidth() + 0.5 * gridSpielfeld.getKachelgroeße())){
            blockiert = true;
        }
        else if(y > (600 - s.getHeight()) + 0.5 * gridSpielfeld.getKachelgroeße()){
            blockiert = true;
        }
        else{
            blockiert = false;
        }
        
        if(!blockiert){
            if(x <= (600 - s.getWidth())){
                s.setFill(Color.GREEN);
            }
            else{
                s.setFill(Color.RED);
            }
            s.setX(x);
            s.setY(y);
        }  
        
        //Neu zeichnen
        //drawWasser(s, Color.WHITE);
        s.draw();
    }
    
    public void cleanFeld(){
        for (int i = 0; i < gridSpielfeld.getKachelAnzahl(); i++) {
            for (int j = 0; j < gridSpielfeld.getKachelAnzahl(); j++) {
                if (!gridSpielfeld.getGrid()[i][j].getFill().equals(Color.NAVY)) {

                    gridSpielfeld.getGrid()[i][j].setFill(Color.WHITE);
                }
            }
        }
    }

    public void drawWasser(Schiff s, Color c) {
        int aktX = (int) s.getX() / gridSpielfeld.getKachelgroeße();
        int aktY = (int) s.getY() / gridSpielfeld.getKachelgroeße();

        //Spielfeld cleanen bei jedem draw
        cleanFeld();
        if (aktX < gridSpielfeld.getKachelAnzahl() - s.getWidth() / gridSpielfeld.getKachelgroeße()) {
            for (int i = -1; i < s.getWidth() / gridSpielfeld.getKachelgroeße() + 1; i++) {
                //Von -1 links vom Schiff bis eins rechts vom schiff mache obere und unter linie
                //
                //  [-1 / -1][0 / -1  ][ +1 / -1][+2/-1]
                //  [-1 /  y][SCHIFF_1][SCHIFF_1][schiffbreite / y]
                //  [-1 / +1][0 / +1  ][ +1 / +1][+2/+1]
                //
                gridSpielfeld.getGrid()[aktX + i][aktY + 1].setFill(c);
                gridSpielfeld.getGrid()[aktX + i][aktY - 1].setFill(c);
            }
            //Kachel rechts
            gridSpielfeld.getGrid()[aktX + (int) s.getWidth() / gridSpielfeld.getKachelgroeße()][aktY].setFill(c);
            //Kachel links
            gridSpielfeld.getGrid()[aktX - 1][aktY].setFill(c);

        }
    }

    //Verwalten des Zustands losgelassen
    public void released(MouseEvent event, Schiff s) {
        int puff = (int) (s.getX() / gridSpielfeld.getKachelgroeße());
        int puffY = (int) (s.getY() / gridSpielfeld.getKachelgroeße());
        s.setX(puff * gridSpielfeld.getKachelgroeße());
        s.setY(puffY * gridSpielfeld.getKachelgroeße());
        s.setFill(Color.GREEN);
        s.setStroke(Color.GREEN);
        //Ermittle Koordinatenwert der StartPositionen für 2D Array
        int startX = (int) event.getX() / gridSpielfeld.getKachelgroeße();
        int startY = (int) event.getY() / gridSpielfeld.getKachelgroeße();
        //System.out.println(startX + " " + startY);
        s.setStart(startX, startY);
        //drawWasser(s, Color.NAVY);
        s.draw();
    }
    
    @Override
    public void handle(KeyEvent event) {
        System.out.println("Huhu");
        /*for (int i = 0; i < schiffArray.length; i++) {
            System.out.println("Schiff" + i + ": ");
            System.out.println(schiffArray[i].getHeight());
            System.out.println(schiffArray[i].getWidth());
            System.out.println(schiffArray[i].getStartX());
            System.out.println(schiffArray[i].getStartY());
            System.out.println("####################");
        }*/
    }

    
    
}
