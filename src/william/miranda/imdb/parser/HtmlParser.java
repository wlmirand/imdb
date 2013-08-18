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

public class HtmlParser
{
	private URL url;
	private Document doc;
	
	public HtmlParser(String url) throws MalformedURLException
	{
		this.url = new URL(url);
	}
	
	public Filme parseURL() throws IOException
	{
		Filme f = new Filme();
		doc = Jsoup.connect(url.toString()).get();
		
		f.setTitulo(getTitulo());
		f.setAno(getAno());
		f.setSinopse(getSinopse());
		f.setDiretores(getDiretores());
		f.setCriadores(getCriadores());
		f.setAtores(getAtores());
		f.setRating(getRating());
		
		return f;
	}
	
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
}
