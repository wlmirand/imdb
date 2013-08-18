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
			/*
			url = "http://www.imdb.com/title/tt0082398";//For Your Eyes Only
			url = "http://www.imdb.com/title/tt1038988";//Rec
			url = "http://www.imdb.com/title/tt0162930";//Die Hard Dracula
			*/
			
			HtmlParser pick = new HtmlParser(url);
			pick.parseURL();
			pick.parseReviews();
			
			//neste ponto temos um objeto do tipo Filme preenchido
			Filme f = pick.getFilme();
			System.out.println(f);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
