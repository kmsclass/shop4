package exception;

//RuntimeException : 예외처리 생략 가능
public class CartEmptyException extends RuntimeException {
	
	private String url;
	public CartEmptyException(String msg, String url) {
		super(msg); //getMessage()
		this.url = url;
	}
	public String getUrl() {
		return url;
	}
}
