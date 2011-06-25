package eclipsedsm.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Copy&Pasted from java2s.com
 * 
 * This class contains utility methods for drawing graphics
 * 
 * {@author Rob Warner rwarner@interspatial.com} {@author Robert Harris
 * rbrt_harris@yahoo.com}
 */
public final class GraphicsUtils {

	/**
	 * Creates an image containing the specified text, rotated either plus or
	 * minus 90 degrees.
	 * <dl>
	 * <dt><b>Styles: </b></dt>
	 * <dd>UP, DOWN</dd>
	 * </dl>
	 * 
	 * @param text
	 *            the text to rotate
	 * @param font
	 *            the font to use
	 * @param foreground
	 *            the color for the text
	 * @param background
	 *            the background color
	 * @param style
	 *            direction to rotate (up or down)
	 * @return Image
	 *         <p>
	 *         Note: Only one of the style UP or DOWN may be specified.
	 *         </p>
	 */
	public static Image createRotatedText(String text, Font font, Color foreground, int style) {
		// Get the current display
		Display display = Display.getCurrent();
		if (display == null) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		// Create a GC to calculate font's dimensions
		GC gc = new GC(display);
		gc.setFont(font);

		Point pt = gc.textExtent(text);

		// Dispose that gc
		gc.dispose();

		// Create an image the same size as the string
		Image stringImage = new Image(display, pt.x, pt.y);

		// Create a gc for the image
		gc = new GC(stringImage);
		gc.setFont(font);
		gc.setForeground(foreground);
		gc.setBackground(display.getSystemColor(SWT.COLOR_WHITE));

		// Draw the text onto the image
		gc.drawText(text, 0, 0);

		// Draw the image vertically onto the original GC
		Image image = createRotatedImage(stringImage, style);

		// Dispose the new GC
		gc.dispose();

		// Dispose the horizontal image
		stringImage.dispose();

		// Return the rotated image
		return image;
	}

	//	/**
	//	 * Creates an image containing the specified text, rotated either plus or
	//	 * minus 90 degrees.
	//	 * <dl>
	//	 * <dt><b>Styles: </b></dt>
	//	 * <dd>UP, DOWN</dd>
	//	 * </dl>
	//	 * 
	//	 * @param text
	//	 *            the text to rotate
	//	 * @param font
	//	 *            the font to use
	//	 * @param foreground
	//	 *            the color for the text
	//	 * @param background
	//	 *            the background color
	//	 * @param style
	//	 *            direction to rotate (up or down)
	//	 * @return Image
	//	 *         <p>
	//	 *         Note: Only one of the style UP or DOWN may be specified.
	//	 *         </p>
	//	 */
	//	public static Image createRotatedText(String text, Font font, Color foreground, Color background, int style,
	//			boolean transparentBackground) {
	//		// Get the current display
	//		Display display = Display.getCurrent();
	//		if (display == null) {
	//			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
	//		}
	//
	//		// Create a GC to calculate font's dimensions
	//		GC gc = new GC(display);
	//		gc.setFont(font);
	//
	//		Point pt = gc.textExtent(text);
	//
	//		// Dispose that gc
	//		gc.dispose();
	//
	//		// Create an image the same size as the string
	//		Image stringImage = new Image(display, pt.x, pt.y);
	//
	//		// Create a gc for the image
	//		gc = new GC(stringImage);
	//		gc.setFont(font);
	//		gc.setForeground(foreground);
	//		gc.setBackground(background);
	//
	//		// Draw the text onto the image
	//		gc.drawText(text, 0, 0);
	//
	//		// Draw the image vertically onto the original GC
	//		Image image = createRotatedImage(stringImage, style);
	//
	//		// Dispose the new GC
	//		gc.dispose();
	//
	//		// Dispose the horizontal image
	//		stringImage.dispose();
	//
	//		// Return the rotated image
	//		return image;
	//	}

	private GraphicsUtils() {
		//no code
	}

	/**
	 * Creates a rotated image (plus or minus 90 degrees)
	 * <dl>
	 * <dt><b>Styles: </b></dt>
	 * <dd>UP, DOWN</dd>
	 * </dl>
	 * 
	 * @param image
	 *            the image to rotate
	 * @param style
	 *            direction to rotate (up or down)
	 * @return Image
	 *         <p>
	 *         Note: Only one of the style UP or DOWN may be specified.
	 *         </p>
	 */
	private static Image createRotatedImage(Image image, int style) {
		// Get the current display
		Display display = Display.getCurrent();
		if (display == null) {
			SWT.error(SWT.ERROR_THREAD_INVALID_ACCESS);
		}

		// Use the image's data to create a rotated image's data
		ImageData sd = image.getImageData();
		ImageData dd = new ImageData(sd.height, sd.width, sd.depth, sd.palette);

		// Determine which way to rotate, depending on up or down
		boolean up = (style & SWT.UP) == SWT.UP;

		// Run through the horizontal pixels
		for (int sx = 0; sx < sd.width; sx++) {
			// Run through the vertical pixels
			for (int sy = 0; sy < sd.height; sy++) {
				// Determine where to move pixel to in destination image data
				int dx = up ? sy : sd.height - sy - 1;
				int dy = up ? sd.width - sx - 1 : sx;

				// Swap the x, y source data to y, x in the destination
				dd.setPixel(dx, dy, sd.getPixel(sx, sy));
			}
		}
		int whitePixel = dd.palette.getPixel(new RGB(255, 255, 255));
		dd.transparentPixel = whitePixel;
		// Create the vertical image
		return new Image(display, dd);
	}
}
