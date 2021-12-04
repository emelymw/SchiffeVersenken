/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Server.Server;
import Server.Client;
import controll.KISpielSteuerung;
import controll.LokalesSpielSteuerung;
import controll.OnlineSpielSteuerung;
import controll.SpielSteuerung;
import controll.SteuerungSchiffeSetzen;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import shapes.Schiff;

/**
 * FXML Controller class
 *
 * @author Marvin Hofmann, Emely Mayer-Walcher, Torben Doese, Lea-Marie Kindermann
 */
public class SpielGUIController implements Initializable {

    @FXML
    private Pane spielFeld;

    private SpielSteuerung dieSpielSteuerung = null;
    private SteuerungSchiffeSetzen dieSteuerungSchiffeSetzen = null;
    
    private int modus;

    private String ip; // Auslagern in Stuerung 
    private Server server; // Auslagern in Steuerung
    private Client client; 
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("SpielGUI");
        dieSteuerungSchiffeSetzen = new SteuerungSchiffeSetzen(this);
    }    

    void uebergebeInformationen(int spielfeldgroesse, int[] anzahlSchiffeTyp, int modus, String ip) {
        System.out.println("Übergabe Spielfeldgroesse, Anzahl der jeweiligen Schiffstypen und Modus: " + modus);
        this.modus = modus;
        if(modus == 1){ // Lokales Spiel 
            dieSteuerungSchiffeSetzen.uebergebeInformationen(spielfeldgroesse, anzahlSchiffeTyp); // Erzeuge SchiffeSetzenGui
            dieSpielSteuerung = new LokalesSpielSteuerung(this, spielfeldgroesse);
        }
        else if(modus == 21 || modus == 22){ // KI Spiel - 21 ki-host - 22 ki-client 
            if (modus==21) {
                new Thread(() -> {
                server = new Server(this);
                server.start();
                System.out.println(server);
            }).start();
            }
            else if(modus==22){
                new Thread(() -> {
                client = new Client(ip);
                client.start();
            }).start();
            }
            dieSpielSteuerung = new KISpielSteuerung(this, spielfeldgroesse, anzahlSchiffeTyp);
        }
        else if(modus == 31 || modus == 32){ // Online Spiel - 31 host - 32 client
            dieSteuerungSchiffeSetzen.uebergebeInformationen(spielfeldgroesse, anzahlSchiffeTyp); 
            dieSpielSteuerung = new OnlineSpielSteuerung(this);
            if(modus==31){
              new Thread(() -> {
               server = new Server(this);
               server.start();
            }).start();
            }
            else if(modus==32){
                new Thread(() -> {
                client = new Client(ip);
                client.start();
            }).start();
            }
        }
    }
    
    public void zeigeGrid(Rectangle rectangle) {
        spielFeld.getChildren().add(rectangle);
    }

    public void zeigeSchiffe(Schiff schiff) {
        spielFeld.getChildren().add(schiff);
    }
    
    public void zeigeSchiff(Rectangle rec){
        spielFeld.getChildren().add(rec);
    }
    
    @FXML
    private void handleButton(ActionEvent event) {
        // server.send("Hallo Client");
        if(dieSteuerungSchiffeSetzen.isFertig() && (dieSpielSteuerung instanceof LokalesSpielSteuerung)){
            spielFeld.getChildren().clear();
            dieSpielSteuerung.setSchiffe(dieSteuerungSchiffeSetzen.getSchiffArray());
            dieSpielSteuerung.erzeugespielfeld();
            dieSpielSteuerung.erzeugeGrid();
            dieSpielSteuerung.setzeSchiffe();
        } 
        else if(dieSpielSteuerung instanceof KISpielSteuerung){
            // auch alles starten 
        }
        else if(dieSteuerungSchiffeSetzen.isFertig() && (dieSpielSteuerung instanceof OnlineSpielSteuerung)){
            //TEST
            spielFeld.getChildren().clear();
            dieSpielSteuerung.setSchiffe(dieSteuerungSchiffeSetzen.getSchiffArray());
            dieSpielSteuerung.erzeugespielfeld();
            dieSpielSteuerung.erzeugeGrid();
            dieSpielSteuerung.setzeSchiffe();
        }
    }
    
    public void connectedWithClient(){
        System.out.println("Hallo welt");
        server.send("Hello");
    }

    public Server getServer() {
        return server;
    }
    
    
}
