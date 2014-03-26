package com.infthink.itmc.util;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.infthink.itmc.R;
import com.infthink.itmc.type.MediaInfo;
import com.infthink.itmc.widget.MediaView;

public class UIUtil {
    // ListView 滚动到最下提示加载的view
    public static View createMediaLoadMoreView(Context context) {
        View localView = View.inflate(context, R.layout.load_more_view, null);
        localView.setPadding(0, 0, 0, context.getResources()
                .getDimensionPixelSize(R.dimen.page_margin));
        return localView;
    }

    public static void fillMediaSummary(View view, Object data) {
        if (view == null)
            return;
        if (data instanceof MediaInfo) {
            MediaInfo info = (MediaInfo) data;
            ((MediaView) view.findViewById(R.id.media_view))
                    .setMediaInfo((MediaInfo) data);

            // not use
            View directorView = view.findViewById(R.id.director_panel);
            if (directorView != null) {
                if (Util.isEmpty(info.director)) {
                    directorView.setVisibility(View.GONE);
                } else {
                    directorView.setVisibility(View.VISIBLE);
                    ((TextView) directorView.findViewById(R.id.director))
                            .setText(info.director);
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
                    ((TextView) tagsView.findViewById(R.id.tags))
                            .setText(info.tags);
                }
            }

            View arearView = view.findViewById(R.id.area_panel);
            if (arearView != null) {
                if (Util.isEmpty(info.area)) {
                    arearView.setVisibility(View.GONE);
                } else {
                    arearView.setVisibility(View.VISIBLE);
                    ((TextView) arearView.findViewById(R.id.area))
                            .setText(info.area);
                }
            }

            View nameView = view.findViewById(R.id.media_name);
            if (nameView != null) {
                if (Util.isEmpty(info.mediaName)) {
                    nameView.setVisibility(View.GONE);
                } else {
                    nameView.setVisibility(View.VISIBLE);
                    ((TextView) nameView.findViewById(R.id.media_name))
                            .setText(info.mediaName);
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
}
