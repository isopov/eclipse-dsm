package eclipsedsm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableModel {
	private List<VerticalElement> verticals;
	private List<HorizontalElement> horizontals;

	public TableModel(List<String> names, List<List<Integer>> values) {
		checkDimensions(names, values);
	}

	public List<String> getRowNames() {
		List<String> result = new ArrayList<String>();
		for (Element element : horizontals) {
			result.addAll(getNamesFromElement(element));
		}
		return result;
	}

	public List<String> getColumnNames() {
		List<String> result = new ArrayList<String>();
		for (Element element : verticals) {
			result.addAll(getNamesFromElement(element));
		}
		return result;
	}

	private List<String> getNamesFromElement(Element element) {
		if (element.isCollapsed() || !element.isCollapsible()) {
			return Arrays.asList(element.getName());
		} else {
			List<String> result = new ArrayList<String>();
			result.add(element.getName());
			for (Element subElement : element.getChildren()) {
				result.addAll(getNamesFromElement(subElement));
			}
			return result;
		}
	}

	public Integer getValue(Integer row, Integer column) {
		return ((VerticalElement) getRowElementsIndexes().get(row)).getValues()
				.get(getColumnElementsIndexes().get(column));
	}

	private Map<Integer, Element> getRowElementsIndexes() {
		Map<Integer, Element> result = new HashMap<Integer, Element>();
		Integer start = 0;
		for (Element element : verticals) {
			Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(
					element, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	private Map<Integer, Element> getColumnElementsIndexes() {
		Map<Integer, Element> result = new HashMap<Integer, Element>();
		Integer start = 0;
		for (Element element : horizontals) {
			Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(
					element, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	private Map<Integer, Element> getRowElementsIndexes(Element element,
			Integer start) {
		if (element.isCollapsed() || !element.isCollapsible()) {
			return Collections.singletonMap(start, element);
		} else {
			Map<Integer, Element> result = new HashMap<Integer, Element>();
			result.put(start, element);
			start++;
			for (Element subelement : element.getChildren()) {
				Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(
						subelement, start);
				result.putAll(rowElementsIndexes);
				start += rowElementsIndexes.size();
			}
			return result;
		}
	}

	private void checkDimensions(List<String> names, List<List<Integer>> values) {
		int size = names.size();
		if (values.size() != size) {
			throw new IllegalArgumentException("Value matrix of wrong width: "
					+ values.size() + " instead of " + size);

		}
		for (List<Integer> column : values) {
			if (column.size() != size) {
				throw new IllegalArgumentException(
						"One of columns of wrong size: " + column.size()
								+ " instead of " + size);
			}
		}
	}

}