package com.tarks.favorite.page;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.Window;
import com.tarks.favorite.R;
import com.tarks.favorite.connect.AsyncHttpTask;
import com.tarks.favorite.connect.ImageDownloader;
import com.tarks.favorite.fadingactionbar.extras.actionbarsherlock.FadingActionBarHelper;
import com.tarks.favorite.global.Global;
import com.tarks.favorite.page.ProfileActivity.List;
import com.tarks.widget.listviewutil;

public class ProfileInfo extends SherlockActivity {

	String local_path;
	String member_srl = "0";
	String title;
	// ListView
	ListView listView;

	//Edit
	ImageButton profile_edit;
	//Profile
	ImageView profile;
	//TextView
	TextView profile_title;
	TextView profile_des;
	// List
	ArrayList<List> m_orders = new ArrayList<List>();
	// Define ListAdapter
	ListAdapter m_adapter;
	//Array member info
	String[] array;
	//self
	boolean self_profile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Can use progress
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.listview);
		// 액션바백버튼가져오기
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		local_path = getCacheDir().toString() + "/member/";
		// Get Intent
		Intent intent = getIntent();// 인텐트 받아오고
		member_srl = intent.getStringExtra("member_srl");
		
		
		//IF self profile
				self_profile = Global.getSetting("user_srl", "0").matches(member_srl);
		//Set List Adapter
		listView = (ListView) findViewById(R.id.listView1);
		 // Header, Footer 생성 및 등록
       View header = getLayoutInflater().inflate(R.layout.profile_avatar_layout, null, false);
       profile = (ImageView) header.findViewById(R.id.profile_img);
       profile_edit = (ImageButton) header.findViewById(R.id.edit);
       profile_title = (TextView) header.findViewById(R.id.title);
       profile_des = (TextView) header.findViewById(R.id.description);
       profile.setImageDrawable(Drawable.createFromPath(local_path + "thumbnail/"
				+ member_srl + ".jpg"));
       
  //     profile_edit.setOnClickListener(l)

       listView.addHeaderView(header);

		m_adapter = new ListAdapter(this, R.layout.list, m_orders);
		
		// Start Progressbar
		setSupportProgressBarIndeterminateVisibility(true);
		
		ArrayList<String> Paramname = new ArrayList<String>();
		Paramname.add("authcode");
		Paramname.add("user_srl");
		Paramname.add("user_srl_auth");
		Paramname.add("profile_user_srl");
		Paramname.add("member_info");

		ArrayList<String> Paramvalue = new ArrayList<String>();
		Paramvalue.add("642979");
		Paramvalue.add(Global.getSetting("user_srl",
				Global.getSetting("user_srl", "0")));
		Paramvalue.add(Global.getSetting("user_srl_auth",
				Global.getSetting("user_srl_auth", "null")));
		Paramvalue.add(String.valueOf(member_srl));
		Paramvalue
				.add("tarks_account//admin//name_1//name_2//gender//birthday//country_code//phone_number//join_day//profile_pic//profile_update//lang//country//like_me//favorite");

		new AsyncHttpTask(this, getString(R.string.server_path)
				+ "member/profile_info.php", mHandler, Paramname, Paramvalue,
				null, 1,0);

		
		

         
      

	}
	
	public void setProfileList(int like_me, String profile_pic){
		profile.setImageResource(R.drawable.person);
		if(profile_pic.matches("Y"))
			profile.setImageDrawable(Drawable.createFromPath(local_path + "thumbnail/"
				+ member_srl + ".jpg"));
		profile_title.setText(title);
		
		if(!self_profile) {
			profile_edit.setBackgroundResource(R.drawable.transparent);
			profile_edit.setImageResource(R.drawable.transparent);
		}


		
		// number cut
		NumberFormat nf = NumberFormat.getInstance();
		// nf.setMaximumIntegerDigits(5); //최대수 지정
		String result = nf.format(like_me);
		
		if(like_me != 0){
		int plural = like_me > 1 ?  2 : 1;
		profile_des.setText(String.format(getResources().getQuantityString(R.plurals.like_him, plural), result));
		}

	}
	
	public void setList(){
		

		String tarks_account = array[0];
		String admin = array[1];
		String name_1 = array[2];
		String name_2 = array[3];
		String gender = array[4];
		String birthday = array[5];
		String country_code = array[6];
		String phone_number = array[7];
		String join_day = array[8];
		String profile_pic = array[9];
		String profile_update = array[10];
		String lang = array[11];
		String country = array[12];
		String like_me = array[13];
		String favorite = array[14];
		
		setProfileList(Integer.parseInt(like_me), profile_pic);

		if(!tarks_account.matches("null")) AddList(getString(R.string.tarks_account) , tarks_account);
		if(!gender.matches("null")) AddList(getString(R.string.gender) , gender.matches("1") ? getString(R.string.male) : getString(R.string.female));
		if(!birthday.matches("null") && !birthday.matches("0")) AddList(getString(R.string.birthday) , birthday);
		if(!join_day.matches("null")) AddList(getString(R.string.join) , Global.getDate(join_day));
		if(!phone_number.matches("null")) AddList(getString(R.string.phone_number) , "+" + country_code + phone_number);
	//	if(!tarks_account.matches("null")) AddList(getString(R.string.tarks_account) , tarks_account);
	//	if(!tarks_account.matches("null")) AddList(getString(R.string.tarks_account) , tarks_account);
		

		listView.setAdapter(m_adapter);
		
	}
	

	public void AddList(String title, String description){
		List p1 = new List(title, description);
		m_orders.add(p1);
	}

	private class ListAdapter extends ArrayAdapter<List> {

		private ArrayList<List> items;

		public ListAdapter(Context context, int textViewResourceId,
				ArrayList<List> items) {
			super(context, textViewResourceId, items);
			this.items = items;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			if (v == null) {
				LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				v = vi.inflate(R.layout.list_info, null);
			}
			final List p = items.get(position);
			if (p != null) {
				TextView tt = (TextView) v.findViewById(R.id.title);
				TextView des = (TextView) v.findViewById(R.id.description);

				if (tt != null) {
					tt.setText(p.getTitle());
				}
				
				if (des != null) {
					des.setText(p.getDes());
				}
				
			}
			return v;
		}
	}

	class List {


		private String Title;
		private String Description;

		public List( String _Title, String _Description) {
			this.Title = _Title;
			this.Description = _Description;
		}
		
		public String getTitle() {
			return Title;
		}
		
		public String getDes() {
			return Description;
		}



	}
	
	

	
	public void ProfileImageDownload() {
		new ImageDownloader(this, getString(R.string.server_path)
				+ "files/profile/" + member_srl + ".jpg", mHandler, 2,0);
	}
	
	public void MemberInfoError() {
		Global.Infoalert(this, getString(R.string.error),
				getString(R.string.member_info_error_des),
				getString(R.string.yes));
	}
	
	protected Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			setSupportProgressBarIndeterminateVisibility(false);
			// IF Sucessfull no timeout

			if (msg.what == -1) {
				Global.ConnectionError(ProfileInfo.this);
			}

			if (msg.what == 1) {

				try {
					 array = msg.obj.toString().split("/LINE/.");
				//	Global.dumpArray(array);
					 
						String tarks_account = array[0];
						String admin = array[1];
						String name_1 = array[2];
						String name_2 = array[3];
						String gender = array[4];
						String birthday = array[5];
						String country_code = array[6];
						String phone_number = array[7];
						String join_day = array[8];
						String profile_pic = array[9];
						String profile_update = array[10];
						String lang = array[11];
						String country = array[12];
						String like_me = array[13];
						String favorite = array[14];
					
				
					title = Global.NameMaker(lang, name_1, name_2);
					
					
					
					if (Global.UpdateFileCache(profile_update,
							Global.getUser(member_srl, "0"),
							getString(R.string.server_path) + "files/profile/"
									+ member_srl + ".jpg", local_path,
							member_srl + ".jpg")
							&& profile_pic.matches("Y")) {
						Global.SaveUserSetting(member_srl, profile_update);
						ProfileImageDownload();
						// Log.i("test", "Let s profile image download");

					}
					
					if (profile_pic.matches("N")) {
					
						File file = new File(local_path + member_srl + ".jpg");
						file.delete();
						File file_thum = new File(local_path + "thumbnail/" + member_srl + ".jpg");
						file_thum.delete();
					//profile.setImageDrawable(R.drawable.people);
						profile.setImageResource(R.drawable.person);
					//	profile.setId(null);
					//	profile.setImageDrawable(res.getDrawable(R.drawable.person));
					}
					
					setList();
				
				} catch (Exception e) {
					MemberInfoError();

				}
			}

			if (msg.what == 2) {
				// Save File cache
				try {
					Global.SaveBitmapToFileCache((Bitmap) msg.obj, local_path,
							member_srl + ".jpg");
					Global.createThumbnail((Bitmap) msg.obj, local_path
							+ "thumbnail/", member_srl + ".jpg");

					setList();
	
				} catch (Exception e) {
				}
			}

		}
	};

	// 빽백키 상단액션바
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}

	}
	

}