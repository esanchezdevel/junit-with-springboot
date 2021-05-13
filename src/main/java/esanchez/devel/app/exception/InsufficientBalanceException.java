package esanchez.devel.app.exception;

public class InsufficientBalanceException extends RuntimeException {

	private static final long serialVersionUID = -425946831316233521L;

	public InsufficientBalanceException(String message) {
		super(message);
	}
}
