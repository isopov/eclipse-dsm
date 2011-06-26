package eclipsedsm.model;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public final class Collapser {

	private Collapser() {
		// no code
	}

	public static void collapse(List<VerticalElement> verticals, List<HorizontalElement> horizontals) {
		while (commonParent(verticals)) {
			collapse(verticals, VerticalElement.class);
		}
		removeOnechildPaths(verticals);

		while (commonParent(horizontals)) {
			collapse(horizontals, HorizontalElement.class);
		}
		removeOnechildPaths(horizontals);
	}

	private static <T extends Element<T>> void removeOnechildPaths(List<T> elements) {
		if (elements == null) {
			return;
		}
		for (int i = 0; i < elements.size(); i++) {
			T element = elements.get(i);
			if (element.getChildren() != null && elements.get(i).getChildren().size() == 1) {
				elements.set(i, element.getChildren().get(0));
			}
		}
	}

	private static <T extends Element<T>> void collapse(List<T> elements, Class<T> elementClass) {
		Map<String, String[]> splittedNames = new HashMap<String, String[]>();
		Map<String, T> elementsMap = new HashMap<String, T>();
		int maxsize = 0;
		Map<String, List<T>> groupsWithMaxSize = new HashMap<String, List<T>>();

		for (T element : elements) {
			String[] splitName = element.getName().split("\\.");
			String name = element.getName();
			if (splitName.length > maxsize) {
				maxsize = splitName.length;
				groupsWithMaxSize.clear();
			}
			if (splitName.length == maxsize) {
				insertIntoGroups(groupsWithMaxSize, parentPackageName(splitName), element);
			}
			splittedNames.put(name, splitName);
			elementsMap.put(element.getName(), element);
		}
		for (Entry<String, List<T>> entry : groupsWithMaxSize.entrySet()) {
			int insertingIndex = insertingIndex(entry.getKey(), elements);
			T newParent = null;
			try {
				Constructor<?> constructor = elementClass.getConstructors()[0];
				newParent = (T) constructor.newInstance(entry.getKey());
			} catch (Exception e) {
				throw new IllegalStateException("Unsupproted construcor was met");
			}
			newParent.setChildren(entry.getValue());
			elements.add(insertingIndex, newParent);
			elements.removeAll(entry.getValue());
		}
	}

	private static <T extends Element<T>> boolean commonParent(List<T> elements) {
		Set<String> parentNames = new HashSet<String>();
		for (T element : elements) {
			parentNames.add(element.getName().split("\\.")[0]);
		}
		return elements.size() != parentNames.size();
	}

	private static <T extends Element<T>> int insertingIndex(String parentPackageName, List<T> elements) {
		for (int i = 0; i < elements.size(); i++) {
			T element = elements.get(i);
			if (parentPackageName.equals(parentPackageName(element.getName().split("\\.")))) {
				return i;
			}
		}
		throw new IllegalArgumentException("No parent package name " + parentPackageName + "in list" + elements);
	}

	private static String parentPackageName(String[] splitName) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < (splitName.length - 1); i++) {
			result.append(splitName[i]);
			if (i != splitName.length - 2) {
				result.append(".");
			}
		}
		return result.toString();
	}

	private static <T extends Element<T>> void insertIntoGroups(Map<String, List<T>> groups, String name, T element) {
		List<T> group = groups.get(name);
		if (group == null) {
			group = new ArrayList<T>();
			groups.put(name, group);
		}
		group.add(element);

	}

}
