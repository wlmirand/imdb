package william.miranda.imdb;

import java.io.IOException;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.parser.HtmlParser;

public class Teste
{
	public static void main(String[] args)
	{
		try
		{
			String url;
			
			url = "http://www.imdb.com/title/tt0113189";//GoldenEye
			url = "http://www.imdb.com/title/tt0082398";//For Your Eyes Only
			url = "http://www.imdb.com/title/tt1038988";//Rec
			
			HtmlParser pick = new HtmlParser(url);
			pick.parseURL();
			pick.parseReviews();
			
			//System.out.println(pick.getFilme());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
