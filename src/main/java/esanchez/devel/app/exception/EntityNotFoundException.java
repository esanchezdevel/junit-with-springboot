package esanchez.devel.app.exception;

public class EntityNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 7811276467297404258L;

	public EntityNotFoundException(String message) {
		super(message);
	}
}
