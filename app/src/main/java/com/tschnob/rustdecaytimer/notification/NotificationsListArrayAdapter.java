package com.tschnob.rustdecaytimer.notification;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tschnob.rustdecaytimer.R;
import com.tschnob.rustdecaytimer.timer.TimeHelper;

import java.util.List;

public class NotificationsListArrayAdapter extends ArrayAdapter<NotificationMetaData> {

    public interface OnCancelListener {
        void onCancel(int position);
    }

    private LayoutInflater layoutInflater;
    private Context context;
    private OnCancelListener onCancelListener;
    private List<NotificationMetaData> notifications;

    public NotificationsListArrayAdapter(
            Context context,
            @NonNull List<NotificationMetaData> notifications
    ) {
        super(context, R.layout.notification_list_item, notifications);
        layoutInflater = LayoutInflater.from(context);

        this.context = context;
        this.notifications = notifications;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.notification_list_item, parent, false);
        }

        TextView notificationText = (TextView) convertView.findViewById(R.id.notification_item_text);
        NotificationMetaData notification = notifications.get(position);
        notificationText.setText(generateNotificationText(notification, context.getString(R.string.notification_item_text)));

        ImageView cancelNotification = (ImageView) convertView.findViewById(R.id.cancel_notification);
        cancelNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCancelListener.onCancel(position);
            }
        });

        return convertView;
    }

    public void setOnCancelListener(OnCancelListener onCancelListener) {
        this.onCancelListener = onCancelListener;
    }

    private static String generateNotificationText(NotificationMetaData notification, String unformattedText) {
        String beforeEventText = "";
        if (notification.getType() == NotificationMetaData.Type.BEFORE
                && notification.getTimeBeforeEvent().isPresent()) {

            beforeEventText = notification.getTimeBeforeEvent().get().toString() + " ";
        }

        return String.format(
                unformattedText,
                beforeEventText + notification.getType().toString().toLowerCase(),
                notification.getEvent().toString().toLowerCase()
        );
    }
}
