package org.knowtiphy.utils;

//import com.mcdermottroe.apple.OSXKeychain;
//import com.mcdermottroe.apple.OSXKeychainException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

/* Hello, world */

/**
 * @author graham
 */
public class OS
{

	private static final Pattern MAC_PATTERN = Pattern.compile(".*mac.*", Pattern.CASE_INSENSITIVE);
	private static final Pattern WINDOWS_PATTERN = Pattern.compile(".*windows.*", Pattern.CASE_INSENSITIVE);

	private static boolean isMac()
	{
		return MAC_PATTERN.matcher(System.getProperty("os.name")).matches();
	}

	private static boolean isWindows()
	{
		return WINDOWS_PATTERN.matcher(System.getProperty("os.name")).matches();
	}

	private static boolean isKDE()
	{
		@SuppressWarnings("CallToSystemGetenv")
		String kde = System.getenv("KDE_FULL_SESSION");
		return kde != null;
	}

	public static Path getAppDir(Class<?> cls) throws IOException
	{
		String home = System.getProperty("user.home");
		Path dir;

		if (isMac())
		{
			dir = Paths.get(home, "Library", "Application Support", cls.getCanonicalName());
		}
		else if (isWindows())
		{
			dir = Paths.get(home, "AppData", "Roaming", cls.getCanonicalName());
		}
		//	assume its Linux, is this how we check for KDE?
		else if (isKDE())
		{
			dir = Paths.get(home, ".kde", "share", "apps", cls.getCanonicalName());
		}
		else
		{
			//	assume it's gnome
			//	TODO gnome
			assert false;
			return Paths.get("");
		}


		Files.createDirectories(dir);
		return dir;
	}

	public static Path getAppFile(Class<?> cls, String fileName) throws IOException
	{
		return Paths.get(getAppDir(cls).toString(), fileName);
	}

	private static boolean browse(String url)
	{
		try
		{
			Desktop.getDesktop().browse(new URI(url));
			return true;
		} catch (UnsupportedOperationException | IOException | URISyntaxException ex)
		{
			return false;
		}
	}

	public static void open(String url) throws IOException
	{
		if (!Desktop.isDesktopSupported() || !browse(url))
		{
			//  TODO -- windows?
			if (isMac())
			{
				Runtime.getRuntime().exec("open " + url);
			}
			else
			{
				Runtime.getRuntime().exec("xdg-open " + url);
			}
		}
	}
}