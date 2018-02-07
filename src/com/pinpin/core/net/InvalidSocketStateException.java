/**
 * 
 */
package com.pinpin.core.net;

/**
 * @author lixd186
 *
 */
public class InvalidSocketStateException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6672312543045731264L;

	public InvalidSocketStateException(){
        super();
    }

    public InvalidSocketStateException( String desc){
        super(desc);
    }

    public InvalidSocketStateException(String desc, Throwable cause){
        super(desc, cause);
    }

    public InvalidSocketStateException(Throwable cause){
        super(cause);
    }

    /**
     * Since we override this method, no stacktrace is generated - much faster
     * @return always null
     */
    public Throwable fillInStackTrace() {
        return null;
    }

}
