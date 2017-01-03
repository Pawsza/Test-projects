package utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import map.tile;

public class labGen {

	static Random rnd = new Random();
	
	public static void generate(tile[][] map){
		
		int start_x, start_y;
		
		start_x = rnd.nextInt(map.length/2)*2;
		start_y = rnd.nextInt(map[0].length/2)*2;
		
		checkTile(start_x, start_y, -1, map);
	} 
	
	public static void checkTile(int x, int y, int wayFrom, tile[][] map){
		if (map[x][y].canPassBy())return;
		map[x][y].setColideable(true);
		
		switch (wayFrom) {
		case 0:
			map[x][y-1].setColideable(true);
			break;
		case 1:
			map[x-1][y].setColideable(true);
			break;
		case 2:
			map[x][y+1].setColideable(true);
			break;
		case 3:
			map[x+1][y].setColideable(true);
			break;

		default:
			break;
		}
		
		Set<Integer> visited = new HashSet<Integer>();
		int way;
		
		while(visited.size()<4){
			way = rnd.nextInt(4);
			
			if (visited.contains(way))continue;
			visited.add(way);
			try{
				switch(way){
					case 0: 
						checkTile(x, y+2, way, map);
						break;
					case 1: 
						checkTile(x+2, y, way, map);
						break;
					case 2: 
						checkTile(x, y-2, way, map);
						break;
					case 3: 
						checkTile(x-2, y, way, map);
						break;
					
				}
			}catch(Exception e){
				
			}
		}
	}
	
	public static int[] generateExit(tile[][] map){
		
		int exit_x, exit_y;
		
		do{
			exit_x = rnd.nextInt((map.length+1)/2)*2;
			exit_y = rnd.nextInt((map[0].length+1)/2)*2;
		}while (!map[exit_x][exit_y].isEmpty());
		
		map[exit_x][exit_y].setType(tile.TileType.EXIT);
		return new int[] {exit_x, exit_y};
	}
	
	public static int[] generateActor(tile[][] map){
		
		int exit_x, exit_y;
		
		do{
			exit_x = rnd.nextInt(map.length/2)*2;
			exit_y = rnd.nextInt(map[0].length/2)*2;
		}while (!map[exit_x][exit_y].isEmpty());
		
		map[exit_x][exit_y].setType(tile.TileType.ACTOR);
		return new int[] {exit_x, exit_y};
	}
}
