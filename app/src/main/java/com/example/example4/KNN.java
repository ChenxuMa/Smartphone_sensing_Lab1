package com.example.example4;
//package knn;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNN {
	
	/**
	 * private double getmean() { 计算一个特征的mean }
	 **/
	/**
	 * private double getstd() { 计算一个特征的std }
	 **/

	public List<Data> dataset = null;
	public List<Data> testDataSet=null;
	private Context context;
	// 构造函数
	public KNN(String fileName, Context applicationContext) throws IOException {
		dataset = initDataSet(fileName,applicationContext);
		testDataSet=initDataSet("test.txt", applicationContext);
		System.out.println("dataset has been initialized");
	}

	// txt文件读取
	private List<Data> initDataSet(String fileName, Context applicationContext) throws IOException {
		List<Data> list = new ArrayList<Data>();
		AssetManager assetsManager = applicationContext.getAssets();

		InputStream inputStream = assetsManager.open("motion_dataset.txt");
//        InputStreamReader isr = new InputStreamReader(new FileInputStream(fileName), "utf-8");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
		//BufferedReader bufferedReader = new BufferedReader(
		//		new InputStreamReader(KNN.class.getClassLoader().getResourceAsStream(fileName)));
		String line = null;
		while ((line = bufferedReader.readLine()) != null) {
			Data data = new Data();
			String[] s = line.split(" ");
			// Mile Time ICE对应x,y,z
		
			data.setX(Double.parseDouble(s[0]));
			data.setY(Double.parseDouble(s[1]));
			data.setZ(Double.parseDouble(s[2]));
			
			
			
			

			if (s[3].equals("Walking")) {
				data.setType(2);
			} else if(s[3].equals("Jump")){
				data.setType(3);
			} else{
				data.setType(1);
			}
			list.add(data);
		}
		return list;
	}

	/**
	 * 算法核心 data表示要预测的那个数据，在主函数中用类生成
	 * 
	 * @param data
	 * @param dataset
	 * @param k
	 */
	public int knn(Data data, List<Data> dataset, int k) {

		for (Data data2 : dataset) {
			double distance = calDistance(data, data2);
			data2.setDistance(distance);// 计算距离后储存
		}
		// 对距离进行排序，从小到大
		Collections.sort(dataset);// 排序
		// 从前k个样本中，找到出现频率最高的类别
		int type1 = 0, type2 = 0, type3 = 0;
		for (int i = 0; i < k; i++) {
			Data d = dataset.get(i);
			if (d.getType() == 1) {
				++type1;
				continue;
			} else if(d.getType() == 2){
				++type2;
			} else{
				++type3;
			}
		}
		//频率值对比，预测输出
		if (type1 > type2&&type1 > type3) {

			return 1;
		} else if(type2 > type1&&type2 > type3){
			return 2;
		} else{
			return 3;
		}
	}
	// 计算欧式距离
	private double calDistance(Data data, Data data2) {
		double sum = Math.pow((data.getX() - data2.getX()), 2) + Math.pow((data.getZ() - data2.getZ()), 2)
				+ Math.pow((data.getY() - data2.getY()), 2);
		return Math.sqrt(sum);
	}

	private List<Data> autoNorm(List<Data> oldDataSet) {
		List<Data> newDataSet = new ArrayList<Data>();
		// find max and min
		Map<String, Double> map = findMaxAndMin(oldDataSet);
		for (Data data : oldDataSet) {
			data.setX(calNewValue(data.getX(),
					map.get("maxX"), map.get("minX")));
			data.setY(calNewValue(data.getY(), map.get("maxY"),
					map.get("minY")));
			data.setZ(calNewValue(data.getZ(),
					map.get("maxZ"), map.get("minZ")));
			newDataSet.add(data);
		}
		return newDataSet;
	}

	private double calNewValue(double oldValue, double maxValue, double minValue) {
		return (double)(oldValue - minValue) / (maxValue - minValue);
	}

	private Map<String, Double> findMaxAndMin(List<Data> oldDataSet) {
		Map<String, Double> map = new HashMap<String, Double>();
 
		double maxX = Double.MIN_VALUE;
		double minX = Double.MAX_VALUE;
		double maxY = Double.MIN_VALUE;
		double minY = Double.MAX_VALUE;
		double maxZ = Double.MIN_VALUE;
		double minZ = Double.MAX_VALUE;
 
		for (Data data : oldDataSet) {
			if (data.getX() > maxX) {
				maxX = data.getX();
			}
			if (data.getX() < minX) {
				minX = data.getX();
			}
			if (data.getY() > maxY) {
				maxY = data.getY();
			}
			if (data.getY() < minY) {
				minY = data.getY();
			}
			if (data.getZ() > maxZ) {
				maxZ = data.getZ();
			}
			if (data.getZ() < minZ) {
				minZ = data.getZ();
			}
		}
		map.put("maxX", maxX);
		map.put("minX", minX);
		map.put("maxY", maxY);
		map.put("minY", minY);
		map.put("maxZ", maxZ);
		map.put("minZ", minZ);
 
		return map;
	}

	/**
	 * 取已有数据的20%作为测试数据，这里我们选取600个样本作为测试样本，其余作为训练样本（共3000组）
	 * @throws IOException 
	 */
	public void test() throws IOException {
		//List<Data> testDataSet = initDataSet("test.txt", applicationContext);
		//归一化数据
		List<Data> newTestDataSet = autoNorm(testDataSet);
		List<Data> newDataSet = autoNorm(dataset);
		int errorCount = 0;
		for (Data data : newTestDataSet) {
			//knn返回的就是数据类型
			int type = knn(data, newDataSet, 3);
			if (type != data.getType()) {
				++errorCount;
			}
		}
		
		System.out.println("错误率：" + (double)errorCount / testDataSet.size() + "%");
	}
	//存储所用的数据类
	static class Data implements Comparable<Data>{
	
	
		private double x;//mile
		
		private double y;//time
		
		private double z;//icecream
		/**
		 * 	1 代表静止
		 * 	2 代表走动
		 * 	3 代表Jump
		 */
		private int type;
		/**
		 * 两个数据距离（欧式距离）
		 */
		private double distance;
		
		public double getX() {
			return x;
		}
		public void setX(double X) {
			this.x = X;
		}
		public double getY() {
			return y;
		}
		public void setY(double Y) {
			this.y = Y;
		}
		public double getZ() {
			return z;
		}
		public void setZ(double Z) {
			this.z = Z;
		}
		public int getType() {
			return type;
		}
		public void setType(int type) {
			this.type = type;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		/* (non-Javadoc)
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 * 	这里进行倒排序
		 */
		@Override
		public int compareTo(Data o) {
			if (this.distance < o.getDistance()) {
				return -1;
			}else if (this.distance  > o.getDistance()) {
				return 1;
			}
			return 0;
		}
	}
	/*
	// 在主函数中完成输出
	public static void main(String[] args) throws Exception 
	{
		int k = 3;
		KNN knn1 = new KNN("datingTestSet.txt", this.getApplicationContext());// 相当于建立模型的过程
		knn1.test();
		

		List<Data> testDataSet = knn1.dataset;
		//data是新传入的数据
		// Data data = new Data();
		// data.setX(-1.1619061);
		// data.setY(1.4098879);
		// data.setZ(10.067666);
		// System.out.println(knn1.knn(data, testDataSet, k));
	}

	 */

	}

	
	
	



	// 额外定义一个类用于数据存储
	






        
 
