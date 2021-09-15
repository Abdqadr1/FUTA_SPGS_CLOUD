package com.qadr.clouddatabasesystem;

import android.widget.TextView;

public interface StudentInterface {
    void getBioData(TextView name, TextView matric, TextView email, TextView phone, TextView dob, TextView address, TextView program, TextView year_of_award);
    void getPayments();
    void getResults();
    void getThesis(TextView author, TextView title, TextView supervisor, TextView content);
    void navigate(String where);
}
