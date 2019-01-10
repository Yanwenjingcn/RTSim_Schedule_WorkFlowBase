package org.self;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Map.Entry;

import org.jdom.xpath.XPath;
import org.jdom.Attribute;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.schedule.DAG;
import org.schedule.DAGMap;
import org.schedule.DAGdepend;
import org.schedule.PE;
import org.schedule.PrintResult;
import org.schedule.computerability;
import org.schedule.slot;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hslf.exceptions.HSLFException;
import org.generate.BuildParameters;
import org.generate.RandomCreater;

/**
 * 
 * @ClassName: FillbacknewWithoutInsertBak
 * @Description: �ϲ���ʼ������ͬʱ���ύ������Ϊһ�����dagͼ
 * @author YanWenjing
 * @date 2017-10-21 ����7:45:26
 */
public class FillbacknewWithoutInsertEditionThree {

	/**
	 * 
	 */

	private static ArrayList<PE> PEList;
	private static ArrayList<DAGMap> DAGMapList;
	private static ArrayList<DAGMap> tempDAGMapList;

	private static ArrayList<DAG> DAG_queue;
	private static ArrayList<DAG> readyqueue;

	private static HashMap<Integer, Integer> DAGDependMap;
	private static HashMap<String, Double> DAGDependValueMap;

	private static ArrayList<DAG> DAG_queue_personal;
	private static HashMap<Integer, Integer> DAGDependMap_personal;
	private static HashMap<String, Double> DAGDependValueMap_personal;
	private static Map<Integer, int[]> ComputeCostMap;
	private static Map<Integer, Integer> AveComputeCostMap;

//	private static Map<Integer, DAG> DAGIdToDAGMap;
//	public static Map<Integer, double[]> DAGExeTimeMap;
//	private static Map<Integer, Double> upRankValueMap;
	private static Map<Integer, double[]> vmComputeCostMap;
	private static Map<Integer, Double> vmAveComputeCostMap;
//	private static Map<Integer, Integer[]> cloudletInVm;
//	private static Map<Integer, Integer> cloudletInVmId;

	// д���ļ��Ľ��
	public static String[][] rateResult = new String[1][4];

	private static int islastnum = 0;
	private static double deadLineTimes = 1.3;// deadline�ı���ֵ ��1.1��1.3��1.6��2.0��
	private static int pe_number = 8;

	public static String[][] rate = new String[5][2];

	public static int current_time;
	public static int proceesorEndTime = BuildParameters.timeWindow;// ʱ�䴰
	public static int timeWindow;
	public static int T = 1;

	public static int fillbacktasknum = 10;
	public static int[][] message;
	public static int dagnummax = 10000;
	public static int timewindowmax = 9000000;
	public static int mesnum = 5;
	private static HashMap<Integer, ArrayList> SlotListInPes;
	private static HashMap<Integer, HashMap> TASKListInPes;

	// �������ϵĺ��Ʊ��
	private static int[] pushFlag;
	// ��Ҫ���Ƶ�������
	private static int pushCount = 0;
	// ���Ƴɹ��ܴ���
	private static int pushSuccessCount = 0;

	// ��������Ŀ
	private static int taskTotal = 0;
	private static int[][] dagResultMap = null;
	
	//�ϲ�����ܳ���


	public FillbacknewWithoutInsertEditionThree() {
		readyqueue = new ArrayList<DAG>();
		DAG_queue = new ArrayList<DAG>();
		DAG_queue_personal = new ArrayList<DAG>();
		PEList = new ArrayList<PE>();
		DAGMapList = new ArrayList<DAGMap>();
		tempDAGMapList=new ArrayList<DAGMap>();
		DAGDependMap = new HashMap<Integer, Integer>();
		DAGDependValueMap = new HashMap<String, Double>();
		deadLineTimes = BuildParameters.deadLineTimes;
		pe_number = BuildParameters.processorNumber;
		current_time = 0;
		timeWindow = proceesorEndTime / pe_number;

		pushFlag = new int[pe_number];
		dagResultMap = new int[1000][dagnummax];

		message = new int[dagnummax][mesnum];
		SlotListInPes = new HashMap<Integer, ArrayList>(); // �����������ϵĿ��ж���Ϣ

		TASKListInPes = new HashMap<Integer, HashMap>();
		for (int i = 0; i < pe_number; i++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKListInPes.put(i, TASKInPe);
			// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
		}
	}

	/**
	 * 
	 * @Title: runMakespan
	 * @Description: ��ʼfillback�㷨
	 * @param @throws Throwable
	 * @return void
	 * @throws
	 */
	public void runMakespan(String pathXML, String resultPath) throws Throwable {

		// init dagmap
		FillbacknewWithoutInsertEditionThree fb = new FillbacknewWithoutInsertEditionThree();
		DAGdepend dagdepend = new DAGdepend();
		computerability vcc = new computerability();

		// ��ʼ��������
		initPE();

		// ��ʼ������xml
		initdagmap(dagdepend, vcc, pathXML);

		Date begin = new Date();
		Long beginTime = begin.getTime();
		// ���ȵ�һ��DAG����ʼ�������������ϵ�����ֲ������жΡ�����������ɳ����

		//scheduleFirstDAG();

		// ���õ�ǰʱ���ǵ�һ��DAG �ĵ���ʱ��
		current_time = DAGMapList.get(0).getsubmittime();

		// ��ʼ���Ⱥ�������ҵ
		for (int i = 0; i < DAGMapList.size(); i++) {

			HashMap<Integer, ArrayList> SlotListInPestemp = new HashMap<Integer, ArrayList>();
			HashMap<Integer, HashMap> TASKListInPestemp = new HashMap<Integer, HashMap>();

			// �����Ӧ����ҵ��Χ�ڵĿ��п�����
			computeSlot(DAGMapList.get(i).getsubmittime(), DAGMapList.get(i).getDAGdeadline());

			SlotListInPestemp = copySlot();
			TASKListInPestemp = copyTASK();

			scheduleOtherDAG(i, SlotListInPestemp, TASKListInPestemp);

		}

		Date end = new Date();
		Long endTime = end.getTime();

		Long diff = endTime - beginTime;

		outputresult(diff, resultPath);

		storeresultShow();

	}
	
	

	/**
	 * �����������Դ�����ʺ����������
	 */
	public static void outputresult(Long diff, String resultPath) {
		int suc = 0;
		int fault = 0;
		int effective = 0;
		int notEffective=0;
		int tempp = timeWindow;

		for (int j = 0; j < DAGMapList.size(); j++) {
			ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
			for (int i = 0; i < DAGMapList.get(j).gettasklist().size(); i++) {
				DAG dag_temp = (DAG) DAGMapList.get(j).gettasklist().get(i);
				DAGTaskList.add(dag_temp);
			}
			
			if (DAGMapList.get(j).getfillbackdone()) {
				System.out.println("dag" + DAGMapList.get(j).getDAGId()+ "���ȳɹ����ɹ�ִ��");
				suc++;
				for (int i = 0; i < DAGMapList.get(j).gettasklist().size(); i++) {
					effective = effective + DAGTaskList.get(i).getts();
				}
			}else{
				for (int i = 0; i < DAGMapList.get(j).gettasklist().size(); i++) 
					notEffective = notEffective + DAGTaskList.get(i).getts();
			}

			if(j==0){
				System.out.println("effective="+effective);
			}
			if (!DAGMapList.get(j).getfillbackdone()) {
				System.out.println("dag" + DAGMapList.get(j).getDAGId()+ "����ʧ�ܣ�ʧ��ִ�С�����");
				fault++;
			}
		}

		System.out.println("FillbackWithoutInsertOne��" + suc + "������������");
		System.out.println("FillbackWithoutInsertOne��" + fault + "���������ʧ�ܡ�����");

		/**
		 * ��������������ϵĽ��
		 */
		// for(int k=0;k<1;k++){
		// HashMap<Integer, Integer[]> hashMap = TASKListInPes.get(k);
		// for(Map.Entry<Integer, Integer[]> map:hashMap.entrySet()){
		// int order=map.getKey();
		// int DAGID=map.getValue()[2];
		// int TASKORDER=map.getValue()[3];
		// System.out.println("������="+k+";˳��="+order+"   dag"+DAGID+":task"+TASKORDER);
		// }
		// }

		/**
		 * 
		 */

		DecimalFormat df = new DecimalFormat("0.0000");
		System.out.println("FillbackWithoutInsertOne:");
		System.out.println("PE's use ratio is "+ df.format((float) effective / (pe_number * tempp)));
		System.out.println("effective PE's use ratio is "+ df.format((float) effective / (tempp * pe_number)));
		System.out.println("Task Completion Rates is "+ df.format((float) suc / DAGMapList.size()));
		System.out.println();

		rateResult[0][0] = df.format((float) effective / (pe_number * tempp));
		rateResult[0][1] = df.format((float) effective / (tempp * pe_number));
		rateResult[0][2] = df.format((float) suc / DAGMapList.size());

		rateResult[0][3] = df.format(diff);

		System.out.println("������Ƴɹ�����=" + pushSuccessCount);
		System.out.println("��������=" + taskTotal);
		int count = 0;

		for (int k = 0; k < dagResultMap.length; k++) {
			for (int l = 0; l < dagResultMap[k].length; l++) {
				if (dagResultMap[k][l] == 1) {
					System.out.println("����dagid=" + k + "������ı��=" + l + "����ʧ��");
					count++;
				}
			}
		}

		System.out.println("count=" + count);

		 PrintResult.printLREBWithoutInsertThreeToTxt(rateResult,resultPath);
	}

	/**
	 * ���汾�㷨�ĸ�������Ŀ�ʼ����ʱ��,������������Ȳ��ɹ���
	 */
	public static void storeresult() {
		int dagcount = 0;
		for (DAGMap dagmap : DAGMapList) {
			ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
			for (int i = 0; i < dagmap.gettasklist().size(); i++) {
				DAG dag = (DAG) dagmap.gettasklist().get(i);
				DAGTaskList.add(dag);
				message[dagcount][0] = dag.getdagid();
				message[dagcount][1] = dag.getid();
				message[dagcount][2] = dag.getfillbackpeid();
				message[dagcount][3] = dag.getfillbackstarttime();
				message[dagcount][4] = dag.getfillbackfinishtime();
				dagcount++;
			}
		}
	}

	/**
	 * 
	 * @Title: storeresultShow
	 * @Description: ���汾�㷨�ĸ�������Ŀ�ʼ����ʱ��,������ֻ�е��ȳɹ���
	 * @param
	 * @return void
	 * @throws
	 */
	public static void storeresultShow() {
		int dagcount = 0;
		for (DAGMap dagmap : DAGMapList) {

			if (dagmap.fillbackdone) {
				ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();

				for (int i = 0; i < dagmap.gettasklist().size(); i++) {
					DAG dag = (DAG) dagmap.gettasklist().get(i);
					DAGTaskList.add(dag);

					message[dagcount][0] = dag.getdagid();
					message[dagcount][1] = dag.getid();
					message[dagcount][2] = dag.getfillbackpeid();
					message[dagcount][3] = dag.getfillbackstarttime();
					message[dagcount][4] = dag.getfillbackfinishtime();
					dagcount++;

				}
			}
		}

	}

	/**
	 * 
	 * @Title: computeSlot
	 * @Description: ����relax�������¼������ʱ���SlotListInPes������SlotListInPes.put(i,
	 *               slotListinpe)==��slotListinpe��������ɸѡ���ģ�ʱ������ƥ��submit----
	 *               deadlineʱ��ε�slot�ļ���
	 * @param @param submit��DAG�ύʱ��
	 * @param @param deadline��DAG��ֹʱ��
	 * @return void
	 * @throws
	 */
	public static void computeSlot(int submit, int deadline) {

		SlotListInPes.clear();

		for (int i = 0; i < pe_number; i++) {
			// ��ǰ�������Ͽ���Ƭ����������
			int Slotcount = 0;

			// ��ȡĳ�������ϵ���������
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(i);
			// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id

			ArrayList<slot> slotListinpe = new ArrayList<slot>();
			ArrayList<slot> slotListinpe_ori = new ArrayList<slot>();

			if (TASKInPe.size() == 0) {// ��ǰ��������û��ִ�й�����
				slot tem = new slot();
				tem.setPEId(i);
				tem.setslotId(Slotcount);
				tem.setslotstarttime(submit);
				tem.setslotfinishtime(deadline);
				slotListinpe.add(tem);
				Slotcount++;
			} else if (TASKInPe.size() == 1) {// �ô�������ֻ��һ����������

				if (TASKInPe.get(0)[0] > submit) {
					if (deadline <= TASKInPe.get(0)[0]) {
						slot tem = new slot();
						ArrayList<String> below_ = new ArrayList<String>();
						// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
						// ��������dag ������
						below_.add(TASKInPe.get(0)[2] + " "
								+ TASKInPe.get(0)[3] + " " + 0);
						tem.setPEId(i);
						tem.setslotId(Slotcount);
						tem.setslotstarttime(submit);
						tem.setslotfinishtime(deadline);
						tem.setbelow(below_);
						slotListinpe.add(tem);
						Slotcount++;
					} else if (deadline <= TASKInPe.get(0)[1]) {
						slot tem = new slot();
						ArrayList<String> below_ = new ArrayList<String>();
						// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
						// ��������dag ������
						below_.add(TASKInPe.get(0)[2] + " "
								+ TASKInPe.get(0)[3] + " " + 0);
						tem.setPEId(i);
						tem.setslotId(Slotcount);
						tem.setslotstarttime(submit);
						tem.setslotfinishtime(TASKInPe.get(0)[0]);
						tem.setbelow(below_);
						slotListinpe.add(tem);
						Slotcount++;
					} else {
						slot tem = new slot();
						ArrayList<String> below_ = new ArrayList<String>();
						// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
						// ��������dag ������
						below_.add(TASKInPe.get(0)[2] + " "
								+ TASKInPe.get(0)[3] + " " + 0);
						tem.setPEId(i);
						tem.setslotId(Slotcount);
						tem.setslotstarttime(submit);
						tem.setslotfinishtime(TASKInPe.get(0)[0]);
						tem.setbelow(below_);
						slotListinpe.add(tem);
						Slotcount++;
						// ===================================================
						slot temp = new slot();
						// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
						temp.setPEId(i);
						temp.setslotId(Slotcount);
						temp.setslotstarttime(TASKInPe.get(0)[1]);
						temp.setslotfinishtime(deadline);
						slotListinpe.add(temp);
						Slotcount++;

					}
				} else if (submit <= TASKInPe.get(0)[1]
						&& deadline > TASKInPe.get(0)[1]) {
					slot tem = new slot();
					// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
					tem.setPEId(i);
					tem.setslotId(Slotcount);
					tem.setslotstarttime(TASKInPe.get(0)[1]);
					tem.setslotfinishtime(deadline);
					slotListinpe.add(tem);
					Slotcount++;
				} else if (submit > TASKInPe.get(0)[1]
						&& deadline > TASKInPe.get(0)[1]) {
					slot tem = new slot();
					// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
					tem.setPEId(i);
					tem.setslotId(Slotcount);
					tem.setslotstarttime(submit);
					tem.setslotfinishtime(deadline);
					slotListinpe.add(tem);
					Slotcount++;
				}
			} else {// �ô��������ж����������

				// ��ȡ��������ԭ�����ڸ�������ִ�п���Ƭ��
				// �����Ǵ��������еĿ��У���û������submit��deadline������

				// ����������ϵ�һ������Ŀ�ʼʱ�䲻��Ϊ0���ͷ��һ�ο�϶
				if (TASKInPe.get(0)[0] >= 0) {
					slot tem = new slot();
					ArrayList<String> below_ = new ArrayList<String>();
					for (int k = 0; k < TASKInPe.size(); k++) {
						// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
						// ����������ҵ��id ������
						below_.add(TASKInPe.get(k)[2] + " "
								+ TASKInPe.get(k)[3] + " " + 0);
					}
					tem.setPEId(i);
					tem.setslotId(Slotcount);
					// System.out.println("����Ӧ����0�Ŷ�"+Slotcount);
					tem.setslotstarttime(0);
					tem.setslotfinishtime(TASKInPe.get(0)[0]);
					tem.setbelow(below_);
					slotListinpe_ori.add(tem);
					Slotcount++;
				}

				/**
				 * �ڵ�һ����FIFO�����㷨�л�������⣬����ֻ���ڵ�һ���г��֣����Ժ��Բ��ƽ�
				 */
				for (int j = 1; j < TASKInPe.size(); j++) {
					if (TASKInPe.get(j - 1)[1] <= TASKInPe.get(j)[0]) {
						slot tem = new slot();
						ArrayList<String> below_ = new ArrayList<String>();
						for (int k = j; k < TASKInPe.size(); k++) {
							// ����0����task��ʼʱ�䣬1����task����ʱ�䣬2����dagid��3����id
							// ����������ҵ��id ������ ���ĸ����п�ĺ���
							below_.add(TASKInPe.get(k)[2] + " "
									+ TASKInPe.get(k)[3] + " " + j);
						}
						tem.setPEId(i);
						tem.setslotId(Slotcount);
						tem.setslotstarttime(TASKInPe.get(j - 1)[1]);
						tem.setslotfinishtime(TASKInPe.get(j)[0]);
						tem.setbelow(below_);
						slotListinpe_ori.add(tem);
						Slotcount++;
					}

				}

				// �����ڵ�ǰdag��ʼʱ�䵽��ֹʱ��֮�� ��slot��
				// ���������ɵ�ǰDAG����ʼslot�ı��
				int startslot = 0;
				for (int j = 0; j < slotListinpe_ori.size(); j++) {
					slot tem = new slot();
					tem = slotListinpe_ori.get(j);
					/**
					 * �жϴ������ϵ�һ�����п飬�����ʼ��ͷ��,�����⣿������,
					 * 
					 */
					if (j == 0 && (tem.slotstarttime != tem.slotfinishtime)) {
						if (submit >= 0 && submit < tem.slotfinishtime) {
							startslot = 0;
							tem.setslotstarttime(submit);
							break;
						}
					} else if (j > 0 && j <= (slotListinpe_ori.size() - 1)) {
						if (tem.getslotstarttime() <= submit // --slotstarttime--submit--slotfinishtime--
								&& tem.getslotfinishtime() > submit) {
							tem.setslotstarttime(submit);
							startslot = j;
							break;
						} else if (tem.getslotstarttime() > submit // slotfinishtime(��һ��slot)--submit---slotstarttime
								&& slotListinpe_ori.get(j - 1)
										.getslotfinishtime() <= submit) {
							startslot = j;
							break;
						}
					}

					// �������ʱ����ǰ���slot��û�취ƥ����룬����ڴ��������.
					if (j == (slotListinpe_ori.size() - 1))
						startslot = slotListinpe_ori.size();
				}

				// ����slotListinpe���ݣ���������ɸѡ���ģ�ʱ������ƥ��submit----deadlineʱ��ε�slot�ļ���
				/**
				 * ����������Ӧ��û�����⣬��Ϊ�����ÿ���������ϵĿ���ʱ��
				 * ��������Ӧ����ֻҪ����һ�־ͺ��ˣ�����Ϊʲô�����ü��֣���������Ϊ�ڵ����㷨�о����б�����û���õ������ҵ
				 * ������һ�μ����ʱ���ּ�����һ��
				 */
				int count = 0;
				for (int j = startslot; j < slotListinpe_ori.size(); j++) {
					slot tem = new slot();
					tem = slotListinpe_ori.get(j);

					if (tem.getslotfinishtime() <= deadline) {
						tem.setslotId(count);
						slotListinpe.add(tem);
						count++;
					} else if (tem.getslotfinishtime() > deadline
							&& tem.getslotstarttime() < deadline) {// ---slotstarttime---deadline---slotfinishtime---
						tem.setslotId(count);
						tem.setslotfinishtime(deadline);
						// System.out.println("============�ύʱ�䣺"+submit+";����ƥ��Ŀ��п飺"+count+"�����п�ʼʱ��:"+tem.getslotstarttime()+"�����н���ʱ��:"+tem.getslotfinishtime()+";�ڴ�������:"+tem.PEId);
						slotListinpe.add(tem);
						break;
					}
				}

				// �������һ�����п�
				if (TASKInPe.get(TASKInPe.size() - 1)[1] <= submit) {
					slot tem = new slot();
					tem.setPEId(i);
					tem.setslotId(count);
					tem.setslotstarttime(submit);
					tem.setslotfinishtime(deadline);
					slotListinpe.add(tem);
					// System.out.println("Ӧ����ֻ��һ�����п��"+slotListinpe.size());
				} else if (TASKInPe.get(TASKInPe.size() - 1)[1] < deadline
						&& TASKInPe.get(TASKInPe.size() - 1)[1] > submit) {
					slot tem = new slot();
					tem.setPEId(i);
					tem.setslotId(count);
					tem.setslotstarttime(TASKInPe.get(TASKInPe.size() - 1)[1]);
					tem.setslotfinishtime(deadline);
					slotListinpe.add(tem);
				}

			}

			SlotListInPes.put(i, slotListinpe);
		}
		//
		// System.out.println("==========================>");
		// for(int i=0;i<pe_number;i++){
		// if(SlotListInPes.get(i).size()!=0){
		// int size=SlotListInPes.get(i).size();
		// slot so=(slot) SlotListInPes.get(i).get(size-1);
		// System.out.println("��������"+i+";���һ�����п�Ľ���ʱ��Ϊ��"+so.getslotfinishtime()+";��ҵ���ύʱ��Ϊ��"+submit+"����ҵ�Ľ���ʱ��"+deadline+";����ƥ��Ŀ��п��У�"+size);
		// }
		// }

	}

	/**
	 * 
	 * @Title: changeinpe
	 * @Description: ����relax�����޸�slotlistinpe
	 * @param @param slotlistinpe
	 * @param @param inpe
	 * @return void
	 * @throws
	 */
	public static void changeinpe(ArrayList<slot> slotlistinpe, int inpe) {
		ArrayList<String> below = new ArrayList<String>();

		for (int i = 0; i < slotlistinpe.size(); i++) {
			ArrayList<String> belowte = new ArrayList<String>();

			slot slottem = slotlistinpe.get(i);

			for (int j = 0; j < slottem.getbelow().size(); j++) {
				below.add(slottem.getbelow().get(j));
			}

			String belowbuf[] = below.get(0).split(" ");
			// ������п�ĺ��һ������ı��
			int buffer = Integer.valueOf(belowbuf[2]).intValue();
			if (buffer >= inpe) {
				buffer += 1;
				for (int j = 0; j < below.size(); j++) {
					String belowbuff = belowbuf[0] + " " + belowbuf[1] + " "
							+ buffer;
					belowte.add(belowbuff);
				}
				slottem.getbelow().clear();
				slottem.setbelow(belowte);
			}
		}

	}

	/**
	 * 
	 * @Title: changetasklistinpe
	 * @Description: ��һ�ε�����relax�����ݽ���޸�TASKListInPes��ֵ
	 * @param @param dagmap,DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @return void
	 * @throws
	 */
	private static void changetasklistinpe(DAGMap dagmap) {

		for (int i = 0; i < pe_number; i++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(i);

			for (int j = 0; j < TASKInPe.size(); j++) {
				if (TASKInPe.get(j)[2] == dagmap.getDAGId()) {
					DAG temp = new DAG();
					temp = getDAGById(TASKInPe.get(j)[2], TASKInPe.get(j)[3]);
					TASKInPe.get(j)[0] = temp.getfillbackstarttime();
					TASKInPe.get(j)[1] = temp.getfillbackfinishtime();
				}
			}

			TASKListInPes.put(i, TASKInPe);
		}
	}

	// =======================������================

	/**
	 * 
	 * @Title: changetasklistinpeLevel
	 * @Description: TODO
	 * @param dagmap
	 *            :==
	 * @throws
	 */

	// private static void changetasklistinpeLevel(DAGMap dagmap) {
	//
	// int num=dagmap.taskinlevel.size();
	// ArrayList<DAG> list=new ArrayList<>();
	// for(int i=0;i<num;i++){
	// list.addAll(dagmap.taskinlevel.get(i));
	// }
	//
	// for (int i = 0; i < pe_number; i++) {
	// HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
	// TASKInPe = TASKListInPes.get(i);
	//
	// for (int j = 0; j < TASKInPe.size(); j++) {
	// if (TASKInPe.get(j)[2] == dagmap.getDAGId()) {
	// if(list.contains(TASKInPe.get(j))){
	// -----
	// }
	//
	// DAG temp = new DAG();
	// temp = getDAGById(TASKInPe.get(j)[2], TASKInPe.get(j)[3]);
	// TASKInPe.get(j)[0] = temp.getfillbackstarttime();
	// TASKInPe.get(j)[1] = temp.getfillbackfinishtime();
	//
	//
	//
	//
	//
	//
	// }
	// }
	//
	// TASKListInPes.put(i, TASKInPe);
	// }
	// }

	/**
	 * @Description: ����ÿ���еĸ�����������Ժ��Ƶľ��룬����relax�ֶΣ��ɳھ���
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param DAGTaskList
	 *            ��DAG�и���������
	 * @param canrelaxDAGTaskList
	 *            �����Ժ��Ƶ��������б�
	 * @param DAGTaskDependValue
	 *            ��������ϵ
	 * @param levelnumber
	 *            ������
	 * @param totalrelax
	 *            ��������ֵ
	 */
	public static void calculateweight(DAGMap dagmap,
			ArrayList<DAG> DAGTaskList, ArrayList<DAG> canrelaxDAGTaskList,
			Map<String, Double> DAGTaskDependValue, int levelnumber,
			int totalrelax) {

		int startlevelnumber = canrelaxDAGTaskList.get(0).getnewlevel();
		int[] weight = new int[levelnumber];
		int[] relax = new int[DAGTaskList.size()];
		int[] maxlength = new int[levelnumber + 1];
		int weightsum = 0;

		for (int i = startlevelnumber; i <= levelnumber; i++) {
			int max = 0, maxid = 0;
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel
						.get(i).get(j));

				if (canrelaxDAGTaskList.contains(dagtem)) {
					if (i == levelnumber) {
						max = dagtem.getts();
						maxid = i;
					} else {
						int value = dagtem.getts();
						for (int k = 0; k < dagmap.taskinlevel.get(i + 1)
								.size(); k++) {
							DAG dagsuc = new DAG();
							dagsuc = getDAGById(dagmap.getDAGId(),
									(int) dagmap.taskinlevel.get(i + 1).get(k));
							if (dagmap.isDepend(String.valueOf(dagtem.getid()),
									String.valueOf(dagsuc.getid()))) {
								if (dagtem.getfillbackpeid() != dagsuc
										.getfillbackpeid()) {
									int tempp = dagtem.getts()
											+ (int) (double) DAGTaskDependValue
													.get(dagtem.getid() + " "
															+ dagsuc.getid());
									if (value < tempp) {
										value = tempp;
										maxid = dagtem.getid();
									}
								}
							}
						}

						if (max < value) {
							max = value;
							maxid = dagtem.getid();
						}
					}
				}
			}
			weight[i - 1] = max;
			maxlength[i - 1] = maxid;
		}

		for (int i = startlevelnumber - 1; i < levelnumber; i++) {
			weightsum = weight[i] + weightsum;
		}

		for (int i = startlevelnumber; i <= levelnumber; i++) {
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel
						.get(i).get(j));
				if (canrelaxDAGTaskList.contains(dagtem)) {
					int tem = weight[i - 1] * totalrelax / weightsum;
					dagtem.setrelax(tem);
				}
			}
		}

		// for (int i = startlevelnumber; i <= levelnumber; i++) {
		// for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
		// DAG dagtem = new DAG();
		// dagtem = getDAGById(dagmap.getDAGId(), (int)
		// dagmap.taskinlevel.get(i).get(j));
		// if (canrelaxDAGTaskList.contains(dagtem)) {
		//
		// System.out.println("dag����ǣ�"+dagtem.getdagid()+":"+dagtem.getid()+"\t�㼶�ǣ�"+dagtem.getnewlevel()+":"+dagtem.getrelax());
		// }
		// }
		// }

		// for (int j = 0; j < canrelaxDAGTaskList.size(); j++) {
		// DAG dagtem = new DAG();
		// dagtem = getDAGById(dagmap.getDAGId(),
		// canrelaxDAGTaskList.get(j).getid());
		// if (canrelaxDAGTaskList.contains(dagtem)) {
		// System.out.println("dag����ǣ�"+dagtem.getdagid()+":"+dagtem.getid()+"\t�㼶�ǣ�"+dagtem.getnewlevel()+":"+dagtem.getrelax());
		// }
		//
		// }

	}

	/**
	 * @Description: ����ͬ���������������һ���б�������ŵ���ԭ�б��еı��ֵ
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param DAGTaskList
	 *            ��DAG�и���������
	 * @param deadline
	 *            ��DAG�Ľ�ֹʱ��
	 * @return levelnumber,���в���
	 */
	public static int putsameleveltogether(DAGMap dagmap,
			ArrayList<DAG> DAGTaskList, int deadline) {
		int levelnumber = DAGTaskList.get(DAGTaskList.size() - 1).getnewlevel();
		int finishtime = DAGTaskList.get(DAGTaskList.size() - 1)
				.getfillbackfinishtime();
		int totalrelax = deadline - finishtime;

		// �㼶�Ǵ�1��ʼ��
		for (int j = 1; j <= levelnumber; j++) {
			ArrayList<Integer> samelevel = new ArrayList<Integer>();

			for (int i = 0; i < dagmap.gettasklist().size(); i++) {
				if (DAGTaskList.get(i).getnewlevel() == j)
					samelevel.add(i);
			}
			dagmap.taskinlevel.put(j, samelevel);
		}
		return levelnumber;
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: calculatenewlevel
	 * @Description: ���ݵ��Ƚ�������¼���DAG�и���������Ĳ���������newlevel��orderbystarttime����
	 * @param @param dagmap��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param @param DAGTaskList��DAG�и���������
	 * @param @param DAGTaskListtemp
	 * @param @param setorderbystarttime ����ȥ��ʱ����һ���յ�
	 * @return void
	 * @throws
	 */
	public static void calculateNewLevel(DAGMap dagmap,
			ArrayList<DAG> DAGTaskList, ArrayList<DAG> DAGTaskListtemp,
			ArrayList<DAG> setorderbystarttime) throws IOException {

		DAG min = new DAG();
		DAG temp = new DAG();

		// ��DAGTaskListtemp����fillbackstarttime��С��������
		for (int k = 0; k < DAGTaskListtemp.size(); k++) {
			int tag = k;
			min = DAGTaskListtemp.get(k);
			temp = DAGTaskListtemp.get(k);
			for (int p = k + 1; p < DAGTaskListtemp.size(); p++) {
				if (DAGTaskListtemp.get(p).getfillbackstarttime() < min
						.getfillbackstarttime()) {
					min = DAGTaskListtemp.get(p);
					tag = p;
				}
			}
			if (tag != k) {
				DAGTaskListtemp.set(k, min);
				DAGTaskListtemp.set(tag, temp);
			}
		}

		// ���ñ���ҵ�а��յ��Ⱥ�Ľ����ʼʱ����������Ľ���б�
		dagmap.setorderbystarttime(DAGTaskListtemp);

		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			setorderbystarttime.add((DAG) dagmap.getorderbystarttime().get(i));
		}

		// ����starttimeΪ����˳��
		for (int i = 0; i < setorderbystarttime.size(); i++) {
			DAG dag = new DAG();
			dag = setorderbystarttime.get(i);

			if (i == 0) {
				dag.setnewlevel(1);
			} else {
				int max = 0;
				// ��Ѱ��ǰ��������ͬ�������ϵ����ڵ���һ�������ҵ��˾�����ѭ��
				for (int j = i - 1; j >= 0; j--) {
					if (setorderbystarttime.get(j).getfillbackpeid() == dag
							.getfillbackpeid()) {
						max = setorderbystarttime.get(j).getnewlevel() + 1;
						break;
					}
				}

				/**
				 * 
				 */
				// ���pre����ŵ���ԭ����id��ԭ����id��setorderbystarttime.get(i).getid()
				ArrayList<Integer> pre = dag.getpre();
				int id = dag.getdagid();
				// ��ǰ���dag�Ŀ�ʼʱ��
				int childStart = dag.getfillbackstarttime();
				// �������ǵ�ǰ��λ��
				int[] current = new int[pre.size()];
				int co = 0;
				for (int p : pre) {
					for (int c = 0; c < setorderbystarttime.size(); c++) {
						if (p == setorderbystarttime.get(c).getid()) {
							current[co] = c;
							co++;
						}
					}
				}

				for (int cc : current) {
					DAG purePre = setorderbystarttime.get(cc);

//					if (purePre.getnewlevel() == -1)
//						System.out.println("Ϊʲô����-1����������������������������~~~~~~~~~~");

					int leveltemp = purePre.getnewlevel() + 1;
					if (leveltemp > max)
						max = leveltemp;

				}
				DAG NOW = getDAGById(dag.getdagid(), dag.getid());
				NOW.setnewlevel(max);
			}
		}
	}

	/**
	 * 
	 * @Title: calculateoriginallevel
	 * @Description: ����DAG��ԭʼ����������Ĳ��������ã�level
	 * @param @param DAGTaskList��DAG�и���������
	 * @return void
	 * @throws
	 */
	public static void calculateOriginalLevel(ArrayList<DAG> DAGTaskList) {
		for (int i = 0; i < DAGTaskList.size(); i++) {
			// �����ǰ��������ʼ�������ò㼶Ϊ1
			if (i == 0)
				DAGTaskList.get(i).setlevel(1);
			else {// �����ǰ��������ʼ�������ò㼶Ϊ�丸����㼶���ֵ+1
				int max = 0;
				Iterator<Integer> it = DAGTaskList.get(i).getpre().iterator();
				while (it.hasNext()) {
					int pretempid = it.next();
					int leveltemp = DAGTaskList.get(pretempid).getlevel() + 1;
					if (leveltemp > max)
						max = leveltemp;
				}
				DAGTaskList.get(i).setlevel(max);
			}
		}
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: wholerelax
	 * @Description: ���ݵ��Ƚ������levelrelaxing������
	 * @param @param dagmap��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @return void
	 * @throws
	 */
	public static void wholerelax(DAGMap dagmap) throws IOException {
		int Criticalnum = 0;
		if (!dagmap.isSingle) {
			Criticalnum = CriticalPath(dagmap);
		} else {
			Criticalnum = 1;
		}
		int submit = dagmap.getsubmittime();
		int deadline = dagmap.getDAGdeadline();

		ArrayList<DAG> canrelaxDAGTaskList = new ArrayList<DAG>();
		ArrayList<DAG> norelaxDAGTaskList = new ArrayList<DAG>();
		ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
		ArrayList<DAG> DAGTaskListtemp = new ArrayList<DAG>();
		ArrayList<DAG> setorderbystarttime = new ArrayList<DAG>();
		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>();
		DAGTaskDependValue = dagmap.getdependvalue();

		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
			DAGTaskListtemp.add((DAG) dagmap.gettasklist().get(i));
		}

		// ���㱾DAG��ԭ��level
		calculateOriginalLevel(DAGTaskList);

		/**
		 * 10.22������޵�bug�����ǽ���ˣ�����Ŷ
		 * 
		 * 10.23���������ѧ����ʵ�Ѿ���������bug��������
		 */
		calculateNewLevel(dagmap, DAGTaskList, DAGTaskListtemp,setorderbystarttime);

		// ��ͬ�㼶���������ͬһ���б���
		int levelnumber = putsameleveltogether(dagmap, DAGTaskList, deadline);

		int finishtime = DAGTaskList.get(DAGTaskList.size() - 1).getfillbackfinishtime();

		int totalrelax = deadline - finishtime;

		// ֻҪ��һ������û�ܻ���ɹ�������ʧ��
		boolean finishsearch = true;

		for (int i = levelnumber; i >= 1; i--) {
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));
				//System.out.println("+++++++++��ʱ��Ҫ��ȡ��DAG�ǣ�"+dagmap.getDAGId()+":"+(int) dagmap.taskinlevel.get(i).get(j)+"\tԭʼDAG��Ϣ�ǣ�");
				/**
				 * �����м�����񲻲����ɳ�
				 */
				if (dagtem.getisfillback() == false) {
					canrelaxDAGTaskList.add(dagtem);
					// finishsearch=true;
				} else {
					norelaxDAGTaskList.add(dagtem);
				}
			}
		}
		/**
		 * �������ɳڵ��������ֵ������Ϊ�Լ��� ���Ҫ�����㷨��Ҫ�޸�����
		 */
		for (int j = 0; j < norelaxDAGTaskList.size(); j++) {
			DAG dagtem = new DAG();
			dagtem = getDAGById(dagmap.getDAGId(), (int) norelaxDAGTaskList.get(j).getid());
			dagtem.setslidefinishdeadline(dagtem.getfillbackfinishtime());
			dagtem.setslidedeadline(dagtem.getfillbackstarttime());
			dagtem.setslidelength(0);
		}

		// ��ʼ�ɳڲ���
		if (canrelaxDAGTaskList.size() > 0) {
			DAG mindag = new DAG();
			DAG tempdag = new DAG();

			/**
			 * ���տ�ʼʱ������
			 */
			for (int k = 0; k < canrelaxDAGTaskList.size(); k++) {
				int tag = k;
				mindag = canrelaxDAGTaskList.get(k);
				tempdag = canrelaxDAGTaskList.get(k);
				for (int p = k + 1; p < canrelaxDAGTaskList.size(); p++) {
					if (canrelaxDAGTaskList.get(p).getfillbackstarttime() <= mindag
							.getfillbackstarttime()) {
						mindag = canrelaxDAGTaskList.get(p);
						tag = p;
					}
				}
				if (tag != k) {
					canrelaxDAGTaskList.set(k, mindag);
					canrelaxDAGTaskList.set(tag, tempdag);
				}
			}

			/**
			 * 
			 */

			// ����Ĳ�һ���Ǵӵ�һ�㿪ʼ��
			int startlevelnumber = canrelaxDAGTaskList.get(0).getnewlevel();

			/**
			 * ����ÿ�����������ֵ�����ö�Ӧ�����ֶ�
			 */
			// calculateweight(dagmap, DAGTaskList,canrelaxDAGTaskList,DAGTaskDependValue, levelnumber, totalrelax);

			/**
			 * ����Ƕȼ�������ֵ
			 */
			calculateweight(dagmap, DAGTaskList, DAGTaskList,
					DAGTaskDependValue, levelnumber, totalrelax);

			// ============================ �ҵ�����ҵ�е�������ִ��ʱ����Ĵ�����===========
			int startinpe[] = new int[pe_number];
			int finishinpe[] = new int[pe_number];
			int length = -1;
			int maxpeid = -1;
			for (int k = 0; k < pe_number; k++)
				startinpe[k] = timewindowmax;
			for (int k = 0; k < DAGTaskList.size(); k++) {
				DAG dagtem = new DAG();
				dagtem = DAGTaskList.get(k);
				if (startinpe[dagtem.getfillbackpeid()] > dagtem.getfillbackstarttime())
					startinpe[dagtem.getfillbackpeid()] = dagtem.getfillbackstarttime();
				if (finishinpe[dagtem.getfillbackpeid()] < dagtem.getfillbackfinishtime())
					finishinpe[dagtem.getfillbackpeid()] = dagtem.getfillbackfinishtime();
			}
			for (int k = 0; k < pe_number; k++) {
				if (length < (finishinpe[k] - startinpe[k])) {
					length = finishinpe[k] - startinpe[k];
					maxpeid = k;
				}
			}

			// ���øô������ϵ�����Ϊ�ؼ�����
			for (int k = 0; k < DAGTaskList.size(); k++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), DAGTaskList.get(k)
						.getid());
				if (dagtem.getfillbackpeid() == maxpeid) {
					dagtem.setiscriticalnode(true);
				}
			}

			// ������Ҫ���µ��б��е���ʼ�������ʱ��
			for (int j = 0; j < dagmap.taskinlevel.get(startlevelnumber).size(); j++) {
				DAG dagtem = new DAG();
				// taskinlevel�зŵ���ԭ�б��еı�š�dagmap.gettasklist()
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel
						.get(startlevelnumber).get(j));

				if (canrelaxDAGTaskList.contains(dagtem)) {
					// ����fd
					dagtem.setslidefinishdeadline(dagtem.getfillbackfinishtime() + dagtem.getrelax());
	
					// ����sd
					dagtem.setslidedeadline(dagtem.getrelax()+ dagtem.getfillbackstarttime());
					// ���ÿɻ����ĳ���
					dagtem.setslidelength(dagtem.getrelax());
				}
			}

			// ����һ�㿪ʼ����
			for (int i = startlevelnumber + 1; i <= levelnumber; i++) {
				DAG dagtemLast = new DAG();
				dagtemLast = getDAGById(dagmap.getDAGId(),
						(int) dagmap.taskinlevel.get(i - 1).get(0));
				int starttime = dagtemLast.getslidefinishdeadline();
				int finishdeadline = -1;

				for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
					DAG dagtem = new DAG();
					dagtem = getDAGById(dagmap.getDAGId(),
							(int) dagmap.taskinlevel.get(i).get(j));
					// ����s

					dagtem.setfillbackstarttime(starttime);
					dagtem.setfillbackfinishtime(dagtem.getfillbackstarttime()
							+ dagtem.getts());

				}

				// �������û���ڹؼ�·���ϵ�������ѡȡ��������������fdΪ�����fd
				/**
				 * �п��ܱ�����������񶼲���ִ��ʱ������Ǹ��������� ���Ա���û���ڹؼ�λ�õ�����
				 */
				if (finishdeadline == -1) {
					for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
						DAG dagtem = new DAG();
						dagtem = getDAGById(dagmap.getDAGId(),
								(int) dagmap.taskinlevel.get(i).get(j));
						if (finishdeadline < dagtem.getfillbackfinishtime())
							finishdeadline = dagtem.getfillbackfinishtime();
					}
				}

				/**
				 * �в�����ʱ������񣬵������Ƕ�������Ҫ�ɳڵ��б���
				 */

				// ���±������������fdΪ�ڹؼ�·���ϵ������fd
				for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
					DAG dagtem = new DAG();
					dagtem = getDAGById(dagmap.getDAGId(),
							(int) dagmap.taskinlevel.get(i).get(j));
					if (canrelaxDAGTaskList.contains(dagtem)) {
						dagtem.setslidefinishdeadline(finishdeadline);
						dagtem.setslidedeadline(finishdeadline - dagtem.getts());
						dagtem.setslidelength(dagtem.getslidedeadline()
								- dagtem.getfillbackstarttime());
						// System.out.println("dag="+dagtem.getdagid()+":"+dagtem.getid()+";�ɳڿ�ʼʱ�䣺"+dagtem.getslidedeadline());
					}
				}
			}

		}// if (canrelaxDAGTaskList.size() > 0)

		// printRelax(dagmap);
	}

	private static void printRelax(DAGMap dagmap) throws IOException {

		FileWriter writer = new FileWriter("G:\\relax.txt", true);
		DAGMap tempTestJob = dagmap;
		int dagid = tempTestJob.DAGId;
		int num = tempTestJob.tasknumber;

		for (int o = 0; o < num; o++) {
			DAG tempDag = getDAGById(dagid, o);
			writer.write("�㼶�ǣ�" + tempDag.getnewlevel() + "\t��ҵ��"
					+ tempDag.getdagid() + "\t����" + tempDag.getid()
					+ "\t���ȿ�ʼ��" + tempDag.getfillbackstarttime() + "\t�ɳڿ�ʼ��"
					+ tempDag.getslidedeadline() + "\t���Ƚ�����"
					+ tempDag.getfillbackfinishtime() + "\t�ɳڽ�����"
					+ tempDag.getslidefinishdeadline() + "\t�ɺ��Ƴ��ȣ�"
					+ tempDag.getslidelength() + "\n");

		}
		if (writer != null) {
			writer.close();
		}

	}

	/**
	 * 
	 * @Title: printDagMap
	 * @Description: ��ӡ��ҵ�ĵ��Ƚ�����ļ���
	 * @param dagmap
	 * @throws IOException
	 *             :
	 * @throws
	 */
	private static void printDagMap(DAGMap dagmap) throws IOException {
		FileWriter writer = new FileWriter("G:\\dagmap.txt", true);
		DAGMap tempTestJob = dagmap;
		int dagid = tempTestJob.DAGId;
		int num = tempTestJob.tasknumber;

		for (int o = 0; o < num; o++) {
			DAG tempDag = getDAGById(dagid, o);
			ArrayList<Integer> pre = tempDag.getpre();

			for (int p : pre) {
				DAG tempPre = getDAGById(dagid, p);
				if (tempPre.getfillbackstarttime() > tempDag
						.getfillbackstarttime())
					writer.write("DAG��id=" + dagid + "��������id=" + p + ";��ʼʱ�䣺"
							+ tempPre.getfillbackstarttime() + ";������id=" + o
							+ ";������ʼʱ�䣺" + tempDag.getfillbackstarttime()
							+ "\n");
			}
		}
		if (writer != null) {
			writer.close();
		}

	}

	/**
	 * @Description: �жϺ��ƿ��п��ĸ����ܷ�ʹ������ɹ�������п�
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param readylist
	 *            ��readylist��������
	 * 
	 * @return isslide���ܷ����
	 */
	public static boolean scheduling(DAGMap dagmap, ArrayList<DAG> readylist) {
		boolean findsuc = true;// ��DAG�ܷ����ɹ���ֻҪһ������ʧ�ܾ���ȫ��ʧ��
		
		while (readylist.size() > 0) {
			int finimintime = timewindowmax;
			int mindag = -1;
			int message[][] = new int[readylist.size()][6];
			// 0 is if success 1 means success 0 means fail,
			// 1 is earliest starttime
			// 2 is peid
			// 3 is slotid
			// 4 is if need slide
			// 5 is slide length

			int[] finish = new int[readylist.size()];// �������ִ�н���ʱ��

			// ΪDAG�г���һ���������������Ѱ�ɲ����slot��������Ϣ
			for (int i = 0; i < readylist.size(); i++) {
				DAG dag = new DAG();
				dag = readylist.get(i);
				// ������������ȫ������һ�飬����ֻ������һ�飬û��������Ӧ�Ĳ���
				message[i] = findslot(dagmap, dag);
				finish[i] = message[i][1] + dag.getts();
			//	System.out.println("finish[i]="+finish[i]+"\t"+dag.getid());
			}
			
			
			int dagId = dagmap.getDAGId();
			// ֻҪ������һ������û�ܻ���ɹ�����ô����DAGʧ��
			for (int i = 0; i < readylist.size(); i++) {
				DAG tempDagResult = readylist.get(i);
				if (message[i][0] == 0) {
					//System.out.println("�Ҳ���λ�ò����������"+tempDagResult.getid());
					dagResultMap[dagId][tempDagResult.getid()] = 1;
					findsuc = false;
				}
			}

			if (findsuc == false) {
				return findsuc;
			}

			// �������������������ҵ��˺��ʵ�λ�ò���
			// �ҵ�����������ִ�н���ʱ������ģ�mindagΪ������
			for (int i = 0; i < readylist.size(); i++) {
				if (finimintime > finish[i]) {
					finimintime = finish[i];
					mindag = i;
				}
			}

		//	System.out.println("mindag="+mindag);
			// �ҵ����ִ�н���ʱ�����������
			ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
			DAG dagtemp = new DAG();
			
			for (int i = 0; i < dagmap.gettasklist().size(); i++) {
				DAG dag = new DAG();
				DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
				dag = (DAG) dagmap.gettasklist().get(i);

				int tempTaskId=readylist.get(mindag).getid();
				if (dag.getid() == tempTaskId) {
					dagtemp = (DAG) dagmap.gettasklist().get(i);
				}

			}

			// �����������fillbackstarttime����Ϣ
			// int startmin = finimin - readylist.get(mindag).getts();
			int startmin = message[mindag][1];
			int pemin = message[mindag][2];
			int slotid = message[mindag][3];
			dagtemp.setfillbackstarttime(startmin);
			dagtemp.setfillbackpeid(pemin);
			dagtemp.setpeid(pemin);
			dagtemp.setfillbackready(true);
			dagtemp.setprefillbackdone(true);
			dagtemp.setprefillbackdone(true);

			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(pemin);

			// ==================�޸Ĵ������ϵĵ��Ƚ���������������
			// ��Ҫ�����Ŀ���λ���в������񣬲�����ԭ�����ڴ������ϵ�����
			if (TASKInPe.size() > 0) {// �ô�������ԭ��������
				ArrayList<slot> slotlistinpe = new ArrayList<slot>();

				for (int j = 0; j < SlotListInPes.get(pemin).size(); j++)
					slotlistinpe.add((slot) SlotListInPes.get(pemin).get(j));

				ArrayList<String> below = new ArrayList<String>();

				slot slottem = new slot();
				for (int i = 0; i < slotlistinpe.size(); i++) {
					if (slotlistinpe.get(i).getslotId() == slotid) {
						slottem = slotlistinpe.get(i);
						break;
					}
				}

				for (int i = 0; i < slottem.getbelow().size(); i++) {
					below.add(slottem.getbelow().get(i));
				}

				if (below.size() > 0) {// ������slot����ԭ��������
					String buf[] = below.get(0).split(" ");
					//
					int inpe = Integer.valueOf(buf[2]).intValue();
					// ���ƺ���������
					for (int i = TASKInPe.size(); i > inpe; i--) {
						Integer[] st_fitemp = new Integer[4];
						st_fitemp[0] = TASKInPe.get(i - 1)[0];
						st_fitemp[1] = TASKInPe.get(i - 1)[1];
						st_fitemp[2] = TASKInPe.get(i - 1)[2];
						st_fitemp[3] = TASKInPe.get(i - 1)[3];
						TASKInPe.put(i, st_fitemp);
					}

					Integer[] st_fi = new Integer[4];
					st_fi[0] = startmin;
					st_fi[1] = finimintime;
					st_fi[2] = dagtemp.getdagid();
					st_fi[3] = dagtemp.getid();
					TASKInPe.put(inpe, st_fi);

					/**
					 * ����isfillback ʹ������������ɳ���һ����
					 * ֤����������ǲ���������ҵ֮���
					 */
					dagtemp.setisfillback(true);

					// �ı���п�ĵ�below
					changeinpe(slotlistinpe, inpe);

				} else {// ������slot����ԭ��û������
					Integer[] st_fi = new Integer[4];
					st_fi[0] = startmin;
					st_fi[1] = finimintime;
					st_fi[2] = dagtemp.getdagid();
					st_fi[3] = dagtemp.getid();
					TASKInPe.put(TASKInPe.size(), st_fi);
				}
			} else {// �ô�������ԭ��û������
				Integer[] st_fi = new Integer[4];
				st_fi[0] = startmin;
				st_fi[1] = finimintime;
				st_fi[2] = dagtemp.getdagid();
				st_fi[3] = dagtemp.getid();
				TASKInPe.put(TASKInPe.size(), st_fi);
			}

			// ���¼�����п��б�
			computeSlot(dagmap.getsubmittime(), dagmap.getDAGdeadline());

			// mindag�Ǹ����������еı��
			readylist.remove(mindag);

		}

		return findsuc;
	}

	/**
	 * @Description: �ж�DAG������ڵ��ܷ��ҵ�����ʱ��η��룬������򷵻���Ӧ����Ϣ
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param dagtemp
	 *            ��DAG������TASK�е�һ��
	 * @return message��0 is if success(1 means success 0 means fail), 1 is
	 *         earliest start time, 2 is peid, 3 is slotid
	 */
	public static int[] findslot(DAGMap dagmap, DAG dagtemp) {
		int message[] = new int[6];

		boolean findsuc = false;
		int startmin = timewindowmax;
		int finishmin = timewindowmax;
		int diffmin=timewindowmax;
		int pemin = -1;
		int slide;
		int[] startinpe = new int[pe_number]; // �ڴ�����i�Ͽ�ʼִ�е�ʱ��
		int[] slotid = new int[pe_number]; // ���п��ڴ�����i�ϵı��
		int[] isneedslide = new int[pe_number]; // 0 means don't need 1 means
												// need slide
		int[] slidelength = new int[pe_number];// �ڴ�����i����Ҫ�����ĳ���
		int[] diff = new int[pe_number];
		

		/**
		 * ��0
		 */
		for (int k = 0; k < pe_number; k++) {
			pushFlag[k] = 0;
		}

		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>();
		DAGTaskDependValue = dagmap.getdependvalue();

		// ��ȡ�����񼯺�
		ArrayList<DAG> pre_queue = new ArrayList<DAG>();
		ArrayList<Integer> pre = new ArrayList<Integer>();

		pre = dagtemp.getpre();
		if (pre.size() > 0) {
			for (int j = 0; j < pre.size(); j++) {
				DAG buf = new DAG();
				buf = getDAGById(dagtemp.getdagid(), pre.get(j));
				if (!buf.fillbackdone && !buf.fillbackready) {
					message[0] = 0;
					System.out.println("���ĸ��ڵ�û�����");
					return message;
				}
				pre_queue.add(buf);
			}
		}

		int faDone=0;
		// ��ȡ����������翪ʼʱ��
		for (int i = 0; i < pe_number; i++) {
			int predone = 0;// �ڵ�ǰ�������ϵ�ǰ�������翪ʼִ��ʱ��
			if (pre_queue.size() == 1) {// ���������ֻ��һ��������
				if (pre_queue.get(0).getfillbackpeid() == i) {// �븸������ͬһ����������
					predone = pre_queue.get(0).getfillbackfinishtime();
				} else {// �븸������ͬһ����������
					int value = (int) (double) DAGTaskDependValue.get(String.valueOf(pre_queue.get(0).getid())+ " "+ String.valueOf(dagtemp.getid()));
					predone = pre_queue.get(0).getfillbackfinishtime() + value;
				}
			} else if (pre_queue.size() >= 1) {// �ж��������
				for (int j = 0; j < pre_queue.size(); j++) {
					if (pre_queue.get(j).getfillbackpeid() == i) {// �븸������ͬһ����������
						if (predone < pre_queue.get(j).getfillbackfinishtime()) {
							predone = pre_queue.get(j).getfillbackfinishtime();
						}
					} else {// �븸������ͬһ����������
						int valu = (int) (double) DAGTaskDependValue.get(String.valueOf(pre_queue.get(j).getid())+ " "+ String.valueOf(dagtemp.getid()));
						int value = pre_queue.get(j).getfillbackfinishtime()+ valu;
						if (predone < value)
							predone = value;
					}
				}
			}

			
			faDone=predone;
			/**
			 * 
			 */
			// ���¼�����п��б�
			computeSlot(predone, dagmap.getDAGdeadline());
			//System.out.println("��ʱ�Ŀ��п鿪ʼ��λ���ǣ�"+predone);
			/**
			 * 
			 */
			startinpe[i] = -1;
			diff[i] = -1;
			ArrayList<slot> slotlistinpe = new ArrayList<slot>();

			// i:���������
			for (int j = 0; j < SlotListInPes.get(i).size(); j++)
				slotlistinpe.add((slot) SlotListInPes.get(i).get(j));

			HashMap<Integer, Integer[]> tempSlotInfo=new HashMap<>();
			int countSlot=0;
			// ��Ѱ�������ڵ�ǰ�������ϲ�������翪ʼ�Ŀ����������Ϣ
			for (int j = 0; j < SlotListInPes.get(i).size(); j++) {
				int slst = slotlistinpe.get(j).getslotstarttime();
				int slfi = slotlistinpe.get(j).getslotfinishtime();
				
				if (predone < slst) {
					if ((slst + dagtemp.getts()) <= slfi&& (slst + dagtemp.getts()) <= dagtemp.getdeadline()) {
						Integer[] slotInfo=new Integer[4];
						slotInfo[0]=slotlistinpe.get(j).getslotId();//slotid[i]
						slotInfo[1]=slst;//startinpe[i]
						slotInfo[2]=0;//diff[i]
						
						
						slotInfo[3]=0;//isneedslide[i]
						tempSlotInfo.put(countSlot++, slotInfo);
						
						//startinpe[i] = slst;
						//diff[i]=0;
						//slotid[i] = slotlistinpe.get(j).getslotId();
						//isneedslide[i] = 0;
						//break;
					}
				} else if (predone >= slst && predone < slfi) {
					if ((predone + dagtemp.getts()) <= slfi&& (predone + dagtemp.getts()) <= dagtemp.getdeadline()) {
						Integer[] slotInfo=new Integer[4];
						slotInfo[0]=slotlistinpe.get(j).getslotId();//slotid[i]
						slotInfo[1]=predone;//startinpe[i]
						slotInfo[2]=predone-slst;//diff[i]
						slotInfo[3]=0;//isneedslide[i]
						tempSlotInfo.put(countSlot++, slotInfo);
						
//						startinpe[i] = predone;
//						diff[i]=predone-slst;
//						slotid[i] = slotlistinpe.get(j).getslotId();
//						isneedslide[i] = 0;
//						break;
					}
				}
				

			}
				
			int diffTemp=timewindowmax;
			//�ҵ�����������Ͽ�϶��С�ĵ�
			for(Entry<Integer, Integer[]> map:tempSlotInfo.entrySet()){
				Integer[] slotInfo=map.getValue();
				if(diffTemp>slotInfo[2]){
					diffTemp=slotInfo[2];
					slotid[i] = slotInfo[0];
					startinpe[i]=slotInfo[1];
					diff[i]=slotInfo[2];
					isneedslide[i] =slotInfo[3];
				}
			}
			//System.out.println("��������"+i+"���к��ʵĿ���id��"+tempSlotInfo.size()+"\t��ǰ��������Ϊ��"+dagtemp.getid());
		}
		
		
	
		for (int i = 0; i < pe_number; i++) {
			if (startinpe[i] != -1) {
				findsuc = true;
				if (diff[i] < diffmin) {
					diffmin = diff[i];
					//pemin = i;
				}
			}
		}
		//�ҳ���ͬ��϶��С�Ĵ�����
		ArrayList<Integer> adoptPeList=new ArrayList<>();
		for (int i = 0; i < pe_number; i++) {
			if (diffmin==diff[i]) {
				adoptPeList.add(i);
				}
		}
		
		if(adoptPeList.size()>1){
			int minStart=Integer.MAX_VALUE;
			for(Integer peIndex:adoptPeList){
				if(startinpe[peIndex]<minStart){
					minStart=startinpe[peIndex];
					pemin=peIndex;
				}
			}
			
		}else if (adoptPeList.size()==1) {
			pemin=adoptPeList.get(0);
		}
		
		
//		if(adoptPeList.size()>1){
//			int size=adoptPeList.size();
//			int randomIndex=RandomCreater.random(0,size-1);
//			pemin=randomIndex;
//		}else if(adoptPeList.size()==1){
//			pemin = adoptPeList.get(0);
//		}
		
//		System.out.println("�ʺϵĴ���������\t"+adoptPeList.size()+"\t��ʱѡ���Ǵ������ǣ�"+pemin);
		
		
		
		if(!findsuc){
			DAG tempDag=new DAG();
			tempDag=getDAGById(dagmap.getDAGId(), dagtemp.getid());
			ArrayList<Integer> preArrayList=tempDag.getpre();
			StringBuffer sb=new StringBuffer();
			for(Integer preIndex:preArrayList){
				sb.append(preIndex).append(" ");
			}
			System.out.println(dagtemp.getid()+"\t������:"+dagtemp.getts()+"\t������ʼʱ�䣺"+faDone+"\t��ֹʱ����:"+dagtemp.getdeadline()+"\t���׽ڵ��ǣ�"+sb.toString());
		}
//		
		
	//	System.out.println("pemin="+pemin);
		// 0 is if success 1 means success 0 means fail,
		// 1 is earliest starttime
		// 2 is peid
		// 3 is slotid
		// 4 is if need slide
		// 5 is slide length
		if (findsuc) {
			message[0] = 1;
			message[1] = startinpe[pemin];
			message[2] = pemin;
			message[3] = slotid[pemin];
			message[4] = isneedslide[pemin];
			if (isneedslide[pemin] == 1)
				message[5] = slidelength[pemin];
			else
				message[5] = -1;
		} else {
			message[0] = 0;
		}

		return message;
	}

	/**
	 * @Description: �ж�DAG����ʼ�ڵ��ܷ��ҵ�����ʱ��η���
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param dagtemp
	 *            ����ʼ�ڵ�
	 * @return findsuc���ܷ����
	 */
	public static boolean findfirsttaskslot(DAGMap dagmap, DAG dagtemp) {
		// perfinish is the earliest finish time minus task'ts time, the
		// earliest start time

		boolean findsuc = false;
		int startmin = timewindowmax;
		int finishmin = 0;
		int pemin = -1;
		int slide;
		int[] startinpe = new int[pe_number];// �ڴ�����i�Ͽ�ʼִ�е�����ʱ��
		int[] slotid = new int[pe_number];// ����ڴ�����i��ִ�У�����������slot��id
		// int[] slidinpe = new int[pe_number];//����ڴ�����i��ִ�У�����������slot��id

		// �������д�����
		for (int i = 0; i < pe_number; i++) {
			// ��ʼ�����ʼֵ-1�����б�����ǿ������翪ʼ��ʱ��
			startinpe[i] = -1;
			ArrayList<slot> slotlistinpe = new ArrayList<slot>();
			for (int j = 0; j < SlotListInPes.get(i).size(); j++)
				slotlistinpe.add((slot) SlotListInPes.get(i).get(j));

			// ����������������������ʱ��Ҫ��Ŀ��ж�
			for (int j = 0; j < SlotListInPes.get(i).size(); j++) {
				int slst = slotlistinpe.get(j).getslotstarttime();
				int slfi = slotlistinpe.get(j).getslotfinishtime();

				// ÿ������ĵ���ʱ���ʼ������ҵ����ύʱ�䡣
				// �����һ������ĵ���ʱ�������ҵ�ύ��ʱ�䡣dagtemp.getarrive()
				if (dagtemp.getarrive() <= slst) {// predone<=slst
					if ((slst + dagtemp.getts()) <= slfi && // s1+c<f1
							(slst + dagtemp.getts()) <= dagtemp.getdeadline()) {
						startinpe[i] = slst;
						slotid[i] = slotlistinpe.get(j).getslotId();
						break;
					} else if ((slst + dagtemp.getts()) > slfi
							&& (slst + dagtemp.getts()) <= dagtemp
									.getdeadline()) {
						continue;

					}
				} else {// predone>slst
					if ((dagtemp.getarrive() + dagtemp.getts()) <= slfi // predone+c<f1
							&& (dagtemp.getarrive() + dagtemp.getts()) <= dagtemp
									.getdeadline()) {
						startinpe[i] = dagtemp.getarrive();
						slotid[i] = slotlistinpe.get(j).getslotId();
						break;
					} else if ((dagtemp.getarrive() + dagtemp.getts()) > slfi
							&& (dagtemp.getarrive() + dagtemp.getts()) <= dagtemp
									.getdeadline()) {
						continue;
					}
				}
			}

		}

		// �ҵ���ʼʱ������Ĵ�����
		for (int i = 0; i < pe_number; i++) {
			if (startinpe[i] != -1) {
				// ���ڿ��Բ������һ������Ŀ��п�
				findsuc = true;
				// ѡ�ڿ�ʼʱ������Ŀ��п�
				if (startinpe[i] < startmin) {
					startmin = startinpe[i];
					pemin = i;
				}
			}
		}

		/**
		 * 
		 */
		if (findsuc == false) {
			System.out.println("����ĵ�һ���ڵ�û���ҵ����ʴ������ĵط�����");
		}

		/**
		 * 
		 */
		// ���������һ�����������ܹ������������
		if (findsuc) {
			// startmin:�����紦���ʱ��
			// finishmin:������ִ����ϵ�ʱ��
			finishmin = startmin + dagtemp.getts();
			dagtemp.setfillbackstarttime(startmin);
			dagtemp.setfillbackpeid(pemin);
			dagtemp.setfillbackready(true);

			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(pemin);

			if (TASKInPe.size() > 0) {// ԭ���Ĵ��������ж���������

				ArrayList<slot> slotlistinpe = new ArrayList<slot>();
				for (int j = 0; j < SlotListInPes.get(pemin).size(); j++)
					slotlistinpe.add((slot) SlotListInPes.get(pemin).get(j));

				ArrayList<String> below = new ArrayList<String>();

				slot slottem = new slot();
				// �ҵ�slotlistinpe��Ҫ������Ǹ�slot����
				for (int i = 0; i < slotlistinpe.size(); i++) {
					if (slotlistinpe.get(i).getslotId() == slotid[pemin]) {
						slottem = slotlistinpe.get(i);
						break;
					}
				}

				// �õ�below
				for (int i = 0; i < slottem.getbelow().size(); i++) {
					below.add(slottem.getbelow().get(i));
				}

				if (below.size() > 0) {// Ҫ�����λ�ú����ж������
					// ����������뵽��Ӧ��λ��
					String buf[] = below.get(0).split(" ");
					int inpe = Integer.valueOf(buf[2]).intValue();

					// �����п��������ڴ������ϵ�ִ�д��򶼺���һ��λ��
					for (int i = TASKInPe.size(); i > inpe; i--) {
						Integer[] st_fitemp = new Integer[4];
						st_fitemp[0] = TASKInPe.get(i - 1)[0];
						st_fitemp[1] = TASKInPe.get(i - 1)[1];
						st_fitemp[2] = TASKInPe.get(i - 1)[2];
						st_fitemp[3] = TASKInPe.get(i - 1)[3];
						TASKInPe.put(i, st_fitemp);
					}
					Integer[] st_fi = new Integer[4];
					st_fi[0] = startmin;
					st_fi[1] = finishmin;
					st_fi[2] = dagtemp.getdagid();
					st_fi[3] = dagtemp.getid();
					TASKInPe.put(inpe, st_fi);
					dagtemp.setisfillback(true);
				} else {// Ҫ�����λ�ú���û������Ҳ�Ͳ���Ҫ�ƶ�
					Integer[] st_fi = new Integer[4];
					st_fi[0] = startmin;
					st_fi[1] = finishmin;
					st_fi[2] = dagtemp.getdagid();
					st_fi[3] = dagtemp.getid();
					TASKInPe.put(TASKInPe.size(), st_fi);
				}

			} else {// ����ô�������ԭ��û������
				Integer[] st_fi = new Integer[4];
				st_fi[0] = startmin;
				st_fi[1] = finishmin;
				st_fi[2] = dagtemp.getdagid();
				st_fi[3] = dagtemp.getid();
				TASKInPe.put(TASKInPe.size(), st_fi);
			}

			// �����µĿ��п��б�
			computeSlot(dagmap.getsubmittime(), dagmap.getDAGdeadline());
		} else {
			return false;
		}

		return findsuc;// �����Ƿ��ҵ�λ�ò�������

	}

	/**
	 * @Description: �ж�backfilling�����ܷ�ɹ�, �ӵڶ����ύ����ʼ����
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @return fillbacksuc��backfilling�����ĳɹ����
	 */
	public static boolean fillback(DAGMap dagmap) {

		/**
		 * �ӵڶ�����ҵ��ʼ�������
		 */
	
		int runtime = dagmap.getsubmittime();
		boolean fillbacksuc = true; // ֻҪ��һ������ʧ�ܾ���ȫ��ʧ��

		boolean notfini = true; //������ȫ��������ִ�гɹ�

		ArrayList<DAG> readylist = new ArrayList<DAG>();
		ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>();
		DAGTaskDependValue = dagmap.getdependvalue();

		int DAGID = dagmap.getDAGId();

		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
		}

		while (runtime <= dagmap.getDAGdeadline() && notfini && fillbacksuc) {

			// ������������Ϊ��ǰʱ��ִ����ϵ��������ò���
			for (DAG dag : DAGTaskList) {
				if ((dag.getfillbackstarttime() + dag.getts()) == runtime
						&& dag.getfillbackready()
						&& dag.getfillbackdone() == false
						&& dag.getprefillbackdone() == true
						&& dag.getprefillbackready() == true) {
					dag.setfillbackfinishtime(runtime);
					dag.setfillbackdone(true);
				}
			}

			/**
			 * 
			 * �Ӵ˷�Ϊ������ 1����һ���������жϵ��ȳɹ��ĵ����������Ƿ�ִ�н����������������ý���ʱ��
			 * 2����ǰʱ���Ƿ��п���ȥ���ȵ����񣨾��������о�ȥ����û�о��Թ���
			 * 
			 */
			for (DAG dag : DAGTaskList) {
				// =====================���ñ���ҵ�ĵ�һ������============
				if (dag.getid() == 0 && dag.getfillbackready() == false) {
					if (findfirsttaskslot(dagmap, DAGTaskList.get(0))) {// ��ǰDAG����ʼ�ڵ����ҵ�����ʱ��η���
						DAGTaskList.get(0).setprefillbackready(true);//
						DAGTaskList.get(0).setprefillbackdone(true);

						if (dag.getts() == 0) {// ������������Ϊ�˹�һ������ӽ�ȥ����ʼ�ڵ�
							dag.setfillbackfinishtime(dag.getfillbackstarttime());		
						} else {
							dag.setfillbackfinishtime(dag.getfillbackstarttime() + dag.getts());
						}
						dag.setfillbackdone(true);
						dag.setfillbackready(true);
						// ����ǵ��ڵ�������ô�͵��ȳɹ���ѽ~~
						if (dagmap.isSingle)
							return true;
					} else {// ��ʼ�������ʧ��
						fillbacksuc = false;
						System.out.println("DAG" + DAGID + "�ĵ�һ����������ʧ�ܣ��Ӷ�����ʧ��");
						return fillbacksuc;
					}
				}


				// ================��ѯ��ǰ��������и������Ƿ�����ɣ��������ͽ���ǰ��������������
				// ================�鿴��ǰʱ����û�о�������������������============
				if (dag.getfillbackdone() == false&& dag.getfillbackready() == false) {
					ArrayList<DAG> pre_queue = new ArrayList<DAG>();
					ArrayList<Integer> pre = new ArrayList<Integer>();
					pre = dag.getpre();
					if (pre.size() > 0) {
						boolean ready = true;
						// ��ȡ��ǰ��������и�����
						for (int j = 0; j < pre.size(); j++) {
							DAG buf = new DAG();
							buf = getDAGById(dag.getdagid(), pre.get(j));
							if (buf.getfillbackdone() && buf.getfillbackready()) {
								continue;
							} else {
								ready = false;
								break;
							}
						}
						// ���еĸ���������ˣ��Ϳ��Լ����������
						if (ready) {
							// ��ǰ��������������
							readylist.add(dag);
							// ���ñ�����ĸ������Ѿ���ɻ������
							dag.setprefillbackready(true);
							dag.setprefillbackdone(true);
							dag.setfillbackready(true);
						}
					}
				}
			}

			// ===========�Ծ������н��е���================

			/**
			 * ����û������fillbackdone�ı��
			 */
			if (readylist.size() > 0) {
				// �����������е���ʧ�ܵģ���ֱ��������������ʧ��
				if (!scheduling(dagmap, readylist)) {
					fillbacksuc = false;
					System.out.println("���ȵ�ǰ��������ʧ��");
					return fillbacksuc;
				}
			}

			//���ںϲ����Ǹ�DAG��ҵ
			for (DAG dag : DAGTaskList) {
				if ((dag.getfillbackstarttime() + dag.getts()) == runtime
						&& dag.getfillbackready()
						&& dag.getfillbackdone() == false
						&& dag.getprefillbackdone() == true
						&& dag.getprefillbackready() == true) {
					dag.setfillbackfinishtime(runtime);
					dag.setfillbackdone(true);
				}
			}
			
			// ����ҵ�����е����񶼵��ȳɹ�����δ�������Ϊfalse����������ѭ��
			notfini = false;
			// ��ѯ��ǰʱ�̱�DAG�����е������Ƿ����Ѿ�ִ�гɹ������������񶼳ɹ�������ʱ��ѭ��
			// ����ҵ����û���ȳɹ�������
			for (DAG dag : DAGTaskList) {
				if (dag.getfillbackdone() == false) {
					notfini = true;
					break;
				}
			}

			runtime = runtime + T;

		}

		//����������������ҵ�Ļ������ʱ��
		if (!notfini) {
			for (DAG dag : DAGTaskList) {
				dag.setfillbackfinishtime(dag.getfillbackstarttime()+ dag.getts());
			}
		} else {
			fillbacksuc = false;
			System.out.println("��DAGΪ" + dagmap.getDAGId()+ ",�����˳��������ǣ�ʱ���Ѿ������ˣ�������ҵ�л���û�б�������ɵ�����");
		}

		return fillbacksuc;
	}

	/**
	 * 
	 * @Title: restoreSlotandTASK
	 * @Description: ��ԭSlotListInPes��TASKListInPes
	 * @param SlotListInPestemp
	 *            �����ڻ�ԭ��SlotListInPes
	 * @param TASKListInPestemp
	 *            �����ڻ�ԭ��TASKListInPes
	 * @return void
	 * @throws
	 */
	public static void restoreSlotandTASK(
			HashMap<Integer, ArrayList> SlotListInPestemp,
			HashMap<Integer, HashMap> TASKListInPestemp) {

		SlotListInPes.clear();
		TASKListInPes.clear();

		for (int k = 0; k < SlotListInPestemp.size(); k++) {
			ArrayList<slot> slotListinpe = new ArrayList<slot>();
			for (int j = 0; j < SlotListInPestemp.get(k).size(); j++) {
				slot slottemp = new slot();
				slottemp = (slot) SlotListInPestemp.get(k).get(j);
				slotListinpe.add(slottemp);
			}
			SlotListInPes.put(k, slotListinpe);
		}
		for (int k = 0; k < TASKListInPestemp.size(); k++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			for (int j = 0; j < TASKListInPestemp.get(k).size(); j++) {
				Integer[] temp = new Integer[4];
				temp = (Integer[]) TASKListInPestemp.get(k).get(j);
				TASKInPe.put(j, temp);
			}

			TASKListInPes.put(k, TASKInPe);
		}
	}

	/**
	 * @throws IOException
	 * 
	 * @Title: scheduleOtherDAG
	 * @Description: ʹ��Backfilling���ȵ�i��DAG�������ȳɹ�������LevelRelaxing������
	 *               �����޸�TASKListInPes�и���TASK�Ŀ�ʼ����ʱ�䣬�����Ȳ��ɹ���ȡ����DAG��ִ��
	 * @param @param i��DAG��ID
	 * @param @param SlotListInPestemp�����ڻ�ԭ��SlotListInPes
	 * @param @param TASKListInPestemp�����ڻ�ԭ��TASKListInPes
	 * @return void
	 * @throws
	 */
	public static void scheduleOtherDAG(int i,
			HashMap<Integer, ArrayList> SlotListInPestemp,
			HashMap<Integer, HashMap> TASKListInPestemp) throws IOException {

		int arrive = DAGMapList.get(i).getsubmittime();
		if (arrive > current_time)
			current_time = arrive;

		// �жϱ�DAG��backfilling�����ܷ�ɹ�
		boolean fillbacksuc = fillback(DAGMapList.get(i));

		// ������ɹ�
		if (!fillbacksuc) {
			//���ʧ�ܵĻ����ʹ�ӡ��ǰ��
			//printTaskAndSlot();
			// �޸�����֮ǰ����ò
			restoreSlotandTASK(SlotListInPestemp, TASKListInPestemp);

			DAGMapList.get(i).setfillbackdone(false);

			// ������ҵ����
			DAGMapList.get(i).setfillbackpass(true);

			// ������ҵ����������Ϊ���ԣ�pass��
			ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
			for (int j = 0; j < DAGMapList.get(i).gettasklist().size(); j++) {
				DAGTaskList.add((DAG) DAGMapList.get(i).gettasklist().get(j));
				DAGTaskList.get(j).setfillbackpass(true);
			}

		} else { // �����DAG��backfilling�����ɹ�
			DAGMapList.get(i).setfillbackdone(true);
			DAGMapList.get(i).setfillbackpass(false);

			/***
			 * Ŀǰ�ǣ���������û��Ӱ�찡
			 */
			if (!DAGMapList.get(i).isSingle){
				//System.out.println("~~~~~~~~~~~~~~~~~~~~~~~��ǰ��i�ǣ�"+i);
				wholerelax(DAGMapList.get(i));
			}

		}
	}

	
	/**
	 * @throws IOException 
	 * 
	* @Title: printTaskAndSlot
	* @Description: TODO:
	* @throws
	 */
	private static void printTaskAndSlot() throws IOException {
		// TODO Auto-generated method stub
		
		FileWriter writer = new FileWriter("G:\\task.txt", true);
		FileWriter slotWriter = new FileWriter("G:\\slot.txt", true);

		for(int i=0;i<pe_number;i++){
			HashMap<Integer, Integer[]> taskList=TASKListInPes.get(i);
			ArrayList slotList=SlotListInPes.get(i);
			for(int j=0;j<taskList.size();j++){
				Integer[] result=taskList.get(j);
				writer.write("��ǰ��������"+i+"\t��������DAG"+result[2]+"\t������"+result[3]+"\t��ʼʱ��"+result[0]+"\t����ʱ��"+result[1]+"\n");
			}
			
			for(int k=0;k<slotList.size();k++){
				slot tempSlot=(slot) slotList.get(k);
				slotWriter.write("��������ţ�"+tempSlot.getPEId()+":"+tempSlot.getslotId()+"\t��ʼʱ��"+tempSlot.getslotstarttime()+"\t����ʱ��"+tempSlot.getslotfinishtime()+"\n");
			}
		}
		if (writer != null) {
			writer.close();
		}
		if (slotWriter != null) {
			slotWriter.close();
		}
		
	}

	/**
	 * 
	 * @Title: printTest
	 * @Description: ���Դ�ӡ
	 * @param i
	 * @throws IOException
	 *             :
	 * @throws
	 */
	public static void printRelation(int i) throws IOException {

		FileWriter writer = new FileWriter("G:\\x.txt", true);
		DAGMap tempTestJob = DAGMapList.get(i);
		int dagid = tempTestJob.DAGId;
		int num = tempTestJob.tasknumber;
		for (int o = 0; o < num; o++) {
			DAG tempDag = getDAGById(dagid, o);
			ArrayList<Integer> pre = tempDag.getpre();
			for (int p : pre) {
				DAG tempPre = getDAGById(dagid, p);
				// if(tempPre.getfillbackstarttime()>tempDag.getfillbackstarttime())
				writer.write("DAG��id=" + i + "��������id=" + p + ";��ʼʱ�䣺"
						+ tempPre.getfillbackstarttime() + ";������id=" + o
						+ ";������ʼʱ�䣺" + tempDag.getfillbackstarttime() + "\n");
			}
		}
		if (writer != null) {
			writer.close();
		}

		/**
		 * 
		 */
	}

	/**
	 * 
	 * @Title: copySlot
	 * @Description: 
	 *               �������ڵ�SlotListInPes������ŵ������д���������ƥ�䵱ǰDAG��subimit--deadlineʱ��ε�slot
	 *               �������ڻ�ԭ
	 * @param @return
	 * @return HashMap��SlotListInPestemp
	 * @throws
	 */
	public static HashMap copySlot() {
		HashMap<Integer, ArrayList> SlotListInPestemp = new HashMap<Integer, ArrayList>();

		for (int k = 0; k < SlotListInPes.size(); k++) {

			ArrayList<slot> slotListinpe = new ArrayList<slot>();

			for (int j = 0; j < SlotListInPes.get(k).size(); j++) {
				slot slottemp = new slot();
				slottemp = (slot) SlotListInPes.get(k).get(j);
				slotListinpe.add(slottemp);
			}

			SlotListInPestemp.put(k, slotListinpe);
		}

		return SlotListInPestemp;
	}

	/**
	 * 
	 * @Title: copyTASK
	 * @Description: �������ڵ�TASKListInPes�����ڻ�ԭ
	 * @param @return
	 * @return HashMap
	 * @throws
	 */
	public static HashMap copyTASK() {
		HashMap<Integer, HashMap> TASKListInPestemp = new HashMap<Integer, HashMap>();

		for (int k = 0; k < TASKListInPes.size(); k++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			for (int j = 0; j < TASKListInPes.get(k).size(); j++) {
				Integer[] temp = new Integer[4];
				temp = (Integer[]) TASKListInPes.get(k).get(j);
				TASKInPe.put(j, temp);
			}
			TASKListInPestemp.put(k, TASKInPe);
		}

		return TASKListInPestemp;
	}

	/**
	 * @Description:������Ƚ���еĹؼ�·��
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @return Criticalnumber���ؼ�·���ϵ����������
	 */
	private static int CriticalPath(DAGMap dagmap) {

		ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>();

		DAGTaskDependValue = dagmap.getdependvalue(); // DAGDependValueMap

		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
		}

		int Criticalnumber = 0;
		int i = DAGTaskList.size() - 1;

		/**
		 * ����������ύ�ĵ�һ��DAG��ҵ�ĵĵ�һ���ڵ��Ǹ�����ȥ�ģ�Ҳ���ǳ���Ϊ0����ôҲ���Ƿ���Ϊ1
		 * 
		 * 
		 * ����úúÿ�����
		 */
		if ((dagmap.tasknumber == 2) && (DAGTaskList.get(0).length == 0)) {
			return 1;
		}

		while (i >= 0) {
			// ����DAG�����壩�����һ������һ���ڹؼ�·���ϣ�����inCriticalPathΪtrue
			if (i == (DAGTaskList.size() - 1)) {
				DAGTaskList.get(i).setinCriticalPath(true);
				Criticalnumber++;
			}

			int max = -1;
			int maxid = -1;
			Iterator<Integer> it = DAGTaskList.get(i).getpre().iterator();
			while (it.hasNext()) {
				int pretempid = it.next();
				int temp = (int) ((int) DAGTaskList.get(pretempid).getheftaft() + (double) DAGTaskDependValue
						.get(String.valueOf(pretempid + " " + i)));
				if (temp > max) {
					max = temp;
					maxid = pretempid;
				}
			}

			// System.out.println("maxid="+maxid);
			DAGTaskList.get(maxid).setinCriticalPath(true);
			Criticalnumber++;
			i = maxid;
			if (maxid == 0)
				i = -1;
		}
		return Criticalnumber;
	}

	/**
	 * @Description: Ϊ���ȳɹ���DAG����levelRelaxing����
	 * 
	 * @param dagmap
	 *            ��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @param deadline
	 *            ��DAG�Ľ�ֹʱ��
	 * @param Criticalnumber
	 *            ���ؼ�·���ϵ����������
	 * @throws IOException
	 */
	private static void levelrelax(DAGMap dagmap, int deadline,int Criticalnumber) throws IOException {
		ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();
		ArrayList<DAG> DAGTaskListtemp = new ArrayList<DAG>();
		ArrayList<DAG> setorderbystarttime = new ArrayList<DAG>();

		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>();
		DAGTaskDependValue = dagmap.getdependvalue();

		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
			DAGTaskListtemp.add((DAG) dagmap.gettasklist().get(i));
		}

		// ����ÿ���������ڵĲ㼶
		calculateOriginalLevel(DAGTaskList);

		// ���ݵ��Ƚ�������µĲ㼶
		calculateNewLevel(dagmap, DAGTaskList, DAGTaskListtemp,setorderbystarttime);

		// calculate weight
		// �������Ȩֵ��������
		int levelnumber = DAGTaskList.get(DAGTaskList.size() - 1).getnewlevel();// ��DAG�����һ������Ĳ㼶
		int[] weight = new int[levelnumber];
		int[] relax = new int[DAGTaskList.size()];// ÿ�����������ֵ
		int[] maxlength = new int[levelnumber + 1];
		int weightsum = 0; // Ȩֵ�ܺ�
		// ��DAG���һ������Ļ������ʱ��
		int finishtime = DAGTaskList.get(DAGTaskList.size() - 1).getfillbackfinishtime();
		// ������ֵ
		int totalrelax = deadline - finishtime;

		// ��ͬ�㼶���������ͬһ��list��
		for (int j = 1; j <= levelnumber; j++) {
			ArrayList<Integer> samelevel = new ArrayList<Integer>();
			for (int i = 0; i < dagmap.gettasklist().size(); i++) {
				if (DAGTaskList.get(i).getnewlevel() == j)
					samelevel.add(i);
			}
			dagmap.taskinlevel.put(j, samelevel);
		}

		for (int i = 1; i <= levelnumber; i++) {
			int max = 0, maxid = 0;
			// ����ó������Ȩֵ
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));

				if (i == levelnumber) {// ��������һ�㣬��ôȨֵ����Ϊ����ִ��ʱ��
					max = dagtem.getts();
					maxid = i;
				} else {
					int value = dagtem.getts();
					// ��ȡ��һ�㵱ǰ�����������
					for (int k = 0; k < dagmap.taskinlevel.get(i + 1).size(); k++) {
						DAG dagsuc = new DAG();
						dagsuc = getDAGById(dagmap.getDAGId(),
								(int) dagmap.taskinlevel.get(i + 1).get(k));
						if (dagmap.isDepend(String.valueOf(dagtem.getid()),String.valueOf(dagsuc.getid()))) {

							// �����ǰ�������������ͬһ����������
							if (dagtem.getfillbackpeid() != dagsuc.getfillbackpeid()) {
								int tempp = dagtem.getts()+ (int) (double) DAGTaskDependValue.get(dagtem.getid() + " "+ dagsuc.getid());
								if (value < tempp) {
									value = tempp;
									maxid = dagtem.getid();
								}
							}
						}
					}
					if (max < value) {
						max = value;
						maxid = dagtem.getid();
					}
				}
			}

			weight[i - 1] = max;
			maxlength[i - 1] = maxid;// ���ݵ�����ID
		}

		// ����Ȩֵ�ܺ�
		for (int i = 0; i < levelnumber; i++) {
			weightsum = weight[i] + weightsum;
		}

		// findcriticalnode ���������� ����ִ��ʱ�����
		int maxpelength = 0;
		int maxpeid = 0;
		// ��Ѱ���д����������һ������ִ�н���������ʱ����ڵ�ID
		for (int i = 0; i < pe_number; i++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(i);
			if (TASKInPe.size() > 0) {
				if (maxpelength < TASKInPe.get(TASKInPe.size() - 1)[1]) {
					maxpelength = TASKInPe.get(TASKInPe.size() - 1)[1];
					maxpeid = i;
				}
			}
		}

		// ��Ӧ�������ϵ�������������Ϊiscriticalnode
		for (int i = 0; i < TASKListInPes.get(maxpeid).size(); i++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(maxpeid);
			DAG dagtem = new DAG();
			dagtem = getDAGById((int) TASKInPe.get(i)[2],(int) TASKInPe.get(i)[3]);
			dagtem.setiscriticalnode(true);
		}

		// ����ÿһ�������ֵ�������ڱ�������������
		for (int i = 1; i <= levelnumber; i++) {
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));
				int tem = weight[i - 1] * totalrelax / weightsum;
				dagtem.setrelax(tem);
			}
		}

		int relaxlength = DAGTaskList.get(0).getrelax();

		// ���ñ�DAG�ĵ�һ�������fd
		DAGTaskList.get(0).setslidefinishdeadline(DAGTaskList.get(0).getrelax()+ DAGTaskList.get(0).getfillbackfinishtime());
		// ���ñ�DAG�ĵ�һ�������sd
		DAGTaskList.get(0).setslidedeadline(DAGTaskList.get(0).getrelax()+ DAGTaskList.get(0).getfillbackstarttime());

		// ���û�������
		DAGTaskList.get(0).setslidelength(DAGTaskList.get(0).getrelax());

		for (int i = 2; i <= levelnumber; i++) {
			DAG dagtem1 = new DAG();
			dagtem1 = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i - 1).get(0));

			// ͬһ���fd��һ���ġ��õ���һ���fd
			int starttime = dagtem1.getslidefinishdeadline();

			int finishdeadline = -1;

			// ���ñ�����������s��f
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));

				dagtem.setfillbackstarttime(starttime);
				dagtem.setfillbackfinishtime(dagtem.getfillbackstarttime()+ dagtem.getts());
			}

			// Ĭ��ȡ�ؼ�·���ϵ��������ٽ���ʱ��Ϊ���������������ٽ���ʱ��fd
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));
				if (dagtem.getiscriticalnode()) {// �ҵ��ڹؼ�·���ϵ�����
					finishdeadline = dagtem.getfillbackfinishtime()+ dagtem.getrelax();
					break;
				}
			}

			// �������û���ڹؼ�·���ϵ�������ôȡ��������������f�����ʱ��Ϊfinishdeadline��fd
			if (finishdeadline == -1) {
				for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
					DAG dagtem = new DAG();
					dagtem = getDAGById(dagmap.getDAGId(),
							(int) dagmap.taskinlevel.get(i).get(j));
					if (finishdeadline < dagtem.getfillbackfinishtime())
						finishdeadline = dagtem.getfillbackfinishtime();
				}

			}

			// ���ñ��������fd��sd
			for (int j = 0; j < dagmap.taskinlevel.get(i).size(); j++) {
				DAG dagtem = new DAG();
				dagtem = getDAGById(dagmap.getDAGId(), (int) dagmap.taskinlevel.get(i).get(j));

				dagtem.setslidefinishdeadline(finishdeadline);
				dagtem.setslidedeadline(finishdeadline - dagtem.getts());
				// slidelength=sd-s
				dagtem.setslidelength(dagtem.getslidedeadline()- dagtem.getfillbackstarttime());
			}

		}

	}

	/**
	 * 
	 * @Title: FIFO
	 * @Description: ���㱾�㷨��makespan
	 * @param @param dagmap��DAG����DAG�и����������Լ�DAG�������������ϵ
	 * @return void
	 * @throws
	 */
	public static void FIFO(DAGMap dagmap) {

		int time = current_time;

		ArrayList<DAG> DAGTaskList = new ArrayList<DAG>();// ��DAG�����������б�
		Map<String, Double> DAGTaskDependValue = new HashMap<String, Double>(); // ��DAG����������������Ĵ���ֵ
		DAGTaskDependValue = dagmap.getdependvalue();

		// ��ȡ��DAG������������뵽���㷽���Զ���ı���DAGTaskList��
		for (int i = 0; i < dagmap.gettasklist().size(); i++) {
			DAGTaskList.add((DAG) dagmap.gettasklist().get(i));
		}

		
		
//		for(Entry<String, Double> map:DAGTaskDependValue.entrySet()){
//			String key=map.getKey();
//			double value=map.getValue();
//			System.out.println("����fifo����ʱ��ֵ����Ϣ:\t"+key+"��"+value);
//		}
//
//		System.out.println("+++++++++++++++++++++++++++����");
		
		
		while (time <= timeWindow) { // timeWindowΪ���������ܽ���ʱ��
			boolean fini = true;

			// ������DAG��������Ƿ���й������������pass�����Ƿ�����
			for (DAG dag : DAGTaskList) {
				if (dag.getfillbackdone() == false&& dag.getfillbackpass() == false) {
					fini = false;
					break;
				}
			}

			// �����ǰDAG���������ǽ��й�����ģ������ǻ���ʧ�ܵġ���ô���˳������ٽ��к�������
			if (fini) {
				break;
			}

			// �����������񣬲鿴��ǰʱ�䱾�ý����������Ƿ��ܹ�ִ����ϣ�����fillbackfinishtime��fillbackdone
			for (DAG dag : DAGTaskList) {
				if ((dag.getfillbackstarttime() + dag.getts()) == time&& dag.getfillbackready()&& dag.getfillbackdone() == false) {
					dag.setfillbackfinishtime(time);
					dag.setfillbackdone(true);
					PEList.get(dag.getfillbackpeid()).setfree(true);
				}
			}

			// ��ѯ��ǰʱ����û�о����������оͼ����������
			for (DAG dag : DAGTaskList) {
				if (dag.getarrive() <= time && dag.getfillbackdone() == false&& dag.getfillbackready() == false&& dag.getfillbackpass() == false) {
					boolean ifready = checkready(dag, DAGTaskList,DAGTaskDependValue, time);
					if (ifready) {
						dag.setfillbackready(true);
						readyqueue.add(dag);// �������м����������
					}
				}
			}

			// ���Ⱦ�������
			schedule(DAGTaskList, DAGTaskDependValue, time);

			for (DAG dag : DAGTaskList) {
				if (dag.getfillbackstarttime() == time
						&& dag.getfillbackready()
						&& dag.getfillbackdone() == false) {

					if (dag.getdeadline() >= time) {
						if (dag.getts() == 0) {// ��ǰ�����ִ��ʱ��Ϊ0����ô��Ӧ���ǹ�һ��ʱ��ӽ�ȥ�Ľڵ㣬����ʱ���T
							dag.setfillbackfinishtime(time);
							dag.setfillbackdone(true);
							time = time - T;
						} else {
							PEList.get(dag.getfillbackpeid()).setfree(false);// ��ʵ��������û���õ�����ֶΰ�
							PEList.get(dag.getfillbackpeid()).settask(
									dag.getid());// �ô�������Ϊ����ʱ���ڴ����������
						}
					} else {
						dag.setfillbackpass(true);
					}

				}

			}

			time = time + T;
		}

	}

	/**
	 * @Description:����readyList
	 * 
	 * @param DAGTaskList
	 *            ��DAG��һ�����������б�
	 * @param DAGTaskDependValue
	 *            �������������ڵ�DAG���������������ϵ
	 * @param time
	 *            ����ǰʱ��
	 */
	private static void schedule(ArrayList<DAG> DAGTaskList,
			Map<String, Double> DAGTaskDependValue, int time) {

		ArrayList<DAG> buff = new ArrayList<DAG>();
		DAG min = new DAG();
		DAG temp = new DAG();

		// �����������е����������յ�����Ⱥ�ʱ���������
		// ��Ϊ����������ͬ����һ��DAG����ô���ǵĵ���ʱ����һ���ġ�
		for (int k = 0; k < readyqueue.size(); k++) {
			int tag = k;
			min = readyqueue.get(k);
			temp = readyqueue.get(k);
			for (int p = k + 1; p < readyqueue.size(); p++) {
				if (readyqueue.get(p).getarrive() < min.getarrive()) {
					min = readyqueue.get(p);
					tag = p;
				}
			}
			if (tag != k) {
				readyqueue.set(k, min);
				readyqueue.set(tag, temp);
			}
		}

		// Ϊ�ھ��������е�ÿ��������ѡ����������������DAGTaskList��DAG������
		for (int i = 0; i < readyqueue.size(); i++) {
			DAG buf1 = new DAG();
			buf1 = readyqueue.get(i);

			for (DAG dag : DAGTaskList) {
				if (buf1.getid() == dag.getid()) {
					// Ϊ������ѡ��������ѡ��������翪ʼ�����PE��
					// ���ã���ǰ�����fillbackpeid��ts��fillbackstarttime��finish_suppose��fillbackpass
					choosePE(dag, DAGTaskDependValue, time);
					break;
				}
			}
		}

		readyqueue.clear();

	}

	/**
	 * @Description:�ж�ĳһ�������Ƿ�ﵽ����״̬������������������е�fillbackready�ֶ�
	 * 
	 * @param dag
	 *            ��Ҫ�жϵ�������dag
	 * @param DAGTaskList
	 *            ��DAG�����б�
	 * @param DAGTaskDependValue
	 *            �������������ڵ�DAG���������������ϵ
	 * @param time
	 *            ����ǰʱ��
	 * @return isready����������dag�Ƿ���Լ���readyList
	 */
	private static boolean checkready(DAG dag, ArrayList<DAG> DAGTaskList,
			Map<String, Double> DAGTaskDependValue, int time) {

		boolean isready = true;

		if (dag.getfillbackpass() == false && dag.getfillbackdone() == false) {
			if (time > dag.getdeadline()) {
				dag.setfillbackpass(true);
			}
			if (dag.getfillbackstarttime() == 0
					&& dag.getfillbackpass() == false) {
				ArrayList<DAG> pre_queue = new ArrayList<DAG>();
				ArrayList<Integer> pre = new ArrayList<Integer>();
				pre = dag.getpre();
				if (pre.size() >= 0) {
					for (int j = 0; j < pre.size(); j++) {
						DAG buf3 = new DAG();
						buf3 = getDAGById(dag.getdagid(), pre.get(j));
						pre_queue.add(buf3);

						if (buf3.getfillbackpass()) {
							dag.setfillbackpass(true);
							isready = false;
							break;
						}

						if (!buf3.getfillbackdone()) {
							isready = false;
							break;
						}

					}
				}
			}
		}

		return isready;
	}

	/**
	 * @Description:Ϊ��ǰ����ѡ��������ѡ��������翪ʼ�����PE�����ã�fillbackpeid��ts��fillbackstarttime��finish_suppose��fillbackpass
	 * 
	 * @param dag_temp
	 *            ��Ҫѡ��������DAG��������
	 * @param DAGTaskDependValue
	 *            �������������ڵ�DAG���������������ϵ
	 * @param time
	 *            ����ǰʱ��
	 */
	private static void choosePE(DAG dag_temp,Map<String, Double> DAGTaskDependValue, int time) {

		// ��ȡ��ǰ��������и��������
		ArrayList<DAG> pre_queue = new ArrayList<DAG>();
		ArrayList<Integer> pre = new ArrayList<Integer>();
		pre = dag_temp.getpre();
		if (pre.size() >= 0) {
			for (int j = 0; j < pre.size(); j++) {
				DAG buf = new DAG();
				buf = getDAGById(dag_temp.getdagid(), pre.get(j));
				pre_queue.add(buf);
			}
		}

		int temp[] = new int[PEList.size()];// ������ڸ��������ϵ����翪ʼʱ��

		for (int i = 0; i < PEList.size(); i++) {
			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(i);

			if (pre_queue.size() == 0) {// ��ǰ����û�и����������ǿ�ʼ�ڵ�
				if (TASKInPe.size() == 0) {// ��ǰ�����Ĵ�������û��ִ�й�����
					temp[i] = time;
				} else {
					if (time > TASKInPe.get(TASKInPe.size() - 1)[1])
						temp[i] = time;
					else
						temp[i] = TASKInPe.get(TASKInPe.size() - 1)[1];
				}
			} else if (pre_queue.size() == 1) {// ��ǰ����ֻ��һ��������
				if (pre_queue.get(0).getfillbackpeid() == PEList.get(i).getID()) {// �������������ڵ�ǰ�����Ĵ������ϣ������Ǵ�����������ݴ��俪��
					if (TASKInPe.size() == 0) {
						temp[i] = time;
					} else {
						if (time > TASKInPe.get(TASKInPe.size() - 1)[1])
							temp[i] = time;
						else
							temp[i] = TASKInPe.get(TASKInPe.size() - 1)[1];
					}
				} else {// �������������ڵ�ǰ�����Ĵ������ϣ����Ǵ�����������ݴ��俪��
					// value,�����������ݴ���Ŀ���
					int value = (int) (double) DAGTaskDependValue.get(String
							.valueOf(pre_queue.get(0).getid())
							+ " "
							+ String.valueOf(dag_temp.getid()));
					if (TASKInPe.size() == 0) {// ��ǰ�����Ĵ�������û��ִ�й�����
						if ((pre_queue.get(0).getfillbackfinishtime() + value) < time)
							temp[i] = time;
						else
							temp[i] = pre_queue.get(0).getfillbackfinishtime()
									+ value;
					} else {
						if ((pre_queue.get(0).getfillbackfinishtime() + value) > TASKInPe
								.get(TASKInPe.size() - 1)[1]
								&& (pre_queue.get(0).getfillbackfinishtime() + value) > time)
							temp[i] = pre_queue.get(0).getfillbackfinishtime()
									+ value;
						else if (time > (pre_queue.get(0)
								.getfillbackfinishtime() + value)
								&& time > TASKInPe.get(TASKInPe.size() - 1)[1])
							temp[i] = time;
						else
							temp[i] = TASKInPe.get(TASKInPe.size() - 1)[1];
					}
				}
			} else {// ��ǰ�����ж��������
				int max = time;

				// ����ÿ��������
				for (int j = 0; j < pre_queue.size(); j++) {
					if (pre_queue.get(j).getfillbackpeid() == PEList.get(i)
							.getID()) {
						if (TASKInPe.size() != 0) {
							if (max < TASKInPe.get(TASKInPe.size() - 1)[1])
								max = TASKInPe.get(TASKInPe.size() - 1)[1];
						}
					} else {
						int valu = (int) (double) DAGTaskDependValue.get(String
								.valueOf(pre_queue.get(j).getid())
								+ " "
								+ String.valueOf(dag_temp.getid()));
						int value = pre_queue.get(j).getfillbackfinishtime()
								+ valu;

						if (TASKInPe.size() == 0) {
							if (max < value)
								max = value;
						} else {
							if (value <= TASKInPe.get(TASKInPe.size() - 1)[1]) {
								if (max < TASKInPe.get(TASKInPe.size() - 1)[1])
									max = TASKInPe.get(TASKInPe.size() - 1)[1];
							} else {
								if (max < value)
									max = value;
							}
						}
					}

				}

				temp[i] = max;
			}
		}

		// ѡ����������翪ʼʱ����С�Ĵ�����,min�����ʱ�䣬minpeid��Ӧ�Ĵ��������
		// ��������翪ʼʱ����ͬ�ģ���ô��ѡ������С�Ĵ�����
		int min = timewindowmax;
		int minpeid = -1;
		for (int i = 0; i < PEList.size(); i++) {
			if (min > temp[i]) {
				min = temp[i];
				minpeid = i;
			}
		}

		if (min <= dag_temp.getdeadline()) { // ���翪ʼʱ�� < ������Ľ�ֹʱ��

			HashMap<Integer, Integer[]> TASKInPe = new HashMap<Integer, Integer[]>();
			TASKInPe = TASKListInPes.get(minpeid);

			dag_temp.setfillbackpeid(minpeid);
			dag_temp.setts(dag_temp.getlength());
			dag_temp.setfillbackstarttime(min);
			dag_temp.setfinish_suppose(dag_temp.getfillbackstarttime()+ dag_temp.getts());

			Integer[] st_fi = new Integer[4];
			st_fi[0] = dag_temp.getfillbackstarttime(); // ��������翪ʼʱ��
			st_fi[1] = dag_temp.getfillbackstarttime() + dag_temp.getts(); // ������������ʱ��
			st_fi[2] = dag_temp.getdagid();
			st_fi[3] = dag_temp.getid();
			TASKInPe.put(TASKInPe.size(), st_fi);

		} else {
			dag_temp.setfillbackpass(true);
		}

	}

	/**
	 * @throws IOException
	 * 
	 * @Title: scheduleFirstDAG
	 * @Description: 
	 *               ʹ��FIFO���ȵ�һ��DAG�������ȳɹ�������LevelRelaxing�����������޸�TASKListInPes�и���TASK�Ŀ�ʼ����ʱ��
	 * @param
	 * @return void
	 * @throws
	 */
	public static void scheduleFirstDAG() throws IOException {

		FIFO(DAGMapList.get(0));// DAGMapList����DAG�����壩����Ϣ

		// temΪ��һ��DAG�������һ������
		DAG tem = (DAG) DAGMapList.get(0).gettasklist().get(DAGMapList.get(0).gettasknumber() - 1);

		// �����һ��DAG�������һ�������Ѿ�������ɹ�
		if (tem.getfillbackdone()) {
			DAGMapList.get(0).setfillbackdone(true);
			DAGMapList.get(0).setfillbackpass(false);
		}

		int Criticalnumber = 0;

		// �����һ���ύ��ҵ���ǵ��ڵ�����
		if (DAGMapList.get(0).isSingle) {
			Criticalnumber = 1; // find the critical path and get the task
								// number on it
		} else {
			Criticalnumber = CriticalPath(DAGMapList.get(0));
		}

		// �ɳ��׸�DAG
		levelrelax(DAGMapList.get(0), DAGMapList.get(0).getDAGdeadline(),Criticalnumber); // relax the scheduling result

		// �ı�����������ϵ�����ʼ����������Ϣ
		changetasklistinpe(DAGMapList.get(0));
	}

	// ==================��ʼ������DAGxml�е�����=================
	// ==================��ʼ������DAGxml�е�����=================
	// ==================��ʼ������DAGxml�е�����=================
	// ==================��ʼ������DAGxml�е�����=================
	// ==================��ʼ������DAGxml�е�����=================
	// ==================��ʼ������DAGxml�е�����=================

	/**
	 * @Description:����DAGMAPʵ������ʼ��
	 * 
	 * @param dagdepend
	 *            ��������������ϵ
	 * @param vcc
	 *            ����������
	 */
	public static void initdagmap(DAGdepend dagdepend, computerability vcc,String pathXML) throws Throwable {
		int pre_exist = 0;

		// File file = new File(System.getProperty("user.dir") + "\\DAG_XML\\");

		File file = new File(pathXML);
		String[] fileNames = file.list();
		int num = fileNames.length - 1;

		BufferedReader bd = new BufferedReader(new FileReader(pathXML+ "Deadline.txt"));
		String buffered;

		for (int i = 0; i < num; i++) {
			// ÿ��DAG��һ��dagmap
			DAGMap dagmap = new DAGMap();
			DAGdepend dagdepend_persional = new DAGdepend();

			/*
			 * ���DAG_queue_personal
			 * ����ֻ�е�ǰ���DAG�������б�
			 */
			DAG_queue_personal.clear();

			// ��ȡDAG��arrivetime��deadline��task����
			buffered = bd.readLine();
			String bufferedA[] = buffered.split(" ");
			int buff[] = new int[4];

			buff[0] = Integer.valueOf(bufferedA[0].split("dag")[1]).intValue();// dagID
			buff[1] = Integer.valueOf(bufferedA[1]).intValue();// tasknum
			buff[2] = Integer.valueOf(bufferedA[2]).intValue();// arrivetime
			buff[3] = Integer.valueOf(bufferedA[3]).intValue();// deadline
			int deadline = buff[3];
			// ����Ҳ�ǰ�����Щ��ǰ����ȥ��0.0.0�Ľڵ�
			int tasknum = buff[1];
			// ��Ǳ���ҵ�Ƿ��ǵ�������ҵ
			if (tasknum == 1)
				dagmap.setSingle(true);

			taskTotal = taskTotal + tasknum;
			int arrivetime = buff[2];

			// ��ÿ��DAG��������������������Լ�������Ϣ
			pre_exist = initDAG_createDAGdepend_XML(i, pre_exist, tasknum,arrivetime, pathXML);

			vcc.setComputeCostMap(ComputeCostMap);
			vcc.setAveComputeCostMap(AveComputeCostMap);

			dagdepend_persional.setDAGList(DAG_queue_personal);
			dagdepend_persional.setDAGDependMap(DAGDependMap_personal);
			dagdepend_persional.setDAGDependValueMap(DAGDependValueMap_personal);

			// �Ժ���ǰ����DAG��ÿ������Ľ�ֹʱ��
			createDeadline_XML(deadline, dagdepend_persional);
			
			createSlotDeadline(deadline, dagdepend_persional);

			// ΪDAG_queue�е��������ý�ֹʱ��
			int number_1 = DAG_queue.size();
			int number_2 = DAG_queue_personal.size();
			for (int k = 0; k < number_2; k++) {
				DAG_queue.get(number_1 - number_2 + k).setdeadline(DAG_queue_personal.get(k).getdeadline());
				DAG_queue.get(number_1 - number_2 + k).setdeadline(DAG_queue_personal.get(k).getSlotDeadLine());
			}

			dagmap.settasknumber(tasknum);
			dagmap.setDAGId(i);
			dagmap.setDAGdeadline(deadline);
			dagmap.setsubmittime(arrivetime);
			dagmap.settasklist(DAG_queue_personal);
			dagmap.setdepandmap(DAGDependMap_personal);
			dagmap.setdependvalue(DAGDependValueMap_personal);
			tempDAGMapList.add(dagmap);

		}

		mergeDAG();
		
		// ��������DAGxml�ļ����ɵ�
		dagdepend.setdagmaplist(DAGMapList);
		dagdepend.setDAGList(DAG_queue);
		dagdepend.setDAGDependMap(DAGDependMap);
		dagdepend.setDAGDependValueMap(DAGDependValueMap);
		
		
		
//		System.out.println("�ܵ�dag����" + dagdepend.getdagmaplist().size());	
//		for(int i=0;i<DAGMapList.size();i++){
//			printInitDagMap(i);
//		}

	}
	
	/**
	 * @throws IOException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	* @Title: mergeDAG
	* @Description: ����ʼ���ύʱ����ͬ��DAG�ϳ�Ϊһ��
	* @param dAGMapList:
	* @throws
	 */
	private static void mergeDAG() throws IllegalAccessException, InvocationTargetException, IOException {
		//��ȡ���ϲ�����ҵID
		List<Integer> mergeId=new ArrayList<>();
		int tailTaskId=0;
		for(DAGMap dagMap:tempDAGMapList){
			DAG tempDag=new DAG();
			tempDag=(DAG) dagMap.gettasklist().get(0);
			if(tempDag.getarrive()==0){
				mergeId.add(tempDag.getdagid());
				dagMap.setMerge(true);
				tailTaskId=tailTaskId+dagMap.gettasknumber();
				System.out.println("====>���ϲ�������"+tempDag.getdagid());
			}
		}	
		int behindDAGId=mergeId.get(mergeId.size()-1)+1;
		
		/**
		 * ����Ҫ�ϲ�
		 */
		if(mergeId.size()<=1){
			for(int i=0;i<tempDAGMapList.size();i++){
				DAGMap t=new DAGMap();
				t=tempDAGMapList.get(i);
				DAGMapList.add(t);
			}
			
			for(int i=0;i<DAGMapList.size();i++){
				DAGMap t=new DAGMap();
				t=DAGMapList.get(i);
				ArrayList<DAG> taskList=t.gettasklist();
				for(int k=0;k<t.gettasknumber();k++){
					DAG tempDag=taskList.get(k);
					int oriDagId=tempDag.getdagid();
					int oriTaskId=tempDag.getid();
					tempDag.setOriDagId(oriDagId);
					tempDag.setOriID(oriTaskId);
					tempDag.setdagid(i);
				}
				t.setDAGId(i);
			}
			return ;
		}
		
		
		DAGMap merDagMap=new DAGMap();
		merDagMap.setDAGId(0);
		merDagMap.setMerge(true);
		merDagMap.setsubmittime(0);
		
		ComputeCostMap = new HashMap<Integer, int[]>();
		AveComputeCostMap = new HashMap<Integer, Integer>();
		DAGDependMap_personal = new HashMap<Integer, Integer>();
		DAGDependValueMap_personal = new HashMap<String, Double>();
		
		ArrayList<DAG> newTaskList=new ArrayList<>();
		int currentTaskId=0;
		
		DAG newHeadTask = new DAG();
		newHeadTask.setOriDagId(0);
		newHeadTask.setOriID(0);
		newHeadTask.setlength(0);
		newHeadTask.setts(0);
		newHeadTask.setid(currentTaskId);
		newHeadTask.setdagid(0);
		newHeadTask.setarrive(0);
		newHeadTask.setdeadline(0);
		newHeadTask.setSlotDeadLine(0);
		ArrayList<Integer> newHeadParent=new ArrayList<>();
		ArrayList<Integer> newHeadChild=new ArrayList<>();
		//�������µ�ͷ�������ã���û�н���
		newHeadTask.setpre(newHeadParent);
		newTaskList.add(newHeadTask);
		
		
		DAG newTailTask = new DAG();
		newTailTask.setid(tailTaskId+1);
		System.out.println("tailTaskId="+tailTaskId);
		
		ArrayList<Integer> newTailPre=new ArrayList<>();
		ArrayList<Integer> newTailChild=new ArrayList<>();
		
		int newDeadline=0;
		int newTaskNum=0;
		int flag=1;
		currentTaskId++;
		for(Integer id:mergeId){
			
			System.out.println("id="+id);
			
			//�õ���ǰ��Ҫ�ϲ�����ҵ����
			DAGMap currentDagMap=tempDAGMapList.get(id);
			//mergeLength=mergeLength+currentDagMap.getDAGdeadline()-currentDagMap.getsubmittime();
			int tasknumber=currentDagMap.gettasknumber();
			newTaskNum=newTaskNum+tasknumber;
			
			if(newDeadline<currentDagMap.getDAGdeadline()){
				newDeadline=currentDagMap.getDAGdeadline();
			}
			ArrayList<DAG> taskList=currentDagMap.gettasklist();
			
			for (int j = 0; j < tasknumber; j++) {
				//�µ�task
				DAG merDag = new DAG();
				//ԭ��ҵ����Ӧ������
				DAG tempDag = new DAG();
				tempDag=taskList.get(j);	
				
				DAG dag_persional = new DAG();
				
				BeanUtils.copyProperties(merDag,tempDag);
				BeanUtils.copyProperties(dag_persional,tempDag);
				merDag.setOriDagId(tempDag.getdagid());
				merDag.setOriID(tempDag.getid());
				
				merDag.setdagid(0);
				merDag.setid(currentTaskId);
				currentTaskId++;
				
				dag_persional.setid(Integer.valueOf(j).intValue());

				int x=merDag.getlength();
				int sum = 0;
				int[] bufferedDouble = new int[PEList.size()];
				for (int k = 0; k < PEList.size(); k++) { // x������ĳ���
					bufferedDouble[k] = Integer.valueOf(x/ PEList.get(k).getability());
					sum = sum + Integer.valueOf(x / PEList.get(k).getability());
				}
				ComputeCostMap.put(j, bufferedDouble); // ��ǰ������ÿ���������ϵĴ�����
				AveComputeCostMap.put(j, (sum / PEList.size())); // ��ǰ���������д������ϵ�ƽ��������
				
				ArrayList<Integer> parent=tempDag.getpre();
				ArrayList<Integer> newParent=new ArrayList<>();
				for(Integer per:parent){
					newParent.add(per+flag);
				}
				ArrayList<Integer> newChild=new ArrayList<>();
				ArrayList<Integer> child=tempDag.getsuc();
				for(Integer chi:child){
					newChild.add(chi+flag);
				}	
				
				if(j==0){
					newHeadChild.add(merDag.getid());
					newParent.add(0);//Ϊԭ����ʼ������ָ�����ڵ�Ϊ�¼����
					
					DAGDependMap_personal.put(0, merDag.getid());
					
					int from=0;
					int to=merDag.getid();
					String key=from+" "+to;
					DAGDependValueMap_personal.put(key, (double) 0);
					//System.out.println("�����µ�ͷ����������Ϣ��"+key);
				}
				
				if(j==tasknumber-1){
					newChild.add(newTailTask.getid());
					newTailPre.add(merDag.getid());
					
					DAGDependMap_personal.put(merDag.getid(), newTailTask.getid());
					
					int from=merDag.getid();
					int to=newTailTask.getid();
					//System.out.println("to="+to);
					String key=from+" "+to;
					DAGDependValueMap_personal.put(key, (double) 0);
					//System.out.println("++++�����µ�β����������Ϣ��"+key);
				}
				/**
				 * �ı丸���б�����
				 */
				merDag.replacePre(newParent);
				merDag.replaceChild(newChild);
				newTaskList.add(merDag); // ��ǰDAG��һ���������������б�

			}

			HashMap<Integer, Integer> currentTaskDependMap = new HashMap<Integer, Integer>();
			currentTaskDependMap=currentDagMap.getDAGDependMap();
			HashMap<String, Double> currentTaskDependValueMap = new HashMap<String, Double>();
			currentTaskDependValueMap=currentDagMap.getdependvalue();

			for(Entry<Integer, Integer> map:currentTaskDependMap.entrySet()){
				int key=map.getKey()+flag;
				int value=map.getValue()+flag;
				DAGDependMap_personal.put(key, value);
			}
			
			for(Entry<String, Double> mmap:currentTaskDependValueMap.entrySet()){
				String[] key=mmap.getKey().split(" ");
				int newFrom=Integer.valueOf(key[0]).intValue()+flag;
				int newTo=Integer.valueOf(key[1]).intValue()+flag;
				
				String newKey=newFrom+" "+newTo;
				Double value=mmap.getValue();
				DAGDependValueMap_personal.put(newKey, value);
			}
			
			flag=flag+tasknumber;
				
		}
		
		
		newHeadTask.setsuc(newHeadChild);

		//����β�ڵ���Ϣ
		newTailTask.setOriDagId(0);
		newTailTask.setOriID(currentTaskId);
		newTailTask.setlength(0);
		newTailTask.setts(0);
		newTailTask.setid(currentTaskId);
		newTailTask.setdagid(0);
		newTailTask.setarrive(0);
		newTailTask.setdeadline(newDeadline);
		newTailTask.setSlotDeadLine(newDeadline);
		newTailTask.setpre(newTailPre);
		newTailTask.setsuc(newTailChild);
		newTaskList.add(newTailTask);

		
		merDagMap.settasklist(newTaskList);
		merDagMap.settasknumber(newTaskList.size());
		merDagMap.setDAGdeadline(newDeadline);
		merDagMap.setdepandmap(DAGDependMap_personal);
		merDagMap.setdependvalue(DAGDependValueMap_personal);	
		DAGMapList.add(merDagMap);

	
		for(int i=behindDAGId;i<tempDAGMapList.size();i++){
			DAGMap t=new DAGMap();
			t=tempDAGMapList.get(i);
			DAGMapList.add(t);
		}
		
		for(int i=1;i<DAGMapList.size();i++){
			DAGMap t=new DAGMap();
			t=DAGMapList.get(i);
			ArrayList<DAG> taskList=t.gettasklist();
			for(int k=0;k<t.gettasknumber();k++){
				DAG tempDag=taskList.get(k);
				int oriDagId=tempDag.getdagid();
				int oriTaskId=tempDag.getid();
				tempDag.setOriDagId(oriDagId);
				tempDag.setOriID(oriTaskId);
				tempDag.setdagid(i);
			}
			t.setDAGId(i);
		}
		
		
		
//		System.out.println("���º����ҵ��Ŀ��"+DAGMapList.size());
//		
//		for(int j=0;j<DAGMapList.size();j++){
//			System.out.println("��ǰ��ҵ��������Ŀ:"+DAGMapList.get(j).gettasknumber()+"+++++++++++++++++" +
//					"��ǰ��ҵ��ţ�"+j);
//			printInitDagMap(j);
//		}
		

		/**
		 * 
		 * 
		 * 
		 */
		FileWriter writer = new FileWriter("G:\\initTaskList.txt", true);	
		for(DAG mdag:newTaskList){
			StringBuffer spre=new StringBuffer();
			for(Integer pre:mdag.getpre()){
				spre.append(pre).append(";");
			}
			StringBuffer schi=new StringBuffer();
			for(Integer chi:mdag.getsuc()){
				schi.append(chi).append(";");
			}
			writer.write(""+mdag.getdagid()+":"+mdag.getid()+"\tԭʼ��Ϣ��"+mdag.getOriDagId()+":"+mdag.getid()+
					"\t���ڵ��У�"+spre.toString()+"\t�ӽڵ��У�"+schi.toString()+"\n");
		}
		if (writer != null) {
			writer.close();
		}
	}

	/**
	 * 
	* @Title: printInitDagMap
	* @Description: ��ӡ��ʼ�����ɵ�����DAG������
	* @param i
	* @throws IOException:
	* @throws
	 */
	public static void printInitDagMap(int i) throws IOException {
		FileWriter writer = new FileWriter("G:\\DAGMapList.txt", true);
		DAGMap tempTestJob = DAGMapList.get(i);
		
		System.out.println("��ǰ��ҵ��������Ŀ==========>:"+tempTestJob.gettasknumber()+"\t��ţ�"+i);
		
		int dagid = tempTestJob.DAGId;
		int mergeDagIndex = i;
		
		int num = tempTestJob.tasknumber;
		
		for (int o = 0; o < num; o++) {
			//DAG tempDag = getMergeDAGById(mergeDagIndex, o);
			DAG tempDag = getDAGById(mergeDagIndex, o);
			writer.write("��ҵ��ţ�"+tempDag.getdagid()+":"+tempDag.getid()+"\t����ʱ�䣺"+tempDag.getarrive()+"\t����ʱ�䣺"+tempDag.getdeadline()+
					"\tԭDAG��ţ�"+tempDag.getOriDagId()+":"+tempDag.getOriID()+"\n");
		}
		if (writer != null) {
			writer.close();
		}

	}

	

	// ===============================================================

	/**
	 * @Description:����DAX�ļ�ΪDAG����໥������ϵ�����´�����DAG_queue_personal��DAG_queue��AveComputeCostMap��ComputeCostMap��DAGDependValueMap_personal��DAGDependValueMap��DAGDependMap_personal��DAGDependMap
	 * 
	 * @param i
	 *            ��DAGID
	 * @param preexist
	 *            �������еĹ�������������ȫ����ӵ�һ�����У��ڱ�DAGǰ����preexist������
	 * @param tasknumber
	 *            ��DAG���������
	 * @param arrivetimes
	 *            ��DAG����ʱ��
	 * @return back�������еĹ�������������ȫ����ӵ�һ�����У��ڱ�DAGȫ����Ӻ���back������
	 */
	@SuppressWarnings("rawtypes")
	private static int initDAG_createDAGdepend_XML(int i, int preexist,int tasknumber, int arrivetimes, String pathXML)throws NumberFormatException, IOException, JDOMException {

		int back = 0;
		DAGDependMap_personal = new HashMap<Integer, Integer>();
		DAGDependValueMap_personal = new HashMap<String, Double>();
		ComputeCostMap = new HashMap<Integer, int[]>();
		AveComputeCostMap = new HashMap<Integer, Integer>();

		// ��ȡXML������
		SAXBuilder builder = new SAXBuilder();
		// ��ȡdocument����
		Document doc = builder.build(pathXML + "/dag" + (i + 1) + ".xml");
		// ��ȡ���ڵ�
		Element adag = doc.getRootElement();

		for (int j = 0; j < tasknumber; j++) {
			DAG dag = new DAG();
			DAG dag_persional = new DAG();

			dag.setid(Integer.valueOf(preexist + j).intValue());
			// Ϊÿ����������������DAG�ĵ���ʱ��
			dag.setarrive(arrivetimes);
			dag.setdagid(i);
			dag_persional.setid(Integer.valueOf(j).intValue());
			dag_persional.setarrive(arrivetimes);
			dag_persional.setdagid(i);

			XPath path = XPath.newInstance("//job[@id='" + j + "']/@tasklength");
			List list = path.selectNodes(doc);
			Attribute attribute = (Attribute) list.get(0);
			// x������ĳ���
			int x = Integer.valueOf(attribute.getValue()).intValue();
			dag.setlength(x);
			dag.setts(x);
			dag_persional.setlength(x);
			dag_persional.setts(x);

			if (j == tasknumber - 1) {
				dag.setislast(true);
				islastnum++;
			}

			DAG_queue.add(dag); // ����DAG�������б�
			DAG_queue_personal.add(dag_persional); // ��ǰDAG��һ���������������б�

			int sum = 0;
			int[] bufferedDouble = new int[PEList.size()];
			for (int k = 0; k < PEList.size(); k++) { // x������ĳ���
				bufferedDouble[k] = Integer.valueOf(x
						/ PEList.get(k).getability());
				sum = sum + Integer.valueOf(x / PEList.get(k).getability());
			}
			ComputeCostMap.put(j, bufferedDouble); // ��ǰ������ÿ���������ϵĴ�����
			AveComputeCostMap.put(j, (sum / PEList.size())); // ��ǰ���������д������ϵ�ƽ��������
		}

		XPath path1 = XPath.newInstance("//uses[@link='output']/@file");
		List list1 = path1.selectNodes(doc);
		for (int k = 0; k < list1.size(); k++) {
			Attribute attribute1 = (Attribute) list1.get(k);
			String[] pre_suc = attribute1.getValue().split("_");

			int[] presuc = new int[2];
			presuc[0] = Integer.valueOf(pre_suc[0]).intValue() + preexist;
			presuc[1] = Integer.valueOf(pre_suc[1]).intValue() + preexist;

			XPath path2 = XPath.newInstance("//uses[@file='"+ attribute1.getValue() + "']/@size");
			List list2 = path2.selectNodes(doc);
			Attribute attribute2 = (Attribute) list2.get(0);
			int datasize = Integer.valueOf(attribute2.getValue()).intValue();

			DAGDependMap.put(presuc[0], presuc[1]); // ����DAG�����������ӳ�䣬���ս���ŵ�������������һ��������
			DAGDependValueMap.put((presuc[0] + " " + presuc[1]),(double) datasize);
			DAG_queue.get(presuc[0]).addToSuc(presuc[1]);
			DAG_queue.get(presuc[1]).addToPre(presuc[0]);

			DAGDependMap_personal.put(Integer.valueOf(pre_suc[0]).intValue(),Integer.valueOf(pre_suc[1]).intValue());
			DAGDependValueMap_personal.put((pre_suc[0] + " " + pre_suc[1]),(double) datasize);

			int tem0 = Integer.parseInt(pre_suc[0]);
			int tem1 = Integer.parseInt(pre_suc[1]);
			DAG_queue_personal.get(tem0).addToSuc(tem1);
			DAG_queue_personal.get(tem1).addToPre(tem0);

		}

		
//		for(Entry<Integer, Integer> map:DAGDependMap_personal.entrySet()){
//			System.out.println("======>dag:"+i+"\t"+map.getKey()+":"+map.getValue());
//		}
//		System.out.println("DAGDependValueMap_personal="+DAGDependValueMap_personal.size());
//		
		back = preexist + tasknumber;
		return back;
	}

	/**
	 * @Description:ΪDAG����deadline���Ժ���ǰ���㣬��ÿ�������������Ӧ����ٽ�ֹʱ�䣬��������֮������ݴ��俪��
	 * 
	 * @param dead_line
	 *            ��DAG��deadline
	 * @param dagdepend_persion
	 *            ��DAG���໥������ϵ
	 */
	private static void createDeadline_XML(int dead_line,DAGdepend dagdepend_persion) throws Throwable {
		int maxability = 1;// �������Ĵ������������ж�Ĭ��Ϊ1
		int max;

		for (int k = DAG_queue_personal.size() - 1; k >= 0; k--) {

			max = Integer.MAX_VALUE;
			ArrayList<DAG> suc_queue = new ArrayList<DAG>();
			ArrayList<Integer> suc = new ArrayList<Integer>();
			suc = DAG_queue_personal.get(k).getsuc();
			

			StringBuffer sb=new StringBuffer();
			// ѡ�������������������Ŀ�ʼʱ��Ϊ�Լ��Ľ�ֹʱ�䣬���Ե����ݵĴ��俪��
			if (suc.size() > 0) {
				for (int j = 0; j < suc.size(); j++) {
					sb.append(suc.get(j)).append(";");
					int tem = 0;
					DAG buf3 = new DAG(); // ��ȡ��������������
					buf3 = getDAGById_task(suc.get(j));
					tem = (int) (buf3.getdeadline() - (buf3.getlength() / maxability));
				//	System.out.println("��ǰ����"+k+"\t������"+buf3.getid()+"\t��Ӧʱ���ǣ�"+tem);
					if (max > tem)
						max = tem;
				}
				DAG_queue_personal.get(k).setdeadline(max);
				
			} else {
				DAG_queue_personal.get(k).setdeadline(dead_line);
			}
			
//			System.out.println("��ǰ������"+k+"\t�������У�"+sb.toString());
	
		}
		
//		for (int i = DAG_queue_personal.size() - 1; i >= 0; i--) {
//			System.out.println("������\t"+i+"����Ľ�ֹʱ��Ϊ��"+DAG_queue_personal.get(i).getdeadline()+"\tִ��ʱ��:"+DAG_queue_personal.get(i).getts());
//		}
	}

	
	
	/**
	 * 
	* @Title: createSlotDeadline_XML
	* @Description: ����slotDeadLine��ֵ
	* @param dead_line
	* @param dagdepend_persion
	* @throws Throwable:
	* @throws
	 */
	private static void createSlotDeadline(int dead_line,DAGdepend dagdepend_persion) throws Throwable {
		int maxability = 1;// �������Ĵ������������ж�Ĭ��Ϊ1
		int max = Integer.MAX_VALUE;

		for (int k = DAG_queue_personal.size() - 1; k >= 0; k--) {

			ArrayList<DAG> suc_queue = new ArrayList<DAG>();
			ArrayList<Integer> suc = new ArrayList<Integer>();
			suc = DAG_queue_personal.get(k).getsuc();

			if(k==DAG_queue_personal.size() - 1){
				DAG_queue_personal.get(k).setSlotDeadLine(dead_line);
				continue;
			}
			
			// ѡ�������������������Ŀ�ʼʱ��Ϊ�Լ��Ľ�ֹʱ�䣬�������ݵĴ���
			if (suc.size() > 0) {
				for (int j = 0; j < suc.size(); j++) {
					int tem = 0;
					DAG buf3 = new DAG(); // ��ȡ��������������
					buf3 = getDAGById_task(suc.get(j));
					suc_queue.add(buf3);
					tem = (int) (buf3.getdeadline() - (buf3.getlength() / maxability)-DAG_queue_personal.get(k).getlength());
					if (max > tem)
						max = tem;
				}
				DAG_queue_personal.get(k).setSlotDeadLine(max);
			} else {
				DAG_queue_personal.get(k).setSlotDeadLine(dead_line);
			}
			
			//System.out.println("��ʱ�Ľ���ʱ��Ϊ��"+k+"\t"+DAG_queue_personal.get(k).getSlotDeadLine());
		}
	}
	
	/**
	 * 
	 * @Title: initPE
	 * @Description: ����PEʵ������ʼ�������ô������ļ�������Ϊ1
	 * @param @throws Throwable
	 * @return void
	 * @throws
	 */
	private static void initPE() throws Throwable {

		for (int i = 0; i < pe_number; i++) {
			PE pe = new PE();
			pe.setID(i);
			pe.setability(1);
			pe.setfree(true);
			pe.setAvail(0);
			PEList.add(pe);
		}
	}

	/**
	 * @Description:����DAGID��TASKID���ظ�TASKʵ��
	 * 
	 * @param DAGId
	 *            ��DAGID
	 * @param dagId
	 *            ��TASKID
	 * @return DAG��TASKʵ��
	 */
	private static DAG getDAGById(int DAGId, int dagId) {
		
		for (int i = 0; i < DAGMapList.get(DAGId).gettasknumber(); i++) {
			DAG temp = (DAG) DAGMapList.get(DAGId).gettasklist().get(i);
			if (temp.getid() == dagId)
				return temp;
		}

		return null;
	}
	
	
	private static DAG getMergeDAGById(int mergeDagIndex, int dagId) {
		
		for (int i = 0; i < DAGMapList.get(mergeDagIndex).gettasknumber(); i++) {
			DAG temp = (DAG) DAGMapList.get(mergeDagIndex).gettasklist().get(i);
			if (temp.getid() == dagId)
				return temp;
		}

		return null;
	}

	/**
	 * @Description:����TASKID���ظ�TASKʵ��
	 * 
	 * @param dagId
	 *            ��TASKID
	 * @return DAG��TASKʵ��
	 */
	private static DAG getDAGById_task(int dagId) {
		for (DAG dag : DAG_queue_personal) {
			if (dag.getid() == dagId)
				return dag;
		}
		return null;
	}

}
