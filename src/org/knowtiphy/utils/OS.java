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
	private static final Pattern LINUX_PATTERN = Pattern.compile("linux.*", Pattern.CASE_INSENSITIVE);
	private static final Pattern MAC_PATTERN = Pattern.compile(".*mac.*", Pattern.CASE_INSENSITIVE);
	private static final Pattern WINDOWS_PATTERN = Pattern.compile(".*windows.*", Pattern.CASE_INSENSITIVE);

	private static final String PLASMA = "plasma";
	private static final String GNOME = "gnome";

	private static String osName()
	{
		return System.getProperty("os.name");
	}

	private static String home()
	{
		return System.getProperty("user.home");
	}

	private static boolean isMac()
	{
		return MAC_PATTERN.matcher(osName()).matches();
	}

	private static boolean isWindows()
	{
		return WINDOWS_PATTERN.matcher(osName()).matches();
	}

	private static boolean isLinux()
	{
		return LINUX_PATTERN.matcher(osName()).matches();
	}

	private static boolean isKDE()
	{
		return System.getenv("DESKTOP_SESSION").equals(PLASMA);
	}

	private static boolean isGnome()
	{
		return System.getenv("DESKTOP_SESSION").equals(GNOME);
	}

	private static String xdgDataHome()
	{
		String xdg = System.getenv("XDG_DATA_HOME");
		return xdg == null || xdg.isEmpty() ? Paths.get(home(), ".local", "share").toString() : xdg;
	}

	private static String xdgConfigHome()
	{
		String xdg = System.getenv("XDG_CONFIG_HOME");
		return xdg == null || xdg.isEmpty() ? Paths.get(home(), ".config").toString() : xdg;
	}


	public static Path getDataDir(Class<?> cls) throws IOException
	{
		String baseDirName = cls.getCanonicalName();

		Path dir = null;
		if (isMac())
		{
			dir = Paths.get(home(), "Library", "Application Support", baseDirName);
		}
		else if (isWindows())
		{
			dir = Paths.get(home(), "AppData", "Roaming", baseDirName);
		}
		else if (isLinux())
		{
			if (isKDE() || isGnome())
			{
				dir = Paths.get(xdgDataHome(), baseDirName);
			}
			else
			{
				//	TODO -- something else
				assert false;
			}
		}

		//	TODO -- something else
		assert dir != null;

		Files.createDirectories(dir);
		return dir;
	}

	public static Path getSettingsDir(Class<?> cls) throws IOException
	{
		String baseDirName = cls.getCanonicalName();

		Path dir = null;
		if (isMac())
		{
			dir = Paths.get(home(), "Library", "Application Support", baseDirName);
		}
		else if (isWindows())
		{
			dir = Paths.get(home(), "AppData", "Roaming", baseDirName);
		}
		else if (isLinux())
		{
			if (isKDE() || isGnome())
			{
				dir = Paths.get(xdgConfigHome(), baseDirName);
			}
			else
			{
				//	TODO -- something else
				assert false;
			}
		}

		//	TODO -- something else
		assert dir != null;

		Files.createDirectories(dir);
		return dir;
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
			else if (isLinux())
			{
				Runtime.getRuntime().exec("xdg-open " + url);
			}
		}
	}
}