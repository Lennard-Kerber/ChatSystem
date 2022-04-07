import java.net.Socket;
import java.awt.Color;
import javax.swing.*;

/**
 * Ein ChatClient ist ein Teilnehmer in einer Chatkonferenz. 
 * Er verbindet sich mit einem ChatServer, dessen IP über den Aufrufparameter mitgegeben werden muss.
 * <p>
 * Es öffnen sich zwei Fenster:
 * <ul>
 * <li>Im Anzeigefenster werden alle Nachrichten des Chats angezeigt.
 * <li>Im Eingabefenster kann eine Chatzeile einegegeben werden.
 * Nach dem Absenden der Chatzeile öffnet sich das Fenster stets erneut.
 * </ul>
 * <p>
 * Schreibt ein Konferenzteilnehmer die Nachricht ENDE (Groß- und Kleinschreibung werden ignoriert), 
 * so wird die Konferenz für alle Teilnehmer beendet. 
 * 
 * @author LK
 * @version 2022-03-25
 */
public class ChatClient implements ClientType
{
    /**
     * Startet den Clientserver, Hauptprogramm
     * @param args Feld von Argumenten, die in der Komandozeile mit angegeben werden. hier: <code>args[0]</code> ist die IP-Adresse des Servers 
     */
    public static void main(String[] args)
    {
        // wenn die Anzahl der Parameter bei Programmstart stimmt
        if (args!=null && args.length >= 1){ 
            System.out.println("Client wird gestartet ...");
            new ChatClient(args[0],GLOBAL.PORT);  // erzeugt einen ChatClient, der alle Threads und Fenster startet
        } else {
            // Fehlerausgabe
            System.err.println("IP-Adresse des Servers als erster Parameter des Aufrufs fehlt.");
            System.err.println("java ChatClient <ip address>");
        }
    }

    // Instanzvariablen 
    private boolean isActive = true; // true, solange der Client läuft
    private ChatAnwendungsschicht anwendungsschicht;
    private ICI myICI; // ein Client besitzt genau eine Verbindung, diese wird in myICI gespeichert
    private Anzeigefenster anzeige; // Ausgabefenster   
    private Eingabefenster eingabe; // Eingabefenster
    private Benutzer benutzer; // mein Benutzer

    /**
     * Konstruktor für Objekte der Klasse ChatClient
     * @param ip IP-Adresse des Servers
     * @param port Port des Servers
     */
    public ChatClient(String ip,int port)
    {
        // Instanzvariable initialisieren

        myICI = new ICI(ip,port); // von der zukünftigen Verbinung sind ip und port bekannt
        anwendungsschicht = new ChatAnwendungsschicht(this);

        benutzer = fragNachBenutzer("");
        try{
            anwendungsschicht.VerbindungsaufbauREQ(myICI,null);
        } catch (ConnectionException e){
            System.err.println("Verbindung fehlgeschlagen. IP-Adresse ?");
            e.printStackTrace();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Zeigt eine Textübertragung an und gibt diese auf der Anzeigefenster aus.
     * @param ici Verbindung
     * @param sdu In <code>sdu.text</code> wird der Text übergeben
     */
    public synchronized void TextIND(ICI ici, SDU sdu){
        System.out.println("Client: TextIND("+ici.socket.toString()+","+sdu.text+")");

        Color c = null;

        if (sdu.red!=SDU.NULLCOLOR){
            c = new Color(sdu.red,sdu.green,sdu.blue);
        } else {
            c = new Color(0,0,0);
        }

        anzeige.show(sdu.text,c);
    }

    /**
     * Der Verbindungsaufbau wurde abgeschlossen. <p>
     * Wenn die Verbindung erfolgreich aufgebaut wurde gilt: <code>ici.socket!=null</code> <p>
     * Ansonsten wurde keine Verbindung aufgebau.
     * @param ici Verbindung
     * @param sdu wird nicht genutzt
     */
    public synchronized void VerbindungsaufbauCONF(ICI ici, SDU sdu){
        myICI = ici;
        if (myICI.socket == null){
            // es ist keine Verbindung zustande gekommen
            System.out.println("Client: VerbindungsaufbauCONF("+"null"+","+"––"+")");
            System.out.println("Client: Die Verbindung zum Server ist nicht zustande gekommen.");
            close();
        } else {
            System.out.println("Client: VerbindungsaufbauCONF("+ici.socket.toString()+","+"––"+")");
            try{
                anwendungsschicht.NickAnmeldungREQ(ici,new SDU(benutzer.name,benutzer.r,benutzer.g,benutzer.b));
            }  catch (Exception e){
                e.printStackTrace();

            }

        }

        String serverVersion = sdu.text;

        if (!serverVersion.equals(GLOBAL.VERSION)) {
            System.err.println("Client: unbekannte Serverversion: Verbindung wird abgebaut.");
            try{
                anwendungsschicht.VerbindungsabbauAnfrageREQ(myICI, new SDU(""));
            }  catch (Exception e){
                e.printStackTrace();

            }
        } else{
            System.out.println("Client: ServerVersion ist :"+serverVersion);

        }

    }

    /**
     * Der Verbindungsaufbau wurde abgeschlossen. <p>
     * Wenn die Verbindung erfolgreich aufgebaut wurde gilt: <code>ici.socket!=null</code> <p>
     * Ansonsten wurde keine Verbindung aufgebau.
     * @param ici Verbindung
     * @param sdu wird nicht genutzt
     */
    public synchronized void NickAnmeldungCONF(ICI ici, SDU sdu){
        System.out.println("Client: NickAnmeldungCONF("+ici.socket.toString()+","+sdu.text+")");

        Antwort antwort = Antwort.valueOf(sdu.text);

        switch(antwort){
            case ACK: 
                anzeige = new Anzeigefenster("Client: "+GLOBAL.VERSION+":"+benutzer.name);
                eingabe = new Eingabefenster(this,benutzer.name+":");
                eingabe.start(); // startet den Thread des Eingabefensters
                break;
            case REJECT: 
                benutzer = fragNachBenutzer("Name abgelehnt. Eingabeformat >> ");
                try
                {
                    anwendungsschicht.NickAnmeldungREQ(ici,new SDU(benutzer.name,benutzer.r,benutzer.g,benutzer.b));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
        }

    }

    /**
     * Der Verbindungsabbau wurde veranlasst. <p>
     * Der Client wird geschlossen.
     * @param ici wird nicht genutzt
     * @param sdu wird nicht genutzt
     */
    public synchronized void VerbindungsabbauIND(ICI ici, SDU sdu){
        if (ici.socket != null){
            System.out.println("Client: VerbindungsabbauIND("+ici.socket.toString()+","+"––"+")");
        }

        close();
    }

    /**
     * Meldet eine Nachricht am Server an. Sollte die Nachricht aus dem Wort "Ende" bestehen, so wird zusätzlich ein Verbindungsabbau angefragt.
     * @para text Nachricht, die an den Server geschickt wird.
     */
    public synchronized void send(String text){
        System.out.println("Client: send("+text+")");

        // wenn eine Verbindung besteht
        if (myICI != null){
            SDU sdu = new SDU(text,benutzer.r,benutzer.g,benutzer.b); 
            try{
                anwendungsschicht.TextAnmeldenREQ(myICI,sdu); // fordere einen Textwunsch beim Server an
            } catch(Exception e){
                e.printStackTrace();
                System.out.println("ChatClient (Text): unknown Error, ChatSystem will shut down.");
                close();
            }

            // wenn es einen text gibt und dieser Ende heißt
            if (text != null && text.equalsIgnoreCase("ENDE")){
                System.out.println("Client: ENDE erkannt.");
                try {
                    anwendungsschicht.VerbindungsabbauAnfrageREQ(myICI,new SDU("")); // fordere einen Verbindungsabbauwunsch beim Server an
                } catch(Exception e){
                    e.printStackTrace();
                    System.out.println("ChatClient (Abbau): unknown Error, ChatSystem will shut down.");
                    close();
                }
            }
        }

    }

    /**
     * Schließt das Programm und alle gestarteten Threads.
     */
    public synchronized  void close(){
        System.out.println("Client: close("+")");
        isActive = false;

        anwendungsschicht.close();
        eingabe.close();
        anzeige.close();
        System.out.println("Client-Programm beendet."); // Beachte: andere Threads können später beendet sein
    }

    private Benutzer fragNachBenutzer(String fehlerMeldung){
        Benutzer ret = null;

        boolean ok = false;

        while (!ok){
            String antwort = frage(fehlerMeldung+"Name:rot:grün:blau");
            String[] strArray = antwort.split(":",4);
            if (strArray.length == 4){
                String name = strArray[0];
                int r = Integer.parseInt(strArray[1]);
                int g = Integer.parseInt(strArray[2]);
                int b = Integer.parseInt(strArray[3]);

                System.out.println("Client: fragNachBenutzer: "+name+" ("+r+","+g+","+b+")");

                if ((0>r) || (r>255)) r = 120;
                if ((0>g) || (g>255)) g = 120;
                if ((0>b) || (b>255)) b = 120;

                ret = new Benutzer(name,r,g,b);
                System.out.println("Client: fragNachBenutzer: Benutzer: "+ret.toString());
                ok = true;
            }
        }

        return ret;
    }

    private String frage(String titel){
        return JOptionPane.showInputDialog(new JFrame(),titel);
    }

}
