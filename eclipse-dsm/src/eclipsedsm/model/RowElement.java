package eclipsedsm.model;

import java.util.HashMap;
import java.util.Map;

public class RowElement extends Element<RowElement> {
	private final Map<ColumnElement, Integer> values = new HashMap<ColumnElement, Integer>();

	public RowElement(String name) {
		super(name);
	}

	public Integer getValue(ColumnElement element) {
		Integer result = values.get(element);
		if (result == null) {
			result = 0;
			if (element.getChildren() != null) {
				for (ColumnElement child : element.getChildren()) {
					result += getValue(child);
				}
			}
			if (result == 0 && getChildren() != null) {
				for (RowElement child : getChildren()) {
					result += child.getValue(element);
				}
			}
			putValue(element, result);
		}
		return result;
	}

	public void putValue(ColumnElement element, Integer value) {
		values.put(element, value);
	}

}
