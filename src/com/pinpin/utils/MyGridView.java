/**
 * MyGridView.java
 * com.pinpin.utils
 *
 * Function： TODO 
 *
 *   ver     date      		author
 * ──────────────────────────────────
 *   		 2016-1-11 		pano
 *
 * Copyright (c) 2016, TNT All Rights Reserved.
*/

package com.pinpin.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View.MeasureSpec;
import android.widget.GridView;

/**
 * ClassName:MyGridView
 * Function: TODO ADD FUNCTION
 * Reason:	 TODO ADD REASON
 *
 * @author   pano
 * @version  
 * @since    Ver 1.1
 * @Date	 2016-1-11		下午1:44:14
 *
 * @see 	  
 */
public class MyGridView extends GridView {   
    public MyGridView(Context context, AttributeSet attrs) {   
        super(context, attrs);   
    }   
    @Override   
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {   
       
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);   
            super.onMeasure(widthMeasureSpec, expandSpec);   
        
    }   
}
