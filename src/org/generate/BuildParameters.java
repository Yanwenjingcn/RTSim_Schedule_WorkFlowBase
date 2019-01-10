package org.generate;

/**
 * 
 * @ClassName: BuildParameters
 * @Description: 构造DAG图生成的默认参数
 * @author YWJ
 * @date 2017-9-9 下午3:23:04
 */
public class BuildParameters {
	public static int timeWindow = 40000;// 时间窗 默认200000
	public static int taskAverageLength = 40;// 任务的平均长度（20,30,40,50 默认值30）
	public static int dagAverageSize = 40;// dag的平均大小（20，30，40，50 默认值30）
	public static int dagLevelFlag = 2;// (1,2,3)代表([3,sqrt(N-2)],[sqrt(N-2),sqrt(N-2)+4],[sqrt(N-2),N-2])
	public static double deadLineTimes = 1.2;// deadline的倍数值 （1.2,1.5,2.0）
	public static int processorNumber = 8;// 处理单元的个数
	public static double singleDAGPercent = 0.5;// 单个DAG产生的概率

	public static double getSingleDAGPercent() {
		return singleDAGPercent;
	}

	public static void setSingleDAGPercent(double singleDAGPercent) {
		BuildParameters.singleDAGPercent = singleDAGPercent;
	}

	// public int proceesorEndTime = timeWindow/processorNumber;
	public static int getTimeWindow() {
		return timeWindow;
	}

	public static void setTimeWindow(int timeWindow) {
		BuildParameters.timeWindow = timeWindow;
	}

	public static int getTaskAverageLength() {
		return taskAverageLength;
	}

	public static void setTaskAverageLength(int taskAverageLength) {
		BuildParameters.taskAverageLength = taskAverageLength;
	}

	public static int getDagAverageSize() {
		return dagAverageSize;
	}

	public static void setDagAverageSize(int dagAverageSize) {
		BuildParameters.dagAverageSize = dagAverageSize;
	}

	public static int getDagLevelFlag() {
		return dagLevelFlag;
	}

	public static void setDagLevelFlag(int dagLevelFlag) {
		BuildParameters.dagLevelFlag = dagLevelFlag;
	}

	public static double getDeadLineTimes() {
		return deadLineTimes;
	}

	public static void setDeadLineTimes(double deadLineTimes) {
		BuildParameters.deadLineTimes = deadLineTimes;
	}

	public static int getProcessorNumber() {
		return processorNumber;
	}

	public static void setProcessorNumber(int processorNumber) {
		BuildParameters.processorNumber = processorNumber;
	}

}
