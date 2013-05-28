package com.minimalists.simplebible;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ScrollView;
import bible.objects.*;

@SuppressLint("DefaultLocale")
public class MainActivity extends Activity {

	ListView lvBooks;
	ListView lvChapters;
	TextView tvText;
	ScrollView scrollLayout;
	LinearLayout linearLayout;
	Bible kjv;
	ArrayAdapter<String>bookList;
	ArrayAdapter<String>chapterList;
	java.util.HashMap<Integer, String> BibleMap;
	java.util.HashMap<Integer, Integer> BibleKey;
	Integer iSelectedBook;
	Integer iSelectedChapter;
	
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lvBooks = new ListView(this);
		lvChapters = new ListView(this);
		tvText = new TextView(this);
		scrollLayout = new ScrollView(this);
		linearLayout = new LinearLayout(this);
		bookList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		chapterList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		
		//Setup Views
		scrollLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		scrollLayout.setHorizontalScrollBarEnabled(false);
		scrollLayout.setVerticalScrollBarEnabled(true);
		scrollLayout.setBackgroundColor(Color.BLACK);
		
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		
		tvText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tvText.setBackgroundColor(Color.BLACK);
		tvText.setTextColor(Color.WHITE);
		tvText.setVisibility(View.INVISIBLE);
		
		scrollLayout.addView(tvText);
		
		lvChapters.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		lvChapters.setBackgroundColor(Color.WHITE);
		lvChapters.setVisibility(View.INVISIBLE);
		lvChapters.setFastScrollEnabled(true);
		lvChapters.setVerticalScrollBarEnabled(true);
		
		lvBooks.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		lvBooks.setBackgroundColor(Color.WHITE);
		lvBooks.setVisibility(View.VISIBLE);
		lvBooks.setFastScrollEnabled(true);
		lvBooks.setVerticalScrollBarEnabled(true);
		
		lvBooks.setAdapter(bookList);
		lvChapters.setAdapter(chapterList);
		
		LoadBible(bookList);
		lvBooks.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    chapterList.clear();
			    int iChapters = BibleKey.get(position+1);
			    for(int i=0; i<iChapters; i++){
			    	chapterList.add(String.valueOf(i+1));
			    }
			    
			    tvText.setVisibility(View.INVISIBLE);
				lvBooks.setVisibility(View.INVISIBLE);
				lvChapters.setVisibility(View.VISIBLE);
				
				iSelectedBook = (position+1);
				iSelectedChapter = 1;
				
				linearLayout.removeAllViews();
				linearLayout.addView(lvChapters);
			}
		});
		
		lvChapters.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				iSelectedChapter = (position+1);
				
			    tvText.setVisibility(View.VISIBLE);
				lvBooks.setVisibility(View.INVISIBLE);
				lvChapters.setVisibility(View.VISIBLE);
				linearLayout.removeAllViews();
				
				tvText.setText(BibleMap.get((iSelectedBook << 8) | iSelectedChapter));
				setContentView(scrollLayout);
			}
		});
		
		linearLayout.addView(lvBooks);
		
		setContentView(linearLayout);
	}
	
	private void LoadBible(ArrayAdapter<String> bookList){
		BibleObjectLoader loader = new BibleObjectLoader();
		try {
			kjv = new Bible("KJV", true);
			java.io.InputStream in = this.getAssets().open("hashmap_kjv.jpg");
			BibleMap = loader.loadBibleHashMap(in);
			in.close();
			
			in = this.getAssets().open("hashmapkey_kjv.jpg");
			BibleKey = loader.loadBibleHashMapKey(in);
			in.close();
		} catch (Exception e) {
			android.widget.Toast t = new android.widget.Toast(this);
			t.setText(e.getMessage().toString());
			t.show();
		}
		
		BibleBook[] books = kjv.getBooks();
		for(int i=0; i<books.length; i++){
			bookList.add(books[i].BookName);
		}
		books = null;
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
		if(item.getTitle().toString().toLowerCase().equals("books")){
			tvText.setVisibility(View.INVISIBLE);
			lvChapters.setVisibility(View.INVISIBLE);
			lvBooks.setVisibility(View.VISIBLE);
			linearLayout.removeAllViews();
			linearLayout.addView(lvBooks);
			setContentView(linearLayout);
		}
		
		if(item.getTitle().toString().toLowerCase().equals("chapters")){
			tvText.setVisibility(View.INVISIBLE);
			lvBooks.setVisibility(View.INVISIBLE);
			lvChapters.setVisibility(View.VISIBLE);
			linearLayout.removeAllViews();
			linearLayout.addView(lvChapters);
			setContentView(linearLayout);
		}
		
		if(item.getTitle().toString().toLowerCase().equals("exit")){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	

}
