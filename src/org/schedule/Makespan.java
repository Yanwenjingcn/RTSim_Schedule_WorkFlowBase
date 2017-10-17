package org.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.List;
import org.jdom.xpath.XPath;
import org.jdom.Attribute;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.generate.BuildParameters;

public class Makespan {
	
	private static int CURRENT_TIME = 0;
	private static int task_num = 0;
	private static int clocktick = 1;
	private static double heft_deadline = 0;
	private static int islastnum = 0;
	private static double deadLineTimes = 1.3;//deadline的倍数值 （1.1，1.3，1.6，2.0）
	private static int pe_number = 8;
	public static String[][] rate = new String[5][3];
	
	private static ArrayList<PE> PEList;
	private static ArrayList<DAG> DAG_queue;
	private static ArrayList<DAG> ready_queue;

	private static HashMap<Integer,Integer> DAG_deadline;
	private static HashMap<Integer,Integer> DAGDependMap;
	private static HashMap<String,Double> DAGDependValueMap;

	private static ArrayList<DAG> DAG_queue_personal;
	private static HashMap<Integer,Integer> DAGDependMap_personal;
	private static HashMap<String,Double> DAGDependValueMap_personal;
	private static Map<Integer,int[]> ComputeCostMap;
	private static Map<Integer,Integer> AveComputeCostMap;
	
	public static Map<Integer,double[]> DAGExeTimeMap;
	private static Map<Integer,DAG> DAGIdToDAGMap;
	private static Map<Integer,Double> upRankValueMap;
	private static Map<Integer,double[]> vmComputeCostMap;
	private static Map<Integer,Double> vmAveComputeCostMap;	
	private static Map<Integer,Integer[]> cloudletInVm;
	private static Map<Integer,Integer> cloudletInVmId;
	
	public Makespan(){
		ready_queue = new ArrayList<DAG>();
		DAG_queue = new ArrayList<DAG>();
		DAG_queue_personal = new ArrayList<DAG>();
		PEList = new ArrayList<PE>();
		DAGDependMap = new HashMap<Integer,Integer>();
		DAGDependValueMap = new HashMap<String,Double>();
		deadLineTimes = BuildParameters.deadLineTimes;
		pe_number = BuildParameters.processorNumber;
	}

	/**
	 * 输出FIFO，EDF，STF，EFTF，NEWEDF调度结果
	 */
	
	//public static void main(String[] args) throws Throwable{
	public void runMakespan_xml() throws Throwable{

		
		
		Makespan ms = new Makespan();
		DAGdepend dagdepend = new DAGdepend();
		computerability vcc = new computerability();
		initPE();
		int num = initdagmap(dagdepend,vcc);

		fiforesult(dagdepend,num);
		edfresult(dagdepend,num);
		stfresult(dagdepend,num);
		eftfresult(dagdepend,num);
		newEDFresult(dagdepend,num);
	
	}
	
	/**
	 * 输出FIFO调度结果
	 * @param dagdepend，工作流依赖关系
	 * @param num,全部DAG的各个子任务总个数
	 */
	public static void fiforesult(DAGdepend dagdepend,int num) throws Throwable
	{
		FIFO fifo = new FIFO(pe_number);
		fifo.dag_queue_ori = DAG_queue;
		fifo.course_number = DAG_queue.size();
		fifo.pe_number = pe_number;
		fifo.pe_list = PEList;
		fifo.dagdepend = dagdepend;
		int[] temp1 = new int[pe_number+3];
		temp1 = fifo.makespan(pe_number);
		
		DecimalFormat df = new DecimalFormat("0.0000");
		double sum = 0;
		
		System.out.println("FIFO:");
		for(int j = 0;j<PEList.size();j++)
		{
			sum = (float)temp1[j+1]/temp1[0]+sum;
		}
		System.out.println("PE's use ratio is "+df.format((float)sum/pe_number));
		System.out.println("effective PE's use ratio is "+df.format((float)temp1[pe_number+2]/(temp1[0]*pe_number)));
		System.out.println("Task Completion Rates is "+df.format((float)temp1[pe_number+1]/num));
		System.out.println();
		rate[0][0] = df.format((float)sum/pe_number);
		rate[0][1] = df.format((float)temp1[pe_number+1]/num);
		rate[0][2] = df.format((float)temp1[pe_number+2]/(temp1[0]*pe_number));
	}
	
	
	/**
	 * 输出EDF调度结果
	 * @param dagdepend，工作流依赖关系
	 * @param num,全部DAG的各个子任务总个数
	 */
	public static void edfresult(DAGdepend dagdepend,int num) throws Throwable
	{
		EDF edf = new EDF(pe_number);
		edf.dag_queue_ori = DAG_queue;
		edf.course_number = DAG_queue.size();
		edf.pe_number = pe_number;
		edf.pe_list = PEList;
		edf.dagdepend = dagdepend;
		int[] temp2 = new int[pe_number+3];
		temp2 = edf.makespan(pe_number);
		
		DecimalFormat df = new DecimalFormat("0.0000");
		double sum = 0;
		
		System.out.println("EDF:");
		for(int j = 0;j<PEList.size();j++)
		{
			sum = (float)temp2[j+1]/temp2[0]+sum;
		}
		System.out.println("PE's use ratio is "+df.format((float)sum/pe_number));
		System.out.println("effective PE's use ratio is "+df.format((float)temp2[pe_number+2]/(temp2[0]*pe_number)));
		System.out.println("Task Completion Rates is "+df.format((float)temp2[pe_number+1]/num));
		System.out.println();
		rate[1][0] = df.format((float)sum/pe_number);
		rate[1][1] = df.format((float)temp2[pe_number+1]/num);
		rate[1][2] = df.format((float)temp2[pe_number+2]/(temp2[0]*pe_number));
	}
	
	/**
	 * 输出STF调度结果
	 * @param dagdepend，工作流依赖关系
	 * @param num,全部DAG的各个子任务总个数
	 */
	public static void stfresult(DAGdepend dagdepend,int num) throws Throwable
	{
		STF stf = new STF(pe_number);
		stf.dag_queue_ori = DAG_queue;
		stf.course_number = DAG_queue.size();
		stf.pe_number = pe_number;
		stf.pe_list = PEList;
		stf.dagdepend = dagdepend;
		int[] temp3 = new int[pe_number+3];
		temp3 = stf.makespan(pe_number);
		
		DecimalFormat df = new DecimalFormat("0.0000");
		double sum = 0;
		
		System.out.println("STF:");
		for(int j = 0;j<PEList.size();j++)
		{
			sum = (float)temp3[j+1]/temp3[0]+sum;
		}
		System.out.println("PE's use ratio is "+df.format((float)sum/pe_number));
		System.out.println("effective PE's use ratio is "+df.format((float)temp3[pe_number+2]/(temp3[0]*pe_number)));
		System.out.println("Task Completion Rates is "+df.format((float)temp3[pe_number+1]/num));
		System.out.println();
		rate[2][0] = df.format((float)sum/pe_number);
		rate[2][1] = df.format((float)temp3[pe_number+1]/num);
		rate[2][2] = df.format((float)temp3[pe_number+2]/(temp3[0]*pe_number));
	}
	
	/**
	 * 输出EFTF调度结果
	 * @param dagdepend，工作流依赖关系
	 * @param num,全部DAG的各个子任务总个数
	 */
	public static void eftfresult(DAGdepend dagdepend,int num) throws Throwable
	{
		EFFF efff = new EFFF(pe_number);
		efff.dag_queue_ori = DAG_queue;
		efff.course_number = DAG_queue.size();
		efff.pe_number = pe_number;
		efff.pe_list = PEList;
		efff.dagdepend = dagdepend;
		int[] temp4 = new int[pe_number+3];
		temp4 = efff.makespan(pe_number);
		
		DecimalFormat df = new DecimalFormat("0.0000");
		double sum = 0;
		
		System.out.println("EFTF:");
		for(int j = 0;j<PEList.size();j++)
		{
			sum = (float)temp4[j+1]/temp4[0]+sum;
		}
		System.out.println("PE's use ratio is "+df.format((float)sum/pe_number));
		System.out.println("effective PE's use ratio is "+df.format((float)temp4[pe_number+2]/(temp4[0]*pe_number)));
		System.out.println("Task Completion Rates is "+df.format((float)temp4[pe_number+1]/num));
		System.out.println();
		rate[3][0] = df.format((float)sum/pe_number);
		rate[3][1] = df.format((float)temp4[pe_number+1]/num);
		rate[3][2] = df.format((float)temp4[pe_number+2]/(temp4[0]*pe_number));
	}
	
	/**
	 * 输出newEDF调度结果
	 * @param dagdepend，工作流依赖关系
	 * @param num,全部DAG的各个子任务总个数
	 */
	public static void newEDFresult(DAGdepend dagdepend,int num) throws Throwable
	{
		NewEDF newedf = new NewEDF(pe_number);
		newedf.dag_queue_ori = DAG_queue;
		newedf.course_number = DAG_queue.size();
		newedf.pe_number = pe_number;
		newedf.pe_list = PEList;
		newedf.dagdepend = dagdepend;
		int[] temp5 = new int[pe_number+3];
		temp5 = newedf.makespan(pe_number);
		
		DecimalFormat df = new DecimalFormat("0.0000");
		double sum = 0;
		
		System.out.println("NewEDF:");
		for(int j = 0;j<PEList.size();j++)
		{
			sum = (float)temp5[j+1]/temp5[0]+sum;
		}
		System.out.println("PE's use ratio is "+df.format((float)sum/pe_number));
		System.out.println("effective PE's use ratio is "+df.format((float)temp5[pe_number+2]/(temp5[0]*pe_number)));
		System.out.println("Task Completion Rates is "+df.format((float)temp5[pe_number+1]/num));
		System.out.println();
		rate[4][0] = df.format((float)sum/pe_number);
		rate[4][1] = df.format((float)temp5[pe_number+1]/num);
		rate[4][2] = df.format((float)temp5[pe_number+2]/(temp5[0]*pe_number));
	}
	
	/**
	 * 创建DAGMAP实例并初始化
	 * @param dagdepend，工作流依赖关系
	 * @param vcc，计算能力
	 * @return num,全部DAG的各个子任务总个数
	 */
	public static int initdagmap(DAGdepend dagdepend,computerability vcc) throws Throwable
	{
		int pre_exist = 0;
		File file = new File(System.getProperty("user.dir")+"\\DAG_XML\\");
		String[] fileNames =file.list();
		int num = fileNames.length-1;
		
		
		BufferedReader bd = new BufferedReader(new FileReader("DAG_XML/Deadline.txt"));
		String buffered;
		
		for(int i = 0;i<num;i++)
		{

			DAGdepend dagdepend_persional = new DAGdepend();
			DAG_queue_personal.clear();

			//获取DAG的arrivetime和deadline，task个数
			buffered=bd.readLine();
			String bufferedA[] = buffered.split(" ");
			int buff[] = new int[4];
			
			buff[0]= Integer.valueOf(bufferedA[0].split("dag")[1]).intValue();//dagID
			buff[1]= Integer.valueOf(bufferedA[1]).intValue();//tasknum
			buff[2]= Integer.valueOf(bufferedA[2]).intValue();//arrivetime
			buff[3]= Integer.valueOf(bufferedA[3]).intValue();//deadline
			int deadline = buff[3];
			int tasknum = buff[1];
			int arrivetime = buff[2];
			
			pre_exist = initDAG_createDAGdepend_XML(i,pre_exist,tasknum,arrivetime);
			
			vcc.setComputeCostMap(ComputeCostMap);
			vcc.setAveComputeCostMap(AveComputeCostMap);
			dagdepend_persional.setDAGList(DAG_queue_personal);
			dagdepend_persional.setDAGDependMap(DAGDependMap_personal);
			dagdepend_persional.setDAGDependValueMap(DAGDependValueMap_personal);
						
			createDeadline_XML(deadline,dagdepend_persional);
			
			int number_1 = DAG_queue.size();
			int number_2 = DAG_queue_personal.size();
			for(int k=0;k<number_2;k++)
			{
				DAG_queue.get(number_1-number_2+k).setdeadline(DAG_queue_personal.get(k).getdeadline());
			}
			
			
			double makespan = HEFT(DAG_queue_personal,dagdepend_persional);
			ComparatorDAGori comparator = new ComparatorDAGori();
			Collections.sort(DAG_queue_personal,comparator);
			
			int Criticalnumber = CriticalPath(DAG_queue_personal,dagdepend_persional);
			setNewDeadline(DAG_queue_personal,dagdepend_persional,arrivetime,deadline,makespan,Criticalnumber);
			for(int k=0;k<number_2;k++)
			{
				DAG_queue.get(number_1-number_2+k).setnewdeadline(DAG_queue_personal.get(k).getnewdeadline());
			}
			for(int k=0;k<number_2;k++)
			{
				System.out.println("orideadline:"+DAG_queue_personal.get(k).getdeadline()+" newdeadline:"+DAG_queue_personal.get(k).getnewdeadline());
			}

			clear();
		}
	
		dagdepend.setDAGList(DAG_queue);
		dagdepend.setDAGDependMap(DAGDependMap);
		dagdepend.setDAGDependValueMap(DAGDependValueMap);
		
		return num;
	}
	
	/**
	 * 清空PELIST
	 */
	private static void clear() throws Throwable
	{
		PEList.clear();
		initPE();
	}
	
	/**
     * 计算调度结果中的关键路径
     *
     * @param dagqueue_heft，DAG中各个子任务
     * @param dagdepend_heft，DAG中任务间依赖关系
     * @return Criticalnumber，关键路径上的子任务个数
     */
	private static int CriticalPath(ArrayList<DAG> dagqueue_heft,DAGdepend dagdepend_heft)
	{
		int Criticalnumber = 0;
		int i=dagqueue_heft.size()-1;
		while(i>=0)
		{
			if(i==(dagqueue_heft.size()-1))
			{
				dagqueue_heft.get(i).setinCriticalPath(true);
				Criticalnumber++;
			}
			
			int max = -1;
			int maxid = -1;
			Iterator <Integer>it = dagqueue_heft.get(i).getpre().iterator();
			while(it.hasNext())
			{
				int pretempid = it.next();	
				int temp = (int) ((int) dagqueue_heft.get(pretempid).getheftaft()+(double)dagdepend_heft.getDAGDependValueMap().get(pretempid+" "+i));
				if(temp>max)
				{
					max = temp;
					maxid = pretempid;
				}
			}
			dagqueue_heft.get(maxid).setinCriticalPath(true);
			Criticalnumber++;
			i = maxid;
			if(maxid == 0)
				i=-1;
		}
		return Criticalnumber;
	}
	
	/**
	 * 通过HEFT算法的调度结果，对每个子任务计算deadline
	 * @param dagqueue_heft，DAG中各个子任务
     * @param dagdepend_heft，DAG中任务间依赖关系
     * @param arrivetime，DAG到达时间
	 * @param deadline，DAG截止时间
	 * @param makespan，HEFT调度结果的makespan
	 * @param Criticalnumber，关键路径上的子任务个数
	 */
	private static void setNewDeadline(ArrayList<DAG> dagqueue_heft,DAGdepend dagdepend_heft,int arrivetime,int deadline,double makespan,int Criticalnumber)
	{
		double redundancy = ((deadline-arrivetime)-makespan);
		double preredundancy = (redundancy/Criticalnumber);
		int cnum = Criticalnumber;
		
		for(int i=dagqueue_heft.size()-1;i>=0;i--)
		{
			if(dagqueue_heft.get(i).getinCriticalPath())
			{
				int newdeadline = (int) ((int)dagqueue_heft.get(i).getheftaft()+dagqueue_heft.get(i).getarrive()+preredundancy*cnum);
				dagqueue_heft.get(i).setnewdeadline(newdeadline);
				cnum--;
				Iterator <Integer>it = dagqueue_heft.get(i).getpre().iterator();
				while(it.hasNext()){
					int pretempid = it.next();			
					int dead = (int)(dagqueue_heft.get(i).getnewdeadline()-(double)dagdepend_heft.getDAGDependValueMap().get(pretempid+" "+i));
					dagqueue_heft.get(pretempid).setnewdeadline(dead);
				}
			}
			else
			{
				if(dagqueue_heft.get(i).getnewdeadline()!=0)
				{
					Iterator <Integer>it = dagqueue_heft.get(i).getpre().iterator();
					while(it.hasNext()){
						int pretempid = it.next();					
						int dead = (int)(dagqueue_heft.get(i).getnewdeadline()-(double)dagdepend_heft.getDAGDependValueMap().get(pretempid+" "+i));
						dagqueue_heft.get(pretempid).setnewdeadline(dead);
					}
				}
			}
		}
		
		for(int i=dagqueue_heft.size()-1;i>=0;i--)
		{
			if(dagqueue_heft.get(i).getnewdeadline()==0)
			{
				dagqueue_heft.get(i).setnewdeadline(dagqueue_heft.get(dagqueue_heft.size()-1).getnewdeadline());
			}
		}
		
	}
	
	/**
	 * 根据DAX文件为DAG添加相互依赖关系
	 * @param i，DAGID
	 * @param preexist，将所有的工作流中子任务全部添加到一个队列，在本DAG前已有preexist个任务
	 * @param tasknumber，DAG中任务个数
	 * @param arrivetimes，DAG到达时间
	 * @return back，将所有的工作流中子任务全部添加到一个队列，在本DAG全部添加后，有back个任务
	 */
	@SuppressWarnings("rawtypes")
	private static int initDAG_createDAGdepend_XML(int i,int preexist,int tasknumber,int arrivetimes) throws NumberFormatException, IOException, JDOMException{
		
		int back = 0;
		DAGDependMap_personal = new HashMap<Integer,Integer>();
		DAGDependValueMap_personal = new HashMap<String,Double>();
		ComputeCostMap = new HashMap<Integer,int[]>();
		AveComputeCostMap = new HashMap<Integer,Integer>();
	
		//获取XML解析器
		SAXBuilder builder = new SAXBuilder();
		//获取document对象
		Document doc = builder.build("DAG_XML/dag"+(i+1)+".xml");
		//获取根节点
		Element adag = doc.getRootElement();
						
		for(int j = 0;j<tasknumber;j++)
		{
			DAG dag = new DAG();
			DAG dag_persional = new DAG();

			dag.setid(Integer.valueOf(preexist+j).intValue());
			dag.setarrive(arrivetimes);
			dag.setdagid((i+1));
			dag_persional.setid(Integer.valueOf(j).intValue());
			dag_persional.setarrive(arrivetimes);
			dag_persional.setdagid((i+1));
			
			XPath path = XPath.newInstance("//job[@id='"+j+"']/@tasklength");
			List list = path.selectNodes(doc);
			Attribute attribute = (Attribute)list.get(0);
			int x = Integer.valueOf(attribute.getValue()).intValue();
			dag.setlength(x);
			dag_persional.setlength(x);
			
			if(j == tasknumber-1)
			{
				dag.setislast(true);
				islastnum++;
			}
			
			DAG_queue.add(dag);
			DAG_queue_personal.add(dag_persional);
			
			int sum = 0;
			int[] bufferedDouble = new int[PEList.size()];
			for(int k=0;k<PEList.size();k++){
				bufferedDouble[k] = Integer.valueOf(x/PEList.get(k).getability());
				sum = sum + Integer.valueOf(x/PEList.get(k).getability());
			}
			ComputeCostMap.put(j, bufferedDouble);
			AveComputeCostMap.put(j,(sum/PEList.size()));
		} 
		
		XPath path1 = XPath.newInstance("//uses[@link='output']/@file");
		List list1 = path1.selectNodes(doc);
		for(int k=0;k<list1.size();k++)
		{			
			Attribute attribute1 = (Attribute)list1.get(k);
			String[] pre_suc = attribute1.getValue().split("_");
			int[] presuc = new int[2];
			presuc[0] = Integer.valueOf(pre_suc[0]).intValue()+preexist;
			presuc[1] = Integer.valueOf(pre_suc[1]).intValue()+preexist;

			XPath path2 = XPath.newInstance("//uses[@file='"+attribute1.getValue()+"']/@size");
			List list2 = path2.selectNodes(doc);
			Attribute attribute2 = (Attribute)list2.get(0);
			int datasize = Integer.valueOf(attribute2.getValue()).intValue();
			
			DAGDependMap.put(presuc[0],presuc[1]);
			DAGDependValueMap.put((presuc[0]+" "+presuc[1]), (double) datasize);

			DAG_queue.get(presuc[0]).addToSuc(presuc[1]);
			DAG_queue.get(presuc[1]).addToPre(presuc[0]);
			
			DAGDependMap_personal.put(Integer.valueOf(pre_suc[0]).intValue(),Integer.valueOf(pre_suc[1]).intValue());
			DAGDependValueMap_personal.put((pre_suc[0]+" "+pre_suc[1]),(double) datasize);

			int tem0 = Integer.parseInt(pre_suc[0]);
			int tem1 = Integer.parseInt(pre_suc[1]);
			
			DAG_queue_personal.get(tem0).addToSuc(tem1);
			DAG_queue_personal.get(tem1).addToPre(tem0);
			
		}
		
		back = preexist+tasknumber;
		return back;
	}

	/**
	 * 为DAG根据deadline，给每个子任务计算相应的最迟截止时间
	 * @param dead_line，DAG的deadline
	 * @param dagdepend_persion，DAG中相互依赖关系
	 */
	private static void createDeadline_XML(int dead_line,DAGdepend dagdepend_persion) throws Throwable{
		int maxability = 1;
		int max = 10000;

		for(int k=DAG_queue_personal.size()-1 ;k >= 0 ; k--)
		{
			ArrayList<DAG> suc_queue = new ArrayList<DAG>();
			ArrayList<Integer> suc = new ArrayList<Integer>(); 
			suc = DAG_queue_personal.get(k).getsuc();
			if(suc.size()>0)
			{
				for(int j = 0;j<suc.size();j++)
				{
					int tem = 0;
					DAG buf3 = new DAG();
					buf3 = getDAGById(suc.get(j));
					suc_queue.add(buf3);
					tem = (int) (buf3.getdeadline() - (buf3.getlength()/maxability));
					if(max > tem)
						max = tem;
				}	
				DAG_queue_personal.get(k).setdeadline(max);
			}
			else
			{
				DAG_queue_personal.get(k).setdeadline(dead_line);
			}	
		}
	}

	/**
	 * 创建PE实例并初始化
	 */
	private static void initPE() throws Throwable{
		
		for(int i=0;i<pe_number;i++)
		{
			PE pe = new PE();
			pe.setID(i);
			pe.setability(1);
			pe.setfree(true);
			pe.setAvail(0);
			PEList.add(pe);
		}
	}
	
	/**
	 * 根据TASKID返回该TASK实例
	 * @param dagId，TASKID
	 * @return DAG，TASK实例
	 */
	private static DAG getDAGById(int dagId){
		for(DAG dag:DAG_queue_personal){
			if(dag.getid() == dagId)
				return dag;
		}
		return null;
	}
	
	/**
	 * 用HEFT对每个DAG预先进行调度
	 * @param dagqueue_heft，DAG中各个子任务
     * @param dagdepend_heft，DAG中任务间依赖关系
	 * @return makespan，HEFT调度结果的makespan
	 */
	public static double HEFT(ArrayList<DAG> dagqueue_heft,DAGdepend dagdepend_heft) throws Throwable{
		DAGExeTimeMap = new HashMap<Integer,double[]>();
		DAGIdToDAGMap = new HashMap<Integer,DAG>();
		upRankValueMap = new HashMap<Integer,Double>();
		createVmComputeCost(dagqueue_heft);
		for(int i=0;i<dagqueue_heft.size();i++){
			DAGIdToDAGMap.put(i, dagqueue_heft.get(i));
		}
		computeUpRankValue(dagqueue_heft,dagdepend_heft);
		ComparatorDAG comparator = new ComparatorDAG();
		Collections.sort(dagqueue_heft,comparator);
		double makespan = assignVm_(dagqueue_heft,dagdepend_heft);
		return makespan;
	}	

	/**
	 * HEFT初始化
	 * @param dagqueue_heft，DAG中各个子任务
	 */
	private static void createVmComputeCost(ArrayList<DAG> dagqueue_heft1) throws IOException{
		vmComputeCostMap = new HashMap<Integer,double[]>();
		vmAveComputeCostMap = new HashMap<Integer,Double>();
		int num = 0;
		for(int i=0;i<dagqueue_heft1.size();i++){
			double sum = 0;
			double ComputeCost[]=new double[pe_number];
			for(int j=0;j<pe_number;j++){
				ComputeCost[j]=dagqueue_heft1.get(i).getlength();
				sum += ComputeCost[j];
			}
			vmComputeCostMap.put(num, ComputeCost);
			vmAveComputeCostMap.put(num, sum/pe_number);
			num++;
		}
	}
	
	/**
	 * HEFT计算优先级
	 * @param dagqueue_heft，DAG中各个子任务
     * @param dagdepend_heft，DAG中任务间依赖关系
	 */
	public static void computeUpRankValue(ArrayList<DAG> dagqueue_heft2,DAGdepend dagdepend_heft2){
		for(int i = dagqueue_heft2.size()-1;i>=0;i--)
		{
			dagqueue_heft2.get(i).setUpRankValue(dagqueue_heft2.get(i).getlength()/computerability.getAveComputeCost(i));
			double tem = 0;
			Iterator <Integer>it = dagqueue_heft2.get(i).getsuc().iterator();
			while(it.hasNext()){
				int sucCloudletIdTem = it.next();
				double valuetemp = dagqueue_heft2.get(sucCloudletIdTem).getUpRankValue()+(double) dagdepend_heft2.getDAGDependValueMap().get(String.valueOf(i)+" "+String.valueOf(sucCloudletIdTem))/computerability.getAveComputeCost(i);
				if(valuetemp>tem){
					tem = valuetemp;
				}

			}
			tem+=dagqueue_heft2.get(i).getUpRankValue();
			tem = (int)(tem*1000)/1000.0;
			dagqueue_heft2.get(i).setUpRankValue(tem);
		}
	}
	
	/**
	 * 用HEFT对每个DAG预先进行调度
	 * @param dagqueue_heft，DAG中各个子任务
     * @param dagdepend_heft，DAG中任务间依赖关系
	 * @return makespan，HEFT调度结果的makespan
	 */
	public static double assignVm_(ArrayList<DAG> dagqueue_heft3,DAGdepend dagdepend_heft3){
		
		double makespan = 0;
		/* 分配第一个任务*/
		cloudletInVm = new HashMap<Integer,Integer[]>();
		cloudletInVmId = new HashMap<Integer,Integer>();
		DecimalFormat df = new DecimalFormat("#.##");
		double temp = Integer.MAX_VALUE;
		int vmIdTem = -1;
		int[] num = new int[pe_number];
		Integer[][] cloudletinvm = new Integer[pe_number][100];//前一个代表有多少个处理器，后一个代表一个DAG中有多少个子任务
		double exetime=0;
		double[] time = new double[2];
		for(int firTem=0;firTem<pe_number;firTem++){
			if(vmComputeCostMap.get(0)[firTem] < temp){
				temp=vmComputeCostMap.get(0)[firTem];
				vmIdTem = firTem;
			}			
		}
		time[0] = PEList.get(vmIdTem).getAvail();
		time[1] = time[0]+vmComputeCostMap.get(0)[vmIdTem];
		DAGExeTimeMap.put(0, time);
		PEList.get(vmIdTem).setast(num[vmIdTem], time[0]);
		PEList.get(vmIdTem).setaft(num[vmIdTem], time[1]);
		num[vmIdTem]++;
		PEList.get(vmIdTem).setAvail(time[1]);
		cloudletinvm[vmIdTem][0] = 0;
		
		cloudletInVmId.put(0, vmIdTem);			
		exetime = DAGExeTimeMap.get(0)[1]-DAGExeTimeMap.get(0)[0];
		System.out.println("cloudlet 0"+"	ast:"+time[0]+"		aft:"+time[1]+"		processor:"+(vmIdTem+1)+"	pes:"+dagqueue_heft3.get(0).getpeid()+"	exeTime:"+exetime);
		dagqueue_heft3.get(0).setheftast(time[0]);
		dagqueue_heft3.get(0).setheftaft(time[1]);
		
		/*	分配其他的任务*/
		for(int iAssignTem = 1;iAssignTem < dagqueue_heft3.size();iAssignTem++){

			int cloudletIdCurrent = dagqueue_heft3.get(iAssignTem).getid();
			double[] timeTemp = new double[2];
			
			int vmIdTemp = -1;
			boolean success = false;
			timeTemp[1] = Integer.MAX_VALUE;
			
			/*基于插入*/
			for(int Assigntemp =0; Assigntemp<PEList.size(); Assigntemp++){
				for(int i=0 ; i <num[Assigntemp] ; i++){
					Iterator <Integer>it = DAGIdToDAGMap.get(cloudletIdCurrent).getpre().iterator();
					double temp_1 = 0;
					double sum = 0;
					int cloudletIdTemp = 0;
					while(it.hasNext()){
						/*	取出该任务前驱任务id*/
						int pretempid = it.next();					
						double pretemp;
						//System.out.println(cloudletIdCurrent+" "+pretempid+" "+cloudletInVmId.get(pretempid)+" "+Assigntemp);
						if(	cloudletInVmId.get(pretempid) == Assigntemp )
						{
							pretemp =DAGExeTimeMap.get(pretempid)[1];
						}
						else
						{
							pretemp = DAGExeTimeMap.get(pretempid)[1]+(double)dagdepend_heft3.getDAGDependValueMap().get(String.valueOf(pretempid)+" "+String.valueOf(cloudletIdCurrent));
						}
						
						if( pretemp > temp_1){
							temp_1 = pretemp;
							cloudletIdTemp = pretempid;
						}
					}
					sum += temp_1;
					sum += vmComputeCostMap.get(cloudletIdCurrent)[Assigntemp];
					if(PEList.get(Assigntemp).getast(i)!=0&&i==0)
					{
						if(temp_1>=0 &&PEList.get(Assigntemp).getast(i)>=sum)
						{
							vmIdTemp=Assigntemp;
							timeTemp[0]=temp_1;
							timeTemp[1]=sum;
							success = true;
							break;
						}
						else
						{
							continue;
						}
					}
					else if(PEList.get(Assigntemp).getast(i)==0&&i==0)
					{
						continue;
					}
					else
					{
						if(PEList.get(Assigntemp).getast(i)>=sum && (PEList.get(Assigntemp).getast(i)-PEList.get(Assigntemp).getaft(i-1))>=vmComputeCostMap.get(cloudletIdCurrent)[Assigntemp])
						{
							if(temp_1<PEList.get(Assigntemp).getaft(i-1))
							{
								timeTemp[0] = PEList.get(Assigntemp).getaft(i-1);
								timeTemp[1] = PEList.get(Assigntemp).getaft(i-1)+vmComputeCostMap.get(cloudletIdCurrent)[Assigntemp];
							}
							else
							{
								timeTemp[0]=temp_1;
								timeTemp[1]=sum;
							}
							vmIdTemp=Assigntemp;
							success = true;
							break;
						}
						else
						{
							continue;
						}
					}
				}
				if(success)
				{
					break;
				}
			}
			
			if(success)
			{
				DAGExeTimeMap.put(cloudletIdCurrent, timeTemp);
				PEList.get(vmIdTemp).setast(num[vmIdTemp], timeTemp[0]);
				PEList.get(vmIdTemp).setaft(num[vmIdTemp], timeTemp[1]);
				
				DAGIdToDAGMap.get(cloudletIdCurrent).setinserte(true);
				cloudletinvm[vmIdTemp][num[vmIdTemp]] = cloudletIdCurrent;
				int q=0;
				int n=num[vmIdTemp];
				for(int p = num[vmIdTemp]-1;p>=0;p--){
					if(DAGExeTimeMap.get(cloudletinvm[vmIdTemp][p])[0]>timeTemp[0]){
						q = cloudletinvm[vmIdTemp][p];
						cloudletinvm[vmIdTemp][p] = cloudletIdCurrent;
						cloudletinvm[vmIdTemp][n] = q;
						n=p;
					}
				}
				dagqueue_heft3.get(iAssignTem).setinserte(true);
				num[vmIdTemp]++;
				PEList.get(vmIdTemp).setAvail(timeTemp[1]);
				cloudletInVmId.put(cloudletIdCurrent, vmIdTemp);
				exetime = DAGExeTimeMap.get(cloudletIdCurrent)[1]-DAGExeTimeMap.get(cloudletIdCurrent)[0];
				System.out.println("cloudlet "+(cloudletIdCurrent)+"	ast:"+timeTemp[0]+"		aft:"+timeTemp[1]+"	processor:"+(vmIdTemp+1)+"	pes:"+dagqueue_heft3.get(iAssignTem).getpeid()+"	exeTime:"+exetime);

				dagqueue_heft3.get(iAssignTem).setheftast(timeTemp[0]);
				dagqueue_heft3.get(iAssignTem).setheftaft(timeTemp[1]);
				//System.out.println(dagqueue_heft3.get(iAssignTem).getid()+" "+dagqueue_heft3.get(iAssignTem).getheftast()+" "+dagqueue_heft3.get(iAssignTem).getheftaft());
				
				continue;
			}
			
			for(int jAssignTem = 0;jAssignTem < PEList.size();jAssignTem++)
			{
				/* 当前任务在每个处理器上暂定的est记为temEST */
				double temEST = PEList.get(jAssignTem).getAvail();
				/* 当前任务的每个前驱任务传输数据完毕时刻的最大值为tem */
				double tem = 0;
				/*	选定前驱任务id*/
				int cloudletIdTemp = 0;
				
				Iterator <Integer>it = DAGIdToDAGMap.get(cloudletIdCurrent).getpre().iterator();

				while(it.hasNext()){
					/*	取出该任务前驱任务id*/
					int preTempId = it.next();
					/* 当前任务的每个前驱任务传输数据完毕时间*/
					double preTem;

					if(	cloudletInVmId.get(preTempId) == jAssignTem )
					{
						preTem =DAGExeTimeMap.get(preTempId)[1];
					}
					else
					{
						preTem = DAGExeTimeMap.get(preTempId)[1]+(double)dagdepend_heft3.getDAGDependValueMap().get(String.valueOf(preTempId)+" "+String.valueOf(cloudletIdCurrent));
					}

					if( preTem > tem)
					{
						tem = preTem;
						cloudletIdTemp = preTempId;
					}
				}
				temEST = (temEST > tem)?temEST:tem;
				if( (temEST + vmComputeCostMap.get(cloudletIdCurrent)[jAssignTem]) < timeTemp[1])
				{
					timeTemp[0] = temEST;
					timeTemp[1] = temEST + vmComputeCostMap.get(cloudletIdCurrent)[jAssignTem];
					vmIdTemp = jAssignTem;
				}				
			}
			DAGExeTimeMap.put(cloudletIdCurrent, timeTemp);
			PEList.get(vmIdTemp).setast(num[vmIdTemp], timeTemp[0]);
			PEList.get(vmIdTemp).setaft(num[vmIdTemp], timeTemp[1]);

			cloudletinvm[vmIdTemp][num[vmIdTemp]] = cloudletIdCurrent;
			num[vmIdTemp]++;
			PEList.get(vmIdTemp).setAvail(timeTemp[1]);
			cloudletInVmId.put(cloudletIdCurrent, vmIdTemp);
			exetime = DAGExeTimeMap.get(cloudletIdCurrent)[1]-DAGExeTimeMap.get(cloudletIdCurrent)[0];
			System.out.println("cloudlet "+(cloudletIdCurrent)+"	ast:"+timeTemp[0]+"		aft:"+timeTemp[1]+"	processor:"+(vmIdTemp+1)+"	pes:"+dagqueue_heft3.get(iAssignTem).getpeid()+"	exeTime:"+exetime);

			dagqueue_heft3.get(iAssignTem).setheftast(timeTemp[0]);
			dagqueue_heft3.get(iAssignTem).setheftaft(timeTemp[1]);
			//System.out.println(dagqueue_heft3.get(iAssignTem).getid()+" "+dagqueue_heft3.get(iAssignTem).getheftast()+" "+dagqueue_heft3.get(iAssignTem).getheftaft());
			
			makespan = timeTemp[1];
		}

		for(int i=0;i<PEList.size();i++){
			cloudletInVm.put(i, cloudletinvm[i]);
		}

		return makespan;
		
	}

}
