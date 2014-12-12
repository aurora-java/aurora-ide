package com.mysap.sso;
import java.io.*;
public class SSO2Ticket_old
{
    public static final int ISSUER_CERT_SUBJECT = 0;
    public static final int ISSUER_CERT_ISSUER = 1;
    public static final int ISSUER_CERT_SERIALNO = 2;
		
    private static boolean initialized = false;
    public static String SECLIBRARY ;
    public static String SSO2TICKETLIBRARY = "sapssoext";
    
    static {
        if (System.getProperty("os.name").startsWith("Win"))  {
            SECLIBRARY = "sapsecu.dll";
        } else {
            SECLIBRARY = "libsapsecu.so";
        }
        try {
            System.loadLibrary(SSO2TICKETLIBRARY);  
            System.out.println("SAPSSOEXT loaded."); 
			init(SECLIBRARY); 
        } catch (Throwable e) {
            System.out.println ("Error during initialization of SSO2TICKET:\n" + e.getMessage());
        }
        System.out.println("static part ends.\n");
    }

    
    /**
     * Initialization
     * 
     * @param seclib location of ssf-implemenation
     * 
     * @return true/false whether initailisation was ok
     */
    public static native synchronized boolean init(String seclib);

    /**
     * Returns internal version.
     * 
     * @return version
     */
    public static native synchronized String getVersion();
    
    /**
     * eval ticket
     * 
     * @param ticket        the ticket
     * @param pab           location of pab
     * @param pab_password  password for access the pab
     * 
     * @return Object array with:
     *         [0] = (String)user, [1] = (String)sysid, [2] = (String)client , [3] = (byte[])certificate
     *         [4] = (String)portalUser, [5] = (String)authSchema, [6] = validity
     *  
     */
    public static native synchronized Object[] evalLogonTicket(
        String ticket,
        String pab,
        String pab_password)
        throws Exception;
    

    /**
     * Parse certificate
     * @param cert 			Certificate received from evalLogonTicket
     * @param info_id       One of the requst id磗
     * 
     * @return Info string from certificate
     *  
     */
    public static native synchronized String parseCertificate(
        byte[] cert,
        int info_id);
        
    public static void main(String[] args) throws Exception
    {
		byte[] certificate;
		String ticket;
		String pab;
		String pwd;
		String ssf_library;
		
		try {			
			// plausi check
			if(getCommandParam(args,"-i") == null)
			{
				PrintHelp();
				return;
			}
						
	        System.out.println("Start SSO2TICKET main");        
	        System.out.println("-------------- test version --------------");
	        String version =SSO2Ticket_old.getVersion();
	        System.out.println("Version of SAPSSOEXT: " + version);
            // read ticket into a String
        	ticket = getTicket(getCommandParam(args,"-i"));
        	// get PAB (public key) of issuing system
        	pab = getFullFilePath(getCommandParam(args,"-p"));
        	// get PSE password
        	pwd = getCommandParam(args,"-pwd");
			// init sapsecu library
			ssf_library = getCommandParam(args,"-L");
			if(ssf_library==null)
			   ssf_library = SECLIBRARY;
			   
			if( !init(ssf_library)) {
				System.out.println ("Could not load library: " + ssf_library);
				return;
			}			
			// evaluate the ticket
            Object o[] = evalLogonTicket(ticket, pab!=null?pab:"SAPdefault" , pwd);

			// use 3rd object to analyse the certificate
            if (o[3] != null &&
                o[3] instanceof byte[]
               ) {     
                   certificate = (byte[])o[3];          		
                   //System.out.println("Certificate length     : " + certificate.length + " bytes");
                   /*
                    * remark: The "certificate" object is a DER encoded X.509 certificate
                    *         of the issuing system, which can be parsed/analysed with JAVA  
                    *         funtionality e.g. Java Cryptography Architecture API, IAIK and so on.
                    */
            }// or
            
            // print out all parameters received from SAPSSOEXT
            PrintResults((String)o[0],
            			 (String)o[1],
            			 (String)o[2],
						  parseCertificate((byte[])o[3],ISSUER_CERT_SUBJECT),
						  parseCertificate((byte[])o[3],ISSUER_CERT_ISSUER),
						  ticket,
						  (String)o[4],
						  (String)o[5],
						  (String)o[6]);
            
        } catch (Exception e) {
            System.out.println(e);
        } catch (Throwable te) {
              System.out.println(te);
        }
    }
    
	// print the parameters from ticket
	static void PrintResults(String user, String sysid, String client, 
	String subject, String issuer, String ticket, String prtUsr, String authS, String validity) 
	{
		System.out.println("***********************************************");
		System.out.println(" Output of program:");
		System.out.println("***********************************************");
		System.out.println("\n");
		System.out.println("The ticket\n\n" + ticket + "\n");
		System.out.println("was successfully validated.");
		System.out.println("User     : " + user);
		System.out.println("Ident of ticket issuing system:");
		System.out.println("Sysid    : " + sysid);
		System.out.println("Client   : " + client);
		System.out.println("External ident of user:");
		System.out.println("PortalUsr: " + prtUsr);
		System.out.println("Auth     : " + authS);
		System.out.println("Ticket validity in seconds:");
		System.out.println("Valid (s): " + validity);
		System.out.println("Certificate data of issuing system:");
		System.out.println("Subject  : " + subject);
		System.out.println("Issuer   : " + issuer);
		System.out.println("\n");
	}
	
	// read the ticket string from a File
	public static String getTicket(String filename) 
	throws FileNotFoundException
	{
		try {
		   BufferedReader in = new BufferedReader(new FileReader(filename));
		   String str;
		   StringBuffer strBuffer = new StringBuffer();
		   while ((str = in.readLine()) != null) {
		   	strBuffer.append(str);
		   }
		   in.close(); 
		   return strBuffer.toString();
		}
		catch (Exception e) 
		{
			// Let the user know what went wrong.
			System.out.println("The file could not be read:");
			System.out.println(e.getMessage());
			throw new FileNotFoundException("File "+ filename +" could not be read");
		}
            
	}	
	
	// parse the arguments for an option
	static String getCommandParam(String[] args, String option)
	{
		for(int i=0; i<args.length; i++) 
		{
			if(args[i].equals(option) && args.length > i+1)
			{
				return args[i+1];
			}
		}
		return null;
	}
	
	// print help to console
	static void PrintHelp()
	{
		System.out.println("   java SSO2Ticket -i <ticket_file> [-L <SSF_LIB>]");
		System.out.println("   [-p <file containing public key>] [-pwd <PSE password>]");         
	}
	
	// get the full path to a file
	static String getFullFilePath(String filename) 
	throws FileNotFoundException
	{
		if(filename==null)
		   return null;
		String path;
		File file = new File(filename);

		if( file.getAbsolutePath().toLowerCase().indexOf(".pse") > 0 )
		{
			path = file.getAbsolutePath();
		}
		else 
		{
			path = file.getAbsolutePath() + ".pse";
		}
		if( ! new File(path).exists() )
			throw new FileNotFoundException("File "+ filename +" does not exists");
		return path;            
	}
}
