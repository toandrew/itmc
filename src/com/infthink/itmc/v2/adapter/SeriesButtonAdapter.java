package com.infthink.itmc.v2.adapter;

import java.util.ArrayList;
import java.util.List;

import com.infthink.itmc.v2.type.MediaInfo;
import com.infthink.itmc.v2.type.MediaSetInfo;

import android.content.Context;
import android.util.FloatMath;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeriesButtonAdapter extends BaseAdapter
implements View.OnClickListener
{
private SparseArray<MediaSetInfo> availableCiArray;
private int ci = 1;
private boolean ciClickable = true;
private boolean ciClicked;
private Context context;
private List<Integer> disableCiList;
private MediaInfo mediaInfo;
private OnSeriesClickListener onClickListener;

public SeriesButtonAdapter(Context paramContext, MediaInfo paramMediaInfo)
{
  this.context = paramContext;
  this.mediaInfo = paramMediaInfo;
  this.availableCiArray = new SparseArray();
  this.disableCiList = new ArrayList();
}

public int getCount()
{
  if (this.mediaInfo != null)
    return (int)FloatMath.ceil(this.mediaInfo.setNow / 3.0F);
  return 0;
}

public Object getItem(int paramInt)
{
  return null;
}

public long getItemId(int paramInt)
{
  return paramInt;
}

public View getView(int paramInt, View paramView, ViewGroup paramViewGroup)
{
  return paramView;
}

public boolean isAvailableCi(int paramInt)
{
  return !this.disableCiList.contains(Integer.valueOf(paramInt));
}

public boolean isCurrentClickable()
{
  return this.ciClickable;
}

public boolean isEnabled(int paramInt)
{
  return false;
}

public void onClick(View paramView)
{
  if ((this.onClickListener != null) && (paramView.getTag() != null) && ((paramView.getTag() instanceof Integer)))
    this.onClickListener.onSeriesClick(((Integer)paramView.getTag()).intValue());
}

public void refresh()
{
  notifyDataSetChanged();
}

public void setAvailableCiList(int paramInt, List<MediaSetInfo> paramList)
{
        // if ((paramList == null) || (paramList.size() == 0));
        // while (true)
        // {
        // return;
        // int i = paramList.size();
        // for (int j = 0; j < i; j++)
        // {
        // MediaSetInfo localMediaSetInfo = (MediaSetInfo)paramList.get(j);
        // this.availableCiArray.put(localMediaSetInfo.nCi, localMediaSetInfo);
        // }
        // for (int k = 1; k <= paramInt; k++)
        // {
        // if (this.availableCiArray.get(k) != null)
        // continue;
        // this.disableCiList.add(Integer.valueOf(k));
        // }
        // }
}

public void setCurrentClickable(boolean paramBoolean)
{
  this.ciClickable = paramBoolean;
}

public void setCurrentClicked(boolean paramBoolean)
{
  this.ciClicked = paramBoolean;
}

public void setCurrentIndex(int paramInt)
{
  this.ci = paramInt;
}

public void setOnClickListener(OnSeriesClickListener paramOnSeriesClickListener)
{
  this.onClickListener = paramOnSeriesClickListener;
}

public int tryToAdjustCurCi()
{
  if (isAvailableCi(this.ci))
    return this.ci;
  while (true)
  {
    this.ci = (1 + this.ci);
    if (this.ci > this.mediaInfo.setNow)
      break;
    if (isAvailableCi(this.ci))
      return this.ci;
  }
  while (true)
  {
    this.ci = (-1 + this.ci);
    if (this.ci <= 0)
      break;
    if (isAvailableCi(this.ci))
      return this.ci;
  }
  this.ci = 1;
  return this.ci;
}

public static abstract interface OnSeriesClickListener
{
  public abstract void onSeriesClick(int paramInt);
}

private class ViewHolder
{
  public View vLeftLoading;
  public TextView vLeftText;
  public View vMiddleLoading;
  public TextView vMiddleText;
  public View vRightLoadding;
  public TextView vRightText;
  public View vRowContent;
  public View vSeparatorLineFirst;
  public View vSeparatorLineLast;
  public View vSeriesLeft;
  public View vSeriesMiddle;
  public View vSeriesRight;
  public View vSeriesRowDivider;

  private ViewHolder()
  {
  }
}
}