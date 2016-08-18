package bluecake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Scanner.WebScanner;

public class ScannerManager implements Runnable{
	private HashMap<String, HashMap<String, TradeInfo>> masterSourceMap;
	private List<WebScanner> scanners;
	public ScannerManager() {
		masterSourceMap = new HashMap<String, HashMap<String, TradeInfo>>();
		scanners= new ArrayList<WebScanner>();
	}

	
	
	@Override
	public void run() {
		
	}

}
