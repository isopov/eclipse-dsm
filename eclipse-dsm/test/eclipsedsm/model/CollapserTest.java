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

		HorizontalElement horizontal1 = new HorizontalElement("foo.Bar");
		HorizontalElement horizontal2 = new HorizontalElement("foo.Foo");

		vertical1.getValues().put(horizontal2, 1);
		vertical2.getValues().put(horizontal1, 0);

		verticals.add(vertical1);
		verticals.add(vertical2);

		horizontals.add(horizontal1);
		horizontals.add(horizontal2);

		Collapser.collapse(verticals, horizontals);

		Assert.assertEquals(1, verticals.size());
		List<VerticalElement> verticalChildren = verticals.get(0).getChildren();
		Assert.assertEquals(2, verticalChildren.size());
		Assert.assertEquals(vertical1, verticalChildren.get(0));
		Assert.assertEquals(vertical2, verticalChildren.get(1));

		Assert.assertEquals(1, horizontals.size());
		List<HorizontalElement> horizontalChildren = horizontals.get(0).getChildren();
		Assert.assertEquals(2, horizontalChildren.size());
		Assert.assertEquals(horizontal1, horizontalChildren.get(0));
		Assert.assertEquals(horizontal2, horizontalChildren.get(1));

	}
}
