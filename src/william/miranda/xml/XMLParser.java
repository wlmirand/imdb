package william.miranda.xml;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.model.Review;
import william.miranda.imdb.parser.Utils;

/* parseia os XMLs para gerar objetos do tipo Filme */
public class XMLParser
{
	/* parseia apenas o arquivo de UM filme */
	public static Filme parseXML(Path path)
	{
		if (!Files.exists(path, LinkOption.NOFOLLOW_LINKS))
			return null;
		
		Filme filme = new Filme();
		
		/* É melhor abrir os arquivos usando a propria API do Jsoup
		StringBuilder sb = Utils.readStringFromFile(path);
		
		if (sb == null)
			return null;
		*/
		//Document doc = Jsoup.parse(sb.toString(), "", Parser.xmlParser());
		Document doc;
		try
		{
			doc = Jsoup.parse(path.toFile(), Utils.getCharset().displayName());
		
			//pega os campos do xml
			filme.setTitulo(doc.getElementsByTag("titulo").get(0).text());
			filme.setAno(Integer.valueOf(doc.getElementsByTag("ano").get(0).text()));
			filme.setId(Integer.valueOf(doc.getElementsByTag("id").get(0).text()));
			filme.setImdbUrl(doc.getElementsByTag("imdbUrl").get(0).text());
			filme.setRating(Float.valueOf(doc.getElementsByTag("rating").get(0).text()));
			filme.setSinopse(doc.getElementsByTag("sinopse").get(0).text());
			filme.setStoryline(doc.getElementsByTag("storyline").get(0).text());
			
			//seta os generos
			Elements generos = doc.getElementsByTag("genero");
			List<String> listaGeneros = new ArrayList<>();
			
			for (Element genero : generos)
			{
				listaGeneros.add(genero.text());
			}
			filme.setGenres(listaGeneros);
	
			//seta as keywords
			Elements keywords = doc.getElementsByTag("keyword");
			List<String> listaKeywords = new ArrayList<>();
			
			for (Element keyword : keywords)
			{
				listaKeywords.add(keyword.text());
			}
			filme.setPlotKeywords(listaKeywords);
			
			//seta os diretores
			Elements diretores = doc.getElementsByTag("diretor");
			List<String> listaDiretores = new ArrayList<>();
			
			for (Element diretor : diretores)
			{
				listaDiretores.add(diretor.text());
			}
			filme.setDiretores(listaDiretores);
			
			//seta os criadores
			Elements criadores = doc.getElementsByTag("escritor");
			List<String> listaCriadores = new ArrayList<>();
			
			for (Element criador : criadores)
			{
				listaCriadores.add(criador.text());
			}
			filme.setCriadores(listaCriadores);
			
			//seta os atores
			Elements atores = doc.getElementsByTag("ator");
			List<String> listaAtores = new ArrayList<>();
			
			for (Element ator : atores)
			{
				listaAtores.add(ator.text());
			}
			filme.setAtores(listaAtores);
			
			//seta os reviews
			Elements reviews = doc.getElementsByTag("review");
			List<Review> listaReviews = new ArrayList<>();
			
			for (Element r : reviews)
			{
				Review review = new Review();
				
				review.setTitulo(r.getElementsByTag("titulo").get(0).text());
				review.setAutor(r.getElementsByTag("autor").get(0).text());
				review.setData(r.getElementsByTag("data").get(0).text());
				review.setConteudo(r.getElementsByTag("conteudo").get(0).text());
				
				listaReviews.add(review);
			}
			
			filme.setReviews(listaReviews);
			
			return filme;
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/* parseia todos os XMLs existentes dentro do Folder */
	public static List<Filme> parseAllXML(Path folder) throws IOException
	{
		final List<Filme> listaFilmes = new ArrayList<>();
		
		Files.walkFileTree(folder, new SimpleFileVisitor<Path>()
				{
					@Override
					public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs)
					{
						listaFilmes.add(parseXML(file));
						return FileVisitResult.CONTINUE;
					}
				});
		
		return listaFilmes;
	}
}
