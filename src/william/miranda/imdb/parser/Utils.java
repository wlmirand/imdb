package william.miranda.imdb.parser;

public class Utils
{
	public static String preparaURL(String url)
	{
		String tmp = url;
		
		if (url.endsWith("/"))
		{
			tmp = url.substring(0, url.length()-1);
		}
		
		return tmp;		
	}
}
