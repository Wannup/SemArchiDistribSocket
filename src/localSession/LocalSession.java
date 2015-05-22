package localSession;

import java.util.HashMap;
import java.util.Map;

public class LocalSession implements ISession {

	private Map<String, String> session;

	public LocalSession() {
		this.session = new HashMap<String, String>();
	}

	@Override
	public void setAttribute(String key, String value) {
		this.session.put(key, value);
	}

	@Override
	public String getAttribute(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void invalidate() {
		// TODO Auto-generated method stub

	}
}
