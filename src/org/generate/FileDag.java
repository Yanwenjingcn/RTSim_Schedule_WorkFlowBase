package org.generate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 
* @ClassName: FileDag 
* @Description: ��DAG���ɽ��д�뵽txt�ļ��У����ݶ��ǲ�һ���ġ�
* @author YWJ
* @date 2017-9-9 ����3:12:56
 */
public class FileDag {
   private String filePath;
   private String basePath = System.getProperty("user.dir")+"\\DAG_TXT\\";
   private File file;
   private FileWriter fileWriter;
   private List<String> nodeIdList;
  
   public void clearDir()
   {
	   file = new File(basePath);
	   String[] fileNames =file.list();
	   if(fileNames!=null)
	   {
	    File tmp;
	    for(int i = 0;i < fileNames.length;i++)     	
	    	{
	    	tmp = new File(basePath+fileNames[i]);
	    	tmp.delete();
	    	}
	   } 	
   }
    
   /** ��DAGд��TXT�ļ�
	 * @param dag DAG�ļ�
	 */
   public void writeData(Random_Dag dag){	   
	   try {
		   
		   filePath =basePath+dag.dagId+".txt";	

		   nodeIdList = new ArrayList<String>();
		   file = new File(filePath);
		   fileWriter = new FileWriter(file, true);
		 //��һ��д��Dag��size �ύʱ�� ��ֹʱ��
		   fileWriter.write(dag.taskList.size()+" "+dag.submitTime+" "+dag.deadlineTime);
		   fileWriter.write("\r\n");
		   for(TaskNode node:dag.taskList)
		   {
			   nodeIdList.add(node.nodeId);
		   }
		   for(DagEdge dagEdge:dag.edgeList)
		   {
			   fileWriter.append(nodeIdList.indexOf(dagEdge.head.nodeId)+" "+nodeIdList.indexOf(dagEdge.tail.nodeId)+" "+dagEdge.transferData);
			   fileWriter.append("\r\n");
		   }
		   
		   fileWriter.flush();
		   fileWriter.close();
		   
		   String path = "DAG_TXT/"+dag.dagId+"_.txt";
		   PrintStream out = System.out;
		   PrintStream ps=new PrintStream(new FileOutputStream(path));
		   System.setOut(ps);   //�ض��������  	
		   int num = 0;
		   
		   
		   for(TaskNode node:dag.taskList)
		   {
			   System.out.println(num+" "+(node.taskLength));
			   num++;
		   }		

			ps.close();
			System.setOut(out); 
		   
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
   }  
 
}