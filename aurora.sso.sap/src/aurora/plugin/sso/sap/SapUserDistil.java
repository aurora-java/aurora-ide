package aurora.plugin.sso.sap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import uncertain.logging.ILogger;

import com.mysap.sso.SSO2Ticket;

public class SapUserDistil {

	public String execute(HttpServletRequest request, String verifyPseFile,
			ILogger logger) throws Exception {
		// java SSO2Ticket -i ticket.txt -crt SAPLogonTicketKeypair-cert.cert
		logger.info("parse sap cookie.");
		int ISSUER_CERT_SUBJECT = 0;
		int ISSUER_CERT_ISSUER = 1;
		int ISSUER_CERT_SERIALNO = 2;

		String sso = "MYSAPSSO2";
		String ticket = "";
		Cookie[] all_Cookies = request.getCookies();
		if (all_Cookies == null)
			return null;
		for (int i = 0; i < all_Cookies.length; i++) {
			// Get MYSAPSSO2 cookie from request context...
			if (sso.equals(all_Cookies[i].getName())) {
				ticket = all_Cookies[i].getValue();
				break;
			}
		}
		// if (ticket == null || ticket.equals("")){
		// ticket =
		// "AjExMDAgAA5wb3J0YWw6YW5ud2FuZ4gAE2Jhc2ljYXV0aGVudGljYXRpb24BAAdBTk5XQU5HAgADMDAwAwADUFAxBAAMMjAxNDA0MzAwNjI1BQAEAAAADQoAB0FOTldBTkf%2FAQUwggEBBgkqhkiG9w0BBwKggfMwgfACAQExCzAJBgUrDgMCGgUAMAsGCSqGSIb3DQEHATGB0DCBzQIBATAiMB0xDDAKBgNVBAMTA1BQMTENMAsGA1UECxMESjJFRQIBADAJBgUrDgMCGgUAoF0wGAYJKoZIhvcNAQkDMQsGCSqGSIb3DQEHATAcBgkqhkiG9w0BCQUxDxcNMTQwNDMwMDYyNTU3WjAjBgkqhkiG9w0BCQQxFgQUO47rVk2n%2BziiW1BxmsBtk579p5AwCQYHKoZIzjgEAwQvMC0CFHxmhZ4XwG3RvNiZaqwWPy4Gg6s6AhUAwgCWpMt3QlfGOe%2B8xTgDGEIycn8%3D";
		// }
		// If no ticket present we output an error page
		if (ticket.equals(""))
			return null;
		Object[] o = null;
		// Validate logon ticket.
		logger.info("ticket:" + ticket);

		o = evalLogonTicket(verifyPseFile, ticket, logger);

		if (o == null)
			return null;

		String user = (String) o[0]; // First element is the SAP system user
		String Sysid = (String) o[1]; // Second element is the id of the
		// issuing system
		String Client = (String) o[2]; // Third element is the client of
		// the issuing system
		String Subject = (String) SSO2Ticket.parseCertificate((byte[]) o[3],
				ISSUER_CERT_SUBJECT); // Portal user
		String issuer = (String) SSO2Ticket.parseCertificate((byte[]) o[3],
				ISSUER_CERT_ISSUER);
		String PrtUsr = (String) o[4]; // Portal user
		logger.info("PrtUsr:" + PrtUsr);
		String authS = (String) o[5]; // Portal auths
		String validity = (String) o[6]; // Portal auths
		if (validity.equals("0"))
			return null;

		return PrtUsr;
	}

	private Object[] evalLogonTicket(String verifyPseFile, String ticket,
			ILogger logger) throws Exception {
		Object[] o = null;
		if (verifyPseFile.endsWith(".pse")) {
			o = SSO2Ticket.evalLogonTicket(ticket, verifyPseFile, null);
		} else if (verifyPseFile.endsWith(".cert")) {
			o = certEvalLogonTicket(verifyPseFile, ticket, logger);
		} else {
			o = certEvalLogonTicket(verifyPseFile, ticket, logger);
		}
		return o;
	}

	public Object[] certEvalLogonTicket(String cert, String ticket,
			ILogger logger) throws Exception {

		// java SSO2Ticket -i ticket.txt -crt SAPLogonTicketKeypair-cert.cert
		byte[] keyfile = null;
		String pab = null;
		String pwd = null;
		String crt = null;
		Object o[] = null;

		try {
			crt = cert;
			keyfile = SSO2Ticket.getBytesFromFile(crt);
			// load ticket key
			SSO2Ticket.loadKey(keyfile, pwd, 0, pab != null ? 0 : 1);
			// evaluate the ticket
			o = SSO2Ticket.evalLogonTicket(ticket, null, null);
		} catch (Exception e) {
			logger.info(e.getCause().getMessage());
		} catch (Throwable te) {
			logger.info(te.getCause().getMessage());
		}
		return o;
	}

}
