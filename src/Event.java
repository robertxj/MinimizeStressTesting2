public class Event {
	private String type;
	private long threadID;
	private String sharedVariable;
	private Position position;
	Event(String line) {
		// remove the ();
		line = line.substring(1, line.length() - 1);
		// split
		String[] str = line.split(",");

		String type = str[0];
		String threadID = str[1];
		String sharedVariable = str[2];
		String[] str3 = sharedVariable.split(":");
		sharedVariable = str3[0];
		String position = str[3];
		position = position.substring(1, position.length() - 1);
		String[] str2 = position.split(":");
		String fileName = str2[0];
		String lineNum = str2[1];

		Position p = new Position(fileName,
				Integer.parseInt(lineNum));
		
		this.threadID = Long.parseLong(threadID);
		this.sharedVariable = sharedVariable;
		this.type = type;
		this.position = p;
	}
	Event(String t,long tId, String s){
		threadID = tId;
		sharedVariable = s;
		type = t;
	}
	Event(String t,long tId, String s, Position p){
		threadID = tId;
		sharedVariable = s;
		type = t;
		position = p;
	}
	public long getThreadID(){
		return threadID;
	}
	public String getSharedVariable(){
		return sharedVariable;
	}
	public String getType(){
		return type;
	}
	public Position getPosition(){
		return position;
	}
//	public String toString(){
//		return Long.toString(threadId)+","+sharedVariable+","+type+","+Long.toString(eventId)+","+position.toString();
//	}
	public String toString(){
		return "(" + type +","+Long.toString(threadID)+","+sharedVariable+","+"("+position.toString()+")"+")";
	}
	public boolean equals(Object o){
		if(this ==o)
			return true;
		if(o.getClass()==Event.class){
			Event e = (Event)o;
			return e.position.equals(position)&&e.sharedVariable.equals(sharedVariable);
		}
		return false;
	}
	public int hashCode(){
		return position.hashCode()+sharedVariable.hashCode();
	}
}
