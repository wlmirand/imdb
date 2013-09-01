package william.miranda.imdb;

import java.io.IOException;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.parser.HtmlParser;
import william.miranda.imdb.parser.MovieFeeder;
import william.miranda.imdb.parser.Utils;

public class Principal
{
	public static void main(String[] args)
	{
		MovieFeeder mf = new MovieFeeder("data/ml-100k/u.item");
		mf.readFile();
	}
	
	/**
	 * Parseia uma URL do IMDB e retorna o XML contendo os metadados do Filme correspondente
	 * @param url
	 * @return
	 */
	private static Filme startParser(String url)
	{
		try
		{			
			//url = "http://www.imdb.com/title/tt0113189";//GoldenEye
			//url = "http://www.imdb.com/title/tt0082398";//For Your Eyes Only
			//url = "http://www.imdb.com/title/tt1038988";//Rec
			//url = "http://www.imdb.com/title/tt0162930";//Die Hard Dracula
			
			url = Utils.preparaURL(url);
			
			HtmlParser pick = new HtmlParser(url);
			pick.parseURL();
			pick.parseReviews();
			
			//neste ponto temos um objeto do tipo Filme preenchido
			Filme f = pick.getFilme();
			
			return f;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
}
