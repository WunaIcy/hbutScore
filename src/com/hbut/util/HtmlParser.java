package com.hbut.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.*;

import android.util.Log;

public class HtmlParser {

	/*
	 * get the course number from the html doc download
	 * 
	 * @param coutDoc html doc contains the number
	 * */
	public static int parserSbjCount(String countDoc){
		Pattern p =Pattern.compile("target=\"FmRight\">\\d+</A>");
		Matcher m = p.matcher(countDoc);
		int count = 0;
		boolean check = m.find();
		count= Integer.parseInt(m.group().split(">")[1].split("<")[0]);
		Log.v("count", Integer.toString(count));
		return count;
	}
	/*
	 * parse the grades of a class from the html doc using regex
	 * then store them in the Map data structure ,
	 * the first param is the course id,the second param is the grades of 
	 * the students who has chosen this course
	 * 
	 * @param clsDoc html doc contains the grades of the whole class
	 * */
	public static Map<String, List<ClsStuSbj>> parserClsSbj(String clsDoc) {
		Map<String, List<ClsStuSbj>> clsSbjMap = new HashMap<String, List<ClsStuSbj>>();
		String oldKey = "";
		String newKey = null;
		List<ClsStuSbj> clsStuSbjList = null;
		List<ClsStuSbj> clsStuSbjListT = null;
		ClsStuSbj clsStusbj = null;
		Pattern p = Pattern.compile("<TD Nowrap >[^<]*</TD>");
		Matcher m = p.matcher(clsDoc);
		int count = 0;
		while (m.find()) {

			switch (count) {
			case 0:
				newKey = m.group().split(">")[1].split("<")[0];
				if (!newKey.equalsIgnoreCase(oldKey)) {
					clsStuSbjListT = clsStuSbjList;
					clsStuSbjList = new ArrayList<ClsStuSbj>();
				}
				count = 1;
				break;
			case 1:
				clsStusbj = new ClsStuSbj();
				clsStusbj.setStuName(m.group().split(">")[1].split("<")[0]);
				count = 2;
				break;
			case 2:
				clsStusbj.setStuGrade(Integer.parseInt(m.group().split(">")[1]
						.split("<")[0]));
				clsStuSbjList.add(clsStusbj);
				if (!newKey.equalsIgnoreCase(oldKey) && !oldKey.equals(""))
					if (clsStuSbjListT != null)
						clsSbjMap.put(oldKey, clsStuSbjListT);		
				count = 0;
				oldKey = newKey;
				break;
			}
		}
		clsSbjMap.put(oldKey, clsStuSbjList);
		Log.v("map", "finish");
		return clsSbjMap;
	}

	/*
	 * get personal info from the file downloaded and contains these
	 */
	public static PersonInf parserPsInfo(String personDoc) {
		PersonInf pi = new PersonInf();
		Pattern p = Pattern.compile("<TD Nowrap >[^<]*</TD>");
		String[] result = new String[3];
		Matcher m = p.matcher(personDoc);
		for (int i = 0; i < result.length; i++) {
			if (!m.find())
				return null;
			else
				result[i] = m.group();
		}

		pi.setPwd(result[0].split(">")[1].split("<")[0]);

		pi.setCls(result[1].split(">")[1].split("<")[0]);

		pi.setName(result[2].split(">")[1].split("<")[0]);


		return pi;
	}

	/*
	 * get the individual grades of the specific student 
	 * */
	public static ArrayList<PersonSbj> parserPsSbj(String pSbjDoc) {
		ArrayList<PersonSbj> pSbjList = new ArrayList<PersonSbj>();
		PersonSbj pSbj = new PersonSbj();
		Pattern p = Pattern.compile("<TD Nowrap >[^<]*</TD>");
		Matcher m = p.matcher(pSbjDoc);
		int count = 0;
		while (m.find()) {
			switch (count) {
			case 0:
				pSbj.setSbjID(m.group().split(">")[1].split("<")[0]);
				break;
			case 1:
				pSbj.setSbjName(m.group().split(">")[1].split("<")[0]);
				break;
			case 2:
				// String temp = m.group().split(">")[1].split("<")[0];
				// if(temp.matches("[0-9]+")){}
				int grade = Integer
						.parseInt(m.group().split(">")[1].split("<")[0]);
				pSbj.setSbjGrade(grade);

				// else{
				// pSbj.setSbjLevel(temp);
				// }
				break;
			case 3:
				int times = Integer
						.parseInt(m.group().split(">")[1].split("<")[0]);
				pSbj.setSbjTimes(times);
				break;
			case 4:
				String note = null;
				String rawName = m.group().split(">")[1].split("<")[0];
				Matcher nameM = Pattern.compile("[\\d-]").matcher(rawName);
				if (nameM.find())
					note = nameM.replaceAll("");
				note = note.split("_")[0];
				pSbj.setSbjNote(note);
				break;
			}
			if (count < 5)
				count++;

			if (count == 5) {
				count = 0;
				pSbjList.add(pSbj);
				pSbj = new PersonSbj();
			}

		}
		return pSbjList;
	}
}
