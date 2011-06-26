package eclipsedsm.model;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public final class CollapserTest {
	@Test
	public void testSimple() {
		List<VerticalElement> verticals = new ArrayList<VerticalElement>();
		List<HorizontalElement> horizontals = new ArrayList<HorizontalElement>();

		VerticalElement vertical1 = new VerticalElement("foo.Bar");
		VerticalElement vertical2 = new VerticalElement("foo.Foo");
		VerticalElement vertical3 = new VerticalElement("boo.Foo");
		verticals.add(vertical1);
		verticals.add(vertical2);
		verticals.add(vertical3);

		HorizontalElement horizontal1 = new HorizontalElement("foo.Bar");
		HorizontalElement horizontal2 = new HorizontalElement("foo.Foo");
		HorizontalElement horizontal3 = new HorizontalElement("boo.Foo");
		horizontals.add(horizontal1);
		horizontals.add(horizontal2);
		horizontals.add(horizontal3);

		Collapser.collapse(verticals, horizontals);

		Assert.assertEquals(2, verticals.size());
		VerticalElement verticalParent1 = verticals.get(0);
		Assert.assertEquals("foo", verticalParent1.getName());
		List<VerticalElement> verticalChildren = verticalParent1.getChildren();
		Assert.assertEquals(2, verticalChildren.size());
		Assert.assertEquals(vertical1, verticalChildren.get(0));
		Assert.assertEquals(vertical2, verticalChildren.get(1));

		VerticalElement verticalParent2 = verticals.get(1);
		Assert.assertEquals(vertical3, verticalParent2);
		//		Assert.assertEquals("boo", verticalParent2.getName());

		Assert.assertEquals(2, horizontals.size());
		List<HorizontalElement> horizontalChildren = horizontals.get(0).getChildren();
		Assert.assertEquals(2, horizontalChildren.size());
		Assert.assertEquals(horizontal1, horizontalChildren.get(0));
		Assert.assertEquals(horizontal2, horizontalChildren.get(1));

	}
}
