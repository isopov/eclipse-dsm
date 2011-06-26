package eclipsedsm.model;

import java.util.List;

public class Element<T extends Element<T>> {
	private final String name;
	private boolean collapsed = true;
	private List<T> children;
	private T parent;

	public T getParent() {
		return parent;
	}

	public void setParent(T parent) {
		this.parent = parent;
	}

	protected Element(String name) {
		this.name = name;
	}

	public boolean isCollapsible() {
		return children != null && children.size() != 0;
	}

	// Simple getters and setters
	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public List<T> getChildren() {
		return children;
	}

	public void setChildren(List<T> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}
}