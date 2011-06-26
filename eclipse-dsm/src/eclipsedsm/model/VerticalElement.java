package eclipsedsm.model;

import java.util.HashMap;
import java.util.Map;

public class VerticalElement extends Element<VerticalElement> {
	private final Map<HorizontalElement, Integer> values = new HashMap<HorizontalElement, Integer>();

	public VerticalElement(String name) {
		super(name);
	}

	public Map<HorizontalElement, Integer> getValues() {
		return values;
	}

}
