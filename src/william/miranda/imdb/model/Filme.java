package william.miranda.imdb.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Parser;

import william.miranda.imdb.parser.Utils;

public class Filme
{
	private String titulo;
	private int ano;
	private String sinopse;
	private List<String> diretores;
	private List<String> criadores;
	private List<String> atores;
	private float rating;
	private List<Review> reviews;
	private String storyline;
	private List<String> genres;
	private List<String> plotKeywords;
	private String imdbUrl;
	private int id;
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(ano).append(" - ").append(titulo).append('\n');
		sb.append("Rating: ").append(rating).append('\n');
		sb.append("Generos: ").append(genres).append('\n');
		sb.append("Palavras-chave: ").append(plotKeywords).append('\n');
		sb.append("Sinopse: ").append(sinopse).append('\n');
		sb.append("Diretores: ").append(diretores).append('\n');
		sb.append("Criadores: ").append(criadores).append('\n');
		sb.append("Atores: ").append(atores).append('\n');
		sb.append("Storyline: ").append(storyline).append('\n');
		sb.append("---------------------------------------------------------------\n");
		sb.append("Reviews: ").append("\n").append(reviews).append('\n');
		
		return sb.toString();
	}
	
	//Dada uma lista de Filmes, grava um arquivo contendo os XMLs gerados
	public static void toXML(List<Filme> listaFilmes)
	{
		String start = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		Document doc = Jsoup.parse(start, "", Parser.xmlParser());
		
		//extrai a tag raiz
		Element filmes = doc.appendElement("filmes");
		
		//para cada Filme, adicionamos no XML
		for (Filme f : listaFilmes)
		{
			filmes.append(f.toXML().toString());
		}
		
		
		Path path = Paths.get("resultado.xml");
		Utils.saveToFile(path, doc.toString());
	}
	
	//constroi um XML que representa o filme
	public Element toXML()
	{
		String start = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		Document doc = Jsoup.parse(start, "", Parser.xmlParser());
		
		//extrai a tag raiz
		Element filme = doc.appendElement("filme");
		
		//adiciona os campos basicos
		filme.appendElement("id").appendText(String.valueOf(this.getId()));
		filme.appendElement("imdbUrl").appendText(String.valueOf(this.getImdbUrl()));
		filme.appendElement("ano").appendText(String.valueOf(this.getAno()));
		filme.appendElement("titulo").appendText(this.getTitulo());
		filme.appendElement("rating").appendText(String.valueOf(this.getRating()));
		filme.appendElement("sinopse").appendText(this.getSinopse());
		filme.appendElement("storyline").appendText(this.getStoryline());
		
		//adiciona o genero
		Element generos = filme.appendElement("generos");
		for (String s : this.getGenres())
		{
			generos.appendElement("genero").appendText(s);
		}
		
		//adiciona as palavras chaves
		Element keywords = filme.appendElement("keywords");
		for (String s : this.getPlotKeywords())
		{
			keywords.appendElement("keyword").appendText(s);
		}
		
		//adiciona os diretores
		Element diretores = filme.appendElement("diretores");
		for (String s : this.getDiretores())
		{
			diretores.appendElement("diretor").appendText(s);
		}
		
		//adiciona os escritores (criadores)
		Element escritores = filme.appendElement("escritores");
		for (String s : this.getCriadores())
		{
			escritores.appendElement("escritor").appendText(s);
		}
		
		//adiciona os atores
		Element atores = filme.appendElement("atores");
		for (String s : this.getAtores())
		{
			atores.appendElement("ator").appendText(s);
		}
		
		//adiciona os reviews
		Element reviews = filme.appendElement("reviews");
		for (Review r : this.getReviews())
		{
			Element review = reviews.appendElement("review");
			
			review.appendElement("titulo").appendText(r.getTitulo());
			review.appendElement("autor").appendText(r.getAutor());
			review.appendElement("data").appendText(r.printData());
			review.appendElement("conteudo").appendText(r.getConteudo());
		}
		
		//a variavel doc possui o codigo do arquivo XML
		return filme;
	}
	
	public String getTitulo() {
		return titulo;
	}
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public String getSinopse() {
		return sinopse;
	}
	public void setSinopse(String sinopse) {
		this.sinopse = sinopse;
	}
	public List<String> getDiretores() {
		return diretores;
	}
	public void setDiretores(List<String> diretores) {
		this.diretores = diretores;
	}
	public List<String> getCriadores() {
		return criadores;
	}
	public void setCriadores(List<String> criadores) {
		this.criadores = criadores;
	}
	public List<String> getAtores() {
		return atores;
	}
	public void setAtores(List<String> atores) {
		this.atores = atores;
	}
	public float getRating() {
		return rating;
	}
	public void setRating(float rating) {
		this.rating = rating;
	}

	public List<Review> getReviews() {
		return reviews;
	}

	public void setReviews(List<Review> reviews) {
		this.reviews = reviews;
	}
	
	public void addReviews(List<Review> reviews) {
		this.reviews.addAll(reviews);
	}

	public String getStoryline() {
		return storyline;
	}

	public void setStoryline(String storyline) {
		this.storyline = storyline;
	}

	public List<String> getGenres() {
		return genres;
	}

	public void setGenres(List<String> genres) {
		this.genres = genres;
	}

	public List<String> getPlotKeywords() {
		return plotKeywords;
	}

	public void setPlotKeywords(List<String> plotKeywords) {
		this.plotKeywords = plotKeywords;
	}
	
	public String getImdbUrl() {
		return imdbUrl;
	}

	public void setImdbUrl(String imdbUrl) {
		this.imdbUrl = imdbUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
