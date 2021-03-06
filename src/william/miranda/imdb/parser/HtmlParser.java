package william.miranda.imdb.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import william.miranda.imdb.model.Filme;
import william.miranda.imdb.model.Review;

public class HtmlParser
{
	public static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/28.0.1500.95 Safari/537.36";
	
	private URL url;
	private URL urlReview;
	
	private Document doc;
	private Document docReview;
	
	Filme f = new Filme();
	
	public HtmlParser(String url) throws MalformedURLException
	{
		this.url = new URL(url);
		this.urlReview = new URL(url.toString() + "/reviews?filter=chrono");
	}
	
	public void parseURL() throws IOException
	{
		try
		{
			doc = Jsoup.connect(url.toString()).get();
			
			f.setTitulo(getTitulo());
			f.setAno(getAno());
			f.setSinopse(getSinopse());
			f.setDiretores(getDiretores());
			f.setCriadores(getCriadores());
			f.setAtores(getAtores());
			f.setRating(getRating());
			f.setStoryline(getStoryline());
			f.setGenres(getGenres());
			f.setPlotKeywords(getPlotKeywords());
		}
		catch (Exception e)
		{
			parseURL();
			return;
		}
	}
	
	public void parseReviews() throws IOException
	{
		int reviewPorPagina = 10;
		
		carregaPaginaReview();
		
		//obtem o numero de reviews do filme
		Element tmp = docReview.getElementsByAttributeValueMatching("href", "reviews-index?").get(0).parent();
		String[] tmp2 = walkNode(tmp, 1).ownText().trim().split(" ");

		int numReviews = Integer.parseInt(tmp2[0]);
		
		//se tem mais de 100 reviews, pegamos apenas os 100 primeiros
		if (numReviews > 100)
			numReviews = 100;
		
		//ja obtem os reviews da primeira pagina
		f.setReviews(getReviews());
		
		//varre todas as paginas a partir da segunda
		int numPaginas = (int) Math.ceil((float)numReviews / reviewPorPagina);
		
		for (int pagina=1 ; pagina<numPaginas ; pagina++)
		{
			URL urlTmp = new URL(urlReview.toString() + ";start=" + pagina*reviewPorPagina);
			carregaPaginaReview();
			f.addReviews(getReviews());
		}
		
	}
	
	//parsing da pagina principal
	private String getTitulo()
	{
		Elements elements = doc.getElementsByAttributeValueContaining("property", "og:title");
		String tmp = elements.get(0).attr("content");
		return tmp.substring(0, tmp.length()-7);
	}
	
	private int getAno()
	{
		Elements elements = doc.getElementsByAttributeValueContaining("property", "og:title");
		String tmp = elements.get(0).attr("content");
		return Integer.parseInt(tmp.substring(tmp.length()-5, tmp.length()-1));
	}
	
	private String getSinopse()
	{
		Elements elements = doc.getElementsByAttributeValueContaining("itemprop", "description");
		
		if (elements.isEmpty())
			return "";
		
		return elements.get(0).ownText();
	}
	
	private List<String> getDiretores()
	{
		List<String> res = new ArrayList<>();
		
		if (!doc.getElementsByAttributeValue("itemprop", "director").isEmpty())
		{
			Element pai = doc.getElementsByAttributeValue("itemprop", "director").get(0);
			Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
			
			for (Element e : lista)
			{
				res.add(e.ownText());
			}
		}
		
		return res;
	}
	
	private List<String> getCriadores()
	{
		List<String> res = new ArrayList<>();
		
		if (!doc.getElementsByAttributeValue("itemprop", "creator").isEmpty())
		{
			Element pai = doc.getElementsByAttributeValue("itemprop", "creator").get(0);
			Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
			
			for (Element e : lista)
			{
				res.add(e.ownText());
			}
		}
		
		return res;
	}
	
	private List<String> getAtores()
	{
		List<String> res = new ArrayList<>();
		
		if (!doc.getElementsByAttributeValue("itemprop", "actors").isEmpty())
		{
			Element pai = doc.getElementsByAttributeValue("itemprop", "actors").get(0);
			Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
			
			for (Element e : lista)
			{
				res.add(e.ownText());
			}
		}
		
		return res;
	}
	
	private float getRating()
	{
		Elements elements = doc.getElementsByAttributeValueContaining("itemprop", "ratingValue");
		
		if (!elements.isEmpty())
			return Float.parseFloat(elements.get(0).ownText());
		else
			return -1;
	}
	
	private String getStoryline()
	{
		Elements elements = doc.getElementsByTag("h2");
		String tmp = null;
		
		for (Element e : elements)
		{
			if ("Storyline".equals(e.ownText()))//se achou a storyline nas tags h2
			{
				tmp = walkNode(e, 1).child(0).ownText();
			}
		}
		
		return tmp;
	}
	
	private List<String> getGenres()
	{
		Elements elements = doc.getElementsByAttributeValue("itemprop", "genre");
		List<String> tmp = new ArrayList<>();
		
		for (Element e : elements)
		{
			if ("span".equals(e.tagName()))//todos os generos sao da tag SPAN e itemprop="genre"
			{
				tmp.add(e.ownText());
			}
		}
		
		return tmp;
	}
	
	private List<String> getPlotKeywords()
	{
		Elements elements = doc.getElementsByAttributeValue("itemprop", "keywords");
		List<String> tmp = new ArrayList<>();
		
		for (Element e : elements)
		{
			if ("span".equals(e.tagName()))//todos os generos sao da tag SPAN e itemprop="keywords"
			{
				tmp.add(e.ownText());
			}
		}
		
		return tmp;
	}
	
	//parsing da pagina de reviews
	private List<Review> getReviews()
	{
		List<Review> res = new ArrayList<>();
		
		/* Todos os titulos dos revview est�o dentro de <h2> e, esta tag nao eh mais utilizada, entao usaremos
		 * os <h2> como base para encontrar as informacoes dos reviews */
		Elements elements = docReview.getElementsByTag("h2");
		
		//varremos os <h2>
		for (Element e : elements)
		{
			//chama o metodo para fazer o parsing do <h2>
			Review r = parseReviewElement(e.parent());
			res.add(r);
		}
		
		return res;
	}
	
	//obtem o <div> pai do <h2> e retira os dados
	private Review parseReviewElement(Element e)
	{
		Review r = new Review();
		
		Element autorTag = e.getElementsByTag("b").get(0);//usaremos como pivo interno
		
		r.setTitulo(e.getElementsByTag("h2").get(0).ownText());
		r.setConteudo(e.nextElementSibling().ownText());

		r.setAutor(autorTag.nextElementSibling().ownText());
		
		//parseia a data... se o autor nao especificou lugar, muda o indice
		if ("br".equals(walkNode(autorTag, 4).tagName()))
		{
			r.setData(walkNode(autorTag, 3).ownText());
		}
		else
		{
			r.setData(walkNode(autorTag, 4).ownText());
		}
		
		//parseia a nota... pode nao existir nota
		Element notaNode = walkNode(autorTag, -2);
		if (!"".equals(notaNode.attr("alt")))
		{
			String[] tmp = notaNode.attr("alt").split("/");
			r.setRating(Integer.parseInt(tmp[0]));
		}
		
		return r;
	}
	
	//dado um elemento, anda N posicoes e retorna o elemento encontrado
	private Element walkNode(Element e, int offset)
	{
		Element tmp = e;
		
		if (offset > 0)//anda para a frente
		{
			for (int i=0 ; i<offset ; i++)
			{
				tmp = tmp.nextElementSibling();
			}
		}
		else//anda para tras
		{
			for (int i=0 ; i>offset ; i--)
			{
				tmp = tmp.previousElementSibling();
			}
		}
		
		return tmp;
	}
	
	//sets e gets
	public Filme getFilme()
	{
		return this.f;
	}
	
	public void carregaPaginaReview()
	{
		try
		{
			docReview = Jsoup.connect(urlReview.toString()).userAgent("Mozilla").get();
		}
		catch (Exception e)
		{
			carregaPaginaReview();
			return;
		}
	}
}
