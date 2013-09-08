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
 * para o parser. Para obter a URL, este parser precisa do ano e do título do filme.
 * O retorno é uma Lista contendo todas as URLs.
 * @author William
 *
 */
public class MovieFeeder
{
	private Path filePath;
	
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
	public List<String> readFile()
	{
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lista = new ArrayList<>();
		
		try (BufferedReader reader = Files.newBufferedReader(filePath, charset))
		{
		    String line = null;
		    while ((line = reader.readLine()) != null)
		    {
		    	//para cada linha do arquivo, parseamos o nome e o ano
		    	String filmeUrl = parseTituloAno(line);
		        lista.add(filmeUrl);
		    }
		}
		catch (IOException x)
		{
		    System.err.format("IOException: %s%n", x);
		}
		
		return lista;
	}
	
	/**
	 * Obtemos o titulo e o ano do filme
	 */
	private String parseTituloAno(String linha)
	{
		//separamos os campos delimitados por "|" (pipe)
		String[] campos = linha.split("\\|");
		
		//pegamos o titulo e ano, que eh o campo que interessa
		String titulo = campos[1];
		
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
			f.setAno(ano);
			f.setTitulo(titulo);
			
			return getImdbUrl(f);
		}
		
		return null;//se chegou aqui, retorna null
	}
	
	/**
	 * Passamos o objeto contendo Titulo e Ano e precisamos gerar uma URL da forma
	 * String url = "http://www.imdb.com/find?q=Sunchaser,%20The&year=1996";
	 * Entao analisamos o HTML que esta URL nos retorna
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
			Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
			Elements elements = doc.getElementsByClass("result_text");
			
			//se nao achou o filme, modifica a url de busca
			if (elements.isEmpty())
			{
				try
				{
					filme.setTitulo(URLDecoder.decode(filme.getTitulo(), "UTF-8"));
					filme.setTitulo(filme.getTitulo().substring(0, filme.getTitulo().indexOf('(')).trim());
								
					filme.setTitulo(URLEncoder.encode(filme.getTitulo(), "UTF-8"));
					
					url = "http://www.imdb.com/find?";
					url += "q=" + filme.getTitulo();
					
					doc = Jsoup.connect(url).userAgent("Mozilla").get();
					elements = doc.getElementsByClass("result_text");
				}
				catch (UnsupportedEncodingException e1)
				{
					e1.printStackTrace();
				}
			}
			
			Element element = elements.get(0);	
			
			element = element.getElementsByTag("a").get(0);
			
			//obtemos a url
			String id = element.attr("href");
			String[] path = id.split("/");
			
			//retorna o id desejado
			return "http://www.imdb.com/title/" + path[2];
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
		
	}
}
