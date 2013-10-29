package william.miranda.lucene;

import java.util.ArrayList;
import java.util.List;

import william.miranda.imdb.model.Filme;

public class LuceneSearch 
{
	private Filme filmeInicial;
	private LuceneDatabase luceneDB;
	
	private List<LuceneResult> listaSimilaridadeGeneros = new ArrayList<>();
	private List<LuceneResult> listaSimilaridadeAtores = new ArrayList<>();
	private List<LuceneResult> listaSimilaridadeCriadores = new ArrayList<>();
	private List<LuceneResult> listaSimilaridadeDiretores = new ArrayList<>();
	private List<LuceneResult> listaSimilaridadeKeywords = new ArrayList<>();
	
	public LuceneSearch(Filme filmeInicial, LuceneDatabase luceneDB)
	{
		this.filmeInicial = filmeInicial;
		this.luceneDB = luceneDB;
		
		//calcula as listas de similaridades
		getGenerosSimilares();
		getAtoresSimilares();
		getCriadoresSimilares();
		getDiretoresSimilares();
		getKeywordsSimilares();
	}
	
	private void getGenerosSimilares()
	{
		String query = "";
		
		for (String s : filmeInicial.getGenres())
		{
			query += s + " AND ";
		}
		
		query = query.substring(0, query.length()-5);
		listaSimilaridadeGeneros = luceneDB.searchIndex(query);
	}
	
	private void getAtoresSimilares()
	{
		String query = "";
		
		for (String s : filmeInicial.getAtores())
		{
			query += s + " OR ";
		}
		
		query = query.substring(0, query.length()-4);
		listaSimilaridadeAtores = luceneDB.searchIndex(query);
	}
	
	private void getCriadoresSimilares()
	{
		String query = "";
		
		for (String s : filmeInicial.getCriadores())
		{
			query += s + " OR ";
		}
		
		query = query.substring(0, query.length()-4);
		
		listaSimilaridadeCriadores = luceneDB.searchIndex(query);
	}
	
	private void getDiretoresSimilares()
	{
		String query = "";
		
		for (String s : filmeInicial.getDiretores())
		{
			query += s + " OR ";
		}
		
		query = query.substring(0, query.length()-4);
		
		listaSimilaridadeDiretores = luceneDB.searchIndex(query);
	}
	
	private void getKeywordsSimilares()
	{
		String query = "";
		
		for (String s : filmeInicial.getPlotKeywords())
		{
			query += s + " OR ";
		}
		
		query = query.substring(0, query.length()-4);
		
		listaSimilaridadeKeywords = luceneDB.searchIndex(query);
	}

	public Filme getFilmeInicial() {
		return filmeInicial;
	}

	public List<LuceneResult> getListaSimilaridadeGeneros() {
		return listaSimilaridadeGeneros;
	}

	public List<LuceneResult> getListaSimilaridadeAtores() {
		return listaSimilaridadeAtores;
	}

	public List<LuceneResult> getListaSimilaridadeCriadores() {
		return listaSimilaridadeCriadores;
	}

	public List<LuceneResult> getListaSimilaridadeDiretores() {
		return listaSimilaridadeDiretores;
	}
	
	public List<LuceneResult> getListaSimilaridadeKeywords() {
		return listaSimilaridadeKeywords;
	}
}
