package localSession;

public interface ISession {

	void setAttribute(String key, String value);
	String getAttribute(String key);
	void invalidate();
}
