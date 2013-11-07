package william.miranda.imdb.parser;

public class ResultadoPredicao
{
	//atributos principais
	private int userId;
	private int filmeId;
	private int notaOriginal;
	private double notaPredita;
	
	//atributos auxiliares, utilizados na interpretação dos resultados
	private int numFilmesSimilares;
	private int numFilmesSimilaresSkipados;
	
	public ResultadoPredicao(int userId, int filmeId, int notaOriginal, double notaPredita)
	{
		this.userId = userId;
		this.filmeId = filmeId;
		this.notaOriginal = notaOriginal;
		this.notaPredita = notaPredita;
		this.numFilmesSimilares = 0;
		this.numFilmesSimilaresSkipados = 0;
	}
	
	public ResultadoPredicao(int userId, int filmeId, int notaOriginal, double notaPredita, int numFilmesSimilares, int numFilmesSimilaresSkipados)
	{
		this(userId, filmeId, notaOriginal, notaPredita);
		this.numFilmesSimilares = numFilmesSimilares;
		this.numFilmesSimilaresSkipados = numFilmesSimilaresSkipados;
	}
	
	@Override
	public String toString()
	{
		return userId + "\t" + filmeId + "\t" + notaOriginal + "\t" + notaPredita + "\t" + numFilmesSimilares + "\t" + numFilmesSimilaresSkipados;
	}
	
	/* gets */
	public int getUserId() {
		return userId;
	}

	public int getFilmeId() {
		return filmeId;
	}

	public int getNotaOriginal() {
		return notaOriginal;
	}

	public double getNotaPredita() {
		return notaPredita;
	}
	
	public double getNumFilmesSimilares() {
		return numFilmesSimilares;
	}
	
	public double getNumFilmesSimilaresSkipados() {
		return numFilmesSimilaresSkipados;
	}
}
