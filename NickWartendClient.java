
/**
 * Der Zustand VerbindungsaufbauWartendClient gilt nachdem die TCP-Verbindung hergestellt ist, 
 * der bestätigte Verbindungsaufbau aber auf die Bestätigung wartet.
 * 
 * Es können in diesem Zustand folgende Methoden ausgeführt werden: <p>
 * 
 * Client:
 * <ul>
 * <li> VerbindungsaufbauCONF
 * </ul>
 * <p>
 * Es gibt nur eine Instanz dieser Klasse, die mit dem Singelton-Pattern verwaltet wird.
 * 
 * @author LK
 * @version 2021-11-17
 */
public class NickWartendClient extends ChatAnwendungsschichtZustand
{
    // Klassenvariablen
    private static NickWartendClient singelton;

    // Klassenmethode
    /**
     * Gibt das Singelton zurück. Ein Singelton wird beim ersten Aufruf erzeugt.
     * @return Singelton
     */
    public static  NickWartendClient getSingelton(){
        if (singelton == null){
            singelton = new NickWartendClient();
        }
        return singelton;
    }
    // Instanzvariablen     
    // keine

    /**
     * Konstruktor für Objekte der Klasse Unverbunden
     */
    private NickWartendClient()
    {
        super("NickWartendClient"); // Aufruf des Konstruktors der ChatAnwendungsschichtZustand-Klasse
        // Der Aufruf der Konstruktormethode des Oberklasse muss in der ersten Zeile des Konstruktors stehen.
    }

    /**
     * VerbindungsaufbauCONF
     */
    public  synchronized void NickAnmeldungCONF(ChatAnwendungsschicht kontext,ICI ici,SDU sdu) throws Exception
    {
        Antwort antwort = Antwort.valueOf(sdu.text);
        switch(antwort){
            case ACK: 
                System.out.println("Client: NickWartendClient -> VerbundenClient");
                kontext.nextState(ici,VerbundenClient.getSingelton());       
                break;
            case REJECT:
                System.out.println("Client: NickWartendClient -> NickAnmeldungClient");
                kontext.nextState(ici,NickAnmeldungClient.getSingelton());
                break;
        }
        kontext.NickAnmeldungCONFDO(ici,sdu);
    }

}
