import java.io.*;
/**
 * (C) Copyright 2000-2014 SAP AG Walldorf
 *
 * Author:  SAP AG, Security Development
 * 
 * SAP AG DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE,
 * INCLUDING ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO
 * EVENT SHALL SAP AG BE LIABLE FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL
 * DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 * PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS
 * ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE
 * OF THIS SOFTWARE.
 * 
 * This class provides wrapper functionality for SSO2Ticket 
 * (SAP Logon/Assertion Ticket) in Java.
 * 
 * @version 1.10 2014
 * 
 */

public class SSO2Ticket
{
    public static final int ISSUER_CERT_SUBJECT    = 0;
    public static final int ISSUER_CERT_ISSUER     = 1;
    public static final int ISSUER_CERT_SERIALNO   = 2;
    public static final int ISSUER_CERT_SUMMARY    = 3;
    public static final int ISSUER_CERT_SERIAL     = 4;
    public static final int ISSUER_CERT_VALIDITY   = 5;
    public static final int ISSUER_CERT_FINGERPRINT= 6;
    public static final int ISSUER_CERT_ALGID      = 7;
    public static final int ISSUER_CERT_ALL        = 8;
		
    private static boolean initialized = false;
    public static String SECLIBRARY ;
    public static String SSO2TICKETLIBRARY = "sapssoext";
    
    static {
        if (System.getProperty("os.name").startsWith("Win"))  {
            SECLIBRARY = "sapcrypto.dll";       /* Windows */
        } else if (System.getProperty("os.name").startsWith("Mac"))  {
            SECLIBRARY = "libsapcrypto.jnilib"; /* Mac */
        } else if (System.getProperty("os.name").startsWith("HP"))  {
            if (false == System.getProperty("os.arch").startsWith("IA64")) {
                SECLIBRARY = "libsapcrypto.sl"; /* HP RISC */
            } else {
            	SECLIBRARY = "libsapcrypto.so"; /* HP IA64 */
            }
        } else {
            SECLIBRARY = "libsapcrypto.so";     /* Default for Linux/Unix */
        }
        try {
            System.loadLibrary(SSO2TICKETLIBRARY);
            System.out.println("SAPSSOEXT loaded."); 
        } catch (Throwable e) {
            System.out.println ("Error during initialization of SSO2TICKET:\n" + e.getMessage());
        }
        System.out.println("static part ends.\n");
    }

    
    /**
     * Initialization
     * 
     * @param seclib location of SSF-implementation
     * 
     * @return true/false whether initialization was OK
     */
    private static native synchronized boolean init(String seclib);

    /**
     * Returns internal version.
     * 
     * @return version
     */
    public static native synchronized String getVersion();
    
    /**
     * eval logon ticket
     * 
     * @param ticket        the ticket
     * @param pab           location of PAB
     * @param pab_password  password for access the PAB
     * 
     * @return Object array with:
     *         [0] = (String)user, [1] = (String)sysid, [2] = (String)client , [3] = (byte[])certificate
     *         [4] = (String)portalUser, [5] = (String)authSchema, [6] = validity, [7] = ticket type (0: logon, 1: assertion)
     *  
     */
    public static native synchronized Object[] evalLogonTicket(
        String ticket,
        String pab,
        String pab_password)
        throws Exception;
    
    /**
     * eval assertion ticket
     * 
     * @param ticket        the ticket
     * @param pab           location of PAB
     * @param pab_password  password for access the PAB
     * @param my_sysid      Own System Id
     * @param my_client     Own System Client
     * 
     * @return Object array with:
     *         [0] = (String)user, [1] = (String)sysid, [2] = (String)client , [3] = (byte[])certificate
     *         [4] = (String)portalUser, [5] = (String)authSchema, [6] = validity
     *  
     */
    public static native synchronized Object[] evalAssertionTicket(
        String ticket,
        String pab,
        String pab_password,
        String my_sysid,
        String my_client)
        throws Exception;    

    /**
     * create a new assertion ticket
     * @param my_sysid      own system id
     * @param my_client     own system client
     * @param pab           location of PAB
     * @param pab_password  password for access the PAB
     * @param ext_sysid     Recipient System Id
     * @param ext_client    Recipient System Client
     * @param sap_user      SAP user in recipient system
     * @param language      SAP language
     * @param portal_user   Portal user in recipient system
     * @param auth_schema   Authentication Schema
     * 
     * @return String of new Assertion Ticket
     *  
     */
    public static native synchronized String createAssertionTicket(
        String my_sysid,
        String my_client,
        String pab,
        String pab_password,
        String ext_sysid,
        String ext_client,
        String sap_user,
        String language,
        String portal_user,
        String auth_schema)
        throws Exception;

    /**
     * Load a key file into the memory of SAPSSOEXT
     * @param keyFile   	 byte array of key
     * @param key_password	 password of key
     * @param index              index of key if several keys in keyFile
     * @param type               type of key
     * 
     * @return true/false whether load key was ok
     *  
     */
    public static native synchronized boolean loadKey( byte[] keyFile, String key_password, int index , int type );
        

    /**
     * Parse certificate
     * @param cert 			Certificate received from evalLogonTicket
     * @param info_id       One of the request ids
     * 
     * @return Info string from certificate
     *  
     */
    public static native synchronized String parseCertificate(
        byte[] cert,
        int info_id);
        
    /**
     * Get SAPSSOEXT property
     * @param name   property name to be retrieved
     * 
     * @return property string from SAPSSOEXT
     *  
     */
    public static native synchronized String getProperty( String name );
    
    /**
     * Set SAPSSOEXT property
     * @param name   property name to be set
     * @param value  property value to be set
     * 
     * @return true/false whether set was OK
     *  
     */
    public static native synchronized boolean setProperty( String name, String value );
    
        
    public static void main(String[] args) throws Exception
    {
            byte[] certificate = null;
            byte[] keyfile = null;
            int tType = -1;
            String ticket = null;
            String pab = null;
            String pwd = null;
            String crt = null;
            String ssf_library = null;
            Object o[] = null;
    
            try {			
                // plausi check
                if(getCommandParam(args,"-i") == null)
                {
                    PrintHelp();
                    return;
                }

	        System.out.println("Start SSO2TICKET main");        
	        System.out.println("-------------- test version --------------");
	        String version =SSO2Ticket.getVersion();
	        System.out.println("Version of SAPSSOEXT: " + version);
	        if(false == getCommandOption(args,"-c")) {
	            // read ticket into a String
        	    ticket = getTicket(getCommandParam(args,"-i"));
	        } else {
	            ticket = "";
	        }
        	// get PAB (public key) of issuing system
        	pab = getFullFilePath(getCommandParam(args,"-p"));
        	crt = getCommandParam(args,"-crt");
        	if(pab == null && crt == null) {
        	   PrintHelp();
        	   return;	
        	} else {
        	   if(pab != null)
        	      keyfile = getBytesFromFile(pab);
        	   else
        	      keyfile = getBytesFromFile(crt);
        	}
        	// get PSE password
        	pwd = getCommandParam(args,"-pwd");
	        // init sapsecu library
	        ssf_library = getCommandParam(args,"-L");
	        if(ssf_library==null)
	          ssf_library = SECLIBRARY;
		
	        if(getCommandParam(args,"-t")!=null) {
	           setProperty("SAP_EXT_TRC", getCommandParam(args,"-t"));
	           setProperty("SAP_EXT_TRL", getCommandParam(args,"-l"));
	        }
	        if(getCommandParam(args,"-mysid")!=null) {
		           setProperty("SAP_OWN_SYSID", getCommandParam(args,"-mysid"));
	        }
	        if(getCommandParam(args,"-mycli")!=null) {
		           setProperty("SAP_OWN_CLIENT", getCommandParam(args,"-mycli"));
	        }
	        if(getCommandParam(args,"-exsid")!=null) {
		           setProperty("SAP_EXT_SYSID", getCommandParam(args,"-exsid"));
	        }
	        if(getCommandParam(args,"-excli")!=null) {
		           setProperty("SAP_EXT_CLIENT", getCommandParam(args,"-excli"));
	        }
	        if( !init(ssf_library)) {
	        	System.out.println ("Could not load library: " + ssf_library);
	        	return;
	        }
	        // load ticket key
	        loadKey(keyfile,pwd,0,pab!=null?0:1);
	        // create assertion ticket
	        if(getCommandOption(args,"-c")) {
	           String ticket_user = getCommandParam(args,"-user");
	           if(ticket_user == null) ticket_user = "SAPUSER";
	           ticket = createAssertionTicket(getProperty("SAP_OWN_SYSID"), 
	                                          getProperty("SAP_OWN_CLIENT"), 
	                                          null, null,
	                                          getProperty("SAP_EXT_SYSID"),
	                                          getProperty("SAP_EXT_CLIENT"),
	                                          ticket_user, "E", "PORTALUSER", "basicauthentication");
	           BufferedWriter out = new BufferedWriter(new FileWriter(getCommandParam(args,"-i")));
	           out.write(ticket);
	           out.close();
			   if(getCommandParam(args,"-host")!=null && getCommandParam(args,"-sysnr")!=null) {
			      createSAPShortCut(getProperty("SAP_EXT_SYSID"),getProperty("SAP_EXT_CLIENT"),
		                            getCommandParam(args,"-host"),getCommandParam(args,"-sysnr"),
									ticket_user,ticket);
			   }
	        }
            // evaluate the ticket
            if(getCommandOption(args,"-A")) {
                 o = evalAssertionTicket(ticket, null, null, getProperty("SAP_EXT_SYSID"), getProperty("SAP_EXT_CLIENT"));
            } else {
                o = evalLogonTicket(ticket, null , null);
            }
            // use 3rd object to analyze the certificate
            if (o[3] != null &&
                o[3] instanceof byte[]
            ) {     
                certificate = (byte[])o[3];          		
                   //System.out.println("Certificate length     : " + certificate.length + " bytes");
                   /*
                    * remark: The "certificate" object is a DER encoded X.509 certificate
                    *         of the issuing system, which can be parsed/analyzed with JAVA  
                    *         functionality e.g. Java Cryptography Architecture API, IAIK and so on.
                    */
            }// or
            if (o[7] != null && 
                o[7] instanceof String ) {
                String sType = (String)o[7];
                tType = Integer.parseInt(sType);
            }
            
            // print out all parameters received from SAPSSOEXT
            PrintResults(tType,
                        (String)o[0],
                        (String)o[1],
                        (String)o[2],
                        parseCertificate(certificate,ISSUER_CERT_SUBJECT),
                        parseCertificate(certificate,ISSUER_CERT_ISSUER),
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
	static void PrintResults(int ticketType, String user, String sysid, String client, 
	String subject, String issuer, String ticket, String prtUsr, String authS, String validity) 
	{
	    System.out.println("***********************************************");
	    System.out.println(" Output of program:");
	    System.out.println("***********************************************");
	    System.out.println("\n");
	    System.out.println("The ticket\n\n" + ticket + "\n");
	    System.out.println("was successfully validated.");
	    System.out.println("Type     : " + ((ticketType==1)?"SAP Assertion Ticket":"SAP Logon Ticket"));
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
	public static String getTicket(String filename) throws FileNotFoundException {
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

	// create a SAPShortCute for SAPGui logon
	public static void createSAPShortCut( String sysId, String client,
			                              String host, String sysnr,
			                              String user, String ticket) throws FileNotFoundException {
	    try {
	        BufferedWriter out = new BufferedWriter(new FileWriter("ticket.sap"));
			out.write("[System]\nName=");
			out.write(sysId);
			out.write("\nClient=");
			out.write(client);
			out.write("\nGuiParm=/H/");
			out.write(host);
			out.write("/S/32");
			out.write(sysnr);
			out.write("\n[User]\nName=");
			out.write(user);
			out.write("\nat=\"MYSAPSSO2=");
			out.write(ticket);
			out.write("\"\n[Function]\nTitle=TEST\nCommand=SU3\n[Configuration]\nWorkDir=\n[Options]\nReuse=0\n");
	        out.close(); 
	        return;
	    }
	    catch (Exception e) 
	    {
	        // Let the user know what went wrong.
	        System.out.println("The file could not be written:");
	        System.out.println(e.getMessage());
	        throw new FileNotFoundException("File ticket.sap could not be written");
	    }

	}

	// read the key file 
	public static byte[] getBytesFromFile(String filename) throws IOException {
	        File file = new File(filename);
	        InputStream is = new FileInputStream(file);
	    
	        // Get the size of the file
	        long length = file.length();
	    
	        if (length > Integer.MAX_VALUE) {
	            // File is too large
	        }
	    
	        // Create the byte array to hold the data
	        byte[] bytes = new byte[(int)length];
	    
	        // Read in the bytes
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        // Ensure all the bytes have been read in
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+file.getName());
	        }
	    
	        // Close the input stream and return bytes
	        is.close();
	        return bytes;
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

	// parse the arguments for existence of an option
	static boolean getCommandOption(String[] args, String option)
	{
	    for(int i=0; i<args.length; i++) 
            {
                if(args[i].equals(option))
                {
                    return true;
                }
            }
	    return false;
	}

	// print help to console
	static void PrintHelp()
	{
		System.out.println("   java SSO2Ticket -i <ticket_file> [-c (create assertion ticket)] ");
		System.out.println("   [-host <host>] [-sysnr <system number>] (create SAP Shortcut if -c, -host, -sysnr provided)");
		System.out.println("   [-A (validate assertion ticket)] [-L <SSF_LIB>] [-user <ticket user>]");
		System.out.println("   [-mysid <ticket creator id>] [-mycli <ticket creator client>] ");
		System.out.println("   [-exsid <ticket recipient id>] [-excli <ticket recipient client>] ");
		System.out.println("   [-p <file containing PSE>] [-pwd <PSE password>]");
		System.out.println("   [-crt <file containing public certificate>] "); 
		System.out.println("   [-t <developer trace file>] [-l <level of trace (1, 2 or 3)>]");
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
