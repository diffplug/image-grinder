/*
 * Copyright 2020 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.imagegrinder;


import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.function.UnaryOperator;
import javax.imageio.ImageIO;
import org.assertj.core.data.Offset;
import org.assertj.swing.assertions.Assertions;
import org.junit.Test;

public class ParsedSVGTest extends ResourceHarness {
	@Test
	public void testRender() throws Exception {
		File svg = write("test.svg", readTestResource("refresh.svg"));
		ParsedSVG parsed = ParsedSVG.parse(svg);
		parsed.renderPng(file("test16.png"), Size.createSquare(16));
		parsed.renderPng(file("test32.png"), Size.createSquare(32));

		assertEqual(file("test16.png"), "refresh16.png");
		assertEqual(file("test32.png"), "refresh32.png");
	}

	@Test
	public void testRenderBig() throws Exception {
		File svg = write("test.svg", readTestResource("diffpluglogo.svg"));
		ParsedSVG parsed = ParsedSVG.parse(svg);
		parsed.renderPng(file("test256.png"), Size.createSquare(256));
		assertEqual(file("test256.png"), "diffpluglogo256.png");
	}

	/** Asserts that images are equal by downscaling to 8x8 and allowing a 10px offset. */
	static void assertEqual(File testFile, String expectedFile) throws IOException {
		BufferedImage actual = ImageIO.read(testFile);
		BufferedImage expected = ImageIO.read(new ByteArrayInputStream(readTestResource(expectedFile)));
		UnaryOperator<BufferedImage> smudge = in -> toBufferedImage(in.getScaledInstance(8, 8, BufferedImage.SCALE_FAST));
		Assertions.assertThat(smudge.apply(actual)).isEqualTo(smudge.apply(expected), Offset.offset(10));
	}

	private static BufferedImage toBufferedImage(Image img) {
		if (img instanceof BufferedImage) {
			return (BufferedImage) img;
		}

		// Create a buffered image with transparency
		BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		// Draw the image on to the buffered image
		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		// Return the buffered image
		return bimage;
	}
}
