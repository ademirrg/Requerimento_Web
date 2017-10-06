package requerimento;

import java.util.concurrent.TimeUnit;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class ConsultaBH {
	
	WebDriver driver;
	String cpf="";
	int horaBH;
	int minutosBH;
	int horaExtra;
	int minutosExtra;
	int horaAtraso;
	int minutosAtraso;
	int horaFalta;
	int horaCalc = 0;
	int minutosCalc = 0;
	String horaFinal;
	String minutosFinal;
	String saldoFinal;
	JFormattedTextField tCPF;
	
	public void digitaCPF(){
		cpf = JOptionPane.showInputDialog(null, "Digite seu CPF:","Consulta Banco de Horas", JOptionPane.QUESTION_MESSAGE);
		
		if(cpf.length() < 0){
			System.exit(0);
		}
		else if(cpf.length()== 0){
			JOptionPane.showMessageDialog(null, "Campo CPF em branco!", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		else if(cpf.length() > 0 && cpf.length() < 11 || cpf.length() > 11){
			JOptionPane.showMessageDialog(null, "O Campo CPF deve conter 11 caracteres!", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
	
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
		
		if(saldoBH.length()==0){
			saldoBH = "00:00";
			horaBH = 0;
			minutosBH = 0;
		}
		
		else{
			String saldoBHSeparado[] = saldoBH.split(":");
			saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst("0", "");
			saldoBHSeparado[1] = saldoBHSeparado[1].replaceFirst(" ", "");
			saldoBH = saldoBHSeparado[1] + ":" + saldoBHSeparado[2];
			saldoBH.trim();
			horaBH = Integer.parseInt(saldoBHSeparado[1]);
			minutosBH = Integer.parseInt(saldoBHSeparado[2]);
		}
		
		//T.HE
		String saldoHoraExtra = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[5]")).getText();
		
		if(saldoHoraExtra.length()==0){
			saldoHoraExtra = "00:00";
			horaExtra = 0;
			minutosExtra = 0;
		}
		
		else{
			String saldoHoraExtraSep[] = saldoHoraExtra.split(":");
			horaExtra = Integer.parseInt(saldoHoraExtraSep[0]);
			minutosExtra = Integer.parseInt(saldoHoraExtraSep[1]);
		}
		
		//T.Atrasos
		String saldoAtraso = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[6]")).getText();
		
		if(saldoAtraso.length()==0){
			saldoAtraso = "00:00";
			horaAtraso = 0;
			minutosAtraso = 0;
		}
		
		else{
			String saldoAtrasoSep[] = saldoAtraso.split(":");
			horaAtraso = Integer.parseInt(saldoAtrasoSep[0]);
			minutosAtraso = Integer.parseInt(saldoAtrasoSep[1]);
		}
		
		//T.Faltas
		String saldoFaltas = driver.findElement(By.xpath("//*[@id=\"tbDivergencias\"]/tbody/tr/td[7]")).getText();
		
		if(saldoFaltas.length()==0){
			saldoFaltas = "00:00";
			horaFalta = 0;
		}
		
		else{
			String saldoFaltasSep[] = saldoFaltas.split(":");
			horaFalta = Integer.parseInt(saldoFaltasSep[0]);
		}
	}
	
	public void calculaHoras(){	
		//Cálculo das horas
		if(horaBH < 0){
			horaBH = horaBH * 60;
			horaBH = horaBH - minutosBH;
		}
		
		else{
			horaBH = horaBH * 60;
			horaBH = horaBH + minutosBH;
		}
		
		horaExtra = horaExtra * 60 + minutosExtra;
		horaBH = horaBH + horaExtra;
		horaAtraso = horaAtraso * 60 + minutosAtraso;
		horaBH = horaBH - horaAtraso;
		horaFalta = horaFalta * 60;
		horaBH = horaBH - horaFalta;

		if(horaBH < 0){
			for(int h = horaBH; h < -59; h = h + 60){
				horaCalc = horaCalc -1;
				minutosCalc = h + 60;
			}
			
			if(horaBH >= -59){
				minutosCalc = horaBH;
			}
		}
		
		else{
			for(int h = horaBH; h > 59; h = h - 60){
				horaCalc = horaCalc + 1;
				minutosCalc = h - 60;
			}
				
			if(horaBH <= 59){
				minutosCalc = horaBH;
			}
		}
		
		if(minutosCalc < 0){
			if(horaCalc == 0 && minutosCalc < 0){
				horaFinal = "-" + Integer.toString(horaCalc);
				minutosFinal = Integer.toString(minutosCalc).replaceAll("-", "");
				
				if(minutosFinal.length() < 2){
					minutosFinal = "0" + minutosFinal;
					saldoFinal = horaFinal + ":" + minutosFinal;
					JOptionPane.showMessageDialog(null, "Seu saldo: " + 
							saldoFinal, "Consulta Banco de Horas", JOptionPane.ERROR_MESSAGE);
				}
				else{
					saldoFinal = horaFinal + ":" + minutosFinal;
					JOptionPane.showMessageDialog(null, "Seu saldo: " + 
							saldoFinal, "Consulta Banco de Horas", JOptionPane.ERROR_MESSAGE);
				}
			}
			else{	
				horaFinal = Integer.toString(horaCalc);
				minutosFinal = Integer.toString(minutosCalc).replaceAll("-", "");
			
				if(minutosFinal.length() < 2){
					minutosFinal = "0" + minutosFinal;
					saldoFinal = horaFinal + ":" + minutosFinal;
					JOptionPane.showMessageDialog(null, "Seu saldo: " + 
							saldoFinal, "Consulta Banco de Horas", JOptionPane.ERROR_MESSAGE);
				}
				else{
					saldoFinal = horaFinal + ":" + minutosFinal;
					JOptionPane.showMessageDialog(null, "Seu saldo: " + 
							saldoFinal, "Consulta Banco de Horas", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
		
		else{
			horaFinal = Integer.toString(horaCalc);
			minutosFinal = Integer.toString(minutosCalc);
			
			if(minutosFinal.length() < 2){
				minutosFinal = "0" + minutosFinal;
				saldoFinal = horaFinal + ":" + minutosFinal;
				JOptionPane.showMessageDialog(null, "Seu saldo: " + 
						saldoFinal, "Consulta Banco de Horas", JOptionPane.INFORMATION_MESSAGE);
			}
			else{
				saldoFinal = horaFinal + ":" + minutosFinal;
				JOptionPane.showMessageDialog(null, "Seu saldo: " + 
						saldoFinal, "Consulta Banco de Horas", JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
	}	
}
