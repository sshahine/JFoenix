/*
 * JFoenix
 * Copyright (c) 2015, JFoenix and/or its affiliates., All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package com.jfoenix.svg;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author sshahine
 *
 */

public class SVGGlyphLoader {

	private static HashMap<String, SVGGlyphBuilder> glyphsMap = new HashMap<>();
	
	public static SVGGlyph getGlyph(String glyphName){
		return glyphsMap.get(glyphName).build();
	}

	/*
	 * this method is used to retrive icons from icomoon
	 * as we need to apply transformation to correct the icon since 
	 * its being after importing from icomoon
	 */
	public static SVGGlyph getIcoMoonGlyph(String glyphName){
		SVGGlyph glyph = glyphsMap.get(glyphName).build();
		glyph.getTransforms().add(new Scale(1,-1));
		Translate height = new Translate();
		height.yProperty().bind(Bindings.createDoubleBinding(()-> -glyph.getHeight() , glyph.heightProperty()));
		glyph.getTransforms().add(height);
		return glyph;
	}
	
	public static Set<String> getAllGlyphsIDs(){
		return glyphsMap.keySet();
	}
	
	public static void loadGlyphsFont(URL url) throws IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();	
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			docBuilder.setEntityResolver(new EntityResolver() {
	            @Override
	            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	            	// disable dtd entites at runtime
//	                System.out.println("Ignoring " + publicId + ", " + systemId);
	                return new InputSource(new StringReader(""));
	            }
	        });
			
			File svgFontFile = new File(url.toURI());
			Document doc = docBuilder.parse(svgFontFile);	
			doc.getDocumentElement().normalize();
			
			NodeList glyphsList = doc.getElementsByTagName("glyph");
			for (int i = 0; i < glyphsList.getLength(); i++) {
				 Node glyph = glyphsList.item(i);
				 Node glyphName = glyph.getAttributes().getNamedItem("glyph-name");
				 if(glyphName == null) continue;
				
				 String glyphId = glyphName.getNodeValue();
				 SVGGlyphBuilder glyphPane = new SVGGlyphBuilder(i, glyphId, (String)glyph.getAttributes().getNamedItem("d").getNodeValue());
				 glyphsMap.put(svgFontFile.getName() + "." + glyphId, glyphPane);
			}			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static void loadGlyphsFont(InputStream stream, String name) throws IOException {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();	
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			docBuilder.setEntityResolver(new EntityResolver() {
	            @Override
	            public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
	            	// disable dtd entites at runtime
//	                System.out.println("Ignoring " + publicId + ", " + systemId);
	                return new InputSource(new StringReader(""));
	            }
	        });
			
			Document doc = docBuilder.parse(stream);	
			doc.getDocumentElement().normalize();
			
			NodeList glyphsList = doc.getElementsByTagName("glyph");
			for (int i = 0; i < glyphsList.getLength(); i++) {
				 Node glyph = glyphsList.item(i);
				 Node glyphName = glyph.getAttributes().getNamedItem("glyph-name");
				 if(glyphName == null) continue;
				
				 String glyphId = glyphName.getNodeValue();
				 SVGGlyphBuilder glyphPane = new SVGGlyphBuilder(i, glyphId, (String)glyph.getAttributes().getNamedItem("d").getNodeValue());
				 glyphsMap.put(name + "." + glyphId, glyphPane);
			}
			stream.close();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	public static SVGGlyph loadGlyph(URL url) throws IOException {
		String urlString = url.toString();
		String filename = urlString.substring(urlString.lastIndexOf('/') + 1);

		int startPos = 0;
		int endPos = 0;
		while (endPos < filename.length() && filename.charAt(endPos) != '-') {
			endPos++;
		}
		int id = Integer.parseInt(filename.substring(startPos, endPos));
		startPos = endPos + 1;

		while (endPos < filename.length() && filename.charAt(endPos) != '.') {
			endPos++;
		}
		String name = filename.substring(startPos, endPos);

		return new SVGGlyph(id, name, extractSvgPath(getStringFromInputStream(url.openStream())), Color.BLACK);
	}
	
	private static String extractSvgPath(String svgString) {
		return svgString.replaceFirst(".*d=\"", "").replaceFirst("\".*", "");
	}
	
	private static String getStringFromInputStream(InputStream is) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();

		String line;
		try {

			br = new BufferedReader(new InputStreamReader(is));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}
}


class SVGGlyphBuilder{
	int glyphId;
	String name;
	String svgPathContent;
	
	public SVGGlyphBuilder(int glyphId, String name, String svgPathContent) {
		super();
		this.glyphId = glyphId;
		this.name = name;
		this.svgPathContent = svgPathContent;
	}

	SVGGlyph build(){
		return new SVGGlyph(glyphId, name, svgPathContent, Color.BLACK);
	}
	
}
