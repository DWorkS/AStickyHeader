/*
 * Copyright 2013 Hari Krishna Dulipudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.dworks.libs.astickyheader;

import java.util.Arrays;
import java.util.Comparator;

import android.content.Context;
import android.database.DataSetObserver;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import dev.dworks.libs.astickyheader.ui.PinnedSectionGridView;
import dev.dworks.libs.astickyheader.ui.PinnedSectionGridView.PinnedSectionGridAdapter;

public abstract class SectionedGridAdapter extends BaseAdapter implements PinnedSectionGridAdapter {
	protected static final int TYPE_NORMAL_CELL = 0;
	protected static final int TYPE_FILLER = -1;
	protected static final int TYPE_HEADER = -2;
	protected static final int TYPE_HEADER_FILLER = -3;
	private boolean mValid = true;
	private ListAdapter mBaseAdapter;
	private SparseArray<Section> mSections = new SparseArray<Section>();
	private Context mContext;
	private int mNumColumns;
	private int mWidth;
	private int mColumnWidth;
	private int mHorizontalSpacing;
	private int mStrechMode;
	private int requestedColumnWidth;
	private int requestedHorizontalSpacing;
	private GridView mGridView;
	private int mHeaderHeight;
	private int mNormalCellHeight;

	public SectionedGridAdapter(final Context context, final BaseAdapter baseAdapter) {
		mHeaderHeight = getHeaderHeight();
		mNormalCellHeight = getNormalCellHeight();
		// mNormalCellWidth = getNormalCellWidth();
		mBaseAdapter = baseAdapter;
		mContext = context;
		mBaseAdapter.registerDataSetObserver(new DataSetObserver() {
			@Override
			public void onChanged() {
				mValid = !mBaseAdapter.isEmpty();
				notifyDataSetChanged();
			}

			@Override
			public void onInvalidated() {
				mValid = false;
				notifyDataSetInvalidated();
			}
		});
	}

	public void setGridView(GridView gridView) {
		if (!(gridView instanceof PinnedSectionGridView)) {
			throw new IllegalArgumentException("Does your grid view extends PinnedSectionGridView?");
		}
		mGridView = gridView;
		mStrechMode = gridView.getStretchMode();
		mWidth = gridView.getWidth() - (mGridView.getPaddingLeft() + mGridView.getPaddingRight());
		mNumColumns = ((PinnedSectionGridView) gridView).getNumColumns();
		requestedColumnWidth = ((PinnedSectionGridView) gridView).getColumnWidth();
		requestedHorizontalSpacing = ((PinnedSectionGridView) gridView).getHorizontalSpacing();
	}

	private int getHeaderSize() {
		if (mWidth != mGridView.getWidth()) {
			mStrechMode = mGridView.getStretchMode();
			mWidth = mGridView.getWidth() - (mGridView.getPaddingLeft() + mGridView.getPaddingRight());
			mNumColumns = ((PinnedSectionGridView) mGridView).getNumColumns();
			requestedColumnWidth = ((PinnedSectionGridView) mGridView).getColumnWidth();
			requestedHorizontalSpacing = ((PinnedSectionGridView) mGridView).getHorizontalSpacing();
		}

		final int spaceLeftOver = mWidth - mNumColumns * requestedColumnWidth - (mNumColumns - 1)
				* requestedHorizontalSpacing;
		switch (mStrechMode) {
		case GridView.NO_STRETCH: // Nobody stretches
			mWidth -= spaceLeftOver;
			mColumnWidth = requestedColumnWidth;
			mHorizontalSpacing = requestedHorizontalSpacing;
			break;

		case GridView.STRETCH_COLUMN_WIDTH:
			mColumnWidth = requestedColumnWidth + spaceLeftOver / mNumColumns;
			mHorizontalSpacing = requestedHorizontalSpacing;
			break;

		case GridView.STRETCH_SPACING:
			mColumnWidth = requestedColumnWidth;
			if (mNumColumns > 1) {
				mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver / (mNumColumns - 1);
			} else {
				mHorizontalSpacing = requestedHorizontalSpacing + spaceLeftOver;
			}
			break;

		case GridView.STRETCH_SPACING_UNIFORM:
			mColumnWidth = requestedColumnWidth;
			mHorizontalSpacing = requestedHorizontalSpacing;
			mWidth = mWidth - spaceLeftOver + 2 * mHorizontalSpacing;
			break;
		}
		return mWidth + (mNumColumns - 1) * (mColumnWidth + mHorizontalSpacing);
	}

	public void setSections(final Section[] sections) {
		mSections.clear();

		Arrays.sort(sections, new Comparator<Section>() {
			@Override
			public int compare(final Section o, final Section o1) {
				return o.firstPosition == o1.firstPosition ? 0 : o.firstPosition < o1.firstPosition ? -1 : 1;
			}
		});

		int offset = 0; // offset positions for the headers we're adding
		for (int i = 0; i < sections.length; i++) {
			final Section section = sections[i];
			Section sectionAdd;

			for (int j = 0; j < mNumColumns - 1; j++) {
				sectionAdd = new Section(section.firstPosition, section.title);
				sectionAdd.type = TYPE_HEADER_FILLER;
				sectionAdd.sectionedPosition = sectionAdd.firstPosition + offset;
				mSections.append(sectionAdd.sectionedPosition, sectionAdd);
				++offset;
			}

			sectionAdd = new Section(section.firstPosition, section.title);
			sectionAdd.type = TYPE_HEADER;
			sectionAdd.sectionedPosition = sectionAdd.firstPosition + offset;
			mSections.append(sectionAdd.sectionedPosition, sectionAdd);
			++offset;

			if (i + 1 < sections.length) {
				final int nextPos = sections[i + 1].firstPosition;
				final int itemsCount = nextPos - section.firstPosition;
				final int dummyCount = mNumColumns - itemsCount % mNumColumns;
				if (mNumColumns != dummyCount) {
					for (int j = 0; j < dummyCount; j++) {
						sectionAdd = new Section(section.firstPosition, section.title);
						sectionAdd.type = TYPE_FILLER;
						sectionAdd.sectionedPosition = nextPos + offset;
						mSections.append(sectionAdd.sectionedPosition, sectionAdd);
						++offset;
					}
				}
			}
		}

		notifyDataSetChanged();
	}

	public int positionToSectionedPosition(final int position) {
		int offset = 0;
		for (int i = 0; i < mSections.size(); i++) {
			if (mSections.valueAt(i).firstPosition > position) {
				break;
			}
			++offset;
		}
		return position + offset;
	}

	public int sectionedPositionToPosition(final int sectionedPosition) {
		if (isSectionHeaderPosition(sectionedPosition)) {
			return ListView.INVALID_POSITION;
		}

		int offset = 0;
		for (int i = 0; i < mSections.size(); i++) {
			if (mSections.valueAt(i).sectionedPosition > sectionedPosition) {
				break;
			}
			--offset;
		}
		return sectionedPosition + offset;
	}

	public boolean isSectionHeaderPosition(final int position) {
		return mSections.get(position) != null;
	}

	@Override
	public int getCount() {
		return mValid ? mBaseAdapter.getCount() + mSections.size() : 0;
	}

	@Override
	public Object getItem(final int position) {
		return isSectionHeaderPosition(position) ? mSections.get(position) : mBaseAdapter
				.getItem(sectionedPositionToPosition(position));
	}

	@Override
	public long getItemId(final int position) {
		return isSectionHeaderPosition(position) ? Integer.MAX_VALUE - mSections.indexOfKey(position) : mBaseAdapter
				.getItemId(sectionedPositionToPosition(position));
	}

	@Override
	public final int getItemViewType(final int position) {
		if (!isSectionHeaderPosition(position))
			return getItemViewTypeExtra(position);
		final int type = mSections.get(position).type;
		return type;
	}

	/**
	 * this allows whoever uses this adapter to customize additional cells types
	 * 
	 * @param position
	 */
	protected int getItemViewTypeExtra(final int position) {
		return TYPE_NORMAL_CELL;
	}

	@Override
	public boolean isEnabled(final int position) {
		return isSectionHeaderPosition(position) ? false : mBaseAdapter
				.isEnabled(sectionedPositionToPosition(position));
	}

	@Override
	public final int getViewTypeCount() {
		// this includes: empty cells, header, header-filler, and extras (which includes at least the normal cells
		return 3 + getViewTypeCountExtra();
	}

	public int getViewTypeCountExtra() {
		return 1;
	}

	@Override
	public boolean areAllItemsEnabled() {
		return false;
	}

	@Override
	public boolean hasStableIds() {
		return mBaseAdapter.hasStableIds();
	}

	@Override
	public boolean isEmpty() {
		return mBaseAdapter.isEmpty();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		View view = null;
		if (isSectionHeaderPosition(position)) {
			final Section section = mSections.get(position);
			LayoutParams layoutParams;
			switch (section.type) {
			case TYPE_HEADER:
				view = handleSectionHeaderView(convertView, section, parent);
				layoutParams = view.getLayoutParams();
				layoutParams.width = getHeaderSize();
				view.setLayoutParams(layoutParams);
				view.setVisibility(View.VISIBLE);
				break;
			case TYPE_HEADER_FILLER:
				view = convertView;
				if (view == null) {
					view = new View(mContext);
					view.setLayoutParams(new AbsListView.LayoutParams(0, mHeaderHeight));
				}
				break;
			case TYPE_FILLER:
				view = convertView;
				if (view == null) {
					view = new View(mContext);
					view.setLayoutParams(new AbsListView.LayoutParams(0, mNormalCellHeight));
				}
				final boolean lastInRow = position % mNumColumns == mNumColumns - 1;
				layoutParams = view.getLayoutParams();
				layoutParams.width = lastInRow ? LayoutParams.MATCH_PARENT : 0;
				view.setLayoutParams(layoutParams);
				break;
			}
		} else {
			view = mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
			final LayoutParams layoutParams = view.getLayoutParams();
			layoutParams.height = mNormalCellHeight;
			layoutParams.width = mColumnWidth;
			view.setLayoutParams(layoutParams);
		}
		return view;
	}

	protected abstract View handleSectionHeaderView(final View convertView, final Section section, ViewGroup parent);

	protected abstract int getHeaderHeight();

	protected abstract int getNormalCellHeight();

	@Override
	public boolean isItemViewTypePinned(final int position) {
		final Section section = mSections.get(position);
		return isSectionHeaderPosition(position) && section.type == TYPE_HEADER;
	}

	// ///////////////////////////////////////////////////
	// Section //
	// //////////
	public static class Section {
		int firstPosition;
		int sectionedPosition;
		CharSequence title;
		int type = 0;

		public Section(final int firstPosition, final CharSequence title) {
			this.firstPosition = firstPosition;
			this.title = title;
		}

		public CharSequence getTitle() {
			return title;
		}
	}

	// ///////////////////////////////////////////////////
	// ViewHolder //
	// /////////////

	public static class ViewHolder {
		@SuppressWarnings("unchecked")
		public static <T extends View> T get(final View view, final int id) {
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