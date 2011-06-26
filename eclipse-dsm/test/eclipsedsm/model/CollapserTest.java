package eclipsedsm.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public final class CollapserTest {
	@Test
	public void testSimple() {
		List<RowElement> verticals = new ArrayList<RowElement>();
		List<ColumnElement> horizontals = new ArrayList<ColumnElement>();

		RowElement vertical1 = new RowElement("foo.Bar");
		RowElement vertical2 = new RowElement("foo.Foo");
		RowElement vertical3 = new RowElement("boo.foo.Foo");
		verticals.add(vertical1);
		verticals.add(vertical2);
		verticals.add(vertical3);

		ColumnElement horizontal1 = new ColumnElement("foo.Bar");
		ColumnElement horizontal2 = new ColumnElement("foo.Foo");
		ColumnElement horizontal3 = new ColumnElement("boo.foo.Foo");
		horizontals.add(horizontal1);
		horizontals.add(horizontal2);
		horizontals.add(horizontal3);

		Collapser.collapse(verticals, horizontals);

		Assert.assertEquals(2, verticals.size());
		RowElement verticalParent1 = verticals.get(0);
		Assert.assertEquals("foo", verticalParent1.getName());
		List<RowElement> verticalChildren = verticalParent1.getChildren();
		Assert.assertEquals(2, verticalChildren.size());
		Assert.assertEquals(vertical1, verticalChildren.get(0));
		Assert.assertEquals(vertical2, verticalChildren.get(1));

		RowElement verticalParent2 = verticals.get(1);
		Assert.assertEquals(vertical3, verticalParent2);

		Assert.assertEquals(2, horizontals.size());
		List<ColumnElement> horizontalChildren = horizontals.get(0).getChildren();
		Assert.assertEquals(2, horizontalChildren.size());
		Assert.assertEquals(horizontal1, horizontalChildren.get(0));
		Assert.assertEquals(horizontal2, horizontalChildren.get(1));

	}
}
