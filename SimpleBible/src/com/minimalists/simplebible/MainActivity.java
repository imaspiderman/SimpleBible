package com.minimalists.simplebible;

import java.util.HashMap;

import android.os.Bundle;
import android.os.Handler;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
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
import android.widget.Toast;
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
	Integer iSelectedBook = 1;
	Integer iSelectedChapter = 1;
	private Handler loaderHandler;
	
	@SuppressLint("UseSparseArrays")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		int id=1;
		
		lvBooks = new ListView(this);
		lvChapters = new ListView(this);
		tvText = new TextView(this);
		scrollLayout = new ScrollView(this);
		linearLayout = new LinearLayout(this);
		loaderHandler = new Handler();

		bookList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		chapterList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1);
		
		//Setup Views
		scrollLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		scrollLayout.setHorizontalScrollBarEnabled(false);
		scrollLayout.setVerticalScrollBarEnabled(true);
		scrollLayout.setBackgroundColor(Color.BLACK);
		scrollLayout.setId(id++);
		
		linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		linearLayout.setId(id++);
		
		tvText.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		tvText.setBackgroundColor(Color.BLACK);
		tvText.setTextColor(Color.WHITE);
		tvText.setVisibility(View.VISIBLE);
		tvText.setId(id++);
		
		scrollLayout.addView(tvText);
		
		lvChapters.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		lvChapters.setBackgroundColor(Color.WHITE);
		lvChapters.setVisibility(View.INVISIBLE);
		lvChapters.setFastScrollEnabled(true);
		lvChapters.setVerticalScrollBarEnabled(true);
		lvChapters.setId(id++);
		
		lvBooks.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT));
		lvBooks.setBackgroundColor(Color.WHITE);
		lvBooks.setVisibility(View.INVISIBLE);
		lvBooks.setFastScrollEnabled(true);
		lvBooks.setVerticalScrollBarEnabled(true);
		lvBooks.setId(id++);
		
		lvBooks.setAdapter(bookList);
		lvChapters.setAdapter(chapterList);
		LoadBible(savedInstanceState);
		
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
				getWindow().setTitle(kjv.getBook(iSelectedBook).BookName);
			}
		});
		
		lvChapters.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				iSelectedChapter = (position+1);
				
			    setTextViewActive();
			}
		});
		
		linearLayout.addView(lvBooks);
		tvText.setText("Loading and Decompressing the Bible into Memory.\nThis can take a little while");
		setContentView(scrollLayout);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if(!outState.containsKey("BibleData")) outState.putSerializable("BibleData", BibleMap);
		if(!outState.containsKey("BibleKey")) outState.putSerializable("BibleKey", BibleKey);
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onBackPressed() {
		Toast.makeText(this, "If you wish to exit and unload the app please press the exit button from the menu options.", Toast.LENGTH_LONG).show();
	}

	@SuppressWarnings("unchecked")
	private void LoadBible(final Bundle saveState){
		Runnable r = new Runnable(){
			@Override
			public void run(){
				BibleObjectLoader loader = new BibleObjectLoader();
				try {
					kjv = new Bible("KJV", true);
					if(saveState == null){
						java.io.InputStream in = getAssets().open("hashmap_kjv.jpg");
						BibleMap = loader.loadBibleHashMap(in);
						in.close();
						
						in = getAssets().open("hashmapkey_kjv.jpg");
						BibleKey = loader.loadBibleHashMapKey(in);
						in.close();
					}else{
						BibleMap = (HashMap<Integer, String>) saveState.getSerializable("BibleData");
						BibleKey = (HashMap<Integer, Integer>) saveState.getSerializable("BibleKey");
					}
				} catch (Exception e) {
					android.widget.Toast.makeText(getBaseContext(), e.getMessage(), android.widget.Toast.LENGTH_LONG).show();
				}
				
				BibleBook[] books = kjv.getBooks();
				for(int i=0; i<books.length; i++){
					bookList.add(books[i].BookName);
				}
				books = null;
				
				loaderHandler.post(new Runnable(){
						@Override
						public void run(){
							tvText.setVisibility(View.INVISIBLE);
							lvChapters.setVisibility(View.INVISIBLE);
							lvBooks.setVisibility(View.VISIBLE);
							linearLayout.removeAllViews();
							linearLayout.addView(lvBooks);
							setContentView(linearLayout);
						}
					}
				);
			}
		};
		Thread t = new Thread(r);
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		menu.add("Prev Chapter");
		menu.add("Next Chapter");
		menu.add("Books");
		menu.add("Chapters");
		menu.add("Exit");
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if(BibleMap == null || BibleKey == null)return false;
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
		
		if(item.getTitle().toString().toLowerCase().equals("next chapter")){
			if(this.iSelectedChapter < BibleKey.get(this.iSelectedBook)){
				this.iSelectedChapter++;				
			}else{
				if(this.iSelectedBook < 66){
					this.iSelectedBook++;
					this.iSelectedChapter = 1;
				}
			}
			setTextViewActive();
		}
		
		if(item.getTitle().toString().toLowerCase().equals("prev chapter")){
			if(this.iSelectedChapter > 1){
				this.iSelectedChapter--;				
			}else{
				if(this.iSelectedBook > 1){
					this.iSelectedBook--;
					this.iSelectedChapter = BibleKey.get(this.iSelectedBook);
				}
			}
			setTextViewActive();
		}
		
		if(item.getTitle().toString().toLowerCase().equals("exit")){
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void setTextViewActive(){
		tvText.setVisibility(View.VISIBLE);
		lvBooks.setVisibility(View.INVISIBLE);
		lvChapters.setVisibility(View.VISIBLE);
		linearLayout.removeAllViews();
		
		getWindow().setTitle(kjv.getBook(iSelectedBook).BookName);
		tvText.setText(BibleMap.get((iSelectedBook << 8) | iSelectedChapter));
		setContentView(scrollLayout);
	}

}
