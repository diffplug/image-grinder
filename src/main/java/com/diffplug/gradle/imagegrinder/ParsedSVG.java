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
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

import com.diffplug.common.io.Files;

public class ParsedSVG {
	public static ParsedSVG parse(File file) throws IOException {
		try (InputStream iconDocumentStream = new BufferedInputStream(new FileInputStream(file))) {
			return new ParsedSVG(iconDocumentStream);
		}
	}

	private final SVGDocument svgDocument;
	private final Size size;

	private ParsedSVG(InputStream inputStream) throws IOException {
		String parser = XMLResourceDescriptor.getXMLParserClassName();
		SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
		svgDocument = f.createSVGDocument("file://svg", inputStream);

		Element svgDocumentNode = svgDocument.getDocumentElement();
		size = Size.create(
				parseDimension(svgDocumentNode.getAttribute("width")),
				parseDimension(svgDocumentNode.getAttribute("height")));
	}

	private static int parseDimension(String str) {
		if (str.endsWith("px")) {
			return Integer.parseInt(str.substring(0, str.length() - "px".length()));
		} else {
			return Integer.parseInt(str);
		}
	}

	public Size size() {
		return size;
	}

	public void renderFile(File file, Size outSize) throws Exception {
		try (OutputStream stream = Files.asByteSink(file).openBufferedStream()) {
			renderPng(file.getAbsolutePath(), stream, outSize);
		}
	}

	public void renderPng(String name, OutputStream output, Size outSize) throws Exception {
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
		transcoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) outSize.width());
		transcoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) outSize.height());

		transcoder.setErrorHandler(new ErrorHandler() {
			public void warning(TranscoderException arg0) throws TranscoderException {
				log.error("Icon: " + name + " - WARN: " + arg0.getMessage());
			}

			public void fatalError(TranscoderException arg) throws TranscoderException {
				log.error("Icon: " + name + " - FATAL: " + arg.getMessage());
			}

			public void error(TranscoderException arg) throws TranscoderException {
				log.error("Icon: " + name + " - ERROR: " + arg.getMessage());
			}
		});

		// Transcode the SVG document input to a PNG via the output stream
		TranscoderInput svgInput = new TranscoderInput(svgDocument);
		TranscoderOutput pngOutput = new TranscoderOutput(output);
		transcoder.transcode(svgInput, pngOutput);
	}

	static final Logger log = LoggerFactory.getLogger(ParsedSVG.class);
}
