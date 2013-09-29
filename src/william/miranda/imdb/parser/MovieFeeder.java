package william.miranda.imdb.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import william.miranda.imdb.model.Filme;

/**
 * Esta Classe Parseia os arquivos do MovieLens para obter a URL dos filmes a serem passados
 * para o parser. Para obter a URL, este parser precisa do ano e do titulo do filme.
 * O retorno é uma Lista contendo todas as URLs.
 * @author William
 *
 */
public class MovieFeeder
{
	private Path filePath;
	
	private List<Filme> listaFilmes = new ArrayList<>();
	
	private Runnable r = new Runnable()
	{
		@Override
		public void run()
		{
			List<String> res = new ArrayList<>();
			
			for (Filme f : listaFilmes)
			{
				try
				{
					String s = getImdbUrl(f);
					res.add(s);
					
					System.out.println(s);
					
					Thread.sleep(1000);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				Path path = Paths.get("urls.txt");
				Utils.saveToFile(path, res);
			}
		}
	};
	
	/**
	 * Construtor que prepara o Path do arquivo a ser lido 
	 * @param filename
	 */
	public MovieFeeder(String filename)
	{
		filePath = Paths.get(filename);
		filePath = filePath.toAbsolutePath();
	}
	
	/**
	 * Abre o arquivo e le as linhas
	 * Ira retornar em cascata a URL dos filmes que desejamos
	 */
	public void readFile()
	{
		Charset charset = Charset.forName("ISO-8859-1");
		
		try (BufferedReader reader = Files.newBufferedReader(filePath, charset))
		{
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		    	//para cada linha do arquivo, parseamos o nome e o ano e gravamos no objeto
		    	Filme filme = parseTituloAno(line);
		    	
		    	//adicionamos o objeto em uma lista
		    	listaFilmes.add(filme);
		    }
		    
		    /* neste ponto listaFilmes contem todos os filmes do arquivo de texto
		     * basta agora parsear as buscas de modo a obter as URLs */
		    new Thread(r).start();
		}
		catch (IOException x)
		{
		    System.err.format("IOException: %s%n", x);
		}
	}
	
	/**
	 * Obtemos o titulo e o ano do filme
	 */
	private Filme parseTituloAno(String linha)
	{
		//separamos os campos delimitados por "|" (pipe)
		String[] campos = linha.split("\\|");

		//pegamos o id do movie lens
		String id = campos[0].trim();
		
		//pegamos o titulo e ano (estao no mesmo campo)
		String titulo = campos[1].trim();
		
		System.out.println(titulo);
		
		//precisamos verificar se o titulo que pegamos eh do formato: "Titulo (Ano)", como por Ex: "The Green Mile (1999)"
		boolean match = Pattern.matches("[\\S|\\s]+ \\(\\d{4}\\)", titulo);
		
		//se segue o padrao, parseia o ano
		if (match)
		{
			String tmpAno = titulo.substring(titulo.length()-5);
			tmpAno = tmpAno.replace(')', ' ').trim();
			int ano = Integer.parseInt(tmpAno);
			
			/* obtemos somente o titulo (sem o ano) e organizamos de forma a passar para a API */
			//remove o ano
			titulo = titulo.substring(0, titulo.length()-6).trim();
			
			//preenche o objeto
			Filme f = new Filme();
			f.setId(Integer.valueOf(id));
			f.setAno(ano);
			f.setTitulo(titulo);
			
			return f;
		}
		
		return null;//se chegou aqui, retorna null
	}
	
	/**
	 * Passamos o objeto contendo Titulo e Ano e precisamos gerar uma URL da forma
	 * String url = "http://www.imdb.com/find?q=Sunchaser,%20The&year=1996";
	 * Entao, fazemos um GET e analisamos o html que Ã© retornado
	 * para entao procurar a URL correspondente ao filme que desejamos
	 */
	private String getImdbUrl(Filme filme)
	{	
		try
		{
			filme.setTitulo(URLEncoder.encode(filme.getTitulo(), "UTF-8"));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//converte o titulo (URL Encode)
		
		StringBuilder sb = new StringBuilder("http://www.imdb.com/find?");
		sb.append("q=").append(filme.getTitulo());
		sb.append("&year=").append(filme.getAno());
		
		return parseXML(sb.toString(), filme);
	}
	
	private String parseXML(String url, Filme filme)
	{
		try
		{
			//faz o parsing do html e pega o primeiro filme que a busca retorna
			Document doc = Jsoup.connect(url).userAgent("Mozilla").timeout(2000).get();
			Elements elements = doc.getElementsByTag("h3");
			
			
			/* quando chegou uma pagina com os resultados da busca
			 * procuramos primeiro a secao dos Filmes (e nao atores, categorias, etc...) */
			
			Element element = null;
			
			for (Element e : elements)
			{
				if ("Titles".equals(e.text()))
				{
					//achamos a tag que queriamos (a do Titulo)
					element = e.nextElementSibling();
				}
			}
			
			if (element != null)//se achou
			{
				//pegamos a parte que interessa
				element = element.getElementsByClass("result_text").get(0);
				
				//pegamos a url do IMDB
				element = element.getElementsByTag("a").get(0);
				
				//obtemos a url
				String id = element.attr("href");
				String[] path = id.split("/");
				
				//retorna o id desejado
				return "http://www.imdb.com/title/" + path[2];
			}
			
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
}
