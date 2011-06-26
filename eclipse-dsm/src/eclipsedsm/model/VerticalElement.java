package eclipsedsm.model;

import java.util.HashMap;
import java.util.Map;

public class VerticalElement extends Element<VerticalElement> {
	private final Map<HorizontalElement, Integer> values = new HashMap<HorizontalElement, Integer>();

	public VerticalElement(String name) {
		super(name);
	}

	public Integer getValue(HorizontalElement element) {
		Integer result = values.get(element);
		if (result == null) {
			result = 0;
			for (HorizontalElement child : element.getChildren()) {
				result += getValue(child);
			}
			putValue(element, result);
		}
		return result;
	}

	public void putValue(HorizontalElement element, Integer value) {
		values.put(element, value);
	}

}
