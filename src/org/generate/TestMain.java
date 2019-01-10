package org.generate;

public class TestMain {

	/**
	 * @Title: main
	 * @Description: TODO
	 * @return void
	 * @throws
	 */
	public static void main(String[] args) {
		// 调用类生成测试DAG图
		DagBuilder dagbuilder = new DagBuilder(0,"D:\\test\\","E:\\");
		dagbuilder.BuildDAG(0,"D:\\test\\","E:\\");
		//System.out.println(Math.random());

	}

}
