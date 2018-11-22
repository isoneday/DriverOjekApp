package com.isoneday.driverojekapp.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseHistoryRequest{

	@SerializedName("result")
	private String result;

	@SerializedName("msg")
	private String msg;

	@SerializedName("data")
	private List<DataRequestHistory> data;

	public void setResult(String result){
		this.result = result;
	}

	public String getResult(){
		return result;
	}

	public void setMsg(String msg){
		this.msg = msg;
	}

	public String getMsg(){
		return msg;
	}

	public void setData(List<DataRequestHistory> data){
		this.data = data;
	}

	public List<DataRequestHistory> getData(){
		return data;
	}
}