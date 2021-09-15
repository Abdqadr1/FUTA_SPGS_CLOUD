package com.qadr.clouddatabasesystem;

import android.widget.TextView;

public interface AdminInterface {
    void getAdminData(TextView name, TextView id, TextView address);
    void navigate(String which);
}
