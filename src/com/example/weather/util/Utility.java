package com.example.weather.util;

import android.text.TextUtils;

import com.example.weather.db.WeatherDb;
import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;

public class Utility {

	/*
	 * 解析省级数据
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
					//存储省级数据到province表
					weatherDb.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析市级数据
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
					//存储省级数据到province表
					weatherDb.saveCity(city);
				}
				return true;
			}
		}
		return false;
		
	}
	
	/*
	 * 解析县级数据
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
					//存储省级数据到province表
					weatherDb.saveCountry(country);
				}
				return true;
			}
		}
		return false;
		
	}
}
