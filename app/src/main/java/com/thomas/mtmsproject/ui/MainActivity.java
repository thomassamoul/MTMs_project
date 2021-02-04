package com.thomas.mtmsproject.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.thomas.mtmsproject.R;
import com.thomas.mtmsproject.adapters.SourceAdapter;
import com.thomas.mtmsproject.utils.DistanceCalculator;

public class MainActivity extends AppCompatActivity {

    private MainActivityViewModel viewModel;
    private NavController navController;
    private FloatingActionButton fab;
    private View menuLayout;
    private RecyclerView recyclerView;
    private EditText sourceEdt, destinationEdt;
    private boolean icon;
    private ListView listView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);
        fab = findViewById(R.id.fab);

        initFAB();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        menuLayout = findViewById(R.id.menu_layout);

        initView();

        init();
    }

    private void init() {
        Places.initialize(this, getString(R.string.google_maps_key));
        destinationEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initCustomSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    void initCustomSearch(String query) {
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        PlacesClient placesClient = Places.createClient(this);

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            ArrayAdapter<SpannableString> strings = new ArrayAdapter<SpannableString>(this,
                    R.layout.source_item_layout);
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                strings.add(prediction.getFullText(null));
            }
            listView = findViewById(R.id.list_view);
            listView.setAdapter(strings);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                destinationEdt.setText(strings.getItem(position).toString());
                listView.setAdapter(null);
            });
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e("TAG", "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void initView() {
        sourceEdt = findViewById(R.id.source_edt);
        destinationEdt = findViewById(R.id.destination_edt);
        recyclerView = findViewById(R.id.recycler);
        button = findViewById(R.id.request_btn);

        button.setOnClickListener(v -> {
            DistanceCalculator distanceCalculator = new DistanceCalculator();
            viewModel.getDriversMutableLiveData().observe(this, list -> {
                distanceCalculator.findNearbyLocation(this, list);
            });

        });


        sourceEdt.setOnTouchListener((v, event) -> {
            Animation slideDown = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
            if (recyclerView.getVisibility() == View.GONE) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView.startAnimation(slideDown);
                return false;
            }
            return true;
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setHasFixedSize(true);


        viewModel.getSourceMutableLiveData().observe(this, sources -> {
            SourceAdapter adapter = new SourceAdapter(this, sources);
            recyclerView.setAdapter(adapter);

            adapter.setOnItemClickListener(Source -> {
                sourceEdt.setText(Source.getName());
                recyclerView.setVisibility(View.GONE);

            });
        });


    }

    @Override
    public void onBackPressed() {
        if (recyclerView.getVisibility() == View.VISIBLE) {
            recyclerView.setVisibility(View.GONE);
            icon = true;
            listView.setAdapter(null);

        }
        if (menuLayout.getVisibility() == View.VISIBLE) {
            menuLayout.setVisibility(View.GONE);
        }

        super.onBackPressed();
    }

    private void initFAB() {

        icon = false;
        if (!icon) {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_menu_24));
        } else {
            fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_back_24));
        }

        fab.setOnClickListener(v -> {

            if (icon) {
                Animation slideLeft = AnimationUtils.loadAnimation(this, R.anim.slide_left);

                if (menuLayout.getVisibility() == View.VISIBLE) {
                    menuLayout.setVisibility(View.GONE);
                    menuLayout.startAnimation(slideLeft);
                    recyclerView.setVisibility(View.GONE);
                }
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_menu_24));
                icon = false;

            } else {
                Animation slideRight = AnimationUtils.loadAnimation(this, R.anim.slide_right);

                if (menuLayout.getVisibility() == View.GONE) {
                    menuLayout.setVisibility(View.VISIBLE);
                    menuLayout.startAnimation(slideRight);
                }
                fab.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_baseline_arrow_back_24));
                icon = true;

            }
        });
    }
}