package requerimento;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ConsultaBH {
	
	WebDriver driver;
	String cpf = "36264448800";
	
	public void abreBrowser(){
		driver = new ChromeDriver();
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
	}
	
	public void fechaBrowser(){
		driver.quit();
	}
	
	public void consultaHoras(){
		//Login
		driver.get("https://aplic.inmetrics.com.br//requerimento/content/login.php");
		WebElement user = driver.findElement(By.id("fun_Id"));
		user.sendKeys(cpf);
		WebElement pass = driver.findElement(By.id("fun_Senha"));
		pass.sendKeys(cpf);
		WebElement btContinuar = driver.findElement(By.id("btnSubmitLogn"));
		btContinuar.click();
		
		//Saldo do BH
		WebElement tabelaSaldo = driver.findElement(By.xpath("//*[@id=\"frequencia\"]/tbody/tr[2]/td/table[1]"));
		String saldoBH  = tabelaSaldo.findElement(By.xpath("//*[@id=\"frequencia\"]/tbody/tr[2]/td/table[1]/tbody/tr[9]/td")).getText();
		String saldoBHSeparado[] = saldoBH.split(":");
		saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst("0", "");
		saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst(" ", "");
		saldoBH = saldoBHSeparado[1] + ":" + saldoBHSeparado[2];
		saldoBH.trim();
		float horaBH = Float.parseFloat(saldoBHSeparado[1]);
		float minutosBH = Float.parseFloat(saldoBHSeparado[2]);
		//saldoBH = "22:00";
		
		//T.HE
		String saldoHoraExtra = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[5]")).getText();
		String saldoHoraExtraSep[] = saldoHoraExtra.split(":");
		float horaExtra = Float.parseFloat(saldoHoraExtraSep[0]);
		float minutosExtra = Float.parseFloat(saldoHoraExtraSep[1]);
		
		//T.Atrasos
		String saldoAtraso = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[6]")).getText();
		String saldoAtrasoSep[] = saldoAtraso.split(":");
		float horaAtraso = Float.parseFloat(saldoAtrasoSep[0]);
		float minutosAtraso = Float.parseFloat(saldoAtrasoSep[1]);
		
		//T.Faltas
		String saldoFaltas = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[7]")).getText();
		String saldoFaltasSep[] = saldoFaltas.split(":");
		float horaFalta = Float.parseFloat(saldoFaltasSep[0]);
		
		if(saldoBH.startsWith("-")){
			System.out.println("Numero negativo");
			horaBH = horaBH * 60;
			horaBH = horaBH - minutosBH;
			horaExtra = horaExtra * 60 + minutosExtra;
			horaBH = horaBH + horaExtra;
			horaAtraso = horaAtraso * 60 + minutosAtraso;
			horaBH = horaBH - horaAtraso;
			horaFalta = horaFalta * 60;
			horaBH = horaBH - horaFalta;
			System.out.println(horaBH/60);
		}
		
		else{
			System.out.println("Numero positivo");
			horaBH = horaBH * 60;
			horaBH = horaBH + minutosBH;
			horaExtra = horaExtra * 60 + minutosExtra;
			horaBH = horaBH + horaExtra;
			horaAtraso = horaAtraso * 60 + minutosAtraso;
			horaBH = horaBH - horaAtraso;
			horaFalta = horaFalta * 60;
			horaBH = horaBH - horaFalta;
			System.out.println(horaBH/60);
		}
			
		
	}
	
}
