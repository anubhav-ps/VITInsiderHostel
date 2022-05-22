package com.anubhav.vitinsiderhostel.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.anubhav.vitinsiderhostel.R;
import com.anubhav.vitinsiderhostel.models.Notice;
import com.google.android.material.textview.MaterialTextView;
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NoticeAdapter extends PagerAdapter {

    private final Context context;
    private final ViewPager viewPager;
    private final List<Notice> list;


    public NoticeAdapter(Context context,ViewPager viewPager, List<Notice> list) {
        this.context = context;
        this.viewPager = viewPager;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.cell_notice, container, false);

        MaterialTextView titleTxt , shortDescTxt , postedOnTxt;
        DotsIndicator indicator;

        titleTxt = view.findViewById(R.id.cellNoticeTitle);
        shortDescTxt = view.findViewById(R.id.cellNoticeShortDescription);
        postedOnTxt = view.findViewById(R.id.cellNoticePostedOn);
        indicator = view.findViewById(R.id.noticeSliderIndicator);

        indicator.setViewPager(viewPager);

        Notice notice = list.get(position);

        titleTxt.setText(notice.getTitle());

        String shortDesc = notice.getShortDescription().trim();
        shortDescTxt.setText(shortDesc);

        SimpleDateFormat formatToString = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        String dateString = formatToString.format(notice.getPostedOn().toDate());

        String postedOn = "Posted on : "+dateString;
        postedOnTxt.setText(postedOn);

        container.addView(view, position);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
