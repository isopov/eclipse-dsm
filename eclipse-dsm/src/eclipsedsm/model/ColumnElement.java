package eclipsedsm.model;

public class ColumnElement extends Element<ColumnElement> {
	public ColumnElement(String name) {
		super(name);
	}

	public boolean isColumnVisible() {
		if (getParent() != null
				&& ((getParent().isCollapsible() && getParent().isCollapsed()) || !getParent().isColumnVisible())) {
			return false;
		}
		return true;
	}

}