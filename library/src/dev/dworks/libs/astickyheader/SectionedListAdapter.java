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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import dev.dworks.libs.astickyheader.ui.PinnedSectionListView.PinnedSectionListAdapter;

public class SectionedListAdapter extends BaseAdapter implements PinnedSectionListAdapter {
	private boolean mValid = true;
	private final int mSectionResourceId;
	private final LayoutInflater mLayoutInflater;
	private final ListAdapter mBaseAdapter;
	private final SparseArray<Section> mSections = new SparseArray<Section>();
	private final int mHeaderTextViewResId;

	public static class Section {
		int firstPosition;
		int sectionedPosition;
		CharSequence title;

		public Section(final int firstPosition, final CharSequence title) {
			this.firstPosition = firstPosition;
			this.title = title;
		}

		public CharSequence getTitle() {
			return title;
		}
	}

	public SectionedListAdapter(final Context context, final int sectionResourceId,
			final int headerTextViewResId, final BaseAdapter baseAdapter) {
		this.mHeaderTextViewResId = headerTextViewResId;
		mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mSectionResourceId = sectionResourceId;
		mBaseAdapter = baseAdapter;
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

	public void setSections(final Section[] sections) {
		mSections.clear();

		notifyDataSetChanged();
		Arrays.sort(sections, new Comparator<Section>() {
			@Override
			public int compare(final Section o, final Section o1) {
				return o.firstPosition == o1.firstPosition ? 0 : o.firstPosition < o1.firstPosition ? -1 : 1;
			}
		});

		int offset = 0; // offset positions for the headers we're adding
		for (final Section section : sections) {
			section.sectionedPosition = section.firstPosition + offset;
			mSections.append(section.sectionedPosition, section);
			++offset;
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
	public int getItemViewType(final int position) {
		return isSectionHeaderPosition(position) ? getViewTypeCount() - 1 : mBaseAdapter.getItemViewType(position);
	}

	@Override
	public boolean isEnabled(final int position) {
		// noinspection SimplifiableConditionalExpression
		return isSectionHeaderPosition(position) ? false : mBaseAdapter
				.isEnabled(sectionedPositionToPosition(position));
	}

	@Override
	public int getViewTypeCount() {
		return mBaseAdapter.getViewTypeCount() + 1; // the section headings
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
	public View getView(final int position, View convertView, final ViewGroup parent) {
		if (isSectionHeaderPosition(position)) {
			TextView view;
			if (null == convertView) {
				convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
			} else {
				if (null == convertView.findViewById(mHeaderTextViewResId)) {
					convertView = mLayoutInflater.inflate(mSectionResourceId, parent, false);
				}
			}
			view = (TextView) convertView.findViewById(mHeaderTextViewResId);
			view.setText(mSections.get(position).title);
			return convertView;

		} else {
			return mBaseAdapter.getView(sectionedPositionToPosition(position), convertView, parent);
		}
	}

	@Override
	public boolean isItemViewTypePinned(final int position) {
		return isSectionHeaderPosition(position);
	}
}