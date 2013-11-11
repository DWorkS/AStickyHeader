package dev.dworks.libs.astickyheader.demo;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import dev.dworks.libs.astickyheader.R;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class ListActivity extends CacheActivity {
	private ListView list;
	private ImageAdapter mAdapter;
	private ArrayList<Section> sections = new ArrayList<Section>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		initControls();
	}

	private void initControls() {
		list = (ListView)findViewById(R.id.list);
		mAdapter = new ImageAdapter(this);
		for (int i = 0; i < mHeaderPositions.length; i++) {
			sections.add(new Section(mHeaderPositions[i], mHeaderNames[i]));
		}
		SimpleSectionedListAdapter simpleSectionedGridAdapter = new SimpleSectionedListAdapter(this, R.layout.list_item_header, mAdapter);
		simpleSectionedGridAdapter.setSections(sections.toArray(new Section[0]));
		list.setAdapter(simpleSectionedGridAdapter);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.grid, menu);
		return true;
	}
	
	private class ImageAdapter extends BaseAdapter{
		
		private LayoutInflater mInflater;

		public ImageAdapter(Context context) {
			mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return mImageIds.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView image;
			TextView name;
			if (convertView == null) {
				convertView = mInflater.inflate(R.layout.list_item, parent, false);
			}

			image = ViewHolder.get(convertView, R.id.image);
			loadBitmap(mImageIds[position], image);
			name = ViewHolder.get(convertView, R.id.name);
			name.setText("A Cute Cat"+(position + 1));
			return convertView;
		}

	}
	
    private Integer[] mImageIds = { 
    		R.drawable.cat1,R.drawable.cat2,R.drawable.cat3,R.drawable.cat4,R.drawable.cat5,R.drawable.cat6,R.drawable.cat7,R.drawable.cat8,R.drawable.cat9,R.drawable.cat10,
    		R.drawable.cat11,R.drawable.cat12,R.drawable.cat13,R.drawable.cat14,R.drawable.cat15,R.drawable.cat16,R.drawable.cat17,R.drawable.cat18,R.drawable.cat19,R.drawable.cat20,
    		R.drawable.cat21,R.drawable.cat22,R.drawable.cat23,R.drawable.cat24,R.drawable.cat25,R.drawable.cat26,R.drawable.cat27,R.drawable.cat28,R.drawable.cat29,R.drawable.cat30,
    		R.drawable.cat31,R.drawable.cat32,R.drawable.cat33,R.drawable.cat34,R.drawable.cat35,R.drawable.cat36,R.drawable.cat37,R.drawable.cat38,R.drawable.cat39,R.drawable.cat40,
    		R.drawable.cat41,R.drawable.cat42,R.drawable.cat43,R.drawable.cat44,R.drawable.cat45,R.drawable.cat46,R.drawable.cat47,R.drawable.cat48,R.drawable.cat49,R.drawable.cat50,
    		R.drawable.cat51,R.drawable.cat52,R.drawable.cat53,R.drawable.cat54,R.drawable.cat55,R.drawable.cat56,R.drawable.cat57,R.drawable.cat58,R.drawable.cat59,R.drawable.cat60,
    		R.drawable.cat61,R.drawable.cat62,R.drawable.cat63,R.drawable.cat64,R.drawable.cat65,R.drawable.cat66,R.drawable.cat67,R.drawable.cat68,R.drawable.cat69,R.drawable.cat70,
    		R.drawable.cat71,R.drawable.cat72,R.drawable.cat73,R.drawable.cat74,R.drawable.cat75,R.drawable.cat76,R.drawable.cat77,R.drawable.cat78,R.drawable.cat79,R.drawable.cat80,
    		R.drawable.cat81,R.drawable.cat82,R.drawable.cat83,R.drawable.cat84,R.drawable.cat85,R.drawable.cat86,R.drawable.cat87,R.drawable.cat88,R.drawable.cat89,R.drawable.cat90,
    		R.drawable.cat91,R.drawable.cat92,R.drawable.cat93,R.drawable.cat94,R.drawable.cat95,R.drawable.cat96,R.drawable.cat97,R.drawable.cat98,R.drawable.cat99,R.drawable.cat100,
    };
    
    private String[] mHeaderNames = { "Cute Cats", "Few Cats", "Some Cats", "Some More Cats", "Some More More Cats", "Many Cats", "Many Many Cats", "So Many Cats" };
    private Integer[] mHeaderPositions = { 0, 6, 11, 37, 38, 60, 77, 89 };

	public static class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(View view, int id) {
			SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
			if (viewHolder == null) {
				viewHolder = new SparseArray<View>();
				view.setTag(viewHolder);
			}
			View childView = viewHolder.get(id);
			if (childView == null) {
				childView = view.findViewById(id);
				viewHolder.put(id, childView);
			}
			return (T) childView;
		}
	}
}
