package requerimento;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
		saldoBH.trim();
		String saldoBHSeparado[] = saldoBH.split(":");
		saldoBH = saldoBHSeparado[1] + ":" + saldoBHSeparado[2];
		
		try {
			SimpleDateFormat t = new SimpleDateFormat("hh:mm a");
			Date d = t.parse(saldoBH);
			System.out.println(saldoBH + ">>>>>" + d);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
