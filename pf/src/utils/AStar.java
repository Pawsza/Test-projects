package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import map.tile;

public class AStar {
	
	static ArrayList<int[]> visited;
	static ArrayList<int[]> path;
	
	public static boolean findWay(tile[][] tiles, int start_x, int start_y, int end_x,  int end_y){
		visited = new ArrayList<int[]>();
		path = new ArrayList<int[]>();
		return findWay(tiles, start_x, start_y, end_x, end_y, 0);
	}
	
	private static boolean findWay(tile[][] tiles, int start_x, int start_y, int end_x,  int end_y, int startSum){
		int weight = Math.abs(start_x - end_x) + Math.abs(start_y - end_y);
		for(int[] v_tab : visited){
			if ( v_tab[0] == start_x &&
					v_tab[1] == start_y)return false;
		}
		visited.add(new int[]{start_x, start_y});
		
		if (weight == 0) {
			addStepToPath(start_x, start_y);
			return true;
		}
		
		int min_k, min_v;
		boolean flag; 
		
		HashMap<Integer, Integer> waysWeight = new java.util.HashMap<Integer, Integer>();
		
		waysWeight.put(0, getWeightOf(tiles, start_x, start_y-1, end_x, end_y));
		waysWeight.put(1, getWeightOf(tiles, start_x+1, start_y, end_x, end_y));
		waysWeight.put(2, getWeightOf(tiles, start_x, start_y+1, end_x, end_y));
		waysWeight.put(3, getWeightOf(tiles, start_x-1, start_y, end_x, end_y));

		do{
			flag=false;
			min_v = 0;
			min_k = -1;
			for(Map.Entry<Integer, Integer> entry : waysWeight.entrySet()) {
			    int key = entry.getKey();
			    int value = entry.getValue();
	
			    if(value<0)continue;
			    if((value<min_v && value>0) || min_k == -1) {
			    	min_v = value;
			    	min_k = key;
			    	flag = true;
			    }
			}
			
			switch (min_k) {
			case 0:
				if (findWay(tiles, start_x, start_y-1, end_x, end_y, weight+startSum)){
					addStepToPath(start_x, start_y);
					return true;
				}
				break;
			case 1:
				if (findWay(tiles, start_x+1, start_y, end_x, end_y, weight+startSum)){
					addStepToPath(start_x, start_y);
					return true;
				}
				break;
			case 2:
				if (findWay(tiles, start_x, start_y+1, end_x, end_y, weight+startSum)){
					addStepToPath(start_x, start_y);
					return true;
				}
				break;
			case 3:
				if (findWay(tiles, start_x-1, start_y, end_x, end_y, weight+startSum)){
					addStepToPath(start_x, start_y);
					return true;
				}
				break;
			default:
			}
			waysWeight.put(min_k, -1);
		}while(flag);
		
		return false;
	}
	
	private static int getWeightOf(tile[][] tiles, int x, int y, int end_x, int end_y){

		if (x > tiles.length || x < 0) return -1;
		else if ( y > tiles.length || y < 0) return -1;
		
		try{	
			if (!tiles[x][y].canPassBy())return -1;
			for(int[] step : visited){
				if (step[0] == x && step[1] == y)return -1;
			}
		}
		catch(Exception e){
			return -1;
		}
		return Math.abs(x - end_x) + Math.abs(y - end_y);
	}
	
	private static void addStepToPath(int x, int y){
		path.add(new int[]{x, y});
	}
	
	public static ArrayList<int[]> getPath(){
		return path;
	}
	public static ArrayList<int[]> getVisited(){
		return visited;
	}
}
