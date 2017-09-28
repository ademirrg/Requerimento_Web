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
		driver.get("https://aplic.inmetrics.com.br//requerimento/content/login.php");
		WebElement user = driver.findElement(By.id("fun_Id"));
		user.sendKeys(cpf);
		WebElement pass = driver.findElement(By.id("fun_Senha"));
		pass.sendKeys(cpf);
		WebElement btContinuar = driver.findElement(By.id("btnSubmitLogn"));
		btContinuar.click();
		WebElement table = driver.findElement(By.xpath("//*[@id=\"frequencia\"]/tbody/tr[2]/td/table[1]"));
		String saldoBH  = table.findElement(By.xpath("//*[@id=\"frequencia\"]/tbody/tr[2]/td/table[1]/tbody/tr[9]/td")).getText();
		String saldoBHSeparado[] = saldoBH.split(":");
		saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst("0", "");
		saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst(" ", "");
		saldoBH = saldoBHSeparado[1] + ":" + saldoBHSeparado[2];
		saldoBH.trim();
		//saldoBH = "22:00";
		
		if(saldoBH.startsWith("-")){
			System.out.println("Numero negativo");
			float hora = Float.parseFloat(saldoBHSeparado[1]);
			float minutos = Float.parseFloat(saldoBHSeparado[2]);
			hora = hora * 60;
			hora = hora - minutos;
			System.out.println(hora);
		}
		
		else{
			System.out.println("Numero positivo");
			float hora = Float.parseFloat(saldoBHSeparado[1]);
			float minutos = Float.parseFloat(saldoBHSeparado[2]);
			hora = hora * 60;
			hora = hora + minutos;
			System.out.println(hora);
		}
		
		
		
		
		
	
		
		
	}
	
}
