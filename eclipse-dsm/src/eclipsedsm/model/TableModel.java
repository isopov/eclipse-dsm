package eclipsedsm.model;

import java.util.ArrayList;
import java.util.Arrays;
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
	private List<RowElement> rows = new ArrayList<RowElement>();
	private List<ColumnElement> columns = new ArrayList<ColumnElement>();

	public TableModel(DependencyGraph items) {
		Set<Dependable> itemList = new TreeSet<Dependable>(new Comparator<Dependable>() {

			@Override
			public int compare(Dependable o1, Dependable o2) {
				return o1.getFullyQualifiedName().split(" ")[1].compareTo(o2.getFullyQualifiedName().split(" ")[1]);
			}
		});
		itemList.addAll(items.getAllItems());

		Map<String, RowElement> rowMap = new HashMap<String, RowElement>();
		Map<String, ColumnElement> columnMap = new HashMap<String, ColumnElement>();

		for (Dependable item : itemList) {
			String name = item.getFullyQualifiedName().split(" ")[1];

			RowElement vertical = new RowElement(name);
			rowMap.put(name, vertical);
			rows.add(vertical);

			ColumnElement column = new ColumnElement(name);
			columns.add(column);
			columnMap.put(name, column);
		}
		for (Dependable item : items.getAllItems()) {
			for (Dependable subItem : items.getAllItems()) {
				int weight = items.getDependencyWeight(item, subItem);
				String itemName = item.getFullyQualifiedName().split(" ")[1];
				String subItemName = subItem.getFullyQualifiedName().split(" ")[1];

				rowMap.get(itemName).putValue(columnMap.get(subItemName), weight);
			}
		}

		Collapser.collapse(rows, columns);
	}

	public List<String> getColumnNames() {
		List<String> result = new ArrayList<String>();
		for (ColumnElement element : columns) {
			result.addAll(getNamesFromElement(element));
		}
		return result;
	}

	public List<RowElement> getRows() {
		List<RowElement> result = new ArrayList<RowElement>();

		for (RowElement row : rows) {
			result.addAll(getRows(row));
		}
		return result;
	}

	private static List<RowElement> getRows(RowElement row) {
		List<RowElement> result = new ArrayList<RowElement>();
		result.add(row);
		if (!row.isCollapsed() && row.isCollapsible()) {
			for (RowElement subVertical : row.getChildren()) {
				result.addAll(getRows(subVertical));
			}
		}
		return result;
	}

	private static <T extends Element<T>> List<String> getNamesFromElement(Element<T> element) {
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

	private Map<Integer, ColumnElement> getColumnElementsIndexes() {
		Map<Integer, ColumnElement> result = new HashMap<Integer, ColumnElement>();
		Integer start = 0;
		for (ColumnElement column : columns) {
			Map<Integer, ColumnElement> rowElementsIndexes = getColumnElementsIndexes(column, start);
			result.putAll(rowElementsIndexes);
			start += rowElementsIndexes.size();
		}
		return result;
	}

	private static Map<Integer, ColumnElement> getColumnElementsIndexes(ColumnElement column, Integer start) {
		Map<Integer, ColumnElement> result = new HashMap<Integer, ColumnElement>();
		result.put(start, column);
		Integer newStart = start + 1;
		if (column.getChildren() != null) {
			for (ColumnElement subColumn : column.getChildren()) {
				Map<Integer, ColumnElement> rowElementsIndexes = getColumnElementsIndexes(subColumn, newStart);
				result.putAll(rowElementsIndexes);
				newStart += rowElementsIndexes.size();
			}
		}
		return result;
	}

	public RowElement getRowElementByName(String name) {
		for (RowElement vertical : rows) {
			if (name.startsWith(vertical.getName())) {
				return getByNameFromElement(vertical, name);
			}
		}
		throw new IllegalStateException("Vertical element (row) with name " + name + " not found!");
	}

	public ColumnElement getColumnElementByName(String name) {
		for (ColumnElement column : columns) {
			if (name.startsWith(column.getName())) {
				return getByNameFromElement(column, name);
			}
		}
		throw new IllegalStateException("Horizontal element (column) with name " + name + " not found!");
	}

	private static <T extends Element<T>> T getByNameFromElement(T element, String name) {
		if (element.getName().equals(name)) {
			return element;
		} else {
			for (T subElement : element.getChildren()) {
				if (name.startsWith(subElement.getName())) {
					return getByNameFromElement(subElement, name);
				}
			}
		}
		throw new IllegalStateException("Row with name " + name + " not found!");
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		RowElement row = (RowElement) element;
		if (columnIndex == 0) {
			return row.getName();
		} else {
			ColumnElement item = getColumnElementsIndexes().get(columnIndex - 1);
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