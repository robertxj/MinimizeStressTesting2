import java.io.*;
import java.lang.ProcessBuilder.Redirect;
import java.util.*;

public class Sequence {
	List<Event> events;
//	List<Long> threadIDs;
	String traceFilePath;
	// begin with 1
	Set<Integer> removedThreadIDs;
	Set<Pair> removedIterationIDs;
	Set<Triple> removedMethodIDs;

	// total numbers
	int threads;
	int iterations;
	int methods;

	public Sequence(String inputFile, int threads, int iterations, int methods) {
		events = new ArrayList<Event>();
//		threadIDs = new ArrayList<>();
		traceFilePath = inputFile;
		removedThreadIDs = new HashSet<>();
		removedIterationIDs = new HashSet<>();
		removedMethodIDs = new HashSet<>();
		this.threads = threads;
		this.iterations = iterations;
		this.methods = methods;
		try {
			// use buffering, reading one line at a time
			// FileReader always assumes default encoding is OK!
			BufferedReader input = new BufferedReader(new FileReader(inputFile));
			try {
				String line = null; // not declared within while loop
				while ((line = input.readLine()) != null) {
					Event e = new Event(line);
					events.add(e);
				}
				
			} finally {
				input.close();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	// minimizeThreads(1, seq.threadIDs.size() - 1); since main thread is the first thread with index 0;
	// firstThread and lastThread are the index of the second thread and the last thread.
	public void minimizeThreads(int firstThread, int lastThread) {
		if (firstThread > lastThread) {
			return;
		}
		
		removeThreads(firstThread, lastThread);
		
		String[] commandAndOptions_RuntimeExec = {"java", "-jar", "-Dmode=localize", "MyAccount.jar"};
		ProcessBuilder pb = new ProcessBuilder(commandAndOptions_RuntimeExec);
		
		try {
			File log = new File("log");
			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(log));
			
			Process process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// res is the result of the execution 
		ResultFile resultFile = new ResultFile("result.txt");
		boolean res = resultFile.result;
		System.out.println(res);
		
		String newTraceFilePath = null;
		if (!res) {
			for (int i = firstThread; i < lastThread + 1; i++) {
				removedThreadIDs.add(i + 1);
			}
		} else {
			if (firstThread < lastThread) {
				int midThread = (firstThread + lastThread) / 2;
				minimizeThreads(firstThread, midThread);
			    minimizeThreads(midThread + 1, lastThread);
			}
		}
	}

	// firstThread and lastThread are the index of the index inclusively
	public void removeThreads(int firstThread, int lastThread) {
		Set<Integer> removedThreads = new HashSet<>(removedThreadIDs);
		for (int i = firstThread; i < lastThread + 1; i++) {
			removedThreads.add(i + 1);
		}
	
		BufferedWriter bwRMThreads = null;
		try {
			bwRMThreads = new BufferedWriter(new FileWriter("removing thread" + removedThreads.hashCode() + ".txt"));
			for(int removedThread : removedThreads) {
				bwRMThreads.write(Integer.toString(removedThread));
				bwRMThreads.newLine();
			}
			bwRMThreads.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwRMThreadsTrace = null;
		try {
			bwRMThreadsTrace = new BufferedWriter(new FileWriter("removing threads trace" + removedThreads.hashCode()  + ".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			BufferedReader input = new BufferedReader(new FileReader(traceFilePath));
			String line=null;
			try {
				while((line = input.readLine() )!= null){
					Event event = new Event(line);
					if (event.getType().equals("entrymethod") || event.getType().equals("exitmethod")) {
						continue;
					}
					boolean isRemovedJoiner = false;
					for (int threadID : removedThreads) {
						if (event.getSharedVariable().equals("Joiner" + threadID)) {
							isRemovedJoiner = true;
						}
					}
					if (!removedThreads.contains(event.getThreadID()) && !isRemovedJoiner) {
						bwRMThreadsTrace.write(line);
						bwRMThreadsTrace.newLine();
					}
					
				}
				bwRMThreadsTrace.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File source = new File("removing threads trace" + removedThreads.hashCode()  + ".txt");
	    File target = new File("localize-replay.txt");
	    
		copyWithStreams(source, target, false);
		
		File source2 = new File("removing thread" + removedThreads.hashCode() + ".txt");
	    File target2 = new File("removingThreads.txt");
	    copyWithStreams(source2, target2, false);
	    
	}
	
	// minimizeIterations(0, numOfIterations - 1);
	// thread is the index of the thread which is going to remove iterations
	// firstIteration and lastIteration are the index of the first iteration and the last iteration.
	public void minimizeIterations(int thread, int firstIteration, int lastIteration) {
		// if this thread has been removed, we just return
		if (removedThreadIDs.contains(thread + 1)) {
			return;
		}
		
		if (firstIteration > lastIteration) {
			return;
		}
		
		removeIterations(thread, firstIteration, lastIteration);
		
		String[] commandAndOptions_RuntimeExec = {"java", "-jar", "-Dmode=localize", "MyAccount.jar"};
		ProcessBuilder pb = new ProcessBuilder(commandAndOptions_RuntimeExec);
		
		try {
			File log = new File("log");
			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(log));
			
			Process process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// res is the result of the execution 
		ResultFile resultFile = new ResultFile("result.txt");
		boolean res = resultFile.result;
		System.out.println(res);
		
		String newTraceFilePath = null;
		if (!res) {
			for (int i = firstIteration; i < lastIteration + 1; i++) {
				removedIterationIDs.add(new Pair(thread+1, i+1));
			}
		} else {
			if (firstIteration < lastIteration) {
				int midIteration = (firstIteration + lastIteration) / 2;
				minimizeIterations(thread, firstIteration, midIteration);
				minimizeIterations(thread, midIteration + 1, lastIteration);
			}
		}
	}
	
	// firstIteration and lastIteration are the index of the index inclusively
	public void removeIterations(int thread, int firstIteration, int lastIteration) {
		
		Set<Pair> removedIterations = new HashSet<>(removedIterationIDs);
		for (int i = firstIteration; i < lastIteration + 1; i++) {
			removedIterations.add(new Pair(thread+1, i+1));
		}
	
		BufferedWriter bwRMIterations = null;
		try {
			bwRMIterations = new BufferedWriter(new FileWriter("removing iterations" + removedIterations.hashCode() + ".txt"));
			for(Pair removedPair : removedIterations) {
				bwRMIterations.write(removedPair.toString());
				bwRMIterations.newLine();
			}
			bwRMIterations.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwRMIterationsTrace = null;
		try {
			bwRMIterationsTrace = new BufferedWriter(new FileWriter("removing iterations trace" + removedIterations.hashCode()  + ".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Set<Integer> removedIterationThreads = new HashSet<>();
		Map<Integer, Set<Integer>> removedIterationsMap = new HashMap<>();
		for (Pair p : removedIterations) {
			if (removedIterationsMap.containsKey(p.thread)) {
				removedIterationsMap.get(p.thread).add(p.iteration);
			} else {
				Set<Integer> set = new HashSet<Integer>();
				set.add(p.iteration);
				removedIterationsMap.put(p.thread, set);
			}
		}
		try {
			BufferedReader input = new BufferedReader(new FileReader(traceFilePath));
			String line=null;
			try {
				while((line = input.readLine() )!= null){
					
					Event event = new Event(line);
					boolean isRemovedJoiner = false;
					for (int threadID : removedThreadIDs) {
						if (event.getSharedVariable().equals("Joiner" + threadID)) {
							isRemovedJoiner = true;
						}
					}
					if (!removedThreadIDs.contains(event.getThreadID()) && !isRemovedJoiner) {
						// Only write events not in removed iterations
						if (event.getType().equals("entrymethod")) {
							if (removedIterationsMap.containsKey(event.getThreadID()) && removedIterationsMap.get(event.getThreadID()).contains(event.getIterationID())) {
								removedIterationThreads.add(event.getThreadID());
							}
							continue;
						} else if (event.getType().equals("exitmethod")) {
							removedIterationThreads.remove(event.getThreadID());
							continue;
						}
						if (!removedIterationThreads.contains(event.getThreadID())) {
							bwRMIterationsTrace.write(line);
							bwRMIterationsTrace.newLine();
						}
						
						
					}
					
				}
				bwRMIterationsTrace.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File source = new File("removing iterations trace" + removedIterations.hashCode()  + ".txt");
	    File target = new File("localize-replay.txt");
	    
		copyWithStreams(source, target, false);
		
		File source2 = new File("removing iterations" + removedIterations.hashCode() + ".txt");
	    File target2 = new File("removingIterations.txt");
	    copyWithStreams(source2, target2, false);
	    
	}
	
	
	public void minimizeMethods(int thread, int iteration, int firstMethod, int lastMethod) {		
		// if this thread has been removed, we just return
		if (removedThreadIDs.contains(thread + 1)) {
			return;
		}
		// if this iteration has been removed, we just return
		if (removedIterationIDs.contains(new Pair(thread + 1, iteration + 1))) {
			return;
		}
		
		if (firstMethod > lastMethod) {
			return;
		}
		
		removeMethods(thread, iteration, firstMethod, lastMethod);
		
		String[] commandAndOptions_RuntimeExec = {"java", "-jar", "-Dmode=localize", "MyAccount.jar"};
		ProcessBuilder pb = new ProcessBuilder(commandAndOptions_RuntimeExec);
		
		try {
			File log = new File("log");
			pb.redirectErrorStream(true);
			pb.redirectOutput(Redirect.appendTo(log));
			
			Process process = pb.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// res is the result of the execution 
		ResultFile resultFile = new ResultFile("result.txt");
		boolean res = resultFile.result;
		System.out.println(res);
		
		String newTraceFilePath = null;
		if (!res) {
			for (int i = firstMethod; i < lastMethod + 1; i++) {
				removedMethodIDs.add(new Triple(thread + 1, iteration + 1, i+1));
			}
		} else {
			if (firstMethod < lastMethod) {
				int midMethod = (firstMethod + lastMethod) / 2;
				minimizeMethods(thread, iteration, firstMethod, midMethod);
			    minimizeMethods(thread, iteration, midMethod + 1, lastMethod);
			}
		}
	}
	
	// firstIteration and lastIteration are the index of the index inclusively
	public void removeMethods(int thread, int iteration, int firstMethod, int lastMethod) {
		
		Set<Triple> removedMethods = new HashSet<>(removedMethodIDs);
		for (int i = firstMethod; i < lastMethod + 1; i++) {
			removedMethods.add(new Triple(thread+1, iteration+1, i+1));
		}
	
		BufferedWriter bwRMMethods = null;
		try {
			bwRMMethods = new BufferedWriter(new FileWriter("removing methods" + removedMethods.hashCode() + ".txt"));
			for(Triple removedTriple : removedMethods) {
				bwRMMethods.write(removedTriple.toString());
				bwRMMethods.newLine();
			}
			bwRMMethods.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		BufferedWriter bwRMMethodsTrace = null;
		try {
			bwRMMethodsTrace = new BufferedWriter(new FileWriter("removing methods trace" + removedMethods.hashCode()  + ".txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO remove events from trace
	
		Set<Integer> removedIterationThreads = new HashSet<>();
		Map<Integer, Set<Integer>> removedIterationsMap = new HashMap<>();
		for (Pair p : removedIterationIDs) {
			if (removedIterationsMap.containsKey(p.thread)) {
				removedIterationsMap.get(p.thread).add(p.iteration);
			} else {
				Set<Integer> set = new HashSet<Integer>();
				set.add(p.iteration);
				removedIterationsMap.put(p.thread, set);
			}
		}
		Map<Integer, Integer> threadToIteration = new HashMap<>();
		Map<Integer, Map<Integer, String>> threadToIterationToMethod = new HashMap<>();
		try {
			BufferedReader input = new BufferedReader(new FileReader(traceFilePath));
			String line=null;
			try {
				int lineNum = 0;
				while((line = input.readLine() )!= null){
					lineNum++;
					Event event = new Event(line);
					boolean isRemovedJoiner = false;
					for (int threadID : removedThreadIDs) {
						if (event.getSharedVariable().equals("Joiner" + threadID)) {
							isRemovedJoiner = true;
						}
					}
					if (!removedThreadIDs.contains(event.getThreadID()) && !isRemovedJoiner) {
						// Only write events not in removed iterations
						if (event.getType().equals("entrymethod")) {
							if (removedIterationsMap.containsKey(event.getThreadID()) && removedIterationsMap.get(event.getThreadID()).contains(event.getIterationID())) {
								removedIterationThreads.add(event.getThreadID());
							}
//							if (threadToIteration.containsKey(event.getThreadID())) {
//								threadToIteration.put(event.getThreadID(), threadToIteration.get(event.getThreadID())+1);
//							} else {
//								threadToIteration.put(event.getThreadID(), 1);
//							}
							threadToIteration.put(event.getThreadID(), event.getIterationID());
							if (!threadToIterationToMethod.containsKey(event.getThreadID())) {
								Map<Integer, String> map = new HashMap<>();
								map.put(threadToIteration.get(event.getThreadID()), event.getMethodName());
								threadToIterationToMethod.put(event.getThreadID(), map);
							} else {
								Map<Integer, String> map = threadToIterationToMethod.get(event.getThreadID());
								map.put(threadToIteration.get(event.getThreadID()), event.getMethodName());
							}
							
							continue;
						} else if (event.getType().equals("exitmethod")) {
							removedIterationThreads.remove(event.getThreadID());
							continue;
						}
						if (!removedIterationThreads.contains(event.getThreadID())) {
							
							Map<String, Integer> methodNameToID = new HashMap<>();
							methodNameToID.put("ac1deposit", 1);
							methodNameToID.put("ac1withdraw", 2);
							methodNameToID.put("ac1transfer", 3);
							methodNameToID.put("ac2deposit", 4);
							methodNameToID.put("ac2withdraw", 5);
							methodNameToID.put("ac2transfer", 6);
							int eventThreadID = event.getThreadID();
							if (eventThreadID == 1) {
								bwRMMethodsTrace.write(line);
								bwRMMethodsTrace.newLine();
							} else {
								int eventIterationID = threadToIteration.get(eventThreadID);
								int eventMethodID = methodNameToID.get(threadToIterationToMethod.get(eventThreadID).get(eventIterationID));
								if (!removedMethods.contains(new Triple(eventThreadID, eventIterationID, eventMethodID))) {
								//	System.out.println(lineNum);
								//	System.out.println(line);
									bwRMMethodsTrace.write(line);
									bwRMMethodsTrace.newLine();
								}	
							}
							
							
						}
						
						
					}
					
				}
				bwRMMethodsTrace.flush();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		File source = new File("removing methods trace" + removedMethods.hashCode()  + ".txt");
	    File target = new File("localize-replay.txt");
	    
		copyWithStreams(source, target, false);
		
		File source2 = new File("removing methods" + removedMethods.hashCode() + ".txt");
	    File target2 = new File("removingMethods.txt");
	    copyWithStreams(source2, target2, false);
	    
	}

	private void copyWithStreams(File aSourceFile, File aTargetFile, boolean aAppend) {
	    InputStream inStream = null;
	    OutputStream outStream = null;
	    try{
	      try {
	        byte[] bucket = new byte[32*1024];
	        inStream = new BufferedInputStream(new FileInputStream(aSourceFile));
	        outStream = new BufferedOutputStream(new FileOutputStream(aTargetFile, aAppend));
	        int bytesRead = 0;
	        while(bytesRead != -1){
	          bytesRead = inStream.read(bucket); //-1, 0, or more
	          if(bytesRead > 0){
	            outStream.write(bucket, 0, bytesRead);
	          }
	        }
	      }
	      finally {
	        if (inStream != null) inStream.close();
	        if (outStream != null) outStream.close();
	      }
	    }
	    catch (FileNotFoundException ex){
	      
	    }
	    catch (IOException ex){
	      
	    }
	  }
	
	public static void main(String[] args) {
		Sequence seq = new Sequence("trace.txt", 5, 4, 6);
		seq.minimizeThreads(1, 4);
		System.out.println(seq.removedThreadIDs.toString());
		for (int i = 1; i < 5; i++) {
			seq.minimizeIterations(i, 0, 3);
		}
		System.out.println(seq.removedIterationIDs.toString());
		for (int i = 1; i < 5; i++) {
			for (int j = 0; j < 4; j++) {
				seq.minimizeMethods(i, j, 0, 5);
			}
		} 
		System.out.println(seq.removedMethodIDs.toString());
	}

}