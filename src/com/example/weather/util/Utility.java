package com.example.weather.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
	
	/*
	 * 解析服务器返回json数据
	 */
	public static void handleWeatherResponse(Context context,String response) {
		try {
			JSONObject jsonObject=new JSONObject(response);
			JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
			String cityName=weatherInfo.getString("city");
			String weatherCode=weatherInfo.getString("cityid");
			String temp1=weatherInfo.getString("temp1");
			String temp2=weatherInfo.getString("temp2");
			String weatherDesp=weatherInfo.getString("weather");
			String publishTime=weatherInfo.getString("ptime");
			saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void saveWeatherInfo(Context context,String cityName, String weatherCode,
			String temp1, String temp2, String weatherDesp, String publishTime) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy年M月d日");
		SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
	    editor.putBoolean("city_selected", true);
	    editor.putString("city_name", cityName);
	    editor.putString("weather_code",weatherCode );
	    editor.putString("temp1",temp1 );
	    editor.putString("temp2",temp2 );
	    editor.putString("weather_desp",weatherDesp );
	    editor.putString("publish_time",publishTime );
	    editor.putString("current_date",sdf.format(new Date()));
	    editor.commit();
	}
}
