package william.miranda.imdb;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.parser.HtmlParser;
import william.miranda.imdb.parser.MovieFeeder;
import william.miranda.imdb.parser.Utils;

public class Principal
{
	//lista para armazenar os objetos do tipo Filme apos parsear cada URL
	private static List<Filme> filmes = new ArrayList<>();
	
	//lista que contem as URLs que foram obtidas para os filmes do arquivo
	private static List<String> urls;
	
	private static Runnable r = new Runnable()
	{
		@Override
		public void run()
		{
			//parseia o filme para cada URL lida do arquivo de entrada
			for (String url : urls)
			{
				//try
				{
					System.out.println(url);
					Filme f = startParser(url);
					filmes.add(f);

					//Thread.sleep(1000);
				}
				/*
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				*/
				
				//Neste ponto, temos uma Lista de Filmes... entao geramos o XML
				Filme.toXML(filmes);
			}
		}
	};
	
	public static void main(String[] args)
	{
		//parseamos o arquivo do MovieLens e obtemos as URLs de todos os filmes
		MovieFeeder mf = new MovieFeeder("data/ml-100k/u.item");
		mf.readFile();

		//roda a thread para parsear os filmes, com intervalo, de modo que nao exceda o limite de requisicoes
		//new Thread(r).start();
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
