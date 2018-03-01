package requerimento;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;

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
	String versao = "1.1";
	
	public void digitaCPF(){
		cpf = JOptionPane.showInputDialog(null, "Digite seu CPF:", "Consulta Banco de Horas", JOptionPane.QUESTION_MESSAGE);
		
		if(cpf == null){
			System.exit(0);
		}
		else if(cpf.length()== 0){
			JOptionPane.showMessageDialog(null, "Campo CPF em branco!", "ERRO", JOptionPane.ERROR_MESSAGE);
			digitaCPF();
		}
		else if(cpf.equalsIgnoreCase("INFO")){
			JOptionPane.showMessageDialog(null, "APLICAÇÃO CRIADA POR: ADEMIR ROCHA GARCIA\n"
					+ "DATA DE CRIAÇÃO: 27/09/2017\n"
					+ "VERSÃO: " + versao, "INFORMAÇÕES", JOptionPane.INFORMATION_MESSAGE);
			digitaCPF();
		}
		else if(cpf.length() > 0 && cpf.length() < 11 || cpf.length() > 11){
			JOptionPane.showMessageDialog(null, "O Campo CPF deve conter 11 caracteres!", "ERRO", JOptionPane.ERROR_MESSAGE);
			digitaCPF();
		}
	}
	
	public void abreBrowser(){
		try{
			System.setProperty("webdriver.chrome.driver","C:\\chromedriver.exe");
			driver = new ChromeDriver();
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
		}
		catch(IllegalStateException e){
			JOptionPane.showMessageDialog(null, "Ops!\nParece que você não tem o chromedriver em sua máquina!"
					+ "\nPara utilizar esta aplicação, baixe a versão mais recente do chromedriver no link abaixo:"
					+ "\n<html><span style ='color:blue'>https://sites.google.com/a/chromium.org/chromedriver/downloads</span></html>"
					+ "\nApós baixar o arquivo chromedriver.exe, coloque-o no diretório C: e execute a aplicação."
					+ "\nClique em OK e o link será copiado para sua área de transferência, após isso, "
					+ "apenas cole (Ctrl+V) na página do seu navegador.", "ATENÇÃO", JOptionPane.WARNING_MESSAGE);	
			String link = "https://sites.google.com/a/chromium.org/chromedriver/downloads";
			StringSelection selectlLink = new StringSelection(link);
			Clipboard areaTransfer = Toolkit.getDefaultToolkit().getSystemClipboard();
			areaTransfer.setContents(selectlLink, null);
			System.exit(0);
		}
	}	
	
	public void fechaBrowser(){
		driver.quit();
	}
	
	public void login(){
		try{
			driver.get("https://aplic.inmetrics.com.br//requerimento/content/login.php");
			WebElement user = driver.findElement(By.id("fun_Id"));
			user.sendKeys(cpf);
			WebElement pass = driver.findElement(By.id("fun_Senha"));
			pass.sendKeys(cpf);
			WebElement btContinuar = driver.findElement(By.id("btnSubmitLogn"));
			btContinuar.click();	
			boolean userInvalido = driver.getPageSource().contains("Problemas! Usuário ou senha divergente!");
			boolean trocaPeriodo = driver.getPageSource().contains("Nenhum registro encontrado");
			
			if(userInvalido == true){
				driver.quit();
				JOptionPane.showMessageDialog(null, "Problemas ao realizar login!\nVerifique se o CPF informado está correto: " 
						+ cpf, "ERRO", JOptionPane.ERROR_MESSAGE);
				Principal.main(new String[]{});
				System.exit(0);
			}
			else if(trocaPeriodo == true){
				JOptionPane.showMessageDialog(null, "Cálculo de horas ainda não disponível devido a troca recente de período!"
						+ "\nSerá consultado o período anterior.", "ATENÇÃO", JOptionPane.WARNING_MESSAGE);
				consultaPeriodoAnterior();
				System.exit(0);
			}
		}
		catch(NoSuchElementException e){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Erro ao processar solicitação de login!"
					+ "\nProblemas de conexão com a internet ou elemento não encontrado na página.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		catch(org.openqa.selenium.NoSuchWindowException | NullPointerException e2){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Browser fechado!\nA aplicação será encerrada.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
		
	public void consultaHoras(){	
		//Saldo do BH
		try{
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
				if(saldoBHSeparado[1].contains("-") && horaBH == 0){
					saldoBHSeparado[2] = "-" + saldoBHSeparado[2];
					minutosBH = Integer.parseInt(saldoBHSeparado[2]);
				}
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
		catch(NoSuchElementException e){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Erro ao processar solicitação de consulta do banco de horas!"
					+ "\nProblemas de conexão com a internet ou elemento não encontrado na página.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		catch(org.openqa.selenium.NoSuchWindowException | NullPointerException e2){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Browser fechado!\nA aplicação será encerrada.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
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
		
		else if(horaBH < 0){
			horaFinal = Integer.toString(horaCalc);
			minutosFinal = Integer.toString(minutosCalc);
			
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
	
	public void consultaPeriodoAnterior(){
		try{
			//Altera período
			WebElement altPeriodo = driver.findElement(By.xpath("//*[@id=\"frequencia\"]/tbody/tr[3]/td/input[2]"));
			altPeriodo.click();
			GregorianCalendar cal = new GregorianCalendar();
			int mes = cal.get(Calendar.MONTH);
			int ano = cal.get(Calendar.YEAR);
			Integer.toString(mes);
			Integer.toString(ano);
			if(mes == 0){
				ano = ano - 1;
				mes = 12;
			}
			String mesPeriodo = mes + "/" + ano;
			if(mesPeriodo.length() < 7){
				mesPeriodo = "0" + mesPeriodo;
			}
			Select cbPeriodo = new Select(driver.findElement(By.name("refCompetencia")));
			cbPeriodo.selectByValue(mesPeriodo);
			WebElement btAltPeriodo = driver.findElement(By.xpath("/html/body/form/table/tbody/tr[2]/td/table/tbody/tr[3]/td/input[1]"));
			btAltPeriodo.click();
			consultaHoras();
			driver.quit();
			calculaHoras();
		}
		catch(NoSuchElementException e){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Erro ao processar solicitação de alteração de período!"
					+ "\nProblemas de conexão com a internet ou elemento não encontrado na página.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
		catch(org.openqa.selenium.NoSuchWindowException | NullPointerException e2){
			driver.quit();
			JOptionPane.showMessageDialog(null, "Browser fechado!\nA aplicação será encerrada.", "ERRO", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}
	}
}
