package william.miranda.imdb.parser;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
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
		doc = Jsoup.connect(url.toString()).get();
		
		f.setTitulo(getTitulo());
		f.setAno(getAno());
		f.setSinopse(getSinopse());
		f.setDiretores(getDiretores());
		f.setCriadores(getCriadores());
		f.setAtores(getAtores());
		f.setRating(getRating());
	}
	
	public void parseReviews() throws IOException
	{
		docReview = Jsoup.connect(urlReview.toString()).userAgent("Mozilla").get();	
		
		f.setReviews(getReviews());
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
		return elements.get(0).ownText();
	}
	
	private List<String> getDiretores()
	{
		Element pai = doc.getElementsByAttributeValue("itemprop", "director").get(0);
		Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
		
		List<String> res = new ArrayList<>();
		
		for (Element e : lista)
		{
			res.add(e.ownText());
		}
		
		return res;
	}
	
	private List<String> getCriadores()
	{
		Element pai = doc.getElementsByAttributeValue("itemprop", "creator").get(0);
		Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
		
		List<String> res = new ArrayList<>();
		
		for (Element e : lista)
		{
			res.add(e.ownText());
		}
		
		return res;
	}
	
	private List<String> getAtores()
	{
		Element pai = doc.getElementsByAttributeValue("itemprop", "actors").get(0);
		Elements lista = pai.getElementsByAttributeValue("itemprop", "name");
		
		List<String> res = new ArrayList<>();
		
		for (Element e : lista)
		{
			res.add(e.ownText());
		}
		
		return res;
	}
	
	private float getRating()
	{
		Elements elements = doc.getElementsByAttributeValueContaining("itemprop", "ratingValue");
		return Float.parseFloat(elements.get(0).ownText());
	}
	
	//parsing da pagina de reviews
	private List<Review> getReviews()
	{
		List<Review> res = new ArrayList<>();
		
		/* Todos os titulos dos revview estão dentro de <h2> e, esta tag nao eh mais utilizada, entao usaremos
		 * os <h2> como base para encontrar as informacoes dos reviews */
		Elements elements = docReview.getElementsByTag("h2");
		
		//varremos os <h2>
		for (Element e : elements)
		{
			//chama o metodo para fazer o parsing do <h2>
			Review r = parseReviewElement(e.parent());
			res.add(r);
			
			System.out.println(r);
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
		r.setData(walkNode(autorTag, 4).ownText());
		
		//parseia a nota
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
}
