
import java.net.Socket;
import java.util.*;

/**
 * Beschreiben Sie hier die Klasse BenutzerVerwaltung.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class BenutzerVerwaltung
{
    // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen

    HashMap<Socket,Benutzer> benutzerMap = new HashMap<Socket,Benutzer>();
    
    /**
     * Konstruktor für Objekte der Klasse BenutzerVerwaltung
     */
    public BenutzerVerwaltung()
    {
        // Instanzvariable initialisieren
    }

    public boolean add(Socket s, Benutzer neu){
        boolean found = false;
        for(Benutzer b : benutzerMap.values()){
            if (b.name.equalsIgnoreCase(neu.name)){
                found = true;
            }
        }
        if (!found){
            benutzerMap.put(s, neu);
        }
        
        return !found;
    }
    
    public Benutzer getBenutzer(Socket s){
        return benutzerMap.get(s);
    }
    
    public Benutzer removeBenutzer(Socket s){
        return benutzerMap.remove(s);
    }
    
    
}
