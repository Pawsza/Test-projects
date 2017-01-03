package pf;

import java.util.HashMap;
import java.util.Map;

import map.map;
import utils.AStar;
import utils.labGen;
import utils.tools;

public class main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		
		int passed = 0, failed = 0;
		
		for(int i=0; i <10; i++){
		
			long startTime;
			HashMap<String, Long> times = new HashMap<String, Long>();

			startTime = System.currentTimeMillis();
			map m = new map(101, 5);
			times.put("Stworzenia mapy", System.currentTimeMillis()-startTime);
			
			startTime = System.currentTimeMillis();
			labGen.generate(m.getTiles());
			times.put("Wygenerowanie labiryntu", System.currentTimeMillis()-startTime);
			
			startTime = System.currentTimeMillis();
			int[] cor_end = labGen.generateExit(m.getTiles());
			int[] cor_start = labGen.generateActor(m.getTiles());
			times.put("Utworzenie aktora i wyjœcia", System.currentTimeMillis()-startTime);
	
			startTime = System.currentTimeMillis();
			if (!AStar.findWay(m.getTiles(), cor_start[0], cor_start[1], cor_end[0], cor_end[1])){
				tools.applayVisited(m.getTiles(), AStar.getVisited());
				failed++;
			}else passed++;
			times.put("A*", System.currentTimeMillis()-startTime);
			
			tools.applayPathOnMap(m.getTiles(), AStar.getPath());
			
			startTime = System.currentTimeMillis();
			tools.displayMap(m.getTiles());
			times.put("Wyœwietlenie", System.currentTimeMillis()-startTime);
			
			System.out.println();
			for(Map.Entry<String, Long> entry : times.entrySet()) {
			    String key = entry.getKey();
			    Long value = entry.getValue();
	
			    System.out.println(value + "ms \t" + key);
			}
		
		}
		System.out.print("Passed: "+passed+"\tFailed: "+failed);
	}
}
