package william.miranda.imdb;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.nodes.Element;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.parser.HtmlParser;
import william.miranda.imdb.parser.MovieFeeder;
import william.miranda.imdb.parser.Utils;

public class Principal
{
	public static void main(String[] args)
	{
		//parseamos o arquivo do MovieLens e obtemos a URL de todos os filmes
		MovieFeeder mf = new MovieFeeder("data/ml-100k/u.item");
		List<String> urls = mf.readFile();
		
		//lista para armazenar os objetos do tipo Filme apos parsear cada URL
		List<Filme> filmes = new ArrayList<>();
		
		for (String url : urls)
		{
			Filme f = startParser(url);
			filmes.add(f);
		}
		
		//Neste ponto, temos uma Lista de Filmes... entao geramos o XML
		Filme.toXML(filmes);
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
