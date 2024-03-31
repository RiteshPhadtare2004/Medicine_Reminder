package com.example.medicinereminder.ui.addMedicine;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.medicinereminder.AlarmReceiver;
import com.example.medicinereminder.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddMedicineFragment extends Fragment {

    private EditText editName, editCount, editCaregiverName, editCaregiverNumber;
    private DatabaseReference mDatabase;
    private FirebaseUser currentUser;
    private TextView morning, noon, evening;
    private int morningTimeHour = 0, morningTimeMinute = 0, noonTimeHour = 0,noonTimeMinute = 0, eveningTimeHour = 0, eveningTimeMinute = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        editName = root.findViewById(R.id.edit_name);
        editCount = root.findViewById(R.id.edit_count);
        editCaregiverName = root.findViewById(R.id.edit_caregiver_name);
        editCaregiverNumber = root.findViewById(R.id.edit_caregiver_number);
        morning = root.findViewById(R.id.morning);
        noon = root.findViewById(R.id.noon);
        evening = root.findViewById(R.id.evening);

        Button submitButton = root.findViewById(R.id.submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitUserData();
            }
        });

        morning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(morning,"morning");
            }
        });

        noon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(noon,"noon");
            }
        });

        evening.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(evening,"evening");
            }
        });

        return root;
    }

    private void submitUserData() {
        String name = editName.getText().toString().trim();
        String count = editCount.getText().toString().trim();
        String caregiverName = editCaregiverName.getText().toString().trim();
        String caregiverNumber = editCaregiverNumber.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(count) ||
                TextUtils.isEmpty(caregiverName) || TextUtils.isEmpty(caregiverNumber)) {
            Toast.makeText(getContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> userData = new HashMap<>();
        userData.put("name", name);
        userData.put("count", count);
        userData.put("morningTime", morningTimeHour+":"+morningTimeMinute);
        userData.put("noonTime", noonTimeHour+":"+noonTimeMinute);
        userData.put("eveningTime", eveningTimeHour+":"+eveningTimeMinute);
        userData.put("caregiverName", caregiverName);
        userData.put("caregiverNumber", caregiverNumber);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            mDatabase.child("userMedicineData").child(userId).setValue(userData)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Data submitted successfully", Toast.LENGTH_SHORT).show();
                            setAlarms(getActivity());

                            clearFields();
                        } else {
                            Toast.makeText(getContext(), "Failed to submit data", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void clearFields() {
        editName.setText("");
        editCount.setText("");
        editCaregiverName.setText("");
        editCaregiverNumber.setText("");
        morning.setText("");
        noon.setText("");
        evening.setText("");
        morningTimeHour = 0;
        morningTimeMinute = 0;
        noonTimeHour = 0;
        noonTimeMinute = 0;
        eveningTimeHour = 0;
        eveningTimeMinute = 0;
    }

    private void showTimePickerDialog(TextView time, String timeType) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Set the selected time on TextView
                        time.setText(String.format("%02d:%02d", hourOfDay, minute));
                        // Update the corresponding time variable
                        switch (timeType) {
                            case "morning":
                                morningTimeHour = hourOfDay;
                                morningTimeMinute = minute;
                                break;
                            case "noon":
                                noonTimeHour = hourOfDay;
                                noonTimeMinute = minute;
                                break;
                            case "evening":
                                eveningTimeHour = hourOfDay;
                                eveningTimeMinute = minute;
                                break;
                            default:
                                break;
                        }
                    }
                }, hour, minute, false);
        timePickerDialog.show();
    }


    private void setAlarms(Context context) {
        setAlarm(context, morningTimeHour, morningTimeMinute, "MorningAlarm");
        setAlarm(context, noonTimeHour, noonTimeMinute, "Noon Alarm");
        setAlarm(context, eveningTimeHour, eveningTimeMinute, "Evening Alarm");
    }

    public void setAlarm(Context context, int hour, int minute, String alarmName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar alarmTime = Calendar.getInstance();
        alarmTime.set(Calendar.HOUR_OF_DAY, hour);
        alarmTime.set(Calendar.MINUTE, minute);
        alarmTime.set(Calendar.SECOND, 0);

        if (alarmTime.before(Calendar.getInstance())) {
            alarmTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        if (alarmManager != null) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), pendingIntent);
        }
    }



}
