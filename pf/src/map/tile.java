package map;

public class tile {
	
	public enum TileType {
		EXIT,
		ACTOR,
		PATH,
		BLOCK,
		EMPTY,
		VISITED
	}
	
	private boolean canPassBy;
	//private int speed = 1;
	
	TileType type;
	
	public tile(boolean canPassBy){
		this.canPassBy = canPassBy;
	}
	
//	public tile(boolean canPassBy, int speed){
//		this.canPassBy = canPassBy;
//	}
	{
		type = TileType.BLOCK;
	}
	
	public boolean canPassBy(){
		return canPassBy;
	}
	
	public void setColideable(boolean canPassBy){
		if(canPassBy)type = TileType.EMPTY;
		this.canPassBy = canPassBy;
	}
	
	public void setType(TileType type){
		if ( this.type == TileType.EMPTY )this.type = type;
	}
	
	public boolean isExit(){
		return type == TileType.EXIT;
	}
	public boolean isActor(){
		return type == TileType.ACTOR;
	}
	public boolean isPath(){
		return type == TileType.PATH;
	}
	public boolean isEmpty(){
		return type == TileType.EMPTY;
	}
	public boolean isVisited(){
		return type == TileType.VISITED;
	}
	public TileType getType(){
		return type;
	}
}
