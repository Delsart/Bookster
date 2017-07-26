package com.delsart.bookdownload.listandadapter;

public class mlist  {
	private String name;
	private String time;
	private String info;
	private String durl;
	public mlist(String name, String time, String info, String durl){
		this.name=name;
		this.time=time;
		this.info=info;
		this.durl=durl;
	}
	public String getname(){
		return name;
	}
	public String gettime(){
		return time;
	}
	public String getinfo(){
		return info;
	}
	public String getdurl(){
		return durl;
	}
}
