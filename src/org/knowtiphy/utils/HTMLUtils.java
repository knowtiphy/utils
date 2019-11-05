package org.knowtiphy.utils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HTMLUtils
{
	//  TODO -- are there any other ways to getAttr an external reference?
	private static final List<String> ATTRIBUTES = List.of("href", "src");

	public static String extractNonFilePart(URL url) throws MalformedURLException
	{
		//  TODO -- what about strange ones like mailto:philmatthews19@hotmail.com
		if (url.getProtocol() == null || url.getAuthority() == null)
		{
			throw new MalformedURLException("Can't extract non file part");
		}
		else
		{
			return url.getProtocol() + "://" + url.getAuthority();
		}
	}

	private static String extractNonFilePart(String s) throws MalformedURLException
	{
		return extractNonFilePart(new URL(s));
	}

	public static Set<String> computeExternalReferences(Document document)
	{
		document.getDocumentElement().normalize();

		Set<String> externalRefs = new HashSet<>(100);
		NodeList nodeList = document.getElementsByTagName("*");
		for (int index = 0; index < nodeList.getLength(); index++)
		{
			Element eElement = (Element) nodeList.item(index);
			for (String attribute : ATTRIBUTES)
			{
				if (eElement.getAttribute(attribute) != null)
				{
					try
					{
						externalRefs.add(extractNonFilePart(eElement.getAttribute(attribute)));
					} catch (MalformedURLException ex)
					{
						//  ignore crappy content, or things like href=#whatever
					}
				}
			}
		}

		return externalRefs;
	}
}