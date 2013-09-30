package william.miranda.imdb.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
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
					res.add(f.getId() + " | " + s);
					
					System.out.println(s);
					System.out.println("--------");
					
					Thread.sleep(2100);
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
		Charset charset = Utils.getCharset();
		
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
		
		/* Nosso titulo eh da forma <TITULO> <ANO> <LIXO>
		 * entao procuramos o a posicao onde comeca o ano */
		Pattern pattern = Pattern.compile("\\(\\d{4}\\)");
		Matcher matcher = pattern.matcher(titulo);
		
		if(matcher.find())
		{
			int divisor = matcher.start();//this will give you index

			String tmpAno = titulo.substring(divisor+1, divisor+5);
			int ano = Integer.parseInt(tmpAno);

			/* obtemos somente o titulo (sem o ano) e organizamos de forma a passar para a API */
			titulo = titulo.substring(0, divisor).trim();
			
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
			filme.setTitulo(URLEncoder.encode(filme.getTitulo(), Utils.getCharset().displayName()));
		}
		catch (UnsupportedEncodingException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//converte o titulo (URL Encode)
		
		StringBuilder sb = new StringBuilder("http://www.imdb.com/find?&s=tt&ttype=ft&ref_=fn_ft&");
		sb.append("q=").append(filme.getTitulo());
		sb.append("&year=").append(filme.getAno());
		System.out.println(sb.toString());
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
			
			if (element != null)//se achou, procuramos um titulo que de "match" no ano
			{
				//pegamos os result tests de todos os filmes q a busca retornou
				Elements resultTexts = element.getElementsByClass("result_text");
				
				//para cada resultado, analisamos o Ano para ver se bate
				for (int i=0 ; i<resultTexts.size() ; i++)
				{
					Element resultText = resultTexts.get(i);
					String tituloAno = resultText.text();
					
					/* Nosso titulo eh da forma <TITULO> <ANO>
					 * entao procuramos o a posicao onde comeca o ano */
					Pattern pattern = Pattern.compile("\\(\\d{4}\\)");
					Matcher matcher = pattern.matcher(tituloAno);
					
					if (matcher.find())
					{
						int divisor = matcher.start();
						
						int ano = Integer.valueOf(tituloAno.substring(divisor+1, divisor+5).trim());
						
						if (ano == filme.getAno())
						{
							//obtemos a url
							String id = resultText.getElementsByTag("a").get(0).attr("href");
							String[] path = id.split("/");
							
							//retorna o id desejado
							return "http://www.imdb.com/title/" + path[2];
						}
					}
				}
			}
			
		}
		catch (SocketTimeoutException e)
		{
			//se deu timeout, tenta de novo
			return parseXML(url, filme);
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
