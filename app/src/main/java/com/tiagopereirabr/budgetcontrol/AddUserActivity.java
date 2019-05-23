package com.tiagopereirabr.budgetcontrol;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import java.util.GregorianCalendar;

public class AddUserActivity extends AppCompatActivity {
    private static final String TAG = "AddUserActivity";

    private EditText mName;
    private EditText mEmail;
    private Switch mCanInput;
    private GregorianCalendar mCalendar = new GregorianCalendar();
    private AvatarItem mAvatarSelected;

    private AvatarAdapter mAvatarAdapter;

    private int avatars[] = {R.drawable.ic_male1,R.drawable.ic_male2,R.drawable.ic_male3,R.drawable.ic_male4,R.drawable.ic_male5,R.drawable.ic_male6,R.drawable.ic_male7,R.drawable.ic_female1,R.drawable.ic_female2,R.drawable.ic_female3,R.drawable.ic_female4};


    private Bundle mArgs = new Bundle();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinnerAvatar = findViewById(R.id.au_spinner);

        mAvatarAdapter = new AvatarAdapter(this, avatars);
        spinnerAvatar.setAdapter(mAvatarAdapter);

        spinnerAvatar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mAvatarSelected = new AvatarItem(avatars[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mName = findViewById(R.id.au_name);
        mEmail = findViewById(R.id.au_email);
        mCanInput = findViewById(R.id.au_can_input_switch);

        Button saveButton = findViewById(R.id.au_save);

        final User user;

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentResolver contentResolver = getContentResolver();
                ContentValues values = new ContentValues();

                if (mName.length() != 0) {
                    values.put(UsersContract.Columns.NAME, mName.getText().toString());
                    values.put(UsersContract.Columns.EMAIL, mEmail.getText().toString());
                    int canInput;
                    if (mCanInput.isChecked()) {
                        canInput = 1;
                    } else {
                        canInput = 0;
                    }
                    values.put(UsersContract.Columns.CAN_INPUT_EXPSENSES,canInput);
                    long date = mCalendar.getTimeInMillis() / 1000;
                    values.put(UsersContract.Columns.INITIAL_DATE,date);
                    values.put(UsersContract.Columns.AVATAR,mAvatarSelected.getAvatar());
                    contentResolver.insert(UsersContract.CONTENT_URI, values);
                }

                finish();
            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //finish();
                onBackPressed();
                break;
        }
        return true;
    }


}
