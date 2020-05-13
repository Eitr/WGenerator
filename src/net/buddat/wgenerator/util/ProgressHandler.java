package net.buddat.wgenerator.util;

import java.awt.Taskbar;

import javax.swing.*;

import net.buddat.wgenerator.MainWindow;

public class ProgressHandler {

	private JProgressBar progressBar;
	private JLabel lblMemory;
	
	private Taskbar taskbar;
	private MainWindow mainWindow;
	
	public ProgressHandler(JProgressBar progress, JLabel memory, Taskbar taskbar, MainWindow mainWindow) {
		this.progressBar = progress;
		this.lblMemory = memory;
		this.taskbar = taskbar;
		this.mainWindow = mainWindow;
	}
	
	private void setMemoryUsage() {
		double totalMemory = (int)((Runtime.getRuntime().maxMemory())/1024.0/1024/1024*100)/100.0;
		int usedMemory = (int)(((Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())*100/Runtime.getRuntime().maxMemory())); 
		lblMemory.setText(usedMemory+"% used of "+totalMemory+"gb");
	}
	
	public void update(int value) {
		update(value,getText());
	}
	
	public void update(int value, String text) {
		progressBar.setValue(value);
		progressBar.setString(text);
		taskbar.setWindowProgressValue(mainWindow, value);
		if(value == 100) {
			mainWindow.setTitle(Constants.WINDOW_TITLE + " - " + "Ready");
		}else {
			mainWindow.setTitle(Constants.WINDOW_TITLE + " - " + text + ": " + value + "%");
		}
		
		setMemoryUsage();
	}
	
	public String getText() {
		return progressBar.getString();
	}
}
