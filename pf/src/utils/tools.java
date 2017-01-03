package utils;

import java.util.ArrayList;

import map.tile;

public class tools {

	public static void displayMap(tile[][] map){
		for(int y=0; y<map[0].length+2;y++){
			for(int x=0; x<map.length+2; x++){
				if (x == 0 || y == 0 ||
					x == map.length + 1 || y == map[0].length + 1){
					
					System.out.print('#');
					continue;
				}
				
				if (map[x-1][y-1].isExit()){
					System.out.print('E');
				}
				else if (map[x-1][y-1].isActor()){
					System.out.print('A');
				}
				else if (map[x-1][y-1].isPath()){
					System.out.print('o');
				}
				else if (map[x-1][y-1].isVisited()){
					System.out.print('.');
				}
				else if(map[x-1][y-1].canPassBy()){
					System.out.print(' ');
				}
				
				else{
					System.out.print('#');
				}
			}
			if (y != map[0].length+1)System.out.println();
		}
	}
	
	public static tile[][] applayPathOnMap(tile[][] map, ArrayList<int[]> path){
		
		for(int[] step : path){
			map[step[0]][step[1]].setType(tile.TileType.PATH);
		}
		
		return map;
	}
	
	public static tile[][] applayVisited(tile[][] map, ArrayList<int[]> visited){
		
		for(int[] step : visited){
			map[step[0]][step[1]].setType(tile.TileType.VISITED);
		}
		
		return map;
	}
	
}
