package com.minimalists.simplebible;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

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
	private View.OnTouchListener textViewTouchListener;
	float lastX = 0.0f;
    float lastY = 0.0f;
	float currX = 0.0f;
    float currY = 0.0f;
	boolean bFlag = false;
	
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
				lvChapters.scrollTo(0, 0);
				getWindow().setTitle(kjv.getBook(iSelectedBook).BookName + " " + iSelectedChapter);
			}
		});
		
		lvChapters.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				iSelectedChapter = (position+1);
				
			    setTextViewActive();
			}
		});
		scrollLayout.setOnTouchListener(textViewTouchListener);
		linearLayout.addView(lvBooks);
		tvText.setText("Loading and Decompressing the Bible into Memory.\nThis can take a little while");
		setContentView(scrollLayout);
	}
	
	

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		super.dispatchTouchEvent(ev);
		return textViewTouchListener(ev);
	}



	public boolean textViewTouchListener(MotionEvent event) {
		// TODO Auto-generated method stub		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			lastX = event.getX();
            lastY = event.getY();
		}
		if(event.getAction() == MotionEvent.ACTION_UP){
			currX = event.getX();
            currY = event.getY();
			android.view.Display d = this.getWindowManager().getDefaultDisplay();
			int dx = (d.getWidth()/3);
			if(java.lang.Math.abs(lastX-currX) > dx){//Swipe needs to be roughly 1/3 screen width
				if(lastX < currX){//Finger moves from left to right
					if(this.iSelectedChapter > 1){
						this.iSelectedChapter--;				
					}else{
						if(this.iSelectedBook > 1){
							this.iSelectedBook--;
							this.iSelectedChapter = BibleKey.get(this.iSelectedBook);
						}
					}
					setTextViewActive();
				}else{//Finger moves from right to left
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
			}
            if(event.getEventTime() - event.getDownTime() > 1000
                    && Math.abs(lastX - currX) < 2
                    && Math.abs(lastY - currY) < 2){
                this.openOptionsMenu();
            }
		}
		return false;
		//return super.onTouchEvent(event);
	}

	private void LoadBible(final Bundle saveState){
		Runnable r = new Runnable(){
			@Override
			public void run(){
				BibleObjectLoader loader = new BibleObjectLoader();
				try {
					kjv = new Bible("KJV", true);
					java.io.InputStream in = getAssets().open("hashmap_kjv.jpg");
					BibleMap = loader.loadBibleHashMap(in);
					in.close();
					
					in = getAssets().open("hashmapkey_kjv.jpg");
					BibleKey = loader.loadBibleHashMapKey(in);
					in.close();
					
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
		//menu.add("Prev Chapter");
		//menu.add("Next Chapter");
		menu.add("Books");
		menu.add("Chapters");
		menu.add("Font Up");
		menu.add("Font Down");
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
			lvBooks.scrollTo(0, 0);
			setContentView(linearLayout);
		}
		
		if(item.getTitle().toString().toLowerCase().equals("chapters")){
			tvText.setVisibility(View.INVISIBLE);
			lvBooks.setVisibility(View.INVISIBLE);
			lvChapters.setVisibility(View.VISIBLE);
			linearLayout.removeAllViews();
			linearLayout.addView(lvChapters);	
			lvChapters.scrollTo(0, 0);
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
		
		if(item.getTitle().toString().toLowerCase().equals("font up")){
			this.tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.tvText.getTextSize() + 5.0f);
			setTextViewActive();
		}
		
		if(item.getTitle().toString().toLowerCase().equals("font down")){
			this.tvText.setTextSize(TypedValue.COMPLEX_UNIT_PX, this.tvText.getTextSize() - 5.0f);
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
		
		getWindow().setTitle(kjv.getBook(iSelectedBook).BookName + " " + iSelectedChapter);
		tvText.setText(BibleMap.get((iSelectedBook << 8) | iSelectedChapter));
		setContentView(scrollLayout);
		scrollLayout.scrollTo(0, 0);
	}

}
