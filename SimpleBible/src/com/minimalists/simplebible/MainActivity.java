package com.minimalists.simplebible;

import bible.objects.*;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ScrollView;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity {

	ListView lvBooks;
	ListView lvChapters;
	TextView tvText;
	ScrollView scrollLayout;
	Bible kjv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lvBooks = new ListView(this);
		lvChapters = new ListView(this);
		tvText = new TextView(this);
		scrollLayout = new ScrollView(this);
		
		//Setup Views
		scrollLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		scrollLayout.setHorizontalScrollBarEnabled(false);
		scrollLayout.setVerticalScrollBarEnabled(false);
		scrollLayout.setBackgroundColor(Color.BLACK);
		
		tvText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tvText.setBackgroundColor(Color.BLACK);
		tvText.setTextColor(Color.WHITE);
		tvText.setVisibility(TextView.INVISIBLE);
		
		lvChapters.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		lvChapters.setVisibility(ListView.INVISIBLE);
		
		lvBooks.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		lvBooks.setVisibility(ListView.VISIBLE);
		
		ArrayAdapter<String> list = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		list.add("Genesis");
		lvBooks.setAdapter(list);
		
		LoadBible();
		
		scrollLayout.addView(lvBooks);
		
		setContentView(scrollLayout);
	}
	
	private void LoadBible(){
		BibleObjectLoader loader = new BibleObjectLoader();
		try {
			java.io.InputStream in = this.getAssets().open("kjv.dat");
			loader.loadBibleDataObject(kjv, in);
			in.close();
		} catch (Exception e) {
			android.widget.Toast t = new android.widget.Toast(this);
			t.setText(e.getMessage().toString());
			t.show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("Books");
		menu.add("Chapters");
		menu.add("Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(item.getTitle().toString().toLowerCase() == "books"){
			tvText.setVisibility(TextView.INVISIBLE);
			lvChapters.setVisibility(ListView.INVISIBLE);
			lvBooks.setVisibility(ListView.VISIBLE);
			scrollLayout.removeViewAt(0);
			scrollLayout.addView(lvBooks);
		}
		
		if(item.getTitle().toString().toLowerCase() == "chapters"){
			tvText.setVisibility(TextView.INVISIBLE);
			lvBooks.setVisibility(ListView.INVISIBLE);
			lvChapters.setVisibility(ListView.VISIBLE);
			scrollLayout.removeViewAt(0);
			scrollLayout.addView(lvChapters);
		}
		
		if(item.getTitle().toString().toLowerCase() == "exit"){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
