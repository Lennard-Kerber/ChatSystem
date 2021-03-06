
/**
 * Eine Protocol Data Unit (PDU) wird zwischen den Partnerinstanzen ausgetauscht. 
 * Sie enthält strukturiert einen Header und die Nutzlast (SDU).
 * 
 * Der Header speichert die Kontrollinformationen für die aktuelle Schicht.
 * 
 * Eine PDU kann die Struktur auch aufgeben und Headeer und SDU in einer Zeichenkette kodieren.
 * Bei der Erzeugung kann aus einer unstrukturierten Zeichenkette die Darstellung in Header und SDU erstellt werden.
 * 
 * @author LK
 * @version 2021-09-30
 */
public class PDU
{

    // Instanzvariablen
    public Header header;
    public SDU sdu;

    /**
     * Konstruktor für Objekte der Klasse PDU
     * @param header Header der Nachricht
     * @param sdu Datenteil der Nachricht
     */
    public PDU(Header header, SDU sdu)
    {
        // Instanzvariable initialisieren
        this.header = header;
        this.sdu = sdu;
    }
    
    
    /**
     * Konstruktor für Objekte der Klasse PDU
     * Die Daten werden als Header und SDU gespeichert.
     * @param pdu gesamte PDU der Nachricht mit Header und Datenteil, getrennt von einem Doppelpunkt
     */
    public PDU(String pdu)
    {
        String[] strArray = pdu.split(":",2);

        // Instanzvariable initialisieren
        if (strArray.length == 2) {
            this.header = Header.valueOf(strArray[0]);
            this.sdu    = SDU.valueOf(strArray[1]);
        } else {
            this.header = Header.NullHeader;
            this.sdu    = null;
        }
    }

    /**
     * Wandelt den gespeicherten Header und SDU in eine PDU um. 
     * Die PDU besteht aus dem Header gefolgt von einem Doppelpunkt und schließt mir der SDU ab.
     * return PDU
     */
    String getPDU(){
        return header.toString()+":"+sdu.toString();
    }
    
}
