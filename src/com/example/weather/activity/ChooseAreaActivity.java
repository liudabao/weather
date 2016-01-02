package com.example.weather.activity;


import java.util.ArrayList;
import java.util.List;

import com.example.weather.R;
import com.example.weather.db.WeatherDb;
import com.example.weather.model.City;
import com.example.weather.model.Country;
import com.example.weather.model.Province;
import com.example.weather.util.HttpCallbackListener;
import com.example.weather.util.HttpUtil;
import com.example.weather.util.Utility;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class ChooseAreaActivity extends Activity{

	public static final int LEVEL_PROVINCE=0;
	public static final int LEVEL_CITY=1;
	public static final int LEVEL_COUNTRY=2;
	
	private ProgressDialog progressDialog;
	private TextView title;
	private ListView listView;
	private ArrayAdapter<String> adapter;
	private WeatherDb weatherDb;
	private List<String> dataList=new ArrayList<String>();
	//省列表
	private List<Province> provinceList;
	//市列表
	private List<City> cityList;
	//县列表
	private List<Country> countryList;
	
	private Province selectedProvince;
	private City selectedCity;
	private Country selectedCountry;
	
	private int currentLevle;
	
	private boolean isFromWeatherActivity;
	
	public void onCreate(Bundle savedInstanceBundle){
		super.onCreate(savedInstanceBundle);
		setContentView(R.layout.choose_area);
		
		isFromWeatherActivity=getIntent().getBooleanExtra("from_weather_activity", false);
		SharedPreferences prefs=PreferenceManager.getDefaultSharedPreferences(this);
		if(prefs.getBoolean("city_selected", false)&&!isFromWeatherActivity){
			Intent intent=new Intent(this,WeatherActivity.class);
			startActivity(intent);
			finish();
			return;
		}
		listView=(ListView)findViewById(R.id.list_view);
		title=(TextView)findViewById(R.id.title);
		adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,dataList);
		listView.setAdapter(adapter);
		weatherDb=WeatherDb.getInstance(this);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(currentLevle==LEVEL_PROVINCE){
					selectedProvince=provinceList.get(position);
					queryCities();
				}
				else if(currentLevle==LEVEL_CITY){
					selectedCity=cityList.get(position);
					queryCounties();
				}
				else if(currentLevle==LEVEL_COUNTRY){
					String countryCode=countryList.get(position).getCountryCode();
					Intent intent=new Intent(ChooseAreaActivity.this,WeatherActivity.class);
				    intent.putExtra("country_code", countryCode);
				    startActivity(intent);
				    finish();
				}
			}
		});
		queryProvinces();
	}
	
	/*
	 * 查询所有省份
	 */
	private void queryProvinces(){
		provinceList=weatherDb.loadProvince();
		if(provinceList.size()>0){
			dataList.clear();
			for(Province p:provinceList){
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText("中国");
			currentLevle=LEVEL_PROVINCE;
		}
		else{
			queryFromServer(null,"province");
		}
	}
	/*
	 * 查询所有市
	 */
	private void queryCities(){
		cityList=weatherDb.loadCities(selectedProvince.getId());
		if(cityList.size()>0){
			dataList.clear();
			for(City c:cityList){
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedProvince.getProvinceName());
			currentLevle=LEVEL_CITY;
		}
		else{
			queryFromServer(selectedProvince.getProvinceCode(),"city");
		}
	}
	/*
	 * 查询所有县
	 */
	private void queryCounties(){
		countryList=weatherDb.loadCounties(selectedCity.getId());
		if(countryList.size()>0){
			dataList.clear();
			for(Country c:countryList){
				dataList.add(c.getCountryName());
			}
			adapter.notifyDataSetChanged();
			listView.setSelection(0);
			title.setText(selectedCity.getCityName());
			currentLevle=LEVEL_COUNTRY;
		}
		else{
			queryFromServer(selectedCity.getCityCode(),"country");
		}
	}
	
	private void queryFromServer(final String code,final String type){
		String address;
		if(!TextUtils.isEmpty(code)){
			address= "http://www.weather.com.cn/data/list3/city" + code +
					".xml";
		}
		else{
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpResquest(address, new HttpCallbackListener() {
			
			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean result=false;
				if("province".equals(type)){
					result=Utility.handleProvincesResponse(weatherDb, response);
				}
				else if("city".equals(type)){
					result=Utility.handleCitiesResponse(weatherDb, response, selectedProvince.getId());
				}
				else if("country".equals(type)){
					result=Utility.handleCountiesResponse(weatherDb, response, selectedCity.getId());
				}
				if(result){
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							closeProgressDialog();
							if("province".equals(type)){
								queryProvinces();
							}
							else if("city".equals(type)){
								queryCities();
							}
							else if("country".equals(type)){
								queryCounties();
							}
						}
					});
				}
			}
			
			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "Failed to load", Toast.LENGTH_SHORT).show();
					}
				});
			}
		});
	}
	private void showProgressDialog(){
		if(progressDialog==null){
		
			progressDialog=new ProgressDialog(this);
		    progressDialog.setMessage("Loading");
		    progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}
	
	private void closeProgressDialog(){
		if(progressDialog!=null){
			progressDialog.dismiss();
		}
	}
	
	@Override
	public void onBackPressed(){
		if(currentLevle==LEVEL_COUNTRY){
			queryCities();
		}
		else if(currentLevle==LEVEL_CITY){
			queryProvinces();
		}
		else{
			if(isFromWeatherActivity){
				Intent intent=new Intent(this,WeatherActivity.class);
				startActivity(intent);
			}
			finish();
		}
	}
	public void onDestroy(){
		super.onDestroy();
	}
}
