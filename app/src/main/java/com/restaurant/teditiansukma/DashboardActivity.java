package com.restaurant.teditiansukma;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.restaurant.teditiansukma.adapter.RestaurantAdapter;
import com.restaurant.teditiansukma.data.Session;
import com.restaurant.teditiansukma.model.ListRestaurantResponse;

import static com.restaurant.teditiansukma.data.Constans.GET_LIST_RESTAURANT;
import static com.restaurant.teditiansukma.data.Constans.GET_SEARCH_RESTAURANT;

public class DashboardActivity extends AppCompatActivity {
    RestaurantAdapter adapter;
    RecyclerView rv;
    ProgressDialog progressDialog;
    Session session;
    SearchView svRestaurant;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        session=new Session(this);
        progressDialog=new ProgressDialog(this);
        initView();
        initRecyclerView();
        initSearch();
    }

    //MethodSearch
    private void initSearch(){
        svRestaurant.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query){
            loadItem(GET_SEARCH_RESTAURANT+query);
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText){
            return false;
        }
        });
        svRestaurant.setOnCloseListener(new SearchView.OnCloseListener(){
            @Override
            public boolean onClose(){
                loadItem(GET_LIST_RESTAURANT);
                return false;
            }
        });
    }
//Method untuk bind view
private void initView(){
    svRestaurant=findViewById(R.id.sv_restaurant);
}
//Method ini digunakan untuk menampilkan menu
@Override
public boolean onCreateOptionsMenu(Menu menu){
    getMenuInflater().inflate(R.menu.menu_dashboard,menu);
    return true;
}
//Menthod ini digunakan untuk menangani kejadian saat Option Menu di klik
@Override
public boolean onOptionsItemSelected(@NonNull MenuItem item){
    switch(item.getItemId()){
        case R.id.menu_logout:session.logoutUser();
        break;
    }
    return true;
}
//Method untuk set recycler view
public void initRecyclerView(){adapter=new RestaurantAdapter(this);
loadItem(GET_LIST_RESTAURANT);
rv=findViewById(R.id.rv_restaurant);
rv.setLayoutManager(new GridLayoutManager(getApplicationContext(),2));
rv.setItemAnimator(new DefaultItemAnimator());
rv.setNestedScrollingEnabled(false);
rv.hasFixedSize();
rv.setAdapter(adapter);
adapter.setOnItemClickListener(new RestaurantAdapter.OnItemClickListener(){
    @Override
    public void onClick(int position){
        Toast.makeText(DashboardActivity.this,"ClickedItem-"+position,
                Toast.LENGTH_SHORT).show();
    }
});
}

//Method untuk load data dari api
public void loadItem(String url){
    //show progress dialog
    progressDialog.setMessage("PleaseWait..");
    progressDialog.show();
    AndroidNetworking.get(url)
            .build()
            .getAsObject(ListRestaurantResponse.class,new ParsedRequestListener(){
                @Override
                public void onResponse(Object response){
                    if(response instanceof ListRestaurantResponse){
                        //disable progress dialog
                        progressDialog.dismiss();
                        //null data check
                        if(((ListRestaurantResponse)response)
                                .getData()!=null&&((ListRestaurantResponse)response)
                                .getData().size()>0){adapter.swap(((ListRestaurantResponse)response)
                                .getData());
                                adapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onError(ANError anError){
                    progressDialog.dismiss();
                    Toast.makeText(DashboardActivity.this,"Failedtofetchdata",
                            Toast.LENGTH_SHORT).show();
                }
            });
}
}