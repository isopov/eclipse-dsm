package eclipsedsm.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.dtangler.core.dependencies.Dependable;
import org.dtangler.core.dependencies.DependencyGraph;

public class TableModel {
	private List<VerticalElement> verticals = new ArrayList<VerticalElement>();
	private List<HorizontalElement> horizontals = new ArrayList<HorizontalElement>();

	public TableModel(DependencyGraph items) {
		Set<Dependable> itemList = new TreeSet<Dependable>(new Comparator<Dependable>() {

			@Override
			public int compare(Dependable o1, Dependable o2) {
				return o1.getFullyQualifiedName().split(" ")[1].compareTo(o2.getFullyQualifiedName().split(" ")[1]);
			}
		});
		itemList.addAll(items.getAllItems());

		Map<String, VerticalElement> verticalMap = new HashMap<String, VerticalElement>();
		Map<String, HorizontalElement> horizontalMap = new HashMap<String, HorizontalElement>();

		for (Dependable item : itemList) {
			String name = item.getFullyQualifiedName().split(" ")[1];

			VerticalElement vertical = new VerticalElement(name);
			verticalMap.put(name, vertical);
			verticals.add(vertical);

			HorizontalElement horizontal = new HorizontalElement(name);
			horizontals.add(horizontal);
			horizontalMap.put(name, horizontal);
		}
		for (Dependable item : items.getAllItems()) {
			for (Dependable subItem : items.getAllItems()) {
				int weight = items.getDependencyWeight(item, subItem);
				String itemName = item.getFullyQualifiedName().split(" ")[1];
				String subItemName = subItem.getFullyQualifiedName().split(" ")[1];

				verticalMap.get(itemName).getValues().put(horizontalMap.get(subItemName), weight);
			}
		}

		Collapser.collapse(verticals, horizontals);
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
		return ((VerticalElement) getRowElementsIndexes().get(row)).getValues().get(
				getColumnElementsIndexes().get(column));
	}

	private Map<Integer, Element> getRowElementsIndexes() {
		Map<Integer, Element> result = new HashMap<Integer, Element>();
		Integer start = 0;
		for (Element element : verticals) {
			Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(element, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	private Map<Integer, Element> getColumnElementsIndexes() {
		Map<Integer, Element> result = new HashMap<Integer, Element>();
		Integer start = 0;
		for (Element element : horizontals) {
			Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(element, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	private Map<Integer, Element> getRowElementsIndexes(Element element, Integer start) {
		if (element.isCollapsed() || !element.isCollapsible()) {
			return Collections.singletonMap(start, element);
		} else {
			Map<Integer, Element> result = new HashMap<Integer, Element>();
			result.put(start, element);
			Integer newStart = start + 1;
			for (Element subelement : element.getChildren()) {
				Map<Integer, Element> rowElementsIndexes = getRowElementsIndexes(subelement, newStart);
				result.putAll(rowElementsIndexes);
				newStart += rowElementsIndexes.size();
			}
			return result;
		}
	}
}