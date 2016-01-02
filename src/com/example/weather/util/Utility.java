package com.example.weather.util;

import android.text.TextUtils;

import com.example.weather.db.WeatherDb;
import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;

public class Utility {

	/*
	 * ����ʡ������
	 */
	public synchronized static boolean handleProvincesResponse(WeatherDb weatherDb,String response){
		
		if(!TextUtils.isEmpty(response)){
			String[] allProvince=response.split(",");
			if(allProvince!=null&&allProvince.length>0){
				for(String p:allProvince){
					String [] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					//�洢ʡ�����ݵ�province��
					weatherDb.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * �����м�����
	 */
	public static boolean handleCitiesResponse(WeatherDb weatherDb,String response,int provinceId){
		if(!TextUtils.isEmpty(response)){
			String[] allCity=response.split(",");
			if(allCity!=null&&allCity.length>0){
				for(String c:allCity){
					String [] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					//�洢ʡ�����ݵ�province��
					weatherDb.saveCity(city);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/*
	 * �����ؼ�����
	 */
	public static boolean handleCountiesResponse(WeatherDb weatherDb,String response,int cityId){
		if(!TextUtils.isEmpty(response)){
			String[] allCountry=response.split(",");
			if(allCountry!=null&&allCountry.length>0){
				for(String c:allCountry){
					String [] array=c.split("\\|");
					Country country=new Country();
					country.setCountryCode(array[0]);
					country.setCountryName(array[1]);
					country.setCityId(cityId);
					//�洢ʡ�����ݵ�province��
					weatherDb.saveCountry(country);
				}
				return true;
			}
		}
		return false;
		
	}
}
