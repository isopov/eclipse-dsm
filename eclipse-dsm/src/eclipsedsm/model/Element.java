package eclipsedsm.model;

import java.util.List;

public class Element {
	private final String name;
	private boolean collapsed = true;
	private List<Element> children;

	protected Element(String name) {
		this.name = name;
	}

	public boolean isCollapsible() {
		return children == null || children.size() == 0;
	}

	// Simple getters and setters
	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}

	public List<Element> getChildren() {
		return children;
	}

	public void setChildren(List<Element> children) {
		this.children = children;
	}

	public String getName() {
		return name;
	}
}