package org.knowtiphy.utils;

import java.io.File;

public class FileUtils
{
	public static String baseName(File file)
	{
		String[] split = file.getAbsolutePath().split(File.separator);
		return split[split.length - 1];
	}
}
