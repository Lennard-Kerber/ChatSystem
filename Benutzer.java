
/**
 * Beschreiben Sie hier die Klasse Benutzer.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Benutzer
{
    public static Benutzer valueOf(String str){

        String n     = "";
        int r        = 0;
        int g        = 0;
        int b        = 0;
        String[] strArray = str.split("$",4);        
        if (strArray.length == 4){
            n        = strArray[0];
            r        = Integer.parseInt(strArray[1]);
            g        = Integer.parseInt(strArray[2]);
            b        = Integer.parseInt(strArray[3]);
        }
        return new Benutzer(n,r,g,b);
    }

    // Instanzvariablen - ersetzen Sie das folgende Beispiel mit Ihren Variablen
    public String name;
    public int r;
    public int g;
    public int b;

    /**
     * Konstruktor für Objekte der Klasse Benutzer
     */
    public Benutzer(String name, int r, int g, int b)
    {
        // Instanzvariable initialisieren
        this.name = name;
        this.r=r;
        this.g=g;
        this.b=b;
    }

    public String toString(){
        return name+"$"+r+"$"+g+"$"+b+")";
    }
}
