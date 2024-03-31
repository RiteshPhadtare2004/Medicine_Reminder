package com.example.medicinereminder.ui.Profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.medicinereminder.R;
import com.example.medicinereminder.databinding.FragmentNotificationsBinding;
import android.widget.LinearLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String userId = firebaseAuth.getCurrentUser().getUid();

        DatabaseReference userDataRef = FirebaseDatabase.getInstance().getReference("userData").child(userId);
        userDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                String email = dataSnapshot.child("email").getValue(String.class);
                String careTakerName = dataSnapshot.child("careTakerName").getValue(String.class);
                String careTakerNumber = dataSnapshot.child("careTakerNumber").getValue(String.class);

                binding.textViewName.setText("Name: " + name);
                binding.textViewEmail.setText("Email: " + email);
                binding.textViewCareTaker.setText("CareTaker: " + careTakerName + " (" + careTakerNumber + ")");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        DatabaseReference userMedicineDataRef = FirebaseDatabase.getInstance().getReference("userMedicineData").child(userId);
        userMedicineDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinearLayout medicineContainer = binding.medicineContainer;
                LayoutInflater inflater = LayoutInflater.from(getContext());

                for (DataSnapshot medicineSnapshot : dataSnapshot.getChildren()) {
                    View cardView = inflater.inflate(R.layout.medicine_card, medicineContainer, false);

                    TextView medicineNameTextView = cardView.findViewById(R.id.medicineNameTextView);
                    TextView careTakerNameTextView = cardView.findViewById(R.id.careTakerNameTextView);
                    TextView morningTimeTextView = cardView.findViewById(R.id.morningTimeTextView);
                    TextView noonTimeTextView = cardView.findViewById(R.id.noonTimeTextView);
                    TextView eveningTimeTextView = cardView.findViewById(R.id.eveningTimeTextView);

                    String medicineName = medicineSnapshot.child("Medicine Name").getValue(String.class);
                    String careTakerName = medicineSnapshot.child("caregiverName").getValue(String.class);
                    String morningTime = medicineSnapshot.child("morningTime").getValue(String.class);
                    String noonTime = medicineSnapshot.child("noonTime").getValue(String.class);
                    String eveningTime = medicineSnapshot.child("eveningTime").getValue(String.class);

                    medicineNameTextView.setText("Medicine Name: " + medicineName);
                    careTakerNameTextView.setText("CareTaker Name: " + careTakerName);
                    morningTimeTextView.setText("Morning Time: " + morningTime);
                    noonTimeTextView.setText("Noon Time: " + noonTime);
                    eveningTimeTextView.setText("Evening Time: " + eveningTime);

                    medicineContainer.addView(cardView);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle error
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
