package william.miranda.lucene;


public class LuceneResult implements Comparable<LuceneResult>
{
	private int id;
	private String titulo;
	private float similaridade;
	
	public LuceneResult(int id, String titulo, float similaridade)
	{
		this.id = id;
		this.titulo = titulo;
		this.similaridade = similaridade;
	}
	
	@Override
	public String toString()
	{
		return id + " - " + titulo + " - " + similaridade;
	}
	
	//ordena por similaridade na ordem reversa (do maior pro menor)
	@Override
	public int compareTo(LuceneResult o)
	{
		if (o.similaridade > this.similaridade)
			return -1;
		else if (o.similaridade < this.similaridade)
			return 1;
		else
			return 0;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (!(o instanceof LuceneResult))
			return false;
		
		LuceneResult lr = (LuceneResult) o;
		
		if (lr.id == this.id)
			return true;
		else
			return false;
	}
}
