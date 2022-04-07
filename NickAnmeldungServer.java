
/**
 * Der Zustand VerbindungsaufbauWartendClient gilt nachdem die TCP-Verbindung hergestellt ist, 
 * der bestätigte Verbindungsaufbau aber auf die Bestätigung wartet.
 * 
 * Es können in diesem Zustand folgende Methoden ausgeführt werden: <p>
 * 
 * Client:
 * <ul>
 * <li> NickAnmeldungREQ
 * </ul>
 * <p>
 * Es gibt nur eine Instanz dieser Klasse, die mit dem Singelton-Pattern verwaltet wird.
 * 
 * @author LK
 * @version 2021-11-17
 */
public class NickAnmeldungServer extends ChatAnwendungsschichtZustand
{
    // Klassenvariablen
    private static NickAnmeldungServer singelton;
    
    // Klassenmethode
    /**
     * Gibt das Singelton zurück. Ein Singelton wird beim ersten Aufruf erzeugt.
     * @return Singelton
     */
    public static  NickAnmeldungServer getSingelton(){
        if (singelton == null){
            singelton = new NickAnmeldungServer();
        }
        return singelton;
    }
    // Instanzvariablen     
    // keine

    /**
     * Konstruktor für Objekte der Klasse Unverbunden
     */
    private NickAnmeldungServer()
    {
        super("NickAnmeldungServer"); // Aufruf des Konstruktors der ChatAnwendungsschichtZustand-Klasse
                              // Der Aufruf der Konstruktormethode des Oberklasse muss in der ersten Zeile des Konstruktors stehen.
    }
     
    
    /**
     * NickAnmeldungIND
     */
    public  synchronized void NickAnmeldungIND(ChatAnwendungsschicht kontext,ICI ici,SDU sdu) throws Exception
    {
        System.out.println("Server: NickAnmeldungServer -> NickWartendServer");
        kontext.nextState(ici,NickWartendServer.getSingelton());
        kontext.NickAnmeldungINDDO(ici,sdu);
    }

}
