package william.miranda.lucene;

import william.miranda.imdb.model.Filme;

/**
 * Nesta classe, pegamos um filme inicial e procuramos similares atraves de seus metadados,
 * gerando listas
 * @author william.miranda
 *
 */
public class LuceneSearch
{
	private Filme filme;
	private LuceneDatabase luceneDB;
	
	public LuceneSearch(Filme filme, LuceneDatabase luceneDB)
	{
		this.filme = filme;
		this.luceneDB = luceneDB;
	}
	
	public void buscarGeneros()
	{
		
	}
}
