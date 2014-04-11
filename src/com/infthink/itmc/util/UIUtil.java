package com.infthink.itmc.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PaintFlagsDrawFilter;
import android.view.View;
import android.widget.TextView;

import com.infthink.itmc.MediaDetailActivity;
import com.infthink.itmc.R;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.type.PersonInfo;
import com.infthink.itmc.widget.MediaView;

public class UIUtil {
    // ListView 滚动到最下提示加载的view
    public static View createMediaLoadMoreView(Context context) {
        View localView = View.inflate(context, R.layout.load_more_view, null);
        localView.setPadding(0, 0, 0,
                context.getResources().getDimensionPixelSize(R.dimen.page_margin));
        return localView;
    }

    public static void fillMediaSummary(View view, Object data) {
        if (view == null) return;
        if (data instanceof MediaInfo) {
            MediaInfo info = (MediaInfo) data;
            ((MediaView) view.findViewById(R.id.media_view)).setMediaInfo((MediaInfo) data);

            // not use
            View directorView = view.findViewById(R.id.director_panel);
            if (directorView != null) {
                if (Util.isEmpty(info.director)) {
                    directorView.setVisibility(View.GONE);
                } else {
                    directorView.setVisibility(View.VISIBLE);
                    ((TextView) directorView.findViewById(R.id.director)).setText(info.director);
                }
            }

            View actorsView = view.findViewById(R.id.actors_panel);
            if (actorsView != null) {
                if (Util.isEmpty(info.actors)) {
                    actorsView.setVisibility(View.GONE);
                } else {
                    actorsView.setVisibility(View.VISIBLE);

                    View actors = view.findViewById(R.id.actors);
                    if (actors instanceof TextView) {
                        ((TextView) actors).setText(info.actors);
                    } else {
                        // ((ActorsView)actors).setActors(info.actors);
                    }
                }
            }

            View tagsView = view.findViewById(R.id.tags_panel);
            if (tagsView != null) {
                if (Util.isEmpty(info.tags)) {
                    tagsView.setVisibility(View.GONE);
                } else {
                    tagsView.setVisibility(View.VISIBLE);
                    ((TextView) tagsView.findViewById(R.id.tags)).setText(info.tags);
                }
            }

            View arearView = view.findViewById(R.id.area_panel);
            if (arearView != null) {
                if (Util.isEmpty(info.area)) {
                    arearView.setVisibility(View.GONE);
                } else {
                    arearView.setVisibility(View.VISIBLE);
                    ((TextView) arearView.findViewById(R.id.area)).setText(info.area);
                }
            }

            View nameView = view.findViewById(R.id.media_name);
            if (nameView != null) {
                if (Util.isEmpty(info.mediaName)) {
                    nameView.setVisibility(View.GONE);
                } else {
                    nameView.setVisibility(View.VISIBLE);
                    ((TextView) nameView.findViewById(R.id.media_name)).setText(info.mediaName);
                }
            }

            View timeView = view.findViewById(R.id.time_panel);
            if (timeView != null) {
                if (Util.isEmpty(info.issueDate)) {
                    timeView.setVisibility(View.GONE);
                } else {
                    timeView.setVisibility(View.VISIBLE);
                    ((TextView) timeView.findViewById(R.id.time))
                            .setText(info.issueDate.split("-")[0]);
                }
            }
        }
    }

    public static void handleMediaViewClick(Activity paramActivity, Object paramObject,
            boolean paramBoolean)
    // ,StatisticInfo paramStatisticInfo)
    {
        Intent localIntent;
        if (paramObject != null) {
            // if (!(paramObject instanceof MediaInfo))
            // break label69;
            localIntent = new Intent(paramActivity, MediaDetailActivity.class);
            localIntent.putExtra("mediaInfo", (MediaInfo) paramObject);
            localIntent.putExtra("isBanner", paramBoolean);
            // localIntent.putExtra("enterPathInfo", paramStatisticInfo);
        }
        // while (true)
        // {
        // if (localIntent != null)
        // paramActivity.startActivity(localIntent);
        // return;
        // label69: if ((paramObject instanceof PersonInfo))
        // {
        // localIntent = new Intent(paramActivity, MediaDetailActivity.class);
        // localIntent.putExtra("personInfo", (PersonInfo)paramObject);
        // localIntent.putExtra("isBanner", paramBoolean);
        // continue;
        // }
        // boolean bool = paramObject instanceof AlbumInfo;
        // localIntent = null;
        // if (!bool)
        // continue;
        // localIntent = new Intent(paramActivity, SubjectMediaActivity.class);
        // localIntent.putExtra("album", (AlbumInfo)paramObject);
        // localIntent.putExtra("enterPath", paramStatisticInfo);
        // }
    }

    public static void fillPosterViews(MediaView[] paramArrayOfMediaView,
            Object[] paramArrayOfObject) {
        if ((paramArrayOfMediaView != null) && (paramArrayOfObject != null)) {
            int i = 0;
            if (i < paramArrayOfMediaView.length) {
                paramArrayOfMediaView[i].setVisibility(4);
                for (int j = 0; j < paramArrayOfObject.length; j++) {
                    if (!(paramArrayOfObject[j] instanceof MediaInfo)) {

                    } else {
                        MediaInfo localMediaInfo = (MediaInfo) paramArrayOfObject[j];
                        paramArrayOfMediaView[j].setMediaInfo(localMediaInfo);
                        paramArrayOfMediaView[j].setDefaultPoster();
                    }
                }
                paramArrayOfMediaView[i].setVisibility(0);
                i++;
                // if ((i < paramArrayOfObject.length) && (paramArrayOfObject[i] != null)) {
                // if (!(paramArrayOfObject[i] instanceof MediaInfo)) {
                //
                // } else {
                // MediaInfo localMediaInfo = (MediaInfo) paramArrayOfObject[i];
                // paramArrayOfMediaView[i].setMediaInfo(localMediaInfo);
                // }
                //
                // paramArrayOfMediaView[i].setVisibility(0);
                // i++;
                // }
                // while (true) {
                // paramArrayOfMediaView[i].setVisibility(0);
                // i++;
                // if ((paramArrayOfObject[i] instanceof PersonInfo)) {
                // PersonInfo localPersonInfo = (PersonInfo) paramArrayOfObject[i];
                // paramArrayOfMediaView[i].setPersonInfo(localPersonInfo);
                // continue;
                // }
                // if ((paramArrayOfObject[i] instanceof Recommendation)) {
                // Recommendation localRecommendation = (Recommendation) paramArrayOfObject[i];
                // if (localRecommendation.mediaInfo != null) {
                // paramArrayOfMediaView[i].setMediaInfo(localRecommendation.mediaInfo);
                // continue;
                // }
                // if (localRecommendation.personInfo == null) continue;
                // paramArrayOfMediaView[i].setPersonInfo(localRecommendation.personInfo);
                // continue;
                // }
                // if (!(paramArrayOfObject[i] instanceof SpecialSubjectMedia)) continue;
                // SpecialSubjectMedia localSpecialSubjectMedia =
                // (SpecialSubjectMedia) paramArrayOfObject[i];
                // if (localSpecialSubjectMedia.mediaInfo != null) {
                // paramArrayOfMediaView[i].setMediaInfo(localSpecialSubjectMedia.mediaInfo);
                // continue;
                // }
                // if (localSpecialSubjectMedia.personInfo == null) continue;
                // paramArrayOfMediaView[i].setPersonInfo(localSpecialSubjectMedia.personInfo);
                // }
            }
        }
    }

    private static final Canvas sCanvas = new Canvas();

    static {
        sCanvas.setDrawFilter(new PaintFlagsDrawFilter(4, 2));
    }


    public static void fastBlur(Bitmap paramBitmap1, Bitmap paramBitmap2, int paramInt) {
        sCanvas.setBitmap(paramBitmap2);
        sCanvas.drawARGB(255, 0, 0, 0);
        // fastblur(paramBitmap1, paramBitmap2, paramInt);
        sCanvas.setBitmap(paramBitmap2);
        sCanvas.setBitmap(null);
    }

    /** 水平方向模糊度 */
    private static float hRadius = 10;
    /** 竖直方向模糊度 */
    private static float vRadius = 10;
    /** 模糊迭代度 */
    private static int iterations = 7;

    public static Bitmap BoxBlurFilter(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] inPixels = new int[width * height];
        int[] outPixels = new int[width * height];
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmp.getPixels(inPixels, 0, width, 0, 0, width, height);
        for (int i = 0; i < iterations; i++) {
            blur(inPixels, outPixels, width, height, hRadius);
            blur(outPixels, inPixels, height, width, vRadius);
        }
        blurFractional(inPixels, outPixels, width, height, hRadius);
        blurFractional(outPixels, inPixels, height, width, vRadius);
        bitmap.setPixels(inPixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    public static void blur(int[] in, int[] out, int width, int height, float radius) {
        int widthMinus1 = width - 1;
        int r = (int) radius;
        int tableSize = 2 * r + 1;
        int divide[] = new int[256 * tableSize];

        for (int i = 0; i < 256 * tableSize; i++)
            divide[i] = i / tableSize;

        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;
            int ta = 0, tr = 0, tg = 0, tb = 0;

            for (int i = -r; i <= r; i++) {
                int rgb = in[inIndex + clamp(i, 0, width - 1)];
                ta += (rgb >> 24) & 0xff;
                tr += (rgb >> 16) & 0xff;
                tg += (rgb >> 8) & 0xff;
                tb += rgb & 0xff;
            }

            for (int x = 0; x < width; x++) {
                out[outIndex] =
                        (divide[ta] << 24) | (divide[tr] << 16) | (divide[tg] << 8) | divide[tb];

                int i1 = x + r + 1;
                if (i1 > widthMinus1) i1 = widthMinus1;
                int i2 = x - r;
                if (i2 < 0) i2 = 0;
                int rgb1 = in[inIndex + i1];
                int rgb2 = in[inIndex + i2];

                ta += ((rgb1 >> 24) & 0xff) - ((rgb2 >> 24) & 0xff);
                tr += ((rgb1 & 0xff0000) - (rgb2 & 0xff0000)) >> 16;
                tg += ((rgb1 & 0xff00) - (rgb2 & 0xff00)) >> 8;
                tb += (rgb1 & 0xff) - (rgb2 & 0xff);
                outIndex += height;
            }
            inIndex += width;
        }
    }

    public static void blurFractional(int[] in, int[] out, int width, int height, float radius) {
        radius -= (int) radius;
        float f = 1.0f / (1 + 2 * radius);
        int inIndex = 0;

        for (int y = 0; y < height; y++) {
            int outIndex = y;

            out[outIndex] = in[0];
            outIndex += height;
            for (int x = 1; x < width - 1; x++) {
                int i = inIndex + x;
                int rgb1 = in[i - 1];
                int rgb2 = in[i];
                int rgb3 = in[i + 1];

                int a1 = (rgb1 >> 24) & 0xff;
                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;
                int a2 = (rgb2 >> 24) & 0xff;
                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;
                int a3 = (rgb3 >> 24) & 0xff;
                int r3 = (rgb3 >> 16) & 0xff;
                int g3 = (rgb3 >> 8) & 0xff;
                int b3 = rgb3 & 0xff;
                a1 = a2 + (int) ((a1 + a3) * radius);
                r1 = r2 + (int) ((r1 + r3) * radius);
                g1 = g2 + (int) ((g1 + g3) * radius);
                b1 = b2 + (int) ((b1 + b3) * radius);
                a1 *= f;
                r1 *= f;
                g1 *= f;
                b1 *= f;
                out[outIndex] = (a1 << 24) | (r1 << 16) | (g1 << 8) | b1;
                outIndex += height;
            }
            out[outIndex] = in[width - 1];
            inIndex += width;
        }
    }

    public static int clamp(int x, int a, int b) {
        return (x < a) ? a : (x > b) ? b : x;
    }


    public static String getMediaStatus(Context paramContext, MediaInfo paramMediaInfo) {
        // StringBuilder localStringBuilder = new StringBuilder();
        // if
        // (paramContext.getResources().getString(R.string.variety).equals(paramMediaInfo.category))
        // if (!Util.isEmpty(paramMediaInfo.lastIssueDate)) {
        // String[] arrayOfString = paramMediaInfo.lastIssueDate.split("-");
        // if (arrayOfString.length >= 3) {
        // localStringBuilder.append(arrayOfString[1]
        // + paramContext.getResources().getString(R.string.month));
        // localStringBuilder.append(arrayOfString[2]
        // + paramContext.getResources().getString(R.string.day));
        // localStringBuilder.append(paramContext.getResources().getString(R.string.update));
        // }
        // }
        //
        // if ((paramMediaInfo.setCount > 1) || (paramMediaInfo.setNow > 1)) {
        // if (paramMediaInfo.setNow == paramMediaInfo.setCount) {
        // String str2 = paramContext.getResources().getString(R.string.count_ji_quan);
        // Object[] arrayOfObject2 = new Object[1];
        // arrayOfObject2[0] = Integer.valueOf(paramMediaInfo.setNow);
        // localStringBuilder.append(String.format(str2, arrayOfObject2));
        // } else {
        // String str1 = paramContext.getResources().getString(R.string.update_to_count_ji);
        // Object[] arrayOfObject1 = new Object[1];
        // arrayOfObject1[0] = Integer.valueOf(paramMediaInfo.setNow);
        // localStringBuilder.append(String.format(str1, arrayOfObject1));
        // }
        //
        // }
        // if (paramMediaInfo.playLength <= 0) {
        //
        // } else {
        // int i = paramMediaInfo.playLength / 60;
        // int j = paramMediaInfo.playLength % 60;
        // if (i > 0) {
        // localStringBuilder.append(paramMediaInfo.playLength / 60);
        // localStringBuilder.append(paramContext.getResources().getString(R.string.minute));
        // } else {
        // localStringBuilder.append(j);
        // localStringBuilder.append(paramContext.getResources().getString(R.string.seconds));
        // }
        //
        // }
        //
        // return localStringBuilder.toString();

        StringBuilder sb = new StringBuilder();
        if ("综艺".equals(paramMediaInfo.category)) {
            if (!Util.isEmpty(paramMediaInfo.lastIssueDate)) {
                String[] arrayOfString = paramMediaInfo.lastIssueDate.split("-");
                if (arrayOfString.length >= 3) {
                    sb.append(arrayOfString[1] + "月");
                    sb.append(arrayOfString[2] + "日");
                    sb.append("更新");
                }
            }
        } else if ((paramMediaInfo.setCount > 1) || (paramMediaInfo.setNow > 1)) {
            if (paramMediaInfo.setNow == paramMediaInfo.setCount) {
                sb.append(paramMediaInfo.setCount + "集全");
            } else {
                sb.append("更新至" + paramMediaInfo.setNow + "集");
            }
        } else {
            int i = paramMediaInfo.playLength / 60;
            int j = paramMediaInfo.playLength % 60;
            if (i > 0) {
                sb.append(paramMediaInfo.playLength / 60);
                sb.append(paramContext.getResources().getString(R.string.minute));
            } else {
                sb.append(j);
                sb.append(paramContext.getResources().getString(R.string.seconds));
            }
        }
        return sb.toString();
        // }
    }



}
