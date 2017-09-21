/*
 * Copyright 2017 DiffPlug
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diffplug.gradle.imagegrinder;

import java.awt.RenderingHints;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.transcoder.ErrorHandler;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.batik.util.XMLResourceDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGDocument;

/** Mojo which renders SVG icons into PNG format. */
public class RenderMojo {
	static final Logger log = LoggerFactory.getLogger("RenderMojo");

	private static int parseDimension(String str) {
		if (str.endsWith("px")) {
			return Integer.parseInt(str.substring(0, str.length() - "px".length()));
		} else {
			return Integer.parseInt(str);
		}
	}

	public static void rasterize(File inputPath, File outputPath, int outputScale) throws Exception {
		// Create the document to rasterize
		SVGDocument svgDocument = parseSVG(inputPath);

		// Determine the output sizes (native, double, quad)
		// We render at quad size and resample down for output
		Element svgDocumentNode = svgDocument.getDocumentElement();

		int nativeWidth = parseDimension(svgDocumentNode.getAttribute("width"));
		int nativeHeight = parseDimension(svgDocumentNode.getAttribute("height"));

		int outputWidth = (nativeWidth * outputScale) / 100;
		int outputHeight = (nativeHeight * outputScale) / 100;

		outputPath.getParentFile().mkdirs();
		try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputPath))) {
			TranscoderInput svgInput = new TranscoderInput(svgDocument);
			renderIcon(outputPath, outputWidth, outputHeight, svgInput, output);
		}

		// Guesstimate the PNG size in memory, BAOS will enlarge if necessary.
		// int outputInitSize = nativeWidth * nativeHeight * 4 + 1024;
		//		// Generate a buffered image from Batik's png output
		//		byte[] imageBytes = iconOutput.toByteArray();
		//		ByteArrayInputStream imageInputStream = new ByteArrayInputStream(imageBytes);

		//		BufferedImage inputImage = null;
		//		try {
		//			inputImage = ImageIO.read(imageInputStream);

		//		ImageIO.write(sourceImage, "PNG", new File(icon.outputPath, icon.nameBase + ".png"));
		//
		//		if (icon.disabledPath != null) {
		//			BufferedImage desaturated16 = desaturator.filter(
		//					grayFilter.filter(sourceImage, null), null);
		//
		//			BufferedImage deconstrast = decontrast.filter(desaturated16, null);
		//
		//			ImageIO.write(deconstrast, "PNG", new File(icon.disabledPath, icon.nameBase + ".png"));
		//		}
	}

	private static SVGDocument parseSVG(File file) throws Exception {
		try (InputStream iconDocumentStream = new BufferedInputStream(new FileInputStream(file))) {
			String parser = XMLResourceDescriptor.getXMLParserClassName();
			SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
			return f.createSVGDocument("file://" + file.getAbsolutePath(), iconDocumentStream);
		}
	}

	/**
	 * Use batik to rasterize the input SVG into a raster image at the specified
	 * image dimensions.  
	 * 
	 * @param width the width to render the icons at
	 * @param height the height to render the icon at
	 * @param input the SVG transcoder input
	 * @param stream the stream to write the PNG data to
	 */
	private static void renderIcon(File outputFile, int width, int height, TranscoderInput input, OutputStream stream) throws TranscoderException {
		PNGTranscoder transcoder = new PNGTranscoder() {
			protected ImageRenderer createRenderer() {
				ImageRenderer renderer = super.createRenderer();
				RenderingHints renderHints = renderer.getRenderingHints();
				renderHints.add(new RenderingHints(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
				renderHints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
				renderHints.add(new RenderingHints(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE));
				renderHints.add(new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC));
				renderHints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
				renderHints.add(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
				renderHints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
				renderHints.add(new RenderingHints(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE));
				renderHints.add(new RenderingHints(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON));
				renderer.setRenderingHints(renderHints);
				return renderer;
			}
		};
		transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, new Float(width));
		transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, new Float(height));

		transcoder.setErrorHandler(new ErrorHandler() {
			public void warning(TranscoderException arg0) throws TranscoderException {
				log.error("Icon: " + outputFile + " - WARN: " + arg0.getMessage());
			}

			public void fatalError(TranscoderException arg) throws TranscoderException {
				log.error("Icon: " + outputFile + " - FATAL: " + arg.getMessage());
			}

			public void error(TranscoderException arg) throws TranscoderException {
				log.error("Icon: " + outputFile + " - ERROR: " + arg.getMessage());
			}
		});

		// Transcode the SVG document input to a PNG via the output stream
		TranscoderOutput output = new TranscoderOutput(stream);
		transcoder.transcode(input, output);
	}
}
