package org.schedule;

import java.io.FileWriter;
import java.io.IOException;

public class PrintResult {

	static String fifoResult = "fifo.txt";
	static String edfResult = "edf.txt";
	static String stfResult = "stf.txt";
	static String etfResult = "etf.txt";
	static String lrebResult = "lreb.txt";
	static String mergeDagResult = "mergeDag.txt";
	
	
	
	public static void printMergeDagToTxt(String[][] rate,String resultPath) {
		FileWriter mergeDagWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String mergeDagFileName = resultPath+ mergeDagResult;
			mergeDagWriter = new FileWriter(mergeDagFileName, true);
			mergeDagWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (mergeDagWriter != null) {
					mergeDagWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


	static String lrebWithoutInsertOneResult = "lrebWithoutInsertOne.txt";
	static String lrebWithoutInsertTwoResult = "lrebWithoutInsertTwo.txt";
	static String lrebWithoutInsertThreeResult = "lrebWithoutInsertThree.txt";
	
	
	static String OrderAsBtoS="OrderAsBtoS.txt";
	
	
	
	
	
static String OrderAsBtoSWithAdaptation="OrderAsBtoSWithAdaptation.txt";
	
	public static void printOrderAsBtoSWithAdaptationToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsBtoSWithAdaptationWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsBtoSWithAdaptationFileName = resultPath+ OrderAsBtoSWithAdaptation;
			OrderAsBtoSWithAdaptationWriter = new FileWriter(OrderAsBtoSWithAdaptationFileName, true);
			OrderAsBtoSWithAdaptationWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsBtoSWithAdaptationWriter != null) {
					OrderAsBtoSWithAdaptationWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
static String OrderAsStoBWithAdaptation="OrderAsStoBWithAdaptation.txt";
	
	public static void printOrderAsStoBWithAdaptationToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsStoBWithAdaptationWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsStoBWithAdaptationFileName = resultPath+ OrderAsStoBWithAdaptation;
			OrderAsStoBWithAdaptationWriter = new FileWriter(OrderAsStoBWithAdaptationFileName, true);
			OrderAsStoBWithAdaptationWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsStoBWithAdaptationWriter != null) {
					OrderAsStoBWithAdaptationWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	

	//��ӡ��ǰ������㷨
	
	public static void printOrderAsBtoSToTxt(String[][] rate,String resultPath) {

		FileWriter OrderAsBtoSWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsBtoSFileName = resultPath+ OrderAsBtoS;
			OrderAsBtoSWriter = new FileWriter(OrderAsBtoSFileName, true);
			OrderAsBtoSWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsBtoSWriter != null) {
					OrderAsBtoSWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	static String OrderAsHEFT="OrderAsHEFT.txt";
	public static void printOrderAsHEFTToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsHEFTWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsHEFTFileName = resultPath+ OrderAsHEFT;
			OrderAsHEFTWriter = new FileWriter(OrderAsHEFTFileName, true);
			OrderAsHEFTWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsHEFTWriter != null) {
					OrderAsHEFTWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	static String OrderAsIDWithAdaptation="OrderAsIDWithAdaptation.txt";
	
	public static void printOrderAsIDWithAdaptationToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsIDWithAdaptationWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsIDWithAdaptationFileName = resultPath+ OrderAsIDWithAdaptation;
			OrderAsIDWithAdaptationWriter = new FileWriter(OrderAsIDWithAdaptationFileName, true);
			OrderAsIDWithAdaptationWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsIDWithAdaptationWriter != null) {
					OrderAsIDWithAdaptationWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	static String OrderAsOriId="OrderAsOriId.txt";
	public static void printOrderAsOriIdToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsOriIdWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsOriIdFileName = resultPath+ OrderAsOriId;
			OrderAsOriIdWriter = new FileWriter(OrderAsOriIdFileName, true);
			OrderAsOriIdWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsOriIdWriter != null) {
					OrderAsOriIdWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	static String OrderAsStoB="OrderAsStoB.txt";
	public static void printOrderAsStoBToTxt(String[][] rate,String resultPath) {
		FileWriter OrderAsStoBWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String OrderAsStoBFileName = resultPath+ OrderAsStoB;
			OrderAsStoBWriter = new FileWriter(OrderAsStoBFileName, true);
			OrderAsStoBWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (OrderAsStoBWriter != null) {
					OrderAsStoBWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//

	/**
	 * 
	* @Title: printToTxt
	* @Description: �򵥵ļ������ȷ�ʽ
	* @param rate
	* @param resultPath:
	* @throws
	 */
	public static void printToTxt(String[][] rate, String resultPath) {

		FileWriter fifoResultWriter = null;
		FileWriter edfResultWriter = null;
		FileWriter stfResultWriter = null;
		FileWriter etfResultWriter = null;

		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String fifoResultFileName = resultPath + fifoResult;
			fifoResultWriter = new FileWriter(fifoResultFileName, true);
			fifoResultWriter.write(rate[0][0] + "\t" + rate[0][1] + "\t"
					+ rate[0][2] + "\t" + rate[0][3] + "\n");

			String edfResultFileName = resultPath + edfResult;
			edfResultWriter = new FileWriter(edfResultFileName, true);
			edfResultWriter.write(rate[1][0] + "\t" + rate[1][1] + "\t"
					+ rate[1][2] + "\t" + rate[1][3] + "\n");

			String stfResultFileName = resultPath + stfResult;
			stfResultWriter = new FileWriter(stfResultFileName, true);
			stfResultWriter.write(rate[2][0] + "\t" + rate[2][1] + "\t"
					+ rate[2][2] + "\t" + rate[2][3] + "\n");

			String etfResultFileName = resultPath + etfResult;
			etfResultWriter = new FileWriter(etfResultFileName, true);
			etfResultWriter.write(rate[3][0] + "\t" + rate[3][1] + "\t"
					+ rate[3][2] + "\t" + rate[3][3] + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fifoResultWriter != null) {
					fifoResultWriter.close();
				}
				if (edfResultWriter != null) {
					edfResultWriter.close();
				}
				if (stfResultWriter != null) {
					stfResultWriter.close();
				}
				if (etfResultWriter != null) {
					etfResultWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void printLREBToTxt(String[][] rate, String resultPath) {

		FileWriter lrebResultWriter = null;

		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String lrebResultFileName = resultPath + lrebResult;
			lrebResultWriter = new FileWriter(lrebResultFileName, true);
			lrebResultWriter.write(rate[0][0] + "\t" + rate[0][1] + "\t"
					+ rate[0][2] + "\t" + rate[0][3] + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (lrebResultWriter != null) {
					lrebResultWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printLREBWithoutInsertOneToTxt(String[][] rate,
			String resultPath) {

		FileWriter lrebWithoutInsertOneWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String lrebWithoutInsertResultFileName = resultPath
					+ lrebWithoutInsertOneResult;
			lrebWithoutInsertOneWriter = new FileWriter(
					lrebWithoutInsertResultFileName, true);
			lrebWithoutInsertOneWriter.write(rate[0][0] + "\t" + rate[0][1]
					+ "\t" + rate[0][2] + "\t" + rate[0][3] + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (lrebWithoutInsertOneWriter != null) {
					lrebWithoutInsertOneWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printLREBWithoutInsertTwoToTxt(String[][] rate,
			String resultPath) {

		FileWriter lrebWithoutInsertTwoWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String lrebWithoutInsertResultFileName = resultPath
					+ lrebWithoutInsertTwoResult;
			lrebWithoutInsertTwoWriter = new FileWriter(
					lrebWithoutInsertResultFileName, true);
			lrebWithoutInsertTwoWriter.write(rate[0][0] + "\t" + rate[0][1]
					+ "\t" + rate[0][2] + "\t" + rate[0][3] + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (lrebWithoutInsertTwoWriter != null) {
					lrebWithoutInsertTwoWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	
	public static void printLREBWithoutInsertThreeToTxt(String[][] rate,
			String resultPath) {

		FileWriter lrebWithoutInsertThreeWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String lrebWithoutInsertResultFileName = resultPath
					+ lrebWithoutInsertThreeResult;
			lrebWithoutInsertThreeWriter = new FileWriter(
					lrebWithoutInsertResultFileName, true);
			lrebWithoutInsertThreeWriter.write(rate[0][0] + "\t" + rate[0][1]
					+ "\t" + rate[0][2] + "\t" + rate[0][3] + "\n");

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (lrebWithoutInsertThreeWriter != null) {
					lrebWithoutInsertThreeWriter.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}











	static String fillbackResult = "fillback.txt";
	public static void printFillBackToTxt(String[][] rate, String resultPath, int trySlid, int pushSuccessCount) {
		FileWriter FillBackWriter = null;
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			String fillBackFileName = resultPath+ fillbackResult;
			FillBackWriter = new FileWriter(fillBackFileName, true);
			FillBackWriter.write(rate[0][0] + "\t" + rate[0][1]+ "\t" + rate[0][2] +  "\t" +rate[0][3] +  "\t" +trySlid+  "\t" + pushSuccessCount+"\n");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (FillBackWriter != null) {
					FillBackWriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
}
