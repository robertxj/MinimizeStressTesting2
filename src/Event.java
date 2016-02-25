public class Event {
	private String type;
	private int threadID;
	private String sharedVariable;
	private String methodName;
	private Position position;
	private int iterationID = -1;
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
		methodName = sharedVariable + str3[1];
		String position = str[3];
		position = position.substring(1, position.length() - 1);
		String[] str2 = position.split(":");
		String fileName = str2[0];
		String lineNum = str2[1];
		String iterationID = str[4];

		Position p = new Position(fileName,
				Integer.parseInt(lineNum));
		
		this.threadID = Integer.parseInt(threadID);
		this.sharedVariable = sharedVariable;
		this.type = type;
		this.position = p;
		this.iterationID = Integer.parseInt(iterationID);
	}
	Event(String t,int tId, String s){
		threadID = tId;
		sharedVariable = s;
		type = t;
	}
	Event(String t,int tId, String s, Position p){
		threadID = tId;
		sharedVariable = s;
		type = t;
		position = p;
	}
	Event(String t,int tId, String s, Position p, int iId){
		threadID = tId;
		sharedVariable = s;
		type = t;
		position = p;
		iterationID = iId;
	}
	public int getThreadID(){
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
	public int getIterationID() {
		return iterationID;
	}
	public String getMethodName() {
		return methodName;
	}
//	public String toString(){
//		return Long.toString(threadId)+","+sharedVariable+","+type+","+Long.toString(eventId)+","+position.toString();
//	}
	public String toString(){
		return "(" + type +","+Long.toString(threadID)+","+sharedVariable+","+"("+position.toString()+")"+ "," + iterationID +")";
	}
	public boolean equals(Object o){
		if(this ==o)
			return true;
		if(o.getClass()==Event.class){
			Event e = (Event)o;
			return e.type.equals(type) && e.position.equals(position)&&e.sharedVariable.equals(sharedVariable);
		}
		return false;
	}
	public int hashCode(){
		return type.hashCode() + position.hashCode()+sharedVariable.hashCode();
	}
}
