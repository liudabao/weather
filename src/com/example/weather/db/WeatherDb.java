package com.example.weather.db;

import java.util.ArrayList;
import java.util.List;

import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class WeatherDb {

	public static final String DB_NAME="weather";
	
	public static final int version=1;
	
	private static WeatherDb weatherDb;
	
	private SQLiteDatabase db;
	
	/*
	 * 私有化	 
	 */
	private WeatherDb(Context context){
		WeatherOpenHelper dbHelper=new WeatherOpenHelper(context, DB_NAME, null, version);
	    db=dbHelper.getWritableDatabase();
	}
	/*
	 * 获取单例
	 */
	public synchronized static WeatherDb getInstance(Context context){
		
		if(weatherDb==null){
			weatherDb=new WeatherDb(context);
		}
		return weatherDb;
	}
	
	/*
	 * 保存省数据
	 */
	public void saveProvince(Province province){
		if(province!=null){
			ContentValues values=new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values)	;	
		}
	}
	
	public List<Province> loadProvince(){
		List<Province> list=new ArrayList<Province>();
		Cursor cursor=db.query("Province", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Province province=new Province();
				province.setId(cursor.getInt(cursor.getColumnIndex("id")));
				province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
			    province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
			    list.add(province);
			}while(cursor.moveToNext());
			
		}
		return list;
		
	}
	
	public void saveCity(City city){
		if(city!=null){
			ContentValues values=new ContentValues();
			values.put("city_name", city.getCityName());
			values.put("city_code", city.getCityCode());
			values.put("province_id", city.getProvinceId());
			db.insert("City", null, values);
		}
	}
	
	public List<City> loadCities(int provinceId){
		List<City> list=new ArrayList<City>();
		Cursor cursor=db.query("City", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				City city=new City();
				city.setId(cursor.getInt(cursor.getColumnIndex("id")));
				city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
				city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
				city.setProvinceId(provinceId);
			    list.add(city);
			}while(cursor.moveToNext());
			
		}
		return list;
		
	}
	
	public void saveCountry(Country country){
		if(country!=null){
			ContentValues values=new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("country", null, values);
		}
	}
	
	public List<Country> loadCounties(int cityId){
		List<Country> list=new ArrayList<Country>();
		Cursor cursor=db.query("Country", null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				Country country=new Country();
				country.setId(cursor.getInt(cursor.getColumnIndex("id")));
				country.setCountryName(cursor.getString(cursor.getColumnIndex("country_name")));
				country.setCountryCode(cursor.getString(cursor.getColumnIndex("country_code")));
				country.setCityId(cityId);
			    list.add(country);
			}while(cursor.moveToNext());
			
		}
		return list;
		
	}
	
}
