public class Position {
	String filename;
	int line;
	Position(String s, int l){
		filename = s;
		line = l;
	}
//	public String toString(){
//		return filename +","+ Integer.toString(line);
//	}
	public String toString(){
		return filename +":"+ Integer.toString(line);
	}
	public boolean equals(Object o){
		if(this ==o)
			return true;
		if(o.getClass()==Position.class){
			Position p = (Position)o;
			return p.filename.equals(filename)&&(p.line==line);
		}
		return false;
	}
	public int hashCode(){
		return filename.hashCode()+line;
	}
}
