package org.generate;

/**
 * 
* @ClassName: BuildParameters 
* @Description: ����DAGͼ���ɵ�Ĭ�ϲ���
* @author YWJ
* @date 2017-9-9 ����3:23:04
 */
public class BuildParameters {
      public static int timeWindow=40000;//ʱ�䴰 Ĭ��200000
      public static int taskAverageLength=40;//�����ƽ�����ȣ�20,30,40,50 Ĭ��ֵ30��
      public static int dagAverageSize=40;//dag��ƽ����С��20��30��40��50 Ĭ��ֵ30��
      public static int dagLevelFlag=2;//(1,2,3)����([3,sqrt(N-2)],[sqrt(N-2),sqrt(N-2)+4],[sqrt(N-2),N-2])
      public static double deadLineTimes=1.1;//deadline�ı���ֵ ��1.1��1.3��1.6��2.0��
      public static int processorNumber=8;//����Ԫ�ĸ�����2,4,8,16,32��
      //public int proceesorEndTime = timeWindow/processorNumber;
      
}
