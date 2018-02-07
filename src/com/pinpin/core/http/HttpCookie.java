/**
 * 
 */
package com.pinpin.core.http;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.pinpin.core.net.InetAddressUtil;

/**
 * @author tony
 * 
 */
public class HttpCookie {
	private static DateFormat expiresFormat1 = new SimpleDateFormat(
			"E, dd MMM yyyy k:m:s 'GMT'", Locale.US);
	private static DateFormat expiresFormat2 = new SimpleDateFormat(
			"E, dd-MMM-yyyy k:m:s 'GMT'", Locale.US);

	String name;
	String value;
	URI uri;
	String domain;
	Date expires;
	String path;

	public HttpCookie(URI uri, String header) {
		String attributes[] = header.split(";");
		String nameValue = attributes[0].trim();
		this.uri = uri;
		this.name = nameValue.substring(0, nameValue.indexOf('='));
		this.value = nameValue.substring(nameValue.indexOf('=') + 1);
		this.path = "/";
		this.domain = uri.getHost();

		for (int i = 1; i < attributes.length; i++) {
			nameValue = attributes[i].trim();
			int equals = nameValue.indexOf('=');
			if (equals == -1) {
				continue;
			}
			String name = nameValue.substring(0, equals);
			String value = nameValue.substring(equals + 1);
			if (name.equalsIgnoreCase("domain")) {
				String uriDomain = uri.getHost();
				if (uriDomain.equals(value)) {
					this.domain = value;
				} else {
					if (!value.startsWith(".")) {
						value = "." + value;
					}
					uriDomain = uriDomain.substring(uriDomain.indexOf('.'));
					if (!uriDomain.equals(value)) {
						throw new IllegalArgumentException(
								"Trying to set foreign cookie");
					}
					this.domain = value;
				}
			} else if (name.equalsIgnoreCase("path")) {
				this.path = value;
			} else if (name.equalsIgnoreCase("expires")) {
				try {
					this.expires = expiresFormat1.parse(value);
				} catch (ParseException e) {
					try {
						this.expires = expiresFormat2.parse(value);
					} catch (ParseException e2) {
						throw new IllegalArgumentException(
								"Bad date format in header: " + value);
					}
				}
			}
		}
	}
	
	
	/**
     * Returns true if {@code cookie} should be sent to or accepted from {@code uri} with respect
     * to the cookie's path. Cookies match by directory prefix: URI "/foo" matches cookies "/foo",
     * "/foo/" and "/foo/bar", but not "/" or "/foobar".
     */
    static boolean pathMatches(HttpCookie cookie, URI uri) {
        String uriPath = matchablePath(uri.getPath());
        String cookiePath = matchablePath(cookie.getPath());
        return uriPath.startsWith(cookiePath);
    }
    
    /**
     * Returns a non-null path ending in "/".
     */
    private static String matchablePath(String path) {
        if (path == null) {
            return "/";
        } else if (path.endsWith("/")) {
            return path;
        } else {
            return path + "/";
        }
    }
    
    /**
     * Returns true if {@code host} matches the domain pattern {@code domain}.
     *
     * @param domainPattern a host name (like {@code android.com} or {@code
     *     localhost}), or a pattern to match subdomains of a domain name (like
     *     {@code .android.com}). A special case pattern is {@code .local},
     *     which matches all hosts without a TLD (like {@code localhost}).
     * @param host the host name or IP address from an HTTP request.
     */
    public static boolean domainMatches(String domainPattern, String host) {
        if (domainPattern == null || host == null) {
            return false;
        }

        String a = host.toLowerCase(Locale.US);
        String b = domainPattern.toLowerCase(Locale.US);

        /*
         * From the spec: "both host names are IP addresses and their host name strings match
         * exactly; or both host names are FQDN strings and their host name strings match exactly"
         */
        if (a.equals(b) && (isFullyQualifiedDomainName(a, 0) || InetAddressUtil.isNumeric(a))) {
            return true;
        }
        if (!isFullyQualifiedDomainName(a, 0)) {
            return b.equals(".local");
        }

        /*
         * Not in the spec! If prefixing a hostname with "." causes it to equal the domain pattern,
         * then it should match. This is necessary so that the pattern ".google.com" will match the
         * host "google.com".
         */
        if (b.length() == 1 + a.length()
                && b.startsWith(".")
                && b.endsWith(a)
                && isFullyQualifiedDomainName(b, 1)) {
            return true;
        }

        /*
         * From the spec: "A is a HDN string and has the form NB, where N is a
         * non-empty name string, B has the form .B', and B' is a HDN string.
         * (So, x.y.com domain-matches .Y.com but not Y.com.)
         */
        return a.length() > b.length()
                && a.endsWith(b)
                && ((b.startsWith(".") && isFullyQualifiedDomainName(b, 1)) || b.equals(".local"));
    }
    
    /**
     * Returns true if {@code s.substring(firstCharacter)} contains a dot
     * between its first and last characters, exclusive. This considers both
     * {@code android.com} and {@code co.uk} to be fully qualified domain names,
     * but not {@code android.com.}, {@code .com}. or {@code android}.
     *
     * <p>Although this implements the cookie spec's definition of FQDN, it is
     * not general purpose. For example, this returns true for IPv4 addresses.
     */
    private static boolean isFullyQualifiedDomainName(String s, int firstCharacter) {
        int dotPosition = s.indexOf('.', firstCharacter + 1);
        return dotPosition != -1 && dotPosition < s.length() - 1;
    }
    

	public boolean hasExpired() {
		if (expires == null) {
			return false;
		}
		Date now = new Date();
		return now.after(expires);
	}

	public String getName() {
		return name;
	}

	public URI getURI() {
		return uri;
	}
	
	public String getPath() {
		return path;
	}

	public boolean matches(URI uri) {

		if (hasExpired()) {
			return false;
		}
		
		String host = uri.getHost();

		return pathMatches(this, uri) && domainMatches(domain, host);
		
	}

	public String toString() {
		StringBuilder result = new StringBuilder(name);
		result.append("=");
		result.append(value);
		return result.toString();
	}
	public String toLog() {
		StringBuilder result = new StringBuilder(name);
		result.append("=");
		result.append(value);
		result.append("|uri");
		result.append("=");
		result.append(uri);
		return result.toString();
	}
}
