import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ResultFile {
	boolean result;
	ResultFile(String file){
		try {
			BufferedReader input =  new BufferedReader(new FileReader(file));
			try {
				String result = input.readLine();
				if(result.equals("passing")){
					this.result = true;
				}else if(result.equals("failing")){
					this.result = false;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		try {
			//存放运行结果 passing/failing
			BufferedWriter output = new BufferedWriter(new FileWriter("result.txt"));
			//output.write("passing");
			output.write("failing");
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ResultFile r = new ResultFile("result.txt");
		System.out.println(r.result);
	}
}