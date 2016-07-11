package com.example.greent.petals.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greent.petals.R;
import com.example.greent.petals.data.AnalyticsApplication;
import com.example.greent.petals.data.FlowerProduct;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import java.util.Arrays;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "CheckoutActivity";

    //Analytics Setup
    Tracker mTracker;

    FlowerProduct mSelectedProduct;
    final double DELIVERY_FEE = 10.00;
    double mTaxPercentage = 0.08;
    MessageDialogFragment mMessagePopUp;
    static final int PICK_CONTACT_REQUEST = 100;
    private static final int RC_SIGN_IN = 9001;
    String userSubmittedMessage = null;
    String userSubmittedRecName = null;
    String userSubmittedRecAddress = null;
    String mUserEmailAddress = "";
    TextView mMessageContentTextView;
    TextView mRecipientNameTextView;
    TextView mRecipientAddressTextView;
    private GoogleApiClient mGoogleApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() called with: savedInstanceState = [" + savedInstanceState + "]");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();

        //Configure Google Sign-in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        //Create a Play Services API Client
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Bundle startArgs = getIntent().getExtras();

        if (startArgs != null) {
            mSelectedProduct = startArgs.getParcelable(getString(R.string.checkout_selected_product));
        }

        if (mSelectedProduct != null) {

            ImageView thumbView = (ImageView) findViewById(R.id.checkout_order_thumb);

            Picasso.with(this)
                    .load(mSelectedProduct.img_url_sml)
                    .into(thumbView);

            TextView productNameTextView = (TextView) findViewById(R.id.checkout_order_name);
            productNameTextView.setText(mSelectedProduct.name);

            TextView productPriceTextView = (TextView) findViewById(R.id.checkout_order_price);
            productPriceTextView.setText(String.format(getString(R.string.format_price),mSelectedProduct.price));


            CardView messageCardView = (CardView) findViewById(R.id.checkout_message_container);

            mMessageContentTextView = (TextView) findViewById(R.id.checkout_message_content);

            mRecipientNameTextView = (TextView) findViewById(R.id.checkout_recipient_name);

            mRecipientAddressTextView = (TextView) findViewById(R.id.checkout_recipient_address_line);

            //Restore User entry if they existed
            if (savedInstanceState != null) {
                String savedMessage = savedInstanceState.getString(getString(R.string.checkout_bundle_message_key));
                Log.d(TAG, "onCreate: restoring savedMessage: " + savedMessage);
                mMessageContentTextView.setText(savedMessage);
                String savedRecName = savedInstanceState.getString(getString(R.string.checkout_bundle_rec_name_key));
                Log.d(TAG, "onCreate: restoring savedRecName: " + savedRecName);
                mRecipientNameTextView.setText(savedRecName);
                String savedRecAddress = savedInstanceState.getString(getString(R.string.checkout_bundle_rec_address_key));
                Log.d(TAG, "onCreate: restoring savedRecAddress: " + savedRecAddress);
                mRecipientAddressTextView.setText(savedRecAddress);
            }




            //Declare the message dialog
            mMessagePopUp = new MessageDialogFragment();



            messageCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Validate the message
                    //set background color
                    //pop up a modal
                    mMessagePopUp.show(getSupportFragmentManager(),"message");
                }
            });

            CardView recipientCardView = (CardView) findViewById(R.id.checkout_recipient_container);

            recipientCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pop up a modal
                    //pick Contact
                    pickContact();
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (userSubmittedRecAddress == null) {
                        Log.d(TAG, "onClick: Recipient not set.");
                        Snackbar.make(view, "Hi. Please choose select a recipient.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Log.d(TAG, "onClick: Recipient set. Trying sign in...");
                        signIn();
                        //sendOrderEmailToUser();
                    }
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void sendOrderEmailToUser() {
        Intent orderEmailIntent = new Intent(Intent.ACTION_SENDTO);
        orderEmailIntent.setType("text/plain");
        orderEmailIntent.putExtra(Intent.EXTRA_EMAIL,mUserEmailAddress);
        orderEmailIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.checkout_email_subject));
        orderEmailIntent.putExtra(Intent.EXTRA_TEXT, composeEmailBody());
        if (orderEmailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(orderEmailIntent);
        }
    }

    private String composeEmailBody() {
        String emailBody = "Thank you so much for your Order.\n" +
                           mSelectedProduct.name + "is sure to brighten up the day for " + mRecipientNameTextView + ".\n" +
                           "\n\nSincerely,\n" + "Petals Team";
        return emailBody;
    }

    public void processDialogPositiveClick(String message) {
        Log.d(TAG, "processDialogPositiveClick: Positive Message Dialog button pressed.");
        userSubmittedMessage = message;
        mMessageContentTextView.setText(userSubmittedMessage);
    }

    public void processDialogNegativeClick() {
        Log.d(TAG, "processDialogNegativeClick: Negative Message Dialog button pressed.");
    }

    private void pickContact() {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_TYPE); // Show user only contacts w/ addresses
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == PICK_CONTACT_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.

                // Do something with the contact here (bigger example below)
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {"display_name",
                        ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.

                //[sort_key, photo_uri, status_label, status_ts, status_res_package, display_name,

                Cursor cursor = getContentResolver()
                        .query(contactUri, projection, null, null, null);
                cursor.moveToFirst();


                Log.d(TAG, "onActivityResult: contactURI" + contactUri);

                String[] columnNames = cursor.getColumnNames();

                List<String> columnsList = Arrays.asList(columnNames);

                Log.d(TAG, "onActivityResult: columnNamesList : " + columnsList);

                Log.d(TAG, "onActivityResult: # of columns in row: " + cursor.getColumnNames().length);

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                Log.d(TAG, "onActivityResult: phoneNumberColumn #: " + column);
                int postalColumn = cursor.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS);

                Log.d(TAG, "onActivityResult: postalColumn #: " + postalColumn);
                String number = cursor.getString(column);

                userSubmittedRecAddress = cursor.getString(postalColumn);

                userSubmittedRecName = cursor.getString(0);

                Log.d(TAG, "onActivityResult: captured Name: " + userSubmittedRecAddress);

                Log.d(TAG, "onActivityResult: captured address: " + userSubmittedRecName);


                // Do something with the phone number...

                mRecipientNameTextView.setText(userSubmittedRecName);

                mRecipientAddressTextView.setText(userSubmittedRecAddress);

                cursor.close();
            }
        } else if (requestCode == RC_SIGN_IN) {
            //Handle sign-in result here
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "handleSignInResult:" + result.isSuccess());
            if (result.isSuccess()) {
                // Signed in successfully, show authenticated UI.
                GoogleSignInAccount acct = result.getSignInAccount();
                mUserEmailAddress = acct.getEmail();
                sendOrderEmailToUser();
            } else {
                // Signed out, show unauthenticated UI.
                Log.d(TAG, "onActivityResult: sign in failed or User declined" + result.getStatus().getStatusMessage());
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "Setting screen name: " + TAG);
        mTracker.setScreenName("Image~" + TAG);
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the message
        Log.d(TAG, "onSaveInstanceState: saving Message content: " + mMessageContentTextView.getText().toString());
        outState.putString(getString(R.string.checkout_bundle_message_key),mMessageContentTextView.getText().toString());
        //save the recipient name
        Log.d(TAG, "onSaveInstanceState: saving To Name: " + mRecipientNameTextView.getText().toString());
        outState.putString(getString(R.string.checkout_bundle_rec_name_key),mRecipientNameTextView.getText().toString());
        //save the recipient address
        Log.d(TAG, "onSaveInstanceState: saving Address: " + mRecipientAddressTextView.getText().toString());
        outState.putString(getString(R.string.checkout_bundle_rec_address_key),mRecipientAddressTextView.getText().toString());
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed: Google Sign on Failed: cause this stuff - " + connectionResult.toString());
    }


    public static class MessageDialogFragment extends DialogFragment {
        EditText messageEditText;

        public interface MessageDialogListener {
             void onDialogPositiveClick(DialogFragment dialog);
             void onDialogNegativeClick(DialogFragment dialog);
        }

        CheckoutActivity mListener;

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            // Verify that the host activity implements the callback interface
            try {
                // Instantiate the NoticeDialogListener so we can send events to the host
                mListener = (CheckoutActivity) activity;
            } catch (ClassCastException e) {
                // The activity doesn't implement the interface, throw exception
                throw new ClassCastException(activity.toString()
                        + " must implement NoticeDialogListener");
            }
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setMessage(R.string.dialog_message_title);




            builder.setNegativeButton(R.string.dialog_message_negative_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //User clicked the cancel button
                    //mListener.onDialogNegativeClick(MessageDialogFragment.this);
                    mListener.processDialogNegativeClick();
                }
            });

            builder.setPositiveButton(R.string.dialog_message_positive_btn, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //User clicked the ok button
                    //mListener.onDialogPositiveClick(MessageDialogFragment.this);
                    Dialog ourDialog = getDialog();
                    messageEditText = (EditText) ourDialog.findViewById(R.id.dialog_user_message_content);

                    String theMessage = "";
                    if (messageEditText != null) {
                        theMessage = messageEditText.getText().toString();
                        Log.d(TAG, "onClick: Pulled " + theMessage + " from message Dialog.");
                    }
                    mListener.processDialogPositiveClick(theMessage);
                }
            });

            builder.setView(R.layout.dialog_message_content);



            return builder.create();
        }
    }
}
