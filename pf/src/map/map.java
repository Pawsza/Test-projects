package map;

public class map {

	tile[][] tiles;
	
	public map(int width, int height){
		tiles= new tile[width][height];
		
		for(int y=0; y<tiles[0].length;y++){
			for(int x=0; x<tiles.length; x++){
				tiles[x][y] = new tile(false);
			}
		}
	}
	
	public tile[][] getTiles(){
		return tiles;
	}
}
