package william.miranda.imdb.parser;

public class ResultadoPredicao
{
	private int userId;
	private int filmeId;
	private int notaOriginal;
	private double notaPredita;
	
	public ResultadoPredicao(int userId, int filmeId, int notaOriginal, double notaPredita)
	{
		this.userId = userId;
		this.filmeId = filmeId;
		this.notaOriginal = notaOriginal;
		this.notaPredita = notaPredita;
	}
	
	@Override
	public String toString()
	{
		return userId + "\t" + filmeId + "\t" + notaOriginal + "\t" + notaPredita;
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
}
