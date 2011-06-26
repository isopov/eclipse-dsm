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
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TableModel implements ITableLabelProvider {
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

				verticalMap.get(itemName).putValue(horizontalMap.get(subItemName), weight);
			}
		}

		Collapser.collapse(verticals, horizontals);
	}

	public List<String> getColumnNames() {
		List<String> result = new ArrayList<String>();
		for (HorizontalElement element : horizontals) {
			result.addAll(getNamesFromElement(element));
		}
		return result;
	}

	public List<VerticalElement> getRows() {
		List<VerticalElement> result = new ArrayList<VerticalElement>();

		for (VerticalElement vertical : verticals) {
			result.addAll(getRows(vertical));
		}
		return result;
	}

	private List<VerticalElement> getRows(VerticalElement vertical) {
		List<VerticalElement> result = new ArrayList<VerticalElement>();
		result.add(vertical);
		if (!vertical.isCollapsed() && vertical.isCollapsible()) {
			for (VerticalElement subVertical : vertical.getChildren()) {
				result.addAll(getRows(subVertical));
			}
		}
		return result;
	}

	private <T extends Element<T>> List<String> getNamesFromElement(Element<T> element) {
		if (element.isCollapsed() || !element.isCollapsible()) {
			return Arrays.asList(element.getName());
		} else {
			List<String> result = new ArrayList<String>();
			result.add(element.getName());
			for (Element<T> subElement : element.getChildren()) {
				result.addAll(getNamesFromElement(subElement));
			}
			return result;
		}
	}

	//	private Map<Integer, VerticalElement> getVerticalElementsIndexes() {
	//		Map<Integer, VerticalElement> result = new HashMap<Integer, VerticalElement>();
	//		Integer start = 0;
	//		for (VerticalElement element : verticals) {
	//			Map<Integer, VerticalElement> rowElementsIndexes = getElementsIndexes(element, start);
	//			result.putAll(rowElementsIndexes);
	//			start += rowElementsIndexes.size();
	//		}
	//		return result;
	//	}

	private Map<Integer, HorizontalElement> getHorizontalElementsIndexes() {
		Map<Integer, HorizontalElement> result = new HashMap<Integer, HorizontalElement>();
		Integer start = 0;
		for (HorizontalElement element : horizontals) {
			Map<Integer, HorizontalElement> rowElementsIndexes = getElementsIndexes(element, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	public VerticalElement getVerticalByName(String name) {
		for (VerticalElement vertical : verticals) {
			if (name.startsWith(vertical.getName())) {
				return getByNameFromVertical(vertical, name);
			}
		}
		throw new IllegalStateException("Row with name " + name + " not found!");
	}

	private static VerticalElement getByNameFromVertical(VerticalElement vertical, String name) {
		if (vertical.getName().equals(name)) {
			return vertical;
		} else {
			for (VerticalElement subVertical : vertical.getChildren()) {
				if (name.startsWith(subVertical.getName())) {
					return getByNameFromVertical(subVertical, name);
				}
			}
		}
		throw new IllegalStateException("Row with name " + name + " not found!");
	}

	private <T extends Element<T>> Map<Integer, T> getElementsIndexes(T element, Integer start) {
		if (element.isCollapsed() || !element.isCollapsible()) {
			return Collections.singletonMap(start, element);
		} else {
			Map<Integer, T> result = new HashMap<Integer, T>();
			result.put(start, element);
			Integer newStart = start + 1;
			for (T subelement : element.getChildren()) {
				Map<Integer, T> rowElementsIndexes = getElementsIndexes(subelement, newStart);
				result.putAll(rowElementsIndexes);
				newStart += rowElementsIndexes.size();
			}
			return result;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		VerticalElement row = (VerticalElement) element;
		if (columnIndex == 0) {
			return row.getName();
		} else {
			HorizontalElement item = getHorizontalElementsIndexes().get(columnIndex - 1);
			if (item.getName().equals(row.getName())) {
				return "-";
			}
			return String.valueOf(row.getValue(item));

		}
	}

	@Override
	public void addListener(ILabelProviderListener listener) {
		// no code

	}

	@Override
	public void dispose() {
		// no code

	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// no code
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// no code

	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// no code
		return null;
	}
}