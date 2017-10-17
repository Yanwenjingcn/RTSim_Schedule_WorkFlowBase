package org.gui;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.graphics.*;
import swing2swt.layout.BorderLayout;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.PaintEvent;

import org.generate.TaskNode;
import org.generate.BuildParameters;
import org.generate.DagBuilder;
import org.generate.Random_Dag;
import org.gui.SystemSetting;
import org.schedule.Makespan;
import org.schedule.*;

public class rtwsimframe {

	protected Shell shlElasticworkflowsim;

	protected TabFolder tabFolder;
	protected Display display;
	ToolItem tltmNewItem_1, tltmNewItem_2, tltmNewItem_3, tltmNewItem_4,
			tltmNewItem_5, tltmNewItem_6, tltmNewItem_7, tltmNewItem_8;
	protected Composite composite, compositefifo, compositeedf, compositeefff,
			compositestf, compositenewedf, compositefillback,
			compositefillbackWithoutInsert;
	ScrolledComposite scrolledComposite, scrolledCompositefifo,
			scrolledCompositeedf, scrolledCompositestf, scrolledCompositeefff,
			scrolledCompositenewedf, scrolledCompositefillback,
			scrolledCompositefillbackWithoutInsert;

	private static int[][] color = new int[500][3]; // 同一个DAG中不同TASK均为一个颜色，color[i]
	private int leftmargin = 110;
	private int maxheight = 1000;
	private int width;
	private int height;
	private int timewind;
	public static int[][] message;
	public static int[][] messageWithoutInsert;
	int dagnummax = 10000;
	int mesnum = 5;

	// private HashMap<String,ArrayList<Integer[]>> location = new
	// HashMap<String,ArrayList<Integer[]>>();
	ArrayList<Integer[]> locat = new ArrayList<Integer[]>();
	int loccount = 0;

	ArrayList<Integer[]> locatfifo = new ArrayList<Integer[]>();
	int loccountfifo = 0;

	ArrayList<Integer[]> locatedf = new ArrayList<Integer[]>();
	int loccountedf = 0;

	ArrayList<Integer[]> locatstf = new ArrayList<Integer[]>();
	int loccountstf = 0;

	ArrayList<Integer[]> locatefff = new ArrayList<Integer[]>();
	int loccountefff = 0;

	ArrayList<Integer[]> locatnewedf = new ArrayList<Integer[]>();
	int loccountnewedf = 0;

	ArrayList<Integer[]> locatfillback = new ArrayList<Integer[]>();
	int loccountfillback = 0;

	ArrayList<Integer[]> locatfillbackWithoutInsert = new ArrayList<Integer[]>();
	int loccountfillbackWithoutInsert = 0;

	DagBuilder dagbuilder = new DagBuilder();

	Thread simtd = null;

	public static void main(String[] args) {
		try {
			rtwsimframe window = new rtwsimframe();
			window.open();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void open() {
		display = Display.getDefault();
		// if(shell==null)
		createContents();
		shlElasticworkflowsim.open();
		shlElasticworkflowsim.layout();

		while (!shlElasticworkflowsim.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}

	}

	/**
	 * 创建窗口
	 */
	public void createContents() {

		shlElasticworkflowsim = new Shell();
		shlElasticworkflowsim.setSize(800, 500);
		shlElasticworkflowsim.setText("RTWSim");
		shlElasticworkflowsim.setLayout(new BorderLayout(0, 0));

		ToolBar toolBar = new ToolBar(shlElasticworkflowsim, SWT.FLAT
				| SWT.RIGHT);
		toolBar.setLayoutData(BorderLayout.NORTH);

		ToolItem tltmNewItem = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				BuildParameters();

				// 显示直线和processor名字
				M_paintControl();

				// 显示调度分布图
				displayTask();
			}
		});
		tltmNewItem.setText("BuildParameters");

		tltmNewItem_5 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_5.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				OtherAlgorithms();
			}
		});
		tltmNewItem_5.setText("OtherAlgorithms");

		tltmNewItem_1 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FIFO();
			}
		});

		tltmNewItem_1.setText("FIFO");

		tltmNewItem_2 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EDF();
			}
		});
		tltmNewItem_2.setText("EDF");

		tltmNewItem_3 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				STF();
			}
		});

		tltmNewItem_3.setText("STF");

		tltmNewItem_4 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				EFFF();
			}
		});
		tltmNewItem_4.setText("EFTF");

//		tltmNewItem_6 = new ToolItem(toolBar, SWT.NONE);
//		tltmNewItem_6.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				NewEDF();
//			}
//		});
//		tltmNewItem_6.setText("NewEDF");

		
//		tltmNewItem_7 = new ToolItem(toolBar, SWT.NONE);
//		tltmNewItem_7.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent e) {
//				fillback();
//			}
//		});
//		tltmNewItem_7.setText("FILLBACK");
//		tabFolder = new TabFolder(shlElasticworkflowsim, SWT.NONE);
//		tabFolder.setLayoutData(BorderLayout.CENTER);

		
		tltmNewItem_8 = new ToolItem(toolBar, SWT.NONE);
		tltmNewItem_8.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fillbackWithoutInsert();
			}
		});
		tltmNewItem_8.setText("Workflow-based");
		tabFolder = new TabFolder(shlElasticworkflowsim, SWT.NONE);
		tabFolder.setLayoutData(BorderLayout.CENTER);

	}

	/**
	 * 时间窗开始的竖线
	 */
	public void M_paintline(PaintEvent e) {
		Color linecolor = new Color(display, 255, 255, 255);
		e.gc.setBackground(linecolor);
		e.gc.setLineWidth(2);
		e.gc.drawLine(leftmargin, 0, leftmargin, maxheight);

	}

	/**
	 * 时间窗结束的竖线
	 */
	public void M_paintline_end(PaintEvent e) {
		int lengthtimes = 1;
		if (timewind < 800) {
			lengthtimes = (int) 800 / timewind;
		}

		Color linecolor = new Color(display, 255, 255, 255);
		e.gc.setBackground(linecolor);
		e.gc.setLineWidth(2);
		e.gc.drawLine(leftmargin + 5 + 2 + timewind * lengthtimes, 0,
				leftmargin + 5 + timewind * lengthtimes, maxheight);

	}

	/**
	 * 每个processor名称
	 */
	public void M_paintControl() {

		Button btnSave = new Button(composite, SWT.NONE);
		btnSave.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				SaveImage();
			}
		});
		btnSave.setText("Save");
		btnSave.setBounds(20, 10, 65, 20);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			Label lblprocessor = new Label(composite, SWT.NONE);
			lblprocessor.setText("Processor" + i);
			lblprocessor.setBounds(20, 50 * i - 30 + 30, 75, 50);
		}

	}

	/**
	 * 画出RTWSim生成的调度图
	 */
	public void displayconsole() {
		TabItem tbtmConsole = new TabItem(tabFolder, SWT.NONE);
		tbtmConsole.setText("Console");

		scrolledComposite = new ScrolledComposite(tabFolder, SWT.BORDER
				| SWT.H_SCROLL | SWT.V_SCROLL);
		tbtmConsole.setControl(scrolledComposite);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);
		composite = new Composite(scrolledComposite, SWT.NONE);
		composite.addPaintListener(new PaintListener() {
			public void paintControl(PaintEvent e) {
				M_paintline(e);
				M_paintline_end(e);
			}
		});
		scrolledComposite.setContent(composite);
		scrolledComposite.setMinSize(width, height);
		composite.layout();
	}

	/**
	 * 画出FIFO,STF,EFTF,EDF的调度结果图
	 */
	public void DisplayNewResult(final String itemname) {
		try {
			Display display = Display.getDefault();
			display.syncExec(new Runnable() {
				public void run() {

					int lengthtimes = 1;
					if (timewind < 800) {
						lengthtimes = (int) 800 / timewind;
					}

					if (itemname.equals("FIFO")) {
						TabItem tbtmfifo = new TabItem(tabFolder, SWT.NONE);
						tbtmfifo.setText(itemname);

						scrolledCompositefifo = new ScrolledComposite(
								tabFolder, SWT.BORDER | SWT.H_SCROLL
										| SWT.V_SCROLL);
						tbtmfifo.setControl(scrolledCompositefifo);
						scrolledCompositefifo.setExpandHorizontal(true);
						scrolledCompositefifo.setExpandVertical(true);
						compositefifo = new Composite(scrolledCompositefifo,
								SWT.NONE);
						compositefifo.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositefifo.setContent(compositefifo);
						scrolledCompositefifo.setMinSize(width, height);
						compositefifo.layout();

						Button btnSave = new Button(compositefifo, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								SaveImagefifo();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositefifo,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}

					} else if (itemname.equals("EDF")) {
						TabItem tbtmedf = new TabItem(tabFolder, SWT.NONE);
						tbtmedf.setText(itemname);

						scrolledCompositeedf = new ScrolledComposite(tabFolder,
								SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
						tbtmedf.setControl(scrolledCompositeedf);
						scrolledCompositeedf.setExpandHorizontal(true);
						scrolledCompositeedf.setExpandVertical(true);
						compositeedf = new Composite(scrolledCompositeedf,
								SWT.NONE);
						compositeedf.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositeedf.setContent(compositeedf);
						scrolledCompositeedf.setMinSize(width, height);
						compositeedf.layout();

						Button btnSave = new Button(compositeedf, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								SaveImageedf();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositeedf,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}
					} else if (itemname.equals("STF")) {
						TabItem tbtmstf = new TabItem(tabFolder, SWT.NONE);
						tbtmstf.setText(itemname);

						scrolledCompositestf = new ScrolledComposite(tabFolder,
								SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
						tbtmstf.setControl(scrolledCompositestf);
						scrolledCompositestf.setExpandHorizontal(true);
						scrolledCompositestf.setExpandVertical(true);
						compositestf = new Composite(scrolledCompositestf,
								SWT.NONE);
						compositestf.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositestf.setContent(compositestf);
						scrolledCompositestf.setMinSize(width, height);
						compositestf.layout();

						Button btnSave = new Button(compositestf, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								SaveImagestf();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositestf,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}
					} else if (itemname.equals("EFFF")) {
						TabItem tbtmefff = new TabItem(tabFolder, SWT.NONE);
						tbtmefff.setText("EFTF");

						scrolledCompositeefff = new ScrolledComposite(
								tabFolder, SWT.BORDER | SWT.H_SCROLL
										| SWT.V_SCROLL);
						tbtmefff.setControl(scrolledCompositeefff);
						scrolledCompositeefff.setExpandHorizontal(true);
						scrolledCompositeefff.setExpandVertical(true);
						compositeefff = new Composite(scrolledCompositeefff,
								SWT.NONE);
						compositeefff.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositeefff.setContent(compositeefff);
						scrolledCompositeefff.setMinSize(width, height);
						compositeefff.layout();

						Button btnSave = new Button(compositeefff, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								SaveImageefff();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositeefff,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}

					} else if (itemname.equals("NewEDF")) {
						TabItem tbtmnewedf = new TabItem(tabFolder, SWT.NONE);
						tbtmnewedf.setText(itemname);

						scrolledCompositenewedf = new ScrolledComposite(
								tabFolder, SWT.BORDER | SWT.H_SCROLL
										| SWT.V_SCROLL);
						tbtmnewedf.setControl(scrolledCompositenewedf);
						scrolledCompositenewedf.setExpandHorizontal(true);
						scrolledCompositenewedf.setExpandVertical(true);
						compositenewedf = new Composite(
								scrolledCompositenewedf, SWT.NONE);
						compositenewedf.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositenewedf.setContent(compositenewedf);
						scrolledCompositenewedf.setMinSize(width, height);
						compositenewedf.layout();

						Button btnSave = new Button(compositenewedf, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								SaveImagenewedf();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositenewedf,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}

					}
					
					if (itemname.equals("FillbackFIFO")) {
						TabItem tbtmfillback = new TabItem(tabFolder, SWT.NONE);
						tbtmfillback.setText(itemname);

						scrolledCompositefillback = new ScrolledComposite(
								tabFolder, SWT.BORDER | SWT.H_SCROLL
										| SWT.V_SCROLL);
						tbtmfillback.setControl(scrolledCompositefillback);
						scrolledCompositefillback.setExpandHorizontal(true);
						scrolledCompositefillback.setExpandVertical(true);
						compositefillback = new Composite(
								scrolledCompositefillback, SWT.NONE);
						compositefillback.addPaintListener(new PaintListener() {
							public void paintControl(PaintEvent e) {
								M_paintline(e);
								M_paintline_end(e);
							}
						});
						scrolledCompositefillback.setContent(compositefillback);
						scrolledCompositefillback.setMinSize(width, height);
						compositefillback.layout();

						Button btnSave = new Button(compositefillback, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								// SaveImagefillback();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(compositefillback,
									SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}

					}
					if (itemname.equals("Workflow-based")) {
						TabItem tbtmfillback = new TabItem(tabFolder, SWT.NONE);
						tbtmfillback.setText(itemname);

						scrolledCompositefillbackWithoutInsert = new ScrolledComposite(
								tabFolder, SWT.BORDER | SWT.H_SCROLL
										| SWT.V_SCROLL);
						tbtmfillback
								.setControl(scrolledCompositefillbackWithoutInsert);
						scrolledCompositefillbackWithoutInsert
								.setExpandHorizontal(true);
						scrolledCompositefillbackWithoutInsert
								.setExpandVertical(true);
						compositefillbackWithoutInsert = new Composite(
								scrolledCompositefillbackWithoutInsert,
								SWT.NONE);
						compositefillbackWithoutInsert
								.addPaintListener(new PaintListener() {
									public void paintControl(PaintEvent e) {
										M_paintline(e);
										M_paintline_end(e);
									}
								});
						scrolledCompositefillbackWithoutInsert
								.setContent(compositefillbackWithoutInsert);
						scrolledCompositefillbackWithoutInsert.setMinSize(
								width, height);
						compositefillbackWithoutInsert.layout();

						Button btnSave = new Button(
								compositefillbackWithoutInsert, SWT.NONE);
						btnSave.addSelectionListener(new SelectionAdapter() {
							@Override
							public void widgetSelected(SelectionEvent e) {
								// SaveImagefillback();
							}
						});
						btnSave.setText("Save");
						btnSave.setBounds(20, 10, 65, 20);

						for (int i = 1; i <= BuildParameters.processorNumber; i++) {
							Label lblprocessor = new Label(
									compositefillbackWithoutInsert, SWT.NONE);
							lblprocessor.setText("Processor" + i);
							lblprocessor
									.setBounds(20, 50 * i - 30 + 30, 75, 50);
						}

					}

				}
			});

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 调用parametersetting中窗口，获取用户输入的实验参数
	 */
	public void BuildParameters() {
		parametersetting pasetdialog = new parametersetting(new Shell(),
				SWT.TITLE);

		if (pasetdialog.open() != SWT.OK)
			return;
		SystemSetting.setMainframe(this);

		BuildParameters.timeWindow = pasetdialog.timeWindow;
		BuildParameters.taskAverageLength = pasetdialog.taskAverageLength;
		BuildParameters.dagAverageSize = pasetdialog.dagAverageSize;
		BuildParameters.dagLevelFlag = pasetdialog.dagLevelFlag;
		BuildParameters.deadLineTimes = pasetdialog.deadLineTimes;
		BuildParameters.processorNumber = pasetdialog.processorNumber;

		dagbuilder.BuildDAG();
		width = (int) (BuildParameters.timeWindow / BuildParameters.processorNumber)
				+ leftmargin + 20;
		height = BuildParameters.processorNumber * 50 + 100;
		timewind = (int) (BuildParameters.timeWindow / BuildParameters.processorNumber);

		randomcolor();

		displayconsole();

	}

	/**
	 * 为每一个DAG生成一种颜色
	 */
	public void randomcolor() {
		Random random = new Random();
		int max = 230;
		int min = 30;
		for (int i = 0; i < DagBuilder.finishDagList.size(); i++) {
			color[i][0] = random.nextInt(max) % (max - min + 1) + min;
			color[i][1] = random.nextInt(max) % (max - min + 1) + min;
			color[i][2] = random.nextInt(max) % (max - min + 1) + min;
		}
	}

	/**
	 * 调用Makespan和fillbacknew，分别进行6种调度算法
	 */
	public void OtherAlgorithms() {

		Makespan ms = new Makespan();
		/**
		 * 
		 */
	//	fillbacknew fb = new fillbacknew();
		FillbacknewWithoutInsert fi = new FillbacknewWithoutInsert();
		/**
		 * 
		 */
		
		message = new int[dagnummax][mesnum];
		messageWithoutInsert=new int[dagnummax][mesnum];

		try {
			ms.runMakespan_xml();
			//fb.runMakespan(
//					"D:\\MyEclipse\\ProjectSelf\\schedule_show\\DAG_XML\\",
//					"E:\\DagCasesResult\\");
			
			/**
			 * 这里是需要人工修改的
			 */
			fi.runMakespan(
					"D:\\MyEclipse\\ProjectSelf\\RTSim_Schedule_Show\\DAG_XML\\",
					"E:\\DagCasesResult\\");
			
			
			/**
			 * 
			 */
//			for (int i = 0; i < FIFO.tasknum; i++)
//
//			{
//				message[i][0] = fb.message[i][0];// DAGid
//				message[i][1] = fb.message[i][1];// TASKid
//				message[i][2] = fb.message[i][2];// PEid
//				message[i][3] = fb.message[i][3];// starttime
//				message[i][4] = fb.message[i][4];// finishtime
//			}

			for (int i = 0; i < FIFO.tasknum; i++) {
				messageWithoutInsert[i][0] = fi.message[i][0];// DAGid
				messageWithoutInsert[i][1] = fi.message[i][1];// TASKid
				messageWithoutInsert[i][2] = fi.message[i][2];// PEid
				messageWithoutInsert[i][3] = fi.message[i][3];// starttime
				messageWithoutInsert[i][4] = fi.message[i][4];// finishtime
			}

			MessageBox box = new MessageBox(shlElasticworkflowsim);
			box.setMessage("Complete!");
			box.open();

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印fifo算法调度结果
	 */
	public void FIFO() {
		try {

			DisplayNewResult("FIFO");
			displayOtherAlogrithms("FIFO");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印edf算法调度结果
	 */
	public void EDF() {
		try {

			DisplayNewResult("EDF");
			displayOtherAlogrithms("EDF");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印stf算法调度结果
	 */
	public void STF() {
		try {

			DisplayNewResult("STF");
			displayOtherAlogrithms("STF");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印eftf算法调度结果
	 */
	public void EFFF() {
		try {

			DisplayNewResult("EFFF");
			displayOtherAlogrithms("EFFF");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印newedf算法调度结果
	 */
	public void NewEDF() {
		try {

			DisplayNewResult("NewEDF");
			displayOtherAlogrithms("NewEDF");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 打印fillback算法调度结果
	 */
	public void fillback() {
		try {

			DisplayNewResult("FillbackFIFO");
			displayOtherAlogrithms("FillbackFIFO");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void fillbackWithoutInsert() {
		try {

			DisplayNewResult("Workflow-based");
			displayOtherAlogrithms("Workflow-based");

		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void GoClick(SelectionEvent e) {
		try {
			synchronized (this) {
				this.notify();
				// tltmNewItem_1.setEnabled(false);
			}
		} catch (Exception ex) {
			// TODO: handle exception
			ex.printStackTrace();
		}
	}

	public int[] getcolor(int dagcount) {
		return color[dagcount];
	}

	public void displayTask() {
		int lengthtimes = 1;
		if (timewind < 800) {
			lengthtimes = (int) 800 / timewind;
		}

		Label lblrate = new Label(composite, SWT.NONE);
		lblrate.setText("    UR : 100%    SR : 100%");
		lblrate.setBounds(leftmargin + 5, 10, 400, 20);

		Color timewindow = new Color(display, 230, 230, 230);
		for (int i = 0; i < BuildParameters.processorNumber; i++) {
			Label lblproce = new Label(composite, SWT.NONE);
			lblproce.setBackground(timewindow);
			lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
					* lengthtimes, 10);
		}

		for (Random_Dag dag : DagBuilder.finishDagList) {
			int[] color = new int[3];
			String[] number = dag.dagId.split("dag");

			color = getcolor(Integer.valueOf(number[1]).intValue());
			Color dagcolor = new Color(display, color[0], color[1], color[2]);

			List<String> nodeIdList = new ArrayList<String>();
			for (TaskNode node : dag.taskList) {
				nodeIdList.add(node.nodeId);
			}

			for (TaskNode node : dag.taskList) {
				if (node.getProcessorId() == 0)
					continue;
				for (int j = 1; j <= BuildParameters.processorNumber; j++) {
					if (j == node.getProcessorId()) {
						Label lbltask = new Label(composite, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText(dag.dagId + ":task"
								+ nodeIdList.indexOf(node.nodeId));
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + node.startTime
								* lengthtimes), 50 * (j - 1) + 15 + 30,
								(node.endTime - node.startTime) * lengthtimes,
								20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + node.startTime;
						loc[4] = 50 * (j - 1) + 15 + 30;
						loc[5] = node.endTime - node.startTime;
						loc[6] = 20;
						locat.add(loc);
						loccount++;

						break;

					}

				}

			}
		}

	}

	public void displayOtherAlogrithms(String alogrithm) {
		int lengthtimes = 1;
		if (timewind < 800) {
			lengthtimes = (int) 800 / timewind;
		}

		if (alogrithm.equals("FIFO")) {
			if (BuildParameters.timeWindow > 100000)
				composite.dispose();

			Label lblrate = new Label(compositefifo, SWT.NONE);
			lblrate.setText("    UR : " + Makespan.rate[0][0]
					+ "    EUR : " + Makespan.rate[0][2]
					+ "    SR : " + Makespan.rate[0][1]);
			lblrate.setBounds(leftmargin + 5, 10, 700, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositefifo, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
						* lengthtimes, 10);
			}

			int dagcount = 0;

			for (int k = 0; k < FIFO.tasknum; k++) {

				int[] color = new int[3];
				color = getcolor(FIFO.message[k][0]);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == FIFO.message[k][2]) {//message[k][2] 处理器id
						Label lbltask = new Label(compositefifo, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + FIFO.message[k][0] + ":task"
								+ FIFO.message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + FIFO.message[k][3]
								* lengthtimes), 50 * j + 15 + 30,
								(FIFO.message[k][4] - FIFO.message[k][3])
										* lengthtimes, 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + FIFO.message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = FIFO.message[k][4] - FIFO.message[k][3];
						loc[6] = 20;
						locatfifo.add(loc);
						loccountfifo++;

						break;

					}

				}

			}
		} else if (alogrithm.equals("EDF")) {
			if (BuildParameters.timeWindow > 100000)
				compositefifo.dispose();

			Label lblrate = new Label(compositeedf, SWT.NONE);
			lblrate.setText("    UR : " + Makespan.rate[1][0]
					+ "    EUR : " + Makespan.rate[1][2]
					+ "    SR : " + Makespan.rate[1][1]);
			lblrate.setBounds(leftmargin + 5, 10, 700, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositeedf, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
						* lengthtimes, 10);
			}

			int dagcount = 0;

			for (int k = 0; k < EDF.tasknum; k++) {
				int[] color = new int[3];
				color = getcolor(EDF.message[k][0]);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == EDF.message[k][2]) {
						Label lbltask = new Label(compositeedf, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + EDF.message[k][0] + ":task"
								+ EDF.message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + EDF.message[k][3]
								* lengthtimes), 50 * j + 15 + 30,
								(EDF.message[k][4] - EDF.message[k][3])
										* lengthtimes, 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + EDF.message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = EDF.message[k][4] - EDF.message[k][3];
						loc[6] = 20;
						locatedf.add(loc);
						loccountedf++;

						break;

					}

				}

			}
		} else if (alogrithm.equals("STF")) {
			if (BuildParameters.timeWindow > 100000)
				compositeedf.dispose();

			Label lblrate = new Label(compositestf, SWT.NONE);
			lblrate.setText("    UR : " + Makespan.rate[2][0]
					+ "    EUR : " + Makespan.rate[2][2]
					+ "    SR : " + Makespan.rate[2][1]);
			lblrate.setBounds(leftmargin + 5, 10, 700, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositestf, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
						* lengthtimes, 10);
			}

			int dagcount = 0;

			for (int k = 0; k < STF.tasknum; k++) {
				int[] color = new int[3];
				color = getcolor(STF.message[k][0]);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == STF.message[k][2]) {
						Label lbltask = new Label(compositestf, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + STF.message[k][0] + ":task"
								+ STF.message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + STF.message[k][3]
								* lengthtimes), 50 * j + 15 + 30,
								(STF.message[k][4] - STF.message[k][3])
										* lengthtimes, 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + STF.message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = STF.message[k][4] - STF.message[k][3];
						loc[6] = 20;
						locatstf.add(loc);
						loccountstf++;

						break;

					}

				}

			}
		} else if (alogrithm.equals("EFFF")) {
			if (BuildParameters.timeWindow > 100000)
				compositestf.dispose();

			Label lblrate = new Label(compositeefff, SWT.NONE);
			lblrate.setText("    UR : " + Makespan.rate[3][0]
					+ "    EUR : " + Makespan.rate[3][2]
					+ "    SR : " + Makespan.rate[3][1]);
			lblrate.setBounds(leftmargin + 5, 10, 700, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositeefff, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
						* lengthtimes, 10);
			}

			int dagcount = 0;

			for (int k = 0; k < EFFF.tasknum; k++) {
				int[] color = new int[3];
				color = getcolor(EFFF.message[k][0]);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == EFFF.message[k][2]) {
						Label lbltask = new Label(compositeefff, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + EFFF.message[k][0] + ":task"
								+ EFFF.message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + EFFF.message[k][3]
								* lengthtimes), 50 * j + 15 + 30,
								(EFFF.message[k][4] - EFFF.message[k][3])
										* lengthtimes, 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + EFFF.message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = EFFF.message[k][4] - EFFF.message[k][3];
						loc[6] = 20;
						locatefff.add(loc);
						loccountefff++;

						break;

					}

				}

			}
		} else if (alogrithm.equals("NewEDF")) {
			if (BuildParameters.timeWindow > 100000)
				compositeefff.dispose();

			Label lblrate = new Label(compositenewedf, SWT.NONE);
			lblrate.setText("    UR : " + Makespan.rate[4][0]
					+ "    EUR : " + Makespan.rate[4][2]
					+ "    SR : " + Makespan.rate[4][1]);
			lblrate.setBounds(leftmargin + 5, 10, 700, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositenewedf, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind
						* lengthtimes, 10);
			}

			int dagcount = 0;

			for (int k = 0; k < NewEDF.tasknum; k++) {
				int[] color = new int[3];
				color = getcolor(NewEDF.message[k][0]);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == NewEDF.message[k][2]) {
						Label lbltask = new Label(compositenewedf, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + NewEDF.message[k][0] + ":task"
								+ NewEDF.message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds(
								(leftmargin + 5 + NewEDF.message[k][3]
										* lengthtimes), 50 * j + 15 + 30,
								(NewEDF.message[k][4] - NewEDF.message[k][3])
										* lengthtimes, 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + NewEDF.message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = NewEDF.message[k][4] - NewEDF.message[k][3];
						loc[6] = 20;
						locatnewedf.add(loc);
						loccountedf++;

						break;

					}

				}

			}
		}
		
		if (alogrithm.equals("FillbackFIFO")) {
			if (BuildParameters.timeWindow > 300000)
				compositefifo.dispose();

			Label lblrate = new Label(compositefillback, SWT.NONE);
			// lblrate.setText("   PE's use ratio is "+Makespan.rate[0][0]+"     Task Completion Rates is "+Makespan.rate[0][1]);
			lblrate.setBounds(leftmargin + 5, 10, 400, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositefillback, SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind,
						10);
			}

			int dagcount = 0;

			for (int k = 0; k < FIFO.tasknum; k++) {

				int[] color = new int[3];
				color = getcolor(message[k][0] + 1);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == message[k][2]) {
						Label lbltask = new Label(compositefillback, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + message[k][0] + ":task"
								+ message[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + message[k][3]),
								50 * j + 15 + 30,
								(message[k][4] - message[k][3]), 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + message[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = message[k][4] - message[k][3];
						loc[6] = 20;
						locatfillback.add(loc);
						loccountfillback++;

						break;

					}

				}

			}
		}

		if (alogrithm.equals("Workflow-based")) {
			if (BuildParameters.timeWindow > 100000)
				compositefifo.dispose();

			Label lblrate = new Label(compositefillbackWithoutInsert, SWT.NONE);
			
			lblrate.setText("    UR : " + FillbacknewWithoutInsert.rateResult[0][0]
					+ "    EUR : " +  FillbacknewWithoutInsert.rateResult[0][1]
					+ "    SR : " +  FillbacknewWithoutInsert.rateResult[0][2]);
			
			lblrate.setBounds(leftmargin + 5, 10, 400, 20);

			Color timewindow = new Color(display, 230, 230, 230);
			for (int i = 0; i < BuildParameters.processorNumber; i++) {
				Label lblproce = new Label(compositefillbackWithoutInsert,SWT.NONE);
				lblproce.setBackground(timewindow);
				lblproce.setBounds(leftmargin + 5, 50 * i + 35 + 30, timewind,
						10);
			}

			int dagcount = 0;

			for (int k = 0; k < FIFO.tasknum; k++) {

				int[] color = new int[3];
				color = getcolor(messageWithoutInsert[k][0] + 1);
				Color dagcolor = new Color(display, color[0], color[1],
						color[2]);

				for (int j = 0; j < BuildParameters.processorNumber; j++) {
					if (j == messageWithoutInsert[k][2]) {
						Label lbltask = new Label(
								compositefillbackWithoutInsert, SWT.BORDER);
						lbltask.setBackground(dagcolor);
						lbltask.setText("dag" + messageWithoutInsert[k][0] + ":task"
								+ messageWithoutInsert[k][1]);
						lbltask.setAlignment(1);
						lbltask.setBounds((leftmargin + 5 + messageWithoutInsert[k][3]),
								50 * j + 15 + 30,
								(messageWithoutInsert[k][4] - messageWithoutInsert[k][3]), 20);

						Integer[] loc = new Integer[7];
						loc[0] = color[0];
						loc[1] = color[1];
						loc[2] = color[2];
						loc[3] = leftmargin + 5 + messageWithoutInsert[k][3];
						loc[4] = 50 * j + 15 + 30;
						loc[5] = messageWithoutInsert[k][4] - messageWithoutInsert[k][3];
						loc[6] = 20;
						locatfillbackWithoutInsert.add(loc);
						loccountfillbackWithoutInsert++;

						break;

					}

				}

			}
		}

	}

	private void SaveImage() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(composite.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImage(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImage(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccount; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locat.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

	private void SaveImagefifo() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(compositefifo.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImagefifo(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImagefifo(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccountfifo; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locatfifo.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

	private void SaveImageedf() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(compositeedf.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImageedf(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImageedf(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccountedf; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locatedf.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

	private void SaveImagestf() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(compositestf.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImagestf(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImagestf(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccountstf; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locatstf.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

	private void SaveImageefff() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(compositeefff.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImageefff(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImageefff(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccountefff; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locatefff.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

	private void SaveImagenewedf() {
		FileDialog dlg = new FileDialog(shlElasticworkflowsim, SWT.SAVE);
		dlg.setFilterExtensions(new String[] { "*.jpg" });
		dlg.open();
		String path = dlg.getFilterPath() + "\\" + dlg.getFileName();

		Image image = new Image(compositenewedf.getDisplay(), width, height);
		GC gc = new GC(image);// 构造 GC
		ShowImagenewedf(gc);

		ImageData imageData = image.getImageData();
		ImageLoader imageLoader = new ImageLoader();
		imageLoader.data = new ImageData[] { imageData };
		imageLoader.save(path, SWT.BITMAP);// 以 PNG 的方式保存为图片

		image.dispose();
		gc.dispose();
		MessageBox box = new MessageBox(shlElasticworkflowsim);
		box.setMessage("Save successful!");
		box.open();

	}

	public void ShowImagenewedf(GC gc) {
		Color linecolor = new Color(display, 255, 255, 255);
		gc.setBackground(linecolor);
		gc.setLineWidth(2);
		gc.drawLine(leftmargin, 0, leftmargin, maxheight);

		gc.setLineWidth(2);
		gc.drawLine(leftmargin + 5 + 2 + timewind, 0,
				leftmargin + 5 + timewind, maxheight);

		for (int i = 1; i <= BuildParameters.processorNumber; i++) {
			gc.setBackground(new Color(display, 255, 255, 255));
			gc.drawText("Processor" + i, 20, 50 * i - 30 + 30);

			gc.setBackground(new Color(display, 230, 230, 230));
			gc.drawRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);
			gc.fillRectangle(leftmargin + 5, 50 * (i - 1) + 35 + 30, timewind,
					10);

		}

		for (int i = 0; i < loccountnewedf; i++) {
			Integer[] temp = new Integer[7];
			;
			temp = locatnewedf.get(i);

			Color color = new Color(display, temp[0], temp[1], temp[2]);
			gc.setBackground(color);

			gc.drawRectangle(temp[3], temp[4], temp[5], temp[6]);
			gc.fillRectangle(temp[3], temp[4], temp[5], temp[6]);

		}

	}

}
