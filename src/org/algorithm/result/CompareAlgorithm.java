package org.algorithm.result;

import org.compare.early.Fillback;
import org.compare.early.MergeDAGConstrainedEarlyStart;
import org.compare.early.OrderAsBtoS;
import org.compare.early.OrderAsBtoSWithAdaptation;
import org.compare.early.OrderAsHEFT;
import org.compare.early.OrderAsIDWithAdaptation;
import org.compare.early.OrderAsOriId;
import org.compare.early.OrderAsStoB;
import org.compare.early.OrderAsStoBWithAdaptation;
import org.generate.BuildParameters;
import org.schedule.Makespan;

public class CompareAlgorithm {

	/**
	 * @throws Throwable
	 * @Title: main
	 * @Description: TODO
	 * @param @param args
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) throws Throwable {

		singleDAGPercent();
		dagAverageSize();
		dagLevelFlag();
		processorNumber();
		taskAverageLength();
		deadLineTimes();


	}
	
	public static void singleDAGPercent() throws Throwable {
		double[] singleDAGPercent = { 0.2, 0.5, 0.8 };
		for (int i = 0; i < singleDAGPercent.length; i++) {
			BuildParameters.setSingleDAGPercent(singleDAGPercent[i]);
			String basePathXML = "G:\\DagCasesXML\\singleDAGPercent"
					+ singleDAGPercent[i] + "\\";
			String resultPath = "G:\\DagCasesResult\\singleDAGPercent"
					+ singleDAGPercent[i] + "\\";
			runAlgorithm(basePathXML, resultPath);
			
			BuildParameters.setSingleDAGPercent(0.5);
		}
	}



	public static void deadLineTimes() throws Throwable {
		// deadline的倍数值 （1.1，1.3，1.6，2.0）
		// String deadLineTimes = String.valueOf(BuildParameters.deadLineTimes);

		double[] deadLineTimes = { 1.2,1.5,1.8};

		for (int i = 0; i < deadLineTimes.length; i++) {
			BuildParameters.setDeadLineTimes(deadLineTimes[i]);

			String basePathXML = "G:\\DagCasesXML\\deadLineTimes" + deadLineTimes[i] + "\\";

			String resultPath = "G:\\DagCasesResult\\deadLineTimes" + deadLineTimes[i] + "\\";

			runAlgorithm(basePathXML, resultPath);

			BuildParameters.setDeadLineTimes(1.5);

		}
	}

	public static void taskAverageLength() throws Throwable {
		// 任务的平均长度（20,30,40,50 默认值30）
		// String taskAverageLength =
		// String.valueOf(BuildParameters.taskAverageLength);

		int[] taskAverageLength = { 20,  40, 60 };
		//int[] taskAverageLength = {60 };
		for (int i = 0; i < taskAverageLength.length; i++) {
			BuildParameters.setTaskAverageLength(taskAverageLength[i]);

			String basePathXML = "G:\\DagCasesXML\\taskAverageLength" + taskAverageLength[i] + "\\";

			String resultPath = "G:\\DagCasesResult\\taskAverageLength" + taskAverageLength[i] + "\\";

			runAlgorithm(basePathXML, resultPath);

			BuildParameters.setTaskAverageLength(40);
		}
	}

	public static void processorNumber() throws Throwable {
		// 处理单元的个数（2,4,8,16,32）String processorNumber =
		// String.valueOf(BuildParameters.processorNumber);

		int[] processorNumber = { 4, 8, 16 };
		for (int i = 0; i < processorNumber.length; i++) {
			BuildParameters.setProcessorNumber(processorNumber[i]);

			String basePathXML = "G:\\DagCasesXML\\processorNumber" + processorNumber[i] + "\\";

			String resultPath = "G:\\DagCasesResult\\processorNumber" + processorNumber[i] + "\\";

			runAlgorithm(basePathXML, resultPath);

			BuildParameters.setProcessorNumber(8);
		}
	}

	public static void dagLevelFlag() throws Throwable {
		int[] dagLevelFlag = { 1, 2, 3 };

		for (int i = 0; i < dagLevelFlag.length; i++) {
			BuildParameters.setDagLevelFlag(dagLevelFlag[i]);

			String basePathXML = "G:\\DagCasesXML\\dagLevelFlag" + dagLevelFlag[i] + "\\";

			String resultPath = "G:\\DagCasesResult\\dagLevelFlag" + dagLevelFlag[i] + "\\";

			runAlgorithm(basePathXML, resultPath);

			BuildParameters.setDagLevelFlag(2);
		}
	}

	public static void dagAverageSize() throws Throwable {
		int[] dagAverageSize = { 20,  40, 60};
		for (int i = 0; i < dagAverageSize.length; i++) {

			BuildParameters.setDagAverageSize(dagAverageSize[i]);
			// XML文件放置的位置
			String basePathXML = "G:\\DagCasesXML\\dagAverageSize" + dagAverageSize[i] + "\\";

			// 结果输出的位置
			String resultPath = "G:\\DagCasesResult\\dagAverageSize" + dagAverageSize[i] + "\\";

			runAlgorithm(basePathXML, resultPath);

			BuildParameters.setDagAverageSize(40);
		}
	}

	public static void runAlgorithm(String basePathXML, String resultPath) throws Throwable {

		// 每种情况运行100次，100次的结果输出到一个文件就好。

		for (int i = 0; i < 50; i++) {
			
			String pathXML = basePathXML;
			pathXML = basePathXML + i + "\\";
			
//			Makespan ms = new Makespan();
//			ms.runMakespan_xml(pathXML, resultPath);
//			
//			MergeDAGConstrainedEarlyStart mergeDAGConstrainedEarlyStart= new MergeDAGConstrainedEarlyStart();
//			mergeDAGConstrainedEarlyStart.runMakespan(pathXML, resultPath);
			
			Fillback fillback=new Fillback();
			fillback.runMakespan(pathXML, resultPath);

		}

	}
	
	

}
