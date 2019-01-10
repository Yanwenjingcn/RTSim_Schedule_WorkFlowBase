package org.algorithm.result;

import java.io.File;
import java.io.IOException;

import org.generate.BuildParameters;
import org.generate.DagBuilder;

/**
 * 
* @ClassName: ResultDirGenerate
* @Description: 生成结果文件夹
* @author YanWenjing
* @date 2017-9-22 下午1:06:27
 */
public class ResultDirGenerate {


	static File fileResult;
	static File resultTxt;
	
	static String fifo="fifo.txt";
	static String edf="edf.txt";
	static String stf="stf.txt";
	static String etf="etf.txt";
	static String mergeDag="mergeDag.txt";
	static String fillback="fillback.txt";
	
	static String OrderAsBtoS="OrderAsBtoS.txt";
	static String OrderAsHEFT="OrderAsHEFT.txt";
	static String OrderAsIDWithAdaptation="OrderAsIDWithAdaptation.txt";
	static String OrderAsOriId="OrderAsOriId.txt";
	static String OrderAsStoB="OrderAsStoB.txt";
	
	static String OrderAsBtoSWithAdaptation="OrderAsBtoSWithAdaptation.txt";
	static String OrderAsStoBWithAdaptation="OrderAsStoBWithAdaptation.txt";

	

	
	

	/**
	 * @throws IOException 
	 * @Title: main
	 * @Description: TODO
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws IOException {

		int[] dagAverageSize = { 20, 40, 60 };
		for (int i = 0; i < dagAverageSize.length; i++) {
			BuildParameters.setDagAverageSize(dagAverageSize[i]);
			String basePathResult="G:\\DagCasesResult\\dagAverageSize"+ dagAverageSize[i] + "\\";
			cycle(basePathResult);

		}
		
		
		int[] dagLevelFlag = { 1,2,3 };
		for (int i = 0; i < dagLevelFlag.length; i++) {
			BuildParameters.setDagLevelFlag(dagLevelFlag[i]);
			String basePathResult="G:\\DagCasesResult\\dagLevelFlag"+ dagLevelFlag[i] + "\\";
			cycle(basePathResult);
		}
		

		
		// 处理单元的个数（2,4,8,16,32）String processorNumber = String.valueOf(BuildParameters.processorNumber);
		int[] processorNumber = {4,8,16 };
		for (int i = 0; i < processorNumber.length; i++) {
			BuildParameters.setProcessorNumber(processorNumber[i]);
			String basePathResult="G:\\DagCasesResult\\processorNumber"+ processorNumber[i] + "\\";
			cycle(basePathResult);
		}
		

		// 任务的平均长度（20,30,40,50 默认值30）
		//String taskAverageLength = String.valueOf(BuildParameters.taskAverageLength);
		
		int[] taskAverageLength = { 20, 40, 60 };
		for (int i = 0; i < taskAverageLength.length; i++) {
			BuildParameters.setTaskAverageLength(taskAverageLength[i]);
			
			String basePathResult="G:\\DagCasesResult\\taskAverageLength"+ taskAverageLength[i] + "\\";
			cycle(basePathResult);
		}
		
		
		
		
		// deadline的倍数值 （1.1，1.3，1.6，2.0）
		//String deadLineTimes = String.valueOf(BuildParameters.deadLineTimes);

	
		double[] deadLineTimes = {1.2,1.5,1.8};
		
		for (int i = 0; i < deadLineTimes.length; i++) {
			BuildParameters.setDeadLineTimes(deadLineTimes[i]);
			
			String basePathResult="G:\\DagCasesResult\\deadLineTimes"+ deadLineTimes[i] + "\\";
			cycle(basePathResult);
			
		}
		
		double[] singleDAGPercent = {0.2,0.5,0.8};
		for (int i = 0; i < singleDAGPercent.length; i++) {
			BuildParameters.setSingleDAGPercent(singleDAGPercent[i]);
			String basePathResult="G:\\DagCasesResult\\singleDAGPercent"+ singleDAGPercent[i] + "\\";
			cycle(basePathResult);	
		}
		
		
		
		System.out.println("执行结束");
	}


	/**
	 * @throws IOException 
	 * 
	* @Title: cycle
	* @Description: TODO
	* @param @param basePathResult
	* @return void
	* @throws
	 */
	public static void cycle(String basePathResult) throws IOException {

		String pathResult = basePathResult;
		fileResult=new File(pathResult);
		fileResult.mkdirs();
		
		
//		resultTxt=new File(basePathResult+fifoResult);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+edfResult);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+stfResult);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+etfResult);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+lrebResult);
//		resultTxt.createNewFile();
		
		
//		resultTxt=new File(basePathResult+OrderAsBtoS);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsHEFT);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsIDWithAdaptation);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsOriId);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsStoB);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsBtoSWithAdaptation);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+OrderAsStoBWithAdaptation);
//		resultTxt.createNewFile();
		
		
		
//		resultTxt=new File(basePathResult+fifo);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+stf);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+edf);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+etf);
//		resultTxt.createNewFile();
//		
//		resultTxt=new File(basePathResult+mergeDag);
//		resultTxt.createNewFile();
		
		resultTxt=new File(basePathResult+fillback);
		resultTxt.createNewFile();
		
	}

}
