package com.example.greent.petals.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.greent.petals.R;
import com.example.greent.petals.data.FlowerProduct;
import com.squareup.picasso.Picasso;

public class CheckoutActivity extends AppCompatActivity {

    FlowerProduct mSelectedProduct;
    final double DELIVERY_FEE = 5.00;
    double mTaxPercentage = 0.08;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

            messageCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Validate the message
                    //set background color
                    //pop up a modal
                }
            });

            CardView recipientCardView = (CardView) findViewById(R.id.checkout_recipient_container);

            recipientCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //pop up a modal
                    //enter the name
                }
            });

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Snackbar.make(view, "Order Submitted!", Snackbar.LENGTH_LONG)
                    //        .setAction("Action", null).show();
                    //validateSubmission();
                }
            });
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

}
