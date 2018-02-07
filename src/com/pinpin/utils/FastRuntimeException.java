package com.pinpin.utils;

/**
 *
 * @author lixd186
 */
public class FastRuntimeException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8626551117283910368L;

	public FastRuntimeException(){
        super();
    }

    public FastRuntimeException( String desc){
        super(desc);
    }

    public FastRuntimeException(String desc, Throwable cause){
        super(desc, cause);
    }

    public FastRuntimeException(Throwable cause){
        super(cause);
    }

    /**
     * Since we override this method, no stacktrace is generated - much faster
     * @return always null
     */
    @Override
    public Throwable fillInStackTrace() {
        return null;
    }

}