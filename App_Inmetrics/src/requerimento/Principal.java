package requerimento;

public class Principal {
	
	public static void main(String[] args) {
		ConsultaBH bh = new ConsultaBH();
		bh.digitaCPF();
		bh.abreBrowser();
		bh.login();
		bh.consultaHoras();
		bh.fechaBrowser();
		bh.calculaHoras();
	}
}
