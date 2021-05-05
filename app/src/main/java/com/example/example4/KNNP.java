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

public class KNNP {
	
	/**
	 * private double getmean() { 计算一个特征的mean }
	 **/
	/**
	 * private double getstd() { 计算一个特征的std }
	 **/

	public List<Data> wifi_dataset = null;

	// 构造函数
	public KNNP(String fileName, Context applicationContext) throws IOException {
		wifi_dataset = initDataSet(fileName, applicationContext);
	}

	// txt文件读取
	public List<Data> initDataSet(String fileName, Context applicationContext) throws IOException {
		List<Data> list = new ArrayList<Data>();
		/*
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(KNNP.class.getClassLoader().getResourceAsStream(fileName)));

		 */

		AssetManager assetsManager = applicationContext.getAssets();

		InputStream inputStream = assetsManager.open(fileName);

		BufferedReader bufferedReader_wifi = new BufferedReader(new InputStreamReader(inputStream));
		String line = null;
		while ((line = bufferedReader_wifi.readLine()) != null) {
			Data data = new Data();
			String[] s = line.split(" ");
			//六列特征值，最后一列是标签
			//String a1 = s[0];
			data.setWIFI1(Double.parseDouble(s[0]));
			data.setWIFI2(Double.parseDouble(s[1]));
			data.setWIFI3(Double.parseDouble(s[2]));
			data.setWIFI4(Double.parseDouble(s[3]));
			data.setWIFI5(Double.parseDouble(s[4]));
			data.setWIFI6(Double.parseDouble(s[5]));
			
			
			
			

			if (s[6].equals("687")) {
				data.setType(1);
			} else if(s[6].equals("689")){
				data.setType(2);
			} else if(s[6].equals("691")){
				data.setType(3);
			}else if(s[6].equals("693")){
				data.setType(4);
			}else if(s[6].equals("695")){
				data.setType(5);
			}else if(s[6].equals("697")){
				data.setType(6);
			}else if(s[6].equals("699")){
				data.setType(7);
			}else if(s[6].equals("701")){
				data.setType(8);
			}else if(s[6].equals("703")){
				data.setType(9);
			}else if(s[6].equals("705")){
				data.setType(10);
			}else if(s[6].equals("elevator")){
				data.setType(11);
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
		int type1 = 0, type2 = 0, type3 = 0,type4 = 0, type5 = 0, type6 = 0, type7 = 0, type8 = 0, type9 = 0,type10 = 0, type11 = 0;
		for (int i = 0; i < k; i++) {
			Data d = dataset.get(i);
			if (d.getType() == 1) {
				++type1;
				continue;
			} else if(d.getType() == 2){
				++type2;
			} else if(d.getType() == 3){
				++type3;
			}else if(d.getType() == 4){
				++type4;
			}else if(d.getType() == 5){
				++type5;
			}else if(d.getType() == 6){
				++type6;
			}else if(d.getType() == 7){
				++type7;
			}else if(d.getType() == 8){
				++type8;
			}else if(d.getType() == 9){
				++type9;
			}else if(d.getType() == 10){
				++type10;
			}else{
				++type11;
			}
		}
		int[] arr={type1,type2,type3,type4,type5,type6,type7,type8,type9,type10,type11};
		int temp=0;
		for(int i=0;i<arr.length;i++){
		for(int j=i+1;j<arr.length;j++){
		if(arr[j]>arr[i]){
		temp=arr[i];
		arr[i]=arr[j];
		arr[j]=temp;  
		}
		}
		}

		//频率值对比，预测输出
		if (arr[0]==type1) {

			return 1;
		} else if(arr[0]==type2){
			return 2;
		} else if(arr[0]==type2){
			return 2;
		}else if(arr[0]==type3){
			return 3;
		}else if(arr[0]==type4){
			return 4;
		}else if(arr[0]==type5){
			return 5;
		}else if(arr[0]==type6){
			return 6;
		}else if(arr[0]==type7){
			return 7;
		}else if(arr[0]==type8){
			return 8;
		}else if(arr[0]==type9){
			return 9;
		}else if(arr[0]==type10){
			return 10;
		}else{
			return 11;
		}
	}
	// 计算欧式距离
	private double calDistance(Data data, Data data2) {
		double sum = Math.pow((data.getWIFI1() - data2.getWIFI1()), 2) + Math.pow((data.getWIFI2() - data2.getWIFI2()), 2)
				+ Math.pow((data.getWIFI3() - data2.getWIFI3()), 2) + Math.pow((data.getWIFI4() - data2.getWIFI4()), 2)
				+ Math.pow((data.getWIFI5() - data2.getWIFI5()), 2) + Math.pow((data.getWIFI6() - data2.getWIFI6()), 2);
		return Math.sqrt(sum);
	}

	private List<Data> autoNorm(List<Data> oldDataSet) {
		List<Data> newDataSet = new ArrayList<Data>();
		// find max and min
		Map<String, Double> map = findMaxAndMin(oldDataSet);
		for (Data data : oldDataSet) {
			data.setWIFI1(calNewValue(data.getWIFI1(),
					map.get("maxWIFI1"), map.get("minWIFI1")));
			data.setWIFI2(calNewValue(data.getWIFI2(), map.get("maxWIFI2"),
					map.get("minWIFI2")));
			data.setWIFI3(calNewValue(data.getWIFI3(),
					map.get("maxWIFI3"), map.get("minWIFI3")));
			data.setWIFI4(calNewValue(data.getWIFI2(), map.get("maxWIFI4"),
					map.get("minWIFI4")));
			data.setWIFI5(calNewValue(data.getWIFI2(), map.get("maxWIFI5"),
					map.get("minWIFI5")));
			data.setWIFI6(calNewValue(data.getWIFI2(), map.get("maxWIFI6"),
					map.get("minWIFI6")));
			//todo
			newDataSet.add(data);
		}
		return newDataSet;
	}

	private double calNewValue(double oldValue, double maxValue, double minValue) {
		return (double)(oldValue - minValue) / (maxValue - minValue);
	}

	private Map<String, Double> findMaxAndMin(List<Data> oldDataSet) {
		Map<String, Double> map = new HashMap<String, Double>();
 
		double maxWIFI1 = Double.MIN_VALUE;
		double minWIFI1 = Double.MAX_VALUE;
		double maxWIFI2 = Double.MIN_VALUE;
		double minWIFI2 = Double.MAX_VALUE;
		double maxWIFI3 = Double.MIN_VALUE;
		double minWIFI3 = Double.MAX_VALUE;
		double maxWIFI4 = Double.MIN_VALUE;
		double minWIFI4 = Double.MAX_VALUE;
		double maxWIFI5 = Double.MIN_VALUE;
		double minWIFI5 = Double.MAX_VALUE;
		double maxWIFI6 = Double.MIN_VALUE;
		double minWIFI6 = Double.MAX_VALUE;
		
 
		for (Data data : oldDataSet) {
			if (data.getWIFI1() > maxWIFI1) {
				maxWIFI1 = data.getWIFI1();
			}
			if (data.getWIFI1() < minWIFI1) {
				minWIFI1 = data.getWIFI1();
			}

			if (data.getWIFI2() > maxWIFI2) {
				maxWIFI2 = data.getWIFI2();
			}
			if (data.getWIFI2() < minWIFI2) {
				minWIFI2 = data.getWIFI2();
			}

			if (data.getWIFI3() > maxWIFI3) {
				maxWIFI3 = data.getWIFI3();
			}
			if (data.getWIFI3() < minWIFI3) {
				minWIFI3 = data.getWIFI3();
			}
			if (data.getWIFI4() > maxWIFI4) {
				maxWIFI4 = data.getWIFI4();
			}
			if (data.getWIFI4() < minWIFI4) {
				minWIFI4 = data.getWIFI4();
			}
			if (data.getWIFI5() > maxWIFI5) {
				maxWIFI5 = data.getWIFI5();
			}
			if (data.getWIFI5() < minWIFI5) {
				minWIFI5 = data.getWIFI5();
			}
			if (data.getWIFI6() > maxWIFI6) {
				maxWIFI6 = data.getWIFI6();
			}
			if (data.getWIFI6() < minWIFI6) {
				minWIFI6 = data.getWIFI6();
			}
			
		}
		map.put("maxWIFI1", maxWIFI1);
		map.put("minWIFI1", minWIFI1);
		map.put("maxWIFI2", maxWIFI2);
		map.put("minWIFI2", minWIFI2);
		map.put("maxWIFI3", maxWIFI3);
		map.put("minWIFI3", minWIFI3);
		map.put("maxWIFI4", maxWIFI4);
		map.put("minWIFI4", minWIFI4);
		map.put("maxWIFI5", maxWIFI5);
		map.put("minWIFI5", minWIFI5);
		map.put("maxWIFI6", maxWIFI6);
		map.put("minWIFI6", minWIFI6);
	
 
		return map;
	}

	/**
	 * 取已有数据的20%作为测试数据，这里我们选取600个样本作为测试样本，其余作为训练样本（共3000组）
	 * @throws IOException
	 * @param wifi_testdata_random_revise
	 * @param String
	 * @param wifi_data
	 * @param wifi_dataset
	 */


	 public void test(Data wifi_data, List<Data> wifi_dataset, Context applicationContext) throws IOException {
		 List<Data> testDataSet = null;

		 testDataSet = initDataSet("wifi_testdata_random_revise", applicationContext);

		System.out.println(testDataSet);
		 //归一化数据
	 	List<Data> newTestDataSet = autoNorm(testDataSet);
	 	List<Data> newDataSet = autoNorm(wifi_dataset);
	 	int errorCount = 0;
	 	for (int i=0;i<newDataSet.size();i++){
	 		int predict_type=knn(wifi_data, newDataSet, 3);
	 		int actual_type=newDataSet.get(i).getType();
	 		if(predict_type!=actual_type){
	 			++errorCount;
			}
		}
	 	/*
	 	for (Data data : newTestDataSet) {
	 		//knn返回的就是数据类型
	 		int type = knn(data, newDataSet, 3);
	 		if (type != data.getType()) {
	 			++errorCount;
			}
	 	}

	 	 */
		
	 	System.out.println("错误率：" + (double)errorCount / testDataSet.size() + "%");
	}

	/*
	// 在主函数中完成输出
	public static void main(String[] args) throws Exception 
	{
		int k = 3;
		KNNP knn1 = new KNNP("posi_data.txt");// 相当于建立模型的过程
		knn1.test();
		

		List<Data> testDataSet = knn1.wifi_dataset;
		
		// Data data = new Data();
		// data.setWIFI1(0);
		// data.setWIFI2(0);
		// data.setWIFI3(-50);
		// data.setWIFI4(0);
		// data.setWIFI5(0);
		// data.setWIFI6(0);
		
		// System.out.println(knn1.knn(data, testDataSet, k));
	}

	 */



	}

	
	
	



	// 额外定义一个类用于数据存储
	class Data implements Comparable<Data>{
	
	
		private double WIFI1;
		
		private double WIFI2;
		
		private double WIFI3;

		private double WIFI4;

		private double WIFI5;

		private double WIFI6;
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
		
		public double getWIFI1() {
			return WIFI1;
		}
		public void setWIFI1(double WIFI1) {
			this.WIFI1 = WIFI1;
		}
		public double getWIFI2() {
			return WIFI2;
		}
		public void setWIFI2(double WIFI2) {
			this.WIFI2 = WIFI2;
		}

		public double getWIFI3() {
			return WIFI3;
		}
		public void setWIFI3(double WIFI3) {
			this.WIFI3 = WIFI3;
		}

		public double getWIFI4() {
			return WIFI4;
		}
		public void setWIFI4(double WIFI4) {
			this.WIFI4 = WIFI4;
		}

		public double getWIFI5() {
			return WIFI5;
		}
		public void setWIFI5(double WIFI5) {
			this.WIFI5 = WIFI5;
		}

		public double getWIFI6() {
			return WIFI6;
		}
		public void setWIFI6(double WIFI6) {
			this.WIFI6 = WIFI6;
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






        
 
