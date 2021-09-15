package com.qadr.clouddatabasesystem;

import android.widget.TextView;

public interface StaffInterface {
    void getStaffInfo(TextView name, TextView staff_id, TextView office_address, TextView dept);
    void navigate(String where);
}
