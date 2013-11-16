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

package dev.dworks.libs.astickyheader.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class HeaderLayout extends FrameLayout {
    private int mHeaderWidth = 1;

    public HeaderLayout(Context context) {
        super(context);
    }	

    public HeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeaderLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    
    public void setHeaderWidth(int width) {
        mHeaderWidth = width;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    	int widthMeasureSpecNew = mHeaderWidth == 1 
    			? widthMeasureSpec 
    			: MeasureSpec.makeMeasureSpec(mHeaderWidth, MeasureSpec.getMode(widthMeasureSpec));
		super.onMeasure(widthMeasureSpecNew, heightMeasureSpec);
    }
}